package ru.nsu.ccfit.network.p2psnake.desktop.hub;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

@Component
@Slf4j
public class NodeStorage
        implements Closeable {

    private static final int BUFFER_SIZE = 4096;

    private final Map<UUID, Date> dateTable = new ConcurrentHashMap<>();
    private final Map<UUID, String> hostTable = new ConcurrentHashMap<>();
    private final ExecutorService messageCollector = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("announce-collector-%d").build());
    private final ScheduledExecutorService garbageCollector = Executors.newScheduledThreadPool(1,
            new ThreadFactoryBuilder().setNameFormat("announce-remover-%d").build());

    private final String address;
    private final Integer port;
    private final Long expiredTime;
    private final Integer version;

    public NodeStorage(@Value("${hub.multicast.address}") String address,
            @Value("${hub.multicast.port}")Integer port,
            @Value("${hub.message.expired-time}") Long expiredTime,
            @Value("${hub.message.version}") Integer version) {
        this.address = address;
        this.port = port;
        this.expiredTime = expiredTime;
        this.version = version;

        messageCollector.execute(this::collectFreeNodes);
        garbageCollector.scheduleAtFixedRate(() -> removeAnnouncement(new Date()),
                0, expiredTime * 3 / 2, TimeUnit.MILLISECONDS);
    }

    private void collectFreeNodes() {
        try (MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(new InetSocketAddress(address, port),
                    NetworkInterface.getByInetAddress(InetAddress.getByName(address)));
            socket.setSoTimeout(1000);
            log.info("Hub receiver subscribed on {}:{}", address, port);

            byte[] buffer = new byte[BUFFER_SIZE];
            while (!Thread.currentThread().isInterrupted()) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                } catch (SocketTimeoutException e) {
                    continue;
                }
                log.debug("Received packet from {}:{}", packet.getAddress().getHostAddress(), packet.getPort());

                HubProtocol.HubMessage msg;
                try {
                    msg = HubProtocol.HubMessage.parseFrom(
                            Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));
                } catch (InvalidProtocolBufferException e) {
                    log.error(e.getMessage());
                    continue;
                }

                if (validatePacket(msg)) {
                    addAnnouncement(UUID.fromString(msg.getId()), msg.getApiUrl());
                }
            }
        } catch (IOException e) {
            log.error("Multicast socket error", e);
        }
    }

    private boolean validatePacket(HubProtocol.HubMessage msg) {
        boolean isValidUUID;
        try {
            UUID.fromString(msg.getId());
            isValidUUID = true;
        } catch (IllegalArgumentException e) {
            isValidUUID = false;
        }

        return msg.getVersion() == version && isValidUUID;
    }

    public void addAnnouncement(UUID id, String host) {
        boolean exist = hostTable.containsKey(id);

        hostTable.put(id, host);
        dateTable.put(id, new Date(System.currentTimeMillis() + expiredTime));

        if (exist) {
            log.debug("Updated copy {}", host);
        } else {
            log.debug("Added copy {}", host);
        }
    }

    public List<String> getNodeList() {
        return hostTable.values()
                .stream()
                .sorted()
                .toList();
    }

    public void removeAnnouncement(Date now) {
        for (Map.Entry<UUID, Date> entry : dateTable.entrySet()) {
            if (entry.getValue().before(now)) {
                String host = hostTable.get(entry.getKey());

                hostTable.remove(entry.getKey());
                dateTable.remove(entry.getKey());

                log.debug("Removed copy {}", host);
            }
        }
    }

    @Override
    public void close()
            throws IOException {
        messageCollector.shutdownNow();
        garbageCollector.shutdownNow();
    }
}

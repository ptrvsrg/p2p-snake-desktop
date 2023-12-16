package ru.nsu.ccfit.network.p2psnake.desktop.api.util;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.ccfit.network.p2psnake.desktop.api.ApiProtocol;
import ru.nsu.ccfit.network.p2psnake.desktop.api.exception.ApiErrorException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ApiClientUtils {

    public static String getAddress(String host) {
        if (host == null) {
            return null;
        }
        String[] parts = host.split(":");
        if (parts.length != 2) {
            return null;
        }
        return parts[0];
    }

    public static Integer getPort(String host) {
        String[] parts = host.split(":");
        if (parts.length == 1) {
            return 80;
        }
        if (parts.length != 2) {
            return null;
        }

        int port;
        try {
            port = Integer.parseUnsignedInt(parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }
        if (port > 0xFFFF) {
            return null;
        }
        return port;
    }

    public static void sendProto(DatagramSocket socket, ApiProtocol.APIRequest request, SocketAddress address)
            throws ApiErrorException {
        byte[] msgBytes = request.toByteArray();
        try {
            socket.send(new DatagramPacket(msgBytes, msgBytes.length, address));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ApiErrorException("Network problems");
        }
    }

    public static ApiProtocol.APIResponse receiveProto(DatagramSocket socket)
            throws ApiErrorException {
        DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
        try {
            socket.receive(packet);
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ApiErrorException("Network problems");
        }

        try {
            return ApiProtocol.APIResponse.parseFrom(
                    Arrays.copyOfRange(packet.getData(), 0, packet.getLength())
            );
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static void sendConnectMsg(DatagramSocket socket, SocketAddress address)
            throws ApiErrorException {
        sendProto(
                socket,
                ApiProtocol.APIRequest
                        .newBuilder()
                        .setConnect(ApiProtocol.APIRequest.ConnectMsg
                                .newBuilder()
                                .build())
                        .build(),
                address);
    }

    public static void sendPingMsg(DatagramSocket socket, String token, SocketAddress address)
            throws ApiErrorException {
        sendProto(
                socket,
                ApiProtocol.APIRequest
                        .newBuilder()
                        .setPing(ApiProtocol.APIRequest.PingMsg
                                .newBuilder()
                                .setToken(token)
                                .build())
                        .build(),
                address);
    }

    public static void sendCreateGameMsg(DatagramSocket socket, String token, String gameName, int width, int height,
                                         int foodStatic, int stateDelay, String playerName, SocketAddress address)
            throws ApiErrorException {
        sendProto(
                socket,
                ApiProtocol.APIRequest
                        .newBuilder()
                        .setCreateGame(ApiProtocol.APIRequest.CreateGameMsg
                                .newBuilder()
                                .setToken(token)
                                .setPlayerName(playerName)
                                .setGameName(gameName)
                                .setWidth(width)
                                .setHeight(height)
                                .setFoodStatic(foodStatic)
                                .setStateDelayMs(stateDelay)
                                .build())
                        .build(),
                address);
    }

    public static void sendDiscoverGamesMsg(DatagramSocket socket, String token, SocketAddress address)
            throws ApiErrorException {
        sendProto(
                socket,
                ApiProtocol.APIRequest
                        .newBuilder()
                        .setDiscoverGames(ApiProtocol.APIRequest.DiscoverGamesMsg
                                .newBuilder()
                                .setToken(token)
                                .build())
                        .build(),
                address);
    }

    public static void sendJoinGameMsg(DatagramSocket socket, String token, String gameName, String playerName,
            boolean isPlayer, SocketAddress address)
            throws ApiErrorException {
        sendProto(
                socket,
                ApiProtocol.APIRequest
                        .newBuilder()
                        .setJoinGame(ApiProtocol.APIRequest.JoinGameMsg
                                .newBuilder()
                                .setToken(token)
                                .setGameName(gameName)
                                .setPlayerName(playerName)
                                .setIsPlayer(isPlayer)
                                .build())
                        .build(),
                address);
    }

    public static void sendSteerSnakeMsg(DatagramSocket socket, String token, ApiProtocol.Direction direction,
            SocketAddress address)
            throws ApiErrorException {
        sendProto(
                socket,
                ApiProtocol.APIRequest
                        .newBuilder()
                        .setSteerSnake(ApiProtocol.APIRequest.SteerSnakeMsg
                                .newBuilder()
                                .setToken(token)
                                .setDirection(direction)
                                .build())
                        .build(),
                address);
    }

    public static void sendGetGameStateMsg(DatagramSocket socket, String token, SocketAddress address)
            throws ApiErrorException {
        sendProto(
                socket,
                ApiProtocol.APIRequest
                        .newBuilder()
                        .setGetGameState(ApiProtocol.APIRequest.GetGameStateMsg
                                .newBuilder()
                                .setToken(token)
                                .build())
                        .build(),
                address);
    }

    public static void sendExitGameMsg(DatagramSocket socket, String token, SocketAddress address)
            throws ApiErrorException {
        sendProto(
                socket,
                ApiProtocol.APIRequest
                        .newBuilder()
                        .setExitGame(ApiProtocol.APIRequest.ExitGameMsg
                                .newBuilder()
                                .setToken(token)
                                .build())
                        .build(),
                address);
    }

    public static void sendDisconnectMsg(DatagramSocket socket, String token, SocketAddress address)
            throws ApiErrorException {
        sendProto(
                socket,
                ApiProtocol.APIRequest
                        .newBuilder()
                        .setDisconnect(ApiProtocol.APIRequest.DisconnectMsg
                                .newBuilder()
                                .setToken(token)
                                .build())
                        .build(),
                address);
    }
}

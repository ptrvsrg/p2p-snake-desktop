package ru.nsu.ccfit.network.p2psnake.desktop.api;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.nsu.ccfit.network.p2psnake.desktop.api.dto.Direction;
import ru.nsu.ccfit.network.p2psnake.desktop.api.dto.GameInfoDto;
import ru.nsu.ccfit.network.p2psnake.desktop.api.dto.GameStateDto;
import ru.nsu.ccfit.network.p2psnake.desktop.api.exception.ApiErrorException;
import ru.nsu.ccfit.network.p2psnake.desktop.api.mapper.ApiProtocolMapper;
import ru.nsu.ccfit.network.p2psnake.desktop.api.util.ApiClientUtils;

import java.io.Closeable;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiClient
        implements Closeable {

    private final ApiProtocolMapper mapper;

    private final ThreadFactory pingerThreadFactory = new ThreadFactoryBuilder().setNameFormat("API-pinger-%d").build();

    private ScheduledExecutorService pinger;
    private SocketAddress apiAddress;
    private DatagramSocket socket;
    private String token;

    public void start(String apiUrl)
            throws ApiErrorException {
        close();

        String address = ApiClientUtils.getAddress(apiUrl);
        Integer port = ApiClientUtils.getPort(apiUrl);
        if (address == null || port == null) {
            throw new ApiErrorException("Not valid API URL");
        }
        apiAddress = new InetSocketAddress(address, port);

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(1000);
        } catch (SocketException e) {
            throw new ApiErrorException("Network problems");
        }
        log.info("API client listen on {}:{}", socket.getLocalAddress().getHostAddress(), socket.getLocalPort());

        ApiClientUtils.sendConnectMsg(socket, apiAddress);
        ApiProtocol.APIResponse response = ApiClientUtils.receiveProto(socket);
        if (response == null) {
            throw new ApiErrorException("API server is not responding");
        }
        if (response.hasError()) {
            throw new ApiErrorException(response.getError().getErrorMessage());
        }
        if (!response.hasSuccessConnect()) {
            throw new ApiErrorException("Unexpected API server response");
        }

        token = response.getSuccessConnect().getToken();
        int timeout = response.getSuccessConnect().getTimeout();

        pinger = Executors.newScheduledThreadPool(2, pingerThreadFactory);
        pinger.scheduleWithFixedDelay(this::pingServer, 0, timeout / 3, TimeUnit.MILLISECONDS);
    }

    private void pingServer() {
        try {
            ApiClientUtils.sendPingMsg(socket, token, apiAddress);
            log.debug("Send PingMsg");
        } catch (ApiErrorException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void createGame(String gameName, int width, int height, int foodStatic, int stateDelay, String playerName)
            throws ApiErrorException {
        if (socket == null) {
            throw new ApiErrorException("Socket is closed");
        }

        ApiClientUtils.sendCreateGameMsg(socket, token, gameName, width, height, foodStatic, stateDelay, playerName, apiAddress);
        ApiProtocol.APIResponse response = ApiClientUtils.receiveProto(socket);
        if (response == null) {
            throw new ApiErrorException("API server is not responding");
        }
        if (response.hasError()) {
            throw new ApiErrorException(response.getError().getErrorMessage());
        }
        if (!response.hasAck()) {
            throw new ApiErrorException("Unexpected API server response");
        }
    }

    public List<GameInfoDto> discoverGame()
            throws ApiErrorException {
        if (socket == null) {
            throw new ApiErrorException("Socket is closed");
        }

        ApiClientUtils.sendDiscoverGamesMsg(socket, token, apiAddress);
        ApiProtocol.APIResponse response = ApiClientUtils.receiveProto(socket);
        if (response == null) {
            throw new ApiErrorException("API server is not responding");
        }
        if (response.hasError()) {
            throw new ApiErrorException(response.getError().getErrorMessage());
        }
        if (!response.hasGameList()) {
            throw new ApiErrorException("Unexpected API server response");
        }

        return response.getGameList()
                .getGamesList()
                .stream()
                .map(mapper::toGameInfoDto)
                .toList();
    }

    public void joinGame(String gameName, String playerName, boolean isPlayer)
            throws ApiErrorException {
        if (socket == null) {
            throw new ApiErrorException("Socket is closed");
        }

        ApiClientUtils.sendJoinGameMsg(socket, token, gameName, playerName, isPlayer, apiAddress);
        ApiProtocol.APIResponse response = ApiClientUtils.receiveProto(socket);
        if (response == null) {
            throw new ApiErrorException("API server is not responding");
        }
        if (response.hasError()) {
            throw new ApiErrorException(response.getError().getErrorMessage());
        }
        if (!response.hasAck()) {
            throw new ApiErrorException("Unexpected API server response");
        }
    }

    public void steerSnake(Direction direction)
            throws ApiErrorException {
        if (socket == null) {
            throw new ApiErrorException("Socket is closed");
        }

        ApiClientUtils.sendSteerSnakeMsg(socket, token, mapper.toApiProtocolDirection(direction), apiAddress);
        ApiProtocol.APIResponse response = ApiClientUtils.receiveProto(socket);
        if (response == null) {
            throw new ApiErrorException("API server is not responding");
        }
        if (response.hasError()) {
            throw new ApiErrorException(response.getError().getErrorMessage());
        }
        if (!response.hasAck()) {
            throw new ApiErrorException("Unexpected API server response");
        }
    }

    public GameStateDto getGameState()
            throws ApiErrorException {
        if (socket == null) {
            throw new ApiErrorException("Socket is closed");
        }

        ApiClientUtils.sendGetGameStateMsg(socket, token, apiAddress);
        ApiProtocol.APIResponse response = ApiClientUtils.receiveProto(socket);
        if (response == null) {
            throw new ApiErrorException("API server is not responding");
        }
        if (response.hasError()) {
            throw new ApiErrorException(response.getError().getErrorMessage());
        }
        if (!response.hasGameState()) {
            throw new ApiErrorException("Unexpected API server response");
        }

        return mapper.toGameStateDto(response.getGameState().getSnakesList(),
                response.getGameState().getFoodsList(),
                response.getGameState().getPlayersList());
    }

    public void exitGame()
            throws ApiErrorException {
        if (socket == null) {
            throw new ApiErrorException("Socket is closed");
        }

        ApiClientUtils.sendExitGameMsg(socket, token, apiAddress);
        ApiProtocol.APIResponse response = ApiClientUtils.receiveProto(socket);
        if (response == null) {
            throw new ApiErrorException("API server is not responding");
        }
        if (response.hasError()) {
            throw new ApiErrorException(response.getError().getErrorMessage());
        }
        if (!response.hasAck()) {
            throw new ApiErrorException("Unexpected API server response");
        }
    }

    @Override
    public void close() {
        if (pinger != null) {
            pinger.shutdownNow();
        }
        if (socket != null) {
            socket.close();
        }
    }
}

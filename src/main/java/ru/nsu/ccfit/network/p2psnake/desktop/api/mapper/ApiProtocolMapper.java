package ru.nsu.ccfit.network.p2psnake.desktop.api.mapper;

import org.springframework.stereotype.Component;
import ru.nsu.ccfit.network.p2psnake.desktop.api.ApiProtocol;
import ru.nsu.ccfit.network.p2psnake.desktop.api.dto.*;

import java.util.List;

@Component
public class ApiProtocolMapper {

    public GameInfoDto toGameInfoDto(ApiProtocol.APIResponse.GameListMsg.GameInfo gameInfo) {
        return GameInfoDto.builder()
                .name(gameInfo.getGameName())
                .width(gameInfo.getWidth())
                .height(gameInfo.getHeight())
                .stateDelay(gameInfo.getStateDelay())
                .build();
    }

    public ApiProtocol.Direction toApiProtocolDirection(Direction direction) {
        switch (direction) {
            case UP -> {
                return ApiProtocol.Direction.UP;
            }
            case DOWN -> {
                return ApiProtocol.Direction.DOWN;
            }
            case LEFT -> {
                return ApiProtocol.Direction.LEFT;
            }
            case RIGHT -> {
                return ApiProtocol.Direction.RIGHT;
            }
            default -> {
                return null;
            }
        }
    }

    public Direction toDirection(ApiProtocol.Direction direction) {
        switch (direction) {
            case UP -> {
                return Direction.UP;
            }
            case DOWN -> {
                return Direction.DOWN;
            }
            case LEFT -> {
                return Direction.LEFT;
            }
            case RIGHT -> {
                return Direction.RIGHT;
            }
            default -> {
                return null;
            }
        }
    }

    public PlayerDto.Role toRole(ApiProtocol.APIResponse.GameStateMsg.Role role) {
        switch (role) {
            case MASTER -> {
                return PlayerDto.Role.MASTER;
            }
            case NORMAL -> {
                return PlayerDto.Role.NORMAL;
            }
            case DEPUTY -> {
                return PlayerDto.Role.DEPUTY;
            }
            case VIEWER -> {
                return PlayerDto.Role.VIEWER;
            }
            default -> {
                return null;
            }
        }
    }

    public CoordDto toCoordDto(ApiProtocol.APIResponse.GameStateMsg.Coord coord) {
        return CoordDto.builder()
                .x(coord.getX())
                .y(coord.getY())
                .build();
    }

    public SnakeDto toSnakeDto(ApiProtocol.APIResponse.GameStateMsg.Snake snake) {
        return SnakeDto.builder()
                .playerId(snake.getPlayerId())
                .headDirection(toDirection(snake.getHeadDirection()))
                .points(snake.getPointsList()
                        .stream()
                        .map(this::toCoordDto)
                        .toList())
                .build();
    }

    public PlayerDto toPlayerDto(ApiProtocol.APIResponse.GameStateMsg.Player player) {
        return PlayerDto.builder()
                .id(player.getId())
                .name(player.getName())
                .role(toRole(player.getRole()))
                .score(player.getScore())
                .build();
    }

    public GameStateDto toGameStateDto(List<ApiProtocol.APIResponse.GameStateMsg.Snake> snakes,
                                       List<ApiProtocol.APIResponse.GameStateMsg.Coord> foods,
                                       List<ApiProtocol.APIResponse.GameStateMsg.Player> players) {
        return GameStateDto.builder()
                .snakes(snakes.stream()
                        .map(this::toSnakeDto)
                        .toList())
                .foods(foods.stream()
                        .map(this::toCoordDto)
                        .toList())
                .players(players.stream()
                        .map(this::toPlayerDto)
                        .toList())
                .build();
    }
}

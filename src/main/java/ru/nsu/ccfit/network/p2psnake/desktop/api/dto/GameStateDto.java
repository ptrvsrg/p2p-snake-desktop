package ru.nsu.ccfit.network.p2psnake.desktop.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GameStateDto {

    private List<SnakeDto> snakes;
    private List<CoordDto> foods;
    private List<PlayerDto> players;
}

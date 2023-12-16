package ru.nsu.ccfit.network.p2psnake.desktop.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SnakeDto {

    private int playerId;
    private Direction headDirection;
    private List<CoordDto> points;
}

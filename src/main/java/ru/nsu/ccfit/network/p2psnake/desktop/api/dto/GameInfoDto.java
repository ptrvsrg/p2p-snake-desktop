package ru.nsu.ccfit.network.p2psnake.desktop.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameInfoDto {

    private String name;
    private int width;
    private int height;
    private int stateDelay;
}

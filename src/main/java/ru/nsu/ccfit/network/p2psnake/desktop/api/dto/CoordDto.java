package ru.nsu.ccfit.network.p2psnake.desktop.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoordDto {

    private int x;
    private int y;
}

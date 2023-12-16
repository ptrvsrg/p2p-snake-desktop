package ru.nsu.ccfit.network.p2psnake.desktop.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerDto {

    public enum Role {
        NORMAL, MASTER, DEPUTY, VIEWER
    }

    private int id;
    private String name;
    private Role role;
    private int score;
}

package ru.nsu.ccfit.network.p2psnake.desktop.app;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class StageReadyEvent
        extends ApplicationEvent {

    public final Stage stage;

    public StageReadyEvent(Stage stage) {
        super(stage);
        this.stage = stage;
    }
}

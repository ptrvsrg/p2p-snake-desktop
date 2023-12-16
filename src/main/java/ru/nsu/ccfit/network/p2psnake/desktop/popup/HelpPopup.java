package ru.nsu.ccfit.network.p2psnake.desktop.popup;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HelpPopup {

    private static final String HELP_CONTENT = """
            Version: 0.0.0
            Developer: ptrvsrg
            Source: https://github.com/ptrvsrg/p2p-snake-desktop
            """;

    public static void show(Stage primaryStage) {
        Label body = new Label(HELP_CONTENT);
        body.setFont(new Font("Ticketing", 20));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(primaryStage);
        alert.getDialogPane().setHeader(new Label(""));
        alert.getDialogPane().setContent(body);
        alert.showAndWait();
    }
}
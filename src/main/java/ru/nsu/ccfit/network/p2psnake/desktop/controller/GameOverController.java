package ru.nsu.ccfit.network.p2psnake.desktop.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("game-over.fxml")
@RequiredArgsConstructor
public class GameOverController {

    private final FxWeaver fxWeaver;

    @FXML
    private Label score;

    @FXML
    public void initialize(int score) {
        Platform.runLater(() -> this.score.setText(score + " points"));
    }

    @FXML
    private void handleReturnMainMenu(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(MainController.class)));
        stage.setFullScreen(true);
    }
}

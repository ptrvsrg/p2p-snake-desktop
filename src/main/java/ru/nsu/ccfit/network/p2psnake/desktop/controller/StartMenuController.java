package ru.nsu.ccfit.network.p2psnake.desktop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.nsu.ccfit.network.p2psnake.desktop.api.ApiClient;

@Component
@FxmlView("start-menu.fxml")
@RequiredArgsConstructor
public class StartMenuController {

    private final FxWeaver fxWeaver;
    private final ApiClient apiClient;

    @FXML
    public void handleCreateNewGame(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(GameConfigController.class)));
        stage.setFullScreen(true);
    }

    @FXML
    public void handleJoinExistingGame(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(DiscoverGameController.class)));
        stage.setFullScreen(true);
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        apiClient.close();

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(MainController.class)));
        stage.setFullScreen(true);
    }
}

package ru.nsu.ccfit.network.p2psnake.desktop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.nsu.ccfit.network.p2psnake.desktop.api.ApiClient;
import ru.nsu.ccfit.network.p2psnake.desktop.api.dto.GameInfoDto;
import ru.nsu.ccfit.network.p2psnake.desktop.api.exception.ApiErrorException;
import ru.nsu.ccfit.network.p2psnake.desktop.popup.ErrorPopup;

import java.util.List;

@Component
@FxmlView("discover-game.fxml")
@RequiredArgsConstructor
public class DiscoverGameController {

    private final FxWeaver fxWeaver;
    private final ApiClient apiClient;

    @FXML
    private VBox gameButtonContainer;

    @FXML
    public void handleUpdate(ActionEvent event) {
        List<GameInfoDto> gameInfos = null;
        try {
            gameInfos = apiClient.discoverGame();
        } catch (ApiErrorException e) {
            apiClient.close();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(fxWeaver.loadView(MainController.class)));
            stage.setFullScreen(true);

            ErrorPopup.show(e.getMessage());
            return;
        }

        gameButtonContainer.getChildren().clear();
        for (GameInfoDto gameInfo : gameInfos) {
            Button btn = new Button();
            btn.setText(String.format("%s, %dx%d, %dms",
                    gameInfo.getName(), gameInfo.getWidth(), gameInfo.getHeight(), gameInfo.getStateDelay()));
            btn.setFont(new Font("Ticketing", 32));
            btn.setPrefWidth(1880);
            btn.setAlignment(Pos.CENTER);
            btn.setTextAlignment(TextAlignment.CENTER);
            btn.setFocusTraversable(true);
            btn.setOnAction(event1 -> {
                JoinGameController joinGameController = fxWeaver.loadController(JoinGameController.class);
                joinGameController.initialize(gameInfo);

                Stage stage = (Stage)((Node) event1.getSource()).getScene().getWindow();
                stage.setScene(new Scene(fxWeaver.loadView(JoinGameController.class)));
                stage.setFullScreen(true);
            });
            VBox.setMargin(btn, new Insets(0, 0, 10, 0));
            gameButtonContainer.getChildren().add(btn);
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(StartMenuController.class)));
        stage.setFullScreen(true);
    }
}

package ru.nsu.ccfit.network.p2psnake.desktop.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.nsu.ccfit.network.p2psnake.desktop.api.ApiClient;
import ru.nsu.ccfit.network.p2psnake.desktop.api.dto.GameInfoDto;
import ru.nsu.ccfit.network.p2psnake.desktop.api.dto.PlayerDto;
import ru.nsu.ccfit.network.p2psnake.desktop.api.exception.ApiErrorException;
import ru.nsu.ccfit.network.p2psnake.desktop.popup.ErrorPopup;

@Component
@FxmlView("join-game.fxml")
@RequiredArgsConstructor
public class JoinGameController {

    private final FxWeaver fxWeaver;
    private final ApiClient apiClient;

    @FXML
    private TextField playerNameInput;
    @FXML
    private ComboBox<PlayerDto.Role> roleBox;
    private GameInfoDto gameInfoDto;

    @FXML
    public void initialize(GameInfoDto gameInfoDto) {
        Platform.runLater(() -> {
            roleBox.getItems().clear();
            roleBox.getItems().addAll(FXCollections.observableArrayList(PlayerDto.Role.NORMAL, PlayerDto.Role.VIEWER));
        });

        this.gameInfoDto = gameInfoDto;
    }

    @FXML
    public void handleJoinGame(ActionEvent event) {
        String playerName = getPlayerName();
        if (playerName == null) return;

        PlayerDto.Role role = getPlayerRole();
        if (role == null) return;

        try {
            apiClient.joinGame(gameInfoDto.getName(), playerName, role == PlayerDto.Role.NORMAL);
        } catch (ApiErrorException e) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(fxWeaver.loadView(MainController.class)));
            stage.setFullScreen(true);

            ErrorPopup.show(e.getMessage());
            return;
        }

        GameBoardController gameBoardController = fxWeaver.loadController(GameBoardController.class);
        gameBoardController.initialize(gameInfoDto.getName(), gameInfoDto.getWidth(), gameInfoDto.getHeight(),
                gameInfoDto.getStateDelay(), playerName);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(GameBoardController.class)));
        stage.setFullScreen(true);
    }

    private String getPlayerName() {
        String playerName = playerNameInput.getText();
        if (playerName == null || playerName.isBlank()) {
            ErrorPopup.show("Player name must be not empty and contains not only white space codepoints");
            return null;
        }
        return playerName;
    }

    private PlayerDto.Role getPlayerRole() {
        PlayerDto.Role role = roleBox.getValue();
        if (role == null) {
            ErrorPopup.show("Player role not selected");
            return null;
        }
        return role;
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(StartMenuController .class)));
        stage.setFullScreen(true);
    }
}

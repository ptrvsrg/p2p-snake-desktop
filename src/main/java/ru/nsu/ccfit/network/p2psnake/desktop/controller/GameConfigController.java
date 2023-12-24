package ru.nsu.ccfit.network.p2psnake.desktop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.nsu.ccfit.network.p2psnake.desktop.api.ApiClient;
import ru.nsu.ccfit.network.p2psnake.desktop.api.exception.ApiErrorException;
import ru.nsu.ccfit.network.p2psnake.desktop.popup.ErrorPopup;

@Component
@FxmlView("game-config.fxml")
@RequiredArgsConstructor
public class GameConfigController {

    private final FxWeaver fxWeaver;
    private final ApiClient apiClient;

    @FXML
    private TextField gameNameInput;

    @FXML
    private TextField widthInput;

    @FXML
    private TextField heightInput;

    @FXML
    private TextField foodStaticInput;

    @FXML
    private TextField stateDelayInput;

    @FXML
    private TextField playerNameInput;

    private String getPlayerName() {
        String playerName = playerNameInput.getText();
        if (playerName == null || playerName.isBlank()) {
            ErrorPopup.show("Player name must be not empty and contains not only white space codepoints");
            return null;
        }
        return playerName;
    }

    @FXML
    public void handleCreateGame(ActionEvent event) {
        String gameName = getGameName();
        if (gameName == null) return;

        Integer width = getWidth();
        if (width == null) return;

        Integer height = getHeight();
        if (height == null) return;

        Integer foodStatic = getFoodStatic();
        if (foodStatic == null) return;

        Integer stateDelay = getStateDelay();
        if (stateDelay == null) return;

        String playerName = getPlayerName();
        if (playerName == null) return;

        try {
            apiClient.createGame(gameName, width, height, foodStatic, stateDelay, playerName);
        } catch (ApiErrorException e) {
            apiClient.close();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(fxWeaver.loadView(MainController.class)));
            stage.setFullScreen(true);

            ErrorPopup.show(e.getMessage());
            return;
        }

        GameBoardController gameBoardController = fxWeaver.loadController(GameBoardController.class);
        gameBoardController.initialize(gameName, width, height, stateDelay, playerName);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(GameBoardController.class)));
        stage.setFullScreen(true);
    }

    private String getGameName() {
        String gameName = gameNameInput.getText();
        if (gameName == null || gameName.isBlank()) {
            ErrorPopup.show("Game name must be not empty and contains not only white space codepoints");
            return null;
        }
        return gameName;
    }

    private Integer getWidth() {
        String widthStr = widthInput.getText();
        if (widthStr == null || widthStr.isBlank()) {
            ErrorPopup.show("Width must be not empty");
            return null;
        }
        int width;
        try {
            width = Integer.parseInt(widthStr);
        } catch (NumberFormatException e) {
            ErrorPopup.show("Width must be number");
            return null;
        }
        if (width < 10 || width > 100) {
            ErrorPopup.show("Width must be between 10 and 100");
            return null;
        }
        return width;
    }

    private Integer getHeight() {
        String heightStr = heightInput.getText();
        if (heightStr == null || heightStr.isBlank()) {
            ErrorPopup.show("Height must be not empty");
            return null;
        }
        int height;
        try {
            height = Integer.parseInt(heightStr);
        } catch (NumberFormatException e) {
            ErrorPopup.show("Height must be number");
            return null;
        }
        if (height < 10 || height > 100) {
            ErrorPopup.show("Height must be between 10 and 100");
            return null;
        }
        return height;
    }

    private Integer getFoodStatic() {
        String foodStaticStr = foodStaticInput.getText();
        if (foodStaticStr == null || foodStaticStr.isBlank()) {
            ErrorPopup.show("Initial amount of food must be not empty");
            return null;
        }
        int foodStatic;
        try {
            foodStatic = Integer.parseInt(foodStaticStr);
        } catch (NumberFormatException e) {
            ErrorPopup.show("Initial amount of food must be number");
            return null;
        }
        if (foodStatic < 0 || foodStatic > 100) {
            ErrorPopup.show("Initial amount of food must be between 0 and 100");
            return null;
        }
        return foodStatic;
    }

    private Integer getStateDelay() {
        String stateDelayStr = stateDelayInput.getText();
        if (stateDelayStr == null || stateDelayStr.isBlank()) {
            ErrorPopup.show("State delay must be not empty");
            return null;
        }
        int stateDelay;
        try {
            stateDelay = Integer.parseInt(stateDelayStr);
        } catch (NumberFormatException e) {
            ErrorPopup.show("State delay must be number");
            return null;
        }
        if (stateDelay < 100 || stateDelay > 3000) {
            ErrorPopup.show("State delay must be between 100 and 3000");
            return null;
        }
        return stateDelay;
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(StartMenuController.class)));
        stage.setFullScreen(true);
    }
}

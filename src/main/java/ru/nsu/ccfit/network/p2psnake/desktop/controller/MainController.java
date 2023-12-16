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
import ru.nsu.ccfit.network.p2psnake.desktop.popup.HelpPopup;

@Component
@FxmlView("main.fxml")
@RequiredArgsConstructor
public class MainController {

    private final FxWeaver fxWeaver;

    @FXML
    private void handleFindNodes(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(HubController.class)));
        stage.setFullScreen(true);
    }

    @FXML
    private void handleHelp(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        HelpPopup.show(stage);
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}

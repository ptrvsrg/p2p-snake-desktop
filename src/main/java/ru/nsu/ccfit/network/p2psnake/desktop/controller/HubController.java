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
import ru.nsu.ccfit.network.p2psnake.desktop.api.exception.ApiErrorException;
import ru.nsu.ccfit.network.p2psnake.desktop.hub.HubService;
import ru.nsu.ccfit.network.p2psnake.desktop.popup.ErrorPopup;

import java.util.List;

@Component
@FxmlView("hub.fxml")
@RequiredArgsConstructor
public class HubController {

    private final FxWeaver fxWeaver;
    private final HubService hubService;
    private final ApiClient apiClient;

    @FXML
    private VBox apiUrlButtonContainer;

    @FXML
    public void handleUpdate() {
        List<String> nodes = hubService.getNodeList();
        apiUrlButtonContainer.getChildren().clear();
        for (String apiUrl : nodes) {
            Button btn = new Button();
            btn.setText(apiUrl);
            btn.setFont(new Font("Ticketing", 32));
            btn.setPrefWidth(1880);
            btn.setAlignment(Pos.CENTER);
            btn.setTextAlignment(TextAlignment.CENTER);
            btn.setFocusTraversable(true);
            btn.setOnAction(event -> {
                try {
                    apiClient.start(apiUrl);
                } catch (ApiErrorException e) {
                    ErrorPopup.show(e.getMessage());
                    return;
                }

                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(fxWeaver.loadView(StartMenuController.class)));
                stage.setFullScreen(true);
            });
            VBox.setMargin(btn, new Insets(0, 0, 10, 0));
            apiUrlButtonContainer.getChildren().add(btn);
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(MainController.class)));
        stage.setFullScreen(true);
    }
}

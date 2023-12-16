package ru.nsu.ccfit.network.p2psnake.desktop.popup;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorPopup {

    public static void show(String message) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(message);
        text.setFont(Font.font("Ticketing", 20));
        text.setFill(Color.BLACK);
        text.setWrappingWidth(380);
        text.setLineSpacing(5);
        text.setTextAlignment(TextAlignment.CENTER);

        Button closeButton = new Button();
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        closeButton.setOnAction(event -> stage.close());

        StackPane stackPane = new StackPane(text, closeButton);
        stackPane.setStyle("-fx-background-color: white;");
        stackPane.setPadding(new Insets(10));
        stackPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        Scene scene = new Scene(stackPane, 400, 150, Color.TRANSPARENT);
        stage.setScene(scene);

        stage.setX(Screen.getPrimary().getVisualBounds().getMaxX() - 420);
        stage.setY(Screen.getPrimary().getVisualBounds().getMaxY() - 170);

        stage.setAlwaysOnTop(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), stackPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(fadeEvent -> stage.close());
            fadeOut.play();
        });

        scene.setOnMouseEntered(event -> delay.stop());
        scene.setOnMouseExited(event -> delay.play());

        stage.show();
        delay.play();
    }
}
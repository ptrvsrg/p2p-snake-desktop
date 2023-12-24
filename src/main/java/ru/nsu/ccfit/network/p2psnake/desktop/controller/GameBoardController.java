package ru.nsu.ccfit.network.p2psnake.desktop.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.nsu.ccfit.network.p2psnake.desktop.api.ApiClient;
import ru.nsu.ccfit.network.p2psnake.desktop.api.dto.*;
import ru.nsu.ccfit.network.p2psnake.desktop.api.exception.ApiErrorException;
import ru.nsu.ccfit.network.p2psnake.desktop.popup.ErrorPopup;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@FxmlView("game-board.fxml")
@RequiredArgsConstructor
@Slf4j
public class GameBoardController {

    private static final double MAX_FIELD_HEIGHT = 650;
    private static final double MAX_FIELD_WIDTH = 850;


    private final FxWeaver fxWeaver;
    private final ApiClient apiClient;

    @FXML
    private Label title;
    @FXML
    private GridPane field;
    @FXML
    private VBox playerList;

    private int width;
    private int height;
    private String playerName;
    private PlayerDto player;
    private ScheduledExecutorService stateUpdater;

    @FXML
    public void initialize(String gameName, int width, int height, int stateDelay, String playerName) {
        this.width = width;
        this.height = height;
        this.playerName = playerName;
        this.stateUpdater = Executors.newScheduledThreadPool(1,
                new ThreadFactoryBuilder().setNameFormat("state-updater-%d").build());

        Platform.runLater(() -> {
            this.title.setText(gameName);
            Scene scene = field.getScene();
            scene.setOnKeyPressed(this::handleKeyPressed);
        });

        stateUpdater.scheduleAtFixedRate(this::updateState, 0, stateDelay, TimeUnit.MILLISECONDS);
    }

    private void handleKeyPressed(KeyEvent event) {
        try {
            switch (event.getCode()) {
                case W, UP -> apiClient.steerSnake(Direction.UP);
                case A, LEFT -> apiClient.steerSnake(Direction.LEFT);
                case S, DOWN -> apiClient.steerSnake(Direction.DOWN);
                case D, RIGHT -> apiClient.steerSnake(Direction.RIGHT);
            }
        } catch (ApiErrorException e) {
            log.error(e.getMessage());
            Platform.runLater(() -> {
                GameOverController gameOverController = fxWeaver.loadController(GameOverController.class);
                gameOverController.initialize(player == null ? 0 : player.getScore());

                Stage stage = (Stage) field.getScene().getWindow();
                stage.setScene(new Scene(fxWeaver.loadView(GameOverController.class)));
                stage.setFullScreen(true);
            });
            apiClient.close();
            stateUpdater.shutdownNow();
        }
    }

    @FXML
    public void handleExit(ActionEvent event) {
        try {
            apiClient.exitGame();
        } catch (ApiErrorException e) {
            log.error(e.getMessage());
            ErrorPopup.show(e.getMessage());
        }

        stateUpdater.shutdownNow();
        apiClient.close();

        GameOverController gameOverController = fxWeaver.loadController(GameOverController.class);
        gameOverController.initialize(player == null ? 0 : player.getScore());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxWeaver.loadView(GameOverController.class)));
        stage.setFullScreen(true);
    }

    private void updateState() {
        GameStateDto gameState;
        try {
            gameState = apiClient.getGameState();
        } catch (ApiErrorException e) {
            log.error(e.getMessage());
            Platform.runLater(() -> {
                GameOverController gameOverController = fxWeaver.loadController(GameOverController.class);
                gameOverController.initialize(player == null ? 0 : player.getScore());

                Stage stage = (Stage) field.getScene().getWindow();
                stage.setScene(new Scene(fxWeaver.loadView(GameOverController.class)));
                stage.setFullScreen(true);
            });
            apiClient.close();
            stateUpdater.shutdownNow();
            return;
        }

        Platform.runLater(() -> {
            field.getChildren().clear();
            initializeField();
        });
        double cellSize = getCellSize();

        // Add foods
        for (CoordDto coord : gameState.getFoods()) {
            ImageView imageView = new ImageView("img/food.png");
            imageView.setFitHeight(cellSize);
            imageView.setFitWidth(cellSize);

            VBox cell = new VBox(imageView);
            cell.setPrefWidth(cellSize);
            cell.setPrefHeight(cellSize);

            Platform.runLater(() -> field.add(cell, coord.getX(), coord.getY()));
        }

        // Add snakes
        int currentPlayerId = gameState.getPlayers()
                .stream()
                .filter(player1 -> player1.getName().equals(playerName))
                .findFirst()
                .map(PlayerDto::getId)
                .orElse(-1);
        for (SnakeDto snake : gameState.getSnakes()) {
            Background snakeColor;
            if (currentPlayerId == snake.getPlayerId()) {
                snakeColor = new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY));
            } else {
                snakeColor = new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY));
            }

            int x = snake.getPoints().get(0).getX();
            int y = snake.getPoints().get(0).getY();
            int finalX1 = x;
            int finalY1 = y;
            Platform.runLater(() -> field.add(createCell(cellSize, snakeColor), finalX1, finalY1));

            for (int i = 1; i < snake.getPoints().size(); i++) {
                CoordDto coord = snake.getPoints().get(i);

                for (int j = 0; j < coord.getX(); j++) {
                    x = (x + 1) % width;
                    int finalX = x;
                    int finalY = y;
                    Platform.runLater(() -> field.add(createCell(cellSize, snakeColor), finalX, finalY));
                }
                for (int j = 0; j < -coord.getX(); j++) {
                    x = (x - 1 + width) % width;
                    int finalX = x;
                    int finalY = y;
                    Platform.runLater(() -> field.add(createCell(cellSize, snakeColor), finalX, finalY));
                }
                for (int j = 0; j < coord.getY(); j++) {
                    y = (y + 1) % height;
                    int finalY = y;
                    int finalX = x;
                    Platform.runLater(() -> field.add(createCell(cellSize, snakeColor), finalX, finalY));
                }
                for (int j = 0; j < -coord.getY(); j++) {
                    y = (y - 1 + height) % height;
                    int finalY = y;
                    int finalX = x;
                    Platform.runLater(() -> field.add(createCell(cellSize, snakeColor), finalX, finalY));
                }
            }
        }

        // Add players
        Platform.runLater(() -> playerList.getChildren().clear());
        List<PlayerDto> sortedPlayers = gameState.getPlayers()
                .stream()
                .sorted(Comparator.comparingInt(PlayerDto::getId))
                .toList();
        for (PlayerDto player1 : sortedPlayers) {
            Label label = new Label();
            label.setText(String.format("%d) %s (%s) - %d points",
                    player1.getId(), player1.getName(), player1.getRole().name(), player1.getScore()));
            label.setFont(new Font("Ticketing", 28));
            Platform.runLater(() -> playerList.getChildren().add(label));

            if (player1.getId() == currentPlayerId) {
                player = player1;
            }
        }
    }

    private double getCellSize() {
        return Math.min(MAX_FIELD_HEIGHT / height, MAX_FIELD_WIDTH / width);
    }

    private void initializeField() {
        double cellSize = getCellSize();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                VBox cell;
                if ((i + j) % 2 == 0) {
                    cell = createCell(cellSize, new Background(
                            new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)
                    ));
                } else {
                    cell = createCell(cellSize, new Background(
                            new BackgroundFill(Color.rgb(255, 240, 255), CornerRadii.EMPTY, Insets.EMPTY)
                    ));
                }
                field.add(cell, j, i);
            }
        }
    }

    private VBox createCell(double size, Background background) {
        VBox cell = new VBox();
        cell.setPrefSize(size, size);
        cell.setBackground(background);
        return cell;
    }
}

package ru.nsu.ccfit.network.p2psnake.desktop.app;


import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.nsu.ccfit.network.p2psnake.desktop.controller.MainController;

@Component
@Slf4j
@RequiredArgsConstructor
public class PrimaryStageInitializer
        implements ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Font.loadFont(PrimaryStageInitializer.class.getClassLoader().getResourceAsStream("font/Arcade.ttf"), 70);
        Font.loadFont(PrimaryStageInitializer.class.getClassLoader().getResourceAsStream("font/Ticketing.ttf"), 70);

        Stage stage = event.stage;
        Scene scene = new Scene(fxWeaver.loadView(MainController.class));
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setOnCloseRequest(event1 -> System.exit(0));
        stage.show();
    }
}



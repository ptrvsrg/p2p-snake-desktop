package ru.nsu.ccfit.network.p2psnake.desktop;

import javafx.application.Application;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.nsu.ccfit.network.p2psnake.desktop.app.JavaFxApplication;

@SpringBootApplication
public class P2pSnakeDesktopApplication {

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
    @Bean
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        return new SpringFxWeaver(applicationContext);
    }
}

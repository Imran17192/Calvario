package de.uniks.stp24;

import de.uniks.stp24.controllers.CreateGameController;
import de.uniks.stp24.dagger.DaggerMainComponent;
import de.uniks.stp24.dagger.MainComponent;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.fulib.fx.FulibFxApp;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public class App extends FulibFxApp {
    @Inject
    CreateGameController createGameController;
    private MainComponent component;
    private Runnable cssFxStop;

    public App() {
        super();

        this.component = DaggerMainComponent.builder().mainApp(this).build();
    }

    // package-private - only for testing
    void setComponent(MainComponent component) {
        this.component = component;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            super.start(primaryStage);

            registerRoutes(component.routes());

            stage().addEventHandler(KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.F5) {
                    this.refresh();
                }
            });

            primaryStage.getScene().getStylesheets().add(Objects.requireNonNull(App.class.getResource("style/styles.css")).toExternalForm());
            cssFxStop = CSSFX.start(primaryStage);

            primaryStage.setWidth(1280);
            primaryStage.setHeight(720);

            primaryStage.centerOnScreen();
            setAppIcon(primaryStage);
            setTaskbarIcon();

            Locale.setDefault(Locale.ENGLISH);

            show("/load");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while starting the application: " + e.getMessage(), e);
        }
    }

    public void cleanUp(){
        super.stop();
        cssFxStop.run();
        autoRefresher().close();
    }

    @Override
    public void stop() {
        cleanUp();
        System.exit(0);
    }


    private void setAppIcon(Stage stage) {
        final Image image = new Image(Objects.requireNonNull(App.class.getResource("icons/gameIcon.png")).toString());
        stage.getIcons().add(image);
    }

    private void setTaskbarIcon() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        try {
            final Taskbar taskbar = Taskbar.getTaskbar();
            final java.awt.Image image = ImageIO.read(Objects.requireNonNull(App.class.getResource("icons/gameIcon.png")));
            taskbar.setIconImage(image);
        } catch (Exception ignored) {
        }
    }

    public MainComponent component() {
        return component;
    }
}

package de.uniks.stp24;

import de.uniks.stp24.controllers.LoadController;
import de.uniks.stp24.controllers.LoginController;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class LoadTest extends ControllerTest {
    @InjectMocks
    LoadController loadController;
    @InjectMocks
    LoginController loginController;

    @Spy
    Subscriber subscriber;


    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        app.show(loadController);
    }

    @Test
    void testLoad() {
        // Start:
        // Alice has started the game STPellar. She sees the Load screen.
        // She is prompted to press a key to continue.
        assertEquals("Calvario", stage.getTitle());

        // Action:
        // Alice presses Enter
        press(KeyCode.ENTER);

        waitForFxEvents();

        // Result:
        // Alice should now be in Login screen.
        assertEquals("LOGIN", stage.getTitle());
    }

}

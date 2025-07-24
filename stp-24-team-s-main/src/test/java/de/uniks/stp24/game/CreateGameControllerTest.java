package de.uniks.stp24.game;

import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.controllers.CreateGameController;
import de.uniks.stp24.service.menu.CreateGameService;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateGameControllerTest extends ControllerTest {

    @Mock
    CreateGameService createGameService;

    @Spy
    Subscriber subscriber;

    @InjectMocks
    CreateGameController createGameController;
    @Spy
    BubbleComponent bubbleComponent;

    @Override
    public void start(Stage stage)  throws Exception{
        super.start(stage);
        bubbleComponent.subscriber = this.subscriber;
        app.show(createGameController);
    }

    //Check if screen changes although there is no input for creating a new game.
    @Test
    void noInput(){
        Button confirmButton = lookup("#createGameConfirmButton").queryButton();
        clickOn(confirmButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(resources.getString("create.game"), stage.getTitle());
    }

    @Test
    void createGameWithInputs(){
        createGameController.setCreateGameService(createGameService);

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#createNameTextField");
        write("testgame95create");

        clickOn("#createPasswordTextField");
        write("1");

        WaitForAsyncUtils.waitForFxEvents();

        Button confirmButton = lookup("#createGameConfirmButton").queryButton();
        clickOn(confirmButton);
        WaitForAsyncUtils.waitForFxEvents();
        //Confirm if the game function that creates the game was invoked
//        verify(createGameService, times(3)).createGame(any(), any(), any());
    }

    @Test
    public void testCancel() {
        doReturn(null).when(app).show("/browseGames");
        clickOn("#createGameCancelButton");
        WaitForAsyncUtils.waitForFxEvents();

        verify(app, times(1)).show("/browseGames");

    }
}


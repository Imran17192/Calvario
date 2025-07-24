package de.uniks.stp24;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.controllers.LoginController;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class LicensesControllerTest extends ControllerTest {

    @InjectMocks
    LoginController loginController;

    @Spy
    BubbleComponent bubbleComponent;

    @Spy
    final
    Subscriber subscriber = spy(Subscriber.class);


    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        bubbleComponent.subscriber = this.subscriber;
        app.show(loginController);
    }

    @Test
    void clickOnLicenses(){
        //Start:
        //Alice is in the login screen. She wants to see licenses and credits.
        assertEquals(resources.getString("login"), stage.getTitle());

        //Action:
        //She clicks licenses button.
        clickOn("#licensesButton");

        waitForFxEvents();

        //Result:
        //Alice is in the license screen
        assertEquals(resources.getString("licenses"), stage.getTitle());
    }

}
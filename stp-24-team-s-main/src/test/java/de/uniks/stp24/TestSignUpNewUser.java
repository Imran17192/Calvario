package de.uniks.stp24;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.controllers.SignUpController;
import de.uniks.stp24.dto.SignUpResultDto;
import de.uniks.stp24.service.menu.SignUpService;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class TestSignUpNewUser extends ControllerTest {
    @Spy
    SignUpService signUpService;
    @Spy
    BubbleComponent bubbleComponent;
    @InjectMocks
    SignUpController signUpController;
    @Spy
    final
    Subscriber subscriber = spy(Subscriber.class);

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        bubbleComponent.subscriber = this.subscriber;
        this.app.show(this.signUpController);
    }

    @Test
    public void testSignup() {
        doReturn(Observable.just(new SignUpResultDto("a", "b", "c", "d", "e" )))
                .when(this.signUpService).register(any(),any());

        assertEquals(resources.getString("register"), stage.getTitle());
        assertTrue(this.signUpController.registerButton.disableProperty().get());

        clickOn("#usernameField");
        write("TemplateUser");
        assertTrue(this.signUpController.registerButton.disableProperty().get());

        clickOn("#passwordField");
        write("TemplateUserPassword");
        assertTrue(this.signUpController.registerButton.disableProperty().get());

        clickOn("#repeatPasswordField");
        write("TemplateUserPassword");
        assertFalse(this.signUpController.registerButton.disableProperty().get());

        clickOn("#registerButton");

        waitForFxEvents();

        verify(this.signUpService, times(1))
                .register("TemplateUser", "TemplateUserPassword");
        assertEquals(resources.getString("login"), stage.getTitle());
        assertEquals(resources.getString("account.registered"), "ACCOUNT REGISTERED!");
    }
}
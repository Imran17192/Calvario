package de.uniks.stp24;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.controllers.LoginController;
import de.uniks.stp24.model.LoginResult;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.TechnologyService;
import de.uniks.stp24.service.menu.LoginService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest extends ControllerTest {
    @Spy
    AuthApiService authApiService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    TechnologyService technologyService;

    @Spy
    final
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    BubbleComponent bubbleComponent;

    @InjectMocks
    LoginService loginService;
    @InjectMocks
    LoginController loginController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);

        this.loginService.technologyService = this.technologyService;
        this.loginController.loginService = this.loginService;
        this.loginController.tokenStorage = this.tokenStorage;
        bubbleComponent.subscriber = this.subscriber;


        app.show(loginController);
    }

    @Test
    void login(){
        Map<String,Integer> _public = new HashMap<>();
        _public.put("backgroundIndex", 1);
        _public.put("portraitIndex", 1);
        _public.put("frameIndex", 1);

        when(authApiService.login(any()))
                .thenReturn(Observable.just(new LoginResult("1", "a", "b", _public, "c", "d")));
        doReturn(null).when(app).show("/browseGames");
        doNothing().when(technologyService).initAllTechnologies();

        // Start:
        // Alice has started the game STPellar. She sees the Log in screen.
        // She has already registered an account and wants to log in.
        assertEquals("LOGIN", stage.getTitle());

        // Action:
        // Alice writes her username and her password
        clickOn("#usernameInput");
        write("alice999");
        clickOn("#passwordInput");
        write("1234");
        clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents();
        // Result:
        // She logged in successfully. She can now navigate through game states
        //verify(loginService, times(1)).login("alice999", "1234", false);
        WaitForAsyncUtils.waitForFxEvents();
        verify(app, times(1)).show("/browseGames");
    }
    @Test
    void noLoginIfMissingInputs() {

        // Start:
        // Alice is a curious person, who wants to play STPellar.
        // She is in the log in screen and wonders what would happen,
        // if she enters login without entering a name or password.
        assertEquals("LOGIN", stage.getTitle());

        // Action:

        // Alice enters her username "alice999" and password "1234". She clicks Login
        doubleClickOn("#usernameInput");
        write("");
        doubleClickOn("#passwordInput");
        write("");
        clickOn("#loginButton");

        waitForFxEvents();

        // Result:
        // Alice remains in the login screen.
        // The window states that she has put in name and password for login.
        String text = ((TextArea) lookup("#captainText").query()).getText();

        assertFalse((text.isBlank() || text.isEmpty()));

    }

    @Test
    void switchToRegisterScreen(){
        // Title: Switch to register screen
        // Start:
        // Alice wants to start playing STPellar. However, she has no account to play with.

        // Action:
        // Alice clicks register.
        clickOn("#signupButton");

        // Result:
        // Her screen switches to the register window.
        waitForFxEvents();
        assertEquals("REGISTER", stage.getTitle());
    }

    @Test
    void clickOnRememberMe(){
        
        // Start:
        // Alice is in the log in screen.
        // She has put in her username and password. Next time she logs in she wants to use the same account
        assertEquals("LOGIN", stage.getTitle());
        CheckBox rememberMe = (CheckBox) lookup("#rememberMeBox").queryParent();
        assertFalse(rememberMe.isSelected());

        clickOn("#usernameInput");
        write("alice999\t");
        write("1234");

        // Alice activates the remember me checkbox
        // Action:

        clickOn("#rememberMeBox");
//
        // Result:
        // The game will remember now she her account data.

        assertTrue(rememberMe.isSelected());
    }

}

package de.uniks.stp24.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.component.menu.WarningScreenComponent;
import de.uniks.stp24.controllers.EditAccController;
import de.uniks.stp24.model.User;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.menu.EditAccService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestEditAcc extends ControllerTest {
    @Spy
    BubbleComponent bubbleComponent;
    @Spy
    EditAccService editAccService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    final
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    ObjectMapper objectMapper;
    @Spy
    ImageCache imageCache;
    @Spy
    PopupBuilder popupBuilder;
    @Spy
    ErrorService errorService;

    @InjectMocks
    EditAccController editAccController;
    @InjectMocks
    WarningScreenComponent warningScreenComponent;

    final Map<String,Integer>  avatarMap2 = new HashMap<>();

    @Override
    public void start(Stage stage) throws Exception{
        bubbleComponent.subscriber = this.subscriber;
        this.editAccController.avatarMap = new HashMap<>();
        doReturn(null).when(this.imageCache).get(any());

        this.editAccController.avatarMap.put("backgroundIndex", 1);
        this.editAccController.avatarMap.put("portraitIndex", 1);
        this.editAccController.avatarMap.put("frameIndex", 1);

        avatarMap2.put("backgroundIndex", 1);
        avatarMap2.put("portraitIndex", 1);
        avatarMap2.put("frameIndex", 1);

        this.warningScreenComponent.warningText = new Text("waring");

        this.editAccController.warningScreen = this.warningScreenComponent;
        super.start(stage);
        bubbleComponent.subscriber = this.subscriber;
        app.show(editAccController);

    }

    @Test
    void switchToBrowseGamesScreen(){
        doReturn(null).when(app).show("/browseGames");
        // Title: Going back from user administration screen to browse game screen
        // Start: Alice is currently in the user administration screen. She wants to go back to the browse game screen

        // Action: She clicks on go back.
        clickOn("#goBackButton");

        // Result: The window changed to the edit user Game screen window.
        waitForFxEvents();
        verify(app, times(1)).show("/browseGames");
    }

    @Test
    void cancelChangeAccount(){
        // Title: Cancel changing editing account in the user administration window
        final ToggleButton changeUserInfoButton = lookup("#changeUserInfoButton").query();
        final Button cancelChangesButton = lookup("#cancelChangesButton").queryButton();
        final Button goBackButton = lookup("#goBackButton").query();
        final Button deleteUserButton = lookup("#deleteUserButton").query();

        // Start:
        // Alice wants to change her Account name in Calvario. She is in the user administration screen and has clicked on edit account.
        tokenStorage.setName("Alice");

        // The cancelChangesButton is not visible and goBack and deleteUser are enabled
        assertFalse(goBackButton.isDisabled());
        assertFalse(deleteUserButton.isDisabled());
        assertFalse(cancelChangesButton.isVisible());

        clickOn("#changeUserInfoButton");

        // The cancelChangesButton is visible now and goBack and deleteUser are disabled
        assertTrue(cancelChangesButton.isVisible());
        assertTrue(changeUserInfoButton.isSelected());
        assertTrue(goBackButton.isDisabled());
        assertTrue(deleteUserButton.isDisabled());

        // Action:
        // She changes name and or password but changes her mind and clicks canel
        clickOn("#usernameInput");
        write("Calvario");
        clickOn("#passwordInput");
        write("password");
        clickOn("#cancelChangesButton");
        waitForFxEvents();
        // Result:
        // Alice’s Account has not changed, and she is still in the user administration screen.
        TextField username = lookup("#usernameInput").query();
        assertFalse(username.isEditable());
        assertEquals(username.getText(), "Alice");
    }

    @Test
    void changeAccount() {
        Map<String,Integer> _public = new HashMap<>();
        // Title: Confirming edited account
        doReturn(Observable.just(new User("Calvario", "a","b","c","d",_public))).when(editAccService).changeUserInfo(any(),any());

        final TextField username = lookup("#usernameInput").query();
        final TextField password = lookup("#passwordInput").query();

        // Start:
        // Alice wants to change her Account name in Calvario. She is in the user administrtation screen and has clicked on edit account.
        tokenStorage.setName("Alice");
        clickOn("#changeUserInfoButton");

        // Action:
        // She puts in a new name and password. She clicks the confirm button
        clickOn("#usernameInput");
        write("Calvario");
        clickOn("#passwordInput");
        write("password");
        clickOn("#saveChangesButton");
        waitForFxEvents();

        // Result:
        // Alice stays in the same screen but her name and password changed

        assertFalse(username.isEditable());
        assertEquals("Calvario",username.getText());

        assertFalse(password.isEditable());
        assertEquals("",password.getText());
    }

    @Test
    void cancelDeleteAccountTest(){
        //Title: Cancel after clicking delete account button
        //Start:
        //Alice wants to delete her account in STPellaris. She is in the user administration window. She clicked the delete user button and a pop up came up.
        tokenStorage.setName("Alice");
        VBox editAccVBoxRightToBlur = (VBox) lookup("#editAccVBoxRightToBlur").query();
        VBox editAccVBoxLeftToBlur = (VBox) lookup("#editAccVBoxLeftToBlur").query();
        StackPane warningScreenContainer = (StackPane) lookup("#warningScreenContainer").query();
        clickOn("#deleteUserButton");
        waitForFxEvents();

        //Action:
        //She clicks cancel because she changed her mind.
        Button deleteUserButton = lookup("#deleteUserButton").queryButton();
        Button cancelDeleteButton = lookup("#cancelDeleteButton").queryButton();
        assertTrue(cancelDeleteButton.isVisible());
        assertFalse(cancelDeleteButton.isDisabled());
        assertEquals(BoxBlur.class, editAccVBoxLeftToBlur.getEffect().getClass());
        assertEquals(BoxBlur.class, editAccVBoxRightToBlur.getEffect().getClass());

        //Result:
        //The pop up disappeared. Alice stays in the same window and her account was not deleted.
        clickOn("#cancelDeleteButton");
        waitForFxEvents();
        assertNull(editAccVBoxLeftToBlur.getEffect());
        assertNull(editAccVBoxRightToBlur.getEffect());
        assertFalse(warningScreenContainer.isVisible());
        assertTrue(deleteUserButton.isVisible());
        assertFalse(deleteUserButton.isDisabled());
        assertEquals("Alice", tokenStorage.getName());
    }

    @Test
    void deleteAccountTest(){
        // Title: Confirm after clicking delete account button
        doAnswer(show -> { tokenStorage.setName(null);
            tokenStorage.setAvatar(null);
            prefService.removeRefreshToken();
            return Observable.just(new User("1", "a","b","c","d",avatarMap2));
        }).when(this.editAccService).deleteUser();

        // Start:
        // Alice wants to delete her account in Calvario. She is in the user administration window. She clicked the delete user button and a pop up came up.
        tokenStorage.setName("Alice");
        clickOn("#deleteUserButton");
        waitForFxEvents();

        // Action:
        // She clicks confirm.
        Button deleteAccButton = lookup("#deleteAccButton").queryButton();
        clickOn("#deleteAccButton");
        waitForFxEvents();

        // Result:
        // The pop up disappeared. Alice is now in the log in window again.
        assertNull(tokenStorage.getName());
        assertEquals(resources.getString("login"), stage.getTitle());
    }

}

package de.uniks.stp24.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.component.menu.GameComponent;
import de.uniks.stp24.component.menu.LogoutComponent;
import de.uniks.stp24.component.menu.WarningComponent;
import de.uniks.stp24.controllers.BrowseGameController;
import de.uniks.stp24.controllers.EditAccController;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.LogoutResult;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.menu.BrowseGameService;
import de.uniks.stp24.service.menu.CreateGameService;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import javax.inject.Provider;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class BrowseGameControllerTest extends ControllerTest {
    @Spy
    GamesApiService gamesApiService;
    @Spy
    GameMembersApiService gameMembersApiService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ImageCache imageCache;
    @Spy
    PopupBuilder popupBuilder;
    @Spy
    BrowseGameService browseGameService;
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    CreateGameService createGameService;
    @Spy
    EditGameService editGameService;
    @Spy
    EmpireService empireService;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    ErrorService errorService;
    @Spy
    LobbyService lobbyService;
    @Spy
    final
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @InjectMocks
    LogoutComponent logoutComponent;
    @InjectMocks
    BubbleComponent bubbleComponent;
    @InjectMocks
    WarningComponent warningComponent;
    @Spy
    EditAccController editAccController;
    @Mock
    Comparable<Game> comparable;


    final Game game = new Game("11", null, "1", "Was geht", "testID2", 2,0, false, 0,0, null);

    Provider<GameComponent> GameComponentProvider = () -> new GameComponent(bubbleComponent, browseGameService, editGameService, tokenStorage,resources);

    @InjectMocks
    BrowseGameController browseGameController;


    final Subject<Event<Game>> subject = BehaviorSubject.create();

    @Override
    public void start(Stage stage)  throws Exception{
        browseGameController.logoutComponent = logoutComponent;
        browseGameController.bubbleComponent = bubbleComponent;
        browseGameController.warningComponent = warningComponent;

        Mockito.doReturn(Observable.just(List.of(
                game,
                new Game("88888", null, "2", "rapapa", "testID", 2,0, false, 0,0, null)
        ))).when(gamesApiService).findAll();

        Mockito.doReturn(subject).when(eventListener).listen("games.*.*", Game.class);
        doReturn(null).when(this.imageCache).get(any());

        super.start(stage);
        app.show(browseGameController);
    }

    /*
    ============================================= Test browse game buttons =============================================
     */

    @Test
    void logOut(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(resources.getString("browse.game"), stage.getTitle());
        clickOn(browseGameController.log_out_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(lookup("#logoutButton").queryButton());
    }

    @Test
    void newGame(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(resources.getString("browse.game"), stage.getTitle());
        clickOn(browseGameController.new_game_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(resources.getString("create.game"), stage.getTitle());
    }

    @Test
    void loadGame(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(resources.getString("browse.game"), stage.getTitle());
        clickOn(browseGameController.load_game_b);
        WaitForAsyncUtils.waitForFxEvents();
        //TODO: Wait for PR LoadGame
        //assertEquals("Load Game", stage.getTitle());
    }

    @Test
    void deleteGame(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(resources.getString("browse.game"), stage.getTitle());
        clickOn(browseGameController.del_game_b);
        WaitForAsyncUtils.waitForFxEvents();
        //TODO: Wait for PR Delete Game
        //assertEquals("Delete Game", stage.getTitle());
    }

    @Test
    void editAcc(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(resources.getString("browse.game"), stage.getTitle());
        clickOn(browseGameController.edit_acc_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(resources.getString("edit.account"), stage.getTitle());
    }

    /*
    ============================================= Test ListView of Lobby =============================================
     */

    @Test
    void testLobbyList(){
        //Create new Game and check if game is listed on ListView
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2, browseGameController.gameList.getItems().size());
        subject.onNext(new Event<>("games.3.created", new Game("22", null, "3", "taschaka", "testID2", 2, 0,false, 0,0, null)));

        //Delete existing game and check if game is still listed or not.
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(3, browseGameController.gameList.getItems().size());
        subject.onNext(new Event<>("games.652.deleted", new Game("22", null, "2", "rapapa", "testID", 2, 0,false, 0,0, null)));

        //Check amount of Listview items
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2, browseGameController.gameList.getItems().size());
    }

    /*
    ============================================= Test edit game =========================================================
     */

    @Test
    void editGameNoInputs(){
        //Set selected Game as one of the games u have created
        WaitForAsyncUtils.waitForFxEvents();
        browseGameController.browseGameService.setGame(browseGameController.gameList.getItems().getFirst());
        browseGameController.browseGameService.setTokenStorage();

        browseGameController.gameList.getSelectionModel().clearAndSelect(0);
        browseGameController.gameList.getFocusModel().focus(0);

        //Click on edit game button and check if edit game screen is now displayed.
        clickOn(browseGameController.edit_game_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(resources.getString("edit.game"), stage.getTitle());

        //Click on confirm. No inputs for change was given. Screen do not change
        WaitForAsyncUtils.waitForFxEvents();
        Button confirmButton = lookup("#editGameConfirmButton").queryButton();
        clickOn(confirmButton);
        assertEquals(resources.getString("edit.game"), stage.getTitle());
    }

    @Test
    void editNotPossible(){
        //Game which is not yours is selected for edit.
        WaitForAsyncUtils.waitForFxEvents();
        browseGameController.browseGameService.setGame(browseGameController.gameList.getItems().get(1));
        browseGameController.browseGameService.setTokenStorage();

        browseGameController.gameList.getSelectionModel().clearAndSelect(1);
        browseGameController.gameList.getFocusModel().focus(1);

        //Click on confirm changes nothing. Screen is still browse game.
        clickOn(browseGameController.edit_game_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(resources.getString("browse.game"),stage.getTitle());
    }

    @Test
    void deleteGameCancel(){
        //doNothing().when(warningComponent).onCancel();
        //doNothing().when(warningComponent).setGameName();
        WaitForAsyncUtils.waitForFxEvents();
        browseGameController.browseGameService.setGame(browseGameController.gameList.getItems().get(0));
        browseGameController.browseGameService.setTokenStorage();

        browseGameController.gameList.getSelectionModel().clearAndSelect(0);
        browseGameController.gameList.getFocusModel().focus(0);

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#del_game_b");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#cancelButton");
//        assertFalse(warningComponent.getParent().isVisible());
        //verify(this.warningComponent).onCancel();
    }

    @Test
    void deleteGameConfirm(){
        //doNothing().when(warningComponent).setGameName();
        //doNothing().when(warningComponent).deleteGame();
//        doReturn(Observable.just(new Game("1", "a","b","c","d", 2,0, true,4, 5, null)) ).when(this.browseGameService).deleteGame();
        WaitForAsyncUtils.waitForFxEvents();
        browseGameController.browseGameService.setGame(browseGameController.gameList.getItems().get(0));
        browseGameController.browseGameService.setTokenStorage();

        browseGameController.gameList.getSelectionModel().clearAndSelect(0);
        browseGameController.gameList.getFocusModel().focus(0);

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#del_game_b");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#confirmButton");
//        verify(browseGameService, times(1)).deleteGame();
        //verify(this.warningComponent).deleteGame();
    }

    @Test
    public void clickOnLogout() {
        prefService.setRefreshToken("lastRefreshToken");
        doReturn(Observable.just(new LogoutResult("a")))
                .when(browseGameService).logout(any());
        doReturn(null).when(app).show("/login");

        // Start
        // Alice is playing Calvario and wants to log out.
        // The game's prefService contains a refresh token for her account
        // The browse games screen is being shown
        assertEquals(resources.getString("browse.game"), stage.getTitle());
        assertNotNull(prefService.getRefreshToken());

        // Alice clicks on logout
        clickOn("#log_out_b");

        waitForFxEvents();

        clickOn("#logoutButton");
        // it's necessary to generate the observable, that the test mocks,
        // because with it a .doOnNext(...) should be invoke
        Observable<LogoutResult> observable = browseGameService.logout("");
        observable.doOnComplete(() -> prefService.removeRefreshToken()).subscribe();
        waitForFxEvents();

        // Alice sees now the login screen
        // The game's prefService does not contain a refresh token for her account
        verify(browseGameService, times(2)).logout("");
        verify(app, times(1)).show("/login");
        assertNull(prefService.getRefreshToken());
    }


    @Test
    public void clickOnCancel() {
        // Start:
        // Alice has unintended clicked the logout button
        // and see the logout screen
        assertEquals(resources.getString("browse.game"), stage.getTitle());

        // Alice clicks on cancel
        clickOn("#log_out_b");
        waitForFxEvents();

        clickOn("#cancelButton");

        waitForFxEvents();

        // Alice return to the browse game login screen
        // must be fixed when browsegames.fxml is available
        assertEquals(resources.getString("browse.game"), stage.getTitle());
    }

}

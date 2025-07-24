
package de.uniks.stp24.controller.lobby;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.menu.*;
import de.uniks.stp24.controllers.LobbyController;
import de.uniks.stp24.dto.JoinGameDto;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.model.MemberUser;
import de.uniks.stp24.model.User;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.menu.GamesService;
import de.uniks.stp24.service.menu.JoinGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestLobbyControllerAsNewUser extends ControllerTest {
    @Spy
    TokenStorage tokenStorage;
    @Spy
    GamesApiService gamesApiService;
    @Spy
    GameMembersApiService gameMembersApiService;
    @Spy
    ImageCache imageCache;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    AuthApiService authApiService;
    @Spy
    JoinGameService joinGameService;
    @Spy
    UserApiService userApiService;
    @Spy
    LobbyService lobbyService;
    @Spy
    GamesService gamesService;
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    final
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @InjectMocks
    UserComponent userComponent;
    @InjectMocks
    EnterGameComponent enterGameComponent;
    @InjectMocks
    LobbySettingsComponent lobbySettingsComponent;
    @InjectMocks
    LobbyHostSettingsComponent lobbyHostSettingsComponent;
    @InjectMocks
    LobbyController lobbyController;
    @InjectMocks
    BubbleComponent bubbleComponent;

    final Provider<UserComponent> userComponentProvider = ()-> new UserComponent(imageCache, resources);

    final Subject<Event<MemberDto>> memberSubject = BehaviorSubject.create();
    final Subject<Event<Game>> gameSubject = BehaviorSubject.create();

    @Override
    public void start(Stage stage) throws Exception {
        Map<String,Integer> _public = new HashMap<>();
        _public.put("backgroundIndex", 1);
        _public.put("portraitIndex", 1);
        _public.put("frameIndex", 1);

        super.start(stage);

        this.joinGameService.gameMembersApiService = this.gameMembersApiService;

        this.lobbyController.bubbleComponent = this.bubbleComponent;
        this.lobbyController.lobbyHostSettingsComponent = this.lobbyHostSettingsComponent;
        this.lobbyController.lobbySettingsComponent = this.lobbySettingsComponent;
        this.lobbyController.enterGameComponent = this.enterGameComponent;
        this.lobbyController.userComponent = this.userComponent;
        this.lobbyController.userComponentProvider = this.userComponentProvider;

        // Mock getting userID
        doReturn("testNewUserID").when(this.tokenStorage).getUserId();

        // Mock getting game
        doReturn(Observable.just(new Game("1", "a","testGameID","testGame","testGameHostID", 2,
                0, false, 1, 0, new GameSettings(1))))
                .when(this.gamesService).getGame(any());

        // Mock listen to game deletion
        doReturn(gameSubject).when(this.eventListener).listen(eq("games.testGameID.deleted"), eq(Game.class));

        // Mock loading lobby members
        doReturn(Observable.just(new MemberDto[]{
                new MemberDto(false, "testGameHostID", null, "88888888"),
                new MemberDto(false, "testMemberUnoID", null, "88888888"),
                new MemberDto(false, "testMemberDosID", null, "88888888")
        })).when(this.lobbyService).loadPlayers(any());

        // Mock getting user
        when(this.userApiService.getUser(any()))
                .thenReturn(Observable.just(new User("gameHost", "testGameHostID", null, "1", "1",_public)))
                .thenReturn(Observable.just(new User("testMemberUno", "testMemberUnoID", null, "1", "1",_public)))
                .thenReturn(Observable.just(new User("testMemberDos", "testMemberDosID", null, "1", "1",_public)))
                .thenReturn(Observable.just(new User("testNewUser", "testNewUserID", null, "1", "1",_public)));

        // Mock getting members updates
        doReturn(memberSubject).when(this.eventListener).listen(eq("games.testGameID.members.*.*"), eq(MemberDto.class));

        this.app.show(this.lobbyController);

        this.lobbyController.backgroundAnchorPane.getStylesheets().clear();

        doReturn(gameSubject).when(this.eventListener).listen(eq("games.testGameID.updated"),eq(Game.class));

        doReturn(null).when(imageCache).get(any());
    }

    /**
     * Collective method to test all functions of the lobby from the view of a member.
     */
    @Test
    public void testLobbyFunctionsAsNewUser() {
        this.testOpenLobbyAsNewUser();
    }

    /**
     * Tests the behavior of the lobby when new user switches to lobby screen.
     */
    @Test
    public void testOpenLobbyAsNewUser() {
        WaitForAsyncUtils.waitForFxEvents();

        // Test if the correct component is shown to the user
        Node component = this.lobbyController.lobbyElement.getChildren().getFirst();
        assertEquals(EnterGameComponent.class, component.getClass());

        // Test if the new user is not a member of the lobby
        assertFalse(this.lobbyController.playerListView.getItems().stream()
                .map(MemberUser::user)
                .anyMatch(user -> user._id().equals("testNewUserID")));
    }

    @Test
    public void testJoinLobbyAsNewUser() {
        WaitForAsyncUtils.waitForFxEvents();

        when(this.joinGameService.joinGame(anyString(), any(), any()))
                .thenReturn(Observable.error(new IllegalAccessException()))
                .thenReturn(Observable.just(new JoinGameDto()));

//        doReturn(Observable.just(new JoinGameDto())).when(this.joinGameService).joinGame(anyString(), any(), any());

        // Test pressing enter with no password entered
        clickOn("#joinButton");

        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(this.resources.getString("pirate.enterGame.noPassword"),
                lookup("#errorMessage").queryText().getText());

        // Test inputting the incorrect password
        clickOn("#passwordInputField");
        write("1");

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#joinButton");

        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(this.resources.getString("pirate.enterGame.wrongPassword").replace("{password}", "1"),
                lookup("#errorMessage").queryText().getText());

        // Test inputting the correct password
        clickOn("#passwordInputField");
        write("88888888");

        clickOn("#joinButton");
        this.memberSubject.onNext(new Event<>("games.testTestID.members.testNewUserID.created",
                new MemberDto(false, "testNewUserID", null, "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(lookup("#lobbyElement").queryAs(StackPane.class).getChildren().getLast().getClass(),
                LobbySettingsComponent.class);
        assertEquals(4, this.lobbyController.playerListView.getItems().size());

        MemberUser newUser = this.lobbyController.playerListView.getItems().getLast();
        assertTrue(newUser.user().name().contains("testNewUser")
                && !newUser.ready());
    }

    /**
     * Test the proper switching to game browser by pressing the cancel button.
     */
    @Test
    public void leaveEnterGameScreen() {
        doReturn(null).when(this.app).show("/browseGames");

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#cancelButton");

        verify(this.app, times(1)).show("/browseGames");
    }
}


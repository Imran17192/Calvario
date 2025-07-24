
package de.uniks.stp24.controller.lobby;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.menu.*;
import de.uniks.stp24.controllers.LoadingScreenController;
import de.uniks.stp24.controllers.LobbyController;
import de.uniks.stp24.controllers.helper.JoinGameHelper;
import de.uniks.stp24.dto.JoinGameDto;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.menu.GamesService;
import de.uniks.stp24.service.menu.JoinGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.Node;
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
public class TestLobbyControllerAsMember extends ControllerTest {
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
    EmpireService empireService;
    @Spy
    ErrorService errorService;
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    final
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);

    final Provider<UserComponent> userComponentProvider = ()-> new UserComponent(imageCache, resources);

    @InjectMocks
    JoinGameHelper joinGameHelper;
    @InjectMocks
    LoadingScreenController loadingScreenController;
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

    final Subject<Event<MemberDto>> memberSubject = BehaviorSubject.create();
    final Subject<Event<Game>> gameSubject = BehaviorSubject.create();


    @Override
    public void start(Stage stage) throws Exception{
        Map<String,Integer> _public = new HashMap<>();
        _public.put("backgroundIndex", 1);
        _public.put("portraitIndex", 1);
        _public.put("frameIndex", 1);

        super.start(stage);

        this.lobbyController.bubbleComponent = this.bubbleComponent;
        this.lobbyController.lobbyHostSettingsComponent = this.lobbyHostSettingsComponent;
        this.lobbyController.lobbySettingsComponent = this.lobbySettingsComponent;
        this.lobbyController.enterGameComponent = this.enterGameComponent;
        this.lobbyController.userComponent = this.userComponent;
        this.lobbyController.userComponentProvider = this.userComponentProvider;
        this.lobbyController.joinGameHelper = this.joinGameHelper;
        this.loadingScreenController.empireService = this.empireService;
        this.loadingScreenController.islandsService = this.islandsService;
        this.loadingScreenController.subscriber = this.subscriber;


        // Mock getting userID
        doReturn("testMemberUnoID").when(this.tokenStorage).getUserId();

        // Mock getting game
        doReturn(Observable.just(new Game("1", "a","testGameID","testGame","testGameHostID", 2,
                0, false, 1, 0, new GameSettings(1))))
                .when(this.gamesService).getGame(any());

        // Mock listen to game deletion
        doReturn(gameSubject).when(this.eventListener).listen(eq("games.testGameID.deleted"), eq(Game.class));

        // Mock listen to game update
        doReturn(gameSubject).when(this.eventListener).listen(eq("games.testGameID.updated"), eq(Game.class));

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
                .thenReturn(Observable.just(new User("testMemberDos", "testMemberDosID", null, "1", "1",_public)));

        // Mock getting members updates
        doReturn(memberSubject).when(this.eventListener).listen(eq("games.testGameID.members.*.*"), eq(MemberDto.class));

        this.app.show(this.lobbyController);

        this.lobbyController.backgroundAnchorPane.getStylesheets().clear();

        doReturn(null).when(imageCache).get(any());
    }

    /**
     * Tests the behavior of the lobby when the joining player is the host of this game.
     */
    @Test
    public void testJoinLobbyAsMember() {
        WaitForAsyncUtils.waitForFxEvents();

        // Test if one of the users is a joined member
        User member = this.lobbyController.playerListView.getItems().stream()
                .map(MemberUser::user)
                .filter(user -> user._id().equals("testMemberUnoID"))
                .findFirst().orElseThrow();
        assertEquals(3, this.lobbyController.playerListView.getItems().size());
        assertTrue(member.name().contains("testMemberUno"));

        // Test if the correct component is shown to the member
        Node component = this.lobbyController.lobbyElement.getChildren().getFirst();
        assertEquals(LobbySettingsComponent.class, component.getClass());
    }

    /**
     * Test the proper removal of the user from member list after leaving.
     */
    @Test
    public void testLeaveLobbyAsMember() {
        doReturn(null).when(this.app).show("/browseGames");
        doReturn(Observable.just(new JoinGameDto())).when(this.lobbyService).leaveLobby(any(), any());

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#leaveLobbyButton");

        this.memberSubject.onNext(new Event<>("games.testGameID.members.*.deleted",
                new MemberDto(false, "testMemberUnoID", null, "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2, this.lobbyController.playerListView.getItems().size());
        assertFalse(this.lobbyController.playerListView.getItems().stream()
                .map(MemberUser::user).map(User::_id)
                .anyMatch(id -> id.equals("tetMemberUnoID"))
        );

        verify(this.eventListener, times(1)).listen("games.testGameID.members.*.*", MemberDto.class);
        verify(this.app, times(1)).show("/browseGames");
    }

    /**
     * Test proper changing of member's status after pressing the ready button on certain occasions.
     */
    @Test
    public void testPressReadyAsMember() {
        doReturn(Observable.just(new MemberDto(false, "testMemberUnoID", null, "88888888")))
                .when(this.lobbyService).getMember(any(), any());

        doReturn(Observable.just(new MemberDto(false, "testMemberUnoID", null, "88888888")))
                .when(this.lobbyService).updateMember(anyString(), anyString(), anyBoolean(), any());

        MemberUser member = this.lobbyController.playerListView.getItems().stream()
                .filter(memberUser -> memberUser.user()._id().equals("testMemberUnoID"))
                .findFirst().orElseThrow();
        assertFalse(member.ready());

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#readyButton");

        // Test readiness update on user that has not yet selected an empire
        this.memberSubject.onNext(new Event<>("games.testGameID.members.testMemberUnoID.updated",
                new MemberDto(true, "testMemberUnoID", null, "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        member = this.lobbyController.playerListView.getItems().stream()
                .filter(memberUser -> memberUser.user()._id().equals("testMemberUnoID"))
                .findFirst().orElseThrow();
        assertTrue(member.ready());

        // Test readiness update on user that has selected an empire
        this.memberSubject.onNext(new Event<>("games.testGameID.members.testMemberUnoID.updated",
                new MemberDto(false, "testMemberUnoID",
                        new Empire(null, null, null, 0, 0, null, null),
                        "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#readyButton");
        this.memberSubject.onNext(new Event<>("games.testGameID.members.testMemberUnoID.updated",
                new MemberDto(true, "testMemberUnoID",
                        new Empire(null, null, null, 0, 0, null, null),
                        "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        member = this.lobbyController.playerListView.getItems().stream()
                .filter(memberUser -> memberUser.user()._id().equals("testMemberUnoID"))
                .findFirst().orElseThrow();
        assertTrue(member.ready());
        assertFalse(member.user().name().contains("(Spectator)"));
    }

    /**
     * Tests proper switching to the empire selection screen.
     */
    @Test
    public void testSwitchToEmpireSelection() {
        doReturn(null).when(this.app).show(eq("/creation"), any());

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#selectEmpireButton");


        WaitForAsyncUtils.waitForFxEvents();

        verify(this.app, times(1)).show(eq("/creation"), any());
    }


    /**
     * Tests being kicked from the lobby by host.
     */
    @Test
    public void testBeingKickedFromLobbyByHost() {
        WaitForAsyncUtils.waitForFxEvents();

        this.memberSubject.onNext(new Event<>("games.testGameID.members.testMemberUnoID.deleted",
                new MemberDto(true, "testMemberUnoID", new Empire(null, null, null,
                        0, 0, null, null), "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(lookup("#lobbyMessageElement").query().isVisible());
        assertTrue(lookup("#messageText").queryText().getText().contains("kicked"));
    }


    /**
     * Test the proper message appearing if the host deletes the lobby.
     */
    @Test
    public void testOnLobbyDeletion() {
        doReturn(null).when(this.app).show("/browseGames");
        doReturn(Observable.just(new JoinGameDto()))
                .when(this.lobbyService).leaveLobby(any(), any());

        WaitForAsyncUtils.waitForFxEvents();

        this.gameSubject.onNext(new Event<>("games.testGameID.deleted",
                new Game("1", "a","testGameID","testGame","testGameHostID", 2,
                        0, false, 1, 0, new GameSettings(1))));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(lookup("#lobbyMessageElement").query().isVisible());
        assertTrue(lookup("#messageText").queryText().getText().contains("deleted"));
        clickOn("#returnButton");

        verify(this.app, times(1)).show("/browseGames");
    }

    @Test
    public void startGameAsPlayer(){
        WaitForAsyncUtils.waitForFxEvents();

        Empire testEmpire = new Empire("testEmpire", "a","a", 1,  1, null, "a");

        doReturn(null).when(this.app).show("/ingame");
        doReturn(null).when(this.app).show("/loading-screen");

        doAnswer(show-> {app.show("/ingame");
            return null;
        }).when(this.islandsService).retrieveIslands(any(), anyBoolean());

        doReturn(Observable.just(new ReadEmpireDto[]{new ReadEmpireDto("1","a","testEmpireID", "testGameID",
                "testMemberUnoID","testGame","a","a",1, 2, "a")})).when(this.empireService).getEmpires(any());

        doReturn(Observable.just(new MemberDto(true, "testMemberUnoID", testEmpire, "88888888")))
                .when(this.lobbyService).getMember(any(), any());

        // start game
        this.gameSubject.onNext(new Event<>("games.testGameID.updated",
                new Game("1", "a","testGameID","testGame","testGameHostID", 2,
                        0, true, 1, 0, new GameSettings(1))));

        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("testEmpireID", tokenStorage.getEmpireId());
        assertEquals("testGameID", tokenStorage.getGameId());
        assertFalse(tokenStorage.isSpectator());

        verify(this.app, times(1)).show("/ingame");
    }

    @Test
    public void startGameAsSpectator(){
        WaitForAsyncUtils.waitForFxEvents();

        Empire testEmpire = new Empire("testEmpire", "a","a", 1,  1, null, "a");

        doReturn(null).when(this.app).show("/ingame");

        doAnswer(show-> {app.show("/ingame");
            return null;
        }).when(this.islandsService).retrieveIslands(any(),anyBoolean());

        doReturn(Observable.just(new ReadEmpireDto[]{new ReadEmpireDto("1","a","testEmpireID", "testGameID",
                "testGameHostID","testGame","a","a",1, 2, "a")})).when(this.empireService).getEmpires(any());

        doReturn(Observable.just(new MemberDto(true, "testMemberUnoID", null, "88888888")))
                .when(this.lobbyService).getMember(any(), any());

        // start game
        this.gameSubject.onNext(new Event<>("games.testGameID.updated",
                new Game("1", "a","testGameID","testGame","testGameHostID", 2,
                        0, true, 1, 0, new GameSettings(1))));

        WaitForAsyncUtils.waitForFxEvents();

        assertNull(tokenStorage.getEmpireId());
        assertEquals("testGameID", tokenStorage.getGameId());
        assertTrue(tokenStorage.isSpectator());

        verify(this.app, times(1)).show("/ingame");
    }
}


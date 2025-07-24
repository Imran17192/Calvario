package de.uniks.stp24.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.component.menu.GameComponent;
import de.uniks.stp24.component.menu.LogoutComponent;
import de.uniks.stp24.component.menu.WarningComponent;
import de.uniks.stp24.controllers.BrowseGameController;
import de.uniks.stp24.controllers.LoadingScreenController;
import de.uniks.stp24.controllers.helper.JoinGameHelper;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.model.Empire;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.menu.BrowseGameService;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import javax.inject.Provider;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestEnterGame extends ControllerTest {
    @Spy
    GamesApiService gamesApiService;
    @Spy
    GameMembersApiService gameMembersApiService;
    @Spy
    UserApiService userApiService;
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
    JoinGameHelper joinGameHelper;
    @InjectMocks
    LoadingScreenController loadingScreenController;
    @InjectMocks
    LogoutComponent logoutComponent;
    @InjectMocks
    BubbleComponent bubbleComponent;
    @InjectMocks
    WarningComponent warningComponent;
    @InjectMocks
    BrowseGameController browseGameController;

    Provider<GameComponent> GameComponentProvider = () -> new GameComponent(bubbleComponent, browseGameService, editGameService, tokenStorage,resources);
    final Subject<Event<Game>> gameSubject = BehaviorSubject.create();
    final Game game1 = new Game("a", null, "game1Id", "testGame1", "testHost1", 2,0, true, 1,10, null);
    final Game game2 = new Game("a", null, "game2Id", "testGame2", "testHost2", 2,0, false, 1,10, null);
    final Game game3 = new Game("a", null, "game3Id", "testGame3", "testHost3", 2,0, true, 0,0, null);


    @Override
    public void start(Stage stage)  throws Exception{
        super.start(stage);
        browseGameController.logoutComponent = logoutComponent;
        browseGameController.bubbleComponent = bubbleComponent;
        browseGameController.warningComponent = warningComponent;
        browseGameController.joinGameHelper = joinGameHelper;
        lobbyService.gameMembersApiService = gameMembersApiService;
        loadingScreenController.empireService = this.empireService;
        this.loadingScreenController.islandsService = this.islandsService;
        this.loadingScreenController.subscriber = this.subscriber;

        doReturn(Observable.just(List.of(
                game1, game2, game3
        ))).when(gamesApiService).findAll();

        Mockito.doReturn(gameSubject).when(eventListener).listen("games.*.*", Game.class);

        // Mock userId
        doReturn("testUserID").when(this.tokenStorage).getUserId();

        doReturn(null).when(this.app).show("/loading-screen");
        // Mock show ingame
        doReturn(null).when(this.app).show("/ingame");
        doAnswer(show-> {app.show("/ingame");
            return null;
        }).when(this.islandsService).retrieveIslands(any(), anyBoolean());

        app.show(browseGameController);
    }


    @Test
    public void loadGameAsMember(){
        WaitForAsyncUtils.waitForFxEvents();

        Empire testEmpire = new Empire("testEmpire", "a","a", 1,  1, null, "a");

        // Mock get all Empires of the game
        doReturn(Observable.just(new ReadEmpireDto[]{new ReadEmpireDto("1","a","testEmpireID", "testGameID",
                "testUserID","testEmpire1","a","a",1, 2, "a")
        })).when(this.empireService).getEmpires(any());

        // Mock the members of the different games
        when(this.lobbyService.getMember(any(),any()))
                .thenReturn(Observable.error(new RuntimeException("Server error")))
                .thenReturn(Observable.just(new MemberDto(true, "testGameHostID", testEmpire, "88888888")))
                .thenReturn(Observable.just(new MemberDto(true, "testUserID", testEmpire,"88888888")));


        // Try to enter first game: user is not a member, game has already started -> not possible
        browseGameController.gameList.getSelectionModel().clearAndSelect(0);
        browseGameController.gameList.getFocusModel().focus(0);
        browseGameService.setGame(game1);

        clickOn("#load_game_b");
        WaitForAsyncUtils.waitForFxEvents();

        assertNull(tokenStorage.getGameId());
        assertNull(tokenStorage.getEmpireId());
        verify(this.app, times(0)).show("/ingame");
        verify(this.app, times(0)).show("/lobby", Map.of("gameid","testGame1" ));


        // Try to enter second game: user is not a member, game has not started yet -> show lobby
        browseGameController.gameList.getSelectionModel().clearAndSelect(1);
        browseGameController.gameList.getFocusModel().focus(1);
        browseGameService.setGame(game2);
        String gameId = browseGameService.getGame()._id();
        doReturn(null).when(this.app).show("/lobby", Map.of("gameid",gameId));

        clickOn("#load_game_b");
        WaitForAsyncUtils.waitForFxEvents();

        assertNull(tokenStorage.getGameId());
        assertNull(tokenStorage.getEmpireId());
        verify(this.app, times(0)).show("/ingame");
        verify(this.app, times(1)).show("/lobby", Map.of("gameid",gameId));


        // Try to enter third game: user is a member, game has already started -> user will go to ingame
        browseGameController.gameList.getSelectionModel().clearAndSelect(2);
        browseGameController.gameList.getFocusModel().focus(2);
        browseGameService.setGame(game3);

        clickOn("#load_game_b");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("game3Id", tokenStorage.getGameId());
        assertEquals("testEmpireID", tokenStorage.getEmpireId());
        assertFalse(tokenStorage.isSpectator());
        verify(this.app, times(1)).show("/ingame");
    }

    @Test
    public void rejoinGameAsSpectator() {
        WaitForAsyncUtils.waitForFxEvents();

        // Mock get all Empires of the game1
        doReturn(Observable.just(new ReadEmpireDto[]{new ReadEmpireDto("1","a","testEmpireID", "game1Id",
                "testHost1","testEmpire","a","a",1, 2, "a")
        })).when(this.empireService).getEmpires(any());


        // Mock the requested member of the game (the user without an empire, not the host)
        when(this.lobbyService.getMember(any(),any()))
                .thenReturn(Observable.just(new MemberDto(true, "testUserID", null,"88888888")));

        assertNull(tokenStorage.getGameId());
        assertNull(tokenStorage.getEmpireId());

        // Try to enter first game: user is a member, but has no empire; game has already started -> go to ingame without an empire
        browseGameController.gameList.getSelectionModel().clearAndSelect(0);
        browseGameController.gameList.getFocusModel().focus(0);
        browseGameService.setGame(game1);

        clickOn("#load_game_b");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("game1Id", tokenStorage.getGameId());
        assertNull(tokenStorage.getEmpireId());
        assertTrue(tokenStorage.isSpectator());
        verify(this.app, times(1)).show("/ingame");
    }


}

package de.uniks.stp24;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.menu.CreateGameService;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.service.menu.LoginService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.testfx.matcher.base.WindowMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;



public class AppTest extends ControllerTest {
    ImageCache imageCache;
    LoginService loginService;
    AuthApiService authApiService;
    GamesApiService gamesApiService;
    GameMembersApiService gameMembersApiService;
    PresetsApiService presetsApiService;
    CreateGameService createGameService;
    EventListener eventListener;
    LobbyService lobbyService;
    UserApiService userApiService;
    TokenStorage tokenStorage;
    EditGameService editGameService;
    EmpireService empireService;
    IslandsService islandsService;

    final Subject<Event<Game>> gameSubject = BehaviorSubject.create();
    final Subject<Event<MemberDto>> memberSubject = BehaviorSubject.create();


    @BeforeEach
    public void setUp() {
        Map<String,Integer> _public = new HashMap<>();
        _public.put("backgroundIndex", 1);
        _public.put("portraitIndex", 1);
        _public.put("frameIndex", 1);

        SignUpResultDto signUpResult = new SignUpResultDto(null, null, "1", "JustATest", null);
        LoginResult loginResult = new LoginResult("1", "JustATest", null,_public, "a", "r");
        LoginDto loginDto = new LoginDto("JustATest", "testpassword");
        RefreshDto refreshDto = new RefreshDto("r");
        Game game1 = new Game("2024-05-28T12:55:25.688Z", null, "1", "Was geht", "1", 2,0, false, 0,0, null);
        Game game2 = new Game("2024-05-28T13:55:25.688Z", null, "2", "rapapa", "testID", 2,0, false, 0,0, null);
        Game game3 = new Game("2024-05-28T14:55:25.688Z", null, "123", "AwesomeLobby123", "1", 2,0, false, 0, 0, null);

        User user = new User("JustATest", "1", null, null, null,_public);

        GameSettings gameSettings = new GameSettings(100);
        CreateGameResultDto createGameResultDto = new CreateGameResultDto("2024-05-28T14:55:25.688Z",null,game3._id(), "AwesomeLobby123","1", false,1, 1, gameSettings);
        CreateGameDto createGameDto = new CreateGameDto("AwesomeLobby123",2, false, 1, gameSettings, "123");

        MemberDto memberDto = new MemberDto(false, user._id(), null, null);
        MemberDto[] memberDtos = new MemberDto[1];
        memberDtos[0] = memberDto;


        userApiService = testComponent.userApiService();
        doReturn(Observable.just(signUpResult)).when(userApiService).signup(any());

        imageCache = testComponent.imageCache();
        authApiService = testComponent.authApiService();
        loginService = testComponent.loginService();
        gamesApiService = testComponent.gamesApiService();
        gameMembersApiService = testComponent.gameMemberApiService();
        presetsApiService = testComponent.presetsApiService();
        createGameService = testComponent.createGameService();
        eventListener = testComponent.eventListener();
        lobbyService = testComponent.lobbyService();
        tokenStorage = testComponent.tokenStorage();
        empireService = testComponent.empireService();
        editGameService = testComponent.editGameService();
        islandsService = testComponent.islandsService();

        doReturn(Observable.just(loginResult)).when(authApiService).login(loginDto);
        doReturn(Observable.just(loginResult)).when(authApiService).refresh(refreshDto);

        doReturn(Observable.just(loginResult))
                .when(loginService).login(any(), any(), eq(false));

        doReturn(Observable.just(List.of(
                game1, game2
        ))).when(gamesApiService).findAll();

        doReturn(Observable.just(createGameResultDto)).when(createGameService).createGame(any(), any(), any(), eq(2));

        Event<Game> gameEvent = new Event<>("games." + game3._id() + ".created", new Game("2024-05-28T14:55:25.688Z", null, game3._id(), createGameDto.name(), "1", 2,0, false, 0,0, null));
        doReturn(Observable.empty()).doReturn(Observable.just(gameEvent)).when(eventListener).listen(eq("games.*.*"), eq(Game.class));

        doReturn(Observable.just(game3)).when(gamesApiService).getGame(game3._id());

        when(lobbyService.loadPlayers(any()))
                .thenReturn(Observable.just(memberDtos))
                .thenReturn(Observable.just(new MemberDto[]{
                new MemberDto(true, "1", null, null)}));

        doReturn(Observable.empty()).when(eventListener).listen(eq("games." + game3._id() + ".deleted"), eq(Game.class));

        doReturn(memberSubject).when(eventListener).listen("games." + game3._id() + ".members.*.*", MemberDto.class);
        doReturn(memberSubject).when(eventListener).listen(eq("games." + game3._id() + ".members.*.updated"), eq(MemberDto.class));


        doReturn(Observable.just(memberDto)).when(lobbyService).getMember(game3._id(), user._id());
        doReturn(Observable.just(new MemberDto(true, user._id(), null, null))).when(lobbyService).updateMember(game3._id(), user._id(), true, null);

        doReturn(true).when(createGameService).nameIsAvailable("AwesomeLobby123");
        doAnswer((Answer<Observable<User>>) invocation -> {
            // Hier können wir eine Benachrichtigung senden, dass getUser aufgerufen wurde
            return Observable.just(user);
        }).when(userApiService).getUser(any());

        doReturn("1").when(tokenStorage).getUserId();
        doReturn(Observable.just(new MemberDto(false, user._id(), new Empire("Buccaneers", "", "#DC143C", 0, 0, null,"uninhabitable_0"), null))).when(lobbyService).updateMember(game3._id(),user._id(), false, null);
        doReturn(Observable.just(new MemberDto(true, user._id(), new Empire("Buccaneers", "", "#DC143C", 0, 0, null,"uninhabitable_0"), null))).when(lobbyService).updateMember(game3._id(),user._id(), true, null);
        doReturn(Observable.just(new UpdateGameResultDto("2024-05-28T14:55:25.688Z", null,game3._id(),"testGame", user._id(),
                true, 0, 0, null))).when(this.editGameService).startGame(any());


        doReturn(Observable.just(new ReadEmpireDto[]{new ReadEmpireDto("1","a","testEmpireID", game3._id(),
                user._id(),"tesEmpire","a","#DC143C",0, 0, "uninhabitable_0")})).when(this.empireService).getEmpires(any());

        doReturn(Observable.just(new AggregateResultDto(1,null))).when(this.empireService).getResourceAggregates(any(),any());

        doReturn(Observable.just(new Trait[]{})).when(presetsApiService).getTraitsPreset();
        doReturn(null).when(this.imageCache).get(any());
    }

    @Test
    public void v1() throws Exception {
        goToSignup();
        signupUser();
        loginUser();
        goToNewGame();
        createGame();
        loadGame();
        selectEmpire();
        startAGame();
    }


    /*
    =================================== Navigate methods ===================================
     */


    private void goToSignup(){
        verifyThat(window("Calvario"), WindowMatchers.isShowing());
        clickOn("#press_any_key");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("LOGIN"), WindowMatchers.isShowing());
        clickOn("#signupButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("REGISTER"), WindowMatchers.isShowing());
        WaitForAsyncUtils.waitForFxEvents();
    }


    private void signupUser(){
        clickOn("#usernameField");
        write("JustATest");
        clickOn("#passwordField");
        write("testpassword");
        clickOn("#repeatPasswordField");
        write("testpassword");

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("LOGIN"), WindowMatchers.isShowing());
    }

    private void loginUser(){
        clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents();
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void goToNewGame(){
        clickOn("#new_game_b");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("CREATE GAME"), WindowMatchers.isShowing());
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void createGame(){
        clickOn("#createNameTextField");
        write("AwesomeLobby123");
        clickOn("#createPasswordTextField");
        write("123");
        clickOn("#maxMembersTextField");
        write("2");
        clickOn("#editMapSizeTextfield");
        clickOn("50");
        clickOn("100");
        clickOn("150");
        clickOn("200");
        clickOn("#createGameConfirmButton");
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void loadGame(){
        ListView<String> listView = lookup("#gameList").query();
        Node listCell = listView.lookupAll(".list-cell").toArray(new Node[0])[0];
        clickOn(listCell);
        clickOn("#load_game_b");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("ENTER GAME"), WindowMatchers.isShowing());
        WaitForAsyncUtils.waitForFxEvents();
//        lookup("#backgroundAnchorPane").queryAs(AnchorPane.class).getStylesheets().clear();
    }

    private void selectEmpire(){
        clickOn("#selectEmpireButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#backButton");
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void startAGame() {
        clickOn("#readyButton");
        this.memberSubject.onNext(new Event<>("games.123.members.1.updated",
                new MemberDto(true, "JustATest", null, null)));
        assertFalse(lookup("#startJourneyButton").queryButton().isDisabled());

        clickOn("#startJourneyButton");
        this.gameSubject.onNext(new Event<>("games.testGameID.updated", new Game("1", "a","testGameID","testGame","testGameHostID",
                2, 0,true, 1, 0, new GameSettings(1))));
        WaitForAsyncUtils.waitForFxEvents();

        // game screen will not be shown, but loaded
        assertEquals("ENTER GAME",stage.getTitle() );
    }

}

package de.uniks.stp24;

import dagger.Component;
import de.uniks.stp24.dagger.MainComponent;
import de.uniks.stp24.dagger.MainModule;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.PrefService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.menu.CreateGameService;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.service.menu.LoginService;
import de.uniks.stp24.ws.EventListener;

import javax.inject.Singleton;

@Component(modules = {MainModule.class, TestModule.class})
@Singleton
public interface TestComponent extends MainComponent {
    PrefService prefService();
    AuthApiService authApiService();
    UserApiService userApiService();
    GamesApiService gamesApiService();
    GameLogicApiService gameLogicApiService();
    LoginService loginService();
    CreateGameService createGameService();
    EventListener eventListener();
    LobbyService lobbyService();
    GameMembersApiService gameMemberApiService();
    TokenStorage tokenStorage();
    EditGameService editGameService();
    EmpireService empireService();
    IslandsService islandsService();
    PresetsApiService presetsApiService();
    JobsService jobsService();
    JobsApiService jobsApiService();
    ShipsApiService shipsApiService();
    FleetApiService fleetApiService();
    ImageCache imageCache();
    WarsApiService warsApiService();

    @Component.Builder
    interface Builder extends MainComponent.Builder {
        @Override
        TestComponent build();
    }
}
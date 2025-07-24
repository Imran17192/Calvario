package de.uniks.stp24;


import de.uniks.stp24.component.game.BuildingPropertiesComponent;
import de.uniks.stp24.component.menu.GangDeletionComponent;
import de.uniks.stp24.component.menu.LogoutComponent;
import de.uniks.stp24.component.menu.WarningComponent;
import de.uniks.stp24.component.menu.WarningScreenComponent;
import de.uniks.stp24.controllers.*;
import org.fulib.fx.annotation.Route;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class Routes {  
    @Route("load")
    @Inject
    Provider<LoadController> load;

    @Route("signup")
    @Inject
    Provider<SignUpController> signup;
    
    @Route("browseGames")
    @Inject
    Provider<BrowseGameController> browseGames;
    @Route("licenses")
    @Inject
    Provider<LicensesController> licenses;

    @Route("editgame")
    @Inject
    Provider<EditGameController> editgame;

    @Route("createGameController")
    @Inject
    Provider<CreateGameController> createGameController;

    @Route("editAcc")
    @Inject
    Provider<EditAccController> editAcc;

    @Route("warningScreen")
    @Inject
    Provider<WarningScreenComponent> warningScreen;

    @Route("gangDeletion")
    @Inject
    Provider<GangDeletionComponent> gangDeletion;

    @Route("logout")
    @Inject
    Provider<LogoutComponent> logout;
    @Route("warning")
    @Inject
    Provider<WarningComponent> warning;


    @Route("buildingProperties")
    @Inject
    Provider<BuildingPropertiesComponent> buildingProperties;

    @Route("ingame")
    @Inject
    Provider<InGameController> game;

    @Route("creation")
    @Inject
    Provider<GangCreationController> creation;

    @Route("lobby")
    @Inject
    Provider<LobbyController> lobby;

    @Route("login")
    @Inject
    Provider<LoginController> login;

    @Route("loading-screen")
    @Inject
    Provider<LoadingScreenController> loadingScreen;


    @Inject
    public Routes() {
    }
}

package de.uniks.stp24.appTestModules;

import de.uniks.stp24.component.menu.*;
import de.uniks.stp24.controllers.GangCreationController;
import de.uniks.stp24.controllers.LobbyController;
import de.uniks.stp24.controllers.helper.JoinGameHelper;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.SaveLoadService;
import de.uniks.stp24.service.menu.GamesService;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.stage.Stage;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import javax.inject.Provider;

public class LobbyTestLoader extends InGameTestLoader {
    @Spy
    GamesService gamesService;
    @Spy
    UserApiService userApiService;
    @Spy
    ErrorService errorService;
    @Spy
    SaveLoadService saveLoadService;

    @InjectMocks
    protected JoinGameHelper joinGameHelper;
    @InjectMocks
    LobbyService lobbyService;
    @InjectMocks
    protected LobbyController lobbyController;
    @InjectMocks
    protected GangCreationController gangCreationController;
    @InjectMocks
    LobbyHostSettingsComponent lobbyHostSettingsComponent;
    @InjectMocks
    LobbySettingsComponent lobbySettingsComponent;
    @InjectMocks
    EnterGameComponent enterGameComponent;
    @InjectMocks
    BubbleComponent bubbleComponent;
    @InjectMocks
    UserComponent userComponent;
    @InjectMocks
    GangDeletionComponent gangDeletionComponent;

    final Provider<GangComponent> gangComponentProvider = GangComponent::new;


    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
    }

    @Override
    protected void setControllers() {
        this.lobbyController.lobbyHostSettingsComponent = this.lobbyHostSettingsComponent;
        this.lobbyController.lobbySettingsComponent = this.lobbySettingsComponent;
        this.lobbyController.enterGameComponent = this.enterGameComponent;
        this.lobbyController.bubbleComponent = this.bubbleComponent;
        this.lobbyController.userComponent = this.userComponent;

        this.gangCreationController.gangDeletionComponent = this.gangDeletionComponent;
        this.gangCreationController.gangComponentProvider = this.gangComponentProvider;
        this.gangCreationController.bubbleComponent = this.bubbleComponent;

        super.setControllers();
    }

    @Override
    protected void setServices() {
        this.lobbyController.gamesService = this.gamesService;
        this.lobbyController.tokenStorage = this.tokenStorage;
        this.lobbyController.lobbyService = this.lobbyService;
        this.lobbyController.subscriber = this.subscriber;
        this.lobbyController.errorService = this.errorService;

        this.gangCreationController.presetsApiService = this.presetsApiService;
        this.gangCreationController.saveLoadService = this.saveLoadService;
        this.gangCreationController.lobbyService = this.lobbyService;
        this.gangCreationController.subscriber = this.subscriber;

        this.gamesService.gamesApiService = this.gamesApiService;

        this.lobbyService.gameMembersApiService = this.gameMembersApiService;
        this.lobbyService.userApiService = this.userApiService;

        this.lobbyHostSettingsComponent.editGameService = this.editGameService;

        this.joinGameHelper.islandsService = this.islandsService;
        this.joinGameHelper.empireService = this.empireService;
        this.joinGameHelper.tokenStorage = this.tokenStorage;
        this.joinGameHelper.subscriber = this.subscriber;

        super.setServices();
    }

    @Override
    protected void clearStyles() {
        super.clearStyles();
    }
}

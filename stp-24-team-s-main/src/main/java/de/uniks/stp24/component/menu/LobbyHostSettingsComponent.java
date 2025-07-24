package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.ResourceBundle;

@Singleton
@Component(view = "LobbyHostSettings.fxml")
public class LobbyHostSettingsComponent extends AnchorPane {
    @FXML
    public Button startJourneyButton;
    @FXML
    public Button readyButton;
    @FXML
    public Button selectEmpireButton;
    @FXML
    public Button closeLobbyButton;
    @Inject
    public Subscriber subscriber;
    @Inject
    LobbyService lobbyService;
    @Inject
    App app;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    ImageCache imageCache;
    @Inject
    GamesApiService gamesApiService;
    @Inject
    public EditGameService editGameService;
    @Inject
    EventListener eventListener;
    @Inject
    EmpireService empireService;
    @Inject
    @Resource
    ResourceBundle resources;
    @Inject
    ErrorService errorService;
    @Inject
    IslandsService islandsService;

    private String gameID;
    public final boolean leftLobby;
    public Image readyIconBlueImage;
    public Image readyIconGreenImage;


    @Inject
    public LobbyHostSettingsComponent() {
        this.leftLobby = false;
    }

    @OnInit
    public void init(){
    }

    public void setReadyButton(boolean ready){
        if(ready){
            readyButton.getStyleClass().removeAll("lobbyButtonReadyNot");
            readyButton.getStyleClass().add("lobbyButtonReady");
        } else {
            readyButton.getStyleClass().removeAll("lobbyButtonReady");
            readyButton.getStyleClass().add("lobbyButtonReadyNot");
        }
    }

    @OnRender
    public void render() {
        this.startJourneyButton.setDisable(true);
    }

    public void startGame() {
        subscriber.subscribe(editGameService.startGame(this.gameID),
          result -> this.startJourneyButton.setDisable(true),
          error -> this.startJourneyButton.setDisable(false));
    }

    /**
     * Sends a blank update message to the server so the members are notified about host leaving the lobby.
     */
    public void leaveLobby() {
        this.subscriber.subscribe(this.lobbyService.getMember(this.gameID, this.tokenStorage.getUserId()),
            host -> this.subscriber.subscribe(this.lobbyService.updateMember(this.gameID,
                    this.tokenStorage.getUserId(), host.ready(), host.empire()),
            result -> this.app.show("/browseGames"),
            error -> errorService.getStatus(error)),
          error -> errorService.getStatus(error));
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void selectEmpire() {
        this.app.show("/creation", Map.of("gameid", this.gameID));
    }

    public void ready() {
        this.subscriber.subscribe(
          this.lobbyService.getMember(this.gameID, this.tokenStorage.getUserId()), result -> {
                  this.subscriber.subscribe(this.lobbyService.updateMember(this.gameID, this.tokenStorage.getUserId(),
                          !result.ready(), result.empire()));
                  setReadyButton(!result.ready());
          });
    }


    @OnDestroy
    public void destroy(){
        readyIconBlueImage = null;
        readyIconGreenImage = null;
    }
}

package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Map;
import java.util.ResourceBundle;

@Component(view = "LobbySettings.fxml")
public class LobbySettingsComponent extends AnchorPane {
    @FXML
    public Button readyButton;
    @FXML
    public Button selectEmpireButton;
    @FXML
    public Button leaveLobbyButton;
    @Inject
    Subscriber subscriber;
    @Inject
    LobbyService lobbyService;
    @Inject
    App app;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    ImageCache imageCache;
    @Inject
    @Resource
    ResourceBundle resources;
    @Inject
    ErrorService errorService;

    private String gameID;
    public boolean leftLobby = false;
    public Image readyIconBlueImage;
    public Image readyIconGreenImage;

    @Inject
    public LobbySettingsComponent() {

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

    public void leaveLobby() {
        this.leftLobby = true;
        this.subscriber.subscribe(this.lobbyService.leaveLobby(this.gameID, this.tokenStorage.getUserId()),
            result -> this.app.show("/browseGames"),
            error -> errorService.getStatus(error));
    }

    public void selectEmpire() {
        this.app.show("/creation", Map.of("gameid", this.gameID));
    }
    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void ready() {
        this.subscriber.subscribe(
                this.lobbyService.getMember(this.gameID, this.tokenStorage.getUserId()),
          result -> {
                    if (result.ready()) {
                        this.subscriber.subscribe(this.lobbyService
                                .updateMember(this.gameID, this.tokenStorage.getUserId(), false, result.empire()));
                        setReadyButton(false);
                    } else {
                        this.subscriber.subscribe(this.lobbyService
                                .updateMember(this.gameID, this.tokenStorage.getUserId(), true, result.empire()));
                        setReadyButton(true);
                    }
                },
          error -> errorService.getStatus(error));
    }

    @OnDestroy
    public void destroy(){
        readyIconBlueImage = null;
        readyIconGreenImage = null;
    }

}

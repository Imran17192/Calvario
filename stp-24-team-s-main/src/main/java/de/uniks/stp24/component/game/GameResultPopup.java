package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.UpdateSpeedDto;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

@Component(view = "GameResultPopup.fxml")
public class GameResultPopup extends VBox {
    @FXML
    Button quitButton;
    @FXML
    TextFlow resultText;
    @FXML
    Text resultText1;
    @FXML
    Text resultText2;
    @FXML
    Text resultText3;
    @FXML
    ImageView resultImageView;
    @FXML
    Text spectatorText;
    @Inject
    public ImageCache imageCache;
    @Inject
    public InGameService inGameService;
    @Inject
    public GamesApiService gamesApiService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public Subscriber subscriber;
    @Inject
    public App app;

    InGameController inGameController;


    @Inject
    public GameResultPopup() {
    }

    public void open(boolean lost) {
        inGameController.shadow.setVisible(true);
        inGameController.shadow.setStyle("-fx-opacity: 0.5; -fx-background-color: black");
        this.setVisible(true);
        resultText1.setText("You have");
        if (lost) {
            resultImageView.setImage(imageCache.get("/de/uniks/stp24/assets/gameResult/Game_Lost_Image.png"));
            resultText2.setText(" ");
            resultText3.setText("Lost");
            spectatorText.setVisible(true);
        } else {
            resultImageView.setImage(imageCache.get("/de/uniks/stp24/assets/gameResult/Game_Won_Image.png"));
            resultText2.setText(" Won ");
            resultText3.setText("");
            spectatorText.setVisible(false);
        }
        System.out.println("popUp open");
    }


    public void handleGameLeaving() {
        tokenStorage.setGameId(null);
        tokenStorage.setEmpireId(null);
        app.show("/browseGames");
    }


    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

}
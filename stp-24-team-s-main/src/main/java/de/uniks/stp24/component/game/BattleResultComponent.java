package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.helper.BattleEntry;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "BattleResult.fxml")
public class BattleResultComponent extends Pane {
    public ImageView resultImageView;
    public Button closeResultButton;
    public TextFlow resultTextFlow;
    public Text messageText;
    public Text coloredText;
    public VBox theyLostBox;
    public VBox youLostBox;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    IslandsService islandsService;
    @Inject
    ImageCache imageCache;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    @Inject
    public BattleResultComponent() {}

    public void setInfo(BattleEntry battleEntry) {
        this.setVisible(true);

        this.youLostBox.getChildren().clear();
        this.theyLostBox.getChildren().clear();

        String empireID = this.tokenStorage.getEmpireId();

        switch (battleEntry.getBattleType()) {
            case EMPIRES -> {
                if (battleEntry.getWinner().equals(empireID)) {
                    this.messageText.setText("Congratulations! Your pirates just won a battle against the ");
                    this.coloredText.setText(this.islandsService.getEmpire(battleEntry.getLoser()).name());
                    this.resultImageView.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/battle_won.png"));
                } else {
                    this.messageText.setText("A black day it be on the high seas. You just lost a battle against the ");
                    this.coloredText.setText(this.islandsService.getEmpire(battleEntry.getWinner()).name());
                    this.resultImageView.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/battle_lost.png"));
                }
                if (battleEntry.getAttacker().equals(empireID)) {
                    battleEntry.getShipsLostByAttacker().forEach(this::setYourLostShips);
                    battleEntry.getShipsLostByDefender().forEach(this::setTheirLostShips);
                } else {
                    battleEntry.getShipsLostByDefender().forEach(this::setYourLostShips);
                    battleEntry.getShipsLostByAttacker().forEach(this::setTheirLostShips);
                }
            }
            case WILD -> {
                if (Objects.nonNull(battleEntry.getWinner())) {
                    this.messageText.setText("Congratulations! Your pirates just won a battle against a ");
                    this.coloredText.setText("wild fleet");
                    this.resultImageView.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/battle_won.png"));
                } else {
                    this.messageText.setText("A black day it be on the high seas. You just lost a battle against a ");
                    this.coloredText.setText("wild fleet");
                    this.resultImageView.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/battle_lost.png"));
                }
                battleEntry.getShipsLostByAttacker().forEach(this::setYourLostShips);
                battleEntry.getShipsLostByDefender().forEach(this::setTheirLostShips);
            }
        }
    }

    private void setYourLostShips(String type, int count) {
        Label newLabel = new Label(
                count+"x "+gameResourceBundle.getString("ship." + type));
        newLabel.getStyleClass().add("tempusSansITCWarText");
        this.youLostBox.getChildren().add(newLabel);
    }

    private void setTheirLostShips(String type, int count) {
        Label newLabel = new Label(
                count+"x "+gameResourceBundle.getString("ship." + type));
        newLabel.getStyleClass().add("tempusSansITCWarText");
        this.theyLostBox.getChildren().add(newLabel);
    }


    public void close() {
        this.setVisible(false);
    }
}

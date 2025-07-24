package de.uniks.stp24.component.game;

import de.uniks.stp24.model.SeasonComponent;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.MarketService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "MarketSeasonComponent.fxml")
public class MarketSeasonComponent extends HBox implements ReusableItemComponent<SeasonComponent> {
    public Button resourceIconButton;
    public Text resourceAmount;
    @FXML
    Text transActionTypeText;
    @FXML
    ImageView resourceTypeImageView;
    @FXML
    Text resourceAmountText;
    @FXML
    AnchorPane moneyIconAnchorPane;
    @FXML
    Text moneyAmountText;
    @FXML
    ToggleButton playControlsButton;
    @FXML
    Button cancelTradesButton;
    @Inject
    @org.fulib.fx.annotation.controller.Resource
    ResourceBundle langBundle;
    @Inject
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;
    @Inject
    public ImageCache imageCache;
    private SeasonComponent seasonComponent;
    @Inject
    public MarketService marketService;

    @Inject
    public MarketSeasonComponent() {
    }

    @Override
    public void setItem(@NotNull SeasonComponent seasonComponent) {
        this.seasonComponent = seasonComponent;
        resourceTypeImageView.setImage(imageCache.get("/de/uniks/stp24/icons/resources/" + this.seasonComponent.resourceType() + ".png"));
        if (this.seasonComponent.transActionTypeText().equals("buy")) {
            transActionTypeText.setText("Buy");
            resourceAmountText.setText("+" + this.seasonComponent.resourceAmount());
            moneyAmountText.setText("-" + this.seasonComponent.moneyAmount());
        } else {
            transActionTypeText.setText("Sell");
            double sellAmount = this.seasonComponent.resourceAmount() * -1;
            moneyAmountText.setText("+" + this.seasonComponent.moneyAmount());

            resourceAmountText.setText("-" + sellAmount);
        }

        playControlsButton.setSelected(this.seasonComponent.isPlaying());
        playControlsButton.setOnAction(event -> this.seasonComponent = new SeasonComponent(
                this.seasonComponent.transActionTypeText(), this.seasonComponent.resourceType(), this.seasonComponent.resourceAmount(), this.seasonComponent.moneyAmount(), playControlsButton.isSelected()));

        cancelTradesButton.setOnAction(event -> marketService.cancelSeasonalTrade(this.seasonComponent));
    }
}

package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Ships.ShipType;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Component(view = "Blueprints.fxml")
public class BlueprintsComponent extends VBox implements ReusableItemComponent<ShipType> {
    @FXML
    public Label blueprintTypeLabel;
    @FXML
    public ImageView healthImage;
    @FXML
    public Label healthLabel;
    @FXML
    public ImageView timeImage;
    @FXML
    public Label timeLabel;
    @FXML
    public ImageView speedImage;
    @FXML
    public Label speedLabel;
    @FXML
    public HBox blueprintHBox;
    @FXML
    public ImageView attackImage;
    @FXML
    public Label attackLabel;
    @FXML
    public ImageView defenseImage;
    @FXML
    public Label defenseLabel;
    @FXML
    public ImageView resourceImage1;
    @FXML
    public Label resourceLabel1;
    @FXML
    public ImageView resourceImage2;
    @FXML
    public Label resourceLabel2;
    @FXML
    public Button addBlueprintButton;
    @FXML
    public Button infoButton;

    @Inject
    ImageCache imageCache = new ImageCache();

    public ResourceBundle gameResourceBundle;

    private final FleetManagerComponent fleetManagerComponent;
    private final boolean canBeAddedToFleet;
    private ShipType shipType;

    public BlueprintsDetailsComponent blueprintDetailsComponent;

    @Inject
    public BlueprintsComponent(FleetManagerComponent fleetManagerComponent, boolean canBeAddedToFleet, BlueprintsDetailsComponent blueprintDetailsComponent, ResourceBundle resourceBundle) {
        this.fleetManagerComponent = fleetManagerComponent;
        this.canBeAddedToFleet = canBeAddedToFleet;
        this.blueprintDetailsComponent = blueprintDetailsComponent;
        this.gameResourceBundle = resourceBundle;
    }

    @OnRender
    public void render() {
        Tooltip details = new Tooltip();
        details.getStyleClass().add("details-tooltip");
        details.setShowDelay(Duration.ZERO);
        details.setShowDuration(Duration.INDEFINITE);
        Tooltip.install(infoButton, details);

        addEventFilter(ScrollEvent.SCROLL, event -> {
            if (details.isShowing()) {
                forwardScrollEvent(blueprintDetailsComponent.damage, event);
                event.consume();
            }
        });

        details.setOnShowing(e -> {
            details.setGraphic(blueprintDetailsComponent);
            blueprintDetailsComponent.showBlueprintDetails(shipType);
            fleetManagerComponent.blueprintsListView.setStyle("-fx-opacity: 1");
        });

        details.setOnHiding(e -> details.setGraphic(null));

        healthImage.setImage(imageCache.get("icons/ships/health.png"));
        timeImage.setImage(imageCache.get("icons/ships/stopwatch.png"));
        speedImage.setImage(imageCache.get("icons/ships/windy.png"));
        attackImage.setImage(imageCache.get("icons/ships/sword.png"));
        defenseImage.setImage(imageCache.get("icons/ships/shield.png"));
        resourceImage1.setImage(imageCache.get("icons/resources/energy.png"));
        resourceImage2.setImage(imageCache.get("icons/resources/alloys.png"));
    }

    private void forwardScrollEvent(ListView<?> targetListView, ScrollEvent event) {
        ScrollBar verticalScrollBar = (ScrollBar) targetListView.lookup(".scroll-bar:vertical");

        if (verticalScrollBar != null) {
            // Adjust the ScrollBar value based on the scroll delta
            double deltaY = event.getDeltaY();
            double newValue = verticalScrollBar.getValue() - deltaY;

            // Ensure newValue is within the bounds
            double min = verticalScrollBar.getMin();
            double max = verticalScrollBar.getMax();

            if (newValue < min) {
                newValue = min;
            } else if (newValue > max) {
                newValue = max;
            }

            verticalScrollBar.setValue(newValue);
        }
    }

    @Override
    public void setItem(@NotNull ShipType shipType) {
        this.addBlueprintButton.setId("addBlueprintButton_" + shipType._id());

        this.shipType = shipType;
        this.blueprintTypeLabel.setText(gameResourceBundle.getString("ship." + shipType._id()));
        this.healthLabel.setText(String.valueOf(shipType.health()));
        this.timeLabel.setText(String.valueOf(shipType.build_time()));
        this.speedLabel.setText(String.valueOf(shipType.speed()));
        this.attackLabel.setText(String.valueOf(shipType.attack().get("default")));
        this.defenseLabel.setText(String.valueOf(shipType.defense().get("default")));
        resourceLabel1.setText(String.valueOf(shipType.cost().get("alloys")));
        resourceLabel2.setText(String.valueOf(shipType.cost().get("energy")));
        this.addBlueprintButton.setVisible(canBeAddedToFleet);
    }

    public void addBlueprint() {this.fleetManagerComponent.addBlueprintToFleet(shipType);}
}

package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.model.Ships;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Component(view = "BlueprintsDetails.fxml")
public class BlueprintsDetailsComponent extends VBox {
    @FXML
    public Label shipType;
    @FXML
    ListView<de.uniks.stp24.model.Resource> consumed;
    @FXML
    ListView<String> damage;

    @Inject
    App app;
    @Inject
    ImageCache imageCache = new ImageCache();
    @Inject
    ResourcesService resourcesService;
    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    Provider<ResourceComponent> provider = () -> new ResourceComponent(true, false, true, false, gameResourceBundle, imageCache);


    @Inject
    public BlueprintsDetailsComponent() {

    }

    @OnRender
    public void render() {

    }

    @OnInit
    public void init() {

    }

    @OnDestroy
    public void destroy() {

    }

    public void showBlueprintDetails(Ships.ShipType shipType) {
        this.shipType.setText(gameResourceBundle.getString("ship." + shipType._id()));
        ArrayList<String> attackList = new ArrayList<>();
        for (String attack : shipType.attack().keySet()) {
            attackList.add(gameResourceBundle.getString("ship." + attack) + ": " + shipType.attack().get(attack));
        }
        damage.getItems().setAll(attackList);

        consumed.setCellFactory(list -> new ComponentListCell<>(this.app, this.provider));
        ObservableList<de.uniks.stp24.model.Resource> resources = resourcesService.generateResourceListForDouble(shipType.upkeep(), consumed.getItems(), null, false);
        consumed.setItems(resources);

    }
}

package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.service.game.IslandsService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.util.Objects;
import java.util.ResourceBundle;


@Component(view = "IslandComponent.fxml")
public class IslandComponent extends Pane {
    @FXML
    public ImageView rudderImage;
    @FXML
    public ImageView islandImage;
    @FXML
    public StackPane flagPane;
    public Circle collisionCircle;
    @FXML
    ImageView flagImage;
    @FXML
    ImageView spyglassImage;
    @FXML
    Text healthBar;
    @FXML
    Text defenseBar;
    @FXML
    ImageView healthIcon;
    @FXML
    ImageView defenseIcon;
    @FXML
    public ImageView sableImage;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    @Resource
    ResourceBundle resource;
    @Inject
    IslandAttributeStorage islandAttributes;
    @Inject
    FleetService fleetService;

    IslandsService islandsService;

    ImageCache imageCache;

    public Island island;

    public InGameController inGameController;

    double x, y;

    public boolean islandIsSelected = false;

    private int health, maxHealth, maxDef;
    BooleanProperty statsVisibility = new SimpleBooleanProperty(false);
    public boolean foggy = true;

    @Inject
    public IslandComponent(IslandsService islandsService) {
        this.islandsService = islandsService;
        this.imageCache = islandsService.imageCache;
        this.islandImage = new ImageView();
        this.flagImage = new ImageView();
        this.spyglassImage = new ImageView();
        this.healthIcon = new ImageView();
        this.defenseIcon = new ImageView();
        this.setPickOnBounds(false);
    }

    public void render() {
        this.sableImage.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/war_icon.png"));
        this.sableImage.setVisible(false);
    }

    public void applyIcon(boolean isFoggy, BlendMode blendMode) {
        if (Objects.nonNull(this.islandImage))
            this.islandImage.setImage(imageCache.get("icons/islands/" + island.type().name() + ".png"));

        this.foggy = isFoggy;

        if (isFoggy)
            this.changeBlendMode(blendMode);
        else {
            if (Objects.nonNull(this.islandImage))
                this.islandImage.setBlendMode((BlendMode.SRC_OVER));
        }

        if (Objects.nonNull(this.spyglassImage)) {
            if (this.island.upgrade().equals("explored"))
                this.spyglassImage.setImage(imageCache.get("/de/uniks/stp24/icons/other/spyglass.png"));
            else // islands with upgrades other than explored
                hideSpyGlass();
        }

    }

    public void changeBlendMode(BlendMode blendMode) {
        if (this.foggy)
            this.islandImage.setBlendMode(blendMode);
    }

    public void toggleSableVisibility(boolean isVisible) {
        this.sableImage.setVisible(isVisible);
    }


    // use our flag images
    // by the moment numeration from 0 til 16
    public void setFlagImage(int flag) {
        if (!this.foggy) {
            if (flag >= 0) this.flagImage.setImage(imageCache.get("assets/flags/flag_" + flag + ".png"));
        }
    }

    public void applyInfo(Island islandInfo) {
        this.island = islandInfo;
        this.setId(island.id() + "_instance");
        this.spyglassImage.setVisible(island.upgrade().equals("explored"));

        applyIcon(this.foggy, BlendMode.LIGHTEN);
    }

    // retrieve information for game owner's islands
    public void getDefenceAndHealth(String gameOwnerId) {
        if (Objects.nonNull(island.owner()) && island.owner().equals(gameOwnerId)) {
            islandsService.getSystemAggregate(island.owner(), "system.max_health", island.id());
            islandsService.getSystemAggregate(island.owner(), "system.defense", island.id());
        }
        setHealth(island.health());
    }

    // round double to have only 2 decimals
    public void setPosition(double x, double y) {
        this.x = Math.rint(x * 100.00) / 100.00;
        this.y = Math.rint(y * 100.00) / 100.00;
    }

    public double getPosX() {
        return this.x;
    }

    public double getPosY() {
        return this.y;
    }

    // switch the visibility of all flags
    public void showFlag(boolean selected) {
        if (!this.foggy) {
            this.flagPane.setVisible(selected);
            inGameController.islandsService.keyCodeFlag = selected;
        }
    }

    @OnKey(code = KeyCode.F, alt = true)
    public void showFlagH() {
        if (!this.foggy) {
            if (island.flagIndex() >= 0 && !islandIsSelected) {
                this.flagPane.setVisible(!flagPane.isVisible());
            }
        }
    }

    public Island getIsland() {
        return this.island;
    }

    public void showRudder() {
        if (!this.foggy)
            rudderImage.setVisible(true);
    }

    public void unshowRudder() {
        if (!this.foggy && !islandIsSelected) {
            rudderImage.setVisible(false);
        }
    }

    /*
    Logic for showing/unshowing rudder
     */
    public void showUnshowRudder() {
        if (!this.foggy) {
            if (islandIsSelected) {
                reset();
                islandIsSelected = false;
            } else {
                if (island.owner() != null) {
                    inGameController.islandsService.islandComponentMap.forEach((id, comp) -> {
                        if (comp.islandIsSelected) {
                            comp.rudderImage.setVisible(false);
                            if (!inGameController.islandsService.keyCodeFlag) {
                                comp.flagPane.setVisible(!comp.flagPane.isVisible());
                            }
                            comp.islandIsSelected = false;
                        }
                    });
                    islandIsSelected = true;
                }
            }

            if (!inGameController.islandsService.keyCodeFlag) {
                this.flagPane.setVisible(!this.flagPane.isVisible());
            }
        }
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    @OnDestroy
    public void destroy() {
        flagImage = null;
        islandImage = null;
    }

    /*
    Reset of components for showing information of current selected island.
     */
    public void reset(){
        inGameController.overviewSitesComponent.resetButtons();
        inGameController.buildingsWindowComponent.setVisible(false);
        inGameController.sitePropertiesComponent.setVisible(false);
        inGameController.buildingPropertiesComponent.setVisible(false);
        inGameController.overviewContainer.setVisible(false);
        inGameController.selectedIsland.islandIsSelected = false;

        if (!inGameController.islandsService.keyCodeFlag) {
            inGameController.selectedIsland.rudderImage.setVisible(false);
        }

        inGameController.selectedIsland = null;
    }

    // apply drop shadow and flag for newly colonized systems
    public void applyEmpireInfo() {
        // apply drop shadow and flag for newly colonized systems (if they're not under fog)
        if (!this.foggy && Objects.nonNull(this.island.owner())) {
            this.islandsService.applyDropShadowToIsland(this);
            this.setFlagImage(islandsService.getEmpire(island.owner()).flag());
        }
    }

    public void hideSpyGlass() {
        this.spyglassImage.setVisible(false);
    }

    public boolean isCollided(double fleetX, double fleetY, double fleetR) {
        double x1 = this.getPosX();
        double y1 = this.getPosY();
        double r1 = Constants.ISLAND_COLLISION_RADIUS;

        return  Math.sqrt(Math.pow(fleetX - x1, 2) + Math.pow(fleetY - y1, 2)) <= r1 + fleetR;
    }

    public void setHealth(int health) {
        this.health = health;
        updateHealthBar();
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        updateHealthBar();
    }

    public void setMaxDefense(int defense) {
        this.maxDef = defense;
        this.defenseBar.setText(this.maxDef != 0 ? this.maxDef + "" : "???");
    }

    // determine if the 'stats' should be shown ->
    private void updateHealthBar() {
        this.statsVisibility.setValue(this.health < this.maxHealth);
        this.healthBar.setText(this.health + (this.maxHealth != 0 ? "/" + this.maxHealth : ""));
    }


    public void showDefenseAndHealth() {
        this.statsVisibility.setValue(!this.statsVisibility.get());
    }

    @OnRender
    void createBindings() {
        this.healthIcon.visibleProperty().bind(this.statsVisibility);
        this.healthBar.visibleProperty().bind(this.statsVisibility);
        this.defenseIcon.visibleProperty().bind(this.statsVisibility);
        this.defenseBar.visibleProperty().bind(this.statsVisibility);
    }
}
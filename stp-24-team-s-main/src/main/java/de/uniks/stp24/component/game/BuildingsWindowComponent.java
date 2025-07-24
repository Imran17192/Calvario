package de.uniks.stp24.component.game;


import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.BUILT_STATUS;
import static de.uniks.stp24.service.Constants.imagePaths;

@Component(view = "BuildingsWindow.fxml")
public class BuildingsWindowComponent extends AnchorPane {
    @FXML
    public Button buildingRefinery;
    @FXML
    Button buildingFactory;
    @FXML
    Button buildingFoundry;
    @FXML
    Button buildingResearchLab;
    @FXML
    Button buildingFarm;
    @FXML
    Button buildingMine;
    @FXML
    Button buildingPowerPlant;
    @FXML
    Button buildingExchange;
    @FXML
    Button buildingShipyard;
    @FXML
    Button buildingStronghold;
    @FXML
    Button closeWindowButton;

    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public ImageCache imageCache;

    @Inject
    Subscriber subscriber;

    @Inject
    IslandsService islandsService;

    @Inject
    ResourcesService resourcesService;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    private String buildingToAdd;

    private Button[] buttons;

    InGameController inGameController;



    @Inject
    public BuildingsWindowComponent(){

    }

    @FXML
    public void initialize() {
        buttons = new Button[]{
                buildingRefinery,
                buildingFactory,
                buildingFoundry,
                buildingResearchLab,
                buildingFarm,
                buildingMine,
                buildingPowerPlant,
                buildingExchange,
                buildingShipyard,
                buildingStronghold
        };
    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }

    //Takes buttons in order and sets image for each button
    @OnRender
    public void setImages() {
        this.setPickOnBounds(true);

        for (int i = 0; i < buttons.length; i++) {
            ImageView imageView = new ImageView(this.imageCache.get("/" + imagePaths[i]));
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            buttons[i].setGraphic(imageView);
            buttons[i].getStyleClass().clear();
        }
    }

    /*
    The following are onAction methods from buttons
    isBuilt = NOT_BUILT cause these are for build new Buildings window
     */
    public void buildExchange(){
        this.buildingToAdd = "exchange";
        inGameController.showBuildingInformation(buildingToAdd, "", BUILT_STATUS.NOT_BUILT);
    }

    public void buildPowerPlant(){
        this.buildingToAdd = "power_plant";
        inGameController.showBuildingInformation(buildingToAdd, "",  BUILT_STATUS.NOT_BUILT);
    }

    public void buildMine(){
        this.buildingToAdd = "mine";
        inGameController.showBuildingInformation(buildingToAdd, "",  BUILT_STATUS.NOT_BUILT);
    }


    public void buildFarm(){
        this.buildingToAdd = "farm";
        inGameController.showBuildingInformation(buildingToAdd, "",  BUILT_STATUS.NOT_BUILT);
    }

    public void buildResearchLab(){
        this.buildingToAdd = "research_lab";
        inGameController.showBuildingInformation(buildingToAdd, "",  BUILT_STATUS.NOT_BUILT);
    }

    public void buildFoundry(){
        this.buildingToAdd = "foundry";
        inGameController.showBuildingInformation(buildingToAdd, "",  BUILT_STATUS.NOT_BUILT);
    }

    public void buildFactory(){
        this.buildingToAdd = "factory";
        inGameController.showBuildingInformation(buildingToAdd, "",  BUILT_STATUS.NOT_BUILT);
    }

    public void buildRefinery(){
        this.buildingToAdd = "refinery";
        inGameController.showBuildingInformation(buildingToAdd, "",  BUILT_STATUS.NOT_BUILT);
    }

    public void buildShipyard() {
        this.buildingToAdd = "shipyard";
        inGameController.showBuildingInformation(buildingToAdd, "", BUILT_STATUS.NOT_BUILT);
    }

    public void buildStronghold() {
        this.buildingToAdd = "fortress";
        inGameController.showBuildingInformation(buildingToAdd, "", BUILT_STATUS.NOT_BUILT);
    }

    public void onClose(){
        setVisible(false);
    }
}
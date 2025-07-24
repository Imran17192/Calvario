package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.BuildingAttributes;
import de.uniks.stp24.model.DistrictAttributes;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.*;

@Component(view = "DeleteStructureWarning.fxml")
public class DeleteStructureComponent extends VBox{
    @FXML
    Text questionMark;
    @FXML
    public Text deleteText;
    @FXML
    ListView<Resource> deleteStructureListView;
    @FXML
    ImageView deleteStructureImageView;
    @FXML
    Button confirmButton;
    @FXML
    Button cancelButton;
    @FXML
    public Text warningText;
    @FXML
    VBox warningContainer;
    @Inject
    Subscriber subscriber;

    @Inject
    IslandsService islandsService;
    @Inject
    ResourcesService resourcesService;
    @Inject
    ImageCache imageCache;

    InGameController inGameController;

    @Inject
    App app;

    @Inject
    ErrorService errorService;


    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    public final Map<String, String> sites = sitesIconPathsMap;
    public final Map<String, String> buildings = buildingsIconPathsMap;
    @Inject
    public TokenStorage tokenStorage;

    @Inject
    public IslandAttributeStorage islandAttributeStorage;

    final Provider<ResourceComponent> resourceComponentProvider = ()-> new ResourceComponent(true, false, true, false, gameResourceBundle, this.imageCache);


    String structureType;

    @Inject
    public DeleteStructureComponent(){
    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }

    public void setWarningText(){
        warningText.setFill(Color.GREEN);
        if(buildings.containsKey(structureType)){
            deleteText.setText(gameResourceBundle.getString("sure.you.want.delete") + " ");
            warningText.setText(gameResourceBundle.getString( buildingTranslation.get(structureType)));
        }else{
            deleteText.setText(gameResourceBundle.getString("sure.you.want.delete") + " ");
            warningText.setText(gameResourceBundle.getString( siteTranslation.get(structureType)));
        }
    }

    //This method is called from inGameController and handles displaying the information in the popup
    public void handleDeleteStructure(String structureType){
        this.structureType = structureType;
        setWarningText();
        displayStructureInfo();
        if (sites.containsKey(structureType)) {
            // Set the image for sites
            deleteStructureImageView.setImage(this.imageCache.get("/"+sites.get(structureType)));
        } else if (buildings.containsKey(structureType)) {
            // Set the image for buildings
            deleteStructureImageView.setImage(new Image(buildings.get(structureType)));
        }
    }

    //Checks if structure is a building or a site and calls method resourceListGeneration for calculating resources
    //that will be returned when deleting a structure
    private void displayStructureInfo() {
        if (buildings.containsKey(structureType)){
            for(BuildingAttributes building: islandAttributeStorage.buildingsAttributes){
                if(building.id().equals(structureType)){
                    resourceListGenerationBuilding(building);
                    break;
                }
            }
        }
        if (sites.containsKey(structureType)){
            resourceListGenerationSite(Objects.requireNonNull(getCertainSite()));
        }

        deleteStructureListView.setCellFactory(list -> new ComponentListCell<>(app, resourceComponentProvider));
    }

    private void resourceListGenerationBuilding(BuildingAttributes structure) {
        Map<String, Double> resourceMapCost = structure.cost();
        Map<String, Double> halvedResourceMapCost = new HashMap<>();
        listGenerateHelper(resourceMapCost, halvedResourceMapCost);
    }
    private void resourceListGenerationSite(DistrictAttributes structure) {
        Map<String, Double> resourceMapCost = structure.cost();
        Map<String, Double> halvedResourceMapCost = new HashMap<>();
        if (Objects.nonNull(resourceMapCost)) {
            listGenerateHelper(resourceMapCost, halvedResourceMapCost);
        }
    }

    private void listGenerateHelper(Map<String, Double> resourceMapCost, Map<String, Double> halvedResourceMapCost) {
        for (Map.Entry<String, Double> entry : resourceMapCost.entrySet()) {
            halvedResourceMapCost.put(entry.getKey(), entry.getValue() / 2);
        }
        ObservableList<Resource> resourceListCost = resourcesService.generateResourceList(halvedResourceMapCost, deleteStructureListView.getItems(), null, false);
        deleteStructureListView.setItems(resourceListCost);
    }

    public void onCancel(){
        inGameController.handleAfterStructureDelete();
        setVisible(false);
    }

    //Differs between building and site
    //Calls delete function in resourcesService
    public void delete(){
        Island island = tokenStorage.getIsland();
        if (tokenStorage.getIsland() != null){
            if (sites.containsKey(structureType)) {
                // Handle deletion for sites
                if (tokenStorage.getIsland().sites().get(structureType) != 0) {
                    subscriber.subscribe(resourcesService.destroySite(tokenStorage.getGameId(), island, structureType), result -> {
                        tokenStorage.setIsland(islandsService.updateIsland(result));
                        islandAttributeStorage.setIsland(islandsService.updateIsland(result));
                        inGameController.islandsService.updateIslandBuildings(islandAttributeStorage, inGameController, islandAttributeStorage.getIsland().buildings());
                        inGameController.updateAmountSitesGrid();
                        onCancel();
                    },
                            error -> errorService.getStatus(error));
                }
            } else if (buildings.containsKey(structureType)) {
                // Handle deletion for buildings
                subscriber.subscribe(resourcesService.destroyBuilding(tokenStorage.getGameId(), island, structureType), result -> {
                    tokenStorage.setIsland(islandsService.updateIsland(result));
                    islandAttributeStorage.setIsland(islandsService.updateIsland(result));
                    inGameController.islandsService.updateIslandBuildings(islandAttributeStorage, inGameController, islandAttributeStorage.getIsland().buildings());
                    inGameController.setSitePropertiesInvisible();
                    onCancel();
                },
            error -> errorService.getStatus(error));
            } else {
                throw new IllegalArgumentException("Unknown structure type: " + structureType);
            }
        }
    }

    private DistrictAttributes getCertainSite(){
        for(DistrictAttributes site: islandAttributeStorage.districtAttributes){
            if(site.id().equals(structureType)){
                return site;
            }
        }
        return null;
    }

}
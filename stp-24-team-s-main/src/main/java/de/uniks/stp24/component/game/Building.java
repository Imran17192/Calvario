package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.JobsService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;

import static de.uniks.stp24.service.Constants.*;


@Component(view = "BuildingElement.fxml")
public class Building extends VBox {
    @FXML
    Button building;

    @Inject
    IslandAttributeStorage islandAttributeStorage;
    InGameController inGameController;
    @Inject
    JobsService jobsService;

    public Building(BuildingsComponent buildingsComponent, String buildingName,
                    IslandAttributeStorage islandAttributes, InGameController inGameController, String presetType, String jobID){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BuildingElement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        building.setDisable(inGameController.tokenStorage.isSpectator() ||
                !Objects.equals(islandAttributes.getIsland().owner(), inGameController.tokenStorage.getEmpireId()));
        building.getStyleClass().clear();
        this.inGameController = inGameController;

        ImageView imageView = new ImageView();
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);

        imageView.setImage(buildingsComponent.imageCache.get("/" + buildingsIconPathsMap.getOrDefault(buildingName,
                "de/uniks/stp24/icons/buildings/empty_building_element.png")));
        setId("empty_building_element");
        if (Objects.nonNull(presetType))
            if (presetType.equals("on_pause"))
                imageView.setImage(buildingsComponent.imageCache.get("/" + buildingsOnQueueMap.get(buildingName)));
            else imageView.setImage(buildingsComponent.imageCache.get("/" + buildingsJobProgressMap.get(buildingName)));

        building.setGraphic(imageView);
        building.setOnMouseClicked(event -> {
            String relevantPart = null;
            if(imageView.getImage() != null) {
                String imageUrl = imageView.getImage().getUrl();
                relevantPart = extractRelevantPath(imageUrl);
            }
            if (imageView.getImage() != null && !Objects.equals(relevantPart,
                    "/de/uniks/stp24/icons/buildings/empty_building_element.png")){
                inGameController.buildingsWindowComponent.setVisible(false);
                inGameController.setSitePropertiesInvisible();
                BUILT_STATUS isBuilt;
                if (jobID.isEmpty())
                    isBuilt = BUILT_STATUS.BUILT;
                else
                    isBuilt = BUILT_STATUS.QUEUED;
                inGameController.showBuildingInformation(buildingName, jobID, isBuilt);

            } else inGameController.showBuildingWindow();
        });
    }

    private String extractRelevantPath(String imageUrl) {
        int index = imageUrl.indexOf("/de/uniks/stp24/");
        if (index != -1) return imageUrl.substring(index);
        else return imageUrl; // Return the whole URL if the relevant part is not found
    }

    @Inject
    public Building(){

    }
}

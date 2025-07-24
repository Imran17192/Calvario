package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Ships;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.List;
import java.util.ResourceBundle;

@Component(view = "ShipTypesOfFleet.fxml")
public class ShipTypesOfFleetComponent extends VBox implements ReusableItemComponent<Ships.BlueprintInFleetDto> {
    @FXML
    public Label typeLabel;
    @FXML
    public Label sizeLabel;
    @FXML
    public Button buildShipButton;
    @FXML
    public Button decrementSizeButton;
    @FXML
    public Button incrementSizeButton;

    public final ResourcesService resourcesService;
    public final ShipService shipService;
    private final FleetService fleetService;
    public final Subscriber subscriber;
    public final JobsService jobService;
    public final IslandsService islandsService;
    private final TokenStorage tokenStorage;
    private final FleetManagerComponent fleetManagerComponent;
    private final ResourceBundle gameResourceBundle;

    private Ships.BlueprintInFleetDto blueprintInFleetDto;

    @Inject
    public ShipTypesOfFleetComponent(FleetManagerComponent fleetManagerComponent, ResourcesService resourcesService, ShipService shipService, Subscriber subscriber, FleetService fleetService, ResourceBundle gameResourceBundle){
        this.fleetManagerComponent = fleetManagerComponent;
        this.resourcesService = resourcesService;
        this.shipService = shipService;
        this.subscriber = subscriber;
        this.fleetService = fleetService;
        this.islandsService = fleetManagerComponent.islandsService;
        this.jobService = fleetManagerComponent.jobsService;
        this.tokenStorage = fleetManagerComponent.tokenStorage;
        this.gameResourceBundle = gameResourceBundle;
    }

    @OnRender
    public void render() {
        Timeline incTimeline = new Timeline(new KeyFrame(Duration.millis(100), actionEvent -> incrementSize()));
        incTimeline.setCycleCount(Timeline.INDEFINITE);

        Timeline decTimeline = new Timeline(new KeyFrame(Duration.millis(100), actionEvent -> decrementSize()));
        decTimeline.setCycleCount(Timeline.INDEFINITE);

        incrementSizeButton.setOnMousePressed(e -> incTimeline.playFromStart());
        incrementSizeButton.setOnMouseReleased(e -> incTimeline.stop());

        decrementSizeButton.setOnMousePressed(e -> decTimeline.playFromStart());
        decrementSizeButton.setOnMouseReleased(e -> decTimeline.stop());
    }

    public void setItem(Ships.BlueprintInFleetDto blueprintInFleetDto){
        this.incrementSizeButton.setId("incrementSizeButton_" + blueprintInFleetDto.type());
        this.decrementSizeButton.setId("decrementSizeButton_" + blueprintInFleetDto.type());
        this.blueprintInFleetDto = blueprintInFleetDto;

        this.typeLabel.setText(gameResourceBundle.getString("ship." + blueprintInFleetDto.type()));
        int plannedSize = 0;
        if(blueprintInFleetDto.fleet().size().get(blueprintInFleetDto.type()) != null) {
            plannedSize = blueprintInFleetDto.fleet().size().get(blueprintInFleetDto.type());
        }
        this.sizeLabel.setText(blueprintInFleetDto.count() + "/" + plannedSize);
        this.decrementSizeButton.setDisable(false);
        if(blueprintInFleetDto.fleet().size().containsKey(this.blueprintInFleetDto.type())) {
            if (blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) == 0) {
                this.decrementSizeButton.setDisable(true);
            }
            if (blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) <= this.blueprintInFleetDto.count()) {
                this.decrementSizeButton.setDisable(true);
            }
        } else {
            this.incrementSize();
        }
    }

    public int shipJobsOnIsland(){
        return this.jobService.getObservableListForSystem(blueprintInFleetDto.fleet().location()).filtered(job -> job.type().equals("ship")).size();
    }

    private boolean buildIsPossible(){
        boolean buildIsPossible = true;
        if (!this.resourcesService.hasEnoughResources(shipService.getNeededResources(blueprintInFleetDto.type()))) {
            this.fleetManagerComponent.setErrorLabel("resources");
            buildIsPossible = false;
        }
        if(blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) <= this.blueprintInFleetDto.count()){
            this.fleetManagerComponent.setErrorLabel("plannedSize");
            buildIsPossible = false;
        }
        int numberOfShipJobs = shipJobsOnIsland();
        List<Island> islands = islandsService.getIsles().stream().filter(island -> island.id().equals(this.blueprintInFleetDto.fleet().location())).toList();
        if(islands.isEmpty()){
            this.fleetManagerComponent.setErrorLabel("wilderness");
            buildIsPossible = false;
        } else if (!islands.getFirst().owner().equals(this.tokenStorage.getEmpireId())) {
            this.fleetManagerComponent.setErrorLabel("enemiesIsland");
            buildIsPossible = false;
        } else {
            int numberOfShipYards = islands.getFirst().buildings().stream()
                    .filter("shipyard"::equals).toList().size();
            if (numberOfShipJobs >= numberOfShipYards) {
                this.fleetManagerComponent.setErrorLabel("shipyard");
                buildIsPossible = false;
            }
        }
        return buildIsPossible;
    }

    public void buildShip() {
        if(buildIsPossible()) {
            int shipJobsBeforeStart = shipJobsOnIsland();
            this.subscriber.subscribe(this.shipService.beginShipJob(this.blueprintInFleetDto.fleet()._id(), this.blueprintInFleetDto.type(), this.blueprintInFleetDto.fleet().location()),
                    job -> {
                        this.fleetManagerComponent.setShipFinisher(job);
                        this.fleetManagerComponent.setErrorLabel("successful");
                        int shipJobsAfterStart = shipJobsOnIsland();
                        this.fleetManagerComponent.setIslandName(shipJobsAfterStart == shipJobsBeforeStart);
                    }, error -> System.out.println("Error while trying to create a new ship job in ShipTypesOfFleetComponent:\n" + error.getMessage()));
        }
    }


    public void decrementSize(){
        int newSize = 0;
        if(this.blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) != null) {
            newSize = this.blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) > 0 ? this.blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) - 1 : 0;
        }
        editSize(newSize);
    }

    public void incrementSize(){
        int newSize = 1;
        if(this.blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) != null) {
           newSize = this.blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) + 1;
        }
        editSize(newSize);
    }

    /**
     * Planned size is changed: sizeLabel and command limit are updated
     * The decrement button is disabled if - there are 0 planned ships
     *                                     - number of planned ships <= real ships of this blueprint
     * If the planned size is 0 and there are no real ships the blueprint is removed
     * @param newSize: new planned size of ships of this type
     */
    public void editSize(int newSize) {
        this.subscriber.subscribe(this.fleetService.editSizeOfFleet(this.blueprintInFleetDto.type(), newSize, this.blueprintInFleetDto.fleet()),
                dto -> {
                    this.sizeLabel.setText(this.sizeLabel.getText().replaceAll("/.*", "/" + dto.size()
                            .get(this.blueprintInFleetDto.type())));
                    this.fleetManagerComponent.setCommandLimit(dto,false);
                    this.decrementSizeButton.setDisable(false);
                    if (dto.size().get(this.blueprintInFleetDto.type()) == 0) {
                        this.decrementSizeButton.setDisable(true);
                        if (this.blueprintInFleetDto.count() == 0) {
                            this.shipService.removeBlueprintFromFleet(blueprintInFleetDto);
                        }
                    }
                    if (dto.size().get(this.blueprintInFleetDto.type()) <= this.blueprintInFleetDto.count())
                        this.decrementSizeButton.setDisable(true);
                }, error -> System.out.println("Error while changing planned Size in the ShipTypesOfFleetComponent:\n" + error.getMessage()));
    }
}


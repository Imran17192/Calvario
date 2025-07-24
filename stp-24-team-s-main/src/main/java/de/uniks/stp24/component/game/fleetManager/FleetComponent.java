package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.service.game.JobsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

@Component(view = "Fleet.fxml")
public class FleetComponent extends VBox implements ReusableItemComponent<Fleet> {
    @FXML
    public Label fleetNameLabel;
    @FXML
    public ImageView fleetImageview;
    @FXML
    public Label sizeLabel;
    @FXML
    public Button editFleetButton;
    @FXML
    public Button deleteFleetButton;

    @Inject
    ImageCache imageCache = new ImageCache();

    private final TokenStorage tokenStorage;
    private final Subscriber subscriber;
    private final FleetService fleetService;
    private final FleetManagerComponent fleetManagerComponent;
    private final JobsService jobsService;
    private Fleet fleet;

    @Inject
    public FleetComponent(FleetManagerComponent fleetManagerComponent, TokenStorage tokenStorage, Subscriber subscriber, FleetService fleetService){
        this.fleetManagerComponent = fleetManagerComponent;
        this.subscriber = subscriber;
        this.tokenStorage = tokenStorage;
        this.fleetService = fleetService;
        this.jobsService = fleetManagerComponent.jobsService;
    }

    @Override
    public void setItem(Fleet fleet){
        this.fleet = fleet;
        this.deleteFleetButton.setId("deleteFleetButton_" + fleet._id());
        this.editFleetButton.setId("editFleetButton_"  + fleet._id());

        this.fleetNameLabel.setText(fleet.name());
        this.sizeLabel.setText(fleet.ships() + "/"  + fleet.size().values().stream().mapToInt(Integer::intValue).sum());
    }

    public void deleteFleet(){
        int jobCount = 0;
        if (this.jobsService.getJobObservableListOfType("ship") != null) {
            jobCount = this.jobsService.getJobObservableListOfType("ship").stream().filter(job -> job.fleet().equals(this.fleet._id())).toList().size();
        }
        if (this.jobsService.getJobObservableListOfType("travel") != null) {
            jobCount += this.jobsService.getJobObservableListOfType("travel").stream().filter(job -> job.fleet().equals(this.fleet._id())).toList().size();
        }
        if (this.jobsService.getJobObservableListOfType("upgrade") != null) {
            jobCount += this.jobsService.getJobObservableListOfType("upgrade").stream().filter(job -> job.fleet() != null && job.fleet().equals(this.fleet._id())).toList().size();
        }
        if(jobCount == 0) {
            this.subscriber.subscribe(this.fleetService.deleteFleet(this.tokenStorage.getGameId(), this.fleet._id()),
                    result -> {
                    },
                    error -> System.out.println("Error while deleting a fleet in the FleetComponent:\n" + error.getMessage()));
        }
    }

    public void editFleet(){
        this.fleetManagerComponent.editSelectedFleet(fleet);
    }

    @OnRender
    public void render() {
        fleetImageview.setImage(imageCache.get("assets/technologies/tags/engineering.png"));
    }
}

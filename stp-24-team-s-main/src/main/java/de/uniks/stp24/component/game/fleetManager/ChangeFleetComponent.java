package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.service.game.ShipService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.uniks.stp24.model.Ships.ReadShipDTO;
import static de.uniks.stp24.model.Ships.Ship;

@Component(view = "ChangeFleet.fxml")
public class ChangeFleetComponent extends VBox {
    @FXML
    public Label newFleetOfShipNameLabel;
    @FXML
    public Button showLastFleetButton;
    @FXML
    public Button showNextFleetButton;
    @FXML
    public Button confirmFleetChangeButton;

    @Inject
    public ShipService shipService;
    @Inject
    public FleetService fleetService;
    @Inject
    public Subscriber subscriber;

    private FleetManagerComponent fleetManagerComponent;

    private int fleetNameIndex = 0;
    public List<Fleet> fleetsOnIslandList = new ArrayList<>();
    private ReadShipDTO readShipDTO;

    @Inject
    public ChangeFleetComponent() {
    }

    public void setFleetManager(FleetManagerComponent fleetManagerComponent){
        this.fleetManagerComponent = fleetManagerComponent;
    }

    public void close() {
        this.setVisible(false);
    }

    public void showLastFleetName() {
        this.fleetNameIndex = this.fleetNameIndex - 1 >= 0 ? this.fleetNameIndex - 1 : this.fleetsOnIslandList.size() - 1;
        this.newFleetOfShipNameLabel.setText(fleetsOnIslandList.get(fleetNameIndex).name());
    }

    public void showNextFleetName() {
        this.fleetNameIndex = this.fleetNameIndex + 1 < this.fleetsOnIslandList.size() ? this.fleetNameIndex + 1 : 0;
        this.newFleetOfShipNameLabel.setText(fleetsOnIslandList.get(fleetNameIndex).name());
    }

    /** Change fleet of a ship and add planned size for this shipType of the new fleet to be at least one */
    public void confirmFleetChange() {
        this.subscriber.subscribe(this.shipService.changeFleetOfShip(fleetsOnIslandList.get(fleetNameIndex)._id(), readShipDTO),
                ship -> {
                    Fleet fleetOfShip = this.fleetService.getFleet(ship.fleet());
                    if (!fleetOfShip.size().containsKey(ship.type())) {
                        this.subscriber.subscribe(this.fleetService.editSizeOfFleet(ship.type(), 1, fleetOfShip),
                                dto -> finishFleetChange(ship),
                                error -> System.out.println("Error while incrementing the planned size of a fleet after a fleet change in the ChangeFleetComponent:\n" + error.getMessage()));
                    } else {
                        finishFleetChange(ship);
                    }
                }, error -> System.out.println("Error while changing the fleet of a ship in the ChangeFleetComponent:\n" + error.getMessage()));
    }

    /** Update ships of the old and the new fleet of the ship **/
    private void finishFleetChange(Ship ship) {
        this.shipService.deleteShipFromGroups(this.readShipDTO);
        this.fleetService.adaptShipCount(this.readShipDTO.fleet(), -1);
        this.fleetService.adaptShipCount(ship.fleet(), 1);
        this.fleetManagerComponent.setCommandLimit(this.fleetService.getFleet(readShipDTO.fleet()), false);
        this.fleetManagerComponent.blueprintInFleetListView.refresh();
        this.close();
    }

    /** Get all fleets of the empire at this system which are different to the currently edited fleet **/
    public void changeFleetOfShip(ReadShipDTO readShipDTO) {
        Fleet editedFleet = this.fleetService.getFleet(readShipDTO.fleet());

        this.fleetsOnIslandList = this.fleetService.getFleetsOnIsland(editedFleet.location()).stream()
                .filter(fleetDto -> !fleetDto._id().equals(editedFleet._id()) && fleetDto.empire().equals(editedFleet.empire()))
                .collect(Collectors.toList());
        this.fleetNameIndex = 0;
        this.readShipDTO = readShipDTO;
        this.newFleetOfShipNameLabel.setText(fleetsOnIslandList.getFirst().name());
        this.setVisible(true);
    }

    @OnDestroy
    public void destroy(){
        this.subscriber.dispose();
    }
}

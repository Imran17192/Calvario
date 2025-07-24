package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.EffectSource;
import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.service.game.IslandsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Component(view = "NewFleet.fxml")
public class NewFleetComponent extends VBox {
    @FXML
    public Label islandNameLabel;
    @FXML
    public Button confirmIslandButton;
    @FXML
    public Button lastIslandButton;
    @FXML
    public Button nextIslandButton;

    @Inject
    public IslandsService islandsService;
    @Inject
    public FleetService fleetService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public Subscriber subscriber;
    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;


    private FleetManagerComponent fleetManagerComponent;
    final Random rand = new Random();
    public int islandNameIndex = 0;
    public List<Island> islandList = new ArrayList<>();

    @Inject
    public NewFleetComponent() {
    }

    public void setFleetManager(FleetManagerComponent fleetManagerComponent){
        this.fleetManagerComponent = fleetManagerComponent;
    }

    public void close() {
        this.setVisible(false);
    }

    public void showNextIslandName() {
        this.islandNameIndex = this.islandNameIndex + 1 < this.islandList.size() ? this.islandNameIndex + 1 : 0;
        setIslandNameText(this.islandNameIndex);
    }

    public void showLastIslandName() {
        this.islandNameIndex = this.islandNameIndex - 1 >= 0 ? this.islandNameIndex - 1 : this.islandList.size() - 1;
        setIslandNameText(this.islandNameIndex);
    }

    public void setIslandNameText(int index) {
        int numberOfShipyards = (int) this.islandList.get(index).buildings().stream()
                .filter("shipyard"::equals)
                .count();
        this.islandNameLabel.setText(islandList.get(index).name() + " (" + numberOfShipyards + " " + this.gameResourceBundle.getString("building.shipyard") + ")");
    }

    public void createNewFleet() {
        this.setVisible(true);
        this.islandList.addAll(this.islandsService.getIsles());
        this.islandList = this.islandList.stream()
                .filter(shortSystemDto -> shortSystemDto.owner() != null && shortSystemDto.owner().equals(this.tokenStorage.getEmpireId()) && shortSystemDto.buildings().contains("shipyard"))
                .collect(Collectors.toList());
        this.islandNameIndex = 0;
        setIslandNameText(0);
    }

    /** Creation of a new fleet at the chosen island **/
    public void confirmIsland() {
        String fleetName = getFleetName(); 
        
        Fleets.CreateFleetDTO newFleet = new Fleets.CreateFleetDTO(fleetName,
                this.islandList.get(islandNameIndex).id(), new HashMap<>(),
                new HashMap<>(), new HashMap<>(), new EffectSource[]{});
        this.subscriber.subscribe(this.fleetService.createFleet(this.tokenStorage.getGameId(), newFleet),
                result -> {
                    close();
                    this.fleetManagerComponent.showFleets();
                    this.islandList.clear();
                }, error -> System.out.println("Error while creating a new fleet in the NewFleetComponent:\n" + error.getMessage())
        );
    }
    
    public String getFleetName(){
        final String randomfleetName = gameResourceBundle.getString("fleetName" + rand.nextInt(1,20 ));
        String fleetName = randomfleetName;
        if(this.fleetService.getEmpireFleets(this.tokenStorage.getEmpireId()).stream().anyMatch(fleet -> fleet.name().equals(randomfleetName))){
            fleetName += " 2";
        }
        return fleetName;
    }

    @OnDestroy
    public void destroy(){
        this.subscriber.dispose();
    }
}

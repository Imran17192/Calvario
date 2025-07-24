package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.GameFleetController;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.rest.ShipsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static de.uniks.stp24.model.Fleets.Fleet;
import static de.uniks.stp24.model.Ships.*;

@Singleton
public class ShipService {
    @Inject
    public Subscriber subscriber;
    @Inject
    public EventListener eventListener;
    @Inject
    public ShipsApiService shipsApiService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public JobsApiService jobsApiService;
    @Inject
    public VariableDependencyService variableDependencyService;

    @Inject
    public ShipService(){}


    public ObservableList<ReadShipDTO> shipsInSelectedFleet = FXCollections.observableArrayList();
    public Map<String, Integer> blueprintsInFleetMap = new HashMap<>();
    public ObservableList<BlueprintInFleetDto> blueprintsInFleetList = FXCollections.observableArrayList();
    public ArrayList<ShipType> shipTypesAttributes;
    public Map<String, Double> shipSpeeds;
    private String lastShipUpdate = "";
    private String lastShipCreation = "";
    private String lastShipDeletion= "";
    public Map<String, GameFleetController> mapOfFleetComponents = new HashMap<>();
    List<GameFleetController> aliveFleets = new ArrayList<>();



    public void initShipTypes(){
        shipTypesAttributes = variableDependencyService.createVariableDependencyShipType();
        shipSpeeds = shipTypesAttributes.stream().collect(Collectors.toMap(ShipType::_id, ShipType::speed));
    }

    public int getFleetSpeed(ReadShipDTO[] ships) {
        if (Objects.isNull(this.shipSpeeds)) return 5;

        return (int) Arrays.stream(ships).toList().stream()
                .map(ReadShipDTO::type)
                .collect(Collectors.toSet()).stream()
                .map(type -> this.shipSpeeds.get(type))
                .mapToDouble(v -> (Objects.isNull(v)) ? 5.0f : v)
                .min().orElse(5);
    }

    public Map<String, Double> getNeededResources(String type) {
        for(ShipType shipType : shipTypesAttributes){
            if (shipType._id().equals(type)) {
                return shipType.cost();
            }
        }
        return null;
    }

    /**
     * Initializes the shipsList and blueprintsInFleetList with given data of the fleet
     * @param dto: all ships of the edited fleet
     * @param fleet: currently edited fleet
     */
    public void initializeFleetEdition(ReadShipDTO[] dto, Fleet fleet){
        Arrays.stream(dto).forEach(ship -> {
            this.shipsInSelectedFleet.add(ship);
            if (!this.blueprintsInFleetMap.containsKey(ship.type())) {
                this.blueprintsInFleetMap.put(ship.type(), 1);
            } else {
                this.blueprintsInFleetMap.compute(ship.type(), (k, currentCount) -> currentCount == null ? 1 : currentCount + 1);
            }
        });
        fleet.size().forEach((key, value) -> {
            if (value != 0) {
                blueprintsInFleetMap.putIfAbsent(key, 0);
            }
        });
        this.blueprintsInFleetList.addAll(this.blueprintsInFleetMap.entrySet().stream().map(entry ->
                new BlueprintInFleetDto(entry.getKey(), entry.getValue(), fleet)).toList());
        initializeShipListeners(fleet._id());
    }

    public ObservableList<ReadShipDTO> getShipsInSelectedFleet(){
        return this.shipsInSelectedFleet;
    }

    public ObservableList<BlueprintInFleetDto> getBlueprintsInSelectedFleet(){
        return this.blueprintsInFleetList;
    }

    public void clearEditedFleetInfos(){
        this.blueprintsInFleetMap.clear();
        this.shipsInSelectedFleet.clear();
        this.blueprintsInFleetList.clear();
        this.subscriber.dispose();
    }

    public void initializeShipListeners(String fleetID) {
        this.subscriber.subscribe(this.eventListener.listen("games." + this.tokenStorage.getGameId() + ".fleets." + fleetID + ".ships.*.*",
                Ship.class), event -> {
            ReadShipDTO ship = readShipDTOFromShip(event.data());
            switch (event.suffix()) {
                case "created" -> {
                    giveInfo(event.data().fleet());
                    if (!ship._id().equals(this.lastShipCreation)) {
                        this.addShipToGroups(ship);
                        this.lastShipCreation = ship._id();
                    }
                }
                case "updated" -> {
                    if (!ship.updatedAt().equals(this.lastShipUpdate)) {
                        this.updateShipInGroups(ship);
                        this.lastShipUpdate = ship.updatedAt();
                    }
                }
                case "deleted" -> {
                    if (!ship._id().equals(this.lastShipDeletion)) {
                        this.lastShipDeletion = ship._id();
                        this.deleteShipFromGroups(ship);
                    }
                }
            }
        }, Throwable::printStackTrace);
    }

    public void deleteShipFromGroups(ReadShipDTO ship) {
        this.shipsInSelectedFleet.removeIf(other -> other.equals(ship));
        replaceBlueprintByType(ship.type(), -1);
    }

    private void updateShipInGroups(ReadShipDTO ship) {
        this.shipsInSelectedFleet.replaceAll(old -> old.equals(ship) ? ship : old);
    }

    private void addShipToGroups(ReadShipDTO ship) {
        this.shipsInSelectedFleet.add(ship);
        replaceBlueprintByType(ship.type(), 1);
    }

    public void replaceBlueprintByType(String type, int diff) {
        for (int i = 0; i < this.blueprintsInFleetList.size(); i++) {
            BlueprintInFleetDto blueprint = this.blueprintsInFleetList.get(i);
            if (blueprint.type().equals(type)) {
                this.blueprintsInFleetList.set(i, new BlueprintInFleetDto(blueprint.type(), blueprint.count() + diff, blueprint.fleet()));
            }
        }
    }

    public void addBlueprintToFleet(BlueprintInFleetDto blueprintInFleetDto){
        this.blueprintsInFleetList.add(blueprintInFleetDto);
    }

    public void removeBlueprintFromFleet(BlueprintInFleetDto blueprintInFleetDto){
        this.blueprintsInFleetList.removeIf(other -> other.equals(blueprintInFleetDto));
    }

    public Observable<ReadShipDTO[]> getShipsOfFleet(String fleetID){
        return this.shipsApiService.getAllShips(tokenStorage.getGameId(), fleetID);
    }

    public Observable<Jobs.Job> beginShipJob(String fleetID, String shipType, String systemID) {
        return this.jobsApiService.createShipJob(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(),
                Jobs.createShipJob(fleetID, shipType, systemID));
    }

    public Observable<Ship> deleteShip(ReadShipDTO readShipDTO){
        return this.shipsApiService.deleteShip(this.tokenStorage.getGameId(),readShipDTO.fleet(), readShipDTO._id());
    }

    public Observable<Ship> changeFleetOfShip(String newFleetID, ReadShipDTO readShipDTO){
        return this.shipsApiService.patchShip(this.tokenStorage.getGameId(), readShipDTO.fleet(), readShipDTO._id(), new UpdateShipDTO(newFleetID, readShipDTO._public(), new HashMap<>()));
    }

    public void recreateListOfShips(String fleetID, ObservableList<ReadShipDTO> list) {
        this.subscriber.subscribe(shipsApiService.getAllShips(this.tokenStorage.getGameId(), fleetID),
          result ->     {
              Arrays.stream(result).forEach(ship -> {
                  if (ship.fleet().equals(fleetID) && !list.contains(ship)) {
                      list.add(ship);
                  }
              });
              mapOfFleetComponents.get(fleetID).refreshHealthInfo();
          },
          Throwable::printStackTrace);
    }

    public void giveInfo(String fleetID) {
        GameFleetController tmp = mapOfFleetComponents.getOrDefault(fleetID,null);
        if (Objects.nonNull(tmp)) recreateListOfShips(fleetID, tmp.dynShipInFleet);
    }

    public void addFleetComponent (String id, GameFleetController ctrl) {
        this.mapOfFleetComponents.put(id,ctrl);
        this.aliveFleets.add(ctrl);
    }

    public Map<String, Double> getMaxHealthInfoMap() {
        Map<String, Double> tmp = new HashMap<>();
        for (ShipType ship : shipTypesAttributes){
            tmp.put(ship._id(), ship.health());
        }
        return tmp;
    }
}























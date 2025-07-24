package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.BattleResultComponent;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.controllers.helper.BattleEntry;
import de.uniks.stp24.controllers.helper.BattleEntry.BATTLE_TYPE;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Ships.Ship;
import de.uniks.stp24.service.TokenStorage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class BattleService {
    @Inject
    public FleetService fleetService;
    @Inject
    public IslandsService islandsService;
    @Inject
    public ContactsService contactsService;
    @Inject
    public TokenStorage tokenStorage;

    BattleResultComponent battleResultComponent;

    @Inject
    public BattleService() {}

    private final List<BattleEntry> battles = new ArrayList<>();

    public void setBattleConditionUpdates() {
        this.fleetService.onFleetFled(this::onFleetFled);
        this.fleetService.onLoadingFinished(this::checkFleetPosition);
        this.fleetService.onFleetLocationChanged(this::checkFleetPosition);
        this.fleetService.onShipDestroyed(this::addDestroyedShip);
        this.fleetService.onFleetDestroyed(this::checkBattleConditionOnFleetDestroyed);
        this.contactsService.onWarDeleted(this::checkBattleConditionOnWarFinished);
        this.contactsService.onWarCreated(this::createBattlesOnWarBegin);
    }

    public void checkFleetPosition(Fleet fleet) {
        if (Objects.isNull(fleet.empire())) return;

        IslandComponent islandComponent =  this.islandsService.getIslandComponent(fleet.location());
        Island location = islandComponent.island;

        if (Objects.nonNull(location.owner())) {
            if (isNoneBattleStarted(fleet.empire(), location.owner(), location.id()))
                this.createBattle(fleet.empire(), location.owner(), location.id(), islandComponent);
        } else {
            List<Fleet> otherFleets = this.fleetService.getFleetsOnIsland(location.id()).stream()
                    .filter(other -> Objects.isNull(other.empire()) || !other.empire().equals(fleet.empire())).toList();
            otherFleets.stream().map(Fleet::empire).collect(Collectors.toSet()).forEach(empireID -> {
                if (Objects.nonNull(empireID)) {
                    if (isNoneBattleStarted(fleet.empire(), empireID, location.id()))
                        this.createBattle(fleet.empire(), empireID, location.id(), islandComponent);
                } else {
                    if (isNoneBattleStarted(fleet.empire(), location.id()))
                        this.createBattle(fleet.empire(), null, location.id(), islandComponent);
                }
            });
        }
    }

    public void createBattlesOnWarBegin(WarDto warDto) {
        this.fleetService.getEmpireFleets(warDto.attacker()).forEach(this::checkFleetPosition);
        this.fleetService.getEmpireFleets(warDto.defender()).forEach(this::checkFleetPosition);
    }

    public void addDestroyedShip(Ship ship) {
        this.battles.stream().filter(battleEntry ->
                        isInBattle(battleEntry, ship.empire(), this.fleetService.getFleet(ship.fleet()).location()))
                .findFirst().map(battleEntry -> battleEntry.addShip(ship));
    }

    private void onFleetFled(Fleet oldFleet) {
        this.battles.stream().filter(battleEntry -> battleEntry.getLocation().equals(oldFleet.location()))
                .findFirst().map(battleEntry -> {
                    if (this.fleetService.getFleetsOnIsland(oldFleet.location())
                            .filtered(other -> oldFleet.empire().equals(other.empire())).isEmpty() &&
                             Objects.nonNull(this.islandsService.getIsland(oldFleet.location()).owner()) &&
                    !this.islandsService.getIsland(oldFleet.location()).owner().equals(oldFleet.empire()))
                        this.deleteBattle(battleEntry);

                    return battleEntry;
                });
    }

    private void checkBattleConditionOnWarFinished(WarDto warDto) {
        this.battles.stream()
                .filter(battleEntry -> battleEntry.equals(warDto.attacker(), warDto.defender()))
                .toList().forEach(this::deleteBattle);
    }

    public void checkBattleConditionOnIslandClaimed(Island island) {
        this.battles.stream().filter(battleEntry -> battleEntry.getLocation().equals(island.id()))
                .findFirst().map(battleEntry -> {
                    battleEntry.setWinner(island.owner());
                    this.finishBattle(battleEntry);
                    return battleEntry;
                });
    }

    public void checkBattleConditionOnFleetDestroyed(Fleet fleet) {
        this.battles.stream().filter(battleEntry -> isInBattle(battleEntry, fleet.empire(), fleet.location()))
                .findFirst().map(battleEntry -> {
                    if (Objects.isNull(fleet.empire())) {
                        if (!this.fleetService.getFleetsOnIsland(fleet.location()).filtered(other ->
                                other.empire().equals(this.tokenStorage.getEmpireId())).isEmpty()) {
                            battleEntry.setWinner(this.tokenStorage.getEmpireId());
                            finishBattle(battleEntry);
                        }
                        return battleEntry;
                    }

                    if (this.fleetService.getFleetsOnIsland(fleet.location()).filtered(other ->
                            Objects.nonNull(other.empire()) && other.empire().equals(fleet.empire())).isEmpty()) {

                        if (this.tokenStorage.getEmpireId().equals(fleet.empire())) {
                            if (this.tokenStorage.getEmpireId().equals(battleEntry.getDefender()))
                                battleEntry.setWinner(battleEntry.getAttacker());
                            else battleEntry.setWinner(battleEntry.getDefender());
                        } else
                            battleEntry.setWinner(this.tokenStorage.getEmpireId());

                        finishBattle(battleEntry);
                    }
                    return battleEntry;
                });
    }

    private void createBattle(String empireID1, String empireID2, String location, IslandComponent comp) {
        BattleEntry entry;
        if (Objects.isNull(empireID2))
            entry = new BattleEntry(empireID1, location);
        else entry = new BattleEntry(empireID1, empireID2, location);

        if (entry.containsEmpire(this.tokenStorage.getEmpireId()))
            comp.toggleSableVisibility(true);

        this.battles.add(entry);
    }

    private void deleteBattle(BattleEntry battleEntry) {
        this.battles.remove(battleEntry);
        if (battleEntry.containsEmpire(this.tokenStorage.getEmpireId()))
            this.islandsService.getIslandComponent(battleEntry.getLocation())
                .toggleSableVisibility(false);
    }

    private void finishBattle(BattleEntry battleEntry) {
        this.deleteBattle(battleEntry);
        if (battleEntry.containsEmpire(this.tokenStorage.getEmpireId()))
            this.battleResultComponent.setInfo(battleEntry);
    }

    private boolean isInBattle(BattleEntry battleEntry, String empireID, String locationID) {
        if (Objects.isNull(empireID))
            return battleEntry.getLocation().equals(locationID);
        if (Objects.isNull(battleEntry.getDefender()))
            return (battleEntry.getAttacker().equals(empireID)) && battleEntry.getLocation().equals(locationID);

        return (battleEntry.getAttacker().equals(empireID) || battleEntry.getDefender().equals(empireID)) &&
                battleEntry.getLocation().equals(locationID);
    }

    private boolean isNoneBattleStarted(String attackerID, String locationID) {
        return battles.stream().noneMatch(battle -> battle.equals(locationID, null, attackerID, BATTLE_TYPE.WILD));
    }

    private boolean isNoneBattleStarted(String attackerID, String defenderID, String locationID) {
        return contactsService.areAtWar(attackerID, defenderID) &&
                battles.stream().noneMatch(battle -> battle.equals(locationID, defenderID, attackerID, BATTLE_TYPE.EMPIRES));
    }

    public void dispose() {
        this.battles.clear();
    }

    public void setBattleResultComponent(BattleResultComponent battleResultComponent) {
        this.battleResultComponent = battleResultComponent;
    }
}

package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.ExplainedVariableDTO;
import de.uniks.stp24.model.BuildingAttributes;
import de.uniks.stp24.model.DistrictAttributes;
import de.uniks.stp24.model.SystemUpgrades;
import de.uniks.stp24.model.UpgradeStatus;
import de.uniks.stp24.service.VariablesTree;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.uniks.stp24.model.Ships.ShipType;

@Singleton
public class VariableDependencyService {
    @Inject
    public VariableService variableService;

    @Inject
    public VariableDependencyService() {

    }

     /*
    Logic for making attributes of upgrades dependent from Variables
     */

    public SystemUpgrades createVariableDependencyUpgrades() {
        UpgradeStatus unexplored = upgradeDependencyHandler("unexplored");
        UpgradeStatus explored = upgradeDependencyHandler("explored");
        UpgradeStatus upgraded = upgradeDependencyHandler("upgraded");
        UpgradeStatus developed = upgradeDependencyHandler("developed");
        UpgradeStatus colonized = upgradeDependencyHandler("colonized");
        return new SystemUpgrades(unexplored, explored, colonized, upgraded, developed);
    }

    private UpgradeStatus upgradeDependencyHandler(String key) {
        String next = null;
        double upgrade_time = 0;
        double pop_growth = 0;
        Map<String, Double> cost = new HashMap<>();
        Map<String, Double> upkeep = new HashMap<>();
        double capacity_multiplier = 0;

        if (variableService.systemsTree.getNode(key, "pop_growth") != null) {
            pop_growth = variableService.systemsTree.getNode(key, "pop_growth").getValue().finalValue();
        }

        next = switch (key) {
            case "unexplored" -> "explored";
            case "explored" -> "colonized";
            case "colonized" -> "upgraded";
            case "upgraded" -> "developed";
            default -> next;
        };

        if (variableService.systemsTree.getNode(key, "upgrade_time") != null) {
            upgrade_time = variableService.systemsTree.getNode(key, "upgrade_time").getValue().finalValue();
        }

        if (variableService.systemsTree.getNode(key, "cost") != null) {
            cost = createResourceMap(variableService.systemsTree.getNode(key, "cost").getChildren());
        }

        if (variableService.systemsTree.getNode(key, "upkeep") != null) {
            upkeep = createResourceMap(variableService.systemsTree.getNode(key, "upkeep").getChildren());
        }

        if (variableService.systemsTree.getNode(key, "capacity_multiplier") != null) {
            capacity_multiplier = variableService.systemsTree.getNode(key, "capacity_multiplier").getValue().finalValue();
        }

        return new UpgradeStatus(key, next, upgrade_time, pop_growth, cost, upkeep, capacity_multiplier);
    }

    /*
    Logic for making attributes of buildings (cost, upkeep, production) dependent from Variables
     */

    public ArrayList<BuildingAttributes> createVariableDependencyBuildings() {
        ArrayList<BuildingAttributes> buildingsAttributes = new ArrayList<>();
        for (VariablesTree.Node<ExplainedVariableDTO> buildingNode : variableService.buildingsTree.getRoot().getChildren()) {
            buildingsAttributes.add(buildingDependencyHandler(buildingNode));
        }
        return buildingsAttributes;
    }

    private BuildingAttributes buildingDependencyHandler(VariablesTree.Node<ExplainedVariableDTO> buildingNode) {
        String id = buildingNode.getKey();
        double build_time = variableService.buildingsTree.getNode(id, "build_time").getValue().finalValue();
        Map<String, Double> cost = createResourceMap(variableService.buildingsTree.getNode(id, "cost").getChildren());
        Map<String, Double> upkeep = createResourceMap(variableService.buildingsTree.getNode(id, "upkeep").getChildren());
        Map<String, Double> production = new HashMap<>();

        if (variableService.buildingsTree.getNode(id, "production") != null) {
            production = createResourceMap(variableService.buildingsTree.getNode(id, "production").getChildren());
        }
        return new BuildingAttributes(id, build_time, cost, upkeep, production);
    }


    /*
    Logic for making attributes of ships dependent from Variables
     */
    public ArrayList<ShipType> createVariableDependencyShipType(){
        ArrayList<ShipType> shipTypesAttributes = new ArrayList<>();
        for (VariablesTree.Node<ExplainedVariableDTO> shipTypeNode : variableService.shipTree.getRoot().getChildren()) {
            shipTypesAttributes.add(shipTypeDependencyHandler(shipTypeNode));
        }
        return shipTypesAttributes;
    }


    private ShipType shipTypeDependencyHandler(VariablesTree.Node<ExplainedVariableDTO> shipTypeNode) {
        String id = shipTypeNode.getKey();
        double build_time = variableService.shipTree.getNode(id, "build_time").getValue().finalValue();
        double health = variableService.shipTree.getNode(id, "health").getValue().finalValue();
        double speed = variableService.shipTree.getNode(id, "speed").getValue().finalValue();
        Map<String, Integer> attack = new HashMap<>();
        Map<String, Integer> defense = new HashMap<>();

        if(variableService.shipTree.getNode(id, "attack") != null) {
            for (VariablesTree.Node<ExplainedVariableDTO> attackNodes : variableService.shipTree.getNode(id, "attack").getChildren()) {
                attack.put(attackNodes.getKey(), (int) attackNodes.getValue().finalValue());
            }
        }

        for (VariablesTree.Node<ExplainedVariableDTO> defenseNodes : variableService.shipTree.getNode(id, "defense").getChildren()) {
            defense.put(defenseNodes.getKey(), (int) defenseNodes.getValue().finalValue());
        }
        Map<String, Double> cost = createResourceMap(variableService.shipTree.getNode(id, "cost").getChildren());
        Map<String, Double> upkeep = createResourceMap(variableService.shipTree.getNode(id, "upkeep").getChildren());
        return new ShipType(id, build_time, health, speed, attack, defense, cost, upkeep);
    }

    /*
    Logic for making attributes of districts (cost, upkeep, production) dependent from Variables
     */

    public ArrayList<DistrictAttributes> createVariableDependencyDistricts() {
        ArrayList<DistrictAttributes> districtsAttributes = new ArrayList<>();
        for (VariablesTree.Node<ExplainedVariableDTO> districtNode : variableService.districtsTree.getRoot().getChildren()) {
            districtsAttributes.add(districtsDependencyHandler(districtNode));
        }
        return districtsAttributes;
    }

    private DistrictAttributes districtsDependencyHandler(VariablesTree.Node<ExplainedVariableDTO> districtNode) {
        String id = districtNode.getKey();
        double build_time = variableService.districtsTree.getNode(id, "build_time").getValue().finalValue();

        Map<String, Double> chance = new HashMap<>();
        if(variableService.districtsTree.getNode(id, "chance") != null){
            chance = createResourceMap(variableService.districtsTree.getNode(id, "chance").getChildren());
        }

        Map<String, Double> cost = createResourceMap(variableService.districtsTree.getNode(id, "cost").getChildren());
        Map<String, Double> upkeep = createResourceMap(variableService.districtsTree.getNode(id, "upkeep").getChildren());
        Map<String, Double> production = createResourceMap(variableService.districtsTree.getNode(id, "production").getChildren());

        return new DistrictAttributes(id, build_time, chance, cost, upkeep, production);
    }

    private Map<String, Double> createResourceMap(List<VariablesTree.Node<ExplainedVariableDTO>> children) {
        Map<String, Double> result = new HashMap<>();
        for (VariablesTree.Node<ExplainedVariableDTO> node : children) {
            result.put(node.getKey(), node.getValue().finalValue());
        }
        return result;
    }
}

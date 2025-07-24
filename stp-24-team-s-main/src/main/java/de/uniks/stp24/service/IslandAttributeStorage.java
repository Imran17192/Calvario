package de.uniks.stp24.service;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.BuildingAttributes;
import de.uniks.stp24.model.DistrictAttributes;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.SystemUpgrades;
import de.uniks.stp24.service.game.VariableDependencyService;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.islandTranslation;

//Class can be used for getting Information of certain islands
@Singleton
public class IslandAttributeStorage {
    public EmpireDto empireDto;
    public SystemUpgrades systemUpgradeAttributes;
    public Island island;
    public ArrayList<BuildingAttributes> buildingsAttributes;
    public ArrayList<DistrictAttributes> districtAttributes;
    public final Map<Integer, String> upgradeEffects = new HashMap<>();

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;
    private int usedSlots;
    @Inject
    VariableDependencyService variableDependencyService;


    @Inject
    public IslandAttributeStorage() {

    }

    /*
    Methods for saving and getting certain inf. about selected island
     */

    public void setEmpireDto(EmpireDto empire) {
        empireDto = empire;
    }

    public void setSystemUpgradeAttributes() {
        this.systemUpgradeAttributes = variableDependencyService.createVariableDependencyUpgrades();

        String effectsColonized = "+" + this.systemUpgradeAttributes.colonized().pop_growth() * 100 + "% " + gameResourceBundle.getString("more.crewmates");
        String effectsUpgraded = "+" + this.systemUpgradeAttributes.upgraded().pop_growth() * 100 + "% " + gameResourceBundle.getString("more.crewmates");
        String effectsDeveloped = "+" + this.systemUpgradeAttributes.developed().pop_growth() * 100 + "% " + gameResourceBundle.getString("more.crewmates");

        String effectsColonizedCap;
        String effectsUpgradedCap;
        String effectsDevelopedCap;

        if(this.systemUpgradeAttributes.colonized().capacity_multiplier() != 1.0) {
            effectsColonizedCap = "+" + (this.systemUpgradeAttributes.colonized().capacity_multiplier() - 1) * 100 + "% " + gameResourceBundle.getString("more.capacity");
            effectsColonized = effectsColonized + "\n" + effectsColonizedCap;
        }
        if(this.systemUpgradeAttributes.upgraded().capacity_multiplier() != 1.0){
            effectsUpgradedCap = "+" + (this.systemUpgradeAttributes.upgraded().capacity_multiplier() - 1) * 100 + "% " + gameResourceBundle.getString("more.capacity");
            effectsUpgraded = effectsUpgraded + "\n" + effectsUpgradedCap;
        }
        if(this.systemUpgradeAttributes.developed().capacity_multiplier() != 1.0){
            effectsDevelopedCap = "+" + (this.systemUpgradeAttributes.developed().capacity_multiplier() - 1) * 100 + "% " + gameResourceBundle.getString("more.capacity");
            effectsDeveloped = effectsDeveloped + "\n" + effectsDevelopedCap;
        }

        upgradeEffects.put(1, null);
        upgradeEffects.put(2, effectsColonized);
        upgradeEffects.put(3, effectsUpgraded);
        upgradeEffects.put(4, effectsDeveloped);
    }

    public void setIsland(Island island) {
        this.island = island;
    }

    public Map<String, Double> getNeededResources(int key) {
        return switch (key) {
            case 2 -> systemUpgradeAttributes.colonized().cost();
            case 3 -> systemUpgradeAttributes.upgraded().cost();
            case 4 -> systemUpgradeAttributes.developed().cost();
            default -> null;
        };
    }

    public Map<String, Double> getUpkeep(int key) {
        return switch (key) {
            case 2 -> systemUpgradeAttributes.colonized().upkeep();
            case 3 -> systemUpgradeAttributes.upgraded().upkeep();
            case 4 -> systemUpgradeAttributes.developed().upkeep();
            default -> null;
        };
    }

    public Island getIsland() {
        return this.island;
    }

    public void setBuildingAttributes() {
        this.buildingsAttributes = variableDependencyService.createVariableDependencyBuildings();
    }

    public void setDistrictAttributes() {
        this.districtAttributes = variableDependencyService.createVariableDependencyDistricts();
    }

    public Map<String, Double> getBuildingsProduction(Island island) {
        Map<String, Double> buildingsProduction = new HashMap<>();
        for(String building: island.buildings()){
            int counter = 0;
            for(String tmp: island.buildings()){
                if(building.equals(tmp)){
                    counter++;
                }
            }
            for (BuildingAttributes preset : buildingsAttributes) {
                if(preset.id().equals(building)) {
                    for (Map.Entry<String, Double> entry : preset.production().entrySet()) {
                        buildingsProduction.merge(entry.getKey(), entry.getValue() * counter, Double::sum);
                    }
                }
            }
        }
        return buildingsProduction;
    }

    public Map<String, Double> getDistrictProduction(Island island) {
        Map<String, Double> sitesProduction = new HashMap<>();
        for(Map.Entry<String, Integer> entry : island.sites().entrySet()){
            for (DistrictAttributes preset : districtAttributes) {
                if(preset.id().equals(entry.getKey())) {
                    for (Map.Entry<String, Double> site : preset.production().entrySet()) {
                        sitesProduction.merge(site.getKey(), (site.getValue() * entry.getValue()), Double::sum);
                    }
                }
            }
        }
        return sitesProduction;
    }

    public Map<String, Double> getBuildingsConsumption(Island island) {
        Map<String, Double> buildingsConsumption = new HashMap<>();

        for(String building: island.buildings()){
            int counter = 0;
            for(String tmp: island.buildings()){
                if(building.equals(tmp)){
                    counter++;
                }
            }
            for (BuildingAttributes preset : buildingsAttributes) {
                if(preset.id().equals(building)) {
                    for (Map.Entry<String, Double> entry : preset.upkeep().entrySet()) {
                        buildingsConsumption.merge(entry.getKey(), entry.getValue() * counter, Double::sum);
                    }
                }
            }
        }
        return buildingsConsumption;
    }

    public Map<String, Double> getDistrictConsumption(Island island) {
        Map<String, Double> sitesConsumption = new HashMap<>();
        for(Map.Entry<String, Integer> entry : island.sites().entrySet()){
            for (DistrictAttributes preset : districtAttributes) {
                if(preset.id().equals(entry.getKey())) {
                    for (Map.Entry<String, Double> site : preset.upkeep().entrySet()) {
                        sitesConsumption.merge(site.getKey(), site.getValue() * entry.getValue(), Double::sum);
                    }
                }
            }
        }
        return sitesConsumption;
    }

    public Map<String, Double> mergeProduction(Island island){
        Map<String, Double> mergedMap = new HashMap<>(getBuildingsProduction(island));

        for (Map.Entry<String, Double> entry : getDistrictProduction(island).entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            if (mergedMap.containsKey(key)) {
                mergedMap.put(key, mergedMap.get(key) + value);
            } else {
                mergedMap.put(key, value);
            }
        }
        return mergedMap;
    }

//    public Map<String, Integer> mergeResourceMaps(ArrayList<Map<String, Integer>> maps) {
//        Map<String, Integer> mergedMap = new HashMap<>();
//
//        for (Map<String, Integer> map : maps) {
//            for (Map.Entry<String, Integer> entry : map.entrySet()) {
//                String resourceId = entry.getKey();
//                Integer value = entry.getValue();
//
//                mergedMap.put(resourceId, mergedMap.getOrDefault(resourceId, 0) + value);
//            }
//        }
//
//        return mergedMap;
//    }

    public Map<String, Double> mergeConsumption(Island island){
        Map<String, Double> mergedMap = new HashMap<>(getBuildingsConsumption(island));

        for (Map.Entry<String, Double> entry : getDistrictConsumption(island).entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            if (mergedMap.containsKey(key)) {
                mergedMap.put(key, mergedMap.get(key) + value);
            } else {
                mergedMap.put(key, value);
            }
        }
        return mergedMap;
    }

    public String getIslandTypeTranslated(){
        return gameResourceBundle.getString(islandTranslation.get(island.type().name()));
    }

    public String getUpgradeTranslation(int lvl){
        String upgradeLvl = String.valueOf(Upgrade.values()[lvl]);
        return gameResourceBundle.getString(Constants.upgradeTranslation.get(upgradeLvl));
    }

    public void setUsedSlots(int slots){
        this.usedSlots = slots;
    }

    public int getUsedSlots(){
        return usedSlots;
    }
}


package de.uniks.stp24.service.game;

import de.uniks.stp24.service.BasicService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InfrastructureService extends BasicService {

    private final Map<String, Integer> siteCapacities = new HashMap<>();
    private final Map<String, Integer> buildingCapacities = new HashMap<>();

    @Inject
    public InfrastructureService() {}

    public void resetMap() {
        this.siteCapacities.clear();
        this.buildingCapacities.clear();
    }

    // ---------- sites ---------- //

    public void putOrUpdateSiteInfo(String key, int value) {
        if (siteCapacities.containsKey(key)) {
            siteCapacities.compute(key,(k, v) -> Objects.nonNull(v) ? v + value : value );
        } else {
            siteCapacities.put(key,value);
        }
    }

    public int getSiteCapacities(String siteID) {
        return getCapacities(siteCapacities,siteID);
    }

    public int getTotalSiteCapacity() {
        return getTotalCapacity(siteCapacities);    }

    // ---------- buildings ---------- //

    public void putOrUpdateBuildingInfo(String key) {
        if (buildingCapacities.containsKey(key)) {
            buildingCapacities.compute(key,(k, v) -> Objects.nonNull(v) ? v + 1 : 1 );
        } else {
            buildingCapacities.put(key,1);
        }
    }

    public int getTotalBuildingCapacity() {
        return getTotalCapacity(buildingCapacities);    }


    private int getCapacities(Map<String, Integer> map, String id) {
        int capacity = 0 ;
        for (String key : map.keySet()) {
            capacity += key.equals(id) ? map.get(id) : 0 ;
        }
        return capacity;
    }

    public int getTotalCapacity(Map<String,Integer> map) {
        return map.values().stream().mapToInt(Integer::intValue).sum();
    }

}

package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.dto.BuildingDto;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.SystemsDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;

import static de.uniks.stp24.component.game.ResourceComponent.refactorNumber;

@Singleton
public class ResourcesService {
 	@Inject
    public GameSystemsApiService gameSystemsApiService;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    public EmpireService empireService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public Subscriber subscriber;

    @Named("gameResourceBundle")
    @Inject
    ResourceBundle gameResourceBundle;

    public final ArrayList<Runnable> runnables = new ArrayList<>();

    /**
     * storage for actual resources
     */
    private Map<String, Double> currentResources = new HashMap<>();

    @Inject
    public ResourcesService() {
    }

    public double getResourceCount(String resourceId) {
        if (currentResources.containsKey(resourceId))
            return currentResources.get(resourceId);
        return 0;
    }

    public Observable<SystemDto> destroyBuilding(String gameID, Island island, String buildingToDestroy) {
        ArrayList<String> buildings = island.buildings();

        // Look in list for building to delete
        Iterator<String> iterator = buildings.iterator();
        while (iterator.hasNext()) {
            String building = iterator.next();
            if (building.equals(buildingToDestroy)) {
                iterator.remove();
                break;
            }
        }
        Map<String, Integer> sitesValue = new HashMap<>();

        if (island.owner() != null){
            if (island.owner().equals(tokenStorage.getEmpireId())){
                return gameSystemsApiService.updateIsland(gameID, island.id(), new SystemsDto("",
                        sitesValue, buildings, null, island.owner()));
            }
        }

        return gameSystemsApiService.updateIsland(gameID, island.id(), new SystemsDto("",
                island.sites(), island.buildings(),island.upgrade(), island.owner()));
    }

    // Uses update island api-service to change the value of a system and delete a site
    public Observable<SystemDto> destroySite(String gameID, Island island, String siteToDestroy) {
        Map<String, Integer> sitesValue = new HashMap<>();
        sitesValue.put(siteToDestroy, -1);


        if (island.owner() != null){
            if (island.owner().equals(tokenStorage.getEmpireId())){
                return gameSystemsApiService.updateIsland(gameID, island.id(), new SystemsDto("",
                        sitesValue, island.buildings(), null, island.owner()));
            }
        }

        return gameSystemsApiService.updateIsland(gameID, island.id(), new SystemsDto("",
                island.sites(), island.buildings(),island.upgrade(), island.owner()));
    }

    public Observable<BuildingDto> getResourcesBuilding(String buildingType) {
        return gameSystemsApiService.getBuilding(buildingType);
    }

    // made a new method in order to prevent currentResources being overwritten in generateResourceList
    public void setCurrentResources(Map<String, Double> resourceMap) {
        currentResources = resourceMap;
    }

    /**
     * Updates the ObservableList which shows the count and change per season of a resource
     */
    public ObservableList<Resource> generateResourceList(Map<String, Double> resourceMap, ObservableList<Resource> oldResourceList, AggregateItemDto[] aggregateItems , boolean requireChangePerSeason) {
        int i = 0;
        ObservableList<Resource> resourceList = FXCollections.observableArrayList();
        if (Objects.nonNull(resourceMap)) {
            for (Map.Entry<String, Double> entry : resourceMap.entrySet()) {
                String resourceID = entry.getKey();
                double count = entry.getValue();
                double changePerSeason = 0;
                if (requireChangePerSeason && !oldResourceList.isEmpty() && oldResourceList.size() >= 2) {
                    changePerSeason = oldResourceList.get(i).changePerSeason();
                }
                if (Objects.nonNull(aggregateItems)) {
                    changePerSeason = aggregateItems[i].subtotal();
                }
                Resource resource = new Resource(resourceID, count, Math.round(changePerSeason * 1000.0) / 1000.0);
                resourceList.add(resource);
                i++;
            }
        }
        return resourceList;
    }

    public ObservableList<Resource> generateResourceListForDouble(Map<String, Double> resourceMap, ObservableList<Resource> oldResourceList, AggregateItemDto[] aggregateItems , boolean requireChangePerSeason) {
        int i = 0;
        ObservableList<Resource> resourceList = FXCollections.observableArrayList();
        if (Objects.nonNull(resourceMap)) {
            for (Map.Entry<String, Double> entry : resourceMap.entrySet()) {
                String resourceID = entry.getKey();
                double count = entry.getValue();
                double changePerSeason = 0;
                if (requireChangePerSeason && !oldResourceList.isEmpty() && oldResourceList.size() >= 2) {
                    changePerSeason = oldResourceList.get(i).changePerSeason();
                }
                if (Objects.nonNull(aggregateItems)) {
                    changePerSeason = aggregateItems[i].subtotal();
                }
                Resource resource = new Resource(resourceID, count, changePerSeason);
                resourceList.add(resource);
                i++;
            }
        }
        return resourceList;
    }

    public boolean hasEnoughResources(Map<String, Double> neededResources) {
        if (currentResources.isEmpty()) {
            this.subscriber.subscribe(empireService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                    result -> {
                        islandAttributes.setEmpireDto(result);
                        currentResources = result.resources();
                    },
                    error -> System.out.println("error in getEmpire in inGame"));
        }

        if (Objects.nonNull(neededResources)) {
            for (Map.Entry<String, Double> entry : neededResources.entrySet()) {
                String res = entry.getKey();
                double neededAmount = entry.getValue();
                double availableAmount = currentResources.get(res);
                if (availableAmount < neededAmount) {
                    return false;
                }
            }
        }

        return true;
    }

    public void setOnResourceUpdates(Runnable func) {
        // methods to run after resource updates
        runnables.add(func);
    }

    public Resource aggregateItemDtoToResource(AggregateItemDto aggregateItemDto) {
        String resourceID = aggregateItemDto.variable().replace("resources.", "").replace(".periodic", "");
        double resourceCount = getResourceCount(resourceID);
        return new Resource(resourceID, resourceCount, aggregateItemDto.subtotal());
    }

    public String formatNumber(double number) {
        return refactorNumber(number, gameResourceBundle);
    }
}

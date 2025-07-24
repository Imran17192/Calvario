package de.uniks.stp24.model;

import de.uniks.stp24.component.game.ContactDetailsComponent;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.ShortSystemDto;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.*;

public class Contact {
    String empireFlag;
    String empireName;
    public String empireID;
    String atIsland;
    String myOwnId;
    final List<String> empireIslandsIDs = new ArrayList<>();
    final List<String> discoveredIslands = new ArrayList<>();
    final List<ShortSystemDto> empireDtos = new ArrayList<>();
    final Map<String, ShortSystemDto> mapEmpireDtos = new HashMap<>();

    ContactDetailsComponent pane;

    public Contact(ReadEmpireDto dto) {
        this.empireID = dto._id();
        this.empireName = dto.name();
        this.empireFlag = "assets/flags/flag_" + dto.flag() + ".png";
    }

    public String getEmpireID() {
        return this.empireID;
    }

    public BooleanProperty atWarWith = new SimpleBooleanProperty(false);

    public void setEmpireID(String empireID) {
        this.empireID = empireID;
    }

    public String getEmpireName() {
        return this.empireName;
    }

    public String getEmpireFlag() {
        return this.empireFlag;
    }

    public void addIsland(String id) {
        this.atIsland = id;
        if (!discoveredIslands.contains(id)) discoveredIslands.add(id);
    }

    public List<String> getDiscoveredIslands() {
        return Collections.unmodifiableList(this.discoveredIslands);
    }


    public void setEmpireDtos(List<ShortSystemDto> shorts) {
//        System.out.println(shorts);
        shorts.forEach(
          dto -> {
              if (dto.owner().equals(this.empireID) && !empireIslandsIDs.contains(dto._id())) {
                  mapEmpireDtos.putIfAbsent(dto._id(), dto);
                  // are these necessary?
                  empireIslandsIDs.add(dto._id());
                  empireDtos.add(dto);
              }
          }
        );
        System.out.println("INFO ISLANDS DTOS = " + empireIslandsIDs.size());
    }

    public void checkIslands() {
        if (discoveredIslands.isEmpty()) {
            discoveredIslands.addAll(this.getDiscoveredIslands());
        } else {
            this.getDiscoveredIslands().forEach(id -> {
                if (!discoveredIslands.contains(id)) {
                    discoveredIslands.add(id);
                }
            });
        }
    }

    public Map<String, Integer> getDiscoveryStats() {
        int discoveredPopulation = 0;
        int discoveredSites = 0;
        int discoveredBuildings = 0;
        for (String id : discoveredIslands) {
            ShortSystemDto tmp = mapEmpireDtos.getOrDefault(id, null);
            if (tmp != null) {
                discoveredPopulation += tmp.population();
                discoveredSites += tmp.districts().values().stream().mapToInt(Integer::intValue).sum();
                discoveredBuildings += tmp.buildings().size();
            }
        }
        return Map.of("pop", discoveredPopulation,
          "sites", discoveredSites,
          "buildings", discoveredBuildings);
    }

    public Map<String, Integer> getStatsAtLocation() {
        ShortSystemDto tmp = mapEmpireDtos.getOrDefault(this.atIsland, null);
        return Map.of("pop", tmp.population(),
          "sites", tmp.districts().values().stream().mapToInt(Integer::intValue).sum(),
          "buildings", tmp.buildings().size());
    }

    public double getIntel() {
        if (empireDtos.isEmpty()) return 0;
        return 100.0 * discoveredIslands.size() / empireDtos.size();
    }

    public String getAtIsland() {
        return this.atIsland;
    }

    public void setPane(ContactDetailsComponent pane) {
        this.pane = pane;
    }

    public ContactDetailsComponent getPane() {
        return this.pane;
    }

    public void setMyOwnId(String gameOwner) {
        this.myOwnId = gameOwner;
    }

    public String getMyOwnId() {
        return this.myOwnId;
    }

    public void setStrength(double value) {
        this.pane.calculateStrength(value);
    }

    public boolean isAtWarWith() {
        return atWarWith.get();
    }

    public void setAtWarWith(boolean atWar) {
        this.atWarWith.set(atWar);
    }

    public BooleanProperty atWarWithProperty() {
        return atWarWith;
    }
}

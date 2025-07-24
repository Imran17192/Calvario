package de.uniks.stp24.dto;

import de.uniks.stp24.model.IslandType;

import java.util.ArrayList;
import java.util.Map;

public record CreateSystemsDto(
        String createdAt,
        String updatedAt,
        String _id,
        String game,
        IslandType type,
        String name,
        Map<String, Integer> districtSlots,
        Map<String, Integer> districts,
        int capacity,
        ArrayList<String> buildings,
        String upgrade,
        int population,
        Map<String, Integer> links,
        int x,
        int y,
        String owner
        ) {

}

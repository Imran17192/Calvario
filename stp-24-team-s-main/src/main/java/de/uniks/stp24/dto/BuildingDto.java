package de.uniks.stp24.dto;

import java.util.Map;

public record BuildingDto(
        String id,
        double build_time,
        Map<String, Double> cost,
        Map<String, Double> upkeep,
        Map<String, Double> production) {
}

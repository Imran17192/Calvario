package de.uniks.stp24.model;

import java.util.Map;

public record BuildingAttributes(
        String id,
        double build_time,
        Map<String, Double> cost,
        Map<String, Double> upkeep,
        Map<String, Double> production
) {
}

package de.uniks.stp24.model;

import java.util.Map;

public record DistrictAttributes(
        String id,
        double build_time,
        Map<String, Double> chance,
        Map<String, Double> cost,
        Map<String, Double> upkeep,
        Map<String, Double> production
) {
}

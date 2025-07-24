package de.uniks.stp24.model;

import java.util.Map;

public record UpgradeStatus(
        String id,
        String next,
        double upgrade_time,
        double pop_growth,
        Map<String, Double> cost,
        Map<String, Double> upkeep,
        double capacity_multiplier
) {
}

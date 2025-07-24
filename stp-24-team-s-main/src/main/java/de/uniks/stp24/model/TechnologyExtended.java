package de.uniks.stp24.model;

public record TechnologyExtended(
        String id,
        Effect[] effects,
        String[] tags,
        double cost,
        String[] requires,
        String[] precedes
) {
}

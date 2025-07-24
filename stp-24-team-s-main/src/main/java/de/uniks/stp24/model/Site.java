package de.uniks.stp24.model;

import java.util.Map;

public record Site(
    String siteID,
    Map<Resource, Integer> required,
    Map<Resource, Integer> production,
    Map<Resource, Integer> consumption,
    int cells,
    int maxCells
) {
}

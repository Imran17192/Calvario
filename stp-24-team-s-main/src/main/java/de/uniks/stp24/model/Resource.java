package de.uniks.stp24.model;


public record Resource(
    String resourceID,
    double count,
    double changePerSeason
) {
}

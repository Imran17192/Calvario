package de.uniks.stp24.dto;

public record AggregateResultDto(
        int total,
        AggregateItemDto[] items
) {
}

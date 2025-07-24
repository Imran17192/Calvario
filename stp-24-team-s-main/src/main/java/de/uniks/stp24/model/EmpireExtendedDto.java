package de.uniks.stp24.model;

import java.util.Map;

public record EmpireExtendedDto(
        String createdAt,
        String updatedAt,
        String _id,
        String game,
        String user,
        String name,
        String color,
        int flag,
        int portrait,
        String homeSystem,
        String description,
        Map<String, Integer> resources,
        String[] technologies,
        String[] traits
) {
}

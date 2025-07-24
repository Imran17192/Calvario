package de.uniks.stp24.dto;

import java.util.Map;

public record EmpireDto(
        String createdAt,
        String updatedAt,
        String _id,
        String game,
        String user,
        String name,
        String description,
        String color,
        int flag,
        int portrait,
        String homeSystem,
        String[] traits,
        Map<String, Double> resources,
        String[] technologies) {
}

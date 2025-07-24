package de.uniks.stp24.dto;

public record ReadEmpireDto(
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
        String homeSystem
) {
}

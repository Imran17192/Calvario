package de.uniks.stp24.dto;

public record WarDto(
     String createAt,
     String updateAt,
     String _id,
     String game,
     String attacker,
     String defender,
     String name
) {
}

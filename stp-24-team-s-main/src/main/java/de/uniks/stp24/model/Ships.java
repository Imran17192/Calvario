package de.uniks.stp24.model;

import java.util.Map;

public class Ships {

    public record Ship(
            String createdAt,
            String updatedAt,
            String _id,
            String game,
            String empire,
            String fleet,
            String type,
            int health,
            int experience,
            Map<String, Object> _public,
            Map<String, Object> _private
    ){}

    public record ReadShipDTO(
            String createdAt,
            String updatedAt,
            String _id,
            String game,
            String empire,
            String fleet,
            String type,
            int health,
            int experience,
            Map<String, Object> _public
    ) {}

    public record UpdateShipDTO(
            String fleet,
            Map<String, Object> _public,
            Map<String, Object> _private
    ) {}

    public record ShipType(
            String _id,
            double build_time,
            double health,
            double speed,
            Map<String, Integer> attack,
            Map<String, Integer> defense,
            Map<String, Double> cost,
            Map<String, Double> upkeep
    ){}

    public record BlueprintInFleetDto(
            String type,
            int count,
            Fleets.Fleet fleet
    ){}

    public static Ships.ReadShipDTO readShipDTOFromShip(Ship dto) {
        return new Ships.ReadShipDTO(dto.createdAt, dto.updatedAt, dto._id, dto.game, dto.empire, dto.fleet, dto.type, dto.health, dto.experience, dto._public);
    }
}

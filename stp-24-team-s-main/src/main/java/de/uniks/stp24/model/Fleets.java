package de.uniks.stp24.model;

import java.util.*;

public class Fleets {
    private static final Random random = new Random();

    public record Fleet(
          String createdAt,
          String updatedAt,
          String _id,
          String game,
          String empire,
          String name,
          String location,
          int ships,
          Map<String, Integer> size,
          Map<String, Object> _public,
          Map<String, Object> _private,
          EffectSource[] effects
    ) {
        public boolean equals(final Object o) {
            if (o instanceof Fleet fleet) return this._id.equals(fleet._id);
            return false;
        }
    }

    public record ReadFleetDTO(
            String createdAt,
            String updatedAt,
            String _id,
            String game,
            String empire,
            String name,
            String location,
            int ships,
            Map<String, Integer> size,
            Map<String, Object> _public
    ) {}

    public record UpdateFleetDTO(
            String name,
            Map<String, Integer> size,
            Map<String, Object> _public,
            Map<String, Object> _private,
            EffectSource[] effects
    ) {}

    public record CreateFleetDTO(
            String name,
            String location,
            Map<String, Integer> size,
            Map<String, Object> _public,
            Map<String, Object> _private,
            EffectSource[] effects
    ) {}

    public static Fleet fleetFromReadDTO(ReadFleetDTO dto) {
        return new Fleet(dto.createdAt, dto.updatedAt, dto._id, dto.game, dto.empire, dto.name, dto.location, dto.ships,
                dto.size, dto._public, null, null);
    }
}

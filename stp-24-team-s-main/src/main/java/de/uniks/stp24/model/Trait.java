package de.uniks.stp24.model;

import de.uniks.stp24.dto.EffectDto;

public record Trait(
        String id,
        EffectDto[] effects,
        int cost,
        String[] conflicts
) {
}

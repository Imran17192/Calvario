package de.uniks.stp24.model;

import java.util.List;

public record EffectSource(
        String id,
        List<Effect> effects
) {

}

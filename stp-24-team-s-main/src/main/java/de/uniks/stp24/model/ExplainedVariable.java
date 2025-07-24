package de.uniks.stp24.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExplainedVariable(
        String variable,
        double initial,
        List<EffectSource> sources,
        @JsonProperty("final")
        int finale
) {
}

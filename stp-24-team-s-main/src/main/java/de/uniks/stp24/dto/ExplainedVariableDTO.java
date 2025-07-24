package de.uniks.stp24.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.uniks.stp24.model.Sources;

import java.util.ArrayList;

public record ExplainedVariableDTO(
        String variable,
        double initial,
        ArrayList<Sources> sources,
        @JsonProperty("final")
        double finalValue
) {
}

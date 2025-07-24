package de.uniks.stp24.model;

import java.util.ArrayList;

public record Sources(
        String id,
        ArrayList<Effect> effects
) {
}

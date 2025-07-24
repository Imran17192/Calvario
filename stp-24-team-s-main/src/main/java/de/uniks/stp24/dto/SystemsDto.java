package de.uniks.stp24.dto;

import java.util.ArrayList;
import java.util.Map;

public record SystemsDto(
        String name,
        Map<String, Integer> districts,
        ArrayList<String> buildings,
        String upgrade,
        String owner

) {

}


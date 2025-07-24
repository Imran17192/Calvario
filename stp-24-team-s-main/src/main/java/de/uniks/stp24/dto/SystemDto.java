package de.uniks.stp24.dto;

import java.util.ArrayList;
import java.util.Map;

public record SystemDto(
  String createdAt,
  String updatedAt,
  String _id,
  String game,
  String type,
  String name,
  Map<String, Integer> districtSlots,
  Map<String, Integer> districts,
  int capacity,
  ArrayList<String> buildings,
  Upgrade upgrade,
  int population,
  Map<String,Integer> links,
  double x,
  double y,
  String owner,
  int health
) {
}

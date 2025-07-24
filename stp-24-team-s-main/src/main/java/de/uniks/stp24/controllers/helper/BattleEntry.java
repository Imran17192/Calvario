package de.uniks.stp24.controllers.helper;

import de.uniks.stp24.model.Ships.Ship;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BattleEntry {
    public enum BATTLE_TYPE {
        EMPIRES,
        WILD
    }

    private final BATTLE_TYPE battleType;

    private final String attacker, defender, islandID;
    private String winnerID, loserID;

    private final Map<String, Integer> shipsLostByAttacker = new HashMap<>();
    private final Map<String, Integer> shipsLostByDefender = new HashMap<>();

    public BattleEntry(String attacker, String defender, String islandID) {
        this.attacker = attacker;
        this.defender = defender;
        this.islandID = islandID;
        this.battleType = BATTLE_TYPE.EMPIRES;
    }

    public BattleEntry(String attacker, String islandID) {
        this.attacker = attacker;
        this.defender = null;
        this.islandID = islandID;
        this.battleType = BATTLE_TYPE.WILD;
    }

    public Map<String, Integer> getShipsLostByDefender() {
        return shipsLostByDefender;
    }

    public Map<String, Integer> getShipsLostByAttacker() {
        return shipsLostByAttacker;
    }

    public String getDefender() {
        return defender;
    }

    public String getAttacker() {
        return attacker;
    }

    public String getLocation() {
        return islandID;
    }

    public void setWinner(String winnerID) {
        this.winnerID = winnerID;
        this.loserID = attacker.equals(winnerID) ? defender : attacker;
    }

    public void addShipsLostByAttacker(String shipType) {
        if (!this.shipsLostByAttacker.containsKey(shipType)) this.shipsLostByAttacker.put(shipType, 0);
        this.shipsLostByAttacker.put(shipType, this.shipsLostByAttacker.get(shipType)+1);
    }

    public void addShipsLostByDefender(String shipType) {
        if (!this.shipsLostByDefender.containsKey(shipType)) this.shipsLostByDefender.put(shipType, 0);
        this.shipsLostByDefender.put(shipType, this.shipsLostByDefender.get(shipType)+1);
    }

    public BATTLE_TYPE getBattleType() {
        return battleType;
    }

    public boolean equals(String id1, String id2, String islandID, BATTLE_TYPE battleType) {
        return switch (battleType) {
            case EMPIRES -> this.islandID.equals(islandID) && (id1.equals(attacker) && id2.equals(defender) ||
                            id1.equals(defender) && id2.equals(attacker));
            case WILD -> this.attacker.equals(id1) && this.islandID.equals(islandID);
        };
    }

    public boolean equals(String id1, String id2) {
        return id1.equals(attacker) && id2.equals(defender) || id1.equals(defender) && id2.equals(attacker);
    }

    public BattleEntry addShip(Ship ship) {
        if (Objects.isNull(ship.empire())) {
            this.addShipsLostByDefender(ship.type());
            return this;
        }

        if (ship.empire().equals(this.attacker))
            this.addShipsLostByAttacker(ship.type());
        else this.addShipsLostByDefender(ship.type());

        return this;
    }

    public boolean containsEmpire(String islandID) {
        return islandID.equals(attacker) || islandID.equals(defender);
    }

    public String getWinner() {
        return winnerID;
    }

    public String getLoser() {
        return loserID;
    }
}

package pjvsemproj.dto;

import pjvsemproj.models.game.players.Player;

/**
 * Container representing a player.
 * <p>
 * This Data Transfer Object (DTO) isolates fundamental identity properties
 * and economic balance snapshots of a player for serialization or UI binding loops.
 */
public class PlayerDTO {
    public String name;
    public Integer balance;

    /**
     * Constructs a player data transfer container with explicit state specifications.
     *
     * @param name    The unique username tracking token assigned to this player participant profile.
     * @param balance The current total gold currency remaining inside this player's economic account balance.
     */
    public PlayerDTO(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    /**
     * Contextual hydration constructor that maps live domain player objects directly into static transfer representations.
     * <p>
     * Extracts username metadata strings and reads persistent account properties from the simulation engine layer.
     *
     * @param player The live active {@link Player} domain object instance to extract current state snapshots from.
     */
    public PlayerDTO(Player player) {
        this.name = player.getName();
        this.balance = player.getBalance();
    }
}
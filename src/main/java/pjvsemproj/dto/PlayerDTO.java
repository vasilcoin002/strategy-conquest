package pjvsemproj.dto;

import pjvsemproj.models.game.players.Player;

/**
 * Container representing a player.
 */
public class PlayerDTO {
    public final String name;
    public int balance;

    public PlayerDTO(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public PlayerDTO(Player player) {
        this.name = player.getName();
        this.balance = player.getBalance();
    }
}

package com.mrfoster.software_wolf;

public class Player {

    public enum Role {
        Villager, Werewolf
    }

    private Role role;
    private String name;
    private boolean dead;

    public Player(Role role, String name) {
        this.role = role;
        this.name = name;
        dead = false;
    }

    public Player(String name) {
        this.name = name;
        dead = false;
    }

    public Player() {
        dead = false;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}

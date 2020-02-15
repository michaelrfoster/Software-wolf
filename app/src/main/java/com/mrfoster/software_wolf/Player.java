package com.mrfoster.software_wolf;

public class Player {

    public enum Role {
        Villiager, Werewolf
    }

    private Role role;
    private String name;

    public Player(Role role, String name) {
        this.role = role;
        this.name = name;
    }

    public Player(String name) {
        this.name = name;
    }

    public Player() {
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
}

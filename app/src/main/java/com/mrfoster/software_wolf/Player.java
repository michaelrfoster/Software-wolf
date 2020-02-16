package com.mrfoster.software_wolf;

public class Player {

    public enum Role {
        Villiager, Werewolf
    }

    private Role role;
    private String name;
    private boolean ready;

    public Player(Role role, String name) {
        this.role = role;
        this.name = name;
        this.ready = false;
    }

    public Player(String name) {
        this.name = name;
        this.ready = false;
    }

    public Player() {
        this.ready = false;
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

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}

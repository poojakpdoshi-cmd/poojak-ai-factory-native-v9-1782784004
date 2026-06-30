package com.poojak.aifactory;

public class Agent {
    public String name;
    public String role;
    public String systemPrompt;
    public String provider;

    public Agent(String name, String role, String provider) {
        this.name = name;
        this.role = role;
        this.provider = provider;
        this.systemPrompt = "You are " + name + ". Role: " + role + ". Reply short, direct, useful. Made by Poojak Doshi.";
    }
}

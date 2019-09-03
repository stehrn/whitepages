package com.stehnik.whitepages.model;

public class Match {

    private final String name;
    private final String number;

    public Match(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}

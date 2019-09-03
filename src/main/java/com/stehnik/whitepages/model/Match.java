package com.stehnik.whitepages.model;

public class Match {

    private String name;
    private String number;

    // for json
    public Match() {
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

package com.stehnik.whitepages;

import io.vertx.core.Launcher;


public class Main {

    public static void main(String[] args) {
        Launcher.executeCommand("run", WhitepagesVerticle.class.getName());
    }
}

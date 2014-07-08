package de.futjikato.stroiz;

import de.futjikato.stroiz.ui.Starter;

public class ClientMain {
    public static void main(String[] args) {
        StroizLogger.init("client.log");

        Starter.doLaunch();
    }
}

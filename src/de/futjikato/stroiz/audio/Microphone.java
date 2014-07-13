package de.futjikato.stroiz.audio;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.network.UdpSender;

public class Microphone extends Thread{

    private UdpSender sender;

    public Microphone() {
    }

    public void setSender(UdpSender sender) {
        this.sender = sender;
    }

    public void run() {
        if(sender == null) {
            StroizLogger.getLogger().severe("Microphone started with no sender");
            return;
        }
    }
}

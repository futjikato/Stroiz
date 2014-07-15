package de.futjikato.stroiz.audio;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.client.RemoteClient;
import de.futjikato.stroiz.network.UdpSender;

import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class Microphone extends Thread{

    private static final int BUFF_SIZE = 128;

    private List<UdpSender> senders;

    private TargetDataLine dataLine;

    public Microphone(TargetDataLine targetLine) {
        this.dataLine = targetLine;
    }

    public void run() {

        dataLine.start();
        dataLine.flush();

        byte[] buff = new byte[BUFF_SIZE];
        while(!isInterrupted()) {
            dataLine.read(buff, 0, BUFF_SIZE);

            for(UdpSender sender : senders) {
                try {
                    sender.send(buff);
                } catch (IOException e) {
                    StroizLogger.getLogger().log(Level.SEVERE, "Error sending UDP packet.", e);
                }
            }
        }

        dataLine.stop();
        dataLine.flush();
    }

    public void addReceivers(List<RemoteClient> users) {
        for(RemoteClient client : users) {
            senders.add(client.createUdpSender());
        }
    }
}

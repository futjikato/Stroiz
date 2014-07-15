package de.futjikato.stroiz.network;

import de.futjikato.stroiz.StroizLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;

public class UdpReceiver {

    private DatagramSocket ds;

    byte[] buffer;

    public UdpReceiver(int port, int bufferSize) throws SocketException {
        this.buffer = new byte[bufferSize];
        this.ds = new DatagramSocket(port);
    }

    public byte[] receive() {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            ds.receive(packet);
        } catch (IOException e) {
            StroizLogger.getLogger().log(Level.SEVERE, "Error receiving UDP packet.", e);
            return new byte[0];
        }

        return packet.getData();
    }
}

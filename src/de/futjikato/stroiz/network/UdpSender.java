package de.futjikato.stroiz.network;

import de.futjikato.stroiz.StroizLogger;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;

public class UdpSender {

    private DatagramSocket ds;

    private InetAddress address;

    private int port;

    public UdpSender(String ip, int port) {
        this.port = port;

        try {
            this.address = InetAddress.getByAddress(ip.getBytes());
        } catch (UnknownHostException e) {
            StroizLogger.getLogger().log(Level.SEVERE, "Unable to create address from IP.", e);
        }

        try {
            ds = new DatagramSocket();
        } catch (SocketException e) {
            StroizLogger.getLogger().log(Level.SEVERE, "Unable to create datagram socket", e);
        }
    }

    public void send(byte[] buff) throws IOException {
        if(address == null || ds == null) {
            // error should be already logged above
            return;
        }

        DatagramPacket packet = new DatagramPacket(buff, buff.length, address, port);
        ds.send(packet);
    }
}

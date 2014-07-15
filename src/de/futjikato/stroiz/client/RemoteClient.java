package de.futjikato.stroiz.client;

import de.futjikato.stroiz.network.UdpSender;

public class RemoteClient {

    private String techName;

    private String ip;

    private int port;

    private String username;

    public RemoteClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.techName = String.format("Client %s", ip);
    }

    public String getListName() {
        if(username != null) {
            return username;
        }

        return techName;
    }

    public UdpSender createUdpSender() {
        return new UdpSender(ip, port);
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

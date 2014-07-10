package de.futjikato.stroiz.client;

/**
 * Created by moritz on 10.07.14.
 */
public class RemoteClient {

    private String listName;

    private String ip;

    public RemoteClient(String ip) {
        this.ip = ip;
        this.listName = String.format("Client %s", ip);
    }

    public String getListName() {
        return listName;
    }
}

package de.futjikato.stroiz.server;

import de.futjikato.stroiz.network.TcpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserManager {

    private List<TcpClient> clientList = new CopyOnWriteArrayList<TcpClient>();

    public void register(TcpClient client) {
        this.clientList.add(client);
    }

    public List<TcpClient> getAuthed() {
        List<TcpClient> list = new ArrayList<TcpClient>();
        for(TcpClient client : clientList) {
            if(client.isAuthenticated()) {
                list.add(client);
            }
        }

        return list;
    }

    public List<TcpClient> getPendingAuth() {
        List<TcpClient> list = new ArrayList<TcpClient>();
        for(TcpClient client : clientList) {
            if(!client.isAuthenticated()) {
                list.add(client);
            }
        }

        return list;
    }
}

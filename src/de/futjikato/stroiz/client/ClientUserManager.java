package de.futjikato.stroiz.client;

import de.futjikato.stroiz.task.tasks.UserManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientUserManager implements UserManager<RemoteClient> {

    protected CopyOnWriteArrayList<RemoteClient> remoteClients = new CopyOnWriteArrayList<RemoteClient>();

    @Override
    public List<RemoteClient> getUsers() {
        return remoteClients;
    }

    @Override
    public void register(RemoteClient client) {
        remoteClients.add(client);
    }
}

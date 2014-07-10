package de.futjikato.stroiz.server;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.network.TcpClient;
import de.futjikato.stroiz.task.tasks.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerUserManager implements UserManager<TcpClient> {

    private List<TcpClient> clientList = new CopyOnWriteArrayList<TcpClient>();

    public ServerUserManager() {
        ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        cleanupExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                cleanup();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public List<TcpClient> getUsers() {
        List<TcpClient> list = new ArrayList<TcpClient>();
        for(TcpClient client : clientList) {
            if(client.isAuthenticated()) {
                list.add(client);
            }
        }

        return list;
    }

    public void register(TcpClient client) {
        this.clientList.add(client);
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

    protected void cleanup() {
        for(TcpClient client : clientList) {
            if(!client.isAlive()) {
                clientList.remove(client);
                StroizLogger.getLogger().finer(String.format("Cleanup removed disconnected client %s", client.getUsername()));
            }
        }
    }
}

package de.futjikato.stroiz.server;

import de.futjikato.stroiz.network.TcpClient;
import de.futjikato.stroiz.network.TcpServer;
import de.futjikato.stroiz.task.tasks.ClientAuthTask;
import de.futjikato.stroiz.task.tasks.ListTask;
import de.futjikato.stroiz.task.PacketProcessor;

public class Server extends TcpServer {

    private UserManager usermanager;

    private ClientAuthTask authTask;

    private ListTask listTask;

    public void setUsermanager(UserManager usermanager) {
        this.usermanager = usermanager;
    }

    @Override
    protected void newClient(TcpClient client) {
        usermanager.register(client);
    }

    @Override
    protected void preListen() {
        // add tcp server packet processors
        authTask = new ClientAuthTask();
        listTask = new ListTask(usermanager);

        PacketProcessor.getInstance().addObserver(listTask);
        PacketProcessor.getInstance().addObserver(authTask);
    }

    @Override
    protected void preEnd() {
        // remove tcp server packet processors
        PacketProcessor.getInstance().deleteObserver(authTask);
        PacketProcessor.getInstance().deleteObserver(listTask);
    }
}

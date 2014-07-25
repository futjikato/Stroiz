package de.futjikato.stroiz.server;

import de.futjikato.stroiz.network.TcpClient;
import de.futjikato.stroiz.network.TcpServer;
import de.futjikato.stroiz.server.tasks.ServerUserList;
import de.futjikato.stroiz.task.PacketProcessor;

/**
 * Server implementation
 * Handles the new client by simply registering it with the UserManager.
 */
public class Server extends TcpServer {

    /**
     * Client listing task
     */
    private ServerUserList userList;

    /**
     * Handle a new client connection
     *
     * @param client
     */
    @Override
    protected void newClient(TcpClient client) {
        // todo may not needed ?
    }

    /**
     * Hook called before the server blocks to listen for new TCP connections.
     */
    @Override
    protected void preListen() {
        // add tcp server packet processors
        userList = new ServerUserList();

        PacketProcessor.getInstance().addObserver(userList);
    }

    /**
     * Hook called after the server stops listening and right before the thread ends.
     */
    @Override
    protected void preEnd() {
        // remove tcp server packet processors
        PacketProcessor.getInstance().deleteObserver(userList);
    }
}

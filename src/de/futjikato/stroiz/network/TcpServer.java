package de.futjikato.stroiz.network;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.server.tasks.ClientAuthTask;
import de.futjikato.stroiz.server.UserManager;
import de.futjikato.stroiz.server.tasks.ListTask;
import de.futjikato.stroiz.task.PacketProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public abstract class TcpServer extends Thread {

    protected ServerSocket serverSocket;



    public void listen(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        start();
    }

    public void run() {
        if(serverSocket == null || !serverSocket.isBound()) {
            StroizLogger.getLogger().severe("Socket unbound.");
            interrupt();
        }

        preListen();
        while(!isInterrupted()) {
            try {
                Socket clientSocket = serverSocket.accept();
                TcpClient client = new TcpClient(clientSocket);

                newClient(client);

                client.start();
            } catch (IOException e) {
                StroizLogger.getLogger().log(Level.SEVERE, "Server socket unable to accept clients.", e);
                e.printStackTrace();
            }
        }
        preEnd();
    }

    protected abstract void newClient(TcpClient client);

    protected abstract void preListen();

    protected abstract void preEnd();
}

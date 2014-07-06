package de.futjikato.stroiz.network;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.network.tasks.ClientAuthTask;
import de.futjikato.stroiz.task.PacketProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class TcpServer extends Thread {

    protected ServerSocket serverSocket;

    private List<TcpClient> clientList = new CopyOnWriteArrayList<TcpClient>();

    public void listen(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        start();
    }

    public void run() {
        if(serverSocket == null || !serverSocket.isBound()) {
            StroizLogger.getLogger().severe("Socket unbound.");
            interrupt();
        }

        // add tcp server packet processors
        ClientAuthTask authTask = new ClientAuthTask();
        PacketProcessor.getInstance().addObserver(authTask);

        while(!isInterrupted()) {
            try {
                Socket clientSocket = serverSocket.accept();
                TcpClient client = new TcpClient(clientSocket);

                clientList.add(client);
            } catch (IOException e) {
                StroizLogger.getLogger().log(Level.SEVERE, "Server socket unable to accept clients.", e);
                e.printStackTrace();
            }
        }

        // remove tcp server packet processors
        PacketProcessor.getInstance().deleteObserver(authTask);
    }
}

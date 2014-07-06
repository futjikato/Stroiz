package de.futjikato.stroiz.network;

import de.futjikato.stroiz.task.PacketProcessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Tcp Client
 *
 * A TCP client first try to read a packet.
 * If no message arrives after 100ms it checks if there are messages to write out.
 * A maximum of 5 messages are written before the client jumps back to read mode.
 * The client jumps in write mode immediately after a packet is read.
 */
public class TcpClient extends Thread {

    private static final int MAX_ONGOING_WRITE = 5;

    private static final int READ_TIMEOUT = 100;

    private DataOutputStream outputStream;

    private DataInputStream inputStream;

    private Queue<TcpPacket> writeQueue;

    private String username;

    private boolean authenticated;

    public TcpClient(Socket socket) throws IOException {
        socket.setSoTimeout(READ_TIMEOUT);

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());

        writeQueue = new ConcurrentLinkedDeque<TcpPacket>();
    }

    public void queueWrite(TcpPacket packet) {
        writeQueue.add(packet);
    }

    public void run() {
        while(!isInterrupted()) {
            // read
            try {
                TcpPacket packet = TcpPacket.read(inputStream);
                packet.setClient(this);
                PacketProcessor.getInstance().process(packet);
            } catch (IOException e) {
                System.err.println("TCP read error.");
                e.printStackTrace();
            }

            // write
            try {
                int i = 0;
                while (!writeQueue.isEmpty() && i++ < MAX_ONGOING_WRITE) {
                    TcpPacket packet = writeQueue.poll();
                    packet.writeTo(outputStream);
                }
            } catch (IOException e) {
                System.err.println("TCP write error.");
                e.printStackTrace();
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}

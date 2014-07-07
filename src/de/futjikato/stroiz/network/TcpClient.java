package de.futjikato.stroiz.network;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.task.PacketProcessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

/**
 * Tcp Client
 *
 * A TCP client first try to read a packet.
 * If no message arrives after 100ms it checks if there are messages to write out.
 * A maximum of 5 messages are written before the client jumps back to read mode.
 * The client jumps in write mode immediately after a packet is read.
 */
public class TcpClient extends Thread {

    /**
     * Maximum writes before the client tries to read a package.
     */
    private static final int MAX_ONGOING_WRITE = 5;

    /**
     * Time to wait for input before check if a message is queued up to write out.
     * Time in ms.
     */
    private static final int READ_TIMEOUT = 100;

    /**
     * Maximum package size in bytes
     */
    private static final int MAX_PACKET_SIZE = 2048;

    /**
     * Network address of the client.
     * The Stroiz client will only accept a UDP package from the same IP as
     * the TCP connection to the server was established.
     */
    private InetAddress address;

    /**
     * Output stream
     */
    private DataOutputStream outputStream;

    /**
     * Input stream
     */
    private DataInputStream inputStream;

    /**
     * Package queue.
     * This queue will be checked at least after the time specified by <code>READ_TIMEOUT</code>
     * passes without information from the client.
     */
    private Queue<TcpPacket> writeQueue;

    /**
     * Client username to propagate.
     * Only set after authentication.
     */
    private String username;

    /**
     * Flag if the clint was successfully authenticated.
     */
    private boolean authenticated;

    /**
     * Currently reading incomplete message.
     * The client will store bytes in this array until a complete message is received.
     */
    private byte[] incompletePacket = new byte[MAX_PACKET_SIZE];

    /**
     * Size of the new
     */
    private int incompletePacketByteSize;

    private int incompletePacketIndex;

    public TcpClient(Socket socket) throws IOException {
        socket.setSoTimeout(READ_TIMEOUT);

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        address = socket.getInetAddress();

        writeQueue = new ConcurrentLinkedDeque<TcpPacket>();
    }

    public void queueWrite(TcpPacket packet) {
        writeQueue.add(packet);
    }

    public void run() {
        while(!isInterrupted()) {
            // read
            try {
                byte[] packetBytes = readPacketComplete(inputStream);
                TcpPacket packet = new TcpPacket();
                packet.parse(packetBytes);
                packet.setClient(this);

                PacketProcessor.getInstance().process(packet);
            } catch(SocketTimeoutException e){
                // skip to write
            } catch (IOException e) {
                StroizLogger.getLogger().log(Level.SEVERE, "TCP socket error on read.", e);
                interrupt();
            }

            // write
            try {
                int i = 0;
                while (!writeQueue.isEmpty() && i++ < MAX_ONGOING_WRITE) {
                    TcpPacket packet = writeQueue.poll();
                    packet.writeTo(outputStream);
                }
            } catch (IOException e) {
                StroizLogger.getLogger().log(Level.SEVERE, "TCP socket error on write.", e);
                interrupt();
            }
        }
    }

    /**
     * Reading from teh client input stream until a complete package is read.
     *
     * @param inputStream Input stream
     * @return Packet bytes without size
     * @throws IOException
     */
    private byte[] readPacketComplete(DataInputStream inputStream) throws IOException {
        // check if this is the beginning of a new package
        if(incompletePacketByteSize == 0) {
            incompletePacketByteSize = inputStream.readInt();
            incompletePacketIndex = 0;
        }

        // read until max package size is reached or the packet is complete
        while(incompletePacketIndex < incompletePacketByteSize && incompletePacketIndex < 2048) {
            incompletePacket[incompletePacketIndex++] = inputStream.readByte();
        }

        // copy out the data from the max size buffer
        byte[] packetBuffer = new byte[incompletePacketIndex];
        System.arraycopy(incompletePacket, 0, packetBuffer, 0, incompletePacketIndex);

        // after the message is compete reset the byte size variable
        incompletePacketByteSize = 0;

        return packetBuffer;
    }

    /**
     * Returns the username of the client.
     * Notice that this can be null if the client is not yet authenticated.
     *
     * @return Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username.
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Check if the client has already authenticated.
     *
     * @return flag
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Set the authentication flag.
     *
     * @param authenticated flag
     */
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    /**
     * Returns the network address of the client.
     * A Stroiz client should only accept UDP packages from the same IP as the server
     * has used to authenticate with the server.
     *
     * @return Network address
     */
    public InetAddress getAddress() {
        return address;
    }
}

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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
     * UDP port the clients wants to receive all voice data on.
     */
    private int udpPort;

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
     * Byte size of the new packet to read.
     * If a new package can be read this is <code>0</code>
     */
    private int incompletePacketByteSize;
    /**
     * Index to write the next byte in the <code>incompletePacket</code>
     * Value between <code>0</code> and the current value of <code>incompletePacketByteSize</code>
     */
    private int incompletePacketIndex;

    /**
     * Every TCP socket accepted by the server will be wrapped in a TcpClient class.
     *
     * @param socket Client socket to read from and write to
     * @throws IOException
     */
    public TcpClient(Socket socket) throws IOException {
        socket.setSoTimeout(READ_TIMEOUT);

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        address = socket.getInetAddress();

        writeQueue = new ConcurrentLinkedDeque<TcpPacket>();
    }

    /**
     * Add a package to the write queue.
     * If the read timeout raises or a message is read a maximum number of packages is polled from this queue and
     * written into the client output stream.
     * The number of written packages is limited by <code>MAX_ONGOING_WRITE</code>
     *
     * @param packet package to write to client
     */
    public void queueWrite(TcpPacket packet) {
        writeQueue.add(packet);
    }

    /**
     * Client loop.
     * See class description for more details.
     */
    public void run() {
        while(!isInterrupted()) {
            // read
            try {
                byte[] packetBytes = readPacketComplete(inputStream);
                TcpPacket packet = new TcpPacket();
                packet.parse(packetBytes);
                packet.setClient(this);

                PacketProcessor.getInstance().process(packet);
            } catch (SocketTimeoutException e){
                // skip to write
            } catch (PacketException e) {
                StroizLogger.getLogger().log(Level.SEVERE, "Packet read error.", e);
                interrupt();
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
    private byte[] readPacketComplete(DataInputStream inputStream) throws IOException, PacketException {
        // check if this is the beginning of a new package
        if(incompletePacketByteSize == 0) {
            incompletePacketByteSize = inputStream.readInt();

            if(incompletePacketByteSize > MAX_PACKET_SIZE) {
                throw new PacketException("Incoming message too long.");
            }

            incompletePacketIndex = 0;
            incompletePacket = new byte[incompletePacketByteSize];
        }

        // read until max package size is reached or the packet is complete
        while(incompletePacketIndex < incompletePacketByteSize && incompletePacketIndex < 2048) {
            incompletePacket[incompletePacketIndex++] = inputStream.readByte();
        }


        // after the message is compete reset the byte size variable
        incompletePacketByteSize = 0;

        return incompletePacket;
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
     * Get the UDP port for voice transmission.
     *
     * @return UDP port
     */
    public int getUdpPort() {
        return udpPort;
    }

    /**
     * Set the UDP port for voice transmission
     *
     * @param udpPort UDP port
     */
    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
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

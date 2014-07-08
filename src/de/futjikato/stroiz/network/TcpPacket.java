package de.futjikato.stroiz.network;

import de.futjikato.stroiz.StroizLogger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Representation of a TCP packet.
 *
 * A packet consists of the following information:
 * - It's byte size
 * - Length of the action name byte representation
 * - Byte representation of action name
 * - Amount of parameter
 * - For each parameter : Length of parameter byte representation
 * - For each parameter : Parameter byte representation
 */
public class TcpPacket {

    /**
     * TCP client
     */
    private TcpClient client;

    /**
     * Action name
     * Must match to a known PacketHandler action
     */
    private String action;

    /**
     * Array of parameters
     */
    private String[] parameters;

    /**
     * Parses the raw message without the first 4 byte that were the message length
     *
     * @param bytes Message bytes
     * @throws IOException
     */
    public void parse(byte[] bytes) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        action = parseStr(buffer);

        int paramCount = buffer.getInt();
        parameters = new String[paramCount];
        for(int i = 0 ; i < paramCount ; i++) {
            parameters[i] = parseStr(buffer);
        }
    }

    /**
     * Write a packet to an outputStream.
     *
     * @param outputStream stream to write to
     * @throws IOException
     */
    public void writeTo(DataOutputStream outputStream) throws IOException {
        // write packet size
        outputStream.writeInt(calculateByteSize());

        // write action name
        writeStr(outputStream, action);
        outputStream.writeInt(parameters.length);

        // write each parameter
        for(String param : parameters) {
            writeStr(outputStream, param);
        }

        // flush output
        outputStream.flush();
    }

    /**
     * Parses a string out of the given ByteBuffer.
     * Reads first an integer with the byte length of the string and then the bytes itself.
     *
     * @param buffer ByteBuffer to read from
     * @return String representation read from ByteBuffer
     * @throws IOException
     */
    private String parseStr(ByteBuffer buffer) throws IOException {
        int strLength = buffer.getInt();
        byte[] strBytes = new byte[strLength];
        buffer.get(strBytes);
        return new String(strBytes);
    }

    /**
     * Write a string to the given OutputStream
     *
     * @param outputStream Output stream to write to
     * @param str String to write
     * @throws IOException
     */
    private void writeStr(DataOutputStream outputStream, String str) throws IOException {
        byte[] strBytes = str.getBytes();
        outputStream.writeInt(strBytes.length);
        outputStream.write(strBytes);
    }

    /**
     * Calculates the byte size of the packet.
     *
     * @return Byte size
     */
    private int calculateByteSize() {
        int size = 4;
        size += action.getBytes().length;

        size += 4;

        for(String param : parameters) {
            size += 4;
            size += param.getBytes().length;
        }

        return size;
    }

    public TcpClient getClient() {
        return client;
    }

    public void setClient(TcpClient client) {
        this.client = client;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    public String getParameter(int index) throws PacketException {
        if(parameters.length <= index) {
            throw new PacketException("No such parameter.");
        }
        return parameters[index];
    }

    /**
     * Send the packet to the bound TCP client.
     * If no client is bound a log message is generated.
     */
    public void send() {
        if(client == null) {
            StroizLogger.getLogger().severe("Unable to send unbound packet.");
            return;
        }

        client.queueWrite(this);
    }
}

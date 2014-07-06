package de.futjikato.stroiz.network;

import de.futjikato.stroiz.StroizLogger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.futjikato.stroiz.network
 */
public class TcpPacket {

    private TcpClient client;

    private String action;

    private String[] parameters;

    public static TcpPacket read(DataInputStream inputStream) throws IOException {
        TcpPacket packet = new TcpPacket();
        packet.parseFrom(inputStream);

        return packet;
    }

    private void parseFrom(DataInputStream inputStream) throws IOException {
        action = parseStr(inputStream);

        int paramCount = inputStream.readInt();
        parameters = new String[paramCount];
        for(int i = 0 ; i < paramCount ; i++) {
            parameters[0] = parseStr(inputStream);
        }
    }

    public void writeTo(DataOutputStream outputStream) throws IOException {
        writeStr(outputStream, action);
        outputStream.writeInt(parameters.length);

        for(String param : parameters) {
            writeStr(outputStream, param);
        }
    }

    private String parseStr(DataInputStream inputStream) throws IOException {
        int strLength = inputStream.readInt();
        byte[] strBytes = new byte[strLength];
        inputStream.read(strBytes);
        return new String(strBytes);
    }

    private void writeStr(DataOutputStream outputStream, String str) throws IOException {
        outputStream.writeInt(str.length());
        outputStream.write(str.getBytes());
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
        if(parameters.length >= index) {
            throw new PacketException("No such parameter.");
        }
        return parameters[index];
    }

    public void send() {
        if(client == null) {
            StroizLogger.getLogger().severe("Unable to send unbound packet.");
            return;
        }

        client.queueWrite(this);
    }
}

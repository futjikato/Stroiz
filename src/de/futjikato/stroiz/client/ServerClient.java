package de.futjikato.stroiz.client;

import de.futjikato.stroiz.network.TcpClient;
import de.futjikato.stroiz.network.TcpPacket;

import java.io.IOException;
import java.net.Socket;

public class ServerClient extends TcpClient {

    public ServerClient(String host, int port) throws IOException {
        super(new Socket(host, port));
    }

    public void queryAuth(String username, int udpReceivePort) {
        TcpPacket request = new TcpPacket();
        request.setClient(this);
        request.setAction("NET_CLIENT_AUTH_REQ");
        request.setParameters(new String[]{username, String.valueOf(udpReceivePort)});

        queueWrite(request);
    }

    public void queryMemberList() {
        TcpPacket request = new TcpPacket();
        request.setClient(this);
        request.setAction("SERVER_LIST_REQ");
        request.setParameters(new String[0]);

        queueWrite(request);
    }
}

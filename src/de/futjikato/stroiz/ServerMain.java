package de.futjikato.stroiz;

import de.futjikato.stroiz.network.TcpServer;

import java.io.IOException;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.futjikato.stroiz
 */
public class ServerMain {

    public static void main(String[] args) throws IOException {
        int port = Integer.valueOf(args[0]);

        TcpServer server = new TcpServer();
        server.listen(port);
    }

}

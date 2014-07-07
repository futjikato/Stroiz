package de.futjikato.stroiz;

import de.futjikato.stroiz.server.Server;
import de.futjikato.stroiz.server.UserManager;

import java.io.IOException;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.futjikato.stroiz
 */
public class ServerMain {

    public static void main(String[] args) throws IOException {
        // init logger
        StroizLogger.init("server");

        int port = Integer.valueOf(args[0]);

        UserManager userManager = new UserManager();

        Server server = new Server();
        server.setUsermanager(userManager);
        server.listen(port);
    }
}

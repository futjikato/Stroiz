package de.futjikato.stroiz;

import de.futjikato.stroiz.server.Server;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Server Main class
 */
public class ServerMain {

    /**
     * Main method
     * Startup TCP server
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // init logger
        StroizLogger.init("server.log");

        int port = Integer.valueOf(args[0]);

        Server server = new Server();
        server.listen(port);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                MemoryUsage usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
                System.out.println(usage);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }
}

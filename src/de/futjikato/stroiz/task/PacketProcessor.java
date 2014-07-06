package de.futjikato.stroiz.task;

import de.futjikato.stroiz.network.TcpPacket;

import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.futjikato.stroiz.task
 */
public final class PacketProcessor extends Observable {

    private static PacketProcessor instance;

    /**
     * Observable with executor service only works with single thread executor
     */
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static PacketProcessor getInstance() {
        if(instance == null) {
            instance = new PacketProcessor();
        }

        return instance;
    }

    public void process(final TcpPacket request) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                setChanged();
                notifyObservers(request);
            }
        });
    }
}

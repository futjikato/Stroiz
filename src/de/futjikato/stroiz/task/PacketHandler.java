package de.futjikato.stroiz.task;

import de.futjikato.stroiz.network.TcpPacket;

import java.util.Observable;
import java.util.Observer;

public abstract class PacketHandler<K extends PacketHandler> implements Observer {

    protected abstract PacketAction<K> getAction(String action);

    protected abstract K getFinalHandler();

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof TcpPacket) {
            TcpPacket packet = (TcpPacket) arg;
            try {
                PacketAction api = getAction(packet.getAction());
                api.process(this, packet);
            } catch (IllegalArgumentException e) {
                // api not found ... don´t care
            }
        }
    }

}

package de.futjikato.stroiz.task;

import de.futjikato.stroiz.network.TcpClient;
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
                // api not found ... don�t care
            }
        }
    }

    public TcpPacket createPacket(TcpClient client, String action, String[] params) {
        TcpPacket packet = new TcpPacket();
        packet.setAction(action);
        packet.setParameters(params);
        packet.setClient(client);

        return packet;
    }

    public TcpPacket createPacket(TcpClient client, String action, String message) {
        return createPacket(client, action, new String[]{message});
    }

    public TcpPacket createPacket(TcpClient client, String action) {
        return createPacket(client, action, new String[0]);
    }
}

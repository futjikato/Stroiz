package de.futjikato.stroiz.task;

import de.futjikato.stroiz.network.TcpPacket;

public interface PacketAction<K extends PacketHandler> {
    public void process(K packetHandler, TcpPacket packet);
}

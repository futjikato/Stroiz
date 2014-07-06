package de.futjikato.stroiz.network.tasks;

import de.futjikato.stroiz.network.PacketException;
import de.futjikato.stroiz.network.TcpPacket;
import de.futjikato.stroiz.task.PacketAction;
import de.futjikato.stroiz.task.PacketHandler;
import de.futjikato.stroiz.task.PacketProcessor;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.futjikato.stroiz.network.tasks
 */
public class ClientAuthTask extends PacketHandler<ClientAuthTask> {

    @Override
    protected PacketAction<ClientAuthTask> getAction(String action) {
        return ApiActions.valueOf(action);
    }

    @Override
    protected ClientAuthTask getFinalHandler() {
        return this;
    }

    public enum ApiActions implements PacketAction<ClientAuthTask> {
        NET_CLIENT_AUTH_RESP {
            @Override
            public void process(ClientAuthTask packetHandler, TcpPacket packet) {

            }
        },
        NET_CLIENT_AUTH_REQ {
            @Override
            public void process(ClientAuthTask packetHandler, TcpPacket request) {
                try {
                    String username = request.getParameter(0);
                } catch (PacketException e) {
                    TcpPacket response = new TcpPacket();
                    response.setAction("NET_CLIENT_AUTH_ERR");
                    response.setParameters(new String[] {
                        "Unable to fetch username"
                    });

                    request.getClient().queueWrite(response);
                }
            }
        },
        NET_CLIENT_AUTH_ERR {
            @Override
            public void process(ClientAuthTask packetHandler, TcpPacket packet) {

            }
        }
    }
}

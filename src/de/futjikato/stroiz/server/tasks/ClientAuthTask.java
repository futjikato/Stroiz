package de.futjikato.stroiz.server.tasks;

import de.futjikato.stroiz.network.PacketException;
import de.futjikato.stroiz.network.TcpClient;
import de.futjikato.stroiz.network.TcpPacket;
import de.futjikato.stroiz.task.PacketAction;
import de.futjikato.stroiz.task.PacketHandler;

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
        NET_CLIENT_AUTH_REQ {
            @Override
            public void process(ClientAuthTask packetHandler, TcpPacket request) {
                try {
                    String username = request.getParameter(0);
                    TcpClient client = request.getClient();

                    // todo make real auth with key exchange and stuff

                    client.setUsername(username);
                    client.setAuthenticated(true);

                    TcpPacket response = packetHandler.createPacket(request.getClient(), "NET_CLIENT_AUTH_OK", "Unable to fetch username");
                    response.send();
                } catch (PacketException e) {
                    TcpPacket response = packetHandler.createPacket(request.getClient(), "NET_CLIENT_AUTH_ERR", "Unable to fetch username");
                    response.send();
                }
            }
        },

        NET_CLIENT_AUTH_OK {
            @Override
            public void process(ClientAuthTask packetHandler, TcpPacket packet) {

            }
        },

        NET_CLIENT_AUTH_ERR {
            @Override
            public void process(ClientAuthTask packetHandler, TcpPacket packet) {

            }
        }
    }
}

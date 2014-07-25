package de.futjikato.stroiz.server.tasks;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.network.PacketException;
import de.futjikato.stroiz.network.TcpClient;
import de.futjikato.stroiz.network.TcpPacket;
import de.futjikato.stroiz.task.PacketAction;
import de.futjikato.stroiz.task.PacketHandler;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class ServerUserList extends PacketHandler<ServerUserList> {

    private List<TcpClient> clientList = new ArrayList<TcpClient>();

    public ServerUserList() {
        // todo start interval cleanup task
    }

    private enum Actions implements PacketAction<ServerUserList> {
        SERVER_USER_AUTHED {
            @Override
            public void process(ServerUserList packetHandler, TcpPacket request) {
                try {
                    String username = request.getParameter(0);
                    int udpReceivePort = Integer.valueOf(request.getParameter(1));

                    TcpClient client = request.getClient();

                    // todo make real auth with key exchange and stuff

                    client.setUsername(username);
                    client.setUdpPort(udpReceivePort);
                    client.setAuthenticated(true);

                    packetHandler.clientList.add(client);
                    packetHandler.distributeClient(client);

                    TcpPacket response = packetHandler.createPacket(request.getClient(), "CLIENT_USER_AUTH_OK", "Unable to fetch username");
                    StroizLogger.getLogger().info(String.format("New client authenticated with username: %s", username));
                    response.send();
                } catch (PacketException e) {
                    TcpPacket response = packetHandler.createPacket(request.getClient(), "CLIENT_USER_AUTH_ER", "Unable to fetch username");
                    StroizLogger.getLogger().log(Level.INFO, "Client failed authentication.", e);
                    response.send();
                }
            }
        },

        SERVER_USER_OUT {
            @Override
            public void process(ServerUserList packetHandler, TcpPacket packet) {
                throw new NotImplementedException();
            }
        },

        SERVER_USER_CHANGE {
            @Override
            public void process(ServerUserList packetHandler, TcpPacket packet) {
                throw new NotImplementedException();
            }
        },

        SERVER_USER_LIST {
            @Override
            public void process(ServerUserList packetHandler, TcpPacket packet) {
                TcpPacket response = packetHandler.createPacket(packet.getClient(), "CLIENT_USER_LIST");

                String[] parameter = new String[packetHandler.clientList.size() * 3];
                int index = 0;
                for(TcpClient client : packetHandler.clientList) {
                    parameter[index++] = client.getAddress().getHostAddress();
                    parameter[index++] = client.getUsername();
                    parameter[index++] = String.valueOf(client.getUdpPort());
                }

                response.setParameters(parameter);
                response.send();
            }
        }
    }

    private void distributeClient(TcpClient client) {
        TcpPacket packet = new TcpPacket();
        packet.setAction("CLIENT_USER_NEW");
        packet.setParameters(new String[]{
            client.getAddress().getHostAddress(),
            client.getUsername(),
            String.valueOf(client.getUdpPort())
        });

        for(TcpClient knownClient : clientList) {
            packet.setClient(knownClient);
            packet.send();
        }
    }

    @Override
    protected PacketAction<ServerUserList> getAction(String action) {
        return Actions.valueOf(action);
    }

    @Override
    protected ServerUserList getFinalHandler() {
        return this;
    }
}

package de.futjikato.stroiz.task.tasks;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.network.PacketException;
import de.futjikato.stroiz.network.TcpClient;
import de.futjikato.stroiz.network.TcpPacket;
import de.futjikato.stroiz.task.PacketAction;
import de.futjikato.stroiz.task.PacketHandler;
import de.futjikato.stroiz.ui.Invoker;
import de.futjikato.stroiz.ui.UiTask;

import java.util.logging.Level;

/**
 * Server and client implementation for authentication
 */
public class ClientAuthTask extends PacketHandler<ClientAuthTask> {

    /**
     * Get the action implementation
     *
     * @param action Action name
     * @return ApiActions
     */
    @Override
    protected PacketAction<ClientAuthTask> getAction(String action) {
        return ApiActions.valueOf(action);
    }

    /**
     * Return packet handler implementation
     *
     * @return Packet handler
     */
    @Override
    protected ClientAuthTask getFinalHandler() {
        return this;
    }

    /**
     * Api actions
     */
    public enum ApiActions implements PacketAction<ClientAuthTask> {
        /**
         * Server authentication request
         */
        NET_CLIENT_AUTH_REQ {
            @Override
            public void process(ClientAuthTask packetHandler, TcpPacket request) {
                try {
                    String username = request.getParameter(0);
                    int udpReceivePort = Integer.valueOf(request.getParameter(1));

                    TcpClient client = request.getClient();

                    // todo make real auth with key exchange and stuff

                    client.setUsername(username);
                    client.setUdpPort(udpReceivePort);
                    client.setAuthenticated(true);

                    TcpPacket response = packetHandler.createPacket(request.getClient(), "NET_CLIENT_AUTH_OK", "Unable to fetch username");
                    StroizLogger.getLogger().info(String.format("New client authenticated with username: %s", username));
                    response.send();
                } catch (PacketException e) {
                    TcpPacket response = packetHandler.createPacket(request.getClient(), "NET_CLIENT_AUTH_ERR", "Unable to fetch username");
                    StroizLogger.getLogger().log(Level.INFO, "Client failed authentication.", e);
                    response.send();
                }
            }
        },

        /**
         * Response the client received
         */
        NET_CLIENT_AUTH_OK {
            @Override
            public void process(ClientAuthTask packetHandler, final TcpPacket packet) {
                Invoker.getInstance().invoke(new UiTask() {
                    @Override
                    public void run() {
                        this.application.getController().authSucc(packet.getClient().getAddress().getHostName());
                    }
                });
            }
        },
        NET_CLIENT_AUTH_ERR {
            @Override
            public void process(ClientAuthTask packetHandler, TcpPacket packet) {
                Invoker.getInstance().invoke(new UiTask() {
                    @Override
                    public void run() {
                        this.application.getController().authError("Authentication failed");
                    }
                });
            }
        }
    }
}

package de.futjikato.stroiz.task.tasks;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.client.RemoteClient;
import de.futjikato.stroiz.network.TcpClient;
import de.futjikato.stroiz.network.TcpPacket;
import de.futjikato.stroiz.task.PacketAction;
import de.futjikato.stroiz.task.PacketHandler;
import de.futjikato.stroiz.ui.Invoker;
import de.futjikato.stroiz.ui.UiTask;

import java.util.List;

public class ListTask extends PacketHandler<ListTask> {

    private UserManager userManager;

    public ListTask(UserManager userManager) {
        this.userManager = userManager;
    }

    public enum ApiActions implements PacketAction<ListTask> {
        SERVER_LIST_REQ {
            @Override
            public void process(ListTask packetHandler, TcpPacket packet) {
                TcpPacket response = packetHandler.createPacket(packet.getClient(), "SERVER_LIST_RES");
                List<TcpClient> authedUsers = packetHandler.userManager.getUsers();

                String[] parameter = new String[authedUsers.size()];
                int index = 0;
                for(TcpClient client : authedUsers) {
                    parameter[index++] = client.getAddress().getHostAddress();
                }

                response.setParameters(parameter);
                response.send();

                StroizLogger.getLogger().info("Send member list");
            }
        },

        SERVER_LIST_RES {
            @Override
            public void process(ListTask packetHandler, final TcpPacket packet) {
                final String[] params = packet.getParameters();
                StroizLogger.getLogger().info("Received member list");
                for(String ip : params) {
                    RemoteClient client = new RemoteClient(ip);
                    packetHandler.userManager.register(client);
                    Invoker.getInstance().invoke(new UiTask() {
                        @Override
                        public void run() {
                            application.updateUsers();
                        }
                    });
                }
            }
        }
    }

    @Override
    protected PacketAction<ListTask> getAction(String action) {
        return ApiActions.valueOf(action);
    }

    @Override
    protected ListTask getFinalHandler() {
        return this;
    }
}

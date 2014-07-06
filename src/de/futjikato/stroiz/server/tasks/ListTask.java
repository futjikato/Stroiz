package de.futjikato.stroiz.server.tasks;

import de.futjikato.stroiz.network.TcpClient;
import de.futjikato.stroiz.network.TcpPacket;
import de.futjikato.stroiz.server.UserManager;
import de.futjikato.stroiz.task.PacketAction;
import de.futjikato.stroiz.task.PacketHandler;

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
                List<TcpClient> authedUsers = packetHandler.userManager.getAuthed();

                String[] parameter = new String[authedUsers.size()];
                int index = 0;
                for(TcpClient client : authedUsers) {
                    parameter[index++] = client.getAddress().getHostAddress();
                }

                response.setParameters(parameter);
                response.send();
            }
        }
    }

    @Override
    protected PacketAction<ListTask> getAction(String action) {
        return ApiActions.valueOf(action);
    }

    @Override
    protected ListTask getFinalHandler() {
        return null;
    }
}

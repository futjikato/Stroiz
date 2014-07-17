package de.futjikato.stroiz.client.tasks;

import de.futjikato.stroiz.network.TcpClient;
import de.futjikato.stroiz.network.TcpPacket;
import de.futjikato.stroiz.task.PacketAction;
import de.futjikato.stroiz.task.PacketHandler;
import de.futjikato.stroiz.ui.Invoker;
import de.futjikato.stroiz.ui.UiTask;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class ClientUserList extends PacketHandler<ClientUserList> {

    private List<TcpClient> clientList = new ArrayList<TcpClient>();

    private TcpClient self;

    public ClientUserList(TcpClient self) {
        this.self = self;
    }

    private enum Actions implements PacketAction<ClientUserList> {
        CLIENT_USER_AUTH_OK {
            @Override
            public void process(ClientUserList packetHandler, TcpPacket packet) {
                packetHandler.self.setAuthenticated(true);
                Invoker.getInstance().invoke(new UiTask() {
                    @Override
                    public void run() {
                        // todo send proper server message
                        application.getController().authSucc("ok");
                    }
                });
            }
        },
        CLIENT_USER_AUTH_ER {
            @Override
            public void process(ClientUserList packetHandler, TcpPacket packet) {
                packetHandler.self.setAuthenticated(false);
                Invoker.getInstance().invoke(new UiTask() {
                    @Override
                    public void run() {
                        // todo send proper server message
                        application.getController().authError("not ok");
                    }
                });
            }
        },
        CLIENT_USER_LIST {
            @Override
            public void process(ClientUserList packetHandler, TcpPacket packet) {
                // todo implement
            }
        },
        CLIENT_USER_NEW {
            @Override
            public void process(ClientUserList packetHandler, TcpPacket packet) {
                // todo implement
            }
        },
        CLIENT_USER_OUT {
            @Override
            public void process(ClientUserList packetHandler, TcpPacket packet) {
                throw new NotImplementedException();
            }
        }
    }

    @Override
    protected PacketAction<ClientUserList> getAction(String action) {
        return Actions.valueOf(action);
    }

    @Override
    protected ClientUserList getFinalHandler() {
        return this;
    }
}

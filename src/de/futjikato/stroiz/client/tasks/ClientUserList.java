package de.futjikato.stroiz.client.tasks;

import de.futjikato.stroiz.network.TcpClient;
import de.futjikato.stroiz.network.TcpPacket;
import de.futjikato.stroiz.network.UdpSender;
import de.futjikato.stroiz.task.PacketAction;
import de.futjikato.stroiz.task.PacketHandler;
import de.futjikato.stroiz.ui.Invoker;
import de.futjikato.stroiz.ui.UiTask;
import javafx.scene.control.TreeItem;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class ClientUserList extends PacketHandler<ClientUserList> {

    private List<UdpSender> clientList = new ArrayList<UdpSender>();

    private TcpClient self;

    public List<UdpSender> getUserList() {
        List<UdpSender> outSenders = new ArrayList<UdpSender>();
        for (UdpSender sender : clientList) {
            if(!sender.getUsername().equals(self.getUsername())) {
                outSenders.add(sender);
            }
        }

        return outSenders;
    }

    private enum Actions implements PacketAction<ClientUserList> {
        CLIENT_USER_AUTH_OK {
            @Override
            public void process(ClientUserList packetHandler, TcpPacket packet) {
                // set authenticated
                packetHandler.self.setAuthenticated(true);

                // request new client list
                packetHandler.clientList.clear();
                packetHandler.requestUserList();

                // invoke user interface
                Invoker.getInstance().invoke(new UiTask() {
                    @Override
                    public void run() {
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
            public void process(final ClientUserList packetHandler, TcpPacket packet) {
                String[] params = packet.getParameters();

                for (int i = 0 ; i < params.length ; ) {
                    // read params
                    String ip = params[i++];
                    String username = params[i++];
                    int udpPort = Integer.valueOf(params[i++]);

                    // build client
                    UdpSender client = new UdpSender(ip, udpPort);
                    client.setUsername(username);

                    // add to client list
                    packetHandler.clientList.add(client);

                    Invoker.getInstance().invoke(new UiTask() {
                        @Override
                        public void run() {
                            TreeItem<String> rootNode = application.getController().getMemberListRoot();
                            rootNode.getChildren().clear();

                            for(UdpSender c : packetHandler.clientList) {
                                rootNode.getChildren().addAll(new TreeItem<String>(c.getUsername()));
                            }
                        }
                    });
                }
            }
        },
        CLIENT_USER_NEW {
            @Override
            public void process(ClientUserList packetHandler, TcpPacket packet) {
                String[] params = packet.getParameters();

                // read params
                String ip = params[0];
                String username = params[1];
                int udpPort = Integer.valueOf(params[2]);

                // build client
                final UdpSender client = new UdpSender(ip, udpPort);
                client.setUsername(username);

                // add to client list
                packetHandler.clientList.add(client);

                Invoker.getInstance().invoke(new UiTask() {
                    @Override
                    public void run() {
                        TreeItem<String> rootNode = application.getController().getMemberListRoot();
                        rootNode.getChildren().addAll(new TreeItem<String>(client.getUsername()));
                    }
                });
            }
        },
        CLIENT_USER_OUT {
            @Override
            public void process(ClientUserList packetHandler, TcpPacket packet) {
                throw new NotImplementedException();
            }
        }
    }

    private void requestUserList() {
        TcpPacket packet = createPacket(self, "SERVER_USER_LIST");
        packet.send();
    }

    @Override
    protected PacketAction<ClientUserList> getAction(String action) {
        return Actions.valueOf(action);
    }

    @Override
    protected ClientUserList getFinalHandler() {
        return this;
    }

    public TcpClient getSelf() {
        return self;
    }

    public void setSelf(TcpClient self) {
        this.self = self;
    }
}

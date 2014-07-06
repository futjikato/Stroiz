package de.futjikato.stroiz.ui.elements;

import de.futjikato.stroiz.network.TcpPacket;
import de.futjikato.stroiz.task.PacketAction;
import de.futjikato.stroiz.task.PacketHandler;
import de.futjikato.stroiz.task.PacketProcessor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.futjikato.stroiz.ui.elements
 */
public class MemberList extends PacketHandler<MemberList> {

    private List<Member> list = new CopyOnWriteArrayList<Member>();

    @Override
    protected PacketAction<MemberList> getAction(String action) {
        return ApiActions.valueOf(action);
    }

    @Override
    protected MemberList getFinalHandler() {
        return this;
    }

    public enum ApiActions implements PacketAction<MemberList> {
        NEW_MEMBER {
            @Override
            public void process(MemberList packetHandler, TcpPacket packet) {
                MemberList memberList = (MemberList) packetHandler;

                String[] params = packet.getParameters();
                String ip = params[0];
                String name = params[1];

                Member newMember = new Member(ip, name);
                memberList.list.add(newMember);
            }
        },
        REM_MEMBER {
            @Override
            public void process(MemberList packetHandler, TcpPacket packet) {
                MemberList memberList = (MemberList) packetHandler;
            }
        },
        CNG_MEMBER {
            @Override
            public void process(MemberList packetHandler, TcpPacket packet) {

            }
        };
    }

    public MemberList() {
        PacketProcessor.getInstance().addObserver(this);
    }


}

package de.futjikato.stroiz.ui.elements;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.futjikato.stroiz.ui.elements
 */
public class Member {

    private String ip;

    private String name;

    public Member(String ip, String name) {
        this.ip = ip;
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

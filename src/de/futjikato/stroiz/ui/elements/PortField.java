package de.futjikato.stroiz.ui.elements;

import de.futjikato.stroiz.StroizLogger;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.logging.Level;

public class PortField extends ValidationField {

    public static final int TYPE_TCP = 1;
    public static final int TYPE_UDP = 2;

    public static final int TYPE_SERVER = 1;
    public static final int TYPE_CLIENT = 2;

    public int networkInterfaceType;

    public int socketType = TYPE_CLIENT;

    public int getNetworkInterfaceType() {
        return networkInterfaceType;
    }

    public void setNetworkInterfaceType(int networkInterfaceType) {
        this.networkInterfaceType = networkInterfaceType;
    }

    public int getSocketType() {
        return socketType;
    }

    public void setSocketType(int socketType) {
        this.socketType = socketType;
    }

    @Override
    public boolean validate(String value) {
        int port;
        try {
            port = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return false;
        }

        // no availability check for client ports
        if(socketType == TYPE_CLIENT) {
            return true;
        }

        // server port availability check
        switch(networkInterfaceType) {
            case TYPE_TCP:
                return checkTcp(port);

            case TYPE_UDP:
                return checkUdp(port);

            default:
                return checkTcp(port) && checkUdp(port);
        }
    }

    private boolean checkTcp(int port) {
        try {
            ServerSocket ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            try {
                ss.close();
            } catch (IOException e) {
                // its ok
            }
            return true;
        } catch (IOException e) {
            StroizLogger.getLogger().log(Level.INFO, "Port validation failed.", e);
            return false;
        } catch (IllegalArgumentException e) {
            StroizLogger.getLogger().log(Level.INFO, "Port validation failed.", e);
            return false;
        }
    }

    private boolean checkUdp(int port) {
        try {
            DatagramSocket ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            ds.close();
            return true;
        } catch (SocketException e) {
            StroizLogger.getLogger().log(Level.INFO, "Port validation failed.", e);
            return false;
        } catch (IllegalArgumentException e) {
            StroizLogger.getLogger().log(Level.INFO, "Port validation failed.", e);
            return false;
        }
    }

    public int getInt() throws ValidationException {
        if (!validate()) {
            throw new ValidationException("Validation failed.");
        }

        return Integer.valueOf(getText());
    }
}

package cz.eclub.xtherm.xtherm.bus.events;

/**
 * Created by vesely on 9/21/16.
 */
public class ConnectionEvent {
    private boolean connected;

    public ConnectionEvent(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }
}

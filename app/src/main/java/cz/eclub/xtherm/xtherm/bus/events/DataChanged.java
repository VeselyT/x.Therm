package cz.eclub.xtherm.xtherm.bus.events;

import cz.eclub.xtherm.xtherm.ui.XTHRMData;

/**
 * Created by vesely on 9/19/16.
 */
public class DataChanged {

    private XTHRMData data;

    public DataChanged(XTHRMData data) {
        this.data = data;
    }

    public XTHRMData getData() {
        return data;
    }
}

package cz.eclub.xtherm.xtherm.ui;

/**
 * Created by vesely on 9/17/16.
 */
public class XTHRMData {
    private Float humidity;
    private Float temperature;

    public XTHRMData() {
        this(Float.NaN, Float.NaN);
    }

    public XTHRMData(Float temperature) {
        this(temperature, null);
    }

    public XTHRMData(Float temperature, Float humidity) {
        this.humidity = humidity;
        this.temperature = temperature;
    }

    public boolean hasHumidity() {
        return humidity != null;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getTemperature() {
        return temperature;
    }
}

package cz.eclub.xtherm.xtherm;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cz.eclub.xtherm.xtherm.bus.OttoBus;
import cz.eclub.xtherm.xtherm.bus.events.ConnectionEvent;
import cz.eclub.xtherm.xtherm.bus.events.DataChanged;
import cz.eclub.xtherm.xtherm.bus.events.UpdateStarted;
import cz.eclub.xtherm.xtherm.preferences.Prefs_;
import cz.eclub.xtherm.xtherm.ui.XTHRMData;

/**
 * Created by Tom on 05.05.2016.
 */
@EBean
public class NfcCardReader implements NfcAdapter.ReaderCallback {
    private Context context;
    private NfcV nfcV;
    private ScheduledExecutorService scheduledExecutorService;
    private Thread t;
    private final Object lock = new Object();
    private ScheduledFuture<?> future;

    @Bean
    OttoBus bus;

    @Pref
    Prefs_ preferences;

    public NfcCardReader(Context context) {
        this.context = context;
    }

    public void init() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendEvent(new UpdateStarted());
                    Log.d("X.THERM", "READING");

                    byte[] a = {0x02, 0x23, 0x00, 0x02}; //sys info
                    synchronized (lock) {
                        processResult(nfcV.transceive(a));
                    }
                } catch (IOException e) {
                    sendEvent(new ConnectionEvent(false));
                    if (future != null) {
                        future.cancel(true);
                        future = null;
                    }
                    Log.e("X.THERM", e.getMessage());
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
    }

    @AfterInject
    void initBus() {
        bus.register(context);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.d("X.THERM", "discovered");
        sendEvent(new ConnectionEvent(true));
        synchronized (lock) {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
            nfcV = NfcV.get(tag);
            for (int i = 0; i < 5; i++) {
                try {
                    nfcV.connect();
                    future = scheduledExecutorService.scheduleAtFixedRate(t, 0, preferences.interval().get(), TimeUnit.MILLISECONDS);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @UiThread
    protected void sendEvent(Object data) {
        bus.post(data);
    }


    private void processResult(byte[] result) {
        Log.d("Aaa", ""+ result[0]+ " "+ result[1]+ " "+ result[2]+ " "+ result[3]+ " "+ result[4]);
        int h = byteToInt(result[6], result[5]);
        int t = byteToInt(result[8], result[7]);
        float humidity = (float) (((125 * h) / 65536.0) - 6);
        float temperature = (float) (((175.72 * t) / 65536) - 46.85);
        byte b = (byte) 0xFF;
        String pages = ByteArrayToHexString(result);
        Log.d("X.THERM", pages);
        pages = pages.substring(2, 10) + "\n" + pages.substring(10, 18) + "\n" + pages.substring(18, 26);
        Log.d("X.THERM", pages);
        if (humidity >= 0 && humidity <= 100) {
            if (temperature >= -30 && temperature <= 80) {
                XTHRMData xthrmData = new XTHRMData(temperature, (result[6]==b && result[5]==b)? null : humidity);
                Log.d("aaa", h + " "+0xffff + " " + humidity + "  " + temperature);
                sendEvent(new DataChanged(xthrmData));
            }
        }
    }


    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int byteToInt(byte first, byte second) {
        return (((int)second & 0xFF) << (Byte.SIZE)) | ((int)first & 0xFF);
    }

    public void stop() {
        synchronized (lock) {
            if (future != null) {
                future.cancel(true);
            }
            scheduledExecutorService.shutdown();
        }
    }
}

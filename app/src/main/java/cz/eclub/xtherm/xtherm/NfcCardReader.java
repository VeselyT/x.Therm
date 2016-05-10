package cz.eclub.xtherm.xtherm;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tom on 05.05.2016.
 */
public class NfcCardReader implements NfcAdapter.ReaderCallback {
    private MainActivity mainActivity;
    private byte old = -1;
    private NfcV nfcV;
    private ScheduledExecutorService scheduledExecutorService;
    private Thread t;
    private final Object lock = new Object();
    private ScheduledFuture<?> future;

    public NfcCardReader(MainActivity mainActivity){
        this.mainActivity=mainActivity;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("X.THERM","READING");
                    byte[] a = {0x02,0x23,0x00,0x02}; //sys info
                    synchronized (lock) {
                        processResult(nfcV.transceive(a));
                    }
                } catch (IOException e) {
                    Log.e("X.THERM",e.getMessage());
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.d("X.THERM","discovered");
        synchronized (lock) {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
            nfcV = NfcV.get(tag);
            try {
                nfcV.connect();
                future = scheduledExecutorService.scheduleAtFixedRate(t, 0, 750, TimeUnit.MILLISECONDS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processResult(byte[] result){
        int h = byteToInt(result[6],result[5]);
        int t = byteToInt(result[8],result[7]);

        double humidity = ((125*h)/65536)-6;
        double temperature = ((175.72*t)/65536)-46.85;

        String pages = ByteArrayToHexString(result);
        Log.d("X.THERM",pages);

        pages = pages.substring(2,10)+"\n"+pages.substring(10,18)+"\n"+pages.substring(18,26);
        Log.d("X.THERM",pages);

        if(humidity>=0 && humidity<=100){
            if(temperature>=-30 && temperature<=80){
                mainActivity.updateUI(humidity,temperature,pages);
            }
        }
    }


    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int byteToInt(byte first, byte second) {
        int value = (second & 0xFF) << (Byte.SIZE * 1);
        value |= (first & 0xFF);
        return value;
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

package cz.eclub.xtherm.xtherm;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tom on 05.05.2016.
 */
public class NfcCardReader implements NfcAdapter.ReaderCallback {
    private MainActivity mainActivity;


    public NfcCardReader(MainActivity mainActivity){
        this.mainActivity=mainActivity;
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.d("SHIT","read "+ Arrays.toString(tag.getTechList()));


        final NfcV nfcV = NfcV.get(tag);
        //byte[] a = {0x02,0x2B}; //sys info
        byte[] a = {0x02,0x23,0x00,0x01}; //sys info

        try {
            nfcV.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] a = {0x02,0x23,0x00,0x01}; //sys info
                    Log.d("SHIT","Reading");
                    byte[] b = new byte[0];
                    b = nfcV.transceive(a);
                    processResult(b);

                } catch (IOException e) {
                    Log.e("SHIT",e.getMessage());
                    scheduledExecutorService.shutdown();
                }
            }
        };
        Thread t = new Thread(r);
        t.setPriority(Thread.MAX_PRIORITY);

        scheduledExecutorService.scheduleAtFixedRate(t,0,750, TimeUnit.MILLISECONDS);

    }

    private void processResult(byte[] result){
        int h = byteToInt(result[6],result[5]);
        int t = byteToInt(result[8],result[7]);

        double humidity = ((125*h)/65536)-6;
        double temperature = ((175.72*t)/65536)-46.85;

        Log.d("SHIT",ByteArrayToHexString(result));
        Log.d("SHIT","H: "+humidity+" T: "+temperature);

        mainActivity.updateUI(humidity,temperature);

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
}

package cz.eclub.xtherm.xtherm;

import android.animation.ObjectAnimator;
import android.media.AudioManager;
import android.media.Image;
import android.media.ToneGenerator;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_V;


    TextView humidityText;
    TextView temperatureText;
    ProgressBar humidityProgress;
    ProgressBar temperatureProgress;
    ImageView karaImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        humidityText=((TextView)findViewById(R.id.textView2));
        temperatureText=((TextView)findViewById(R.id.textView3));



    }


    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        NfcCardReader nfcCardReader = new NfcCardReader(this);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableReaderMode(this,nfcCardReader,READER_FLAGS,null);

    }

    public void updateUI(final double humidity, final double temperature){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            humidityText.setText(String.format("%.0f",humidity)+"%");
            temperatureText.setText(String.format("%.1f",temperature)+"°C");
        }
        });
    }
}

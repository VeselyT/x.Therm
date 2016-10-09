package cz.eclub.xtherm.xtherm;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import cz.eclub.xtherm.xtherm.preferences.SettingsActivity_;
import cz.eclub.xtherm.xtherm.ui.MainFragment_;
import cz.eclub.xtherm.xtherm.utils.NFCDisabledException;
import cz.eclub.xtherm.xtherm.utils.NFCNotSupportedException;
import cz.eclub.xtherm.xtherm.utils.NFCUtils;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main_menu)
public class MainActivity extends AppCompatActivity {

    private static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_V;

    @ViewById
    protected Toolbar toolbar;

    @Bean
    protected NfcCardReader nfcCardReader;

    private NfcAdapter nfcAdapter;

    @AfterViews
    protected void setActionBar() {
        setSupportActionBar(toolbar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.container, new MainFragment_.FragmentBuilder_().build()).commit();
    }

    public void initNFC() {
        if (nfcCardReader != null) {
            nfcCardReader.init();
        }
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        try {
            NFCUtils.checkNFCExists(nfcAdapter, this);
            NFCUtils.checkNFCEnabled(nfcAdapter, this);
            nfcAdapter.enableReaderMode(this, nfcCardReader, READER_FLAGS, null);
        } catch (NFCDisabledException | NFCNotSupportedException e) {
            // nothing to do - exceptions are handled internally by NFCUtils
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initNFC();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(this);
        }
        if (nfcCardReader != null) {
            nfcCardReader.stop();
        }
        nfcAdapter = null;
    }

    @OptionsItem(R.id.menu_button_preferences)
    public void onPreferencesMenuClicked() {
        startActivity(new Intent(this, SettingsActivity_.class));
    }
}

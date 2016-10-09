package cz.eclub.xtherm.xtherm.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import cz.eclub.xtherm.xtherm.R;

/**
 * Created by vesely on 9/15/16.
 */
public class NFCUtils {
    public static void checkNFCEnabled(NfcAdapter nfcAdapter, final Activity activity) throws NFCDisabledException {
        if (!nfcAdapter.isEnabled()) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
            alertbox.setTitle(activity.getString(R.string.nfc_dialog));
            alertbox.setMessage(activity.getString(R.string.msg_nfcon));
            alertbox.setPositiveButton(activity.getString(R.string.settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        activity.startActivity(intent);
                    }
                }
            });
            alertbox.setCancelable(false);
            alertbox.setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.finishAffinity();
                }
            });
            alertbox.show();
            throw new NFCDisabledException();
        }
    }

    public static void checkNFCExists(NfcAdapter nfcAdapter, final Activity activity) throws NFCNotSupportedException {
        if (nfcAdapter == null) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
            alertbox.setTitle(activity.getString(R.string.nfc_dialog_exists));
            alertbox.setMessage(activity.getString(R.string.msg_nfcon_exists));
            alertbox.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.finishAffinity();
                }
            });
            alertbox.setCancelable(false);
            alertbox.show();
            throw new NFCNotSupportedException();
        }
    }
}

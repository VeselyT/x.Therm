package cz.eclub.xtherm.xtherm.preferences;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by vesely on 9/15/16.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface Prefs {
    @DefaultInt(3500)
    int interval();
}

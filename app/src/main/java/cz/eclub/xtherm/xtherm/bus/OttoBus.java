package cz.eclub.xtherm.xtherm.bus;

import android.util.Log;

import com.squareup.otto.Bus;

import org.androidannotations.annotations.EBean;

/**
 * Created by vesely on 9/19/16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class OttoBus extends Bus {
    public OttoBus() {
    }
}
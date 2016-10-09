package cz.eclub.xtherm.xtherm.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

import cz.eclub.xtherm.xtherm.R;
import cz.eclub.xtherm.xtherm.bus.OttoBus;
import cz.eclub.xtherm.xtherm.bus.events.TriggerUpdate;
import cz.eclub.xtherm.xtherm.ui.utils.BindableLinearLayout;

/**
 * Created by vesely on 9/17/16.
 */
@EViewGroup(R.layout.humidity_view)
public class HumidityView extends BindableLinearLayout<XTHRMData> {

    @ViewById
    protected TextView humidity;

    @Bean
    protected OttoBus bus;

    private XTHRMData data;

    public HumidityView(Context context) {
        super(context);
    }

    public HumidityView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @AfterInject
    protected void initBus() {
        bus.register(this);
    }

    @Override
    public void bind(XTHRMData data, int position) {
        this.data = data;
        if (data != null && Float.isNaN(data.getHumidity())) {
            humidity.setText("-- %");
        }
    }

    @Subscribe
    public void onTriggerUpdate(TriggerUpdate event){
        updateValue(data);
    }

    private void updateValue(XTHRMData data) {
        if (data != null && Float.isNaN(data.getHumidity())) {
            humidity.setText("-- %");
        } else if (data != null) {
            humidity.setText(String.format(Locale.getDefault(), "%.0f %%", data.getHumidity()));
        }
    }
}

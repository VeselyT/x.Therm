package cz.eclub.xtherm.xtherm.ui;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.EasingFunction;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

import cz.eclub.xtherm.xtherm.bus.OttoBus;
import cz.eclub.xtherm.xtherm.bus.events.ConnectionEvent;
import cz.eclub.xtherm.xtherm.R;
import cz.eclub.xtherm.xtherm.bus.events.TriggerUpdate;
import cz.eclub.xtherm.xtherm.bus.events.UpdateStarted;
import cz.eclub.xtherm.xtherm.ui.utils.BindableLinearLayout;

/**
 * Created by vesely on 9/17/16.
 */
@EViewGroup(R.layout.temperature_view)
public class TempView extends BindableLinearLayout<XTHRMData> {

    private final Object lock = new Object();
    private XTHRMData queuedUpdateValue = null;

    @Bean
    protected OttoBus bus;

    @ViewById
    protected TextView temperature;

    @ViewById
    protected ProgressBar connectionActive;

    @ViewById
    protected ProgressBar updateValue;
    private ValueAnimator animator;

    public TempView(Context context) {
        super(context);
    }

    public TempView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterInject
    protected void initBus() {
        bus.register(this);
    }

    @Override
    public void bind(XTHRMData data, int position) {
        synchronized (lock) {
            if (animator != null && animator.isRunning()) {
                queuedUpdateValue = data;
            } else {
                updateValue(data);
            }
        }
    }

    private void updateValue(XTHRMData value) {
        if (value != null && Float.isNaN(value.getTemperature())) {
            temperature.setText("--  ºC");
        } else if (value != null) {
            temperature.setText(String.format(Locale.getDefault(), "%.1f ºC", value.getTemperature()));
        }
        bus.post(new TriggerUpdate());
    }

    @Subscribe
    public void onConnectionChanged(ConnectionEvent event) {
        connectionActive.setProgress(event.isConnected() ? 100 : 0);
    }

    @Subscribe
    public void onUpdateStarted(UpdateStarted event) {
        if (animator == null) {
            animator = new ValueAnimator();
            animator.setObjectValues(0, 101);
            animator.setDuration(750);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    updateValue.setProgress((Integer) animation.getAnimatedValue());
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    createAlphaAnimator();
                    synchronized (lock) {
                        updateValue(queuedUpdateValue);
                        queuedUpdateValue = null;
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    createAlphaAnimator();
                    synchronized (lock) {
                        updateValue(queuedUpdateValue);
                        queuedUpdateValue = null;
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

        }
        if (!animator.isRunning()) {
            animator.start();
        }
    }

    private void createAlphaAnimator(){
        ValueAnimator v = new ValueAnimator();
        v.setFloatValues(1, 0);
        v.setDuration(1000);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateValue.setAlpha((Float) animation.getAnimatedValue());
            }
        });
        v.setInterpolator(new DecelerateInterpolator(2f));
        v.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                updateValue.setAlpha(1);
                updateValue.setProgress(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                updateValue.setAlpha(1);
                updateValue.setProgress(0);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        v.start();
    }
}

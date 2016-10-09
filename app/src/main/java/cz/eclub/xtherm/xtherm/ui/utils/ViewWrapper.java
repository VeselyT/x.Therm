package cz.eclub.xtherm.xtherm.ui.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by vesely on 9/17/16.
 */
public class ViewWrapper<T, V extends View & ViewWrapper.Binder<T>> extends RecyclerView.ViewHolder {

    private V view;

    public ViewWrapper(V itemView) {
        super(itemView);
        view = itemView;
    }

    public V getView() {
        return view;
    }

    public interface Binder<T> {
        void bind(T data, int position);
    }
}
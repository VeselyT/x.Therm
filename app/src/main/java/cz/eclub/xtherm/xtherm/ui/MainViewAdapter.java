package cz.eclub.xtherm.xtherm.ui;

import android.view.ViewGroup;

import cz.eclub.xtherm.xtherm.ui.utils.BindableLinearLayout;
import cz.eclub.xtherm.xtherm.ui.utils.RecyclerViewAdapterBase;

/**
 * Created by vesely on 9/17/16.
 */
public class MainViewAdapter extends RecyclerViewAdapterBase<XTHRMData, BindableLinearLayout<XTHRMData>> {

    private XTHRMData data;

    public MainViewAdapter() {
        data = new XTHRMData();
    }

    @Override
    protected BindableLinearLayout<XTHRMData> onCreateItemView(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return TempView_.build(parent.getContext());
        } else {
            return HumidityView_.build(parent.getContext());
        }
    }

    @Override
    protected XTHRMData getItem(int position) {
        return data;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : (data.hasHumidity() ? 2 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setData(XTHRMData data) {
        this.data = data;
        notifyDataSetChanged();
    }
}

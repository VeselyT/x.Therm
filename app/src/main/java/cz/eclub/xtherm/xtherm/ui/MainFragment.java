package cz.eclub.xtherm.xtherm.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import cz.eclub.xtherm.xtherm.bus.events.DataChanged;
import cz.eclub.xtherm.xtherm.R;
import cz.eclub.xtherm.xtherm.bus.OttoBus;

/**
 * Created by vesely on 9/19/16.
 */
@EFragment(R.layout.main_fragment)
public class MainFragment extends Fragment {

    @Bean
    OttoBus bus;

    @ViewById(R.id.list)
    RecyclerView list;

    private MainViewAdapter adapter;

    @AfterViews
    protected void initRecyclerView() {
        adapter = new MainViewAdapter();
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list.setAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Subscribe
    public void onDataChanged(DataChanged data){
        adapter.setData(data.getData());
    }
}

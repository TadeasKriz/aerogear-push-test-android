package org.jboss.aerogear.pushtest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.jboss.aerogear.pushtest.BaseFragment;
import org.jboss.aerogear.pushtest.R;

public class PerformanceOutputFragment extends BaseFragment {

    @Override
    public String getTitle() {
        return "Performance output";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.performance_output_fragment, null);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // do getView().findViewById here
    }

    @Override
    public void onPushMessageReceived(Context context, Bundle message) {


    }
}
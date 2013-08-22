package org.jboss.aerogear.pushtest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.jboss.aerogear.pushtest.BaseFragment;
import org.jboss.aerogear.pushtest.R;
import org.jboss.aerogear.pushtest.util.JsonBundleUtil;

public class SimpleOutputFragment extends BaseFragment {

    private TextView outputTextView;

    @Override
    public String getTitle() {
        return "Simple output";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.simple_output_fragment, null);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        outputTextView = (TextView) getView().findViewById(R.id.textView_output);
    }

    @Override
    public void onPushMessageReceived(Context context, Bundle message) {
        String newText = JsonBundleUtil.asJsonPrettyPrint(message);

        outputTextView.setText(newText);
    }
}

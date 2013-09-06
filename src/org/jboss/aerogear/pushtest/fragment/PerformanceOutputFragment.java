package org.jboss.aerogear.pushtest.fragment;

import java.util.List;

import org.jboss.aerogear.pushtest.BaseFragment;
import org.jboss.aerogear.pushtest.R;
import org.jboss.aerogear.pushtest.perf.PushMessageDeliveryReport;
import org.jboss.aerogear.pushtest.util.JsonBundleUtil;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class PerformanceOutputFragment extends BaseFragment {
    private static final String TAG = PerformanceOutputFragment.class.getSimpleName();

    private Switch performanceStatsSwitch;
    private Button performanceResetButton;
    private TextView outputTextView;

    private PushMessageDeliveryReport deliveryReport;

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

        outputTextView = (TextView) getView().findViewById(R.id.textView_output);
        performanceStatsSwitch = (Switch) getView().findViewById(R.id.switch_stats);
        performanceResetButton = (Button) getView().findViewById(R.id.button_reset);
        deliveryReport = new PushMessageDeliveryReport();

        performanceStatsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    List<Integer> missingMessages = deliveryReport.missingMessages();
                    StringBuilder sb = new StringBuilder("Total delivered messages: ");
                    sb.append(deliveryReport.totalDeliveredMessages());

                    if (missingMessages.size() > 0) {
                        sb.append("\n\nTotal missing messages: ");
                        sb.append(missingMessages.size());
                        for (Integer msg : missingMessages) {
                            sb.append("\n").append(msg);
                        }
                    }

                    outputTextView.setText(sb.toString());
                } else {
                    outputTextView.setText(R.string.performance_output_text);
                }
            }
        });

        performanceResetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // reset delivery report
                deliveryReport.reset();
            }
        });

    }

    @Override
    public void onPushMessageReceived(Context context, Bundle message) {
        JSONObject msg = JsonBundleUtil.asJson(message);
        try {
            int agent = msg.getInt("agent");
            int process = msg.getInt("process");
            int thread = msg.getInt("thread");
            int run = msg.getInt("run");

            deliveryReport.delivered(agent, process, thread, run);
            // update text output only if not showing stats at the very same time
            if (!performanceStatsSwitch.isChecked()) {
                outputTextView.setText(JsonBundleUtil.prettyPrint(msg));
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
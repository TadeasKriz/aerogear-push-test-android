package org.jboss.aerogear.pushtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

    public abstract String getTitle();

    public void onPushMessageReceived(Context context, Bundle message) {

    }


    public void onDeletePushMessage(Context context, Bundle message) {


    }

    public void onPushMessageError() {

    }
}

package org.jboss.aerogear.pushtest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.impl.unifiedpush.AeroGearGCMPushRegistrar;
import org.jboss.aerogear.android.unifiedpush.PushConfig;

import java.net.MalformedURLException;
import java.net.URL;

public class BaseActivity extends Activity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private OnRegistrationSuccessListener onRegistrationSuccessListener;
    private OnRegistrationFailedListener onRegistrationFailedListener;
    private OnUnregistrationSuccessListener onUnregistrationSuccessListener;
    private OnUnregistrationFailedListener onUnregistrationFailedListener;

    private boolean registered = false;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        registered = savedInstanceState.getBoolean("registered", registered);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("registered", registered);
    }

    public BaseApplication getBaseApplication() {
        return (BaseApplication) getApplication();
    }

    protected void registerDeviceOnPushServer(final Config config) {
        try {
            final URL registerURL = new URL(config.host);

            PushConfig pushConfig = new PushConfig(registerURL, config.senderId);
            pushConfig.setVariantID(config.variantId);
            pushConfig.setAlias(config.alias);
            pushConfig.setSecret(config.secret);

            final AeroGearGCMPushRegistrar registrar = new AeroGearGCMPushRegistrar(pushConfig);
            registrar.register(this, new Callback<Void>() {
                @Override
                public void onSuccess(Void ignore) {
                    registered = true;

                    if(onRegistrationSuccessListener != null) {
                        onRegistrationSuccessListener.onRegistrationSuccess();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    if(onRegistrationFailedListener != null) {
                        onRegistrationFailedListener.onRegistrationFailed(e);
                    }
                }
            });

        } catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void unregisterDeviceOnPushServer(final Config config) {
        if(onUnregistrationFailedListener != null) {
            onUnregistrationFailedListener.onUnregistrationFailed(new RuntimeException("Not implemented!"));
        }

        try {
            final URL registerURL = new URL(config.host);
           // final Registrar registrar = new Registrar(registerURL);


        } catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setOnRegistrationSuccessListener(OnRegistrationSuccessListener listener) {
        onRegistrationSuccessListener = listener;
    }

    public void setOnRegistrationFailedListener(OnRegistrationFailedListener listener) {
        onRegistrationFailedListener = listener;
    }

    public void setOnUnregistrationSuccessListener(OnUnregistrationSuccessListener listener) {
        onUnregistrationSuccessListener = listener;
    }

    public void setOnUnregistrationFailedListener(OnUnregistrationFailedListener listener) {
        onUnregistrationFailedListener = listener;
    }

    public boolean isRegistered() {
        return registered;
    }

    public static interface OnRegistrationSuccessListener {
        public void onRegistrationSuccess();
    }

    public static interface OnRegistrationFailedListener {
        public void onRegistrationFailed(Exception e);
    }

    public static interface OnUnregistrationSuccessListener {
        public void onUnregistrationSuccess();
    }

    public static interface OnUnregistrationFailedListener {
        public void onUnregistrationFailed(Exception e);
    }

    public static class Config {
        public String host;
        public String senderId;
        public String variantId;
        public String alias;
        public String secret;
    }

}

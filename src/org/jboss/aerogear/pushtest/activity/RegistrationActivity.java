package org.jboss.aerogear.pushtest.activity;

import org.jboss.aerogear.pushtest.BaseActivity;
import org.jboss.aerogear.pushtest.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistrationActivity extends BaseActivity implements BaseActivity.OnRegistrationSuccessListener,
        BaseActivity.OnRegistrationFailedListener, BaseActivity.OnUnregistrationSuccessListener,
        BaseActivity.OnUnregistrationFailedListener {

    private Button buttonRegister;
    private EditText editTextPushServer;
    private EditText editTextVariantId;
    private EditText editTextSenderId;
    private EditText editTextAlias;
    private EditText editTextSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        setOnRegistrationSuccessListener(this);
        setOnRegistrationFailedListener(this);
        setOnUnregistrationSuccessListener(this);
        setOnUnregistrationFailedListener(this);

        buttonRegister = (Button) findViewById(R.id.button_register);
        editTextPushServer = (EditText) findViewById(R.id.editText_pushServer);
        editTextVariantId = (EditText) findViewById(R.id.editText_variantId);
        editTextSenderId = (EditText) findViewById(R.id.editText_senderId);
        editTextAlias = (EditText) findViewById(R.id.editText_alias);
        editTextSecret = (EditText) findViewById(R.id.editText_secret);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRegister.setEnabled(false);
                disableEditTexts();

                Config config = new Config();
                config.host = editTextPushServer.getText().toString();
                config.variantId = editTextVariantId.getText().toString();
                config.senderId = editTextSenderId.getText().toString();
                config.alias = editTextAlias.getText().toString();
                config.secret = editTextSecret.getText().toString();

                if (isRegistered()) {
                    unregisterDeviceOnPushServer(config);
                } else {
                    registerDeviceOnPushServer(config);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRegistered()) {
            buttonRegister.setEnabled(true);
            buttonRegister.setText(R.string.main_unregister);
            disableEditTexts();
        }
    }

    @Override
    public void onRegistrationSuccess() {
        buttonRegister.setEnabled(true);
        buttonRegister.setText(R.string.main_unregister);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRegistrationFailed(Exception e) {
        buttonRegister.setEnabled(true);
        enableEditTexts();
    }

    @Override
    public void onUnregistrationSuccess() {
        buttonRegister.setEnabled(true);
        buttonRegister.setText(R.string.main_register);
        enableEditTexts();
    }

    @Override
    public void onUnregistrationFailed(Exception e) {
        buttonRegister.setEnabled(true);
    }

    private void disableEditTexts() {
        setEditTextsEnabled(false);
    }

    private void enableEditTexts() {
        setEditTextsEnabled(true);
    }

    private void setEditTextsEnabled(boolean enabled) {
        editTextPushServer.setEnabled(enabled);
        editTextVariantId.setEnabled(enabled);
        editTextSenderId.setEnabled(enabled);
        editTextAlias.setEnabled(enabled);
        editTextSecret.setEnabled(enabled);
    }
}

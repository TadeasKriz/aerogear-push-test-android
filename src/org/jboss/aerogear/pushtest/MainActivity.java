package org.jboss.aerogear.pushtest;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.Registrar;

public class MainActivity extends BaseActivity implements MessageHandler,
        BaseActivity.OnRegistrationSuccessListener, BaseActivity.OnRegistrationFailedListener,
        BaseActivity.OnUnregistrationSuccessListener, BaseActivity.OnUnregistrationFailedListener {

    private Button buttonRegister;
    private CheckBox checkBoxAppendOutput;
    private EditText editTextPushServer;
    private EditText editTextVariantId;
    private EditText editTextSenderId;
    private EditText editTextAlias;
    private EditText editTextSecret;
    private TextView textViewOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setOnRegistrationSuccessListener(this);
        setOnRegistrationFailedListener(this);
        setOnUnregistrationSuccessListener(this);
        setOnUnregistrationFailedListener(this);

        buttonRegister = (Button) findViewById(R.id.button_register);
        checkBoxAppendOutput = (CheckBox) findViewById(R.id.checkBox_appendOutput);
        editTextPushServer = (EditText) findViewById(R.id.editText_pushServer);
        editTextVariantId = (EditText) findViewById(R.id.editText_variantId);
        editTextSenderId = (EditText) findViewById(R.id.editText_senderId);
        editTextAlias = (EditText) findViewById(R.id.editText_alias);
        editTextSecret = (EditText) findViewById(R.id.editText_secret);
        textViewOutput = (TextView) findViewById(R.id.textView_output);

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

                if(isRegistered()) {
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
        outState.putString("textViewOutput_text", textViewOutput.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textViewOutput.setText(savedInstanceState.getString("textViewOutput_text", getString(R.string.main_output_text)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Registrar.registerMainThreadHandler(this);
        if(isRegistered()) {
            buttonRegister.setEnabled(true);
            buttonRegister.setText(R.string.main_unregister);
            disableEditTexts();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Registrar.unregisterMainThreadHandler(this);
    }

    @Override
    public void onMessage(Context context, Bundle message) {

        String text = "{";

        for(String key : message.keySet()) {
            if(!text.equals("{")) {
                text += ", ";
            }

            text += "\"" + key + "\" : ";

            Object object = message.get(key);
            if(object instanceof Integer) {
                text += object;
            } else {
                if(object instanceof String){
                    try {
                        int intValue = Integer.parseInt((String) object);
                        text += intValue;
                        continue;
                    } catch(NumberFormatException e) {

                    }
                }
                text += "\"" + object + "\"";
            }
        }
        text += "}";

        if(!checkBoxAppendOutput.isChecked() || textViewOutput.getText().equals(getString(R.string.main_output_text))) {
            textViewOutput.setText(text);
        } else {
            textViewOutput.setText(text + "\n\n" + textViewOutput.getText());


        }
    }

    @Override
    public void onDeleteMessage(Context context, Bundle message) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onRegistrationSuccess() {
        buttonRegister.setEnabled(true);
        buttonRegister.setText(R.string.main_unregister);
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
    public void onUnregistrationFiled(Exception e) {
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

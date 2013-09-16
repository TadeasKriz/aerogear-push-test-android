package org.jboss.aerogear.pushtest.activity;

import android.util.Log;
import android.widget.TextView;
import org.jboss.aerogear.pushtest.BaseActivity;
import org.jboss.aerogear.pushtest.R;

import android.os.AsyncTask;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.impl.http.HttpRestProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;

public class RegistrationActivity extends BaseActivity implements BaseActivity.OnRegistrationSuccessListener,
        BaseActivity.OnRegistrationFailedListener, BaseActivity.OnUnregistrationSuccessListener,
        BaseActivity.OnUnregistrationFailedListener {

    private Button buttonPreload;
    private Button buttonRegister;
    private EditText editTextPreloadUrl;
    private EditText editTextPushServer;
    private EditText editTextVariantId;
    private EditText editTextSenderId;
    private EditText editTextAlias;
    private EditText editTextSecret;
    private TextView textViewOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        setOnRegistrationSuccessListener(this);
        setOnRegistrationFailedListener(this);
        setOnUnregistrationSuccessListener(this);
        setOnUnregistrationFailedListener(this);

        buttonPreload = (Button) findViewById(R.id.button_preload);
        buttonRegister = (Button) findViewById(R.id.button_register);
        editTextPreloadUrl = (EditText) findViewById(R.id.editText_preloadUrl);
        editTextPushServer = (EditText) findViewById(R.id.editText_pushServer);
        editTextVariantId = (EditText) findViewById(R.id.editText_variantId);
        editTextSenderId = (EditText) findViewById(R.id.editText_senderId);
        editTextAlias = (EditText) findViewById(R.id.editText_alias);
        editTextSecret = (EditText) findViewById(R.id.editText_secret);
        textViewOutput = (TextView) findViewById(R.id.textView_output);

        buttonPreload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPreload.setEnabled(false);

                new AsyncTask<Void, String, JSONObject>() {

                    @Override
                    protected JSONObject doInBackground(Void... voids) {

                        URI uri = URI.create(editTextPreloadUrl.getText().toString());

                        try {
                            HttpRestProvider httpRestProvider = new HttpRestProvider(uri.toURL());

                            HeaderAndBody headerAndBody = httpRestProvider.get();

                            String jsonString = new String(headerAndBody.getBody());

                            JSONObject json = new JSONObject(jsonString);

                            return json;
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            publishProgress(e.getMessage());
                            return null;
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                            publishProgress(e.getMessage());
                            return null;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            publishProgress(e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        textViewOutput.setText(values[0]);
                    }

                    @Override
                    protected void onPostExecute(JSONObject json) {
                        buttonPreload.setEnabled(true);

                        if(json == null) {
                            return;
                        }

                        if(json.has("url")) {
                            editTextPushServer.setText(json.optString("url"));
                        }

                        if(json.has("variantID")) {
                            editTextVariantId.setText(json.optString("variantID"));
                        }

                        if(json.has("senderID")) {
                            editTextSenderId.setText(json.optString("senderID"));
                        }

                        if(json.has("alias")) {
                            editTextAlias.setText(json.optString("alias"));
                        }

                        if(json.has("secret")) {
                            editTextSecret.setText(json.optString("secret"));
                        }

                    }
                }.execute();


            }
        });

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
        textViewOutput.setText(e.getMessage());
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

package com.gabrielezanelli.schoolendar.preferences;


import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.gabrielezanelli.schoolendar.FirebaseUser;
import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.services.SpaggiariDownloaderService;
import com.gabrielezanelli.schoolendar.spaggiari.SpaggiariClient;
import com.gabrielezanelli.schoolendar.spaggiari.SpaggiariCredentials;
import com.google.gson.Gson;

public class SpaggiariPreference extends DialogPreference {

    private SpaggiariCredentials spaggiariCredentials;

    // UI References
    private EditText schoolCodeText;
    private EditText userCodeText;
    private EditText passwordText;

    public SpaggiariPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_spaggiari_login);
        setPositiveButtonText(R.string.action_login);
        setNegativeButtonText(R.string.action_cancel);

        spaggiariCredentials = new SpaggiariCredentials("", "", "");
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        schoolCodeText = (EditText) view.findViewById(R.id.school_code_text);
        userCodeText = (EditText) view.findViewById(R.id.user_code_text);
        passwordText = (EditText) view.findViewById(R.id.password_text);

        return view;
    }


    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        SpaggiariCredentials emptyCredentials = new SpaggiariCredentials("", "", "");
        if (restorePersistedValue) {
            spaggiariCredentials = new Gson().fromJson(getPersistedString(new Gson().toJson(emptyCredentials)), SpaggiariCredentials.class);
            FirebaseUser.setSpaggiariCredentials(spaggiariCredentials);
        }
        else
            spaggiariCredentials = emptyCredentials;

        setSummary(getSummary());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

            final String schoolCode = "BSIT0006";
            final String userCode = "S828535F";
            final String password = "Gabriele97";

            //schoolCodeText.getText().toString();
            //userCodeText.getText().toString();
            //passwordText.getText().toString();

            SpaggiariClient.getIstance().login(schoolCode, userCode, password,
                    new SpaggiariClient.SpaggiariAuthStateListener() {
                        @Override
                        public void onLoginSuccess(String response) {
                            setSummary("Connected");
                            SpaggiariCredentials spaggiariCredentials = new SpaggiariCredentials(schoolCode, userCode, password);
                            Log.d("Gson credentials", new Gson().toJson(spaggiariCredentials));
                            persistString(new Gson().toJson(spaggiariCredentials));
                            new SpaggiariDownloaderService(getContext());
                        }

                        @Override
                        public void onLoginFailure(String response) {
                            setSummary("Could not connect, wrong credentials");
                        }
                    });
        }
        super.onDialogClosed(positiveResult);
    }


    private void setErrors() {
        userCodeText.setError("Wrong credential");
        // TODO: Handle error when credentials are wrong
    }

    @Override
    public CharSequence getSummary() {
        if(spaggiariCredentials.isEmpty())
            return "Not connected";
        else
            return "Connected";
    }
}

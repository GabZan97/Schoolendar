package com.gabrielezanelli.schoolendar.preferences;


import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.spaggiari.SpaggiariClient;

public class SpaggiariPreference extends DialogPreference {

    private EditText schoolCodeText;
    private EditText userCodeText;
    private EditText passwordText;

    public SpaggiariPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_spaggiari_login);
        setPositiveButtonText(R.string.action_login);
        setNegativeButtonText(R.string.action_cancel);
    }


    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        schoolCodeText = (EditText)view.findViewById(R.id.school_code_text);
        userCodeText = (EditText)view.findViewById(R.id.user_code_text);
        passwordText = (EditText)view.findViewById(R.id.password_text);
    }

    @Override
    public void onClick(final DialogInterface dialog,final int buttonClicked) {

        if(buttonClicked== DialogInterface.BUTTON_POSITIVE) {
            
            //schoolCodeText.getText().toString();
            //userCodeText.getText().toString();
            //passwordText.getText().toString();

            new SpaggiariClient().login(getContext(),schoolCode, userCode, password,
                    new SpaggiariClient.SpaggiariAuthStateListener() {
                        @Override
                        public void onLoginSuccess() {
                            setSummary("Linked");
                        }

                        @Override
                        public void onLoginFailure() {
                            // TODO: Find a way not to close the dialog onFailure
                            setSummary("Not linked");
                        }
                    });
        }
    }

    private void setErrors() {
        userCodeText.setError("Wrong credential");
        // TODO: Handle error when credentials are wrong
    }

}

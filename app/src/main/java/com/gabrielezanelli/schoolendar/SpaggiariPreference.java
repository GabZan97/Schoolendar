package com.gabrielezanelli.schoolendar;


import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SpaggiariPreference extends DialogPreference {

    private EditText a;
    private TextView b;
    private EditText c;
    private TextView e;
    private EditText d;
    private TextView f;

    public SpaggiariPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPersistent(false);
        setDialogLayoutResource(R.layout.dialog_spaggiari_login);
        setPositiveButtonText(R.string.action_login);
        setNegativeButtonText(R.string.action_cancel);
    }


    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);


    }
}

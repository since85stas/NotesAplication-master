package com.batura.stas.notesaplication.Other;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.batura.stas.notesaplication.MainActivity;
import com.batura.stas.notesaplication.R;
import com.batura.stas.notesaplication.data.NoteContract;

import eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener;
import eltos.simpledialogfragment.color.SimpleColorDialog;
import eltos.simpledialogfragment.form.SimpleFormDialog;
import eltos.simpledialogfragment.input.SimplePinDialog;

/**
 * Created by seeyo on 20.08.2018.
 */

public class Password extends AppCompatActivity implements OnDialogResultListener {

    public static final String PASS_OK_INTENT = Password.class.getPackage() + ".passOk" ;

    private static final String PIN = "pin";
    private static final String PIN_TAG = "pin_tag";


    //preferences keys
    // Sharedpref file name
    private static final String PREF_NAME = "NotesPref";
    private static final String HAS_PASS = "hasPass";
    public static final String PASSWORD = "password";
    public static final String SORTED_BY = "sorted";

    private SharedPreferences mSettings;
    private boolean mHasPass;
    private String  mPass;

    private void saveSettings () {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(HAS_PASS,mHasPass);
        editor.putString(PASSWORD,mPass);
        editor.apply();
    }

    private void loadSettings() {
        if (mSettings.contains(HAS_PASS)) {
            mHasPass = mSettings.getBoolean(HAS_PASS,false);
        }
        if (mSettings.contains(PASSWORD) && mHasPass) {
            mPass = mSettings.getString(PASSWORD,"");
        }
    }
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        loadSettings();
        
        String intentType = 
                getIntent().getExtras().getString(MainActivity.PASS_INTENT_TYPE);
        
        switch (intentType) {
            case MainActivity.TYPE_CHECK:
                checkPassword();
                break;
            case MainActivity.TYPE_SET:
                setPassword();
                break;
            case MainActivity.TYPE_DEL:
                delPassword();
                break;
            default:
                throw (new IllegalArgumentException("wronf intent"));
        }

    }

    private void delPassword() {
        setContentView(R.layout.password_activity);
        TextView textView = findViewById(R.id.passTitle);
        textView.setText(R.string.delletePass);
        final EditText passwordText = findViewById(R.id.passwordText);
        final EditText confirmPass = findViewById(R.id.passwordTextConfirm);
        confirmPass.setVisibility(View.GONE);
        Button ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passEnter = passwordText.getText().toString();
                if (passEnter.equals(mPass)) {
                    Toast.makeText(Password.this,
                            "Password is del",
                            Toast.LENGTH_SHORT).show();
                    mHasPass = false;
                    mPass = "";
                    saveSettings();
                    finish();
                } else {
                    Toast.makeText(Password.this,
                            "Wrong password, ENTER another password",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void setPassword() {
        setContentView(R.layout.password_activity);

        final EditText passwordText = findViewById(R.id.passwordText);
        final EditText confirmPass = findViewById(R.id.passwordTextConfirm);
        Button ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passEnter = passwordText.getText().toString();
                String passConfirm = confirmPass.getText().toString();
                if (passEnter.equals(passConfirm) && passEnter.length() == 4) {
                    Toast.makeText(Password.this,
                            "Password is set",
                            Toast.LENGTH_SHORT).show();

                    mHasPass = true;
                    mPass = passConfirm;
                    saveSettings();
                    finish();
                } else {
                    Toast.makeText(Password.this,
                            "Wrong password, ENTER another password",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void checkPassword() {

        //setContentView(R.layout.password_activity);

//        SimpleFormDialog.buildPinCodeInput(PIN,4)
//                .show(this,SimplePinDialog.TAG);

        SimplePinDialog.build()
                .pin(mPass)
                //.length(6)
                .show(this);

//        final EditText passwordText = findViewById(R.id.passwordText);
//        EditText confirmPass = findViewById(R.id.passwordTextConfirm);
//        confirmPass.setVisibility(View.GONE);
//        Button ok = findViewById(R.id.ok);
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String passEnter = passwordText.getText().toString();
//                if (passEnter.equals(mPass)) {
//                    Toast.makeText(Password.this,
//                            "Password is Ok",
//                            Toast.LENGTH_SHORT).show();
//                    Intent answIntent = new Intent(Password.this,MainActivity.class);
//                    answIntent.putExtra(PASS_OK_INTENT, true);
//                    setResult(RESULT_OK, answIntent); // отвечаем что напоминание установлено
//                    //startActi);
//                    finish();
//                } else {
//                    Toast.makeText(Password.this,
//                            "Wrong password, ENTER another password",
//                            Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {

        if (which == BUTTON_POSITIVE) {
            switch (dialogTag) {
                case SimplePinDialog.TAG: /** {@link MainActivity#showPinInput(View)} **/
                    String pin = extras.getString(SimplePinDialog.PIN);
                    //Toast.makeText(this, pin, Toast.LENGTH_SHORT).show();
                    Toast.makeText(Password.this,
                            "PIN is Ok",
                            Toast.LENGTH_SHORT).show();
                    Intent answIntent = new Intent(Password.this,MainActivity.class);
                    answIntent.putExtra(PASS_OK_INTENT, true);
                    setResult(RESULT_OK, answIntent);
                    //startActivity(answIntent);
                    finish();
                    return true;
                }
            }
        return false;
    }
}

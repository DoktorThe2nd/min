package com.doktorthe2nd.min;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.doktorthe2nd.min.luaj.ScriptAPI;
import com.doktorthe2nd.min.luaj.ScriptEngine;
import com.doktorthe2nd.min.modules.MReporter;
import com.doktorthe2nd.min.modules.session.MSession;
import com.doktorthe2nd.min.modules.session.SessionData;
import com.doktorthe2nd.min.modules.sync.MSync;
import com.doktorthe2nd.min.net.Connection;

public class MainActivity extends Activity {
    public interface RunOnUi {
        void run(Runnable runnable);
    }

    public static Context appContext;
    public static RunOnUi runOnUi;

    protected void goToAuth() {
        setContentView(R.layout.auth);
        TextView text_phone = findViewById(R.id.text_phone);
        EditText edit_phone = findViewById(R.id.edit_phone);
        Button button_phone = findViewById(R.id.button_phone);
        TextView text_code = findViewById(R.id.text_code);
        EditText edit_code = findViewById(R.id.edit_code);
        Button button_code = findViewById(R.id.button_code);
        TextView text_password = findViewById(R.id.text_password);
        EditText edit_password = findViewById(R.id.edit_password);
        Button button_password = findViewById(R.id.button_password);

        button_phone.setOnClickListener(v -> {
            String phone = MSession.normalizePhone(edit_phone.getText().toString());
            if (phone == null) {
                MReporter.toastError("Not a phone number (or not typical idk)");
                return;
            }
            MSession.authRequest(phone, ()->runOnUiThread(()->{
                text_phone.setVisibility(ViewGroup.GONE);
                edit_phone.setVisibility(ViewGroup.GONE);
                button_phone.setVisibility(ViewGroup.GONE);
                text_code.setVisibility(ViewGroup.VISIBLE);
                edit_code.setVisibility(ViewGroup.VISIBLE);
                button_code.setVisibility(ViewGroup.VISIBLE);
            }));
        });

        button_code.setOnClickListener(v -> {
            String code = edit_code.getText().toString();
            if (code.length() != 6) {
                MReporter.toastError("Not a code");
                return;
            }
            MSession.authSendCode(code, ()->runOnUiThread(()->{
                if (MSession.gotPasswordChallenge()) {
                    text_code.setVisibility(ViewGroup.GONE);
                    edit_code.setVisibility(ViewGroup.GONE);
                    button_code.setVisibility(ViewGroup.GONE);
                    text_password.setVisibility(ViewGroup.VISIBLE);
                    edit_password.setVisibility(ViewGroup.VISIBLE);
                    button_password.setVisibility(ViewGroup.VISIBLE);
                } else login();
            }));
        });

        button_password.setOnClickListener(v -> {
            String password = edit_password.getText().toString();
            MSession.authSendPassword(password, ()->runOnUiThread(this::login));
        });
    }

    protected void login() {
        MSession.init(()->{
            MSync.sendLogin();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        runOnUi = this::runOnUiThread;

        try {

            ScriptEngine.init(new ScriptAPI() {
                @Override
                public Context getAppContext() {
                    return getApplicationContext();
                }
            });

            String answer = ScriptEngine.runAllAssets();
            if (!answer.equals("OK")) {
                System.err.println(answer);
                throw new RuntimeException("Lua: "+answer);
            }

            setContentView(R.layout.connecting);

            Connection.start();
            if (SessionData.isSessionSaved()) {
                MSession.loadFromSave();
                login();
            } else MSession.init(() -> runOnUiThread(this::goToAuth));
        } catch (RuntimeException e) {
            MReporter.makeErrorScreen(this, e.getMessage());
        }
    }
}
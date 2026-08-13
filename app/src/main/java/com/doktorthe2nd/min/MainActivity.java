package com.doktorthe2nd.min;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doktorthe2nd.min.luaj.ScriptAPI;
import com.doktorthe2nd.min.luaj.ScriptEngine;
import com.doktorthe2nd.min.modules.MReporter;
import com.doktorthe2nd.min.modules.session.MSession;
import com.doktorthe2nd.min.modules.session.SessionData;
import com.doktorthe2nd.min.net.Connection;

import org.w3c.dom.Text;

public class MainActivity extends Activity {
    public interface RunOnUi {
        void run(Runnable runnable);
    }

    public static Context appContext;
    public static RunOnUi runOnUi;

    private ViewGroup viewView = null;
    public void setCView(ViewGroup view) {
        viewView = view;
        setContentView(view);
    }
    public ViewGroup getCView() {
        return viewView;
    }

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

        final boolean[] accept_phone = {true};
        button_phone.setOnClickListener(v -> {
            if (!accept_phone[0]) return;
            String phone = MSession.normalizePhone(edit_phone.getText().toString());
            if (phone == null) {
                MReporter.toastError("Not a phone number (or not typical idk)");
                return;
            }
            accept_phone[0] = false;
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
                } else {
                    MReporter.toast("LOG IN");
                }
            }));
        });

        button_password.setOnClickListener(v -> {
            String password = edit_password.getText().toString();
            MSession.authSendPassword(password, ()->runOnUiThread(()->{
                MReporter.toast("LOG IN");
            }));
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        runOnUi = this::runOnUiThread;

        ScriptEngine.init(new ScriptAPI(){
            @Override
            public void setView(ViewGroup view) {
                setCView(view);
            }
            @Override
            public ViewGroup getView() {
                return getCView();
            }
            @Override
            public Context getAppContext() {
                return getApplicationContext();
            }
        });

        String answer = ScriptEngine.runAllAssets();
        if (!answer.equals("OK")) System.err.println(answer);

        Connection.start();
        if (SessionData.isSessionSaved()) {
            Consts.currentSession = SessionData.loadSession();
            Consts.deviceId = Consts.currentSession.deviceId;
            Consts.instanceId = Consts.currentSession.mt_instanceid;
        }
        else MSession.init(()->runOnUiThread(this::goToAuth));
    }
}
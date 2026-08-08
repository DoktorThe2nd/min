package com.doktorthe2nd.min;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.doktorthe2nd.min.luaj.ScriptInstaller;
import com.doktorthe2nd.min.modules.session.MSession;
import com.doktorthe2nd.min.web.Connection;

public class MainActivity extends Activity {
    private static final int REQ_PICK_SCRIPT = 1001;
    private ScriptInstaller installer;

    @FunctionalInterface
    public interface RunOnUi {
        void run(Runnable runnable);
    }

    public static Context appContext;
    public static RunOnUi runOnUi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        appContext = getApplicationContext();
        runOnUi = this::runOnUiThread;

        Button run = findViewById(R.id.run);
        run.setOnClickListener(v -> {
            Connection.start();
        });

        Button add = findViewById(R.id.add);
        add.setOnClickListener(v -> {
            MSession.init();
        });

        Button req = findViewById(R.id.request_auth);
        req.setOnClickListener(v -> {
            MSession.authRequest("+79595658086");
        });

        EditText codeinput = findViewById(R.id.inputcode);

        Button sendCode = findViewById(R.id.sendCode);
        sendCode.setOnClickListener(v -> {
            MSession.authSendCode(codeinput.getText().toString());
        });

        EditText passwordInput = findViewById(R.id.password);

        Button sendPassword = findViewById(R.id.sendPassword);
        sendPassword.setOnClickListener(v -> {
            MSession.authSendPassword(passwordInput.getText().toString());
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQ_PICK_SCRIPT) return;
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
    }
}
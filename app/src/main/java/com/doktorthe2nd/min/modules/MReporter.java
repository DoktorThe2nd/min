package com.doktorthe2nd.min.modules;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doktorthe2nd.min.MainActivity;
import com.doktorthe2nd.min.net.Packet;

public class MReporter {
    public static void makeErrorScreen(Activity activity, String text) {
        MainActivity.runOnUi.run(()->{
            LinearLayout layout = new LinearLayout(activity);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER);
            TextView text1View = new TextView(activity);
            text1View.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            text1View.setText("Critical error:");
            TextView textView = new TextView(activity);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setText(text);
            layout.addView(text1View);
            layout.addView(textView);
            activity.setContentView(layout);
        });
    }

    public static void toastError(String text, int dur) {
        System.err.println(text);
        MainActivity.runOnUi.run(()->Toast.makeText(MainActivity.appContext, text, dur).show());
    }
    public static void toastError(String text) {
        toastError(text, Toast.LENGTH_LONG);
    }
    public static boolean toastIfError(Packet packet) {
        if (packet.isError()) {
            toastError("Replied error: "+packet.payload.get("message"));
            return true;
        }
        return false;
    }

    public static void toast(String text, int dur) {
        System.out.println(text);
        MainActivity.runOnUi.run(()->Toast.makeText(MainActivity.appContext, text, dur).show());
    }
    public static void toast(String text) {
        toast(text, Toast.LENGTH_LONG);
    }
}

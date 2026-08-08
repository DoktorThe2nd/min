package com.doktorthe2nd.min.modules;

import android.widget.Toast;

import com.doktorthe2nd.min.MainActivity;
import com.doktorthe2nd.min.web.Packet;

public class MReporter {
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

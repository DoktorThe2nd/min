package com.doktorthe2nd.min.luaj;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public interface ScriptAPI {
    void setView(ViewGroup view);
    ViewGroup getView();
    Context getAppContext();
}

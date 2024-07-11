package com.serenegiant.usb.glutils;

import android.view.Surface;

public interface RenderHolderCallback {
    void onCreate(Surface surface);

    void onDestroy();

    void onFrameAvailable();
}

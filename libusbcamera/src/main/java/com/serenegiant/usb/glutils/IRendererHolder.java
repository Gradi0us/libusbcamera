package com.serenegiant.usb.glutils;

import android.graphics.SurfaceTexture;
import android.view.Surface;

public interface IRendererHolder extends IRendererCommon {
    void addSurface(int i, Object obj, boolean z);

    void addSurface(int i, Object obj, boolean z, int i2);

    void captureStill(String str);

    void captureStillAsync(String str);

    int getCount();

    Surface getSurface();

    SurfaceTexture getSurfaceTexture();

    boolean isEnabled(int i);

    boolean isRunning();

    void release();

    void removeSurface(int i);

    void requestFrame();

    void reset();

    void resize(int i, int i2);

    void setEnabled(int i, boolean z);
}

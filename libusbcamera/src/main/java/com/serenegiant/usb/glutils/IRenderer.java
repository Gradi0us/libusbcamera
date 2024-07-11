package com.serenegiant.usb.glutils;

import android.graphics.SurfaceTexture;
import android.view.Surface;

public interface IRenderer extends IRendererCommon {
    void release();

    void requestRender(Object... objArr);

    void resize(int i, int i2);

    void setSurface(SurfaceTexture surfaceTexture);

    void setSurface(Surface surface);
}

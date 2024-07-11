package com.serenegiant.usb.glutils;

import android.os.Build;


public abstract class EGLBase {
    public static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    public static final Object EGL_LOCK = new Object();
    public static final int EGL_OPENGL_ES2_BIT = 4;
    public static final int EGL_OPENGL_ES3_BIT_KHR = 64;
    public static final int EGL_RECORDABLE_ANDROID = 12610;

    public static abstract class IConfig {
    }

    public static abstract class IContext {
    }

    public interface IEglSurface {
        IContext getContext();

        boolean isValid();

        void makeCurrent();

        void release();

        void swap();

        void swap(long j);
    }

    public abstract IEglSurface createFromSurface(Object obj);

    public abstract IEglSurface createOffscreen(int i, int i2);

    public abstract IConfig getConfig();

    public abstract IContext getContext();

    public abstract int getGlVersion();

    public abstract void makeDefault();

    public abstract String queryString(int i);

    public abstract void release();

    public abstract void sync();

    public static EGLBase createFrom(IContext sharedContext, boolean withDepthBuffer, boolean isRecordable) {
        return createFrom(3, sharedContext, withDepthBuffer, 0, isRecordable);
    }

    public static EGLBase createFrom(IContext sharedContext, boolean withDepthBuffer, int stencilBits, boolean isRecordable) {
        return createFrom(3, sharedContext, withDepthBuffer, stencilBits, isRecordable);
    }

    public static EGLBase createFrom(int maxClientVersion, IContext sharedContext, boolean withDepthBuffer, int stencilBits, boolean isRecordable) {
        if (!isEGL14Supported() || (sharedContext != null && !(sharedContext instanceof EGLBase14.Context))) {
            return new EGLBase10(maxClientVersion, (EGLBase10.Context) sharedContext, withDepthBuffer, stencilBits, isRecordable);
        }
        return new EGLBase14(maxClientVersion, (EGLBase14.Context) sharedContext, withDepthBuffer, stencilBits, isRecordable);
    }

    public static boolean isEGL14Supported() {
        return Build.VERSION.SDK_INT >= 18;
    }
}

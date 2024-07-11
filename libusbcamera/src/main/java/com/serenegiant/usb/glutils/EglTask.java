package com.serenegiant.usb.glutils;


import com.serenegiant.usb.utils.MessageTask;

public abstract class EglTask extends MessageTask {
    public static final int EGL_FLAG_DEPTH_BUFFER = 1;
    public static final int EGL_FLAG_RECORDABLE = 2;
    public static final int EGL_FLAG_STENCIL_1BIT = 4;
    public static final int EGL_FLAG_STENCIL_8BIT = 32;
    private EGLBase mEgl = null;
    private EGLBase.IEglSurface mEglHolder;

    public EglTask(EGLBase.IContext sharedContext, int flags) {
        init(flags, 3, sharedContext);
    }

    public EglTask(int maxClientVersion, EGLBase.IContext sharedContext, int flags) {
        init(flags, maxClientVersion, sharedContext);
    }

    /* access modifiers changed from: protected */
    public void onInit(int flags, int maxClientVersion, Object sharedContext) {
        boolean z;
        boolean z2 = false;
        if (sharedContext == null || (sharedContext instanceof EGLBase.IContext)) {
            int stencilBits = (flags & 4) == 4 ? 1 : (flags & 32) == 32 ? 8 : 0;
            EGLBase.IContext iContext = (EGLBase.IContext) sharedContext;
            if ((flags & 1) == 1) {
                z = true;
            } else {
                z = false;
            }
            if ((flags & 2) == 2) {
                z2 = true;
            }
            this.mEgl = EGLBase.createFrom(maxClientVersion, iContext, z, stencilBits, z2);
        }
        if (this.mEgl == null) {
            callOnError(new RuntimeException("failed to create EglCore"));
            releaseSelf();
            return;
        }
        this.mEglHolder = this.mEgl.createOffscreen(1, 1);
        this.mEglHolder.makeCurrent();
    }

    /* access modifiers changed from: protected */
    public MessageTask.Request takeRequest() throws InterruptedException {
        MessageTask.Request result = super.takeRequest();
        this.mEglHolder.makeCurrent();
        return result;
    }

    /* access modifiers changed from: protected */
    public void onBeforeStop() {
        this.mEglHolder.makeCurrent();
    }

    /* access modifiers changed from: protected */
    public void onRelease() {
        this.mEglHolder.release();
        this.mEgl.release();
    }

    /* access modifiers changed from: protected */
    public EGLBase getEgl() {
        return this.mEgl;
    }

    /* access modifiers changed from: protected */
    public EGLBase.IContext getEGLContext() {
        return this.mEgl.getContext();
    }

    /* access modifiers changed from: protected */
    public EGLBase.IConfig getConfig() {
        return this.mEgl.getConfig();
    }

    /* access modifiers changed from: protected */
    public EGLBase.IContext getContext() {
        if (this.mEgl != null) {
            return this.mEgl.getContext();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void makeCurrent() {
        this.mEglHolder.makeCurrent();
    }

    /* access modifiers changed from: protected */
    public boolean isGLES3() {
        return this.mEgl != null && this.mEgl.getGlVersion() > 2;
    }
}

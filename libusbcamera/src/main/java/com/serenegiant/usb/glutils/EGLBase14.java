package com.serenegiant.usb.glutils;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES10;
import android.opengl.GLES20;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import androidx.annotation.NonNull;

import com.serenegiant.usb.system.BuildCheck;


@TargetApi(18)
public class EGLBase14 extends EGLBase {
    private static final Context EGL_NO_CONTEXT = new Context(EGL14.EGL_NO_CONTEXT);
    private static final String TAG = "EGLBase14";
    @NonNull
    private Context mContext = EGL_NO_CONTEXT;
    private EGLContext mDefaultContext = EGL14.EGL_NO_CONTEXT;
    private Config mEglConfig = null;
    /* access modifiers changed from: private */
    public EGLDisplay mEglDisplay = EGL14.EGL_NO_DISPLAY;
    private int mGlVersion = 2;
    private final int[] mSurfaceDimension = new int[2];

    public static class Context extends EGLBase.IContext {
        public final EGLContext eglContext;

        private Context(EGLContext context) {
            this.eglContext = context;
        }
    }

    public static class Config extends EGLBase.IConfig {
        public final EGLConfig eglConfig;

        private Config(EGLConfig eglConfig2) {
            this.eglConfig = eglConfig2;
        }
    }

    public static class EglSurface implements EGLBase.IEglSurface {
        private final EGLBase14 mEglBase;
        private EGLSurface mEglSurface;

        private EglSurface(EGLBase14 eglBase, Object surface) throws IllegalArgumentException {
            this.mEglSurface = EGL14.EGL_NO_SURFACE;
            this.mEglBase = eglBase;
            if ((surface instanceof Surface) || (surface instanceof SurfaceHolder) || (surface instanceof SurfaceTexture) || (surface instanceof SurfaceView)) {
                this.mEglSurface = this.mEglBase.createWindowSurface(surface);
                return;
            }
            throw new IllegalArgumentException("unsupported surface");
        }

        private EglSurface(EGLBase14 eglBase, int width, int height) {
            this.mEglSurface = EGL14.EGL_NO_SURFACE;
            this.mEglBase = eglBase;
            if (width <= 0 || height <= 0) {
                this.mEglSurface = this.mEglBase.createOffscreenSurface(1, 1);
            } else {
                this.mEglSurface = this.mEglBase.createOffscreenSurface(width, height);
            }
        }

        public void makeCurrent() {
            boolean unused = this.mEglBase.makeCurrent(this.mEglSurface);
            if (this.mEglBase.getGlVersion() >= 2) {
                GLES20.glViewport(0, 0, this.mEglBase.getSurfaceWidth(this.mEglSurface), this.mEglBase.getSurfaceHeight(this.mEglSurface));
            } else {
                GLES10.glViewport(0, 0, this.mEglBase.getSurfaceWidth(this.mEglSurface), this.mEglBase.getSurfaceHeight(this.mEglSurface));
            }
        }

        public void swap() {
            int unused = this.mEglBase.swap(this.mEglSurface);
        }

        public void swap(long presentationTimeNs) {
            int unused = this.mEglBase.swap(this.mEglSurface, presentationTimeNs);
        }

        public void setPresentationTime(long presentationTimeNs) {
            EGLExt.eglPresentationTimeANDROID(this.mEglBase.mEglDisplay, this.mEglSurface, presentationTimeNs);
        }

        public EGLBase.IContext getContext() {
            return this.mEglBase.getContext();
        }

        public boolean isValid() {
            return this.mEglSurface != null && this.mEglSurface != EGL14.EGL_NO_SURFACE && this.mEglBase.getSurfaceWidth(this.mEglSurface) > 0 && this.mEglBase.getSurfaceHeight(this.mEglSurface) > 0;
        }

        public void release() {
            this.mEglBase.makeDefault();
            this.mEglBase.destroyWindowSurface(this.mEglSurface);
            this.mEglSurface = EGL14.EGL_NO_SURFACE;
        }
    }

    public EGLBase14(int maxClientVersion, Context sharedContext, boolean withDepthBuffer, int stencilBits, boolean isRecordable) {
        init(maxClientVersion, sharedContext, withDepthBuffer, stencilBits, isRecordable);
    }

    public void release() {
        if (this.mEglDisplay != EGL14.EGL_NO_DISPLAY) {
            destroyContext();
            EGL14.eglTerminate(this.mEglDisplay);
            EGL14.eglReleaseThread();
        }
        this.mEglDisplay = EGL14.EGL_NO_DISPLAY;
        this.mContext = EGL_NO_CONTEXT;
    }

//    public EglSurface createFromSurface(Object nativeWindow) {
//        EglSurface eglSurface = new EglSurface(nativeWindow);
//        eglSurface.makeCurrent();
//        return eglSurface;
//    }
//
//    public EglSurface createOffscreen(int width, int height) {
//        EglSurface eglSurface = new EglSurface(width, height);
//        eglSurface.makeCurrent();
//        return eglSurface;
//    }

    public String queryString(int what) {
        return EGL14.eglQueryString(this.mEglDisplay, what);
    }

    public int getGlVersion() {
        return this.mGlVersion;
    }

    public Context getContext() {
        return this.mContext;
    }

    @Override
    public IEglSurface createFromSurface(Object obj) {
        return null;
    }

    @Override
    public IEglSurface createOffscreen(int i, int i2) {
        return null;
    }

    public Config getConfig() {
        return this.mEglConfig;
    }

    public void makeDefault() {
        if (!EGL14.eglMakeCurrent(this.mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)) {
            Log.w("TAG", "makeDefault" + EGL14.eglGetError());
        }
    }

    public void sync() {
        EGL14.eglWaitGL();
        EGL14.eglWaitNative(12379);
    }

    private void init(int maxClientVersion, Context sharedContext, boolean withDepthBuffer, int stencilBits, boolean isRecordable) {
        EGLConfig config;
        if (this.mEglDisplay != EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGL already set up");
        }
        this.mEglDisplay = EGL14.eglGetDisplay(0);
        if (this.mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }
        int[] version = new int[2];
        if (!EGL14.eglInitialize(this.mEglDisplay, version, 0, version, 1)) {
            this.mEglDisplay = null;
            throw new RuntimeException("eglInitialize failed");
        }
        if (sharedContext == null) {
            sharedContext = EGL_NO_CONTEXT;
        }
        if (maxClientVersion >= 3 && (config = getConfig(3, withDepthBuffer, stencilBits, isRecordable)) != null) {
            EGLContext context = createContext(sharedContext, config, 3);
            if (EGL14.eglGetError() == 12288) {
                this.mEglConfig = new Config(config);
                this.mContext = new Context(context);
                this.mGlVersion = 3;
            }
        }
        if (maxClientVersion >= 2 && (this.mContext == null || this.mContext.eglContext == EGL14.EGL_NO_CONTEXT)) {
            EGLConfig config2 = getConfig(2, withDepthBuffer, stencilBits, isRecordable);
            if (config2 == null) {
                throw new RuntimeException("chooseConfig failed");
            }
            try {
                EGLContext context2 = createContext(sharedContext, config2, 2);
                checkEglError("eglCreateContext");
                this.mEglConfig = new Config(config2);
                this.mContext = new Context(context2);
                this.mGlVersion = 2;
            } catch (Exception e) {
                if (isRecordable) {
                    EGLConfig config3 = getConfig(2, withDepthBuffer, stencilBits, false);
                    if (config3 == null) {
                        throw new RuntimeException("chooseConfig failed");
                    }
                    EGLContext context3 = createContext(sharedContext, config3, 2);
                    checkEglError("eglCreateContext");
                    this.mEglConfig = new Config(config3);
                    this.mContext = new Context(context3);
                    this.mGlVersion = 2;
                }
            }
        }
        if (this.mContext == null || this.mContext.eglContext == EGL14.EGL_NO_CONTEXT) {
            EGLConfig config4 = getConfig(1, withDepthBuffer, stencilBits, isRecordable);
            if (config4 == null) {
                throw new RuntimeException("chooseConfig failed");
            }
            EGLContext context4 = createContext(sharedContext, config4, 1);
            checkEglError("eglCreateContext");
            this.mEglConfig = new Config(config4);
            this.mContext = new Context(context4);
            this.mGlVersion = 1;
        }
        int[] values = new int[1];
        EGL14.eglQueryContext(this.mEglDisplay, this.mContext.eglContext, EGLBase.EGL_CONTEXT_CLIENT_VERSION, values, 0);
        Log.d(TAG, "EGLContext created, client version " + values[0]);
        makeDefault();
    }

    /* access modifiers changed from: private */
    public boolean makeCurrent(EGLSurface surface) {
        if (surface == null || surface == EGL14.EGL_NO_SURFACE) {
            if (EGL14.eglGetError() != 12299) {
                return false;
            }
            Log.e(TAG, "makeCurrent:returned EGL_BAD_NATIVE_WINDOW.");
            return false;
        } else if (EGL14.eglMakeCurrent(this.mEglDisplay, surface, surface, this.mContext.eglContext)) {
            return true;
        } else {
            Log.w("TAG", "eglMakeCurrent" + EGL14.eglGetError());
            return false;
        }
    }

    /* access modifiers changed from: private */
    public int swap(EGLSurface surface) {
        if (!EGL14.eglSwapBuffers(this.mEglDisplay, surface)) {
            return EGL14.eglGetError();
        }
        return 12288;
    }

    /* access modifiers changed from: private */
    public int swap(EGLSurface surface, long presentationTimeNs) {
        EGLExt.eglPresentationTimeANDROID(this.mEglDisplay, surface, presentationTimeNs);
        if (!EGL14.eglSwapBuffers(this.mEglDisplay, surface)) {
            return EGL14.eglGetError();
        }
        return 12288;
    }

    private EGLContext createContext(Context sharedContext, EGLConfig config, int version) {
        return EGL14.eglCreateContext(this.mEglDisplay, config, sharedContext.eglContext, new int[]{12440, version, 12344}, 0);
    }

    private void destroyContext() {
        if (!EGL14.eglDestroyContext(this.mEglDisplay, this.mContext.eglContext)) {
            Log.e("destroyContext", "display:" + this.mEglDisplay + " context: " + this.mContext.eglContext);
            Log.e(TAG, "eglDestroyContext:" + EGL14.eglGetError());
        }
        this.mContext = EGL_NO_CONTEXT;
        if (this.mDefaultContext != EGL14.EGL_NO_CONTEXT) {
            if (!EGL14.eglDestroyContext(this.mEglDisplay, this.mDefaultContext)) {
                Log.e("destroyContext", "display:" + this.mEglDisplay + " context: " + this.mDefaultContext);
                Log.e(TAG, "eglDestroyContext:" + EGL14.eglGetError());
            }
            this.mDefaultContext = EGL14.EGL_NO_CONTEXT;
        }
    }

    /* access modifiers changed from: private */
    public final int getSurfaceWidth(EGLSurface surface) {
        if (!EGL14.eglQuerySurface(this.mEglDisplay, surface, 12375, this.mSurfaceDimension, 0)) {
            this.mSurfaceDimension[0] = 0;
        }
        return this.mSurfaceDimension[0];
    }

    /* access modifiers changed from: private */
    public final int getSurfaceHeight(EGLSurface surface) {
        if (!EGL14.eglQuerySurface(this.mEglDisplay, surface, 12374, this.mSurfaceDimension, 1)) {
            this.mSurfaceDimension[1] = 0;
        }
        return this.mSurfaceDimension[1];
    }

    /* access modifiers changed from: private */
    public final EGLSurface createWindowSurface(Object nativeWindow) {
        try {
            EGLSurface result = EGL14.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig.eglConfig, nativeWindow, new int[]{12344}, 0);
            if (result == null || result == EGL14.EGL_NO_SURFACE) {
                int error = EGL14.eglGetError();
                if (error == 12299) {
                    Log.e(TAG, "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                }
                throw new RuntimeException("createWindowSurface failed error=" + error);
            }
            makeCurrent(result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "eglCreateWindowSurface", e);
            throw new IllegalArgumentException(e);
        }
    }

    /* access modifiers changed from: private */
    public final EGLSurface createOffscreenSurface(int width, int height) {
        EGLSurface result = null;
        try {
            result = EGL14.eglCreatePbufferSurface(this.mEglDisplay, this.mEglConfig.eglConfig, new int[]{12375, width, 12374, height, 12344}, 0);
            checkEglError("eglCreatePbufferSurface");
            if (result == null) {
                throw new RuntimeException("surface was null");
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "createOffscreenSurface", e);
        } catch (RuntimeException e2) {
            Log.e(TAG, "createOffscreenSurface", e2);
        }
        return result;
    }

    /* access modifiers changed from: private */
    public void destroyWindowSurface(EGLSurface surface) {
        if (surface != EGL14.EGL_NO_SURFACE) {
            EGL14.eglMakeCurrent(this.mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(this.mEglDisplay, surface);
        }
        EGLSurface surface2 = EGL14.EGL_NO_SURFACE;
    }

    private void checkEglError(String msg) {
        int error = EGL14.eglGetError();
        if (error != 12288) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }

    private EGLConfig getConfig(int version, boolean hasDepthBuffer, int stencilBits, boolean isRecordable) {
        int renderableType = 4;
        if (version >= 3) {
            renderableType = 4 | 64;
        }
        int[] attribList = {12352, renderableType, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12344, 12344, 12344, 12344, 12344, 12344, 12344};
        int offset = 10;
        if (stencilBits > 0) {
            int offset2 = 10 + 1;
            attribList[10] = 12326;
            offset = offset2 + 1;
            attribList[offset2] = stencilBits;
        }
        int offset3 = offset;
        if (hasDepthBuffer) {
            int offset4 = offset3 + 1;
            attribList[offset3] = 12325;
            offset3 = offset4 + 1;
            attribList[offset4] = 16;
        }
        if (isRecordable && BuildCheck.isAndroid4_3()) {
            int offset5 = offset3 + 1;
            attribList[offset3] = 12610;
            offset3 = offset5 + 1;
            attribList[offset5] = 1;
        }
        int offset6 = offset3;
        for (int i = attribList.length - 1; i >= offset6; i--) {
            attribList[i] = 12344;
        }
        EGLConfig config = internalGetConfig(attribList);
        if (config == null && version == 2 && isRecordable) {
            int n = attribList.length;
            int i2 = 10;
            while (true) {
                if (i2 >= n - 1) {
                    break;
                } else if (attribList[i2] == 12610) {
                    for (int j = i2; j < n; j++) {
                        attribList[j] = 12344;
                    }
                } else {
                    i2 += 2;
                }
            }
            config = internalGetConfig(attribList);
        }
        if (config != null) {
            return config;
        }
        Log.w(TAG, "try to fallback to RGB565");
        attribList[3] = 5;
        attribList[5] = 6;
        attribList[7] = 5;
        return internalGetConfig(attribList);
    }

    private EGLConfig internalGetConfig(int[] attribList) {
        EGLConfig[] configs = new EGLConfig[1];
        if (!EGL14.eglChooseConfig(this.mEglDisplay, attribList, 0, configs, 0, configs.length, new int[1], 0)) {
            return null;
        }
        return configs[0];
    }
}

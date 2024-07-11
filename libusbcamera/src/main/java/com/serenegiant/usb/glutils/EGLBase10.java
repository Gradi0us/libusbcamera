package com.serenegiant.usb.glutils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.opengl.GLES10;
import android.opengl.GLES20;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.serenegiant.usb.system.BuildCheck;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class EGLBase10 extends EGLBase {
    private static final Context EGL_NO_CONTEXT = new Context(EGL10.EGL_NO_CONTEXT);
    private static final String TAG = "EGLBase10";
    @NonNull
    private Context mContext = EGL_NO_CONTEXT;
    private EGL10 mEgl = null;
    private Config mEglConfig = null;
    private EGLDisplay mEglDisplay = null;
    private int mGlVersion = 2;

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

    public static class MySurfaceHolder implements SurfaceHolder {
        private final Surface surface;

        public MySurfaceHolder(Surface surface2) {
            this.surface = surface2;
        }

        public Surface getSurface() {
            return this.surface;
        }

        public void addCallback(Callback callback) {
        }

        public void removeCallback(Callback callback) {
        }

        public boolean isCreating() {
            return false;
        }

        public void setType(int type) {
        }

        public void setFixedSize(int width, int height) {
        }

        public void setSizeFromLayout() {
        }

        public void setFormat(int format) {
        }

        public void setKeepScreenOn(boolean screenOn) {
        }

        public Canvas lockCanvas() {
            return null;
        }

        public Canvas lockCanvas(Rect dirty) {
            return null;
        }

        public void unlockCanvasAndPost(Canvas canvas) {
        }

        public Rect getSurfaceFrame() {
            return null;
        }
    }

    public static class EglSurface implements EGLBase.IEglSurface {
        private final EGLBase10 mEglBase;
        private EGLSurface mEglSurface;

        private EglSurface(EGLBase10 eglBase, Object surface) throws IllegalArgumentException {
            this.mEglSurface = EGL10.EGL_NO_SURFACE;
            this.mEglBase = eglBase;
            if ((surface instanceof Surface) && !BuildCheck.isAndroid4_2()) {
                this.mEglSurface = this.mEglBase.createWindowSurface(new MySurfaceHolder((Surface) surface));
            } else if ((surface instanceof Surface) || (surface instanceof SurfaceHolder) || (surface instanceof SurfaceTexture) || (surface instanceof SurfaceView)) {
                this.mEglSurface = this.mEglBase.createWindowSurface(surface);
            } else {
                throw new IllegalArgumentException("unsupported surface");
            }
        }

        private EglSurface(EGLBase10 eglBase, int width, int height) {
            this.mEglSurface = EGL10.EGL_NO_SURFACE;
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

        public EGLBase.IContext getContext() {
            return this.mEglBase.getContext();
        }

        public void setPresentationTime(long presentationTimeNs) {
        }

        public boolean isValid() {
            return this.mEglSurface != null && this.mEglSurface != EGL10.EGL_NO_SURFACE && this.mEglBase.getSurfaceWidth(this.mEglSurface) > 0 && this.mEglBase.getSurfaceHeight(this.mEglSurface) > 0;
        }

        public void release() {
            this.mEglBase.makeDefault();
            this.mEglBase.destroyWindowSurface(this.mEglSurface);
            this.mEglSurface = EGL10.EGL_NO_SURFACE;
        }
    }

    public EGLBase10(int maxClientVersion, Context sharedContext, boolean withDepthBuffer, int stencilBits, boolean isRecordable) {
        init(maxClientVersion, sharedContext, withDepthBuffer, stencilBits, isRecordable);
    }

    public void release() {
        destroyContext();
        this.mContext = EGL_NO_CONTEXT;
        if (this.mEgl != null) {
            this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            this.mEgl.eglTerminate(this.mEglDisplay);
            this.mEglDisplay = null;
            this.mEglConfig = null;
            this.mEgl = null;
        }
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
        if (!this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)) {
            Log.w(TAG, "makeDefault:eglMakeCurrent:err=" + this.mEgl.eglGetError());
        }
    }

    public void sync() {
        this.mEgl.eglWaitGL();
        this.mEgl.eglWaitNative(12379, (Object) null);
    }

    public String queryString(int what) {
        return this.mEgl.eglQueryString(this.mEglDisplay, what);
    }

    public int getGlVersion() {
        return this.mGlVersion;
    }

    private final void init(int maxClientVersion, @Nullable Context sharedContext, boolean withDepthBuffer, int stencilBits, boolean isRecordable) {
        EGLConfig config;
        if (sharedContext == null) {
            sharedContext = EGL_NO_CONTEXT;
        }
        if (this.mEgl == null) {
            this.mEgl = (EGL10) EGLContext.getEGL();
            this.mEglDisplay = this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.mEglDisplay == EGL10.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed");
            }
            if (!this.mEgl.eglInitialize(this.mEglDisplay, new int[2])) {
                this.mEglDisplay = null;
                throw new RuntimeException("eglInitialize failed");
            }
        }
        if (maxClientVersion >= 3 && (config = getConfig(3, withDepthBuffer, stencilBits, isRecordable)) != null) {
            EGLContext context = createContext(sharedContext, config, 3);
            if (this.mEgl.eglGetError() == 12288) {
                this.mEglConfig = new Config(config);
                this.mContext = new Context(context);
                this.mGlVersion = 3;
            }
        }
        if (maxClientVersion >= 2 && (this.mContext == null || this.mContext.eglContext == EGL10.EGL_NO_CONTEXT)) {
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
        if (this.mContext == null || this.mContext.eglContext == EGL10.EGL_NO_CONTEXT) {
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
        this.mEgl.eglQueryContext(this.mEglDisplay, this.mContext.eglContext, EGLBase.EGL_CONTEXT_CLIENT_VERSION, values);
        Log.d(TAG, "EGLContext created, client version " + values[0]);
        makeDefault();
    }

    /* access modifiers changed from: private */
    public final boolean makeCurrent(EGLSurface surface) {
        if (surface == null || surface == EGL10.EGL_NO_SURFACE) {
            if (this.mEgl.eglGetError() != 12299) {
                return false;
            }
            Log.e(TAG, "makeCurrent:EGL_BAD_NATIVE_WINDOW");
            return false;
        } else if (this.mEgl.eglMakeCurrent(this.mEglDisplay, surface, surface, this.mContext.eglContext)) {
            return true;
        } else {
            Log.w("TAG", "eglMakeCurrent" + this.mEgl.eglGetError());
            return false;
        }
    }

    /* access modifiers changed from: private */
    public final int swap(EGLSurface surface) {
        if (!this.mEgl.eglSwapBuffers(this.mEglDisplay, surface)) {
            return this.mEgl.eglGetError();
        }
        return 12288;
    }

    /* access modifiers changed from: private */
    public final int swap(EGLSurface surface, long ignored) {
        if (!this.mEgl.eglSwapBuffers(this.mEglDisplay, surface)) {
            return this.mEgl.eglGetError();
        }
        return 12288;
    }

    private final EGLContext createContext(@NonNull Context sharedContext, EGLConfig config, int version) {
        return this.mEgl.eglCreateContext(this.mEglDisplay, config, sharedContext.eglContext, new int[]{12440, version, 12344});
    }

    private final void destroyContext() {
        if (!this.mEgl.eglDestroyContext(this.mEglDisplay, this.mContext.eglContext)) {
            Log.e("destroyContext", "display:" + this.mEglDisplay + " context: " + this.mContext.eglContext);
            Log.e(TAG, "eglDestroyContext:" + this.mEgl.eglGetError());
        }
        this.mContext = EGL_NO_CONTEXT;
    }

    /* access modifiers changed from: private */
    public final int getSurfaceWidth(EGLSurface surface) {
        int[] value = new int[1];
        if (!this.mEgl.eglQuerySurface(this.mEglDisplay, surface, 12375, value)) {
            value[0] = 0;
        }
        return value[0];
    }

    /* access modifiers changed from: private */
    public final int getSurfaceHeight(EGLSurface surface) {
        int[] value = new int[1];
        if (!this.mEgl.eglQuerySurface(this.mEglDisplay, surface, 12374, value)) {
            value[0] = 0;
        }
        return value[0];
    }

    /* access modifiers changed from: private */
    public final EGLSurface createWindowSurface(Object nativeWindow) {
        try {
            EGLSurface result = this.mEgl.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig.eglConfig, nativeWindow, new int[]{12344});
            if (result == null || result == EGL10.EGL_NO_SURFACE) {
                int error = this.mEgl.eglGetError();
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
        int[] surfaceAttribs = {12375, width, 12374, height, 12344};
        this.mEgl.eglWaitGL();
        EGLSurface result = null;
        try {
            result = this.mEgl.eglCreatePbufferSurface(this.mEglDisplay, this.mEglConfig.eglConfig, surfaceAttribs);
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
    public final void destroyWindowSurface(EGLSurface surface) {
        if (surface != EGL10.EGL_NO_SURFACE) {
            this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            this.mEgl.eglDestroySurface(this.mEglDisplay, surface);
        }
        EGLSurface surface2 = EGL10.EGL_NO_SURFACE;
    }

    private final void checkEglError(String msg) {
        int error = this.mEgl.eglGetError();
        if (error != 12288) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }

    private final EGLConfig getConfig(int version, boolean hasDepthBuffer, int stencilBits, boolean isRecordable) {
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
            attribList[offset2] = 8;
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
        if (!this.mEgl.eglChooseConfig(this.mEglDisplay, attribList, configs, configs.length, new int[1])) {
            return null;
        }
        return configs[0];
    }
}

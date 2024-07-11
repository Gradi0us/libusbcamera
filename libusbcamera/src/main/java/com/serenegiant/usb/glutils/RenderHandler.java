package com.serenegiant.usb.glutils;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;


public final class RenderHandler extends Handler {
    private static final int MSG_CHECK_VALID = 3;
    private static final int MSG_RENDER_DRAW = 2;
    private static final int MSG_RENDER_QUIT = 9;
    private static final int MSG_RENDER_SET_GLCONTEXT = 1;
    private static final String TAG = "RenderHandler";
    private int mTexId;
    private final RenderThread mThread;

    public static RenderHandler createHandler() {
        return createHandler("RenderThread");
    }

    public static final RenderHandler createHandler(String name) {
        RenderThread thread = new RenderThread(name);
        thread.start();
        return thread.getHandler();
    }

    public final void setEglContext(EGLBase.IContext sharedContext, int tex_id, Object surface, boolean isRecordable) {
        int i;
        if ((surface instanceof Surface) || (surface instanceof SurfaceTexture) || (surface instanceof SurfaceHolder)) {
            this.mTexId = tex_id;
            if (isRecordable) {
                i = 1;
            } else {
                i = 0;
            }
            sendMessage(obtainMessage(1, i, 0, new ContextParams(sharedContext, surface)));
            return;
        }
        throw new RuntimeException("unsupported window type:" + surface);
    }

    public final void draw() {
        sendMessage(obtainMessage(2, this.mTexId, 0, (Object) null));
    }

    public final void draw(int tex_id) {
        sendMessage(obtainMessage(2, tex_id, 0, (Object) null));
    }

    public final void draw(float[] tex_matrix) {
        sendMessage(obtainMessage(2, this.mTexId, 0, tex_matrix));
    }

    public final void draw(int tex_id, float[] tex_matrix) {
        sendMessage(obtainMessage(2, tex_id, 0, tex_matrix));
    }

    public boolean isValid() {
        boolean isValid;
        synchronized (this.mThread.mSync) {
            sendEmptyMessage(3);
            try {
                this.mThread.mSync.wait();
            } catch (InterruptedException e) {
            }
            isValid = this.mThread.mSurface != null ? this.mThread.mSurface.isValid() : false;
        }
        return isValid;
    }

    public final void release() {
        removeMessages(1);
        removeMessages(2);
        sendEmptyMessage(9);
    }

    public final void handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                ContextParams params = (ContextParams) msg.obj;
                this.mThread.handleSetEglContext(params.sharedContext, params.surface, msg.arg1 != 0);
                return;
            case 2:
                this.mThread.handleDraw(msg.arg1, (float[]) msg.obj);
                return;
            case 3:
                synchronized (this.mThread.mSync) {
                    this.mThread.mSync.notify();
                }
                return;
            case 9:
                Looper.myLooper().quit();
                return;
            default:
                super.handleMessage(msg);
                return;
        }
    }

    private RenderHandler(RenderThread thread) {
        this.mTexId = -1;
        this.mThread = thread;
    }

    private static final class ContextParams {
        final EGLBase.IContext sharedContext;
        final Object surface;

        public ContextParams(EGLBase.IContext sharedContext2, Object surface2) {
            this.sharedContext = sharedContext2;
            this.surface = surface2;
        }
    }

    private static final class RenderThread extends Thread {
        private static final String TAG_THREAD = "RenderThread";
        private GLDrawer2D mDrawer;
        private EGLBase mEgl;
        private RenderHandler mHandler;
        /* access modifiers changed from: private */
        public Surface mSurface;
        /* access modifiers changed from: private */
        public final Object mSync = new Object();
        private EGLBase.IEglSurface mTargetSurface;

        public RenderThread(String name) {
            super(name);
        }

        public final RenderHandler getHandler() {
            synchronized (this.mSync) {
                try {
                    this.mSync.wait();
                } catch (InterruptedException e) {
                }
            }
            return this.mHandler;
        }

        public final void handleSetEglContext(EGLBase.IContext shardContext, Object surface, boolean isRecordable) {
            release();
            synchronized (this.mSync) {
                this.mSurface = surface instanceof Surface ? (Surface) surface : surface instanceof SurfaceTexture ? new Surface((SurfaceTexture) surface) : null;
            }
            this.mEgl = EGLBase.createFrom(3, shardContext, false, 0, isRecordable);
            try {
                this.mTargetSurface = this.mEgl.createFromSurface(surface);
                this.mDrawer = new GLDrawer2D(isRecordable);
            } catch (Exception e) {
                Log.w(RenderHandler.TAG, e);
                if (this.mTargetSurface != null) {
                    this.mTargetSurface.release();
                    this.mTargetSurface = null;
                }
                if (this.mDrawer != null) {
                    this.mDrawer.release();
                    this.mDrawer = null;
                }
            }
        }

        public void handleDraw(int tex_id, float[] tex_matrix) {
            if (tex_id >= 0 && this.mTargetSurface != null) {
                this.mTargetSurface.makeCurrent();
                this.mDrawer.draw(tex_id, tex_matrix, 0);
                this.mTargetSurface.swap();
            }
        }

        public final void run() {
            Looper.prepare();
            synchronized (this.mSync) {
                this.mHandler = new RenderHandler(this);
                this.mSync.notify();
            }
            Looper.loop();
            release();
            synchronized (this.mSync) {
                this.mHandler = null;
            }
        }

        private final void release() {
            if (this.mDrawer != null) {
                this.mDrawer.release();
                this.mDrawer = null;
            }
            synchronized (this.mSync) {
                this.mSurface = null;
            }
            if (this.mTargetSurface != null) {
                clear();
                this.mTargetSurface.release();
                this.mTargetSurface = null;
            }
            if (this.mEgl != null) {
                this.mEgl.release();
                this.mEgl = null;
            }
        }

        private final void clear() {
            this.mTargetSurface.makeCurrent();
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(16384);
            this.mTargetSurface.swap();
        }
    }
}

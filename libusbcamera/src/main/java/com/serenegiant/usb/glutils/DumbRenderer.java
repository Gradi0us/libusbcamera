package com.serenegiant.usb.glutils;

import android.graphics.SurfaceTexture;

import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.serenegiant.usb.utils.MessageTask;


public class DumbRenderer implements IRenderer {
    private static final int REQUEST_DRAW = 2;
    private static final int REQUEST_MIRROR = 4;
    private static final int REQUEST_RESIZE = 3;
    private static final int REQUEST_SET_SURFACE = 1;
    /* access modifiers changed from: private */
    public static final String TAG = DumbRenderer.class.getSimpleName();
    private int mMirror = 0;
    private RendererTask mRendererTask;
    private final Object mSync = new Object();

    public interface RendererDelegater {
        void onDraw(EGLBase eGLBase, Object... objArr);

        void onMirror(EGLBase eGLBase, int i);

        void onResize(EGLBase eGLBase, int i, int i2);

        void onSetSurface(EGLBase eGLBase, Object obj);

        void onStart(EGLBase eGLBase);

        void onStop(EGLBase eGLBase);
    }

    public DumbRenderer(EGLBase.IContext sharedContext, int flags, RendererDelegater delegater) {
        this.mRendererTask = new RendererTask(sharedContext, flags, delegater);
        new Thread(this.mRendererTask, TAG).start();
        if (!this.mRendererTask.waitReady()) {
            throw new RuntimeException("failed to start renderer thread");
        }
    }

    public void release() {
        synchronized (this.mSync) {
            if (this.mRendererTask != null) {
                this.mRendererTask.release();
                this.mRendererTask = null;
            }
        }
    }

    public void setSurface(Surface surface) {
        synchronized (this.mSync) {
            if (this.mRendererTask != null) {
                this.mRendererTask.offer(1, (Object) surface);
            }
        }
    }

    public void setSurface(SurfaceTexture surface) {
        synchronized (this.mSync) {
            if (this.mRendererTask != null) {
                this.mRendererTask.offer(1, (Object) surface);
            }
        }
    }

    public void setMirror(int mirror) {
        synchronized (this.mSync) {
            if (this.mMirror != mirror) {
                this.mMirror = mirror;
                if (this.mRendererTask != null) {
                    this.mRendererTask.offer(4, mirror % 4);
                }
            }
        }
    }

    public int getMirror() {
        return this.mMirror;
    }

    public void resize(int width, int height) {
        synchronized (this.mSync) {
            if (this.mRendererTask != null) {
                this.mRendererTask.offer(3, width, height);
            }
        }
    }

    public void requestRender(Object... args) {
        synchronized (this.mSync) {
            if (this.mRendererTask != null) {
                this.mRendererTask.offer(2, (Object) args);
            }
        }
    }

    private static class RendererTask extends EglTask {
        private int frameHeight;
        private int frameRotation;
        private int frameWidth;
        private final RendererDelegater mDelegater;
        private boolean mirror;
        private int surfaceHeight;
        private int surfaceWidth;

        public RendererTask(EGLBase.IContext sharedContext, int flags, @NonNull RendererDelegater delegater) {
            super(sharedContext, flags);
            this.mDelegater = delegater;
        }

        /* access modifiers changed from: protected */
        public void onStart() {
            makeCurrent();
            try {
                this.mDelegater.onStart(getEgl());
            } catch (Exception e) {
                Log.w(DumbRenderer.TAG, e);
            }
        }

        /* access modifiers changed from: protected */
        public void onStop() {
            makeCurrent();
            try {
                this.mDelegater.onStop(getEgl());
            } catch (Exception e) {
                Log.w(DumbRenderer.TAG, e);
            }
        }

        /* access modifiers changed from: protected */
        public Object processRequest(int request, int arg1, int arg2, Object obj) throws MessageTask.TaskBreak {
            switch (request) {
                case 1:
                    handleSetSurface(obj);
                    return null;
                case 2:
                    handleDraw(obj);
                    return null;
                case 3:
                    handleResize(arg1, arg2);
                    return null;
                case 4:
                    handleMirror(arg1);
                    return null;
                default:
                    return null;
            }
        }

        private void handleSetSurface(Object surface) {
            makeCurrent();
            try {
                this.mDelegater.onSetSurface(getEgl(), surface);
            } catch (Exception e) {
                Log.w(DumbRenderer.TAG, e);
            }
        }

        private void handleResize(int width, int height) {
            if (this.surfaceWidth != width || this.surfaceHeight != height) {
                this.surfaceWidth = width;
                this.surfaceHeight = height;
                makeCurrent();
                try {
                    this.mDelegater.onResize(getEgl(), width, height);
                } catch (Exception e) {
                    Log.w(DumbRenderer.TAG, e);
                }
                handleDraw(new Object[0]);
            }
        }

        private void handleDraw(Object... args) {
            makeCurrent();
            try {
                this.mDelegater.onDraw(getEgl(), args);
            } catch (Exception e) {
                Log.w(DumbRenderer.TAG, e);
            }
        }

        private void handleMirror(int mirror2) {
            makeCurrent();
            try {
                this.mDelegater.onMirror(getEgl(), mirror2);
            } catch (Exception e) {
                Log.w(DumbRenderer.TAG, e);
            }
        }
    }
}

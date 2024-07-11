package com.serenegiant.usb.glutils;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;

import com.serenegiant.usb.system.BuildCheck;

import java.io.File;

public class RendererHolder implements IRendererHolder {
    private static final int REQUEST_ADD_SURFACE = 3;
    private static final int REQUEST_DRAW = 1;
    private static final int REQUEST_MIRROR = 6;
    private static final int REQUEST_RECREATE_MASTER_SURFACE = 5;
    private static final int REQUEST_REMOVE_SURFACE = 4;
    private static final int REQUEST_UPDATE_SIZE = 2;
    /* access modifiers changed from: private */
    public static final String TAG = RendererHolder.class.getSimpleName();
    /* access modifiers changed from: private */
    public volatile boolean isRunning;
    /* access modifiers changed from: private */
    public final RenderHolderCallback mCallback;
    /* access modifiers changed from: private */
    public File mCaptureFile;
    /* access modifiers changed from: private */
    public final Runnable mCaptureTask = new Runnable() {
        EGLBase.IEglSurface captureSurface;
        GLDrawer2D drawer;
        EGLBase eglBase;

        public void run() {
            synchronized (RendererHolder.this.mSync) {
                if (!RendererHolder.this.isRunning) {
                    try {
                        RendererHolder.this.mSync.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            init();
            if (this.eglBase.getGlVersion() > 2) {
                captureLoopGLES3();
            } else {
                captureLoopGLES2();
            }
            release();
        }

        private final void init() {
            this.eglBase = EGLBase.createFrom(3, RendererHolder.this.mRendererTask.getContext(), false, 0, false);
            this.captureSurface = this.eglBase.createOffscreen(RendererHolder.this.mRendererTask.mVideoWidth, RendererHolder.this.mRendererTask.mVideoHeight);
            this.drawer = new GLDrawer2D(true);
            float[] mvpMatrix = this.drawer.getMvpMatrix();
            mvpMatrix[5] = mvpMatrix[5] * -1.0f;
        }

        /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x011b A[SYNTHETIC, Splitter:B:57:0x011b] */
        /* JADX WARNING: Removed duplicated region for block: B:73:0x011e A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private final void captureLoopGLES2() {
            /*
                r14 = this;
                r2 = -1
                r3 = -1
                r6 = 0
                r8 = 0
            L_0x0004:
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this
                boolean r0 = r0.isRunning
                if (r0 == 0) goto L_0x0041
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this
                java.lang.Object r13 = r0.mSync
                monitor-enter(r13)
                if (r8 != 0) goto L_0x0042
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                java.io.File r0 = r0.mCaptureFile     // Catch:{ all -> 0x003c }
                if (r0 != 0) goto L_0x0026
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ InterruptedException -> 0x003f }
                java.lang.Object r0 = r0.mSync     // Catch:{ InterruptedException -> 0x003f }
                r0.wait()     // Catch:{ InterruptedException -> 0x003f }
            L_0x0026:
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                java.io.File r0 = r0.mCaptureFile     // Catch:{ all -> 0x003c }
                if (r0 == 0) goto L_0x003a
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                java.io.File r8 = r0.mCaptureFile     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                r1 = 0
                java.io.File unused = r0.mCaptureFile = r1     // Catch:{ all -> 0x003c }
            L_0x003a:
                monitor-exit(r13)     // Catch:{ all -> 0x003c }
                goto L_0x0004
            L_0x003c:
                r0 = move-exception
                monitor-exit(r13)     // Catch:{ all -> 0x003c }
                throw r0
            L_0x003f:
                r10 = move-exception
                monitor-exit(r13)     // Catch:{ all -> 0x003c }
            L_0x0041:
                return
            L_0x0042:
                if (r6 != 0) goto L_0x0111
                r0 = 1
                r1 = r0
            L_0x0046:
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r0 = r0.mRendererTask     // Catch:{ all -> 0x003c }
                int r0 = r0.mVideoWidth     // Catch:{ all -> 0x003c }
                if (r2 == r0) goto L_0x0115
                r0 = 1
            L_0x0053:
                r0 = r0 | r1
                if (r0 != 0) goto L_0x0062
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r0 = r0.mRendererTask     // Catch:{ all -> 0x003c }
                int r0 = r0.mVideoHeight     // Catch:{ all -> 0x003c }
                if (r3 == r0) goto L_0x0097
            L_0x0062:
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r0 = r0.mRendererTask     // Catch:{ all -> 0x003c }
                int r2 = r0.mVideoWidth     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r0 = r0.mRendererTask     // Catch:{ all -> 0x003c }
                int r3 = r0.mVideoHeight     // Catch:{ all -> 0x003c }
                int r0 = r2 * r3
                int r0 = r0 * 4
                java.nio.ByteBuffer r6 = java.nio.ByteBuffer.allocateDirect(r0)     // Catch:{ all -> 0x003c }
                java.nio.ByteOrder r0 = java.nio.ByteOrder.LITTLE_ENDIAN     // Catch:{ all -> 0x003c }
                r6.order(r0)     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.EGLBase$IEglSurface r0 = r14.captureSurface     // Catch:{ all -> 0x003c }
                if (r0 == 0) goto L_0x008f
                com.serenegiant.glutils.EGLBase$IEglSurface r0 = r14.captureSurface     // Catch:{ all -> 0x003c }
                r0.release()     // Catch:{ all -> 0x003c }
                r0 = 0
                r14.captureSurface = r0     // Catch:{ all -> 0x003c }
            L_0x008f:
                com.serenegiant.glutils.EGLBase r0 = r14.eglBase     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.EGLBase$IEglSurface r0 = r0.createOffscreen(r2, r3)     // Catch:{ all -> 0x003c }
                r14.captureSurface = r0     // Catch:{ all -> 0x003c }
            L_0x0097:
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                boolean r0 = r0.isRunning     // Catch:{ all -> 0x003c }
                if (r0 == 0) goto L_0x0104
                com.serenegiant.glutils.EGLBase$IEglSurface r0 = r14.captureSurface     // Catch:{ all -> 0x003c }
                r0.makeCurrent()     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.GLDrawer2D r0 = r14.drawer     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder r1 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r1 = r1.mRendererTask     // Catch:{ all -> 0x003c }
                int r1 = r1.mTexId     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder r4 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r4 = r4.mRendererTask     // Catch:{ all -> 0x003c }
                float[] r4 = r4.mTexMatrix     // Catch:{ all -> 0x003c }
                r5 = 0
                r0.draw(r1, r4, r5)     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.EGLBase$IEglSurface r0 = r14.captureSurface     // Catch:{ all -> 0x003c }
                r0.swap()     // Catch:{ all -> 0x003c }
                r6.clear()     // Catch:{ all -> 0x003c }
                r0 = 0
                r1 = 0
                r4 = 6408(0x1908, float:8.98E-42)
                r5 = 5121(0x1401, float:7.176E-42)
                android.opengl.GLES20.glReadPixels(r0, r1, r2, r3, r4, r5, r6)     // Catch:{ all -> 0x003c }
                android.graphics.Bitmap$CompressFormat r9 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ all -> 0x003c }
                java.lang.String r0 = r8.toString()     // Catch:{ all -> 0x003c }
                java.lang.String r1 = ".jpg"
                boolean r0 = r0.endsWith(r1)     // Catch:{ all -> 0x003c }
                if (r0 == 0) goto L_0x00dd
                android.graphics.Bitmap$CompressFormat r9 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x003c }
            L_0x00dd:
                r11 = 0
                java.io.BufferedOutputStream r12 = new java.io.BufferedOutputStream     // Catch:{ all -> 0x0118 }
                java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x0118 }
                r0.<init>(r8)     // Catch:{ all -> 0x0118 }
                r12.<init>(r0)     // Catch:{ all -> 0x0118 }
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x013b }
                android.graphics.Bitmap r7 = android.graphics.Bitmap.createBitmap(r2, r3, r0)     // Catch:{ all -> 0x013b }
                r6.clear()     // Catch:{ all -> 0x013b }
                r7.copyPixelsFromBuffer(r6)     // Catch:{ all -> 0x013b }
                r0 = 90
                r7.compress(r9, r0, r12)     // Catch:{ all -> 0x013b }
                r7.recycle()     // Catch:{ all -> 0x013b }
                r12.flush()     // Catch:{ all -> 0x013b }
                if (r12 == 0) goto L_0x0104
                r12.close()     // Catch:{ FileNotFoundException -> 0x0138, IOException -> 0x012a }
            L_0x0104:
                r8 = 0
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                java.lang.Object r0 = r0.mSync     // Catch:{ all -> 0x003c }
                r0.notifyAll()     // Catch:{ all -> 0x003c }
                monitor-exit(r13)     // Catch:{ all -> 0x003c }
                goto L_0x0004
            L_0x0111:
                r0 = 0
                r1 = r0
                goto L_0x0046
            L_0x0115:
                r0 = 0
                goto L_0x0053
            L_0x0118:
                r0 = move-exception
            L_0x0119:
                if (r11 == 0) goto L_0x011e
                r11.close()     // Catch:{ FileNotFoundException -> 0x011f, IOException -> 0x0136 }
            L_0x011e:
                throw r0     // Catch:{ FileNotFoundException -> 0x011f, IOException -> 0x0136 }
            L_0x011f:
                r10 = move-exception
            L_0x0120:
                java.lang.String r0 = com.serenegiant.glutils.RendererHolder.TAG     // Catch:{ all -> 0x003c }
                java.lang.String r1 = "failed to save file"
                android.util.Log.w(r0, r1, r10)     // Catch:{ all -> 0x003c }
                goto L_0x0104
            L_0x012a:
                r10 = move-exception
                r11 = r12
            L_0x012c:
                java.lang.String r0 = com.serenegiant.glutils.RendererHolder.TAG     // Catch:{ all -> 0x003c }
                java.lang.String r1 = "failed to save file"
                android.util.Log.w(r0, r1, r10)     // Catch:{ all -> 0x003c }
                goto L_0x0104
            L_0x0136:
                r10 = move-exception
                goto L_0x012c
            L_0x0138:
                r10 = move-exception
                r11 = r12
                goto L_0x0120
            L_0x013b:
                r0 = move-exception
                r11 = r12
                goto L_0x0119
            */
            throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.glutils.RendererHolder.AnonymousClass1.captureLoopGLES2():void");
        }

        /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x011b A[SYNTHETIC, Splitter:B:57:0x011b] */
        /* JADX WARNING: Removed duplicated region for block: B:73:0x011e A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private final void captureLoopGLES3() {
            /*
                r14 = this;
                r2 = -1
                r3 = -1
                r6 = 0
                r8 = 0
            L_0x0004:
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this
                boolean r0 = r0.isRunning
                if (r0 == 0) goto L_0x0041
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this
                java.lang.Object r13 = r0.mSync
                monitor-enter(r13)
                if (r8 != 0) goto L_0x0042
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                java.io.File r0 = r0.mCaptureFile     // Catch:{ all -> 0x003c }
                if (r0 != 0) goto L_0x0026
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ InterruptedException -> 0x003f }
                java.lang.Object r0 = r0.mSync     // Catch:{ InterruptedException -> 0x003f }
                r0.wait()     // Catch:{ InterruptedException -> 0x003f }
            L_0x0026:
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                java.io.File r0 = r0.mCaptureFile     // Catch:{ all -> 0x003c }
                if (r0 == 0) goto L_0x003a
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                java.io.File r8 = r0.mCaptureFile     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                r1 = 0
                java.io.File unused = r0.mCaptureFile = r1     // Catch:{ all -> 0x003c }
            L_0x003a:
                monitor-exit(r13)     // Catch:{ all -> 0x003c }
                goto L_0x0004
            L_0x003c:
                r0 = move-exception
                monitor-exit(r13)     // Catch:{ all -> 0x003c }
                throw r0
            L_0x003f:
                r10 = move-exception
                monitor-exit(r13)     // Catch:{ all -> 0x003c }
            L_0x0041:
                return
            L_0x0042:
                if (r6 != 0) goto L_0x0111
                r0 = 1
                r1 = r0
            L_0x0046:
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r0 = r0.mRendererTask     // Catch:{ all -> 0x003c }
                int r0 = r0.mVideoWidth     // Catch:{ all -> 0x003c }
                if (r2 == r0) goto L_0x0115
                r0 = 1
            L_0x0053:
                r0 = r0 | r1
                if (r0 != 0) goto L_0x0062
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r0 = r0.mRendererTask     // Catch:{ all -> 0x003c }
                int r0 = r0.mVideoHeight     // Catch:{ all -> 0x003c }
                if (r3 == r0) goto L_0x0097
            L_0x0062:
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r0 = r0.mRendererTask     // Catch:{ all -> 0x003c }
                int r2 = r0.mVideoWidth     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r0 = r0.mRendererTask     // Catch:{ all -> 0x003c }
                int r3 = r0.mVideoHeight     // Catch:{ all -> 0x003c }
                int r0 = r2 * r3
                int r0 = r0 * 4
                java.nio.ByteBuffer r6 = java.nio.ByteBuffer.allocateDirect(r0)     // Catch:{ all -> 0x003c }
                java.nio.ByteOrder r0 = java.nio.ByteOrder.LITTLE_ENDIAN     // Catch:{ all -> 0x003c }
                r6.order(r0)     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.EGLBase$IEglSurface r0 = r14.captureSurface     // Catch:{ all -> 0x003c }
                if (r0 == 0) goto L_0x008f
                com.serenegiant.glutils.EGLBase$IEglSurface r0 = r14.captureSurface     // Catch:{ all -> 0x003c }
                r0.release()     // Catch:{ all -> 0x003c }
                r0 = 0
                r14.captureSurface = r0     // Catch:{ all -> 0x003c }
            L_0x008f:
                com.serenegiant.glutils.EGLBase r0 = r14.eglBase     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.EGLBase$IEglSurface r0 = r0.createOffscreen(r2, r3)     // Catch:{ all -> 0x003c }
                r14.captureSurface = r0     // Catch:{ all -> 0x003c }
            L_0x0097:
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                boolean r0 = r0.isRunning     // Catch:{ all -> 0x003c }
                if (r0 == 0) goto L_0x0104
                com.serenegiant.glutils.EGLBase$IEglSurface r0 = r14.captureSurface     // Catch:{ all -> 0x003c }
                r0.makeCurrent()     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.GLDrawer2D r0 = r14.drawer     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder r1 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r1 = r1.mRendererTask     // Catch:{ all -> 0x003c }
                int r1 = r1.mTexId     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder r4 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.RendererHolder$RendererTask r4 = r4.mRendererTask     // Catch:{ all -> 0x003c }
                float[] r4 = r4.mTexMatrix     // Catch:{ all -> 0x003c }
                r5 = 0
                r0.draw(r1, r4, r5)     // Catch:{ all -> 0x003c }
                com.serenegiant.glutils.EGLBase$IEglSurface r0 = r14.captureSurface     // Catch:{ all -> 0x003c }
                r0.swap()     // Catch:{ all -> 0x003c }
                r6.clear()     // Catch:{ all -> 0x003c }
                r0 = 0
                r1 = 0
                r4 = 6408(0x1908, float:8.98E-42)
                r5 = 5121(0x1401, float:7.176E-42)
                android.opengl.GLES20.glReadPixels(r0, r1, r2, r3, r4, r5, r6)     // Catch:{ all -> 0x003c }
                android.graphics.Bitmap$CompressFormat r9 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ all -> 0x003c }
                java.lang.String r0 = r8.toString()     // Catch:{ all -> 0x003c }
                java.lang.String r1 = ".jpg"
                boolean r0 = r0.endsWith(r1)     // Catch:{ all -> 0x003c }
                if (r0 == 0) goto L_0x00dd
                android.graphics.Bitmap$CompressFormat r9 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x003c }
            L_0x00dd:
                r11 = 0
                java.io.BufferedOutputStream r12 = new java.io.BufferedOutputStream     // Catch:{ all -> 0x0118 }
                java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x0118 }
                r0.<init>(r8)     // Catch:{ all -> 0x0118 }
                r12.<init>(r0)     // Catch:{ all -> 0x0118 }
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x013b }
                android.graphics.Bitmap r7 = android.graphics.Bitmap.createBitmap(r2, r3, r0)     // Catch:{ all -> 0x013b }
                r6.clear()     // Catch:{ all -> 0x013b }
                r7.copyPixelsFromBuffer(r6)     // Catch:{ all -> 0x013b }
                r0 = 90
                r7.compress(r9, r0, r12)     // Catch:{ all -> 0x013b }
                r7.recycle()     // Catch:{ all -> 0x013b }
                r12.flush()     // Catch:{ all -> 0x013b }
                if (r12 == 0) goto L_0x0104
                r12.close()     // Catch:{ FileNotFoundException -> 0x0138, IOException -> 0x012a }
            L_0x0104:
                r8 = 0
                com.serenegiant.glutils.RendererHolder r0 = com.serenegiant.glutils.RendererHolder.this     // Catch:{ all -> 0x003c }
                java.lang.Object r0 = r0.mSync     // Catch:{ all -> 0x003c }
                r0.notifyAll()     // Catch:{ all -> 0x003c }
                monitor-exit(r13)     // Catch:{ all -> 0x003c }
                goto L_0x0004
            L_0x0111:
                r0 = 0
                r1 = r0
                goto L_0x0046
            L_0x0115:
                r0 = 0
                goto L_0x0053
            L_0x0118:
                r0 = move-exception
            L_0x0119:
                if (r11 == 0) goto L_0x011e
                r11.close()     // Catch:{ FileNotFoundException -> 0x011f, IOException -> 0x0136 }
            L_0x011e:
                throw r0     // Catch:{ FileNotFoundException -> 0x011f, IOException -> 0x0136 }
            L_0x011f:
                r10 = move-exception
            L_0x0120:
                java.lang.String r0 = com.serenegiant.glutils.RendererHolder.TAG     // Catch:{ all -> 0x003c }
                java.lang.String r1 = "failed to save file"
                android.util.Log.w(r0, r1, r10)     // Catch:{ all -> 0x003c }
                goto L_0x0104
            L_0x012a:
                r10 = move-exception
                r11 = r12
            L_0x012c:
                java.lang.String r0 = com.serenegiant.glutils.RendererHolder.TAG     // Catch:{ all -> 0x003c }
                java.lang.String r1 = "failed to save file"
                android.util.Log.w(r0, r1, r10)     // Catch:{ all -> 0x003c }
                goto L_0x0104
            L_0x0136:
                r10 = move-exception
                goto L_0x012c
            L_0x0138:
                r10 = move-exception
                r11 = r12
                goto L_0x0120
            L_0x013b:
                r0 = move-exception
                r11 = r12
                goto L_0x0119
            */
            throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.glutils.RendererHolder.AnonymousClass1.captureLoopGLES3():void");
        }

        private final void release() {
            if (this.captureSurface != null) {
                this.captureSurface.makeCurrent();
                if (this.drawer != null) {
                    this.drawer.release();
                }
                this.captureSurface.release();
                this.captureSurface = null;
            }
            if (this.drawer != null) {
                this.drawer.release();
                this.drawer = null;
            }
            if (this.eglBase != null) {
                this.eglBase.release();
                this.eglBase = null;
            }
        }
    };
    /* access modifiers changed from: private */
    public final RendererTask mRendererTask;
    /* access modifiers changed from: private */
    public final Object mSync = new Object();

    public RendererHolder(int width, int height, @Nullable RenderHolderCallback callback) {
        this.mCallback = callback;
        this.mRendererTask = new RendererTask(this, width, height);
        new Thread(this.mRendererTask, TAG).start();
        if (!this.mRendererTask.waitReady()) {
            throw new RuntimeException("failed to start renderer thread");
        }
        new Thread(this.mCaptureTask, "CaptureTask").start();
        synchronized (this.mSync) {
            if (!this.isRunning) {
                try {
                    this.mSync.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void release() {
        this.mRendererTask.release();
        synchronized (this.mSync) {
            this.isRunning = false;
            this.mSync.notifyAll();
        }
    }

    public Surface getSurface() {
        return this.mRendererTask.getSurface();
    }

    public SurfaceTexture getSurfaceTexture() {
        return this.mRendererTask.getSurfaceTexture();
    }

    public void reset() {
        this.mRendererTask.checkMasterSurface();
    }

    public void resize(int width, int height) {
        this.mRendererTask.resize(width, height);
    }

    public void setMirror(int mirror) {
        this.mRendererTask.mirror(mirror % 4);
    }

    public int getMirror() {
        return this.mRendererTask.mirror();
    }

    public void addSurface(int id, Object surface, boolean isRecordable) {
        this.mRendererTask.addSurface(id, surface);
    }

    public void addSurface(int id, Object surface, boolean isRecordable, int maxFps) {
        this.mRendererTask.addSurface(id, surface, maxFps);
    }

    public void removeSurface(int id) {
        this.mRendererTask.removeSurface(id);
    }

    public boolean isEnabled(int id) {
        return this.mRendererTask.isEnabled(id);
    }

    public void setEnabled(int id, boolean enable) {
        this.mRendererTask.setEnabled(id, enable);
    }

    public void requestFrame() {
        this.mRendererTask.removeRequest(1);
        this.mRendererTask.offer(1);
    }

    public int getCount() {
        return this.mRendererTask.getCount();
    }

    public void captureStillAsync(String path) {
        File file = new File(path);
        synchronized (this.mSync) {
            this.mCaptureFile = file;
            this.mSync.notifyAll();
        }
    }

    public void captureStill(String path) {
        File file = new File(path);
        synchronized (this.mSync) {
            this.mCaptureFile = file;
            this.mSync.notifyAll();
            try {
                this.mSync.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    private static final class RendererTask extends EglTask {
        private final Object mClientSync = new Object();
        private final SparseArray<RendererSurfaceRec> mClients = new SparseArray<>();
        private GLDrawer2D mDrawer;
        private Surface mMasterSurface;
        private SurfaceTexture mMasterTexture;
        private int mMirror = 0;
        private final SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                RendererTask.this.offer(1);
            }
        };
        private final RendererHolder mParent;
        /* access modifiers changed from: private */
        public int mTexId;
        final float[] mTexMatrix = new float[16];
        /* access modifiers changed from: private */
        public int mVideoHeight;
        /* access modifiers changed from: private */
        public int mVideoWidth;

        public RendererTask(RendererHolder parent, int width, int height) {
            super(3, (EGLBase.IContext) null, 2);
            this.mParent = parent;
            this.mVideoWidth = width;
            this.mVideoHeight = height;
        }

        /* access modifiers changed from: protected */
        public void onStart() {
            this.mDrawer = new GLDrawer2D(true);
            handleReCreateMasterSurface();
            synchronized (this.mParent.mSync) {
                boolean unused = this.mParent.isRunning = true;
                this.mParent.mSync.notifyAll();
            }
        }

        /* access modifiers changed from: protected */
        public void onStop() {
            synchronized (this.mParent.mSync) {
                boolean unused = this.mParent.isRunning = false;
                this.mParent.mSync.notifyAll();
            }
            makeCurrent();
            if (this.mDrawer != null) {
                this.mDrawer.release();
                this.mDrawer = null;
            }
            handleReleaseMasterSurface();
            handleRemoveAll();
        }

        /* access modifiers changed from: protected */
        public boolean onError(Exception e) {
            return false;
        }

        /* access modifiers changed from: protected */
        public Object processRequest(int request, int arg1, int arg2, Object obj) {
            switch (request) {
                case 1:
                    handleDraw();
                    return null;
                case 2:
                    handleResize(arg1, arg2);
                    return null;
                case 3:
                    handleAddSurface(arg1, obj, arg2);
                    return null;
                case 4:
                    handleRemoveSurface(arg1);
                    return null;
                case 5:
                    handleReCreateMasterSurface();
                    return null;
                case 6:
                    handleMirror(arg1);
                    return null;
                default:
                    return null;
            }
        }

        public Surface getSurface() {
            checkMasterSurface();
            return this.mMasterSurface;
        }

        public SurfaceTexture getSurfaceTexture() {
            checkMasterSurface();
            return this.mMasterTexture;
        }

        public void addSurface(int id, Object surface) {
            addSurface(id, surface, -1);
        }

        public void addSurface(int id, Object surface, int maxFps) {
            checkFinished();
            if ((surface instanceof SurfaceTexture) || (surface instanceof Surface) || (surface instanceof SurfaceHolder)) {
                synchronized (this.mClientSync) {
                    if (this.mClients.get(id) == null) {
                        while (true) {
                            if (isRunning()) {
                                if (offer(3, id, maxFps, surface)) {
                                    try {
                                        this.mClientSync.wait();
                                        break;
                                    } catch (InterruptedException e) {
                                    }
                                } else {
                                    try {
                                        this.mClientSync.wait(10);
                                    } catch (InterruptedException e2) {
                                    }
                                }
                            }
                        }
                    }
                }
                return;
            }
            throw new IllegalArgumentException("Surface should be one of Surface, SurfaceTexture or SurfaceHolder");
        }

        public void removeSurface(int id) {
            synchronized (this.mClientSync) {
                if (this.mClients.get(id) != null) {
                    while (true) {
                        if (isRunning()) {
                            if (offer(4, id)) {
                                try {
                                    this.mClientSync.wait();
                                    break;
                                } catch (InterruptedException e) {
                                }
                            } else {
                                try {
                                    this.mClientSync.wait(10);
                                } catch (InterruptedException e2) {
                                }
                            }
                        }
                    }
                }
            }
        }

        public boolean isEnabled(int id) {
            boolean z;
            synchronized (this.mClientSync) {
                RendererSurfaceRec rec = this.mClients.get(id);
                z = rec != null && rec.isEnabled();
            }
            return z;
        }

        public void setEnabled(int id, boolean enable) {
            synchronized (this.mClientSync) {
                RendererSurfaceRec rec = this.mClients.get(id);
                if (rec != null) {
                    rec.setEnabled(enable);
                }
            }
        }

        public int getCount() {
            int size;
            synchronized (this.mClientSync) {
                size = this.mClients.size();
            }
            return size;
        }

        public void resize(int width, int height) {
            checkFinished();
            if (this.mVideoWidth != width || this.mVideoHeight != height) {
                offer(2, width, height);
            }
        }

        public void mirror(int mirror) {
            checkFinished();
            if (this.mMirror != mirror) {
                offer(6, mirror);
            }
        }

        public int mirror() {
            return this.mMirror;
        }

        public void checkMasterSurface() {
            checkFinished();
            if (this.mMasterSurface == null || !this.mMasterSurface.isValid()) {
                Log.d(RendererHolder.TAG, "checkMasterSurface:invalid master surface");
                offerAndWait(5, 0, 0, (Object) null);
            }
        }

        private void checkFinished() {
            if (isFinished()) {
                throw new RuntimeException("already finished");
            }
        }

        private void handleDraw() {
            if (this.mMasterSurface == null || !this.mMasterSurface.isValid()) {
                Log.w(RendererHolder.TAG, "checkMasterSurface:invalid master surface");
                offer(5);
                return;
            }
            try {
                makeCurrent();
                this.mMasterTexture.updateTexImage();
                this.mMasterTexture.getTransformMatrix(this.mTexMatrix);
                synchronized (this.mParent.mCaptureTask) {
                    this.mParent.mCaptureTask.notify();
                }
                synchronized (this.mClientSync) {
                    for (int i = this.mClients.size() - 1; i >= 0; i--) {
                        RendererSurfaceRec client = this.mClients.valueAt(i);
                        if (client != null && client.canDraw()) {
                            try {
                                client.draw(this.mDrawer, this.mTexId, this.mTexMatrix);
                            } catch (Exception e) {
                                this.mClients.removeAt(i);
                                client.release();
                            }
                        }
                    }
                }
                if (this.mParent.mCallback != null) {
                    try {
                        this.mParent.mCallback.onFrameAvailable();
                    } catch (Exception e2) {
                    }
                }
                GLES20.glClear(16384);
                GLES20.glFlush();
            } catch (Exception e3) {
                Log.w(RendererHolder.TAG, "draw:thread id =" + Thread.currentThread().getId(), e3);
                offer(5);
            }
        }

        private void handleAddSurface(int id, Object surface, int maxFps) {
            checkSurface();
            synchronized (this.mClientSync) {
                if (this.mClients.get(id) == null) {
                    try {
                        RendererSurfaceRec client = RendererSurfaceRec.newInstance(getEgl(), surface, maxFps);
                        setMirror(client, this.mMirror);
                        this.mClients.append(id, client);
                    } catch (Exception e) {
                        Log.w(RendererHolder.TAG, "invalid surface: surface=" + surface, e);
                    }
                } else {
                    Log.w(RendererHolder.TAG, "surface is already added: id=" + id);
                }
                this.mClientSync.notifyAll();
            }
        }

        private void handleRemoveSurface(int id) {
            synchronized (this.mClientSync) {
                RendererSurfaceRec client = this.mClients.get(id);
                if (client != null) {
                    this.mClients.remove(id);
                    client.release();
                }
                checkSurface();
                this.mClientSync.notifyAll();
            }
        }

        private void handleRemoveAll() {
            synchronized (this.mClientSync) {
                int n = this.mClients.size();
                for (int i = 0; i < n; i++) {
                    RendererSurfaceRec client = this.mClients.valueAt(i);
                    if (client != null) {
                        makeCurrent();
                        client.release();
                    }
                }
                this.mClients.clear();
            }
        }

        private void checkSurface() {
            synchronized (this.mClientSync) {
                int n = this.mClients.size();
                for (int i = 0; i < n; i++) {
                    RendererSurfaceRec client = this.mClients.valueAt(i);
                    if (client != null && !client.isValid()) {
                        int id = this.mClients.keyAt(i);
                        this.mClients.valueAt(i).release();
                        this.mClients.remove(id);
                    }
                }
            }
        }

        @SuppressLint({"NewApi"})
        private void handleReCreateMasterSurface() {
            makeCurrent();
            handleReleaseMasterSurface();
            makeCurrent();
//            this.mTexId = GLHelper.initTex(ShaderConst.GL_TEXTURE_EXTERNAL_OES, 9728);
            this.mMasterTexture = new SurfaceTexture(this.mTexId);
            this.mMasterSurface = new Surface(this.mMasterTexture);
            if (BuildCheck.isAndroid4_1()) {
                this.mMasterTexture.setDefaultBufferSize(this.mVideoWidth, this.mVideoHeight);
            }
            this.mMasterTexture.setOnFrameAvailableListener(this.mOnFrameAvailableListener);
            try {
                if (this.mParent.mCallback != null) {
                    this.mParent.mCallback.onCreate(this.mMasterSurface);
                }
            } catch (Exception e) {
                Log.w(RendererHolder.TAG, e);
            }
        }

        private void handleReleaseMasterSurface() {
            try {
                if (this.mParent.mCallback != null) {
                    this.mParent.mCallback.onDestroy();
                }
            } catch (Exception e) {
                Log.w(RendererHolder.TAG, e);
            }
            this.mMasterSurface = null;
            if (this.mMasterTexture != null) {
                this.mMasterTexture.release();
                this.mMasterTexture = null;
            }
            if (this.mTexId != 0) {
                GLHelper.deleteTex(this.mTexId);
                this.mTexId = 0;
            }
        }

        @SuppressLint({"NewApi"})
        private void handleResize(int width, int height) {
            this.mVideoWidth = width;
            this.mVideoHeight = height;
            if (BuildCheck.isAndroid4_1()) {
                this.mMasterTexture.setDefaultBufferSize(this.mVideoWidth, this.mVideoHeight);
            }
        }

        private void handleMirror(int mirror) {
            this.mMirror = mirror;
            synchronized (this.mClientSync) {
                int n = this.mClients.size();
                for (int i = 0; i < n; i++) {
                    RendererSurfaceRec client = this.mClients.valueAt(i);
                    if (client != null) {
                        setMirror(client, mirror);
                    }
                }
            }
        }

        private void setMirror(RendererSurfaceRec client, int mirror) {
            float[] mvp = client.mMvpMatrix;
            switch (mirror) {
                case 0:
                    mvp[0] = Math.abs(mvp[0]);
                    mvp[5] = Math.abs(mvp[5]);
                    return;
                case 1:
                    mvp[0] = -Math.abs(mvp[0]);
                    mvp[5] = Math.abs(mvp[5]);
                    return;
                case 2:
                    mvp[0] = Math.abs(mvp[0]);
                    mvp[5] = -Math.abs(mvp[5]);
                    return;
                case 3:
                    mvp[0] = -Math.abs(mvp[0]);
                    mvp[5] = -Math.abs(mvp[5]);
                    return;
                default:
                    return;
            }
        }
    }
}

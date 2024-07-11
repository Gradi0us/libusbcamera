package com.serenegiant.usb.glutils;

import android.opengl.GLES20;
import android.view.MotionEvent;
import java.nio.FloatBuffer;

public class Texture2dProgram {
    private static final boolean DEBUG = false;
    private static final String TAG = "Texture2dProgram";
    private float mColorAdjust;
    private final int[] mFlags;
    protected boolean mHasKernel2;
    private final float[] mKernel;
    private final float[] mLastTouchPosition;
    private int mProgramHandle;
    private final ProgramType mProgramType;
    private final float[] mSummedTouchPosition;
    private final Object mSync;
    private float mTexHeight;
    private float[] mTexOffset;
    private float mTexWidth;
    private int mTextureTarget;
    private final int maPositionLoc;
    private final int maTextureCoordLoc;
    private int muColorAdjustLoc;
    private int muFlagsLoc;
    private int muKernelLoc;
    private final int muMVPMatrixLoc;
    private final int muTexMatrixLoc;
    private int muTexOffsetLoc;
    private int muTouchPositionLoc;

    public enum ProgramType {
        TEXTURE_2D,
        TEXTURE_FILT3x3,
        TEXTURE_CUSTOM,
        TEXTURE_EXT,
        TEXTURE_EXT_BW,
        TEXTURE_EXT_NIGHT,
        TEXTURE_EXT_CHROMA_KEY,
        TEXTURE_EXT_SQUEEZE,
        TEXTURE_EXT_TWIRL,
        TEXTURE_EXT_TUNNEL,
        TEXTURE_EXT_BULGE,
        TEXTURE_EXT_DENT,
        TEXTURE_EXT_FISHEYE,
        TEXTURE_EXT_STRETCH,
        TEXTURE_EXT_MIRROR,
        TEXTURE_EXT_FILT3x3
    }



    protected Texture2dProgram(ProgramType programType, int target, String vss, String fss) {
        this.mSync = new Object();
        this.mKernel = new float[18];
        this.mSummedTouchPosition = new float[2];
        this.mLastTouchPosition = new float[2];
        this.mFlags = new int[4];
        this.mProgramType = programType;
//        switch (programType) {
//            case TEXTURE_2D:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_2D;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_2D);
//                break;
//            case TEXTURE_FILT3x3:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_2D;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_FILT3x3);
//                break;
//            case TEXTURE_EXT:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT);
//                break;
//            case TEXTURE_EXT_BW:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_BW);
//                break;
//            case TEXTURE_EXT_NIGHT:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_NIGHT);
//                break;
//            case TEXTURE_EXT_CHROMA_KEY:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_CHROMA_KEY);
//                break;
//            case TEXTURE_EXT_SQUEEZE:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_SQUEEZE);
//                break;
//            case TEXTURE_EXT_TWIRL:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_TWIRL);
//                break;
//            case TEXTURE_EXT_TUNNEL:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_TUNNEL);
//                break;
//            case TEXTURE_EXT_BULGE:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_BULGE);
//                break;
//            case TEXTURE_EXT_FISHEYE:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_FISHEYE);
//                break;
//            case TEXTURE_EXT_DENT:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_DENT);
//                break;
//            case TEXTURE_EXT_MIRROR:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_MIRROR);
//                break;
//            case TEXTURE_EXT_STRETCH:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_STRETCH);
//                break;
//            case TEXTURE_EXT_FILT3x3:
//                this.mTextureTarget = ShaderConst.GL_TEXTURE_EXTERNAL_OES;
//                this.mProgramHandle = GLHelper.loadShader(ShaderConst.VERTEX_SHADER, ShaderConst.FRAGMENT_SHADER_EXT_FILT3x3);
//                break;
//            case TEXTURE_CUSTOM:
//                switch (target) {
//                    case ShaderConst.GL_TEXTURE_2D:
//                    case ShaderConst.GL_TEXTURE_EXTERNAL_OES:
//                        this.mTextureTarget = target;
//                        this.mProgramHandle = GLHelper.loadShader(vss, fss);
//                        break;
//                    default:
//                        throw new IllegalArgumentException("target should be GL_TEXTURE_2D or GL_TEXTURE_EXTERNAL_OES");
//                }
//            default:
//                throw new RuntimeException("Unhandled type " + programType);
//        }
        if (this.mProgramHandle == 0) {
            throw new RuntimeException("Unable to create program");
        }
        this.maPositionLoc = GLES20.glGetAttribLocation(this.mProgramHandle, "aPosition");
        GLHelper.checkLocation(this.maPositionLoc, "aPosition");
        this.maTextureCoordLoc = GLES20.glGetAttribLocation(this.mProgramHandle, "aTextureCoord");
        GLHelper.checkLocation(this.maTextureCoordLoc, "aTextureCoord");
        this.muMVPMatrixLoc = GLES20.glGetUniformLocation(this.mProgramHandle, "uMVPMatrix");
        GLHelper.checkLocation(this.muMVPMatrixLoc, "uMVPMatrix");
        this.muTexMatrixLoc = GLES20.glGetUniformLocation(this.mProgramHandle, "uTexMatrix");
        initLocation((float[]) null, (float[]) null);
    }

    public void release() {
        GLES20.glDeleteProgram(this.mProgramHandle);
        this.mProgramHandle = -1;
    }

    public ProgramType getProgramType() {
        return this.mProgramType;
    }

    public int getProgramHandle() {
        return this.mProgramHandle;
    }

    public int createTextureObject() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLHelper.checkGlError("glGenTextures");
        int texId = textures[0];
        GLES20.glBindTexture(this.mTextureTarget, texId);
        GLHelper.checkGlError("glBindTexture " + texId);
        GLES20.glTexParameterf(this.mTextureTarget, 10241, 9728.0f);
        GLES20.glTexParameterf(this.mTextureTarget, 10240, 9729.0f);
        GLES20.glTexParameteri(this.mTextureTarget, 10242, 33071);
        GLES20.glTexParameteri(this.mTextureTarget, 10243, 33071);
        GLHelper.checkGlError("glTexParameter");
        return texId;
    }

    public void handleTouchEvent(MotionEvent ev) {
        synchronized (this.mSync) {
            if (ev.getAction() == 2) {
                if (!(this.mTexHeight == 0.0f || this.mTexWidth == 0.0f)) {
                    float[] fArr = this.mSummedTouchPosition;
                    fArr[0] = fArr[0] + (((ev.getX() - this.mLastTouchPosition[0]) * 2.0f) / this.mTexWidth);
                    float[] fArr2 = this.mSummedTouchPosition;
                    fArr2[1] = fArr2[1] + (((ev.getY() - this.mLastTouchPosition[1]) * 2.0f) / (-this.mTexHeight));
                    this.mLastTouchPosition[0] = ev.getX();
                    this.mLastTouchPosition[1] = ev.getY();
                }
            } else if (ev.getAction() == 0) {
                this.mLastTouchPosition[0] = ev.getX();
                this.mLastTouchPosition[1] = ev.getY();
            }
        }
    }

    public void setKernel(float[] values, float colorAdj) {
        if (values.length < 9) {
            throw new IllegalArgumentException("Kernel size is " + values.length + " vs. " + 9);
        }
        System.arraycopy(values, 0, this.mKernel, 0, 9);
        this.mColorAdjust = colorAdj;
    }

    public void setKernel2(float[] values) {
        boolean z = false;
        synchronized (this.mSync) {
            if (values != null) {
                if (values.length == 9) {
                    z = true;
                }
            }
            this.mHasKernel2 = z;
            if (this.mHasKernel2) {
                System.arraycopy(values, 0, this.mKernel, 9, 9);
            }
        }
    }

    public void setColorAdjust(float adjust) {
        synchronized (this.mSync) {
            this.mColorAdjust = adjust;
        }
    }

    public void setTexSize(int width, int height) {
        this.mTexHeight = (float) height;
        this.mTexWidth = (float) width;
        float rw = 1.0f / ((float) width);
        float rh = 1.0f / ((float) height);
        synchronized (this.mSync) {
            this.mTexOffset = new float[]{-rw, -rh, 0.0f, -rh, rw, -rh, -rw, 0.0f, 0.0f, 0.0f, rw, 0.0f, -rw, rh, 0.0f, rh, rw, rh};
        }
    }

    public void setFlags(int[] flags) {
        int i = 0;
        if (flags != null) {
            i = flags.length;
        }
        int n = Math.min(4, i);
        if (n > 0) {
            synchronized (this.mSync) {
                System.arraycopy(flags, 0, this.mFlags, 0, n);
            }
        }
    }

    public void setFlag(int index, int value) {
        if (index >= 0 && index < this.mFlags.length) {
            synchronized (this.mSync) {
                this.mFlags[index] = value;
            }
        }
    }

    public void draw(float[] mvpMatrix, int mvpMatrixOffset, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, int texMatrixOffset, FloatBuffer texBuffer, int textureId, int texStride) {
        GLHelper.checkGlError("draw start");
        GLES20.glUseProgram(this.mProgramHandle);
        GLHelper.checkGlError("glUseProgram");
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(this.mTextureTarget, textureId);
        GLHelper.checkGlError("glBindTexture");
        synchronized (this.mSync) {
            GLES20.glUniformMatrix4fv(this.muMVPMatrixLoc, 1, false, mvpMatrix, mvpMatrixOffset);
            GLHelper.checkGlError("glUniformMatrix4fv");
            if (this.muTexMatrixLoc >= 0) {
                GLES20.glUniformMatrix4fv(this.muTexMatrixLoc, 1, false, texMatrix, texMatrixOffset);
                GLHelper.checkGlError("glUniformMatrix4fv");
            }
            GLES20.glEnableVertexAttribArray(this.maPositionLoc);
            GLHelper.checkGlError("glEnableVertexAttribArray");
            GLES20.glVertexAttribPointer(this.maPositionLoc, coordsPerVertex, 5126, false, vertexStride, vertexBuffer);
            GLHelper.checkGlError("glVertexAttribPointer");
            GLES20.glEnableVertexAttribArray(this.maTextureCoordLoc);
            GLHelper.checkGlError("glEnableVertexAttribArray");
            GLES20.glVertexAttribPointer(this.maTextureCoordLoc, 2, 5126, false, texStride, texBuffer);
            GLHelper.checkGlError("glVertexAttribPointer");
            if (this.muKernelLoc >= 0) {
                if (!this.mHasKernel2) {
                    GLES20.glUniform1fv(this.muKernelLoc, 9, this.mKernel, 0);
                } else {
                    GLES20.glUniform1fv(this.muKernelLoc, 18, this.mKernel, 0);
                }
                GLHelper.checkGlError("set kernel");
            }
            if (this.muTexOffsetLoc >= 0 && this.mTexOffset != null) {
                GLES20.glUniform2fv(this.muTexOffsetLoc, 9, this.mTexOffset, 0);
            }
            if (this.muColorAdjustLoc >= 0) {
                GLES20.glUniform1f(this.muColorAdjustLoc, this.mColorAdjust);
            }
            if (this.muTouchPositionLoc >= 0) {
                GLES20.glUniform2fv(this.muTouchPositionLoc, 1, this.mSummedTouchPosition, 0);
            }
            if (this.muFlagsLoc >= 0) {
                GLES20.glUniform1iv(this.muFlagsLoc, 4, this.mFlags, 0);
            }
        }
        internal_draw(firstVertex, vertexCount);
        GLES20.glDisableVertexAttribArray(this.maPositionLoc);
        GLES20.glDisableVertexAttribArray(this.maTextureCoordLoc);
        GLES20.glBindTexture(this.mTextureTarget, 0);
        GLES20.glUseProgram(0);
    }

    /* access modifiers changed from: protected */
    public void initLocation(float[] kernel, float[] kernel2) {
        this.muKernelLoc = GLES20.glGetUniformLocation(this.mProgramHandle, "uKernel");
        if (this.muKernelLoc < 0) {
            this.muKernelLoc = -1;
            this.muTexOffsetLoc = -1;
        } else {
            this.muTexOffsetLoc = GLES20.glGetUniformLocation(this.mProgramHandle, "uTexOffset");
            if (this.muTexOffsetLoc < 0) {
                this.muTexOffsetLoc = -1;
            }

            setKernel(kernel, 0.0f);
            setTexSize(256, 256);
        }
        if (kernel2 != null) {
            setKernel2(kernel2);
        }
        this.muColorAdjustLoc = GLES20.glGetUniformLocation(this.mProgramHandle, "uColorAdjust");
        if (this.muColorAdjustLoc < 0) {
            this.muColorAdjustLoc = -1;
        }
        this.muTouchPositionLoc = GLES20.glGetUniformLocation(this.mProgramHandle, "uPosition");
        if (this.muTouchPositionLoc < 0) {
            this.muTouchPositionLoc = -1;
        }
        this.muFlagsLoc = GLES20.glGetUniformLocation(this.mProgramHandle, "uFlags");
        if (this.muFlagsLoc < 0) {
            this.muFlagsLoc = -1;
        }
    }

    /* access modifiers changed from: protected */
    public void internal_draw(int firstVertex, int vertexCount) {
        GLES20.glDrawArrays(5, firstVertex, vertexCount);
        GLHelper.checkGlError("glDrawArrays");
    }
}

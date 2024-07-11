package com.serenegiant.usb.glutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.text.TextUtils;
import java.io.IOException;
import java.nio.Buffer;

public class GLTexture implements ITexture {
    int mImageHeight;
    int mImageWidth;
    int mTexHeight;
    final float[] mTexMatrix = new float[16];
    int mTexWidth;
    int mTextureId;
//    int mTextureTarget = ShaderConst.GL_TEXTURE_2D;
    int mTextureUnit = 33984;

    public GLTexture(int width, int height, int filter_param) {
        int w = 32;
        while (w < width) {
            w <<= 1;
        }
        int h = 32;
        while (h < height) {
            h <<= 1;
        }
        if (!(this.mTexWidth == w && this.mTexHeight == h)) {
            this.mTexWidth = w;
            this.mTexHeight = h;
        }
//        this.mTextureId = GLHelper.initTex(this.mTextureTarget, filter_param);
//        GLES20.glTexImage2D(this.mTextureTarget, 0, 6408, this.mTexWidth, this.mTexHeight, 0, 6408, 5121, (Buffer) null);
        Matrix.setIdentityM(this.mTexMatrix, 0);
        this.mTexMatrix[0] = ((float) width) / ((float) this.mTexWidth);
        this.mTexMatrix[5] = ((float) height) / ((float) this.mTexHeight);
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        release();
        super.finalize();
    }

    public void release() {
        if (this.mTextureId > 0) {
            GLHelper.deleteTex(this.mTextureId);
            this.mTextureId = 0;
        }
    }

    @Override
    public void unbind() {

    }

    public void bind() {
        GLES20.glActiveTexture(this.mTextureUnit);
//        GLES20.glBindTexture(this.mTextureTarget, this.mTextureId);
    }

//    public void unbind() {
//        GLES20.glBindTexture(this.mTextureTarget, 0);
//    }
//
//    public int getTexTarget() {
//        return this.mTextureTarget;
//    }

    public int getTexture() {
        return this.mTextureId;
    }

    public float[] getTexMatrix() {
        return this.mTexMatrix;
    }

    @Override
    public int getTexTarget() {
        return 0;
    }

    public void getTexMatrix(float[] matrix, int offset) {
        System.arraycopy(this.mTexMatrix, 0, matrix, offset, this.mTexMatrix.length);
    }

    public int getTexWidth() {
        return this.mTexWidth;
    }

    public int getTexHeight() {
        return this.mTexHeight;
    }

    public void loadTexture(String filePath) throws NullPointerException, IOException {
        if (TextUtils.isEmpty(filePath)) {
            throw new NullPointerException("image file path should not be a null");
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        int inSampleSize = 1;
        if (imageHeight > this.mTexHeight || imageWidth > this.mTexWidth) {
            if (imageWidth > imageHeight) {
                inSampleSize = (int) Math.ceil((double) (((float) imageHeight) / ((float) this.mTexHeight)));
            } else {
                inSampleSize = (int) Math.ceil((double) (((float) imageWidth) / ((float) this.mTexWidth)));
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        this.mImageWidth = bitmap.getWidth();
        this.mImageHeight = bitmap.getHeight();
        Bitmap texture = Bitmap.createBitmap(this.mTexWidth, this.mTexHeight, Bitmap.Config.ARGB_8888);
        new Canvas(texture).drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        bitmap.recycle();
        Matrix.setIdentityM(this.mTexMatrix, 0);
        this.mTexMatrix[0] = ((float) this.mImageWidth) / ((float) this.mTexWidth);
        this.mTexMatrix[5] = ((float) this.mImageHeight) / ((float) this.mTexHeight);
        bind();
//        GLUtils.texImage2D(this.mTextureTarget, 0, texture, 0);
        unbind();
        texture.recycle();
    }
}

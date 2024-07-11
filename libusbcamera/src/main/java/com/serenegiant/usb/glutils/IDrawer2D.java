package com.serenegiant.usb.glutils;

public interface IDrawer2D {
    void draw(int i, float[] fArr, int i2);

    void draw(ITexture iTexture);

    void draw(TextureOffscreen textureOffscreen);

    void getMvpMatrix(float[] fArr, int i);

    float[] getMvpMatrix();

    void release();

    IDrawer2D setMvpMatrix(float[] fArr, int i);
}

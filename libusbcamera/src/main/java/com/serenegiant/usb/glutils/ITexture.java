package com.serenegiant.usb.glutils;

import java.io.IOException;

public interface ITexture {
    void bind();

    int getTexHeight();

    void getTexMatrix(float[] fArr, int i);

    float[] getTexMatrix();

    int getTexTarget();

    int getTexWidth();

    int getTexture();

    void loadTexture(String str) throws NullPointerException, IOException;

    void release();

    void unbind();
}

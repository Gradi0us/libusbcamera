package com.serenegiant.usb.glutils;

public interface IDrawer2dES2 extends IDrawer2D {
    int glGetAttribLocation(String str);

    int glGetUniformLocation(String str);

    void glUseProgram();
}

package com.example.goddice.firstopenglproject;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by goddice on 9/27/15.
 */
public class RippleRenderer implements GLSurfaceView.Renderer{

    public RippleRenderer(Context context)
    {
        ;
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        ;
    }

    public void onDrawFrame(GL10 glUnused)
    {
        ;
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        mWidth = width;
        mHeight = height;
    }


    private int mWidth;
    private int mHeight;
}

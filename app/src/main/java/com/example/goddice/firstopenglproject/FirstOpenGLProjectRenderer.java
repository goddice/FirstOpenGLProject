package com.example.goddice.firstopenglproject;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Goddice on 9/24/2015.
 */
public class FirstOpenGLProjectRenderer implements GLSurfaceView.Renderer{
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 Unused)
    {
        glClear(GL_COLOR_BUFFER_BIT);
    }
}

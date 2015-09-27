package com.example.goddice.firstopenglproject;

import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import android.opengl.GLES20;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;
    private SceneRenderer mRenderer;
	
	private float mPreviousY;
    private float mPreviousX;
    
    int textureId;
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new SceneRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;
            float dx = x - mPreviousX;
        }
        mPreviousY = y;
        mPreviousX = x;
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
        Rectangle texRect;
    	
        public void onDrawFrame(GL10 gl) 
        {
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            texRect.drawSelf(textureId);             
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
        	GLES20.glViewport(0, 0, width, height);
            
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.5f,0.5f,0.5f, 1.0f);  

            texRect=new Rectangle(MySurfaceView.this);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            initTexture();
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }
    }
	
	public void initTexture()//textureId
	{
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
				1,
				textures,
				0
		);    
		textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        


        try
        {
            InputStream is = this.getResources().getAssets().open("res/funny.jpg");
            Bitmap bitmapTmp;
            try
            {
                bitmapTmp = BitmapFactory.decodeStream(is);
            }
            finally
            {
                try
                {
                    is.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            GLUtils.texImage2D
                    (
                            GLES20.GL_TEXTURE_2D,
                            0,
                            bitmapTmp,
                            0
                    );
            bitmapTmp.recycle();
        }catch (Exception e)
        {
            Toast.makeText(this.getContext(), "Cannot open file", Toast.LENGTH_SHORT).show();
        }

	}
}

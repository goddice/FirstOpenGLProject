package com.example.goddice.firstopenglproject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import java.io.InputStream;

import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.opengl.GLES20;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.WindowManager;
import android.widget.Toast;

class MySurfaceView extends GLSurfaceView 
{
    public SceneRenderer mRenderer;

    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                mRenderer.dropAtFinger(e, 0.03f, 0.01f); break;
            case MotionEvent.ACTION_DOWN:
                mRenderer.dropAtFinger(e, 0.09f, 0.14f); break;
        }
        return true;
    }

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new SceneRenderer(context);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

	public class SceneRenderer implements GLSurfaceView.Renderer
    {
        Context mContext;
        public final String TAG = "GVCGVC";
        public int resolution = 256;
        public float perturbance = 0.03f;
        public float textureDelte[] = {1 / (float) resolution, 1 / (float) resolution};
        public float [] renderProgram_topLeft = new float[2];
        public float [] renderProgram_bottomRight = new float[2];
        public float [] renderProgram_containRatio = new float[2];

        public int [] texture = new int[2];
        public int backgroundTexture;
        public int backgroundWidth;
        public int backgroundHeight;

        public int [] framebuffer = new int[2];
        public int quad;

        public int dropProgram;
        public int [] updateProgram = new int[2];
        public int renderProgram;

        public int [] updateProgram_delta_handler = new int[2];
        public int [] updateProgram_texture_handler = new int[2];
        public int renderProgram_perturbance_handler;
        public int renderProgram_topLeft_handler;
        public int renderProgram_bottomRight_handler;
        public int renderProgram_containerRatio_handler;
        public int renderProgram_samplerBackground_handler;
        public int renderProgram_samplerRipples_handler;
        public int dropProgram_center_handler;
        public int dropProgram_radius_handler;
        public int dropProgram_strength_handler;
        public int dropProgram_texture_handler;
        public float xCoord;
        public boolean inDropping = false;
        float dropPosition[] = {0.0f, 0.0f};
        float mRadius = 0.0f;
        float mStrength = 0.0f;

        public SceneRenderer(Context context)
        {
            mContext = context;
        }

        public boolean isPowerOfTwo(int x)
        {
            return (x & (x - 1)) == 0;
        }

        public void initTextures() throws Exception// Init textures
        {
            InputStream is = mContext.getResources().getAssets().open("res/girl.jpg");
            Bitmap bitmapTmp;
            bitmapTmp = BitmapFactory.decodeStream(is);

            int wrapping = (isPowerOfTwo(bitmapTmp.getWidth()) && isPowerOfTwo(bitmapTmp.getHeight())) ? GLES20.GL_REPEAT : GLES20.GL_CLAMP_TO_EDGE;

            backgroundWidth = bitmapTmp.getWidth();
            backgroundHeight = bitmapTmp.getHeight();

            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            int width1 = display.getWidth();
            int height1 = display.getHeight();
            backgroundHeight = height1;
            backgroundWidth = width1;

            int [] mTexture = new int[1];
            GLES20.glGenTextures(1, mTexture, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[0]);
            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wrapping);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapping);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTmp, 0);
            bitmapTmp.recycle();

            backgroundTexture = mTexture[0];

            for (int i=0; i<2; ++i)
            {
                GLES20.glGenTextures(1, texture, i);
                GLES20.glGenFramebuffers(1, framebuffer, i);

                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer[i]);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[i]);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, resolution, resolution, 0, GLES20.GL_RGBA, GLES20.GL_FLOAT, null);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture[i], 0);

                //GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }


            GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);

            // Init GL Stuff
            int [] mBuffer = new int[1];
            GLES20.glGenBuffers(1, mBuffer, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBuffer[0]);
            float vertexArray[] = {1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
            FloatBuffer fb = ByteBuffer.allocateDirect(4 * vertexArray.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
            fb.put(vertexArray);
            fb.position(0);
            GLES20.glEnableVertexAttribArray(0);
            GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 0, 0);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexArray.length * 4, fb, GLES20.GL_STATIC_DRAW);
            quad = mBuffer[0];

            initShaders();
        }

        public void initShaders()
        {
            dropProgram = ShaderUtil.createProgram(ShaderUtil.loadFromAssetsFile("vertexShader", mContext.getResources()), ShaderUtil.loadFromAssetsFile("fragShader_DropProgram", mContext.getResources()));
            updateProgram[0] = ShaderUtil.createProgram(ShaderUtil.loadFromAssetsFile("vertexShader", mContext.getResources()), ShaderUtil.loadFromAssetsFile("fragShader_updateProgram0", mContext.getResources()));
            updateProgram[1] = ShaderUtil.createProgram(ShaderUtil.loadFromAssetsFile("vertexShader", mContext.getResources()), ShaderUtil.loadFromAssetsFile("fragShader_updateProgram1", mContext.getResources()));
            renderProgram = ShaderUtil.createProgram(ShaderUtil.loadFromAssetsFile("vertexShader_renderProgram", mContext.getResources()), ShaderUtil.loadFromAssetsFile("fragShader_renderProgram", mContext.getResources()));

            updateProgram_delta_handler[0] = GLES20.glGetUniformLocation(updateProgram[0], "delta");
            updateProgram_delta_handler[1] = GLES20.glGetUniformLocation(updateProgram[1], "delta");
            updateProgram_texture_handler[0] = GLES20.glGetUniformLocation(updateProgram[0], "texture");
            updateProgram_texture_handler[1] = GLES20.glGetUniformLocation(updateProgram[1], "texture");

            renderProgram_perturbance_handler = GLES20.glGetUniformLocation(renderProgram, "perturbance");
            renderProgram_topLeft_handler = GLES20.glGetUniformLocation(renderProgram, "topLeft");
            renderProgram_bottomRight_handler = GLES20.glGetUniformLocation(renderProgram, "bottomRight");
            renderProgram_containerRatio_handler = GLES20.glGetUniformLocation(renderProgram, "containerRatio");
            renderProgram_samplerBackground_handler = GLES20.glGetUniformLocation(renderProgram, "samplerBackground");
            renderProgram_samplerRipples_handler = GLES20.glGetUniformLocation(renderProgram, "samplerRipples");

            dropProgram_center_handler = GLES20.glGetUniformLocation(dropProgram, "center");
            dropProgram_radius_handler = GLES20.glGetUniformLocation(dropProgram, "radius");
            dropProgram_strength_handler = GLES20.glGetUniformLocation(dropProgram, "strength");
            dropProgram_texture_handler = GLES20.glGetUniformLocation(dropProgram, "texture");

        }

        public void update()
        {
            updateTexture();
            render();
        }

        public void drawQuad()
        {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, quad);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        }

        public void render()
        {
            //Log.e(TAG, "rendering");

            computeTextureBoundaries();

            GLES20.glViewport(0, 0, backgroundWidth, backgroundHeight);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            GLES20.glUseProgram(renderProgram);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backgroundTexture);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

            GLES20.glUniform2fv(renderProgram_topLeft_handler, 1, renderProgram_topLeft, 0);
            GLES20.glUniform2fv(renderProgram_bottomRight_handler, 1, renderProgram_bottomRight, 0);
            GLES20.glUniform2fv(renderProgram_containerRatio_handler, 1, renderProgram_containRatio, 0);
            GLES20.glUniform1i(renderProgram_samplerBackground_handler, 0);
            GLES20.glUniform1i(renderProgram_samplerRipples_handler, 1);
//            Log.e(TAG, String.valueOf(renderProgram_perturbance_handler));
            GLES20.glUniform1f(renderProgram_perturbance_handler, perturbance);

            drawQuad();
            GLES20.glUseProgram(0);
        }

        public void updateTexture()
        {
            //computeTextureBoundaries();

            GLES20.glViewport(0, 0, resolution, resolution);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            for (int i=0; i<2; ++i) {
                GLES20.glUseProgram(updateProgram[i]);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer[i]);
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[1 - i]);
                GLES20.glUniform1i(updateProgram_texture_handler[i], 0);
                GLES20.glUniform2f(updateProgram_delta_handler[i], textureDelte[0], textureDelte[1]);

                drawQuad();
                GLES20.glUseProgram(0);
            }


            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }

        public void computeTextureBoundaries()
        {
            renderProgram_topLeft[0] = 0.0f;
            renderProgram_topLeft[1] = 0.0f;
            renderProgram_bottomRight[0] = 1.0f;
            renderProgram_bottomRight[1] = 1.0f;

            int maxSide = Math.max(backgroundHeight, backgroundWidth);
            renderProgram_containRatio[0] = (float)backgroundWidth / (float)maxSide;
            renderProgram_containRatio[1] = (float)backgroundHeight / (float)maxSide;
        }

        public void dropAtFinger(MotionEvent e, float radius, float strength)
        {
            //computeTextureBoundaries();
            //inDropping = true;

            int longestSide = Math.max(backgroundHeight, backgroundWidth);
            dropPosition[0] = (2 * e.getX() - (float)backgroundWidth) / (float)longestSide;
            dropPosition[1] = ((float)backgroundHeight - 2 * e.getY()) / (float) longestSide;
            mRadius = radius;
            mStrength = strength;


            //GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer[0]);
            //GLES20.glViewport(0, 0, resolution, resolution);

//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//
//            GLES20.glUseProgram(dropProgram);
//
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backgroundTexture);
//            GLES20.glUniform1i(dropProgram_texture_handler, 0);
//
//            GLES20.glUniform2f(dropProgram_center_handler, dropPosition[0], dropPosition[1]);
//            Log.e(TAG, "radius: " + String.valueOf(radius));
//            Log.e(TAG, "strength: " + String.valueOf(strength));
//            Log.e(TAG, "dropProgram_radius_handler: " + String.valueOf(dropProgram_radius_handler));
//            Log.e(TAG, "dropProgram_strength_handler: " + String.valueOf(dropProgram_strength_handler));
//            Log.e(TAG, "dropProgram_center_handler: " + String.valueOf(dropProgram_center_handler));
//            xCoord = dropPosition[0];
//            GLES20.glUniform1f(dropProgram_radius_handler, radius);
//            GLES20.glUniform1f(dropProgram_strength_handler, strength);
//
//            drawQuad();
//            GLES20.glUseProgram(0);
//
//
//            int t = framebuffer[0]; framebuffer[0] = framebuffer[1]; framebuffer[1] = t;
//            t = texture[0]; texture[0] = texture[1]; texture[1] = t;

            //GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            //GLES20.glUseProgram(0);
//            inDropping = true;
        }

        public void onDrawFrame(GL10 gl) 
        {
            if (mStrength > 0.0001)
            {
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer[0]);
                GLES20.glViewport(0, 0, resolution, resolution);

                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

                GLES20.glUseProgram(dropProgram);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[1]);
                GLES20.glUniform1i(dropProgram_texture_handler, 0);

                GLES20.glUniform2f(dropProgram_center_handler, dropPosition[0], dropPosition[1]);

                GLES20.glUniform1f(dropProgram_radius_handler, mRadius);
                GLES20.glUniform1f(dropProgram_strength_handler, mStrength);
                if (mRadius > 0) Log.e(TAG, String.valueOf(mRadius));
                if (mStrength > 0) Log.e(TAG, String.valueOf(mStrength));

                drawQuad();
                GLES20.glUseProgram(0);


                int t = framebuffer[0]; framebuffer[0] = framebuffer[1]; framebuffer[1] = t;
                t = texture[0]; texture[0] = texture[1]; texture[1] = t;

                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }
            //GLES20.glUseProgram(0);
            //inDropping = true;


            update();
            dropPosition[0] = 0.0f;
            dropPosition[1] = 0.0f;
            mRadius = 0.0f;
            mStrength = 0.0f;
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
        	GLES20.glViewport(0, 0, width, height);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            try
            {
                initTextures();
                Log.e(TAG, "Initialize successful");
            }
            catch (Exception e)
            {
                Toast.makeText(mContext, "Something is wrong.", Toast.LENGTH_SHORT);
            }
        }
    }

}

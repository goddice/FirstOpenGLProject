package com.example.goddice.firstopenglproject;
import static com.example.goddice.firstopenglproject.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;

public class Rectangle
{	
	int mDropProgram;
    int []mUpdateProgram = new int[2];
    int mRenderProgram;

    int maPositionHandle;
    int maTexCoorHandle;
    String mVertexShader;
    String mFragmentShader;
	
	FloatBuffer   mVertexBuffer;
	FloatBuffer   mTexCoorBuffer;
    int vCount=0;
    
    public Rectangle(MySurfaceView mv)
    {
    	initVertexData(mv);
    	initShader(mv);
    }

    public void initVertexData(MySurfaceView mv)
    {
        float w = mv.getWidth();
        float h = mv.getHeight();
        //float r = (float) w / h;
        float r = 1.0f;
        //MatrixState.setProject(-ratio, ratio, -1, 1, 1, 10);
        //float w = 1.0f;
        //float h = 1.0f;
        vCount=4;
        float vertices[]=new float[]
        {
                r, 1.0f, 0.0f,
                r, -1.0f, 0.0f,
                -r, 1.0f, 0.0f,
                -r, -1.0f, 0.0f
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float texCoor[]=new float[]
        {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);

    }

    public void initShader(MySurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
    }
    
    public void drawSelf(int texId)
    {
    	 GLES20.glUseProgram(mProgram);        

         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );
         GLES20.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  

         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

         GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vCount);
    }
}

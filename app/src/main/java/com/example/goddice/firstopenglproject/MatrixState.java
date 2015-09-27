package com.example.goddice.firstopenglproject;

import android.opengl.Matrix;

public class MatrixState 
{
	private static float[] mProjMatrix = new float[16];
    private static float[] mVMatrix = new float[16];
    private static float[] mMVPMatrix;
    static float[] mMMatrix=new float[16] ;
    
    
    
    public static void setInitStack()
    {
    	Matrix.setRotateM(mMMatrix, 0, 0, 1, 0, 0);
    }
    
    public static void transtate(float x,float y,float z)
    {
    	Matrix.translateM(mMMatrix, 0, x, y, z);
    }
    
    public static void rotate(float angle,float x,float y,float z)
    {
    	Matrix.rotateM(mMMatrix,0,angle,x,y,z);
    }
    
    
    //���������
    public static void setCamera
    (
    		float cx,
    		float cy,
    		float cz,
    		float tx,
    		float ty,
    		float tz,
    		float upx,
    		float upy,
    		float upz
    )
    {
    	Matrix.setLookAtM
        (
        		mVMatrix, 
        		0, 
        		cx,
        		cy,
        		cz,
        		tx,
        		ty,
        		tz,
        		upx,
        		upy,
        		upz
        );
    }

    public static void setProject
    (
    	float left,
    	float right,
    	float bottom,
    	float top,
    	float near,
    	float far
    )
    {
    	Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    public static float[] getFinalMatrix()
    {
    	mMVPMatrix=new float[16];
    	Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);        
        return mMVPMatrix;
    }
}

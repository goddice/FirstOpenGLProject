package com.example.goddice.firstopenglproject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class FirstOpenGLProjectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mGLSurfaceView = new MySurfaceView(this);
        if (detectOpenGLES20())
        {
            mGLSurfaceView.setEGLContextClientVersion(2);
            mGLSurfaceView.setRenderer(new RippleRenderer(this));
        }
        else
        {
            Log.e("Water Ripple", "OpenGL ES 2.0 not supported on device. Exiting...");
            finish();
        }
        setContentView(mGLSurfaceView);
        //mGLSurfaceView.requestFocus();
        //mGLSurfaceView.setFocusableInTouchMode(true);
    }

    private boolean detectOpenGLES20()
    {
        ActivityManager am =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x20000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        threadFlag=true;
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        threadFlag=false;
        mGLSurfaceView.onPause();
    }


    private MySurfaceView mGLSurfaceView;
    static boolean threadFlag;
}




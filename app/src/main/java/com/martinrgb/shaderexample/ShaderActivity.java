package com.martinrgb.shaderexample;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.martinrgb.shaderexample.shaderprograms.ShaderRenderer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ShaderActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private ShaderRenderer shaderRenderer;
    private boolean isRendering = false;
    private final String TAG = "ShaderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Delete Title Bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Delete Action Bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_shader);
        initGLSurfaceView(this);
    }

    private void initGLSurfaceView(Context context){
        glSurfaceView = findViewById(R.id.gl_surfaceview);
        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean suppoertsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        //# New Instance Renderer
        shaderRenderer = new ShaderRenderer(context,R.raw.testvert,R.raw.flow);
        if(suppoertsEs2){
            //请求 OpenGL ES 2.0的上下文（Context是一个场景,代表与操作系统的交互的一种过程。）
            glSurfaceView.setEGLContextClientVersion(2);
            //#分配Renderer
            glSurfaceView.setRenderer(shaderRenderer);
            isRendering = true;
            Log.d(TAG, "support OpenGL ES 2.0");
        } else {
            Log.d(TAG, "do not support OpenGLES 2.0");
            return;
        }
    }


    @Override
    public void onPause(){
        super.onPause();
        if(isRendering){
            glSurfaceView.onPause();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(isRendering){
            glSurfaceView.onResume();
        }
    }



}

package com.martinrgb.shaderexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class ShaderActivity extends AppCompatActivity {

    private ShaderSurfaceView shaderSurfaceView;
    private final String TAG = "ShaderActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // # delete Title Bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // # delete Action Bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_shader);

        initShaderView();
    }

    private void initShaderView() {
        shaderSurfaceView = findViewById(R.id.shader_surfaceview);
        shaderSurfaceView.setFrag( R.raw.simplefrag,1/2f,new int[]{R.drawable.test,R.drawable.test2,R.drawable.saturation_2_blurred});
    }

}

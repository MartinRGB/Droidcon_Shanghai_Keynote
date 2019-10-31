package com.martinrgb.shaderexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.martinrgb.shaderexample.seekbar.BubbleSeekBar;


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
        setRangeSeekbar();
    }

    private void initShaderView() {
        shaderSurfaceView = findViewById(R.id.shader_surfaceview);
        shaderSurfaceView.setFrag( R.raw.simplefrag,1,new int[]{R.drawable.texture});
    }

    private BubbleSeekBar mSeek;
    private void setRangeSeekbar(){
        mSeek = findViewById(R.id.bs_1);
        mSeek.setProgress(1.67f);

        mSeek.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                shaderSurfaceView.onSaturationInput(progressFloat);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });

    }

    private boolean isClosed = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            if(!isClosed){
                findViewById(R.id.borad).setVisibility(View.INVISIBLE);
            }
            else{
                findViewById(R.id.borad).setVisibility(View.VISIBLE);
            }
            isClosed = !isClosed;
        }
        return true;
    }

}

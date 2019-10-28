package com.martinrgb.shaderexample;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.martinrgb.shaderexample.renderer.ShaderRenderer;
import com.martinrgb.shaderexample.renderer.util.TextReader;
import com.martinrgb.shaderexample.rendereralternative.ShaderView;

public class ShaderActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private ShaderRenderer shaderRenderer;

    private ShaderView shaderView;
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
        //initGLSurfaceView(this);
        initShaderView();
    }

    private void initGLSurfaceView(Context context){
        glSurfaceView = findViewById(R.id.gl_surfaceview);
        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean suppoertsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        // New Renderer Instance
        shaderRenderer = new ShaderRenderer(context,R.raw.simplevert,R.raw.flow);
        if(suppoertsEs2){
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(shaderRenderer);
            isRendering = true;
            Log.d(TAG, "support OpenGL ES 2.0");
        } else {
            Log.d(TAG, "do not support OpenGLES 2.0");
            return;
        }
    }

    private void initShaderView() {
        shaderView = (ShaderView) findViewById(R.id.preview);
        shaderView.setFragmentShader( R.raw.flow,1/4f,new int[]{R.drawable.test,R.drawable.saturation_2_blurred});
        shaderView.getRenderer().setOnRendererListener(
                new com.martinrgb.shaderexample.rendereralternative.ShaderRenderer.OnRendererListener() {
                    @Override
                    public void onInfoLog(String error) {

                    }

                    @Override
                    public void onFramesPerSecond(int fps) {
                        Log.e("Fps",String.valueOf(fps));
                    }
        });

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

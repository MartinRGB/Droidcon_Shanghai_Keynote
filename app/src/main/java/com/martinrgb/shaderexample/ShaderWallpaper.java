package com.martinrgb.shaderexample;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.martinrgb.shaderexample.shaderprograms.ShaderRenderer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by MartinRGB on 2017/2/21.
 */

public class ShaderWallpaper extends WallpaperService {

    //###################### Setting ######################
    private String LOCAL_VERT = "testvert.glsl";
    private String LOCAL_FRAG = "flow.glsl";

    private InputStream finalVert;
    private InputStream finalFrag;

    public static void setToWallPaper(Context context) {

        WallpaperUtil.setToWallPaper(context,
                "com.martinrgb.shaderexample.ShaderWallpaper",true);

    }


    @Override
    public Engine onCreateEngine(){
        return new GLEngine();
    }


    public class GLEngine extends Engine{
        private static final String TAG = "GLEngine";
        private WallpaperGLSurfaceView glSurfaceView;
        private ShaderRenderer mCanvasRenderer;
        private boolean rendererSet;


        //###################Life Cycle###################
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "onCreate(" + surfaceHolder + ")");



            //#实例化
            glSurfaceView = new WallpaperGLSurfaceView(ShaderWallpaper.this);

            //#检测是否支持2.0
            ActivityManager activityManager =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo configurationInfo = activityManager
                    .getDeviceConfigurationInfo();

            final boolean supportsEs2 =
                    configurationInfo.reqGlEsVersion >= 0x20000
                            // Check for emulator.
                            || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                            && (Build.FINGERPRINT.startsWith("generic")
                            || Build.FINGERPRINT.startsWith("unknown")
                            || Build.MODEL.contains("google_sdk")
                            || Build.MODEL.contains("Emulator")
                            || Build.MODEL.contains("Android SDK built for x86")));

            try {

                if(MainActivity.vertName == null){

                    InputStream mInputStream = getApplicationContext().getResources().getAssets().open(LOCAL_VERT);
                    finalVert = mInputStream;
                }
                else {
                    File shaderFile =  new File(MainActivity.vertPath, MainActivity.vertName);
                    final int readLimit = 16 * 1024;
                    if(shaderFile != null){
                        InputStream mInputStream =  new BufferedInputStream(new FileInputStream(shaderFile), readLimit);
                        mInputStream.mark(readLimit);
                        finalVert = mInputStream;
                        Log.w(TAG, "GIF image is not available!");
                    }
                }

            }catch(IOException e){
                Log.d("GIF", "Could not load vert");
            }

            try {

                if(MainActivity.fragName == null){

                    InputStream mInputStream = getApplicationContext().getResources().getAssets().open(LOCAL_FRAG);
                    finalFrag = mInputStream;
                }
                else {
                    File shaderFile =  new File(MainActivity.fragPath, MainActivity.fragName);
                    final int readLimit = 16 * 1024;
                    if(shaderFile != null){
                        InputStream mInputStream =  new BufferedInputStream(new FileInputStream(shaderFile), readLimit);
                        mInputStream.mark(readLimit);
                        finalFrag = mInputStream;
                        Log.w(TAG, "Shader file is not available!");
                    }
                }

            }catch(IOException e){
                Log.d("Shader", "Could not load vert");
            }



            mCanvasRenderer = new ShaderRenderer(ShaderWallpaper.this,finalVert,finalFrag);




            //#如果支持，设置渲染
            if (supportsEs2) {
                glSurfaceView.setEGLContextClientVersion(2);

                //防止切换主屏后，效果被切断，保留上下文，不必重新加载
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    glSurfaceView.setPreserveEGLContextOnPause(true);
                }
                glSurfaceView.setRenderer(mCanvasRenderer);
                rendererSet = true;
            } else {
                Log.d(TAG, "This device does not support OpenGL ES 2.0.");
                return;
            }


        }



        //#Destory
        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d(TAG, "onDestroy()");
            glSurfaceView.onWallpaperDestroy();
            glSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {

                    mCanvasRenderer.mSpringSensorX.setCurrentValue(0.f);
                    mCanvasRenderer.mSpringSensorY.setCurrentValue(0.f);
                }
            });

        }

        //#Visibile改变
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.d(TAG, "onVisibilityChanged(" + visible + ")");
            if (rendererSet) {
                if (visible) {
                    glSurfaceView.onResume();

                } else {
                    glSurfaceView.onPause();
                }
            }
        }




        //###################Touch Event###################
        @Override
        public void onTouchEvent(MotionEvent event){
            {
                if(event !=null){
                    //*把坐标系转换成OpenGL坐标系(后期还要转换成GLSL内部归一化坐标系)
                    //u_mouse
                    final float normalizedX = (float)event.getX() / (float) mCanvasRenderer.screenWidth;
                    final float normalizedY = -(float)event.getY() / (float) mCanvasRenderer.screenHeight+ 1;

                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            glSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    mCanvasRenderer.handleTouchDown(-normalizedX,normalizedY);
                                }
                            });
                            break;

                        case MotionEvent.ACTION_UP:
                            glSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {

                                    mCanvasRenderer.handleTouchUp(-normalizedX,normalizedY);
                                }
                            });
                            break;

                        case MotionEvent.ACTION_MOVE:
                            glSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    //mCanvasRenderer.handleTouchDrag(normalizedX,normalizedY);
                                    mCanvasRenderer.handleTouchDrag(-normalizedX,normalizedY);
                                }
                            });
                            break;
                        default:
                    }

                }
            }


        }

        private float mXOffset;
        private float mYOffset;

        @Override
        public void onOffsetsChanged(
                float xOffset,
                float yOffset,
                float xStep,
                float yStep,
                int xPixels,
                int yPixels) {
        }



        ////###################GL Surface Class###################
        class WallpaperGLSurfaceView extends GLSurfaceView {
            private static final String TAG = "WallpaperGLSurfaceView";
            WallpaperGLSurfaceView(Context context) {
                super(context);

                Log.d(TAG, "WallpaperGLSurfaceView(" + context + ")");
            }
            @Override
            public SurfaceHolder getHolder() {
                Log.d(TAG, "getHolder(): returning " + getSurfaceHolder());
                return getSurfaceHolder();
            }

            public void onWallpaperDestroy() {
                Log.d(TAG, "onWallpaperDestroy()");
                super.onDetachedFromWindow();
            }
        }
    }


}



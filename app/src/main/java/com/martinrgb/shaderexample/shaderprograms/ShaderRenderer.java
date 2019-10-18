package com.martinrgb.shaderexample.shaderprograms;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.martinrgb.shaderexample.R;
import com.martinrgb.shaderexample.shaderprograms.shaderutil.TextureHelper;

import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by MartinRGB on 2017/2/26.
 */

public class ShaderRenderer  implements GLSurfaceView.Renderer{
    //Constructor
    private final Context context;
    private SimpleShaderProgram simpleShaderProgram;
    private static final String TAG = "CanvasRenderer";
    private boolean logOn = true;

    public InputStream mVert;
    public InputStream mFrag;

    public ShaderRenderer(Context context,InputStream vert,InputStream frag) {
        this.context = context;
        setSpringSystem();
        mVert = vert;
        mFrag = frag;
    }

    //###################Init Render###################
    public int screenWidth;
    public int screenHeight;
    private int wallPaperTexture;
    private int noiseTexture;
    private long globalStartTime;
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        //前后遮挡图片
        //GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //透明图片
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //GLES20.glEnable(GLES20.GL_CULL_FACE);
        //GLES20.glCullFace(GLES20.GL_BACK);
        simpleShaderProgram = new SimpleShaderProgram(context,mVert,mFrag);
        wallPaperTexture = TextureHelper.loadTexture(context, R.drawable.saturation_2_blurred);
        globalStartTime = System.nanoTime();
    }
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height){
        GLES20.glViewport(0,0,width,height);
        screenWidth = width;
        screenHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl10){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


        float currentTime = (float) (System.nanoTime() - globalStartTime)/1000000000f;
        //getScreenOnValue(),

        //在内部完成了 vertex和tex array的内存分配和指向，以及程序的数据绑定和使用
//        simpleShaderProgram.setUniforms(
//                screenWidth,screenHeight,
//                (float) mSpringX.getCurrentValue(),
//                (float) mSpringY.getCurrentValue(),
//                (float)mSpringSensorX.getCurrentValue(),
//                (float)mSpringSensorY.getCurrentValue(),
//                (float)mSpringSensorZ.getCurrentValue(),
//                (float)mSpringSensorAX.getCurrentValue(),(float)mSpringSensorAY.getCurrentValue(),
//                getScreenOnValue(),
//                wallPaperTexture,
//                (float)mSpringTotalAlpha.getCurrentValue(),
//                (float)mSpringTexAlpha.getCurrentValue(),
//                mOrientation,myOffsetX,myOffsetY,currentTime
//                );

        simpleShaderProgram.setUniforms(
                screenWidth,
                screenHeight,
                wallPaperTexture,
                currentTime
        );

        if(logOn){
            logFrameRate();
        }else {
        }
    }

    //###################Touch Event###################
    private boolean hasPressed = false;
    private boolean hasDraged = false;
    private float prevY;
    private float prevX;
    private float vectorY;
    private float vectorX;
    //写一个无限滚
    //private float prevVectorY;
    public void handleTouchDown(float normalizedX,float normalizedY){
        Log.e(TAG,"Down");

        //Spring
        //mSpringX.setEndValue(normalizedX);
        //mSpringY.setEndValue(normalizedY);

        mSpringSensorZ.setEndValue(0.2);
        mSpringSensorAX.setEndValue(normalizedX);
        mSpringSensorAY.setEndValue(normalizedY);
        prevY = normalizedY;
        prevX = normalizedX;

        hasPressed = true;
    }

    public void handleTouchUp(float normalizedX,float normalizedY){
        Log.e(TAG,"Up");
        hasPressed = false;
        hasDraged = false;
        //prevVectorY = vectorY;

        if(vectorY>0.25){
            mOrientation = 0;
        }

        mSpringSensorZ.setEndValue(0.);
        mSpringSensorAX.setEndValue(normalizedX+vectorX);
        mSpringSensorAY.setEndValue(normalizedY+vectorY);

        mSpringX.setEndValue(0);
        mSpringY.setEndValue(0);

    }

    private float mMouseYValue;
    private float mMouseXValue;
    public void handleTouchDrag(float normalizedX,float normalizedY){


        if(hasPressed){
            hasDraged = true;
            //Log.e(TAG,"Drag");
            Log.e("x 的变化量",String.valueOf(vectorX));
            vectorY = (normalizedY - prevY);
            vectorX = (normalizedX - prevX) ;

            if(vectorY <0.){

                mMouseYValue = (float) -(-Math.pow(Math.E,-6.f*-vectorY) + 1.f)/2.5f;
            }
            else {
                mMouseYValue = (float) (-Math.pow(Math.E,-6.f*vectorY) + 1.f)/2.5f;
            }

            if(vectorX <0.){

                mMouseXValue = (float) -(-Math.pow(Math.E,-6.f*-vectorX) + 1.f)/2.5f;
            }else {
                mMouseXValue = (float) (-Math.pow(Math.E,-6.f*vectorX) + 1.f)/2.5f;
            }

            mSpringX.setEndValue(vectorX);
            mSpringY.setEndValue(vectorY);
            mOrientation = 1;


            mSpringScreenOn.setSpringConfig(gestureDragConfig);
            //Log.e("ST - X",String.valueOf(normalizedX/1080));
            //Log.e("ST - Y",String.valueOf(normalizedY/1920));
        }


    }

    private float myOffsetX;
    private float myOffsetY;

    public void handleOffset(float oX,float oY){
        myOffsetX = oX;
        myOffsetY = oY;
    }


    //###################Sensor Event###################
    private float mSensorX;
    private float mSensorY;
    private float mSensorZ;
    private float mSensorA;
    public void handleSensorEvent(float sensorX,float sensorY,float sensorZ,float sensorAccelX,float sensorAccelY){
        mSpringSensorX.setEndValue(sensorX);
        mSpringSensorY.setEndValue(sensorY);
        //mSpringSensorZ.setEndValue(sensorZ);
        //mSpringSensorAX.setEndValue(sensorAccelX);
        //mSpringSensorAY.setEndValue(sensorAccelY);

//        mSensorX= sensorX;
//        mSensorY = sensorY;
//        mSensorZ = sensorZ;
//        mSensorA = sensorAccel;

        //Log.e("SensorValueX",String.valueOf(sensorX));
        //Log.e("SensorValueY",String.valueOf(sensorY));
    }


    //###################Gesture Control Center Event###################
    public void handleScreenOnOff(float screenValue){

        mSpringScreenOn.setEndValue(screenValue);


    }


    public int mOrientation;

    //
    private float uYValue;
    public float getScreenOnValue(){
        if(!hasDraged){
            //mSpringScreenOn.setEndValue(1);
            uYValue = (float) mSpringScreenOn.getCurrentValue();

        }

        else if(hasDraged) {

            //分段函数的对数函数
            if(vectorY >0.){
                //uYValue = (float)( 1.-Math.log((vectorY/4.+vectorY)+1.));
                //uYValue = (float)(1. - (Math.exp(1/(-vectorY)))/1.5);

                //uYValue = (float) Math.pow((double)Math.E,-6.*(double)vectorY)/5.f + .9f;
                uYValue = (float) Math.pow((double)Math.E,-2.*(double)vectorY/5.+1.5)/20.f + .774f;
            }
            else if(vectorY <0.) {
                //uYValue =(float)(1.+Math.log(-vectorY/(10.-vectorY)+1.));
                //uYValue =(float)(1.+Math.exp(1/(vectorY))/3.);

                uYValue = (float) -Math.pow((double)Math.E,(double)vectorY/18.) + 2.f;
            }
//            else if (vectorY == 0){
//                uYValue = 1;
//            }

            //设置mSpring的值，方便松手后弹性复原

            mSpringScreenOn.setCurrentValue(uYValue);

        }

        return uYValue;
    }

    //###################Spring Animation###################Springsytem
    private static final SpringConfig mconfig = SpringConfig.fromOrigamiTensionAndFriction(120, 20); //60
    public static final SpringConfig sensorconfig = SpringConfig.fromOrigamiTensionAndFriction(5, 30); // 5 20
    public static final SpringConfig sensorconfig2 = SpringConfig.fromOrigamiTensionAndFriction(20, 20); // 5 20
    private static final SpringConfig gestureDragConfig = SpringConfig.fromOrigamiTensionAndFriction(100,35);
    public static final SpringConfig gestureUpConfig = SpringConfig.fromOrigamiTensionAndFriction(50,26); // 16 10 20 16
    private static final SpringConfig sensortotalAlpha = SpringConfig.fromOrigamiTensionAndFriction(60,20);
    private static final SpringConfig sensorTexAlpha = SpringConfig.fromOrigamiTensionAndFriction(20, 20);
    private static final SpringConfig sensorconfiglight = SpringConfig.fromOrigamiTensionAndFriction(120, 20); //60
    private SpringSystem mSpringSystem;
    public Spring mSpringX;
    public Spring mSpringY;
    public Spring mSpringSensorX;
    public Spring mSpringSensorY;
    public Spring mSpringSensorZ;
    public Spring mSpringSensorAX;
    public Spring mSpringSensorAY;
    public Spring mSpringScreenOn;
    public Spring mSpringTotalAlpha;
    public Spring mSpringTexAlpha;
    public Spring mSpringLightAlpha;


    public interface SensorChangeListener
    {
        public void shouldAddSensor(boolean Boo);
    }
    //实例化一个监听器的数值为空
    private SensorChangeListener mSensorChangeListener = null;

    //添加监听器注册方法
    public void setSensorChangeListener(SensorChangeListener sensorChangeListener)
    {
        mSensorChangeListener = sensorChangeListener;
    }


    private void setSpringSystem() {
        mSpringSystem = SpringSystem.create();

        mSpringX = mSpringSystem.createSpring();
        mSpringX.setSpringConfig(mconfig);
        mSpringY = mSpringSystem.createSpring();
        mSpringY.setSpringConfig(mconfig);

        mSpringLightAlpha = mSpringSystem.createSpring();
        mSpringLightAlpha.setSpringConfig(mconfig);

        mSpringSensorX = mSpringSystem.createSpring();
        mSpringSensorX.setSpringConfig(sensorconfig);
        mSpringSensorY = mSpringSystem.createSpring();
        mSpringSensorY.setSpringConfig(sensorconfig);
        mSpringSensorZ = mSpringSystem.createSpring();
        mSpringSensorZ.setSpringConfig(mconfig);

        mSpringSensorAX = mSpringSystem.createSpring();
        mSpringSensorAX.setSpringConfig(sensorconfiglight);
        mSpringSensorAY = mSpringSystem.createSpring();
        mSpringSensorAY.setSpringConfig(sensorconfiglight);

        mSpringScreenOn = mSpringSystem.createSpring();
        mSpringScreenOn.setSpringConfig(gestureUpConfig);
        mSpringScreenOn.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) mSpring.getCurrentValue();
            }

            public void onSpringAtRest(Spring mSpring){
                if(mSpring.getCurrentValue() ==1 && !hasDraged){
                    //mSensorChangeListener.shouldAddSensor(true);
                }
                else if(mSpring.getCurrentValue() ==0 && !hasDraged){
                    //mSensorChangeListener.shouldAddSensor(false);
                }
            }
        });

        mSpringTotalAlpha = mSpringSystem.createSpring();
        mSpringTotalAlpha.setCurrentValue(1);
        mSpringTotalAlpha.setSpringConfig(sensortotalAlpha);

        mSpringTexAlpha = mSpringSystem.createSpring();
        mSpringTexAlpha.setCurrentValue(0);
        mSpringTexAlpha.setSpringConfig(sensorTexAlpha);

    }



    //###################Utils###################Clamp Value
    private float clamp(float value,float min,float max){
        return Math.min(max,Math.max(value,min));
    }

    //###################Utils################### FPS Detector;
    private long startTimeMs;
    private int frameCount;

    private void logFrameRate(){
        long elapsedRealtimeMs = SystemClock.elapsedRealtime();
        double elapsedSeconds = (elapsedRealtimeMs - startTimeMs)/1000.0;

        if(elapsedSeconds >= 1.0){
            Log.v(TAG,frameCount/elapsedSeconds + "fps");
            startTimeMs = SystemClock.elapsedRealtime();
            frameCount = 0;
        }
        frameCount ++;
    }


}

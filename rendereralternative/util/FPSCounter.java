package com.martinrgb.shaderexample.rendereralternative.util;

import android.os.SystemClock;
import android.util.Log;

import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;

/**
 * Created by MartinRGB on 2017/2/17.
 */

public class FPSCounter {
    private static long startTimeMs;
    private static int frameCount;

    public static void logFrameRate(){
        final long elapsedRealtimeMs = SystemClock.elapsedRealtime();
        final double elapsedSeconds = (elapsedRealtimeMs - startTimeMs)/1000.0;

        if(elapsedSeconds >= 1.0){
            Log.v("Current FPS is ",frameCount/elapsedSeconds + "fps");
            startTimeMs = SystemClock.elapsedRealtime();
            frameCount = 0;
        }
        frameCount ++;
    }
}

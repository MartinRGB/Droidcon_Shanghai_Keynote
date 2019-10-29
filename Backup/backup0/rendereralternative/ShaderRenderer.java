package com.martinrgb.shaderexample.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.martinrgb.shaderexample.R;
import com.martinrgb.shaderexample.renderer.util.FPSCounter;
import com.martinrgb.shaderexample.renderer.util.LoggerConfig;
import com.martinrgb.shaderexample.renderer.program.TextureHelper;

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

    public InputStream mVert;
    public InputStream mFrag;

    public int mVertId;
    public int mFragId;

    public ShaderRenderer(Context context,int vert,int frag) {
        this.context = context;
        mVertId = vert;
        mFragId = frag;
    }

    public ShaderRenderer(Context context,InputStream vert,InputStream frag) {
        this.context = context;
        mVert = vert;
        mFrag = frag;
    }

    //###################Init Render###################
    public int screenWidth;
    public int screenHeight;
    private int texture;
    private long globalStartTime;
    private final float resolution[] = new float[]{0, 0};
    private final float surfaceResolution[] = new float[]{0, 0};
    private float quality = (float) 1/4;

    public void setQuality(float quality) {
        this.quality = quality;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        simpleShaderProgram = new SimpleShaderProgram(context,mVertId,mFragId);
        texture = TextureHelper.loadTexture(context, R.drawable.saturation_2_blurred);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height){

        globalStartTime = System.nanoTime();

//        surfaceResolution[0] = width;
//        surfaceResolution[1] = height;
//
//
//
////        if (w != resolution[0] || h != resolution[1]) {
////            deleteTargets();
////        }

        resolution[0] = width;
        resolution[1] = height;
    }

    @Override
    public void onDrawFrame(GL10 gl10){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        float currentTime = (float) (System.nanoTime() - globalStartTime)/1000000000f;

        simpleShaderProgram.setUniforms(
                (int)resolution[0],
                (int)resolution[1],
                texture,
                currentTime,
                quality
        );

        if(LoggerConfig.ON == true){
            FPSCounter.logFrameRate();
        }else {
        }
    }
//
//    private final BackBufferParameters backBufferTextureParams = new BackBufferParameters();
//    private final int fb[] = new int[]{0, 0};
//    private final int tx[] = new int[]{0, 0};
//    private int frontTarget;
//    private int backTarget = 1;
//
//    private void deleteTargets() {
//        if (fb[0] == 0) {
//            return;
//        }
//        GLES20.glDeleteFramebuffers(2, fb, 0);
//        GLES20.glDeleteTextures(2, tx, 0);
//
//        fb[0] = 0;
//    }
//
//    private void createTargets(int width, int height) {
//        deleteTargets();
//
//        GLES20.glGenFramebuffers(2, fb, 0);
//        GLES20.glGenTextures(2, tx, 0);
//
//        createTarget(frontTarget, width, height, backBufferTextureParams);
//        createTarget(backTarget, width, height, backBufferTextureParams);
//
//        // unbind textures that were bound in createTarget()
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//    }
//
//    private void createTarget(
//            int idx,
//            int width,
//            int height,
//            BackBufferParameters tp) {
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tx[idx]);
//
//        //Log.e("Bitmap",String.valueOf(useBitmap));
//        GLES20.glTexImage2D(
//                GLES20.GL_TEXTURE_2D,
//                0,
//                GLES20.GL_RGBA,
//                width,
//                height,
//                0,
//                GLES20.GL_RGBA,
//                GLES20.GL_UNSIGNED_BYTE,
//                null);
//
//        tp.setParameters(GLES20.GL_TEXTURE_2D);
//        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
//
//        GLES20.glBindFramebuffer(
//                GLES20.GL_FRAMEBUFFER,
//                fb[idx]);
//        GLES20.glFramebufferTexture2D(
//                GLES20.GL_FRAMEBUFFER,
//                GLES20.GL_COLOR_ATTACHMENT0,
//                GLES20.GL_TEXTURE_2D,
//                tx[idx],
//                0);
//
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT |
//                GLES20.GL_DEPTH_BUFFER_BIT);
//    }


}

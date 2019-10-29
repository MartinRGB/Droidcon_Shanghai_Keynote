package com.martinrgb.shaderexample.rendereralternative;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.martinrgb.shaderexample.renderer.program.FrameBufferHelper;
import com.martinrgb.shaderexample.rendereralternative.util.FPSCounter;
import com.martinrgb.shaderexample.rendereralternative.util.LoggerConfig;
import com.martinrgb.shaderexample.rendereralternative.program.TextureHelper;

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
    private int[] resolution = new int[2];
    private int[] textureIds;
    private int[] textures = new int[32];

    public ShaderRenderer(Context context,int vert,int frag) {
        this.context = context;
        mVertId = vert;
        mFragId = frag;
    }

    public ShaderRenderer(Context context,int vert,int frag,int[] texture) {
        this.context = context;
        mVertId = vert;
        mFragId = frag;
        this.textureIds = texture;
    }

    //###################Init Render###################

    private long globalStartTime;

    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){

        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        simpleShaderProgram = new SimpleShaderProgram(context,mVertId,mFragId);

        if(textureIds !=null ){
            for(int i = 0;i<textureIds.length;i++){
                textures[i] = TextureHelper.loadTexture(context, textureIds[i]);
            }
        }


    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height){

        globalStartTime = System.nanoTime();

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
                textures,
                currentTime
        );

        if(LoggerConfig.ON == true){
            FPSCounter.logFrameRate();
        }else {
        }
    }
}

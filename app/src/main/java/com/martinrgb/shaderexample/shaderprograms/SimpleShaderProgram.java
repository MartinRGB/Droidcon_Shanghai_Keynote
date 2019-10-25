package com.martinrgb.shaderexample.shaderprograms;

import android.content.Context;
import android.opengl.GLES20;

import com.martinrgb.shaderexample.shaderprograms.shaderutil.TexRenderBuffer;

import java.io.InputStream;

/**
 * Created by MartinRGB on 2017/2/26.
 */

public class SimpleShaderProgram extends ShaderProgram {


    private TexRenderBuffer bufA;
    private TexRenderBuffer bufB;
    private TexRenderBuffer bufC;
    private TexRenderBuffer bufPrev;
    private TexRenderBuffer bufLight;
    private TexRenderBuffer bufBlur;

    public SimpleShaderProgram(Context context, InputStream mVert, InputStream mFrag){
        super(context,mVert,mFrag);
    }

    public SimpleShaderProgram(Context context, int vertId, int fragId){
        super(context,vertId,fragId);
    }



    //###################Init Input Value & Draw Program###################
    //int width, int height, float mouseX, float mouseY, float sensorX, float sensorY, float sensorZ, float sensorAccelX,float sensorAccelY,float screenValue,int textureId,float totalAlpha,float texAlpha,int orientation,float offsetX,float offsetY,float time
    public void setUniforms(int width, int height, int textureId,float time){



        setupShaderInputs(programOrig,
                new int[]{width, height},
                new int[]{textureId},time);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }


}

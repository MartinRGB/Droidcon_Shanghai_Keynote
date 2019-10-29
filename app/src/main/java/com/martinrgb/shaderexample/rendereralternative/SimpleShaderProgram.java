package com.martinrgb.shaderexample.rendereralternative;

import android.content.Context;
import android.opengl.GLES20;

import com.martinrgb.shaderexample.rendereralternative.program.ShaderProgram;

import java.io.InputStream;

/**
 * Created by MartinRGB on 2017/2/26.
 */

public class SimpleShaderProgram extends ShaderProgram {



    public SimpleShaderProgram(Context context, int vertId, int fragId){
        super(context,vertId,fragId);
    }

    public SimpleShaderProgram(Context context, InputStream mVert, InputStream mFrag){
        super(context,mVert,mFrag);
    }

    //###################Init Input Value & Draw Program###################
    public void setUniforms(int width, int height, int[] textureId,float time){
        setupShaderInputs(programOrig,
                new int[]{width,height},
                textureId,time);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

    }

}

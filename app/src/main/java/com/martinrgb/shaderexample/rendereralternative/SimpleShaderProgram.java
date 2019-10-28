package com.martinrgb.shaderexample.rendereralternative;

import android.opengl.GLES20;

import android.content.Context;
import android.opengl.GLES20;

import com.martinrgb.shaderexample.renderer.program.TextureRenderBuffer;
import com.martinrgb.shaderexample.rendereralternative.program.ShaderProgram;

import java.io.InputStream;

/**
 * Created by MartinRGB on 2017/2/26.
 */

public class SimpleShaderProgram extends ShaderProgram {



	public SimpleShaderProgram(Context context, int vertId, int fragId){
		super(context,vertId,fragId);
	}


	//###################Init Input Value & Draw Program###################
	public void setUniforms(int frameNum,float[] mouse){

		setupShaderInputs(frameNum,mouse);

	}

}

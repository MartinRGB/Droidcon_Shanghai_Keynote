package com.martinrgb.shaderexample.renderer;

import android.content.Context;

import com.martinrgb.shaderexample.renderer.program.ShaderProgram;

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

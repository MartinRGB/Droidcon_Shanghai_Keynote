package com.martinrgb.shaderexample.renderer.program;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.Buffer;

public class VertexHelper{

	public static void enableVertexAttribArray(int loc) {
		GLES20.glEnableVertexAttribArray(loc);
	}
	public static void parseVertexAttribArray(int loc, Buffer buf){GLES20.glVertexAttribPointer(loc,2,GLES20.GL_BYTE, false, 0, buf);}
	public static void passVertexData(int loc, Buffer buf){GLES20.glVertexAttribPointer(loc,2,GLES20.GL_BYTE, false, 0, buf);}

}
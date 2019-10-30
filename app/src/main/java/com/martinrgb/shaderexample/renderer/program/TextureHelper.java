package com.martinrgb.shaderexample.renderer.program;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.martinrgb.shaderexample.renderer.util.LoggerConfig;

import java.util.ArrayList;

import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;

/**
 * Created by MartinRGB on 2017/2/17.
 */


public class TextureHelper {
	private static final String TAG = "TextureHelper";
	private static final int MAX_TEX_SIZE = 32;

	private static final int textureIds[] = new int[32];
	private static int textureSize = 0;

	public static void deleteTextures() {
		if (textureIds[0] == 1 || textureSize < 1) {
			return;
		}
		GLES20.glDeleteTextures(textureSize, textureIds, 0);
	}

	public static int[] loadTextures(Context context, int[] resourceId){
		deleteTextures();
		GLES20.glGenTextures(resourceId.length, textureIds, 0);
		for (int i = 0; i < resourceId.length; ++i) {
			createTexture(context,resourceId[i],textureIds[i]);
		}
		textureSize = resourceId.length;
		return textureIds;
	}


	public static void createTexture(Context context,int resId,int texId) {
		// # bind tex
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

		// # set parameters
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);

		// # set bitmap
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resId,options);
		Matrix flipMatrix = new Matrix();
		flipMatrix.postScale(1f, -1f);
		Bitmap flippedBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),flipMatrix,true);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,flippedBitmap,GLES20.GL_UNSIGNED_BYTE,0);
		flippedBitmap.recycle();
		bitmap.recycle();

		// # generate mipmap
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
	}


	public static int index;

	public static void resetTextureData() {
		index = 0;
	}

	public static void passTextureData(int loc,int textureId) {
		if (loc < 0 || index >= MAX_TEX_SIZE) {
			return;
		}

		GLES20.glUniform1i(loc, index);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

		++index;
	}
}

package com.martinrgb.shaderexample.rendereralternative.program;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.martinrgb.shaderexample.renderer.util.LoggerConfig;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

/**
 * Created by MartinRGB on 2017/2/17.
 */


public class TextureHelper {
	private static final String TAG = "TextureHelper";

	private static final ArrayList<TextureParameters> textureParameters = new ArrayList<>();
	//private static final TextureBinder textureBinder = new TextureBinder();
	private static final ArrayList<String> textureNames = new ArrayList<>();
	private static final int textureTargets[] = new int[32];
	private static final int textureIds[] = new int[32];
	private static final int textureResIds[] = new int[32];
	private static int numberOfTextures;
	private static final int TEXTURE_UNITS[] = {
			GLES20.GL_TEXTURE0,
			GLES20.GL_TEXTURE1,
			GLES20.GL_TEXTURE2,
			GLES20.GL_TEXTURE3,
			GLES20.GL_TEXTURE4,
			GLES20.GL_TEXTURE5,
			GLES20.GL_TEXTURE6,
			GLES20.GL_TEXTURE7,
			GLES20.GL_TEXTURE8,
			GLES20.GL_TEXTURE9,
			GLES20.GL_TEXTURE10,
			GLES20.GL_TEXTURE11,
			GLES20.GL_TEXTURE12,
			GLES20.GL_TEXTURE13,
			GLES20.GL_TEXTURE14,
			GLES20.GL_TEXTURE15,
			GLES20.GL_TEXTURE16,
			GLES20.GL_TEXTURE17,
			GLES20.GL_TEXTURE18,
			GLES20.GL_TEXTURE19,
			GLES20.GL_TEXTURE20,
			GLES20.GL_TEXTURE21,
			GLES20.GL_TEXTURE22,
			GLES20.GL_TEXTURE23,
			GLES20.GL_TEXTURE24,
			GLES20.GL_TEXTURE25,
			GLES20.GL_TEXTURE26,
			GLES20.GL_TEXTURE27,
			GLES20.GL_TEXTURE28,
			GLES20.GL_TEXTURE29,
			GLES20.GL_TEXTURE30,
			GLES20.GL_TEXTURE31};

	public static void parseAsset(int[] textures){
		if(textures !=null){
			textureNames.clear();
			textureParameters.clear();
			numberOfTextures = textures.length;
			//backBufferTextureParams.reset();

			for(int i =0;i<numberOfTextures;i++){
				//textureTargets[i] = GLES20.GL_TEXTURE_2D;
				textureResIds[i] = textures[i];
			}
		}
	}

	public static int getTextureNumber(){
		return numberOfTextures;
	}

	public static int genTextureId(int i){
		return textureIds[i];
	};

	public static void deleteTextures() {
		if (textureIds[0] == 1 || numberOfTextures < 1) {
			return;
		}
		GLES20.glDeleteTextures(numberOfTextures, textureIds, 0);
	}

	public static void createTextures(Context context) {
		deleteTextures();
		GLES20.glGenTextures(numberOfTextures, textureIds, 0);

		for (int i = 0; i < numberOfTextures; ++i) {
			Bitmap  bitmap = loadBitmap(context, textureResIds[i]);
			createTexture(textureIds[i], bitmap);
			bitmap.recycle();
		}
	}

	public static Bitmap loadBitmap(Context context, int resourceId){
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		//图像解码
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resourceId,options);
		return bitmap;
	}


	public static void createTexture(int id,Bitmap bitmap) {
		// # bind tex
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);

		// # set parameters
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);

		// # set bitmap
		if (bitmap == null) {
			return;
		}

		Matrix flipMatrix = new Matrix();
		flipMatrix.postScale(1f, -1f);
		Bitmap flippedBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),flipMatrix,true);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,flippedBitmap,GLES20.GL_UNSIGNED_BYTE,0);
		flippedBitmap.recycle();

		// # generate mipmap
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
	}


	private static int index;

	public static void reset() {
		index = 0;
	}

	public static void bind(int loc, int target, int textureId) {
		if (loc < 0 || index >= TEXTURE_UNITS.length) {
			return;
		}

		GLES20.glUniform1i(loc, index);
		GLES20.glActiveTexture(TEXTURE_UNITS[index]);
		GLES20.glBindTexture(target, textureId);

		++index;
	}

//	public static class TextureBinder {
//		private int index;
//
//		private void reset() {
//			index = 0;
//		}
//
//		private void bind(int loc, int target, int textureId) {
//			if (loc < 0 || index >= TEXTURE_UNITS.length) {
//				return;
//			}
//
//			GLES20.glUniform1i(loc, index);
//			GLES20.glActiveTexture(TEXTURE_UNITS[index]);
//			GLES20.glBindTexture(target, textureId);
//
//			++index;
//		}
//	}
}

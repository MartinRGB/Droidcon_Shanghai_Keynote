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


public class FrameBufferHelper {
	private static final String TAG = "FrameBufferHelper";
	private final BackBufferParameters backBufferTextureParams =
			new BackBufferParameters();
	private final int fb[] = new int[]{0, 0};
	private final int tx[] = new int[]{0, 0};
	private int frontTarget;
	private int backTarget = 1;

	public FrameBufferHelper() {
		backBufferTextureParams.reset();
	}

	public void deleteTargets() {
		if (fb[0] == 0) {
			return;
		}

		GLES20.glDeleteFramebuffers(2, fb, 0);
		GLES20.glDeleteTextures(2, tx, 0);

		fb[0] = 0;
	}

	public int[] getBuffer(){
		return fb;
	}

	public int getFrontTextureId(){
		return tx[backTarget];
	}

	public int getBackTextureId(){
		return tx[backTarget];
	}

	public void swapBuffer(){
		// swap buffers so the next image will be rendered
		// over the current backbuffer and the current image
		// will be the backbuffer for the next image
		int t = frontTarget;
		frontTarget = backTarget;
		backTarget = t;
	}

	public void createTargets(int width, int height) {
		deleteTargets();

		GLES20.glGenFramebuffers(2, fb, 0);
		GLES20.glGenTextures(2, tx, 0);

		createTarget(frontTarget, width, height, backBufferTextureParams);
		createTarget(backTarget, width, height, backBufferTextureParams);

		// unbind textures that were bound in createTarget()
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}

	public void createTarget(
			int idx,
			int width,
			int height,
			BackBufferParameters tp) {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tx[idx]);

		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,
				0,
				GLES20.GL_RGBA,
				width,
				height,
				0,
				GLES20.GL_RGBA,
				GLES20.GL_UNSIGNED_BYTE,
				null);

		tp.setParameters(GLES20.GL_TEXTURE_2D);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

		GLES20.glBindFramebuffer(
				GLES20.GL_FRAMEBUFFER,
				fb[idx]);
		GLES20.glFramebufferTexture2D(
				GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0,
				GLES20.GL_TEXTURE_2D,
				tx[idx],
				0);

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT |
				GLES20.GL_DEPTH_BUFFER_BIT);
	}

	public void bind(){
		GLES20.glBindFramebuffer(
		GLES20.GL_FRAMEBUFFER,
		fb[frontTarget]);
	}

	public void unbind(){

		GLES20.glBindFramebuffer(
				GLES20.GL_FRAMEBUFFER,
				0);

	}
}

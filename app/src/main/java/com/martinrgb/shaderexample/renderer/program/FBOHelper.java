package com.martinrgb.shaderexample.renderer.program;

import android.opengl.GLES20;

/**
 * Created by MartinRGB on 2017/2/17.
 */


public class FBOHelper {
	private static final String TAG = "FBOHelper";
	private static final int fb[] = new int[]{0, 0};
	private static final int tx[] = new int[]{0, 0};
	private static int frontTarget;
	private static int backTarget = 1;

	public static int[] getBuffers(){
		return fb;
	}
	public static int getFrontTextureId(){
		return tx[frontTarget];
	}
	public static int getBackTextureId(){return tx[backTarget]; }


	public static void createFBOs(int width, int height) {
		deleteFBOs();

		GLES20.glGenFramebuffers(2, fb, 0);
		GLES20.glGenTextures(2, tx, 0);

		createFBO(frontTarget, width, height);
		createFBO(backTarget, width, height);

		// unbind textures that were bound in createTarget()
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}

	private static void createFBO(int id,int width,int height) {

		// # create texture for framebuffer
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tx[id]);

		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,width,height,0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,null);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

		// # bind framebuffer
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fb[id]);
		// # add texture to framebuffer
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D,tx[id],0);

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

	}

	public static void swapFrameBuffer(){
		// swap buffers so the next image will be rendered
		// over the current backbuffer and the current image
		// will be the backbuffer for the next image
		int t = frontTarget;
		frontTarget = backTarget;
		backTarget = t;
	}

	private static void deleteFBOs() {
		if (fb[0] == 0) {
			return;
		}

		GLES20.glDeleteFramebuffers(2, fb, 0);
		GLES20.glDeleteTextures(2, tx, 0);

		fb[0] = 0;
	}


	public static void bindFBO(){GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fb[frontTarget]);}

	public static void unbindFBO(){ GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0); }
}

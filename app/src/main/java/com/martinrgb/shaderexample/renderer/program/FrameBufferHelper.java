package com.martinrgb.shaderexample.renderer.program;

import android.opengl.GLES20;

/**
 * Created by MartinRGB on 2017/2/17.
 */


public class FrameBufferHelper {
	private static final String TAG = "FrameBufferHelper";
	private final int fb[] = new int[]{0, 0};
	private final int tx[] = new int[]{0, 0};
	private int frontTarget;
	private int backTarget = 1;

	public FrameBufferHelper() {

	}

	public void deleteTargets() {
		if (fb[0] == 0) {
			return;
		}

		GLES20.glDeleteFramebuffers(2, fb, 0);
		GLES20.glDeleteTextures(2, tx, 0);

		fb[0] = 0;
	}

	public int[] getBuffers(){
		return fb;
	}

	public int getFrontTextureId(){
		return tx[frontTarget];
	}
	public int getBackTextureId(){return tx[backTarget]; }

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

		createTarget(frontTarget, width, height);
		createTarget(backTarget, width, height);

		// unbind textures that were bound in createTarget()
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}

	public void createTarget(int id,int width,int height) {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tx[id]);

		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,width,height,0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,null);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fb[id]);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D,tx[id],0);

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT |
				GLES20.GL_DEPTH_BUFFER_BIT);
	}

	public void bind(){
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fb[frontTarget]);
	}

	public void unbind(){

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);

	}
}

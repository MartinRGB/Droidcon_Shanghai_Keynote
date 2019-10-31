package com.martinrgb.shaderexample.renderer.program;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by MartinRGB on 2017/2/17.
 */


public class FBOHelper {
	private static final String TAG = "FBOHelper";
	private static final int fb[] = new int[]{0, 0};
	private static final int tx[] = new int[]{0, 0};
	private static int frontTarget;
	private static int backTarget = 1;
	private static BufferTarget frontBufferTarget = new BufferTarget(0,"T-A");
	private static BufferTarget backBufferTarget = new BufferTarget(1,"T-B");

	public static int[] getBuffers(){
		return fb;
	}
	public static int getFrontTextureId(){
		return tx[frontBufferTarget.getCurrentState()];
	}
	public static int getBackTextureId(){return tx[backBufferTarget.getCurrentState()]; }


	public static void createFBOs(int width, int height) {
		deleteFBOs();

		GLES20.glGenFramebuffers(2, fb, 0);
		GLES20.glGenTextures(2, tx, 0);

		createFBO(frontBufferTarget.getCurrentState(), width, height);
		createFBO(backBufferTarget.getCurrentState(), width, height);

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

//		int t = frontTarget;
//		frontTarget = backTarget;
//		backTarget = t;

		int t = frontBufferTarget.getCurrentState();
		frontBufferTarget.setCurrentState(backBufferTarget.getCurrentState());
		backBufferTarget.setCurrentState(t);
	}

	private static void deleteFBOs() {
		if (fb[0] == 0) {
			return;
		}

		GLES20.glDeleteFramebuffers(2, fb, 0);
		GLES20.glDeleteTextures(2, tx, 0);

		fb[0] = 0;
	}


	public static void bindFBO(){GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fb[frontBufferTarget.getCurrentState()]);}

	public static void unbindFBO(){ GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0); }

	private static class BufferTarget{
		private final int FRONT = 0;
		private int currentState = 0;
		private String currentTag = "Target-A";

		public BufferTarget() {
			setCurrentState(currentState);
			setCurrentTag(currentTag);
		}

		public BufferTarget(int state,String tag) {
			setCurrentState(state);
			setCurrentTag(tag);
		}

		public void setCurrentState(int state) {
//			if(state == FRONT){
//				Log.d("Curr FrongTarget Tag:",String.valueOf(getCurrentTag()));
//			}
			currentState = state;
		}

		public int getCurrentState() {
			return currentState;
		}

		public void setCurrentTag(String tag) {
			currentTag = tag;
		}

		public String getCurrentTag() {
			return currentTag;
		}
	}
}

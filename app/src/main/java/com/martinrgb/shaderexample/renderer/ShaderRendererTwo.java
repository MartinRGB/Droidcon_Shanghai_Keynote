package com.martinrgb.shaderexample.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import com.martinrgb.shaderexample.R;
import com.martinrgb.shaderexample.renderer.program.ShaderHelper;
import com.martinrgb.shaderexample.renderer.program.SimpleShaderProgram;
import com.martinrgb.shaderexample.renderer.program.VertexHelper;
import com.martinrgb.shaderexample.renderer.util.FPSCounter;
import com.martinrgb.shaderexample.renderer.util.LoggerConfig;
import com.martinrgb.shaderexample.renderer.util.TextReader;
import com.martinrgb.shaderexample.renderer.program.FrameBufferHelper;
import com.martinrgb.shaderexample.renderer.program.TextureHelper;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class ShaderRendererTwo implements GLSurfaceView.Renderer {


	private final float resolution[] = new float[]{0, 0};
	private final float surfaceResolution[] = new float[]{0,0};
	private final float mouse[] = new float[]{0, 0};

	private long startTime;
	private float quality = 1f;
	private static final float NS_PER_SECOND = 1000000000f;

	private FrameBufferHelper mFramerBuffer;
	private int[] mRes;
	private int[] mTexs;
	private int mFrag;

	private final Context context;

//	public static final String UNIFORM_BACKBUFFER = "u_backbuffer";
//	public static final String UNIFORM_FRAME_NUMBER = "u_frame";
//	public static final String UNIFORM_MOUSE = "u_mouse";
//	public static final String ATTRIBUTE_POSITION = "a_position";
//	public static final String ATTRIBUTE_TEXCOORD = "a_texcoord";
//	public static final String UNIFORM_RESOLUTION = "u_resolution";
//	public static final String UNIFORM_TIME = "u_time";
//	public static final String UNIFORM_TEXTURE = "u_tex";
//
//
//	private final float surfaceResolution[] = new float[]{0, 0};
//
//	private static ByteBuffer vertexBuffer;
//	private static ByteBuffer textureBuffer;
//
//	private int surfaceProgram = 0;
//	private int program = 0;
//
//	private int surfacePositionLoc;
//	private int surfaceTexCoordLoc;
//	private int surfaceResolutionLoc;
//	private int surfaceFrameLoc;
//
//	private int positionLoc;
//	private int texCoordLoc;
//	private int timeLoc;
//	private int resolutionLoc;
//	private int mouseLoc;
//	private int backBufferLoc;
//	private final int textureLocs[] = new int[32];





	public ShaderRendererTwo(Context context) {
		this.context = context;
	}

	public void setFrag(int frag, float quality,int[] res) {
		setQuality(quality);
		mFramerBuffer = new FrameBufferHelper();
		this.mFrag = frag;
		this.mRes = res;
	}

	public void setQuality(float quality) {
		this.quality = quality;
	}


	private SimpleShaderProgram simpleShaderProgram;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearColor(0f, 0f, 0f, 1f);

		if(mRes != null){
			mTexs = TextureHelper.loadTextures(context,mRes);
		}
		simpleShaderProgram = new SimpleShaderProgram(context,R.raw.simplevert,mFrag,mTexs);

		Log.e("Tag","2");
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		startTime =  System.nanoTime();

		surfaceResolution[0] = width;
		surfaceResolution[1] = height;

		float w = Math.round(width * quality);
		float h = Math.round(height * quality);

		if (w != resolution[0] || h != resolution[1]) {
			mFramerBuffer.deleteTargets();
		}

		resolution[0] = w;
		resolution[1] = h;


		Log.e("Tag","3");
	}

	@Override
	public void onDrawFrame(GL10 gl) {

		final long now = System.nanoTime();
		float delta = (now - startTime) / NS_PER_SECOND;

		simpleShaderProgram.setUniformInput(mFramerBuffer,delta,resolution,surfaceResolution,mouse,mTexs);


//		if (surfaceProgram == 0 || program == 0) {
//			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//			return;
//		}
//
//		final long now = System.nanoTime();
//
//		GLES20.glUseProgram(program);
//
//		VertexHelper.parseVertexAttribArray(positionLoc,vertexBuffer);
//		VertexHelper.parseVertexAttribArray(texCoordLoc,textureBuffer);
//
//		float delta = (now - startTime) / NS_PER_SECOND;
//
//		if (timeLoc > -1) {
//			GLES20.glUniform1f(timeLoc,delta);
//		}
//
//
//		if (resolutionLoc > -1) {
//			GLES20.glUniform2fv(resolutionLoc,1,resolution,0);
//		}
//
//		if (mouseLoc > -1) {
//			GLES20.glUniform2fv(mouseLoc,1,mouse,0);
//		}
//
//		if (mFramerBuffer.getBuffers()[0] == 0) {
//			mFramerBuffer.createTargets((int) resolution[0],(int) resolution[1]);
//		}
//
//
//		// first draw custom shader in framebuffer
//		GLES20.glViewport(0,0,(int) resolution[0],(int) resolution[1]);
//
//		int texIndex = 0;
//
//		if (backBufferLoc > -1) {
//			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + texIndex);
//			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFramerBuffer.getBackTextureId());
//			GLES20.glUniform1i(backBufferLoc, texIndex);
//			++texIndex;
//		}
//
//		for (int i = 0; i < mTexs.length; ++i) {
//			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + texIndex);
//			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexs[i]);
//			GLES20.glUniform1i(textureLocs[i], texIndex);
//			++texIndex;
//		}
//
//		mFramerBuffer.bind();
//
//		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
//
//		// then draw framebuffer on screen
//		mFramerBuffer.unbind();
//
//		GLES20.glViewport(0,0,(int) surfaceResolution[0],(int) surfaceResolution[1]);
//
//		GLES20.glUseProgram(surfaceProgram);
//
//		VertexHelper.parseVertexAttribArray(surfacePositionLoc,vertexBuffer);
//		VertexHelper.parseVertexAttribArray(surfaceTexCoordLoc,textureBuffer);
//
//		GLES20.glUniform2fv(surfaceResolutionLoc,1,surfaceResolution,0);
//
//		GLES20.glUniform1i(surfaceFrameLoc, 0);
//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mFramerBuffer.getFrontTextureId());
//
//		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
//
//
//
//		mFramerBuffer.swapBuffer();


		if(LoggerConfig.ON == true){
			FPSCounter.logFrameRate();
		}

	}


	public void touchAt(MotionEvent e) {
		mouse[0] = e.getX(0) * quality;
		mouse[1] = resolution[1] - e.getY(0) * quality;;
	}



}

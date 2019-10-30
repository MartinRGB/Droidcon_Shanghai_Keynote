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

		float delta = (System.nanoTime() - startTime) / NS_PER_SECOND;

		simpleShaderProgram.setUniformInput(mFramerBuffer,delta,resolution,surfaceResolution,mouse,mTexs);

		if(LoggerConfig.ON == true){
			FPSCounter.logFrameRate();
		}

	}


	public void touchAt(MotionEvent e) {
		mouse[0] = e.getX(0) * quality;
		mouse[1] = resolution[1] - e.getY(0) * quality;;
	}



}

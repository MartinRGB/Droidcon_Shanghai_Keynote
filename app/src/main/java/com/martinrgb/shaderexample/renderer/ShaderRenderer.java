package com.martinrgb.shaderexample.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.martinrgb.shaderexample.R;
import com.martinrgb.shaderexample.renderer.program.ShaderProgram;
import com.martinrgb.shaderexample.renderer.util.FPSCounter;
import com.martinrgb.shaderexample.renderer.util.LoggerConfig;
import com.martinrgb.shaderexample.renderer.program.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class ShaderRenderer implements GLSurfaceView.Renderer {

	private long startTime;
	private static final float NS_PER_SECOND = 1000000000f;

	private final float resolution[] = new float[]{0,0};
	private final float frameResolution[] = new float[]{0,0};

	private final float mouse[] = new float[]{0, 0};

	private int[] resIds;
	private int[] textures;

	private float quality = 1f;

	private int frag;

	private final Context context;

	private float mSaturation = 1.67f;

	public ShaderRenderer(Context context) {
		this.context = context;
	}

	public void setFrag(int frag, float quality,int[] resIds) {
		setQuality(quality);
		setFrag(frag);
		setResIds(resIds);
	}
	public void setQuality(float quality) {
		this.quality = quality;
	}
	public void setFrag(int frag) {
		this.frag = frag;
	}
	public void setResIds(int[] resIds) {
		this.resIds = resIds;
	}

	private ShaderProgram shaderProgram;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearColor(0f, 0f, 0f, 1f);

		if(resIds != null){
			textures = TextureHelper.loadTextures(context,resIds);
		}

		shaderProgram = new ShaderProgram(context,R.raw.simplevert,frag);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		startTime =  System.nanoTime();
		resolution[0] = width;
		resolution[1] = height;

		frameResolution[0] = Math.round(width * quality);
		frameResolution[1] = Math.round(height * quality);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		float time = (System.nanoTime() - startTime) / NS_PER_SECOND;
		shaderProgram.setUniformInput(time,frameResolution,resolution,mouse,textures,mSaturation);

		if(LoggerConfig.ON == true){
			FPSCounter.logFrameRate();
		}
	}


	public void touchAt(MotionEvent e) {
		mouse[0] = e.getX(0) * quality;
		mouse[1] = Math.round(resolution[1] * quality) - e.getY(0) * quality;
	}

	// seekbar input
	public void onSaturationInput(float input) {
		mSaturation = input;
	}


}

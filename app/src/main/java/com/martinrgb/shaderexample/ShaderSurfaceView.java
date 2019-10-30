package com.martinrgb.shaderexample;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.martinrgb.shaderexample.renderer.ShaderRenderer;

public class ShaderSurfaceView extends GLSurfaceView {
	private ShaderRenderer renderer;

	public ShaderSurfaceView(Context context) {
		super(context);
		setRenderer(context);
	}

	public ShaderSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setRenderer(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		renderer.touchAt(event);
		return true;
	}

	private void setRenderer(Context context) {
		renderer = new ShaderRenderer(context);
		setEGLContextClientVersion(2);
		setRenderer(renderer);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	public ShaderRenderer getRenderer() {
		return renderer;
	}

	public void setFrag(int shaderid, float quality,int[] texrues) {
		onPause();
		renderer.setFrag(shaderid, quality,texrues);
		onResume();
	}

}
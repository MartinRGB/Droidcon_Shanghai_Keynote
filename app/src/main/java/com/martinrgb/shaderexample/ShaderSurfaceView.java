package com.martinrgb.shaderexample;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.martinrgb.shaderexample.renderer.ShaderRendererTwo;
import com.martinrgb.shaderexample.rendereralternative.ShaderRenderer;

public class ShaderSurfaceView extends GLSurfaceView {
//	private ShaderRenderer renderer;
	private ShaderRendererTwo renderer;

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
		//renderer = new ShaderRenderer(context,R.raw.simplevert,R.raw.flow,new int[]{R.drawable.test,R.drawable.test2});
		renderer = new ShaderRendererTwo(context);

		setEGLContextClientVersion(2);
		setRenderer(renderer);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

//	public ShaderRenderer getRenderer() {
//		return renderer;
//	}

	public void setFragWithTex(int shaderid, float quality,int[] texrues) {
		onPause();
		renderer.setFragWithTex(shaderid, quality,texrues);

		onResume();
	}

	public void setFrag(int shaderid, float quality) {
		onPause();
		renderer.setFrag(shaderid, quality);
		onResume();
	}

}

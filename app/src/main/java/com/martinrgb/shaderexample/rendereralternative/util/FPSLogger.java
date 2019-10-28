package com.martinrgb.shaderexample.rendereralternative.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.MotionEvent;

import com.martinrgb.shaderexample.rendereralternative.ShaderRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.util.ArrayList;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glTexParameteri;

public class FPSLogger{

	public long lastRender;
	private volatile long nextFpsUpdate = 0;
	private volatile float sum;
	private volatile float samples;
	private volatile int lastFps;
	private static final float NS_PER_SECOND = 1000000000f;
	private static final long FPS_UPDATE_FREQUENCY_NS = 200000000L;
	private ShaderRenderer.OnRendererListener rendererListener;

	public FPSLogger(ShaderRenderer.OnRendererListener rendererListener) {
		this.rendererListener = rendererListener;
	}

	public void resetFps() {
		sum = samples = 0;
		lastFps = 0;
		nextFpsUpdate = 0;
	}

	public void updateFps(long now) {
		long delta = now - lastRender;

		// because sum and samples are volatile
		synchronized (this) {
			sum += Math.min(NS_PER_SECOND / delta, 60f);

			if (++samples > 0xffff) {
				sum = sum / samples;
				samples = 1;
			}
		}

		if (now > nextFpsUpdate) {
			int fps = Math.round(sum / samples);

			if (fps != lastFps) {
				rendererListener.onFramesPerSecond(fps);
				lastFps = fps;
			}

			nextFpsUpdate = now + FPS_UPDATE_FREQUENCY_NS;
		}

		lastRender = now;
	}
}

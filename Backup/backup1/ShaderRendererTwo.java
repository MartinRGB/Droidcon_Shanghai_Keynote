package com.martinrgb.shaderexample.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import com.martinrgb.shaderexample.renderer.util.TextReader;
import com.martinrgb.shaderexample.renderer.program.FrameBufferHelper;
import com.martinrgb.shaderexample.renderer.program.TextureHelper;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.util.ArrayList;

public class ShaderRendererTwo implements GLSurfaceView.Renderer {


	public static final String UNIFORM_BACKBUFFER = "backbuffer";
	public static final String UNIFORM_FRAME_NUMBER = "frame";
	public static final String UNIFORM_MOUSE = "u_mouse";
	public static final String ATTRIBUTE_POSITION = "a_position";
	public static final String VARYING_TEXTURECOORD = "v_texcoord";
	public static final String UNIFORM_RESOLUTION = "u_resolution";
	public static final String UNIFORM_TIME = "u_time";
	public static final String UNIFORM_TEXTURE = "u_tex";


	private static final String VERTEX_SHADER =
			"attribute vec2 a_position;" +
					"void main() {" +
					"gl_Position = vec4(a_position, 0., 1.);" +
					"}";
	private static final String FRAGMENT_SHADER =
			"#ifdef GL_FRAGMENT_PRECISION_HIGH\n" +
					"precision highp float;\n" +
					"#else\n" +
					"precision mediump float;\n" +
					"#endif\n" +
					"uniform vec2 u_resolution;" +
					"uniform sampler2D frame;" +
					"void main(void) {" +
					"gl_FragColor = texture2D(frame," +
					"gl_FragCoord.xy / u_resolution.xy).rgba;" +
					"}";



	private final float surfaceResolution[] = new float[]{0, 0};
	private final float resolution[] = new float[]{0, 0};
	private final float mouse[] = new float[]{0, 0};
	private final Context context;
	private static ByteBuffer vertexBuffer;
	private static ByteBuffer textureBuffer;

	private String fragmentShader;

	private int surfaceProgram = 0;
	private int program = 0;

	private int surfacePositionLoc;
	private int surfaceTexCoordLoc;
	private int surfaceResolutionLoc;
	private int surfaceFrameLoc;

	private int positionLoc;
	private int texCoordLoc;
	private int timeLoc;
	private int frameNumLoc;
	private int resolutionLoc;
	private int mouseLoc;
	private int backBufferLoc;
	private final int textureLocs[] = new int[32];

	private int frameNum;
	private long startTime;
	private float quality = 1f;
	private static final float NS_PER_SECOND = 1000000000f;

	private FrameBufferHelper mFramerBuffer;

	public ShaderRendererTwo(Context context) {
		this.context = context;

//		vertexBuffer = ByteBuffer.allocateDirect(8);
//		vertexBuffer.put(new byte[]{
//				-1, 1,
//				-1, -1,
//				1, 1,
//				1, -1}).position(0);
//
//		textureBuffer = ByteBuffer.allocateDirect(8);
//		textureBuffer.put(new byte[]{
//				1, 0,
//				0, 0,
//				1, 1,
//				0, 1}).position(0);
	}

	public void setFragWithTex(int frag, float quality,int[] texures) {
		setQuality(quality);
		setFragmentShader(TextReader.readTextFileFromResource(context,frag));
		//TextureHelper.setTextures(texures);
		//textureHelper = new TextureHelper(texures);
		TextureHelper.parseAsset(texures);
		mFramerBuffer = new FrameBufferHelper();

	}

	public void setFrag(int frag, float quality) {
		setQuality(quality);
		setFragmentShader(TextReader.readTextFileFromResource(context,frag));
	}

	private void setFragmentShader(String source) {
		fragmentShader = source;
	}


	public void setQuality(float quality) {
		this.quality = quality;
	}


	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {


		GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);

		GLES20.glClearColor(0f, 0f, 0f, 1f);

		if (surfaceProgram != 0) {
			surfaceProgram = 0;
		}

		if (program != 0) {
			program = 0;
			mFramerBuffer.deleteTargets();
		}

		if (fragmentShader != null && fragmentShader.length() > 0) {
			TextureHelper.createTextures(context);
			loadPrograms();
			indexLocations();
			setupVertex();
		}
		Log.e("Tag","2");
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		startTime =  System.nanoTime();

		frameNum = 0;

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
		if (surfaceProgram == 0 || program == 0) {
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT |
					GLES20.GL_DEPTH_BUFFER_BIT);

			return;
		}

		final long now = System.nanoTime();

		GLES20.glUseProgram(program);

//		GLES20.glVertexAttribPointer(
//				positionLoc,
//				2,
//				GLES20.GL_BYTE,
//				false,
//				0,
//				vertexBuffer);
//
//		GLES20.glVertexAttribPointer(
//				texCoordLoc,
//				2,
//				GLES20.GL_BYTE,
//				false,
//				0,
//				textureBuffer);

		VertexHelper.parseVertexAttribArray(positionLoc,vertexBuffer);
		VertexHelper.parseVertexAttribArray(texCoordLoc,textureBuffer);

		float delta = (now - startTime) / NS_PER_SECOND;

		if (timeLoc > -1) {
			GLES20.glUniform1f(
					timeLoc,
					delta);
		}

		if (frameNumLoc > -1) {
			GLES20.glUniform1i(
					frameNumLoc,
					frameNum);
		}


		if (resolutionLoc > -1) {
			GLES20.glUniform2fv(
					resolutionLoc,
					1,
					resolution,
					0);
		}

		if (mouseLoc > -1) {
			GLES20.glUniform2fv(
					mouseLoc,
					1,
					mouse,
					0);
		}

		if (mFramerBuffer.getBuffer()[0] == 0) {
			mFramerBuffer.createTargets(
					(int) resolution[0],
					(int) resolution[1]);
		}

		// first draw custom shader in framebuffer
		GLES20.glViewport(
				0,
				0,
				(int) resolution[0],
				(int) resolution[1]);

		TextureHelper.reset();

		if (backBufferLoc > -1) {
			TextureHelper.bind(
					backBufferLoc,
					GLES20.GL_TEXTURE_2D,
					mFramerBuffer.getBackTextureId());
		}

		for (int i = 0; i < TextureHelper.getTextureNumber(); ++i) {
			TextureHelper.bind(
					textureLocs[i],
					GLES20.GL_TEXTURE_2D,
					TextureHelper.genTextureId(i));
		}

		mFramerBuffer.bind();

		GLES20.glDrawArrays(
				GLES20.GL_TRIANGLE_STRIP,
				0,
				4);

		// then draw framebuffer on screen
		mFramerBuffer.unbind();

		GLES20.glViewport(
				0,
				0,
				(int) surfaceResolution[0],
				(int) surfaceResolution[1]);

		GLES20.glUseProgram(surfaceProgram);

//		GLES20.glVertexAttribPointer(
//				surfacePositionLoc,
//				2,
//				GLES20.GL_BYTE,
//				false,
//				0,
//				vertexBuffer);
//
//		GLES20.glVertexAttribPointer(
//				surfaceTexCoordLoc,
//				2,
//				GLES20.GL_BYTE,
//				false,
//				0,
//				textureBuffer);
		VertexHelper.parseVertexAttribArray(surfacePositionLoc,vertexBuffer);
		VertexHelper.parseVertexAttribArray(surfaceTexCoordLoc,textureBuffer);

		GLES20.glUniform2fv(
				surfaceResolutionLoc,
				1,
				surfaceResolution,
				0);

		GLES20.glUniform1i(surfaceFrameLoc, 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(
				GLES20.GL_TEXTURE_2D,
				mFramerBuffer.getFrontTextureId());

		GLES20.glClear(
				GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glDrawArrays(
				GLES20.GL_TRIANGLE_STRIP,
				0,
				4);


		mFramerBuffer.swapBuffer();

		++frameNum;
	}


	public void touchAt(MotionEvent e) {
		mouse[0] = e.getX(0) * quality;
		mouse[1] = resolution[1] - e.getY(0) * quality;;
	}

	private void loadPrograms() {
		String vertexShader = VERTEX_SHADER;

		surfaceProgram = Program.loadProgram(VERTEX_SHADER,FRAGMENT_SHADER);
		program = Program.loadProgram(vertexShader,fragmentShader);
	}

	private void indexLocations() {
		surfacePositionLoc = GLES20.glGetAttribLocation(
				surfaceProgram, ATTRIBUTE_POSITION);
		surfaceTexCoordLoc = GLES20.glGetAttribLocation(
				surfaceProgram, VARYING_TEXTURECOORD);
		positionLoc = GLES20.glGetAttribLocation(
				program, ATTRIBUTE_POSITION);
		texCoordLoc = GLES20.glGetAttribLocation(
				program, VARYING_TEXTURECOORD);

		surfaceResolutionLoc = GLES20.glGetUniformLocation(
				surfaceProgram, UNIFORM_RESOLUTION);
		surfaceFrameLoc = GLES20.glGetUniformLocation(
				surfaceProgram, "frame");


		timeLoc = GLES20.glGetUniformLocation(
				program, UNIFORM_TIME);

		resolutionLoc = GLES20.glGetUniformLocation(
				program, UNIFORM_RESOLUTION);
		mouseLoc = GLES20.glGetUniformLocation(
				program, UNIFORM_MOUSE);
		frameNumLoc = GLES20.glGetUniformLocation(
				program, UNIFORM_FRAME_NUMBER);

		backBufferLoc = GLES20.glGetUniformLocation(
				program, UNIFORM_BACKBUFFER);

		for (int i = TextureHelper.getTextureNumber(); i-- > 0; ) {
			textureLocs[i] = GLES20.glGetUniformLocation(
					program,UNIFORM_TEXTURE + i);

		}
	}


	private void setupVertex(){
		vertexBuffer = ByteBuffer.allocateDirect(8);
		vertexBuffer.put(new byte[]{
				-1, 1,
				-1, -1,
				1, 1,
				1, -1}).position(0);

		textureBuffer = ByteBuffer.allocateDirect(8);
		textureBuffer.put(new byte[]{
				1, 0,
				0, 0,
				1, 1,
				0, 1}).position(0);

		VertexHelper.enableVertexAttribArray(surfacePositionLoc);
		VertexHelper.enableVertexAttribArray(positionLoc);
		VertexHelper.enableVertexAttribArray(surfaceTexCoordLoc);
		VertexHelper.enableVertexAttribArray(texCoordLoc);
	}


	public static class VertexHelper{

		private static void enableVertexAttribArray(int loc) {
			GLES20.glEnableVertexAttribArray(loc);
		}
		private static void parseVertexAttribArray(int loc,Buffer buf){
			GLES20.glVertexAttribPointer(loc,2,GLES20.GL_BYTE, false, 0, buf);
		}

	}

}

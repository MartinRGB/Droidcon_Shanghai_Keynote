package com.martinrgb.shaderexample.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import com.martinrgb.shaderexample.R;
import com.martinrgb.shaderexample.renderer.util.FPSCounter;
import com.martinrgb.shaderexample.renderer.util.LoggerConfig;
import com.martinrgb.shaderexample.renderer.util.TextReader;
import com.martinrgb.shaderexample.renderer.program.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ShaderRenderer implements GLSurfaceView.Renderer {


//	public static final String UNIFORM_BACKBUFFER = "backbuffer";
//	public static final String UNIFORM_FRAME_NUMBER = "frame";
//	public static final String UNIFORM_MOUSE = "u_mouse";
//	public static final String ATTRIBUTE_POSITION = "a_position";
//	public static final String VARYING_TEXTURECOORD = "v_texcoord";
//	public static final String UNIFORM_RESOLUTION = "u_resolution";
//	public static final String UNIFORM_TIME = "u_time";
//	public static final String UNIFORM_TEXTURE = "u_tex";


//	private static final String VERTEX_SHADER =
//			"attribute vec2 a_position;" +
//					"void main() {" +
//					"gl_Position = vec4(a_position, 0., 1.);" +
//					"}";
//	private static final String FRAGMENT_SHADER =
//			"#ifdef GL_FRAGMENT_PRECISION_HIGH\n" +
//					"precision highp float;\n" +
//					"#else\n" +
//					"precision mediump float;\n" +
//					"#endif\n" +
//					"uniform vec2 u_resolution;" +
//					"uniform sampler2D frame;" +
//					"void main(void) {" +
//					"gl_FragColor = texture2D(frame," +
//					"gl_FragCoord.xy / u_resolution.xy).rgba;" +
//					"}";



//	private final float surfaceResolution[] = new float[]{0, 0};
	private final float resolution[] = new float[]{0, 0};
	private final float mouse[] = new float[]{0, 0};
	private final Context context;
//	private final ByteBuffer vertexBuffer;
//	private final ByteBuffer textureBuffer;


//	private String fragmentShader;

//	private int surfaceProgram = 0;
//	private int program = 0;

//	private int surfacePositionLoc;
//	private int surfaceTexCoordLoc;
//	private int surfaceResolutionLoc;
//	private int surfaceFrameLoc;
//
//	private int positionLoc;
//	private int texCoordLoc;
//	private int timeLoc;
//	private int frameNumLoc;
//	private int resolutionLoc;
//	private int mouseLoc;
//	private int backBufferLoc;
//	private final int textureLocs[] = new int[32];

//	private int frameNum;
//	private long startTime;
//	private FrameBufferHelper mFramerBuffer;
//	private static final float NS_PER_SECOND = 1000000000f;

	private float quality = 1f;
	private SimpleShaderProgram simpleShaderProgram;
	private int mFragShader;

	public ShaderRenderer(Context context) {
		this.context = context;
	}

	public void setFragtWithTex(int frag, float quality,int[] texures) {
		setQuality(quality);
		this.mFragShader = frag;
		//TextureHelper.setTextures(texures);
		//textureHelper = new TextureHelper(texures);
		TextureHelper.parseAsset(texures);
//		mFramerBuffer = new FrameBufferHelper();
		Log.e("1","1");
	}



	public void setFrag(int frag, float quality) {
		setQuality(quality);
		this.mFragShader = frag;
		Log.e("1","1-0");
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
		Log.e("2","2");
		simpleShaderProgram= new SimpleShaderProgram(context, R.raw.simplevert,mFragShader);
	}

	private int frameNum;
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		frameNum = 0;

		simpleShaderProgram.onSurfaceChanged(width,height,quality);

		resolution[0] = Math.round(width * quality);
		resolution[1] = Math.round(height * quality);

		Log.e("3","3");

	}

	@Override
	public void onDrawFrame(GL10 gl) {

		simpleShaderProgram.setUniforms(frameNum,mouse);


		if(LoggerConfig.ON == true){
			FPSCounter.logFrameRate();
		}

		++frameNum;
	}


	public void touchAt(MotionEvent e) {
		mouse[0] = e.getX(0) * quality;
		mouse[1] = resolution[1] - e.getY(0) * quality;;
	}

}

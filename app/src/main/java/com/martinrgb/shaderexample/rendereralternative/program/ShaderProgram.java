package com.martinrgb.shaderexample.rendereralternative.program;


import android.content.Context;
import android.opengl.GLES20;

import com.martinrgb.shaderexample.R;
import com.martinrgb.shaderexample.renderer.util.Constants;
import com.martinrgb.shaderexample.renderer.util.TextReader;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by MartinRGB on 2017/2/26.
 */

public class ShaderProgram {

    // Uniform
    public static final String UNIFORM_BACKBUFFER = "backbuffer";
    public static final String UNIFORM_FRAME_NUMBER = "frame";
    public static final String UNIFORM_MOUSE = "u_mouse";
    public static final String UNIFORM_RESOLUTION = "u_resolution";
    public static final String UNIFORM_TIME = "u_time";
    public static final String UNIFORM_TEXTURE = "u_tex";
    // Attribute
    public static final String ATTRIBUTE_POSITION = "a_position";
    // Vary
    public static final String VARYING_TEXTURECOORD = "v_texcoord";

    //###################Memory Allocate & Build Program###################

//    protected final int programOrig;
//    protected final int programFinal;
    private static ByteBuffer vertexBuffer;
    private static ByteBuffer textureBuffer;

    private FrameBufferHelper mFramerBuffer = new FrameBufferHelper();

    protected ShaderProgram(Context context, int vertexId, int fragId){

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


        if (surfaceProgram != 0) {
			surfaceProgram = 0;
		}

		if (program != 0) {
			program = 0;
			mFramerBuffer.deleteTargets();
		}

        loadPrograms(context);
        indexLocations();
        enableAttribArrays();
    }

    private int surfaceProgram = 0;
    private int program = 0;

    private int surfacePositionLoc;
    private int surfaceTexCoordLoc;
    private int surfaceResolutionLoc;
    private int surfaceFrameLoc;

    private final float surfaceResolution[] = new float[]{0, 0};
    private final float resolution[] = new float[]{0, 0};
//    private final float mouse[] = new float[]{0, 0};

    private int positionLoc;
    private int texCoordLoc;
    private int timeLoc;
    private int frameNumLoc;
    private int resolutionLoc;
    private int mouseLoc;
    private int backBufferLoc;
    private final int textureLocs[] = new int[32];

//    private int frameNum;
    private long startTime;
    private static final float NS_PER_SECOND = 1000000000f;

    public void onSurfaceChanged(int width,int height,float quality){
        surfaceResolution[0] = width;
        surfaceResolution[1] = height;

        float w = Math.round(width * quality);
        float h = Math.round(height * quality);

        if (w != resolution[0] || h != resolution[1]) {
            mFramerBuffer.deleteTargets();
        }

        resolution[0] = w;
        resolution[1] = h;
    }

    private void loadPrograms(Context context){
        // 拿到着色器 GLSL 文件，用buildProgram构建Program
        program = ShaderHelper.buildProgram(
                TextReader.readTextFileFromResource(context, R.raw.simplevert),
                TextReader.readTextFileFromResource(context, R.raw.flow));

        surfaceProgram = ShaderHelper.buildProgram(
                TextReader.readTextFileFromResource(context, R.raw.simplevert),
                TextReader.readTextFileFromResource(context, R.raw.lastpass));
    }

    private void indexLocations() {
        surfacePositionLoc = GLES20.glGetAttribLocation(
                surfaceProgram, ATTRIBUTE_POSITION);
        surfaceTexCoordLoc = GLES20.glGetAttribLocation(
                surfaceProgram, VARYING_TEXTURECOORD);
        surfaceResolutionLoc = GLES20.glGetUniformLocation(
                surfaceProgram, UNIFORM_RESOLUTION);
        surfaceFrameLoc = GLES20.glGetUniformLocation(
                surfaceProgram, "frame");

        positionLoc = GLES20.glGetAttribLocation(
                program, ATTRIBUTE_POSITION);
        texCoordLoc = GLES20.glGetAttribLocation(
                program, VARYING_TEXTURECOORD);
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

    private void enableAttribArrays() {
        GLES20.glEnableVertexAttribArray(surfacePositionLoc);
        GLES20.glEnableVertexAttribArray(positionLoc);
        GLES20.glEnableVertexAttribArray(surfaceTexCoordLoc);
        GLES20.glEnableVertexAttribArray(texCoordLoc);
    }

    //###################Init Input Function###################
    public void setupShaderInputs(int frameNum,float[] mouse) {
        if (surfaceProgram == 0 || program == 0) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            return;
        }

        final long now = System.nanoTime();

        GLES20.glUseProgram(program);

        GLES20.glVertexAttribPointer(positionLoc,2,GLES20.GL_BYTE,false,0,vertexBuffer);
        GLES20.glVertexAttribPointer(texCoordLoc,2,GLES20.GL_BYTE,false, 0, textureBuffer);

        float delta = (now - startTime) / NS_PER_SECOND;

        if (timeLoc > -1) {
            GLES20.glUniform1f(timeLoc,delta);
        }

        if (frameNumLoc > -1) {
            GLES20.glUniform1i(frameNumLoc,frameNum);
        }

        if (resolutionLoc > -1) {
            GLES20.glUniform2fv(resolutionLoc,1,resolution,0);
        }

        if (mouseLoc > -1) {
            GLES20.glUniform2fv( mouseLoc, 1, mouse,0);
        }

        if (mFramerBuffer.getBuffer()[0] == 0) {
            mFramerBuffer.createTargets((int) resolution[0],(int) resolution[1]);
        }

        // first draw custom shader in framebuffer
        GLES20.glViewport( 0,0,(int) resolution[0],(int) resolution[1]);

        TextureHelper.reset();

        if (backBufferLoc > -1) {
            TextureHelper.bind(backBufferLoc, GLES20.GL_TEXTURE_2D,mFramerBuffer.getBackTextureId());
        }

        for (int i = 0; i < TextureHelper.getTextureNumber(); ++i) {
            TextureHelper.bind(textureLocs[i],GLES20.GL_TEXTURE_2D,TextureHelper.genTextureId(i));
        }

        mFramerBuffer.bind();

        GLES20.glDrawArrays( GLES20.GL_TRIANGLE_STRIP,0,4);

        // then draw framebuffer on screen
        mFramerBuffer.unbind();

        GLES20.glViewport(0,0,(int) surfaceResolution[0],(int) surfaceResolution[1]);

        GLES20.glUseProgram(surfaceProgram);

        GLES20.glVertexAttribPointer(surfacePositionLoc,2,GLES20.GL_BYTE,false,0,vertexBuffer);

        GLES20.glVertexAttribPointer(surfaceTexCoordLoc,2,GLES20.GL_BYTE,false,0,textureBuffer);

        GLES20.glUniform2fv(surfaceResolutionLoc,1, surfaceResolution,0);

        GLES20.glUniform1i(surfaceFrameLoc, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mFramerBuffer.getFrontTextureId());

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);


        mFramerBuffer.swapBuffer();
    }

}
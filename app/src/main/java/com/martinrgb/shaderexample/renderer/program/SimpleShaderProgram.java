package com.martinrgb.shaderexample.renderer.program;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.martinrgb.shaderexample.R;
import com.martinrgb.shaderexample.renderer.util.TextReader;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class SimpleShaderProgram {

    public static final String UNIFORM_BACKBUFFER = "u_backbuffer";
    public static final String UNIFORM_FRAME_NUMBER = "u_frame";
    public static final String UNIFORM_MOUSE = "u_mouse";
    public static final String ATTRIBUTE_POSITION = "a_position";
    public static final String ATTRIBUTE_TEXCOORD = "a_texcoord";
    public static final String UNIFORM_RESOLUTION = "u_resolution";
    public static final String UNIFORM_TIME = "u_time";
    public static final String UNIFORM_TEXTURE = "u_tex";

    private int surfaceProgram = 0;
    private int program = 0;

    private static ByteBuffer vertexBuffer;
    private static ByteBuffer textureBuffer;

    private int surfacePositionLoc;
    private int surfaceTexCoordLoc;
    private int surfaceResolutionLoc;
    private int surfaceFrameLoc;

    private int positionLoc;
    private int texCoordLoc;
    private int timeLoc;
    private int resolutionLoc;
    private int mouseLoc;
    private int backBufferLoc;
    private final int textureLocs[] = new int[32];

    private int[] mTexturess;

    public SimpleShaderProgram(Context context,int vert, int frag,int[] textures) {

        if (surfaceProgram != 0) { surfaceProgram = 0; }
        if (program != 0) { program = 0; }

        program = ShaderHelper.buildProgram(
                TextReader.readTextFileFromResource(context,vert),
                TextReader.readTextFileFromResource(context,frag));

        surfaceProgram = ShaderHelper.buildProgram(
                TextReader.readTextFileFromResource(context,vert),
                TextReader.readTextFileFromResource(context,R.raw.lastpass));

        mTexturess = textures;

        init();
    }

    private void init(){
        indexLocations();
        setupVertex();
    }


    private void indexLocations() {
        surfacePositionLoc = GLES20.glGetAttribLocation(surfaceProgram, ATTRIBUTE_POSITION);
        surfaceTexCoordLoc = GLES20.glGetAttribLocation(surfaceProgram, ATTRIBUTE_TEXCOORD);
        positionLoc = GLES20.glGetAttribLocation(program, ATTRIBUTE_POSITION);
        texCoordLoc = GLES20.glGetAttribLocation(program, ATTRIBUTE_TEXCOORD);

        Log.e("Loc",String.valueOf(surfacePositionLoc));
        Log.e("Loc",String.valueOf(surfaceTexCoordLoc));
        Log.e("Loc",String.valueOf(positionLoc));
        Log.e("Loc",String.valueOf(texCoordLoc));

        surfaceResolutionLoc = GLES20.glGetUniformLocation( surfaceProgram, UNIFORM_RESOLUTION);
        surfaceFrameLoc = GLES20.glGetUniformLocation(surfaceProgram, UNIFORM_FRAME_NUMBER);

        resolutionLoc = GLES20.glGetUniformLocation(program, UNIFORM_RESOLUTION);
        timeLoc = GLES20.glGetUniformLocation(program, UNIFORM_TIME);
        mouseLoc = GLES20.glGetUniformLocation(program, UNIFORM_MOUSE);
        backBufferLoc = GLES20.glGetUniformLocation(program, UNIFORM_BACKBUFFER);
        if(mTexturess !=null){
            for (int i =  mTexturess.length; i-- > 0; ) {
                textureLocs[i] = GLES20.glGetUniformLocation(program,UNIFORM_TEXTURE + i);
            }
        }
    }

    private void setupVertex(){
        vertexBuffer = ByteBuffer.allocateDirect(8);
        vertexBuffer.put(new byte[]{1, -1,-1, -1,1, 1,-1, 1}).position(0);


        textureBuffer = ByteBuffer.allocateDirect(8);
        textureBuffer.put(new byte[]{1, 0,0, 0,1, 1,0, 1}).position(0);

        VertexHelper.enableVertexAttribArray(surfacePositionLoc);
        VertexHelper.enableVertexAttribArray(surfaceTexCoordLoc);
        VertexHelper.enableVertexAttribArray(positionLoc);
        VertexHelper.enableVertexAttribArray(texCoordLoc);
    }

    public void setUniformInput(FrameBufferHelper mFramerBuffer,float delta,float[] resolution,float[] surfaceResolution,float[] mouse,int[] textures){
        if (surfaceProgram == 0 || program == 0) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            return;
        }

        GLES20.glUseProgram(program);

        VertexHelper.parseVertexAttribArray(positionLoc,vertexBuffer);
        VertexHelper.parseVertexAttribArray(texCoordLoc,textureBuffer);


        if (timeLoc > -1) { GLES20.glUniform1f(timeLoc,delta); }
        if (resolutionLoc > -1) { GLES20.glUniform2fv(resolutionLoc,1,resolution,0); }
        if (mouseLoc > -1) { GLES20.glUniform2fv(mouseLoc,1,mouse,0); }

        if (mFramerBuffer.getBuffers()[0] == 0) { mFramerBuffer.createTargets((int) resolution[0],(int) resolution[1]); }

        // first draw custom shader in framebuffer
        GLES20.glViewport(0,0,(int) resolution[0],(int) resolution[1]);

        int texIndex = 0;

        if (backBufferLoc > -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + texIndex);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFramerBuffer.getBackTextureId());
            GLES20.glUniform1i(backBufferLoc, texIndex);
            ++texIndex;
        }

        if(textures != null){
            for (int i = 0; i < textures.length; ++i) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + texIndex);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);
                GLES20.glUniform1i(textureLocs[i], texIndex);
                ++texIndex;
            }
        }

        mFramerBuffer.bind();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        // then draw framebuffer on screen
        mFramerBuffer.unbind();

        GLES20.glViewport(0,0,(int) surfaceResolution[0],(int) surfaceResolution[1]);

        GLES20.glUseProgram(surfaceProgram);

        VertexHelper.parseVertexAttribArray(surfacePositionLoc,vertexBuffer);
        VertexHelper.parseVertexAttribArray(surfaceTexCoordLoc,textureBuffer);

        GLES20.glUniform2fv(surfaceResolutionLoc,1,surfaceResolution,0);

//        if (surfaceFrameLoc > -1) {
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + texIndex);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mFramerBuffer.getFrontTextureId());
//            GLES20.glUniform1i(surfaceFrameLoc, texIndex);
//            ++texIndex;
//        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mFramerBuffer.getFrontTextureId());
        GLES20.glUniform1i(surfaceFrameLoc, 0);


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        mFramerBuffer.swapBuffer();
    }
}

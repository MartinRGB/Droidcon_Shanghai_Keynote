package com.martinrgb.shaderexample.renderer.program;

import android.content.Context;
import android.opengl.GLES20;

import com.martinrgb.shaderexample.R;
import com.martinrgb.shaderexample.renderer.util.TextReader;

import java.nio.ByteBuffer;

public class ShaderProgram {


    public static final String ATTRIBUTE_POSITION = "a_position";
    public static final String ATTRIBUTE_TEXCOORD = "a_texcoord";

    public static final String UNIFORM_BACKBUFFER = "u_backbuffer";
    public static final String UNIFORM_FRAME = "u_frame";
    public static final String UNIFORM_MOUSE = "u_mouse";
    public static final String UNIFORM_RESOLUTION = "u_resolution";
    public static final String UNIFORM_TIME = "u_time";
    public static final String UNIFORM_TEXTURE = "u_tex";
    public static final String UNIFORM_SATURATION = "u_saturation";

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
    private int saturationLoc;
    private final int textureLocs[] = new int[32];

    public ShaderProgram(Context context, int vert, int frag) {

        if (surfaceProgram != 0) { surfaceProgram = 0; }
        if (program != 0) { program = 0; }

        program = ShaderHelper.buildProgram(
                TextReader.readTextFileFromResource(context,vert),
                TextReader.readTextFileFromResource(context,frag));

        surfaceProgram = ShaderHelper.buildProgram(
                TextReader.readTextFileFromResource(context,vert),
                TextReader.readTextFileFromResource(context,R.raw.finalpass));

        init();
    }

    private void init(){
        indexLocations();
        initVertexBuffer();
        enableAttribArrays();
    }

    private void buildPrograms(){

    }

    private void initVertexBuffer(){
        vertexBuffer = ByteBuffer.allocateDirect(8);
        vertexBuffer.put(new byte[]{1, -1,-1, -1,1, 1,-1, 1}).position(0);

        textureBuffer = ByteBuffer.allocateDirect(8);
        textureBuffer.put(new byte[]{1, 0,0, 0,1, 1,0, 1}).position(0);
    }

    private void indexLocations() {
        surfacePositionLoc = GLES20.glGetAttribLocation(surfaceProgram, ATTRIBUTE_POSITION);
        surfaceTexCoordLoc = GLES20.glGetAttribLocation(surfaceProgram, ATTRIBUTE_TEXCOORD);

        positionLoc = GLES20.glGetAttribLocation(program, ATTRIBUTE_POSITION);
        texCoordLoc = GLES20.glGetAttribLocation(program, ATTRIBUTE_TEXCOORD);

        surfaceFrameLoc = GLES20.glGetUniformLocation(surfaceProgram, UNIFORM_FRAME);
        surfaceResolutionLoc = GLES20.glGetUniformLocation( surfaceProgram, UNIFORM_RESOLUTION);

        backBufferLoc = GLES20.glGetUniformLocation(program, UNIFORM_BACKBUFFER);
        resolutionLoc = GLES20.glGetUniformLocation(program, UNIFORM_RESOLUTION);

        timeLoc = GLES20.glGetUniformLocation(program, UNIFORM_TIME);
        mouseLoc = GLES20.glGetUniformLocation(program, UNIFORM_MOUSE);
        saturationLoc = GLES20.glGetUniformLocation(program, UNIFORM_SATURATION);
        // static texture location
//        if(mTextures !=null){
//            for (int i =  mTextures.length; i-- > 0; ) {
//                textureLocs[i] = GLES20.glGetUniformLocation(program,UNIFORM_TEXTURE + i);
//            }
//        }
    }


    private void enableAttribArrays(){
        VertexHelper.enableVertexAttribArray(surfacePositionLoc);
        VertexHelper.enableVertexAttribArray(surfaceTexCoordLoc);
        VertexHelper.enableVertexAttribArray(positionLoc);
        VertexHelper.enableVertexAttribArray(texCoordLoc);
    }

    public void setUniformInput(float time,float[] frameResolution,float[] resolution,float[] mouse,int[] textures,float saturation){
        // ##################### clear the canvas #####################
        if (surfaceProgram == 0 || program == 0) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            return;
        }

        // create FBO targets
        if (FBOHelper.getBuffers()[0] == 0) { FBOHelper.createFBOs((int) frameResolution[0],(int) frameResolution[1]); }

        // ##################### first program #####################
        GLES20.glUseProgram(program);

        VertexHelper.passVertexData(positionLoc,vertexBuffer);
        VertexHelper.passVertexData(texCoordLoc,textureBuffer);

        if (timeLoc > -1) { GLES20.glUniform1f(timeLoc,time); }
        if (resolutionLoc > -1) { GLES20.glUniform2fv(resolutionLoc,1,frameResolution,0); }
        if (mouseLoc > -1) { GLES20.glUniform2fv(mouseLoc,1,mouse,0); }
        if (saturation > -1) { GLES20.glUniform1f(saturationLoc,saturation); }

        GLES20.glViewport(0,0,(int) frameResolution[0],(int) frameResolution[1]);

        TextureHelper.resetTextureData();

        if (backBufferLoc > -1) { TextureHelper.passTextureData(backBufferLoc,FBOHelper.getBackTextureId()); }

        if(textures != null){ for (int i = 0; i < textures.length; ++i) {
            textureLocs[i] = GLES20.glGetUniformLocation(program,UNIFORM_TEXTURE + i);
            TextureHelper.passTextureData(textureLocs[i],textures[i]);} }

        // ## draw first program to framebuffer
        FBOHelper.bindFBO();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        FBOHelper.unbindFBO();

        // ##################### second program #####################
        GLES20.glUseProgram(surfaceProgram);

        VertexHelper.passVertexData(surfacePositionLoc,vertexBuffer);
        VertexHelper.passVertexData(surfaceTexCoordLoc,textureBuffer);

        GLES20.glUniform2fv(surfaceResolutionLoc,1,resolution,0);

        GLES20.glViewport((int)0,0,(int) resolution[0],(int) resolution[1]);

        // ## get first program frame's from framebuffer
        if (surfaceFrameLoc > -1) { TextureHelper.passTextureData(surfaceFrameLoc,FBOHelper.getFrontTextureId()); }

        // ## draw framebuffer on screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        // // ##################### swap the buffer #####################
        FBOHelper.swapFrameBuffer();
    }
}

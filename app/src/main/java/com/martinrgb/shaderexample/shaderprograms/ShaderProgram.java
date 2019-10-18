package com.martinrgb.shaderexample.shaderprograms;

import android.content.Context;
import android.opengl.GLES20;

import com.martinrgb.shaderexample.shaderprograms.shaderutil.Constants;
import com.martinrgb.shaderexample.shaderprograms.shaderutil.ShaderHelper;
import com.martinrgb.shaderexample.shaderprograms.shaderutil.TextResourceReader;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by MartinRGB on 2017/2/26.
 */

public class ShaderProgram {

    //Uniform常量
    //protected static final String U_MATRIX = "u_Matrix";
    //protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    //protected static final String U_COLOR = "u_Color";

    //Attribute 常量

    //###################Vertex Data###################
    //Vertex Array 和 Simple Color 对象移过来

    private static FloatBuffer VERTEX_BUF, TEXTURE_COORD_BUF;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;
    private static final float[] VERTEX_DATA = {
            //Order of coordinates: X, Y
            1.0f, -1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            -1.0f, 1.0f,
    };

    private static final float[] TEXTURE_DATA = {
            //Order of coordinates: X, Y
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
    };

    //###################Memory Allocate & Build Program###################


    protected final int programOrig;
    //protected final Buffer vertexBuffer;

    protected ShaderProgram(Context context, InputStream givenVertex, InputStream givenFrag){

        if (VERTEX_BUF == null) {
            VERTEX_BUF = ByteBuffer.allocateDirect(VERTEX_DATA.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            VERTEX_BUF.put(VERTEX_DATA);
            VERTEX_BUF.position(0);
        }

        if (TEXTURE_COORD_BUF == null) {
            TEXTURE_COORD_BUF = ByteBuffer.allocateDirect(TEXTURE_DATA.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            TEXTURE_COORD_BUF.put(TEXTURE_DATA);
            TEXTURE_COORD_BUF.position(0);
        }


        //拿到着色器GLSL文件，用buildProgram构建Program
        programOrig = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, givenVertex),
                TextResourceReader.readTextFileFromResource(context, givenFrag));


    }


    //###################Init Input Function###################

    //int program, int[] iResolution, int[] iChannels, float mouseX,float mouseY,float sensorX,float sensorY,float sensorZ,float sensorAccelX,float sensorAccelY,float screenValue,float totalAlpha,float texAlpha,int orientation,float offsetX,float offsetY,float time

    void setupShaderInputs(int program, int[] iResolution, int[] iTextures,float time) {
        GLES20.glUseProgram(program);

        int vPositionLocation = GLES20.glGetAttribLocation(program, "a_Position");
        GLES20.glEnableVertexAttribArray(vPositionLocation);
        GLES20.glVertexAttribPointer(vPositionLocation, 2, GLES20.GL_FLOAT, false, 8, VERTEX_BUF);

        int vTexCoordLocation = GLES20.glGetAttribLocation(program, "v_texcoord");
        GLES20.glEnableVertexAttribArray(vTexCoordLocation);
        GLES20.glVertexAttribPointer(vTexCoordLocation, 2, GLES20.GL_FLOAT, false, 8, TEXTURE_COORD_BUF);

        int iResolutionLocation = GLES20.glGetUniformLocation(program, "u_resolution");
        GLES20.glUniform2f(iResolutionLocation,(float) iResolution[0],(float) iResolution[1]);

        int iGlobalTimeLocation = GLES20.glGetUniformLocation(program, "u_time");
        GLES20.glUniform1f(iGlobalTimeLocation, time);

        for (int i = 0; i < iTextures.length; i++) {
            int sTextureLocation = GLES20.glGetUniformLocation(program, "u_tex" + i);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iTextures[i]);
            GLES20.glUniform1i(sTextureLocation, i);
        }
    }



}
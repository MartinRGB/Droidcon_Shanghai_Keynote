package com.martinrgb.shaderexample.renderer.program;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.martinrgb.shaderexample.renderer.util.LoggerConfig;

import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

/**
 * Created by MartinRGB on 2017/2/17.
 */

public class TextureHelper {
    private static final String TAG = "TextureHelper";

    public static int genTexture() {
        return genTexture(GLES20.GL_TEXTURE_2D);
    }

    public static int genTexture(int textureType) {
        int[] genBuf = new int[1];
        GLES20.glGenTextures(1, genBuf, 0);
        GLES20.glBindTexture(textureType, genBuf[0]);

        // Set texture default draw parameters
        if (textureType == GLES11Ext.GL_TEXTURE_EXTERNAL_OES) {
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        } else {
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
        }

        return genBuf[0];
    }

    public static int loadTexture(Context context, int resourceId) {
        //材质ID
        final int[] textureObjectIds = new int[1];
        //创建了材质纹理对象
        glGenTextures(1, textureObjectIds, 0);
        //检查调用是否成功
        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        };

        //OpenGL 不支持读取压缩图形格式如 JPEG PNG，所以要用Android内置解码器把图像压缩为OpenGL 所需格式
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        //图像解码
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resourceId,options);
        bitmap = flipBitmap(bitmap);
        if(bitmap == null){
            if(LoggerConfig.ON){
                Log.w(TAG,"Resource ID" + resourceId + "could not be decoded.");
            }
            glDeleteTextures(1,textureObjectIds,0);
            return 0;
        }

        //二维纹理，对象ID;
        glBindTexture(GL_TEXTURE_2D,textureObjectIds[0]);

        //纹理扩大或缩小的时候，需要纹理过滤来告知，纹理元素挤压到更小的片段 - 缩小 ,纹理元素拓展到更大的 片段 - 放大
        //缩小 使用三线性过滤（更平滑）
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
        //放大 双线性过滤（解决了最临界过滤放大的锯齿效果）
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);

        //告诉OpenGL读取Bitmap，并且绑定到纹理对象上
        GLUtils.texImage2D(GL_TEXTURE_2D,0,bitmap,0);
        //生成MIP贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        //纹理对象绑定了Bitmap之后，Bitmap 回收
        bitmap.recycle();
        //纹理加载成贴图后，解除纹理的绑定
        glBindTexture(GL_TEXTURE_2D,0);

        return textureObjectIds[0];

    }

    public static int loadTexture(Context context, int resourceId,boolean isSDCard,Bitmap localBitmap) {
        //材质ID
        final int[] textureObjectIds = new int[1];
        //创建了材质纹理对象
        glGenTextures(1, textureObjectIds, 0);
        //检查调用是否成功
        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        };

        //OpenGL 不支持读取压缩图形格式如 JPEG PNG，所以要用Android内置解码器把图像压缩为OpenGL 所需格式
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        //图像解码
        Bitmap bitmap = null;
        if(!isSDCard){
            bitmap = BitmapFactory.decodeResource(context.getResources(),resourceId,options);
            bitmap = flipBitmap(bitmap);
        }
        else{
            bitmap = localBitmap;
            bitmap = flipBitmap(bitmap);
        }

        if(bitmap == null){
            if(LoggerConfig.ON){
                Log.w(TAG,"Resource ID" + resourceId + "could not be decoded.");
            }
            glDeleteTextures(1,textureObjectIds,0);
            return 0;
        }

        //二维纹理，对象ID;
        glBindTexture(GL_TEXTURE_2D,textureObjectIds[0]);

        //纹理扩大或缩小的时候，需要纹理过滤来告知，纹理元素挤压到更小的片段 - 缩小 ,纹理元素拓展到更大的 片段 - 放大
        //缩小 使用三线性过滤（更平滑）
        //放大 双线性过滤（解决了最临界过滤放大的锯齿效果）
        //两个 Linear 保证了纹理导入的平滑
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);

        //告诉OpenGL读取Bitmap，并且绑定到纹理对象上
        GLUtils.texImage2D(GL_TEXTURE_2D,0,bitmap,0);
        //生成MIP贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        //纹理对象绑定了Bitmap之后，Bitmap 回收
        bitmap.recycle();
        //纹理加载成贴图后，解除纹理的绑定
        glBindTexture(GL_TEXTURE_2D,0);

        return textureObjectIds[0];

    }

    public static Bitmap flipBitmap(Bitmap bm){
        Matrix flip = new Matrix();
        flip.postScale(1f, -1f);
        Bitmap bmp = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), flip, true);
        return bmp;
    }

}

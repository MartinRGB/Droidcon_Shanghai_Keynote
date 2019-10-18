package com.martinrgb.shaderexample.shaderprograms.shaderutil;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by MartinRGB on 2017/2/26.
 */

public class TextResourceReader {

    public static String readTextFileFromResource(Context context, InputStream inputStream){
        StringBuilder body = new StringBuilder();

        try{
//            InputStream inputStream = context.getResources().getAssets().open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;

            while((nextLine = bufferedReader.readLine()) != null){
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e){
            throw new RuntimeException("Could not open resource:" ,e);
        } catch (Resources.NotFoundException nfe){
            throw new RuntimeException("Resource not found:" ,nfe);
        }

        return body.toString();
    };

    public static String readTextFileFromResource(Context context, int resourceID){
        StringBuilder body = new StringBuilder();

        try{
            InputStream inputStream = context.getResources().openRawResource(resourceID);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;

            while((nextLine = bufferedReader.readLine()) != null){
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e){
            throw new RuntimeException("Could not open resource:" + resourceID,e);
        } catch (Resources.NotFoundException nfe){
            throw new RuntimeException("Resource not found:" + resourceID,nfe);
        }

        return body.toString();
    };


}

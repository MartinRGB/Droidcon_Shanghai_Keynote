package com.martinrgb.shaderexample;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import java.io.IOException;

/**
 * Created by MartinRGB on 2017/11/29.
 */

public class WallpaperUtil {

    public static final String MIHOME_WALLPAPER_ACTION = "com.miui.home.WALLPAPER_PREVIEW";
    static final String EXTRA_LIVE_WALLPAPER_INTENT = "android.live_wallpaper.intent";
    static final String EXTRA_LIVE_WALLPAPER_SETTINGS = "android.live_wallpaper.settings";
    static final String EXTRA_LIVE_WALLPAPER_PACKAGE = "android.live_wallpaper.package";


    public static class LiveWallpaperInfo {
        public Drawable thumbnail;
        public WallpaperInfo info;
        public Intent intent;
    }

    public static void setToWallPaper(Context context,String className,boolean MIDevice) {

        try {
            WallpaperManager.getInstance(context).clear();

        }catch (IOException e) {
            e.printStackTrace();
        }

        if( DeviceDetectUtil.isMiUi()){
            final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(context, className));
            intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);

            context.startActivity(intent);
        }
        else{
            final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(context, className));
            intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);

            context.startActivity(intent);
        }



    }


    public static void showLiveWallpaperPreview(WallpaperInfo info, Intent intent, Context context) {
        if (info == null) return;

        Intent preview = new Intent(MIHOME_WALLPAPER_ACTION);
        //preview.setComponent(new ComponentName(context, VideoLiveWallpaper.class));
        preview.putExtra(EXTRA_LIVE_WALLPAPER_INTENT, intent);
        preview.putExtra(EXTRA_LIVE_WALLPAPER_SETTINGS, info.getSettingsActivity());
        preview.putExtra(EXTRA_LIVE_WALLPAPER_PACKAGE, info.getPackageName());
        context.startActivity(preview);
    }
}

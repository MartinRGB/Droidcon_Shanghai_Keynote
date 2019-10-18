package com.martinrgb.shaderexample;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;


//两个进程这个设计原理 - android:process,后期还是保留，
// 然后重新写跨进程访问的方案，用静态变量肯定是不合适了，
// 把GIFNAME相关逻辑从activity提取出来，封装到一个专门的业务类，然后这个业务类搞成单例的

public class MainActivity extends AppCompatActivity {

    private static final int VERT_FILE_PICKER_RESULT = 2;
    private static final int FRAG_FILE_PICKER_RESULT = 3;
    private ExFilePicker mExFilePicker;

    private DevicePolicyManager devicePolicyManager;
    private boolean isAdminActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

        mExFilePicker = new ExFilePicker();
        mExFilePicker.setCanChooseOnlyOneItem(true);
        mExFilePicker.setQuitButtonEnabled(true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setShaderToWallPaper(View view) {ShaderWallpaper.setToWallPaper(MainActivity.this);}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){

            case R.id.vert_setting:
                mExFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
                mExFilePicker.start(this,VERT_FILE_PICKER_RESULT);
                break;

            case R.id.frag_setting:
                mExFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
                mExFilePicker.start(this,FRAG_FILE_PICKER_RESULT);
                break;

            default:
        }
        return super.onOptionsItemSelected(item);
    }



    public static String vertPath;
    public static String vertName;

    public static String fragPath;
    public static String fragName;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VERT_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(result.getNames().get(0));
                vertPath = result.getPath();
                vertName = stringBuilder.toString();

                ShaderWallpaper.setToWallPaper(this);
            }
        }
        else if (requestCode == FRAG_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(result.getNames().get(0));
                fragPath = result.getPath();
                fragName = stringBuilder.toString();

                ShaderWallpaper.setToWallPaper(this);
            }
        }
    }
}

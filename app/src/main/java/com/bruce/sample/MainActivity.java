package com.bruce.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bruce.permisson.PermissonUtil;

public class MainActivity extends AppCompatActivity {
    private String[] permissonArr = {Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void reauestPermisson(View view) {
        //默认 不用出入要请求的权限
        PermissonUtil.getInstance().requestPermissons(this, new PermissonUtil.RequestCallback() {
            @Override
            public void onResult(boolean isAllGranted, String[] permissins) {
                for (int i = 0; i < permissins.length; i++) {
                    Log.i("reauestPermisson--", permissins[i]);
                }
            }
        });
    }

    public void reauestPermisson1(View view) {
        //请求指定的权限
        PermissonUtil.getInstance().requestPermissons(this, new PermissonUtil.RequestCallback() {
            @Override
            public void onResult(boolean isAllGranted, String[] permissins) {
                for (int i = 0; i < permissins.length; i++) {
                    Log.i("reauestPermisson1--", permissins[i]);
                }
            }
        }, permissonArr);
    }

    //重写 系统权限回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissonUtil.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

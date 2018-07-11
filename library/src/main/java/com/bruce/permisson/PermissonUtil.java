package com.bruce.permisson;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zy on 2017/6/28.
 */

public class PermissonUtil {
    private String TAG = "PermissonUtil";
    private static PermissonUtil permissonUtil;
    private int index = 0;//requestCode
    //map 绑定了 requestCode和RequestCallback
    //private ArrayMap<Integer, RequestCallback> map = new ArrayMap<>();
    private HashMap<Integer, RequestCallback> map = new HashMap<>();

    private PermissonUtil() {
    }

    public static PermissonUtil getInstance() {
        if (permissonUtil == null) {
            permissonUtil = new PermissonUtil();
        }
        return permissonUtil;
    }

    //NonNull  不可传入null
    //Nullable 可以传入null
    public void requestPermissons(@NonNull Activity activity, @NonNull RequestCallback requestCallback,
                                  @Nullable String... permissins) {
        if (requestCallback == null) {
            throw new RuntimeException("requestCallback is null!");
        }
        String[] permArr = null;
        permArr = permissins;
        if (permArr == null || permArr.length == 0) {
            //如果传入的权限 为空，就获取清单文件中的权限
            permArr = getAllPermissons(activity);
        }
        if (permArr != null && permArr.length > 0) {
            //map 可转 Set对象
            //map 没有迭代
            Set<Integer> keySet = map.keySet();
            Set<Map.Entry<Integer, RequestCallback>> entrySet = map.entrySet();
            map.put(index, requestCallback);
            boolean allPermissonGrant = true;
            for (int i = 0; i < permArr.length; i++) {
                if (ActivityCompat.checkSelfPermission(activity, permArr[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    allPermissonGrant = false;
                    break;
                }
            }
            if (allPermissonGrant) {
                //请求的权限 全部 已获取到
                requestCallback.onResult(true, permArr);
                //移除map中的元素
                map.remove(index);
            } else {
                //请求的权限 有未获取到
                //进行请求
                ActivityCompat.requestPermissions(activity, permArr, index);
            }
            index++;
        }

    }

    public void requestPermissons(@NonNull Fragment fragment, @NonNull RequestCallback requestCallback,
                                  @Nullable String... permissins) {
        if (requestCallback == null) {
            throw new RuntimeException("requestCallback is null!");
        }
        String[] permArr = null;
        permArr = permissins;
        if (permArr == null) {
            //如果传入的权限 为空，就获取清单文件中的权限
            permArr = getAllPermissons(fragment.getContext());
        }
        if (permArr != null && permArr.length > 0) {
            map.put(index, requestCallback);
            boolean allPermissonGrant = true;
            for (int i = 0; i < permArr.length; i++) {
                if (ActivityCompat.checkSelfPermission(fragment.getContext(), permArr[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    allPermissonGrant = false;
                    break;
                }
            }
            if (allPermissonGrant) {
                //请求的权限 已获取到
                requestCallback.onResult(true, permArr);
                //移除map中的元素
                map.remove(index);
            } else {
                //有 未获取到的权限，进行请求
                fragment.requestPermissions(permArr, index);
            }
            index++;
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (map != null) {
            RequestCallback requestCallback = map.get(requestCode);
            requestCallback.onResult(isAllRequestGranted(grantResults, permissions), permissions);
            //移除map中的元素
            map.remove(requestCode);
        }
    }


    //获取 清单文件(AndroidManifest) 声明请求的权限
    public String[] getAllPermissons(@NonNull Context context) {
        String[] permissions = null;
        try {
            // 参数2必须是PackageManager.GET_PERMISSIONS
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            permissions = pi.requestedPermissions;
            /*if (permissions != null) {
                for (String str : permissions) {
                    Log.d(TAG, "getAllPermissons:" + str);
                }
            }*/
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return permissions;
    }

    private boolean isAllRequestGranted(int[] grantResults, String[] permissions) {
        boolean allPermissonGrant = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                allPermissonGrant = false;
                Log.i(TAG, "--" + permissions[i] + " 权限不同意");
                break;
            }
        }
        return allPermissonGrant;
    }

    public interface RequestCallback {
        //isGranted:全部获取到权限
        void onResult(boolean isAllGranted, String[] permissins);
    }
}

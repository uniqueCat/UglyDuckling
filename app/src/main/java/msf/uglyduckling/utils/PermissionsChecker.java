package msf.uglyduckling.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 马帅发 on 2017/11/2.
 * 权限检测工具类
 */
public class PermissionsChecker {

    private static List<String> list = new ArrayList<>();

    private PermissionsChecker() {
    }

    // 获取没有权限  的权限集合
    public static String[] lacksPermissions(Context mContext, String... permissions) {
        for (String permission : permissions) {
            if (!lacksPermission(mContext, permission)) {
                list.add(permission);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    // 判断是否缺少权限 true表示有
    public static boolean lacksPermission(Context mContext, String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }


}
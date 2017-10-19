package com.yh.yhchanneledit.Utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by YH on 2017/10/13.
 */

public class PermissionGetter {
    private Context context;

    public PermissionGetter(Context context) {
        this.context = context;
    }

    /**
     * 判断系统是否已为应用开启某项权限
     *
     * @param num 权限编号
     * @return 已开启则返回0，否则返回1
     */
    private int checkPermission(int num) {
        int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            Class c = object.getClass();
            try {
                Class[] classes = new Class[3];
                classes[0] = int.class;
                classes[1] = int.class;
                classes[2] = String.class;
                Method lMethod = c.getDeclaredMethod("checkOp", classes);
                return (Integer) lMethod.invoke(object, num, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * Android 6.0之后的手机需要进行弹窗权限的申请
     * 其中小米、魅族以及华为三种机型需要特殊处理
     */
    public void alertWindowPermission() {
        if (this.checkPermission(24) == 1) {
            Toast toast = Toast.makeText(context, "请先为您的手机开启悬浮窗权限", Toast.LENGTH_SHORT);
            toast.show();

            //  处理小米手机权限
            if ("Xiaomi".equals(Build.MANUFACTURER)) {
                try {
                    Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                    intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                    intent.putExtra("extra_pkgname", context.getPackageName());
                    context.startActivity(intent);
                } catch (ActivityNotFoundException localActivityNotFoundException) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            }
            //  处理魅族手机权限
            else if ("Meizu".equals(Build.MANUFACTURER)) {
                try {
                    Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.putExtra("packageName", context.getPackageName());
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //  处理华为手机权限
            else if ("Huawei".equals(Build.MANUFACTURER)) {
                try {
                    Intent intent = new Intent(context.getPackageName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");
                    intent.setComponent(comp);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //  处理其他手机权限
            else if (Build.VERSION.SDK_INT >= 23) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            }
        }
    }
}

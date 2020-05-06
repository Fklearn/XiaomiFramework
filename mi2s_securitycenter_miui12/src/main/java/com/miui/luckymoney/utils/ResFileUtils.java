package com.miui.luckymoney.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.miui.luckymoney.config.CommonConfig;
import java.io.File;

public class ResFileUtils {
    public static final String ADSPATH = "Ads";
    public static final String FLOAT34 = "Float_3_4";
    public static final String FLOAT5 = "Float_5";
    public static final String FLOATTIPS = "FloatTips";
    public static final String LUCKYALARMPATH = "LuckyAlarm";
    private static final String RESDIRPATH = "LuckymoneyRes";
    private static final String TAG = "com.miui.luckymoney.utils.ResFileUtils";

    public static void cleanPNGRes(Context context) {
        if (context != null && CommonConfig.getInstance(context).shouldCleanResDir()) {
            Log.i(TAG, "Clean up *.png files.");
            CommonConfig.getInstance(context).setShouldCleanResDir(false);
            File file = new File(context.getFilesDir().toString() + File.separator);
            if (file.isDirectory()) {
                String[] list = file.list();
                for (int i = 0; i < list.length; i++) {
                    if (list[i].endsWith(".png")) {
                        new File(file, list[i]).delete();
                    }
                }
            }
        }
    }

    public static void cleanResDir(Context context, String str) {
        if (context != null) {
            File file = new File(context.getFilesDir().toString() + File.separator + RESDIRPATH + File.separator + str + File.separator);
            if (file.exists() && file.isDirectory()) {
                deleteFiles(file);
            }
        }
    }

    public static boolean createDir(String str) {
        File file = new File(str);
        if (file.exists()) {
            return false;
        }
        return file.mkdirs();
    }

    private static void deleteFiles(File file) {
        if (file.isDirectory()) {
            String[] list = file.list();
            for (String file2 : list) {
                new File(file, file2).delete();
            }
        }
    }

    public static String getResDirPath(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return null;
        }
        String str2 = context.getFilesDir().toString() + File.separator + RESDIRPATH + File.separator + str + File.separator;
        createDir(str2);
        return str2;
    }

    public static File getResFile(Context context, String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            return null;
        }
        return new File(getResDirPath(context, str), str2);
    }
}

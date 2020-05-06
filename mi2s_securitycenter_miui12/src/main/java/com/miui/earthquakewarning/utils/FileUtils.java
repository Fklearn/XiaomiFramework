package com.miui.earthquakewarning.utils;

import android.content.Context;
import android.util.Log;
import com.miui.gamebooster.m.C0382m;
import java.io.File;

public class FileUtils {
    private static final String EARTHQUAKE_WARNING_DIR = "earthquakewarning";
    private static final String SIGNATURE_FILE_NAME = "signatures";
    private static final String TAG = "FileUtils";

    public static void deleteSignature(Context context) {
        try {
            File file = new File(context.getFilesDir().getPath() + File.separator + EARTHQUAKE_WARNING_DIR + File.separator + SIGNATURE_FILE_NAME);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, "delete file error" + e);
        }
    }

    public static String getSignatureFromData(Context context) {
        return C0382m.b(EARTHQUAKE_WARNING_DIR, SIGNATURE_FILE_NAME, context);
    }

    public static void saveSignatureToData(String str, Context context) {
        C0382m.a(EARTHQUAKE_WARNING_DIR, SIGNATURE_FILE_NAME, str, context);
    }
}

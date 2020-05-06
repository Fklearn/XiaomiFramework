package com.miui.internal.vip.utils;

import android.text.TextUtils;
import java.io.File;
import java.util.regex.Pattern;
import miui.telephony.phonenumber.Prefix;

public class FileServerUtil {
    static final Pattern MARKET_FILE = Pattern.compile("^[0-9a-z]+$");
    static final String[] SPECIAL_DIR = {"MiVip/", "AppStore/"};
    public static final String TAG = "FileServerUtil";

    public static String getUrlName(String url) {
        String urlLocal = url != null ? url.trim() : null;
        return !TextUtils.isEmpty(urlLocal) ? urlLocal.substring(urlLocal.lastIndexOf(File.separator) + 1) : Prefix.EMPTY;
    }

    public static int indexOfSpecialDir(String url) {
        int index = -1;
        if (url == null) {
            return -1;
        }
        for (String dir : SPECIAL_DIR) {
            index = url.indexOf(dir);
            if (index >= 0) {
                break;
            }
        }
        return index;
    }

    public static boolean isFileOnMarketServer(String url) {
        return indexOfSpecialDir(url) >= 0 && MARKET_FILE.matcher(getUrlName(url)).find();
    }
}

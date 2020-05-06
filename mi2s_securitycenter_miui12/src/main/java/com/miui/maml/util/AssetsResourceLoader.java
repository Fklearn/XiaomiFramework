package com.miui.maml.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;

public class AssetsResourceLoader extends ResourceLoader {
    private static final String LOG_TAG = "AssetsResourceLoader";
    private Context mContext;
    private String mResourcePath;

    public AssetsResourceLoader(Context context, String str) {
        this.mContext = context.getApplicationContext();
        this.mResourcePath = str;
    }

    public InputStream getInputStream(String str, long[] jArr) {
        InputStream inputStream = null;
        if (!TextUtils.isEmpty(str)) {
            try {
                AssetManager assets = this.mContext.getAssets();
                inputStream = assets.open(this.mResourcePath + "/" + str);
                if (jArr != null && jArr.length > 0) {
                    jArr[0] = (long) inputStream.available();
                }
            } catch (IOException unused) {
                Log.d(LOG_TAG, "resource " + str + " do not exists");
            }
        }
        return inputStream;
    }

    public boolean resourceExists(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                AssetManager assets = this.mContext.getAssets();
                InputStream open = assets.open(this.mResourcePath + "/" + str);
                if (open != null) {
                    try {
                        open.close();
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException unused) {
                Log.d(LOG_TAG, "resource " + str + " do not exists");
            }
        }
        return false;
    }
}

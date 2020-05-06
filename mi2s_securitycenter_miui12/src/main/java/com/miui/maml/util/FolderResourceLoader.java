package com.miui.maml.util;

import android.text.TextUtils;
import com.miui.maml.ResourceLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FolderResourceLoader extends ResourceLoader {
    private String mResourcePath;

    public FolderResourceLoader(String str) {
        this.mResourcePath = str;
    }

    public InputStream getInputStream(String str, long[] jArr) {
        FileInputStream fileInputStream;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            fileInputStream = new FileInputStream(this.mResourcePath + "/" + str);
            if (jArr != null) {
                try {
                    if (jArr.length > 0) {
                        jArr[0] = (long) fileInputStream.available();
                    }
                } catch (Exception e) {
                    e = e;
                    e.printStackTrace();
                    return fileInputStream;
                }
            }
        } catch (Exception e2) {
            e = e2;
            fileInputStream = null;
            e.printStackTrace();
            return fileInputStream;
        }
        return fileInputStream;
    }

    public boolean resourceExists(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return new File(this.mResourcePath + "/" + str).exists();
    }
}

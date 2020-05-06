package com.miui.maml.util;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourceLoader extends ResourceLoader {
    private static final String LOG_TAG = "ZipResourceLoader";
    private String mInnerPath;
    private Object mLock;
    private String mResourcePath;
    private ZipFile mZipFile;

    public ZipResourceLoader(String str) {
        this(str, (String) null, (String) null);
    }

    public ZipResourceLoader(String str, String str2) {
        this(str, str2, (String) null);
    }

    public ZipResourceLoader(String str, String str2, String str3) {
        this.mLock = new Object();
        if (!TextUtils.isEmpty(str)) {
            this.mResourcePath = str;
            this.mInnerPath = str2 == null ? "" : str2;
            if (str3 != null) {
                this.mManifestName = str3;
            }
            init();
            return;
        }
        throw new IllegalArgumentException("empty zip path");
    }

    private void close() {
        synchronized (this.mLock) {
            if (this.mZipFile != null) {
                try {
                    this.mZipFile.close();
                } catch (IOException unused) {
                }
                this.mZipFile = null;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        close();
        super.finalize();
    }

    public void finish() {
        close();
        super.finish();
    }

    public InputStream getInputStream(String str, long[] jArr) {
        if (this.mZipFile == null || str == null) {
            return null;
        }
        synchronized (this.mLock) {
            if (this.mZipFile != null) {
                ZipFile zipFile = this.mZipFile;
                ZipEntry entry = zipFile.getEntry(this.mInnerPath + str);
                if (entry == null) {
                    return null;
                }
                if (jArr != null) {
                    try {
                        jArr[0] = entry.getSize();
                    } catch (IOException e) {
                        Log.d(LOG_TAG, e.toString());
                        return null;
                    }
                }
                InputStream inputStream = this.mZipFile.getInputStream(entry);
                return inputStream;
            }
        }
    }

    public void init() {
        super.init();
        synchronized (this.mLock) {
            if (this.mZipFile == null) {
                try {
                    this.mZipFile = new ZipFile(this.mResourcePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "fail to init zip file: " + this.mResourcePath);
                }
            }
        }
    }

    public boolean resourceExists(String str) {
        boolean z = false;
        if (this.mZipFile == null || str == null) {
            return false;
        }
        synchronized (this.mLock) {
            if (this.mZipFile != null) {
                if (str != null) {
                    ZipFile zipFile = this.mZipFile;
                    if (zipFile.getEntry(this.mInnerPath + str) != null) {
                        z = true;
                    }
                }
            }
        }
        return z;
    }
}

package com.miui.internal.vip;

import android.text.TextUtils;
import com.miui.internal.vip.utils.ImageDownloader;
import com.miui.internal.vip.utils.Utils;
import com.miui.internal.vip.utils.WebContentDownloader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import miui.util.IOUtils;
import miui.vip.VipService;

public class VipResInputStream extends InputStream {
    String mIconName;
    InputStream mIs;
    int mUserLevel;

    public VipResInputStream(String iconName, int level) {
        this.mIconName = iconName;
        this.mUserLevel = level;
    }

    public int read() throws IOException {
        open();
        InputStream inputStream = this.mIs;
        if (inputStream != null) {
            return inputStream.read();
        }
        return -1;
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        open();
        InputStream inputStream = this.mIs;
        if (inputStream != null) {
            return inputStream.read(buffer, byteOffset, byteCount);
        }
        return -1;
    }

    public void close() throws IOException {
        super.close();
        IOUtils.closeQuietly(this.mIs);
    }

    /* access modifiers changed from: package-private */
    public void open() {
        if (this.mIs == null) {
            try {
                this.mIs = openIconStream();
            } catch (Exception e) {
                Utils.logW("VipResInputStream.open failed, %s", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public InputStream openIconStream() throws Exception {
        if (!TextUtils.equals(this.mIconName, VipService.VIP_LEVEL_ICON)) {
            return null;
        }
        String url = String.format(VipConstants.LEVEL_IMG, new Object[]{Integer.valueOf(this.mUserLevel)});
        String path = ImageDownloader.getImageFilePathAndClearUnusedFiles(Utils.getContext(), url, VipService.VIP_LEVEL_ICON, true);
        File file = new File(path);
        if (!file.exists() || file.length() <= 0) {
            return new WebContentDownloader(url, path);
        }
        return new FileInputStream(file.getPath());
    }
}

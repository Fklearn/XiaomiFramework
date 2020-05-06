package com.miui.internal.vip.utils;

import android.os.SystemClock;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.net.ConnectivityHelper;
import miui.net.http.HttpResponse;
import miui.os.FileUtils;
import miui.util.async.Cacheable;
import miui.util.async.tasks.HttpTask;

public class ImageDownloadTask extends HttpTask<File> implements Cacheable {
    static final String CONTENT_LENGTH = "content-length";
    static final long DELAY = TimeUnit.SECONDS.toMillis(2);
    static final int RETRY_TIMES = 3;
    final String mSavePath;
    final String mUrl;
    AtomicBoolean mWaitNetwork = new AtomicBoolean(false);

    ImageDownloadTask(String url, String savePath) {
        super(url);
        this.mUrl = url;
        this.mSavePath = savePath;
    }

    public String getCacheKey() {
        return this.mSavePath;
    }

    public int sizeOf(Object o) {
        String str = this.mSavePath;
        if (str != null) {
            return str.getBytes().length;
        }
        return 0;
    }

    public File doLoad() throws Exception {
        int retryTimes = 0;
        File file = null;
        while (file == null && retryTimes < 3) {
            if (retryTimes > 0) {
                SystemClock.sleep(DELAY * ((long) retryTimes));
            }
            this.mWaitNetwork.set(!ConnectivityHelper.getInstance().isNetworkConnected());
            if (this.mWaitNetwork.get()) {
                break;
            }
            HttpResponse response = request();
            long length = response.getContentLength();
            File file2 = new File(this.mSavePath);
            Utils.logI("ImageDownloadTask, %s, isCopySuccess = %s, file.exists = %s, length = %d, file.length = %d, content-length: %s", this.mUrl, Boolean.valueOf(FileUtils.copyToFile(response.getContent(), file2)), Boolean.valueOf(file2.exists()), Long.valueOf(length), Long.valueOf(file2.length()), response.getHeaders().get(CONTENT_LENGTH));
            file = checkIfDownloadSuccess(file2, length) ? file2 : null;
            response.release();
            retryTimes++;
        }
        return file;
    }

    public boolean isWaitNetwork() {
        return this.mWaitNetwork.get();
    }

    private static boolean checkIfDownloadSuccess(File file, long length) {
        boolean ret = file.exists();
        if (!ret) {
            return ret;
        }
        if (file.length() != 0 && (length <= 0 || file.length() == length)) {
            return ret;
        }
        Utils.log("ImageDownloadTask, file is corrupted, file.length = %d, deleted = %s", Long.valueOf(file.length()), Boolean.valueOf(file.delete()));
        return false;
    }
}

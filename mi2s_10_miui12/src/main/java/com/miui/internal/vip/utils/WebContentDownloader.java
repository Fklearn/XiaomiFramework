package com.miui.internal.vip.utils;

import android.text.TextUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import miui.telephony.phonenumber.Prefix;
import miui.util.IOUtils;

public class WebContentDownloader extends InputStream {
    static final String[] EXT_IMAGES = {".jpg", ".jpeg", ".png", ".webp", ".bmp", ".mng", ".gif", ".apng", ".ico"};
    static final long INTERVAL = TimeUnit.SECONDS.toMillis(1);
    static final int MAX_RETRY_TIMES = 3;
    static final String QUESTION_MARK = "?";
    static final String TAG = "WebContentDownloader";
    public static final int TIMEOUT_CONNECTION = ((int) TimeUnit.SECONDS.toMillis(6));
    public static final int TIMEOUT_SOCKET = ((int) TimeUnit.SECONDS.toMillis(4));
    static final long WAIT_TIME = TimeUnit.SECONDS.toMillis(5);
    HttpURLConnection mConn;
    boolean mFailed = false;
    InputStream mIs;
    long mReadBytes = 0;
    int mRetryTimes;
    String mUrl;
    FileWriter mWriter;

    public WebContentDownloader(String url, String path) {
        this.mUrl = url;
        if (!TextUtils.isEmpty(path)) {
            this.mWriter = new FileWriter(path);
        }
    }

    private void openIfNeed() {
        if (this.mIs == null) {
            openConnection();
            this.mFailed = this.mIs == null;
        }
    }

    public int read() throws IOException {
        openIfNeed();
        InputStream inputStream = this.mIs;
        if (inputStream != null) {
            try {
                int ch = inputStream.read();
                if (ch >= 0 && this.mWriter != null) {
                    this.mWriter.writeToFile(ch);
                }
                return ch;
            } catch (Exception e) {
                Utils.logW("WebContentDownloader, InputStreamProxy.read failed, url = %s, %s", this.mUrl, e);
            }
        }
        this.mFailed = true;
        return -1;
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        openIfNeed();
        InputStream inputStream = this.mIs;
        if (inputStream != null) {
            try {
                int size = inputStream.read(buffer, byteOffset, byteCount);
                if (size > 0 && this.mWriter != null) {
                    this.mWriter.writeToFile(buffer, byteOffset, size);
                }
                this.mReadBytes = size >= 0 ? this.mReadBytes + ((long) size) : this.mReadBytes;
                return size;
            } catch (Exception e) {
                Utils.logW("WebContentDownloader, InputStreamProxy.read buffer failed, url = %s, %s", this.mUrl, e);
            }
        }
        this.mFailed = true;
        return -1;
    }

    public void close() throws IOException {
        super.close();
        try {
            disconnect();
            if (this.mWriter != null) {
                this.mWriter.mFailed.set(this.mFailed);
                this.mWriter.close();
            }
            this.mWriter = null;
            if (this.mFailed && isImageFile(this.mUrl)) {
                Utils.logW("WebContentDownloaderInputStreamProxy.close, failed, image, mRetryTiems = %d", Integer.valueOf(this.mRetryTimes));
            }
        } finally {
            IOUtils.closeQuietly(this.mIs);
            this.mIs = null;
        }
    }

    private void openConnection() {
        int retryTimes = 0;
        InputStream is = null;
        while (retryTimes < 3) {
            try {
                this.mConn = (HttpURLConnection) new URL(this.mUrl).openConnection();
                this.mConn.setRequestProperty("Accept-Encoding", "gzip");
                this.mConn.setUseCaches(false);
                this.mConn.setConnectTimeout(TIMEOUT_CONNECTION);
                this.mConn.setReadTimeout(TIMEOUT_SOCKET);
                InputStream is2 = this.mConn.getInputStream();
                if (TextUtils.equals(this.mConn.getHeaderField("Content-Encoding"), "gzip")) {
                    this.mIs = new GZIPInputStream(is2);
                    return;
                } else {
                    this.mIs = new BufferedInputStream(is2);
                    return;
                }
            } catch (Exception e) {
                Utils.logW("WebContentDownloaderopenConnection failed, %s, %s", this.mUrl, e);
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(this.mIs);
                is = null;
                this.mIs = null;
                disconnect();
                retryTimes++;
            }
        }
    }

    private void disconnect() {
        HttpURLConnection httpURLConnection = this.mConn;
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
            this.mConn = null;
        }
    }

    static String getExtName(String path) {
        int index;
        if (TextUtils.isEmpty(path) || (index = path.lastIndexOf(".")) <= 0 || index >= path.length() - 1) {
            return Prefix.EMPTY;
        }
        String ext = path.substring(index);
        int qIndex = ext.indexOf(QUESTION_MARK);
        if (qIndex > 0) {
            return ext.substring(0, qIndex);
        }
        return ext;
    }

    static boolean isImageFile(String path) {
        if (FileServerUtil.isFileOnMarketServer(path)) {
            return true;
        }
        String fileExt = getExtName(path);
        if (!TextUtils.isEmpty(fileExt)) {
            for (String ext : EXT_IMAGES) {
                if (ext.equalsIgnoreCase(fileExt)) {
                    return true;
                }
            }
        }
        return false;
    }
}

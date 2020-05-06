package com.miui.internal.vip.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.util.IOUtils;

public class FileWriter {
    static final String TAG = "CacheFileWriter";
    static final String TEMP = ".temp";
    private static final Executor sThread = RunnableHelper.createSingleThreadPool();
    AtomicBoolean mFailed = new AtomicBoolean();
    File mFile;
    OutputStream mOut;
    String mRealPath;

    public FileWriter(final String path) {
        sThread.execute(new Runnable() {
            public void run() {
                FileWriter fileWriter = FileWriter.this;
                fileWriter.mRealPath = path;
                fileWriter.mFile = new File(path + FileWriter.TEMP);
                try {
                    FileWriter.this.mOut = new FileOutputStream(FileWriter.this.mFile);
                } catch (IOException e) {
                    FileWriter.this.onFileOperationError();
                    Utils.logW("CacheFileWriterFileWriter, create file failed, %s", e);
                }
            }
        });
    }

    public void writeToFile(final int ch) {
        if (ch >= 0) {
            sThread.execute(new Runnable() {
                public void run() {
                    try {
                        FileWriter.this.mOut.write(ch);
                    } catch (Exception e) {
                        FileWriter.this.onFileOperationError();
                        Utils.logW("CacheFileWriterFileWriter, write failed, char = %d, %s", Integer.valueOf(ch), e);
                    }
                }
            });
        }
    }

    public void writeToFile(byte[] buffer, int byteOffset, final int size) {
        if (size > 0) {
            final byte[] copyBuff = new byte[size];
            System.arraycopy(buffer, byteOffset, copyBuff, 0, size);
            sThread.execute(new Runnable() {
                public void run() {
                    try {
                        FileWriter.this.mOut.write(copyBuff);
                    } catch (Exception e) {
                        FileWriter.this.onFileOperationError();
                        Utils.logW("CacheFileWriterFileWriter, write failed, size = %d, %s", Integer.valueOf(size), e);
                    }
                }
            });
        }
    }

    public void close() {
        sThread.execute(new Runnable() {
            public void run() {
                FileWriter.this.closeOutputStream();
                if (FileWriter.this.mFile.exists()) {
                    if (FileWriter.this.mFailed.get() || FileWriter.this.mFile.length() == 0) {
                        Utils.logW("CacheFileWriterFileWriter.close, download %s failed, delete", FileWriter.this.mFile.getPath());
                        FileWriter.deleteFile(FileWriter.this.mFile);
                    } else {
                        FileWriter.this.mFile.renameTo(new File(FileWriter.this.mRealPath));
                    }
                }
                FileWriter.this.mFile = null;
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void closeOutputStream() {
        IOUtils.closeQuietly(this.mOut);
        this.mOut = null;
    }

    /* access modifiers changed from: package-private */
    public void onFileOperationError() {
        closeOutputStream();
        deleteFile(this.mFile);
    }

    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            if (!file.isDirectory()) {
                deleteNonDirectory(file);
                return;
            }
            File[] subs = file.listFiles();
            if (subs != null && subs.length > 0) {
                for (File sub : subs) {
                    deleteFile(sub);
                }
            }
            deleteNonDirectory(file);
        }
    }

    private static void deleteNonDirectory(File file) {
        boolean suc = file.delete();
        Object[] objArr = new Object[3];
        objArr[0] = file.isDirectory() ? "directory" : "file";
        objArr[1] = file.getPath();
        objArr[2] = suc ? "suc" : "failed";
        Utils.logW("CacheFileWriterdelete %s %s %s", objArr);
    }
}

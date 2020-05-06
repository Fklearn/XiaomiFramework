package com.miui.networkassistant.config;

import android.content.Context;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConfigFile {
    private static final String EN_KEY = "_&^%&*20131128#$%)%^@";
    private static final byte[] HEADER = {119, 97, 108, 105, 109, 105, 110, 105};
    private Context mContext;
    protected ConcurrentHashMap<String, String> mPairValues;
    private ConcurrentHashMap<String, String> mTrans;

    protected ConfigFile() {
    }

    protected ConfigFile(Context context) {
        init(context);
    }

    private String getFromTrans(String str) {
        ConcurrentHashMap<String, String> concurrentHashMap = this.mTrans;
        if (concurrentHashMap == null) {
            return null;
        }
        return concurrentHashMap.get(str);
    }

    private synchronized String readString(DataInputStream dataInputStream) {
        byte[] bArr;
        bArr = new byte[dataInputStream.readShort()];
        dataInputStream.read(bArr);
        return new String(bArr);
    }

    private synchronized void saveConfig() {
        File file = new File(this.mContext.getFilesDir().getAbsoluteFile() + File.separator + getFileName());
        FileOutputStream fileOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.write(HEADER);
            dataOutputStream.writeShort(this.mPairValues.size());
            for (Map.Entry next : this.mPairValues.entrySet()) {
                writeString(dataOutputStream, (String) next.getKey());
                writeString(dataOutputStream, (String) next.getValue());
            }
            byte[] encrypt = encrypt(byteArrayOutputStream.toByteArray(), EN_KEY.getBytes());
            FileOutputStream fileOutputStream2 = new FileOutputStream(file, false);
            try {
                fileOutputStream2.write(encrypt);
                fileOutputStream2.flush();
                byteArrayOutputStream.close();
                try {
                    dataOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    fileOutputStream2.close();
                } catch (Exception e2) {
                    e = e2;
                }
            } catch (Exception e3) {
                e = e3;
                fileOutputStream = fileOutputStream2;
                try {
                    e.printStackTrace();
                    try {
                        dataOutputStream.close();
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                    try {
                        fileOutputStream.close();
                    } catch (Exception e5) {
                        e = e5;
                    }
                    return;
                } catch (Throwable th) {
                    th = th;
                    try {
                        dataOutputStream.close();
                    } catch (Exception e6) {
                        e6.printStackTrace();
                    }
                    try {
                        fileOutputStream.close();
                    } catch (Exception e7) {
                        e7.printStackTrace();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                fileOutputStream = fileOutputStream2;
                dataOutputStream.close();
                fileOutputStream.close();
                throw th;
            }
        } catch (Exception e8) {
            e = e8;
            e.printStackTrace();
            dataOutputStream.close();
            fileOutputStream.close();
            return;
        }
        return;
        e.printStackTrace();
        return;
    }

    private boolean setToTrans(String str, String str2) {
        ConcurrentHashMap<String, String> concurrentHashMap = this.mTrans;
        if (concurrentHashMap == null) {
            return false;
        }
        concurrentHashMap.put(str, str2);
        return true;
    }

    private synchronized void writeString(DataOutputStream dataOutputStream, String str) {
        byte[] bytes = str.getBytes();
        dataOutputStream.writeShort(bytes.length);
        dataOutputStream.write(bytes);
    }

    public synchronized void Delete(String str) {
        this.mPairValues.remove(str);
    }

    public synchronized void beginTrans() {
        if (this.mTrans != null) {
            this.mTrans = null;
        }
        this.mTrans = new ConcurrentHashMap<>(this.mPairValues);
    }

    public synchronized void clear() {
        if (this.mTrans != null) {
            this.mTrans.clear();
            this.mTrans = null;
        }
        this.mPairValues.clear();
        saveNow();
    }

    public synchronized void commitTrans() {
        if (this.mTrans != null) {
            this.mPairValues.clear();
            for (Map.Entry next : this.mTrans.entrySet()) {
                this.mPairValues.put(next.getKey(), next.getValue());
            }
            saveNow();
            this.mTrans = null;
        }
    }

    public byte[] encrypt(byte[] bArr, byte[] bArr2) {
        int i;
        byte[] bArr3 = new byte[bArr.length];
        int i2 = 0;
        while (i2 < bArr.length) {
            int i3 = 0;
            while (i3 < bArr2.length && (i = i2 + i3) < bArr.length) {
                bArr3[i] = (byte) (bArr[i] ^ bArr2[i3]);
                i3++;
            }
            i2 += bArr2.length;
        }
        return bArr3;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(5:12|13|14|15|16) */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001f, code lost:
        return (int) java.lang.Double.parseDouble(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0021, code lost:
        return r3;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x0019 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int get(java.lang.String r2, int r3) {
        /*
            r1 = this;
            monitor-enter(r1)
            java.lang.String r0 = r1.getFromTrans(r2)     // Catch:{ all -> 0x0022 }
            if (r0 != 0) goto L_0x000f
            java.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.String> r0 = r1.mPairValues     // Catch:{ all -> 0x0022 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x0022 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x0022 }
        L_0x000f:
            if (r0 != 0) goto L_0x0013
            monitor-exit(r1)
            return r3
        L_0x0013:
            int r2 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0019 }
            monitor-exit(r1)
            return r2
        L_0x0019:
            double r2 = java.lang.Double.parseDouble(r2)     // Catch:{ Exception -> 0x0020 }
            int r2 = (int) r2
            monitor-exit(r1)
            return r2
        L_0x0020:
            monitor-exit(r1)
            return r3
        L_0x0022:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.config.ConfigFile.get(java.lang.String, int):int");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(5:12|13|14|15|16) */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001f, code lost:
        return (long) java.lang.Double.parseDouble(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0021, code lost:
        return r3;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x0019 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized long get(java.lang.String r2, long r3) {
        /*
            r1 = this;
            monitor-enter(r1)
            java.lang.String r0 = r1.getFromTrans(r2)     // Catch:{ all -> 0x0022 }
            if (r0 != 0) goto L_0x000f
            java.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.String> r0 = r1.mPairValues     // Catch:{ all -> 0x0022 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x0022 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x0022 }
        L_0x000f:
            if (r0 != 0) goto L_0x0013
            monitor-exit(r1)
            return r3
        L_0x0013:
            long r2 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x0019 }
            monitor-exit(r1)
            return r2
        L_0x0019:
            double r2 = java.lang.Double.parseDouble(r2)     // Catch:{ Exception -> 0x0020 }
            long r2 = (long) r2
            monitor-exit(r1)
            return r2
        L_0x0020:
            monitor-exit(r1)
            return r3
        L_0x0022:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.config.ConfigFile.get(java.lang.String, long):long");
    }

    public synchronized String get(String str) {
        String fromTrans;
        fromTrans = getFromTrans(str);
        if (fromTrans == null) {
            fromTrans = this.mPairValues.get(str);
        }
        return fromTrans;
    }

    public synchronized String get(String str, String str2) {
        String fromTrans = getFromTrans(str);
        if (fromTrans != null) {
            return fromTrans;
        }
        String str3 = this.mPairValues.get(str);
        return str3 == null ? str2 : str3;
    }

    public synchronized boolean get(String str, boolean z) {
        String fromTrans = getFromTrans(str);
        if (fromTrans == null) {
            fromTrans = this.mPairValues.get(str);
        }
        if (fromTrans == null) {
            return z;
        }
        try {
            return Boolean.parseBoolean(fromTrans);
        } catch (Exception unused) {
            return z;
        }
    }

    /* access modifiers changed from: protected */
    public abstract String getFileName();

    /* access modifiers changed from: protected */
    public void init(Context context) {
        this.mPairValues = new ConcurrentHashMap<>(256);
        this.mContext = context.getApplicationContext();
        loadConfig();
    }

    public void loadConfig() {
        int length;
        this.mPairValues.clear();
        File file = new File(this.mContext.getFilesDir().getAbsoluteFile() + File.separator + getFileName());
        if (file.exists() && (length = (int) file.length()) != 0) {
            byte[] bArr = new byte[length];
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.read(bArr);
                fileInputStream.close();
                byte[] encrypt = encrypt(bArr, EN_KEY.getBytes());
                int length2 = HEADER.length;
                int i = 0;
                while (i < length2) {
                    if (encrypt[i] == HEADER[i]) {
                        i++;
                    } else {
                        return;
                    }
                }
                DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(encrypt));
                try {
                    dataInputStream.skip((long) length2);
                    short readShort = dataInputStream.readShort();
                    for (int i2 = 0; i2 < readShort; i2++) {
                        String readString = readString(dataInputStream);
                        String readString2 = readString(dataInputStream);
                        boolean z = true;
                        boolean z2 = readString != null;
                        if (readString2 == null) {
                            z = false;
                        }
                        if (z && z2) {
                            this.mPairValues.put(readString, readString2);
                        }
                    }
                    try {
                        dataInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    dataInputStream.close();
                } catch (Throwable th) {
                    try {
                        dataInputStream.close();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                    throw th;
                }
            } catch (Exception unused) {
            }
        }
    }

    public void release() {
        this.mPairValues.clear();
    }

    public synchronized void remove(String str) {
        Delete(str);
    }

    public synchronized void rollBackTrans() {
        if (this.mTrans != null) {
            this.mTrans.clear();
            this.mTrans = null;
        }
    }

    public synchronized void saveNow() {
        saveConfig();
    }

    public synchronized void set(String str, String str2) {
        if (!setToTrans(str, str2)) {
            this.mPairValues.put(str, str2);
        }
    }

    public synchronized void set(String str, String str2, boolean z) {
        set(str, str2);
    }
}

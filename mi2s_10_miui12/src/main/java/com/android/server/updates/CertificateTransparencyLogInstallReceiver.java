package com.android.server.updates;

import android.os.FileUtils;
import android.util.Base64;
import com.android.internal.util.HexDump;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CertificateTransparencyLogInstallReceiver extends ConfigUpdateInstallReceiver {
    private static final String LOGDIR_PREFIX = "logs-";
    private static final String TAG = "CTLogInstallReceiver";

    public CertificateTransparencyLogInstallReceiver() {
        super("/data/misc/keychain/trusted_ct_logs/", "ct_logs", "metadata/", "version");
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0135, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0136, code lost:
        android.os.FileUtils.deleteContentsAndDir(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0139, code lost:
        throw r1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0135 A[ExcHandler: IOException | RuntimeException (r1v6 'e' java.lang.Exception A[CUSTOM_DECLARE]), Splitter:B:11:0x006a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void install(byte[] r9, int r10) throws java.io.IOException {
        /*
            r8 = this;
            java.io.File r0 = r8.updateDir
            r0.mkdir()
            java.io.File r0 = r8.updateDir
            boolean r0 = r0.isDirectory()
            java.lang.String r1 = "Unable to make directory "
            if (r0 == 0) goto L_0x0157
            java.io.File r0 = r8.updateDir
            r2 = 0
            r3 = 1
            boolean r0 = r0.setReadable(r3, r2)
            if (r0 == 0) goto L_0x013a
            java.io.File r0 = new java.io.File
            java.io.File r4 = r8.updateDir
            java.lang.String r5 = "current"
            r0.<init>(r4, r5)
            java.io.File r4 = new java.io.File
            java.io.File r5 = r8.updateDir
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "logs-"
            r6.append(r7)
            java.lang.String r7 = java.lang.String.valueOf(r10)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r4.<init>(r5, r6)
            boolean r5 = r4.exists()
            if (r5 == 0) goto L_0x006a
            java.lang.String r5 = r4.getCanonicalPath()
            java.lang.String r6 = r0.getCanonicalPath()
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x0067
            java.io.File r1 = r8.updateDir
            java.io.File r2 = r8.updateVersion
            long r5 = (long) r10
            java.lang.String r3 = java.lang.Long.toString(r5)
            byte[] r3 = r3.getBytes()
            r8.writeUpdate(r1, r2, r3)
            r8.deleteOldLogDirectories()
            return
        L_0x0067:
            android.os.FileUtils.deleteContentsAndDir(r4)
        L_0x006a:
            r4.mkdir()     // Catch:{ IOException | RuntimeException -> 0x0135 }
            boolean r5 = r4.isDirectory()     // Catch:{ IOException | RuntimeException -> 0x0135 }
            if (r5 == 0) goto L_0x011c
            boolean r1 = r4.setReadable(r3, r2)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            if (r1 == 0) goto L_0x00fc
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00f3, IOException | RuntimeException -> 0x0135 }
            java.lang.String r3 = new java.lang.String     // Catch:{ JSONException -> 0x00f3, IOException | RuntimeException -> 0x0135 }
            java.nio.charset.Charset r5 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ JSONException -> 0x00f3, IOException | RuntimeException -> 0x0135 }
            r3.<init>(r9, r5)     // Catch:{ JSONException -> 0x00f3, IOException | RuntimeException -> 0x0135 }
            r1.<init>(r3)     // Catch:{ JSONException -> 0x00f3, IOException | RuntimeException -> 0x0135 }
            java.lang.String r3 = "logs"
            org.json.JSONArray r3 = r1.getJSONArray(r3)     // Catch:{ JSONException -> 0x00f3, IOException | RuntimeException -> 0x0135 }
        L_0x008d:
            int r5 = r3.length()     // Catch:{ JSONException -> 0x00f3, IOException | RuntimeException -> 0x0135 }
            if (r2 >= r5) goto L_0x009d
            org.json.JSONObject r5 = r3.getJSONObject(r2)     // Catch:{ JSONException -> 0x00f3, IOException | RuntimeException -> 0x0135 }
            r8.installLog(r4, r5)     // Catch:{ JSONException -> 0x00f3, IOException | RuntimeException -> 0x0135 }
            int r2 = r2 + 1
            goto L_0x008d
        L_0x009d:
            java.io.File r1 = new java.io.File     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.io.File r2 = r8.updateDir     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.String r3 = "new_symlink"
            r1.<init>(r2, r3)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.String r2 = r4.getCanonicalPath()     // Catch:{ ErrnoException -> 0x00ea, IOException | RuntimeException -> 0x0135 }
            java.lang.String r3 = r1.getCanonicalPath()     // Catch:{ ErrnoException -> 0x00ea, IOException | RuntimeException -> 0x0135 }
            android.system.Os.symlink(r2, r3)     // Catch:{ ErrnoException -> 0x00ea, IOException | RuntimeException -> 0x0135 }
            java.io.File r2 = r0.getAbsoluteFile()     // Catch:{ IOException | RuntimeException -> 0x0135 }
            r1.renameTo(r2)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "CT log directory updated to "
            r1.append(r2)
            java.lang.String r2 = r4.getAbsolutePath()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "CTLogInstallReceiver"
            android.util.Slog.i(r2, r1)
            java.io.File r1 = r8.updateDir
            java.io.File r2 = r8.updateVersion
            long r5 = (long) r10
            java.lang.String r3 = java.lang.Long.toString(r5)
            byte[] r3 = r3.getBytes()
            r8.writeUpdate(r1, r2, r3)
            r8.deleteOldLogDirectories()
            return
        L_0x00ea:
            r2 = move-exception
            java.io.IOException r3 = new java.io.IOException     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.String r5 = "Failed to create symlink"
            r3.<init>(r5, r2)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            throw r3     // Catch:{ IOException | RuntimeException -> 0x0135 }
        L_0x00f3:
            r1 = move-exception
            java.io.IOException r2 = new java.io.IOException     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.String r3 = "Failed to parse logs"
            r2.<init>(r3, r1)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            throw r2     // Catch:{ IOException | RuntimeException -> 0x0135 }
        L_0x00fc:
            java.io.IOException r1 = new java.io.IOException     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException | RuntimeException -> 0x0135 }
            r2.<init>()     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.String r3 = "Failed to set "
            r2.append(r3)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.String r3 = r4.getCanonicalPath()     // Catch:{ IOException | RuntimeException -> 0x0135 }
            r2.append(r3)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.String r3 = " readable"
            r2.append(r3)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException | RuntimeException -> 0x0135 }
            r1.<init>(r2)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            throw r1     // Catch:{ IOException | RuntimeException -> 0x0135 }
        L_0x011c:
            java.io.IOException r2 = new java.io.IOException     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IOException | RuntimeException -> 0x0135 }
            r3.<init>()     // Catch:{ IOException | RuntimeException -> 0x0135 }
            r3.append(r1)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.String r1 = r4.getCanonicalPath()     // Catch:{ IOException | RuntimeException -> 0x0135 }
            r3.append(r1)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            java.lang.String r1 = r3.toString()     // Catch:{ IOException | RuntimeException -> 0x0135 }
            r2.<init>(r1)     // Catch:{ IOException | RuntimeException -> 0x0135 }
            throw r2     // Catch:{ IOException | RuntimeException -> 0x0135 }
        L_0x0135:
            r1 = move-exception
            android.os.FileUtils.deleteContentsAndDir(r4)
            throw r1
        L_0x013a:
            java.io.IOException r0 = new java.io.IOException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unable to set permissions on "
            r1.append(r2)
            java.io.File r2 = r8.updateDir
            java.lang.String r2 = r2.getCanonicalPath()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0157:
            java.io.IOException r0 = new java.io.IOException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            java.io.File r1 = r8.updateDir
            java.lang.String r1 = r1.getCanonicalPath()
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.updates.CertificateTransparencyLogInstallReceiver.install(byte[], int):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0060, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0069, code lost:
        throw r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void installLog(java.io.File r9, org.json.JSONObject r10) throws java.io.IOException {
        /*
            r8 = this;
            java.lang.String r0 = "description"
            java.lang.String r1 = "url"
            java.lang.String r2 = "key"
            java.lang.String r3 = r10.getString(r2)     // Catch:{ JSONException -> 0x006a }
            java.lang.String r3 = r8.getLogFileName(r3)     // Catch:{ JSONException -> 0x006a }
            java.io.File r4 = new java.io.File     // Catch:{ JSONException -> 0x006a }
            r4.<init>(r9, r3)     // Catch:{ JSONException -> 0x006a }
            java.io.OutputStreamWriter r5 = new java.io.OutputStreamWriter     // Catch:{ JSONException -> 0x006a }
            java.io.FileOutputStream r6 = new java.io.FileOutputStream     // Catch:{ JSONException -> 0x006a }
            r6.<init>(r4)     // Catch:{ JSONException -> 0x006a }
            java.nio.charset.Charset r7 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ JSONException -> 0x006a }
            r5.<init>(r6, r7)     // Catch:{ JSONException -> 0x006a }
            java.lang.String r6 = r10.getString(r2)     // Catch:{ all -> 0x005e }
            r8.writeLogEntry(r5, r2, r6)     // Catch:{ all -> 0x005e }
            java.lang.String r2 = r10.getString(r1)     // Catch:{ all -> 0x005e }
            r8.writeLogEntry(r5, r1, r2)     // Catch:{ all -> 0x005e }
            java.lang.String r1 = r10.getString(r0)     // Catch:{ all -> 0x005e }
            r8.writeLogEntry(r5, r0, r1)     // Catch:{ all -> 0x005e }
            r5.close()     // Catch:{ JSONException -> 0x006a }
            r0 = 1
            r1 = 0
            boolean r0 = r4.setReadable(r0, r1)     // Catch:{ JSONException -> 0x006a }
            if (r0 == 0) goto L_0x0043
            return
        L_0x0043:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ JSONException -> 0x006a }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x006a }
            r1.<init>()     // Catch:{ JSONException -> 0x006a }
            java.lang.String r2 = "Failed to set permissions on "
            r1.append(r2)     // Catch:{ JSONException -> 0x006a }
            java.lang.String r2 = r4.getCanonicalPath()     // Catch:{ JSONException -> 0x006a }
            r1.append(r2)     // Catch:{ JSONException -> 0x006a }
            java.lang.String r1 = r1.toString()     // Catch:{ JSONException -> 0x006a }
            r0.<init>(r1)     // Catch:{ JSONException -> 0x006a }
            throw r0     // Catch:{ JSONException -> 0x006a }
        L_0x005e:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0060 }
        L_0x0060:
            r1 = move-exception
            r5.close()     // Catch:{ all -> 0x0065 }
            goto L_0x0069
        L_0x0065:
            r2 = move-exception
            r0.addSuppressed(r2)     // Catch:{ JSONException -> 0x006a }
        L_0x0069:
            throw r1     // Catch:{ JSONException -> 0x006a }
        L_0x006a:
            r0 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.String r2 = "Failed to parse log"
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.updates.CertificateTransparencyLogInstallReceiver.installLog(java.io.File, org.json.JSONObject):void");
    }

    private String getLogFileName(String base64PublicKey) {
        try {
            return HexDump.toHexString(MessageDigest.getInstance("SHA-256").digest(Base64.decode(base64PublicKey, 0)), false);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeLogEntry(OutputStreamWriter out, String key, String value) throws IOException {
        out.write(key + ":" + value + "\n");
    }

    private void deleteOldLogDirectories() throws IOException {
        if (this.updateDir.exists()) {
            final File currentTarget = new File(this.updateDir, "current").getCanonicalFile();
            for (File f : this.updateDir.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return !currentTarget.equals(file) && file.getName().startsWith(CertificateTransparencyLogInstallReceiver.LOGDIR_PREFIX);
                }
            })) {
                FileUtils.deleteContentsAndDir(f);
            }
        }
    }
}

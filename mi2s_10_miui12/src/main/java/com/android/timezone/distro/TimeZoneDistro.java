package com.android.timezone.distro;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class TimeZoneDistro {
    private static final int BUFFER_SIZE = 8192;
    public static final String DISTRO_VERSION_FILE_NAME = "distro_version";
    public static final String FILE_NAME = "distro.zip";
    public static final String ICU_DATA_FILE_NAME = "icu/icu_tzdata.dat";
    private static final long MAX_GET_ENTRY_CONTENTS_SIZE = 131072;
    public static final String TZDATA_FILE_NAME = "tzdata";
    public static final String TZLOOKUP_FILE_NAME = "tzlookup.xml";
    private final InputStream inputStream;

    public TimeZoneDistro(byte[] bytes) {
        this((InputStream) new ByteArrayInputStream(bytes));
    }

    public TimeZoneDistro(InputStream inputStream2) {
        this.inputStream = inputStream2;
    }

    public DistroVersion getDistroVersion() throws DistroException, IOException {
        byte[] contents = getEntryContents(this.inputStream, DISTRO_VERSION_FILE_NAME);
        if (contents != null) {
            return DistroVersion.fromBytes(contents);
        }
        throw new DistroException("Distro version file entry not found");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r6 = r0.read(r4);
        r7 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0032, code lost:
        if (r6 == -1) goto L_0x0039;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0034, code lost:
        r5.write(r4, 0, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0039, code lost:
        r6 = r5.toByteArray();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        $closeResource((java.lang.Throwable) null, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0040, code lost:
        $closeResource((java.lang.Throwable) null, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0043, code lost:
        return r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0046, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        $closeResource(r3, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004a, code lost:
        throw r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x006d, code lost:
        throw new java.io.IOException("Entry " + r10 + " too large: " + r2.getSize());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0075, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0076, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0079, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0021, code lost:
        if (r2.getSize() > MAX_GET_ENTRY_CONTENTS_SIZE) goto L_0x004b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0023, code lost:
        r4 = new byte[8192];
        r5 = new java.io.ByteArrayOutputStream();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static byte[] getEntryContents(java.io.InputStream r9, java.lang.String r10) throws java.io.IOException {
        /*
            java.util.zip.ZipInputStream r0 = new java.util.zip.ZipInputStream
            r0.<init>(r9)
        L_0x0005:
            java.util.zip.ZipEntry r1 = r0.getNextEntry()     // Catch:{ all -> 0x0073 }
            r2 = r1
            r3 = 0
            if (r1 == 0) goto L_0x006e
            java.lang.String r1 = r2.getName()     // Catch:{ all -> 0x0073 }
            boolean r4 = r10.equals(r1)     // Catch:{ all -> 0x0073 }
            if (r4 != 0) goto L_0x0018
            goto L_0x0005
        L_0x0018:
            long r4 = r2.getSize()     // Catch:{ all -> 0x0073 }
            r6 = 131072(0x20000, double:6.47582E-319)
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 > 0) goto L_0x004b
            r4 = 8192(0x2000, float:1.14794E-41)
            byte[] r4 = new byte[r4]     // Catch:{ all -> 0x0073 }
            java.io.ByteArrayOutputStream r5 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x0073 }
            r5.<init>()     // Catch:{ all -> 0x0073 }
        L_0x002c:
            int r6 = r0.read(r4)     // Catch:{ all -> 0x0044 }
            r7 = r6
            r8 = -1
            if (r6 == r8) goto L_0x0039
            r6 = 0
            r5.write(r4, r6, r7)     // Catch:{ all -> 0x0044 }
            goto L_0x002c
        L_0x0039:
            byte[] r6 = r5.toByteArray()     // Catch:{ all -> 0x0044 }
            $closeResource(r3, r5)     // Catch:{ all -> 0x0073 }
            $closeResource(r3, r0)
            return r6
        L_0x0044:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0046 }
        L_0x0046:
            r6 = move-exception
            $closeResource(r3, r5)     // Catch:{ all -> 0x0073 }
            throw r6     // Catch:{ all -> 0x0073 }
        L_0x004b:
            java.io.IOException r3 = new java.io.IOException     // Catch:{ all -> 0x0073 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0073 }
            r4.<init>()     // Catch:{ all -> 0x0073 }
            java.lang.String r5 = "Entry "
            r4.append(r5)     // Catch:{ all -> 0x0073 }
            r4.append(r10)     // Catch:{ all -> 0x0073 }
            java.lang.String r5 = " too large: "
            r4.append(r5)     // Catch:{ all -> 0x0073 }
            long r5 = r2.getSize()     // Catch:{ all -> 0x0073 }
            r4.append(r5)     // Catch:{ all -> 0x0073 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0073 }
            r3.<init>(r4)     // Catch:{ all -> 0x0073 }
            throw r3     // Catch:{ all -> 0x0073 }
        L_0x006e:
            $closeResource(r3, r0)
            return r3
        L_0x0073:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0075 }
        L_0x0075:
            r2 = move-exception
            $closeResource(r1, r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.timezone.distro.TimeZoneDistro.getEntryContents(java.io.InputStream, java.lang.String):byte[]");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    public void extractTo(File targetDir) throws IOException {
        extractZipSafely(this.inputStream, targetDir, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004a, code lost:
        r6.getFD().sync();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        $closeResource((java.lang.Throwable) null, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0054, code lost:
        if (r12 == false) goto L_0x000c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0056, code lost:
        com.android.timezone.distro.FileUtils.makeWorldReadable(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005c, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        $closeResource(r4, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0060, code lost:
        throw r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0067, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0068, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x006b, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void extractZipSafely(java.io.InputStream r10, java.io.File r11, boolean r12) throws java.io.IOException {
        /*
            com.android.timezone.distro.FileUtils.ensureDirectoriesExist(r11, r12)
            java.util.zip.ZipInputStream r0 = new java.util.zip.ZipInputStream
            r0.<init>(r10)
            r1 = 8192(0x2000, float:1.14794E-41)
            byte[] r1 = new byte[r1]     // Catch:{ all -> 0x0065 }
        L_0x000c:
            java.util.zip.ZipEntry r2 = r0.getNextEntry()     // Catch:{ all -> 0x0065 }
            r3 = r2
            r4 = 0
            if (r2 == 0) goto L_0x0061
            java.lang.String r2 = r3.getName()     // Catch:{ all -> 0x0065 }
            java.io.File r5 = com.android.timezone.distro.FileUtils.createSubFile(r11, r2)     // Catch:{ all -> 0x0065 }
            boolean r6 = r3.isDirectory()     // Catch:{ all -> 0x0065 }
            if (r6 == 0) goto L_0x0026
            com.android.timezone.distro.FileUtils.ensureDirectoriesExist(r5, r12)     // Catch:{ all -> 0x0065 }
            goto L_0x0059
        L_0x0026:
            java.io.File r6 = r5.getParentFile()     // Catch:{ all -> 0x0065 }
            boolean r6 = r6.exists()     // Catch:{ all -> 0x0065 }
            if (r6 != 0) goto L_0x0038
            java.io.File r6 = r5.getParentFile()     // Catch:{ all -> 0x0065 }
            com.android.timezone.distro.FileUtils.ensureDirectoriesExist(r6, r12)     // Catch:{ all -> 0x0065 }
        L_0x0038:
            java.io.FileOutputStream r6 = new java.io.FileOutputStream     // Catch:{ all -> 0x0065 }
            r6.<init>(r5)     // Catch:{ all -> 0x0065 }
        L_0x003d:
            int r7 = r0.read(r1)     // Catch:{ all -> 0x005a }
            r8 = r7
            r9 = -1
            if (r7 == r9) goto L_0x004a
            r7 = 0
            r6.write(r1, r7, r8)     // Catch:{ all -> 0x005a }
            goto L_0x003d
        L_0x004a:
            java.io.FileDescriptor r7 = r6.getFD()     // Catch:{ all -> 0x005a }
            r7.sync()     // Catch:{ all -> 0x005a }
            $closeResource(r4, r6)     // Catch:{ all -> 0x0065 }
            if (r12 == 0) goto L_0x0059
            com.android.timezone.distro.FileUtils.makeWorldReadable(r5)     // Catch:{ all -> 0x0065 }
        L_0x0059:
            goto L_0x000c
        L_0x005a:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x005c }
        L_0x005c:
            r7 = move-exception
            $closeResource(r4, r6)     // Catch:{ all -> 0x0065 }
            throw r7     // Catch:{ all -> 0x0065 }
        L_0x0061:
            $closeResource(r4, r0)
            return
        L_0x0065:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0067 }
        L_0x0067:
            r2 = move-exception
            $closeResource(r1, r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.timezone.distro.TimeZoneDistro.extractZipSafely(java.io.InputStream, java.io.File, boolean):void");
    }
}

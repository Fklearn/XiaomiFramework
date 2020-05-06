package com.miui.earthquakewarning.soundplay;

import com.miui.permission.PermissionManager;

public class SntpClient {
    private static final int NTP_MODE_CLIENT = 3;
    private static final int NTP_PACKET_SIZE = 48;
    private static final int NTP_PORT = 123;
    private static final int NTP_VERSION = 3;
    private static final long OFFSET_1900_TO_1970 = 2208988800L;
    private static final int ORIGINATE_TIME_OFFSET = 24;
    private static final int RECEIVE_TIME_OFFSET = 32;
    private static final int REFERENCE_TIME_OFFSET = 16;
    private static final String TAG = "SntpClient";
    private static final int TRANSMIT_TIME_OFFSET = 40;
    private long mNtpTime;
    private long mNtpTimeReference;
    private long mRoundTripTime;

    private long read32(byte[] bArr, int i) {
        byte b2 = bArr[i];
        byte b3 = bArr[i + 1];
        byte b4 = bArr[i + 2];
        byte b5 = bArr[i + 3];
        byte b6 = b2 & true;
        int i2 = b2;
        if (b6 == true) {
            i2 = (b2 & Byte.MAX_VALUE) + 128;
        }
        byte b7 = b3 & true;
        int i3 = b3;
        if (b7 == true) {
            i3 = (b3 & Byte.MAX_VALUE) + 128;
        }
        byte b8 = b4 & true;
        int i4 = b4;
        if (b8 == true) {
            i4 = (b4 & Byte.MAX_VALUE) + 128;
        }
        byte b9 = b5 & true;
        int i5 = b5;
        if (b9 == true) {
            i5 = (b5 & Byte.MAX_VALUE) + 128;
        }
        return (((long) i2) << 24) + (((long) i3) << 16) + (((long) i4) << 8) + ((long) i5);
    }

    private long readTimeStamp(byte[] bArr, int i) {
        return ((read32(bArr, i) - OFFSET_1900_TO_1970) * 1000) + ((read32(bArr, i + 4) * 1000) / PermissionManager.PERM_ID_ACCESS_XIAOMI_ACCOUNT);
    }

    private void writeTimeStamp(byte[] bArr, int i, long j) {
        long j2 = j / 1000;
        long j3 = j - (j2 * 1000);
        long j4 = j2 + OFFSET_1900_TO_1970;
        int i2 = i + 1;
        bArr[i] = (byte) ((int) (j4 >> 24));
        int i3 = i2 + 1;
        bArr[i2] = (byte) ((int) (j4 >> 16));
        int i4 = i3 + 1;
        bArr[i3] = (byte) ((int) (j4 >> 8));
        int i5 = i4 + 1;
        bArr[i4] = (byte) ((int) (j4 >> 0));
        long j5 = (j3 * PermissionManager.PERM_ID_ACCESS_XIAOMI_ACCOUNT) / 1000;
        int i6 = i5 + 1;
        bArr[i5] = (byte) ((int) (j5 >> 24));
        int i7 = i6 + 1;
        bArr[i6] = (byte) ((int) (j5 >> 16));
        bArr[i7] = (byte) ((int) (j5 >> 8));
        bArr[i7 + 1] = (byte) ((int) (Math.random() * 255.0d));
    }

    public long getNtpTime() {
        return this.mNtpTime;
    }

    public long getNtpTimeReference() {
        return this.mNtpTimeReference;
    }

    public long getRoundTripTime() {
        return this.mRoundTripTime;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x006e  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0075  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean requestTime(java.lang.String r19, int r20) {
        /*
            r18 = this;
            r1 = r18
            r0 = 0
            r2 = 0
            java.net.DatagramSocket r3 = new java.net.DatagramSocket     // Catch:{ Exception -> 0x0072, all -> 0x006a }
            r3.<init>()     // Catch:{ Exception -> 0x0072, all -> 0x006a }
            r2 = r20
            r3.setSoTimeout(r2)     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            java.net.InetAddress r2 = java.net.InetAddress.getByName(r19)     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            r4 = 48
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            java.net.DatagramPacket r5 = new java.net.DatagramPacket     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            int r6 = r4.length     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            r7 = 123(0x7b, float:1.72E-43)
            r5.<init>(r4, r6, r2, r7)     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            r2 = 27
            r4[r0] = r2     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            long r6 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            long r8 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            r2 = 40
            r1.writeTimeStamp(r4, r2, r6)     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            r3.send(r5)     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            java.net.DatagramPacket r5 = new java.net.DatagramPacket     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            int r10 = r4.length     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            r5.<init>(r4, r10)     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            r3.receive(r5)     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            long r10 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            long r8 = r10 - r8
            long r6 = r6 + r8
            r5 = 24
            long r12 = r1.readTimeStamp(r4, r5)     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            r5 = 32
            long r14 = r1.readTimeStamp(r4, r5)     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            long r4 = r1.readTimeStamp(r4, r2)     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            long r16 = r4 - r14
            long r8 = r8 - r16
            long r14 = r14 - r12
            long r4 = r4 - r6
            long r14 = r14 + r4
            r4 = 2
            long r14 = r14 / r4
            long r6 = r6 + r14
            r1.mNtpTime = r6     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            r1.mNtpTimeReference = r10     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            r1.mRoundTripTime = r8     // Catch:{ Exception -> 0x0073, all -> 0x0068 }
            r3.close()
            r0 = 1
            return r0
        L_0x0068:
            r0 = move-exception
            goto L_0x006c
        L_0x006a:
            r0 = move-exception
            r3 = r2
        L_0x006c:
            if (r3 == 0) goto L_0x0071
            r3.close()
        L_0x0071:
            throw r0
        L_0x0072:
            r3 = r2
        L_0x0073:
            if (r3 == 0) goto L_0x0078
            r3.close()
        L_0x0078:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.earthquakewarning.soundplay.SntpClient.requestTime(java.lang.String, int):boolean");
    }
}

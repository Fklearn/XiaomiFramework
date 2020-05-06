package com.android.server;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.miui.Manifest;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.persistentdata.IPersistentDataBlockService;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.DumpState;
import com.android.server.slice.SliceClientPermissions;
import com.miui.enterprise.settings.EnterpriseSettings;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;

public class PersistentDataBlockService extends SystemService {
    public static final int DIGEST_SIZE_BYTES = 32;
    private static final int ENT_ACCESS_BLOCK_SIZE = 2048;
    private static final String FLASH_LOCK_LOCKED = "1";
    private static final String FLASH_LOCK_PROP = "ro.boot.flash.locked";
    private static final String FLASH_LOCK_UNLOCKED = "0";
    private static final int FRP_CREDENTIAL_RESERVED_SIZE = 1000;
    private static final int HEADER_SIZE = 8;
    private static final int MAX_DATA_BLOCK_SIZE = 102400;
    private static final int MAX_FRP_CREDENTIAL_HANDLE_SIZE = 996;
    private static final int MAX_TEST_MODE_DATA_SIZE = 9996;
    private static final String OEM_UNLOCK_PROP = "sys.oem_unlock_allowed";
    private static final int PARTITION_TYPE_MARKER = 428873843;
    private static final String PERSISTENT_DATA_BLOCK_PROP = "ro.frp.pst";
    /* access modifiers changed from: private */
    public static final String TAG = PersistentDataBlockService.class.getSimpleName();
    private static final int TEST_MODE_RESERVED_SIZE = 10000;
    private int mAllowedUid = -1;
    private long mBlockDeviceSize;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final String mDataBlockFile;
    private final CountDownLatch mInitDoneSignal = new CountDownLatch(1);
    /* access modifiers changed from: private */
    public PersistentDataBlockManagerInternal mInternalService = new PersistentDataBlockManagerInternal() {
        public void setFrpCredentialHandle(byte[] handle) {
            writeInternal(handle, PersistentDataBlockService.this.getFrpCredentialDataOffset(), PersistentDataBlockService.MAX_FRP_CREDENTIAL_HANDLE_SIZE);
        }

        public byte[] getFrpCredentialHandle() {
            return readInternal(PersistentDataBlockService.this.getFrpCredentialDataOffset(), PersistentDataBlockService.MAX_FRP_CREDENTIAL_HANDLE_SIZE);
        }

        public void setTestHarnessModeData(byte[] data) {
            writeInternal(data, PersistentDataBlockService.this.getTestHarnessModeDataOffset(), PersistentDataBlockService.MAX_TEST_MODE_DATA_SIZE);
        }

        public byte[] getTestHarnessModeData() {
            byte[] data = readInternal(PersistentDataBlockService.this.getTestHarnessModeDataOffset(), PersistentDataBlockService.MAX_TEST_MODE_DATA_SIZE);
            if (data == null) {
                return new byte[0];
            }
            return data;
        }

        public void clearTestHarnessModeData() {
            writeDataBuffer(PersistentDataBlockService.this.getTestHarnessModeDataOffset(), ByteBuffer.allocate(Math.min(PersistentDataBlockService.MAX_TEST_MODE_DATA_SIZE, getTestHarnessModeData().length) + 4));
        }

        private void writeInternal(byte[] data, long offset, int dataLength) {
            boolean z = true;
            int i = 0;
            Preconditions.checkArgument(data == null || data.length > 0, "data must be null or non-empty");
            if (data != null && data.length > dataLength) {
                z = false;
            }
            Preconditions.checkArgument(z, "data must not be longer than " + dataLength);
            ByteBuffer dataBuffer = ByteBuffer.allocate(dataLength + 4);
            if (data != null) {
                i = data.length;
            }
            dataBuffer.putInt(i);
            if (data != null) {
                dataBuffer.put(data);
            }
            dataBuffer.flip();
            writeDataBuffer(offset, dataBuffer);
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        private void writeDataBuffer(long offset, ByteBuffer dataBuffer) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(PersistentDataBlockService.this.mDataBlockFile));
                synchronized (PersistentDataBlockService.this.mLock) {
                    if (!PersistentDataBlockService.this.mIsWritable) {
                        IoUtils.closeQuietly(outputStream);
                        return;
                    }
                    try {
                        FileChannel channel = outputStream.getChannel();
                        channel.position(offset);
                        channel.write(dataBuffer);
                        outputStream.flush();
                        IoUtils.closeQuietly(outputStream);
                        boolean unused = PersistentDataBlockService.this.computeAndWriteDigestLocked();
                    } catch (IOException e) {
                        try {
                            Slog.e(PersistentDataBlockService.TAG, "unable to access persistent partition", e);
                        } finally {
                            IoUtils.closeQuietly(outputStream);
                        }
                    }
                }
            } catch (FileNotFoundException e2) {
                Slog.e(PersistentDataBlockService.TAG, "partition not available", e2);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        private byte[] readInternal(long offset, int maxLength) {
            if (PersistentDataBlockService.this.enforceChecksumValidity()) {
                try {
                    DataInputStream inputStream = new DataInputStream(new FileInputStream(new File(PersistentDataBlockService.this.mDataBlockFile)));
                    try {
                        synchronized (PersistentDataBlockService.this.mLock) {
                            inputStream.skip(offset);
                            int length = inputStream.readInt();
                            if (length > 0) {
                                if (length <= maxLength) {
                                    byte[] bytes = new byte[length];
                                    inputStream.readFully(bytes);
                                    IoUtils.closeQuietly(inputStream);
                                    return bytes;
                                }
                            }
                            IoUtils.closeQuietly(inputStream);
                            return null;
                        }
                    } catch (IOException e) {
                        try {
                            throw new IllegalStateException("persistent partition not readable", e);
                        } catch (Throwable th) {
                            IoUtils.closeQuietly(inputStream);
                            throw th;
                        }
                    }
                } catch (FileNotFoundException e2) {
                    throw new IllegalStateException("persistent partition not available");
                }
            } else {
                throw new IllegalStateException("invalid checksum");
            }
        }

        public void forceOemUnlockEnabled(boolean enabled) {
            synchronized (PersistentDataBlockService.this.mLock) {
                PersistentDataBlockService.this.doSetOemUnlockEnabledLocked(enabled);
                boolean unused = PersistentDataBlockService.this.computeAndWriteDigestLocked();
            }
        }
    };
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean mIsWritable = true;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private final IBinder mService = new IPersistentDataBlockService.Stub() {
        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public int writeEnterprisePersistentData(int offset, byte[] data) {
            if (!EnterpriseSettings.ENTERPRISE_ACTIVATED) {
                Slog.d(PersistentDataBlockService.TAG, "This device does not support Enterprise.");
                return -1;
            }
            Slog.d(PersistentDataBlockService.TAG, " Going to write data into Enterprise access block.");
            if (data == null || data.length == 0) {
                Slog.e(PersistentDataBlockService.TAG, " There is no data need to write, please check the byte array");
                return -1;
            } else if (offset < 0 || data.length + offset > 2048) {
                String access$000 = PersistentDataBlockService.TAG;
                Slog.e(access$000, " There is no enough space from offset: " + offset);
                return -1;
            } else {
                PersistentDataBlockService.this.enforceEnterprisePermission();
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(PersistentDataBlockService.this.mDataBlockFile));
                    synchronized (PersistentDataBlockService.this.mLock) {
                        if (!PersistentDataBlockService.this.mIsWritable) {
                            IoUtils.closeQuietly(outputStream);
                            Slog.e(PersistentDataBlockService.TAG, " Enterprise access block is not writable");
                            return -1;
                        }
                        try {
                            FileChannel channel = outputStream.getChannel();
                            channel.position(PersistentDataBlockService.this.getEnterpriseDataOffset() + ((long) offset));
                            ByteBuffer dataBuf = ByteBuffer.allocate(data.length);
                            dataBuf.put(data);
                            dataBuf.flip();
                            channel.write(dataBuf);
                            outputStream.flush();
                            IoUtils.closeQuietly(outputStream);
                            if (PersistentDataBlockService.this.computeAndWriteDigestLocked()) {
                                int length = data.length;
                                return length;
                            }
                            Slog.e(PersistentDataBlockService.TAG, " failed computing digest lock");
                            return -1;
                        } catch (IOException e) {
                            try {
                                Slog.e(PersistentDataBlockService.TAG, " failed writing to the Enterprise access block", e);
                                return -1;
                            } finally {
                                IoUtils.closeQuietly(outputStream);
                            }
                        }
                    }
                } catch (FileNotFoundException e2) {
                    Slog.e(PersistentDataBlockService.TAG, " Persistent partition not available", e2);
                    return -1;
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public byte[] readEnterprisePersistentData(int offset, int size) {
            if (!EnterpriseSettings.ENTERPRISE_ACTIVATED) {
                Slog.d(PersistentDataBlockService.TAG, "This device does not support Enterprise.");
                return null;
            }
            Slog.d(PersistentDataBlockService.TAG, " Going to read data from Enterprise access block");
            if (offset < 0 || offset + size > 2048) {
                Slog.e(PersistentDataBlockService.TAG, " invalid offset or size");
                return null;
            }
            PersistentDataBlockService.this.enforceEnterprisePermission();
            if (PersistentDataBlockService.this.enforceChecksumValidity()) {
                try {
                    DataInputStream inputStream = new DataInputStream(new FileInputStream(new File(PersistentDataBlockService.this.mDataBlockFile)));
                    try {
                        synchronized (PersistentDataBlockService.this.mLock) {
                            inputStream.skip(PersistentDataBlockService.this.getEnterpriseDataOffset() + ((long) offset));
                            byte[] data = new byte[size];
                            int read = inputStream.read(data, 0, size);
                            if (read < size) {
                                String access$000 = PersistentDataBlockService.TAG;
                                Slog.e(access$000, " failed to read all the data. bytes read: " + read + SliceClientPermissions.SliceAuthority.DELIMITER + size);
                                IoUtils.closeQuietly(inputStream);
                                return null;
                            }
                            IoUtils.closeQuietly(inputStream);
                            return data;
                        }
                    } catch (IOException e) {
                        try {
                            Slog.e(PersistentDataBlockService.TAG, " failed to read data from Enterprise access block", e);
                            return null;
                        } finally {
                            IoUtils.closeQuietly(inputStream);
                        }
                    }
                } catch (FileNotFoundException e2) {
                    Slog.e(PersistentDataBlockService.TAG, " partition not available?", e2);
                    return null;
                }
            } else {
                throw new IllegalStateException("invalid checksum");
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public int write(byte[] data) throws RemoteException {
            PersistentDataBlockService.this.enforceUid(Binder.getCallingUid());
            long maxBlockSize = PersistentDataBlockService.this.doGetMaximumDataBlockSize();
            if (((long) data.length) > maxBlockSize) {
                return (int) (-maxBlockSize);
            }
            try {
                DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(PersistentDataBlockService.this.mDataBlockFile)));
                ByteBuffer headerAndData = ByteBuffer.allocate(data.length + 8);
                headerAndData.putInt(PersistentDataBlockService.PARTITION_TYPE_MARKER);
                headerAndData.putInt(data.length);
                headerAndData.put(data);
                synchronized (PersistentDataBlockService.this.mLock) {
                    if (!PersistentDataBlockService.this.mIsWritable) {
                        IoUtils.closeQuietly(outputStream);
                        return -1;
                    }
                    try {
                        outputStream.write(new byte[32], 0, 32);
                        outputStream.write(headerAndData.array());
                        outputStream.flush();
                        IoUtils.closeQuietly(outputStream);
                        if (!PersistentDataBlockService.this.computeAndWriteDigestLocked()) {
                            return -1;
                        }
                        int length = data.length;
                        return length;
                    } catch (IOException e) {
                        try {
                            Slog.e(PersistentDataBlockService.TAG, "failed writing to the persistent data block", e);
                            return -1;
                        } finally {
                            IoUtils.closeQuietly(outputStream);
                        }
                    }
                }
            } catch (FileNotFoundException e2) {
                Slog.e(PersistentDataBlockService.TAG, "partition not available?", e2);
                return -1;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
            r2.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0043, code lost:
            android.util.Slog.e(com.android.server.PersistentDataBlockService.access$000(), "failed to close OutputStream");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
            r2.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x007b, code lost:
            android.util.Slog.e(com.android.server.PersistentDataBlockService.access$000(), "failed to close OutputStream");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
            r2.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x008b, code lost:
            android.util.Slog.e(com.android.server.PersistentDataBlockService.access$000(), "failed to close OutputStream");
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public byte[] read() {
            /*
                r9 = this;
                com.android.server.PersistentDataBlockService r0 = com.android.server.PersistentDataBlockService.this
                int r1 = android.os.Binder.getCallingUid()
                r0.enforceUid(r1)
                com.android.server.PersistentDataBlockService r0 = com.android.server.PersistentDataBlockService.this
                boolean r0 = r0.enforceChecksumValidity()
                r1 = 0
                if (r0 != 0) goto L_0x0015
                byte[] r0 = new byte[r1]
                return r0
            L_0x0015:
                r0 = 0
                java.io.DataInputStream r2 = new java.io.DataInputStream     // Catch:{ FileNotFoundException -> 0x00c3 }
                java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x00c3 }
                java.io.File r4 = new java.io.File     // Catch:{ FileNotFoundException -> 0x00c3 }
                com.android.server.PersistentDataBlockService r5 = com.android.server.PersistentDataBlockService.this     // Catch:{ FileNotFoundException -> 0x00c3 }
                java.lang.String r5 = r5.mDataBlockFile     // Catch:{ FileNotFoundException -> 0x00c3 }
                r4.<init>(r5)     // Catch:{ FileNotFoundException -> 0x00c3 }
                r3.<init>(r4)     // Catch:{ FileNotFoundException -> 0x00c3 }
                r2.<init>(r3)     // Catch:{ FileNotFoundException -> 0x00c3 }
                com.android.server.PersistentDataBlockService r3 = com.android.server.PersistentDataBlockService.this     // Catch:{ IOException -> 0x009a }
                java.lang.Object r3 = r3.mLock     // Catch:{ IOException -> 0x009a }
                monitor-enter(r3)     // Catch:{ IOException -> 0x009a }
                com.android.server.PersistentDataBlockService r4 = com.android.server.PersistentDataBlockService.this     // Catch:{ all -> 0x0095 }
                int r4 = r4.getTotalDataSizeLocked(r2)     // Catch:{ all -> 0x0095 }
                if (r4 != 0) goto L_0x004d
                byte[] r1 = new byte[r1]     // Catch:{ all -> 0x0095 }
                monitor-exit(r3)     // Catch:{ all -> 0x0095 }
                r2.close()     // Catch:{ IOException -> 0x0042 }
                goto L_0x004c
            L_0x0042:
                r0 = move-exception
                java.lang.String r3 = com.android.server.PersistentDataBlockService.TAG
                java.lang.String r5 = "failed to close OutputStream"
                android.util.Slog.e(r3, r5)
            L_0x004c:
                return r1
            L_0x004d:
                byte[] r5 = new byte[r4]     // Catch:{ all -> 0x0095 }
                int r1 = r2.read(r5, r1, r4)     // Catch:{ all -> 0x0095 }
                if (r1 >= r4) goto L_0x0085
                java.lang.String r6 = com.android.server.PersistentDataBlockService.TAG     // Catch:{ all -> 0x0095 }
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0095 }
                r7.<init>()     // Catch:{ all -> 0x0095 }
                java.lang.String r8 = "failed to read entire data block. bytes read: "
                r7.append(r8)     // Catch:{ all -> 0x0095 }
                r7.append(r1)     // Catch:{ all -> 0x0095 }
                java.lang.String r8 = "/"
                r7.append(r8)     // Catch:{ all -> 0x0095 }
                r7.append(r4)     // Catch:{ all -> 0x0095 }
                java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0095 }
                android.util.Slog.e(r6, r7)     // Catch:{ all -> 0x0095 }
                monitor-exit(r3)     // Catch:{ all -> 0x0095 }
                r2.close()     // Catch:{ IOException -> 0x007a }
                goto L_0x0084
            L_0x007a:
                r3 = move-exception
                java.lang.String r6 = com.android.server.PersistentDataBlockService.TAG
                java.lang.String r7 = "failed to close OutputStream"
                android.util.Slog.e(r6, r7)
            L_0x0084:
                return r0
            L_0x0085:
                monitor-exit(r3)     // Catch:{ all -> 0x0095 }
                r2.close()     // Catch:{ IOException -> 0x008a }
                goto L_0x0094
            L_0x008a:
                r0 = move-exception
                java.lang.String r3 = com.android.server.PersistentDataBlockService.TAG
                java.lang.String r6 = "failed to close OutputStream"
                android.util.Slog.e(r3, r6)
            L_0x0094:
                return r5
            L_0x0095:
                r1 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x0095 }
                throw r1     // Catch:{ IOException -> 0x009a }
            L_0x0098:
                r0 = move-exception
                goto L_0x00b4
            L_0x009a:
                r1 = move-exception
                java.lang.String r3 = com.android.server.PersistentDataBlockService.TAG     // Catch:{ all -> 0x0098 }
                java.lang.String r4 = "failed to read data"
                android.util.Slog.e(r3, r4, r1)     // Catch:{ all -> 0x0098 }
                r2.close()     // Catch:{ IOException -> 0x00a9 }
                goto L_0x00b3
            L_0x00a9:
                r3 = move-exception
                java.lang.String r4 = com.android.server.PersistentDataBlockService.TAG
                java.lang.String r5 = "failed to close OutputStream"
                android.util.Slog.e(r4, r5)
            L_0x00b3:
                return r0
            L_0x00b4:
                r2.close()     // Catch:{ IOException -> 0x00b8 }
                goto L_0x00c2
            L_0x00b8:
                r1 = move-exception
                java.lang.String r3 = com.android.server.PersistentDataBlockService.TAG
                java.lang.String r4 = "failed to close OutputStream"
                android.util.Slog.e(r3, r4)
            L_0x00c2:
                throw r0
            L_0x00c3:
                r1 = move-exception
                java.lang.String r2 = com.android.server.PersistentDataBlockService.TAG
                java.lang.String r3 = "partition not available?"
                android.util.Slog.e(r2, r3, r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.PersistentDataBlockService.AnonymousClass1.read():byte[]");
        }

        public void wipe() {
            PersistentDataBlockService.this.enforceOemUnlockWritePermission();
            synchronized (PersistentDataBlockService.this.mLock) {
                if (PersistentDataBlockService.this.nativeWipe(PersistentDataBlockService.this.mDataBlockFile) < 0) {
                    Slog.e(PersistentDataBlockService.TAG, "failed to wipe persistent partition");
                } else {
                    boolean unused = PersistentDataBlockService.this.mIsWritable = false;
                    Slog.i(PersistentDataBlockService.TAG, "persistent partition now wiped and unwritable");
                }
            }
        }

        public void setOemUnlockEnabled(boolean enabled) throws SecurityException {
            if (!ActivityManager.isUserAMonkey()) {
                PersistentDataBlockService.this.enforceOemUnlockWritePermission();
                PersistentDataBlockService.this.enforceIsAdmin();
                if (enabled) {
                    PersistentDataBlockService.this.enforceUserRestriction("no_oem_unlock");
                    PersistentDataBlockService.this.enforceUserRestriction("no_factory_reset");
                }
                synchronized (PersistentDataBlockService.this.mLock) {
                    PersistentDataBlockService.this.doSetOemUnlockEnabledLocked(enabled);
                    boolean unused = PersistentDataBlockService.this.computeAndWriteDigestLocked();
                }
            }
        }

        public boolean getOemUnlockEnabled() {
            PersistentDataBlockService.this.enforceOemUnlockReadPermission();
            return PersistentDataBlockService.this.doGetOemUnlockEnabled();
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x0033  */
        /* JADX WARNING: Removed duplicated region for block: B:15:0x0037 A[RETURN] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int getFlashLockState() {
            /*
                r6 = this;
                com.android.server.PersistentDataBlockService r0 = com.android.server.PersistentDataBlockService.this
                r0.enforceOemUnlockReadPermission()
                java.lang.String r0 = "ro.boot.flash.locked"
                java.lang.String r0 = android.os.SystemProperties.get(r0)
                int r1 = r0.hashCode()
                r2 = 48
                r3 = 0
                r4 = -1
                r5 = 1
                if (r1 == r2) goto L_0x0026
                r2 = 49
                if (r1 == r2) goto L_0x001c
            L_0x001b:
                goto L_0x0030
            L_0x001c:
                java.lang.String r1 = "1"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x001b
                r1 = r3
                goto L_0x0031
            L_0x0026:
                java.lang.String r1 = "0"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x001b
                r1 = r5
                goto L_0x0031
            L_0x0030:
                r1 = r4
            L_0x0031:
                if (r1 == 0) goto L_0x0037
                if (r1 == r5) goto L_0x0036
                return r4
            L_0x0036:
                return r3
            L_0x0037:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.PersistentDataBlockService.AnonymousClass1.getFlashLockState():int");
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public int getDataBlockSize() {
            int access$1000;
            enforcePersistentDataBlockAccess();
            try {
                DataInputStream inputStream = new DataInputStream(new FileInputStream(new File(PersistentDataBlockService.this.mDataBlockFile)));
                try {
                    synchronized (PersistentDataBlockService.this.mLock) {
                        access$1000 = PersistentDataBlockService.this.getTotalDataSizeLocked(inputStream);
                    }
                    IoUtils.closeQuietly(inputStream);
                    return access$1000;
                } catch (IOException e) {
                    try {
                        Slog.e(PersistentDataBlockService.TAG, "error reading data block size");
                        return 0;
                    } finally {
                        IoUtils.closeQuietly(inputStream);
                    }
                }
            } catch (FileNotFoundException e2) {
                Slog.e(PersistentDataBlockService.TAG, "partition not available");
                return 0;
            }
        }

        private void enforcePersistentDataBlockAccess() {
            if (PersistentDataBlockService.this.mContext.checkCallingPermission("android.permission.ACCESS_PDB_STATE") != 0) {
                PersistentDataBlockService.this.enforceUid(Binder.getCallingUid());
            }
        }

        public long getMaximumDataBlockSize() {
            PersistentDataBlockService.this.enforceUid(Binder.getCallingUid());
            return PersistentDataBlockService.this.doGetMaximumDataBlockSize();
        }

        public boolean hasFrpCredentialHandle() {
            enforcePersistentDataBlockAccess();
            try {
                return PersistentDataBlockService.this.mInternalService.getFrpCredentialHandle() != null;
            } catch (IllegalStateException e) {
                Slog.e(PersistentDataBlockService.TAG, "error reading frp handle", e);
                throw new UnsupportedOperationException("cannot read frp credential");
            }
        }
    };

    private native long nativeGetBlockDeviceSize(String str);

    /* access modifiers changed from: private */
    public native int nativeWipe(String str);

    /* JADX WARNING: type inference failed for: r0v3, types: [com.android.server.PersistentDataBlockService$1, android.os.IBinder] */
    public PersistentDataBlockService(Context context) {
        super(context);
        this.mContext = context;
        this.mDataBlockFile = SystemProperties.get(PERSISTENT_DATA_BLOCK_PROP);
        this.mBlockDeviceSize = -1;
    }

    private int getAllowedUid(int userHandle) {
        String allowedPackage = this.mContext.getResources().getString(17039790);
        try {
            return this.mContext.getPackageManager().getPackageUidAsUser(allowedPackage, DumpState.DUMP_DEXOPT, userHandle);
        } catch (PackageManager.NameNotFoundException e) {
            String str = TAG;
            Slog.e(str, "not able to find package " + allowedPackage, e);
            return -1;
        }
    }

    public void onStart() {
        SystemServerInitThreadPool systemServerInitThreadPool = SystemServerInitThreadPool.get();
        $$Lambda$PersistentDataBlockService$EZl9OYaT2eNL7kfSr2nKUBjxidk r1 = new Runnable() {
            public final void run() {
                PersistentDataBlockService.this.lambda$onStart$0$PersistentDataBlockService();
            }
        };
        systemServerInitThreadPool.submit(r1, TAG + ".onStart");
    }

    public /* synthetic */ void lambda$onStart$0$PersistentDataBlockService() {
        this.mAllowedUid = getAllowedUid(0);
        enforceChecksumValidity();
        formatIfOemUnlockEnabled();
        publishBinderService("persistent_data_block", this.mService);
        this.mInitDoneSignal.countDown();
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void onBootPhase(int phase) {
        if (phase == 500) {
            try {
                if (this.mInitDoneSignal.await(10, TimeUnit.SECONDS)) {
                    LocalServices.addService(PersistentDataBlockManagerInternal.class, this.mInternalService);
                } else {
                    throw new IllegalStateException("Service " + TAG + " init timeout");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Service " + TAG + " init interrupted", e);
            }
        }
        super.onBootPhase(phase);
    }

    private void formatIfOemUnlockEnabled() {
        boolean enabled = doGetOemUnlockEnabled();
        if (enabled) {
            synchronized (this.mLock) {
                formatPartitionLocked(true);
            }
        }
        SystemProperties.set(OEM_UNLOCK_PROP, enabled ? "1" : FLASH_LOCK_UNLOCKED);
    }

    /* access modifiers changed from: private */
    public void enforceOemUnlockReadPermission() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.READ_OEM_UNLOCK_STATE") == -1 && this.mContext.checkCallingOrSelfPermission("android.permission.OEM_UNLOCK_STATE") == -1) {
            throw new SecurityException("Can't access OEM unlock state. Requires READ_OEM_UNLOCK_STATE or OEM_UNLOCK_STATE permission.");
        }
    }

    /* access modifiers changed from: private */
    public void enforceOemUnlockWritePermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.OEM_UNLOCK_STATE", "Can't modify OEM unlock state");
    }

    /* access modifiers changed from: private */
    public void enforceUid(int callingUid) {
        if (callingUid != this.mAllowedUid) {
            throw new SecurityException("uid " + callingUid + " not allowed to access PST");
        }
    }

    /* access modifiers changed from: private */
    public void enforceIsAdmin() {
        if (!UserManager.get(this.mContext).isUserAdmin(UserHandle.getCallingUserId())) {
            throw new SecurityException("Only the Admin user is allowed to change OEM unlock state");
        }
    }

    /* access modifiers changed from: private */
    public void enforceUserRestriction(String userRestriction) {
        if (UserManager.get(this.mContext).hasUserRestriction(userRestriction)) {
            throw new SecurityException("OEM unlock is disallowed by user restriction: " + userRestriction);
        }
    }

    /* access modifiers changed from: private */
    public int getTotalDataSizeLocked(DataInputStream inputStream) throws IOException {
        inputStream.skipBytes(32);
        if (inputStream.readInt() == PARTITION_TYPE_MARKER) {
            return inputStream.readInt();
        }
        return 0;
    }

    private long getBlockDeviceSize() {
        synchronized (this.mLock) {
            if (this.mBlockDeviceSize == -1) {
                this.mBlockDeviceSize = nativeGetBlockDeviceSize(this.mDataBlockFile);
            }
        }
        return this.mBlockDeviceSize;
    }

    /* access modifiers changed from: private */
    public long getFrpCredentialDataOffset() {
        return (getBlockDeviceSize() - 1) - 1000;
    }

    /* access modifiers changed from: private */
    public long getTestHarnessModeDataOffset() {
        return getFrpCredentialDataOffset() - JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
    }

    /* access modifiers changed from: private */
    public boolean enforceChecksumValidity() {
        byte[] storedDigest = new byte[32];
        synchronized (this.mLock) {
            byte[] digest = computeDigestLocked(storedDigest);
            if (digest != null) {
                if (Arrays.equals(storedDigest, digest)) {
                    return true;
                }
            }
            Slog.i(TAG, "Formatting FRP partition...");
            formatPartitionLocked(false);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public boolean computeAndWriteDigestLocked() {
        byte[] digest = computeDigestLocked((byte[]) null);
        if (digest == null) {
            return false;
        }
        try {
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(this.mDataBlockFile)));
            try {
                outputStream.write(digest, 0, 32);
                outputStream.flush();
                IoUtils.closeQuietly(outputStream);
                return true;
            } catch (IOException e) {
                Slog.e(TAG, "failed to write block checksum", e);
                IoUtils.closeQuietly(outputStream);
                return false;
            } catch (Throwable th) {
                IoUtils.closeQuietly(outputStream);
                throw th;
            }
        } catch (FileNotFoundException e2) {
            Slog.e(TAG, "partition not available?", e2);
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0038 A[Catch:{ IOException -> 0x0047, all -> 0x0045 }, LOOP:0: B:14:0x0030->B:16:0x0038, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x003c A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private byte[] computeDigestLocked(byte[] r9) {
        /*
            r8 = this;
            r0 = 0
            java.io.DataInputStream r1 = new java.io.DataInputStream     // Catch:{ FileNotFoundException -> 0x0064 }
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0064 }
            java.io.File r3 = new java.io.File     // Catch:{ FileNotFoundException -> 0x0064 }
            java.lang.String r4 = r8.mDataBlockFile     // Catch:{ FileNotFoundException -> 0x0064 }
            r3.<init>(r4)     // Catch:{ FileNotFoundException -> 0x0064 }
            r2.<init>(r3)     // Catch:{ FileNotFoundException -> 0x0064 }
            r1.<init>(r2)     // Catch:{ FileNotFoundException -> 0x0064 }
            java.lang.String r2 = "SHA-256"
            java.security.MessageDigest r2 = java.security.MessageDigest.getInstance(r2)     // Catch:{ NoSuchAlgorithmException -> 0x0058 }
            r3 = 32
            if (r9 == 0) goto L_0x0025
            int r4 = r9.length     // Catch:{ IOException -> 0x0047 }
            if (r4 != r3) goto L_0x0025
            r1.read(r9)     // Catch:{ IOException -> 0x0047 }
            goto L_0x0028
        L_0x0025:
            r1.skipBytes(r3)     // Catch:{ IOException -> 0x0047 }
        L_0x0028:
            r4 = 1024(0x400, float:1.435E-42)
            byte[] r4 = new byte[r4]     // Catch:{ IOException -> 0x0047 }
            r5 = 0
            r2.update(r4, r5, r3)     // Catch:{ IOException -> 0x0047 }
        L_0x0030:
            int r3 = r1.read(r4)     // Catch:{ IOException -> 0x0047 }
            r6 = r3
            r7 = -1
            if (r3 == r7) goto L_0x003c
            r2.update(r4, r5, r6)     // Catch:{ IOException -> 0x0047 }
            goto L_0x0030
        L_0x003c:
            libcore.io.IoUtils.closeQuietly(r1)
            byte[] r0 = r2.digest()
            return r0
        L_0x0045:
            r0 = move-exception
            goto L_0x0054
        L_0x0047:
            r3 = move-exception
            java.lang.String r4 = TAG     // Catch:{ all -> 0x0045 }
            java.lang.String r5 = "failed to read partition"
            android.util.Slog.e(r4, r5, r3)     // Catch:{ all -> 0x0045 }
            libcore.io.IoUtils.closeQuietly(r1)
            return r0
        L_0x0054:
            libcore.io.IoUtils.closeQuietly(r1)
            throw r0
        L_0x0058:
            r2 = move-exception
            java.lang.String r3 = TAG
            java.lang.String r4 = "SHA-256 not supported?"
            android.util.Slog.e(r3, r4, r2)
            libcore.io.IoUtils.closeQuietly(r1)
            return r0
        L_0x0064:
            r1 = move-exception
            java.lang.String r2 = TAG
            java.lang.String r3 = "partition not available?"
            android.util.Slog.e(r2, r3, r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.PersistentDataBlockService.computeDigestLocked(byte[]):byte[]");
    }

    private void formatPartitionLocked(boolean setOemUnlockEnabled) {
        try {
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(this.mDataBlockFile)));
            try {
                outputStream.write(new byte[32], 0, 32);
                outputStream.writeInt(PARTITION_TYPE_MARKER);
                outputStream.writeInt(0);
                outputStream.flush();
                IoUtils.closeQuietly(outputStream);
                doSetOemUnlockEnabledLocked(setOemUnlockEnabled);
                computeAndWriteDigestLocked();
            } catch (IOException e) {
                Slog.e(TAG, "failed to format block", e);
                IoUtils.closeQuietly(outputStream);
            } catch (Throwable th) {
                IoUtils.closeQuietly(outputStream);
                throw th;
            }
        } catch (FileNotFoundException e2) {
            Slog.e(TAG, "partition not available?", e2);
        }
    }

    /* access modifiers changed from: private */
    public void doSetOemUnlockEnabledLocked(boolean enabled) {
        String str = "1";
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(this.mDataBlockFile));
            try {
                FileChannel channel = outputStream.getChannel();
                channel.position(getBlockDeviceSize() - 1);
                byte b = 1;
                ByteBuffer data = ByteBuffer.allocate(1);
                if (!enabled) {
                    b = 0;
                }
                data.put(b);
                data.flip();
                channel.write(data);
                outputStream.flush();
                if (!enabled) {
                    str = FLASH_LOCK_UNLOCKED;
                }
                SystemProperties.set(OEM_UNLOCK_PROP, str);
                IoUtils.closeQuietly(outputStream);
            } catch (IOException e) {
                Slog.e(TAG, "unable to access persistent partition", e);
                if (!enabled) {
                    str = FLASH_LOCK_UNLOCKED;
                }
                SystemProperties.set(OEM_UNLOCK_PROP, str);
                IoUtils.closeQuietly(outputStream);
            } catch (Throwable th) {
                if (!enabled) {
                    str = FLASH_LOCK_UNLOCKED;
                }
                SystemProperties.set(OEM_UNLOCK_PROP, str);
                IoUtils.closeQuietly(outputStream);
                throw th;
            }
        } catch (FileNotFoundException e2) {
            Slog.e(TAG, "partition not available", e2);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: private */
    public boolean doGetOemUnlockEnabled() {
        boolean z;
        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(new File(this.mDataBlockFile)));
            try {
                synchronized (this.mLock) {
                    inputStream.skip(getBlockDeviceSize() - 1);
                    z = inputStream.readByte() != 0;
                }
                IoUtils.closeQuietly(inputStream);
                return z;
            } catch (IOException e) {
                try {
                    Slog.e(TAG, "unable to access persistent partition", e);
                    return false;
                } finally {
                    IoUtils.closeQuietly(inputStream);
                }
            }
        } catch (FileNotFoundException e2) {
            Slog.e(TAG, "partition not available");
            return false;
        }
    }

    /* access modifiers changed from: private */
    public long doGetMaximumDataBlockSize() {
        long actualSize = ((((getBlockDeviceSize() - 8) - 32) - JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY) - 1000) - 1;
        if (actualSize <= 102400) {
            return actualSize;
        }
        return 102400;
    }

    /* access modifiers changed from: private */
    public long getEnterpriseDataOffset() {
        return ((getBlockDeviceSize() - 1) - 1000) - 2048;
    }

    /* access modifiers changed from: private */
    public void enforceEnterprisePermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.ACTIVE_ENTERPRISE_MODE, "Can't access enterprise data block");
    }
}

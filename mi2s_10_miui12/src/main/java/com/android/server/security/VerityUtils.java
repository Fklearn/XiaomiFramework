package com.android.server.security;

import android.os.SharedMemory;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Pair;
import android.util.Slog;
import android.util.apk.ApkSignatureVerifier;
import android.util.apk.ByteBufferFactory;
import android.util.apk.SignatureNotFoundException;
import com.android.server.job.controllers.JobStatus;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import libcore.util.HexEncoding;

public abstract class VerityUtils {
    private static final int COMMON_LINUX_PAGE_SIZE_IN_BYTES = 4096;
    private static final boolean DEBUG = false;
    public static final String FSVERITY_SIGNATURE_FILE_EXTENSION = ".fsv_sig";
    private static final int MAX_SIGNATURE_FILE_SIZE_BYTES = 8192;
    private static final String TAG = "VerityUtils";

    private static native byte[] constructFsverityDescriptorNative(long j);

    private static native byte[] constructFsverityExtensionNative(short s, int i);

    private static native byte[] constructFsverityFooterNative(int i);

    private static native byte[] constructFsveritySignedDataNative(byte[] bArr);

    private static native int enableFsverityNative(String str);

    private static native int measureFsverityNative(String str);

    public static boolean isFsveritySignatureFile(File file) {
        return file.getName().endsWith(FSVERITY_SIGNATURE_FILE_EXTENSION);
    }

    public static String getFsveritySignatureFilePath(String filePath) {
        return filePath + FSVERITY_SIGNATURE_FILE_EXTENSION;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x00b1, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00b2, code lost:
        $closeResource(r3, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00b5, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void setUpFsverity(java.lang.String r14, java.lang.String r15) throws java.io.IOException, java.security.DigestException, java.security.NoSuchAlgorithmException {
        /*
            sun.security.pkcs.PKCS7 r0 = new sun.security.pkcs.PKCS7
            r1 = 0
            java.lang.String[] r1 = new java.lang.String[r1]
            java.nio.file.Path r1 = java.nio.file.Paths.get(r15, r1)
            byte[] r1 = java.nio.file.Files.readAllBytes(r1)
            r0.<init>(r1)
            sun.security.pkcs.ContentInfo r1 = r0.getContentInfo()
            byte[] r1 = r1.getContentBytes()
            com.android.server.security.VerityUtils$TrackedBufferFactory r2 = new com.android.server.security.VerityUtils$TrackedBufferFactory
            r3 = 0
            r2.<init>()
            byte[] r4 = generateFsverityMetadata(r14, r15, r2)
            java.io.RandomAccessFile r5 = new java.io.RandomAccessFile
            java.lang.String r6 = "rw"
            r5.<init>(r14, r6)
            java.nio.channels.FileChannel r6 = r5.getChannel()     // Catch:{ all -> 0x00af }
            long r7 = r6.size()     // Catch:{ all -> 0x00af }
            r9 = 4096(0x1000, double:2.0237E-320)
            long r7 = roundUpToNextMultiple(r7, r9)     // Catch:{ all -> 0x00af }
            r6.position(r7)     // Catch:{ all -> 0x00af }
            java.nio.ByteBuffer r7 = r2.getBuffer()     // Catch:{ all -> 0x00af }
            int r8 = r7.position()     // Catch:{ all -> 0x00af }
            long r8 = (long) r8     // Catch:{ all -> 0x00af }
            int r10 = r7.limit()     // Catch:{ all -> 0x00af }
            long r10 = (long) r10     // Catch:{ all -> 0x00af }
        L_0x0049:
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 >= 0) goto L_0x0055
            int r12 = r6.write(r7)     // Catch:{ all -> 0x00af }
            long r12 = (long) r12
            long r8 = r8 + r12
            long r10 = r10 - r12
            goto L_0x0049
        L_0x0055:
            $closeResource(r3, r5)
            boolean r3 = java.util.Arrays.equals(r1, r4)
            if (r3 == 0) goto L_0x0088
            int r3 = enableFsverityNative(r14)
            if (r3 != 0) goto L_0x0065
            return
        L_0x0065:
            java.io.IOException r5 = new java.io.IOException
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Failed to enable fs-verity on "
            r6.append(r7)
            r6.append(r14)
            java.lang.String r7 = ": "
            r6.append(r7)
            java.lang.String r7 = android.system.Os.strerror(r3)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.<init>(r6)
            throw r5
        L_0x0088:
            java.lang.SecurityException r3 = new java.lang.SecurityException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "fs-verity measurement mismatch: "
            r5.append(r6)
            java.lang.String r6 = bytesToString(r4)
            r5.append(r6)
            java.lang.String r6 = " != "
            r5.append(r6)
            java.lang.String r6 = bytesToString(r1)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r3.<init>(r5)
            throw r3
        L_0x00af:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x00b1 }
        L_0x00b1:
            r6 = move-exception
            $closeResource(r3, r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.security.VerityUtils.setUpFsverity(java.lang.String, java.lang.String):void");
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

    public static boolean hasFsverity(String filePath) {
        int errno = measureFsverityNative(filePath);
        if (errno == 0) {
            return true;
        }
        if (errno == OsConstants.ENODATA) {
            return false;
        }
        Slog.e(TAG, "Failed to measure fs-verity, errno " + errno + ": " + filePath);
        return false;
    }

    public static SetupResult generateApkVeritySetupData(String apkPath) {
        SharedMemory shm = null;
        try {
            byte[] signedVerityHash = ApkSignatureVerifier.getVerityRootHash(apkPath);
            if (signedVerityHash == null) {
                SetupResult skipped = SetupResult.skipped();
                if (shm != null) {
                    shm.close();
                }
                return skipped;
            }
            Pair<SharedMemory, Integer> result = generateFsVerityIntoSharedMemory(apkPath, signedVerityHash);
            shm = (SharedMemory) result.first;
            int contentSize = ((Integer) result.second).intValue();
            FileDescriptor rfd = shm.getFileDescriptor();
            if (rfd != null) {
                if (rfd.valid()) {
                    SetupResult ok = SetupResult.ok(Os.dup(rfd), contentSize);
                    shm.close();
                    return ok;
                }
            }
            SetupResult failed = SetupResult.failed();
            shm.close();
            return failed;
        } catch (ErrnoException | SignatureNotFoundException | IOException | SecurityException | DigestException | NoSuchAlgorithmException e) {
            Slog.e(TAG, "Failed to set up apk verity: ", e);
            SetupResult failed2 = SetupResult.failed();
            if (shm != null) {
                shm.close();
            }
            return failed2;
        } catch (Throwable th) {
            if (shm != null) {
                shm.close();
            }
            throw th;
        }
    }

    public static byte[] generateApkVerityRootHash(String apkPath) throws NoSuchAlgorithmException, DigestException, IOException {
        return ApkSignatureVerifier.generateApkVerityRootHash(apkPath);
    }

    public static byte[] getVerityRootHash(String apkPath) throws IOException, SignatureNotFoundException {
        return ApkSignatureVerifier.getVerityRootHash(apkPath);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002b, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0027, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0028, code lost:
        $closeResource(r1, r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static byte[] generateFsverityMetadata(java.lang.String r6, java.lang.String r7, android.util.apk.ByteBufferFactory r8) throws java.io.IOException, java.security.DigestException, java.security.NoSuchAlgorithmException {
        /*
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile
            java.lang.String r1 = "r"
            r0.<init>(r6, r1)
            android.util.apk.VerityBuilder$VerityResult r1 = android.util.apk.VerityBuilder.generateFsVerityTree(r0, r8)     // Catch:{ all -> 0x0025 }
            java.nio.ByteBuffer r2 = r1.verityData     // Catch:{ all -> 0x0025 }
            int r3 = r1.merkleTreeSize     // Catch:{ all -> 0x0025 }
            r2.position(r3)     // Catch:{ all -> 0x0025 }
            byte[] r3 = r1.rootHash     // Catch:{ all -> 0x0025 }
            byte[] r3 = generateFsverityDescriptorAndMeasurement(r0, r3, r7, r2)     // Catch:{ all -> 0x0025 }
            r2.flip()     // Catch:{ all -> 0x0025 }
            byte[] r4 = constructFsveritySignedDataNative(r3)     // Catch:{ all -> 0x0025 }
            r5 = 0
            $closeResource(r5, r0)
            return r4
        L_0x0025:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0027 }
        L_0x0027:
            r2 = move-exception
            $closeResource(r1, r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.security.VerityUtils.generateFsverityMetadata(java.lang.String, java.lang.String, android.util.apk.ByteBufferFactory):byte[]");
    }

    private static byte[] generateFsverityDescriptorAndMeasurement(RandomAccessFile file, byte[] rootHash, String pkcs7SignaturePath, ByteBuffer output) throws IOException, NoSuchAlgorithmException, DigestException {
        int origPosition = output.position();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] desc = constructFsverityDescriptorNative(file.length());
        output.put(desc);
        md.update(desc);
        byte[] authExt = constructFsverityExtensionNative(1, rootHash.length);
        output.put(authExt);
        output.put(rootHash);
        md.update(authExt);
        md.update(rootHash);
        ByteBuffer order = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        output.putShort(1);
        output.position(output.position() + 6);
        Path path = Paths.get(pkcs7SignaturePath, new String[0]);
        if (Files.size(path) <= 8192) {
            byte[] pkcs7Signature = Files.readAllBytes(path);
            output.put(constructFsverityExtensionNative(3, pkcs7Signature.length));
            output.put(pkcs7Signature);
            output.put(constructFsverityFooterNative(output.position() - origPosition));
            return md.digest();
        }
        throw new IllegalArgumentException("Signature size is unexpectedly large: " + pkcs7SignaturePath);
    }

    private static Pair<SharedMemory, Integer> generateFsVerityIntoSharedMemory(String apkPath, byte[] expectedRootHash) throws IOException, DigestException, NoSuchAlgorithmException, SignatureNotFoundException {
        TrackedShmBufferFactory shmBufferFactory = new TrackedShmBufferFactory();
        byte[] generatedRootHash = ApkSignatureVerifier.generateApkVerity(apkPath, shmBufferFactory);
        if (Arrays.equals(expectedRootHash, generatedRootHash)) {
            int contentSize = shmBufferFactory.getBufferLimit();
            SharedMemory shm = shmBufferFactory.releaseSharedMemory();
            if (shm == null) {
                throw new IllegalStateException("Failed to generate verity tree into shared memory");
            } else if (shm.setProtect(OsConstants.PROT_READ)) {
                return Pair.create(shm, Integer.valueOf(contentSize));
            } else {
                throw new SecurityException("Failed to set up shared memory correctly");
            }
        } else {
            throw new SecurityException("verity hash mismatch: " + bytesToString(generatedRootHash) + " != " + bytesToString(expectedRootHash));
        }
    }

    private static String bytesToString(byte[] bytes) {
        return HexEncoding.encodeToString(bytes);
    }

    public static class SetupResult {
        private static final int RESULT_FAILED = 3;
        private static final int RESULT_OK = 1;
        private static final int RESULT_SKIPPED = 2;
        private final int mCode;
        private final int mContentSize;
        private final FileDescriptor mFileDescriptor;

        public static SetupResult ok(FileDescriptor fileDescriptor, int contentSize) {
            return new SetupResult(1, fileDescriptor, contentSize);
        }

        public static SetupResult skipped() {
            return new SetupResult(2, (FileDescriptor) null, -1);
        }

        public static SetupResult failed() {
            return new SetupResult(3, (FileDescriptor) null, -1);
        }

        private SetupResult(int code, FileDescriptor fileDescriptor, int contentSize) {
            this.mCode = code;
            this.mFileDescriptor = fileDescriptor;
            this.mContentSize = contentSize;
        }

        public boolean isFailed() {
            return this.mCode == 3;
        }

        public boolean isOk() {
            return this.mCode == 1;
        }

        public FileDescriptor getUnownedFileDescriptor() {
            return this.mFileDescriptor;
        }

        public int getContentSize() {
            return this.mContentSize;
        }
    }

    private static class TrackedShmBufferFactory implements ByteBufferFactory {
        private ByteBuffer mBuffer;
        private SharedMemory mShm;

        private TrackedShmBufferFactory() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public ByteBuffer create(int capacity) {
            try {
                if (this.mBuffer == null) {
                    this.mShm = SharedMemory.create("apkverity", capacity);
                    if (this.mShm.setProtect(OsConstants.PROT_READ | OsConstants.PROT_WRITE)) {
                        this.mBuffer = this.mShm.mapReadWrite();
                        return this.mBuffer;
                    }
                    throw new SecurityException("Failed to set protection");
                }
                throw new IllegalStateException("Multiple instantiation from this factory");
            } catch (ErrnoException e) {
                throw new SecurityException("Failed to set protection", e);
            }
        }

        public SharedMemory releaseSharedMemory() {
            ByteBuffer byteBuffer = this.mBuffer;
            if (byteBuffer != null) {
                SharedMemory.unmap(byteBuffer);
                this.mBuffer = null;
            }
            SharedMemory tmp = this.mShm;
            this.mShm = null;
            return tmp;
        }

        public int getBufferLimit() {
            ByteBuffer byteBuffer = this.mBuffer;
            if (byteBuffer == null) {
                return -1;
            }
            return byteBuffer.limit();
        }
    }

    private static class TrackedBufferFactory implements ByteBufferFactory {
        private ByteBuffer mBuffer;

        private TrackedBufferFactory() {
        }

        public ByteBuffer create(int capacity) {
            if (this.mBuffer == null) {
                this.mBuffer = ByteBuffer.allocate(capacity);
                return this.mBuffer;
            }
            throw new IllegalStateException("Multiple instantiation from this factory");
        }

        public ByteBuffer getBuffer() {
            return this.mBuffer;
        }
    }

    private static long roundUpToNextMultiple(long number, long divisor) {
        if (number <= JobStatus.NO_LATEST_RUNTIME - divisor) {
            return (((divisor - 1) + number) / divisor) * divisor;
        }
        throw new IllegalArgumentException("arithmetic overflow");
    }
}

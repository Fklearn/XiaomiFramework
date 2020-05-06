package com.android.timezone.distro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public final class FileUtils {
    private FileUtils() {
    }

    public static File createSubFile(File parentDir, String name) throws IOException {
        File subFile = new File(parentDir, name).getCanonicalFile();
        if (subFile.getPath().startsWith(parentDir.getCanonicalPath())) {
            return subFile;
        }
        throw new IOException(name + " must exist beneath " + parentDir + ". Canonicalized subpath: " + subFile);
    }

    public static void ensureDirectoriesExist(File dir, boolean makeWorldReadable) throws IOException {
        LinkedList<File> dirs = new LinkedList<>();
        File currentDir = dir;
        do {
            dirs.addFirst(currentDir);
            currentDir = currentDir.getParentFile();
        } while (currentDir != null);
        Iterator it = dirs.iterator();
        while (it.hasNext()) {
            File dirToCheck = (File) it.next();
            if (!dirToCheck.exists()) {
                if (!dirToCheck.mkdir()) {
                    throw new IOException("Unable to create directory: " + dir);
                } else if (makeWorldReadable) {
                    makeDirectoryWorldAccessible(dirToCheck);
                }
            } else if (!dirToCheck.isDirectory()) {
                throw new IOException(dirToCheck + " exists but is not a directory");
            }
        }
    }

    public static void makeDirectoryWorldAccessible(File directory) throws IOException {
        if (directory.isDirectory()) {
            makeWorldReadable(directory);
            if (!directory.setExecutable(true, false)) {
                throw new IOException("Unable to make " + directory + " world-executable");
            }
            return;
        }
        throw new IOException(directory + " must be a directory");
    }

    public static void makeWorldReadable(File file) throws IOException {
        if (!file.setReadable(true, false)) {
            throw new IOException("Unable to make " + file + " world-readable");
        }
    }

    public static void rename(File from, File to) throws IOException {
        ensureFileDoesNotExist(to);
        if (!from.renameTo(to)) {
            throw new IOException("Unable to rename " + from + " to " + to);
        }
    }

    public static void ensureFileDoesNotExist(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            doDelete(file);
            return;
        }
        throw new IOException(file + " is not a file");
    }

    public static void doDelete(File file) throws IOException {
        if (!file.delete()) {
            throw new IOException("Unable to delete: " + file);
        }
    }

    public static boolean isSymlink(File file) throws IOException {
        return !file.getCanonicalPath().equals(new File(file.getParentFile().getCanonicalFile(), file.getName()).getPath());
    }

    public static void deleteRecursive(File toDelete) throws IOException {
        if (toDelete.isDirectory()) {
            for (File file : toDelete.listFiles()) {
                if (!file.isDirectory() || isSymlink(file)) {
                    doDelete(file);
                } else {
                    deleteRecursive(file);
                }
            }
            String[] remainingFiles = toDelete.list();
            if (remainingFiles.length != 0) {
                throw new IOException("Unable to delete files: " + Arrays.toString(remainingFiles));
            }
        }
        doDelete(toDelete);
    }

    public static boolean filesExist(File rootDir, String... fileNames) {
        for (String fileName : fileNames) {
            if (!new File(rootDir, fileName).exists()) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001f, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0020, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0023, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001a, code lost:
        r2 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] readBytes(java.io.File r5, int r6) throws java.io.IOException {
        /*
            if (r6 <= 0) goto L_0x0024
            java.io.FileInputStream r0 = new java.io.FileInputStream
            r0.<init>(r5)
            byte[] r1 = new byte[r6]     // Catch:{ all -> 0x0018 }
            r2 = 0
            int r3 = r0.read(r1, r2, r6)     // Catch:{ all -> 0x0018 }
            byte[] r4 = new byte[r3]     // Catch:{ all -> 0x0018 }
            java.lang.System.arraycopy(r1, r2, r4, r2, r3)     // Catch:{ all -> 0x0018 }
            r0.close()
            return r4
        L_0x0018:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001a }
        L_0x001a:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x001f }
            goto L_0x0023
        L_0x001f:
            r3 = move-exception
            r1.addSuppressed(r3)
        L_0x0023:
            throw r2
        L_0x0024:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "maxBytes =="
            r1.append(r2)
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.timezone.distro.FileUtils.readBytes(java.io.File, int):byte[]");
    }

    public static void createEmptyFile(File file) throws IOException {
        new FileOutputStream(file, false).close();
    }
}

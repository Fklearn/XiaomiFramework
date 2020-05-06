package com.android.commands.sm;

import android.os.IVoldTaskListener;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.DiskInfo;
import android.os.storage.IStorageManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

public final class Sm {
    private static final String TAG = "Sm";
    private String[] mArgs;
    private String mCurArgData;
    private int mNextArg;
    IStorageManager mSm;

    public static void main(String[] args) {
        boolean success = false;
        int i = 1;
        try {
            new Sm().run(args);
            success = true;
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                showUsage();
                System.exit(1);
            }
            Log.e(TAG, "Error", e);
            PrintStream printStream = System.err;
            printStream.println("Error: " + e);
        }
        if (success) {
            i = 0;
        }
        System.exit(i);
    }

    public void run(String[] args) throws Exception {
        if (args.length >= 1) {
            this.mSm = IStorageManager.Stub.asInterface(ServiceManager.getService("mount"));
            if (this.mSm != null) {
                this.mArgs = args;
                String op = args[0];
                this.mNextArg = 1;
                if ("list-disks".equals(op)) {
                    runListDisks();
                } else if ("list-volumes".equals(op)) {
                    runListVolumes();
                } else if ("has-adoptable".equals(op)) {
                    runHasAdoptable();
                } else if ("get-primary-storage-uuid".equals(op)) {
                    runGetPrimaryStorageUuid();
                } else if ("set-force-adoptable".equals(op)) {
                    runSetForceAdoptable();
                } else if ("set-sdcardfs".equals(op)) {
                    runSetSdcardfs();
                } else if ("partition".equals(op)) {
                    runPartition();
                } else if ("mount".equals(op)) {
                    runMount();
                } else if ("unmount".equals(op)) {
                    runUnmount();
                } else if ("format".equals(op)) {
                    runFormat();
                } else if ("benchmark".equals(op)) {
                    runBenchmark();
                } else if ("forget".equals(op)) {
                    runForget();
                } else if ("set-emulate-fbe".equals(op)) {
                    runSetEmulateFbe();
                } else if ("get-fbe-mode".equals(op)) {
                    runGetFbeMode();
                } else if ("idle-maint".equals(op)) {
                    runIdleMaint();
                } else if ("fstrim".equals(op)) {
                    runFstrim();
                } else if ("set-virtual-disk".equals(op)) {
                    runSetVirtualDisk();
                } else if ("defrag".equals(op)) {
                    runDefrag();
                } else if ("set-isolated-storage".equals(op)) {
                    runIsolatedStorage();
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                throw new RemoteException("Failed to find running mount service");
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void runListDisks() throws RemoteException {
        boolean onlyAdoptable = "adoptable".equals(nextArg());
        for (DiskInfo disk : this.mSm.getDisks()) {
            if (!onlyAdoptable || disk.isAdoptable()) {
                System.out.println(disk.getId());
            }
        }
    }

    public void runListVolumes() throws RemoteException {
        int filterType;
        String filter = nextArg();
        if ("public".equals(filter)) {
            filterType = 0;
        } else if ("private".equals(filter)) {
            filterType = 1;
        } else if ("emulated".equals(filter)) {
            filterType = 2;
        } else if ("stub".equals(filter)) {
            filterType = 5;
        } else {
            filterType = -1;
        }
        for (VolumeInfo vol : this.mSm.getVolumes(0)) {
            if (filterType == -1 || filterType == vol.getType()) {
                String envState = VolumeInfo.getEnvironmentForState(vol.getState());
                System.out.println(vol.getId() + " " + envState + " " + vol.getFsUuid());
            }
        }
    }

    public void runHasAdoptable() {
        System.out.println(StorageManager.hasAdoptable());
    }

    public void runGetPrimaryStorageUuid() throws RemoteException {
        System.out.println(this.mSm.getPrimaryStorageUuid());
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void runSetForceAdoptable() throws android.os.RemoteException {
        /*
            r8 = this;
            r0 = 3
            java.lang.String r1 = r8.nextArg()
            int r2 = r1.hashCode()
            r3 = 0
            r4 = 4
            r5 = 2
            r6 = 1
            r7 = 3
            switch(r2) {
                case 3551: goto L_0x003a;
                case 109935: goto L_0x0030;
                case 3569038: goto L_0x0026;
                case 97196323: goto L_0x001c;
                case 1544803905: goto L_0x0012;
                default: goto L_0x0011;
            }
        L_0x0011:
            goto L_0x0044
        L_0x0012:
            java.lang.String r2 = "default"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0011
            r1 = r7
            goto L_0x0045
        L_0x001c:
            java.lang.String r2 = "false"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0011
            r1 = r4
            goto L_0x0045
        L_0x0026:
            java.lang.String r2 = "true"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0011
            r1 = r6
            goto L_0x0045
        L_0x0030:
            java.lang.String r2 = "off"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0011
            r1 = r5
            goto L_0x0045
        L_0x003a:
            java.lang.String r2 = "on"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0011
            r1 = r3
            goto L_0x0045
        L_0x0044:
            r1 = -1
        L_0x0045:
            if (r1 == 0) goto L_0x005c
            if (r1 == r6) goto L_0x005c
            if (r1 == r5) goto L_0x0056
            if (r1 == r7) goto L_0x0050
            if (r1 == r4) goto L_0x0050
            goto L_0x0062
        L_0x0050:
            android.os.storage.IStorageManager r1 = r8.mSm
            r1.setDebugFlags(r3, r7)
            goto L_0x0062
        L_0x0056:
            android.os.storage.IStorageManager r1 = r8.mSm
            r1.setDebugFlags(r5, r7)
            goto L_0x0062
        L_0x005c:
            android.os.storage.IStorageManager r1 = r8.mSm
            r1.setDebugFlags(r6, r7)
        L_0x0062:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.commands.sm.Sm.runSetForceAdoptable():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0052  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void runSetSdcardfs() throws android.os.RemoteException {
        /*
            r7 = this;
            r0 = 24
            java.lang.String r1 = r7.nextArg()
            int r2 = r1.hashCode()
            r3 = 3551(0xddf, float:4.976E-42)
            r4 = 0
            r5 = 2
            r6 = 1
            if (r2 == r3) goto L_0x0030
            r3 = 109935(0x1ad6f, float:1.54052E-40)
            if (r2 == r3) goto L_0x0026
            r3 = 1544803905(0x5c13d641, float:1.66449585E17)
            if (r2 == r3) goto L_0x001c
        L_0x001b:
            goto L_0x003a
        L_0x001c:
            java.lang.String r2 = "default"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x001b
            r1 = r5
            goto L_0x003b
        L_0x0026:
            java.lang.String r2 = "off"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x001b
            r1 = r6
            goto L_0x003b
        L_0x0030:
            java.lang.String r2 = "on"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x001b
            r1 = r4
            goto L_0x003b
        L_0x003a:
            r1 = -1
        L_0x003b:
            r2 = 24
            if (r1 == 0) goto L_0x0052
            if (r1 == r6) goto L_0x004a
            if (r1 == r5) goto L_0x0044
            goto L_0x005a
        L_0x0044:
            android.os.storage.IStorageManager r1 = r7.mSm
            r1.setDebugFlags(r4, r2)
            goto L_0x005a
        L_0x004a:
            android.os.storage.IStorageManager r1 = r7.mSm
            r3 = 16
            r1.setDebugFlags(r3, r2)
            goto L_0x005a
        L_0x0052:
            android.os.storage.IStorageManager r1 = r7.mSm
            r3 = 8
            r1.setDebugFlags(r3, r2)
        L_0x005a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.commands.sm.Sm.runSetSdcardfs():void");
    }

    public void runSetEmulateFbe() throws RemoteException {
        this.mSm.setDebugFlags(Boolean.parseBoolean(nextArg()) ? 4 : 0, 4);
    }

    public void runGetFbeMode() {
        if (StorageManager.isFileEncryptedNativeOnly()) {
            System.out.println("native");
        } else if (StorageManager.isFileEncryptedEmulatedOnly()) {
            System.out.println("emulated");
        } else {
            System.out.println("none");
        }
    }

    public void runPartition() throws RemoteException {
        String diskId = nextArg();
        String type = nextArg();
        if ("public".equals(type)) {
            this.mSm.partitionPublic(diskId);
        } else if ("private".equals(type)) {
            this.mSm.partitionPrivate(diskId);
        } else if ("mixed".equals(type)) {
            this.mSm.partitionMixed(diskId, Integer.parseInt(nextArg()));
        } else {
            throw new IllegalArgumentException("Unsupported partition type " + type);
        }
    }

    public void runMount() throws RemoteException {
        this.mSm.mount(nextArg());
    }

    public void runUnmount() throws RemoteException {
        this.mSm.unmount(nextArg());
    }

    public void runFormat() throws RemoteException {
        this.mSm.format(nextArg());
    }

    public void runBenchmark() throws Exception {
        String volId = nextArg();
        final CompletableFuture<PersistableBundle> result = new CompletableFuture<>();
        this.mSm.benchmark(volId, new IVoldTaskListener.Stub() {
            public void onStatus(int status, PersistableBundle extras) {
            }

            public void onFinished(int status, PersistableBundle extras) {
                extras.size();
                result.complete(extras);
            }
        });
        System.out.println(result.get());
    }

    public void runForget() throws RemoteException {
        String fsUuid = nextArg();
        if ("all".equals(fsUuid)) {
            this.mSm.forgetAllVolumes();
        } else {
            this.mSm.forgetVolume(fsUuid);
        }
    }

    public void runFstrim() throws Exception {
        final CompletableFuture<PersistableBundle> result = new CompletableFuture<>();
        this.mSm.fstrim(0, new IVoldTaskListener.Stub() {
            public void onStatus(int status, PersistableBundle extras) {
            }

            public void onFinished(int status, PersistableBundle extras) {
                extras.size();
                result.complete(extras);
            }
        });
        System.out.println(result.get());
    }

    public void runDefrag() throws Exception {
        boolean im_run = "run".equals(nextArg());
        final CompletableFuture<PersistableBundle> result = new CompletableFuture<>();
        if (im_run) {
            this.mSm.runDefrag(new IVoldTaskListener.Stub() {
                public void onStatus(int status, PersistableBundle extras) {
                    Log.e(Sm.TAG, "defrag progress: " + status + "%");
                    PrintStream printStream = System.err;
                    printStream.println("defrag progress: " + status + "%");
                }

                public void onFinished(int status, PersistableBundle extras) {
                    Log.e(Sm.TAG, "defrag has finished with status " + status);
                    PrintStream printStream = System.err;
                    printStream.println("defrag has finished with status " + status);
                    extras.size();
                    result.complete(extras);
                }
            });
        } else {
            this.mSm.stopDefrag(new IVoldTaskListener.Stub() {
                public void onStatus(int status, PersistableBundle extras) {
                }

                public void onFinished(int status, PersistableBundle extras) {
                    Log.e(Sm.TAG, "defrag stop successfully");
                    System.err.println("defrag stop successfully");
                    extras.size();
                    result.complete(extras);
                }
            });
        }
        System.out.println(result.get());
    }

    public void runSetVirtualDisk() throws RemoteException {
        this.mSm.setDebugFlags(Boolean.parseBoolean(nextArg()) ? 32 : 0, 32);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void runIsolatedStorage() throws android.os.RemoteException {
        /*
            r7 = this;
            r0 = 192(0xc0, float:2.69E-43)
            java.lang.String r1 = r7.nextArg()
            int r2 = r1.hashCode()
            r3 = 4
            r4 = 3
            r5 = 2
            r6 = 1
            switch(r2) {
                case 3551: goto L_0x003a;
                case 109935: goto L_0x0030;
                case 3569038: goto L_0x0026;
                case 97196323: goto L_0x001c;
                case 1544803905: goto L_0x0012;
                default: goto L_0x0011;
            }
        L_0x0011:
            goto L_0x0044
        L_0x0012:
            java.lang.String r2 = "default"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0011
            r1 = r4
            goto L_0x0045
        L_0x001c:
            java.lang.String r2 = "false"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0011
            r1 = r3
            goto L_0x0045
        L_0x0026:
            java.lang.String r2 = "true"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0011
            r1 = r6
            goto L_0x0045
        L_0x0030:
            java.lang.String r2 = "off"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0011
            r1 = r5
            goto L_0x0045
        L_0x003a:
            java.lang.String r2 = "on"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0011
            r1 = 0
            goto L_0x0045
        L_0x0044:
            r1 = -1
        L_0x0045:
            if (r1 == 0) goto L_0x0055
            if (r1 == r6) goto L_0x0055
            if (r1 == r5) goto L_0x0052
            if (r1 == r4) goto L_0x0050
            if (r1 == r3) goto L_0x0050
            return
        L_0x0050:
            r1 = 0
            goto L_0x0058
        L_0x0052:
            r1 = 128(0x80, float:1.794E-43)
            goto L_0x0058
        L_0x0055:
            r1 = 64
        L_0x0058:
            android.os.storage.IStorageManager r2 = r7.mSm
            r3 = 192(0xc0, float:2.69E-43)
            r2.setDebugFlags(r1, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.commands.sm.Sm.runIsolatedStorage():void");
    }

    public void runIdleMaint() throws RemoteException {
        if ("run".equals(nextArg())) {
            this.mSm.runIdleMaintenance();
        } else {
            this.mSm.abortIdleMaintenance();
        }
    }

    private String nextArg() {
        int i = this.mNextArg;
        String[] strArr = this.mArgs;
        if (i >= strArr.length) {
            return null;
        }
        String arg = strArr[i];
        this.mNextArg = i + 1;
        return arg;
    }

    private static int showUsage() {
        System.err.println("usage: sm list-disks [adoptable]");
        System.err.println("       sm list-volumes [public|private|emulated|stub|all]");
        System.err.println("       sm has-adoptable");
        System.err.println("       sm get-primary-storage-uuid");
        System.err.println("       sm set-force-adoptable [on|off|default]");
        System.err.println("       sm set-virtual-disk [true|false]");
        System.err.println("");
        System.err.println("       sm partition DISK [public|private|mixed] [ratio]");
        System.err.println("       sm mount VOLUME");
        System.err.println("       sm unmount VOLUME");
        System.err.println("       sm format VOLUME");
        System.err.println("       sm benchmark VOLUME");
        System.err.println("       sm idle-maint [run|abort]");
        System.err.println("       sm fstrim");
        System.err.println("");
        System.err.println("       sm forget [UUID|all]");
        System.err.println("");
        System.err.println("       sm set-emulate-fbe [true|false]");
        System.err.println("");
        System.err.println("       sm defrag [run|stop]");
        System.err.println("");
        System.err.println("       sm set-isolated-storage [on|off|default]");
        System.err.println("");
        return 1;
    }
}

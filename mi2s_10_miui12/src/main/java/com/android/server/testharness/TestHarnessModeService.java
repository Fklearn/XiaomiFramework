package com.android.server.testharness;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.debug.AdbManagerInternal;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.util.Slog;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.LocalServices;
import com.android.server.PersistentDataBlockManagerInternal;
import com.android.server.SystemService;
import com.android.server.pm.PackageManagerService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public class TestHarnessModeService extends SystemService {
    /* access modifiers changed from: private */
    public static final String TAG = TestHarnessModeService.class.getSimpleName();
    private static final String TEST_HARNESS_MODE_PROPERTY = "persist.sys.test_harness";
    private PersistentDataBlockManagerInternal mPersistentDataBlockManagerInternal;
    private final IBinder mService = new Binder() {
        public void onShellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, ShellCallback callback, ResultReceiver resultReceiver) {
            new TestHarnessModeShellCommand().exec(this, in, out, err, args, callback, resultReceiver);
        }
    };

    public TestHarnessModeService(Context context) {
        super(context);
    }

    public void onStart() {
        publishBinderService("testharness", this.mService);
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            setUpTestHarnessMode();
        } else if (phase == 1000) {
            completeTestHarnessModeSetup();
            showNotificationIfEnabled();
        }
        super.onBootPhase(phase);
    }

    private void setUpTestHarnessMode() {
        Slog.d(TAG, "Setting up test harness mode");
        if (getTestHarnessModeData() != null) {
            setDeviceProvisioned();
            disableLockScreen();
            SystemProperties.set(TEST_HARNESS_MODE_PROPERTY, SplitScreenReporter.ACTION_ENTER_SPLIT);
        }
    }

    private void disableLockScreen() {
        new LockPatternUtils(getContext()).setLockScreenDisabled(true, getPrimaryUser().id);
    }

    private void completeTestHarnessModeSetup() {
        Slog.d(TAG, "Completing Test Harness Mode setup.");
        byte[] testHarnessModeData = getTestHarnessModeData();
        if (testHarnessModeData != null) {
            try {
                setUpAdbFiles(PersistentData.fromBytes(testHarnessModeData));
                configureSettings();
                configureUser();
            } catch (SetUpTestHarnessModeException e) {
                Slog.e(TAG, "Failed to set up Test Harness Mode. Bad data.", e);
            } catch (Throwable th) {
                getPersistentDataBlock().clearTestHarnessModeData();
                throw th;
            }
            getPersistentDataBlock().clearTestHarnessModeData();
        }
    }

    private byte[] getTestHarnessModeData() {
        PersistentDataBlockManagerInternal blockManager = getPersistentDataBlock();
        if (blockManager == null) {
            Slog.e(TAG, "Failed to start Test Harness Mode; no implementation of PersistentDataBlockManagerInternal was bound!");
            return null;
        }
        byte[] testHarnessModeData = blockManager.getTestHarnessModeData();
        if (testHarnessModeData == null || testHarnessModeData.length == 0) {
            return null;
        }
        return testHarnessModeData;
    }

    private void configureSettings() {
        ContentResolver cr = getContext().getContentResolver();
        Settings.Global.putLong(cr, "adb_allowed_connection_time", 0);
        Settings.Global.putInt(cr, "adb_enabled", 1);
        Settings.Global.putInt(cr, "development_settings_enabled", 1);
        Settings.Global.putInt(cr, "package_verifier_enable", 0);
        Settings.Global.putInt(cr, "stay_on_while_plugged_in", 7);
        Settings.Global.putInt(cr, "ota_disable_automatic_update", 1);
    }

    private void setUpAdbFiles(PersistentData persistentData) {
        AdbManagerInternal adbManager = (AdbManagerInternal) LocalServices.getService(AdbManagerInternal.class);
        writeBytesToFile(persistentData.mAdbKeys, adbManager.getAdbKeysFile().toPath());
        writeBytesToFile(persistentData.mAdbTempKeys, adbManager.getAdbTempKeysFile().toPath());
    }

    private void configureUser() {
        UserInfo primaryUser = getPrimaryUser();
        ContentResolver.setMasterSyncAutomaticallyAsUser(false, primaryUser.id);
        ((LocationManager) getContext().getSystemService(LocationManager.class)).setLocationEnabledForUser(true, primaryUser.getUserHandle());
    }

    /* access modifiers changed from: private */
    public UserInfo getPrimaryUser() {
        return UserManager.get(getContext()).getPrimaryUser();
    }

    private void writeBytesToFile(byte[] keys, Path adbKeys) {
        try {
            OutputStream fileOutputStream = Files.newOutputStream(adbKeys, new OpenOption[0]);
            fileOutputStream.write(keys);
            fileOutputStream.close();
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(adbKeys, new LinkOption[0]);
            permissions.add(PosixFilePermission.GROUP_READ);
            Files.setPosixFilePermissions(adbKeys, permissions);
        } catch (IOException e) {
            Slog.e(TAG, "Failed to set up adb keys", e);
        }
    }

    private void setDeviceProvisioned() {
        ContentResolver cr = getContext().getContentResolver();
        Settings.Global.putInt(cr, "device_provisioned", 1);
        Settings.Secure.putIntForUser(cr, "user_setup_complete", 1, -2);
    }

    private void showNotificationIfEnabled() {
        if (SystemProperties.getBoolean(TEST_HARNESS_MODE_PROPERTY, false)) {
            String title = getContext().getString(17041222);
            ((NotificationManager) getContext().getSystemService(NotificationManager.class)).notifyAsUser((String) null, 54, new Notification.Builder(getContext(), SystemNotificationChannels.DEVELOPER).setSmallIcon(17303570).setWhen(0).setOngoing(true).setTicker(title).setDefaults(0).setColor(getContext().getColor(17170460)).setContentTitle(title).setContentText(getContext().getString(17041221)).setVisibility(1).build(), UserHandle.ALL);
        }
    }

    /* access modifiers changed from: private */
    public PersistentDataBlockManagerInternal getPersistentDataBlock() {
        if (this.mPersistentDataBlockManagerInternal == null) {
            Slog.d(TAG, "Getting PersistentDataBlockManagerInternal from LocalServices");
            this.mPersistentDataBlockManagerInternal = (PersistentDataBlockManagerInternal) LocalServices.getService(PersistentDataBlockManagerInternal.class);
        }
        return this.mPersistentDataBlockManagerInternal;
    }

    private class TestHarnessModeShellCommand extends ShellCommand {
        private TestHarnessModeShellCommand() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x0028 A[ADDED_TO_REGION] */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x003c A[Catch:{ all -> 0x0052 }] */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x004a A[SYNTHETIC, Splitter:B:22:0x004a] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int onCommand(java.lang.String r5) {
            /*
                r4 = this;
                int r0 = r5.hashCode()
                r1 = -1298848381(0xffffffffb2952583, float:-1.7362941E-8)
                r2 = 1
                if (r0 == r1) goto L_0x001b
                r1 = 1097519758(0x416ad28e, float:14.676405)
                if (r0 == r1) goto L_0x0010
            L_0x000f:
                goto L_0x0025
            L_0x0010:
                java.lang.String r0 = "restore"
                boolean r0 = r5.equals(r0)
                if (r0 == 0) goto L_0x000f
                r0 = r2
                goto L_0x0026
            L_0x001b:
                java.lang.String r0 = "enable"
                boolean r0 = r5.equals(r0)
                if (r0 == 0) goto L_0x000f
                r0 = 0
                goto L_0x0026
            L_0x0025:
                r0 = -1
            L_0x0026:
                if (r0 == 0) goto L_0x002f
                if (r0 == r2) goto L_0x002f
                int r0 = r4.handleDefaultCommands(r5)
                return r0
            L_0x002f:
                r4.checkPermissions()
                long r0 = android.os.Binder.clearCallingIdentity()
                boolean r2 = r4.isDeviceSecure()     // Catch:{ all -> 0x0052 }
                if (r2 == 0) goto L_0x004a
                java.io.PrintWriter r2 = r4.getErrPrintWriter()     // Catch:{ all -> 0x0052 }
                java.lang.String r3 = "Test Harness Mode cannot be enabled if there is a lock screen"
                r2.println(r3)     // Catch:{ all -> 0x0052 }
                r2 = 2
                android.os.Binder.restoreCallingIdentity(r0)
                return r2
            L_0x004a:
                int r2 = r4.handleEnable()     // Catch:{ all -> 0x0052 }
                android.os.Binder.restoreCallingIdentity(r0)
                return r2
            L_0x0052:
                r2 = move-exception
                android.os.Binder.restoreCallingIdentity(r0)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.testharness.TestHarnessModeService.TestHarnessModeShellCommand.onCommand(java.lang.String):int");
        }

        private void checkPermissions() {
            TestHarnessModeService.this.getContext().enforceCallingPermission("android.permission.ENABLE_TEST_HARNESS_MODE", "You must hold android.permission.ENABLE_TEST_HARNESS_MODE to enable Test Harness Mode");
        }

        private boolean isDeviceSecure() {
            return ((KeyguardManager) TestHarnessModeService.this.getContext().getSystemService(KeyguardManager.class)).isDeviceSecure(TestHarnessModeService.this.getPrimaryUser().id);
        }

        private int handleEnable() {
            AdbManagerInternal adbManager = (AdbManagerInternal) LocalServices.getService(AdbManagerInternal.class);
            File adbKeys = adbManager.getAdbKeysFile();
            File adbTempKeys = adbManager.getAdbTempKeysFile();
            if (adbKeys == null && adbTempKeys == null) {
                getErrPrintWriter().println("No ADB keys stored; not enabling test harness mode");
                return 1;
            }
            try {
                PersistentData persistentData = new PersistentData(getBytesFromFile(adbKeys), getBytesFromFile(adbTempKeys));
                PersistentDataBlockManagerInternal blockManager = TestHarnessModeService.this.getPersistentDataBlock();
                if (blockManager == null) {
                    Slog.e(TestHarnessModeService.TAG, "Failed to enable Test Harness Mode. No implementation of PersistentDataBlockManagerInternal was bound.");
                    getErrPrintWriter().println("Failed to enable Test Harness Mode");
                    return 1;
                }
                blockManager.setTestHarnessModeData(persistentData.toBytes());
                Intent i = new Intent("android.intent.action.FACTORY_RESET");
                i.setPackage(PackageManagerService.PLATFORM_PACKAGE_NAME);
                i.addFlags(268435456);
                i.putExtra("android.intent.extra.REASON", TestHarnessModeService.TAG);
                i.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", true);
                TestHarnessModeService.this.getContext().sendBroadcastAsUser(i, UserHandle.SYSTEM);
                return 0;
            } catch (IOException e) {
                Slog.e(TestHarnessModeService.TAG, "Failed to store ADB keys.", e);
                getErrPrintWriter().println("Failed to enable Test Harness Mode");
                return 1;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0030, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0031, code lost:
            if (r0 != null) goto L_0x0033;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            r0.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0037, code lost:
            r4 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0038, code lost:
            r2.addSuppressed(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x003b, code lost:
            throw r3;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private byte[] getBytesFromFile(java.io.File r8) throws java.io.IOException {
            /*
                r7 = this;
                r0 = 0
                if (r8 == 0) goto L_0x003c
                boolean r1 = r8.exists()
                if (r1 != 0) goto L_0x000a
                goto L_0x003c
            L_0x000a:
                java.nio.file.Path r1 = r8.toPath()
                java.nio.file.OpenOption[] r0 = new java.nio.file.OpenOption[r0]
                java.io.InputStream r0 = java.nio.file.Files.newInputStream(r1, r0)
                long r2 = java.nio.file.Files.size(r1)     // Catch:{ all -> 0x002e }
                int r2 = (int) r2     // Catch:{ all -> 0x002e }
                byte[] r3 = new byte[r2]     // Catch:{ all -> 0x002e }
                int r4 = r0.read(r3)     // Catch:{ all -> 0x002e }
                if (r4 != r2) goto L_0x0026
                r0.close()
                return r3
            L_0x0026:
                java.io.IOException r5 = new java.io.IOException     // Catch:{ all -> 0x002e }
                java.lang.String r6 = "Failed to read the whole file"
                r5.<init>(r6)     // Catch:{ all -> 0x002e }
                throw r5     // Catch:{ all -> 0x002e }
            L_0x002e:
                r2 = move-exception
                throw r2     // Catch:{ all -> 0x0030 }
            L_0x0030:
                r3 = move-exception
                if (r0 == 0) goto L_0x003b
                r0.close()     // Catch:{ all -> 0x0037 }
                goto L_0x003b
            L_0x0037:
                r4 = move-exception
                r2.addSuppressed(r4)
            L_0x003b:
                throw r3
            L_0x003c:
                byte[] r0 = new byte[r0]
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.testharness.TestHarnessModeService.TestHarnessModeShellCommand.getBytesFromFile(java.io.File):byte[]");
        }

        public void onHelp() {
            PrintWriter pw = getOutPrintWriter();
            pw.println("About:");
            pw.println("  Test Harness Mode is a mode that the device can be placed in to prepare");
            pw.println("  the device for running UI tests. The device is placed into this mode by");
            pw.println("  first wiping all data from the device, preserving ADB keys.");
            pw.println();
            pw.println("  By default, the following settings are configured:");
            pw.println("    * Package Verifier is disabled");
            pw.println("    * Stay Awake While Charging is enabled");
            pw.println("    * OTA Updates are disabled");
            pw.println("    * Auto-Sync for accounts is disabled");
            pw.println();
            pw.println("  Other apps may configure themselves differently in Test Harness Mode by");
            pw.println("  checking ActivityManager.isRunningInUserTestHarness()");
            pw.println();
            pw.println("Test Harness Mode commands:");
            pw.println("  help");
            pw.println("    Print this help text.");
            pw.println();
            pw.println("  enable|restore");
            pw.println("    Erase all data from this device and enable Test Harness Mode,");
            pw.println("    preserving the stored ADB keys currently on the device and toggling");
            pw.println("    settings in a way that are conducive to Instrumentation testing.");
        }
    }

    public static class PersistentData {
        static final byte VERSION_1 = 1;
        static final byte VERSION_2 = 2;
        final byte[] mAdbKeys;
        final byte[] mAdbTempKeys;
        final int mVersion;

        PersistentData(byte[] adbKeys, byte[] adbTempKeys) {
            this(2, adbKeys, adbTempKeys);
        }

        PersistentData(int version, byte[] adbKeys, byte[] adbTempKeys) {
            this.mVersion = version;
            this.mAdbKeys = adbKeys;
            this.mAdbTempKeys = adbTempKeys;
        }

        static PersistentData fromBytes(byte[] bytes) throws SetUpTestHarnessModeException {
            try {
                DataInputStream is = new DataInputStream(new ByteArrayInputStream(bytes));
                int version = is.readInt();
                if (version == 1) {
                    is.readBoolean();
                }
                byte[] adbKeys = new byte[is.readInt()];
                is.readFully(adbKeys);
                byte[] adbTempKeys = new byte[is.readInt()];
                is.readFully(adbTempKeys);
                return new PersistentData(version, adbKeys, adbTempKeys);
            } catch (IOException e) {
                throw new SetUpTestHarnessModeException(e);
            }
        }

        /* access modifiers changed from: package-private */
        public byte[] toBytes() {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeInt(2);
                dos.writeInt(this.mAdbKeys.length);
                dos.write(this.mAdbKeys);
                dos.writeInt(this.mAdbTempKeys.length);
                dos.write(this.mAdbTempKeys);
                dos.close();
                return os.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class SetUpTestHarnessModeException extends Exception {
        SetUpTestHarnessModeException(Exception e) {
            super(e);
        }
    }
}

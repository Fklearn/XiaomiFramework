package com.android.server.adb;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.AtomicFile;
import android.util.Base64;
import android.util.Slog;
import android.util.StatsLog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.dump.DumpUtils;
import com.android.server.FgThread;
import com.android.server.usage.UnixCalendar;
import com.android.server.usb.descriptors.UsbDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlSerializer;

public class AdbDebuggingManager {
    private static final String ADBD_SOCKET = "adbd";
    private static final String ADB_DIRECTORY = "misc/adb";
    private static final String ADB_KEYS_FILE = "adb_keys";
    private static final String ADB_TEMP_KEYS_FILE = "adb_temp_keys.xml";
    private static final int BUFFER_SIZE = 65536;
    private static final boolean DEBUG = false;
    private static final String TAG = "AdbDebuggingManager";
    /* access modifiers changed from: private */
    public boolean mAdbEnabled = false;
    private String mConfirmComponent;
    /* access modifiers changed from: private */
    public final List<String> mConnectedKeys;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public String mFingerprints;
    /* access modifiers changed from: private */
    public final Handler mHandler = new AdbDebuggingHandler(FgThread.get().getLooper());
    private final File mTestUserKeyFile;
    /* access modifiers changed from: private */
    public AdbDebuggingThread mThread;

    public AdbDebuggingManager(Context context) {
        this.mContext = context;
        this.mTestUserKeyFile = null;
        this.mConnectedKeys = new ArrayList(1);
    }

    protected AdbDebuggingManager(Context context, String confirmComponent, File testUserKeyFile) {
        this.mContext = context;
        this.mConfirmComponent = confirmComponent;
        this.mTestUserKeyFile = testUserKeyFile;
        this.mConnectedKeys = new ArrayList();
    }

    class AdbDebuggingThread extends Thread {
        private InputStream mInputStream;
        private OutputStream mOutputStream;
        private LocalSocket mSocket;
        private boolean mStopped;

        AdbDebuggingThread() {
            super(AdbDebuggingManager.TAG);
        }

        public void run() {
            while (true) {
                synchronized (this) {
                    if (!this.mStopped) {
                        try {
                            openSocketLocked();
                        } catch (Exception e) {
                            SystemClock.sleep(1000);
                        }
                    } else {
                        return;
                    }
                }
            }
            try {
                listenToSocket();
            } catch (Exception e2) {
                SystemClock.sleep(1000);
            }
        }

        private void openSocketLocked() throws IOException {
            try {
                LocalSocketAddress address = new LocalSocketAddress(AdbDebuggingManager.ADBD_SOCKET, LocalSocketAddress.Namespace.RESERVED);
                this.mInputStream = null;
                this.mSocket = new LocalSocket(3);
                this.mSocket.connect(address);
                this.mOutputStream = this.mSocket.getOutputStream();
                this.mInputStream = this.mSocket.getInputStream();
            } catch (IOException ioe) {
                Slog.e(AdbDebuggingManager.TAG, "Caught an exception opening the socket: " + ioe);
                closeSocketLocked();
                throw ioe;
            }
        }

        private void listenToSocket() throws IOException {
            try {
                byte[] buffer = new byte[65536];
                while (true) {
                    int count = this.mInputStream.read(buffer);
                    if (count < 2) {
                        Slog.w(AdbDebuggingManager.TAG, "Read failed with count " + count);
                        break;
                    } else if (buffer[0] == 80 && buffer[1] == 75) {
                        String key = new String(Arrays.copyOfRange(buffer, 2, count));
                        Slog.d(AdbDebuggingManager.TAG, "Received public key: " + key);
                        Message msg = AdbDebuggingManager.this.mHandler.obtainMessage(5);
                        msg.obj = key;
                        AdbDebuggingManager.this.mHandler.sendMessage(msg);
                    } else if (buffer[0] == 68 && buffer[1] == 67) {
                        String key2 = new String(Arrays.copyOfRange(buffer, 2, count));
                        Slog.d(AdbDebuggingManager.TAG, "Received disconnected message: " + key2);
                        Message msg2 = AdbDebuggingManager.this.mHandler.obtainMessage(7);
                        msg2.obj = key2;
                        AdbDebuggingManager.this.mHandler.sendMessage(msg2);
                    } else if (buffer[0] == 67 && buffer[1] == 75) {
                        String key3 = new String(Arrays.copyOfRange(buffer, 2, count));
                        Slog.d(AdbDebuggingManager.TAG, "Received connected key message: " + key3);
                        Message msg3 = AdbDebuggingManager.this.mHandler.obtainMessage(10);
                        msg3.obj = key3;
                        AdbDebuggingManager.this.mHandler.sendMessage(msg3);
                    } else {
                        Slog.e(AdbDebuggingManager.TAG, "Wrong message: " + new String(Arrays.copyOfRange(buffer, 0, 2)));
                    }
                }
                Slog.e(AdbDebuggingManager.TAG, "Wrong message: " + new String(Arrays.copyOfRange(buffer, 0, 2)));
                synchronized (this) {
                    closeSocketLocked();
                }
            } catch (Throwable th) {
                synchronized (this) {
                    closeSocketLocked();
                    throw th;
                }
            }
        }

        private void closeSocketLocked() {
            try {
                if (this.mOutputStream != null) {
                    this.mOutputStream.close();
                    this.mOutputStream = null;
                }
            } catch (IOException e) {
                Slog.e(AdbDebuggingManager.TAG, "Failed closing output stream: " + e);
            }
            try {
                if (this.mSocket != null) {
                    this.mSocket.close();
                    this.mSocket = null;
                }
            } catch (IOException ex) {
                Slog.e(AdbDebuggingManager.TAG, "Failed closing socket: " + ex);
            }
        }

        /* access modifiers changed from: package-private */
        public void stopListening() {
            synchronized (this) {
                this.mStopped = true;
                closeSocketLocked();
            }
        }

        /* access modifiers changed from: package-private */
        public void sendResponse(String msg) {
            synchronized (this) {
                if (!this.mStopped && this.mOutputStream != null) {
                    try {
                        this.mOutputStream.write(msg.getBytes());
                    } catch (IOException ex) {
                        Slog.e(AdbDebuggingManager.TAG, "Failed to write response:", ex);
                    }
                }
            }
        }
    }

    class AdbDebuggingHandler extends Handler {
        static final int MESSAGE_ADB_ALLOW = 3;
        static final int MESSAGE_ADB_CLEAR = 6;
        static final int MESSAGE_ADB_CONFIRM = 5;
        static final int MESSAGE_ADB_CONNECTED_KEY = 10;
        static final int MESSAGE_ADB_DENY = 4;
        static final int MESSAGE_ADB_DISABLED = 2;
        static final int MESSAGE_ADB_DISCONNECT = 7;
        static final int MESSAGE_ADB_ENABLED = 1;
        static final int MESSAGE_ADB_PERSIST_KEYSTORE = 8;
        static final int MESSAGE_ADB_UPDATE_KEYSTORE = 9;
        static final long UPDATE_KEYSTORE_JOB_INTERVAL = 86400000;
        static final long UPDATE_KEYSTORE_MIN_JOB_INTERVAL = 60000;
        private AdbKeyStore mAdbKeyStore;
        private ContentObserver mAuthTimeObserver = new ContentObserver(this) {
            public void onChange(boolean selfChange, Uri uri) {
                Slog.d(AdbDebuggingManager.TAG, "Received notification that uri " + uri + " was modified; rescheduling keystore job");
                AdbDebuggingHandler.this.scheduleJobToUpdateAdbKeyStore();
            }
        };

        AdbDebuggingHandler(Looper looper) {
            super(looper);
        }

        AdbDebuggingHandler(Looper looper, AdbDebuggingThread thread, AdbKeyStore adbKeyStore) {
            super(looper);
            AdbDebuggingThread unused = AdbDebuggingManager.this.mThread = thread;
            this.mAdbKeyStore = adbKeyStore;
        }

        public void handleMessage(Message msg) {
            boolean z = true;
            switch (msg.what) {
                case 1:
                    if (!AdbDebuggingManager.this.mAdbEnabled) {
                        registerForAuthTimeChanges();
                        boolean unused = AdbDebuggingManager.this.mAdbEnabled = true;
                        AdbDebuggingManager adbDebuggingManager = AdbDebuggingManager.this;
                        AdbDebuggingThread unused2 = adbDebuggingManager.mThread = new AdbDebuggingThread();
                        AdbDebuggingManager.this.mThread.start();
                        this.mAdbKeyStore = new AdbKeyStore();
                        this.mAdbKeyStore.updateKeyStore();
                        scheduleJobToUpdateAdbKeyStore();
                        return;
                    }
                    return;
                case 2:
                    if (AdbDebuggingManager.this.mAdbEnabled) {
                        boolean unused3 = AdbDebuggingManager.this.mAdbEnabled = false;
                        if (AdbDebuggingManager.this.mThread != null) {
                            AdbDebuggingManager.this.mThread.stopListening();
                            AdbDebuggingThread unused4 = AdbDebuggingManager.this.mThread = null;
                        }
                        if (!AdbDebuggingManager.this.mConnectedKeys.isEmpty()) {
                            for (String connectedKey : AdbDebuggingManager.this.mConnectedKeys) {
                                this.mAdbKeyStore.setLastConnectionTime(connectedKey, System.currentTimeMillis());
                            }
                            AdbDebuggingManager.this.sendPersistKeyStoreMessage();
                            AdbDebuggingManager.this.mConnectedKeys.clear();
                        }
                        scheduleJobToUpdateAdbKeyStore();
                        return;
                    }
                    return;
                case 3:
                    String key = (String) msg.obj;
                    String fingerprints = AdbDebuggingManager.this.getFingerprints(key);
                    if (!fingerprints.equals(AdbDebuggingManager.this.mFingerprints)) {
                        Slog.e(AdbDebuggingManager.TAG, "Fingerprints do not match. Got " + fingerprints + ", expected " + AdbDebuggingManager.this.mFingerprints);
                        return;
                    }
                    if (msg.arg1 != 1) {
                        z = false;
                    }
                    boolean alwaysAllow = z;
                    if (AdbDebuggingManager.this.mThread != null) {
                        AdbDebuggingManager.this.mThread.sendResponse("OK");
                        if (alwaysAllow) {
                            if (!AdbDebuggingManager.this.mConnectedKeys.contains(key)) {
                                AdbDebuggingManager.this.mConnectedKeys.add(key);
                            }
                            this.mAdbKeyStore.setLastConnectionTime(key, System.currentTimeMillis());
                            AdbDebuggingManager.this.sendPersistKeyStoreMessage();
                            scheduleJobToUpdateAdbKeyStore();
                        }
                        logAdbConnectionChanged(key, 2, alwaysAllow);
                        return;
                    }
                    return;
                case 4:
                    if (AdbDebuggingManager.this.mThread != null) {
                        AdbDebuggingManager.this.mThread.sendResponse("NO");
                        logAdbConnectionChanged((String) null, 3, false);
                        return;
                    }
                    return;
                case 5:
                    String key2 = (String) msg.obj;
                    if ("trigger_restart_min_framework".equals(SystemProperties.get("vold.decrypt"))) {
                        Slog.d(AdbDebuggingManager.TAG, "Deferring adb confirmation until after vold decrypt");
                        if (AdbDebuggingManager.this.mThread != null) {
                            AdbDebuggingManager.this.mThread.sendResponse("NO");
                            logAdbConnectionChanged(key2, 6, false);
                            return;
                        }
                        return;
                    }
                    String fingerprints2 = AdbDebuggingManager.this.getFingerprints(key2);
                    if (!"".equals(fingerprints2)) {
                        logAdbConnectionChanged(key2, 1, false);
                        String unused5 = AdbDebuggingManager.this.mFingerprints = fingerprints2;
                        AdbDebuggingManager adbDebuggingManager2 = AdbDebuggingManager.this;
                        adbDebuggingManager2.startConfirmation(key2, adbDebuggingManager2.mFingerprints);
                        return;
                    } else if (AdbDebuggingManager.this.mThread != null) {
                        AdbDebuggingManager.this.mThread.sendResponse("NO");
                        logAdbConnectionChanged(key2, 5, false);
                        return;
                    } else {
                        return;
                    }
                case 6:
                    Slog.d(AdbDebuggingManager.TAG, "Received a request to clear the adb authorizations");
                    AdbDebuggingManager.this.mConnectedKeys.clear();
                    AdbKeyStore adbKeyStore = this.mAdbKeyStore;
                    if (adbKeyStore == null) {
                        adbKeyStore = new AdbKeyStore();
                    }
                    adbKeyStore.deleteKeyStore();
                    cancelJobToUpdateAdbKeyStore();
                    return;
                case 7:
                    String key3 = (String) msg.obj;
                    boolean alwaysAllow2 = false;
                    if (key3 == null || key3.length() <= 0) {
                        Slog.w(AdbDebuggingManager.TAG, "Received a disconnected key message with an empty key");
                    } else if (AdbDebuggingManager.this.mConnectedKeys.contains(key3)) {
                        alwaysAllow2 = true;
                        this.mAdbKeyStore.setLastConnectionTime(key3, System.currentTimeMillis());
                        AdbDebuggingManager.this.sendPersistKeyStoreMessage();
                        scheduleJobToUpdateAdbKeyStore();
                        AdbDebuggingManager.this.mConnectedKeys.remove(key3);
                    }
                    logAdbConnectionChanged(key3, 7, alwaysAllow2);
                    return;
                case 8:
                    AdbKeyStore adbKeyStore2 = this.mAdbKeyStore;
                    if (adbKeyStore2 != null) {
                        adbKeyStore2.persistKeyStore();
                        return;
                    }
                    return;
                case 9:
                    if (!AdbDebuggingManager.this.mConnectedKeys.isEmpty()) {
                        for (String connectedKey2 : AdbDebuggingManager.this.mConnectedKeys) {
                            this.mAdbKeyStore.setLastConnectionTime(connectedKey2, System.currentTimeMillis());
                        }
                        AdbDebuggingManager.this.sendPersistKeyStoreMessage();
                        scheduleJobToUpdateAdbKeyStore();
                        return;
                    } else if (!this.mAdbKeyStore.isEmpty()) {
                        this.mAdbKeyStore.updateKeyStore();
                        scheduleJobToUpdateAdbKeyStore();
                        return;
                    } else {
                        return;
                    }
                case 10:
                    String key4 = (String) msg.obj;
                    if (key4 == null || key4.length() == 0) {
                        Slog.w(AdbDebuggingManager.TAG, "Received a connected key message with an empty key");
                        return;
                    }
                    if (!AdbDebuggingManager.this.mConnectedKeys.contains(key4)) {
                        AdbDebuggingManager.this.mConnectedKeys.add(key4);
                    }
                    this.mAdbKeyStore.setLastConnectionTime(key4, System.currentTimeMillis());
                    AdbDebuggingManager.this.sendPersistKeyStoreMessage();
                    scheduleJobToUpdateAdbKeyStore();
                    logAdbConnectionChanged(key4, 4, true);
                    return;
                default:
                    return;
            }
        }

        /* access modifiers changed from: package-private */
        public void registerForAuthTimeChanges() {
            AdbDebuggingManager.this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("adb_allowed_connection_time"), false, this.mAuthTimeObserver);
        }

        private void logAdbConnectionChanged(String key, int state, boolean alwaysAllow) {
            long lastConnectionTime = this.mAdbKeyStore.getLastConnectionTime(key);
            long authWindow = this.mAdbKeyStore.getAllowedConnectionTime();
            Slog.d(AdbDebuggingManager.TAG, "Logging key " + key + ", state = " + state + ", alwaysAllow = " + alwaysAllow + ", lastConnectionTime = " + lastConnectionTime + ", authWindow = " + authWindow);
            StatsLog.write(144, lastConnectionTime, authWindow, state, alwaysAllow);
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public long scheduleJobToUpdateAdbKeyStore() {
            long delay;
            cancelJobToUpdateAdbKeyStore();
            long keyExpiration = this.mAdbKeyStore.getNextExpirationTime();
            if (keyExpiration == -1) {
                return -1;
            }
            if (keyExpiration == 0) {
                delay = 0;
            } else {
                delay = Math.max(Math.min(86400000, keyExpiration), 60000);
            }
            sendMessageDelayed(obtainMessage(9), delay);
            return delay;
        }

        private void cancelJobToUpdateAdbKeyStore() {
            removeMessages(9);
        }
    }

    /* access modifiers changed from: private */
    public String getFingerprints(String key) {
        StringBuilder sb = new StringBuilder();
        if (key == null) {
            return "";
        }
        try {
            try {
                byte[] digest = MessageDigest.getInstance("MD5").digest(Base64.decode(key.split("\\s+")[0].getBytes(), 0));
                for (int i = 0; i < digest.length; i++) {
                    sb.append("0123456789ABCDEF".charAt((digest[i] >> 4) & 15));
                    sb.append("0123456789ABCDEF".charAt(digest[i] & UsbDescriptor.DESCRIPTORTYPE_BOS));
                    if (i < digest.length - 1) {
                        sb.append(":");
                    }
                }
                return sb.toString();
            } catch (IllegalArgumentException e) {
                Slog.e(TAG, "error doing base64 decoding", e);
                return "";
            }
        } catch (Exception ex) {
            Slog.e(TAG, "Error getting digester", ex);
            return "";
        }
    }

    /* access modifiers changed from: private */
    public void startConfirmation(String key, String fingerprints) {
        String componentString;
        UserInfo userInfo = UserManager.get(this.mContext).getUserInfo(ActivityManager.getCurrentUser());
        if (userInfo.isAdmin()) {
            componentString = this.mConfirmComponent;
            if (componentString == null) {
                componentString = Resources.getSystem().getString(17039712);
            }
        } else {
            componentString = Resources.getSystem().getString(17039713);
        }
        ComponentName componentName = ComponentName.unflattenFromString(componentString);
        if (!startConfirmationActivity(componentName, userInfo.getUserHandle(), key, fingerprints) && !startConfirmationService(componentName, userInfo.getUserHandle(), key, fingerprints)) {
            Slog.e(TAG, "unable to start customAdbPublicKeyConfirmation[SecondaryUser]Component " + componentString + " as an Activity or a Service");
        }
    }

    private boolean startConfirmationActivity(ComponentName componentName, UserHandle userHandle, String key, String fingerprints) {
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent intent = createConfirmationIntent(componentName, key, fingerprints);
        intent.addFlags(268435456);
        if (packageManager.resolveActivity(intent, 65536) == null) {
            return false;
        }
        try {
            this.mContext.startActivityAsUser(intent, userHandle);
            return true;
        } catch (ActivityNotFoundException e) {
            Slog.e(TAG, "unable to start adb whitelist activity: " + componentName, e);
            return false;
        }
    }

    private boolean startConfirmationService(ComponentName componentName, UserHandle userHandle, String key, String fingerprints) {
        try {
            if (this.mContext.startServiceAsUser(createConfirmationIntent(componentName, key, fingerprints), userHandle) != null) {
                return true;
            }
            return false;
        } catch (SecurityException e) {
            Slog.e(TAG, "unable to start adb whitelist service: " + componentName, e);
            return false;
        }
    }

    private Intent createConfirmationIntent(ComponentName componentName, String key, String fingerprints) {
        Intent intent = new Intent();
        intent.setClassName(componentName.getPackageName(), componentName.getClassName());
        intent.putExtra("key", key);
        intent.putExtra("fingerprints", fingerprints);
        return intent;
    }

    private File getAdbFile(String fileName) {
        File adbDir = new File(Environment.getDataDirectory(), ADB_DIRECTORY);
        if (adbDir.exists()) {
            return new File(adbDir, fileName);
        }
        Slog.e(TAG, "ADB data directory does not exist");
        return null;
    }

    /* access modifiers changed from: package-private */
    public File getAdbTempKeysFile() {
        return getAdbFile(ADB_TEMP_KEYS_FILE);
    }

    /* access modifiers changed from: package-private */
    public File getUserKeyFile() {
        File file = this.mTestUserKeyFile;
        return file == null ? getAdbFile(ADB_KEYS_FILE) : file;
    }

    /* access modifiers changed from: private */
    public void writeKey(String key) {
        try {
            File keyFile = getUserKeyFile();
            if (keyFile != null) {
                FileOutputStream fo = new FileOutputStream(keyFile, true);
                fo.write(key.getBytes());
                fo.write(10);
                fo.close();
                FileUtils.setPermissions(keyFile.toString(), 416, -1, -1);
            }
        } catch (IOException ex) {
            Slog.e(TAG, "Error writing key:" + ex);
        }
    }

    /* access modifiers changed from: private */
    public void writeKeys(Iterable<String> keys) {
        AtomicFile atomicKeyFile = null;
        try {
            File keyFile = getUserKeyFile();
            if (keyFile != null) {
                AtomicFile atomicKeyFile2 = new AtomicFile(keyFile);
                FileOutputStream fo = atomicKeyFile2.startWrite();
                for (String key : keys) {
                    fo.write(key.getBytes());
                    fo.write(10);
                }
                atomicKeyFile2.finishWrite(fo);
                FileUtils.setPermissions(keyFile.toString(), 416, -1, -1);
            }
        } catch (IOException ex) {
            Slog.e(TAG, "Error writing keys: " + ex);
            if (atomicKeyFile != null) {
                atomicKeyFile.failWrite((FileOutputStream) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public void deleteKeyFile() {
        File keyFile = getUserKeyFile();
        if (keyFile != null) {
            keyFile.delete();
        }
    }

    public void setAdbEnabled(boolean enabled) {
        int i;
        Handler handler = this.mHandler;
        if (enabled) {
            i = 1;
        } else {
            i = 2;
        }
        handler.sendEmptyMessage(i);
    }

    public void allowDebugging(boolean alwaysAllow, String publicKey) {
        Message msg = this.mHandler.obtainMessage(3);
        msg.arg1 = alwaysAllow;
        msg.obj = publicKey;
        this.mHandler.sendMessage(msg);
    }

    public void denyDebugging() {
        this.mHandler.sendEmptyMessage(4);
    }

    public void clearDebuggingKeys() {
        this.mHandler.sendEmptyMessage(6);
    }

    /* access modifiers changed from: private */
    public void sendPersistKeyStoreMessage() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(8));
    }

    public void dump(DualDumpOutputStream dump, String idName, long id) {
        long token = dump.start(idName, id);
        dump.write("connected_to_adb", 1133871366145L, this.mThread != null);
        DumpUtils.writeStringIfNotNull(dump, "last_key_received", 1138166333442L, this.mFingerprints);
        try {
            dump.write("user_keys", 1138166333443L, FileUtils.readTextFile(new File("/data/misc/adb/adb_keys"), 0, (String) null));
        } catch (IOException e) {
            Slog.e(TAG, "Cannot read user keys", e);
        }
        try {
            dump.write("system_keys", 1138166333444L, FileUtils.readTextFile(new File("/adb_keys"), 0, (String) null));
        } catch (IOException e2) {
            Slog.e(TAG, "Cannot read system keys", e2);
        }
        try {
            dump.write("keystore", 1138166333445L, FileUtils.readTextFile(getAdbTempKeysFile(), 0, (String) null));
        } catch (IOException e3) {
            Slog.e(TAG, "Cannot read keystore: ", e3);
        }
        dump.end(token);
    }

    class AdbKeyStore {
        public static final long NO_PREVIOUS_CONNECTION = 0;
        private static final String SYSTEM_KEY_FILE = "/adb_keys";
        private static final String XML_ATTRIBUTE_KEY = "key";
        private static final String XML_ATTRIBUTE_LAST_CONNECTION = "lastConnection";
        private static final String XML_TAG_ADB_KEY = "adbKey";
        private AtomicFile mAtomicKeyFile;
        private File mKeyFile;
        private Map<String, Long> mKeyMap;
        private Set<String> mSystemKeys;

        AdbKeyStore() {
            init();
        }

        AdbKeyStore(File keyFile) {
            this.mKeyFile = keyFile;
            init();
        }

        private void init() {
            initKeyFile();
            this.mKeyMap = getKeyMap();
            this.mSystemKeys = getSystemKeysFromFile(SYSTEM_KEY_FILE);
            addUserKeysToKeyStore();
        }

        private void initKeyFile() {
            if (this.mKeyFile == null) {
                this.mKeyFile = AdbDebuggingManager.this.getAdbTempKeysFile();
            }
            File file = this.mKeyFile;
            if (file != null) {
                this.mAtomicKeyFile = new AtomicFile(file);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
            $closeResource((java.lang.Throwable) null, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0036, code lost:
            r4 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
            $closeResource(r3, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x003a, code lost:
            throw r4;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.util.Set<java.lang.String> getSystemKeysFromFile(java.lang.String r7) {
            /*
                r6 = this;
                java.util.HashSet r0 = new java.util.HashSet
                r0.<init>()
                java.io.File r1 = new java.io.File
                r1.<init>(r7)
                boolean r2 = r1.exists()
                if (r2 == 0) goto L_0x005a
                java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ IOException -> 0x003b }
                java.io.FileReader r3 = new java.io.FileReader     // Catch:{ IOException -> 0x003b }
                r3.<init>(r1)     // Catch:{ IOException -> 0x003b }
                r2.<init>(r3)     // Catch:{ IOException -> 0x003b }
                r3 = 0
            L_0x001b:
                java.lang.String r4 = r2.readLine()     // Catch:{ all -> 0x0034 }
                r5 = r4
                if (r4 == 0) goto L_0x0030
                java.lang.String r4 = r5.trim()     // Catch:{ all -> 0x0034 }
                int r5 = r4.length()     // Catch:{ all -> 0x0034 }
                if (r5 <= 0) goto L_0x001b
                r0.add(r4)     // Catch:{ all -> 0x0034 }
                goto L_0x001b
            L_0x0030:
                $closeResource(r3, r2)     // Catch:{ IOException -> 0x003b }
                goto L_0x005a
            L_0x0034:
                r3 = move-exception
                throw r3     // Catch:{ all -> 0x0036 }
            L_0x0036:
                r4 = move-exception
                $closeResource(r3, r2)     // Catch:{ IOException -> 0x003b }
                throw r4     // Catch:{ IOException -> 0x003b }
            L_0x003b:
                r2 = move-exception
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "Caught an exception reading "
                r3.append(r4)
                r3.append(r7)
                java.lang.String r4 = ": "
                r3.append(r4)
                r3.append(r2)
                java.lang.String r3 = r3.toString()
                java.lang.String r4 = "AdbDebuggingManager"
                android.util.Slog.e(r4, r3)
            L_0x005a:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.adb.AdbDebuggingManager.AdbKeyStore.getSystemKeysFromFile(java.lang.String):java.util.Set");
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

        public boolean isEmpty() {
            return this.mKeyMap.isEmpty();
        }

        public void updateKeyStore() {
            if (filterOutOldKeys()) {
                AdbDebuggingManager.this.sendPersistKeyStoreMessage();
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 10 */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x00a9, code lost:
            r4 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x00aa, code lost:
            if (r2 != null) goto L_0x00ac;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
            $closeResource(r0, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x00af, code lost:
            throw r4;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.util.Map<java.lang.String, java.lang.Long> getKeyMap() {
            /*
                r10 = this;
                java.lang.String r0 = "adbKey"
                java.util.HashMap r1 = new java.util.HashMap
                r1.<init>()
                android.util.AtomicFile r2 = r10.mAtomicKeyFile
                java.lang.String r3 = "AdbDebuggingManager"
                if (r2 != 0) goto L_0x0030
                r10.initKeyFile()
                android.util.AtomicFile r2 = r10.mAtomicKeyFile
                if (r2 != 0) goto L_0x0030
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r2 = "Unable to obtain the key file, "
                r0.append(r2)
                java.io.File r2 = r10.mKeyFile
                r0.append(r2)
                java.lang.String r2 = ", for reading"
                r0.append(r2)
                java.lang.String r0 = r0.toString()
                android.util.Slog.e(r3, r0)
                return r1
            L_0x0030:
                android.util.AtomicFile r2 = r10.mAtomicKeyFile
                boolean r2 = r2.exists()
                if (r2 != 0) goto L_0x0039
                return r1
            L_0x0039:
                android.util.AtomicFile r2 = r10.mAtomicKeyFile     // Catch:{ IOException | XmlPullParserException -> 0x00b0 }
                java.io.FileInputStream r2 = r2.openRead()     // Catch:{ IOException | XmlPullParserException -> 0x00b0 }
                org.xmlpull.v1.XmlPullParser r4 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x00a7 }
                java.nio.charset.Charset r5 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ all -> 0x00a7 }
                java.lang.String r5 = r5.name()     // Catch:{ all -> 0x00a7 }
                r4.setInput(r2, r5)     // Catch:{ all -> 0x00a7 }
                com.android.internal.util.XmlUtils.beginDocument(r4, r0)     // Catch:{ all -> 0x00a7 }
            L_0x004f:
                int r5 = r4.next()     // Catch:{ all -> 0x00a7 }
                r6 = 1
                r7 = 0
                if (r5 == r6) goto L_0x00a1
                java.lang.String r5 = r4.getName()     // Catch:{ all -> 0x00a7 }
                if (r5 != 0) goto L_0x005e
                goto L_0x00a1
            L_0x005e:
                boolean r6 = r5.equals(r0)     // Catch:{ all -> 0x00a7 }
                if (r6 != 0) goto L_0x0068
                com.android.internal.util.XmlUtils.skipCurrentTag(r4)     // Catch:{ all -> 0x00a7 }
                goto L_0x004f
            L_0x0068:
                java.lang.String r6 = "key"
                java.lang.String r6 = r4.getAttributeValue(r7, r6)     // Catch:{ all -> 0x00a7 }
                java.lang.String r8 = "lastConnection"
                java.lang.String r7 = r4.getAttributeValue(r7, r8)     // Catch:{ NumberFormatException -> 0x0088 }
                java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch:{ NumberFormatException -> 0x0088 }
                long r7 = r7.longValue()     // Catch:{ NumberFormatException -> 0x0088 }
                java.lang.Long r9 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x00a7 }
                r1.put(r6, r9)     // Catch:{ all -> 0x00a7 }
                goto L_0x004f
            L_0x0088:
                r7 = move-exception
                java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a7 }
                r8.<init>()     // Catch:{ all -> 0x00a7 }
                java.lang.String r9 = "Caught a NumberFormatException parsing the last connection time: "
                r8.append(r9)     // Catch:{ all -> 0x00a7 }
                r8.append(r7)     // Catch:{ all -> 0x00a7 }
                java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x00a7 }
                android.util.Slog.e(r3, r8)     // Catch:{ all -> 0x00a7 }
                com.android.internal.util.XmlUtils.skipCurrentTag(r4)     // Catch:{ all -> 0x00a7 }
                goto L_0x004f
            L_0x00a1:
                if (r2 == 0) goto L_0x00a6
                $closeResource(r7, r2)     // Catch:{ IOException | XmlPullParserException -> 0x00b0 }
            L_0x00a6:
                goto L_0x00b6
            L_0x00a7:
                r0 = move-exception
                throw r0     // Catch:{ all -> 0x00a9 }
            L_0x00a9:
                r4 = move-exception
                if (r2 == 0) goto L_0x00af
                $closeResource(r0, r2)     // Catch:{ IOException | XmlPullParserException -> 0x00b0 }
            L_0x00af:
                throw r4     // Catch:{ IOException | XmlPullParserException -> 0x00b0 }
            L_0x00b0:
                r0 = move-exception
                java.lang.String r2 = "Caught an exception parsing the XML key file: "
                android.util.Slog.e(r3, r2, r0)
            L_0x00b6:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.adb.AdbDebuggingManager.AdbKeyStore.getKeyMap():java.util.Map");
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x003e, code lost:
            r4 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
            $closeResource(r3, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0042, code lost:
            throw r4;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void addUserKeysToKeyStore() {
            /*
                r9 = this;
                com.android.server.adb.AdbDebuggingManager r0 = com.android.server.adb.AdbDebuggingManager.this
                java.io.File r0 = r0.getUserKeyFile()
                r1 = 0
                if (r0 == 0) goto L_0x0062
                boolean r2 = r0.exists()
                if (r2 == 0) goto L_0x0062
                java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0043 }
                java.io.FileReader r3 = new java.io.FileReader     // Catch:{ IOException -> 0x0043 }
                r3.<init>(r0)     // Catch:{ IOException -> 0x0043 }
                r2.<init>(r3)     // Catch:{ IOException -> 0x0043 }
                r3 = 0
                long r4 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x003c }
            L_0x001e:
                java.lang.String r6 = r2.readLine()     // Catch:{ all -> 0x003c }
                r7 = r6
                if (r6 == 0) goto L_0x0038
                java.util.Map<java.lang.String, java.lang.Long> r6 = r9.mKeyMap     // Catch:{ all -> 0x003c }
                boolean r6 = r6.containsKey(r7)     // Catch:{ all -> 0x003c }
                if (r6 != 0) goto L_0x001e
                java.util.Map<java.lang.String, java.lang.Long> r6 = r9.mKeyMap     // Catch:{ all -> 0x003c }
                java.lang.Long r8 = java.lang.Long.valueOf(r4)     // Catch:{ all -> 0x003c }
                r6.put(r7, r8)     // Catch:{ all -> 0x003c }
                r1 = 1
                goto L_0x001e
            L_0x0038:
                $closeResource(r3, r2)     // Catch:{ IOException -> 0x0043 }
                goto L_0x0062
            L_0x003c:
                r3 = move-exception
                throw r3     // Catch:{ all -> 0x003e }
            L_0x003e:
                r4 = move-exception
                $closeResource(r3, r2)     // Catch:{ IOException -> 0x0043 }
                throw r4     // Catch:{ IOException -> 0x0043 }
            L_0x0043:
                r2 = move-exception
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "Caught an exception reading "
                r3.append(r4)
                r3.append(r0)
                java.lang.String r4 = ": "
                r3.append(r4)
                r3.append(r2)
                java.lang.String r3 = r3.toString()
                java.lang.String r4 = "AdbDebuggingManager"
                android.util.Slog.e(r4, r3)
            L_0x0062:
                if (r1 == 0) goto L_0x0069
                com.android.server.adb.AdbDebuggingManager r2 = com.android.server.adb.AdbDebuggingManager.this
                r2.sendPersistKeyStoreMessage()
            L_0x0069:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.adb.AdbDebuggingManager.AdbKeyStore.addUserKeysToKeyStore():void");
        }

        public void persistKeyStore() {
            filterOutOldKeys();
            if (this.mKeyMap.isEmpty()) {
                deleteKeyStore();
                return;
            }
            if (this.mAtomicKeyFile == null) {
                initKeyFile();
                if (this.mAtomicKeyFile == null) {
                    Slog.e(AdbDebuggingManager.TAG, "Unable to obtain the key file, " + this.mKeyFile + ", for writing");
                    return;
                }
            }
            try {
                XmlSerializer serializer = new FastXmlSerializer();
                FileOutputStream keyStream = this.mAtomicKeyFile.startWrite();
                serializer.setOutput(keyStream, StandardCharsets.UTF_8.name());
                serializer.startDocument((String) null, true);
                for (Map.Entry<String, Long> keyEntry : this.mKeyMap.entrySet()) {
                    serializer.startTag((String) null, XML_TAG_ADB_KEY);
                    serializer.attribute((String) null, XML_ATTRIBUTE_KEY, keyEntry.getKey());
                    serializer.attribute((String) null, XML_ATTRIBUTE_LAST_CONNECTION, String.valueOf(keyEntry.getValue()));
                    serializer.endTag((String) null, XML_TAG_ADB_KEY);
                }
                serializer.endDocument();
                this.mAtomicKeyFile.finishWrite(keyStream);
            } catch (IOException e) {
                Slog.e(AdbDebuggingManager.TAG, "Caught an exception writing the key map: ", e);
                this.mAtomicKeyFile.failWrite((FileOutputStream) null);
            }
        }

        private boolean filterOutOldKeys() {
            boolean keysDeleted = false;
            long allowedTime = getAllowedConnectionTime();
            long systemTime = System.currentTimeMillis();
            Iterator<Map.Entry<String, Long>> keyMapIterator = this.mKeyMap.entrySet().iterator();
            while (keyMapIterator.hasNext()) {
                long connectionTime = keyMapIterator.next().getValue().longValue();
                if (allowedTime != 0 && systemTime > connectionTime + allowedTime) {
                    keyMapIterator.remove();
                    keysDeleted = true;
                }
            }
            if (keysDeleted) {
                AdbDebuggingManager.this.writeKeys(this.mKeyMap.keySet());
            }
            return keysDeleted;
        }

        public long getNextExpirationTime() {
            long minExpiration = -1;
            long allowedTime = getAllowedConnectionTime();
            if (allowedTime == 0) {
                return -1;
            }
            long systemTime = System.currentTimeMillis();
            for (Map.Entry<String, Long> keyEntry : this.mKeyMap.entrySet()) {
                long keyExpiration = Math.max(0, (keyEntry.getValue().longValue() + allowedTime) - systemTime);
                if (minExpiration == -1 || keyExpiration < minExpiration) {
                    minExpiration = keyExpiration;
                }
            }
            return minExpiration;
        }

        public void deleteKeyStore() {
            this.mKeyMap.clear();
            AdbDebuggingManager.this.deleteKeyFile();
            AtomicFile atomicFile = this.mAtomicKeyFile;
            if (atomicFile != null) {
                atomicFile.delete();
            }
        }

        public long getLastConnectionTime(String key) {
            return this.mKeyMap.getOrDefault(key, 0L).longValue();
        }

        public void setLastConnectionTime(String key, long connectionTime) {
            setLastConnectionTime(key, connectionTime, false);
        }

        public void setLastConnectionTime(String key, long connectionTime, boolean force) {
            if ((!this.mKeyMap.containsKey(key) || this.mKeyMap.get(key).longValue() < connectionTime || force) && !this.mSystemKeys.contains(key)) {
                if (!this.mKeyMap.containsKey(key)) {
                    AdbDebuggingManager.this.writeKey(key);
                }
                this.mKeyMap.put(key, Long.valueOf(connectionTime));
            }
        }

        public long getAllowedConnectionTime() {
            return Settings.Global.getLong(AdbDebuggingManager.this.mContext.getContentResolver(), "adb_allowed_connection_time", UnixCalendar.WEEK_IN_MILLIS);
        }

        public boolean isKeyAuthorized(String key) {
            if (this.mSystemKeys.contains(key)) {
                return true;
            }
            long lastConnectionTime = getLastConnectionTime(key);
            if (lastConnectionTime == 0) {
                return false;
            }
            long allowedConnectionTime = getAllowedConnectionTime();
            if (allowedConnectionTime == 0 || System.currentTimeMillis() < lastConnectionTime + allowedConnectionTime) {
                return true;
            }
            return false;
        }
    }
}

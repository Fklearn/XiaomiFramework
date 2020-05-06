package com.android.server.gpu;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.gamedriver.GameDriverProto;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.Base64;
import com.android.framework.protobuf.InvalidProtocolBufferException;
import com.android.internal.annotations.GuardedBy;
import com.android.server.SystemService;
import com.android.server.pm.DumpState;
import com.android.server.pm.Settings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GpuService extends SystemService {
    private static final int BASE64_FLAGS = 3;
    public static final boolean DEBUG = false;
    private static final String GAME_DRIVER_WHITELIST_FILENAME = "whitelist.txt";
    private static final String PROPERTY_GFX_DRIVER = "ro.gfx.driver.0";
    public static final String TAG = "GpuService";
    @GuardedBy({"mLock"})
    private GameDriverProto.Blacklists mBlacklists;
    /* access modifiers changed from: private */
    public ContentResolver mContentResolver;
    /* access modifiers changed from: private */
    public final Context mContext;
    private DeviceConfigListener mDeviceConfigListener;
    /* access modifiers changed from: private */
    public final Object mDeviceConfigLock = new Object();
    /* access modifiers changed from: private */
    public final String mDriverPackageName;
    private long mGameDriverVersionCode;
    private final Object mLock = new Object();
    private final PackageManager mPackageManager;
    private SettingsObserver mSettingsObserver;

    public GpuService(Context context) {
        super(context);
        this.mContext = context;
        this.mDriverPackageName = SystemProperties.get(PROPERTY_GFX_DRIVER);
        this.mGameDriverVersionCode = -1;
        this.mPackageManager = context.getPackageManager();
        String str = this.mDriverPackageName;
        if (str != null && !str.isEmpty()) {
            IntentFilter packageFilter = new IntentFilter();
            packageFilter.addAction("android.intent.action.PACKAGE_ADDED");
            packageFilter.addAction("android.intent.action.PACKAGE_CHANGED");
            packageFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            packageFilter.addDataScheme(Settings.ATTR_PACKAGE);
            getContext().registerReceiverAsUser(new PackageReceiver(), UserHandle.ALL, packageFilter, (String) null, (Handler) null);
        }
    }

    public void onStart() {
    }

    public void onBootPhase(int phase) {
        if (phase == 1000) {
            this.mContentResolver = this.mContext.getContentResolver();
            String str = this.mDriverPackageName;
            if (str != null && !str.isEmpty()) {
                this.mSettingsObserver = new SettingsObserver();
                this.mDeviceConfigListener = new DeviceConfigListener();
                fetchGameDriverPackageProperties();
                processBlacklists();
                setBlacklist();
            }
        }
    }

    private final class SettingsObserver extends ContentObserver {
        private final Uri mGameDriverBlackUri = Settings.Global.getUriFor("game_driver_blacklists");

        SettingsObserver() {
            super(new Handler());
            GpuService.this.mContentResolver.registerContentObserver(this.mGameDriverBlackUri, false, this, -1);
        }

        public void onChange(boolean selfChange, Uri uri) {
            if (uri != null && this.mGameDriverBlackUri.equals(uri)) {
                GpuService.this.processBlacklists();
                GpuService.this.setBlacklist();
            }
        }
    }

    private final class DeviceConfigListener implements DeviceConfig.OnPropertiesChangedListener {
        DeviceConfigListener() {
            DeviceConfig.addOnPropertiesChangedListener("game_driver", GpuService.this.mContext.getMainExecutor(), this);
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            synchronized (GpuService.this.mDeviceConfigLock) {
                if (properties.getKeyset().contains("game_driver_blacklists")) {
                    GpuService.this.parseBlacklists(properties.getString("game_driver_blacklists", ""));
                    GpuService.this.setBlacklist();
                }
            }
        }
    }

    private final class PackageReceiver extends BroadcastReceiver {
        private PackageReceiver() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x003e, code lost:
            if (r4.equals("android.intent.action.PACKAGE_ADDED") == false) goto L_0x0055;
         */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x0058 A[ADDED_TO_REGION] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r11, android.content.Intent r12) {
            /*
                r10 = this;
                android.net.Uri r0 = r12.getData()
                java.lang.String r1 = r0.getSchemeSpecificPart()
                com.android.server.gpu.GpuService r2 = com.android.server.gpu.GpuService.this
                java.lang.String r2 = r2.mDriverPackageName
                boolean r2 = r1.equals(r2)
                if (r2 != 0) goto L_0x0016
                return
            L_0x0016:
                r2 = 0
                java.lang.String r3 = "android.intent.extra.REPLACING"
                boolean r3 = r12.getBooleanExtra(r3, r2)
                java.lang.String r4 = r12.getAction()
                r5 = -1
                int r6 = r4.hashCode()
                r7 = 172491798(0xa480416, float:9.630418E-33)
                r8 = 2
                r9 = 1
                if (r6 == r7) goto L_0x004b
                r7 = 525384130(0x1f50b9c2, float:4.419937E-20)
                if (r6 == r7) goto L_0x0041
                r7 = 1544582882(0x5c1076e2, float:1.62652439E17)
                if (r6 == r7) goto L_0x0038
            L_0x0037:
                goto L_0x0055
            L_0x0038:
                java.lang.String r6 = "android.intent.action.PACKAGE_ADDED"
                boolean r4 = r4.equals(r6)
                if (r4 == 0) goto L_0x0037
                goto L_0x0056
            L_0x0041:
                java.lang.String r2 = "android.intent.action.PACKAGE_REMOVED"
                boolean r2 = r4.equals(r2)
                if (r2 == 0) goto L_0x0037
                r2 = r8
                goto L_0x0056
            L_0x004b:
                java.lang.String r2 = "android.intent.action.PACKAGE_CHANGED"
                boolean r2 = r4.equals(r2)
                if (r2 == 0) goto L_0x0037
                r2 = r9
                goto L_0x0056
            L_0x0055:
                r2 = r5
            L_0x0056:
                if (r2 == 0) goto L_0x005d
                if (r2 == r9) goto L_0x005d
                if (r2 == r8) goto L_0x005d
                goto L_0x0068
            L_0x005d:
                com.android.server.gpu.GpuService r2 = com.android.server.gpu.GpuService.this
                r2.fetchGameDriverPackageProperties()
                com.android.server.gpu.GpuService r2 = com.android.server.gpu.GpuService.this
                r2.setBlacklist()
            L_0x0068:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.gpu.GpuService.PackageReceiver.onReceive(android.content.Context, android.content.Intent):void");
        }
    }

    private static void assetToSettingsGlobal(Context context, Context driverContext, String fileName, String settingsGlobal, CharSequence delimiter) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(driverContext.getAssets().open(fileName)));
            ArrayList<String> assetStrings = new ArrayList<>();
            while (true) {
                String readLine = reader.readLine();
                String assetString = readLine;
                if (readLine != null) {
                    assetStrings.add(assetString);
                } else {
                    Settings.Global.putString(context.getContentResolver(), settingsGlobal, String.join(delimiter, assetStrings));
                    return;
                }
            }
        } catch (IOException e) {
        }
    }

    /* access modifiers changed from: private */
    public void fetchGameDriverPackageProperties() {
        try {
            ApplicationInfo driverInfo = this.mPackageManager.getApplicationInfo(this.mDriverPackageName, DumpState.DUMP_DEXOPT);
            if (driverInfo.targetSdkVersion >= 26) {
                Settings.Global.putString(this.mContentResolver, "game_driver_whitelist", "");
                this.mGameDriverVersionCode = driverInfo.longVersionCode;
                try {
                    assetToSettingsGlobal(this.mContext, this.mContext.createPackageContext(this.mDriverPackageName, 4), GAME_DRIVER_WHITELIST_FILENAME, "game_driver_whitelist", ",");
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
        } catch (PackageManager.NameNotFoundException e2) {
        }
    }

    /* access modifiers changed from: private */
    public void processBlacklists() {
        String base64String = DeviceConfig.getProperty("game_driver", "game_driver_blacklists");
        if (base64String == null) {
            base64String = Settings.Global.getString(this.mContentResolver, "game_driver_blacklists");
        }
        parseBlacklists(base64String != null ? base64String : "");
    }

    /* access modifiers changed from: private */
    public void parseBlacklists(String base64String) {
        synchronized (this.mLock) {
            this.mBlacklists = null;
            try {
                this.mBlacklists = GameDriverProto.Blacklists.parseFrom(Base64.decode(base64String, 3));
            } catch (InvalidProtocolBufferException | IllegalArgumentException e) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void setBlacklist() {
        Settings.Global.putString(this.mContentResolver, "game_driver_blacklist", "");
        synchronized (this.mLock) {
            if (this.mBlacklists != null) {
                for (GameDriverProto.Blacklist blacklist : this.mBlacklists.getBlacklistsList()) {
                    if (blacklist.getVersionCode() == this.mGameDriverVersionCode) {
                        Settings.Global.putString(this.mContentResolver, "game_driver_blacklist", String.join(",", blacklist.getPackageNamesList()));
                        return;
                    }
                }
            }
        }
    }
}

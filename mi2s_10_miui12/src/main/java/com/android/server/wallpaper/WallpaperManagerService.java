package com.android.server.wallpaper;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IWallpaperManager;
import android.app.IWallpaperManagerCallback;
import android.app.UserSwitchObserver;
import android.app.WallpaperColors;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.FileObserver;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.service.wallpaper.IWallpaperConnection;
import android.service.wallpaper.IWallpaperEngine;
import android.service.wallpaper.IWallpaperService;
import android.system.ErrnoException;
import android.system.Os;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.IWindowManager;
import android.view.WindowManager;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.JournaledFile;
import com.android.server.BatteryService;
import com.android.server.EventLogTags;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.pm.Settings;
import com.android.server.wallpaper.WallpaperManagerService;
import com.android.server.wm.WindowManagerInternal;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class WallpaperManagerService extends IWallpaperManager.Stub implements IWallpaperManagerService {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_LIVE = true;
    private static final int MAX_BITMAP_SIZE = 104857600;
    private static final int MAX_WALLPAPER_COMPONENT_LOG_LENGTH = 128;
    private static final long MIN_WALLPAPER_CRASH_TIME = 10000;
    private static final String TAG = "WallpaperManagerService";
    static final String WALLPAPER = "wallpaper_orig";
    static final String WALLPAPER_CROP = "wallpaper";
    static final String WALLPAPER_INFO = "wallpaper_info.xml";
    static final String WALLPAPER_LOCK_CROP = "wallpaper_lock";
    static final String WALLPAPER_LOCK_ORIG = "wallpaper_lock_orig";
    /* access modifiers changed from: private */
    public static final String[] sPerUserFiles = {WALLPAPER, WALLPAPER_CROP, WALLPAPER_LOCK_ORIG, WALLPAPER_LOCK_CROP, WALLPAPER_INFO};
    private final AppOpsManager mAppOpsManager;
    private WallpaperColors mCacheDefaultImageWallpaperColors;
    /* access modifiers changed from: private */
    public final SparseArray<SparseArray<RemoteCallbackList<IWallpaperManagerCallback>>> mColorsChangedListeners;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
    /* access modifiers changed from: private */
    public final ComponentName mDefaultWallpaperComponent;
    private SparseArray<DisplayData> mDisplayDatas = new SparseArray<>();
    private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() {
        public void onDisplayAdded(int displayId) {
        }

        public void onDisplayRemoved(int displayId) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mLastWallpaper != null) {
                    WallpaperData targetWallpaper = null;
                    if (WallpaperManagerService.this.mLastWallpaper.connection.containsDisplay(displayId)) {
                        targetWallpaper = WallpaperManagerService.this.mLastWallpaper;
                    } else if (WallpaperManagerService.this.mFallbackWallpaper.connection.containsDisplay(displayId)) {
                        targetWallpaper = WallpaperManagerService.this.mFallbackWallpaper;
                    }
                    if (targetWallpaper != null) {
                        WallpaperConnection.DisplayConnector connector = targetWallpaper.connection.getDisplayConnectorOrCreate(displayId);
                        if (connector != null) {
                            connector.disconnectLocked();
                            targetWallpaper.connection.removeDisplayConnector(displayId);
                            WallpaperManagerService.this.removeDisplayData(displayId);
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                for (int i = WallpaperManagerService.this.mColorsChangedListeners.size() - 1; i >= 0; i--) {
                    ((SparseArray) WallpaperManagerService.this.mColorsChangedListeners.valueAt(i)).delete(displayId);
                }
            }
        }

        public void onDisplayChanged(int displayId) {
        }
    };
    /* access modifiers changed from: private */
    public final DisplayManager mDisplayManager;
    /* access modifiers changed from: private */
    public WallpaperData mFallbackWallpaper;
    private final IPackageManager mIPackageManager;
    /* access modifiers changed from: private */
    public final IWindowManager mIWindowManager;
    /* access modifiers changed from: private */
    public final ComponentName mImageWallpaper;
    /* access modifiers changed from: private */
    public boolean mInAmbientMode;
    private IWallpaperManagerCallback mKeyguardListener;
    /* access modifiers changed from: private */
    public WallpaperData mLastWallpaper;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    public final SparseArray<WallpaperData> mLockWallpaperMap = new SparseArray<>();
    private final MyPackageMonitor mMonitor;
    /* access modifiers changed from: private */
    public boolean mShuttingDown;
    private final SparseBooleanArray mUserRestorecon = new SparseBooleanArray();
    private boolean mWaitingForUnlock;
    private int mWallpaperId;
    /* access modifiers changed from: private */
    public final SparseArray<WallpaperData> mWallpaperMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public final WindowManagerInternal mWindowManagerInternal;

    public static class Lifecycle extends SystemService {
        private IWallpaperManagerService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        public void onStart() {
            try {
                this.mService = (IWallpaperManagerService) Class.forName(getContext().getResources().getString(17039800)).getConstructor(new Class[]{Context.class}).newInstance(new Object[]{getContext()});
                publishBinderService(WallpaperManagerService.WALLPAPER_CROP, this.mService);
            } catch (Exception exp) {
                Slog.wtf(WallpaperManagerService.TAG, "Failed to instantiate WallpaperManagerService", exp);
            }
        }

        public void onBootPhase(int phase) {
            IWallpaperManagerService iWallpaperManagerService = this.mService;
            if (iWallpaperManagerService != null) {
                iWallpaperManagerService.onBootPhase(phase);
            }
        }

        public void onUnlockUser(int userHandle) {
            IWallpaperManagerService iWallpaperManagerService = this.mService;
            if (iWallpaperManagerService != null) {
                iWallpaperManagerService.onUnlockUser(userHandle);
            }
        }
    }

    private class WallpaperObserver extends FileObserver {
        final int mUserId;
        final WallpaperData mWallpaper;
        final File mWallpaperDir;
        final File mWallpaperFile = new File(this.mWallpaperDir, WallpaperManagerService.WALLPAPER);
        final File mWallpaperLockFile = new File(this.mWallpaperDir, WallpaperManagerService.WALLPAPER_LOCK_ORIG);

        public WallpaperObserver(WallpaperData wallpaper) {
            super(WallpaperManagerService.getWallpaperDir(wallpaper.userId).getAbsolutePath(), 1672);
            this.mUserId = wallpaper.userId;
            this.mWallpaperDir = WallpaperManagerService.getWallpaperDir(wallpaper.userId);
            this.mWallpaper = wallpaper;
        }

        private WallpaperData dataForEvent(boolean sysChanged, boolean lockChanged) {
            WallpaperData wallpaper = null;
            synchronized (WallpaperManagerService.this.mLock) {
                if (lockChanged) {
                    try {
                        wallpaper = (WallpaperData) WallpaperManagerService.this.mLockWallpaperMap.get(this.mUserId);
                    } catch (Throwable th) {
                        while (true) {
                            throw th;
                        }
                    }
                }
                if (wallpaper == null) {
                    wallpaper = (WallpaperData) WallpaperManagerService.this.mWallpaperMap.get(this.mUserId);
                }
            }
            return wallpaper != null ? wallpaper : this.mWallpaper;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:70:0x00de, code lost:
            if (r11 == 0) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:71:0x00e0, code lost:
            com.android.server.wallpaper.WallpaperManagerService.access$500(r1.this$0, r4, r11);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:78:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:79:?, code lost:
            return;
         */
        /* JADX WARNING: Removed duplicated region for block: B:64:0x00d4 A[SYNTHETIC, Splitter:B:64:0x00d4] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onEvent(int r21, java.lang.String r22) {
            /*
                r20 = this;
                r1 = r20
                r2 = r21
                r3 = r22
                if (r3 != 0) goto L_0x0009
                return
            L_0x0009:
                r0 = 128(0x80, float:1.794E-43)
                r4 = 0
                r5 = 1
                if (r2 != r0) goto L_0x0011
                r0 = r5
                goto L_0x0012
            L_0x0011:
                r0 = r4
            L_0x0012:
                r6 = r0
                r0 = 8
                if (r2 == r0) goto L_0x001c
                if (r6 == 0) goto L_0x001a
                goto L_0x001c
            L_0x001a:
                r7 = r4
                goto L_0x001d
            L_0x001c:
                r7 = r5
            L_0x001d:
                java.io.File r8 = new java.io.File
                java.io.File r9 = r1.mWallpaperDir
                r8.<init>(r9, r3)
                java.io.File r9 = r1.mWallpaperFile
                boolean r9 = r9.equals(r8)
                java.io.File r10 = r1.mWallpaperLockFile
                boolean r10 = r10.equals(r8)
                r11 = 0
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r15 = r1.dataForEvent(r9, r10)
                r14 = 2
                if (r6 == 0) goto L_0x0048
                if (r10 == 0) goto L_0x0048
                android.os.SELinux.restorecon(r8)
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this
                r0.notifyLockWallpaperChanged()
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this
                r0.notifyWallpaperColorsChanged(r15, r14)
                return
            L_0x0048:
                com.android.server.wallpaper.WallpaperManagerService r12 = com.android.server.wallpaper.WallpaperManagerService.this
                java.lang.Object r18 = r12.mLock
                monitor-enter(r18)
                if (r9 != 0) goto L_0x0057
                if (r10 == 0) goto L_0x0054
                goto L_0x0057
            L_0x0054:
                r4 = r15
                goto L_0x00dd
            L_0x0057:
                com.android.server.wallpaper.WallpaperManagerService r12 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x00e8 }
                r12.notifyCallbacksLocked(r15)     // Catch:{ all -> 0x00e8 }
                android.content.ComponentName r12 = r15.wallpaperComponent     // Catch:{ all -> 0x00e8 }
                if (r12 == 0) goto L_0x006e
                if (r2 != r0) goto L_0x006e
                boolean r0 = r15.imageWallpaperPending     // Catch:{ all -> 0x006a }
                if (r0 == 0) goto L_0x0067
                goto L_0x006e
            L_0x0067:
                r4 = r15
                goto L_0x00dd
            L_0x006a:
                r0 = move-exception
                r4 = r15
                goto L_0x00ea
            L_0x006e:
                if (r7 == 0) goto L_0x00dc
                android.os.SELinux.restorecon(r8)     // Catch:{ all -> 0x00e8 }
                if (r6 == 0) goto L_0x007c
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x006a }
                int r12 = r15.userId     // Catch:{ all -> 0x006a }
                r0.loadSettingsLocked(r12, r5)     // Catch:{ all -> 0x006a }
            L_0x007c:
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x00e8 }
                r0.generateCrop(r15)     // Catch:{ all -> 0x00e8 }
                r15.imageWallpaperPending = r4     // Catch:{ all -> 0x00e8 }
                if (r9 == 0) goto L_0x00a5
                com.android.server.wallpaper.WallpaperManagerService r12 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x00a2 }
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x00a2 }
                android.content.ComponentName r13 = r0.mImageWallpaper     // Catch:{ all -> 0x00a2 }
                r0 = 1
                r4 = 0
                r17 = 0
                r5 = r14
                r14 = r0
                r19 = r15
                r15 = r4
                r16 = r19
                boolean unused = r12.bindWallpaperComponentLocked(r13, r14, r15, r16, r17)     // Catch:{ all -> 0x009e }
                r11 = r11 | 1
                goto L_0x00a8
            L_0x009e:
                r0 = move-exception
                r4 = r19
                goto L_0x00ea
            L_0x00a2:
                r0 = move-exception
                r4 = r15
                goto L_0x00ea
            L_0x00a5:
                r5 = r14
                r19 = r15
            L_0x00a8:
                if (r10 != 0) goto L_0x00b2
                r4 = r19
                int r0 = r4.whichPending     // Catch:{ all -> 0x00e6 }
                r0 = r0 & r5
                if (r0 == 0) goto L_0x00c9
                goto L_0x00b4
            L_0x00b2:
                r4 = r19
            L_0x00b4:
                if (r10 != 0) goto L_0x00c1
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x00e6 }
                android.util.SparseArray r0 = r0.mLockWallpaperMap     // Catch:{ all -> 0x00e6 }
                int r5 = r4.userId     // Catch:{ all -> 0x00e6 }
                r0.remove(r5)     // Catch:{ all -> 0x00e6 }
            L_0x00c1:
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x00e6 }
                r0.notifyLockWallpaperChanged()     // Catch:{ all -> 0x00e6 }
                r0 = r11 | 2
                r11 = r0
            L_0x00c9:
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x00e6 }
                int r5 = r4.userId     // Catch:{ all -> 0x00e6 }
                r0.saveSettingsLocked(r5)     // Catch:{ all -> 0x00e6 }
                android.app.IWallpaperManagerCallback r0 = r4.setComplete     // Catch:{ all -> 0x00e6 }
                if (r0 == 0) goto L_0x00dd
                android.app.IWallpaperManagerCallback r0 = r4.setComplete     // Catch:{ RemoteException -> 0x00da }
                r0.onWallpaperChanged()     // Catch:{ RemoteException -> 0x00da }
                goto L_0x00dd
            L_0x00da:
                r0 = move-exception
                goto L_0x00dd
            L_0x00dc:
                r4 = r15
            L_0x00dd:
                monitor-exit(r18)     // Catch:{ all -> 0x00e6 }
                if (r11 == 0) goto L_0x00e5
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this
                r0.notifyWallpaperColorsChanged(r4, r11)
            L_0x00e5:
                return
            L_0x00e6:
                r0 = move-exception
                goto L_0x00ea
            L_0x00e8:
                r0 = move-exception
                r4 = r15
            L_0x00ea:
                monitor-exit(r18)     // Catch:{ all -> 0x00e6 }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.WallpaperObserver.onEvent(int, java.lang.String):void");
        }
    }

    /* access modifiers changed from: private */
    public void notifyLockWallpaperChanged() {
        IWallpaperManagerCallback cb = this.mKeyguardListener;
        if (cb != null) {
            try {
                cb.onWallpaperChanged();
            } catch (RemoteException e) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyWallpaperColorsChanged(WallpaperData wallpaper, int which) {
        if (wallpaper.connection != null) {
            wallpaper.connection.forEachDisplayConnector(new Consumer(wallpaper, which) {
                private final /* synthetic */ WallpaperManagerService.WallpaperData f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    WallpaperManagerService.this.lambda$notifyWallpaperColorsChanged$0$WallpaperManagerService(this.f$1, this.f$2, (WallpaperManagerService.WallpaperConnection.DisplayConnector) obj);
                }
            });
        } else {
            notifyWallpaperColorsChangedOnDisplay(wallpaper, which, 0);
        }
    }

    public /* synthetic */ void lambda$notifyWallpaperColorsChanged$0$WallpaperManagerService(WallpaperData wallpaper, int which, WallpaperConnection.DisplayConnector connector) {
        notifyWallpaperColorsChangedOnDisplay(wallpaper, which, connector.mDisplayId);
    }

    private RemoteCallbackList<IWallpaperManagerCallback> getWallpaperCallbacks(int userId, int displayId) {
        SparseArray<RemoteCallbackList<IWallpaperManagerCallback>> displayListeners = this.mColorsChangedListeners.get(userId);
        if (displayListeners != null) {
            return displayListeners.get(displayId);
        }
        return null;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0025, code lost:
        notifyColorListeners(r5.primaryColors, r6, r5.userId, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002c, code lost:
        if (r1 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002e, code lost:
        extractColors(r5);
        r0 = r4.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0033, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0036, code lost:
        if (r5.primaryColors != null) goto L_0x003a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0038, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0039, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003a, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x003b, code lost:
        notifyColorListeners(r5.primaryColors, r6, r5.userId, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyWallpaperColorsChangedOnDisplay(com.android.server.wallpaper.WallpaperManagerService.WallpaperData r5, int r6, int r7) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            int r1 = r5.userId     // Catch:{ all -> 0x0047 }
            android.os.RemoteCallbackList r1 = r4.getWallpaperCallbacks(r1, r7)     // Catch:{ all -> 0x0047 }
            r2 = -1
            android.os.RemoteCallbackList r2 = r4.getWallpaperCallbacks(r2, r7)     // Catch:{ all -> 0x0047 }
            boolean r3 = emptyCallbackList(r1)     // Catch:{ all -> 0x0047 }
            if (r3 == 0) goto L_0x001c
            boolean r3 = emptyCallbackList(r2)     // Catch:{ all -> 0x0047 }
            if (r3 == 0) goto L_0x001c
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            return
        L_0x001c:
            android.app.WallpaperColors r3 = r5.primaryColors     // Catch:{ all -> 0x0047 }
            if (r3 != 0) goto L_0x0022
            r3 = 1
            goto L_0x0023
        L_0x0022:
            r3 = 0
        L_0x0023:
            r1 = r3
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            android.app.WallpaperColors r0 = r5.primaryColors
            int r2 = r5.userId
            r4.notifyColorListeners(r0, r6, r2, r7)
            if (r1 == 0) goto L_0x0046
            r4.extractColors(r5)
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            android.app.WallpaperColors r2 = r5.primaryColors     // Catch:{ all -> 0x0043 }
            if (r2 != 0) goto L_0x003a
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            return
        L_0x003a:
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            android.app.WallpaperColors r0 = r5.primaryColors
            int r2 = r5.userId
            r4.notifyColorListeners(r0, r6, r2, r7)
            goto L_0x0046
        L_0x0043:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            throw r2
        L_0x0046:
            return
        L_0x0047:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.notifyWallpaperColorsChangedOnDisplay(com.android.server.wallpaper.WallpaperManagerService$WallpaperData, int, int):void");
    }

    private static <T extends IInterface> boolean emptyCallbackList(RemoteCallbackList<T> list) {
        return list == null || list.getRegisteredCallbackCount() == 0;
    }

    private void notifyColorListeners(WallpaperColors wallpaperColors, int which, int userId, int displayId) {
        IWallpaperManagerCallback keyguardListener;
        ArrayList<IWallpaperManagerCallback> colorListeners = new ArrayList<>();
        synchronized (this.mLock) {
            RemoteCallbackList<IWallpaperManagerCallback> currentUserColorListeners = getWallpaperCallbacks(userId, displayId);
            RemoteCallbackList<IWallpaperManagerCallback> userAllColorListeners = getWallpaperCallbacks(-1, displayId);
            keyguardListener = this.mKeyguardListener;
            if (currentUserColorListeners != null) {
                int count = currentUserColorListeners.beginBroadcast();
                for (int i = 0; i < count; i++) {
                    colorListeners.add(currentUserColorListeners.getBroadcastItem(i));
                }
                currentUserColorListeners.finishBroadcast();
            }
            if (userAllColorListeners != null) {
                int count2 = userAllColorListeners.beginBroadcast();
                for (int i2 = 0; i2 < count2; i2++) {
                    colorListeners.add(userAllColorListeners.getBroadcastItem(i2));
                }
                userAllColorListeners.finishBroadcast();
            }
        }
        int count3 = colorListeners.size();
        for (int i3 = 0; i3 < count3; i3++) {
            try {
                colorListeners.get(i3).onWallpaperColorsChanged(wallpaperColors, which, userId);
            } catch (RemoteException e) {
            }
        }
        if (keyguardListener != null && displayId == 0) {
            try {
                keyguardListener.onWallpaperColorsChanged(wallpaperColors, which, userId);
            } catch (RemoteException e2) {
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0054 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x008b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void extractColors(com.android.server.wallpaper.WallpaperManagerService.WallpaperData r8) {
        /*
            r7 = this;
            r0 = 0
            r1 = 0
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r2 = r7.mFallbackWallpaper
            boolean r2 = r8.equals(r2)
            if (r2 == 0) goto L_0x0029
            java.lang.Object r2 = r7.mLock
            monitor-enter(r2)
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r3 = r7.mFallbackWallpaper     // Catch:{ all -> 0x0026 }
            android.app.WallpaperColors r3 = r3.primaryColors     // Catch:{ all -> 0x0026 }
            if (r3 == 0) goto L_0x0015
            monitor-exit(r2)     // Catch:{ all -> 0x0026 }
            return
        L_0x0015:
            monitor-exit(r2)     // Catch:{ all -> 0x0026 }
            android.app.WallpaperColors r3 = r7.extractDefaultImageWallpaperColors()
            java.lang.Object r4 = r7.mLock
            monitor-enter(r4)
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r2 = r7.mFallbackWallpaper     // Catch:{ all -> 0x0023 }
            r2.primaryColors = r3     // Catch:{ all -> 0x0023 }
            monitor-exit(r4)     // Catch:{ all -> 0x0023 }
            return
        L_0x0023:
            r2 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0023 }
            throw r2
        L_0x0026:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0026 }
            throw r3
        L_0x0029:
            java.lang.Object r2 = r7.mLock
            monitor-enter(r2)
            android.content.ComponentName r3 = r7.mImageWallpaper     // Catch:{ all -> 0x00a6 }
            android.content.ComponentName r4 = r8.wallpaperComponent     // Catch:{ all -> 0x00a6 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x00a6 }
            if (r3 != 0) goto L_0x003d
            android.content.ComponentName r3 = r8.wallpaperComponent     // Catch:{ all -> 0x00a6 }
            if (r3 != 0) goto L_0x003b
            goto L_0x003d
        L_0x003b:
            r3 = 0
            goto L_0x003e
        L_0x003d:
            r3 = 1
        L_0x003e:
            if (r3 == 0) goto L_0x0054
            java.io.File r4 = r8.cropFile     // Catch:{ all -> 0x00a6 }
            if (r4 == 0) goto L_0x0054
            java.io.File r4 = r8.cropFile     // Catch:{ all -> 0x00a6 }
            boolean r4 = r4.exists()     // Catch:{ all -> 0x00a6 }
            if (r4 == 0) goto L_0x0054
            java.io.File r4 = r8.cropFile     // Catch:{ all -> 0x00a6 }
            java.lang.String r4 = r4.getAbsolutePath()     // Catch:{ all -> 0x00a6 }
            r0 = r4
            goto L_0x0063
        L_0x0054:
            if (r3 == 0) goto L_0x0063
            boolean r4 = r8.cropExists()     // Catch:{ all -> 0x00a6 }
            if (r4 != 0) goto L_0x0063
            boolean r4 = r8.sourceExists()     // Catch:{ all -> 0x00a6 }
            if (r4 != 0) goto L_0x0063
            r1 = 1
        L_0x0063:
            int r4 = r8.wallpaperId     // Catch:{ all -> 0x00a6 }
            r3 = r4
            monitor-exit(r2)     // Catch:{ all -> 0x00a6 }
            r2 = 0
            if (r0 == 0) goto L_0x0078
            android.graphics.Bitmap r4 = android.graphics.BitmapFactory.decodeFile(r0)
            if (r4 == 0) goto L_0x0080
            android.app.WallpaperColors r2 = android.app.WallpaperColors.fromBitmap(r4)
            r4.recycle()
            goto L_0x0080
        L_0x0078:
            if (r1 == 0) goto L_0x0080
            android.app.WallpaperColors r2 = r7.extractDefaultImageWallpaperColors()
            r4 = r2
            goto L_0x0081
        L_0x0080:
            r4 = r2
        L_0x0081:
            if (r4 != 0) goto L_0x008b
            java.lang.String r2 = "WallpaperManagerService"
            java.lang.String r5 = "Cannot extract colors because wallpaper could not be read."
            android.util.Slog.w(r2, r5)
            return
        L_0x008b:
            java.lang.Object r5 = r7.mLock
            monitor-enter(r5)
            int r2 = r8.wallpaperId     // Catch:{ all -> 0x00a3 }
            if (r2 != r3) goto L_0x009a
            r8.primaryColors = r4     // Catch:{ all -> 0x00a3 }
            int r2 = r8.userId     // Catch:{ all -> 0x00a3 }
            r7.saveSettingsLocked(r2)     // Catch:{ all -> 0x00a3 }
            goto L_0x00a1
        L_0x009a:
            java.lang.String r2 = "WallpaperManagerService"
            java.lang.String r6 = "Not setting primary colors since wallpaper changed"
            android.util.Slog.w(r2, r6)     // Catch:{ all -> 0x00a3 }
        L_0x00a1:
            monitor-exit(r5)     // Catch:{ all -> 0x00a3 }
            return
        L_0x00a3:
            r2 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x00a3 }
            throw r2
        L_0x00a6:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00a6 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.extractColors(com.android.server.wallpaper.WallpaperManagerService$WallpaperData):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r1 = android.app.WallpaperManager.openDefaultWallpaper(r5.mContext, 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0015, code lost:
        if (r1 != null) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        android.util.Slog.w(TAG, "Can't open default wallpaper stream");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001f, code lost:
        if (r1 == null) goto L_0x0024;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0024, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r2 = android.graphics.BitmapFactory.decodeStream(r1, (android.graphics.Rect) null, new android.graphics.BitmapFactory.Options());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x002e, code lost:
        if (r2 == null) goto L_0x0038;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0030, code lost:
        r0 = android.app.WallpaperColors.fromBitmap(r2);
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x003e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x003f, code lost:
        if (r1 != null) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0045, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        r2.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0049, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x004a, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x004b, code lost:
        android.util.Slog.w(TAG, "Can't close default wallpaper stream", r1);
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0054, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0055, code lost:
        android.util.Slog.w(TAG, "Can't decode default wallpaper stream", r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x005f, code lost:
        android.util.Slog.e(TAG, "Extract default image wallpaper colors failed");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0069, code lost:
        monitor-enter(r5.mLock);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:?, code lost:
        r5.mCacheDefaultImageWallpaperColors = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x000c, code lost:
        r0 = null;
     */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0067  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.app.WallpaperColors extractDefaultImageWallpaperColors() {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            android.app.WallpaperColors r1 = r5.mCacheDefaultImageWallpaperColors     // Catch:{ all -> 0x0071 }
            if (r1 == 0) goto L_0x000b
            android.app.WallpaperColors r1 = r5.mCacheDefaultImageWallpaperColors     // Catch:{ all -> 0x0071 }
            monitor-exit(r0)     // Catch:{ all -> 0x0071 }
            return r1
        L_0x000b:
            monitor-exit(r0)     // Catch:{ all -> 0x0071 }
            r0 = 0
            android.content.Context r1 = r5.mContext     // Catch:{ OutOfMemoryError -> 0x0054, IOException -> 0x004a }
            r2 = 1
            java.io.InputStream r1 = android.app.WallpaperManager.openDefaultWallpaper(r1, r2)     // Catch:{ OutOfMemoryError -> 0x0054, IOException -> 0x004a }
            r2 = 0
            if (r1 != 0) goto L_0x0025
            java.lang.String r3 = "WallpaperManagerService"
            java.lang.String r4 = "Can't open default wallpaper stream"
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x003c }
            if (r1 == 0) goto L_0x0024
            r1.close()     // Catch:{ OutOfMemoryError -> 0x0054, IOException -> 0x004a }
        L_0x0024:
            return r2
        L_0x0025:
            android.graphics.BitmapFactory$Options r3 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x003c }
            r3.<init>()     // Catch:{ all -> 0x003c }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeStream(r1, r2, r3)     // Catch:{ all -> 0x003c }
            if (r2 == 0) goto L_0x0038
            android.app.WallpaperColors r4 = android.app.WallpaperColors.fromBitmap(r2)     // Catch:{ all -> 0x003c }
            r0 = r4
            r2.recycle()     // Catch:{ all -> 0x003c }
        L_0x0038:
            r1.close()     // Catch:{ OutOfMemoryError -> 0x0054, IOException -> 0x004a }
            goto L_0x005c
        L_0x003c:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x003e }
        L_0x003e:
            r3 = move-exception
            if (r1 == 0) goto L_0x0049
            r1.close()     // Catch:{ all -> 0x0045 }
            goto L_0x0049
        L_0x0045:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ OutOfMemoryError -> 0x0054, IOException -> 0x004a }
        L_0x0049:
            throw r3     // Catch:{ OutOfMemoryError -> 0x0054, IOException -> 0x004a }
        L_0x004a:
            r1 = move-exception
            java.lang.String r2 = "WallpaperManagerService"
            java.lang.String r3 = "Can't close default wallpaper stream"
            android.util.Slog.w(r2, r3, r1)
            r1 = r0
            goto L_0x005d
        L_0x0054:
            r1 = move-exception
            java.lang.String r2 = "WallpaperManagerService"
            java.lang.String r3 = "Can't decode default wallpaper stream"
            android.util.Slog.w(r2, r3, r1)
        L_0x005c:
            r1 = r0
        L_0x005d:
            if (r1 != 0) goto L_0x0067
            java.lang.String r0 = "WallpaperManagerService"
            java.lang.String r2 = "Extract default image wallpaper colors failed"
            android.util.Slog.e(r0, r2)
            goto L_0x006d
        L_0x0067:
            java.lang.Object r2 = r5.mLock
            monitor-enter(r2)
            r5.mCacheDefaultImageWallpaperColors = r1     // Catch:{ all -> 0x006e }
            monitor-exit(r2)     // Catch:{ all -> 0x006e }
        L_0x006d:
            return r1
        L_0x006e:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x006e }
            throw r0
        L_0x0071:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0071 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.extractDefaultImageWallpaperColors():android.app.WallpaperColors");
    }

    /* Debug info: failed to restart local var, previous not found, register: 24 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0244  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0256  */
    /* JADX WARNING: Removed duplicated region for block: B:106:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateCrop(com.android.server.wallpaper.WallpaperManagerService.WallpaperData r25) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
            r3 = 0
            r0 = 0
            com.android.server.wallpaper.WallpaperManagerService$DisplayData r4 = r1.getDisplayDataOrCreate(r0)
            android.graphics.Rect r5 = new android.graphics.Rect
            android.graphics.Rect r6 = r2.cropHint
            r5.<init>(r6)
            android.view.DisplayInfo r6 = new android.view.DisplayInfo
            r6.<init>()
            android.hardware.display.DisplayManager r7 = r1.mDisplayManager
            android.view.Display r7 = r7.getDisplay(r0)
            r7.getDisplayInfo(r6)
            android.graphics.BitmapFactory$Options r7 = new android.graphics.BitmapFactory$Options
            r7.<init>()
            r8 = 1
            r7.inJustDecodeBounds = r8
            java.io.File r9 = r2.wallpaperFile
            java.lang.String r9 = r9.getAbsolutePath()
            android.graphics.BitmapFactory.decodeFile(r9, r7)
            int r9 = r7.outWidth
            java.lang.String r10 = "WallpaperManagerService"
            if (r9 <= 0) goto L_0x0236
            int r9 = r7.outHeight
            if (r9 > 0) goto L_0x0042
            r18 = r3
            r22 = r4
            r20 = r6
            goto L_0x023c
        L_0x0042:
            r9 = 0
            r11 = 0
            boolean r12 = r5.isEmpty()
            if (r12 == 0) goto L_0x0057
            r5.top = r0
            r5.left = r0
            int r12 = r7.outWidth
            r5.right = r12
            int r12 = r7.outHeight
            r5.bottom = r12
            goto L_0x0096
        L_0x0057:
            int r12 = r5.right
            int r13 = r7.outWidth
            if (r12 <= r13) goto L_0x0064
            int r12 = r7.outWidth
            int r13 = r5.right
            int r12 = r12 - r13
            goto L_0x0065
        L_0x0064:
            r12 = r0
        L_0x0065:
            int r13 = r5.bottom
            int r14 = r7.outHeight
            if (r13 <= r14) goto L_0x0071
            int r13 = r7.outHeight
            int r14 = r5.bottom
            int r13 = r13 - r14
            goto L_0x0072
        L_0x0071:
            r13 = r0
        L_0x0072:
            r5.offset(r12, r13)
            int r12 = r5.left
            if (r12 >= 0) goto L_0x007b
            r5.left = r0
        L_0x007b:
            int r12 = r5.top
            if (r12 >= 0) goto L_0x0081
            r5.top = r0
        L_0x0081:
            int r12 = r7.outHeight
            int r13 = r5.height()
            if (r12 > r13) goto L_0x0094
            int r12 = r7.outWidth
            int r13 = r5.width()
            if (r12 <= r13) goto L_0x0092
            goto L_0x0094
        L_0x0092:
            r12 = r0
            goto L_0x0095
        L_0x0094:
            r12 = r8
        L_0x0095:
            r9 = r12
        L_0x0096:
            int r12 = r4.mHeight
            int r13 = r5.height()
            if (r12 != r13) goto L_0x00b5
            int r12 = r5.height()
            int r13 = com.android.server.wallpaper.GLHelper.getMaxTextureSize()
            if (r12 > r13) goto L_0x00b5
            int r12 = r5.width()
            int r13 = com.android.server.wallpaper.GLHelper.getMaxTextureSize()
            if (r12 <= r13) goto L_0x00b3
            goto L_0x00b5
        L_0x00b3:
            r12 = r0
            goto L_0x00b6
        L_0x00b5:
            r12 = r8
        L_0x00b6:
            r11 = r12
            if (r11 == 0) goto L_0x00de
            int r12 = r4.mHeight
            float r12 = (float) r12
            int r13 = r5.height()
            float r13 = (float) r13
            float r12 = r12 / r13
            int r13 = r5.width()
            float r13 = (float) r13
            float r13 = r13 * r12
            int r13 = (int) r13
            int r14 = r6.logicalWidth
            if (r13 >= r14) goto L_0x00de
            int r14 = r6.logicalHeight
            float r14 = (float) r14
            int r15 = r6.logicalWidth
            float r15 = (float) r15
            float r14 = r14 / r15
            int r15 = r5.width()
            float r15 = (float) r15
            float r15 = r15 * r14
            int r15 = (int) r15
            r5.bottom = r15
            r9 = 1
        L_0x00de:
            if (r9 != 0) goto L_0x0106
            if (r11 != 0) goto L_0x0106
            int r0 = r7.outWidth
            int r8 = r7.outHeight
            int r0 = r0 * r8
            int r0 = r0 * 4
            long r12 = (long) r0
            r14 = 104857600(0x6400000, double:5.1806538E-316)
            int r0 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r0 >= 0) goto L_0x00f9
            java.io.File r0 = r2.wallpaperFile
            java.io.File r8 = r2.cropFile
            boolean r3 = android.os.FileUtils.copyFile(r0, r8)
        L_0x00f9:
            if (r3 != 0) goto L_0x0100
            java.io.File r0 = r2.cropFile
            r0.delete()
        L_0x0100:
            r22 = r4
            r20 = r6
            goto L_0x0242
        L_0x0106:
            r12 = 0
            r13 = 0
            java.io.File r14 = r2.wallpaperFile     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            java.lang.String r14 = r14.getAbsolutePath()     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            android.graphics.BitmapRegionDecoder r14 = android.graphics.BitmapRegionDecoder.newInstance(r14, r0)     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            int r15 = r5.height()     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            int r8 = r4.mHeight     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            int r15 = r15 / r8
            r8 = r15
            r15 = 1
        L_0x011b:
            int r0 = r15 * 2
            if (r0 > r8) goto L_0x0123
            int r15 = r15 * 2
            r0 = 0
            goto L_0x011b
        L_0x0123:
            r7.inSampleSize = r15     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            r0 = 0
            r7.inJustDecodeBounds = r0     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            android.graphics.Rect r0 = new android.graphics.Rect     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            r0.<init>(r5)     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            int r1 = r7.inSampleSize     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            float r1 = (float) r1     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            r17 = 1065353216(0x3f800000, float:1.0)
            float r1 = r17 / r1
            r0.scale(r1)     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            int r1 = r4.mHeight     // Catch:{ Exception -> 0x0226, all -> 0x0218 }
            float r1 = (float) r1
            r18 = r3
            int r3 = r0.height()     // Catch:{ Exception -> 0x0212, all -> 0x020c }
            float r3 = (float) r3     // Catch:{ Exception -> 0x0212, all -> 0x020c }
            float r1 = r1 / r3
            int r3 = r0.height()     // Catch:{ Exception -> 0x0212, all -> 0x020c }
            float r3 = (float) r3     // Catch:{ Exception -> 0x0212, all -> 0x020c }
            float r3 = r3 * r1
            int r3 = (int) r3     // Catch:{ Exception -> 0x0212, all -> 0x020c }
            r19 = r3
            int r3 = r0.width()     // Catch:{ Exception -> 0x0212, all -> 0x020c }
            float r3 = (float) r3
            float r3 = r3 * r1
            int r3 = (int) r3
            r20 = r6
            int r6 = com.android.server.wallpaper.GLHelper.getMaxTextureSize()     // Catch:{ Exception -> 0x0208, all -> 0x0204 }
            if (r3 <= r6) goto L_0x019d
            int r6 = r4.mHeight     // Catch:{ Exception -> 0x0208, all -> 0x0204 }
            float r6 = (float) r6     // Catch:{ Exception -> 0x0208, all -> 0x0204 }
            float r6 = r6 / r1
            int r6 = (int) r6     // Catch:{ Exception -> 0x0208, all -> 0x0204 }
            r21 = r3
            int r3 = r4.mWidth     // Catch:{ Exception -> 0x0208, all -> 0x0204 }
            float r3 = (float) r3     // Catch:{ Exception -> 0x0208, all -> 0x0204 }
            float r3 = r3 / r1
            int r3 = (int) r3     // Catch:{ Exception -> 0x0208, all -> 0x0204 }
            r0.set(r5)     // Catch:{ Exception -> 0x0208, all -> 0x0204 }
            r22 = r4
            int r4 = r0.left     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            int r23 = r5.width()     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            int r23 = r23 - r3
            int r23 = r23 / 2
            int r4 = r4 + r23
            r0.left = r4     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            int r4 = r0.top     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            int r23 = r5.height()     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            int r23 = r23 - r6
            int r23 = r23 / 2
            int r4 = r4 + r23
            r0.top = r4     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            int r4 = r0.left     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            int r4 = r4 + r3
            r0.right = r4     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            int r4 = r0.top     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            int r4 = r4 + r6
            r0.bottom = r4     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r5.set(r0)     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            int r4 = r7.inSampleSize     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            float r4 = (float) r4     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            float r4 = r17 / r4
            r0.scale(r4)     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            goto L_0x01a1
        L_0x019d:
            r21 = r3
            r22 = r4
        L_0x01a1:
            int r3 = r0.height()     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            float r3 = (float) r3     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            float r3 = r3 * r1
            int r3 = (int) r3     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            int r4 = r0.width()     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            float r4 = (float) r4     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            float r4 = r4 * r1
            int r4 = (int) r4     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            android.graphics.Bitmap r6 = r14.decodeRegion(r5, r7)     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r14.recycle()     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            if (r6 != 0) goto L_0x01c2
            r17 = r0
            java.lang.String r0 = "Could not decode new wallpaper"
            android.util.Slog.e(r10, r0)     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r3 = r18
            goto L_0x01f1
        L_0x01c2:
            r17 = r0
            r0 = 1
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r6, r4, r3, r0)     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r16 = r1
            int r1 = r0.getByteCount()     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r23 = r3
            r3 = 104857600(0x6400000, float:3.6111186E-35)
            if (r1 > r3) goto L_0x01f8
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            java.io.File r3 = r2.cropFile     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r12 = r1
            java.io.BufferedOutputStream r1 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r3 = 32768(0x8000, float:4.5918E-41)
            r1.<init>(r12, r3)     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r13 = r1
            android.graphics.Bitmap$CompressFormat r1 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r3 = 100
            r0.compress(r1, r3, r13)     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r13.flush()     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            r3 = 1
        L_0x01f1:
            libcore.io.IoUtils.closeQuietly(r13)
            libcore.io.IoUtils.closeQuietly(r12)
            goto L_0x0242
        L_0x01f8:
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            java.lang.String r3 = "Too large bitmap, limit=104857600"
            r1.<init>(r3)     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
            throw r1     // Catch:{ Exception -> 0x0202, all -> 0x0200 }
        L_0x0200:
            r0 = move-exception
            goto L_0x021f
        L_0x0202:
            r0 = move-exception
            goto L_0x022d
        L_0x0204:
            r0 = move-exception
            r22 = r4
            goto L_0x021f
        L_0x0208:
            r0 = move-exception
            r22 = r4
            goto L_0x022d
        L_0x020c:
            r0 = move-exception
            r22 = r4
            r20 = r6
            goto L_0x021f
        L_0x0212:
            r0 = move-exception
            r22 = r4
            r20 = r6
            goto L_0x022d
        L_0x0218:
            r0 = move-exception
            r18 = r3
            r22 = r4
            r20 = r6
        L_0x021f:
            libcore.io.IoUtils.closeQuietly(r13)
            libcore.io.IoUtils.closeQuietly(r12)
            throw r0
        L_0x0226:
            r0 = move-exception
            r18 = r3
            r22 = r4
            r20 = r6
        L_0x022d:
            libcore.io.IoUtils.closeQuietly(r13)
            libcore.io.IoUtils.closeQuietly(r12)
            r3 = r18
            goto L_0x0242
        L_0x0236:
            r18 = r3
            r22 = r4
            r20 = r6
        L_0x023c:
            java.lang.String r0 = "Invalid wallpaper data"
            android.util.Slog.w(r10, r0)
            r3 = 0
        L_0x0242:
            if (r3 != 0) goto L_0x024e
            java.lang.String r0 = "Unable to apply new wallpaper"
            android.util.Slog.e(r10, r0)
            java.io.File r0 = r2.cropFile
            r0.delete()
        L_0x024e:
            java.io.File r0 = r2.cropFile
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x025f
            java.io.File r0 = r2.cropFile
            java.io.File r0 = r0.getAbsoluteFile()
            android.os.SELinux.restorecon(r0)
        L_0x025f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.generateCrop(com.android.server.wallpaper.WallpaperManagerService$WallpaperData):void");
    }

    static class WallpaperData {
        boolean allowBackup;
        /* access modifiers changed from: private */
        public RemoteCallbackList<IWallpaperManagerCallback> callbacks = new RemoteCallbackList<>();
        WallpaperConnection connection;
        final File cropFile;
        final Rect cropHint = new Rect(0, 0, 0, 0);
        boolean imageWallpaperPending;
        long lastDiedTime;
        String name = "";
        ComponentName nextWallpaperComponent;
        WallpaperColors primaryColors;
        IWallpaperManagerCallback setComplete;
        int userId;
        ComponentName wallpaperComponent;
        final File wallpaperFile;
        int wallpaperId;
        WallpaperObserver wallpaperObserver;
        boolean wallpaperUpdating;
        int whichPending;

        WallpaperData(int userId2, String inputFileName, String cropFileName) {
            this.userId = userId2;
            File wallpaperDir = WallpaperManagerService.getWallpaperDir(userId2);
            this.wallpaperFile = new File(wallpaperDir, inputFileName);
            this.cropFile = new File(wallpaperDir, cropFileName);
        }

        /* access modifiers changed from: package-private */
        public boolean cropExists() {
            return this.cropFile.exists();
        }

        /* access modifiers changed from: package-private */
        public boolean sourceExists() {
            return this.wallpaperFile.exists();
        }
    }

    private static final class DisplayData {
        final int mDisplayId;
        int mHeight = -1;
        final Rect mPadding = new Rect(0, 0, 0, 0);
        int mWidth = -1;

        DisplayData(int displayId) {
            this.mDisplayId = displayId;
        }
    }

    /* access modifiers changed from: private */
    public void removeDisplayData(int displayId) {
        this.mDisplayDatas.remove(displayId);
    }

    /* access modifiers changed from: private */
    public DisplayData getDisplayDataOrCreate(int displayId) {
        DisplayData wpdData = this.mDisplayDatas.get(displayId);
        if (wpdData != null) {
            return wpdData;
        }
        DisplayData wpdData2 = new DisplayData(displayId);
        ensureSaneWallpaperDisplaySize(wpdData2, displayId);
        this.mDisplayDatas.append(displayId, wpdData2);
        return wpdData2;
    }

    private void ensureSaneWallpaperDisplaySize(DisplayData wpdData, int displayId) {
        int baseSize = getMaximumSizeDimension(displayId);
        if (wpdData.mWidth < baseSize) {
            wpdData.mWidth = baseSize;
        }
        if (wpdData.mHeight < baseSize) {
            wpdData.mHeight = baseSize;
        }
    }

    private int getMaximumSizeDimension(int displayId) {
        Display display = this.mDisplayManager.getDisplay(displayId);
        if (display == null) {
            Slog.w(TAG, "Invalid displayId=" + displayId + " " + Debug.getCallers(4));
            display = this.mDisplayManager.getDisplay(0);
        }
        return display.getMaximumSizeDimension();
    }

    /* access modifiers changed from: package-private */
    public void forEachDisplayData(Consumer<DisplayData> action) {
        for (int i = this.mDisplayDatas.size() - 1; i >= 0; i--) {
            action.accept(this.mDisplayDatas.valueAt(i));
        }
    }

    /* access modifiers changed from: package-private */
    public int makeWallpaperIdLocked() {
        int i;
        do {
            this.mWallpaperId++;
            i = this.mWallpaperId;
        } while (i == 0);
        return i;
    }

    /* access modifiers changed from: private */
    public boolean supportsMultiDisplay(WallpaperConnection connection) {
        if (connection == null) {
            return false;
        }
        if (connection.mInfo == null || connection.mInfo.supportsMultipleDisplays()) {
            return true;
        }
        return false;
    }

    private void updateFallbackConnection() {
        WallpaperData wallpaperData = this.mLastWallpaper;
        if (wallpaperData != null && this.mFallbackWallpaper != null) {
            WallpaperConnection systemConnection = wallpaperData.connection;
            WallpaperConnection fallbackConnection = this.mFallbackWallpaper.connection;
            if (fallbackConnection == null) {
                Slog.w(TAG, "Fallback wallpaper connection has not been created yet!!");
            } else if (!supportsMultiDisplay(systemConnection)) {
                fallbackConnection.appendConnectorWithCondition(new Predicate() {
                    public final boolean test(Object obj) {
                        return WallpaperManagerService.lambda$updateFallbackConnection$2(WallpaperManagerService.WallpaperConnection.this, (Display) obj);
                    }
                });
                fallbackConnection.forEachDisplayConnector(new Consumer(fallbackConnection) {
                    private final /* synthetic */ WallpaperManagerService.WallpaperConnection f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void accept(Object obj) {
                        WallpaperManagerService.this.lambda$updateFallbackConnection$3$WallpaperManagerService(this.f$1, (WallpaperManagerService.WallpaperConnection.DisplayConnector) obj);
                    }
                });
            } else if (fallbackConnection.mDisplayConnector.size() != 0) {
                fallbackConnection.forEachDisplayConnector($$Lambda$WallpaperManagerService$pVmree9DyIpBSg0s3RDK3MDesvs.INSTANCE);
                fallbackConnection.mDisplayConnector.clear();
            }
        }
    }

    static /* synthetic */ void lambda$updateFallbackConnection$1(WallpaperConnection.DisplayConnector connector) {
        if (connector.mEngine != null) {
            connector.disconnectLocked();
        }
    }

    static /* synthetic */ boolean lambda$updateFallbackConnection$2(WallpaperConnection fallbackConnection, Display display) {
        return fallbackConnection.isUsableDisplay(display) && display.getDisplayId() != 0 && !fallbackConnection.containsDisplay(display.getDisplayId());
    }

    public /* synthetic */ void lambda$updateFallbackConnection$3$WallpaperManagerService(WallpaperConnection fallbackConnection, WallpaperConnection.DisplayConnector connector) {
        if (connector.mEngine == null) {
            connector.connectLocked(fallbackConnection, this.mFallbackWallpaper);
        }
    }

    class WallpaperConnection extends IWallpaperConnection.Stub implements ServiceConnection {
        private static final long WALLPAPER_RECONNECT_TIMEOUT_MS = 10000;
        final int mClientUid;
        /* access modifiers changed from: private */
        public SparseArray<DisplayConnector> mDisplayConnector = new SparseArray<>();
        final WallpaperInfo mInfo;
        IRemoteCallback mReply;
        /* access modifiers changed from: private */
        public Runnable mResetRunnable = new Runnable() {
            public final void run() {
                WallpaperManagerService.WallpaperConnection.this.lambda$new$0$WallpaperManagerService$WallpaperConnection();
            }
        };
        IWallpaperService mService;
        WallpaperData mWallpaper;

        private final class DisplayConnector {
            boolean mDimensionsChanged;
            final int mDisplayId;
            /* access modifiers changed from: package-private */
            public IWallpaperEngine mEngine;
            boolean mPaddingChanged;
            final Binder mToken = new Binder();

            DisplayConnector(int displayId) {
                this.mDisplayId = displayId;
            }

            /* access modifiers changed from: package-private */
            public void ensureStatusHandled() {
                DisplayData wpdData = WallpaperManagerService.this.getDisplayDataOrCreate(this.mDisplayId);
                if (this.mDimensionsChanged) {
                    try {
                        this.mEngine.setDesiredSize(wpdData.mWidth, wpdData.mHeight);
                    } catch (RemoteException e) {
                        Slog.w(WallpaperManagerService.TAG, "Failed to set wallpaper dimensions", e);
                    }
                    this.mDimensionsChanged = false;
                }
                if (this.mPaddingChanged) {
                    try {
                        this.mEngine.setDisplayPadding(wpdData.mPadding);
                    } catch (RemoteException e2) {
                        Slog.w(WallpaperManagerService.TAG, "Failed to set wallpaper padding", e2);
                    }
                    this.mPaddingChanged = false;
                }
            }

            /* access modifiers changed from: package-private */
            public void connectLocked(WallpaperConnection connection, WallpaperData wallpaper) {
                if (connection.mService == null) {
                    Slog.w(WallpaperManagerService.TAG, "WallpaperService is not connected yet");
                    return;
                }
                try {
                    WallpaperManagerService.this.mIWindowManager.addWindowToken(this.mToken, 2013, this.mDisplayId);
                    DisplayData wpdData = WallpaperManagerService.this.getDisplayDataOrCreate(this.mDisplayId);
                    try {
                        connection.mService.attach(connection, this.mToken, 2013, false, wpdData.mWidth, wpdData.mHeight, wpdData.mPadding, this.mDisplayId);
                    } catch (RemoteException e) {
                        Slog.w(WallpaperManagerService.TAG, "Failed attaching wallpaper on display", e);
                        if (wallpaper != null && !wallpaper.wallpaperUpdating && connection.getConnectedEngineSize() == 0) {
                            boolean unused = WallpaperManagerService.this.bindWallpaperComponentLocked((ComponentName) null, false, false, wallpaper, (IRemoteCallback) null);
                        }
                    }
                } catch (RemoteException e2) {
                    Slog.e(WallpaperManagerService.TAG, "Failed add wallpaper window token on display " + this.mDisplayId, e2);
                }
            }

            /* access modifiers changed from: package-private */
            public void disconnectLocked() {
                try {
                    WallpaperManagerService.this.mIWindowManager.removeWindowToken(this.mToken, this.mDisplayId);
                } catch (RemoteException e) {
                }
                try {
                    if (this.mEngine != null) {
                        this.mEngine.destroy();
                    }
                } catch (RemoteException e2) {
                }
                this.mEngine = null;
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0055, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$new$0$WallpaperManagerService$WallpaperConnection() {
            /*
                r5 = this;
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.wallpaper.WallpaperManagerService r1 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x0056 }
                boolean r1 = r1.mShuttingDown     // Catch:{ all -> 0x0056 }
                if (r1 == 0) goto L_0x0018
                java.lang.String r1 = "WallpaperManagerService"
                java.lang.String r2 = "Ignoring relaunch timeout during shutdown"
                android.util.Slog.i(r1, r2)     // Catch:{ all -> 0x0056 }
                monitor-exit(r0)     // Catch:{ all -> 0x0056 }
                return
            L_0x0018:
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = r5.mWallpaper     // Catch:{ all -> 0x0056 }
                boolean r1 = r1.wallpaperUpdating     // Catch:{ all -> 0x0056 }
                if (r1 != 0) goto L_0x0054
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = r5.mWallpaper     // Catch:{ all -> 0x0056 }
                int r1 = r1.userId     // Catch:{ all -> 0x0056 }
                com.android.server.wallpaper.WallpaperManagerService r2 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x0056 }
                int r2 = r2.mCurrentUserId     // Catch:{ all -> 0x0056 }
                if (r1 != r2) goto L_0x0054
                java.lang.String r1 = "WallpaperManagerService"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0056 }
                r2.<init>()     // Catch:{ all -> 0x0056 }
                java.lang.String r3 = "Wallpaper reconnect timed out for "
                r2.append(r3)     // Catch:{ all -> 0x0056 }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r3 = r5.mWallpaper     // Catch:{ all -> 0x0056 }
                android.content.ComponentName r3 = r3.wallpaperComponent     // Catch:{ all -> 0x0056 }
                r2.append(r3)     // Catch:{ all -> 0x0056 }
                java.lang.String r3 = ", reverting to built-in wallpaper!"
                r2.append(r3)     // Catch:{ all -> 0x0056 }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0056 }
                android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x0056 }
                com.android.server.wallpaper.WallpaperManagerService r1 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x0056 }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r2 = r5.mWallpaper     // Catch:{ all -> 0x0056 }
                int r2 = r2.userId     // Catch:{ all -> 0x0056 }
                r3 = 0
                r4 = 1
                r1.clearWallpaperLocked(r4, r4, r2, r3)     // Catch:{ all -> 0x0056 }
            L_0x0054:
                monitor-exit(r0)     // Catch:{ all -> 0x0056 }
                return
            L_0x0056:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0056 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.WallpaperConnection.lambda$new$0$WallpaperManagerService$WallpaperConnection():void");
        }

        WallpaperConnection(WallpaperInfo info, WallpaperData wallpaper, int clientUid) {
            this.mInfo = info;
            this.mWallpaper = wallpaper;
            this.mClientUid = clientUid;
            initDisplayState();
        }

        private void initDisplayState() {
            if (this.mWallpaper.equals(WallpaperManagerService.this.mFallbackWallpaper)) {
                return;
            }
            if (WallpaperManagerService.this.supportsMultiDisplay(this)) {
                appendConnectorWithCondition(new Predicate() {
                    public final boolean test(Object obj) {
                        return WallpaperManagerService.WallpaperConnection.this.isUsableDisplay((Display) obj);
                    }
                });
            } else {
                this.mDisplayConnector.append(0, new DisplayConnector(0));
            }
        }

        /* access modifiers changed from: private */
        public void appendConnectorWithCondition(Predicate<Display> tester) {
            for (Display display : WallpaperManagerService.this.mDisplayManager.getDisplays()) {
                if (tester.test(display)) {
                    int displayId = display.getDisplayId();
                    if (this.mDisplayConnector.get(displayId) == null) {
                        this.mDisplayConnector.append(displayId, new DisplayConnector(displayId));
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public boolean isUsableDisplay(Display display) {
            if (display == null || !display.hasAccess(this.mClientUid)) {
                return false;
            }
            int displayId = display.getDisplayId();
            if (displayId == 0) {
                return true;
            }
            long ident = Binder.clearCallingIdentity();
            try {
                return WallpaperManagerService.this.mWindowManagerInternal.shouldShowSystemDecorOnDisplay(displayId);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* access modifiers changed from: package-private */
        public void forEachDisplayConnector(Consumer<DisplayConnector> action) {
            for (int i = this.mDisplayConnector.size() - 1; i >= 0; i--) {
                action.accept(this.mDisplayConnector.valueAt(i));
            }
        }

        /* access modifiers changed from: package-private */
        public int getConnectedEngineSize() {
            int engineSize = 0;
            for (int i = this.mDisplayConnector.size() - 1; i >= 0; i--) {
                if (this.mDisplayConnector.valueAt(i).mEngine != null) {
                    engineSize++;
                }
            }
            return engineSize;
        }

        /* access modifiers changed from: package-private */
        public DisplayConnector getDisplayConnectorOrCreate(int displayId) {
            DisplayConnector connector = this.mDisplayConnector.get(displayId);
            if (connector != null || !isUsableDisplay(WallpaperManagerService.this.mDisplayManager.getDisplay(displayId))) {
                return connector;
            }
            DisplayConnector connector2 = new DisplayConnector(displayId);
            this.mDisplayConnector.append(displayId, connector2);
            return connector2;
        }

        /* access modifiers changed from: package-private */
        public boolean containsDisplay(int displayId) {
            return this.mDisplayConnector.get(displayId) != null;
        }

        /* access modifiers changed from: package-private */
        public void removeDisplayConnector(int displayId) {
            if (this.mDisplayConnector.get(displayId) != null) {
                this.mDisplayConnector.remove(displayId);
            }
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (this.mWallpaper.connection == this) {
                    this.mService = IWallpaperService.Stub.asInterface(service);
                    WallpaperManagerService.this.attachServiceLocked(this, this.mWallpaper);
                    if (!this.mWallpaper.equals(WallpaperManagerService.this.mFallbackWallpaper)) {
                        WallpaperManagerService.this.saveSettingsLocked(this.mWallpaper.userId);
                    }
                    FgThread.getHandler().removeCallbacks(this.mResetRunnable);
                }
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            synchronized (WallpaperManagerService.this.mLock) {
                Slog.w(WallpaperManagerService.TAG, "Wallpaper service gone: " + name);
                if (!Objects.equals(name, this.mWallpaper.wallpaperComponent)) {
                    Slog.e(WallpaperManagerService.TAG, "Does not match expected wallpaper component " + this.mWallpaper.wallpaperComponent);
                }
                this.mService = null;
                forEachDisplayConnector($$Lambda$WallpaperManagerService$WallpaperConnection$87DhM3RJJxRNtgkHmd_gtnGkz4.INSTANCE);
                if (this.mWallpaper.connection == this && !this.mWallpaper.wallpaperUpdating) {
                    WallpaperManagerService.this.mContext.getMainThreadHandler().postDelayed(new Runnable() {
                        public final void run() {
                            WallpaperManagerService.WallpaperConnection.this.lambda$onServiceDisconnected$2$WallpaperManagerService$WallpaperConnection();
                        }
                    }, 1000);
                }
            }
        }

        public /* synthetic */ void lambda$onServiceDisconnected$2$WallpaperManagerService$WallpaperConnection() {
            processDisconnect(this);
        }

        public void scheduleTimeoutLocked() {
            Handler fgHandler = FgThread.getHandler();
            fgHandler.removeCallbacks(this.mResetRunnable);
            fgHandler.postDelayed(this.mResetRunnable, 10000);
            Slog.i(WallpaperManagerService.TAG, "Started wallpaper reconnect timeout for " + this.mWallpaper.wallpaperComponent);
        }

        private void processDisconnect(ServiceConnection connection) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (connection == this.mWallpaper.connection) {
                    ComponentName wpService = this.mWallpaper.wallpaperComponent;
                    if (!this.mWallpaper.wallpaperUpdating && this.mWallpaper.userId == WallpaperManagerService.this.mCurrentUserId && !Objects.equals(WallpaperManagerService.this.mDefaultWallpaperComponent, wpService) && !Objects.equals(WallpaperManagerService.this.mImageWallpaper, wpService)) {
                        if (this.mWallpaper.lastDiedTime == 0 || this.mWallpaper.lastDiedTime + 10000 <= SystemClock.uptimeMillis()) {
                            this.mWallpaper.lastDiedTime = SystemClock.uptimeMillis();
                            WallpaperManagerService.this.clearWallpaperComponentLocked(this.mWallpaper);
                            if (WallpaperManagerService.this.bindWallpaperComponentLocked(wpService, false, false, this.mWallpaper, (IRemoteCallback) null)) {
                                this.mWallpaper.connection.scheduleTimeoutLocked();
                            } else {
                                Slog.w(WallpaperManagerService.TAG, "Reverting to built-in wallpaper!");
                                WallpaperManagerService.this.clearWallpaperLocked(true, 1, this.mWallpaper.userId, (IRemoteCallback) null);
                            }
                        } else {
                            Slog.w(WallpaperManagerService.TAG, "Reverting to built-in wallpaper!");
                            WallpaperManagerService.this.clearWallpaperLocked(true, 1, this.mWallpaper.userId, (IRemoteCallback) null);
                        }
                        String flattened = wpService.flattenToString();
                        EventLog.writeEvent(EventLogTags.WP_WALLPAPER_CRASHED, flattened.substring(0, Math.min(flattened.length(), 128)));
                    }
                } else {
                    Slog.i(WallpaperManagerService.TAG, "Wallpaper changed during disconnect tracking; ignoring");
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0035, code lost:
            if (r1 == 0) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0037, code lost:
            com.android.server.wallpaper.WallpaperManagerService.access$2800(r4.this$0, r4.mWallpaper, r1, r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onWallpaperColorsChanged(android.app.WallpaperColors r5, int r6) {
            /*
                r4 = this;
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.wallpaper.WallpaperManagerService r1 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x003f }
                android.content.ComponentName r1 = r1.mImageWallpaper     // Catch:{ all -> 0x003f }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r2 = r4.mWallpaper     // Catch:{ all -> 0x003f }
                android.content.ComponentName r2 = r2.wallpaperComponent     // Catch:{ all -> 0x003f }
                boolean r1 = r1.equals(r2)     // Catch:{ all -> 0x003f }
                if (r1 == 0) goto L_0x0019
                monitor-exit(r0)     // Catch:{ all -> 0x003f }
                return
            L_0x0019:
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = r4.mWallpaper     // Catch:{ all -> 0x003f }
                r1.primaryColors = r5     // Catch:{ all -> 0x003f }
                r1 = 1
                if (r6 != 0) goto L_0x0034
                com.android.server.wallpaper.WallpaperManagerService r2 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x003f }
                android.util.SparseArray r2 = r2.mLockWallpaperMap     // Catch:{ all -> 0x003f }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r3 = r4.mWallpaper     // Catch:{ all -> 0x003f }
                int r3 = r3.userId     // Catch:{ all -> 0x003f }
                java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x003f }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r2 = (com.android.server.wallpaper.WallpaperManagerService.WallpaperData) r2     // Catch:{ all -> 0x003f }
                if (r2 != 0) goto L_0x0034
                r1 = r1 | 2
            L_0x0034:
                monitor-exit(r0)     // Catch:{ all -> 0x003f }
                if (r1 == 0) goto L_0x003e
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r2 = r4.mWallpaper
                r0.notifyWallpaperColorsChangedOnDisplay(r2, r1, r6)
            L_0x003e:
                return
            L_0x003f:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x003f }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.WallpaperConnection.onWallpaperColorsChanged(android.app.WallpaperColors, int):void");
        }

        public void attachEngine(IWallpaperEngine engine, int displayId) {
            synchronized (WallpaperManagerService.this.mLock) {
                DisplayConnector connector = getDisplayConnectorOrCreate(displayId);
                if (connector == null) {
                    try {
                        engine.destroy();
                    } catch (RemoteException e) {
                        Slog.w(WallpaperManagerService.TAG, "Failed to destroy engine", e);
                    }
                } else {
                    connector.mEngine = engine;
                    connector.ensureStatusHandled();
                    if (this.mInfo != null && this.mInfo.supportsAmbientMode() && displayId == 0) {
                        try {
                            connector.mEngine.setInAmbientMode(WallpaperManagerService.this.mInAmbientMode, 0);
                        } catch (RemoteException e2) {
                            Slog.w(WallpaperManagerService.TAG, "Failed to set ambient mode state", e2);
                        }
                    }
                    try {
                        connector.mEngine.requestWallpaperColors();
                    } catch (RemoteException e3) {
                        Slog.w(WallpaperManagerService.TAG, "Failed to request wallpaper colors", e3);
                    }
                }
            }
            return;
        }

        public void engineShown(IWallpaperEngine engine) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (this.mReply != null) {
                    long ident = Binder.clearCallingIdentity();
                    try {
                        this.mReply.sendResult((Bundle) null);
                    } catch (RemoteException e) {
                        Binder.restoreCallingIdentity(ident);
                    }
                    this.mReply = null;
                }
            }
        }

        public ParcelFileDescriptor setWallpaper(String name) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (this.mWallpaper.connection != this) {
                    return null;
                }
                ParcelFileDescriptor updateWallpaperBitmapLocked = WallpaperManagerService.this.updateWallpaperBitmapLocked(name, this.mWallpaper, (Bundle) null);
                return updateWallpaperBitmapLocked;
            }
        }
    }

    class MyPackageMonitor extends PackageMonitor {
        MyPackageMonitor() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:17:0x008d, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onPackageUpdateFinished(java.lang.String r11, int r12) {
            /*
                r10 = this;
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.wallpaper.WallpaperManagerService r1 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x008e }
                int r1 = r1.mCurrentUserId     // Catch:{ all -> 0x008e }
                int r2 = r10.getChangingUserId()     // Catch:{ all -> 0x008e }
                if (r1 == r2) goto L_0x0015
                monitor-exit(r0)     // Catch:{ all -> 0x008e }
                return
            L_0x0015:
                com.android.server.wallpaper.WallpaperManagerService r1 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x008e }
                android.util.SparseArray r1 = r1.mWallpaperMap     // Catch:{ all -> 0x008e }
                com.android.server.wallpaper.WallpaperManagerService r2 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x008e }
                int r2 = r2.mCurrentUserId     // Catch:{ all -> 0x008e }
                java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x008e }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = (com.android.server.wallpaper.WallpaperManagerService.WallpaperData) r1     // Catch:{ all -> 0x008e }
                if (r1 == 0) goto L_0x008c
                android.content.ComponentName r2 = r1.wallpaperComponent     // Catch:{ all -> 0x008e }
                r8 = r2
                if (r8 == 0) goto L_0x008c
                java.lang.String r2 = r8.getPackageName()     // Catch:{ all -> 0x008e }
                boolean r2 = r2.equals(r11)     // Catch:{ all -> 0x008e }
                if (r2 == 0) goto L_0x008c
                java.lang.String r2 = "WallpaperManagerService"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x008e }
                r3.<init>()     // Catch:{ all -> 0x008e }
                java.lang.String r4 = "Wallpaper "
                r3.append(r4)     // Catch:{ all -> 0x008e }
                r3.append(r8)     // Catch:{ all -> 0x008e }
                java.lang.String r4 = " update has finished"
                r3.append(r4)     // Catch:{ all -> 0x008e }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x008e }
                android.util.Slog.i(r2, r3)     // Catch:{ all -> 0x008e }
                r9 = 0
                r1.wallpaperUpdating = r9     // Catch:{ all -> 0x008e }
                com.android.server.wallpaper.WallpaperManagerService r2 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x008e }
                r2.clearWallpaperComponentLocked(r1)     // Catch:{ all -> 0x008e }
                com.android.server.wallpaper.WallpaperManagerService r2 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x008e }
                r4 = 0
                r5 = 0
                r7 = 0
                r3 = r8
                r6 = r1
                boolean r2 = r2.bindWallpaperComponentLocked(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x008e }
                if (r2 != 0) goto L_0x008c
                java.lang.String r2 = "WallpaperManagerService"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x008e }
                r3.<init>()     // Catch:{ all -> 0x008e }
                java.lang.String r4 = "Wallpaper "
                r3.append(r4)     // Catch:{ all -> 0x008e }
                r3.append(r8)     // Catch:{ all -> 0x008e }
                java.lang.String r4 = " no longer available; reverting to default"
                r3.append(r4)     // Catch:{ all -> 0x008e }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x008e }
                android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x008e }
                com.android.server.wallpaper.WallpaperManagerService r2 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x008e }
                r3 = 1
                int r4 = r1.userId     // Catch:{ all -> 0x008e }
                r5 = 0
                r2.clearWallpaperLocked(r9, r3, r4, r5)     // Catch:{ all -> 0x008e }
            L_0x008c:
                monitor-exit(r0)     // Catch:{ all -> 0x008e }
                return
            L_0x008e:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x008e }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.MyPackageMonitor.onPackageUpdateFinished(java.lang.String, int):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0040, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0042, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onPackageModified(java.lang.String r4) {
            /*
                r3 = this;
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.wallpaper.WallpaperManagerService r1 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x0043 }
                int r1 = r1.mCurrentUserId     // Catch:{ all -> 0x0043 }
                int r2 = r3.getChangingUserId()     // Catch:{ all -> 0x0043 }
                if (r1 == r2) goto L_0x0015
                monitor-exit(r0)     // Catch:{ all -> 0x0043 }
                return
            L_0x0015:
                com.android.server.wallpaper.WallpaperManagerService r1 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x0043 }
                android.util.SparseArray r1 = r1.mWallpaperMap     // Catch:{ all -> 0x0043 }
                com.android.server.wallpaper.WallpaperManagerService r2 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x0043 }
                int r2 = r2.mCurrentUserId     // Catch:{ all -> 0x0043 }
                java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0043 }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = (com.android.server.wallpaper.WallpaperManagerService.WallpaperData) r1     // Catch:{ all -> 0x0043 }
                if (r1 == 0) goto L_0x0041
                android.content.ComponentName r2 = r1.wallpaperComponent     // Catch:{ all -> 0x0043 }
                if (r2 == 0) goto L_0x003f
                android.content.ComponentName r2 = r1.wallpaperComponent     // Catch:{ all -> 0x0043 }
                java.lang.String r2 = r2.getPackageName()     // Catch:{ all -> 0x0043 }
                boolean r2 = r2.equals(r4)     // Catch:{ all -> 0x0043 }
                if (r2 != 0) goto L_0x003a
                goto L_0x003f
            L_0x003a:
                r2 = 1
                r3.doPackagesChangedLocked(r2, r1)     // Catch:{ all -> 0x0043 }
                goto L_0x0041
            L_0x003f:
                monitor-exit(r0)     // Catch:{ all -> 0x0043 }
                return
            L_0x0041:
                monitor-exit(r0)     // Catch:{ all -> 0x0043 }
                return
            L_0x0043:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0043 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.MyPackageMonitor.onPackageModified(java.lang.String):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:17:0x006b, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onPackageUpdateStarted(java.lang.String r6, int r7) {
            /*
                r5 = this;
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.wallpaper.WallpaperManagerService r1 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x006c }
                int r1 = r1.mCurrentUserId     // Catch:{ all -> 0x006c }
                int r2 = r5.getChangingUserId()     // Catch:{ all -> 0x006c }
                if (r1 == r2) goto L_0x0015
                monitor-exit(r0)     // Catch:{ all -> 0x006c }
                return
            L_0x0015:
                com.android.server.wallpaper.WallpaperManagerService r1 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x006c }
                android.util.SparseArray r1 = r1.mWallpaperMap     // Catch:{ all -> 0x006c }
                com.android.server.wallpaper.WallpaperManagerService r2 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x006c }
                int r2 = r2.mCurrentUserId     // Catch:{ all -> 0x006c }
                java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x006c }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = (com.android.server.wallpaper.WallpaperManagerService.WallpaperData) r1     // Catch:{ all -> 0x006c }
                if (r1 == 0) goto L_0x006a
                android.content.ComponentName r2 = r1.wallpaperComponent     // Catch:{ all -> 0x006c }
                if (r2 == 0) goto L_0x006a
                android.content.ComponentName r2 = r1.wallpaperComponent     // Catch:{ all -> 0x006c }
                java.lang.String r2 = r2.getPackageName()     // Catch:{ all -> 0x006c }
                boolean r2 = r2.equals(r6)     // Catch:{ all -> 0x006c }
                if (r2 == 0) goto L_0x006a
                java.lang.String r2 = "WallpaperManagerService"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x006c }
                r3.<init>()     // Catch:{ all -> 0x006c }
                java.lang.String r4 = "Wallpaper service "
                r3.append(r4)     // Catch:{ all -> 0x006c }
                android.content.ComponentName r4 = r1.wallpaperComponent     // Catch:{ all -> 0x006c }
                r3.append(r4)     // Catch:{ all -> 0x006c }
                java.lang.String r4 = " is updating"
                r3.append(r4)     // Catch:{ all -> 0x006c }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x006c }
                android.util.Slog.i(r2, r3)     // Catch:{ all -> 0x006c }
                r2 = 1
                r1.wallpaperUpdating = r2     // Catch:{ all -> 0x006c }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r2 = r1.connection     // Catch:{ all -> 0x006c }
                if (r2 == 0) goto L_0x006a
                android.os.Handler r2 = com.android.server.FgThread.getHandler()     // Catch:{ all -> 0x006c }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r3 = r1.connection     // Catch:{ all -> 0x006c }
                java.lang.Runnable r3 = r3.mResetRunnable     // Catch:{ all -> 0x006c }
                r2.removeCallbacks(r3)     // Catch:{ all -> 0x006c }
            L_0x006a:
                monitor-exit(r0)     // Catch:{ all -> 0x006c }
                return
            L_0x006c:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x006c }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.MyPackageMonitor.onPackageUpdateStarted(java.lang.String, int):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0031, code lost:
            return r1;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onHandleForceStop(android.content.Intent r5, java.lang.String[] r6, int r7, boolean r8) {
            /*
                r4 = this;
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                r1 = 0
                com.android.server.wallpaper.WallpaperManagerService r2 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x0032 }
                int r2 = r2.mCurrentUserId     // Catch:{ all -> 0x0032 }
                int r3 = r4.getChangingUserId()     // Catch:{ all -> 0x0032 }
                if (r2 == r3) goto L_0x0017
                r2 = 0
                monitor-exit(r0)     // Catch:{ all -> 0x0032 }
                return r2
            L_0x0017:
                com.android.server.wallpaper.WallpaperManagerService r2 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x0032 }
                android.util.SparseArray r2 = r2.mWallpaperMap     // Catch:{ all -> 0x0032 }
                com.android.server.wallpaper.WallpaperManagerService r3 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x0032 }
                int r3 = r3.mCurrentUserId     // Catch:{ all -> 0x0032 }
                java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x0032 }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r2 = (com.android.server.wallpaper.WallpaperManagerService.WallpaperData) r2     // Catch:{ all -> 0x0032 }
                if (r2 == 0) goto L_0x0030
                boolean r3 = r4.doPackagesChangedLocked(r8, r2)     // Catch:{ all -> 0x0032 }
                r1 = r1 | r3
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0032 }
                return r1
            L_0x0032:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0032 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.MyPackageMonitor.onHandleForceStop(android.content.Intent, java.lang.String[], int, boolean):boolean");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x002e, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onSomePackagesChanged() {
            /*
                r3 = this;
                com.android.server.wallpaper.WallpaperManagerService r0 = com.android.server.wallpaper.WallpaperManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.wallpaper.WallpaperManagerService r1 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x002f }
                int r1 = r1.mCurrentUserId     // Catch:{ all -> 0x002f }
                int r2 = r3.getChangingUserId()     // Catch:{ all -> 0x002f }
                if (r1 == r2) goto L_0x0015
                monitor-exit(r0)     // Catch:{ all -> 0x002f }
                return
            L_0x0015:
                com.android.server.wallpaper.WallpaperManagerService r1 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x002f }
                android.util.SparseArray r1 = r1.mWallpaperMap     // Catch:{ all -> 0x002f }
                com.android.server.wallpaper.WallpaperManagerService r2 = com.android.server.wallpaper.WallpaperManagerService.this     // Catch:{ all -> 0x002f }
                int r2 = r2.mCurrentUserId     // Catch:{ all -> 0x002f }
                java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x002f }
                com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = (com.android.server.wallpaper.WallpaperManagerService.WallpaperData) r1     // Catch:{ all -> 0x002f }
                if (r1 == 0) goto L_0x002d
                r2 = 1
                r3.doPackagesChangedLocked(r2, r1)     // Catch:{ all -> 0x002f }
            L_0x002d:
                monitor-exit(r0)     // Catch:{ all -> 0x002f }
                return
            L_0x002f:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x002f }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.MyPackageMonitor.onSomePackagesChanged():void");
        }

        /* access modifiers changed from: package-private */
        public boolean doPackagesChangedLocked(boolean doit, WallpaperData wallpaper) {
            int change;
            int change2;
            boolean changed = false;
            if (wallpaper.wallpaperComponent != null && ((change2 = isPackageDisappearing(wallpaper.wallpaperComponent.getPackageName())) == 3 || change2 == 2)) {
                changed = true;
                if (doit) {
                    Slog.w(WallpaperManagerService.TAG, "Wallpaper uninstalled, removing: " + wallpaper.wallpaperComponent);
                    WallpaperManagerService.this.clearWallpaperLocked(false, 1, wallpaper.userId, (IRemoteCallback) null);
                }
            }
            if (wallpaper.nextWallpaperComponent != null && ((change = isPackageDisappearing(wallpaper.nextWallpaperComponent.getPackageName())) == 3 || change == 2)) {
                wallpaper.nextWallpaperComponent = null;
            }
            if (wallpaper.wallpaperComponent != null && isPackageModified(wallpaper.wallpaperComponent.getPackageName())) {
                try {
                    WallpaperManagerService.this.mContext.getPackageManager().getServiceInfo(wallpaper.wallpaperComponent, 786432);
                } catch (PackageManager.NameNotFoundException e) {
                    Slog.w(WallpaperManagerService.TAG, "Wallpaper component gone, removing: " + wallpaper.wallpaperComponent);
                    WallpaperManagerService.this.clearWallpaperLocked(false, 1, wallpaper.userId, (IRemoteCallback) null);
                }
            }
            if (wallpaper.nextWallpaperComponent != null && isPackageModified(wallpaper.nextWallpaperComponent.getPackageName())) {
                try {
                    WallpaperManagerService.this.mContext.getPackageManager().getServiceInfo(wallpaper.nextWallpaperComponent, 786432);
                } catch (PackageManager.NameNotFoundException e2) {
                    wallpaper.nextWallpaperComponent = null;
                }
            }
            return changed;
        }
    }

    public WallpaperManagerService(Context context) {
        this.mContext = context;
        this.mShuttingDown = false;
        this.mImageWallpaper = ComponentName.unflattenFromString(context.getResources().getString(17040174));
        this.mDefaultWallpaperComponent = WallpaperManager.getDefaultWallpaperComponent(context);
        this.mIWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        this.mWindowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
        this.mIPackageManager = AppGlobals.getPackageManager();
        this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService("appops");
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        this.mDisplayManager.registerDisplayListener(this.mDisplayListener, (Handler) null);
        this.mMonitor = new MyPackageMonitor();
        this.mColorsChangedListeners = new SparseArray<>();
        LocalServices.addService(WallpaperManagerInternal.class, new LocalService());
    }

    private final class LocalService extends WallpaperManagerInternal {
        private LocalService() {
        }

        public void onDisplayReady(int displayId) {
            WallpaperManagerService.this.onDisplayReadyInternal(displayId);
        }
    }

    /* access modifiers changed from: package-private */
    public void initialize() {
        this.mMonitor.register(this.mContext, (Looper) null, UserHandle.ALL, true);
        getWallpaperDir(0).mkdirs();
        loadSettingsLocked(0, false);
        getWallpaperSafeLocked(0, 1);
    }

    /* access modifiers changed from: private */
    public static File getWallpaperDir(int userId) {
        return Environment.getUserSystemDirectory(userId);
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        for (int i = 0; i < this.mWallpaperMap.size(); i++) {
            this.mWallpaperMap.valueAt(i).wallpaperObserver.stopWatching();
        }
    }

    /* access modifiers changed from: package-private */
    public void systemReady() {
        initialize();
        WallpaperData wallpaper = this.mWallpaperMap.get(0);
        if (this.mImageWallpaper.equals(wallpaper.nextWallpaperComponent)) {
            if (!wallpaper.cropExists()) {
                generateCrop(wallpaper);
            }
            if (!wallpaper.cropExists()) {
                clearWallpaperLocked(false, 1, 0, (IRemoteCallback) null);
            }
        }
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction("android.intent.action.USER_REMOVED");
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.USER_REMOVED".equals(intent.getAction())) {
                    WallpaperManagerService.this.onRemoveUser(intent.getIntExtra("android.intent.extra.user_handle", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION));
                }
            }
        }, userFilter);
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
                    synchronized (WallpaperManagerService.this.mLock) {
                        boolean unused = WallpaperManagerService.this.mShuttingDown = true;
                    }
                }
            }
        }, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"));
        try {
            ActivityManager.getService().registerUserSwitchObserver(new UserSwitchObserver() {
                public void onUserSwitching(int newUserId, IRemoteCallback reply) {
                    WallpaperManagerService.this.switchUser(newUserId, reply);
                }
            }, TAG);
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        }
    }

    public String getName() {
        String str;
        if (Binder.getCallingUid() == 1000) {
            synchronized (this.mLock) {
                str = this.mWallpaperMap.get(0).name;
            }
            return str;
        }
        throw new RuntimeException("getName() can only be called from the system process");
    }

    /* access modifiers changed from: package-private */
    public void stopObserver(WallpaperData wallpaper) {
        if (wallpaper != null && wallpaper.wallpaperObserver != null) {
            wallpaper.wallpaperObserver.stopWatching();
            wallpaper.wallpaperObserver = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void stopObserversLocked(int userId) {
        stopObserver(this.mWallpaperMap.get(userId));
        stopObserver(this.mLockWallpaperMap.get(userId));
        this.mWallpaperMap.remove(userId);
        this.mLockWallpaperMap.remove(userId);
    }

    public void onBootPhase(int phase) {
        if (phase == 550) {
            systemReady();
        } else if (phase == 600) {
            switchUser(0, (IRemoteCallback) null);
        }
    }

    public void onUnlockUser(final int userId) {
        synchronized (this.mLock) {
            if (this.mCurrentUserId == userId) {
                if (this.mWaitingForUnlock) {
                    WallpaperData systemWallpaper = getWallpaperSafeLocked(userId, 1);
                    switchWallpaper(systemWallpaper, (IRemoteCallback) null);
                    notifyCallbacksLocked(systemWallpaper);
                }
                if (!this.mUserRestorecon.get(userId)) {
                    this.mUserRestorecon.put(userId, true);
                    BackgroundThread.getHandler().post(new Runnable() {
                        public void run() {
                            File wallpaperDir = WallpaperManagerService.getWallpaperDir(userId);
                            for (String filename : WallpaperManagerService.sPerUserFiles) {
                                File f = new File(wallpaperDir, filename);
                                if (f.exists()) {
                                    SELinux.restorecon(f);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onRemoveUser(int userId) {
        if (userId >= 1) {
            File wallpaperDir = getWallpaperDir(userId);
            synchronized (this.mLock) {
                stopObserversLocked(userId);
                for (String filename : sPerUserFiles) {
                    new File(wallpaperDir, filename).delete();
                }
                this.mUserRestorecon.delete(userId);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void switchUser(int userId, IRemoteCallback reply) {
        synchronized (this.mLock) {
            if (this.mCurrentUserId != userId) {
                this.mCurrentUserId = userId;
                WallpaperData systemWallpaper = getWallpaperSafeLocked(userId, 1);
                WallpaperData tmpLockWallpaper = this.mLockWallpaperMap.get(userId);
                WallpaperData lockWallpaper = tmpLockWallpaper == null ? systemWallpaper : tmpLockWallpaper;
                if (systemWallpaper.wallpaperObserver == null) {
                    systemWallpaper.wallpaperObserver = new WallpaperObserver(systemWallpaper);
                    systemWallpaper.wallpaperObserver.startWatching();
                }
                switchWallpaper(systemWallpaper, reply);
                FgThread.getHandler().post(new Runnable(systemWallpaper, lockWallpaper) {
                    private final /* synthetic */ WallpaperManagerService.WallpaperData f$1;
                    private final /* synthetic */ WallpaperManagerService.WallpaperData f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        WallpaperManagerService.this.lambda$switchUser$4$WallpaperManagerService(this.f$1, this.f$2);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$switchUser$4$WallpaperManagerService(WallpaperData systemWallpaper, WallpaperData lockWallpaper) {
        notifyWallpaperColorsChanged(systemWallpaper, 1);
        notifyWallpaperColorsChanged(lockWallpaper, 2);
        notifyWallpaperColorsChanged(this.mFallbackWallpaper, 1);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0071, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void switchWallpaper(com.android.server.wallpaper.WallpaperManagerService.WallpaperData r16, android.os.IRemoteCallback r17) {
        /*
            r15 = this;
            r7 = r15
            r8 = r16
            java.lang.Object r9 = r7.mLock
            monitor-enter(r9)
            r10 = 0
            r7.mWaitingForUnlock = r10     // Catch:{ all -> 0x0072 }
            android.content.ComponentName r0 = r8.wallpaperComponent     // Catch:{ all -> 0x0072 }
            if (r0 == 0) goto L_0x0010
            android.content.ComponentName r0 = r8.wallpaperComponent     // Catch:{ all -> 0x0072 }
            goto L_0x0012
        L_0x0010:
            android.content.ComponentName r0 = r8.nextWallpaperComponent     // Catch:{ all -> 0x0072 }
        L_0x0012:
            r11 = r0
            r3 = 1
            r4 = 0
            r1 = r15
            r2 = r11
            r5 = r16
            r6 = r17
            boolean r0 = r1.bindWallpaperComponentLocked(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x0072 }
            if (r0 != 0) goto L_0x006e
            r1 = 0
            android.content.pm.IPackageManager r0 = r7.mIPackageManager     // Catch:{ RemoteException -> 0x002e }
            r2 = 262144(0x40000, float:3.67342E-40)
            int r3 = r8.userId     // Catch:{ RemoteException -> 0x002e }
            android.content.pm.ServiceInfo r0 = r0.getServiceInfo(r11, r2, r3)     // Catch:{ RemoteException -> 0x002e }
            r1 = r0
            goto L_0x0030
        L_0x002e:
            r0 = move-exception
            r0 = r1
        L_0x0030:
            r12 = 1
            if (r0 != 0) goto L_0x0042
            java.lang.String r1 = "WallpaperManagerService"
            java.lang.String r2 = "Failure starting previous wallpaper; clearing"
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x0072 }
            int r1 = r8.userId     // Catch:{ all -> 0x0072 }
            r13 = r17
            r15.clearWallpaperLocked(r10, r12, r1, r13)     // Catch:{ all -> 0x0077 }
            goto L_0x0070
        L_0x0042:
            r13 = r17
            java.lang.String r1 = "WallpaperManagerService"
            java.lang.String r2 = "Wallpaper isn't direct boot aware; using fallback until unlocked"
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x0077 }
            android.content.ComponentName r1 = r8.nextWallpaperComponent     // Catch:{ all -> 0x0077 }
            r8.wallpaperComponent = r1     // Catch:{ all -> 0x0077 }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = new com.android.server.wallpaper.WallpaperManagerService$WallpaperData     // Catch:{ all -> 0x0077 }
            int r2 = r8.userId     // Catch:{ all -> 0x0077 }
            java.lang.String r3 = "wallpaper_lock_orig"
            java.lang.String r4 = "wallpaper_lock"
            r1.<init>(r2, r3, r4)     // Catch:{ all -> 0x0077 }
            r14 = r1
            r15.ensureSaneWallpaperData(r14, r10)     // Catch:{ all -> 0x0077 }
            android.content.ComponentName r2 = r7.mImageWallpaper     // Catch:{ all -> 0x0077 }
            r3 = 1
            r4 = 0
            r1 = r15
            r5 = r14
            r6 = r17
            r1.bindWallpaperComponentLocked(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x0077 }
            r7.mWaitingForUnlock = r12     // Catch:{ all -> 0x0077 }
            goto L_0x0070
        L_0x006e:
            r13 = r17
        L_0x0070:
            monitor-exit(r9)     // Catch:{ all -> 0x0077 }
            return
        L_0x0072:
            r0 = move-exception
            r13 = r17
        L_0x0075:
            monitor-exit(r9)     // Catch:{ all -> 0x0077 }
            throw r0
        L_0x0077:
            r0 = move-exception
            goto L_0x0075
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.switchWallpaper(com.android.server.wallpaper.WallpaperManagerService$WallpaperData, android.os.IRemoteCallback):void");
    }

    public void clearWallpaper(String callingPackage, int which, int userId) {
        checkPermission("android.permission.SET_WALLPAPER");
        if (isWallpaperSupported(callingPackage) && isSetWallpaperAllowed(callingPackage)) {
            int userId2 = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, true, "clearWallpaper", (String) null);
            WallpaperData data = null;
            synchronized (this.mLock) {
                clearWallpaperLocked(false, which, userId2, (IRemoteCallback) null);
                if (which == 2) {
                    data = this.mLockWallpaperMap.get(userId2);
                }
                if (which == 1 || data == null) {
                    data = this.mWallpaperMap.get(userId2);
                }
            }
            if (data != null) {
                notifyWallpaperColorsChanged(data, which);
                notifyWallpaperColorsChanged(this.mFallbackWallpaper, 1);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void clearWallpaperLocked(boolean defaultFailed, int which, int userId, IRemoteCallback reply) {
        WallpaperData wallpaper;
        ComponentName componentName;
        int i = which;
        int i2 = userId;
        IRemoteCallback iRemoteCallback = reply;
        if (i == 1 || i == 2) {
            if (i == 2) {
                WallpaperData wallpaper2 = this.mLockWallpaperMap.get(i2);
                if (wallpaper2 != null) {
                    wallpaper = wallpaper2;
                } else {
                    return;
                }
            } else {
                WallpaperData wallpaper3 = this.mWallpaperMap.get(i2);
                if (wallpaper3 == null) {
                    loadSettingsLocked(i2, false);
                    wallpaper = this.mWallpaperMap.get(i2);
                } else {
                    wallpaper = wallpaper3;
                }
            }
            if (wallpaper != null) {
                long ident = Binder.clearCallingIdentity();
                try {
                    if (wallpaper.wallpaperFile.exists()) {
                        wallpaper.wallpaperFile.delete();
                        wallpaper.cropFile.delete();
                        if (i == 2) {
                            this.mLockWallpaperMap.remove(i2);
                            IWallpaperManagerCallback cb = this.mKeyguardListener;
                            if (cb != null) {
                                try {
                                    cb.onWallpaperChanged();
                                } catch (RemoteException e) {
                                }
                            }
                            saveSettingsLocked(i2);
                            return;
                        }
                    }
                    RuntimeException e2 = null;
                    try {
                        wallpaper.primaryColors = null;
                        wallpaper.imageWallpaperPending = false;
                        if (i2 != this.mCurrentUserId) {
                            Binder.restoreCallingIdentity(ident);
                            return;
                        }
                        if (defaultFailed) {
                            componentName = this.mImageWallpaper;
                        } else {
                            componentName = null;
                        }
                        if (bindWallpaperComponentLocked(componentName, true, false, wallpaper, reply)) {
                            Binder.restoreCallingIdentity(ident);
                            return;
                        }
                        Slog.e(TAG, "Default wallpaper component not found!", e2);
                        clearWallpaperComponentLocked(wallpaper);
                        if (iRemoteCallback != null) {
                            try {
                                iRemoteCallback.sendResult((Bundle) null);
                            } catch (RemoteException e3) {
                            }
                        }
                        Binder.restoreCallingIdentity(ident);
                    } catch (IllegalArgumentException e1) {
                        e2 = e1;
                    }
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        } else {
            throw new IllegalArgumentException("Must specify exactly one kind of wallpaper to clear");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* JADX INFO: finally extract failed */
    public boolean hasNamedWallpaper(String name) {
        synchronized (this.mLock) {
            long ident = Binder.clearCallingIdentity();
            try {
                List<UserInfo> users = ((UserManager) this.mContext.getSystemService("user")).getUsers();
                Binder.restoreCallingIdentity(ident);
                for (UserInfo user : users) {
                    if (!user.isManagedProfile()) {
                        WallpaperData wd = this.mWallpaperMap.get(user.id);
                        if (wd == null) {
                            loadSettingsLocked(user.id, false);
                            wd = this.mWallpaperMap.get(user.id);
                        }
                        if (wd != null && name.equals(wd.name)) {
                            return true;
                        }
                    }
                }
                return false;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        }
    }

    private boolean isValidDisplay(int displayId) {
        return this.mDisplayManager.getDisplay(displayId) != null;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0073, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setDimensionHints(int r9, int r10, java.lang.String r11, int r12) throws android.os.RemoteException {
        /*
            r8 = this;
            java.lang.String r0 = "android.permission.SET_WALLPAPER_HINTS"
            r8.checkPermission(r0)
            boolean r0 = r8.isWallpaperSupported(r11)
            if (r0 != 0) goto L_0x000c
            return
        L_0x000c:
            int r0 = com.android.server.wallpaper.GLHelper.getMaxTextureSize()
            int r9 = java.lang.Math.min(r9, r0)
            int r0 = com.android.server.wallpaper.GLHelper.getMaxTextureSize()
            int r10 = java.lang.Math.min(r10, r0)
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            int r1 = android.os.UserHandle.getCallingUserId()     // Catch:{ all -> 0x0094 }
            r2 = 1
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r3 = r8.getWallpaperSafeLocked(r1, r2)     // Catch:{ all -> 0x0094 }
            if (r9 <= 0) goto L_0x008b
            if (r10 <= 0) goto L_0x008b
            boolean r4 = r8.isValidDisplay(r12)     // Catch:{ all -> 0x0094 }
            if (r4 == 0) goto L_0x0074
            com.android.server.wallpaper.WallpaperManagerService$DisplayData r4 = r8.getDisplayDataOrCreate(r12)     // Catch:{ all -> 0x0094 }
            int r5 = r4.mWidth     // Catch:{ all -> 0x0094 }
            if (r9 != r5) goto L_0x003e
            int r5 = r4.mHeight     // Catch:{ all -> 0x0094 }
            if (r10 == r5) goto L_0x0072
        L_0x003e:
            r4.mWidth = r9     // Catch:{ all -> 0x0094 }
            r4.mHeight = r10     // Catch:{ all -> 0x0094 }
            if (r12 != 0) goto L_0x0047
            r8.saveSettingsLocked(r1)     // Catch:{ all -> 0x0094 }
        L_0x0047:
            int r5 = r8.mCurrentUserId     // Catch:{ all -> 0x0094 }
            if (r5 == r1) goto L_0x004d
            monitor-exit(r0)     // Catch:{ all -> 0x0094 }
            return
        L_0x004d:
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r5 = r3.connection     // Catch:{ all -> 0x0094 }
            if (r5 == 0) goto L_0x0072
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r5 = r3.connection     // Catch:{ all -> 0x0094 }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection$DisplayConnector r5 = r5.getDisplayConnectorOrCreate(r12)     // Catch:{ all -> 0x0094 }
            if (r5 == 0) goto L_0x005c
            android.service.wallpaper.IWallpaperEngine r6 = r5.mEngine     // Catch:{ all -> 0x0094 }
            goto L_0x005d
        L_0x005c:
            r6 = 0
        L_0x005d:
            if (r6 == 0) goto L_0x0068
            r6.setDesiredSize(r9, r10)     // Catch:{ RemoteException -> 0x0063 }
            goto L_0x0064
        L_0x0063:
            r2 = move-exception
        L_0x0064:
            r8.notifyCallbacksLocked(r3)     // Catch:{ all -> 0x0094 }
            goto L_0x0072
        L_0x0068:
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r7 = r3.connection     // Catch:{ all -> 0x0094 }
            android.service.wallpaper.IWallpaperService r7 = r7.mService     // Catch:{ all -> 0x0094 }
            if (r7 == 0) goto L_0x0072
            if (r5 == 0) goto L_0x0072
            r5.mDimensionsChanged = r2     // Catch:{ all -> 0x0094 }
        L_0x0072:
            monitor-exit(r0)     // Catch:{ all -> 0x0094 }
            return
        L_0x0074:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0094 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0094 }
            r4.<init>()     // Catch:{ all -> 0x0094 }
            java.lang.String r5 = "Cannot find display with id="
            r4.append(r5)     // Catch:{ all -> 0x0094 }
            r4.append(r12)     // Catch:{ all -> 0x0094 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0094 }
            r2.<init>(r4)     // Catch:{ all -> 0x0094 }
            throw r2     // Catch:{ all -> 0x0094 }
        L_0x008b:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0094 }
            java.lang.String r4 = "width and height must be > 0"
            r2.<init>(r4)     // Catch:{ all -> 0x0094 }
            throw r2     // Catch:{ all -> 0x0094 }
        L_0x0094:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0094 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.setDimensionHints(int, int, java.lang.String, int):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public int getWidthHint(int displayId) throws RemoteException {
        synchronized (this.mLock) {
            if (!isValidDisplay(displayId)) {
                throw new IllegalArgumentException("Cannot find display with id=" + displayId);
            } else if (this.mWallpaperMap.get(UserHandle.getCallingUserId()) == null) {
                return 0;
            } else {
                int i = getDisplayDataOrCreate(displayId).mWidth;
                return i;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public int getHeightHint(int displayId) throws RemoteException {
        synchronized (this.mLock) {
            if (!isValidDisplay(displayId)) {
                throw new IllegalArgumentException("Cannot find display with id=" + displayId);
            } else if (this.mWallpaperMap.get(UserHandle.getCallingUserId()) == null) {
                return 0;
            } else {
                int i = getDisplayDataOrCreate(displayId).mHeight;
                return i;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0070, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setDisplayPadding(android.graphics.Rect r9, java.lang.String r10, int r11) {
        /*
            r8 = this;
            java.lang.String r0 = "android.permission.SET_WALLPAPER_HINTS"
            r8.checkPermission(r0)
            boolean r0 = r8.isWallpaperSupported(r10)
            if (r0 != 0) goto L_0x000c
            return
        L_0x000c:
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            boolean r1 = r8.isValidDisplay(r11)     // Catch:{ all -> 0x00a0 }
            if (r1 == 0) goto L_0x0089
            int r1 = android.os.UserHandle.getCallingUserId()     // Catch:{ all -> 0x00a0 }
            r2 = 1
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r3 = r8.getWallpaperSafeLocked(r1, r2)     // Catch:{ all -> 0x00a0 }
            int r4 = r9.left     // Catch:{ all -> 0x00a0 }
            if (r4 < 0) goto L_0x0071
            int r4 = r9.top     // Catch:{ all -> 0x00a0 }
            if (r4 < 0) goto L_0x0071
            int r4 = r9.right     // Catch:{ all -> 0x00a0 }
            if (r4 < 0) goto L_0x0071
            int r4 = r9.bottom     // Catch:{ all -> 0x00a0 }
            if (r4 < 0) goto L_0x0071
            com.android.server.wallpaper.WallpaperManagerService$DisplayData r4 = r8.getDisplayDataOrCreate(r11)     // Catch:{ all -> 0x00a0 }
            android.graphics.Rect r5 = r4.mPadding     // Catch:{ all -> 0x00a0 }
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x00a0 }
            if (r5 != 0) goto L_0x006f
            android.graphics.Rect r5 = r4.mPadding     // Catch:{ all -> 0x00a0 }
            r5.set(r9)     // Catch:{ all -> 0x00a0 }
            if (r11 != 0) goto L_0x0044
            r8.saveSettingsLocked(r1)     // Catch:{ all -> 0x00a0 }
        L_0x0044:
            int r5 = r8.mCurrentUserId     // Catch:{ all -> 0x00a0 }
            if (r5 == r1) goto L_0x004a
            monitor-exit(r0)     // Catch:{ all -> 0x00a0 }
            return
        L_0x004a:
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r5 = r3.connection     // Catch:{ all -> 0x00a0 }
            if (r5 == 0) goto L_0x006f
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r5 = r3.connection     // Catch:{ all -> 0x00a0 }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection$DisplayConnector r5 = r5.getDisplayConnectorOrCreate(r11)     // Catch:{ all -> 0x00a0 }
            if (r5 == 0) goto L_0x0059
            android.service.wallpaper.IWallpaperEngine r6 = r5.mEngine     // Catch:{ all -> 0x00a0 }
            goto L_0x005a
        L_0x0059:
            r6 = 0
        L_0x005a:
            if (r6 == 0) goto L_0x0065
            r6.setDisplayPadding(r9)     // Catch:{ RemoteException -> 0x0060 }
            goto L_0x0061
        L_0x0060:
            r2 = move-exception
        L_0x0061:
            r8.notifyCallbacksLocked(r3)     // Catch:{ all -> 0x00a0 }
            goto L_0x006f
        L_0x0065:
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r7 = r3.connection     // Catch:{ all -> 0x00a0 }
            android.service.wallpaper.IWallpaperService r7 = r7.mService     // Catch:{ all -> 0x00a0 }
            if (r7 == 0) goto L_0x006f
            if (r5 == 0) goto L_0x006f
            r5.mPaddingChanged = r2     // Catch:{ all -> 0x00a0 }
        L_0x006f:
            monitor-exit(r0)     // Catch:{ all -> 0x00a0 }
            return
        L_0x0071:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x00a0 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a0 }
            r4.<init>()     // Catch:{ all -> 0x00a0 }
            java.lang.String r5 = "padding must be positive: "
            r4.append(r5)     // Catch:{ all -> 0x00a0 }
            r4.append(r9)     // Catch:{ all -> 0x00a0 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00a0 }
            r2.<init>(r4)     // Catch:{ all -> 0x00a0 }
            throw r2     // Catch:{ all -> 0x00a0 }
        L_0x0089:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x00a0 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a0 }
            r2.<init>()     // Catch:{ all -> 0x00a0 }
            java.lang.String r3 = "Cannot find display with id="
            r2.append(r3)     // Catch:{ all -> 0x00a0 }
            r2.append(r11)     // Catch:{ all -> 0x00a0 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00a0 }
            r1.<init>(r2)     // Catch:{ all -> 0x00a0 }
            throw r1     // Catch:{ all -> 0x00a0 }
        L_0x00a0:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00a0 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.setDisplayPadding(android.graphics.Rect, java.lang.String, int):void");
    }

    public ParcelFileDescriptor getWallpaper(String callingPkg, IWallpaperManagerCallback cb, int which, Bundle outParams, int wallpaperUserId) {
        IWallpaperManagerCallback iWallpaperManagerCallback = cb;
        int i = which;
        Bundle bundle = outParams;
        if (this.mContext.checkCallingOrSelfPermission("android.permission.READ_WALLPAPER_INTERNAL") != 0) {
            ((StorageManager) this.mContext.getSystemService(StorageManager.class)).checkPermissionReadImages(true, Binder.getCallingPid(), Binder.getCallingUid(), callingPkg);
        } else {
            String str = callingPkg;
        }
        int wallpaperUserId2 = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), wallpaperUserId, false, true, "getWallpaper", (String) null);
        if (i == 1 || i == 2) {
            synchronized (this.mLock) {
                WallpaperData wallpaper = (i == 2 ? this.mLockWallpaperMap : this.mWallpaperMap).get(wallpaperUserId2);
                if (wallpaper == null) {
                    return null;
                }
                DisplayData wpdData = getDisplayDataOrCreate(0);
                if (bundle != null) {
                    try {
                        bundle.putInt("width", wpdData.mWidth);
                        bundle.putInt("height", wpdData.mHeight);
                    } catch (FileNotFoundException e) {
                        Slog.w(TAG, "Error getting wallpaper", e);
                        return null;
                    }
                }
                if (iWallpaperManagerCallback != null) {
                    wallpaper.callbacks.register(iWallpaperManagerCallback);
                }
                if (!wallpaper.cropFile.exists()) {
                    return null;
                }
                ParcelFileDescriptor open = ParcelFileDescriptor.open(wallpaper.cropFile, 268435456);
                return open;
            }
        }
        throw new IllegalArgumentException("Must specify exactly one kind of wallpaper to read");
    }

    public WallpaperInfo getWallpaperInfo(int userId) {
        int userId2 = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, true, "getWallpaperInfo", (String) null);
        synchronized (this.mLock) {
            WallpaperData wallpaper = this.mWallpaperMap.get(userId2);
            if (wallpaper == null || wallpaper.connection == null) {
                return null;
            }
            WallpaperInfo wallpaperInfo = wallpaper.connection.mInfo;
            return wallpaperInfo;
        }
    }

    public int getWallpaperIdForUser(int which, int userId) {
        int userId2 = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, true, "getWallpaperIdForUser", (String) null);
        if (which == 1 || which == 2) {
            SparseArray<WallpaperData> map = which == 2 ? this.mLockWallpaperMap : this.mWallpaperMap;
            synchronized (this.mLock) {
                WallpaperData wallpaper = map.get(userId2);
                if (wallpaper == null) {
                    return -1;
                }
                int i = wallpaper.wallpaperId;
                return i;
            }
        }
        throw new IllegalArgumentException("Must specify exactly one kind of wallpaper");
    }

    public void registerWallpaperColorsCallback(IWallpaperManagerCallback cb, int userId, int displayId) {
        int userId2 = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, true, true, "registerWallpaperColorsCallback", (String) null);
        synchronized (this.mLock) {
            SparseArray<RemoteCallbackList<IWallpaperManagerCallback>> userDisplayColorsChangedListeners = this.mColorsChangedListeners.get(userId2);
            if (userDisplayColorsChangedListeners == null) {
                userDisplayColorsChangedListeners = new SparseArray<>();
                this.mColorsChangedListeners.put(userId2, userDisplayColorsChangedListeners);
            }
            RemoteCallbackList<IWallpaperManagerCallback> displayChangedListeners = userDisplayColorsChangedListeners.get(displayId);
            if (displayChangedListeners == null) {
                displayChangedListeners = new RemoteCallbackList<>();
                userDisplayColorsChangedListeners.put(displayId, displayChangedListeners);
            }
            displayChangedListeners.register(cb);
        }
    }

    public void unregisterWallpaperColorsCallback(IWallpaperManagerCallback cb, int userId, int displayId) {
        RemoteCallbackList<IWallpaperManagerCallback> displayChangedListeners;
        int userId2 = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, true, true, "unregisterWallpaperColorsCallback", (String) null);
        synchronized (this.mLock) {
            SparseArray<RemoteCallbackList<IWallpaperManagerCallback>> userDisplayColorsChangedListeners = this.mColorsChangedListeners.get(userId2);
            if (!(userDisplayColorsChangedListeners == null || (displayChangedListeners = userDisplayColorsChangedListeners.get(displayId)) == null)) {
                displayChangedListeners.unregister(cb);
            }
        }
    }

    public void setInAmbientMode(boolean inAmbientMode, long animationDuration) {
        IWallpaperEngine engine;
        synchronized (this.mLock) {
            this.mInAmbientMode = inAmbientMode;
            WallpaperData data = this.mWallpaperMap.get(this.mCurrentUserId);
            if (data == null || data.connection == null || (data.connection.mInfo != null && !data.connection.mInfo.supportsAmbientMode())) {
                engine = null;
            } else {
                engine = data.connection.getDisplayConnectorOrCreate(0).mEngine;
            }
        }
        if (engine != null) {
            try {
                engine.setInAmbientMode(inAmbientMode, animationDuration);
            } catch (RemoteException e) {
            }
        }
    }

    public boolean setLockWallpaperCallback(IWallpaperManagerCallback cb) {
        checkPermission("android.permission.INTERNAL_SYSTEM_WINDOW");
        synchronized (this.mLock) {
            this.mKeyguardListener = cb;
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0047, code lost:
        if (r0 == false) goto L_0x004c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0049, code lost:
        extractColors(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004c, code lost:
        r1 = r9.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004e, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        r3 = r2.primaryColors;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0051, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0052, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.app.WallpaperColors getWallpaperColors(int r10, int r11, int r12) throws android.os.RemoteException {
        /*
            r9 = this;
            r0 = 1
            r1 = 2
            if (r10 == r1) goto L_0x0010
            if (r10 != r0) goto L_0x0007
            goto L_0x0010
        L_0x0007:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "which should be either FLAG_LOCK or FLAG_SYSTEM"
            r0.<init>(r1)
            throw r0
        L_0x0010:
            int r2 = android.os.Binder.getCallingPid()
            int r3 = android.os.Binder.getCallingUid()
            r5 = 0
            r6 = 1
            r8 = 0
            java.lang.String r7 = "getWallpaperColors"
            r4 = r11
            int r11 = android.app.ActivityManager.handleIncomingUser(r2, r3, r4, r5, r6, r7, r8)
            r2 = 0
            java.lang.Object r3 = r9.mLock
            monitor-enter(r3)
            if (r10 != r1) goto L_0x0034
            android.util.SparseArray<com.android.server.wallpaper.WallpaperManagerService$WallpaperData> r1 = r9.mLockWallpaperMap     // Catch:{ all -> 0x0032 }
            java.lang.Object r1 = r1.get(r11)     // Catch:{ all -> 0x0032 }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = (com.android.server.wallpaper.WallpaperManagerService.WallpaperData) r1     // Catch:{ all -> 0x0032 }
            r2 = r1
            goto L_0x0034
        L_0x0032:
            r0 = move-exception
            goto L_0x0056
        L_0x0034:
            if (r2 != 0) goto L_0x003b
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = r9.findWallpaperAtDisplay(r11, r12)     // Catch:{ all -> 0x0032 }
            r2 = r1
        L_0x003b:
            if (r2 != 0) goto L_0x0040
            r0 = 0
            monitor-exit(r3)     // Catch:{ all -> 0x0032 }
            return r0
        L_0x0040:
            android.app.WallpaperColors r1 = r2.primaryColors     // Catch:{ all -> 0x0032 }
            if (r1 != 0) goto L_0x0045
            goto L_0x0046
        L_0x0045:
            r0 = 0
        L_0x0046:
            monitor-exit(r3)     // Catch:{ all -> 0x0032 }
            if (r0 == 0) goto L_0x004c
            r9.extractColors(r2)
        L_0x004c:
            java.lang.Object r1 = r9.mLock
            monitor-enter(r1)
            android.app.WallpaperColors r3 = r2.primaryColors     // Catch:{ all -> 0x0053 }
            monitor-exit(r1)     // Catch:{ all -> 0x0053 }
            return r3
        L_0x0053:
            r3 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0053 }
            throw r3
        L_0x0056:
            monitor-exit(r3)     // Catch:{ all -> 0x0032 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.getWallpaperColors(int, int, int):android.app.WallpaperColors");
    }

    private WallpaperData findWallpaperAtDisplay(int userId, int displayId) {
        WallpaperData wallpaperData = this.mFallbackWallpaper;
        if (wallpaperData == null || wallpaperData.connection == null || !this.mFallbackWallpaper.connection.containsDisplay(displayId)) {
            return this.mWallpaperMap.get(userId);
        }
        return this.mFallbackWallpaper;
    }

    public WallpaperColors getPartialWallpaperColors(int which, int userId, Rect rectOnScreen) {
        if (which == 2 || which == 1) {
            int userId2 = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, true, "getWallpaperColors", (String) null);
            WallpaperData wallpaperData = null;
            synchronized (this.mLock) {
                if (which == 2) {
                    try {
                        wallpaperData = this.mLockWallpaperMap.get(userId2);
                    } catch (Throwable th) {
                        while (true) {
                            throw th;
                        }
                    }
                }
                if (wallpaperData == null) {
                    wallpaperData = this.mWallpaperMap.get(userId2);
                }
                if (wallpaperData == null) {
                    return null;
                }
                return extractColors(wallpaperData, rectOnScreen);
            }
        }
        throw new IllegalArgumentException("which should be either FLAG_LOCK or FLAG_SYSTEM");
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public ParcelFileDescriptor setWallpaper(String name, String callingPackage, Rect cropHint, boolean allowBackup, Bundle extras, int which, IWallpaperManagerCallback completion, int userId) {
        Rect cropHint2;
        ParcelFileDescriptor pfd;
        int userId2 = ActivityManager.handleIncomingUser(getCallingPid(), getCallingUid(), userId, false, true, "changing wallpaper", (String) null);
        checkPermission("android.permission.SET_WALLPAPER");
        if ((which & 3) == 0) {
            Slog.e(TAG, "Must specify a valid wallpaper category to set");
            throw new IllegalArgumentException("Must specify a valid wallpaper category to set");
        } else if (!isWallpaperSupported(callingPackage) || !isSetWallpaperAllowed(callingPackage)) {
            return null;
        } else {
            if (cropHint == null) {
                cropHint2 = new Rect(0, 0, 0, 0);
            } else if (cropHint.isEmpty() || cropHint.left < 0 || cropHint.top < 0) {
                throw new IllegalArgumentException("Invalid crop rect supplied: " + cropHint);
            } else {
                cropHint2 = cropHint;
            }
            synchronized (this.mLock) {
                if (which == 1) {
                    if (this.mLockWallpaperMap.get(userId2) == null) {
                        migrateSystemToLockWallpaperLocked(userId2);
                    }
                }
                WallpaperData wallpaper = getWallpaperSafeLocked(userId2, which);
                long ident = Binder.clearCallingIdentity();
                try {
                    pfd = updateWallpaperBitmapLocked(name, wallpaper, extras);
                    if (pfd != null) {
                        wallpaper.imageWallpaperPending = true;
                        wallpaper.whichPending = which;
                        wallpaper.setComplete = completion;
                        wallpaper.cropHint.set(cropHint2);
                        wallpaper.allowBackup = allowBackup;
                    }
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
            return pfd;
        }
    }

    private void migrateSystemToLockWallpaperLocked(int userId) {
        WallpaperData sysWP = this.mWallpaperMap.get(userId);
        if (sysWP != null) {
            WallpaperData lockWP = new WallpaperData(userId, WALLPAPER_LOCK_ORIG, WALLPAPER_LOCK_CROP);
            lockWP.wallpaperId = sysWP.wallpaperId;
            lockWP.cropHint.set(sysWP.cropHint);
            lockWP.allowBackup = sysWP.allowBackup;
            lockWP.primaryColors = sysWP.primaryColors;
            try {
                Os.rename(sysWP.wallpaperFile.getAbsolutePath(), lockWP.wallpaperFile.getAbsolutePath());
                Os.rename(sysWP.cropFile.getAbsolutePath(), lockWP.cropFile.getAbsolutePath());
                this.mLockWallpaperMap.put(userId, lockWP);
            } catch (ErrnoException e) {
                Slog.e(TAG, "Can't migrate system wallpaper: " + e.getMessage());
                lockWP.wallpaperFile.delete();
                lockWP.cropFile.delete();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ParcelFileDescriptor updateWallpaperBitmapLocked(String name, WallpaperData wallpaper, Bundle extras) {
        if (name == null) {
            name = "";
        }
        try {
            File dir = getWallpaperDir(wallpaper.userId);
            if (!dir.exists()) {
                dir.mkdir();
                FileUtils.setPermissions(dir.getPath(), 505, -1, -1);
            }
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(wallpaper.wallpaperFile, 1006632960);
            if (!SELinux.restorecon(wallpaper.wallpaperFile)) {
                return null;
            }
            wallpaper.name = name;
            wallpaper.wallpaperId = makeWallpaperIdLocked();
            if (extras != null) {
                extras.putInt("android.service.wallpaper.extra.ID", wallpaper.wallpaperId);
            }
            wallpaper.primaryColors = null;
            return fd;
        } catch (FileNotFoundException e) {
            Slog.w(TAG, "Error setting wallpaper", e);
            return null;
        }
    }

    public void setWallpaperComponentChecked(ComponentName name, String callingPackage, int userId) {
        if (isWallpaperSupported(callingPackage) && isSetWallpaperAllowed(callingPackage)) {
            setWallpaperComponent(name, userId);
        }
    }

    public void setWallpaperComponent(ComponentName name) {
        setWallpaperComponent(name, UserHandle.getCallingUserId());
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    private void setWallpaperComponent(ComponentName name, int userId) {
        WallpaperData wallpaper;
        int userId2 = ActivityManager.handleIncomingUser(getCallingPid(), getCallingUid(), userId, false, true, "changing live wallpaper", (String) null);
        checkPermission("android.permission.SET_WALLPAPER_COMPONENT");
        int which = 1;
        boolean shouldNotifyColors = false;
        synchronized (this.mLock) {
            wallpaper = this.mWallpaperMap.get(userId2);
            if (wallpaper != null) {
                long ident = Binder.clearCallingIdentity();
                if (this.mImageWallpaper.equals(wallpaper.wallpaperComponent) && this.mLockWallpaperMap.get(userId2) == null) {
                    migrateSystemToLockWallpaperLocked(userId2);
                }
                if (this.mLockWallpaperMap.get(userId2) == null) {
                    which = 1 | 2;
                }
                try {
                    wallpaper.imageWallpaperPending = false;
                    boolean same = changingToSame(name, wallpaper);
                    if (bindWallpaperComponentLocked(name, false, true, wallpaper, (IRemoteCallback) null)) {
                        if (!same) {
                            wallpaper.primaryColors = null;
                        }
                        wallpaper.wallpaperId = makeWallpaperIdLocked();
                        notifyCallbacksLocked(wallpaper);
                        shouldNotifyColors = true;
                    }
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalStateException("Wallpaper not yet initialized for user " + userId2);
            }
        }
        if (shouldNotifyColors) {
            notifyWallpaperColorsChanged(wallpaper, which);
            notifyWallpaperColorsChanged(this.mFallbackWallpaper, 1);
        }
    }

    private boolean changingToSame(ComponentName componentName, WallpaperData wallpaper) {
        if (wallpaper.connection == null) {
            return false;
        }
        if (wallpaper.wallpaperComponent == null) {
            if (componentName == null) {
                return true;
            }
            return false;
        } else if (wallpaper.wallpaperComponent.equals(componentName)) {
            return true;
        } else {
            return false;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00e1, code lost:
        r8 = new android.app.WallpaperInfo(r1.mContext, r10.get(r11));
     */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x021e  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0223  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean bindWallpaperComponentLocked(android.content.ComponentName r20, boolean r21, boolean r22, com.android.server.wallpaper.WallpaperManagerService.WallpaperData r23, android.os.IRemoteCallback r24) {
        /*
            r19 = this;
            r1 = r19
            r2 = r20
            r3 = r23
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "bindWallpaperComponentLocked: componentName="
            r0.append(r4)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r4 = "WallpaperManagerService"
            android.util.Slog.v(r4, r0)
            r0 = 1
            if (r21 != 0) goto L_0x0026
            boolean r5 = r1.changingToSame(r2, r3)
            if (r5 == 0) goto L_0x0026
            return r0
        L_0x0026:
            r5 = 0
            if (r2 != 0) goto L_0x003c
            android.content.ComponentName r6 = r1.mDefaultWallpaperComponent     // Catch:{ RemoteException -> 0x0037 }
            r2 = r6
            if (r2 != 0) goto L_0x003c
            android.content.ComponentName r6 = r1.mImageWallpaper     // Catch:{ RemoteException -> 0x0200 }
            r2 = r6
            java.lang.String r6 = "No default component; using image wallpaper"
            android.util.Slog.v(r4, r6)     // Catch:{ RemoteException -> 0x0200 }
            goto L_0x003c
        L_0x0037:
            r0 = move-exception
            r5 = r24
            goto L_0x0203
        L_0x003c:
            int r6 = r3.userId     // Catch:{ RemoteException -> 0x0200 }
            android.content.pm.IPackageManager r7 = r1.mIPackageManager     // Catch:{ RemoteException -> 0x0200 }
            r8 = 4224(0x1080, float:5.919E-42)
            android.content.pm.ServiceInfo r7 = r7.getServiceInfo(r2, r8, r6)     // Catch:{ RemoteException -> 0x0200 }
            if (r7 != 0) goto L_0x0062
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0200 }
            r0.<init>()     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r8 = "Attempted wallpaper "
            r0.append(r8)     // Catch:{ RemoteException -> 0x0200 }
            r0.append(r2)     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r8 = " is unavailable"
            r0.append(r8)     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r0 = r0.toString()     // Catch:{ RemoteException -> 0x0200 }
            android.util.Slog.w(r4, r0)     // Catch:{ RemoteException -> 0x0200 }
            return r5
        L_0x0062:
            java.lang.String r8 = "android.permission.BIND_WALLPAPER"
            java.lang.String r9 = r7.permission     // Catch:{ RemoteException -> 0x0200 }
            boolean r8 = r8.equals(r9)     // Catch:{ RemoteException -> 0x0200 }
            if (r8 != 0) goto L_0x0089
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0200 }
            r0.<init>()     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r8 = "Selected service does not have android.permission.BIND_WALLPAPER: "
            r0.append(r8)     // Catch:{ RemoteException -> 0x0200 }
            r0.append(r2)     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r0 = r0.toString()     // Catch:{ RemoteException -> 0x0200 }
            if (r22 != 0) goto L_0x0083
            android.util.Slog.w(r4, r0)     // Catch:{ RemoteException -> 0x0200 }
            return r5
        L_0x0083:
            java.lang.SecurityException r8 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x0200 }
            r8.<init>(r0)     // Catch:{ RemoteException -> 0x0200 }
            throw r8     // Catch:{ RemoteException -> 0x0200 }
        L_0x0089:
            r8 = 0
            android.content.Intent r9 = new android.content.Intent     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r10 = "android.service.wallpaper.WallpaperService"
            r9.<init>(r10)     // Catch:{ RemoteException -> 0x0200 }
            if (r2 == 0) goto L_0x011f
            android.content.ComponentName r10 = r1.mImageWallpaper     // Catch:{ RemoteException -> 0x0200 }
            boolean r10 = r2.equals(r10)     // Catch:{ RemoteException -> 0x0200 }
            if (r10 != 0) goto L_0x011f
            android.content.pm.IPackageManager r10 = r1.mIPackageManager     // Catch:{ RemoteException -> 0x0200 }
            android.content.Context r11 = r1.mContext     // Catch:{ RemoteException -> 0x0200 }
            android.content.ContentResolver r11 = r11.getContentResolver()     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r11 = r9.resolveTypeIfNeeded(r11)     // Catch:{ RemoteException -> 0x0200 }
            r12 = 128(0x80, float:1.794E-43)
            android.content.pm.ParceledListSlice r10 = r10.queryIntentServices(r9, r11, r12, r6)     // Catch:{ RemoteException -> 0x0200 }
            java.util.List r10 = r10.getList()     // Catch:{ RemoteException -> 0x0200 }
            r11 = r5
        L_0x00b2:
            int r12 = r10.size()     // Catch:{ RemoteException -> 0x0200 }
            if (r11 >= r12) goto L_0x0100
            java.lang.Object r12 = r10.get(r11)     // Catch:{ RemoteException -> 0x0200 }
            android.content.pm.ResolveInfo r12 = (android.content.pm.ResolveInfo) r12     // Catch:{ RemoteException -> 0x0200 }
            android.content.pm.ServiceInfo r12 = r12.serviceInfo     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r13 = r12.name     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r14 = r7.name     // Catch:{ RemoteException -> 0x0200 }
            boolean r13 = r13.equals(r14)     // Catch:{ RemoteException -> 0x0200 }
            if (r13 == 0) goto L_0x00fd
            java.lang.String r13 = r12.packageName     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r14 = r7.packageName     // Catch:{ RemoteException -> 0x0200 }
            boolean r13 = r13.equals(r14)     // Catch:{ RemoteException -> 0x0200 }
            if (r13 == 0) goto L_0x00fd
            android.app.WallpaperInfo r13 = new android.app.WallpaperInfo     // Catch:{ XmlPullParserException -> 0x00f0, IOException -> 0x00e3 }
            android.content.Context r14 = r1.mContext     // Catch:{ XmlPullParserException -> 0x00f0, IOException -> 0x00e3 }
            java.lang.Object r15 = r10.get(r11)     // Catch:{ XmlPullParserException -> 0x00f0, IOException -> 0x00e3 }
            android.content.pm.ResolveInfo r15 = (android.content.pm.ResolveInfo) r15     // Catch:{ XmlPullParserException -> 0x00f0, IOException -> 0x00e3 }
            r13.<init>(r14, r15)     // Catch:{ XmlPullParserException -> 0x00f0, IOException -> 0x00e3 }
            r8 = r13
            goto L_0x0100
        L_0x00e3:
            r0 = move-exception
            if (r22 != 0) goto L_0x00ea
            android.util.Slog.w(r4, r0)     // Catch:{ RemoteException -> 0x0200 }
            return r5
        L_0x00ea:
            java.lang.IllegalArgumentException r13 = new java.lang.IllegalArgumentException     // Catch:{ RemoteException -> 0x0200 }
            r13.<init>(r0)     // Catch:{ RemoteException -> 0x0200 }
            throw r13     // Catch:{ RemoteException -> 0x0200 }
        L_0x00f0:
            r0 = move-exception
            if (r22 != 0) goto L_0x00f7
            android.util.Slog.w(r4, r0)     // Catch:{ RemoteException -> 0x0200 }
            return r5
        L_0x00f7:
            java.lang.IllegalArgumentException r13 = new java.lang.IllegalArgumentException     // Catch:{ RemoteException -> 0x0200 }
            r13.<init>(r0)     // Catch:{ RemoteException -> 0x0200 }
            throw r13     // Catch:{ RemoteException -> 0x0200 }
        L_0x00fd:
            int r11 = r11 + 1
            goto L_0x00b2
        L_0x0100:
            if (r8 != 0) goto L_0x011f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0200 }
            r0.<init>()     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r11 = "Selected service is not a wallpaper: "
            r0.append(r11)     // Catch:{ RemoteException -> 0x0200 }
            r0.append(r2)     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r0 = r0.toString()     // Catch:{ RemoteException -> 0x0200 }
            if (r22 != 0) goto L_0x0119
            android.util.Slog.w(r4, r0)     // Catch:{ RemoteException -> 0x0200 }
            return r5
        L_0x0119:
            java.lang.SecurityException r11 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x0200 }
            r11.<init>(r0)     // Catch:{ RemoteException -> 0x0200 }
            throw r11     // Catch:{ RemoteException -> 0x0200 }
        L_0x011f:
            if (r8 == 0) goto L_0x0152
            boolean r10 = r8.supportsAmbientMode()     // Catch:{ RemoteException -> 0x0200 }
            if (r10 == 0) goto L_0x0152
            android.content.pm.IPackageManager r10 = r1.mIPackageManager     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r11 = "android.permission.AMBIENT_WALLPAPER"
            java.lang.String r12 = r8.getPackageName()     // Catch:{ RemoteException -> 0x0200 }
            int r10 = r10.checkPermission(r11, r12, r6)     // Catch:{ RemoteException -> 0x0200 }
            if (r10 == 0) goto L_0x0152
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0200 }
            r0.<init>()     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r11 = "Selected service does not have android.permission.AMBIENT_WALLPAPER: "
            r0.append(r11)     // Catch:{ RemoteException -> 0x0200 }
            r0.append(r2)     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r0 = r0.toString()     // Catch:{ RemoteException -> 0x0200 }
            if (r22 != 0) goto L_0x014c
            android.util.Slog.w(r4, r0)     // Catch:{ RemoteException -> 0x0200 }
            return r5
        L_0x014c:
            java.lang.SecurityException r11 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x0200 }
            r11.<init>(r0)     // Catch:{ RemoteException -> 0x0200 }
            throw r11     // Catch:{ RemoteException -> 0x0200 }
        L_0x0152:
            android.content.pm.IPackageManager r10 = r1.mIPackageManager     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r11 = r2.getPackageName()     // Catch:{ RemoteException -> 0x0200 }
            r12 = 268435456(0x10000000, float:2.5243549E-29)
            int r13 = r3.userId     // Catch:{ RemoteException -> 0x0200 }
            int r10 = r10.getPackageUid(r11, r12, r13)     // Catch:{ RemoteException -> 0x0200 }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r11 = new com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection     // Catch:{ RemoteException -> 0x0200 }
            r11.<init>(r8, r3, r10)     // Catch:{ RemoteException -> 0x0200 }
            r9.setComponent(r2)     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r12 = "android.intent.extra.client_label"
            r13 = 17041339(0x10407bb, float:2.4250117E-38)
            r9.putExtra(r12, r13)     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r12 = "android.intent.extra.client_intent"
            android.content.Context r13 = r1.mContext     // Catch:{ RemoteException -> 0x0200 }
            r14 = 0
            android.content.Intent r15 = new android.content.Intent     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r0 = "android.intent.action.SET_WALLPAPER"
            r15.<init>(r0)     // Catch:{ RemoteException -> 0x0200 }
            android.content.Context r0 = r1.mContext     // Catch:{ RemoteException -> 0x0200 }
            r5 = 17039680(0x1040140, float:2.4245468E-38)
            java.lang.CharSequence r0 = r0.getText(r5)     // Catch:{ RemoteException -> 0x0200 }
            android.content.Intent r15 = android.content.Intent.createChooser(r15, r0)     // Catch:{ RemoteException -> 0x0200 }
            r16 = 0
            r17 = 0
            android.os.UserHandle r0 = new android.os.UserHandle     // Catch:{ RemoteException -> 0x0200 }
            r0.<init>(r6)     // Catch:{ RemoteException -> 0x0200 }
            r18 = r0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivityAsUser(r13, r14, r15, r16, r17, r18)     // Catch:{ RemoteException -> 0x0200 }
            r9.putExtra(r12, r0)     // Catch:{ RemoteException -> 0x0200 }
            android.content.Context r0 = r1.mContext     // Catch:{ RemoteException -> 0x0200 }
            r5 = 570429441(0x22001001, float:1.7355707E-18)
            android.os.UserHandle r12 = new android.os.UserHandle     // Catch:{ RemoteException -> 0x0200 }
            r12.<init>(r6)     // Catch:{ RemoteException -> 0x0200 }
            boolean r0 = r0.bindServiceAsUser(r9, r11, r5, r12)     // Catch:{ RemoteException -> 0x0200 }
            if (r0 != 0) goto L_0x01c9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0200 }
            r0.<init>()     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r5 = "Unable to bind service: "
            r0.append(r5)     // Catch:{ RemoteException -> 0x0200 }
            r0.append(r2)     // Catch:{ RemoteException -> 0x0200 }
            java.lang.String r0 = r0.toString()     // Catch:{ RemoteException -> 0x0200 }
            if (r22 != 0) goto L_0x01c3
            android.util.Slog.w(r4, r0)     // Catch:{ RemoteException -> 0x0200 }
            r4 = 0
            return r4
        L_0x01c3:
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException     // Catch:{ RemoteException -> 0x0200 }
            r5.<init>(r0)     // Catch:{ RemoteException -> 0x0200 }
            throw r5     // Catch:{ RemoteException -> 0x0200 }
        L_0x01c9:
            int r0 = r3.userId     // Catch:{ RemoteException -> 0x0200 }
            int r5 = r1.mCurrentUserId     // Catch:{ RemoteException -> 0x0200 }
            if (r0 != r5) goto L_0x01e0
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r0 = r1.mLastWallpaper     // Catch:{ RemoteException -> 0x0200 }
            if (r0 == 0) goto L_0x01e0
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r0 = r1.mFallbackWallpaper     // Catch:{ RemoteException -> 0x0200 }
            boolean r0 = r3.equals(r0)     // Catch:{ RemoteException -> 0x0200 }
            if (r0 != 0) goto L_0x01e0
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r0 = r1.mLastWallpaper     // Catch:{ RemoteException -> 0x0200 }
            r1.detachWallpaperLocked(r0)     // Catch:{ RemoteException -> 0x0200 }
        L_0x01e0:
            r3.wallpaperComponent = r2     // Catch:{ RemoteException -> 0x0200 }
            r3.connection = r11     // Catch:{ RemoteException -> 0x0200 }
            r5 = r24
            r11.mReply = r5     // Catch:{ RemoteException -> 0x01fe }
            int r0 = r3.userId     // Catch:{ RemoteException -> 0x01fe }
            int r12 = r1.mCurrentUserId     // Catch:{ RemoteException -> 0x01fe }
            if (r0 != r12) goto L_0x01f8
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r0 = r1.mFallbackWallpaper     // Catch:{ RemoteException -> 0x01fe }
            boolean r0 = r3.equals(r0)     // Catch:{ RemoteException -> 0x01fe }
            if (r0 != 0) goto L_0x01f8
            r1.mLastWallpaper = r3     // Catch:{ RemoteException -> 0x01fe }
        L_0x01f8:
            r19.updateFallbackConnection()     // Catch:{ RemoteException -> 0x01fe }
            r0 = 1
            return r0
        L_0x01fe:
            r0 = move-exception
            goto L_0x0203
        L_0x0200:
            r0 = move-exception
            r5 = r24
        L_0x0203:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Remote exception for "
            r6.append(r7)
            r6.append(r2)
            java.lang.String r7 = "\n"
            r6.append(r7)
            r6.append(r0)
            java.lang.String r6 = r6.toString()
            if (r22 != 0) goto L_0x0223
            android.util.Slog.w(r4, r6)
            r4 = 0
            return r4
        L_0x0223:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            r4.<init>(r6)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.bindWallpaperComponentLocked(android.content.ComponentName, boolean, boolean, com.android.server.wallpaper.WallpaperManagerService$WallpaperData, android.os.IRemoteCallback):boolean");
    }

    private void detachWallpaperLocked(WallpaperData wallpaper) {
        if (wallpaper.connection != null) {
            if (wallpaper.connection.mReply != null) {
                try {
                    wallpaper.connection.mReply.sendResult((Bundle) null);
                } catch (RemoteException e) {
                }
                wallpaper.connection.mReply = null;
            }
            try {
                if (wallpaper.connection.mService != null) {
                    wallpaper.connection.mService.detach();
                }
            } catch (RemoteException e2) {
                Slog.w(TAG, "Failed detaching wallpaper service ", e2);
            }
            this.mContext.unbindService(wallpaper.connection);
            wallpaper.connection.forEachDisplayConnector($$Lambda$havGP5uMdRgWQrLydPeIOu1qDGE.INSTANCE);
            wallpaper.connection.mService = null;
            wallpaper.connection.mDisplayConnector.clear();
            wallpaper.connection = null;
            if (wallpaper == this.mLastWallpaper) {
                this.mLastWallpaper = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public void clearWallpaperComponentLocked(WallpaperData wallpaper) {
        wallpaper.wallpaperComponent = null;
        detachWallpaperLocked(wallpaper);
    }

    /* access modifiers changed from: private */
    public void attachServiceLocked(WallpaperConnection conn, WallpaperData wallpaper) {
        conn.forEachDisplayConnector(new Consumer(wallpaper) {
            private final /* synthetic */ WallpaperManagerService.WallpaperData f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                ((WallpaperManagerService.WallpaperConnection.DisplayConnector) obj).connectLocked(WallpaperManagerService.WallpaperConnection.this, this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyCallbacksLocked(WallpaperData wallpaper) {
        int n = wallpaper.callbacks.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                wallpaper.callbacks.getBroadcastItem(i).onWallpaperChanged();
            } catch (RemoteException e) {
            }
        }
        wallpaper.callbacks.finishBroadcast();
        this.mContext.sendBroadcastAsUser(new Intent("android.intent.action.WALLPAPER_CHANGED"), new UserHandle(this.mCurrentUserId));
    }

    private void checkPermission(String permission) {
        if (this.mContext.checkCallingOrSelfPermission(permission) != 0) {
            throw new SecurityException("Access denied to process: " + Binder.getCallingPid() + ", must have permission " + permission);
        }
    }

    public boolean isWallpaperSupported(String callingPackage) {
        return this.mAppOpsManager.checkOpNoThrow(48, Binder.getCallingUid(), callingPackage) == 0;
    }

    public boolean isSetWallpaperAllowed(String callingPackage) {
        if (!Arrays.asList(this.mContext.getPackageManager().getPackagesForUid(Binder.getCallingUid())).contains(callingPackage)) {
            return false;
        }
        DevicePolicyManager dpm = (DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class);
        if (dpm.isDeviceOwnerApp(callingPackage) || dpm.isProfileOwnerApp(callingPackage)) {
            return true;
        }
        return true ^ ((UserManager) this.mContext.getSystemService("user")).hasUserRestriction("no_set_wallpaper");
    }

    public boolean isWallpaperBackupEligible(int which, int userId) {
        WallpaperData wallpaper;
        if (Binder.getCallingUid() == 1000) {
            if (which == 2) {
                wallpaper = this.mLockWallpaperMap.get(userId);
            } else {
                wallpaper = this.mWallpaperMap.get(userId);
            }
            if (wallpaper != null) {
                return wallpaper.allowBackup;
            }
            return false;
        }
        throw new SecurityException("Only the system may call isWallpaperBackupEligible");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDisplayReadyInternal(int r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = r4.mLastWallpaper     // Catch:{ all -> 0x004d }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            return
        L_0x0009:
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = r4.mLastWallpaper     // Catch:{ all -> 0x004d }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r1 = r1.connection     // Catch:{ all -> 0x004d }
            boolean r1 = r4.supportsMultiDisplay(r1)     // Catch:{ all -> 0x004d }
            if (r1 == 0) goto L_0x002a
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = r4.mLastWallpaper     // Catch:{ all -> 0x004d }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r1 = r1.connection     // Catch:{ all -> 0x004d }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection$DisplayConnector r1 = r1.getDisplayConnectorOrCreate(r5)     // Catch:{ all -> 0x004d }
            if (r1 != 0) goto L_0x001f
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            return
        L_0x001f:
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r2 = r4.mLastWallpaper     // Catch:{ all -> 0x004d }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r2 = r2.connection     // Catch:{ all -> 0x004d }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r3 = r4.mLastWallpaper     // Catch:{ all -> 0x004d }
            r1.connectLocked(r2, r3)     // Catch:{ all -> 0x004d }
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            return
        L_0x002a:
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = r4.mFallbackWallpaper     // Catch:{ all -> 0x004d }
            if (r1 == 0) goto L_0x0044
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r1 = r4.mFallbackWallpaper     // Catch:{ all -> 0x004d }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r1 = r1.connection     // Catch:{ all -> 0x004d }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection$DisplayConnector r1 = r1.getDisplayConnectorOrCreate(r5)     // Catch:{ all -> 0x004d }
            if (r1 != 0) goto L_0x003a
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            return
        L_0x003a:
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r2 = r4.mFallbackWallpaper     // Catch:{ all -> 0x004d }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection r2 = r2.connection     // Catch:{ all -> 0x004d }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r3 = r4.mFallbackWallpaper     // Catch:{ all -> 0x004d }
            r1.connectLocked(r2, r3)     // Catch:{ all -> 0x004d }
            goto L_0x004b
        L_0x0044:
            java.lang.String r1 = "WallpaperManagerService"
            java.lang.String r2 = "No wallpaper can be added to the new display"
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x004d }
        L_0x004b:
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            return
        L_0x004d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.onDisplayReadyInternal(int):void");
    }

    private static JournaledFile makeJournaledFile(int userId) {
        String base = new File(getWallpaperDir(userId), WALLPAPER_INFO).getAbsolutePath();
        File file = new File(base);
        return new JournaledFile(file, new File(base + ".tmp"));
    }

    /* access modifiers changed from: private */
    public void saveSettingsLocked(int userId) {
        JournaledFile journal = makeJournaledFile(userId);
        try {
            XmlSerializer out = new FastXmlSerializer();
            FileOutputStream fstream = new FileOutputStream(journal.chooseForWrite(), false);
            BufferedOutputStream stream = new BufferedOutputStream(fstream);
            out.setOutput(stream, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            WallpaperData wallpaper = this.mWallpaperMap.get(userId);
            if (wallpaper != null) {
                writeWallpaperAttributes(out, "wp", wallpaper);
            }
            WallpaperData wallpaper2 = this.mLockWallpaperMap.get(userId);
            if (wallpaper2 != null) {
                writeWallpaperAttributes(out, "kwp", wallpaper2);
            }
            out.endDocument();
            stream.flush();
            FileUtils.sync(fstream);
            stream.close();
            journal.commit();
        } catch (IOException e) {
            IoUtils.closeQuietly((AutoCloseable) null);
            journal.rollback();
        }
    }

    private void writeWallpaperAttributes(XmlSerializer out, String tag, WallpaperData wallpaper) throws IllegalArgumentException, IllegalStateException, IOException {
        DisplayData wpdData = getDisplayDataOrCreate(0);
        out.startTag((String) null, tag);
        out.attribute((String) null, "id", Integer.toString(wallpaper.wallpaperId));
        out.attribute((String) null, "width", Integer.toString(wpdData.mWidth));
        out.attribute((String) null, "height", Integer.toString(wpdData.mHeight));
        out.attribute((String) null, "cropLeft", Integer.toString(wallpaper.cropHint.left));
        out.attribute((String) null, "cropTop", Integer.toString(wallpaper.cropHint.top));
        out.attribute((String) null, "cropRight", Integer.toString(wallpaper.cropHint.right));
        out.attribute((String) null, "cropBottom", Integer.toString(wallpaper.cropHint.bottom));
        if (wpdData.mPadding.left != 0) {
            out.attribute((String) null, "paddingLeft", Integer.toString(wpdData.mPadding.left));
        }
        if (wpdData.mPadding.top != 0) {
            out.attribute((String) null, "paddingTop", Integer.toString(wpdData.mPadding.top));
        }
        if (wpdData.mPadding.right != 0) {
            out.attribute((String) null, "paddingRight", Integer.toString(wpdData.mPadding.right));
        }
        if (wpdData.mPadding.bottom != 0) {
            out.attribute((String) null, "paddingBottom", Integer.toString(wpdData.mPadding.bottom));
        }
        if (wallpaper.primaryColors != null) {
            int colorsCount = wallpaper.primaryColors.getMainColors().size();
            out.attribute((String) null, "colorsCount", Integer.toString(colorsCount));
            if (colorsCount > 0) {
                for (int i = 0; i < colorsCount; i++) {
                    out.attribute((String) null, "colorValue" + i, Integer.toString(((Color) wallpaper.primaryColors.getMainColors().get(i)).toArgb()));
                }
            }
            out.attribute((String) null, "colorHints", Integer.toString(wallpaper.primaryColors.getColorHints()));
        }
        out.attribute((String) null, Settings.ATTR_NAME, wallpaper.name);
        if (wallpaper.wallpaperComponent != null && !wallpaper.wallpaperComponent.equals(this.mImageWallpaper)) {
            out.attribute((String) null, "component", wallpaper.wallpaperComponent.flattenToShortString());
        }
        if (wallpaper.allowBackup) {
            out.attribute((String) null, BatteryService.HealthServiceWrapper.INSTANCE_HEALTHD, "true");
        }
        out.endTag((String) null, tag);
    }

    private void migrateFromOld() {
        File preNWallpaper = new File(getWallpaperDir(0), WALLPAPER_CROP);
        File originalWallpaper = new File("/data/data/com.android.settings/files/wallpaper");
        File newWallpaper = new File(getWallpaperDir(0), WALLPAPER);
        if (preNWallpaper.exists()) {
            if (!newWallpaper.exists()) {
                FileUtils.copyFile(preNWallpaper, newWallpaper);
            }
        } else if (originalWallpaper.exists()) {
            File oldInfo = new File("/data/system/wallpaper_info.xml");
            if (oldInfo.exists()) {
                oldInfo.renameTo(new File(getWallpaperDir(0), WALLPAPER_INFO));
            }
            FileUtils.copyFile(originalWallpaper, preNWallpaper);
            originalWallpaper.renameTo(newWallpaper);
        }
    }

    private int getAttributeInt(XmlPullParser parser, String name, int defValue) {
        String value = parser.getAttributeValue((String) null, name);
        if (value == null) {
            return defValue;
        }
        return Integer.parseInt(value);
    }

    private WallpaperData getWallpaperSafeLocked(int userId, int which) {
        SparseArray<WallpaperData> whichSet = which == 2 ? this.mLockWallpaperMap : this.mWallpaperMap;
        WallpaperData wallpaper = whichSet.get(userId);
        if (wallpaper != null) {
            return wallpaper;
        }
        loadSettingsLocked(userId, false);
        WallpaperData wallpaper2 = whichSet.get(userId);
        if (wallpaper2 != null) {
            return wallpaper2;
        }
        if (which == 2) {
            WallpaperData wallpaper3 = new WallpaperData(userId, WALLPAPER_LOCK_ORIG, WALLPAPER_LOCK_CROP);
            this.mLockWallpaperMap.put(userId, wallpaper3);
            ensureSaneWallpaperData(wallpaper3, 0);
            return wallpaper3;
        }
        Slog.wtf(TAG, "Didn't find wallpaper in non-lock case!");
        WallpaperData wallpaper4 = new WallpaperData(userId, WALLPAPER, WALLPAPER_CROP);
        this.mWallpaperMap.put(userId, wallpaper4);
        ensureSaneWallpaperData(wallpaper4, 0);
        return wallpaper4;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0237  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x024c  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0267  */
    /* JADX WARNING: Removed duplicated region for block: B:141:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadSettingsLocked(int r20, boolean r21) {
        /*
            r19 = this;
            r1 = r19
            r2 = r20
            java.lang.String r3 = " "
            java.lang.String r4 = "failed parsing "
            com.android.internal.util.JournaledFile r5 = makeJournaledFile(r20)
            r6 = 0
            java.io.File r7 = r5.chooseForRead()
            android.util.SparseArray<com.android.server.wallpaper.WallpaperManagerService$WallpaperData> r0 = r1.mWallpaperMap
            java.lang.Object r0 = r0.get(r2)
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r0 = (com.android.server.wallpaper.WallpaperManagerService.WallpaperData) r0
            r8 = 1
            java.lang.String r9 = "WallpaperManagerService"
            if (r0 != 0) goto L_0x004e
            r19.migrateFromOld()
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r10 = new com.android.server.wallpaper.WallpaperManagerService$WallpaperData
            java.lang.String r11 = "wallpaper_orig"
            java.lang.String r12 = "wallpaper"
            r10.<init>(r2, r11, r12)
            r0 = r10
            r0.allowBackup = r8
            android.util.SparseArray<com.android.server.wallpaper.WallpaperManagerService$WallpaperData> r10 = r1.mWallpaperMap
            r10.put(r2, r0)
            boolean r10 = r0.cropExists()
            if (r10 != 0) goto L_0x0049
            boolean r10 = r0.sourceExists()
            if (r10 == 0) goto L_0x0044
            r1.generateCrop(r0)
            goto L_0x0049
        L_0x0044:
            java.lang.String r10 = "No static wallpaper imagery; defaults will be shown"
            android.util.Slog.i(r9, r10)
        L_0x0049:
            r19.initializeFallbackWallpaper()
            r10 = r0
            goto L_0x004f
        L_0x004e:
            r10 = r0
        L_0x004f:
            r11 = 0
            r12 = 0
            com.android.server.wallpaper.WallpaperManagerService$DisplayData r13 = r1.getDisplayDataOrCreate(r12)
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0226, NullPointerException -> 0x0208, NumberFormatException -> 0x01ea, XmlPullParserException -> 0x01cc, IOException -> 0x01ae, IndexOutOfBoundsException -> 0x018f }
            r0.<init>(r7)     // Catch:{ FileNotFoundException -> 0x0226, NullPointerException -> 0x0208, NumberFormatException -> 0x01ea, XmlPullParserException -> 0x01cc, IOException -> 0x01ae, IndexOutOfBoundsException -> 0x018f }
            r6 = r0
            org.xmlpull.v1.XmlPullParser r0 = android.util.Xml.newPullParser()     // Catch:{ FileNotFoundException -> 0x0186, NullPointerException -> 0x017d, NumberFormatException -> 0x0174, XmlPullParserException -> 0x016c, IOException -> 0x0164, IndexOutOfBoundsException -> 0x015c }
            java.nio.charset.Charset r14 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ FileNotFoundException -> 0x0186, NullPointerException -> 0x017d, NumberFormatException -> 0x0174, XmlPullParserException -> 0x016c, IOException -> 0x0164, IndexOutOfBoundsException -> 0x015c }
            java.lang.String r14 = r14.name()     // Catch:{ FileNotFoundException -> 0x0186, NullPointerException -> 0x017d, NumberFormatException -> 0x0174, XmlPullParserException -> 0x016c, IOException -> 0x0164, IndexOutOfBoundsException -> 0x015c }
            r0.setInput(r6, r14)     // Catch:{ FileNotFoundException -> 0x0186, NullPointerException -> 0x017d, NumberFormatException -> 0x0174, XmlPullParserException -> 0x016c, IOException -> 0x0164, IndexOutOfBoundsException -> 0x015c }
        L_0x0068:
            int r14 = r0.next()     // Catch:{ FileNotFoundException -> 0x0186, NullPointerException -> 0x017d, NumberFormatException -> 0x0174, XmlPullParserException -> 0x016c, IOException -> 0x0164, IndexOutOfBoundsException -> 0x015c }
            r15 = 2
            if (r14 != r15) goto L_0x0146
            java.lang.String r15 = r0.getName()     // Catch:{ FileNotFoundException -> 0x0186, NullPointerException -> 0x017d, NumberFormatException -> 0x0174, XmlPullParserException -> 0x016c, IOException -> 0x0164, IndexOutOfBoundsException -> 0x015c }
            java.lang.String r8 = "wp"
            boolean r8 = r8.equals(r15)     // Catch:{ FileNotFoundException -> 0x0186, NullPointerException -> 0x017d, NumberFormatException -> 0x0174, XmlPullParserException -> 0x016c, IOException -> 0x0164, IndexOutOfBoundsException -> 0x015c }
            if (r8 == 0) goto L_0x00f3
            r8 = r21
            r1.parseWallpaperAttributes(r0, r10, r8)     // Catch:{ FileNotFoundException -> 0x00f0, NullPointerException -> 0x00ed, NumberFormatException -> 0x00ea, XmlPullParserException -> 0x00e7, IOException -> 0x00e4, IndexOutOfBoundsException -> 0x00e1 }
            java.lang.String r12 = "component"
            r16 = r5
            r5 = 0
            java.lang.String r12 = r0.getAttributeValue(r5, r12)     // Catch:{ FileNotFoundException -> 0x00dc, NullPointerException -> 0x00d7, NumberFormatException -> 0x00d2, XmlPullParserException -> 0x00cd, IOException -> 0x00c8, IndexOutOfBoundsException -> 0x00c3 }
            if (r12 == 0) goto L_0x00a3
            android.content.ComponentName r5 = android.content.ComponentName.unflattenFromString(r12)     // Catch:{ FileNotFoundException -> 0x00a0, NullPointerException -> 0x009d, NumberFormatException -> 0x009a, XmlPullParserException -> 0x0097, IOException -> 0x0094, IndexOutOfBoundsException -> 0x0091 }
            goto L_0x00a4
        L_0x0091:
            r0 = move-exception
            goto L_0x0194
        L_0x0094:
            r0 = move-exception
            goto L_0x01b3
        L_0x0097:
            r0 = move-exception
            goto L_0x01d1
        L_0x009a:
            r0 = move-exception
            goto L_0x01ef
        L_0x009d:
            r0 = move-exception
            goto L_0x020d
        L_0x00a0:
            r0 = move-exception
            goto L_0x022b
        L_0x00a3:
        L_0x00a4:
            r10.nextWallpaperComponent = r5     // Catch:{ FileNotFoundException -> 0x00dc, NullPointerException -> 0x00d7, NumberFormatException -> 0x00d2, XmlPullParserException -> 0x00cd, IOException -> 0x00c8, IndexOutOfBoundsException -> 0x00c3 }
            android.content.ComponentName r5 = r10.nextWallpaperComponent     // Catch:{ FileNotFoundException -> 0x00dc, NullPointerException -> 0x00d7, NumberFormatException -> 0x00d2, XmlPullParserException -> 0x00cd, IOException -> 0x00c8, IndexOutOfBoundsException -> 0x00c3 }
            if (r5 == 0) goto L_0x00bb
            java.lang.String r5 = "android"
            r17 = r6
            android.content.ComponentName r6 = r10.nextWallpaperComponent     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            java.lang.String r6 = r6.getPackageName()     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            boolean r5 = r5.equals(r6)     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            if (r5 == 0) goto L_0x0127
            goto L_0x00bd
        L_0x00bb:
            r17 = r6
        L_0x00bd:
            android.content.ComponentName r5 = r1.mImageWallpaper     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            r10.nextWallpaperComponent = r5     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            goto L_0x0127
        L_0x00c3:
            r0 = move-exception
            r17 = r6
            goto L_0x0194
        L_0x00c8:
            r0 = move-exception
            r17 = r6
            goto L_0x01b3
        L_0x00cd:
            r0 = move-exception
            r17 = r6
            goto L_0x01d1
        L_0x00d2:
            r0 = move-exception
            r17 = r6
            goto L_0x01ef
        L_0x00d7:
            r0 = move-exception
            r17 = r6
            goto L_0x020d
        L_0x00dc:
            r0 = move-exception
            r17 = r6
            goto L_0x022b
        L_0x00e1:
            r0 = move-exception
            goto L_0x015f
        L_0x00e4:
            r0 = move-exception
            goto L_0x0167
        L_0x00e7:
            r0 = move-exception
            goto L_0x016f
        L_0x00ea:
            r0 = move-exception
            goto L_0x0177
        L_0x00ed:
            r0 = move-exception
            goto L_0x0180
        L_0x00f0:
            r0 = move-exception
            goto L_0x0189
        L_0x00f3:
            r8 = r21
            r16 = r5
            r17 = r6
            java.lang.String r5 = "kwp"
            boolean r5 = r5.equals(r15)     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            if (r5 == 0) goto L_0x0127
            android.util.SparseArray<com.android.server.wallpaper.WallpaperManagerService$WallpaperData> r5 = r1.mLockWallpaperMap     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            java.lang.Object r5 = r5.get(r2)     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r5 = (com.android.server.wallpaper.WallpaperManagerService.WallpaperData) r5     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            if (r5 != 0) goto L_0x0120
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r6 = new com.android.server.wallpaper.WallpaperManagerService$WallpaperData     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            java.lang.String r12 = "wallpaper_lock_orig"
            r18 = r5
            java.lang.String r5 = "wallpaper_lock"
            r6.<init>(r2, r12, r5)     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            r5 = r6
            android.util.SparseArray<com.android.server.wallpaper.WallpaperManagerService$WallpaperData> r6 = r1.mLockWallpaperMap     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            r6.put(r2, r5)     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            goto L_0x0122
        L_0x0120:
            r18 = r5
        L_0x0122:
            r6 = 0
            r1.parseWallpaperAttributes(r0, r5, r6)     // Catch:{ FileNotFoundException -> 0x0141, NullPointerException -> 0x013c, NumberFormatException -> 0x0137, XmlPullParserException -> 0x0132, IOException -> 0x012d, IndexOutOfBoundsException -> 0x0128 }
            goto L_0x014c
        L_0x0127:
            goto L_0x014c
        L_0x0128:
            r0 = move-exception
            r6 = r17
            goto L_0x0194
        L_0x012d:
            r0 = move-exception
            r6 = r17
            goto L_0x01b3
        L_0x0132:
            r0 = move-exception
            r6 = r17
            goto L_0x01d1
        L_0x0137:
            r0 = move-exception
            r6 = r17
            goto L_0x01ef
        L_0x013c:
            r0 = move-exception
            r6 = r17
            goto L_0x020d
        L_0x0141:
            r0 = move-exception
            r6 = r17
            goto L_0x022b
        L_0x0146:
            r8 = r21
            r16 = r5
            r17 = r6
        L_0x014c:
            r5 = 1
            if (r14 != r5) goto L_0x0154
            r11 = 1
            r6 = r17
            goto L_0x0232
        L_0x0154:
            r8 = r5
            r5 = r16
            r6 = r17
            r12 = 0
            goto L_0x0068
        L_0x015c:
            r0 = move-exception
            r8 = r21
        L_0x015f:
            r16 = r5
            r17 = r6
            goto L_0x0194
        L_0x0164:
            r0 = move-exception
            r8 = r21
        L_0x0167:
            r16 = r5
            r17 = r6
            goto L_0x01b3
        L_0x016c:
            r0 = move-exception
            r8 = r21
        L_0x016f:
            r16 = r5
            r17 = r6
            goto L_0x01d1
        L_0x0174:
            r0 = move-exception
            r8 = r21
        L_0x0177:
            r16 = r5
            r17 = r6
            goto L_0x01ef
        L_0x017d:
            r0 = move-exception
            r8 = r21
        L_0x0180:
            r16 = r5
            r17 = r6
            goto L_0x020d
        L_0x0186:
            r0 = move-exception
            r8 = r21
        L_0x0189:
            r16 = r5
            r17 = r6
            goto L_0x022b
        L_0x018f:
            r0 = move-exception
            r8 = r21
            r16 = r5
        L_0x0194:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            r5.append(r7)
            r5.append(r3)
            r5.append(r0)
            java.lang.String r3 = r5.toString()
            android.util.Slog.w(r9, r3)
            goto L_0x0232
        L_0x01ae:
            r0 = move-exception
            r8 = r21
            r16 = r5
        L_0x01b3:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            r5.append(r7)
            r5.append(r3)
            r5.append(r0)
            java.lang.String r3 = r5.toString()
            android.util.Slog.w(r9, r3)
            goto L_0x0231
        L_0x01cc:
            r0 = move-exception
            r8 = r21
            r16 = r5
        L_0x01d1:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            r5.append(r7)
            r5.append(r3)
            r5.append(r0)
            java.lang.String r3 = r5.toString()
            android.util.Slog.w(r9, r3)
            goto L_0x0231
        L_0x01ea:
            r0 = move-exception
            r8 = r21
            r16 = r5
        L_0x01ef:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            r5.append(r7)
            r5.append(r3)
            r5.append(r0)
            java.lang.String r3 = r5.toString()
            android.util.Slog.w(r9, r3)
            goto L_0x0231
        L_0x0208:
            r0 = move-exception
            r8 = r21
            r16 = r5
        L_0x020d:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            r5.append(r7)
            r5.append(r3)
            r5.append(r0)
            java.lang.String r3 = r5.toString()
            android.util.Slog.w(r9, r3)
            goto L_0x0231
        L_0x0226:
            r0 = move-exception
            r8 = r21
            r16 = r5
        L_0x022b:
            java.lang.String r3 = "no current wallpaper -- first boot?"
            android.util.Slog.w(r9, r3)
        L_0x0231:
        L_0x0232:
            libcore.io.IoUtils.closeQuietly(r6)
            if (r11 != 0) goto L_0x024c
            android.graphics.Rect r0 = r10.cropHint
            r3 = 0
            r0.set(r3, r3, r3, r3)
            android.graphics.Rect r0 = r13.mPadding
            r0.set(r3, r3, r3, r3)
            java.lang.String r0 = ""
            r10.name = r0
            android.util.SparseArray<com.android.server.wallpaper.WallpaperManagerService$WallpaperData> r0 = r1.mLockWallpaperMap
            r0.remove(r2)
            goto L_0x0256
        L_0x024c:
            int r0 = r10.wallpaperId
            if (r0 > 0) goto L_0x0256
            int r0 = r19.makeWallpaperIdLocked()
            r10.wallpaperId = r0
        L_0x0256:
            r3 = 0
            r1.ensureSaneWallpaperDisplaySize(r13, r3)
            r1.ensureSaneWallpaperData(r10, r3)
            android.util.SparseArray<com.android.server.wallpaper.WallpaperManagerService$WallpaperData> r0 = r1.mLockWallpaperMap
            java.lang.Object r0 = r0.get(r2)
            com.android.server.wallpaper.WallpaperManagerService$WallpaperData r0 = (com.android.server.wallpaper.WallpaperManagerService.WallpaperData) r0
            if (r0 == 0) goto L_0x026a
            r1.ensureSaneWallpaperData(r0, r3)
        L_0x026a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.loadSettingsLocked(int, boolean):void");
    }

    private void initializeFallbackWallpaper() {
        if (this.mFallbackWallpaper == null) {
            this.mFallbackWallpaper = new WallpaperData(0, WALLPAPER, WALLPAPER_CROP);
            WallpaperData wallpaperData = this.mFallbackWallpaper;
            wallpaperData.allowBackup = false;
            wallpaperData.wallpaperId = makeWallpaperIdLocked();
            bindWallpaperComponentLocked(this.mImageWallpaper, true, false, this.mFallbackWallpaper, (IRemoteCallback) null);
        }
    }

    private void ensureSaneWallpaperData(WallpaperData wallpaper, int displayId) {
        DisplayData size = getDisplayDataOrCreate(displayId);
        if (displayId != 0) {
            return;
        }
        if (wallpaper.cropHint.width() <= 0 || wallpaper.cropHint.height() <= 0) {
            wallpaper.cropHint.set(0, 0, size.mWidth, size.mHeight);
        }
    }

    private WallpaperColors extractColors(WallpaperData wallpaper, Rect rectOnScreen) {
        boolean imageWallpaper;
        int wallpaperId;
        Bitmap targetBitmap;
        WallpaperData wallpaperData = wallpaper;
        Rect rect = rectOnScreen;
        String cropFile = null;
        synchronized (this.mLock) {
            if (!this.mImageWallpaper.equals(wallpaperData.wallpaperComponent)) {
                if (wallpaperData.wallpaperComponent != null) {
                    imageWallpaper = false;
                    if (imageWallpaper && wallpaperData.cropFile != null && wallpaperData.cropFile.exists()) {
                        cropFile = wallpaperData.cropFile.getAbsolutePath();
                    }
                    wallpaperId = wallpaperData.wallpaperId;
                }
            }
            imageWallpaper = true;
            cropFile = wallpaperData.cropFile.getAbsolutePath();
            wallpaperId = wallpaperData.wallpaperId;
        }
        if (cropFile != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(cropFile);
            if (bitmap != null) {
                int wallpaperWidth = bitmap.getWidth();
                int wallpaperHeight = bitmap.getHeight();
                if (rect == null) {
                    int i = wallpaperId;
                    targetBitmap = bitmap;
                } else {
                    Point screenDisplay = new Point();
                    ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRealSize(screenDisplay);
                    float scaleX = ((float) wallpaperWidth) / ((float) screenDisplay.x);
                    float scaleY = ((float) wallpaperHeight) / ((float) screenDisplay.y);
                    int i2 = wallpaperId;
                    targetBitmap = Bitmap.createBitmap(bitmap, (int) (((float) rect.left) * scaleX), (int) (((float) rect.top) * scaleY), (int) (((float) rectOnScreen.width()) * scaleX), (int) (((float) rectOnScreen.height()) * scaleY));
                    if (targetBitmap != bitmap) {
                        bitmap.recycle();
                    }
                }
                WallpaperColors colors = WallpaperColors.fromBitmap(targetBitmap);
                targetBitmap.recycle();
                return colors;
            }
            return null;
        }
        return null;
    }

    private void parseWallpaperAttributes(XmlPullParser parser, WallpaperData wallpaper, boolean keepDimensionHints) {
        String idString = parser.getAttributeValue((String) null, "id");
        if (idString != null) {
            int id = Integer.parseInt(idString);
            wallpaper.wallpaperId = id;
            if (id > this.mWallpaperId) {
                this.mWallpaperId = id;
            }
        } else {
            wallpaper.wallpaperId = makeWallpaperIdLocked();
        }
        DisplayData wpData = getDisplayDataOrCreate(0);
        if (!keepDimensionHints) {
            wpData.mWidth = Integer.parseInt(parser.getAttributeValue((String) null, "width"));
            wpData.mHeight = Integer.parseInt(parser.getAttributeValue((String) null, "height"));
        }
        wallpaper.cropHint.left = getAttributeInt(parser, "cropLeft", 0);
        wallpaper.cropHint.top = getAttributeInt(parser, "cropTop", 0);
        wallpaper.cropHint.right = getAttributeInt(parser, "cropRight", 0);
        wallpaper.cropHint.bottom = getAttributeInt(parser, "cropBottom", 0);
        wpData.mPadding.left = getAttributeInt(parser, "paddingLeft", 0);
        wpData.mPadding.top = getAttributeInt(parser, "paddingTop", 0);
        wpData.mPadding.right = getAttributeInt(parser, "paddingRight", 0);
        wpData.mPadding.bottom = getAttributeInt(parser, "paddingBottom", 0);
        int colorsCount = getAttributeInt(parser, "colorsCount", 0);
        if (colorsCount > 0) {
            Color primary = null;
            Color secondary = null;
            Color tertiary = null;
            for (int i = 0; i < colorsCount; i++) {
                Color color = Color.valueOf(getAttributeInt(parser, "colorValue" + i, 0));
                if (i != 0) {
                    if (i != 1) {
                        if (i != 2) {
                            break;
                        }
                        tertiary = color;
                    } else {
                        secondary = color;
                    }
                } else {
                    primary = color;
                }
            }
            wallpaper.primaryColors = new WallpaperColors(primary, secondary, tertiary, getAttributeInt(parser, "colorHints", 0));
        }
        wallpaper.name = parser.getAttributeValue((String) null, Settings.ATTR_NAME);
        wallpaper.allowBackup = "true".equals(parser.getAttributeValue((String) null, BatteryService.HealthServiceWrapper.INSTANCE_HEALTHD));
    }

    public void settingsRestored() {
        WallpaperData wallpaper;
        boolean success;
        if (Binder.getCallingUid() == 1000) {
            synchronized (this.mLock) {
                loadSettingsLocked(0, false);
                wallpaper = this.mWallpaperMap.get(0);
                wallpaper.wallpaperId = makeWallpaperIdLocked();
                wallpaper.allowBackup = true;
                if (wallpaper.nextWallpaperComponent == null || wallpaper.nextWallpaperComponent.equals(this.mImageWallpaper)) {
                    if ("".equals(wallpaper.name)) {
                        success = true;
                    } else {
                        success = restoreNamedResourceLocked(wallpaper);
                    }
                    if (success) {
                        generateCrop(wallpaper);
                        bindWallpaperComponentLocked(wallpaper.nextWallpaperComponent, true, false, wallpaper, (IRemoteCallback) null);
                    }
                } else {
                    if (!bindWallpaperComponentLocked(wallpaper.nextWallpaperComponent, false, false, wallpaper, (IRemoteCallback) null)) {
                        bindWallpaperComponentLocked((ComponentName) null, false, false, wallpaper, (IRemoteCallback) null);
                    }
                    success = true;
                }
            }
            if (!success) {
                Slog.e(TAG, "Failed to restore wallpaper: '" + wallpaper.name + "'");
                wallpaper.name = "";
                getWallpaperDir(0).delete();
            }
            synchronized (this.mLock) {
                saveSettingsLocked(0);
            }
            return;
        }
        throw new RuntimeException("settingsRestored() can only be called from the system process");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0131, code lost:
        if (0 != 0) goto L_0x0133;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0133, code lost:
        android.os.FileUtils.sync((java.io.FileOutputStream) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0136, code lost:
        libcore.io.IoUtils.closeQuietly((java.lang.AutoCloseable) null);
        libcore.io.IoUtils.closeQuietly((java.lang.AutoCloseable) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x015b, code lost:
        if (0 != 0) goto L_0x0133;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0181, code lost:
        if (0 != 0) goto L_0x0133;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:?, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean restoreNamedResourceLocked(com.android.server.wallpaper.WallpaperManagerService.WallpaperData r19) {
        /*
            r18 = this;
            r1 = r19
            java.lang.String r2 = "WallpaperManagerService"
            java.lang.String r0 = r1.name
            int r0 = r0.length()
            r3 = 4
            r4 = 0
            if (r0 <= r3) goto L_0x019b
            java.lang.String r0 = r1.name
            java.lang.String r0 = r0.substring(r4, r3)
            java.lang.String r5 = "res:"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x019b
            java.lang.String r0 = r1.name
            java.lang.String r5 = r0.substring(r3)
            r0 = 0
            r6 = 58
            int r6 = r5.indexOf(r6)
            if (r6 <= 0) goto L_0x0032
            java.lang.String r0 = r5.substring(r4, r6)
            r7 = r0
            goto L_0x0033
        L_0x0032:
            r7 = r0
        L_0x0033:
            r0 = 0
            r8 = 47
            int r8 = r5.lastIndexOf(r8)
            if (r8 <= 0) goto L_0x0044
            int r9 = r8 + 1
            java.lang.String r0 = r5.substring(r9)
            r9 = r0
            goto L_0x0045
        L_0x0044:
            r9 = r0
        L_0x0045:
            r0 = 0
            r10 = 1
            if (r6 <= 0) goto L_0x0057
            if (r8 <= 0) goto L_0x0057
            int r11 = r8 - r6
            if (r11 <= r10) goto L_0x0057
            int r11 = r6 + 1
            java.lang.String r0 = r5.substring(r11, r8)
            r11 = r0
            goto L_0x0058
        L_0x0057:
            r11 = r0
        L_0x0058:
            if (r7 == 0) goto L_0x0198
            if (r9 == 0) goto L_0x0198
            if (r11 == 0) goto L_0x0198
            r12 = -1
            r13 = 0
            r14 = 0
            r15 = 0
            r10 = r18
            android.content.Context r0 = r10.mContext     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            android.content.Context r0 = r0.createPackageContext(r7, r3)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            android.content.res.Resources r3 = r0.getResources()     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r4 = 0
            int r4 = r3.getIdentifier(r5, r4, r4)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r12 = r4
            if (r12 != 0) goto L_0x00b2
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r4.<init>()     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r16 = r0
            java.lang.String r0 = "couldn't resolve identifier pkg="
            r4.append(r0)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r4.append(r7)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            java.lang.String r0 = " type="
            r4.append(r0)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r4.append(r11)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            java.lang.String r0 = " ident="
            r4.append(r0)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r4.append(r9)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            java.lang.String r0 = r4.toString()     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            android.util.Slog.e(r2, r0)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            libcore.io.IoUtils.closeQuietly(r13)
            if (r14 == 0) goto L_0x00a5
            android.os.FileUtils.sync(r14)
        L_0x00a5:
            if (r15 == 0) goto L_0x00aa
            android.os.FileUtils.sync(r15)
        L_0x00aa:
            libcore.io.IoUtils.closeQuietly(r14)
            libcore.io.IoUtils.closeQuietly(r15)
            r2 = 0
            return r2
        L_0x00b2:
            r16 = r0
            java.io.InputStream r0 = r3.openRawResource(r12)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r13 = r0
            java.io.File r0 = r1.wallpaperFile     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            boolean r0 = r0.exists()     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            if (r0 == 0) goto L_0x00cb
            java.io.File r0 = r1.wallpaperFile     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r0.delete()     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            java.io.File r0 = r1.cropFile     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r0.delete()     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
        L_0x00cb:
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            java.io.File r4 = r1.wallpaperFile     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r0.<init>(r4)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r14 = r0
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            java.io.File r4 = r1.cropFile     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r0.<init>(r4)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r15 = r0
            r0 = 32768(0x8000, float:4.5918E-41)
            byte[] r0 = new byte[r0]     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
        L_0x00e0:
            int r4 = r13.read(r0)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r17 = r4
            if (r4 <= 0) goto L_0x00f4
            r4 = r17
            r1 = 0
            r14.write(r0, r1, r4)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r15.write(r0, r1, r4)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r1 = r19
            goto L_0x00e0
        L_0x00f4:
            r4 = r17
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r1.<init>()     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r17 = r0
            java.lang.String r0 = "Restored wallpaper: "
            r1.append(r0)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            r1.append(r5)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            java.lang.String r0 = r1.toString()     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            android.util.Slog.v(r2, r0)     // Catch:{ NameNotFoundException -> 0x015e, NotFoundException -> 0x013d, IOException -> 0x0122 }
            libcore.io.IoUtils.closeQuietly(r13)
            android.os.FileUtils.sync(r14)
            android.os.FileUtils.sync(r15)
            libcore.io.IoUtils.closeQuietly(r14)
            libcore.io.IoUtils.closeQuietly(r15)
            r0 = 1
            return r0
        L_0x0120:
            r0 = move-exception
            goto L_0x0184
        L_0x0122:
            r0 = move-exception
            java.lang.String r1 = "IOException while restoring wallpaper "
            android.util.Slog.e(r2, r1, r0)     // Catch:{ all -> 0x0120 }
            libcore.io.IoUtils.closeQuietly(r13)
            if (r14 == 0) goto L_0x0131
            android.os.FileUtils.sync(r14)
        L_0x0131:
            if (r15 == 0) goto L_0x0136
        L_0x0133:
            android.os.FileUtils.sync(r15)
        L_0x0136:
            libcore.io.IoUtils.closeQuietly(r14)
            libcore.io.IoUtils.closeQuietly(r15)
            goto L_0x019d
        L_0x013d:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0120 }
            r1.<init>()     // Catch:{ all -> 0x0120 }
            java.lang.String r3 = "Resource not found: "
            r1.append(r3)     // Catch:{ all -> 0x0120 }
            r1.append(r12)     // Catch:{ all -> 0x0120 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0120 }
            android.util.Slog.e(r2, r1)     // Catch:{ all -> 0x0120 }
            libcore.io.IoUtils.closeQuietly(r13)
            if (r14 == 0) goto L_0x015b
            android.os.FileUtils.sync(r14)
        L_0x015b:
            if (r15 == 0) goto L_0x0136
            goto L_0x0133
        L_0x015e:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0120 }
            r1.<init>()     // Catch:{ all -> 0x0120 }
            java.lang.String r3 = "Package name "
            r1.append(r3)     // Catch:{ all -> 0x0120 }
            r1.append(r7)     // Catch:{ all -> 0x0120 }
            java.lang.String r3 = " not found"
            r1.append(r3)     // Catch:{ all -> 0x0120 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0120 }
            android.util.Slog.e(r2, r1)     // Catch:{ all -> 0x0120 }
            libcore.io.IoUtils.closeQuietly(r13)
            if (r14 == 0) goto L_0x0181
            android.os.FileUtils.sync(r14)
        L_0x0181:
            if (r15 == 0) goto L_0x0136
            goto L_0x0133
        L_0x0184:
            libcore.io.IoUtils.closeQuietly(r13)
            if (r14 == 0) goto L_0x018c
            android.os.FileUtils.sync(r14)
        L_0x018c:
            if (r15 == 0) goto L_0x0191
            android.os.FileUtils.sync(r15)
        L_0x0191:
            libcore.io.IoUtils.closeQuietly(r14)
            libcore.io.IoUtils.closeQuietly(r15)
            throw r0
        L_0x0198:
            r10 = r18
            goto L_0x019d
        L_0x019b:
            r10 = r18
        L_0x019d:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wallpaper.WallpaperManagerService.restoreNamedResourceLocked(com.android.server.wallpaper.WallpaperManagerService$WallpaperData):boolean");
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            synchronized (this.mLock) {
                pw.println("System wallpaper state:");
                for (int i = 0; i < this.mWallpaperMap.size(); i++) {
                    WallpaperData wallpaper = this.mWallpaperMap.valueAt(i);
                    pw.print(" User ");
                    pw.print(wallpaper.userId);
                    pw.print(": id=");
                    pw.println(wallpaper.wallpaperId);
                    pw.println(" Display state:");
                    forEachDisplayData(new Consumer(pw) {
                        private final /* synthetic */ PrintWriter f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void accept(Object obj) {
                            WallpaperManagerService.lambda$dump$6(this.f$0, (WallpaperManagerService.DisplayData) obj);
                        }
                    });
                    pw.print("  mCropHint=");
                    pw.println(wallpaper.cropHint);
                    pw.print("  mName=");
                    pw.println(wallpaper.name);
                    pw.print("  mAllowBackup=");
                    pw.println(wallpaper.allowBackup);
                    pw.print("  mWallpaperComponent=");
                    pw.println(wallpaper.wallpaperComponent);
                    if (wallpaper.connection != null) {
                        WallpaperConnection conn = wallpaper.connection;
                        pw.print("  Wallpaper connection ");
                        pw.print(conn);
                        pw.println(":");
                        if (conn.mInfo != null) {
                            pw.print("    mInfo.component=");
                            pw.println(conn.mInfo.getComponent());
                        }
                        conn.forEachDisplayConnector(new Consumer(pw) {
                            private final /* synthetic */ PrintWriter f$0;

                            {
                                this.f$0 = r1;
                            }

                            public final void accept(Object obj) {
                                WallpaperManagerService.lambda$dump$7(this.f$0, (WallpaperManagerService.WallpaperConnection.DisplayConnector) obj);
                            }
                        });
                        pw.print("    mService=");
                        pw.println(conn.mService);
                        pw.print("    mLastDiedTime=");
                        pw.println(wallpaper.lastDiedTime - SystemClock.uptimeMillis());
                    }
                }
                pw.println("Lock wallpaper state:");
                for (int i2 = 0; i2 < this.mLockWallpaperMap.size(); i2++) {
                    WallpaperData wallpaper2 = this.mLockWallpaperMap.valueAt(i2);
                    pw.print(" User ");
                    pw.print(wallpaper2.userId);
                    pw.print(": id=");
                    pw.println(wallpaper2.wallpaperId);
                    pw.print("  mCropHint=");
                    pw.println(wallpaper2.cropHint);
                    pw.print("  mName=");
                    pw.println(wallpaper2.name);
                    pw.print("  mAllowBackup=");
                    pw.println(wallpaper2.allowBackup);
                }
                pw.println("Fallback wallpaper state:");
                pw.print(" User ");
                pw.print(this.mFallbackWallpaper.userId);
                pw.print(": id=");
                pw.println(this.mFallbackWallpaper.wallpaperId);
                pw.print("  mCropHint=");
                pw.println(this.mFallbackWallpaper.cropHint);
                pw.print("  mName=");
                pw.println(this.mFallbackWallpaper.name);
                pw.print("  mAllowBackup=");
                pw.println(this.mFallbackWallpaper.allowBackup);
                if (this.mFallbackWallpaper.connection != null) {
                    WallpaperConnection conn2 = this.mFallbackWallpaper.connection;
                    pw.print("  Fallback Wallpaper connection ");
                    pw.print(conn2);
                    pw.println(":");
                    if (conn2.mInfo != null) {
                        pw.print("    mInfo.component=");
                        pw.println(conn2.mInfo.getComponent());
                    }
                    conn2.forEachDisplayConnector(new Consumer(pw) {
                        private final /* synthetic */ PrintWriter f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void accept(Object obj) {
                            WallpaperManagerService.lambda$dump$8(this.f$0, (WallpaperManagerService.WallpaperConnection.DisplayConnector) obj);
                        }
                    });
                    pw.print("    mService=");
                    pw.println(conn2.mService);
                    pw.print("    mLastDiedTime=");
                    pw.println(this.mFallbackWallpaper.lastDiedTime - SystemClock.uptimeMillis());
                }
            }
        }
    }

    static /* synthetic */ void lambda$dump$6(PrintWriter pw, DisplayData wpSize) {
        pw.print("  displayId=");
        pw.println(wpSize.mDisplayId);
        pw.print("  mWidth=");
        pw.print(wpSize.mWidth);
        pw.print("  mHeight=");
        pw.println(wpSize.mHeight);
        pw.print("  mPadding=");
        pw.println(wpSize.mPadding);
    }

    static /* synthetic */ void lambda$dump$7(PrintWriter pw, WallpaperConnection.DisplayConnector connector) {
        pw.print("     mDisplayId=");
        pw.println(connector.mDisplayId);
        pw.print("     mToken=");
        pw.println(connector.mToken);
        pw.print("     mEngine=");
        pw.println(connector.mEngine);
    }

    static /* synthetic */ void lambda$dump$8(PrintWriter pw, WallpaperConnection.DisplayConnector connector) {
        pw.print("     mDisplayId=");
        pw.println(connector.mDisplayId);
        pw.print("     mToken=");
        pw.println(connector.mToken);
        pw.print("     mEngine=");
        pw.println(connector.mEngine);
    }
}

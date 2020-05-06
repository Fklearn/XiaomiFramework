package com.android.server.pm;

import android.accounts.IAccountManager;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.role.IRoleManager;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageInstaller;
import android.content.pm.IPackageManager;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.PackageParser;
import android.content.pm.ParceledListSlice;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.pm.VersionedPackage;
import android.content.pm.dex.DexMetadataHelper;
import android.content.pm.dex.ISnapshotRuntimeProfileCallback;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.rollback.IRollbackManager;
import android.content.rollback.PackageRollbackInfo;
import android.content.rollback.RollbackInfo;
import android.net.Uri;
import android.net.util.NetworkConstants;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IUserManager;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.server.am.SplitScreenReporter;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.PrintWriterPrinter;
import com.android.commands.pm.PmInjector;
import com.android.internal.content.PackageHelper;
import com.android.internal.util.ArrayUtils;
import com.android.server.BatteryService;
import com.android.server.LocalServices;
import com.android.server.SystemConfig;
import com.android.server.UiModeManagerService;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.lights.view.MusicView;
import com.android.server.notification.NotificationShellCmd;
import com.android.server.wm.ActivityTaskManagerService;
import com.android.server.wm.WindowManagerService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import libcore.io.IoUtils;
import libcore.io.Streams;

class PackageManagerShellCommand extends ShellCommand {
    private static final String ART_PROFILE_SNAPSHOT_DEBUG_LOCATION = "/data/misc/profman/";
    private static final String STDIN_PATH = "-";
    boolean mBrief;
    boolean mComponents;
    final IPackageManager mInterface;
    int mQueryFlags;
    private final WeakHashMap<String, Resources> mResourceCache = new WeakHashMap<>();
    int mTargetUser;

    PackageManagerShellCommand(PackageManagerService service) {
        this.mInterface = service;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r9) {
        /*
            r8 = this;
            if (r9 != 0) goto L_0x0007
            int r0 = r8.handleDefaultCommands(r9)
            return r0
        L_0x0007:
            java.io.PrintWriter r0 = r8.getOutPrintWriter()
            r1 = -1
            int r2 = r9.hashCode()     // Catch:{ RemoteException -> 0x0485 }
            r3 = 4
            r4 = 3
            r5 = 2
            r6 = 1
            r7 = 0
            switch(r2) {
                case -2102802879: goto L_0x030a;
                case -1967190973: goto L_0x02fe;
                case -1937348290: goto L_0x02f3;
                case -1852006340: goto L_0x02e7;
                case -1846646502: goto L_0x02dc;
                case -1741208611: goto L_0x02d0;
                case -1347307837: goto L_0x02c5;
                case -1298848381: goto L_0x02ba;
                case -1267782244: goto L_0x02af;
                case -1231004208: goto L_0x02a3;
                case -1102348235: goto L_0x0297;
                case -1091400553: goto L_0x028b;
                case -1070704814: goto L_0x027f;
                case -1032029296: goto L_0x0273;
                case -934343034: goto L_0x0266;
                case -919935069: goto L_0x025a;
                case -840566949: goto L_0x024d;
                case -625596190: goto L_0x0240;
                case -623224643: goto L_0x0234;
                case -539710980: goto L_0x0228;
                case -458695741: goto L_0x021c;
                case -444750796: goto L_0x0210;
                case -440994401: goto L_0x0204;
                case -416698598: goto L_0x01f8;
                case -339687564: goto L_0x01eb;
                case -220055275: goto L_0x01de;
                case -140205181: goto L_0x01d1;
                case -132384343: goto L_0x01c4;
                case -129863314: goto L_0x01b7;
                case -115000827: goto L_0x01ab;
                case -87258188: goto L_0x019e;
                case 3095028: goto L_0x0193;
                case 3202370: goto L_0x0186;
                case 3322014: goto L_0x017a;
                case 3433509: goto L_0x016e;
                case 18936394: goto L_0x0161;
                case 86600360: goto L_0x0155;
                case 94746189: goto L_0x0149;
                case 98615580: goto L_0x013d;
                case 107262333: goto L_0x0130;
                case 139892533: goto L_0x0124;
                case 237392952: goto L_0x0117;
                case 287820022: goto L_0x010a;
                case 359572742: goto L_0x00fd;
                case 377019320: goto L_0x00f0;
                case 467549856: goto L_0x00e3;
                case 798023112: goto L_0x00d6;
                case 826473335: goto L_0x00c9;
                case 925176533: goto L_0x00bc;
                case 925767985: goto L_0x00af;
                case 950491699: goto L_0x00a3;
                case 1053409810: goto L_0x0097;
                case 1124603675: goto L_0x008b;
                case 1177857340: goto L_0x007e;
                case 1429366290: goto L_0x0071;
                case 1538306349: goto L_0x0064;
                case 1671308008: goto L_0x0058;
                case 1697997009: goto L_0x004c;
                case 1746695602: goto L_0x003f;
                case 1783979817: goto L_0x0032;
                case 1858863089: goto L_0x0026;
                case 1957569947: goto L_0x001a;
                default: goto L_0x0018;
            }     // Catch:{ RemoteException -> 0x0485 }
        L_0x0018:
            goto L_0x0316
        L_0x001a:
            java.lang.String r2 = "install"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 7
            goto L_0x0317
        L_0x0026:
            java.lang.String r2 = "get-moduleinfo"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 61
            goto L_0x0317
        L_0x0032:
            java.lang.String r2 = "reconcile-secondary-dex-files"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 21
            goto L_0x0317
        L_0x003f:
            java.lang.String r2 = "set-install-location"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 15
            goto L_0x0317
        L_0x004c:
            java.lang.String r2 = "disable-until-used"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 31
            goto L_0x0317
        L_0x0058:
            java.lang.String r2 = "disable"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 29
            goto L_0x0317
        L_0x0064:
            java.lang.String r2 = "install-write"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 13
            goto L_0x0317
        L_0x0071:
            java.lang.String r2 = "set-home-activity"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 52
            goto L_0x0317
        L_0x007e:
            java.lang.String r2 = "trim-caches"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 46
            goto L_0x0317
        L_0x008b:
            java.lang.String r2 = "force-dex-opt"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 22
            goto L_0x0317
        L_0x0097:
            java.lang.String r2 = "query-activities"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = r3
            goto L_0x0317
        L_0x00a3:
            java.lang.String r2 = "compile"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 20
            goto L_0x0317
        L_0x00af:
            java.lang.String r2 = "set-app-link"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 44
            goto L_0x0317
        L_0x00bc:
            java.lang.String r2 = "set-user-restriction"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 49
            goto L_0x0317
        L_0x00c9:
            java.lang.String r2 = "uninstall-system-updates"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 59
            goto L_0x0317
        L_0x00d6:
            java.lang.String r2 = "install-destroy"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 9
            goto L_0x0317
        L_0x00e3:
            java.lang.String r2 = "snapshot-profile"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 25
            goto L_0x0317
        L_0x00f0:
            java.lang.String r2 = "rollback-app"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 60
            goto L_0x0317
        L_0x00fd:
            java.lang.String r2 = "reset-permissions"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 39
            goto L_0x0317
        L_0x010a:
            java.lang.String r2 = "install-remove"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 12
            goto L_0x0317
        L_0x0117:
            java.lang.String r2 = "install-add-session"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 17
            goto L_0x0317
        L_0x0124:
            java.lang.String r2 = "get-harmful-app-warning"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 57
            goto L_0x0317
        L_0x0130:
            java.lang.String r2 = "install-existing"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 14
            goto L_0x0317
        L_0x013d:
            java.lang.String r2 = "grant"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 37
            goto L_0x0317
        L_0x0149:
            java.lang.String r2 = "clear"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 27
            goto L_0x0317
        L_0x0155:
            java.lang.String r2 = "get-max-users"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 50
            goto L_0x0317
        L_0x0161:
            java.lang.String r2 = "move-package"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 18
            goto L_0x0317
        L_0x016e:
            java.lang.String r2 = "path"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = r7
            goto L_0x0317
        L_0x017a:
            java.lang.String r2 = "list"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = r5
            goto L_0x0317
        L_0x0186:
            java.lang.String r2 = "hide"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 33
            goto L_0x0317
        L_0x0193:
            java.lang.String r2 = "dump"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = r6
            goto L_0x0317
        L_0x019e:
            java.lang.String r2 = "move-primary-storage"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 19
            goto L_0x0317
        L_0x01ab:
            java.lang.String r2 = "default-state"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 32
            goto L_0x0317
        L_0x01b7:
            java.lang.String r2 = "install-create"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 11
            goto L_0x0317
        L_0x01c4:
            java.lang.String r2 = "install-commit"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 10
            goto L_0x0317
        L_0x01d1:
            java.lang.String r2 = "unsuspend"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 36
            goto L_0x0317
        L_0x01de:
            java.lang.String r2 = "set-permission-enforced"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 40
            goto L_0x0317
        L_0x01eb:
            java.lang.String r2 = "remove-user"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 48
            goto L_0x0317
        L_0x01f8:
            java.lang.String r2 = "get-stagedsessions"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 58
            goto L_0x0317
        L_0x0204:
            java.lang.String r2 = "query-receivers"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 6
            goto L_0x0317
        L_0x0210:
            java.lang.String r2 = "bg-dexopt-job"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 23
            goto L_0x0317
        L_0x021c:
            java.lang.String r2 = "query-services"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 5
            goto L_0x0317
        L_0x0228:
            java.lang.String r2 = "create-user"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 47
            goto L_0x0317
        L_0x0234:
            java.lang.String r2 = "get-app-link"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 45
            goto L_0x0317
        L_0x0240:
            java.lang.String r2 = "uninstall"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 26
            goto L_0x0317
        L_0x024d:
            java.lang.String r2 = "unhide"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 34
            goto L_0x0317
        L_0x025a:
            java.lang.String r2 = "dump-profiles"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 24
            goto L_0x0317
        L_0x0266:
            java.lang.String r2 = "revoke"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 38
            goto L_0x0317
        L_0x0273:
            java.lang.String r2 = "disable-user"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 30
            goto L_0x0317
        L_0x027f:
            java.lang.String r2 = "get-privapp-permissions"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 41
            goto L_0x0317
        L_0x028b:
            java.lang.String r2 = "get-oem-permissions"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 43
            goto L_0x0317
        L_0x0297:
            java.lang.String r2 = "get-privapp-deny-permissions"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 42
            goto L_0x0317
        L_0x02a3:
            java.lang.String r2 = "resolve-activity"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = r4
            goto L_0x0317
        L_0x02af:
            java.lang.String r2 = "get-instantapp-resolver"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 54
            goto L_0x0317
        L_0x02ba:
            java.lang.String r2 = "enable"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 28
            goto L_0x0317
        L_0x02c5:
            java.lang.String r2 = "has-feature"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 55
            goto L_0x0317
        L_0x02d0:
            java.lang.String r2 = "set-installer"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 53
            goto L_0x0317
        L_0x02dc:
            java.lang.String r2 = "get-max-running-users"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 51
            goto L_0x0317
        L_0x02e7:
            java.lang.String r2 = "suspend"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 35
            goto L_0x0317
        L_0x02f3:
            java.lang.String r2 = "get-install-location"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 16
            goto L_0x0317
        L_0x02fe:
            java.lang.String r2 = "install-abandon"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 8
            goto L_0x0317
        L_0x030a:
            java.lang.String r2 = "set-harmful-app-warning"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x0485 }
            if (r2 == 0) goto L_0x0018
            r2 = 56
            goto L_0x0317
        L_0x0316:
            r2 = r1
        L_0x0317:
            switch(r2) {
                case 0: goto L_0x044c;
                case 1: goto L_0x0447;
                case 2: goto L_0x0442;
                case 3: goto L_0x043d;
                case 4: goto L_0x0438;
                case 5: goto L_0x0433;
                case 6: goto L_0x042e;
                case 7: goto L_0x0429;
                case 8: goto L_0x0424;
                case 9: goto L_0x0424;
                case 10: goto L_0x041f;
                case 11: goto L_0x041a;
                case 12: goto L_0x0415;
                case 13: goto L_0x0410;
                case 14: goto L_0x040b;
                case 15: goto L_0x0406;
                case 16: goto L_0x0401;
                case 17: goto L_0x03fc;
                case 18: goto L_0x03f7;
                case 19: goto L_0x03f2;
                case 20: goto L_0x03ed;
                case 21: goto L_0x03e8;
                case 22: goto L_0x03e3;
                case 23: goto L_0x03de;
                case 24: goto L_0x03d9;
                case 25: goto L_0x03d4;
                case 26: goto L_0x03cf;
                case 27: goto L_0x03ca;
                case 28: goto L_0x03c5;
                case 29: goto L_0x03c0;
                case 30: goto L_0x03bb;
                case 31: goto L_0x03b6;
                case 32: goto L_0x03b1;
                case 33: goto L_0x03ac;
                case 34: goto L_0x03a7;
                case 35: goto L_0x03a2;
                case 36: goto L_0x039d;
                case 37: goto L_0x0398;
                case 38: goto L_0x0393;
                case 39: goto L_0x038e;
                case 40: goto L_0x0389;
                case 41: goto L_0x0384;
                case 42: goto L_0x037f;
                case 43: goto L_0x037a;
                case 44: goto L_0x0375;
                case 45: goto L_0x0370;
                case 46: goto L_0x036b;
                case 47: goto L_0x0366;
                case 48: goto L_0x0361;
                case 49: goto L_0x035c;
                case 50: goto L_0x0357;
                case 51: goto L_0x0352;
                case 52: goto L_0x034d;
                case 53: goto L_0x0348;
                case 54: goto L_0x0343;
                case 55: goto L_0x033e;
                case 56: goto L_0x0339;
                case 57: goto L_0x0334;
                case 58: goto L_0x032f;
                case 59: goto L_0x032a;
                case 60: goto L_0x0325;
                case 61: goto L_0x0320;
                default: goto L_0x031a;
            }     // Catch:{ RemoteException -> 0x0485 }
        L_0x031a:
            java.lang.String r2 = r8.getNextArg()     // Catch:{ RemoteException -> 0x0485 }
            goto L_0x0451
        L_0x0320:
            int r1 = r8.runGetModuleInfo()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0325:
            int r1 = r8.runRollbackApp()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x032a:
            int r1 = r8.uninstallSystemUpdates()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x032f:
            int r1 = r8.getStagedSessions()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0334:
            int r1 = r8.runGetHarmfulAppWarning()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0339:
            int r1 = r8.runSetHarmfulAppWarning()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x033e:
            int r1 = r8.runHasFeature()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0343:
            int r1 = r8.runGetInstantAppResolver()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0348:
            int r1 = r8.runSetInstaller()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x034d:
            int r1 = r8.runSetHomeActivity()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0352:
            int r1 = r8.runGetMaxRunningUsers()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0357:
            int r1 = r8.runGetMaxUsers()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x035c:
            int r1 = r8.runSetUserRestriction()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0361:
            int r1 = r8.runRemoveUser()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0366:
            int r1 = r8.runCreateUser()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x036b:
            int r1 = r8.runTrimCaches()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0370:
            int r1 = r8.runGetAppLink()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0375:
            int r1 = r8.runSetAppLink()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x037a:
            int r1 = r8.runGetOemPermissions()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x037f:
            int r1 = r8.runGetPrivappDenyPermissions()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0384:
            int r1 = r8.runGetPrivappPermissions()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0389:
            int r1 = r8.runSetPermissionEnforced()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x038e:
            int r1 = r8.runResetPermissions()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0393:
            int r1 = r8.runGrantRevokePermission(r7)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0398:
            int r1 = r8.runGrantRevokePermission(r6)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x039d:
            int r1 = r8.runSuspend(r7)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03a2:
            int r1 = r8.runSuspend(r6)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03a7:
            int r1 = r8.runSetHiddenSetting(r7)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03ac:
            int r1 = r8.runSetHiddenSetting(r6)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03b1:
            int r1 = r8.runSetEnabledSetting(r7)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03b6:
            int r1 = r8.runSetEnabledSetting(r3)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03bb:
            int r1 = r8.runSetEnabledSetting(r4)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03c0:
            int r1 = r8.runSetEnabledSetting(r5)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03c5:
            int r1 = r8.runSetEnabledSetting(r6)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03ca:
            int r1 = r8.runClear()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03cf:
            int r1 = r8.runUninstall()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03d4:
            int r1 = r8.runSnapshotProfile()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03d9:
            int r1 = r8.runDumpProfiles()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03de:
            int r1 = r8.runDexoptJob()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03e3:
            int r1 = r8.runForceDexOpt()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03e8:
            int r1 = r8.runreconcileSecondaryDexFiles()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03ed:
            int r1 = r8.runCompile()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03f2:
            int r1 = r8.runMovePrimaryStorage()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03f7:
            int r1 = r8.runMovePackage()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x03fc:
            int r1 = r8.runInstallAddSession()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0401:
            int r1 = r8.runGetInstallLocation()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0406:
            int r1 = r8.runSetInstallLocation()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x040b:
            int r1 = r8.runInstallExisting()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0410:
            int r1 = r8.runInstallWrite()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0415:
            int r1 = r8.runInstallRemove()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x041a:
            int r1 = r8.runInstallCreate()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x041f:
            int r1 = r8.runInstallCommit()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0424:
            int r1 = r8.runInstallAbandon()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0429:
            int r1 = r8.runInstall()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x042e:
            int r1 = r8.runQueryIntentReceivers()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0433:
            int r1 = r8.runQueryIntentServices()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0438:
            int r1 = r8.runQueryIntentActivities()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x043d:
            int r1 = r8.runResolveActivity()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0442:
            int r1 = r8.runList()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0447:
            int r1 = r8.runDump()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x044c:
            int r1 = r8.runPath()     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0451:
            if (r2 != 0) goto L_0x046d
            java.lang.String r3 = "-l"
            boolean r3 = r9.equalsIgnoreCase(r3)     // Catch:{ RemoteException -> 0x0485 }
            if (r3 == 0) goto L_0x0460
            int r1 = r8.runListPackages(r7)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0460:
            java.lang.String r3 = "-lf"
            boolean r3 = r9.equalsIgnoreCase(r3)     // Catch:{ RemoteException -> 0x0485 }
            if (r3 == 0) goto L_0x0480
            int r1 = r8.runListPackages(r6)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x046d:
            java.lang.String r3 = r8.getNextArg()     // Catch:{ RemoteException -> 0x0485 }
            if (r3 != 0) goto L_0x0480
            java.lang.String r3 = "-p"
            boolean r3 = r9.equalsIgnoreCase(r3)     // Catch:{ RemoteException -> 0x0485 }
            if (r3 == 0) goto L_0x0480
            int r1 = r8.displayPackageFilePath(r2, r7)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0480:
            int r1 = r8.handleDefaultCommands(r9)     // Catch:{ RemoteException -> 0x0485 }
            return r1
        L_0x0485:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Remote exception: "
            r3.append(r4)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            r0.println(r3)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerShellCommand.onCommand(java.lang.String):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0034  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x004c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runGetModuleInfo() {
        /*
            r12 = this;
            java.io.PrintWriter r0 = r12.getOutPrintWriter()
            r1 = 0
        L_0x0005:
            java.lang.String r2 = r12.getNextOption()
            r3 = r2
            r4 = 1
            r5 = -1
            if (r2 == 0) goto L_0x0051
            int r2 = r3.hashCode()
            r6 = 42995713(0x2901001, float:2.1168084E-37)
            if (r2 == r6) goto L_0x0027
            r6 = 517440986(0x1ed785da, float:2.281937E-20)
            if (r2 == r6) goto L_0x001d
        L_0x001c:
            goto L_0x0031
        L_0x001d:
            java.lang.String r2 = "--installed"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x001c
            r2 = r4
            goto L_0x0032
        L_0x0027:
            java.lang.String r2 = "--all"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x001c
            r2 = 0
            goto L_0x0032
        L_0x0031:
            r2 = r5
        L_0x0032:
            if (r2 == 0) goto L_0x004c
            if (r2 == r4) goto L_0x004b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Error: Unknown option: "
            r2.append(r4)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.println(r2)
            return r5
        L_0x004b:
            goto L_0x0050
        L_0x004c:
            r2 = 131072(0x20000, float:1.83671E-40)
            r1 = r1 | r2
        L_0x0050:
            goto L_0x0005
        L_0x0051:
            java.lang.String r2 = r12.getNextArg()
            java.lang.String r6 = " packageName: "
            if (r2 == 0) goto L_0x007d
            android.content.pm.IPackageManager r7 = r12.mInterface     // Catch:{ RemoteException -> 0x00b3 }
            android.content.pm.ModuleInfo r7 = r7.getModuleInfo(r2, r1)     // Catch:{ RemoteException -> 0x00b3 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x00b3 }
            r8.<init>()     // Catch:{ RemoteException -> 0x00b3 }
            java.lang.String r9 = r7.toString()     // Catch:{ RemoteException -> 0x00b3 }
            r8.append(r9)     // Catch:{ RemoteException -> 0x00b3 }
            r8.append(r6)     // Catch:{ RemoteException -> 0x00b3 }
            java.lang.String r6 = r7.getPackageName()     // Catch:{ RemoteException -> 0x00b3 }
            r8.append(r6)     // Catch:{ RemoteException -> 0x00b3 }
            java.lang.String r6 = r8.toString()     // Catch:{ RemoteException -> 0x00b3 }
            r0.println(r6)     // Catch:{ RemoteException -> 0x00b3 }
            goto L_0x00b1
        L_0x007d:
            android.content.pm.IPackageManager r7 = r12.mInterface     // Catch:{ RemoteException -> 0x00b3 }
            java.util.List r7 = r7.getInstalledModules(r1)     // Catch:{ RemoteException -> 0x00b3 }
            java.util.Iterator r8 = r7.iterator()     // Catch:{ RemoteException -> 0x00b3 }
        L_0x0087:
            boolean r9 = r8.hasNext()     // Catch:{ RemoteException -> 0x00b3 }
            if (r9 == 0) goto L_0x00b1
            java.lang.Object r9 = r8.next()     // Catch:{ RemoteException -> 0x00b3 }
            android.content.pm.ModuleInfo r9 = (android.content.pm.ModuleInfo) r9     // Catch:{ RemoteException -> 0x00b3 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x00b3 }
            r10.<init>()     // Catch:{ RemoteException -> 0x00b3 }
            java.lang.String r11 = r9.toString()     // Catch:{ RemoteException -> 0x00b3 }
            r10.append(r11)     // Catch:{ RemoteException -> 0x00b3 }
            r10.append(r6)     // Catch:{ RemoteException -> 0x00b3 }
            java.lang.String r11 = r9.getPackageName()     // Catch:{ RemoteException -> 0x00b3 }
            r10.append(r11)     // Catch:{ RemoteException -> 0x00b3 }
            java.lang.String r10 = r10.toString()     // Catch:{ RemoteException -> 0x00b3 }
            r0.println(r10)     // Catch:{ RemoteException -> 0x00b3 }
            goto L_0x0087
        L_0x00b1:
            return r4
        L_0x00b3:
            r4 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Failure ["
            r6.append(r7)
            java.lang.Class r7 = r4.getClass()
            java.lang.String r7 = r7.getName()
            r6.append(r7)
            java.lang.String r7 = " - "
            r6.append(r7)
            java.lang.String r7 = r4.getMessage()
            r6.append(r7)
            java.lang.String r7 = "]"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r0.println(r6)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerShellCommand.runGetModuleInfo():int");
    }

    private int getStagedSessions() {
        PrintWriter pw = getOutPrintWriter();
        try {
            for (PackageInstaller.SessionInfo session : this.mInterface.getPackageInstaller().getStagedSessions().getList()) {
                pw.println("appPackageName = " + session.getAppPackageName() + "; sessionId = " + session.getSessionId() + "; isStaged = " + session.isStaged() + "; isStagedSessionReady = " + session.isStagedSessionReady() + "; isStagedSessionApplied = " + session.isStagedSessionApplied() + "; isStagedSessionFailed = " + session.isStagedSessionFailed() + ";");
            }
            return 1;
        } catch (RemoteException e) {
            pw.println("Failure [" + e.getClass().getName() + " - " + e.getMessage() + "]");
            return 0;
        }
    }

    private int uninstallSystemUpdates() {
        PrintWriter pw = getOutPrintWriter();
        List<String> failedUninstalls = new LinkedList<>();
        try {
            ParceledListSlice<ApplicationInfo> packages = this.mInterface.getInstalledApplications(DumpState.DUMP_DEXOPT, 0);
            IPackageInstaller installer = this.mInterface.getPackageInstaller();
            for (ApplicationInfo info : packages.getList()) {
                if (info.isUpdatedSystemApp()) {
                    pw.println("Uninstalling updates to " + info.packageName + "...");
                    LocalIntentReceiver receiver = new LocalIntentReceiver();
                    installer.uninstall(new VersionedPackage(info.packageName, info.versionCode), (String) null, 0, receiver.getIntentSender(), 0);
                    if (receiver.getResult().getIntExtra("android.content.pm.extra.STATUS", 1) != 0) {
                        failedUninstalls.add(info.packageName);
                    }
                }
            }
            if (!failedUninstalls.isEmpty()) {
                pw.println("Failure [Couldn't uninstall packages: " + TextUtils.join(", ", failedUninstalls) + "]");
                return 0;
            }
            pw.println("Success");
            return 1;
        } catch (RemoteException e) {
            pw.println("Failure [" + e.getClass().getName() + " - " + e.getMessage() + "]");
            return 0;
        }
    }

    private int runRollbackApp() {
        PrintWriter pw = getOutPrintWriter();
        String packageName = getNextArgRequired();
        if (packageName == null) {
            pw.println("Error: package name not specified");
            return 1;
        }
        LocalIntentReceiver receiver = new LocalIntentReceiver();
        try {
            IRollbackManager rm = IRollbackManager.Stub.asInterface(ServiceManager.getService("rollback"));
            RollbackInfo rollback = null;
            for (RollbackInfo r : rm.getAvailableRollbacks().getList()) {
                Iterator it = r.getPackages().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    } else if (packageName.equals(((PackageRollbackInfo) it.next()).getPackageName())) {
                        rollback = r;
                        break;
                    }
                }
            }
            if (rollback == null) {
                pw.println("No available rollbacks for: " + packageName);
                return 1;
            }
            rm.commitRollback(rollback.getRollbackId(), ParceledListSlice.emptyList(), NotificationShellCmd.NOTIFICATION_PACKAGE, receiver.getIntentSender());
            Intent result = receiver.getResult();
            if (result.getIntExtra("android.content.rollback.extra.STATUS", 1) == 0) {
                pw.println("Success");
                return 0;
            }
            pw.println("Failure [" + result.getStringExtra("android.content.rollback.extra.STATUS_MESSAGE") + "]");
            return 1;
        } catch (RemoteException e) {
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    private void setParamsSize(InstallParams params, String inPath) {
        if (params.sessionParams.sizeBytes == -1 && !STDIN_PATH.equals(inPath)) {
            ParcelFileDescriptor fd = openFileForSystem(inPath, ActivityTaskManagerService.DUMP_RECENTS_SHORT_CMD);
            if (fd != null) {
                try {
                    params.sessionParams.setSize(PackageHelper.calculateInstalledSize(new PackageParser.PackageLite((String) null, PackageParser.parseApkLite(fd.getFileDescriptor(), inPath, 0), (String[]) null, (boolean[]) null, (String[]) null, (String[]) null, (String[]) null, (int[]) null), params.sessionParams.abiOverride, fd.getFileDescriptor()));
                    try {
                        fd.close();
                    } catch (IOException e) {
                    }
                } catch (PackageParser.PackageParserException | IOException e2) {
                    PrintWriter errPrintWriter = getErrPrintWriter();
                    errPrintWriter.println("Error: Failed to parse APK file: " + inPath);
                    throw new IllegalArgumentException("Error: Failed to parse APK file: " + inPath, e2);
                } catch (Throwable th) {
                    try {
                        fd.close();
                    } catch (IOException e3) {
                    }
                    throw th;
                }
            } else {
                PrintWriter errPrintWriter2 = getErrPrintWriter();
                errPrintWriter2.println("Error: Can't open file: " + inPath);
                throw new IllegalArgumentException("Error: Can't open file: " + inPath);
            }
        }
    }

    private int displayPackageFilePath(String pckg, int userId) throws RemoteException {
        PackageInfo info = this.mInterface.getPackageInfo(pckg, 0, userId);
        if (info == null || info.applicationInfo == null) {
            return 1;
        }
        PrintWriter pw = getOutPrintWriter();
        pw.print("package:");
        pw.println(info.applicationInfo.sourceDir);
        if (!ArrayUtils.isEmpty(info.applicationInfo.splitSourceDirs)) {
            for (String splitSourceDir : info.applicationInfo.splitSourceDirs) {
                pw.print("package:");
                pw.println(splitSourceDir);
            }
        }
        return 0;
    }

    private int runPath() throws RemoteException {
        int userId = 0;
        String option = getNextOption();
        if (option != null && option.equals("--user")) {
            userId = UserHandle.parseUserArg(getNextArgRequired());
        }
        String pkg = getNextArgRequired();
        if (pkg != null) {
            return displayPackageFilePath(pkg, userId);
        }
        getErrPrintWriter().println("Error: no package specified");
        return 1;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runList() throws android.os.RemoteException {
        /*
            r12 = this;
            java.io.PrintWriter r0 = r12.getOutPrintWriter()
            java.lang.String r1 = r12.getNextArg()
            r2 = -1
            if (r1 != 0) goto L_0x0011
            java.lang.String r3 = "Error: didn't specify type of data to list"
            r0.println(r3)
            return r2
        L_0x0011:
            int r3 = r1.hashCode()
            r4 = 0
            switch(r3) {
                case -997447790: goto L_0x0066;
                case -807062458: goto L_0x005b;
                case -290659267: goto L_0x0051;
                case 111578632: goto L_0x0046;
                case 544550766: goto L_0x003b;
                case 750867693: goto L_0x0030;
                case 812757657: goto L_0x0025;
                case 1133704324: goto L_0x001a;
                default: goto L_0x0019;
            }
        L_0x0019:
            goto L_0x0071
        L_0x001a:
            java.lang.String r3 = "permissions"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0019
            r3 = 6
            goto L_0x0072
        L_0x0025:
            java.lang.String r3 = "libraries"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0019
            r3 = 2
            goto L_0x0072
        L_0x0030:
            java.lang.String r3 = "packages"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0019
            r3 = 4
            goto L_0x0072
        L_0x003b:
            java.lang.String r3 = "instrumentation"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0019
            r3 = 1
            goto L_0x0072
        L_0x0046:
            java.lang.String r3 = "users"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0019
            r3 = 7
            goto L_0x0072
        L_0x0051:
            java.lang.String r3 = "features"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0019
            r3 = r4
            goto L_0x0072
        L_0x005b:
            java.lang.String r3 = "package"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0019
            r3 = 3
            goto L_0x0072
        L_0x0066:
            java.lang.String r3 = "permission-groups"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0019
            r3 = 5
            goto L_0x0072
        L_0x0071:
            r3 = r2
        L_0x0072:
            switch(r3) {
                case 0: goto L_0x00ce;
                case 1: goto L_0x00c9;
                case 2: goto L_0x00c4;
                case 3: goto L_0x00bf;
                case 4: goto L_0x00bf;
                case 5: goto L_0x00ba;
                case 6: goto L_0x00b5;
                case 7: goto L_0x008f;
                default: goto L_0x0075;
            }
        L_0x0075:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error: unknown list type '"
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = "'"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.println(r3)
            return r2
        L_0x008f:
            java.lang.String r2 = "user"
            android.os.IBinder r5 = android.os.ServiceManager.getService(r2)
            java.io.FileDescriptor r6 = r12.getInFileDescriptor()
            java.io.FileDescriptor r7 = r12.getOutFileDescriptor()
            java.io.FileDescriptor r8 = r12.getErrFileDescriptor()
            java.lang.String r2 = "list"
            java.lang.String[] r9 = new java.lang.String[]{r2}
            android.os.ShellCallback r10 = r12.getShellCallback()
            android.os.ResultReceiver r11 = r12.adoptResultReceiver()
            r5.shellCommand(r6, r7, r8, r9, r10, r11)
            return r4
        L_0x00b5:
            int r2 = r12.runListPermissions()
            return r2
        L_0x00ba:
            int r2 = r12.runListPermissionGroups()
            return r2
        L_0x00bf:
            int r2 = r12.runListPackages(r4)
            return r2
        L_0x00c4:
            int r2 = r12.runListLibraries()
            return r2
        L_0x00c9:
            int r2 = r12.runListInstrumentation()
            return r2
        L_0x00ce:
            int r2 = r12.runListFeatures()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerShellCommand.runList():int");
    }

    private int runListFeatures() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        List<FeatureInfo> list = this.mInterface.getSystemAvailableFeatures().getList();
        Collections.sort(list, new Comparator<FeatureInfo>() {
            public int compare(FeatureInfo o1, FeatureInfo o2) {
                if (o1.name == o2.name) {
                    return 0;
                }
                if (o1.name == null) {
                    return -1;
                }
                if (o2.name == null) {
                    return 1;
                }
                return o1.name.compareTo(o2.name);
            }
        });
        int count = list != null ? list.size() : 0;
        for (int p = 0; p < count; p++) {
            FeatureInfo fi = list.get(p);
            pw.print("feature:");
            if (fi.name != null) {
                pw.print(fi.name);
                if (fi.version > 0) {
                    pw.print("=");
                    pw.print(fi.version);
                }
                pw.println();
            } else {
                pw.println("reqGlEsVersion=0x" + Integer.toHexString(fi.reqGlEsVersion));
            }
        }
        return 0;
    }

    private int runListInstrumentation() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        boolean showSourceDir = false;
        String targetPackage = null;
        while (true) {
            try {
                String nextArg = getNextArg();
                String opt = nextArg;
                if (nextArg != null) {
                    if (!((opt.hashCode() == 1497 && opt.equals("-f")) ? false : true)) {
                        showSourceDir = true;
                    } else if (opt.charAt(0) != '-') {
                        targetPackage = opt;
                    } else {
                        pw.println("Error: Unknown option: " + opt);
                        return -1;
                    }
                } else {
                    List<InstrumentationInfo> list = this.mInterface.queryInstrumentation(targetPackage, 0).getList();
                    Collections.sort(list, new Comparator<InstrumentationInfo>() {
                        public int compare(InstrumentationInfo o1, InstrumentationInfo o2) {
                            return o1.targetPackage.compareTo(o2.targetPackage);
                        }
                    });
                    int count = list != null ? list.size() : 0;
                    for (int p = 0; p < count; p++) {
                        InstrumentationInfo ii = list.get(p);
                        pw.print("instrumentation:");
                        if (showSourceDir) {
                            pw.print(ii.sourceDir);
                            pw.print("=");
                        }
                        pw.print(new ComponentName(ii.packageName, ii.name).flattenToShortString());
                        pw.print(" (target=");
                        pw.print(ii.targetPackage);
                        pw.println(")");
                    }
                    return 0;
                }
            } catch (RuntimeException ex) {
                pw.println("Error: " + ex.toString());
                return -1;
            }
        }
    }

    private int runListLibraries() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        List<String> list = new ArrayList<>();
        String[] rawList = this.mInterface.getSystemSharedLibraryNames();
        for (String add : rawList) {
            list.add(add);
        }
        Collections.sort(list, new Comparator<String>() {
            public int compare(String o1, String o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                return o1.compareTo(o2);
            }
        });
        int count = list.size();
        for (int p = 0; p < count; p++) {
            pw.print("library:");
            pw.println(list.get(p));
        }
        return 0;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00f3, code lost:
        switch(r0) {
            case 0: goto L_0x0138;
            case 1: goto L_0x0135;
            case 2: goto L_0x012c;
            case 3: goto L_0x0129;
            case 4: goto L_0x0126;
            case 5: goto L_0x0125;
            case 6: goto L_0x0122;
            case 7: goto L_0x011f;
            case 8: goto L_0x011b;
            case 9: goto L_0x0118;
            case 10: goto L_0x0115;
            case 11: goto L_0x010e;
            case 12: goto L_0x0104;
            case 13: goto L_0x00f9;
            default: goto L_0x00f6;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00f9, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:?, code lost:
        r11 = java.lang.Integer.parseInt(getNextArgRequired());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x010c, code lost:
        r12 = android.os.UserHandle.parseUserArg(getNextArgRequired());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x010e, code lost:
        r10 = true;
        r3 = true | r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0115, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0118, code lost:
        r13 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x011b, code lost:
        r3 = r3 | true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x011f, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0122, code lost:
        r7 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0126, code lost:
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0129, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x012c, code lost:
        r3 = (true | r3) | true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0135, code lost:
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0138, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:?, code lost:
        r0 = new java.lang.StringBuilder();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x013f, code lost:
        r28 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:?, code lost:
        r0.append("Error: Unknown option: ");
        r0.append(r15);
        r2.println(r0.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0150, code lost:
        return -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0152, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0153, code lost:
        r8 = r28;
        r18 = r3;
        r26 = r4;
        r25 = r5;
        r19 = r12;
     */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0220  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0241  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0247  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runListPackages(boolean r28) throws android.os.RemoteException {
        /*
            r27 = this;
            r1 = r27
            java.io.PrintWriter r2 = r27.getOutPrintWriter()
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = -1
            r12 = 0
            r13 = r6
            r14 = r7
            r6 = r4
            r7 = r5
            r4 = r28
            r5 = r3
            r3 = r0
        L_0x0019:
            java.lang.String r0 = r27.getNextOption()     // Catch:{ RuntimeException -> 0x027b }
            r28 = r0
            r16 = 0
            r17 = 1
            if (r0 == 0) goto L_0x016c
            int r0 = r28.hashCode()     // Catch:{ RuntimeException -> 0x015f }
            switch(r0) {
                case -493830763: goto L_0x00da;
                case 1446: goto L_0x00cd;
                case 1480: goto L_0x00c1;
                case 1492: goto L_0x00b5;
                case 1495: goto L_0x00a8;
                case 1496: goto L_0x009b;
                case 1497: goto L_0x008f;
                case 1500: goto L_0x0082;
                case 1503: goto L_0x0075;
                case 1510: goto L_0x0068;
                case 1512: goto L_0x005a;
                case 43014832: goto L_0x004c;
                case 1333469547: goto L_0x003e;
                case 1809263575: goto L_0x0030;
                default: goto L_0x002c;
            }
        L_0x002c:
            r15 = r28
            goto L_0x00f2
        L_0x0030:
            java.lang.String r0 = "--apex-only"
            r15 = r28
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 11
            goto L_0x00f3
        L_0x003e:
            r15 = r28
            java.lang.String r0 = "--user"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 12
            goto L_0x00f3
        L_0x004c:
            r15 = r28
            java.lang.String r0 = "--uid"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 13
            goto L_0x00f3
        L_0x005a:
            r15 = r28
            java.lang.String r0 = "-u"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 8
            goto L_0x00f3
        L_0x0068:
            r15 = r28
            java.lang.String r0 = "-s"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 6
            goto L_0x00f3
        L_0x0075:
            r15 = r28
            java.lang.String r0 = "-l"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 5
            goto L_0x00f3
        L_0x0082:
            r15 = r28
            java.lang.String r0 = "-i"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 4
            goto L_0x00f3
        L_0x008f:
            r15 = r28
            java.lang.String r0 = "-f"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 3
            goto L_0x00f3
        L_0x009b:
            r15 = r28
            java.lang.String r0 = "-e"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = r17
            goto L_0x00f3
        L_0x00a8:
            r15 = r28
            java.lang.String r0 = "-d"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = r16
            goto L_0x00f3
        L_0x00b5:
            r15 = r28
            java.lang.String r0 = "-a"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 2
            goto L_0x00f3
        L_0x00c1:
            r15 = r28
            java.lang.String r0 = "-U"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 7
            goto L_0x00f3
        L_0x00cd:
            r15 = r28
            java.lang.String r0 = "-3"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 9
            goto L_0x00f3
        L_0x00da:
            r15 = r28
            java.lang.String r0 = "--show-versioncode"
            boolean r0 = r15.equals(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            if (r0 == 0) goto L_0x00f2
            r0 = 10
            goto L_0x00f3
        L_0x00e7:
            r0 = move-exception
            r18 = r3
            r26 = r4
            r25 = r5
            r19 = r12
            goto L_0x0286
        L_0x00f2:
            r0 = -1
        L_0x00f3:
            switch(r0) {
                case 0: goto L_0x0138;
                case 1: goto L_0x0135;
                case 2: goto L_0x012c;
                case 3: goto L_0x0129;
                case 4: goto L_0x0126;
                case 5: goto L_0x0125;
                case 6: goto L_0x0122;
                case 7: goto L_0x011f;
                case 8: goto L_0x011b;
                case 9: goto L_0x0118;
                case 10: goto L_0x0115;
                case 11: goto L_0x010e;
                case 12: goto L_0x0104;
                case 13: goto L_0x00f9;
                default: goto L_0x00f6;
            }
        L_0x00f6:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015f }
            goto L_0x013c
        L_0x00f9:
            r8 = 1
            java.lang.String r0 = r27.getNextArgRequired()     // Catch:{ RuntimeException -> 0x00e7 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            r11 = r0
            goto L_0x013a
        L_0x0104:
            java.lang.String r0 = r27.getNextArgRequired()     // Catch:{ RuntimeException -> 0x00e7 }
            int r0 = android.os.UserHandle.parseUserArg(r0)     // Catch:{ RuntimeException -> 0x00e7 }
            r12 = r0
            goto L_0x013a
        L_0x010e:
            r0 = 1073741824(0x40000000, float:2.0)
            r0 = r0 | r3
            r3 = 1
            r10 = r3
            r3 = r0
            goto L_0x013a
        L_0x0115:
            r0 = 1
            r9 = r0
            goto L_0x013a
        L_0x0118:
            r0 = 1
            r13 = r0
            goto L_0x013a
        L_0x011b:
            r0 = r3 | 8192(0x2000, float:1.14794E-41)
            r3 = r0
            goto L_0x013a
        L_0x011f:
            r0 = 1
            r8 = r0
            goto L_0x013a
        L_0x0122:
            r0 = 1
            r7 = r0
            goto L_0x013a
        L_0x0125:
            goto L_0x013a
        L_0x0126:
            r0 = 1
            r14 = r0
            goto L_0x013a
        L_0x0129:
            r0 = 1
            r4 = r0
            goto L_0x013a
        L_0x012c:
            r0 = 4202496(0x402000, float:5.888951E-39)
            r0 = r0 | r3
            r3 = 536870912(0x20000000, float:1.0842022E-19)
            r0 = r0 | r3
            r3 = r0
            goto L_0x013a
        L_0x0135:
            r0 = 1
            r6 = r0
            goto L_0x013a
        L_0x0138:
            r0 = 1
            r5 = r0
        L_0x013a:
            goto L_0x0019
        L_0x013c:
            r0.<init>()     // Catch:{ RuntimeException -> 0x015f }
            r28 = r8
            java.lang.String r8 = "Error: Unknown option: "
            r0.append(r8)     // Catch:{ RuntimeException -> 0x0152 }
            r0.append(r15)     // Catch:{ RuntimeException -> 0x0152 }
            java.lang.String r0 = r0.toString()     // Catch:{ RuntimeException -> 0x0152 }
            r2.println(r0)     // Catch:{ RuntimeException -> 0x0152 }
            r8 = -1
            return r8
        L_0x0152:
            r0 = move-exception
            r8 = r28
            r18 = r3
            r26 = r4
            r25 = r5
            r19 = r12
            goto L_0x0286
        L_0x015f:
            r0 = move-exception
            r28 = r8
            r18 = r3
            r26 = r4
            r25 = r5
            r19 = r12
            goto L_0x0286
        L_0x016c:
            r15 = r28
            r28 = r8
            java.lang.String r0 = r27.getNextArg()
            android.content.pm.IPackageManager r8 = r1.mInterface
            android.content.pm.ParceledListSlice r8 = r8.getInstalledPackages(r3, r12)
            java.util.List r15 = r8.getList()
            r18 = r3
            int r3 = r15.size()
            r19 = 0
            r20 = r8
            r8 = r19
        L_0x018b:
            if (r8 >= r3) goto L_0x027a
            java.lang.Object r19 = r15.get(r8)
            r21 = r3
            r3 = r19
            android.content.pm.PackageInfo r3 = (android.content.pm.PackageInfo) r3
            if (r0 == 0) goto L_0x01ad
            r19 = r12
            java.lang.String r12 = r3.packageName
            boolean r12 = r12.contains(r0)
            if (r12 != 0) goto L_0x01af
            r23 = r0
            r26 = r4
            r25 = r5
            r22 = r15
            goto L_0x026a
        L_0x01ad:
            r19 = r12
        L_0x01af:
            boolean r12 = r3.isApex
            r22 = r15
            r15 = -1
            if (r11 == r15) goto L_0x01c6
            if (r12 != 0) goto L_0x01c6
            android.content.pm.ApplicationInfo r15 = r3.applicationInfo
            int r15 = r15.uid
            if (r15 == r11) goto L_0x01c6
            r23 = r0
            r26 = r4
            r25 = r5
            goto L_0x026a
        L_0x01c6:
            if (r12 != 0) goto L_0x01d3
            android.content.pm.ApplicationInfo r15 = r3.applicationInfo
            int r15 = r15.flags
            r15 = r15 & 1
            if (r15 == 0) goto L_0x01d3
            r15 = r17
            goto L_0x01d5
        L_0x01d3:
            r15 = r16
        L_0x01d5:
            if (r12 != 0) goto L_0x01e2
            r23 = r0
            android.content.pm.ApplicationInfo r0 = r3.applicationInfo
            boolean r0 = r0.enabled
            if (r0 == 0) goto L_0x01e4
            r0 = r17
            goto L_0x01e6
        L_0x01e2:
            r23 = r0
        L_0x01e4:
            r0 = r16
        L_0x01e6:
            if (r5 == 0) goto L_0x01f1
            if (r0 != 0) goto L_0x01eb
            goto L_0x01f1
        L_0x01eb:
            r26 = r4
            r25 = r5
            goto L_0x026a
        L_0x01f1:
            if (r6 == 0) goto L_0x01f5
            if (r0 == 0) goto L_0x01eb
        L_0x01f5:
            if (r7 == 0) goto L_0x01f9
            if (r15 == 0) goto L_0x01eb
        L_0x01f9:
            if (r13 == 0) goto L_0x01fd
            if (r15 != 0) goto L_0x01eb
        L_0x01fd:
            if (r10 == 0) goto L_0x0201
            if (r12 == 0) goto L_0x01eb
        L_0x0201:
            r24 = r0
            java.lang.String r0 = "package:"
            r2.print(r0)
            if (r4 == 0) goto L_0x0219
            if (r12 != 0) goto L_0x0219
            android.content.pm.ApplicationInfo r0 = r3.applicationInfo
            java.lang.String r0 = r0.sourceDir
            r2.print(r0)
            java.lang.String r0 = "="
            r2.print(r0)
        L_0x0219:
            java.lang.String r0 = r3.packageName
            r2.print(r0)
            if (r9 == 0) goto L_0x0241
            java.lang.String r0 = " versionCode:"
            r2.print(r0)
            android.content.pm.ApplicationInfo r0 = r3.applicationInfo
            if (r0 == 0) goto L_0x0235
            android.content.pm.ApplicationInfo r0 = r3.applicationInfo
            r26 = r4
            r25 = r5
            long r4 = r0.longVersionCode
            r2.print(r4)
            goto L_0x0245
        L_0x0235:
            r26 = r4
            r25 = r5
            long r4 = r3.getLongVersionCode()
            r2.print(r4)
            goto L_0x0245
        L_0x0241:
            r26 = r4
            r25 = r5
        L_0x0245:
            if (r14 == 0) goto L_0x0257
            java.lang.String r0 = "  installer="
            r2.print(r0)
            android.content.pm.IPackageManager r0 = r1.mInterface
            java.lang.String r4 = r3.packageName
            java.lang.String r0 = r0.getInstallerPackageName(r4)
            r2.print(r0)
        L_0x0257:
            if (r28 == 0) goto L_0x0267
            if (r12 != 0) goto L_0x0267
            java.lang.String r0 = " uid:"
            r2.print(r0)
            android.content.pm.ApplicationInfo r0 = r3.applicationInfo
            int r0 = r0.uid
            r2.print(r0)
        L_0x0267:
            r2.println()
        L_0x026a:
            int r8 = r8 + 1
            r12 = r19
            r3 = r21
            r15 = r22
            r0 = r23
            r5 = r25
            r4 = r26
            goto L_0x018b
        L_0x027a:
            return r16
        L_0x027b:
            r0 = move-exception
            r18 = r3
            r26 = r4
            r25 = r5
            r28 = r8
            r19 = r12
        L_0x0286:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error: "
            r3.append(r4)
            java.lang.String r4 = r0.toString()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.println(r3)
            r3 = -1
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerShellCommand.runListPackages(boolean):int");
    }

    private int runListPermissionGroups() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        List<PermissionGroupInfo> pgs = this.mInterface.getAllPermissionGroups(0).getList();
        int count = pgs.size();
        for (int p = 0; p < count; p++) {
            pw.print("permission group:");
            pw.println(pgs.get(p).name);
        }
        return 0;
    }

    private int runListPermissions() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        boolean labels = false;
        boolean groups = false;
        boolean userOnly = false;
        boolean summary = false;
        boolean dangerousOnly = false;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption != null) {
                char c = 65535;
                int hashCode = opt.hashCode();
                if (hashCode != 1495) {
                    if (hashCode != 1510) {
                        if (hashCode != 1512) {
                            if (hashCode != 1497) {
                                if (hashCode == 1498 && opt.equals("-g")) {
                                    c = 2;
                                }
                            } else if (opt.equals("-f")) {
                                c = 1;
                            }
                        } else if (opt.equals("-u")) {
                            c = 4;
                        }
                    } else if (opt.equals("-s")) {
                        c = 3;
                    }
                } else if (opt.equals("-d")) {
                    c = 0;
                }
                if (c == 0) {
                    dangerousOnly = true;
                } else if (c == 1) {
                    labels = true;
                } else if (c == 2) {
                    groups = true;
                } else if (c == 3) {
                    groups = true;
                    labels = true;
                    summary = true;
                } else if (c != 4) {
                    pw.println("Error: Unknown option: " + opt);
                    return 1;
                } else {
                    userOnly = true;
                }
            } else {
                ArrayList<String> groupList = new ArrayList<>();
                if (groups) {
                    List<PermissionGroupInfo> infos = this.mInterface.getAllPermissionGroups(0).getList();
                    int count = infos.size();
                    for (int i = 0; i < count; i++) {
                        groupList.add(infos.get(i).name);
                    }
                    groupList.add((Object) null);
                } else {
                    groupList.add(getNextArg());
                }
                if (dangerousOnly) {
                    pw.println("Dangerous Permissions:");
                    pw.println("");
                    String str = "";
                    doListPermissions(groupList, groups, labels, summary, 1, 1);
                    if (!userOnly) {
                        return 0;
                    }
                    pw.println("Normal Permissions:");
                    pw.println(str);
                    doListPermissions(groupList, groups, labels, summary, 0, 0);
                    return 0;
                }
                String str2 = "";
                if (userOnly) {
                    pw.println("Dangerous and Normal Permissions:");
                    pw.println(str2);
                    doListPermissions(groupList, groups, labels, summary, 0, 1);
                    return 0;
                }
                pw.println("All Permissions:");
                pw.println(str2);
                doListPermissions(groupList, groups, labels, summary, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, 10000);
                return 0;
            }
        }
    }

    private Intent parseIntentAndUser() throws URISyntaxException {
        this.mTargetUser = -2;
        this.mBrief = false;
        this.mComponents = false;
        Intent intent = Intent.parseCommandArgs(this, new Intent.CommandOptionHandler() {
            public boolean handleOption(String opt, ShellCommand cmd) {
                if ("--user".equals(opt)) {
                    PackageManagerShellCommand.this.mTargetUser = UserHandle.parseUserArg(cmd.getNextArgRequired());
                    return true;
                } else if ("--brief".equals(opt)) {
                    PackageManagerShellCommand.this.mBrief = true;
                    return true;
                } else if ("--components".equals(opt)) {
                    PackageManagerShellCommand.this.mComponents = true;
                    return true;
                } else if (!"--query-flags".equals(opt)) {
                    return false;
                } else {
                    PackageManagerShellCommand.this.mQueryFlags = Integer.decode(cmd.getNextArgRequired()).intValue();
                    return true;
                }
            }
        });
        this.mTargetUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), this.mTargetUser, false, false, (String) null, (String) null);
        return intent;
    }

    private void printResolveInfo(PrintWriterPrinter pr, String prefix, ResolveInfo ri, boolean brief, boolean components) {
        ComponentName comp;
        if (brief || components) {
            if (ri.activityInfo != null) {
                comp = new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name);
            } else if (ri.serviceInfo != null) {
                comp = new ComponentName(ri.serviceInfo.packageName, ri.serviceInfo.name);
            } else if (ri.providerInfo != null) {
                comp = new ComponentName(ri.providerInfo.packageName, ri.providerInfo.name);
            } else {
                comp = null;
            }
            if (comp != null) {
                if (!components) {
                    pr.println(prefix + "priority=" + ri.priority + " preferredOrder=" + ri.preferredOrder + " match=0x" + Integer.toHexString(ri.match) + " specificIndex=" + ri.specificIndex + " isDefault=" + ri.isDefault);
                }
                pr.println(prefix + comp.flattenToShortString());
                return;
            }
        }
        ri.dump(pr, prefix);
    }

    private int runResolveActivity() {
        try {
            Intent intent = parseIntentAndUser();
            try {
                ResolveInfo ri = this.mInterface.resolveIntent(intent, intent.getType(), this.mQueryFlags, this.mTargetUser);
                PrintWriter pw = getOutPrintWriter();
                if (ri == null) {
                    pw.println("No activity found");
                    return 0;
                }
                printResolveInfo(new PrintWriterPrinter(pw), "", ri, this.mBrief, this.mComponents);
                return 0;
            } catch (RemoteException e) {
                throw new RuntimeException("Failed calling service", e);
            }
        } catch (URISyntaxException e2) {
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }

    private int runQueryIntentActivities() {
        try {
            Intent intent = parseIntentAndUser();
            try {
                List<ResolveInfo> result = this.mInterface.queryIntentActivities(intent, intent.getType(), this.mQueryFlags, this.mTargetUser).getList();
                PrintWriter pw = getOutPrintWriter();
                if (result != null) {
                    if (result.size() > 0) {
                        if (!this.mComponents) {
                            pw.print(result.size());
                            pw.println(" activities found:");
                            PrintWriterPrinter pr = new PrintWriterPrinter(pw);
                            for (int i = 0; i < result.size(); i++) {
                                pw.print("  Activity #");
                                pw.print(i);
                                pw.println(":");
                                printResolveInfo(pr, "    ", result.get(i), this.mBrief, this.mComponents);
                            }
                        } else {
                            PrintWriterPrinter pr2 = new PrintWriterPrinter(pw);
                            for (int i2 = 0; i2 < result.size(); i2++) {
                                printResolveInfo(pr2, "", result.get(i2), this.mBrief, this.mComponents);
                            }
                        }
                        return 0;
                    }
                }
                pw.println("No activities found");
                return 0;
            } catch (RemoteException e) {
                throw new RuntimeException("Failed calling service", e);
            }
        } catch (URISyntaxException e2) {
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }

    private int runQueryIntentServices() {
        try {
            Intent intent = parseIntentAndUser();
            try {
                List<ResolveInfo> result = this.mInterface.queryIntentServices(intent, intent.getType(), this.mQueryFlags, this.mTargetUser).getList();
                PrintWriter pw = getOutPrintWriter();
                if (result != null) {
                    if (result.size() > 0) {
                        if (!this.mComponents) {
                            pw.print(result.size());
                            pw.println(" services found:");
                            PrintWriterPrinter pr = new PrintWriterPrinter(pw);
                            for (int i = 0; i < result.size(); i++) {
                                pw.print("  Service #");
                                pw.print(i);
                                pw.println(":");
                                printResolveInfo(pr, "    ", result.get(i), this.mBrief, this.mComponents);
                            }
                        } else {
                            PrintWriterPrinter pr2 = new PrintWriterPrinter(pw);
                            for (int i2 = 0; i2 < result.size(); i2++) {
                                printResolveInfo(pr2, "", result.get(i2), this.mBrief, this.mComponents);
                            }
                        }
                        return 0;
                    }
                }
                pw.println("No services found");
                return 0;
            } catch (RemoteException e) {
                throw new RuntimeException("Failed calling service", e);
            }
        } catch (URISyntaxException e2) {
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }

    private int runQueryIntentReceivers() {
        try {
            Intent intent = parseIntentAndUser();
            try {
                List<ResolveInfo> result = this.mInterface.queryIntentReceivers(intent, intent.getType(), this.mQueryFlags, this.mTargetUser).getList();
                PrintWriter pw = getOutPrintWriter();
                if (result != null) {
                    if (result.size() > 0) {
                        if (!this.mComponents) {
                            pw.print(result.size());
                            pw.println(" receivers found:");
                            PrintWriterPrinter pr = new PrintWriterPrinter(pw);
                            for (int i = 0; i < result.size(); i++) {
                                pw.print("  Receiver #");
                                pw.print(i);
                                pw.println(":");
                                printResolveInfo(pr, "    ", result.get(i), this.mBrief, this.mComponents);
                            }
                        } else {
                            PrintWriterPrinter pr2 = new PrintWriterPrinter(pw);
                            for (int i2 = 0; i2 < result.size(); i2++) {
                                printResolveInfo(pr2, "", result.get(i2), this.mBrief, this.mComponents);
                            }
                        }
                        return 0;
                    }
                }
                pw.println("No receivers found");
                return 0;
            } catch (RemoteException e) {
                throw new RuntimeException("Failed calling service", e);
            }
        } catch (URISyntaxException e2) {
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }

    private int runInstall() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        InstallParams params = makeInstallParams();
        String inPath = getNextArg();
        setParamsSize(params, inPath);
        int sessionId = doCreateSession(params.sessionParams, params.installerPackageName, params.userId);
        boolean abandonSession = true;
        if (inPath == null) {
            try {
                if (params.sessionParams.sizeBytes == -1) {
                    pw.println("Error: must either specify a package size or an APK file");
                    return 1;
                }
            } finally {
                if (abandonSession) {
                    try {
                        doAbandonSession(sessionId, false);
                    } catch (Exception e) {
                    }
                }
            }
        }
        boolean isApex = (params.sessionParams.installFlags & 131072) != 0;
        StringBuilder sb = new StringBuilder();
        sb.append("base.");
        sb.append(isApex ? "apex" : "apk");
        if (doWriteSplit(sessionId, inPath, params.sessionParams.sizeBytes, sb.toString(), false) != 0) {
            if (abandonSession) {
                try {
                    doAbandonSession(sessionId, false);
                } catch (Exception e2) {
                }
            }
            return 1;
        } else if (doCommitSession(sessionId, false) != 0) {
            if (abandonSession) {
                try {
                    doAbandonSession(sessionId, false);
                } catch (Exception e3) {
                }
            }
            return 1;
        } else {
            abandonSession = false;
            pw.println("Success");
            if (abandonSession) {
                try {
                    doAbandonSession(sessionId, false);
                } catch (Exception e4) {
                }
            }
            return 0;
        }
    }

    private int runInstallAbandon() throws RemoteException {
        return doAbandonSession(Integer.parseInt(getNextArg()), true);
    }

    private int runInstallCommit() throws RemoteException {
        return doCommitSession(Integer.parseInt(getNextArg()), true);
    }

    private int runInstallCreate() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        InstallParams installParams = makeInstallParams();
        int sessionId = doCreateSession(installParams.sessionParams, installParams.installerPackageName, installParams.userId);
        pw.println("Success: created install session [" + sessionId + "]");
        return 0;
    }

    private int runInstallWrite() throws RemoteException {
        long sizeBytes = -1;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                return doWriteSplit(Integer.parseInt(getNextArg()), getNextArg(), sizeBytes, getNextArg(), true);
            } else if (opt.equals("-S")) {
                sizeBytes = Long.parseLong(getNextArg());
            } else {
                throw new IllegalArgumentException("Unknown option: " + opt);
            }
        }
    }

    private int runInstallAddSession() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        int parentSessionId = Integer.parseInt(getNextArg());
        List<Integer> otherSessionIds = new ArrayList<>();
        while (true) {
            String nextArg = getNextArg();
            String opt = nextArg;
            if (nextArg == null) {
                break;
            }
            otherSessionIds.add(Integer.valueOf(Integer.parseInt(opt)));
        }
        if (otherSessionIds.size() != 0) {
            return doInstallAddSession(parentSessionId, ArrayUtils.convertToIntArray(otherSessionIds), true);
        }
        pw.println("Error: At least two sessions are required.");
        return 1;
    }

    private int runInstallRemove() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        int sessionId = Integer.parseInt(getNextArg());
        String splitName = getNextArg();
        if (splitName != null) {
            return doRemoveSplit(sessionId, splitName, true);
        }
        pw.println("Error: split name not specified");
        return 1;
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    private int runInstallExisting() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        boolean waitTillComplete = false;
        int installFlags = true;
        int userId = 0;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption != null) {
                char c = 65535;
                switch (opt.hashCode()) {
                    case -951415743:
                        if (opt.equals("--instant")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1051781117:
                        if (opt.equals("--ephemeral")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1333024815:
                        if (opt.equals("--full")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1333469547:
                        if (opt.equals("--user")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 1333511957:
                        if (opt.equals("--wait")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1494514835:
                        if (opt.equals("--restrict-permissions")) {
                            c = 5;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    userId = UserHandle.parseUserArg(getNextArgRequired());
                } else if (c == 1 || c == 2) {
                    installFlags = (installFlags | 2048) & -16385;
                } else if (c == 3) {
                    installFlags = (installFlags & -2049) | 16384;
                } else if (c == 4) {
                    waitTillComplete = true;
                } else if (c != 5) {
                    pw.println("Error: Unknown option: " + opt);
                    return 1;
                } else {
                    installFlags = -4194305 & installFlags;
                }
            } else {
                String packageName = getNextArg();
                if (packageName == null) {
                    pw.println("Error: package name not specified");
                    return 1;
                } else if (waitTillComplete) {
                    try {
                        LocalIntentReceiver receiver = new LocalIntentReceiver();
                        IPackageInstaller installer = this.mInterface.getPackageInstaller();
                        pw.println("Installing package " + packageName + " for user: " + userId);
                        String packageName2 = packageName;
                        try {
                            installer.installExistingPackage(packageName, installFlags, 0, receiver.getIntentSender(), userId, (List) null);
                            int status = receiver.getResult().getIntExtra("android.content.pm.extra.STATUS", 1);
                            pw.println("Received intent for package install");
                            if (status == 0) {
                                return 0;
                            }
                            return 1;
                        } catch (PackageManager.NameNotFoundException | RemoteException e) {
                            e = e;
                            String str = packageName2;
                            pw.println(e.toString());
                            return 1;
                        }
                    } catch (PackageManager.NameNotFoundException | RemoteException e2) {
                        e = e2;
                        String str2 = packageName;
                        pw.println(e.toString());
                        return 1;
                    }
                } else {
                    String packageName3 = packageName;
                    try {
                        if (this.mInterface.installExistingPackageAsUser(packageName3, userId, installFlags, 0, (List) null) != -3) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Package ");
                            try {
                                sb.append(packageName3);
                                sb.append(" installed for user: ");
                                sb.append(userId);
                                pw.println(sb.toString());
                                return 0;
                            } catch (PackageManager.NameNotFoundException | RemoteException e3) {
                                e = e3;
                                pw.println(e.toString());
                                return 1;
                            }
                        } else {
                            throw new PackageManager.NameNotFoundException("Package " + packageName3 + " doesn't exist");
                        }
                    } catch (PackageManager.NameNotFoundException | RemoteException e4) {
                        e = e4;
                        String str3 = packageName3;
                        pw.println(e.toString());
                        return 1;
                    }
                }
            }
        }
    }

    private int runSetInstallLocation() throws RemoteException {
        String arg = getNextArg();
        if (arg == null) {
            getErrPrintWriter().println("Error: no install location specified.");
            return 1;
        }
        try {
            if (this.mInterface.setInstallLocation(Integer.parseInt(arg))) {
                return 0;
            }
            getErrPrintWriter().println("Error: install location has to be a number.");
            return 1;
        } catch (NumberFormatException e) {
            getErrPrintWriter().println("Error: install location has to be a number.");
            return 1;
        }
    }

    private int runGetInstallLocation() throws RemoteException {
        int loc = this.mInterface.getInstallLocation();
        String locStr = "invalid";
        if (loc == 0) {
            locStr = UiModeManagerService.Shell.NIGHT_MODE_STR_AUTO;
        } else if (loc == 1) {
            locStr = "internal";
        } else if (loc == 2) {
            locStr = "external";
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println(loc + "[" + locStr + "]");
        return 0;
    }

    public int runMovePackage() throws RemoteException {
        String packageName = getNextArg();
        if (packageName == null) {
            getErrPrintWriter().println("Error: package name not specified");
            return 1;
        }
        String volumeUuid = getNextArg();
        if ("internal".equals(volumeUuid)) {
            volumeUuid = null;
        }
        int moveId = this.mInterface.movePackage(packageName, volumeUuid);
        int status = this.mInterface.getMoveStatus(moveId);
        while (!PackageManager.isMoveStatusFinished(status)) {
            SystemClock.sleep(1000);
            status = this.mInterface.getMoveStatus(moveId);
        }
        if (status == -100) {
            getOutPrintWriter().println("Success");
            return 0;
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Failure [" + status + "]");
        return 1;
    }

    public int runMovePrimaryStorage() throws RemoteException {
        String volumeUuid = getNextArg();
        if ("internal".equals(volumeUuid)) {
            volumeUuid = null;
        }
        int moveId = this.mInterface.movePrimaryStorage(volumeUuid);
        int status = this.mInterface.getMoveStatus(moveId);
        while (!PackageManager.isMoveStatusFinished(status)) {
            SystemClock.sleep(1000);
            status = this.mInterface.getMoveStatus(moveId);
        }
        if (status == -100) {
            getOutPrintWriter().println("Success");
            return 0;
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Failure [" + status + "]");
        return 1;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runCompile() throws android.os.RemoteException {
        /*
            r28 = this;
            r0 = r28
            java.io.PrintWriter r1 = r28.getOutPrintWriter()
            r2 = 0
            java.lang.String r3 = "dalvik.vm.usejitprofiles"
            boolean r3 = android.os.SystemProperties.getBoolean(r3, r2)
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
        L_0x0016:
            java.lang.String r13 = r28.getNextOption()
            r15 = r13
            r2 = 1
            if (r13 == 0) goto L_0x00d8
            int r13 = r15.hashCode()
            switch(r13) {
                case -1615291473: goto L_0x0083;
                case -1614046854: goto L_0x0078;
                case 1492: goto L_0x006e;
                case 1494: goto L_0x0064;
                case 1497: goto L_0x005a;
                case 1504: goto L_0x0050;
                case 1509: goto L_0x0046;
                case 1269477022: goto L_0x003b;
                case 1323879247: goto L_0x0031;
                case 1690714782: goto L_0x0027;
                default: goto L_0x0025;
            }
        L_0x0025:
            goto L_0x008d
        L_0x0027:
            java.lang.String r13 = "--check-prof"
            boolean r13 = r15.equals(r13)
            if (r13 == 0) goto L_0x0025
            r13 = 6
            goto L_0x008e
        L_0x0031:
            java.lang.String r13 = "--compile-layouts"
            boolean r13 = r15.equals(r13)
            if (r13 == 0) goto L_0x0025
            r13 = 5
            goto L_0x008e
        L_0x003b:
            java.lang.String r13 = "--secondary-dex"
            boolean r13 = r15.equals(r13)
            if (r13 == 0) goto L_0x0025
            r13 = 8
            goto L_0x008e
        L_0x0046:
            java.lang.String r13 = "-r"
            boolean r13 = r15.equals(r13)
            if (r13 == 0) goto L_0x0025
            r13 = 4
            goto L_0x008e
        L_0x0050:
            java.lang.String r13 = "-m"
            boolean r13 = r15.equals(r13)
            if (r13 == 0) goto L_0x0025
            r13 = 3
            goto L_0x008e
        L_0x005a:
            java.lang.String r13 = "-f"
            boolean r13 = r15.equals(r13)
            if (r13 == 0) goto L_0x0025
            r13 = 2
            goto L_0x008e
        L_0x0064:
            java.lang.String r13 = "-c"
            boolean r13 = r15.equals(r13)
            if (r13 == 0) goto L_0x0025
            r13 = r2
            goto L_0x008e
        L_0x006e:
            java.lang.String r13 = "-a"
            boolean r13 = r15.equals(r13)
            if (r13 == 0) goto L_0x0025
            r13 = 0
            goto L_0x008e
        L_0x0078:
            java.lang.String r13 = "--split"
            boolean r13 = r15.equals(r13)
            if (r13 == 0) goto L_0x0025
            r13 = 9
            goto L_0x008e
        L_0x0083:
            java.lang.String r13 = "--reset"
            boolean r13 = r15.equals(r13)
            if (r13 == 0) goto L_0x0025
            r13 = 7
            goto L_0x008e
        L_0x008d:
            r13 = -1
        L_0x008e:
            switch(r13) {
                case 0: goto L_0x00d3;
                case 1: goto L_0x00d0;
                case 2: goto L_0x00cd;
                case 3: goto L_0x00c7;
                case 4: goto L_0x00c1;
                case 5: goto L_0x00be;
                case 6: goto L_0x00b8;
                case 7: goto L_0x00af;
                case 8: goto L_0x00ac;
                case 9: goto L_0x00a6;
                default: goto L_0x0091;
            }
        L_0x0091:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "Error: Unknown option: "
            r13.append(r14)
            r13.append(r15)
            java.lang.String r13 = r13.toString()
            r1.println(r13)
            return r2
        L_0x00a6:
            java.lang.String r2 = r28.getNextArgRequired()
            r11 = r2
            goto L_0x00d5
        L_0x00ac:
            r2 = 1
            r10 = r2
            goto L_0x00d5
        L_0x00af:
            r2 = 1
            r4 = 1
            java.lang.String r6 = "install"
            r8 = r6
            r6 = r4
            r4 = r2
            goto L_0x00d5
        L_0x00b8:
            java.lang.String r2 = r28.getNextArgRequired()
            r9 = r2
            goto L_0x00d5
        L_0x00be:
            r2 = 1
            r12 = r2
            goto L_0x00d5
        L_0x00c1:
            java.lang.String r2 = r28.getNextArgRequired()
            r8 = r2
            goto L_0x00d5
        L_0x00c7:
            java.lang.String r2 = r28.getNextArgRequired()
            r7 = r2
            goto L_0x00d5
        L_0x00cd:
            r2 = 1
            r4 = r2
            goto L_0x00d5
        L_0x00d0:
            r2 = 1
            r6 = r2
            goto L_0x00d5
        L_0x00d3:
            r2 = 1
            r5 = r2
        L_0x00d5:
            r2 = 0
            goto L_0x0016
        L_0x00d8:
            if (r9 == 0) goto L_0x00f5
            java.lang.String r13 = "true"
            boolean r13 = r13.equals(r9)
            if (r13 == 0) goto L_0x00e5
            r3 = 1
            goto L_0x00f5
        L_0x00e5:
            java.lang.String r13 = "false"
            boolean r13 = r13.equals(r9)
            if (r13 == 0) goto L_0x00ef
            r3 = 0
            goto L_0x00f5
        L_0x00ef:
            java.lang.String r13 = "Invalid value for \"--check-prof\". Expected \"true\" or \"false\"."
            r1.println(r13)
            return r2
        L_0x00f5:
            if (r7 == 0) goto L_0x00f9
            r13 = r2
            goto L_0x00fa
        L_0x00f9:
            r13 = 0
        L_0x00fa:
            r20 = r13
            if (r8 == 0) goto L_0x0100
            r13 = r2
            goto L_0x0101
        L_0x0100:
            r13 = 0
        L_0x0101:
            r21 = r13
            if (r20 != 0) goto L_0x0109
            if (r21 != 0) goto L_0x0109
            if (r12 == 0) goto L_0x0121
        L_0x0109:
            if (r20 != 0) goto L_0x010f
            if (r21 == 0) goto L_0x010f
            if (r12 != 0) goto L_0x0121
        L_0x010f:
            if (r20 == 0) goto L_0x0115
            if (r21 != 0) goto L_0x0115
            if (r12 != 0) goto L_0x0121
        L_0x0115:
            if (r20 == 0) goto L_0x011b
            if (r21 == 0) goto L_0x011b
            if (r12 == 0) goto L_0x0121
        L_0x011b:
            if (r20 == 0) goto L_0x0127
            if (r21 == 0) goto L_0x0127
            if (r12 == 0) goto L_0x0127
        L_0x0121:
            java.lang.String r13 = "Must specify exactly one of compilation filter (\"-m\"), compilation reason (\"-r\"), or compile layouts (\"--compile-layouts\")"
            r1.println(r13)
            return r2
        L_0x0127:
            if (r5 == 0) goto L_0x0131
            if (r11 == 0) goto L_0x0131
            java.lang.String r13 = "-a cannot be specified together with --split"
            r1.println(r13)
            return r2
        L_0x0131:
            if (r10 == 0) goto L_0x013b
            if (r11 == 0) goto L_0x013b
            java.lang.String r13 = "--secondary-dex cannot be specified together with --split"
            r1.println(r13)
            return r2
        L_0x013b:
            r13 = 0
            if (r20 == 0) goto L_0x0160
            boolean r16 = dalvik.system.DexFile.isValidCompilerFilter(r7)
            if (r16 != 0) goto L_0x015f
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r2 = "Error: \""
            r14.append(r2)
            r14.append(r7)
            java.lang.String r2 = "\" is not a valid compilation filter."
            r14.append(r2)
            java.lang.String r2 = r14.toString()
            r1.println(r2)
            r2 = 1
            return r2
        L_0x015f:
            r13 = r7
        L_0x0160:
            if (r21 == 0) goto L_0x01a3
            r2 = -1
            r16 = 0
            r14 = r16
        L_0x0167:
            r16 = r2
            java.lang.String[] r2 = com.android.server.pm.PackageManagerServiceCompilerMapping.REASON_STRINGS
            int r2 = r2.length
            if (r14 >= r2) goto L_0x017f
            java.lang.String[] r2 = com.android.server.pm.PackageManagerServiceCompilerMapping.REASON_STRINGS
            r2 = r2[r14]
            boolean r2 = r2.equals(r8)
            if (r2 == 0) goto L_0x017a
            r2 = r14
            goto L_0x0181
        L_0x017a:
            int r14 = r14 + 1
            r2 = r16
            goto L_0x0167
        L_0x017f:
            r2 = r16
        L_0x0181:
            r14 = -1
            if (r2 != r14) goto L_0x019c
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r22 = r7
            java.lang.String r7 = "Error: Unknown compilation reason: "
            r14.append(r7)
            r14.append(r8)
            java.lang.String r7 = r14.toString()
            r1.println(r7)
            r7 = 1
            return r7
        L_0x019c:
            r22 = r7
            java.lang.String r7 = com.android.server.pm.PackageManagerServiceCompilerMapping.getCompilerFilterForReason(r2)
            goto L_0x01a6
        L_0x01a3:
            r22 = r7
            r7 = r13
        L_0x01a6:
            r2 = 0
            if (r5 == 0) goto L_0x01b0
            android.content.pm.IPackageManager r13 = r0.mInterface
            java.util.List r2 = r13.getAllPackages()
            goto L_0x01c1
        L_0x01b0:
            java.lang.String r13 = r28.getNextArg()
            if (r13 != 0) goto L_0x01bd
            java.lang.String r14 = "Error: package name not specified"
            r1.println(r14)
            r14 = 1
            return r14
        L_0x01bd:
            java.util.List r2 = java.util.Collections.singletonList(r13)
        L_0x01c1:
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            r14 = r13
            r13 = 0
            java.util.Iterator r23 = r2.iterator()
        L_0x01cc:
            boolean r16 = r23.hasNext()
            if (r16 == 0) goto L_0x025f
            java.lang.Object r16 = r23.next()
            r24 = r8
            r8 = r16
            java.lang.String r8 = (java.lang.String) r8
            if (r6 == 0) goto L_0x01e6
            r25 = r6
            android.content.pm.IPackageManager r6 = r0.mInterface
            r6.clearApplicationProfileData(r8)
            goto L_0x01e8
        L_0x01e6:
            r25 = r6
        L_0x01e8:
            if (r5 == 0) goto L_0x0216
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            int r13 = r13 + 1
            r6.append(r13)
            r26 = r5
            java.lang.String r5 = "/"
            r6.append(r5)
            int r5 = r2.size()
            r6.append(r5)
            java.lang.String r5 = ": "
            r6.append(r5)
            r6.append(r8)
            java.lang.String r5 = r6.toString()
            r1.println(r5)
            r1.flush()
            r5 = r13
            goto L_0x0219
        L_0x0216:
            r26 = r5
            r5 = r13
        L_0x0219:
            r6 = 1
            if (r12 == 0) goto L_0x022c
            java.lang.Class<android.content.pm.PackageManagerInternal> r13 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r13 = com.android.server.LocalServices.getService(r13)
            android.content.pm.PackageManagerInternal r13 = (android.content.pm.PackageManagerInternal) r13
            boolean r6 = r13.compileLayouts(r8)
            r0 = r14
            r27 = r15
            goto L_0x024c
        L_0x022c:
            if (r10 == 0) goto L_0x0238
            android.content.pm.IPackageManager r13 = r0.mInterface
            boolean r13 = r13.performDexOptSecondary(r8, r7, r4)
            r0 = r14
            r27 = r15
            goto L_0x024b
        L_0x0238:
            android.content.pm.IPackageManager r13 = r0.mInterface
            r18 = 1
            r0 = r14
            r14 = r8
            r27 = r15
            r15 = r3
            r16 = r7
            r17 = r4
            r19 = r11
            boolean r13 = r13.performDexOptMode(r14, r15, r16, r17, r18, r19)
        L_0x024b:
            r6 = r13
        L_0x024c:
            if (r6 != 0) goto L_0x0251
            r0.add(r8)
        L_0x0251:
            r14 = r0
            r13 = r5
            r8 = r24
            r6 = r25
            r5 = r26
            r15 = r27
            r0 = r28
            goto L_0x01cc
        L_0x025f:
            r26 = r5
            r25 = r6
            r24 = r8
            r0 = r14
            r27 = r15
            boolean r5 = r0.isEmpty()
            if (r5 == 0) goto L_0x0275
            java.lang.String r5 = "Success"
            r1.println(r5)
            r5 = 0
            return r5
        L_0x0275:
            r5 = 0
            int r6 = r0.size()
            r8 = 1
            if (r6 != r8) goto L_0x029e
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "Failure: package "
            r6.append(r8)
            java.lang.Object r5 = r0.get(r5)
            java.lang.String r5 = (java.lang.String) r5
            r6.append(r5)
            java.lang.String r5 = " could not be compiled"
            r6.append(r5)
            java.lang.String r5 = r6.toString()
            r1.println(r5)
            r5 = 1
            return r5
        L_0x029e:
            java.lang.String r5 = "Failure: the following packages could not be compiled: "
            r1.print(r5)
            r5 = 1
            java.util.Iterator r6 = r0.iterator()
        L_0x02a8:
            boolean r8 = r6.hasNext()
            if (r8 == 0) goto L_0x02c1
            java.lang.Object r8 = r6.next()
            java.lang.String r8 = (java.lang.String) r8
            if (r5 == 0) goto L_0x02b8
            r5 = 0
            goto L_0x02bd
        L_0x02b8:
            java.lang.String r14 = ", "
            r1.print(r14)
        L_0x02bd:
            r1.print(r8)
            goto L_0x02a8
        L_0x02c1:
            r1.println()
            r6 = 1
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerShellCommand.runCompile():int");
    }

    private int runreconcileSecondaryDexFiles() throws RemoteException {
        this.mInterface.reconcileSecondaryDexFiles(getNextArg());
        return 0;
    }

    public int runForceDexOpt() throws RemoteException {
        this.mInterface.forceDexOpt(getNextArgRequired());
        return 0;
    }

    private int runDexoptJob() throws RemoteException {
        List<String> list;
        List<String> packageNames = new ArrayList<>();
        while (true) {
            String nextArg = getNextArg();
            String arg = nextArg;
            if (nextArg == null) {
                break;
            }
            packageNames.add(arg);
        }
        IPackageManager iPackageManager = this.mInterface;
        if (packageNames.isEmpty()) {
            list = null;
        } else {
            list = packageNames;
        }
        boolean result = iPackageManager.runBackgroundDexoptJob(list);
        getOutPrintWriter().println(result ? "Success" : "Failure");
        return result ? 0 : -1;
    }

    private int runDumpProfiles() throws RemoteException {
        this.mInterface.dumpProfiles(getNextArg());
        return 0;
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    private int runSnapshotProfile() throws RemoteException {
        String baseCodePath;
        String codePath;
        String outputFileSuffix;
        OutputStream outStream;
        Throwable th;
        PrintWriter pw = getOutPrintWriter();
        String packageName = getNextArg();
        boolean isBootImage = PackageManagerService.PLATFORM_PACKAGE_NAME.equals(packageName);
        String codePath2 = null;
        while (true) {
            String nextArg = getNextArg();
            String opt = nextArg;
            boolean z = false;
            if (nextArg != null) {
                if (opt.hashCode() != -684928411 || !opt.equals("--code-path")) {
                    z = true;
                }
                if (z) {
                    pw.write("Unknown arg: " + opt);
                    return -1;
                } else if (isBootImage) {
                    pw.write("--code-path cannot be used for the boot image.");
                    return -1;
                } else {
                    codePath2 = getNextArg();
                }
            } else {
                if (!isBootImage) {
                    PackageInfo packageInfo = this.mInterface.getPackageInfo(packageName, 0, 0);
                    if (packageInfo == null) {
                        pw.write("Package not found " + packageName);
                        return -1;
                    }
                    String baseCodePath2 = packageInfo.applicationInfo.getBaseCodePath();
                    if (codePath2 == null) {
                        codePath = baseCodePath2;
                        baseCodePath = baseCodePath2;
                    } else {
                        codePath = codePath2;
                        baseCodePath = baseCodePath2;
                    }
                } else {
                    codePath = codePath2;
                    baseCodePath = null;
                }
                SnapshotRuntimeProfileCallback callback = new SnapshotRuntimeProfileCallback();
                String callingPackage = Binder.getCallingUid() == 0 ? "root" : NotificationShellCmd.NOTIFICATION_PACKAGE;
                int profileType = isBootImage ? 1 : 0;
                if (!this.mInterface.getArtManager().isRuntimeProfilingEnabled(profileType, callingPackage)) {
                    pw.println("Error: Runtime profiling is not enabled");
                    return -1;
                }
                int i = profileType;
                String str = callingPackage;
                this.mInterface.getArtManager().snapshotRuntimeProfile(profileType, packageName, codePath, callback, callingPackage);
                if (!callback.waitTillDone()) {
                    pw.println("Error: callback not called");
                    return callback.mErrCode;
                }
                try {
                    InputStream inStream = new ParcelFileDescriptor.AutoCloseInputStream(callback.mProfileReadFd);
                    if (!isBootImage) {
                        try {
                            if (!Objects.equals(baseCodePath, codePath)) {
                                outputFileSuffix = STDIN_PATH + new File(codePath).getName();
                                String outputProfilePath = ART_PROFILE_SNAPSHOT_DEBUG_LOCATION + packageName + outputFileSuffix + ".prof";
                                outStream = new FileOutputStream(outputProfilePath);
                                Streams.copy(inStream, outStream);
                                $closeResource((Throwable) null, outStream);
                                Os.chmod(outputProfilePath, 420);
                                $closeResource((Throwable) null, inStream);
                                return 0;
                            }
                        } catch (Throwable th2) {
                            Throwable th3 = th2;
                            try {
                                throw th3;
                            } catch (Throwable th4) {
                                Throwable th5 = th4;
                                $closeResource(th3, inStream);
                                throw th5;
                            }
                        }
                    }
                    outputFileSuffix = "";
                    String outputProfilePath2 = ART_PROFILE_SNAPSHOT_DEBUG_LOCATION + packageName + outputFileSuffix + ".prof";
                    outStream = new FileOutputStream(outputProfilePath2);
                    try {
                        Streams.copy(inStream, outStream);
                        $closeResource((Throwable) null, outStream);
                        Os.chmod(outputProfilePath2, 420);
                        $closeResource((Throwable) null, inStream);
                        return 0;
                    } catch (Throwable th6) {
                        Throwable th7 = th6;
                        $closeResource(th, outStream);
                        throw th7;
                    }
                } catch (ErrnoException | IOException e) {
                    pw.println("Error when reading the profile fd: " + e.getMessage());
                    e.printStackTrace(pw);
                    return -1;
                }
            }
        }
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

    private static class SnapshotRuntimeProfileCallback extends ISnapshotRuntimeProfileCallback.Stub {
        private CountDownLatch mDoneSignal;
        /* access modifiers changed from: private */
        public int mErrCode;
        /* access modifiers changed from: private */
        public ParcelFileDescriptor mProfileReadFd;
        private boolean mSuccess;

        private SnapshotRuntimeProfileCallback() {
            this.mSuccess = false;
            this.mErrCode = -1;
            this.mProfileReadFd = null;
            this.mDoneSignal = new CountDownLatch(1);
        }

        public void onSuccess(ParcelFileDescriptor profileReadFd) {
            this.mSuccess = true;
            try {
                this.mProfileReadFd = profileReadFd.dup();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mDoneSignal.countDown();
        }

        public void onError(int errCode) {
            this.mSuccess = false;
            this.mErrCode = errCode;
            this.mDoneSignal.countDown();
        }

        /* access modifiers changed from: package-private */
        public boolean waitTillDone() {
            boolean done = false;
            try {
                done = this.mDoneSignal.await(10000000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            }
            return done && this.mSuccess;
        }
    }

    private int runUninstall() throws RemoteException {
        String str;
        PrintWriter pw = getOutPrintWriter();
        int flags = 0;
        int userId = -1;
        long versionCode = -1;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            char c = 65535;
            if (nextOption != null) {
                int hashCode = opt.hashCode();
                if (hashCode != 1502) {
                    if (hashCode != 1333469547) {
                        if (hashCode == 1884113221 && opt.equals("--versionCode")) {
                            c = 2;
                        }
                    } else if (opt.equals("--user")) {
                        c = 1;
                    }
                } else if (opt.equals("-k")) {
                    c = 0;
                }
                if (c == 0) {
                    flags |= 1;
                } else if (c == 1) {
                    userId = UserHandle.parseUserArg(getNextArgRequired());
                } else if (c != 2) {
                    pw.println("Error: Unknown option: " + opt);
                    return 1;
                } else {
                    versionCode = Long.parseLong(getNextArgRequired());
                }
            } else {
                String packageName = getNextArg();
                if (packageName == null) {
                    pw.println("Error: package name not specified");
                    return 1;
                }
                String splitName = getNextArg();
                if (splitName != null) {
                    return runRemoveSplit(packageName, splitName);
                }
                int userId2 = translateUserId(userId, true, "runUninstall");
                LocalIntentReceiver receiver = new LocalIntentReceiver();
                PackageManagerInternal internal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
                if (internal.isApexPackage(packageName)) {
                    str = "]";
                    PackageManagerInternal packageManagerInternal = internal;
                    String str2 = splitName;
                    internal.uninstallApex(packageName, versionCode, userId2, receiver.getIntentSender());
                } else {
                    str = "]";
                    PackageManagerInternal packageManagerInternal2 = internal;
                    String str3 = splitName;
                    if (userId2 == -1) {
                        userId2 = 0;
                        flags |= 2;
                    } else {
                        PackageInfo info = this.mInterface.getPackageInfo(packageName, BroadcastQueueInjector.FLAG_IMMUTABLE, userId2);
                        if (info == null) {
                            pw.println("Failure [not installed for " + userId2 + str);
                            return 1;
                        }
                        if ((info.applicationInfo.flags & 1) != 0) {
                            flags |= 4;
                        }
                    }
                    this.mInterface.getPackageInstaller().uninstall(new VersionedPackage(packageName, versionCode), (String) null, flags, receiver.getIntentSender(), userId2);
                }
                Intent result = receiver.getResult();
                if (result.getIntExtra("android.content.pm.extra.STATUS", 1) == 0) {
                    pw.println("Success");
                    return 0;
                }
                pw.println("Failure [" + result.getStringExtra("android.content.pm.extra.STATUS_MESSAGE") + str);
                return 1;
            }
        }
    }

    private int runRemoveSplit(String packageName, String splitName) throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(2);
        sessionParams.installFlags = 2 | sessionParams.installFlags;
        sessionParams.appPackageName = packageName;
        int sessionId = doCreateSession(sessionParams, (String) null, -1);
        boolean abandonSession = true;
        try {
            if (doRemoveSplit(sessionId, splitName, false) != 0) {
                if (abandonSession) {
                    try {
                        doAbandonSession(sessionId, false);
                    } catch (Exception e) {
                    }
                }
                return 1;
            } else if (doCommitSession(sessionId, false) != 0) {
                if (abandonSession) {
                    try {
                        doAbandonSession(sessionId, false);
                    } catch (Exception e2) {
                    }
                }
                return 1;
            } else {
                abandonSession = false;
                pw.println("Success");
                return 0;
            }
        } finally {
            if (abandonSession) {
                try {
                    doAbandonSession(sessionId, false);
                } catch (Exception e3) {
                }
            }
        }
    }

    static class ClearDataObserver extends IPackageDataObserver.Stub {
        boolean finished;
        boolean result;

        ClearDataObserver() {
        }

        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            synchronized (this) {
                this.finished = true;
                this.result = succeeded;
                notifyAll();
            }
        }
    }

    private int runClear() throws RemoteException {
        int userId = 0;
        String option = getNextOption();
        if (option != null && option.equals("--user")) {
            userId = UserHandle.parseUserArg(getNextArgRequired());
        }
        String pkg = getNextArg();
        if (pkg == null) {
            getErrPrintWriter().println("Error: no package specified");
            return 1;
        }
        ClearDataObserver obs = new ClearDataObserver();
        ActivityManager.getService().clearApplicationUserData(pkg, false, obs, userId);
        synchronized (obs) {
            while (!obs.finished) {
                try {
                    obs.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        if (obs.result) {
            getOutPrintWriter().println("Success");
            return 0;
        }
        getErrPrintWriter().println("Failed");
        return 1;
    }

    private static String enabledSettingToString(int state) {
        if (state == 0) {
            return BatteryService.HealthServiceWrapper.INSTANCE_VENDOR;
        }
        if (state == 1) {
            return "enabled";
        }
        if (state == 2) {
            return "disabled";
        }
        if (state == 3) {
            return "disabled-user";
        }
        if (state != 4) {
            return UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN;
        }
        return "disabled-until-used";
    }

    private int runSetEnabledSetting(int state) throws RemoteException {
        int userId = 0;
        String option = getNextOption();
        if (option != null && option.equals("--user")) {
            userId = UserHandle.parseUserArg(getNextArgRequired());
        }
        String pkg = getNextArg();
        if (pkg == null) {
            getErrPrintWriter().println("Error: no package or component specified");
            return 1;
        }
        ComponentName cn = ComponentName.unflattenFromString(pkg);
        if (cn == null) {
            IPackageManager iPackageManager = this.mInterface;
            iPackageManager.setApplicationEnabledSetting(pkg, state, 0, userId, "shell:" + Process.myUid());
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("Package " + pkg + " new state: " + enabledSettingToString(this.mInterface.getApplicationEnabledSetting(pkg, userId)));
            return 0;
        }
        this.mInterface.setComponentEnabledSetting(cn, state, 0, userId);
        PrintWriter outPrintWriter2 = getOutPrintWriter();
        outPrintWriter2.println("Component " + cn.toShortString() + " new state: " + enabledSettingToString(this.mInterface.getComponentEnabledSetting(cn, userId)));
        return 0;
    }

    private int runSetHiddenSetting(boolean state) throws RemoteException {
        int userId = 0;
        String option = getNextOption();
        if (option != null && option.equals("--user")) {
            userId = UserHandle.parseUserArg(getNextArgRequired());
        }
        String pkg = getNextArg();
        if (pkg == null) {
            getErrPrintWriter().println("Error: no package or component specified");
            return 1;
        }
        this.mInterface.setApplicationHiddenSettingAsUser(pkg, state, userId);
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Package " + pkg + " new hidden state: " + this.mInterface.getApplicationHiddenSettingAsUser(pkg, userId));
        return 0;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x002c, code lost:
        if (r13.equals("--user") != false) goto L_0x0076;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runSuspend(boolean r18) {
        /*
            r17 = this;
            r1 = r17
            java.io.PrintWriter r2 = r17.getOutPrintWriter()
            r0 = 0
            r3 = 0
            android.os.PersistableBundle r7 = new android.os.PersistableBundle
            r7.<init>()
            android.os.PersistableBundle r8 = new android.os.PersistableBundle
            r8.<init>()
            r12 = r0
        L_0x0013:
            java.lang.String r0 = r17.getNextOption()
            r13 = r0
            r14 = 0
            r15 = 1
            if (r0 == 0) goto L_0x00e3
            r0 = -1
            int r4 = r13.hashCode()
            r5 = 4
            switch(r4) {
                case -39471105: goto L_0x006b;
                case 42995488: goto L_0x0061;
                case 42995496: goto L_0x0057;
                case 42995503: goto L_0x004d;
                case 43006059: goto L_0x0043;
                case 43006067: goto L_0x0039;
                case 43006074: goto L_0x002f;
                case 1333469547: goto L_0x0026;
                default: goto L_0x0025;
            }
        L_0x0025:
            goto L_0x0075
        L_0x0026:
            java.lang.String r4 = "--user"
            boolean r4 = r13.equals(r4)
            if (r4 == 0) goto L_0x0025
            goto L_0x0076
        L_0x002f:
            java.lang.String r4 = "--les"
            boolean r4 = r13.equals(r4)
            if (r4 == 0) goto L_0x0025
            r14 = 6
            goto L_0x0076
        L_0x0039:
            java.lang.String r4 = "--lel"
            boolean r4 = r13.equals(r4)
            if (r4 == 0) goto L_0x0025
            r14 = 5
            goto L_0x0076
        L_0x0043:
            java.lang.String r4 = "--led"
            boolean r4 = r13.equals(r4)
            if (r4 == 0) goto L_0x0025
            r14 = 7
            goto L_0x0076
        L_0x004d:
            java.lang.String r4 = "--aes"
            boolean r4 = r13.equals(r4)
            if (r4 == 0) goto L_0x0025
            r14 = 3
            goto L_0x0076
        L_0x0057:
            java.lang.String r4 = "--ael"
            boolean r4 = r13.equals(r4)
            if (r4 == 0) goto L_0x0025
            r14 = 2
            goto L_0x0076
        L_0x0061:
            java.lang.String r4 = "--aed"
            boolean r4 = r13.equals(r4)
            if (r4 == 0) goto L_0x0025
            r14 = r5
            goto L_0x0076
        L_0x006b:
            java.lang.String r4 = "--dialogMessage"
            boolean r4 = r13.equals(r4)
            if (r4 == 0) goto L_0x0025
            r14 = r15
            goto L_0x0076
        L_0x0075:
            r14 = r0
        L_0x0076:
            switch(r14) {
                case 0: goto L_0x00d8;
                case 1: goto L_0x00d2;
                case 2: goto L_0x008e;
                case 3: goto L_0x008e;
                case 4: goto L_0x008e;
                case 5: goto L_0x008e;
                case 6: goto L_0x008e;
                case 7: goto L_0x008e;
                default: goto L_0x0079;
            }
        L_0x0079:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "Error: Unknown option: "
            r0.append(r4)
            r0.append(r13)
            java.lang.String r0 = r0.toString()
            r2.println(r0)
            return r15
        L_0x008e:
            java.lang.String r0 = r17.getNextArgRequired()
            java.lang.String r4 = r17.getNextArgRequired()
            if (r18 != 0) goto L_0x0099
            goto L_0x00e1
        L_0x0099:
            java.lang.String r6 = "--a"
            boolean r6 = r13.startsWith(r6)
            if (r6 == 0) goto L_0x00a3
            r6 = r7
            goto L_0x00a4
        L_0x00a3:
            r6 = r8
        L_0x00a4:
            char r5 = r13.charAt(r5)
            r9 = 100
            if (r5 == r9) goto L_0x00c5
            r9 = 108(0x6c, float:1.51E-43)
            if (r5 == r9) goto L_0x00b9
            r9 = 115(0x73, float:1.61E-43)
            if (r5 == r9) goto L_0x00b5
            goto L_0x00d1
        L_0x00b5:
            r6.putString(r0, r4)
            goto L_0x00d1
        L_0x00b9:
            java.lang.Long r5 = java.lang.Long.valueOf(r4)
            long r9 = r5.longValue()
            r6.putLong(r0, r9)
            goto L_0x00d1
        L_0x00c5:
            java.lang.Double r5 = java.lang.Double.valueOf(r4)
            double r9 = r5.doubleValue()
            r6.putDouble(r0, r9)
        L_0x00d1:
            goto L_0x00e1
        L_0x00d2:
            java.lang.String r0 = r17.getNextArgRequired()
            r3 = r0
            goto L_0x00e1
        L_0x00d8:
            java.lang.String r0 = r17.getNextArgRequired()
            int r0 = android.os.UserHandle.parseUserArg(r0)
            r12 = r0
        L_0x00e1:
            goto L_0x0013
        L_0x00e3:
            java.lang.String r11 = r17.getNextArg()
            if (r11 != 0) goto L_0x00ef
            java.lang.String r0 = "Error: package name not specified"
            r2.println(r0)
            return r15
        L_0x00ef:
            int r0 = android.os.Binder.getCallingUid()
            if (r0 != 0) goto L_0x00f9
            java.lang.String r0 = "root"
            goto L_0x00fb
        L_0x00f9:
            java.lang.String r0 = "com.android.shell"
        L_0x00fb:
            r10 = r0
            boolean r0 = android.text.TextUtils.isEmpty(r3)
            if (r0 != 0) goto L_0x0112
            android.content.pm.SuspendDialogInfo$Builder r0 = new android.content.pm.SuspendDialogInfo$Builder
            r0.<init>()
            android.content.pm.SuspendDialogInfo$Builder r0 = r0.setMessage(r3)
            android.content.pm.SuspendDialogInfo r0 = r0.build()
            r16 = r0
            goto L_0x0115
        L_0x0112:
            r0 = 0
            r16 = r0
        L_0x0115:
            android.content.pm.IPackageManager r4 = r1.mInterface     // Catch:{ RemoteException | IllegalArgumentException -> 0x0149 }
            java.lang.String[] r5 = new java.lang.String[r15]     // Catch:{ RemoteException | IllegalArgumentException -> 0x0149 }
            r5[r14] = r11     // Catch:{ RemoteException | IllegalArgumentException -> 0x0149 }
            r6 = r18
            r9 = r16
            r15 = r11
            r11 = r12
            r4.setPackagesSuspendedAsUser(r5, r6, r7, r8, r9, r10, r11)     // Catch:{ RemoteException | IllegalArgumentException -> 0x0147 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RemoteException | IllegalArgumentException -> 0x0147 }
            r0.<init>()     // Catch:{ RemoteException | IllegalArgumentException -> 0x0147 }
            java.lang.String r4 = "Package "
            r0.append(r4)     // Catch:{ RemoteException | IllegalArgumentException -> 0x0147 }
            r0.append(r15)     // Catch:{ RemoteException | IllegalArgumentException -> 0x0147 }
            java.lang.String r4 = " new suspended state: "
            r0.append(r4)     // Catch:{ RemoteException | IllegalArgumentException -> 0x0147 }
            android.content.pm.IPackageManager r4 = r1.mInterface     // Catch:{ RemoteException | IllegalArgumentException -> 0x0147 }
            boolean r4 = r4.isPackageSuspendedForUser(r15, r12)     // Catch:{ RemoteException | IllegalArgumentException -> 0x0147 }
            r0.append(r4)     // Catch:{ RemoteException | IllegalArgumentException -> 0x0147 }
            java.lang.String r0 = r0.toString()     // Catch:{ RemoteException | IllegalArgumentException -> 0x0147 }
            r2.println(r0)     // Catch:{ RemoteException | IllegalArgumentException -> 0x0147 }
            return r14
        L_0x0147:
            r0 = move-exception
            goto L_0x014b
        L_0x0149:
            r0 = move-exception
            r15 = r11
        L_0x014b:
            java.lang.String r4 = r0.toString()
            r2.println(r4)
            r4 = 1
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerShellCommand.runSuspend(boolean):int");
    }

    private int runGrantRevokePermission(boolean grant) throws RemoteException {
        int userId = 0;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                break;
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            }
        }
        String pkg = getNextArg();
        if (pkg == null) {
            getErrPrintWriter().println("Error: no package specified");
            return 1;
        }
        String perm = getNextArg();
        if (perm == null) {
            getErrPrintWriter().println("Error: no permission specified");
            return 1;
        } else if (grant) {
            this.mInterface.grantRuntimePermission(pkg, perm, userId);
            return 0;
        } else {
            this.mInterface.revokeRuntimePermission(pkg, perm, userId);
            return 0;
        }
    }

    private int runResetPermissions() throws RemoteException {
        this.mInterface.resetRuntimePermissions();
        return 0;
    }

    private int runSetPermissionEnforced() throws RemoteException {
        String permission = getNextArg();
        if (permission == null) {
            getErrPrintWriter().println("Error: no permission specified");
            return 1;
        }
        String enforcedRaw = getNextArg();
        if (enforcedRaw == null) {
            getErrPrintWriter().println("Error: no enforcement specified");
            return 1;
        }
        this.mInterface.setPermissionEnforced(permission, Boolean.parseBoolean(enforcedRaw));
        return 0;
    }

    private boolean isVendorApp(String pkg) {
        try {
            PackageInfo info = this.mInterface.getPackageInfo(pkg, 0, 0);
            if (info == null || !info.applicationInfo.isVendor()) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    private boolean isProductApp(String pkg) {
        try {
            PackageInfo info = this.mInterface.getPackageInfo(pkg, 0, 0);
            if (info == null || !info.applicationInfo.isProduct()) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    private boolean isProductServicesApp(String pkg) {
        try {
            PackageInfo info = this.mInterface.getPackageInfo(pkg, 0, 0);
            if (info == null || !info.applicationInfo.isProductServices()) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    private int runGetPrivappPermissions() {
        ArraySet<String> privAppPermissions;
        String pkg = getNextArg();
        if (pkg == null) {
            getErrPrintWriter().println("Error: no package specified.");
            return 1;
        }
        if (isVendorApp(pkg)) {
            privAppPermissions = SystemConfig.getInstance().getVendorPrivAppPermissions(pkg);
        } else if (isProductApp(pkg)) {
            privAppPermissions = SystemConfig.getInstance().getProductPrivAppPermissions(pkg);
        } else if (isProductServicesApp(pkg)) {
            privAppPermissions = SystemConfig.getInstance().getProductServicesPrivAppPermissions(pkg);
        } else {
            privAppPermissions = SystemConfig.getInstance().getPrivAppPermissions(pkg);
        }
        getOutPrintWriter().println(privAppPermissions == null ? "{}" : privAppPermissions.toString());
        return 0;
    }

    private int runGetPrivappDenyPermissions() {
        ArraySet<String> privAppPermissions;
        String pkg = getNextArg();
        if (pkg == null) {
            getErrPrintWriter().println("Error: no package specified.");
            return 1;
        }
        if (isVendorApp(pkg)) {
            privAppPermissions = SystemConfig.getInstance().getVendorPrivAppDenyPermissions(pkg);
        } else if (isProductApp(pkg)) {
            privAppPermissions = SystemConfig.getInstance().getProductPrivAppDenyPermissions(pkg);
        } else if (isProductServicesApp(pkg)) {
            privAppPermissions = SystemConfig.getInstance().getProductServicesPrivAppDenyPermissions(pkg);
        } else {
            privAppPermissions = SystemConfig.getInstance().getPrivAppDenyPermissions(pkg);
        }
        getOutPrintWriter().println(privAppPermissions == null ? "{}" : privAppPermissions.toString());
        return 0;
    }

    private int runGetOemPermissions() {
        String pkg = getNextArg();
        if (pkg == null) {
            getErrPrintWriter().println("Error: no package specified.");
            return 1;
        }
        Map<String, Boolean> oemPermissions = SystemConfig.getInstance().getOemPermissions(pkg);
        if (oemPermissions == null || oemPermissions.isEmpty()) {
            getOutPrintWriter().println("{}");
            return 0;
        }
        oemPermissions.forEach(new BiConsumer() {
            public final void accept(Object obj, Object obj2) {
                PackageManagerShellCommand.this.lambda$runGetOemPermissions$0$PackageManagerShellCommand((String) obj, (Boolean) obj2);
            }
        });
        return 0;
    }

    public /* synthetic */ void lambda$runGetOemPermissions$0$PackageManagerShellCommand(String permission, Boolean granted) {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println(permission + " granted:" + granted);
    }

    private String linkStateToString(int state) {
        if (state == 0) {
            return "undefined";
        }
        if (state == 1) {
            return "ask";
        }
        if (state == 2) {
            return "always";
        }
        if (state == 3) {
            return "never";
        }
        if (state == 4) {
            return "always ask";
        }
        return "Unknown link state: " + state;
    }

    private int runSetAppLink() throws RemoteException {
        int newMode;
        int userId = 0;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                String pkg = getNextArg();
                if (pkg == null) {
                    getErrPrintWriter().println("Error: no package specified.");
                    return 1;
                }
                String modeString = getNextArg();
                if (modeString == null) {
                    getErrPrintWriter().println("Error: no app link state specified.");
                    return 1;
                }
                String lowerCase = modeString.toLowerCase();
                char c = 65535;
                switch (lowerCase.hashCode()) {
                    case -1414557169:
                        if (lowerCase.equals("always")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1038130864:
                        if (lowerCase.equals("undefined")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 96889:
                        if (lowerCase.equals("ask")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 104712844:
                        if (lowerCase.equals("never")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1182785979:
                        if (lowerCase.equals("always-ask")) {
                            c = 3;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    newMode = 0;
                } else if (c == 1) {
                    newMode = 2;
                } else if (c == 2) {
                    newMode = 1;
                } else if (c == 3) {
                    newMode = 4;
                } else if (c != 4) {
                    getErrPrintWriter().println("Error: unknown app link state '" + modeString + "'");
                    return 1;
                } else {
                    newMode = 3;
                }
                PackageInfo info = this.mInterface.getPackageInfo(pkg, 0, userId);
                if (info == null) {
                    getErrPrintWriter().println("Error: package " + pkg + " not found.");
                    return 1;
                } else if ((info.applicationInfo.privateFlags & 16) == 0) {
                    getErrPrintWriter().println("Error: package " + pkg + " does not handle web links.");
                    return 1;
                } else if (this.mInterface.updateIntentVerificationStatus(pkg, newMode, userId)) {
                    return 0;
                } else {
                    getErrPrintWriter().println("Error: unable to update app link status for " + pkg);
                    return 1;
                }
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                getErrPrintWriter().println("Error: unknown option: " + opt);
                return 1;
            }
        }
    }

    private int runGetAppLink() throws RemoteException {
        int userId = 0;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                String pkg = getNextArg();
                if (pkg == null) {
                    getErrPrintWriter().println("Error: no package specified.");
                    return 1;
                }
                PackageInfo info = this.mInterface.getPackageInfo(pkg, 0, userId);
                if (info == null) {
                    PrintWriter errPrintWriter = getErrPrintWriter();
                    errPrintWriter.println("Error: package " + pkg + " not found.");
                    return 1;
                } else if ((info.applicationInfo.privateFlags & 16) == 0) {
                    PrintWriter errPrintWriter2 = getErrPrintWriter();
                    errPrintWriter2.println("Error: package " + pkg + " does not handle web links.");
                    return 1;
                } else {
                    getOutPrintWriter().println(linkStateToString(this.mInterface.getIntentVerificationStatus(pkg, userId)));
                    return 0;
                }
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter3 = getErrPrintWriter();
                errPrintWriter3.println("Error: unknown option: " + opt);
                return 1;
            }
        }
    }

    private int runTrimCaches() throws RemoteException {
        long multiplier;
        String size;
        String volumeUuid;
        long multiplier2;
        String size2 = getNextArg();
        if (size2 == null) {
            getErrPrintWriter().println("Error: no size specified");
            return 1;
        }
        int len = size2.length();
        char c = size2.charAt(len - 1);
        if (c < '0' || c > '9') {
            if (c == 'K' || c == 'k') {
                multiplier2 = 1024;
            } else if (c == 'M' || c == 'm') {
                multiplier2 = 1048576;
            } else if (c == 'G' || c == 'g') {
                multiplier2 = 1073741824;
            } else {
                getErrPrintWriter().println("Invalid suffix: " + c);
                return 1;
            }
            multiplier = multiplier2;
            size = size2.substring(0, len - 1);
        } else {
            multiplier = 1;
            size = size2;
        }
        try {
            long sizeVal = Long.parseLong(size) * multiplier;
            String volumeUuid2 = getNextArg();
            if ("internal".equals(volumeUuid2)) {
                volumeUuid = null;
            } else {
                volumeUuid = volumeUuid2;
            }
            ClearDataObserver obs = new ClearDataObserver();
            this.mInterface.freeStorageAndNotify(volumeUuid, sizeVal, 2, obs);
            synchronized (obs) {
                while (!obs.finished) {
                    try {
                        obs.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            return 0;
        } catch (NumberFormatException e2) {
            NumberFormatException numberFormatException = e2;
            getErrPrintWriter().println("Error: expected number at: " + size);
            return 1;
        }
    }

    private static boolean isNumber(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int runCreateUser() throws RemoteException {
        UserInfo info;
        int userId = -1;
        int flags = 0;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                String arg = getNextArg();
                if (arg == null) {
                    getErrPrintWriter().println("Error: no user name specified.");
                    return 1;
                }
                String name = arg;
                IUserManager um = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
                IAccountManager accm = IAccountManager.Stub.asInterface(ServiceManager.getService("account"));
                if ((flags & 8) != 0) {
                    int parentUserId = userId >= 0 ? userId : 0;
                    info = um.createRestrictedProfile(name, parentUserId);
                    accm.addSharedAccountsFromParentUser(parentUserId, userId, Process.myUid() == 0 ? "root" : NotificationShellCmd.NOTIFICATION_PACKAGE);
                } else if (userId < 0) {
                    info = um.createUser(name, flags);
                } else {
                    info = um.createProfileForUser(name, flags, userId, (String[]) null);
                }
                if (info != null) {
                    PrintWriter outPrintWriter = getOutPrintWriter();
                    outPrintWriter.println("Success: created user id " + info.id);
                    return 0;
                }
                getErrPrintWriter().println("Error: couldn't create User.");
                return 1;
            } else if ("--profileOf".equals(opt)) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else if ("--managed".equals(opt)) {
                flags |= 32;
            } else if ("--restricted".equals(opt)) {
                flags |= 8;
            } else if ("--ephemeral".equals(opt)) {
                flags |= 256;
            } else if ("--guest".equals(opt)) {
                flags |= 4;
            } else if ("--demo".equals(opt)) {
                flags |= 512;
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: unknown option " + opt);
                return 1;
            }
        }
    }

    public int runRemoveUser() throws RemoteException {
        String arg = getNextArg();
        if (arg == null) {
            getErrPrintWriter().println("Error: no user id specified.");
            return 1;
        }
        int userId = UserHandle.parseUserArg(arg);
        if (IUserManager.Stub.asInterface(ServiceManager.getService("user")).removeUser(userId)) {
            getOutPrintWriter().println("Success: removed user");
            return 0;
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Error: couldn't remove user id " + userId);
        return 1;
    }

    public int runSetUserRestriction() throws RemoteException {
        boolean value;
        int userId = 0;
        String opt = getNextOption();
        if (opt != null && "--user".equals(opt)) {
            userId = UserHandle.parseUserArg(getNextArgRequired());
        }
        String restriction = getNextArg();
        String arg = getNextArg();
        if (SplitScreenReporter.ACTION_ENTER_SPLIT.equals(arg)) {
            value = true;
        } else if ("0".equals(arg)) {
            value = false;
        } else {
            getErrPrintWriter().println("Error: valid value not specified");
            return 1;
        }
        IUserManager.Stub.asInterface(ServiceManager.getService("user")).setUserRestriction(restriction, value, userId);
        return 0;
    }

    public int runGetMaxUsers() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Maximum supported users: " + UserManager.getMaxSupportedUsers());
        return 0;
    }

    public int runGetMaxRunningUsers() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Maximum supported running users: " + ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getMaxRunningUsers());
        return 0;
    }

    private static class InstallParams {
        String installerPackageName;
        PackageInstaller.SessionParams sessionParams;
        int userId;

        private InstallParams() {
            this.userId = PmInjector.getDefaultUserId();
        }
    }

    private InstallParams makeInstallParams() {
        PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(1);
        InstallParams params = new InstallParams();
        params.sessionParams = sessionParams;
        sessionParams.installFlags |= DumpState.DUMP_CHANGES;
        boolean replaceExisting = true;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption != null) {
                char c = 65535;
                switch (opt.hashCode()) {
                    case -2091380650:
                        if (opt.equals("--install-reason")) {
                            c = 22;
                            break;
                        }
                        break;
                    case -1950997763:
                        if (opt.equals("--force-uuid")) {
                            c = 23;
                            break;
                        }
                        break;
                    case -1777984902:
                        if (opt.equals("--dont-kill")) {
                            c = 8;
                            break;
                        }
                        break;
                    case -1313152697:
                        if (opt.equals("--install-location")) {
                            c = 21;
                            break;
                        }
                        break;
                    case -1137116608:
                        if (opt.equals("--instantapp")) {
                            c = 17;
                            break;
                        }
                        break;
                    case -951415743:
                        if (opt.equals("--instant")) {
                            c = 16;
                            break;
                        }
                        break;
                    case -706813505:
                        if (opt.equals("--referrer")) {
                            c = 10;
                            break;
                        }
                        break;
                    case -653924786:
                        if (opt.equals("--enable-rollback")) {
                            c = 28;
                            break;
                        }
                        break;
                    case -170474990:
                        if (opt.equals("--multi-package")) {
                            c = 26;
                            break;
                        }
                        break;
                    case 1477:
                        if (opt.equals("-R")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1478:
                        if (opt.equals("-S")) {
                            c = 13;
                            break;
                        }
                        break;
                    case 1495:
                        if (opt.equals("-d")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 1497:
                        if (opt.equals("-f")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1498:
                        if (opt.equals("-g")) {
                            c = 6;
                            break;
                        }
                        break;
                    case NetworkConstants.ETHER_MTU:
                        if (opt.equals("-i")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1507:
                        if (opt.equals("-p")) {
                            c = 11;
                            break;
                        }
                        break;
                    case 1509:
                        if (opt.equals("-r")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 1511:
                        if (opt.equals("-t")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 42995400:
                        if (opt.equals("--abi")) {
                            c = 14;
                            break;
                        }
                        break;
                    case 43010092:
                        if (opt.equals("--pkg")) {
                            c = 12;
                            break;
                        }
                        break;
                    case 148207464:
                        if (opt.equals("--originating-uri")) {
                            c = 9;
                            break;
                        }
                        break;
                    case 1051781117:
                        if (opt.equals("--ephemeral")) {
                            c = 15;
                            break;
                        }
                        break;
                    case 1067504745:
                        if (opt.equals("--preload")) {
                            c = 19;
                            break;
                        }
                        break;
                    case 1332870850:
                        if (opt.equals("--apex")) {
                            c = 25;
                            break;
                        }
                        break;
                    case 1333024815:
                        if (opt.equals("--full")) {
                            c = 18;
                            break;
                        }
                        break;
                    case 1333469547:
                        if (opt.equals("--user")) {
                            c = 20;
                            break;
                        }
                        break;
                    case 1494514835:
                        if (opt.equals("--restrict-permissions")) {
                            c = 7;
                            break;
                        }
                        break;
                    case 1507519174:
                        if (opt.equals("--staged")) {
                            c = 27;
                            break;
                        }
                        break;
                    case 2015272120:
                        if (opt.equals("--force-sdk")) {
                            c = 24;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                    case WindowManagerService.H.WAITING_FOR_DRAWN_TIMEOUT /*24*/:
                        break;
                    case 1:
                        replaceExisting = false;
                        break;
                    case 2:
                        params.installerPackageName = getNextArg();
                        if (params.installerPackageName != null) {
                            break;
                        } else {
                            throw new IllegalArgumentException("Missing installer package");
                        }
                    case 3:
                        sessionParams.installFlags |= 4;
                        break;
                    case 4:
                        sessionParams.installFlags |= 16;
                        break;
                    case 5:
                        sessionParams.installFlags |= 128;
                        break;
                    case 6:
                        sessionParams.installFlags |= 256;
                        break;
                    case 7:
                        sessionParams.installFlags &= -4194305;
                        break;
                    case 8:
                        sessionParams.installFlags |= 4096;
                        break;
                    case 9:
                        sessionParams.originatingUri = Uri.parse(getNextArg());
                        break;
                    case 10:
                        sessionParams.referrerUri = Uri.parse(getNextArg());
                        break;
                    case 11:
                        sessionParams.mode = 2;
                        sessionParams.appPackageName = getNextArg();
                        if (sessionParams.appPackageName != null) {
                            break;
                        } else {
                            throw new IllegalArgumentException("Missing inherit package name");
                        }
                    case 12:
                        sessionParams.appPackageName = getNextArg();
                        if (sessionParams.appPackageName != null) {
                            break;
                        } else {
                            throw new IllegalArgumentException("Missing package name");
                        }
                    case 13:
                        long sizeBytes = Long.parseLong(getNextArg());
                        if (sizeBytes > 0) {
                            sessionParams.setSize(sizeBytes);
                            break;
                        } else {
                            throw new IllegalArgumentException("Size must be positive");
                        }
                    case 14:
                        sessionParams.abiOverride = checkAbiArgument(getNextArg());
                        break;
                    case 15:
                    case 16:
                    case 17:
                        sessionParams.setInstallAsInstantApp(true);
                        break;
                    case 18:
                        sessionParams.setInstallAsInstantApp(false);
                        break;
                    case 19:
                        sessionParams.setInstallAsVirtualPreload();
                        break;
                    case 20:
                        params.userId = UserHandle.parseUserArg(getNextArgRequired());
                        break;
                    case 21:
                        sessionParams.installLocation = Integer.parseInt(getNextArg());
                        break;
                    case 22:
                        sessionParams.installReason = Integer.parseInt(getNextArg());
                        break;
                    case WindowManagerService.H.BOOT_TIMEOUT /*23*/:
                        sessionParams.installFlags |= 512;
                        sessionParams.volumeUuid = getNextArg();
                        if (!"internal".equals(sessionParams.volumeUuid)) {
                            break;
                        } else {
                            sessionParams.volumeUuid = null;
                            break;
                        }
                    case WindowManagerService.H.SHOW_STRICT_MODE_VIOLATION /*25*/:
                        sessionParams.setInstallAsApex();
                        sessionParams.setStaged();
                        break;
                    case 26:
                        sessionParams.setMultiPackage();
                        break;
                    case 27:
                        sessionParams.setStaged();
                        break;
                    case MusicView.WAVE_LINE_NUM:
                        if (params.installerPackageName == null) {
                            params.installerPackageName = NotificationShellCmd.NOTIFICATION_PACKAGE;
                        }
                        sessionParams.installFlags |= DumpState.DUMP_DOMAIN_PREFERRED;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown option " + opt);
                }
            } else {
                if (replaceExisting) {
                    sessionParams.installFlags |= 2;
                }
                return params;
            }
        }
    }

    private int runSetHomeActivity() {
        String pkgName;
        PrintWriter pw = getOutPrintWriter();
        int userId = 0;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption != null) {
                char c = 65535;
                if (opt.hashCode() == 1333469547 && opt.equals("--user")) {
                    c = 0;
                }
                if (c != 0) {
                    pw.println("Error: Unknown option: " + opt);
                    return 1;
                }
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                String component = getNextArg();
                if (component.indexOf(47) < 0) {
                    pkgName = component;
                } else {
                    ComponentName componentName = ComponentName.unflattenFromString(component);
                    if (componentName == null) {
                        pw.println("Error: invalid component name");
                        return 1;
                    }
                    pkgName = componentName.getPackageName();
                }
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                try {
                    IRoleManager.Stub.asInterface(ServiceManager.getServiceOrThrow("role")).addRoleHolderAsUser("android.app.role.HOME", pkgName, 0, userId, new RemoteCallback(new RemoteCallback.OnResultListener(future) {
                        private final /* synthetic */ CompletableFuture f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void onResult(Bundle bundle) {
                            PackageManagerShellCommand.lambda$runSetHomeActivity$1(this.f$0, bundle);
                        }
                    }));
                    if (future.get().booleanValue()) {
                        pw.println("Success");
                        return 0;
                    }
                    pw.println("Error: Failed to set default home.");
                    return 1;
                } catch (Exception e) {
                    pw.println(e.toString());
                    return 1;
                }
            }
        }
    }

    static /* synthetic */ void lambda$runSetHomeActivity$1(CompletableFuture future, Bundle res) {
        future.complete(Boolean.valueOf(res != null));
    }

    private int runSetInstaller() throws RemoteException {
        String targetPackage = getNextArg();
        String installerPackageName = getNextArg();
        if (targetPackage == null || installerPackageName == null) {
            getErrPrintWriter().println("Must provide both target and installer package names");
            return 1;
        }
        this.mInterface.setInstallerPackageName(targetPackage, installerPackageName);
        getOutPrintWriter().println("Success");
        return 0;
    }

    private int runGetInstantAppResolver() {
        PrintWriter pw = getOutPrintWriter();
        try {
            ComponentName instantAppsResolver = this.mInterface.getInstantAppResolverComponent();
            if (instantAppsResolver == null) {
                return 1;
            }
            pw.println(instantAppsResolver.flattenToString());
            return 0;
        } catch (Exception e) {
            pw.println(e.toString());
            return 1;
        }
    }

    private int runHasFeature() {
        int version;
        PrintWriter err = getErrPrintWriter();
        String featureName = getNextArg();
        if (featureName == null) {
            err.println("Error: expected FEATURE name");
            return 1;
        }
        String versionString = getNextArg();
        if (versionString == null) {
            version = 0;
        } else {
            try {
                version = Integer.parseInt(versionString);
            } catch (NumberFormatException e) {
                err.println("Error: illegal version number " + versionString);
                return 1;
            } catch (RemoteException e2) {
                err.println(e2.toString());
                return 1;
            }
        }
        boolean hasFeature = this.mInterface.hasSystemFeature(featureName, version);
        getOutPrintWriter().println(hasFeature);
        if (hasFeature) {
            return 0;
        }
        return 1;
    }

    private int runDump() {
        String pkg = getNextArg();
        if (pkg == null) {
            getErrPrintWriter().println("Error: no package specified");
            return 1;
        }
        ActivityManager.dumpPackageStateStatic(getOutFileDescriptor(), pkg);
        return 0;
    }

    private int runSetHarmfulAppWarning() throws RemoteException {
        int userId = -2;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                int userId2 = translateUserId(userId, false, "runSetHarmfulAppWarning");
                this.mInterface.setHarmfulAppWarning(getNextArgRequired(), getNextArg(), userId2);
                return 0;
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    private int runGetHarmfulAppWarning() throws RemoteException {
        int userId = -2;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                int userId2 = translateUserId(userId, false, "runGetHarmfulAppWarning");
                CharSequence warning = this.mInterface.getHarmfulAppWarning(getNextArgRequired(), userId2);
                if (TextUtils.isEmpty(warning)) {
                    return 1;
                }
                getOutPrintWriter().println(warning);
                return 0;
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    private static String checkAbiArgument(String abi) {
        if (TextUtils.isEmpty(abi)) {
            throw new IllegalArgumentException("Missing ABI argument");
        } else if (STDIN_PATH.equals(abi)) {
            return abi;
        } else {
            for (String supportedAbi : Build.SUPPORTED_ABIS) {
                if (supportedAbi.equals(abi)) {
                    return abi;
                }
            }
            throw new IllegalArgumentException("ABI " + abi + " not supported on this device");
        }
    }

    private int translateUserId(int userId, boolean allowAll, String logContext) {
        return ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, allowAll, true, logContext, "pm command");
    }

    private int doCreateSession(PackageInstaller.SessionParams params, String installerPackageName, int userId) throws RemoteException {
        int userId2 = translateUserId(userId, true, "runInstallCreate");
        if (userId2 == -1) {
            userId2 = 0;
            params.installFlags |= 64;
        }
        return this.mInterface.getPackageInstaller().createSession(params, installerPackageName, userId2);
    }

    private int doWriteSplit(int sessionId, String inPath, long sizeBytes, String splitName, boolean logSuccess) throws RemoteException {
        long sizeBytes2;
        ParcelFileDescriptor fd;
        PackageInstaller.Session session;
        long sizeBytes3;
        String str = inPath;
        PackageInstaller.Session session2 = null;
        try {
            PrintWriter pw = getOutPrintWriter();
            if (STDIN_PATH.equals(str)) {
                fd = ParcelFileDescriptor.dup(getInFileDescriptor());
                sizeBytes2 = sizeBytes;
            } else if (str != null) {
                fd = openFileForSystem(str, ActivityTaskManagerService.DUMP_RECENTS_SHORT_CMD);
                if (fd == null) {
                    IoUtils.closeQuietly((AutoCloseable) null);
                    return -1;
                }
                long sizeBytes4 = fd.getStatSize();
                if (sizeBytes4 < 0) {
                    try {
                        PrintWriter errPrintWriter = getErrPrintWriter();
                        errPrintWriter.println("Unable to get size of: " + str);
                        IoUtils.closeQuietly((AutoCloseable) null);
                        return -1;
                    } catch (IOException e) {
                        e = e;
                        int i = sessionId;
                        long j = sizeBytes4;
                        try {
                            PrintWriter errPrintWriter2 = getErrPrintWriter();
                            errPrintWriter2.println("Error: failed to write; " + e.getMessage());
                            IoUtils.closeQuietly(session2);
                            return 1;
                        } catch (Throwable th) {
                            e = th;
                            IoUtils.closeQuietly(session2);
                            throw e;
                        }
                    } catch (Throwable th2) {
                        e = th2;
                        int i2 = sessionId;
                        long j2 = sizeBytes4;
                        IoUtils.closeQuietly(session2);
                        throw e;
                    }
                } else {
                    sizeBytes2 = sizeBytes4;
                }
            } else {
                fd = ParcelFileDescriptor.dup(getInFileDescriptor());
                sizeBytes2 = sizeBytes;
            }
            if (sizeBytes2 <= 0) {
                try {
                    getErrPrintWriter().println("Error: must specify a APK size");
                    IoUtils.closeQuietly((AutoCloseable) null);
                    return 1;
                } catch (IOException e2) {
                    e = e2;
                    int i3 = sessionId;
                    long j3 = sizeBytes2;
                    PrintWriter errPrintWriter22 = getErrPrintWriter();
                    errPrintWriter22.println("Error: failed to write; " + e.getMessage());
                    IoUtils.closeQuietly(session2);
                    return 1;
                } catch (Throwable th3) {
                    e = th3;
                    int i4 = sessionId;
                    long j4 = sizeBytes2;
                    IoUtils.closeQuietly(session2);
                    throw e;
                }
            } else {
                try {
                } catch (IOException e3) {
                    e = e3;
                    int i5 = sessionId;
                    long j5 = sizeBytes2;
                    PrintWriter errPrintWriter222 = getErrPrintWriter();
                    errPrintWriter222.println("Error: failed to write; " + e.getMessage());
                    IoUtils.closeQuietly(session2);
                    return 1;
                } catch (Throwable th4) {
                    e = th4;
                    int i6 = sessionId;
                    long j6 = sizeBytes2;
                    IoUtils.closeQuietly(session2);
                    throw e;
                }
                try {
                    session = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(sessionId));
                    sizeBytes3 = sizeBytes2;
                } catch (IOException e4) {
                    e = e4;
                    long j52 = sizeBytes2;
                    PrintWriter errPrintWriter2222 = getErrPrintWriter();
                    errPrintWriter2222.println("Error: failed to write; " + e.getMessage());
                    IoUtils.closeQuietly(session2);
                    return 1;
                } catch (Throwable th5) {
                    e = th5;
                    long j62 = sizeBytes2;
                    IoUtils.closeQuietly(session2);
                    throw e;
                }
                try {
                    session.write(splitName, 0, sizeBytes2, fd);
                    if (logSuccess) {
                        pw.println("Success: streamed " + sizeBytes3 + " bytes");
                    }
                    IoUtils.closeQuietly(session);
                    return 0;
                } catch (IOException e5) {
                    e = e5;
                    session2 = session;
                    PrintWriter errPrintWriter22222 = getErrPrintWriter();
                    errPrintWriter22222.println("Error: failed to write; " + e.getMessage());
                    IoUtils.closeQuietly(session2);
                    return 1;
                } catch (Throwable th6) {
                    e = th6;
                    session2 = session;
                    IoUtils.closeQuietly(session2);
                    throw e;
                }
            }
        } catch (IOException e6) {
            e = e6;
            int i7 = sessionId;
            long j7 = sizeBytes;
            PrintWriter errPrintWriter222222 = getErrPrintWriter();
            errPrintWriter222222.println("Error: failed to write; " + e.getMessage());
            IoUtils.closeQuietly(session2);
            return 1;
        } catch (Throwable th7) {
            e = th7;
            int i8 = sessionId;
            long j8 = sizeBytes;
            IoUtils.closeQuietly(session2);
            throw e;
        }
    }

    /* JADX INFO: finally extract failed */
    private int doInstallAddSession(int parentId, int[] sessionIds, boolean logSuccess) throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        PackageInstaller.Session session = null;
        try {
            session = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(parentId));
            if (!session.isMultiPackage()) {
                getErrPrintWriter().println("Error: parent session ID is not a multi-package session");
                IoUtils.closeQuietly(session);
                return 1;
            }
            for (int addChildSessionId : sessionIds) {
                session.addChildSessionId(addChildSessionId);
            }
            if (logSuccess) {
                pw.println("Success");
            }
            IoUtils.closeQuietly(session);
            return 0;
        } catch (Throwable th) {
            IoUtils.closeQuietly(session);
            throw th;
        }
    }

    private int doRemoveSplit(int sessionId, String splitName, boolean logSuccess) throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        PackageInstaller.Session session = null;
        try {
            session = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(sessionId));
            session.removeSplit(splitName);
            if (logSuccess) {
                pw.println("Success");
            }
            return 0;
        } catch (IOException e) {
            pw.println("Error: failed to remove split; " + e.getMessage());
            return 1;
        } finally {
            IoUtils.closeQuietly(session);
        }
    }

    private int doCommitSession(int sessionId, boolean logSuccess) throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        PackageInstaller.Session session = null;
        try {
            session = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(sessionId));
            if (!session.isMultiPackage()) {
                if (!session.isStaged()) {
                    DexMetadataHelper.validateDexPaths(session.getNames());
                }
            }
        } catch (IOException | IllegalStateException e) {
            pw.println("Warning [Could not validate the dex paths: " + e.getMessage() + "]");
        } catch (Throwable th) {
            IoUtils.closeQuietly(session);
            throw th;
        }
        LocalIntentReceiver receiver = new LocalIntentReceiver();
        session.commit(receiver.getIntentSender());
        Intent result = receiver.getResult();
        int status = result.getIntExtra("android.content.pm.extra.STATUS", 1);
        if (status != 0) {
            pw.println("Failure [" + result.getStringExtra("android.content.pm.extra.STATUS_MESSAGE") + "]");
        } else if (logSuccess) {
            pw.println("Success");
        }
        IoUtils.closeQuietly(session);
        return status;
    }

    private int doAbandonSession(int sessionId, boolean logSuccess) throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        PackageInstaller.Session session = null;
        try {
            session = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(sessionId));
            session.abandon();
            if (logSuccess) {
                pw.println("Success");
            }
            return 0;
        } finally {
            IoUtils.closeQuietly(session);
        }
    }

    private void doListPermissions(ArrayList<String> groupList, boolean groups, boolean labels, boolean summary, int startProtectionLevel, int endProtectionLevel) throws RemoteException {
        int groupCount;
        String groupName;
        List<PermissionInfo> ps;
        ArrayList<String> arrayList = groupList;
        PrintWriter pw = getOutPrintWriter();
        int groupCount2 = groupList.size();
        int i = 0;
        while (i < groupCount2) {
            String groupName2 = arrayList.get(i);
            String prefix = "";
            if (groups) {
                if (i > 0) {
                    pw.println("");
                }
                if (groupName2 != null) {
                    PermissionGroupInfo pgi = this.mInterface.getPermissionGroupInfo(groupName2, 0);
                    if (!summary) {
                        groupCount = groupCount2;
                        String str = prefix;
                        StringBuilder sb = new StringBuilder();
                        sb.append(labels ? "+ " : "");
                        sb.append("group:");
                        sb.append(pgi.name);
                        pw.println(sb.toString());
                        if (labels) {
                            pw.println("  package:" + pgi.packageName);
                            if (getResources(pgi) != null) {
                                pw.println("  label:" + loadText(pgi, pgi.labelRes, pgi.nonLocalizedLabel));
                                pw.println("  description:" + loadText(pgi, pgi.descriptionRes, pgi.nonLocalizedDescription));
                            }
                        }
                    } else if (getResources(pgi) != null) {
                        StringBuilder sb2 = new StringBuilder();
                        groupCount = groupCount2;
                        String str2 = prefix;
                        sb2.append(loadText(pgi, pgi.labelRes, pgi.nonLocalizedLabel));
                        sb2.append(": ");
                        pw.print(sb2.toString());
                    } else {
                        groupCount = groupCount2;
                        String str3 = prefix;
                        pw.print(pgi.name + ": ");
                    }
                } else {
                    groupCount = groupCount2;
                    String str4 = prefix;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append((!labels || summary) ? "" : "+ ");
                    sb3.append("ungrouped:");
                    pw.println(sb3.toString());
                }
                prefix = "  ";
            } else {
                groupCount = groupCount2;
                String str5 = prefix;
            }
            List<PermissionInfo> ps2 = this.mInterface.queryPermissionsByGroup(arrayList.get(i), 0).getList();
            int count = ps2.size();
            boolean first = true;
            int p = 0;
            while (p < count) {
                PermissionInfo pi = ps2.get(p);
                if (!groups || groupName2 != null || pi.group == null) {
                    int base = pi.protectionLevel & 15;
                    ps = ps2;
                    if (base < startProtectionLevel) {
                        groupName = groupName2;
                    } else if (base > endProtectionLevel) {
                        groupName = groupName2;
                    } else if (summary) {
                        if (first) {
                            first = false;
                            int i2 = base;
                        } else {
                            int i3 = base;
                            pw.print(", ");
                        }
                        Resources res = getResources(pi);
                        if (res != null) {
                            Resources resources = res;
                            pw.print(loadText(pi, pi.labelRes, pi.nonLocalizedLabel));
                        } else {
                            pw.print(pi.name);
                        }
                        groupName = groupName2;
                    } else {
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append(prefix);
                        sb4.append(labels ? "+ " : "");
                        sb4.append("permission:");
                        sb4.append(pi.name);
                        pw.println(sb4.toString());
                        if (labels) {
                            pw.println(prefix + "  package:" + pi.packageName);
                            Resources res2 = getResources(pi);
                            if (res2 != null) {
                                StringBuilder sb5 = new StringBuilder();
                                sb5.append(prefix);
                                sb5.append("  label:");
                                Resources resources2 = res2;
                                groupName = groupName2;
                                sb5.append(loadText(pi, pi.labelRes, pi.nonLocalizedLabel));
                                pw.println(sb5.toString());
                                pw.println(prefix + "  description:" + loadText(pi, pi.descriptionRes, pi.nonLocalizedDescription));
                            } else {
                                groupName = groupName2;
                            }
                            pw.println(prefix + "  protectionLevel:" + PermissionInfo.protectionToString(pi.protectionLevel));
                        } else {
                            groupName = groupName2;
                        }
                    }
                } else {
                    ps = ps2;
                    groupName = groupName2;
                }
                p++;
                ArrayList<String> arrayList2 = groupList;
                ps2 = ps;
                groupName2 = groupName;
            }
            String str6 = groupName2;
            if (summary) {
                pw.println("");
            }
            i++;
            arrayList = groupList;
            groupCount2 = groupCount;
        }
    }

    private String loadText(PackageItemInfo pii, int res, CharSequence nonLocalized) throws RemoteException {
        Resources r;
        if (nonLocalized != null) {
            return nonLocalized.toString();
        }
        if (res == 0 || (r = getResources(pii)) == null) {
            return null;
        }
        try {
            return r.getString(res);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    private Resources getResources(PackageItemInfo pii) throws RemoteException {
        Resources res = this.mResourceCache.get(pii.packageName);
        if (res != null) {
            return res;
        }
        ApplicationInfo ai = this.mInterface.getApplicationInfo(pii.packageName, 0, 0);
        AssetManager am = new AssetManager();
        am.addAssetPath(ai.publicSourceDir);
        Resources res2 = new Resources(am, (DisplayMetrics) null, (Configuration) null);
        this.mResourceCache.put(pii.packageName, res2);
        return res2;
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("Package manager (package) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("");
        pw.println("  path [--user USER_ID] PACKAGE");
        pw.println("    Print the path to the .apk of the given PACKAGE.");
        pw.println("");
        pw.println("  dump PACKAGE");
        pw.println("    Print various system state associated with the given PACKAGE.");
        pw.println("");
        pw.println("  list features");
        pw.println("    Prints all features of the system.");
        pw.println("");
        pw.println("  has-feature FEATURE_NAME [version]");
        pw.println("    Prints true and returns exit status 0 when system has a FEATURE_NAME,");
        pw.println("    otherwise prints false and returns exit status 1");
        pw.println("");
        pw.println("  list instrumentation [-f] [TARGET-PACKAGE]");
        pw.println("    Prints all test packages; optionally only those targeting TARGET-PACKAGE");
        pw.println("    Options:");
        pw.println("      -f: dump the name of the .apk file containing the test package");
        pw.println("");
        pw.println("  list libraries");
        pw.println("    Prints all system libraries.");
        pw.println("");
        pw.println("  list packages [-f] [-d] [-e] [-s] [-3] [-i] [-l] [-u] [-U] ");
        pw.println("      [--show-versioncode] [--apex-only] [--uid UID] [--user USER_ID] [FILTER]");
        pw.println("    Prints all packages; optionally only those whose name contains");
        pw.println("    the text in FILTER.  Options are:");
        pw.println("      -f: see their associated file");
        pw.println("      -a: all known packages (but excluding APEXes)");
        pw.println("      -d: filter to only show disabled packages");
        pw.println("      -e: filter to only show enabled packages");
        pw.println("      -s: filter to only show system packages");
        pw.println("      -3: filter to only show third party packages");
        pw.println("      -i: see the installer for the packages");
        pw.println("      -l: ignored (used for compatibility with older releases)");
        pw.println("      -U: also show the package UID");
        pw.println("      -u: also include uninstalled packages");
        pw.println("      --show-versioncode: also show the version code");
        pw.println("      --apex-only: only show APEX packages");
        pw.println("      --uid UID: filter to only show packages with the given UID");
        pw.println("      --user USER_ID: only list packages belonging to the given user");
        pw.println("");
        pw.println("  list permission-groups");
        pw.println("    Prints all known permission groups.");
        pw.println("");
        pw.println("  list permissions [-g] [-f] [-d] [-u] [GROUP]");
        pw.println("    Prints all known permissions; optionally only those in GROUP.  Options are:");
        pw.println("      -g: organize by group");
        pw.println("      -f: print all information");
        pw.println("      -s: short summary");
        pw.println("      -d: only list dangerous permissions");
        pw.println("      -u: list only the permissions users will see");
        pw.println("");
        pw.println("  resolve-activity [--brief] [--components] [--query-flags FLAGS]");
        pw.println("       [--user USER_ID] INTENT");
        pw.println("    Prints the activity that resolves to the given INTENT.");
        pw.println("");
        pw.println("  query-activities [--brief] [--components] [--query-flags FLAGS]");
        pw.println("       [--user USER_ID] INTENT");
        pw.println("    Prints all activities that can handle the given INTENT.");
        pw.println("");
        pw.println("  query-services [--brief] [--components] [--query-flags FLAGS]");
        pw.println("       [--user USER_ID] INTENT");
        pw.println("    Prints all services that can handle the given INTENT.");
        pw.println("");
        pw.println("  query-receivers [--brief] [--components] [--query-flags FLAGS]");
        pw.println("       [--user USER_ID] INTENT");
        pw.println("    Prints all broadcast receivers that can handle the given INTENT.");
        pw.println("");
        pw.println("  install [-lrtsfdgw] [-i PACKAGE] [--user USER_ID|all|current]");
        pw.println("       [-p INHERIT_PACKAGE] [--install-location 0/1/2]");
        pw.println("       [--install-reason 0/1/2/3/4] [--originating-uri URI]");
        pw.println("       [--referrer URI] [--abi ABI_NAME] [--force-sdk]");
        pw.println("       [--preload] [--instantapp] [--full] [--dont-kill]");
        pw.println("       [--enable-rollback]");
        pw.println("       [--force-uuid internal|UUID] [--pkg PACKAGE] [-S BYTES] [--apex]");
        pw.println("       [PATH|-]");
        pw.println("    Install an application.  Must provide the apk data to install, either as a");
        pw.println("    file path or '-' to read from stdin.  Options are:");
        pw.println("      -l: forward lock application");
        pw.println("      -R: disallow replacement of existing application");
        pw.println("      -t: allow test packages");
        pw.println("      -i: specify package name of installer owning the app");
        pw.println("      -s: install application on sdcard");
        pw.println("      -f: install application on internal flash");
        pw.println("      -d: allow version code downgrade (debuggable packages only)");
        pw.println("      -p: partial application install (new split on top of existing pkg)");
        pw.println("      -g: grant all runtime permissions");
        pw.println("      -S: size in bytes of package, required for stdin");
        pw.println("      --user: install under the given user.");
        pw.println("      --dont-kill: installing a new feature split, don't kill running app");
        pw.println("      --restrict-permissions: don't whitelist restricted permissions at install");
        pw.println("      --originating-uri: set URI where app was downloaded from");
        pw.println("      --referrer: set URI that instigated the install of the app");
        pw.println("      --pkg: specify expected package name of app being installed");
        pw.println("      --abi: override the default ABI of the platform");
        pw.println("      --instantapp: cause the app to be installed as an ephemeral install app");
        pw.println("      --full: cause the app to be installed as a non-ephemeral full app");
        pw.println("      --install-location: force the install location:");
        pw.println("          0=auto, 1=internal only, 2=prefer external");
        pw.println("      --install-reason: indicates why the app is being installed:");
        pw.println("          0=unknown, 1=admin policy, 2=device restore,");
        pw.println("          3=device setup, 4=user request");
        pw.println("      --force-uuid: force install on to disk volume with given UUID");
        pw.println("      --apex: install an .apex file, not an .apk");
        pw.println("");
        pw.println("  install-create [-lrtsfdg] [-i PACKAGE] [--user USER_ID|all|current]");
        pw.println("       [-p INHERIT_PACKAGE] [--install-location 0/1/2]");
        pw.println("       [--install-reason 0/1/2/3/4] [--originating-uri URI]");
        pw.println("       [--referrer URI] [--abi ABI_NAME] [--force-sdk]");
        pw.println("       [--preload] [--instantapp] [--full] [--dont-kill]");
        pw.println("       [--force-uuid internal|UUID] [--pkg PACKAGE] [--apex] [-S BYTES]");
        pw.println("       [--multi-package] [--staged]");
        pw.println("    Like \"install\", but starts an install session.  Use \"install-write\"");
        pw.println("    to push data into the session, and \"install-commit\" to finish.");
        pw.println("");
        pw.println("  install-write [-S BYTES] SESSION_ID SPLIT_NAME [PATH|-]");
        pw.println("    Write an apk into the given install session.  If the path is '-', data");
        pw.println("    will be read from stdin.  Options are:");
        pw.println("      -S: size in bytes of package, required for stdin");
        pw.println("");
        pw.println("  install-add-session MULTI_PACKAGE_SESSION_ID CHILD_SESSION_IDs");
        pw.println("    Add one or more session IDs to a multi-package session.");
        pw.println("");
        pw.println("  install-commit SESSION_ID");
        pw.println("    Commit the given active install session, installing the app.");
        pw.println("");
        pw.println("  install-abandon SESSION_ID");
        pw.println("    Delete the given active install session.");
        pw.println("");
        pw.println("  set-install-location LOCATION");
        pw.println("    Changes the default install location.  NOTE this is only intended for debugging;");
        pw.println("    using this can cause applications to break and other undersireable behavior.");
        pw.println("    LOCATION is one of:");
        pw.println("    0 [auto]: Let system decide the best location");
        pw.println("    1 [internal]: Install on internal device storage");
        pw.println("    2 [external]: Install on external media");
        pw.println("");
        pw.println("  get-install-location");
        pw.println("    Returns the current install location: 0, 1 or 2 as per set-install-location.");
        pw.println("");
        pw.println("  move-package PACKAGE [internal|UUID]");
        pw.println("");
        pw.println("  move-primary-storage [internal|UUID]");
        pw.println("");
        pw.println("  pm uninstall [-k] [--user USER_ID] [--versionCode VERSION_CODE] PACKAGE [SPLIT]");
        pw.println("    Remove the given package name from the system.  May remove an entire app");
        pw.println("    if no SPLIT name is specified, otherwise will remove only the split of the");
        pw.println("    given app.  Options are:");
        pw.println("      -k: keep the data and cache directories around after package removal.");
        pw.println("      --user: remove the app from the given user.");
        pw.println("      --versionCode: only uninstall if the app has the given version code.");
        pw.println("");
        pw.println("  clear [--user USER_ID] PACKAGE");
        pw.println("    Deletes all data associated with a package.");
        pw.println("");
        pw.println("  enable [--user USER_ID] PACKAGE_OR_COMPONENT");
        pw.println("  disable [--user USER_ID] PACKAGE_OR_COMPONENT");
        pw.println("  disable-user [--user USER_ID] PACKAGE_OR_COMPONENT");
        pw.println("  disable-until-used [--user USER_ID] PACKAGE_OR_COMPONENT");
        pw.println("  default-state [--user USER_ID] PACKAGE_OR_COMPONENT");
        pw.println("    These commands change the enabled state of a given package or");
        pw.println("    component (written as \"package/class\").");
        pw.println("");
        pw.println("  hide [--user USER_ID] PACKAGE_OR_COMPONENT");
        pw.println("  unhide [--user USER_ID] PACKAGE_OR_COMPONENT");
        pw.println("");
        pw.println("  suspend [--user USER_ID] TARGET-PACKAGE");
        pw.println("    Suspends the specified package (as user).");
        pw.println("");
        pw.println("  unsuspend [--user USER_ID] TARGET-PACKAGE");
        pw.println("    Unsuspends the specified package (as user).");
        pw.println("");
        pw.println("  grant [--user USER_ID] PACKAGE PERMISSION");
        pw.println("  revoke [--user USER_ID] PACKAGE PERMISSION");
        pw.println("    These commands either grant or revoke permissions to apps.  The permissions");
        pw.println("    must be declared as used in the app's manifest, be runtime permissions");
        pw.println("    (protection level dangerous), and the app targeting SDK greater than Lollipop MR1.");
        pw.println("");
        pw.println("  reset-permissions");
        pw.println("    Revert all runtime permissions to their default state.");
        pw.println("");
        pw.println("  set-permission-enforced PERMISSION [true|false]");
        pw.println("");
        pw.println("  get-privapp-permissions TARGET-PACKAGE");
        pw.println("    Prints all privileged permissions for a package.");
        pw.println("");
        pw.println("  get-privapp-deny-permissions TARGET-PACKAGE");
        pw.println("    Prints all privileged permissions that are denied for a package.");
        pw.println("");
        pw.println("  get-oem-permissions TARGET-PACKAGE");
        pw.println("    Prints all OEM permissions for a package.");
        pw.println("");
        pw.println("  set-app-link [--user USER_ID] PACKAGE {always|ask|never|undefined}");
        pw.println("  get-app-link [--user USER_ID] PACKAGE");
        pw.println("");
        pw.println("  trim-caches DESIRED_FREE_SPACE [internal|UUID]");
        pw.println("    Trim cache files to reach the given free space.");
        pw.println("");
        pw.println("  create-user [--profileOf USER_ID] [--managed] [--restricted] [--ephemeral]");
        pw.println("      [--guest] USER_NAME");
        pw.println("    Create a new user with the given USER_NAME, printing the new user identifier");
        pw.println("    of the user.");
        pw.println("");
        pw.println("  remove-user USER_ID");
        pw.println("    Remove the user with the given USER_IDENTIFIER, deleting all data");
        pw.println("    associated with that user");
        pw.println("");
        pw.println("  set-user-restriction [--user USER_ID] RESTRICTION VALUE");
        pw.println("");
        pw.println("  get-max-users");
        pw.println("");
        pw.println("  get-max-running-users");
        pw.println("");
        pw.println("  compile [-m MODE | -r REASON] [-f] [-c] [--split SPLIT_NAME]");
        pw.println("          [--reset] [--check-prof (true | false)] (-a | TARGET-PACKAGE)");
        pw.println("    Trigger compilation of TARGET-PACKAGE or all packages if \"-a\".  Options are:");
        pw.println("      -a: compile all packages");
        pw.println("      -c: clear profile data before compiling");
        pw.println("      -f: force compilation even if not needed");
        pw.println("      -m: select compilation mode");
        pw.println("          MODE is one of the dex2oat compiler filters:");
        pw.println("            assume-verified");
        pw.println("            extract");
        pw.println("            verify");
        pw.println("            quicken");
        pw.println("            space-profile");
        pw.println("            space");
        pw.println("            speed-profile");
        pw.println("            speed");
        pw.println("            everything");
        pw.println("      -r: select compilation reason");
        pw.println("          REASON is one of:");
        for (int i = 0; i < PackageManagerServiceCompilerMapping.REASON_STRINGS.length; i++) {
            pw.println("            " + PackageManagerServiceCompilerMapping.REASON_STRINGS[i]);
        }
        pw.println("      --reset: restore package to its post-install state");
        pw.println("      --check-prof (true | false): look at profiles when doing dexopt?");
        pw.println("      --secondary-dex: compile app secondary dex files");
        pw.println("      --split SPLIT: compile only the given split name");
        pw.println("      --compile-layouts: compile layout resources for faster inflation");
        pw.println("");
        pw.println("  force-dex-opt PACKAGE");
        pw.println("    Force immediate execution of dex opt for the given PACKAGE.");
        pw.println("");
        pw.println("  bg-dexopt-job");
        pw.println("    Execute the background optimizations immediately.");
        pw.println("    Note that the command only runs the background optimizer logic. It may");
        pw.println("    overlap with the actual job but the job scheduler will not be able to");
        pw.println("    cancel it. It will also run even if the device is not in the idle");
        pw.println("    maintenance mode.");
        pw.println("");
        pw.println("  reconcile-secondary-dex-files TARGET-PACKAGE");
        pw.println("    Reconciles the package secondary dex files with the generated oat files.");
        pw.println("");
        pw.println("  dump-profiles TARGET-PACKAGE");
        pw.println("    Dumps method/class profile files to");
        pw.println("    /data/misc/profman/TARGET-PACKAGE.txt");
        pw.println("");
        pw.println("  snapshot-profile TARGET-PACKAGE [--code-path path]");
        pw.println("    Take a snapshot of the package profiles to");
        pw.println("    /data/misc/profman/TARGET-PACKAGE[-code-path].prof");
        pw.println("    If TARGET-PACKAGE=android it will take a snapshot of the boot image");
        pw.println("");
        pw.println("  set-home-activity [--user USER_ID] TARGET-COMPONENT");
        pw.println("    Set the default home activity (aka launcher).");
        pw.println("    TARGET-COMPONENT can be a package name (com.package.my) or a full");
        pw.println("    component (com.package.my/component.name). However, only the package name");
        pw.println("    matters: the actual component used will be determined automatically from");
        pw.println("    the package.");
        pw.println("");
        pw.println("  set-installer PACKAGE INSTALLER");
        pw.println("    Set installer package name");
        pw.println("");
        pw.println("  get-instantapp-resolver");
        pw.println("    Return the name of the component that is the current instant app installer.");
        pw.println("");
        pw.println("  set-harmful-app-warning [--user <USER_ID>] <PACKAGE> [<WARNING>]");
        pw.println("    Mark the app as harmful with the given warning message.");
        pw.println("");
        pw.println("  get-harmful-app-warning [--user <USER_ID>] <PACKAGE>");
        pw.println("    Return the harmful app warning message for the given app, if present");
        pw.println();
        pw.println("  uninstall-system-updates");
        pw.println("    Remove updates to all system applications and fall back to their /system version.");
        pw.println();
        pw.println("  get-moduleinfo [--all | --installed] [module-name]");
        pw.println("    Displays module info. If module-name is specified only that info is shown");
        pw.println("    By default, without any argument only installed modules are shown.");
        pw.println("      --all: show all module info");
        pw.println("      --installed: show only installed modules");
        pw.println("");
        Intent.printIntentArgsHelp(pw, "");
    }

    private static class LocalIntentReceiver {
        private IIntentSender.Stub mLocalSender;
        /* access modifiers changed from: private */
        public final LinkedBlockingQueue<Intent> mResult;

        private LocalIntentReceiver() {
            this.mResult = new LinkedBlockingQueue<>();
            this.mLocalSender = new IIntentSender.Stub() {
                public void send(int code, Intent intent, String resolvedType, IBinder whitelistToken, IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) {
                    try {
                        LocalIntentReceiver.this.mResult.offer(intent, 5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }

        public IntentSender getIntentSender() {
            return new IntentSender(this.mLocalSender);
        }

        public Intent getResult() {
            try {
                return this.mResult.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

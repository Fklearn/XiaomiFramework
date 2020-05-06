package com.android.server.webkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.Process;
import android.os.UserHandle;
import android.util.Slog;
import android.webkit.IWebViewUpdateService;
import android.webkit.WebViewProviderInfo;
import android.webkit.WebViewProviderResponse;
import com.android.internal.util.DumpUtils;
import com.android.server.SystemService;
import com.android.server.pm.Settings;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

public class WebViewUpdateService extends SystemService {
    static final int PACKAGE_ADDED = 1;
    static final int PACKAGE_ADDED_REPLACED = 2;
    static final int PACKAGE_CHANGED = 0;
    static final int PACKAGE_REMOVED = 3;
    private static final String TAG = "WebViewUpdateService";
    /* access modifiers changed from: private */
    public WebViewUpdateServiceImpl mImpl;
    private BroadcastReceiver mWebViewUpdatedReceiver;

    public WebViewUpdateService(Context context) {
        super(context);
        this.mImpl = new WebViewUpdateServiceImpl(context, SystemImpl.getInstance());
    }

    /* JADX WARNING: type inference failed for: r2v5, types: [com.android.server.webkit.WebViewUpdateService$BinderService, android.os.IBinder] */
    public void onStart() {
        this.mWebViewUpdatedReceiver = new BroadcastReceiver() {
            /* JADX WARNING: Can't fix incorrect switch cases order */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onReceive(android.content.Context r9, android.content.Intent r10) {
                /*
                    r8 = this;
                    java.lang.String r0 = "android.intent.extra.user_handle"
                    r1 = -10000(0xffffffffffffd8f0, float:NaN)
                    int r0 = r10.getIntExtra(r0, r1)
                    java.lang.String r1 = r10.getAction()
                    int r2 = r1.hashCode()
                    r3 = 0
                    r4 = 4
                    r5 = 3
                    r6 = 2
                    r7 = 1
                    switch(r2) {
                        case -2061058799: goto L_0x0041;
                        case -755112654: goto L_0x0037;
                        case 172491798: goto L_0x002d;
                        case 525384130: goto L_0x0023;
                        case 1544582882: goto L_0x0019;
                        default: goto L_0x0018;
                    }
                L_0x0018:
                    goto L_0x004b
                L_0x0019:
                    java.lang.String r2 = "android.intent.action.PACKAGE_ADDED"
                    boolean r1 = r1.equals(r2)
                    if (r1 == 0) goto L_0x0018
                    r1 = r6
                    goto L_0x004c
                L_0x0023:
                    java.lang.String r2 = "android.intent.action.PACKAGE_REMOVED"
                    boolean r1 = r1.equals(r2)
                    if (r1 == 0) goto L_0x0018
                    r1 = r3
                    goto L_0x004c
                L_0x002d:
                    java.lang.String r2 = "android.intent.action.PACKAGE_CHANGED"
                    boolean r1 = r1.equals(r2)
                    if (r1 == 0) goto L_0x0018
                    r1 = r7
                    goto L_0x004c
                L_0x0037:
                    java.lang.String r2 = "android.intent.action.USER_STARTED"
                    boolean r1 = r1.equals(r2)
                    if (r1 == 0) goto L_0x0018
                    r1 = r5
                    goto L_0x004c
                L_0x0041:
                    java.lang.String r2 = "android.intent.action.USER_REMOVED"
                    boolean r1 = r1.equals(r2)
                    if (r1 == 0) goto L_0x0018
                    r1 = r4
                    goto L_0x004c
                L_0x004b:
                    r1 = -1
                L_0x004c:
                    java.lang.String r2 = "android.intent.extra.REPLACING"
                    if (r1 == 0) goto L_0x009b
                    if (r1 == r7) goto L_0x0087
                    if (r1 == r6) goto L_0x006d
                    if (r1 == r5) goto L_0x0063
                    if (r1 == r4) goto L_0x0059
                    goto L_0x00b4
                L_0x0059:
                    com.android.server.webkit.WebViewUpdateService r1 = com.android.server.webkit.WebViewUpdateService.this
                    com.android.server.webkit.WebViewUpdateServiceImpl r1 = r1.mImpl
                    r1.handleUserRemoved(r0)
                    goto L_0x00b4
                L_0x0063:
                    com.android.server.webkit.WebViewUpdateService r1 = com.android.server.webkit.WebViewUpdateService.this
                    com.android.server.webkit.WebViewUpdateServiceImpl r1 = r1.mImpl
                    r1.handleNewUser(r0)
                    goto L_0x00b4
                L_0x006d:
                    com.android.server.webkit.WebViewUpdateService r1 = com.android.server.webkit.WebViewUpdateService.this
                    com.android.server.webkit.WebViewUpdateServiceImpl r1 = r1.mImpl
                    java.lang.String r3 = com.android.server.webkit.WebViewUpdateService.packageNameFromIntent(r10)
                    android.os.Bundle r4 = r10.getExtras()
                    boolean r2 = r4.getBoolean(r2)
                    if (r2 == 0) goto L_0x0082
                    goto L_0x0083
                L_0x0082:
                    r6 = r7
                L_0x0083:
                    r1.packageStateChanged(r3, r6, r0)
                    goto L_0x00b4
                L_0x0087:
                    boolean r1 = com.android.server.webkit.WebViewUpdateService.entirePackageChanged(r10)
                    if (r1 == 0) goto L_0x00b4
                    com.android.server.webkit.WebViewUpdateService r1 = com.android.server.webkit.WebViewUpdateService.this
                    com.android.server.webkit.WebViewUpdateServiceImpl r1 = r1.mImpl
                    java.lang.String r2 = com.android.server.webkit.WebViewUpdateService.packageNameFromIntent(r10)
                    r1.packageStateChanged(r2, r3, r0)
                    goto L_0x00b4
                L_0x009b:
                    android.os.Bundle r1 = r10.getExtras()
                    boolean r1 = r1.getBoolean(r2)
                    if (r1 == 0) goto L_0x00a6
                    return
                L_0x00a6:
                    com.android.server.webkit.WebViewUpdateService r1 = com.android.server.webkit.WebViewUpdateService.this
                    com.android.server.webkit.WebViewUpdateServiceImpl r1 = r1.mImpl
                    java.lang.String r2 = com.android.server.webkit.WebViewUpdateService.packageNameFromIntent(r10)
                    r1.packageStateChanged(r2, r5, r0)
                L_0x00b4:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.webkit.WebViewUpdateService.AnonymousClass1.onReceive(android.content.Context, android.content.Intent):void");
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        filter.addDataScheme(Settings.ATTR_PACKAGE);
        for (WebViewProviderInfo provider : this.mImpl.getWebViewPackages()) {
            filter.addDataSchemeSpecificPart(provider.packageName, 0);
        }
        getContext().registerReceiverAsUser(this.mWebViewUpdatedReceiver, UserHandle.ALL, filter, (String) null, (Handler) null);
        IntentFilter userAddedFilter = new IntentFilter();
        userAddedFilter.addAction("android.intent.action.USER_STARTED");
        userAddedFilter.addAction("android.intent.action.USER_REMOVED");
        getContext().registerReceiverAsUser(this.mWebViewUpdatedReceiver, UserHandle.ALL, userAddedFilter, (String) null, (Handler) null);
        publishBinderService("webviewupdate", new BinderService(), true);
    }

    public void prepareWebViewInSystemServer() {
        this.mImpl.prepareWebViewInSystemServer();
    }

    /* access modifiers changed from: private */
    public static String packageNameFromIntent(Intent intent) {
        return intent.getDataString().substring("package:".length());
    }

    public static boolean entirePackageChanged(Intent intent) {
        return Arrays.asList(intent.getStringArrayExtra("android.intent.extra.changed_component_name_list")).contains(intent.getDataString().substring("package:".length()));
    }

    private class BinderService extends IWebViewUpdateService.Stub {
        private BinderService() {
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
            /*
                r8 = this;
                com.android.server.webkit.WebViewUpdateServiceShellCommand r0 = new com.android.server.webkit.WebViewUpdateServiceShellCommand
                r0.<init>(r8)
                r1 = r8
                r2 = r9
                r3 = r10
                r4 = r11
                r5 = r12
                r6 = r13
                r7 = r14
                r0.exec(r1, r2, r3, r4, r5, r6, r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.webkit.WebViewUpdateService.BinderService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }

        public void notifyRelroCreationCompleted() {
            if (Binder.getCallingUid() == 1037 || Binder.getCallingUid() == 1000) {
                long callingId = Binder.clearCallingIdentity();
                try {
                    WebViewUpdateService.this.mImpl.notifyRelroCreationCompleted();
                } finally {
                    Binder.restoreCallingIdentity(callingId);
                }
            }
        }

        public WebViewProviderResponse waitForAndGetProvider() {
            if (Binder.getCallingPid() != Process.myPid()) {
                return WebViewUpdateService.this.mImpl.waitForAndGetProvider();
            }
            throw new IllegalStateException("Cannot create a WebView from the SystemServer");
        }

        public String changeProviderAndSetting(String newProvider) {
            if (WebViewUpdateService.this.getContext().checkCallingPermission("android.permission.WRITE_SECURE_SETTINGS") == 0) {
                long callingId = Binder.clearCallingIdentity();
                try {
                    return WebViewUpdateService.this.mImpl.changeProviderAndSetting(newProvider);
                } finally {
                    Binder.restoreCallingIdentity(callingId);
                }
            } else {
                String msg = "Permission Denial: changeProviderAndSetting() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.WRITE_SECURE_SETTINGS";
                Slog.w(WebViewUpdateService.TAG, msg);
                throw new SecurityException(msg);
            }
        }

        public WebViewProviderInfo[] getValidWebViewPackages() {
            return WebViewUpdateService.this.mImpl.getValidWebViewPackages();
        }

        public WebViewProviderInfo[] getAllWebViewPackages() {
            return WebViewUpdateService.this.mImpl.getWebViewPackages();
        }

        public String getCurrentWebViewPackageName() {
            PackageInfo pi = WebViewUpdateService.this.mImpl.getCurrentWebViewPackage();
            if (pi == null) {
                return null;
            }
            return pi.packageName;
        }

        public PackageInfo getCurrentWebViewPackage() {
            return WebViewUpdateService.this.mImpl.getCurrentWebViewPackage();
        }

        public boolean isMultiProcessEnabled() {
            return WebViewUpdateService.this.mImpl.isMultiProcessEnabled();
        }

        public void enableMultiProcess(boolean enable) {
            if (WebViewUpdateService.this.getContext().checkCallingPermission("android.permission.WRITE_SECURE_SETTINGS") == 0) {
                long callingId = Binder.clearCallingIdentity();
                try {
                    WebViewUpdateService.this.mImpl.enableMultiProcess(enable);
                } finally {
                    Binder.restoreCallingIdentity(callingId);
                }
            } else {
                String msg = "Permission Denial: enableMultiProcess() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.WRITE_SECURE_SETTINGS";
                Slog.w(WebViewUpdateService.TAG, msg);
                throw new SecurityException(msg);
            }
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(WebViewUpdateService.this.getContext(), WebViewUpdateService.TAG, pw)) {
                WebViewUpdateService.this.mImpl.dumpState(pw);
            }
        }
    }
}

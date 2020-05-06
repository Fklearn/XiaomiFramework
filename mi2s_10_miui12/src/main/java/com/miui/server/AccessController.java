package com.miui.server;

import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MiuiSettings;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.widget.LockPatternUtils;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import miui.util.Log;
import org.json.JSONObject;

public class AccessController {
    private static final String ACCESS_CONTROL = "access_control.key";
    private static final String ACCESS_CONTROL_PASSWORD_TYPE_KEY = "access_control_password_type.key";
    private static final String APPLOCK_WHILTE = "applock_whilte";
    public static final boolean DEBUG = false;
    private static final String GAMEBOOSTER_ANTIMSG = "gamebooster_antimsg";
    public static final String PACKAGE_CAMERA = "com.android.camera";
    public static final String PACKAGE_GALLERY = "com.miui.gallery";
    public static final String PACKAGE_MEITU_CAMERA = "com.mlab.cam";
    public static final String PACKAGE_SYSTEMUI = "com.android.systemui";
    private static final String PASSWORD_TYPE_PATTERN = "pattern";
    public static final String SKIP_INTERCEPT_ACTIVITY_GALLERY_EDIT = "com.miui.gallery.editor.photo.screen.home.ScreenEditorActivity";
    public static final String SKIP_INTERCEPT_ACTIVITY_GALLERY_EXTRA = "com.miui.gallery.activity.ExternalPhotoPageActivity";
    private static final String SYSTEM_DIRECTORY = "/system/";
    private static final String TAG = "AccessController";
    private static final long UPDATE_EVERY_DELAY = 43200000;
    private static final long UPDATE_FIRT_DELAY = 180000;
    private static final int UPDATE_WHITE_LIST = 1;
    private static final String WECHAT_VIDEO_ACTIVITY_CLASSNAME = "com.tencent.mm.plugin.voip.ui.VideoActivity";
    private static ArrayMap<String, ArrayList<Intent>> mAntimsgInterceptList = new ArrayMap<>();
    private static Method mPasswordToHash;
    private static ArrayMap<String, ArrayList<Intent>> mSkipList = new ArrayMap<>();
    private Context mContext;
    private final Object mFileWriteLock = new Object();
    private KeyguardManager mKeyguardManager;
    private LockPatternUtils mLockPatternUtils;
    private WorkHandler mWorkHandler;

    static {
        ArrayList<Pair<String, String>> passList = new ArrayList<>();
        passList.add(new Pair("com.tencent.mobileqq", "com.tencent.av.ui.VideoInviteLock"));
        passList.add(new Pair("com.tencent.mobileqq", "com.tencent.av.ui.VideoInviteFull"));
        passList.add(new Pair("com.tencent.mm", WECHAT_VIDEO_ACTIVITY_CLASSNAME));
        passList.add(new Pair("com.tencent.mm", "com.tencent.mm.plugin.multitalk.ui.MultiTalkMainUI"));
        passList.add(new Pair("com.tencent.mm", "com.tencent.mm.plugin.base.stub.UIEntryStub"));
        passList.add(new Pair("com.tencent.mm", "com.tencent.mm.plugin.webview.ui.tools.SDKOAuthUI"));
        passList.add(new Pair("com.tencent.mm", "com.tencent.mm.plugin.base.stub.WXPayEntryActivity"));
        passList.add(new Pair("com.tencent.mm", "com.tencent.mm.plugin.wallet_index.ui.OrderHandlerUI"));
        passList.add(new Pair("com.whatsapp", "com.whatsapp.VoipActivity"));
        passList.add(new Pair("com.whatsapp", "com.whatsapp.voipcalling.VoipActivityV2"));
        passList.add(new Pair("jp.naver.line.android", "jp.naver.line.android.freecall.FreeCallActivity"));
        passList.add(new Pair("com.bbm", "com.bbm.ui.voice.activities.IncomingCallActivity"));
        passList.add(new Pair("com.xiaomi.channel", "com.xiaomi.channel.voip.VoipCallActivity"));
        passList.add(new Pair("com.facebook.orca", "com.facebook.rtc.activities.WebrtcIncallActivity"));
        passList.add(new Pair("com.bsb.hike", "com.bsb.hike.voip.view.VoIPActivity"));
        passList.add(new Pair("com.eg.android.AlipayGphone", "com.alipay.android.app.TransProcessPayActivity"));
        passList.add(new Pair("com.eg.android.AlipayGphone", "com.alipay.mobile.security.login.ui.AlipayUserLoginActivity"));
        passList.add(new Pair("com.eg.android.AlipayGphone", "com.alipay.mobile.bill.detail.ui.EmptyActivity_"));
        passList.add(new Pair("com.xiaomi.smarthome", "com.xiaomi.smarthome.miio.activity.ClientAllLockedActivity"));
        passList.add(new Pair("com.android.settings", "com.android.settings.FallbackHome"));
        passList.add(new Pair("com.android.mms", "com.android.mms.ui.DummyActivity"));
        passList.add(new Pair("com.android.mms", "com.android.mms.ui.ComposeMessageRouterActivity"));
        passList.add(new Pair("com.xiaomi.jr", "com.xiaomi.jr.EntryActivity"));
        Iterator<Pair<String, String>> it = passList.iterator();
        while (it.hasNext()) {
            Pair<String, String> pair = it.next();
            ArrayList<Intent> intents = mSkipList.get(pair.first);
            if (intents == null) {
                intents = new ArrayList<>(1);
                mSkipList.put((String) pair.first, intents);
            }
            Intent intent = new Intent();
            intent.setComponent(new ComponentName((String) pair.first, (String) pair.second));
            intents.add(intent);
        }
        ArrayList<Pair<String, String>> interceptList = new ArrayList<>();
        interceptList.add(new Pair("com.tencent.mobileqq", "com.tencent.av.ui.VideoInviteLock"));
        interceptList.add(new Pair("com.tencent.mobileqq", "com.tencent.av.ui.VideoInviteFull"));
        interceptList.add(new Pair("com.tencent.mm", WECHAT_VIDEO_ACTIVITY_CLASSNAME));
        Iterator<Pair<String, String>> it2 = interceptList.iterator();
        while (it2.hasNext()) {
            Pair<String, String> pair2 = it2.next();
            ArrayList<Intent> intents2 = mAntimsgInterceptList.get(pair2.first);
            if (intents2 == null) {
                intents2 = new ArrayList<>(1);
                mAntimsgInterceptList.put((String) pair2.first, intents2);
            }
            Intent intent2 = new Intent();
            intent2.setComponent(new ComponentName((String) pair2.first, (String) pair2.second));
            intents2.add(intent2);
        }
        try {
            if (Build.VERSION.SDK_INT > 28) {
                Class<LockPatternUtils> cls = LockPatternUtils.class;
                mPasswordToHash = cls.getDeclaredMethod("legacyPasswordToHash", new Class[]{byte[].class, Integer.TYPE});
            } else if (Build.VERSION.SDK_INT == 28) {
                mPasswordToHash = LockPatternUtils.class.getDeclaredMethod("legacyPasswordToHash", new Class[]{String.class, Integer.TYPE});
            } else {
                mPasswordToHash = LockPatternUtils.class.getDeclaredMethod("passwordToHash", new Class[]{String.class, Integer.TYPE});
            }
            mPasswordToHash.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, " passwordToHash static invoke error", e);
        }
    }

    private class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                AccessController.this.updateWhiteList();
            }
        }
    }

    public AccessController(Context context, Looper looper) {
        this.mContext = context;
        this.mKeyguardManager = (KeyguardManager) context.getSystemService("keyguard");
        this.mWorkHandler = new WorkHandler(looper);
        this.mWorkHandler.sendEmptyMessageDelayed(1, UPDATE_FIRT_DELAY);
        this.mLockPatternUtils = new LockPatternUtils(context);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00a5, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean filterIntentLocked(boolean r12, java.lang.String r13, android.content.Intent r14) {
        /*
            r11 = this;
            r0 = 0
            if (r14 != 0) goto L_0x0004
            return r0
        L_0x0004:
            monitor-enter(r11)
            if (r12 == 0) goto L_0x0010
            android.util.ArrayMap<java.lang.String, java.util.ArrayList<android.content.Intent>> r1 = mSkipList     // Catch:{ all -> 0x00a6 }
            java.lang.Object r1 = r1.get(r13)     // Catch:{ all -> 0x00a6 }
            java.util.ArrayList r1 = (java.util.ArrayList) r1     // Catch:{ all -> 0x00a6 }
            goto L_0x0018
        L_0x0010:
            android.util.ArrayMap<java.lang.String, java.util.ArrayList<android.content.Intent>> r1 = mAntimsgInterceptList     // Catch:{ all -> 0x00a6 }
            java.lang.Object r1 = r1.get(r13)     // Catch:{ all -> 0x00a6 }
            java.util.ArrayList r1 = (java.util.ArrayList) r1     // Catch:{ all -> 0x00a6 }
        L_0x0018:
            if (r1 != 0) goto L_0x001c
            monitor-exit(r11)     // Catch:{ all -> 0x00a6 }
            return r0
        L_0x001c:
            java.lang.String r2 = r14.getAction()     // Catch:{ all -> 0x00a6 }
            android.content.ComponentName r3 = r14.getComponent()     // Catch:{ all -> 0x00a6 }
            r4 = 1
            if (r2 == 0) goto L_0x0044
            java.util.Iterator r5 = r1.iterator()     // Catch:{ all -> 0x00a6 }
        L_0x002b:
            boolean r6 = r5.hasNext()     // Catch:{ all -> 0x00a6 }
            if (r6 == 0) goto L_0x0044
            java.lang.Object r6 = r5.next()     // Catch:{ all -> 0x00a6 }
            android.content.Intent r6 = (android.content.Intent) r6     // Catch:{ all -> 0x00a6 }
            java.lang.String r7 = r6.getAction()     // Catch:{ all -> 0x00a6 }
            boolean r7 = r2.equals(r7)     // Catch:{ all -> 0x00a6 }
            if (r7 == 0) goto L_0x0043
            monitor-exit(r11)     // Catch:{ all -> 0x00a6 }
            return r4
        L_0x0043:
            goto L_0x002b
        L_0x0044:
            if (r3 == 0) goto L_0x00a4
            java.lang.String r5 = r3.getClassName()     // Catch:{ all -> 0x00a6 }
            if (r5 != 0) goto L_0x004e
            monitor-exit(r11)     // Catch:{ all -> 0x00a6 }
            return r0
        L_0x004e:
            char r6 = r5.charAt(r0)     // Catch:{ all -> 0x00a6 }
            r7 = 46
            if (r6 != r7) goto L_0x006a
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a6 }
            r6.<init>()     // Catch:{ all -> 0x00a6 }
            java.lang.String r7 = r3.getPackageName()     // Catch:{ all -> 0x00a6 }
            r6.append(r7)     // Catch:{ all -> 0x00a6 }
            r6.append(r5)     // Catch:{ all -> 0x00a6 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00a6 }
            goto L_0x006b
        L_0x006a:
            r6 = r5
        L_0x006b:
            if (r12 != 0) goto L_0x0081
            java.lang.String r7 = "com.tencent.mm.plugin.voip.ui.VideoActivity"
            boolean r7 = r7.equals(r5)     // Catch:{ all -> 0x00a6 }
            if (r7 == 0) goto L_0x0081
            int r7 = r14.getFlags()     // Catch:{ all -> 0x00a6 }
            r8 = -268435457(0xffffffffefffffff, float:-1.5845632E29)
            r7 = r7 & r8
            if (r7 != 0) goto L_0x0081
            monitor-exit(r11)     // Catch:{ all -> 0x00a6 }
            return r0
        L_0x0081:
            java.util.Iterator r7 = r1.iterator()     // Catch:{ all -> 0x00a6 }
        L_0x0085:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x00a6 }
            if (r8 == 0) goto L_0x00a4
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x00a6 }
            android.content.Intent r8 = (android.content.Intent) r8     // Catch:{ all -> 0x00a6 }
            android.content.ComponentName r9 = r8.getComponent()     // Catch:{ all -> 0x00a6 }
            if (r9 == 0) goto L_0x00a3
            java.lang.String r10 = r9.getClassName()     // Catch:{ all -> 0x00a6 }
            boolean r10 = r6.equals(r10)     // Catch:{ all -> 0x00a6 }
            if (r10 == 0) goto L_0x00a3
            monitor-exit(r11)     // Catch:{ all -> 0x00a6 }
            return r4
        L_0x00a3:
            goto L_0x0085
        L_0x00a4:
            monitor-exit(r11)     // Catch:{ all -> 0x00a6 }
            return r0
        L_0x00a6:
            r0 = move-exception
            monitor-exit(r11)     // Catch:{ all -> 0x00a6 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.AccessController.filterIntentLocked(boolean, java.lang.String, android.content.Intent):boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean skipActivity(Intent intent, String callingPkg) {
        if (intent != null) {
            try {
                ComponentName componentName = intent.getComponent();
                if (componentName != null) {
                    String packageName = componentName.getPackageName();
                    String activity = componentName.getClassName();
                    if (!isOpenedPkg(callingPkg) || TextUtils.isEmpty(packageName) || TextUtils.isEmpty(activity) || !PACKAGE_GALLERY.equals(packageName) || !isOpenedActivity(activity) || !intent.getBooleanExtra("skip_interception", false)) {
                        return false;
                    }
                    return true;
                }
            } catch (Throwable e) {
                Slog.e(TAG, "can not getStringExtra" + e);
            }
        }
        return false;
    }

    private static boolean isOpenedPkg(String callingPkg) {
        return PACKAGE_GALLERY.equals(callingPkg) || PACKAGE_SYSTEMUI.equals(callingPkg) || PACKAGE_CAMERA.equals(callingPkg) || PACKAGE_MEITU_CAMERA.equals(callingPkg);
    }

    private static boolean isOpenedActivity(String activity) {
        return SKIP_INTERCEPT_ACTIVITY_GALLERY_EXTRA.equals(activity) || SKIP_INTERCEPT_ACTIVITY_GALLERY_EDIT.equals(activity);
    }

    private void setAccessControlPattern(String pattern, int userId) {
        byte[] hash = null;
        if (pattern != null) {
            hash = LockPatternUtils.patternToHash(LockPatternUtils.stringToPattern(pattern));
        }
        writeFile(getFilePathForUser(userId, ACCESS_CONTROL), hash);
    }

    /* access modifiers changed from: package-private */
    public void setAccessControlPassword(String passwordType, String password, int userId) {
        if (PASSWORD_TYPE_PATTERN.equals(passwordType)) {
            setAccessControlPattern(password, userId);
            setAccessControlPasswordType(passwordType, userId);
            return;
        }
        byte[] hash = null;
        if (password != null) {
            hash = passwordToHash(password, userId);
        }
        writeFile(getFilePathForUser(userId, ACCESS_CONTROL), hash);
        setAccessControlPasswordType(passwordType, userId);
    }

    private boolean checkAccessControlPattern(String pattern, int userId) {
        if (pattern == null) {
            return false;
        }
        return Arrays.equals(readFile(getFilePathForUser(userId, ACCESS_CONTROL)), LockPatternUtils.patternToHash(LockPatternUtils.stringToPattern(pattern)));
    }

    /* access modifiers changed from: package-private */
    public boolean checkAccessControlPassword(String passwordType, String password, int userId) {
        if (password == null || passwordType == null) {
            return false;
        }
        if (PASSWORD_TYPE_PATTERN.equals(passwordType)) {
            return checkAccessControlPattern(password, userId);
        }
        return Arrays.equals(readFile(getFilePathForUser(userId, ACCESS_CONTROL)), passwordToHash(password, userId));
    }

    private boolean haveAccessControlPattern(int userId) {
        boolean z;
        String filePath = getFilePathForUser(userId, ACCESS_CONTROL);
        synchronized (this.mFileWriteLock) {
            File file = new File(filePath);
            z = file.exists() && file.length() > 0;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public boolean haveAccessControlPassword(int userId) {
        boolean z;
        String filePathType = getFilePathForUser(userId, ACCESS_CONTROL_PASSWORD_TYPE_KEY);
        String filePathPassword = getFilePathForUser(userId, ACCESS_CONTROL);
        synchronized (this.mFileWriteLock) {
            File fileType = new File(filePathType);
            File filePassword = new File(filePathPassword);
            z = fileType.exists() && filePassword.exists() && fileType.length() > 0 && filePassword.length() > 0;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public String getAccessControlPasswordType(int userId) {
        String filePath = getFilePathForUser(userId, ACCESS_CONTROL_PASSWORD_TYPE_KEY);
        if (filePath == null) {
            return PASSWORD_TYPE_PATTERN;
        }
        return readTypeFile(filePath);
    }

    /* access modifiers changed from: package-private */
    public void updatePasswordTypeForPattern(int userId) {
        if (haveAccessControlPattern(userId) && !haveAccessControlPasswordType(userId)) {
            setAccessControlPasswordType(PASSWORD_TYPE_PATTERN, userId);
            Log.d(TAG, "update password type succeed");
        }
    }

    private void setAccessControlPasswordType(String passwordType, int userId) {
        writeTypeFile(getFilePathForUser(userId, ACCESS_CONTROL_PASSWORD_TYPE_KEY), passwordType);
    }

    private boolean haveAccessControlPasswordType(int userId) {
        boolean z;
        String filePath = getFilePathForUser(userId, ACCESS_CONTROL_PASSWORD_TYPE_KEY);
        synchronized (this.mFileWriteLock) {
            File file = new File(filePath);
            z = file.exists() && file.length() > 0;
        }
        return z;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        r5 = "Error closing file " + r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003b, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0072, code lost:
        if (r1 != null) goto L_0x0074;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0078, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        android.util.Slog.e(TAG, "Error closing file " + r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0090, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0022, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0023, code lost:
        r4 = TAG;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:5:0x001e, B:15:0x0040] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private byte[] readFile(java.lang.String r9) {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mFileWriteLock
            monitor-enter(r0)
            r1 = 0
            r2 = 0
            java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ IOException -> 0x003d }
            java.lang.String r4 = "r"
            r3.<init>(r9, r4)     // Catch:{ IOException -> 0x003d }
            r1 = r3
            long r3 = r1.length()     // Catch:{ IOException -> 0x003d }
            int r3 = (int) r3     // Catch:{ IOException -> 0x003d }
            byte[] r3 = new byte[r3]     // Catch:{ IOException -> 0x003d }
            r2 = r3
            r3 = 0
            int r4 = r2.length     // Catch:{ IOException -> 0x003d }
            r1.readFully(r2, r3, r4)     // Catch:{ IOException -> 0x003d }
            r1.close()     // Catch:{ IOException -> 0x003d }
            r1.close()     // Catch:{ IOException -> 0x0022 }
        L_0x0021:
            goto L_0x0070
        L_0x0022:
            r3 = move-exception
            java.lang.String r4 = "AccessController"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0091 }
            r5.<init>()     // Catch:{ all -> 0x0091 }
            java.lang.String r6 = "Error closing file "
            r5.append(r6)     // Catch:{ all -> 0x0091 }
            r5.append(r3)     // Catch:{ all -> 0x0091 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0091 }
        L_0x0036:
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x0091 }
            goto L_0x0070
        L_0x003b:
            r3 = move-exception
            goto L_0x0072
        L_0x003d:
            r3 = move-exception
            java.lang.String r4 = "AccessController"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x003b }
            r5.<init>()     // Catch:{ all -> 0x003b }
            java.lang.String r6 = "Cannot read file "
            r5.append(r6)     // Catch:{ all -> 0x003b }
            r5.append(r3)     // Catch:{ all -> 0x003b }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x003b }
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x003b }
            if (r1 == 0) goto L_0x0070
            r1.close()     // Catch:{ IOException -> 0x005b }
            goto L_0x0021
        L_0x005b:
            r3 = move-exception
            java.lang.String r4 = "AccessController"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0091 }
            r5.<init>()     // Catch:{ all -> 0x0091 }
            java.lang.String r6 = "Error closing file "
            r5.append(r6)     // Catch:{ all -> 0x0091 }
            r5.append(r3)     // Catch:{ all -> 0x0091 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0091 }
            goto L_0x0036
        L_0x0070:
            monitor-exit(r0)     // Catch:{ all -> 0x0091 }
            return r2
        L_0x0072:
            if (r1 == 0) goto L_0x008f
            r1.close()     // Catch:{ IOException -> 0x0078 }
            goto L_0x008f
        L_0x0078:
            r4 = move-exception
            java.lang.String r5 = "AccessController"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0091 }
            r6.<init>()     // Catch:{ all -> 0x0091 }
            java.lang.String r7 = "Error closing file "
            r6.append(r7)     // Catch:{ all -> 0x0091 }
            r6.append(r4)     // Catch:{ all -> 0x0091 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0091 }
            android.util.Slog.e(r5, r6)     // Catch:{ all -> 0x0091 }
        L_0x008f:
            throw r3     // Catch:{ all -> 0x0091 }
        L_0x0091:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0091 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.AccessController.readFile(java.lang.String):byte[]");
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        r5 = "Error closing file " + r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0033, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006a, code lost:
        if (r1 != null) goto L_0x006c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0070, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        android.util.Slog.e(TAG, "Error closing file " + r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0088, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001a, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001b, code lost:
        r4 = TAG;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:5:0x0016, B:15:0x0038] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String readTypeFile(java.lang.String r9) {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mFileWriteLock
            monitor-enter(r0)
            r1 = 0
            r2 = 0
            java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ IOException -> 0x0035 }
            java.lang.String r4 = "r"
            r3.<init>(r9, r4)     // Catch:{ IOException -> 0x0035 }
            r1 = r3
            java.lang.String r3 = r1.readLine()     // Catch:{ IOException -> 0x0035 }
            r2 = r3
            r1.close()     // Catch:{ IOException -> 0x0035 }
            r1.close()     // Catch:{ IOException -> 0x001a }
        L_0x0019:
            goto L_0x0068
        L_0x001a:
            r3 = move-exception
            java.lang.String r4 = "AccessController"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0089 }
            r5.<init>()     // Catch:{ all -> 0x0089 }
            java.lang.String r6 = "Error closing file "
            r5.append(r6)     // Catch:{ all -> 0x0089 }
            r5.append(r3)     // Catch:{ all -> 0x0089 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0089 }
        L_0x002e:
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x0089 }
            goto L_0x0068
        L_0x0033:
            r3 = move-exception
            goto L_0x006a
        L_0x0035:
            r3 = move-exception
            java.lang.String r4 = "AccessController"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0033 }
            r5.<init>()     // Catch:{ all -> 0x0033 }
            java.lang.String r6 = "Cannot read file "
            r5.append(r6)     // Catch:{ all -> 0x0033 }
            r5.append(r3)     // Catch:{ all -> 0x0033 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0033 }
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x0033 }
            if (r1 == 0) goto L_0x0068
            r1.close()     // Catch:{ IOException -> 0x0053 }
            goto L_0x0019
        L_0x0053:
            r3 = move-exception
            java.lang.String r4 = "AccessController"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0089 }
            r5.<init>()     // Catch:{ all -> 0x0089 }
            java.lang.String r6 = "Error closing file "
            r5.append(r6)     // Catch:{ all -> 0x0089 }
            r5.append(r3)     // Catch:{ all -> 0x0089 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0089 }
            goto L_0x002e
        L_0x0068:
            monitor-exit(r0)     // Catch:{ all -> 0x0089 }
            return r2
        L_0x006a:
            if (r1 == 0) goto L_0x0087
            r1.close()     // Catch:{ IOException -> 0x0070 }
            goto L_0x0087
        L_0x0070:
            r4 = move-exception
            java.lang.String r5 = "AccessController"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0089 }
            r6.<init>()     // Catch:{ all -> 0x0089 }
            java.lang.String r7 = "Error closing file "
            r6.append(r7)     // Catch:{ all -> 0x0089 }
            r6.append(r4)     // Catch:{ all -> 0x0089 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0089 }
            android.util.Slog.e(r5, r6)     // Catch:{ all -> 0x0089 }
        L_0x0087:
            throw r3     // Catch:{ all -> 0x0089 }
        L_0x0089:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0089 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.AccessController.readTypeFile(java.lang.String):java.lang.String");
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0020, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0021, code lost:
        r3 = TAG;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r4 = "Error closing file " + r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0039, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0070, code lost:
        if (r1 != null) goto L_0x0072;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0076, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        android.util.Slog.e(TAG, "Error closing file " + r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x008e, code lost:
        throw r2;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:8:0x001c, B:18:0x003e] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void writeFile(java.lang.String r8, byte[] r9) {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mFileWriteLock
            monitor-enter(r0)
            r1 = 0
            java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ IOException -> 0x003b }
            java.lang.String r3 = "rw"
            r2.<init>(r8, r3)     // Catch:{ IOException -> 0x003b }
            r1 = r2
            r2 = 0
            r1.setLength(r2)     // Catch:{ IOException -> 0x003b }
            if (r9 == 0) goto L_0x0018
            r2 = 0
            int r3 = r9.length     // Catch:{ IOException -> 0x003b }
            r1.write(r9, r2, r3)     // Catch:{ IOException -> 0x003b }
        L_0x0018:
            r1.close()     // Catch:{ IOException -> 0x003b }
            r1.close()     // Catch:{ IOException -> 0x0020 }
        L_0x001f:
            goto L_0x006e
        L_0x0020:
            r2 = move-exception
            java.lang.String r3 = "AccessController"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x008f }
            r4.<init>()     // Catch:{ all -> 0x008f }
            java.lang.String r5 = "Error closing file "
            r4.append(r5)     // Catch:{ all -> 0x008f }
            r4.append(r2)     // Catch:{ all -> 0x008f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x008f }
        L_0x0034:
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x008f }
            goto L_0x006e
        L_0x0039:
            r2 = move-exception
            goto L_0x0070
        L_0x003b:
            r2 = move-exception
            java.lang.String r3 = "AccessController"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0039 }
            r4.<init>()     // Catch:{ all -> 0x0039 }
            java.lang.String r5 = "Error writing to file "
            r4.append(r5)     // Catch:{ all -> 0x0039 }
            r4.append(r2)     // Catch:{ all -> 0x0039 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0039 }
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x0039 }
            if (r1 == 0) goto L_0x006e
            r1.close()     // Catch:{ IOException -> 0x0059 }
            goto L_0x001f
        L_0x0059:
            r2 = move-exception
            java.lang.String r3 = "AccessController"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x008f }
            r4.<init>()     // Catch:{ all -> 0x008f }
            java.lang.String r5 = "Error closing file "
            r4.append(r5)     // Catch:{ all -> 0x008f }
            r4.append(r2)     // Catch:{ all -> 0x008f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x008f }
            goto L_0x0034
        L_0x006e:
            monitor-exit(r0)     // Catch:{ all -> 0x008f }
            return
        L_0x0070:
            if (r1 == 0) goto L_0x008d
            r1.close()     // Catch:{ IOException -> 0x0076 }
            goto L_0x008d
        L_0x0076:
            r3 = move-exception
            java.lang.String r4 = "AccessController"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x008f }
            r5.<init>()     // Catch:{ all -> 0x008f }
            java.lang.String r6 = "Error closing file "
            r5.append(r6)     // Catch:{ all -> 0x008f }
            r5.append(r3)     // Catch:{ all -> 0x008f }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x008f }
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x008f }
        L_0x008d:
            throw r2     // Catch:{ all -> 0x008f }
        L_0x008f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x008f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.AccessController.writeFile(java.lang.String, byte[]):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001e, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001f, code lost:
        r3 = TAG;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r4 = "Error closing type file " + r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0037, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x006e, code lost:
        if (r1 != null) goto L_0x0070;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0074, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        android.util.Slog.e(TAG, "Error closing type file " + r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x008c, code lost:
        throw r2;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:8:0x001a, B:18:0x003c] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void writeTypeFile(java.lang.String r8, java.lang.String r9) {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mFileWriteLock
            monitor-enter(r0)
            r1 = 0
            java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ IOException -> 0x0039 }
            java.lang.String r3 = "rw"
            r2.<init>(r8, r3)     // Catch:{ IOException -> 0x0039 }
            r1 = r2
            r2 = 0
            r1.setLength(r2)     // Catch:{ IOException -> 0x0039 }
            if (r9 == 0) goto L_0x0016
            r1.writeBytes(r9)     // Catch:{ IOException -> 0x0039 }
        L_0x0016:
            r1.close()     // Catch:{ IOException -> 0x0039 }
            r1.close()     // Catch:{ IOException -> 0x001e }
        L_0x001d:
            goto L_0x006c
        L_0x001e:
            r2 = move-exception
            java.lang.String r3 = "AccessController"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x008d }
            r4.<init>()     // Catch:{ all -> 0x008d }
            java.lang.String r5 = "Error closing type file "
            r4.append(r5)     // Catch:{ all -> 0x008d }
            r4.append(r2)     // Catch:{ all -> 0x008d }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x008d }
        L_0x0032:
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x008d }
            goto L_0x006c
        L_0x0037:
            r2 = move-exception
            goto L_0x006e
        L_0x0039:
            r2 = move-exception
            java.lang.String r3 = "AccessController"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0037 }
            r4.<init>()     // Catch:{ all -> 0x0037 }
            java.lang.String r5 = "Error writing type to file "
            r4.append(r5)     // Catch:{ all -> 0x0037 }
            r4.append(r2)     // Catch:{ all -> 0x0037 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0037 }
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x0037 }
            if (r1 == 0) goto L_0x006c
            r1.close()     // Catch:{ IOException -> 0x0057 }
            goto L_0x001d
        L_0x0057:
            r2 = move-exception
            java.lang.String r3 = "AccessController"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x008d }
            r4.<init>()     // Catch:{ all -> 0x008d }
            java.lang.String r5 = "Error closing type file "
            r4.append(r5)     // Catch:{ all -> 0x008d }
            r4.append(r2)     // Catch:{ all -> 0x008d }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x008d }
            goto L_0x0032
        L_0x006c:
            monitor-exit(r0)     // Catch:{ all -> 0x008d }
            return
        L_0x006e:
            if (r1 == 0) goto L_0x008b
            r1.close()     // Catch:{ IOException -> 0x0074 }
            goto L_0x008b
        L_0x0074:
            r3 = move-exception
            java.lang.String r4 = "AccessController"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x008d }
            r5.<init>()     // Catch:{ all -> 0x008d }
            java.lang.String r6 = "Error closing type file "
            r5.append(r6)     // Catch:{ all -> 0x008d }
            r5.append(r3)     // Catch:{ all -> 0x008d }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x008d }
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x008d }
        L_0x008b:
            throw r2     // Catch:{ all -> 0x008d }
        L_0x008d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x008d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.AccessController.writeTypeFile(java.lang.String, java.lang.String):void");
    }

    private String getFilePathForUser(int userId, String fileName) {
        String dataSystemDirectory = Environment.getDataDirectory().getAbsolutePath() + SYSTEM_DIRECTORY;
        if (userId != 0) {
            return new File(Environment.getUserSystemDirectory(userId), fileName).getAbsolutePath();
        }
        return dataSystemDirectory + fileName;
    }

    public void updateWhiteList() {
        try {
            ContentResolver resolver = this.mContext.getContentResolver();
            this.mWorkHandler.removeMessages(1);
            this.mWorkHandler.sendEmptyMessageDelayed(1, 43200000);
            List<MiuiSettings.SettingsCloudData.CloudData> appLockList = MiuiSettings.SettingsCloudData.getCloudDataList(resolver, APPLOCK_WHILTE);
            List<MiuiSettings.SettingsCloudData.CloudData> gameAntimsgList = MiuiSettings.SettingsCloudData.getCloudDataList(resolver, GAMEBOOSTER_ANTIMSG);
            updateWhiteList(appLockList, mSkipList);
            updateWhiteList(gameAntimsgList, mAntimsgInterceptList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    private void updateWhiteList(List<MiuiSettings.SettingsCloudData.CloudData> dataList, ArrayMap<String, ArrayList<Intent>> list) {
        if (dataList != null) {
            try {
                if (dataList.size() != 0) {
                    ArrayMap<String, ArrayList<Intent>> cloudList = new ArrayMap<>();
                    for (MiuiSettings.SettingsCloudData.CloudData data : dataList) {
                        String json = data.toString();
                        if (!TextUtils.isEmpty(json)) {
                            JSONObject jsonObject = new JSONObject(json);
                            String pkg = jsonObject.optString(SplitScreenReporter.STR_PKG);
                            String cls = jsonObject.optString("cls");
                            String action = jsonObject.optString("act");
                            Intent intent = new Intent();
                            if (!TextUtils.isEmpty(action)) {
                                intent.setAction(action);
                            } else {
                                intent.setComponent(new ComponentName(pkg, cls));
                            }
                            ArrayList<Intent> intents = cloudList.get(pkg);
                            if (intents == null) {
                                intents = new ArrayList<>(1);
                                cloudList.put(pkg, intents);
                            }
                            intents.add(intent);
                        }
                    }
                    if (cloudList.size() > 0) {
                        synchronized (this) {
                            list.clear();
                            list.putAll(cloudList);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] passwordToHash(String password, int userId) {
        Object hash;
        if (TextUtils.isEmpty(password)) {
            return null;
        }
        try {
            if (Build.VERSION.SDK_INT > 28) {
                hash = mPasswordToHash.invoke(this.mLockPatternUtils, new Object[]{password.getBytes(), Integer.valueOf(userId)});
            } else {
                hash = mPasswordToHash.invoke(this.mLockPatternUtils, new Object[]{password, Integer.valueOf(userId)});
            }
            if (hash != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    return ((String) hash).getBytes(StandardCharsets.UTF_8);
                }
                return (byte[]) hash;
            }
        } catch (Exception e) {
            Log.e(TAG, " passwordToHash invoke error", e);
        }
        return null;
    }
}

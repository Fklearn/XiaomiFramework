package com.android.server.textservices;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.view.inputmethod.InputMethodSystemProperty;
import android.view.textservice.SpellCheckerInfo;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.inputmethod.SubtypeLocaleUtils;
import com.android.internal.textservice.ISpellCheckerService;
import com.android.internal.textservice.ISpellCheckerServiceCallback;
import com.android.internal.textservice.ISpellCheckerSession;
import com.android.internal.textservice.ISpellCheckerSessionListener;
import com.android.internal.textservice.ITextServicesManager;
import com.android.internal.textservice.ITextServicesSessionListener;
import com.android.internal.util.DumpUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.am.AutoStartManagerService;
import com.android.server.textservices.TextServicesManagerService;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import org.xmlpull.v1.XmlPullParserException;

public class TextServicesManagerService extends ITextServicesManager.Stub {
    private static final boolean DBG = false;
    /* access modifiers changed from: private */
    public static final String TAG = TextServicesManagerService.class.getSimpleName();
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private final TextServicesMonitor mMonitor;
    @GuardedBy({"mLock"})
    private final LazyIntToIntMap mSpellCheckerOwnerUserIdMap;
    /* access modifiers changed from: private */
    public final SparseArray<TextServicesData> mUserData = new SparseArray<>();
    private final UserManager mUserManager;

    private static class TextServicesData {
        private final Context mContext;
        private final ContentResolver mResolver;
        /* access modifiers changed from: private */
        public final HashMap<String, SpellCheckerBindGroup> mSpellCheckerBindGroups;
        /* access modifiers changed from: private */
        public final ArrayList<SpellCheckerInfo> mSpellCheckerList;
        /* access modifiers changed from: private */
        public final HashMap<String, SpellCheckerInfo> mSpellCheckerMap;
        public int mUpdateCount = 0;
        /* access modifiers changed from: private */
        public final int mUserId;

        public TextServicesData(int userId, Context context) {
            this.mUserId = userId;
            this.mSpellCheckerMap = new HashMap<>();
            this.mSpellCheckerList = new ArrayList<>();
            this.mSpellCheckerBindGroups = new HashMap<>();
            this.mContext = context;
            this.mResolver = context.getContentResolver();
        }

        private void putString(String key, String str) {
            Settings.Secure.putStringForUser(this.mResolver, key, str, this.mUserId);
        }

        private String getString(String key, String defaultValue) {
            String result = Settings.Secure.getStringForUser(this.mResolver, key, this.mUserId);
            return result != null ? result : defaultValue;
        }

        private void putInt(String key, int value) {
            Settings.Secure.putIntForUser(this.mResolver, key, value, this.mUserId);
        }

        private int getInt(String key, int defaultValue) {
            return Settings.Secure.getIntForUser(this.mResolver, key, defaultValue, this.mUserId);
        }

        private boolean getBoolean(String key, boolean defaultValue) {
            return getInt(key, defaultValue) == 1;
        }

        private void putSelectedSpellChecker(String sciId) {
            putString("selected_spell_checker", sciId);
        }

        private void putSelectedSpellCheckerSubtype(int hashCode) {
            putInt("selected_spell_checker_subtype", hashCode);
        }

        private String getSelectedSpellChecker() {
            return getString("selected_spell_checker", "");
        }

        public int getSelectedSpellCheckerSubtype(int defaultValue) {
            return getInt("selected_spell_checker_subtype", defaultValue);
        }

        public boolean isSpellCheckerEnabled() {
            return getBoolean("spell_checker_enabled", true);
        }

        public SpellCheckerInfo getCurrentSpellChecker() {
            String curSpellCheckerId = getSelectedSpellChecker();
            if (TextUtils.isEmpty(curSpellCheckerId)) {
                return null;
            }
            return this.mSpellCheckerMap.get(curSpellCheckerId);
        }

        public void setCurrentSpellChecker(SpellCheckerInfo sci) {
            if (sci != null) {
                putSelectedSpellChecker(sci.getId());
            } else {
                putSelectedSpellChecker("");
            }
            putSelectedSpellCheckerSubtype(0);
        }

        /* access modifiers changed from: private */
        public void initializeTextServicesData() {
            this.mSpellCheckerList.clear();
            this.mSpellCheckerMap.clear();
            this.mUpdateCount++;
            List<ResolveInfo> services2 = this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.service.textservice.SpellCheckerService"), 128, this.mUserId);
            int N = services2.size();
            for (int i = 0; i < N; i++) {
                ResolveInfo ri = services2.get(i);
                ServiceInfo si = ri.serviceInfo;
                ComponentName compName = new ComponentName(si.packageName, si.name);
                if (!"android.permission.BIND_TEXT_SERVICE".equals(si.permission)) {
                    Slog.w(TextServicesManagerService.TAG, "Skipping text service " + compName + ": it does not require the permission " + "android.permission.BIND_TEXT_SERVICE");
                } else {
                    try {
                        SpellCheckerInfo sci = new SpellCheckerInfo(this.mContext, ri);
                        if (sci.getSubtypeCount() <= 0) {
                            Slog.w(TextServicesManagerService.TAG, "Skipping text service " + compName + ": it does not contain subtypes.");
                        } else {
                            this.mSpellCheckerList.add(sci);
                            this.mSpellCheckerMap.put(sci.getId(), sci);
                        }
                    } catch (XmlPullParserException e) {
                        Slog.w(TextServicesManagerService.TAG, "Unable to load the spell checker " + compName, e);
                    } catch (IOException e2) {
                        Slog.w(TextServicesManagerService.TAG, "Unable to load the spell checker " + compName, e2);
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void dump(PrintWriter pw) {
            PrintWriter printWriter = pw;
            int spellCheckerIndex = 0;
            printWriter.println("  User #" + this.mUserId);
            printWriter.println("  Spell Checkers:");
            printWriter.println("  Spell Checkers: mUpdateCount=" + this.mUpdateCount);
            for (SpellCheckerInfo info : this.mSpellCheckerMap.values()) {
                printWriter.println("  Spell Checker #" + spellCheckerIndex);
                info.dump(printWriter, "    ");
                spellCheckerIndex++;
            }
            printWriter.println("");
            printWriter.println("  Spell Checker Bind Groups:");
            for (Map.Entry<String, SpellCheckerBindGroup> ent : this.mSpellCheckerBindGroups.entrySet()) {
                SpellCheckerBindGroup grp = ent.getValue();
                printWriter.println("    " + ent.getKey() + " " + grp + ":");
                StringBuilder sb = new StringBuilder();
                sb.append("      mInternalConnection=");
                sb.append(grp.mInternalConnection);
                printWriter.println(sb.toString());
                printWriter.println("      mSpellChecker=" + grp.mSpellChecker);
                printWriter.println("      mUnbindCalled=" + grp.mUnbindCalled);
                printWriter.println("      mConnected=" + grp.mConnected);
                int numPendingSessionRequests = grp.mPendingSessionRequests.size();
                int j = 0;
                while (j < numPendingSessionRequests) {
                    SessionRequest req = (SessionRequest) grp.mPendingSessionRequests.get(j);
                    printWriter.println("      Pending Request #" + j + ":");
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("        mTsListener=");
                    sb2.append(req.mTsListener);
                    printWriter.println(sb2.toString());
                    printWriter.println("        mScListener=" + req.mScListener);
                    printWriter.println("        mScLocale=" + req.mLocale + " mUid=" + req.mUid);
                    j++;
                    spellCheckerIndex = spellCheckerIndex;
                }
                int spellCheckerIndex2 = spellCheckerIndex;
                int numOnGoingSessionRequests = grp.mOnGoingSessionRequests.size();
                int j2 = 0;
                while (j2 < numOnGoingSessionRequests) {
                    SessionRequest req2 = (SessionRequest) grp.mOnGoingSessionRequests.get(j2);
                    StringBuilder sb3 = new StringBuilder();
                    int numOnGoingSessionRequests2 = numOnGoingSessionRequests;
                    sb3.append("      On going Request #");
                    sb3.append(j2);
                    sb3.append(":");
                    printWriter.println(sb3.toString());
                    printWriter.println("        mTsListener=" + req2.mTsListener);
                    printWriter.println("        mScListener=" + req2.mScListener);
                    printWriter.println("        mScLocale=" + req2.mLocale + " mUid=" + req2.mUid);
                    j2++;
                    numOnGoingSessionRequests = numOnGoingSessionRequests2;
                }
                int N = grp.mListeners.getRegisteredCallbackCount();
                for (int j3 = 0; j3 < N; j3++) {
                    printWriter.println("      Listener #" + j3 + ":");
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("        mScListener=");
                    sb4.append(grp.mListeners.getRegisteredCallbackItem(j3));
                    printWriter.println(sb4.toString());
                    printWriter.println("        mGroup=" + grp);
                }
                spellCheckerIndex = spellCheckerIndex2;
            }
        }
    }

    public static final class Lifecycle extends SystemService {
        /* access modifiers changed from: private */
        public TextServicesManagerService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new TextServicesManagerService(context);
        }

        /* JADX WARNING: type inference failed for: r0v1, types: [com.android.server.textservices.TextServicesManagerService, android.os.IBinder] */
        public void onStart() {
            LocalServices.addService(TextServicesManagerInternal.class, new TextServicesManagerInternal() {
                public SpellCheckerInfo getCurrentSpellCheckerForUser(int userId) {
                    return Lifecycle.this.mService.getCurrentSpellCheckerForUser(userId);
                }
            });
            publishBinderService("textservices", this.mService);
        }

        public void onStopUser(int userHandle) {
            this.mService.onStopUser(userHandle);
        }

        public void onUnlockUser(int userHandle) {
            this.mService.onUnlockUser(userHandle);
        }
    }

    /* access modifiers changed from: package-private */
    public void onStopUser(int userId) {
        synchronized (this.mLock) {
            this.mSpellCheckerOwnerUserIdMap.delete(userId);
            TextServicesData tsd = this.mUserData.get(userId);
            if (tsd != null) {
                unbindServiceLocked(tsd);
                this.mUserData.remove(userId);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onUnlockUser(int userId) {
        synchronized (this.mLock) {
            initializeInternalStateLocked(userId);
        }
    }

    public TextServicesManagerService(Context context) {
        this.mContext = context;
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mSpellCheckerOwnerUserIdMap = new LazyIntToIntMap(new IntUnaryOperator() {
            public final int applyAsInt(int i) {
                return TextServicesManagerService.this.lambda$new$0$TextServicesManagerService(i);
            }
        });
        this.mMonitor = new TextServicesMonitor();
        this.mMonitor.register(context, (Looper) null, UserHandle.ALL, true);
    }

    public /* synthetic */ int lambda$new$0$TextServicesManagerService(int callingUserId) {
        if (InputMethodSystemProperty.PER_PROFILE_IME_ENABLED) {
            return callingUserId;
        }
        long token = Binder.clearCallingIdentity();
        try {
            UserInfo parent = this.mUserManager.getProfileParent(callingUserId);
            return parent != null ? parent.id : callingUserId;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @GuardedBy({"mLock"})
    private void initializeInternalStateLocked(int userId) {
        if (InputMethodSystemProperty.PER_PROFILE_IME_ENABLED || userId == this.mSpellCheckerOwnerUserIdMap.get(userId)) {
            TextServicesData tsd = this.mUserData.get(userId);
            if (tsd == null) {
                tsd = new TextServicesData(userId, this.mContext);
                this.mUserData.put(userId, tsd);
            }
            tsd.initializeTextServicesData();
            if (tsd.getCurrentSpellChecker() == null) {
                setCurrentSpellCheckerLocked(findAvailSystemSpellCheckerLocked((String) null, tsd), tsd);
            }
        }
    }

    private final class TextServicesMonitor extends PackageMonitor {
        private TextServicesMonitor() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0064, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onSomePackagesChanged() {
            /*
                r9 = this;
                int r0 = r9.getChangingUserId()
                com.android.server.textservices.TextServicesManagerService r1 = com.android.server.textservices.TextServicesManagerService.this
                java.lang.Object r1 = r1.mLock
                monitor-enter(r1)
                com.android.server.textservices.TextServicesManagerService r2 = com.android.server.textservices.TextServicesManagerService.this     // Catch:{ all -> 0x0065 }
                android.util.SparseArray r2 = r2.mUserData     // Catch:{ all -> 0x0065 }
                java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x0065 }
                com.android.server.textservices.TextServicesManagerService$TextServicesData r2 = (com.android.server.textservices.TextServicesManagerService.TextServicesData) r2     // Catch:{ all -> 0x0065 }
                if (r2 != 0) goto L_0x001b
                monitor-exit(r1)     // Catch:{ all -> 0x0065 }
                return
            L_0x001b:
                android.view.textservice.SpellCheckerInfo r3 = r2.getCurrentSpellChecker()     // Catch:{ all -> 0x0065 }
                r2.initializeTextServicesData()     // Catch:{ all -> 0x0065 }
                boolean r4 = r2.isSpellCheckerEnabled()     // Catch:{ all -> 0x0065 }
                if (r4 != 0) goto L_0x002a
                monitor-exit(r1)     // Catch:{ all -> 0x0065 }
                return
            L_0x002a:
                if (r3 != 0) goto L_0x003a
                com.android.server.textservices.TextServicesManagerService r4 = com.android.server.textservices.TextServicesManagerService.this     // Catch:{ all -> 0x0065 }
                r5 = 0
                android.view.textservice.SpellCheckerInfo r4 = r4.findAvailSystemSpellCheckerLocked(r5, r2)     // Catch:{ all -> 0x0065 }
                r3 = r4
                com.android.server.textservices.TextServicesManagerService r4 = com.android.server.textservices.TextServicesManagerService.this     // Catch:{ all -> 0x0065 }
                r4.setCurrentSpellCheckerLocked(r3, r2)     // Catch:{ all -> 0x0065 }
                goto L_0x0063
            L_0x003a:
                java.lang.String r4 = r3.getPackageName()     // Catch:{ all -> 0x0065 }
                int r5 = r9.isPackageDisappearing(r4)     // Catch:{ all -> 0x0065 }
                r6 = 3
                if (r5 == r6) goto L_0x0048
                r6 = 2
                if (r5 != r6) goto L_0x0063
            L_0x0048:
                com.android.server.textservices.TextServicesManagerService r6 = com.android.server.textservices.TextServicesManagerService.this     // Catch:{ all -> 0x0065 }
                android.view.textservice.SpellCheckerInfo r6 = r6.findAvailSystemSpellCheckerLocked(r4, r2)     // Catch:{ all -> 0x0065 }
                if (r6 == 0) goto L_0x005e
                java.lang.String r7 = r6.getId()     // Catch:{ all -> 0x0065 }
                java.lang.String r8 = r3.getId()     // Catch:{ all -> 0x0065 }
                boolean r7 = r7.equals(r8)     // Catch:{ all -> 0x0065 }
                if (r7 != 0) goto L_0x0063
            L_0x005e:
                com.android.server.textservices.TextServicesManagerService r7 = com.android.server.textservices.TextServicesManagerService.this     // Catch:{ all -> 0x0065 }
                r7.setCurrentSpellCheckerLocked(r6, r2)     // Catch:{ all -> 0x0065 }
            L_0x0063:
                monitor-exit(r1)     // Catch:{ all -> 0x0065 }
                return
            L_0x0065:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0065 }
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.textservices.TextServicesManagerService.TextServicesMonitor.onSomePackagesChanged():void");
        }
    }

    private boolean bindCurrentSpellCheckerService(Intent service, ServiceConnection conn, int flags, int userId) {
        if (service == null || conn == null) {
            String str = TAG;
            Slog.e(str, "--- bind failed: service = " + service + ", conn = " + conn + ", userId =" + userId);
            return false;
        } else if (!AutoStartManagerService.isAllowStartService(this.mContext, service, userId)) {
            return false;
        } else {
            return this.mContext.bindServiceAsUser(service, conn, flags, UserHandle.of(userId));
        }
    }

    private void unbindServiceLocked(TextServicesData tsd) {
        HashMap<String, SpellCheckerBindGroup> spellCheckerBindGroups = tsd.mSpellCheckerBindGroups;
        for (SpellCheckerBindGroup scbg : spellCheckerBindGroups.values()) {
            scbg.removeAllLocked();
        }
        spellCheckerBindGroups.clear();
    }

    /* access modifiers changed from: private */
    public SpellCheckerInfo findAvailSystemSpellCheckerLocked(String prefPackage, TextServicesData tsd) {
        String str = prefPackage;
        ArrayList<SpellCheckerInfo> spellCheckerList = new ArrayList<>();
        Iterator it = tsd.mSpellCheckerList.iterator();
        while (it.hasNext()) {
            SpellCheckerInfo sci = (SpellCheckerInfo) it.next();
            if ((1 & sci.getServiceInfo().applicationInfo.flags) != 0) {
                spellCheckerList.add(sci);
            }
        }
        int spellCheckersCount = spellCheckerList.size();
        if (spellCheckersCount == 0) {
            Slog.w(TAG, "no available spell checker services found");
            return null;
        }
        if (str != null) {
            for (int i = 0; i < spellCheckersCount; i++) {
                SpellCheckerInfo sci2 = spellCheckerList.get(i);
                if (str.equals(sci2.getPackageName())) {
                    return sci2;
                }
            }
        }
        ArrayList<Locale> suitableLocales = LocaleUtils.getSuitableLocalesForSpellChecker(this.mContext.getResources().getConfiguration().locale);
        int localeCount = suitableLocales.size();
        for (int localeIndex = 0; localeIndex < localeCount; localeIndex++) {
            Locale locale = suitableLocales.get(localeIndex);
            for (int spellCheckersIndex = 0; spellCheckersIndex < spellCheckersCount; spellCheckersIndex++) {
                SpellCheckerInfo info = spellCheckerList.get(spellCheckersIndex);
                int subtypeCount = info.getSubtypeCount();
                for (int subtypeIndex = 0; subtypeIndex < subtypeCount; subtypeIndex++) {
                    if (locale.equals(SubtypeLocaleUtils.constructLocaleFromString(info.getSubtypeAt(subtypeIndex).getLocale()))) {
                        return info;
                    }
                }
            }
        }
        if (spellCheckersCount > 1) {
            Slog.w(TAG, "more than one spell checker service found, picking first");
        }
        return spellCheckerList.get(0);
    }

    /* access modifiers changed from: private */
    public SpellCheckerInfo getCurrentSpellCheckerForUser(int userId) {
        SpellCheckerInfo currentSpellChecker;
        synchronized (this.mLock) {
            TextServicesData data = this.mUserData.get(this.mSpellCheckerOwnerUserIdMap.get(userId));
            currentSpellChecker = data != null ? data.getCurrentSpellChecker() : null;
        }
        return currentSpellChecker;
    }

    public SpellCheckerInfo getCurrentSpellChecker(int userId, String locale) {
        verifyUser(userId);
        synchronized (this.mLock) {
            TextServicesData tsd = getDataFromCallingUserIdLocked(userId);
            if (tsd == null) {
                return null;
            }
            SpellCheckerInfo currentSpellChecker = tsd.getCurrentSpellChecker();
            return currentSpellChecker;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002c, code lost:
        if (r4.getSubtypeCount() != 0) goto L_0x002f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002f, code lost:
        if (r3 != 0) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0031, code lost:
        if (r12 != false) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0033, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0034, code lost:
        r0 = r4.getSubtypeCount();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0038, code lost:
        if (r3 == 0) goto L_0x004c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003a, code lost:
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003b, code lost:
        if (r5 >= r0) goto L_0x004b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
        r6 = r4.getSubtypeAt(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0045, code lost:
        if (r6.hashCode() != r3) goto L_0x0048;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0047, code lost:
        return r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0048, code lost:
        r5 = r5 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004b, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004c, code lost:
        if (r1 != null) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004e, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004f, code lost:
        r2 = null;
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0055, code lost:
        if (r5 >= r4.getSubtypeCount()) goto L_0x007c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0057, code lost:
        r6 = r4.getSubtypeAt(r5);
        r7 = r6.getLocaleObject();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0063, code lost:
        if (java.util.Objects.equals(r7, r1) == false) goto L_0x0066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0065, code lost:
        return r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0066, code lost:
        if (r2 != null) goto L_0x0079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0068, code lost:
        if (r7 == null) goto L_0x0079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0076, code lost:
        if (android.text.TextUtils.equals(r1.getLanguage(), r7.getLanguage()) == false) goto L_0x0079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0078, code lost:
        r2 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0079, code lost:
        r5 = r5 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x007c, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x007d, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0026, code lost:
        if (r4 == null) goto L_0x007d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.textservice.SpellCheckerSubtype getCurrentSpellCheckerSubtype(int r11, boolean r12) {
        /*
            r10 = this;
            r10.verifyUser(r11)
            java.lang.Object r0 = r10.mLock
            monitor-enter(r0)
            com.android.server.textservices.TextServicesManagerService$TextServicesData r1 = r10.getDataFromCallingUserIdLocked(r11)     // Catch:{ all -> 0x007e }
            r2 = 0
            if (r1 != 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x007e }
            return r2
        L_0x000f:
            r3 = 0
            int r3 = r1.getSelectedSpellCheckerSubtype(r3)     // Catch:{ all -> 0x007e }
            android.view.textservice.SpellCheckerInfo r4 = r1.getCurrentSpellChecker()     // Catch:{ all -> 0x007e }
            android.content.Context r5 = r10.mContext     // Catch:{ all -> 0x007e }
            android.content.res.Resources r5 = r5.getResources()     // Catch:{ all -> 0x007e }
            android.content.res.Configuration r5 = r5.getConfiguration()     // Catch:{ all -> 0x007e }
            java.util.Locale r5 = r5.locale     // Catch:{ all -> 0x007e }
            r1 = r5
            monitor-exit(r0)     // Catch:{ all -> 0x007e }
            if (r4 == 0) goto L_0x007d
            int r0 = r4.getSubtypeCount()
            if (r0 != 0) goto L_0x002f
            goto L_0x007d
        L_0x002f:
            if (r3 != 0) goto L_0x0034
            if (r12 != 0) goto L_0x0034
            return r2
        L_0x0034:
            int r0 = r4.getSubtypeCount()
            if (r3 == 0) goto L_0x004c
            r5 = 0
        L_0x003b:
            if (r5 >= r0) goto L_0x004b
            android.view.textservice.SpellCheckerSubtype r6 = r4.getSubtypeAt(r5)
            int r7 = r6.hashCode()
            if (r7 != r3) goto L_0x0048
            return r6
        L_0x0048:
            int r5 = r5 + 1
            goto L_0x003b
        L_0x004b:
            return r2
        L_0x004c:
            if (r1 != 0) goto L_0x004f
            return r2
        L_0x004f:
            r2 = 0
            r5 = 0
        L_0x0051:
            int r6 = r4.getSubtypeCount()
            if (r5 >= r6) goto L_0x007c
            android.view.textservice.SpellCheckerSubtype r6 = r4.getSubtypeAt(r5)
            java.util.Locale r7 = r6.getLocaleObject()
            boolean r8 = java.util.Objects.equals(r7, r1)
            if (r8 == 0) goto L_0x0066
            return r6
        L_0x0066:
            if (r2 != 0) goto L_0x0079
            if (r7 == 0) goto L_0x0079
            java.lang.String r8 = r1.getLanguage()
            java.lang.String r9 = r7.getLanguage()
            boolean r8 = android.text.TextUtils.equals(r8, r9)
            if (r8 == 0) goto L_0x0079
            r2 = r6
        L_0x0079:
            int r5 = r5 + 1
            goto L_0x0051
        L_0x007c:
            return r2
        L_0x007d:
            return r2
        L_0x007e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x007e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.textservices.TextServicesManagerService.getCurrentSpellCheckerSubtype(int, boolean):android.view.textservice.SpellCheckerSubtype");
    }

    /* Debug info: failed to restart local var, previous not found, register: 15 */
    public void getSpellCheckerService(int userId, String sciId, String locale, ITextServicesSessionListener tsListener, ISpellCheckerSessionListener scListener, Bundle bundle) {
        SpellCheckerBindGroup bindGroup;
        String str = sciId;
        verifyUser(userId);
        if (TextUtils.isEmpty(sciId) || tsListener == null || scListener == null) {
            Slog.e(TAG, "getSpellCheckerService: Invalid input.");
            return;
        }
        synchronized (this.mLock) {
            TextServicesData tsd = getDataFromCallingUserIdLocked(userId);
            if (tsd != null) {
                HashMap access$1800 = tsd.mSpellCheckerMap;
                if (access$1800.containsKey(str)) {
                    SpellCheckerInfo sci = (SpellCheckerInfo) access$1800.get(str);
                    SpellCheckerBindGroup bindGroup2 = (SpellCheckerBindGroup) tsd.mSpellCheckerBindGroups.get(str);
                    int uid = Binder.getCallingUid();
                    if (bindGroup2 == null) {
                        long ident = Binder.clearCallingIdentity();
                        try {
                            SpellCheckerBindGroup bindGroup3 = startSpellCheckerServiceInnerLocked(sci, tsd);
                            Binder.restoreCallingIdentity(ident);
                            if (bindGroup3 != null) {
                                bindGroup = bindGroup3;
                            } else {
                                return;
                            }
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            Binder.restoreCallingIdentity(ident);
                            throw th2;
                        }
                    } else {
                        bindGroup = bindGroup2;
                    }
                    bindGroup.getISpellCheckerSessionOrQueueLocked(new SessionRequest(uid, locale, tsListener, scListener, bundle));
                }
            }
        }
    }

    public boolean isSpellCheckerEnabled(int userId) {
        verifyUser(userId);
        synchronized (this.mLock) {
            TextServicesData tsd = getDataFromCallingUserIdLocked(userId);
            if (tsd == null) {
                return false;
            }
            boolean isSpellCheckerEnabled = tsd.isSpellCheckerEnabled();
            return isSpellCheckerEnabled;
        }
    }

    private SpellCheckerBindGroup startSpellCheckerServiceInnerLocked(SpellCheckerInfo info, TextServicesData tsd) {
        String sciId = info.getId();
        InternalServiceConnection connection = new InternalServiceConnection(sciId, tsd.mSpellCheckerBindGroups);
        Intent serviceIntent = new Intent("android.service.textservice.SpellCheckerService");
        serviceIntent.setComponent(info.getComponent());
        if (!bindCurrentSpellCheckerService(serviceIntent, connection, 8388609, tsd.mUserId)) {
            Slog.e(TAG, "Failed to get a spell checker service.");
            return null;
        }
        SpellCheckerBindGroup group = new SpellCheckerBindGroup(connection);
        tsd.mSpellCheckerBindGroups.put(sciId, group);
        return group;
    }

    public SpellCheckerInfo[] getEnabledSpellCheckers(int userId) {
        verifyUser(userId);
        synchronized (this.mLock) {
            TextServicesData tsd = getDataFromCallingUserIdLocked(userId);
            if (tsd == null) {
                return null;
            }
            ArrayList<SpellCheckerInfo> spellCheckerList = tsd.mSpellCheckerList;
            SpellCheckerInfo[] spellCheckerInfoArr = (SpellCheckerInfo[]) spellCheckerList.toArray(new SpellCheckerInfo[spellCheckerList.size()]);
            return spellCheckerInfoArr;
        }
    }

    public void finishSpellCheckerService(int userId, ISpellCheckerSessionListener listener) {
        verifyUser(userId);
        synchronized (this.mLock) {
            TextServicesData tsd = getDataFromCallingUserIdLocked(userId);
            if (tsd != null) {
                ArrayList<SpellCheckerBindGroup> removeList = new ArrayList<>();
                for (SpellCheckerBindGroup group : tsd.mSpellCheckerBindGroups.values()) {
                    if (group != null) {
                        removeList.add(group);
                    }
                }
                int removeSize = removeList.size();
                for (int i = 0; i < removeSize; i++) {
                    removeList.get(i).removeListener(listener);
                }
            }
        }
    }

    private void verifyUser(int userId) {
        int callingUserId = UserHandle.getCallingUserId();
        if (userId != callingUserId) {
            Context context = this.mContext;
            context.enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "Cross-user interaction requires INTERACT_ACROSS_USERS_FULL. userId=" + userId + " callingUserId=" + callingUserId);
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentSpellCheckerLocked(SpellCheckerInfo sci, TextServicesData tsd) {
        if (sci != null) {
            String id = sci.getId();
        }
        long ident = Binder.clearCallingIdentity();
        try {
            tsd.setCurrentSpellChecker(sci);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            if (args.length == 0 || (args.length == 1 && args[0].equals("-a"))) {
                synchronized (this.mLock) {
                    pw.println("Current Text Services Manager state:");
                    pw.println("  Users:");
                    int numOfUsers = this.mUserData.size();
                    for (int i = 0; i < numOfUsers; i++) {
                        this.mUserData.valueAt(i).dump(pw);
                    }
                }
            } else if (args.length != 2 || !args[0].equals("--user")) {
                pw.println("Invalid arguments to text services.");
            } else {
                int userId = Integer.parseInt(args[1]);
                if (this.mUserManager.getUserInfo(userId) == null) {
                    pw.println("Non-existent user.");
                    return;
                }
                TextServicesData tsd = this.mUserData.get(userId);
                if (tsd == null) {
                    pw.println("User needs to unlock first.");
                    return;
                }
                synchronized (this.mLock) {
                    pw.println("Current Text Services Manager state:");
                    pw.println("  User " + userId + ":");
                    tsd.dump(pw);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    private TextServicesData getDataFromCallingUserIdLocked(int callingUserId) {
        SpellCheckerInfo info;
        int spellCheckerOwnerUserId = this.mSpellCheckerOwnerUserIdMap.get(callingUserId);
        TextServicesData data = this.mUserData.get(spellCheckerOwnerUserId);
        if (InputMethodSystemProperty.PER_PROFILE_IME_ENABLED || spellCheckerOwnerUserId == callingUserId || (data != null && (info = data.getCurrentSpellChecker()) != null && (info.getServiceInfo().applicationInfo.flags & 1) != 0)) {
            return data;
        }
        return null;
    }

    private static final class SessionRequest {
        public final Bundle mBundle;
        public final String mLocale;
        public final ISpellCheckerSessionListener mScListener;
        public final ITextServicesSessionListener mTsListener;
        public final int mUid;

        SessionRequest(int uid, String locale, ITextServicesSessionListener tsListener, ISpellCheckerSessionListener scListener, Bundle bundle) {
            this.mUid = uid;
            this.mLocale = locale;
            this.mTsListener = tsListener;
            this.mScListener = scListener;
            this.mBundle = bundle;
        }
    }

    private final class SpellCheckerBindGroup {
        private final String TAG = SpellCheckerBindGroup.class.getSimpleName();
        /* access modifiers changed from: private */
        public boolean mConnected;
        /* access modifiers changed from: private */
        public final InternalServiceConnection mInternalConnection;
        /* access modifiers changed from: private */
        public final InternalDeathRecipients mListeners;
        /* access modifiers changed from: private */
        public final ArrayList<SessionRequest> mOnGoingSessionRequests = new ArrayList<>();
        /* access modifiers changed from: private */
        public final ArrayList<SessionRequest> mPendingSessionRequests = new ArrayList<>();
        /* access modifiers changed from: private */
        public ISpellCheckerService mSpellChecker;
        HashMap<String, SpellCheckerBindGroup> mSpellCheckerBindGroups;
        /* access modifiers changed from: private */
        public boolean mUnbindCalled;

        public SpellCheckerBindGroup(InternalServiceConnection connection) {
            this.mInternalConnection = connection;
            this.mListeners = new InternalDeathRecipients(this);
            this.mSpellCheckerBindGroups = connection.mSpellCheckerBindGroups;
        }

        public void onServiceConnectedLocked(ISpellCheckerService spellChecker) {
            if (!this.mUnbindCalled) {
                this.mSpellChecker = spellChecker;
                this.mConnected = true;
                try {
                    int size = this.mPendingSessionRequests.size();
                    for (int i = 0; i < size; i++) {
                        SessionRequest request = this.mPendingSessionRequests.get(i);
                        this.mSpellChecker.getISpellCheckerSession(request.mLocale, request.mScListener, request.mBundle, new ISpellCheckerServiceCallbackBinder(this, request));
                        this.mOnGoingSessionRequests.add(request);
                    }
                    this.mPendingSessionRequests.clear();
                } catch (RemoteException e) {
                    removeAllLocked();
                }
                cleanLocked();
            }
        }

        public void onServiceDisconnectedLocked() {
            this.mSpellChecker = null;
            this.mConnected = false;
        }

        public void removeListener(ISpellCheckerSessionListener listener) {
            synchronized (TextServicesManagerService.this.mLock) {
                this.mListeners.unregister(listener);
                Predicate<SessionRequest> removeCondition = new Predicate(listener.asBinder()) {
                    private final /* synthetic */ IBinder f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final boolean test(Object obj) {
                        return TextServicesManagerService.SpellCheckerBindGroup.lambda$removeListener$0(this.f$0, (TextServicesManagerService.SessionRequest) obj);
                    }
                };
                this.mPendingSessionRequests.removeIf(removeCondition);
                this.mOnGoingSessionRequests.removeIf(removeCondition);
                cleanLocked();
            }
        }

        static /* synthetic */ boolean lambda$removeListener$0(IBinder scListenerBinder, SessionRequest request) {
            return request.mScListener.asBinder() == scListenerBinder;
        }

        private void cleanLocked() {
            if (!this.mUnbindCalled && this.mListeners.getRegisteredCallbackCount() <= 0 && this.mPendingSessionRequests.isEmpty() && this.mOnGoingSessionRequests.isEmpty()) {
                String sciId = this.mInternalConnection.mSciId;
                if (this.mSpellCheckerBindGroups.get(sciId) == this) {
                    this.mSpellCheckerBindGroups.remove(sciId);
                }
                TextServicesManagerService.this.mContext.unbindService(this.mInternalConnection);
                this.mUnbindCalled = true;
            }
        }

        public void removeAllLocked() {
            Slog.e(this.TAG, "Remove the spell checker bind unexpectedly.");
            for (int i = this.mListeners.getRegisteredCallbackCount() - 1; i >= 0; i--) {
                InternalDeathRecipients internalDeathRecipients = this.mListeners;
                internalDeathRecipients.unregister(internalDeathRecipients.getRegisteredCallbackItem(i));
            }
            this.mPendingSessionRequests.clear();
            this.mOnGoingSessionRequests.clear();
            cleanLocked();
        }

        public void getISpellCheckerSessionOrQueueLocked(SessionRequest request) {
            if (!this.mUnbindCalled) {
                this.mListeners.register(request.mScListener);
                if (!this.mConnected) {
                    this.mPendingSessionRequests.add(request);
                    return;
                }
                try {
                    this.mSpellChecker.getISpellCheckerSession(request.mLocale, request.mScListener, request.mBundle, new ISpellCheckerServiceCallbackBinder(this, request));
                    this.mOnGoingSessionRequests.add(request);
                } catch (RemoteException e) {
                    removeAllLocked();
                }
                cleanLocked();
            }
        }

        /* access modifiers changed from: package-private */
        public void onSessionCreated(ISpellCheckerSession newSession, SessionRequest request) {
            synchronized (TextServicesManagerService.this.mLock) {
                if (!this.mUnbindCalled) {
                    if (this.mOnGoingSessionRequests.remove(request)) {
                        try {
                            request.mTsListener.onServiceConnected(newSession);
                        } catch (RemoteException e) {
                        }
                    }
                    cleanLocked();
                }
            }
        }
    }

    private final class InternalServiceConnection implements ServiceConnection {
        /* access modifiers changed from: private */
        public final String mSciId;
        /* access modifiers changed from: private */
        public final HashMap<String, SpellCheckerBindGroup> mSpellCheckerBindGroups;

        public InternalServiceConnection(String id, HashMap<String, SpellCheckerBindGroup> spellCheckerBindGroups) {
            this.mSciId = id;
            this.mSpellCheckerBindGroups = spellCheckerBindGroups;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (TextServicesManagerService.this.mLock) {
                onServiceConnectedInnerLocked(name, service);
            }
        }

        private void onServiceConnectedInnerLocked(ComponentName name, IBinder service) {
            ISpellCheckerService spellChecker = ISpellCheckerService.Stub.asInterface(service);
            SpellCheckerBindGroup group = this.mSpellCheckerBindGroups.get(this.mSciId);
            if (group != null && this == group.mInternalConnection) {
                group.onServiceConnectedLocked(spellChecker);
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            synchronized (TextServicesManagerService.this.mLock) {
                onServiceDisconnectedInnerLocked(name);
            }
        }

        private void onServiceDisconnectedInnerLocked(ComponentName name) {
            SpellCheckerBindGroup group = this.mSpellCheckerBindGroups.get(this.mSciId);
            if (group != null && this == group.mInternalConnection) {
                group.onServiceDisconnectedLocked();
            }
        }
    }

    private static final class InternalDeathRecipients extends RemoteCallbackList<ISpellCheckerSessionListener> {
        private final SpellCheckerBindGroup mGroup;

        public InternalDeathRecipients(SpellCheckerBindGroup group) {
            this.mGroup = group;
        }

        public void onCallbackDied(ISpellCheckerSessionListener listener) {
            this.mGroup.removeListener(listener);
        }
    }

    private static final class ISpellCheckerServiceCallbackBinder extends ISpellCheckerServiceCallback.Stub {
        @GuardedBy({"mCallbackLock"})
        private WeakReference<SpellCheckerBindGroup> mBindGroup;
        private final Object mCallbackLock = new Object();
        @GuardedBy({"mCallbackLock"})
        private WeakReference<SessionRequest> mRequest;

        ISpellCheckerServiceCallbackBinder(SpellCheckerBindGroup bindGroup, SessionRequest request) {
            synchronized (this.mCallbackLock) {
                this.mBindGroup = new WeakReference<>(bindGroup);
                this.mRequest = new WeakReference<>(request);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0022, code lost:
            if (r1 == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0024, code lost:
            if (r2 == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0026, code lost:
            r1.onSessionCreated(r5, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onSessionCreated(com.android.internal.textservice.ISpellCheckerSession r5) {
            /*
                r4 = this;
                java.lang.Object r0 = r4.mCallbackLock
                monitor-enter(r0)
                java.lang.ref.WeakReference<com.android.server.textservices.TextServicesManagerService$SpellCheckerBindGroup> r1 = r4.mBindGroup     // Catch:{ all -> 0x002c }
                if (r1 == 0) goto L_0x002a
                java.lang.ref.WeakReference<com.android.server.textservices.TextServicesManagerService$SessionRequest> r1 = r4.mRequest     // Catch:{ all -> 0x002c }
                if (r1 != 0) goto L_0x000c
                goto L_0x002a
            L_0x000c:
                java.lang.ref.WeakReference<com.android.server.textservices.TextServicesManagerService$SpellCheckerBindGroup> r1 = r4.mBindGroup     // Catch:{ all -> 0x002c }
                java.lang.Object r1 = r1.get()     // Catch:{ all -> 0x002c }
                com.android.server.textservices.TextServicesManagerService$SpellCheckerBindGroup r1 = (com.android.server.textservices.TextServicesManagerService.SpellCheckerBindGroup) r1     // Catch:{ all -> 0x002c }
                java.lang.ref.WeakReference<com.android.server.textservices.TextServicesManagerService$SessionRequest> r2 = r4.mRequest     // Catch:{ all -> 0x002c }
                java.lang.Object r2 = r2.get()     // Catch:{ all -> 0x002c }
                com.android.server.textservices.TextServicesManagerService$SessionRequest r2 = (com.android.server.textservices.TextServicesManagerService.SessionRequest) r2     // Catch:{ all -> 0x002c }
                r3 = 0
                r4.mBindGroup = r3     // Catch:{ all -> 0x002c }
                r4.mRequest = r3     // Catch:{ all -> 0x002c }
                monitor-exit(r0)     // Catch:{ all -> 0x002c }
                if (r1 == 0) goto L_0x0029
                if (r2 == 0) goto L_0x0029
                r1.onSessionCreated(r5, r2)
            L_0x0029:
                return
            L_0x002a:
                monitor-exit(r0)     // Catch:{ all -> 0x002c }
                return
            L_0x002c:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x002c }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.textservices.TextServicesManagerService.ISpellCheckerServiceCallbackBinder.onSessionCreated(com.android.internal.textservice.ISpellCheckerSession):void");
        }
    }
}

package miui.telephony;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import com.android.server.wifi.ScoringParams;
import com.miui.internal.telephony.SubscriptionManagerAndroidImpl;
import com.miui.internal.vip.utils.JsonParser;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;
import miui.reflect.Method;
import miui.telephony.phonenumber.Prefix;

public abstract class SubscriptionManager {
    public static final int DEFAULT_PHONE_ID = ConstantsDefiner.getDefaultPhoneIdConstant();
    public static final int DEFAULT_SLOT_ID = ConstantsDefiner.getDefaultSlotIdConstant();
    public static final int DEFAULT_SUBSCRIPTION_ID = ConstantsDefiner.getDefaultSubscriptionIdConstant();
    public static final int INVALID_PHONE_ID = ConstantsDefiner.getInvalidPhoneIdConstant();
    public static final int INVALID_SLOT_ID = ConstantsDefiner.getInvalidSlotIdConstant();
    public static final int INVALID_SUBSCRIPTION_ID = ConstantsDefiner.getInvalidSubscriptionIdConstant();
    protected static final String LOG_TAG = "SubMgr";
    public static final String PHONE_KEY = ConstantsDefiner.getPhoneKeyConstant();
    public static final int SLOT_ID_1 = 0;
    public static final int SLOT_ID_2 = 1;
    public static final String SLOT_KEY = ConstantsDefiner.getSlotKeyConstant();
    public static final String SUBSCRIPTION_KEY = ConstantsDefiner.getSubscriptionKeyConstant();
    private List<SubscriptionInfo> mInsertedSubscriptionInfos = null;
    private ArrayList<OnSubscriptionsChangedListener> mListeners = null;
    private Object mLock = new Object();
    private boolean mSubscriptionsCacheEnabled = false;

    public interface OnSubscriptionsChangedListener {
        void onSubscriptionsChanged();
    }

    /* access modifiers changed from: protected */
    public abstract void addOnSubscriptionsChangedListenerInternal();

    /* access modifiers changed from: protected */
    public abstract List<SubscriptionInfo> getAllSubscriptionInfoListInternal();

    public abstract int getDefaultDataSlotId();

    public abstract int getDefaultDataSubscriptionId();

    public abstract SubscriptionInfo getDefaultDataSubscriptionInfo();

    /* access modifiers changed from: protected */
    public abstract int getDefaultSlotIdInternal();

    public abstract int getDefaultSmsSubscriptionId();

    public abstract SubscriptionInfo getDefaultSmsSubscriptionInfo();

    public abstract int getDefaultVoiceSlotId();

    public abstract int getDefaultVoiceSubscriptionId();

    public abstract SubscriptionInfo getDefaultVoiceSubscriptionInfo();

    /* access modifiers changed from: protected */
    public abstract List<SubscriptionInfo> getSubscriptionInfoListInternal();

    /* access modifiers changed from: protected */
    public abstract void removeOnSubscriptionsChangedListenerInternal();

    public abstract void setDefaultDataSlotId(int i);

    public abstract void setDefaultSmsSubscriptionId(int i);

    public abstract void setDefaultVoiceSlotId(int i);

    private static class Holder {
        static final SubscriptionManager INSTANCE;

        private Holder() {
        }

        static {
            SubscriptionManager subscriptionManager;
            if (Build.IS_MIUI) {
                subscriptionManager = getMiuiSubscriptionManager();
            } else {
                subscriptionManager = SubscriptionManagerAndroidImpl.getDefault();
            }
            INSTANCE = subscriptionManager;
        }

        private static SubscriptionManager getMiuiSubscriptionManager() {
            try {
                Class cls = Class.forName("miui.telephony.SubscriptionManagerEx");
                return (SubscriptionManager) Method.of(cls, "getDefault", cls, new Class[0]).invokeObject(cls, (Object) null, new Object[0]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    static class ConstantsDefiner {
        private static final String PHONE_ID = "phone_id";
        private static final String SLOT_ID = "slot_id";
        private static final String SUBSCRIPTION_ID = "subscription_id";

        private ConstantsDefiner() {
        }

        static int getInvalidSubscriptionIdConstant() {
            return -1;
        }

        static int getInvalidPhoneIdConstant() {
            return -1;
        }

        static int getInvalidSlotIdConstant() {
            return -1;
        }

        static int getDefaultSubscriptionIdConstant() {
            return ScoringParams.Values.MAX_EXPID;
        }

        static int getDefaultPhoneIdConstant() {
            return ScoringParams.Values.MAX_EXPID;
        }

        static int getDefaultSlotIdConstant() {
            return ScoringParams.Values.MAX_EXPID;
        }

        static String getSubscriptionKeyConstant() {
            return "subscription_id";
        }

        static String getPhoneKeyConstant() {
            return PHONE_ID;
        }

        static String getSlotKeyConstant() {
            return "slot_id";
        }
    }

    public static SubscriptionManager getDefault() {
        return Holder.INSTANCE;
    }

    public void addOnSubscriptionsChangedListener(OnSubscriptionsChangedListener listener) {
        if (PhoneDebug.VDBG) {
            Rlog.i(LOG_TAG, "addOnSubscriptionsChangedListener listener=" + listener.getClass().getName());
        }
        synchronized (this.mLock) {
            if (this.mListeners == null) {
                this.mListeners = new ArrayList<>();
            }
            if (!this.mListeners.contains(listener)) {
                this.mListeners.add(listener);
                addOnSubscriptionsChangedListenerInternal();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0021, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeOnSubscriptionsChangedListener(miui.telephony.SubscriptionManager.OnSubscriptionsChangedListener r3) {
        /*
            r2 = this;
            java.lang.Object r0 = r2.mLock
            monitor-enter(r0)
            java.util.ArrayList<miui.telephony.SubscriptionManager$OnSubscriptionsChangedListener> r1 = r2.mListeners     // Catch:{ all -> 0x0022 }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return
        L_0x0009:
            java.util.ArrayList<miui.telephony.SubscriptionManager$OnSubscriptionsChangedListener> r1 = r2.mListeners     // Catch:{ all -> 0x0022 }
            r1.remove(r3)     // Catch:{ all -> 0x0022 }
            java.util.ArrayList<miui.telephony.SubscriptionManager$OnSubscriptionsChangedListener> r1 = r2.mListeners     // Catch:{ all -> 0x0022 }
            int r1 = r1.size()     // Catch:{ all -> 0x0022 }
            if (r1 != 0) goto L_0x0020
            r1 = 0
            r2.mListeners = r1     // Catch:{ all -> 0x0022 }
            boolean r1 = r2.mSubscriptionsCacheEnabled     // Catch:{ all -> 0x0022 }
            if (r1 != 0) goto L_0x0020
            r2.removeOnSubscriptionsChangedListenerInternal()     // Catch:{ all -> 0x0022 }
        L_0x0020:
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return
        L_0x0022:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.telephony.SubscriptionManager.removeOnSubscriptionsChangedListener(miui.telephony.SubscriptionManager$OnSubscriptionsChangedListener):void");
    }

    public void enableSubscriptionsCache() {
        synchronized (this.mLock) {
            this.mSubscriptionsCacheEnabled = true;
            addOnSubscriptionsChangedListenerInternal();
        }
    }

    public void disableSubscriptionsCache() {
        synchronized (this.mLock) {
            this.mSubscriptionsCacheEnabled = false;
            this.mInsertedSubscriptionInfos = null;
            if (this.mListeners == null || this.mListeners.size() == 0) {
                removeOnSubscriptionsChangedListenerInternal();
            }
        }
    }

    /* access modifiers changed from: private */
    public void ensureSubscriptionInfoCache(boolean forceUpdate) {
        boolean update = false;
        if (forceUpdate || this.mInsertedSubscriptionInfos == null) {
            this.mInsertedSubscriptionInfos = getSubscriptionInfoListInternal();
            if (this.mInsertedSubscriptionInfos == null) {
                this.mInsertedSubscriptionInfos = new ArrayList();
            }
            update = true;
        }
        if (update && PhoneDebug.VDBG) {
            StringBuilder sb = new StringBuilder();
            sb.append("ensureSubscriptionInfoCache ");
            sb.append(forceUpdate ? Prefix.EMPTY : "false");
            sb.append(" insert=");
            sb.append(Arrays.toString(this.mInsertedSubscriptionInfos.toArray()));
            Rlog.i(LOG_TAG, sb.toString());
        }
    }

    /* access modifiers changed from: protected */
    public void onSubscriptionInfoChanged() {
        if (!this.mSubscriptionsCacheEnabled) {
            notifyOnSubscriptionsChangedListeners();
            return;
        }
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... params) {
                SubscriptionManager.this.ensureSubscriptionInfoCache(true);
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void result) {
                SubscriptionManager.this.notifyOnSubscriptionsChangedListeners();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null});
    }

    /* access modifiers changed from: private */
    public void notifyOnSubscriptionsChangedListeners() {
        synchronized (this.mLock) {
            if (this.mListeners != null) {
                if (PhoneDebug.VDBG) {
                    Rlog.i(LOG_TAG, "notify OnSubscriptionsChangedListener size=" + this.mListeners.size());
                }
                Iterator<OnSubscriptionsChangedListener> it = this.mListeners.iterator();
                while (it.hasNext()) {
                    it.next().onSubscriptionsChanged();
                }
            }
        }
    }

    public SubscriptionInfo getSubscriptionInfoForSubscription(int subId) {
        if (!isValidSubscriptionId(subId)) {
            return null;
        }
        if (subId == DEFAULT_SUBSCRIPTION_ID) {
            return getSubscriptionInfoForSlot(getDefaultSlotId());
        }
        for (SubscriptionInfo subscriptionInfo : getSubscriptionInfoList()) {
            if (subscriptionInfo.getSubscriptionId() == subId) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    public SubscriptionInfo getSubscriptionInfoForSlot(int slotId) {
        if (!isValidSlotId(slotId)) {
            return null;
        }
        if (slotId == DEFAULT_SLOT_ID) {
            slotId = getDefaultSlotId();
        }
        for (SubscriptionInfo subscriptionInfo : getSubscriptionInfoList()) {
            if (subscriptionInfo.getSlotId() == slotId) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    public List<SubscriptionInfo> getAllSubscriptionInfoList() {
        return getAllSubscriptionInfoListInternal();
    }

    public List<SubscriptionInfo> getSubscriptionInfoList() {
        if (this.mSubscriptionsCacheEnabled) {
            ensureSubscriptionInfoCache(false);
            return this.mInsertedSubscriptionInfos;
        }
        List<SubscriptionInfo> infos = getSubscriptionInfoListInternal();
        if (infos == null) {
            return new ArrayList<>();
        }
        return infos;
    }

    public List<SubscriptionInfo> getActiveSubscriptionInfoList() {
        List<SubscriptionInfo> activeSubscriptionInfoList = new ArrayList<>();
        for (SubscriptionInfo subscriptionInfo : getSubscriptionInfoList()) {
            if (subscriptionInfo.isActivated()) {
                activeSubscriptionInfoList.add(subscriptionInfo);
            }
        }
        return activeSubscriptionInfoList;
    }

    public int getAllSubscriptionInfoCount() {
        long identity = Binder.clearCallingIdentity();
        try {
            return getAllSubscriptionInfoList().size();
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public int getSubscriptionInfoCount() {
        int activeCount = 0;
        long identity = Binder.clearCallingIdentity();
        try {
            for (SubscriptionInfo subscriptionInfo : getSubscriptionInfoList()) {
                if (subscriptionInfo.isActivated()) {
                    activeCount++;
                }
            }
            return activeCount;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public int getSlotIdForSubscription(int subId) {
        if (!isValidSubscriptionId(subId)) {
            return INVALID_SLOT_ID;
        }
        int slotId = subId == DEFAULT_SUBSCRIPTION_ID ? DEFAULT_SLOT_ID : getSlotId(subId);
        return !isValidSlotId(slotId) ? INVALID_SLOT_ID : slotId;
    }

    public int getPhoneIdForSubscription(int subId) {
        if (!isValidSubscriptionId(subId)) {
            return INVALID_PHONE_ID;
        }
        int phoneId = subId == DEFAULT_SUBSCRIPTION_ID ? DEFAULT_PHONE_ID : getSlotId(subId);
        return !isValidPhoneId(phoneId) ? INVALID_PHONE_ID : phoneId;
    }

    /* access modifiers changed from: protected */
    public int getSlotId(int subId) {
        long identity = Binder.clearCallingIdentity();
        try {
            for (SubscriptionInfo subscriptionInfo : getSubscriptionInfoList()) {
                if (subscriptionInfo.getSubscriptionId() == subId) {
                    return subscriptionInfo.getSlotId();
                }
            }
            Binder.restoreCallingIdentity(identity);
            return INVALID_PHONE_ID;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public int getPhoneIdForSlot(int slotId) {
        return slotId;
    }

    public int getSlotIdForPhone(int phoneId) {
        return phoneId;
    }

    public int getSubscriptionIdForSlot(int slotId) {
        if (!isValidSlotId(slotId)) {
            return INVALID_SUBSCRIPTION_ID;
        }
        if (slotId == DEFAULT_SLOT_ID) {
            return DEFAULT_SUBSCRIPTION_ID;
        }
        long identity = Binder.clearCallingIdentity();
        try {
            for (SubscriptionInfo subscriptionInfo : getSubscriptionInfoList()) {
                if (subscriptionInfo.getSlotId() == slotId) {
                    return subscriptionInfo.getSubscriptionId();
                }
            }
            Binder.restoreCallingIdentity(identity);
            return INVALID_SUBSCRIPTION_ID;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public int getDefaultSubscriptionId() {
        int subId;
        int i = INVALID_SUBSCRIPTION_ID;
        if (TelephonyManager.getDefault().isVoiceCapable()) {
            subId = getDefaultVoiceSubscriptionId();
        } else {
            subId = getDefaultDataSubscriptionId();
        }
        if (!isValidSubscriptionId(subId) || subId == DEFAULT_SUBSCRIPTION_ID) {
            return getSubscriptionIdForSlot(getDefaultSlotIdInternal());
        }
        return subId;
    }

    public SubscriptionInfo getDefaultSubscriptionInfo() {
        return getSubscriptionInfoForSubscription(getDefaultSubscriptionId());
    }

    public int getDefaultSlotId() {
        int subId;
        int i = INVALID_SUBSCRIPTION_ID;
        if (TelephonyManager.getDefault().isVoiceCapable()) {
            subId = getDefaultVoiceSubscriptionId();
        } else {
            subId = getDefaultDataSubscriptionId();
        }
        int slotId = INVALID_SLOT_ID;
        if (isValidSubscriptionId(subId)) {
            slotId = getSlotIdForSubscription(subId);
        }
        if (!isValidSlotId(slotId) || slotId == DEFAULT_SLOT_ID) {
            return getDefaultSlotIdInternal();
        }
        return slotId;
    }

    public void setDefaultVoiceSubscriptionId(int subId) {
        setDefaultVoiceSlotId(getSlotIdForSubscription(subId));
    }

    public void setDefaultDataSubscriptionId(int subId) {
        setDefaultDataSlotId(getSlotIdForSubscription(subId));
    }

    public int getDefaultSmsSlotId() {
        return getSlotIdForSubscription(getDefaultSmsSubscriptionId());
    }

    public void setDefaultSmsSlotId(int slotId) {
        if (!isValidSlotId(slotId)) {
            slotId = INVALID_SLOT_ID;
        }
        if (slotId != DEFAULT_SLOT_ID && slotId != getDefaultSmsSlotId()) {
            setDefaultSmsSubscriptionId(getSubscriptionIdForSlot(slotId));
        }
    }

    public static boolean isValidSubscriptionId(int subId) {
        return subId > INVALID_SUBSCRIPTION_ID;
    }

    public static boolean isValidSlotId(int slotId) {
        return (slotId >= 0 && slotId < TelephonyManager.getDefault().getPhoneCount()) || slotId == DEFAULT_SLOT_ID;
    }

    public static boolean isRealSlotId(int slotId) {
        return slotId >= 0 && slotId < TelephonyManager.getDefault().getPhoneCount();
    }

    public static boolean isValidPhoneId(int phoneId) {
        return (phoneId >= 0 && phoneId < TelephonyManager.getDefault().getPhoneCount()) || phoneId == DEFAULT_PHONE_ID;
    }

    public static void putSlotIdExtra(Intent intent, int slotId) {
        putSlotIdPhoneIdAndSubIdExtra(intent, slotId, getDefault().getPhoneIdForSlot(slotId), getDefault().getSubscriptionIdForSlot(slotId));
    }

    public static void putPhoneIdExtra(Intent intent, int phoneId) {
        int slotId = getDefault().getSlotIdForPhone(phoneId);
        putSlotIdPhoneIdAndSubIdExtra(intent, slotId, phoneId, getDefault().getSubscriptionIdForSlot(slotId));
    }

    public static void putSubscriptionIdExtra(Intent intent, int subId) {
        putSlotIdPhoneIdAndSubIdExtra(intent, getDefault().getSlotIdForSubscription(subId), getDefault().getPhoneIdForSubscription(subId), subId);
    }

    public static void putSlotIdPhoneIdAndSubIdExtra(Intent intent, int slotId, int phoneId, int subId) {
        intent.putExtra(SUBSCRIPTION_KEY, subId);
        intent.putExtra(PHONE_KEY, phoneId);
        intent.putExtra(SLOT_KEY, slotId);
    }

    public static void putSlotId(Bundle bundle, int slotId) {
        putSlotIdPhoneIdAndSubId(bundle, slotId, getDefault().getPhoneIdForSlot(slotId), getDefault().getSubscriptionIdForSlot(slotId));
    }

    public static void putPhoneId(Bundle bundle, int phoneId) {
        int slotId = getDefault().getSlotIdForPhone(phoneId);
        putSlotIdPhoneIdAndSubId(bundle, slotId, phoneId, getDefault().getSubscriptionIdForSlot(slotId));
    }

    public static void putSubscriptionId(Bundle bundle, int subId) {
        putSlotIdPhoneIdAndSubId(bundle, getDefault().getSlotIdForSubscription(subId), getDefault().getPhoneIdForSubscription(subId), subId);
    }

    public static void putSlotIdPhoneIdAndSubId(Bundle bundle, int slotId, int phoneId, int subId) {
        bundle.putInt(SUBSCRIPTION_KEY, subId);
        bundle.putInt(PHONE_KEY, phoneId);
        bundle.putInt(SLOT_KEY, slotId);
    }

    public static int getSlotIdExtra(Intent intent, int defaultValue) {
        return intent.getIntExtra(SLOT_KEY, defaultValue);
    }

    public static int getSubscriptionIdExtra(Intent intent, int defaultValue) {
        return intent.getIntExtra(SUBSCRIPTION_KEY, defaultValue);
    }

    public static int getPhoneIdExtra(Intent intent, int defaultValue) {
        return intent.getIntExtra(PHONE_KEY, defaultValue);
    }

    public static int getSlotId(Bundle bundle, int defaultValue) {
        return bundle.getInt(SLOT_KEY, defaultValue);
    }

    public static int getSubscriptionId(Bundle bundle, int defaultValue) {
        return bundle.getInt(SUBSCRIPTION_KEY, defaultValue);
    }

    public static int getPhoneId(Bundle bundle, int defaultValue) {
        return bundle.getInt(PHONE_KEY, defaultValue);
    }

    public static String toSimpleString(List<SubscriptionInfo> infos) {
        int size = infos == null ? 0 : infos.size();
        if (size <= 0) {
            return JsonParser.EMPTY_ARRAY;
        }
        SubscriptionInfo[] subs = new SubscriptionInfo[size];
        infos.toArray(subs);
        StringBuilder sb = new StringBuilder(size * 64);
        sb.append("[ size=");
        StringBuilder sb2 = sb.append(size);
        for (SubscriptionInfo si : subs) {
            if (si == null) {
                Rlog.i(LOG_TAG, "toSimpleString SubscriptionInfo size was changed");
            } else {
                sb2.append(" {id=");
                sb2.append(si.getSubscriptionId());
                sb2.append(" iccid=");
                sb2.append(PhoneDebug.VDBG ? si.getIccId() : TelephonyUtils.pii(si.getIccId()));
                sb2.append(" slot=");
                sb2.append(si.getSlotId());
                sb2.append(" active=");
                sb2.append(si.isActivated());
                sb2.append('}');
            }
        }
        sb2.append(']');
        return sb2.toString();
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("SubscriptionManager:");
        try {
            StringBuilder sb = new StringBuilder(512);
            sb.append("mListeners=");
            sb.append('[');
            if (this.mListeners != null) {
                Iterator<OnSubscriptionsChangedListener> it = this.mListeners.iterator();
                while (it.hasNext()) {
                    sb.append('{');
                    sb.append(it.next().getClass().getName());
                    sb.append('}');
                }
            }
            sb.append(']');
            pw.println(sb.toString());
            pw.println("mInsertedSubscriptionInfos=" + this.mInsertedSubscriptionInfos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pw.flush();
    }
}

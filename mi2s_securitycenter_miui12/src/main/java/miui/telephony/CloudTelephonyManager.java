package miui.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import miui.cloud.telephony.SubscriptionManager;
import miui.cloud.telephony.TelephonyManager;
import miui.cloud.util.AnonymousDeviceIdUtil;
import miui.cloud.util.SysHelper;
import miui.net.ConnectivityHelper;
import miui.telephony.exception.IllegalDeviceException;

public class CloudTelephonyManager {
    public static final String SLOT_ID = SubscriptionManager.getSLOT_KEY();
    private static final String TAG = "CloudTelephonyManager";
    private static volatile String sDeviceIdCache;
    private static volatile DeviceIdConfiguration sDeviceIdConfiguration;
    static volatile DeviceIdConfiguration sDeviceIdConfigurationTestInjection;

    private static class AsyncFuture<V> extends FutureTask<V> {
        public AsyncFuture() {
            super(new Callable<V>() {
                public V call() {
                    throw new IllegalStateException("this should never be called");
                }
            });
        }

        public void setResult(V v) {
            set(v);
        }
    }

    interface DeviceIdConfiguration {
        boolean checkValid(Context context, String str);

        long getBusywaitRetryIntervalMillisRecommandation(Context context);

        long getBusywaitTimeoutMillisRecommandation(Context context);

        String tryGetId(Context context);
    }

    static final class TypedSimId {
        private static final String SP = ",";
        public static final int TYPE_ICCID = 1;
        public static final int TYPE_IMSI = 2;
        public static final int TYPE_UNKNOWN = 0;
        public final int type;
        public final String value;

        TypedSimId(int i, String str) {
            this.type = i;
            this.value = str;
        }

        static TypedSimId parse(String str) {
            String[] split = str.split(SP);
            return (split.length != 2 || !TextUtils.isDigitsOnly(split[0])) ? new TypedSimId(0, str) : new TypedSimId(Integer.parseInt(split[0]), split[1]);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || TypedSimId.class != obj.getClass()) {
                return false;
            }
            TypedSimId typedSimId = (TypedSimId) obj;
            if (this.type != typedSimId.type) {
                return false;
            }
            String str = this.value;
            return str == null ? typedSimId.value == null : str.equals(typedSimId.value);
        }

        public int hashCode() {
            int i = this.type * 31;
            String str = this.value;
            return i + (str != null ? str.hashCode() : 0);
        }

        public String toPlain() {
            return this.type + SP + this.value;
        }

        public String toString() {
            return toPlain();
        }
    }

    public static String blockingGetDeviceId(Context context) {
        return blockingGetDeviceId(context, getDeviceIdConfiguration(context).getBusywaitTimeoutMillisRecommandation(context));
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x005e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String blockingGetDeviceId(final android.content.Context r6, long r7) {
        /*
            ensureNotOnMainThread(r6)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "blockingGetDeviceId is called by "
            r0.append(r1)
            java.lang.String r1 = r6.getPackageName()
            r0.append(r1)
            java.lang.String r1 = " with timeout: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "CloudTelephonyManager"
            android.util.Log.i(r1, r0)
            java.lang.String r0 = sDeviceIdCache
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x002e
            return r0
        L_0x002e:
            miui.telephony.CloudTelephonyManager$DeviceIdConfiguration r0 = getDeviceIdConfiguration(r6)
            r2 = 0
            int r4 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r4 >= 0) goto L_0x0039
            r7 = r2
        L_0x0039:
            long r2 = r0.getBusywaitRetryIntervalMillisRecommandation(r6)
            r4 = 0
            miui.telephony.CloudTelephonyManager$1 r5 = new miui.telephony.CloudTelephonyManager$1     // Catch:{ InterruptedException -> 0x004e, TimeoutException -> 0x004a }
            r5.<init>(r6)     // Catch:{ InterruptedException -> 0x004e, TimeoutException -> 0x004a }
            java.lang.Object r7 = c.a.b.a.a.a(r5, r7, r2)     // Catch:{ InterruptedException -> 0x004e, TimeoutException -> 0x004a }
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ InterruptedException -> 0x004e, TimeoutException -> 0x004a }
            goto L_0x0055
        L_0x004a:
            r7 = move-exception
            java.lang.String r8 = "blockingGetDeviceId, busy-wait timeout"
            goto L_0x0051
        L_0x004e:
            r7 = move-exception
            java.lang.String r8 = "blockingGetDeviceId, InterruptedException while busy-waiting"
        L_0x0051:
            android.util.Log.e(r1, r8, r7)
            r7 = r4
        L_0x0055:
            boolean r8 = r0.checkValid(r6, r7)
            if (r8 == 0) goto L_0x005e
            sDeviceIdCache = r7
            return r7
        L_0x005e:
            miui.cloud.util.SysHelper.showInvalidDeviceIdWarning(r6, r7)
            miui.telephony.exception.IllegalDeviceException r6 = new miui.telephony.exception.IllegalDeviceException
            java.lang.String r7 = "can't get a valid device id"
            r6.<init>(r7)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.telephony.CloudTelephonyManager.blockingGetDeviceId(android.content.Context, long):java.lang.String");
    }

    public static String blockingGetSimId(Context context, int i) {
        return blockingGetTypedSimId(context, i).toPlain();
    }

    private static TypedSimId blockingGetTypedSimId(Context context, int i) {
        try {
            return blockingGetTypedSimId(context, i, -1);
        } catch (TimeoutException unused) {
            throw new IllegalStateException("Never reach here. ");
        }
    }

    private static TypedSimId blockingGetTypedSimId(Context context, int i, long j) {
        TypedSimId waitAndGetSimId = waitAndGetSimId(context, i, j);
        if (waitAndGetSimId != null) {
            return waitAndGetSimId;
        }
        throw new IllegalDeviceException("failed to get sim id");
    }

    private static void ensureNotOnMainThread(Context context) {
        Looper myLooper = Looper.myLooper();
        if (myLooper != null && myLooper == context.getMainLooper()) {
            throw new IllegalStateException("calling this from your main thread can lead to deadlock");
        }
    }

    public static int getAvailableSimCount() {
        return TelephonyManager.getDefault().getIccCardCount();
    }

    public static int getDefaultSlotId() {
        return SubscriptionManager.getDefault().getDefaultSlotId();
    }

    private static DeviceIdConfiguration getDeviceIdConfiguration(Context context) {
        DeviceIdConfiguration deviceIdConfiguration = sDeviceIdConfigurationTestInjection;
        if (deviceIdConfiguration != null) {
            return deviceIdConfiguration;
        }
        DeviceIdConfiguration deviceIdConfiguration2 = sDeviceIdConfiguration;
        if (deviceIdConfiguration2 != null) {
            return deviceIdConfiguration2;
        }
        synchronized (DeviceIdConfiguration.class) {
            DeviceIdConfiguration deviceIdConfiguration3 = sDeviceIdConfiguration;
            if (deviceIdConfiguration3 != null) {
                return deviceIdConfiguration3;
            }
            if (AnonymousDeviceIdUtil.isEnforced(context)) {
                AnonymousClass2 r2 = new DeviceIdConfiguration() {
                    public boolean checkValid(Context context, String str) {
                        return !TextUtils.isEmpty(str);
                    }

                    public long getBusywaitRetryIntervalMillisRecommandation(Context context) {
                        return 30000;
                    }

                    public long getBusywaitTimeoutMillisRecommandation(Context context) {
                        return 60000;
                    }

                    public String tryGetId(Context context) {
                        String oaid = AnonymousDeviceIdUtil.getOAID(context);
                        if (!TextUtils.isEmpty(oaid)) {
                            return oaid;
                        }
                        Bundle call = context.getContentResolver().call(Uri.parse("content://com.xiaomi.cloud.cloudidprovider"), "getCloudId", (String) null, (Bundle) null);
                        return call != null ? call.getString("result_id") : AnonymousDeviceIdUtil.getAndroidId(context);
                    }
                };
                sDeviceIdConfiguration = r2;
                return r2;
            } else if (hasTelephonyFeature(context)) {
                AnonymousClass3 r22 = new DeviceIdConfiguration() {
                    public boolean checkValid(Context context, String str) {
                        return SysHelper.validateIMEI(str);
                    }

                    public long getBusywaitRetryIntervalMillisRecommandation(Context context) {
                        return DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                    }

                    public long getBusywaitTimeoutMillisRecommandation(Context context) {
                        return 300000;
                    }

                    public String tryGetId(Context context) {
                        return TelephonyManager.getDefault().getMiuiDeviceId();
                    }
                };
                sDeviceIdConfiguration = r22;
                return r22;
            } else {
                AnonymousClass4 r23 = new DeviceIdConfiguration() {
                    public boolean checkValid(Context context, String str) {
                        return SysHelper.validateMAC(str);
                    }

                    public long getBusywaitRetryIntervalMillisRecommandation(Context context) {
                        return 10000;
                    }

                    public long getBusywaitTimeoutMillisRecommandation(Context context) {
                        return 60000;
                    }

                    public String tryGetId(Context context) {
                        return ConnectivityHelper.getInstance().getMacAddress();
                    }
                };
                sDeviceIdConfiguration = r23;
                return r23;
            }
        }
    }

    public static String getDeviceIdQuietly(Context context) {
        return getDeviceIdConfiguration(context).tryGetId(context);
    }

    public static String getLine1Number(Context context, int i) {
        return TelephonyManager.getDefault().getLine1NumberForSlot(i);
    }

    public static int getMultiSimCount() {
        return TelephonyManager.getDefault().getPhoneCount();
    }

    public static String getSimId(Context context, int i) {
        TypedSimId simIdByPhoneType = getSimIdByPhoneType(TelephonyManager.getDefault(), i);
        if (simIdByPhoneType != null) {
            return simIdByPhoneType.toPlain();
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static TypedSimId getSimIdByPhoneType(TelephonyManager telephonyManager, int i) {
        int phoneTypeForSlot = telephonyManager.getPhoneTypeForSlot(i);
        Log.v(TAG, "phone type: " + phoneTypeForSlot);
        if (phoneTypeForSlot == TelephonyManager.getPHONE_TYPE_CDMA()) {
            String simSerialNumberForSlot = telephonyManager.getSimSerialNumberForSlot(i);
            if (!TextUtils.isEmpty(simSerialNumberForSlot)) {
                return new TypedSimId(1, simSerialNumberForSlot);
            }
            return null;
        } else if (phoneTypeForSlot != TelephonyManager.getPHONE_TYPE_GSM()) {
            return null;
        } else {
            String subscriberIdForSlot = telephonyManager.getSubscriberIdForSlot(i);
            if (!TextUtils.isEmpty(subscriberIdForSlot)) {
                return new TypedSimId(2, subscriberIdForSlot);
            }
            return null;
        }
    }

    private static TypedSimId getSimIdByPhoneTypeForSubId(TelephonyManager telephonyManager, int i) {
        int phoneTypeForSubscription = telephonyManager.getPhoneTypeForSubscription(i);
        Log.v(TAG, "device type: " + phoneTypeForSubscription);
        if (phoneTypeForSubscription == TelephonyManager.getPHONE_TYPE_CDMA()) {
            String simSerialNumberForSubscription = telephonyManager.getSimSerialNumberForSubscription(i);
            if (!TextUtils.isEmpty(simSerialNumberForSubscription)) {
                return new TypedSimId(1, simSerialNumberForSubscription);
            }
            return null;
        } else if (phoneTypeForSubscription != TelephonyManager.getPHONE_TYPE_GSM()) {
            return null;
        } else {
            String subscriberIdForSubscription = telephonyManager.getSubscriberIdForSubscription(i);
            if (!TextUtils.isEmpty(subscriberIdForSubscription)) {
                return new TypedSimId(2, subscriberIdForSubscription);
            }
            return null;
        }
    }

    public static long getSimIdBySlotId(Context context, int i) {
        return (long) SubscriptionManager.getDefault().getSubscriptionIdForSlot(i);
    }

    public static String getSimIdForSubId(Context context, int i) {
        TypedSimId simIdByPhoneTypeForSubId = getSimIdByPhoneTypeForSubId(TelephonyManager.getDefault(), i);
        if (simIdByPhoneTypeForSubId != null) {
            return simIdByPhoneTypeForSubId.toPlain();
        }
        return null;
    }

    public static String getSimOperator(Context context, int i) {
        return TelephonyManager.getDefault().getSimOperatorForSlot(i);
    }

    public static String getSimOperatorName(Context context, int i) {
        return TelephonyManager.getDefault().getSimOperatorNameForSlot(i);
    }

    public static int getSlotIdBySimId(Context context, long j) {
        return SubscriptionManager.getDefault().getSlotIdForSubscription((int) j);
    }

    private static boolean hasTelephonyFeature(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    public static boolean isMultiSimSupported() {
        return TelephonyManager.getDefault().isMultiSimEnabled();
    }

    public static boolean isSimInserted(Context context, int i) {
        return TelephonyManager.getDefault().hasIccCard(i);
    }

    private static TypedSimId waitAndGetSimId(Context context, final int i, long j) {
        ensureNotOnMainThread(context);
        if (!hasTelephonyFeature(context)) {
            return null;
        }
        final TelephonyManager telephonyManager = TelephonyManager.getDefault();
        final AsyncFuture asyncFuture = new AsyncFuture();
        AnonymousClass5 r3 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (TelephonyManager.getIccCardConstants_INTENT_VALUE_ICC_IMSI().equals(intent.getStringExtra(TelephonyManager.getIccCardConstants_INTENT_KEY_ICC_STATE()))) {
                    AsyncFuture.this.setResult(CloudTelephonyManager.getSimIdByPhoneType(telephonyManager, i));
                }
            }
        };
        context.registerReceiver(r3, new IntentFilter(TelephonyManager.getTelephonyIntents_ACTION_SIM_STATE_CHANGED()));
        TypedSimId simIdByPhoneType = getSimIdByPhoneType(telephonyManager, i);
        if (simIdByPhoneType != null) {
            asyncFuture.setResult(simIdByPhoneType);
        }
        if (j < 0) {
            try {
                return (TypedSimId) asyncFuture.get();
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
                return null;
            } catch (Exception e) {
                if (!(e instanceof TimeoutException)) {
                    Log.e(TAG, "exception when get sim id", e);
                    return null;
                }
                throw ((TimeoutException) e);
            } finally {
                context.unregisterReceiver(r3);
            }
        } else {
            TypedSimId typedSimId = (TypedSimId) asyncFuture.get(j, TimeUnit.MILLISECONDS);
            context.unregisterReceiver(r3);
            return typedSimId;
        }
    }
}

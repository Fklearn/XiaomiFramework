package miui.cloud.common;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import b.d.b.c.a;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import miui.telephony.CloudTelephonyManager;
import miui.telephony.exception.IllegalDeviceException;

public class XDeviceInfo {
    private static final long EMPTY_DEVICE_ID_CACHE_TIME_MILLIS = 180000;
    private static final int QUERTY_TIME_OUT = 60000;
    private static XDeviceInfo sInstance = null;
    private static KeyStoreType sKeyStoreType = null;
    private static long sLastEmptyDeviceIdTime = -1;
    public final String IMEI;
    public final String MAC;
    public final String SN;
    public final String deviceId;
    public final KeyStoreType keyStoreType;
    public final String model;
    public final PhoneType type;

    public interface DeviceInfoReayListener {
        void onDeviceInfoReay(XDeviceInfo xDeviceInfo);
    }

    public enum KeyStoreType {
        TZ("TZ"),
        NONE("NONE");
        
        private String mDesc;

        private KeyStoreType(String str) {
            this.mDesc = str;
        }

        public String getDesc() {
            return this.mDesc;
        }
    }

    public enum PhoneType {
        PAD("pad"),
        PHONE("phone");
        
        private String mDesc;

        private PhoneType(String str) {
            this.mDesc = str;
        }

        public String getDesc() {
            return this.mDesc;
        }
    }

    static {
        HashSet hashSet = new HashSet();
        hashSet.add("leo");
        hashSet.add("andromeda");
        hashSet.add("begonia");
        hashSet.add("davinciin");
        hashSet.add("raphaelin");
        hashSet.add("begoniain");
        hashSet.add("hennessy");
        hashSet.add("olivelite");
        hashSet.add("olivewood");
        hashSet.add("libra");
        hashSet.add("aqua");
        hashSet.add("gemini");
        hashSet.add("gold");
        hashSet.add("vela");
        hashSet.add("kenzo");
        hashSet.add("grus");
        hashSet.add("tucana");
        hashSet.add("ido");
        hashSet.add("hydrogen");
        hashSet.add("helium");
        hashSet.add("kate");
        hashSet.add("land");
        hashSet.add("lavender");
        hashSet.add("markw");
        hashSet.add("nikel");
        hashSet.add("omega");
        hashSet.add("cepheus");
        hashSet.add("capricorn");
        hashSet.add("laurus");
        hashSet.add("prada");
        hashSet.add("lithium");
        hashSet.add("scorpio");
        hashSet.add("natrium");
        hashSet.add("rolex");
        hashSet.add("mido");
        hashSet.add("santoni");
        hashSet.add("ginkgo");
        hashSet.add("sagit");
        hashSet.add("centaur");
        hashSet.add("oxygen");
        hashSet.add("tiffany");
        hashSet.add("ulysse");
        hashSet.add("ugglite");
        hashSet.add("chiron");
        hashSet.add("ugg");
        hashSet.add("jason");
        hashSet.add("riva");
        hashSet.add("crux");
        hashSet.add("vince");
        hashSet.add("rosy");
        hashSet.add("meri");
        hashSet.add("davinci");
        hashSet.add("pine");
        hashSet.add("whyred");
        hashSet.add("dipper");
        hashSet.add("onc");
        hashSet.add("polaris");
        hashSet.add("pyxis");
        hashSet.add("ysl");
        hashSet.add("wayne");
        hashSet.add("nitrogen");
        hashSet.add("sirius");
        hashSet.add("sakura");
        hashSet.add("sakura_india");
        hashSet.add("beryllium");
        hashSet.add("violet");
        hashSet.add("raphael");
        hashSet.add("cactus");
        hashSet.add("cereus");
        hashSet.add("lotus");
        hashSet.add("willow");
        hashSet.add("clover");
        hashSet.add("ursa");
        hashSet.add("olive");
        hashSet.add("tulip");
        hashSet.add("draco");
        hashSet.add("platina");
        hashSet.add("perseus");
        hashSet.add("equuleus");
        sKeyStoreType = hashSet.contains(Build.DEVICE.toLowerCase()) ? KeyStoreType.TZ : KeyStoreType.NONE;
    }

    private XDeviceInfo(Context context) {
        this.type = CloudTelephonyManager.getMultiSimCount() == 0 ? PhoneType.PAD : PhoneType.PHONE;
        String blockingGetNakedDeviceId = blockingGetNakedDeviceId(context);
        this.deviceId = TextUtils.isEmpty(blockingGetNakedDeviceId) ? blockingGetNakedDeviceId : a.a(blockingGetNakedDeviceId);
        this.IMEI = this.type != PhoneType.PHONE ? "" : blockingGetNakedDeviceId;
        this.SN = Build.SERIAL;
        this.MAC = getMAC(context);
        this.model = Build.MODEL;
        this.keyStoreType = getKeyStoreTypeUnblocking();
    }

    private XDeviceInfo(Context context, XDeviceInfo xDeviceInfo) {
        this.type = xDeviceInfo.type;
        String blockingGetNakedDeviceId = blockingGetNakedDeviceId(context);
        this.deviceId = TextUtils.isEmpty(blockingGetNakedDeviceId) ? blockingGetNakedDeviceId : a.a(blockingGetNakedDeviceId);
        this.IMEI = this.type != PhoneType.PHONE ? "" : blockingGetNakedDeviceId;
        this.SN = xDeviceInfo.SN;
        this.MAC = xDeviceInfo.MAC;
        this.model = xDeviceInfo.model;
        this.keyStoreType = xDeviceInfo.keyStoreType;
    }

    public static void asyncGet(final Context context, final XCallback<DeviceInfoReayListener> xCallback) {
        new Thread(new Runnable() {
            public void run() {
                ((DeviceInfoReayListener) XCallback.this.asInterface()).onDeviceInfoReay(XDeviceInfo.syncGet(context));
            }
        }).start();
    }

    public static synchronized String blockingGetNakedDeviceId(Context context) {
        synchronized (XDeviceInfo.class) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (sLastEmptyDeviceIdTime != -1 && elapsedRealtime - sLastEmptyDeviceIdTime < EMPTY_DEVICE_ID_CACHE_TIME_MILLIS) {
                return "";
            }
            String str = null;
            try {
                str = CloudTelephonyManager.blockingGetDeviceId(context, 60000);
            } catch (IllegalDeviceException unused) {
                XLogger.loge("Failed to get the device id.");
            }
            if (TextUtils.isEmpty(str)) {
                sLastEmptyDeviceIdTime = elapsedRealtime;
                return "";
            }
            sLastEmptyDeviceIdTime = -1;
            return str;
        }
    }

    public static KeyStoreType getKeyStoreTypeUnblocking() {
        return Build.VERSION.SDK_INT >= 29 ? KeyStoreType.TZ : sKeyStoreType;
    }

    private String getMAC(Context context) {
        try {
            for (T t : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                String name = t.getName();
                if (name != null) {
                    if (name.toLowerCase().indexOf("wlan") == -1) {
                        continue;
                    } else {
                        try {
                            byte[] hardwareAddress = t.getHardwareAddress();
                            if (hardwareAddress != null) {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < hardwareAddress.length; i++) {
                                    sb.append(String.format("%02X:", new Object[]{Byte.valueOf(hardwareAddress[i])}));
                                }
                                if (sb.length() > 0) {
                                    sb.deleteCharAt(sb.length() - 1);
                                }
                                return sb.toString();
                            }
                        } catch (SocketException e) {
                            XLogger.log("Failed to get MAC for " + name + ", continue. ", e);
                        }
                    }
                }
            }
            return "N/A";
        } catch (SocketException e2) {
            XLogger.log("Failed to get MAC. ", e2);
            return "N/A";
        }
    }

    public static boolean isSupportFido() {
        return "scorpio".equals(Build.DEVICE.toLowerCase());
    }

    public static synchronized XDeviceInfo syncGet(Context context) {
        XDeviceInfo xDeviceInfo;
        synchronized (XDeviceInfo.class) {
            if (!Looper.getMainLooper().getThread().equals(Thread.currentThread())) {
                sInstance = sInstance == null ? new XDeviceInfo(context) : new XDeviceInfo(context, sInstance);
                xDeviceInfo = sInstance;
            } else {
                throw new IllegalStateException("syncGet can not be called in the main thread. ");
            }
        }
        return xDeviceInfo;
    }

    public String toString() {
        return String.format("type: %s, deviceid: %s, IMEM: %s, SN: %s, MAC: %s, model: %s, keyStoreType: %s", new Object[]{this.type.name(), this.deviceId, this.IMEI, this.SN, this.MAC, this.model, this.keyStoreType});
    }
}

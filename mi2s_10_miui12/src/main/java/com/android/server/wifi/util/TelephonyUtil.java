package com.android.server.wifi.util;

import android.net.wifi.WifiConfiguration;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.wifi.CarrierNetworkConfig;
import com.android.server.wifi.WifiNative;
import com.android.server.wifi.hotspot2.anqp.Constants;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import miui.provider.ExtraTelephony;
import miui.telephony.phonenumber.Prefix;

public class TelephonyUtil {
    public static final String ANONYMOUS_IDENTITY = "anonymous";
    public static final int CARRIER_INVALID_TYPE = -1;
    public static final int CARRIER_MNO_TYPE = 0;
    public static final int CARRIER_MVNO_TYPE = 1;
    public static final String DEFAULT_EAP_PREFIX = "\u0000";
    private static final HashMap<Integer, String> EAP_METHOD_PREFIX = new HashMap<>();
    private static final String IMSI_CIPHER_TRANSFORMATION = "RSA/ECB/OAEPwithSHA-256andMGF1Padding";
    private static final int KC_LEN = 8;
    private static final int SRES_LEN = 4;
    private static final int START_KC_POS = 4;
    private static final int START_SRES_POS = 0;
    public static final String TAG = "TelephonyUtil";
    public static final String THREE_GPP_NAI_REALM_FORMAT = "wlan.mnc%s.mcc%s.3gppnetwork.org";

    static {
        EAP_METHOD_PREFIX.put(5, "0");
        EAP_METHOD_PREFIX.put(4, "1");
        EAP_METHOD_PREFIX.put(6, ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_BLACK);
    }

    public static Pair<String, String> getSimIdentity(TelephonyManager tm, TelephonyUtil telephonyUtil, WifiConfiguration config, CarrierNetworkConfig carrierNetworkConfig) {
        if (tm == null) {
            Log.e(TAG, "No valid TelephonyManager");
            return null;
        }
        TelephonyManager defaultDataTm = tm.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId());
        if (carrierNetworkConfig == null) {
            Log.e(TAG, "No valid CarrierNetworkConfig");
            return null;
        }
        String imsi = defaultDataTm.getSubscriberId();
        String mccMnc = Prefix.EMPTY;
        if (defaultDataTm.getSimState() == 5) {
            mccMnc = defaultDataTm.getSimOperator();
        }
        String identity = buildIdentity(getSimMethodForConfig(config), imsi, mccMnc, false);
        if (identity == null) {
            Log.e(TAG, "Failed to build the identity");
            return null;
        }
        try {
            ImsiEncryptionInfo imsiEncryptionInfo = defaultDataTm.getCarrierInfoForImsiEncryption(2);
            if (imsiEncryptionInfo == null) {
                return Pair.create(identity, Prefix.EMPTY);
            }
            String encryptedIdentity = buildEncryptedIdentity(telephonyUtil, identity, imsiEncryptionInfo);
            if (encryptedIdentity != null) {
                return Pair.create(identity, encryptedIdentity);
            }
            Log.e(TAG, "failed to encrypt the identity");
            return null;
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to get imsi encryption info: " + e.getMessage());
            return null;
        }
    }

    public static String getAnonymousIdentityWith3GppRealm(@Nonnull TelephonyManager tm) {
        String mccMnc;
        if (tm == null) {
            return null;
        }
        TelephonyManager defaultDataTm = tm.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId());
        if (defaultDataTm.getSimState() != 5 || (mccMnc = defaultDataTm.getSimOperator()) == null || mccMnc.isEmpty()) {
            return null;
        }
        String mcc = mccMnc.substring(0, 3);
        String mnc = mccMnc.substring(3);
        if (mnc.length() == 2) {
            mnc = "0" + mnc;
        }
        return "anonymous@" + String.format(THREE_GPP_NAI_REALM_FORMAT, new Object[]{mnc, mcc});
    }

    @VisibleForTesting
    public String encryptDataUsingPublicKey(PublicKey key, byte[] data, int encodingFlag) {
        try {
            Cipher cipher = Cipher.getInstance(IMSI_CIPHER_TRANSFORMATION);
            cipher.init(1, key);
            byte[] encryptedBytes = cipher.doFinal(data);
            return Base64.encodeToString(encryptedBytes, 0, encryptedBytes.length, encodingFlag);
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            Log.e(TAG, "Encryption failed: " + e.getMessage());
            return null;
        }
    }

    private static String buildEncryptedIdentity(TelephonyUtil telephonyUtil, String identity, ImsiEncryptionInfo imsiEncryptionInfo) {
        if (imsiEncryptionInfo == null) {
            Log.e(TAG, "imsiEncryptionInfo is not valid");
            return null;
        } else if (identity == null) {
            Log.e(TAG, "identity is not valid");
            return null;
        } else {
            String encryptedIdentity = telephonyUtil.encryptDataUsingPublicKey(imsiEncryptionInfo.getPublicKey(), identity.getBytes(), 2);
            if (encryptedIdentity == null) {
                Log.e(TAG, "Failed to encrypt IMSI");
                return null;
            }
            String encryptedIdentity2 = DEFAULT_EAP_PREFIX + encryptedIdentity;
            if (imsiEncryptionInfo.getKeyIdentifier() == null) {
                return encryptedIdentity2;
            }
            return encryptedIdentity2 + "," + imsiEncryptionInfo.getKeyIdentifier();
        }
    }

    private static String buildIdentity(int eapMethod, String imsi, String mccMnc, boolean isEncrypted) {
        String mcc;
        String mnc;
        if (imsi == null || imsi.isEmpty()) {
            Log.e(TAG, "No IMSI or IMSI is null");
            return null;
        }
        String prefix = isEncrypted ? DEFAULT_EAP_PREFIX : EAP_METHOD_PREFIX.get(Integer.valueOf(eapMethod));
        if (prefix == null) {
            return null;
        }
        if (mccMnc == null || mccMnc.isEmpty()) {
            mcc = imsi.substring(0, 3);
            mnc = imsi.substring(3, 6);
        } else {
            mcc = mccMnc.substring(0, 3);
            mnc = mccMnc.substring(3);
            if (mnc.length() == 2) {
                mnc = "0" + mnc;
            }
        }
        return prefix + imsi + "@" + String.format(THREE_GPP_NAI_REALM_FORMAT, new Object[]{mnc, mcc});
    }

    private static int getSimMethodForConfig(WifiConfiguration config) {
        if (config == null || config.enterpriseConfig == null) {
            return -1;
        }
        int eapMethod = config.enterpriseConfig.getEapMethod();
        if (eapMethod == 0) {
            int phase2Method = config.enterpriseConfig.getPhase2Method();
            if (phase2Method == 5) {
                eapMethod = 4;
            } else if (phase2Method == 6) {
                eapMethod = 5;
            } else if (phase2Method == 7) {
                eapMethod = 6;
            }
        }
        if (isSimEapMethod(eapMethod)) {
            return eapMethod;
        }
        return -1;
    }

    public static boolean isSimConfig(WifiConfiguration config) {
        return getSimMethodForConfig(config) != -1;
    }

    public static boolean isAnonymousAtRealmIdentity(String identity) {
        if (identity == null) {
            return false;
        }
        return identity.startsWith("anonymous@");
    }

    public static boolean isSimEapMethod(int eapMethod) {
        return eapMethod == 4 || eapMethod == 5 || eapMethod == 6;
    }

    private static int parseHex(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('a' <= ch && ch <= 'f') {
            return (ch - 'a') + 10;
        }
        if ('A' <= ch && ch <= 'F') {
            return (ch - 'A') + 10;
        }
        throw new NumberFormatException(Prefix.EMPTY + ch + " is not a valid hex digit");
    }

    private static byte[] parseHex(String hex) {
        if (hex == null) {
            return new byte[0];
        }
        if (hex.length() % 2 == 0) {
            byte[] result = new byte[((hex.length() / 2) + 1)];
            result[0] = (byte) (hex.length() / 2);
            int i = 0;
            int j = 1;
            while (i < hex.length()) {
                result[j] = (byte) (((parseHex(hex.charAt(i)) * 16) + parseHex(hex.charAt(i + 1))) & Constants.BYTE_MASK);
                i += 2;
                j++;
            }
            return result;
        }
        throw new NumberFormatException(hex + " is not a valid hex string");
    }

    private static byte[] parseHexWithoutLength(String hex) {
        byte[] tmpRes = parseHex(hex);
        if (tmpRes.length == 0) {
            return tmpRes;
        }
        byte[] result = new byte[(tmpRes.length - 1)];
        System.arraycopy(tmpRes, 1, result, 0, tmpRes.length - 1);
        return result;
    }

    private static String makeHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x", new Object[]{Byte.valueOf(bytes[i])}));
        }
        return sb.toString();
    }

    private static String makeHex(byte[] bytes, int from, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(String.format("%02x", new Object[]{Byte.valueOf(bytes[from + i])}));
        }
        return sb.toString();
    }

    private static byte[] concatHex(byte[] array1, byte[] array2) {
        byte[] result = new byte[(array1.length + array2.length)];
        int index = 0;
        if (array1.length != 0) {
            int index2 = 0;
            for (byte b : array1) {
                result[index2] = b;
                index2++;
            }
            index = index2;
        }
        if (array2.length != 0) {
            for (byte b2 : array2) {
                result[index] = b2;
                index++;
            }
        }
        return result;
    }

    public static String getGsmSimAuthResponse(String[] requestData, TelephonyManager tm) {
        return getGsmAuthResponseWithLength(requestData, tm, 2);
    }

    public static String getGsmSimpleSimAuthResponse(String[] requestData, TelephonyManager tm) {
        return getGsmAuthResponseWithLength(requestData, tm, 1);
    }

    private static String getGsmAuthResponseWithLength(String[] requestData, TelephonyManager tm, int appType) {
        Object obj;
        String str;
        String[] strArr = requestData;
        TelephonyManager telephonyManager = tm;
        Object obj2 = null;
        if (telephonyManager == null) {
            Log.e(TAG, "No valid TelephonyManager");
            return null;
        }
        TelephonyManager defaultDataTm = telephonyManager.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId());
        StringBuilder sb = new StringBuilder();
        int length = strArr.length;
        int i = 0;
        int i2 = 0;
        while (i2 < length) {
            String challenge = strArr[i2];
            if (challenge == null) {
                int i3 = appType;
                obj = obj2;
            } else if (challenge.isEmpty()) {
                int i4 = appType;
                obj = obj2;
            } else {
                Log.d(TAG, "RAND = " + challenge);
                try {
                    byte[] rand = parseHex(challenge);
                    String tmResponse = defaultDataTm.getIccAuthentication(appType, 128, Base64.encodeToString(rand, 2));
                    Log.v(TAG, "Raw Response - " + tmResponse);
                    if (tmResponse == null) {
                    } else if (tmResponse.length() <= 4) {
                        byte[] bArr = rand;
                    } else {
                        byte[] result = Base64.decode(tmResponse, i);
                        Log.v(TAG, "Hex Response -" + makeHex(result));
                        byte sresLen = result[i];
                        if (sresLen < 0) {
                            byte b = sresLen;
                            str = null;
                        } else if (sresLen >= result.length) {
                            byte[] bArr2 = rand;
                            byte b2 = sresLen;
                            str = null;
                        } else {
                            String sres = makeHex(result, 1, sresLen);
                            byte[] bArr3 = rand;
                            int kcOffset = sresLen + 1;
                            if (kcOffset >= result.length) {
                                Log.e(TAG, "malformed response - " + tmResponse);
                                return null;
                            }
                            byte kcLen = result[kcOffset];
                            if (kcLen >= 0) {
                                byte b3 = sresLen;
                                if (kcOffset + kcLen <= result.length) {
                                    String kc = makeHex(result, kcOffset + 1, kcLen);
                                    sb.append(":" + kc + ":" + sres);
                                    Log.v(TAG, "kc:" + kc + " sres:" + sres);
                                    obj = null;
                                }
                            }
                            Log.e(TAG, "malformed response - " + tmResponse);
                            return null;
                        }
                        Log.e(TAG, "malformed response - " + tmResponse);
                        return str;
                    }
                    Log.e(TAG, "bad response - " + tmResponse);
                    return null;
                } catch (NumberFormatException e) {
                    int i5 = appType;
                    obj = obj2;
                    NumberFormatException numberFormatException = e;
                    Log.e(TAG, "malformed challenge");
                }
            }
            i2++;
            i = 0;
            TelephonyManager telephonyManager2 = tm;
            obj2 = obj;
            strArr = requestData;
        }
        int i6 = appType;
        return sb.toString();
    }

    public static String getGsmSimpleSimNoLengthAuthResponse(String[] requestData, TelephonyManager tm) {
        String[] strArr = requestData;
        TelephonyManager telephonyManager = tm;
        String str = null;
        if (telephonyManager == null) {
            Log.e(TAG, "No valid TelephonyManager");
            return null;
        }
        TelephonyManager defaultDataTm = telephonyManager.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId());
        StringBuilder sb = new StringBuilder();
        int length = strArr.length;
        int i = 0;
        int i2 = 0;
        while (i2 < length) {
            String challenge = strArr[i2];
            if (challenge != null && !challenge.isEmpty()) {
                Log.d(TAG, "RAND = " + challenge);
                try {
                    String tmResponse = defaultDataTm.getIccAuthentication(1, 128, Base64.encodeToString(parseHexWithoutLength(challenge), 2));
                    Log.v(TAG, "Raw Response - " + tmResponse);
                    if (tmResponse == null || tmResponse.length() <= 4) {
                        Log.e(TAG, "bad response - " + tmResponse);
                        return null;
                    }
                    byte[] result = Base64.decode(tmResponse, i);
                    if (12 != result.length) {
                        Log.e(TAG, "malformed response - " + tmResponse);
                        return str;
                    }
                    Log.v(TAG, "Hex Response -" + makeHex(result));
                    String sres = makeHex(result, 0, 4);
                    String kc = makeHex(result, 4, 8);
                    sb.append(":" + kc + ":" + sres);
                    Log.v(TAG, "kc:" + kc + " sres:" + sres);
                    str = null;
                } catch (NumberFormatException e) {
                    NumberFormatException numberFormatException = e;
                    Log.e(TAG, "malformed challenge");
                }
            }
            i2++;
            i = 0;
        }
        return sb.toString();
    }

    public static class SimAuthRequestData {
        public String[] data;
        public int networkId;
        public int protocol;
        public String ssid;

        public SimAuthRequestData() {
        }

        public SimAuthRequestData(int networkId2, int protocol2, String ssid2, String[] data2) {
            this.networkId = networkId2;
            this.protocol = protocol2;
            this.ssid = ssid2;
            this.data = data2;
        }
    }

    public static class SimAuthResponseData {
        public String response;
        public String type;

        public SimAuthResponseData(String type2, String response2) {
            this.type = type2;
            this.response = response2;
        }
    }

    public static SimAuthResponseData get3GAuthResponse(SimAuthRequestData requestData, TelephonyManager tm) {
        SimAuthRequestData simAuthRequestData = requestData;
        TelephonyManager telephonyManager = tm;
        StringBuilder sb = new StringBuilder();
        byte[] rand = null;
        byte[] authn = null;
        String resType = WifiNative.SIM_AUTH_RESP_TYPE_UMTS_AUTH;
        if (simAuthRequestData.data.length == 2) {
            try {
                rand = parseHex(simAuthRequestData.data[0]);
                authn = parseHex(simAuthRequestData.data[1]);
            } catch (NumberFormatException e) {
                Log.e(TAG, "malformed challenge");
            }
        } else {
            Log.e(TAG, "malformed challenge");
        }
        String tmResponse = Prefix.EMPTY;
        if (!(rand == null || authn == null)) {
            String base64Challenge = Base64.encodeToString(concatHex(rand, authn), 2);
            if (telephonyManager != null) {
                tmResponse = telephonyManager.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId()).getIccAuthentication(2, 129, base64Challenge);
                Log.v(TAG, "Raw Response - " + tmResponse);
            } else {
                Log.e(TAG, "No valid TelephonyManager");
            }
        }
        boolean goodReponse = false;
        if (tmResponse == null || tmResponse.length() <= 4) {
            Log.e(TAG, "bad response - " + tmResponse);
        } else {
            byte[] result = Base64.decode(tmResponse, 0);
            Log.e(TAG, "Hex Response - " + makeHex(result));
            byte tag = result[0];
            if (tag == -37) {
                Log.v(TAG, "successful 3G authentication ");
                byte resLen = result[1];
                String res = makeHex(result, 2, resLen);
                byte ckLen = result[resLen + 2];
                String ck = makeHex(result, resLen + 3, ckLen);
                byte ikLen = result[resLen + ckLen + 3];
                String ik = makeHex(result, resLen + ckLen + 4, ikLen);
                byte b = ikLen;
                sb.append(":" + ik + ":" + ck + ":" + res);
                Log.v(TAG, "ik:" + ik + "ck:" + ck + " res:" + res);
                goodReponse = true;
            } else if (tag == -36) {
                Log.e(TAG, "synchronisation failure");
                String auts = makeHex(result, 2, result[1]);
                resType = WifiNative.SIM_AUTH_RESP_TYPE_UMTS_AUTS;
                sb.append(":" + auts);
                Log.v(TAG, "auts:" + auts);
                goodReponse = true;
            } else {
                Log.e(TAG, "bad response - unknown tag = " + tag);
            }
        }
        if (!goodReponse) {
            return null;
        }
        String response = sb.toString();
        Log.v(TAG, "Supplicant Response -" + response);
        return new SimAuthResponseData(resType, response);
    }

    public static int getCarrierType(TelephonyManager tm) {
        if (tm == null) {
            return -1;
        }
        TelephonyManager defaultDataTm = tm.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId());
        if (defaultDataTm.getSimState() != 5) {
            return -1;
        }
        if (defaultDataTm.getCarrierIdFromSimMccMnc() == defaultDataTm.getSimCarrierId()) {
            return 0;
        }
        return 1;
    }

    public static boolean isSimPresent(@Nonnull SubscriptionManager sm) {
        return sm.getActiveSubscriptionIdList().length > 0;
    }
}

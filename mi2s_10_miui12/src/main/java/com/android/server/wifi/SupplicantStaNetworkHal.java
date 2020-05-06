package com.android.server.wifi;

import android.content.Context;
import android.hardware.wifi.supplicant.V1_0.ISupplicantNetwork;
import android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork;
import android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetworkCallback;
import android.hardware.wifi.supplicant.V1_0.SupplicantStatus;
import android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork;
import android.net.wifi.WifiConfiguration;
import android.os.HidlSupport;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.MutableBoolean;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.server.wifi.util.NativeUtil;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.concurrent.ThreadSafe;
import org.json.JSONException;
import org.json.JSONObject;
import vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendorStaNetwork;

@ThreadSafe
public class SupplicantStaNetworkHal {
    private static final Pattern GSM_AUTH_RESPONSE_PARAMS_PATTERN = Pattern.compile(":([0-9a-fA-F]+):([0-9a-fA-F]+)");
    @VisibleForTesting
    public static final String ID_STRING_KEY_CONFIG_KEY = "configKey";
    @VisibleForTesting
    public static final String ID_STRING_KEY_CREATOR_UID = "creatorUid";
    @VisibleForTesting
    public static final String ID_STRING_KEY_FQDN = "fqdn";
    private static final String TAG = "SupplicantStaNetworkHal";
    private static final Pattern UMTS_AUTH_RESPONSE_PARAMS_PATTERN = Pattern.compile("^:([0-9a-fA-F]+):([0-9a-fA-F]+):([0-9a-fA-F]+)$");
    private static final Pattern UMTS_AUTS_RESPONSE_PARAMS_PATTERN = Pattern.compile("^:([0-9a-fA-F]+)$");
    private int mAuthAlgMask;
    private byte[] mBssid;
    private String mEapAltSubjectMatch;
    private ArrayList<Byte> mEapAnonymousIdentity;
    private String mEapCACert;
    private String mEapCAPath;
    private String mEapClientCert;
    private String mEapDomainSuffixMatch;
    private boolean mEapEngine;
    private String mEapEngineID;
    private ArrayList<Byte> mEapIdentity;
    private int mEapMethod;
    private ArrayList<Byte> mEapPassword;
    private int mEapPhase2Method;
    private String mEapPrivateKeyId;
    private String mEapSubjectMatch;
    private int mGroupCipherMask;
    private int mGroupMgmtCipherMask;
    private ISupplicantStaNetwork mISupplicantStaNetwork;
    private ISupplicantStaNetworkCallback mISupplicantStaNetworkCallback;
    private ISupplicantVendorStaNetwork mISupplicantVendorStaNetwork;
    private String mIdStr;
    /* access modifiers changed from: private */
    public final String mIfaceName;
    private int mKeyMgmtMask;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private int mNetworkId;
    private int mPairwiseCipherMask;
    private int mProtoMask;
    private byte[] mPsk;
    private String mPskPassphrase;
    private boolean mRequirePmf;
    private String mSaePassword;
    private String mSaePasswordId;
    private boolean mScanSsid;
    private ArrayList<Byte> mSsid;
    private boolean mSystemSupportsFastBssTransition = false;
    private boolean mVerboseLoggingEnabled = false;
    private String mWapiCertSel;
    private int mWapiCertSelMode;
    private String mWapiPsk;
    private int mWapiPskType;
    private ArrayList<Byte> mWepKey;
    private int mWepTxKeyIdx;
    /* access modifiers changed from: private */
    public final WifiMonitor mWifiMonitor;

    SupplicantStaNetworkHal(ISupplicantStaNetwork iSupplicantStaNetwork, String ifaceName, Context context, WifiMonitor monitor) {
        this.mISupplicantStaNetwork = iSupplicantStaNetwork;
        this.mIfaceName = ifaceName;
        this.mWifiMonitor = monitor;
        this.mSystemSupportsFastBssTransition = context.getResources().getBoolean(17891589);
    }

    /* access modifiers changed from: package-private */
    public void enableVerboseLogging(boolean enable) {
        synchronized (this.mLock) {
            this.mVerboseLoggingEnabled = enable;
        }
    }

    public boolean loadWifiConfiguration(WifiConfiguration config, Map<String, String> networkExtras) {
        synchronized (this.mLock) {
            if (config == null) {
                return false;
            }
            config.SSID = null;
            if (!getSsid() || ArrayUtils.isEmpty(this.mSsid)) {
                Log.e(TAG, "failed to read ssid");
                return false;
            }
            config.SSID = NativeUtil.encodeSsid(this.mSsid);
            config.networkId = -1;
            if (getId()) {
                config.networkId = this.mNetworkId;
                config.getNetworkSelectionStatus().setNetworkSelectionBSSID((String) null);
                if (getBssid() && !ArrayUtils.isEmpty(this.mBssid)) {
                    config.getNetworkSelectionStatus().setNetworkSelectionBSSID(NativeUtil.macAddressFromByteArray(this.mBssid));
                }
                config.hiddenSSID = false;
                if (getScanSsid()) {
                    config.hiddenSSID = this.mScanSsid;
                }
                config.requirePMF = false;
                if (getRequirePmf()) {
                    config.requirePMF = this.mRequirePmf;
                }
                config.wepTxKeyIndex = -1;
                if (getWepTxKeyIdx()) {
                    config.wepTxKeyIndex = this.mWepTxKeyIdx;
                }
                for (int i = 0; i < 4; i++) {
                    config.wepKeys[i] = null;
                    if (getWepKey(i) && !ArrayUtils.isEmpty(this.mWepKey)) {
                        config.wepKeys[i] = NativeUtil.bytesToHexOrQuotedString(this.mWepKey);
                    }
                }
                config.preSharedKey = null;
                if (getPskPassphrase() && !TextUtils.isEmpty(this.mPskPassphrase)) {
                    config.preSharedKey = NativeUtil.addEnclosingQuotes(this.mPskPassphrase);
                } else if (getPsk() && !ArrayUtils.isEmpty(this.mPsk)) {
                    config.preSharedKey = NativeUtil.hexStringFromByteArray(this.mPsk);
                }
                if (getKeyMgmt()) {
                    config.allowedKeyManagement = removeFastTransitionFlags(supplicantToWifiConfigurationKeyMgmtMask(this.mKeyMgmtMask));
                    config.allowedKeyManagement = removeSha256KeyMgmtFlags(config.allowedKeyManagement);
                }
                if (config.allowedKeyManagement.get(190)) {
                    if (getWapiPskType()) {
                        config.wapiPskType = this.mWapiPskType;
                    }
                    if (config.wapiPskType == 0) {
                        if (getWapiPsk()) {
                            config.wapiPsk = NativeUtil.addEnclosingQuotes(this.mWapiPsk);
                        }
                    } else if (getWapiPsk()) {
                        config.wapiPsk = this.mWapiPsk;
                    }
                } else if (config.allowedKeyManagement.get(191)) {
                    if (getWapiCertSelMode()) {
                        config.wapiCertSelMode = this.mWapiCertSelMode;
                    }
                    if (config.wapiCertSelMode == 1 && getWapiCertSel()) {
                        config.wapiCertSel = this.mWapiCertSel;
                    }
                }
                if (getProto()) {
                    config.allowedProtocols = supplicantToWifiConfigurationProtoMask(this.mProtoMask);
                }
                if (getAuthAlg()) {
                    config.allowedAuthAlgorithms = supplicantToWifiConfigurationAuthAlgMask(this.mAuthAlgMask);
                }
                if (getGroupCipher()) {
                    config.allowedGroupCiphers = supplicantToWifiConfigurationGroupCipherMask(this.mGroupCipherMask);
                }
                if (getPairwiseCipher()) {
                    config.allowedPairwiseCiphers = supplicantToWifiConfigurationPairwiseCipherMask(this.mPairwiseCipherMask);
                }
                if (getGroupMgmtCipher()) {
                    config.allowedGroupManagementCiphers = supplicantToWifiConfigurationGroupMgmtCipherMask(this.mGroupMgmtCipherMask);
                }
                if (!getIdStr() || TextUtils.isEmpty(this.mIdStr)) {
                    Log.w(TAG, "getIdStr failed or empty");
                } else {
                    networkExtras.putAll(parseNetworkExtra(this.mIdStr));
                }
                boolean loadWifiEnterpriseConfig = loadWifiEnterpriseConfig(config.SSID, config.enterpriseConfig);
                return loadWifiEnterpriseConfig;
            }
            Log.e(TAG, "getId failed");
            return false;
        }
    }

    public boolean saveWifiConfiguration(WifiConfiguration config) {
        synchronized (this.mLock) {
            if (config == null) {
                return false;
            }
            if (config.SSID != null) {
                String ssid = WifiGbk.getRealSsid(config);
                if (!setSsid(NativeUtil.decodeSsid(ssid))) {
                    Log.e(TAG, "failed to set SSID: " + ssid);
                    return false;
                }
            }
            String bssidStr = config.getNetworkSelectionStatus().getNetworkSelectionBSSID();
            if (bssidStr == null || setBssid(NativeUtil.macAddressToByteArray(bssidStr))) {
                if (config.preSharedKey != null) {
                    if (config.preSharedKey.startsWith("\"")) {
                        if (config.allowedKeyManagement.get(8)) {
                            if (!setSaePassword(NativeUtil.removeEnclosingQuotes(config.preSharedKey))) {
                                Log.e(TAG, "failed to set sae password");
                                return false;
                            }
                        } else if (!setPskPassphrase(NativeUtil.removeEnclosingQuotes(config.preSharedKey))) {
                            Log.e(TAG, "failed to set psk passphrase");
                            return false;
                        }
                    } else if (config.allowedKeyManagement.get(8)) {
                        return false;
                    } else {
                        if (!setPsk(NativeUtil.hexStringToByteArray(config.preSharedKey))) {
                            Log.e(TAG, "failed to set psk");
                            return false;
                        }
                    }
                }
                boolean hasSetKey = false;
                if (config.wepKeys != null) {
                    for (int i = 0; i < config.wepKeys.length; i++) {
                        if (config.wepKeys[i] != null) {
                            if (!setWepKey(i, NativeUtil.hexOrQuotedStringToBytes(config.wepKeys[i]))) {
                                Log.e(TAG, "failed to set wep_key " + i);
                                return false;
                            }
                            hasSetKey = true;
                        }
                    }
                }
                if (hasSetKey && !setWepTxKeyIdx(config.wepTxKeyIndex)) {
                    Log.e(TAG, "failed to set wep_tx_keyidx: " + config.wepTxKeyIndex);
                    return false;
                } else if (!setScanSsid(config.hiddenSSID)) {
                    Log.e(TAG, config.SSID + ": failed to set hiddenSSID: " + config.hiddenSSID);
                    return false;
                } else {
                    if ((!config.requirePMF && (config.allowedKeyManagement.get(2) || config.allowedKeyManagement.get(3))) || setRequirePmf(config.requirePMF)) {
                        if (config.allowedKeyManagement.cardinality() != 0) {
                            BitSet keyMgmtMask = addSha256KeyMgmtFlags(addFastTransitionFlags(config.allowedKeyManagement));
                            if (!setKeyMgmt(wifiConfigurationToSupplicantKeyMgmtMask(keyMgmtMask))) {
                                Log.e(TAG, "failed to set Key Management");
                                return false;
                            }
                            if (!setVendorKeyMgmt(wifiConfigurationToSupplicantVendorKeyMgmtMask(keyMgmtMask))) {
                                Log.e(TAG, "failed to set Vendor Key Management");
                            } else if (keyMgmtMask.get(13) || keyMgmtMask.get(14)) {
                                config.enterpriseConfig.setFieldValue("eap_erp", "1");
                            }
                            if (keyMgmtMask.get(15) && !saveDppConfig(config)) {
                                Log.e(TAG, "Failed to set DPP configurations.");
                                return false;
                            } else if (keyMgmtMask.get(10) && !saveSuiteBConfig(config)) {
                                Log.e(TAG, "Failed to set Suite-B-192 configuration");
                                return false;
                            }
                        }
                        if (config.allowedKeyManagement.get(190)) {
                            if (!setWapiPskType(config.wapiPskType)) {
                                Log.e(TAG, "failed to set wapiPskType");
                                return false;
                            } else if (config.wapiPsk != null) {
                                if (config.wapiPsk.startsWith("\"")) {
                                    if (!setWapiPsk(NativeUtil.removeEnclosingQuotes(config.wapiPsk))) {
                                        Log.e(TAG, "failed to set wapiPsk with quotes");
                                        return false;
                                    }
                                } else if (!setWapiPsk(config.wapiPsk)) {
                                    Log.e(TAG, "failed to set wapiPsk");
                                    return false;
                                }
                            }
                        } else if (config.allowedKeyManagement.get(191)) {
                            if (!setWapiCertSelMode(config.wapiCertSelMode)) {
                                Log.e(TAG, "failed to set wapiCertSelMode");
                                return false;
                            } else if (config.wapiCertSelMode == 1 && !setWapiCertSel(config.wapiCertSel)) {
                                Log.e(TAG, "failed to set wapiCertSel");
                                return false;
                            }
                        }
                        if (config.allowedProtocols.cardinality() == 0 || setProto(wifiConfigurationToSupplicantProtoMask(config.allowedProtocols))) {
                            if (config.allowedProtocols.cardinality() != 0 && !setVendorProto(wifiConfigurationToSupplicantVendorProtoMask(config.allowedProtocols))) {
                                Log.e(TAG, "failed to set Vendor Security Protocol");
                            }
                            if (config.allowedAuthAlgorithms.cardinality() != 0 && isAuthAlgNeeded(config) && !setAuthAlg(wifiConfigurationToSupplicantAuthAlgMask(config.allowedAuthAlgorithms))) {
                                Log.e(TAG, "failed to set AuthAlgorithm");
                                return false;
                            } else if (config.allowedGroupCiphers.cardinality() != 0 && !setGroupCipher(wifiConfigurationToSupplicantGroupCipherMask(config.allowedGroupCiphers))) {
                                Log.e(TAG, "failed to set Group Cipher");
                                return false;
                            } else if (config.allowedPairwiseCiphers.cardinality() == 0 || setPairwiseCipher(wifiConfigurationToSupplicantPairwiseCipherMask(config.allowedPairwiseCiphers))) {
                                Map<String, String> metadata = new HashMap<>();
                                if (config.isPasspoint()) {
                                    metadata.put(ID_STRING_KEY_FQDN, config.FQDN);
                                }
                                metadata.put(ID_STRING_KEY_CONFIG_KEY, config.configKey());
                                metadata.put(ID_STRING_KEY_CREATOR_UID, Integer.toString(config.creatorUid));
                                if (!setIdStr(createNetworkExtra(metadata))) {
                                    Log.e(TAG, "failed to set id string");
                                    return false;
                                } else if (config.updateIdentifier != null && !setUpdateIdentifier(Integer.parseInt(config.updateIdentifier))) {
                                    Log.e(TAG, "failed to set update identifier");
                                    return false;
                                } else if (config.enterpriseConfig != null && config.enterpriseConfig.getEapMethod() != -1 && !saveWifiEnterpriseConfig(config.SSID, config.enterpriseConfig)) {
                                    return false;
                                } else {
                                    this.mISupplicantStaNetworkCallback = new SupplicantStaNetworkHalCallback(config.networkId, config.SSID);
                                    if (registerCallback(this.mISupplicantStaNetworkCallback)) {
                                        return true;
                                    }
                                    Log.e(TAG, "Failed to register callback");
                                    return false;
                                }
                            } else {
                                Log.e(TAG, "failed to set PairwiseCipher");
                                return false;
                            }
                        } else {
                            Log.e(TAG, "failed to set Security Protocol");
                            return false;
                        }
                    } else {
                        Log.e(TAG, config.SSID + ": failed to set requirePMF: " + config.requirePMF);
                        return false;
                    }
                }
            } else {
                Log.e(TAG, "failed to set BSSID: " + bssidStr);
                return false;
            }
        }
    }

    private boolean isAuthAlgNeeded(WifiConfiguration config) {
        if (!config.allowedKeyManagement.get(8)) {
            return true;
        }
        if (!this.mVerboseLoggingEnabled) {
            return false;
        }
        Log.d(TAG, "No need to set Auth Algorithm for SAE");
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0137, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean loadWifiEnterpriseConfig(java.lang.String r5, android.net.wifi.WifiEnterpriseConfig r6) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            r1 = 0
            if (r6 != 0) goto L_0x0008
            monitor-exit(r0)     // Catch:{ all -> 0x014a }
            return r1
        L_0x0008:
            boolean r2 = r4.getEapMethod()     // Catch:{ all -> 0x014a }
            r3 = 1
            if (r2 == 0) goto L_0x0141
            int r2 = r4.mEapMethod     // Catch:{ all -> 0x014a }
            int r2 = supplicantToWifiConfigurationEapMethod(r2)     // Catch:{ all -> 0x014a }
            r6.setEapMethod(r2)     // Catch:{ all -> 0x014a }
            boolean r2 = r4.getEapPhase2Method()     // Catch:{ all -> 0x014a }
            if (r2 == 0) goto L_0x0138
            int r1 = r4.mEapPhase2Method     // Catch:{ all -> 0x014a }
            int r1 = supplicantToWifiConfigurationEapPhase2Method(r1)     // Catch:{ all -> 0x014a }
            r6.setPhase2Method(r1)     // Catch:{ all -> 0x014a }
            boolean r1 = r4.getEapIdentity()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x0040
            java.util.ArrayList<java.lang.Byte> r1 = r4.mEapIdentity     // Catch:{ all -> 0x014a }
            boolean r1 = com.android.internal.util.ArrayUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x0040
            java.lang.String r1 = "identity"
            java.util.ArrayList<java.lang.Byte> r2 = r4.mEapIdentity     // Catch:{ all -> 0x014a }
            java.lang.String r2 = com.android.server.wifi.util.NativeUtil.stringFromByteArrayList(r2)     // Catch:{ all -> 0x014a }
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x0040:
            boolean r1 = r4.getEapAnonymousIdentity()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x0059
            java.util.ArrayList<java.lang.Byte> r1 = r4.mEapAnonymousIdentity     // Catch:{ all -> 0x014a }
            boolean r1 = com.android.internal.util.ArrayUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x0059
            java.lang.String r1 = "anonymous_identity"
            java.util.ArrayList<java.lang.Byte> r2 = r4.mEapAnonymousIdentity     // Catch:{ all -> 0x014a }
            java.lang.String r2 = com.android.server.wifi.util.NativeUtil.stringFromByteArrayList(r2)     // Catch:{ all -> 0x014a }
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x0059:
            boolean r1 = r4.getEapPassword()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x0072
            java.util.ArrayList<java.lang.Byte> r1 = r4.mEapPassword     // Catch:{ all -> 0x014a }
            boolean r1 = com.android.internal.util.ArrayUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x0072
            java.lang.String r1 = "password"
            java.util.ArrayList<java.lang.Byte> r2 = r4.mEapPassword     // Catch:{ all -> 0x014a }
            java.lang.String r2 = com.android.server.wifi.util.NativeUtil.stringFromByteArrayList(r2)     // Catch:{ all -> 0x014a }
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x0072:
            boolean r1 = r4.getEapClientCert()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x0087
            java.lang.String r1 = r4.mEapClientCert     // Catch:{ all -> 0x014a }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x0087
            java.lang.String r1 = "client_cert"
            java.lang.String r2 = r4.mEapClientCert     // Catch:{ all -> 0x014a }
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x0087:
            boolean r1 = r4.getEapCACert()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x009c
            java.lang.String r1 = r4.mEapCACert     // Catch:{ all -> 0x014a }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x009c
            java.lang.String r1 = "ca_cert"
            java.lang.String r2 = r4.mEapCACert     // Catch:{ all -> 0x014a }
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x009c:
            boolean r1 = r4.getEapSubjectMatch()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x00b1
            java.lang.String r1 = r4.mEapSubjectMatch     // Catch:{ all -> 0x014a }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x00b1
            java.lang.String r1 = "subject_match"
            java.lang.String r2 = r4.mEapSubjectMatch     // Catch:{ all -> 0x014a }
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x00b1:
            boolean r1 = r4.getEapEngineID()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x00c6
            java.lang.String r1 = r4.mEapEngineID     // Catch:{ all -> 0x014a }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x00c6
            java.lang.String r1 = "engine_id"
            java.lang.String r2 = r4.mEapEngineID     // Catch:{ all -> 0x014a }
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x00c6:
            boolean r1 = r4.getEapEngine()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x00e2
            java.lang.String r1 = r4.mEapEngineID     // Catch:{ all -> 0x014a }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x00e2
            java.lang.String r1 = "engine"
            boolean r2 = r4.mEapEngine     // Catch:{ all -> 0x014a }
            if (r2 == 0) goto L_0x00dd
            java.lang.String r2 = "1"
            goto L_0x00df
        L_0x00dd:
            java.lang.String r2 = "0"
        L_0x00df:
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x00e2:
            boolean r1 = r4.getEapPrivateKeyId()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x00f7
            java.lang.String r1 = r4.mEapPrivateKeyId     // Catch:{ all -> 0x014a }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x00f7
            java.lang.String r1 = "key_id"
            java.lang.String r2 = r4.mEapPrivateKeyId     // Catch:{ all -> 0x014a }
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x00f7:
            boolean r1 = r4.getEapAltSubjectMatch()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x010c
            java.lang.String r1 = r4.mEapAltSubjectMatch     // Catch:{ all -> 0x014a }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x010c
            java.lang.String r1 = "altsubject_match"
            java.lang.String r2 = r4.mEapAltSubjectMatch     // Catch:{ all -> 0x014a }
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x010c:
            boolean r1 = r4.getEapDomainSuffixMatch()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x0121
            java.lang.String r1 = r4.mEapDomainSuffixMatch     // Catch:{ all -> 0x014a }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x0121
            java.lang.String r1 = "domain_suffix_match"
            java.lang.String r2 = r4.mEapDomainSuffixMatch     // Catch:{ all -> 0x014a }
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x0121:
            boolean r1 = r4.getEapCAPath()     // Catch:{ all -> 0x014a }
            if (r1 == 0) goto L_0x0136
            java.lang.String r1 = r4.mEapCAPath     // Catch:{ all -> 0x014a }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014a }
            if (r1 != 0) goto L_0x0136
            java.lang.String r1 = "ca_path"
            java.lang.String r2 = r4.mEapCAPath     // Catch:{ all -> 0x014a }
            r6.setFieldValue(r1, r2)     // Catch:{ all -> 0x014a }
        L_0x0136:
            monitor-exit(r0)     // Catch:{ all -> 0x014a }
            return r3
        L_0x0138:
            java.lang.String r2 = "SupplicantStaNetworkHal"
            java.lang.String r3 = "failed to get eap phase2 method"
            android.util.Log.e(r2, r3)     // Catch:{ all -> 0x014a }
            monitor-exit(r0)     // Catch:{ all -> 0x014a }
            return r1
        L_0x0141:
            java.lang.String r1 = "SupplicantStaNetworkHal"
            java.lang.String r2 = "failed to get eap method. Assumimg not an enterprise network"
            android.util.Log.e(r1, r2)     // Catch:{ all -> 0x014a }
            monitor-exit(r0)     // Catch:{ all -> 0x014a }
            return r3
        L_0x014a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x014a }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.SupplicantStaNetworkHal.loadWifiEnterpriseConfig(java.lang.String, android.net.wifi.WifiEnterpriseConfig):boolean");
    }

    private boolean saveDppConfig(WifiConfiguration config) {
        if (config.dppConnector != null && !setDppConnector(config.dppConnector)) {
            Log.e(TAG, "failed to set DPP connector");
            return false;
        } else if (config.dppNetAccessKey != null && !setDppNetAccessKey(NativeUtil.stringToByteArrayList(config.dppNetAccessKey))) {
            Log.e(TAG, "failed to set DPP Net Access key");
            return false;
        } else if (config.dppNetAccessKeyExpiry >= 0 && !setDppNetAccessKeyExpiry(config.dppNetAccessKeyExpiry)) {
            Log.e(TAG, "failed to set DPP Net Access Key Expiry time");
            return false;
        } else if (config.dppCsign == null || setDppCsign(NativeUtil.stringToByteArrayList(config.dppCsign))) {
            return true;
        } else {
            Log.e(TAG, "failed to set DPP c-sign");
            return false;
        }
    }

    private boolean saveSuiteBConfig(WifiConfiguration config) {
        if (config.allowedGroupCiphers.cardinality() != 0 && !setGroupCipher(wifiConfigurationToSupplicantGroupCipherMask(config.allowedGroupCiphers))) {
            Log.e(TAG, "failed to set Group Cipher");
            return false;
        } else if (config.allowedPairwiseCiphers.cardinality() != 0 && !setPairwiseCipher(wifiConfigurationToSupplicantPairwiseCipherMask(config.allowedPairwiseCiphers))) {
            Log.e(TAG, "failed to set PairwiseCipher");
            return false;
        } else if (config.allowedGroupManagementCiphers.cardinality() == 0 || setGroupMgmtCipher(wifiConfigurationToSupplicantGroupMgmtCipherMask(config.allowedGroupManagementCiphers))) {
            if (config.allowedSuiteBCiphers.get(1)) {
                if (!enableTlsSuiteBEapPhase1Param(true)) {
                    Log.e(TAG, "failed to set TLSSuiteB");
                    return false;
                }
            } else if (config.allowedSuiteBCiphers.get(0) && !enableSuiteBEapOpenSslCiphers()) {
                Log.e(TAG, "failed to set OpensslCipher");
                return false;
            }
            return true;
        } else {
            Log.e(TAG, "failed to set GroupMgmtCipher");
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:136:0x035b, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean saveWifiEnterpriseConfig(java.lang.String r7, android.net.wifi.WifiEnterpriseConfig r8) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            r1 = 0
            if (r8 != 0) goto L_0x0008
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x0008:
            int r2 = r8.getEapMethod()     // Catch:{ all -> 0x035c }
            int r2 = wifiConfigurationToSupplicantEapMethod(r2)     // Catch:{ all -> 0x035c }
            boolean r2 = r6.setEapMethod(r2)     // Catch:{ all -> 0x035c }
            if (r2 != 0) goto L_0x0035
            java.lang.String r2 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r3.<init>()     // Catch:{ all -> 0x035c }
            r3.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = ": failed to set eap method: "
            r3.append(r4)     // Catch:{ all -> 0x035c }
            int r4 = r8.getEapMethod()     // Catch:{ all -> 0x035c }
            r3.append(r4)     // Catch:{ all -> 0x035c }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r2, r3)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x0035:
            int r2 = r8.getPhase2Method()     // Catch:{ all -> 0x035c }
            int r2 = wifiConfigurationToSupplicantEapPhase2Method(r2)     // Catch:{ all -> 0x035c }
            boolean r2 = r6.setEapPhase2Method(r2)     // Catch:{ all -> 0x035c }
            if (r2 != 0) goto L_0x0063
            java.lang.String r2 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r3.<init>()     // Catch:{ all -> 0x035c }
            r3.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = ": failed to set eap phase 2 method: "
            r3.append(r4)     // Catch:{ all -> 0x035c }
            int r4 = r8.getPhase2Method()     // Catch:{ all -> 0x035c }
            r3.append(r4)     // Catch:{ all -> 0x035c }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r2, r3)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x0063:
            r2 = 0
            java.lang.String r3 = "identity"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0096
            java.util.ArrayList r3 = com.android.server.wifi.util.NativeUtil.stringToByteArrayList(r2)     // Catch:{ all -> 0x035c }
            boolean r3 = r6.setEapIdentity(r3)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0096
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap identity: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x0096:
            java.lang.String r3 = "anonymous_identity"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x00c8
            java.util.ArrayList r3 = com.android.server.wifi.util.NativeUtil.stringToByteArrayList(r2)     // Catch:{ all -> 0x035c }
            boolean r3 = r6.setEapAnonymousIdentity(r3)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x00c8
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap anonymous identity: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x00c8:
            java.lang.String r3 = "password"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x00f7
            java.util.ArrayList r3 = com.android.server.wifi.util.NativeUtil.stringToByteArrayList(r2)     // Catch:{ all -> 0x035c }
            boolean r3 = r6.setEapPassword(r3)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x00f7
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap password"
            r4.append(r5)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x00f7:
            java.lang.String r3 = "client_cert"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0125
            boolean r3 = r6.setEapClientCert(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0125
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap client cert: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x0125:
            java.lang.String r3 = "ca_cert"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0153
            boolean r3 = r6.setEapCACert(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0153
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap ca cert: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x0153:
            java.lang.String r3 = "subject_match"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0181
            boolean r3 = r6.setEapSubjectMatch(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0181
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap subject match: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x0181:
            java.lang.String r3 = "engine_id"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x01af
            boolean r3 = r6.setEapEngineID(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x01af
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap engine id: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x01af:
            java.lang.String r3 = "engine"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            r4 = 1
            if (r3 != 0) goto L_0x01e9
            java.lang.String r3 = "1"
            boolean r3 = r2.equals(r3)     // Catch:{ all -> 0x035c }
            if (r3 == 0) goto L_0x01c7
            r3 = r4
            goto L_0x01c8
        L_0x01c7:
            r3 = r1
        L_0x01c8:
            boolean r3 = r6.setEapEngine(r3)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x01e9
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap engine: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x01e9:
            java.lang.String r3 = "key_id"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0217
            boolean r3 = r6.setEapPrivateKeyId(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0217
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap private key: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x0217:
            java.lang.String r3 = "altsubject_match"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0245
            boolean r3 = r6.setEapAltSubjectMatch(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0245
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap alt subject match: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x0245:
            java.lang.String r3 = "domain_suffix_match"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0273
            boolean r3 = r6.setEapDomainSuffixMatch(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x0273
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap domain suffix match: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x0273:
            java.lang.String r3 = "ca_path"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x02a1
            boolean r3 = r6.setEapCAPath(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x02a1
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap ca path: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x02a1:
            java.lang.String r3 = "proactive_key_caching"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x02da
            java.lang.String r3 = "1"
            boolean r3 = r2.equals(r3)     // Catch:{ all -> 0x035c }
            if (r3 == 0) goto L_0x02b8
            r3 = r4
            goto L_0x02b9
        L_0x02b8:
            r3 = r1
        L_0x02b9:
            boolean r3 = r6.setEapProactiveKeyCaching(r3)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x02da
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set proactive key caching: "
            r4.append(r5)     // Catch:{ all -> 0x035c }
            r4.append(r2)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x02da:
            java.lang.String r3 = "eap_erp"
            java.lang.String r3 = r8.getFieldValue(r3)     // Catch:{ all -> 0x035c }
            r2 = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x032b
            java.lang.String r3 = "1"
            boolean r3 = r2.equals(r3)     // Catch:{ all -> 0x035c }
            if (r3 == 0) goto L_0x032b
            boolean r3 = r6.setEapErp(r4)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x030d
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set eap erp"
            r4.append(r5)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x030d:
            boolean r3 = r6.setAuthAlg(r1)     // Catch:{ all -> 0x035c }
            if (r3 != 0) goto L_0x032b
            java.lang.String r3 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r4.<init>()     // Catch:{ all -> 0x035c }
            r4.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to reset AuthAlgorithm"
            r4.append(r5)     // Catch:{ all -> 0x035c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x035c }
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r1
        L_0x032b:
            java.lang.String r1 = "sim_num"
            java.lang.String r1 = r8.getFieldValue(r1)     // Catch:{ all -> 0x035c }
            boolean r2 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x035c }
            if (r2 != 0) goto L_0x035a
            int r2 = java.lang.Integer.parseInt(r1)     // Catch:{ all -> 0x035c }
            boolean r2 = r6.setVendorSimNumber(r2)     // Catch:{ all -> 0x035c }
            if (r2 != 0) goto L_0x035a
            java.lang.String r2 = "SupplicantStaNetworkHal"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x035c }
            r3.<init>()     // Catch:{ all -> 0x035c }
            r3.append(r7)     // Catch:{ all -> 0x035c }
            java.lang.String r5 = ": failed to set VendorSimNumber : "
            r3.append(r5)     // Catch:{ all -> 0x035c }
            r3.append(r1)     // Catch:{ all -> 0x035c }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x035c }
            android.util.Log.e(r2, r3)     // Catch:{ all -> 0x035c }
        L_0x035a:
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            return r4
        L_0x035c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x035c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.SupplicantStaNetworkHal.saveWifiEnterpriseConfig(java.lang.String, android.net.wifi.WifiEnterpriseConfig):boolean");
    }

    private android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork getV1_2StaNetwork() {
        android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork supplicantStaNetworkForV1_2Mockable;
        synchronized (this.mLock) {
            supplicantStaNetworkForV1_2Mockable = getSupplicantStaNetworkForV1_2Mockable();
        }
        return supplicantStaNetworkForV1_2Mockable;
    }

    private static int wifiConfigurationToSupplicantKeyMgmtMask(BitSet keyMgmt) {
        int mask = 0;
        int bit = keyMgmt.nextSetBit(0);
        while (bit != -1) {
            if (!(bit == 190 || bit == 191)) {
                switch (bit) {
                    case 0:
                        mask |= 4;
                        break;
                    case 1:
                        mask |= 2;
                        break;
                    case 2:
                        mask |= 1;
                        break;
                    case 3:
                        mask |= 8;
                        break;
                    case 4:
                    case 13:
                    case 14:
                    case 15:
                        break;
                    case 5:
                        mask |= 32768;
                        break;
                    case 6:
                        mask |= 64;
                        break;
                    case 7:
                        mask |= 32;
                        break;
                    case 8:
                        mask |= 1024;
                        break;
                    case 9:
                        mask |= 4194304;
                        break;
                    case 10:
                        mask |= 131072;
                        break;
                    case 11:
                        mask |= 256;
                        break;
                    case 12:
                        mask |= 128;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid protoMask bit in keyMgmt: " + bit);
                }
            }
            bit = keyMgmt.nextSetBit(bit + 1);
        }
        return mask;
    }

    private static int wifiConfigurationToSupplicantVendorKeyMgmtMask(BitSet keyMgmt) {
        int mask = 0;
        int bit = keyMgmt.nextSetBit(0);
        while (bit != -1) {
            if (bit == 190) {
                mask |= 4096;
            } else if (bit != 191) {
                switch (bit) {
                    case 8:
                    case 9:
                    case 10:
                        break;
                    default:
                        switch (bit) {
                            case 13:
                                Log.e(TAG, "wifiConfigurationToSupplicantVendorKeyMgmtMask: 13");
                                mask |= 262144;
                                break;
                            case 14:
                                Log.e(TAG, "wifiConfigurationToSupplicantVendorKeyMgmtMask: 14");
                                mask |= ISupplicantVendorStaNetwork.VendorKeyMgmtMask.FILS_SHA384;
                                break;
                            case 15:
                                mask |= 8388608;
                                break;
                            default:
                                Log.e(TAG, "Invalid VendorKeyMgmtMask bit in keyMgmt: " + bit);
                                break;
                        }
                }
            } else {
                mask |= 8192;
            }
            bit = keyMgmt.nextSetBit(bit + 1);
        }
        return mask;
    }

    private static int wifiConfigurationToSupplicantProtoMask(BitSet protoMask) {
        int mask = 0;
        int bit = protoMask.nextSetBit(0);
        while (bit != -1) {
            if (bit == 0) {
                mask |= 1;
            } else if (bit == 1) {
                mask |= 2;
            } else if (bit == 2) {
                mask |= 8;
            } else if (bit != 3) {
                throw new IllegalArgumentException("Invalid protoMask bit in wificonfig: " + bit);
            }
            bit = protoMask.nextSetBit(bit + 1);
        }
        return mask;
    }

    private static int wifiConfigurationToSupplicantVendorProtoMask(BitSet protoMask) {
        int mask = 0;
        int bit = protoMask.nextSetBit(0);
        while (bit != -1) {
            if (bit != 3) {
                Log.e(TAG, "Invalid protoMask bit in wificonfig: " + bit);
            } else {
                mask |= 4;
            }
            bit = protoMask.nextSetBit(bit + 1);
        }
        return mask;
    }

    private static int wifiConfigurationToSupplicantAuthAlgMask(BitSet authAlgMask) {
        int mask = 0;
        int bit = authAlgMask.nextSetBit(0);
        while (bit != -1) {
            if (bit == 0) {
                mask |= 1;
            } else if (bit == 1) {
                mask |= 2;
            } else if (bit == 2) {
                mask |= 4;
            } else {
                throw new IllegalArgumentException("Invalid authAlgMask bit in wificonfig: " + bit);
            }
            bit = authAlgMask.nextSetBit(bit + 1);
        }
        return mask;
    }

    private static int wifiConfigurationToSupplicantGroupCipherMask(BitSet groupCipherMask) {
        int mask = 0;
        int bit = groupCipherMask.nextSetBit(0);
        while (bit != -1) {
            if (bit == 0) {
                mask |= 2;
            } else if (bit == 1) {
                mask |= 4;
            } else if (bit == 2) {
                mask |= 8;
            } else if (bit == 3) {
                mask |= 16;
            } else if (bit == 4) {
                mask |= 16384;
            } else if (bit == 5) {
                mask |= 256;
            } else {
                throw new IllegalArgumentException("Invalid GroupCipherMask bit in wificonfig: " + bit);
            }
            bit = groupCipherMask.nextSetBit(bit + 1);
        }
        return mask;
    }

    private static int wifiConfigurationToSupplicantGroupMgmtCipherMask(BitSet groupMgmtCipherMask) {
        int mask = 0;
        int bit = groupMgmtCipherMask.nextSetBit(0);
        while (bit != -1) {
            if (bit == 0) {
                mask |= 8192;
            } else if (bit == 1) {
                mask |= 2048;
            } else if (bit == 2) {
                mask |= 4096;
            } else {
                throw new IllegalArgumentException("Invalid GroupMgmtCipherMask bit in wificonfig: " + bit);
            }
            bit = groupMgmtCipherMask.nextSetBit(bit + 1);
        }
        return mask;
    }

    private static int wifiConfigurationToSupplicantPairwiseCipherMask(BitSet pairwiseCipherMask) {
        int mask = 0;
        int bit = pairwiseCipherMask.nextSetBit(0);
        while (bit != -1) {
            if (bit == 0) {
                mask |= 1;
            } else if (bit == 1) {
                mask |= 8;
            } else if (bit == 2) {
                mask |= 16;
            } else if (bit == 3) {
                mask |= 256;
            } else {
                throw new IllegalArgumentException("Invalid pairwiseCipherMask bit in wificonfig: " + bit);
            }
            bit = pairwiseCipherMask.nextSetBit(bit + 1);
        }
        return mask;
    }

    private static int supplicantToWifiConfigurationEapMethod(int value) {
        switch (value) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            default:
                Log.e(TAG, "invalid eap method value from supplicant: " + value);
                return -1;
        }
    }

    private static int supplicantToWifiConfigurationEapPhase2Method(int value) {
        switch (value) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            default:
                Log.e(TAG, "invalid eap phase2 method value from supplicant: " + value);
                return -1;
        }
    }

    private static int supplicantMaskValueToWifiConfigurationBitSet(int supplicantMask, int supplicantValue, BitSet bitset, int bitSetPosition) {
        bitset.set(bitSetPosition, (supplicantMask & supplicantValue) == supplicantValue);
        return (~supplicantValue) & supplicantMask;
    }

    private static BitSet supplicantToWifiConfigurationKeyMgmtMask(int mask) {
        BitSet bitset = new BitSet();
        int mask2 = supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(mask, 4, bitset, 0), 2, bitset, 1), 1, bitset, 2), 8, bitset, 3), 32768, bitset, 5), 64, bitset, 6), 32, bitset, 7), 4096, bitset, 190), 8192, bitset, 191), 1024, bitset, 8), 4194304, bitset, 9), 131072, bitset, 10), 256, bitset, 11), 128, bitset, 12), 262144, bitset, 13), ISupplicantVendorStaNetwork.VendorKeyMgmtMask.FILS_SHA384, bitset, 14);
        if (mask2 == 0) {
            return bitset;
        }
        throw new IllegalArgumentException("invalid key mgmt mask from supplicant: " + mask2);
    }

    private static BitSet supplicantToWifiConfigurationProtoMask(int mask) {
        BitSet bitset = new BitSet();
        int mask2 = supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(mask, 1, bitset, 0), 2, bitset, 1), 8, bitset, 2), 4, bitset, 3);
        if (mask2 == 0) {
            return bitset;
        }
        throw new IllegalArgumentException("invalid proto mask from supplicant: " + mask2);
    }

    private static BitSet supplicantToWifiConfigurationAuthAlgMask(int mask) {
        BitSet bitset = new BitSet();
        int mask2 = supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(mask, 1, bitset, 0), 2, bitset, 1), 4, bitset, 2);
        if (mask2 == 0) {
            return bitset;
        }
        throw new IllegalArgumentException("invalid auth alg mask from supplicant: " + mask2);
    }

    private static BitSet supplicantToWifiConfigurationGroupCipherMask(int mask) {
        BitSet bitset = new BitSet();
        int mask2 = supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(mask, 2, bitset, 0), 4, bitset, 1), 8, bitset, 2), 16, bitset, 3), 256, bitset, 5), 256, bitset, 5), 16384, bitset, 4);
        if (mask2 == 0) {
            return bitset;
        }
        throw new IllegalArgumentException("invalid group cipher mask from supplicant: " + mask2);
    }

    private static BitSet supplicantToWifiConfigurationGroupMgmtCipherMask(int mask) {
        BitSet bitset = new BitSet();
        int mask2 = supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(mask, 2048, bitset, 1), 4096, bitset, 2), 8192, bitset, 0);
        if (mask2 == 0) {
            return bitset;
        }
        throw new IllegalArgumentException("invalid group mgmt cipher mask from supplicant: " + mask2);
    }

    private static BitSet supplicantToWifiConfigurationPairwiseCipherMask(int mask) {
        BitSet bitset = new BitSet();
        int mask2 = supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(supplicantMaskValueToWifiConfigurationBitSet(mask, 1, bitset, 0), 8, bitset, 1), 16, bitset, 2), 256, bitset, 3);
        if (mask2 == 0) {
            return bitset;
        }
        throw new IllegalArgumentException("invalid pairwise cipher mask from supplicant: " + mask2);
    }

    private static int wifiConfigurationToSupplicantEapMethod(int value) {
        switch (value) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            default:
                Log.e(TAG, "invalid eap method value from WifiConfiguration: " + value);
                return -1;
        }
    }

    private static int wifiConfigurationToSupplicantEapPhase2Method(int value) {
        switch (value) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            default:
                Log.e(TAG, "invalid eap phase2 method value from WifiConfiguration: " + value);
                return -1;
        }
    }

    public boolean getId() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getId")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getId(new ISupplicantNetwork.getIdCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getId$0$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getId");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getId$0$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int idValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mNetworkId = idValue;
        } else {
            checkStatusAndLogFailure(status, "getId");
        }
    }

    public int getNetworkId() {
        return this.mNetworkId;
    }

    public void setVendorStaNetwork(ISupplicantVendorStaNetwork vendor_network) {
        PrintStream printStream = System.out;
        printStream.println("stanetwork getId >>" + this.mNetworkId);
        if (vendor_network != null) {
            Log.e(TAG, "set ISupplicantVendorStaNetwork successfull");
            this.mISupplicantVendorStaNetwork = vendor_network;
            return;
        }
        Log.e(TAG, "Failed to set ISupplicantVendorStaNetwork due to null");
    }

    private boolean registerCallback(ISupplicantStaNetworkCallback callback) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("registerCallback")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.registerCallback(callback), "registerCallback");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "registerCallback");
                return false;
            }
        }
    }

    private boolean setSsid(ArrayList<Byte> ssid) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setSsid")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setSsid(ssid), "setSsid");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setSsid");
                return false;
            }
        }
    }

    public boolean setBssid(String bssidStr) {
        boolean bssid;
        synchronized (this.mLock) {
            try {
                bssid = setBssid(NativeUtil.macAddressToByteArray(bssidStr));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Illegal argument " + bssidStr, e);
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
        return bssid;
    }

    private boolean setBssid(byte[] bssid) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setBssid")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setBssid(bssid), "setBssid");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setBssid");
                return false;
            }
        }
    }

    private boolean setScanSsid(boolean enable) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setScanSsid")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setScanSsid(enable), "setScanSsid");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setScanSsid");
                return false;
            }
        }
    }

    private boolean setKeyMgmt(int keyMgmtMask) {
        SupplicantStatus status;
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setKeyMgmt")) {
                return false;
            }
            try {
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork iSupplicantStaNetworkV12 = getV1_2StaNetwork();
                if (iSupplicantStaNetworkV12 != null) {
                    status = iSupplicantStaNetworkV12.setKeyMgmt_1_2(keyMgmtMask);
                } else {
                    status = this.mISupplicantStaNetwork.setKeyMgmt(keyMgmtMask);
                }
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(status, "setKeyMgmt");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setKeyMgmt");
                return false;
            }
        }
    }

    private boolean setVendorKeyMgmt(int keyMgmtMask) {
        synchronized (this.mLock) {
            Log.e(TAG, "Vendor Key Management " + keyMgmtMask);
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setVendorKeyMgmt")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setVendorKeyMgmt(keyMgmtMask), "setVendorKeyMgmt");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setVendorKeyMgmt");
                return false;
            }
        }
    }

    private boolean setWapiPskType(int type) {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setWapiPskType")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setWapiPskType(type), "setWapiPskType");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setWapiPskType");
                return false;
            }
        }
    }

    private boolean setWapiPsk(String psk) {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setWapiPsk")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setWapiPsk(psk), "setWapiPsk");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setWapiPsk");
                return false;
            }
        }
    }

    private boolean setWapiCertSelMode(int mode) {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setWapiCertSelMode")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setWapiCertSelMode(mode), "setWapiCertSelMode");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setWapiCertSelMode");
                return false;
            }
        }
    }

    private boolean setWapiCertSel(String name) {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setWapiCertSel")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setWapiCertSel(name), "setWapiCertSel");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setWapiCertSel");
                return false;
            }
        }
    }

    private boolean getWapiPskType() {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("getWapiPskType")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantVendorStaNetwork.getWapiPskType(new ISupplicantVendorStaNetwork.getWapiPskTypeCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getWapiPskType$1$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getWapiPskType");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getWapiPskType$1$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int wapiPskTypeValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mWapiPskType = wapiPskTypeValue;
        } else {
            checkVendorStatusAndLogFailure(status, "getWapiPskType");
        }
    }

    private boolean getWapiPsk() {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("getWapiPsk")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantVendorStaNetwork.getWapiPsk(new ISupplicantVendorStaNetwork.getWapiPskCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getWapiPsk$2$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getWapiPsk");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getWapiPsk$2$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String wapiPskValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mWapiPsk = wapiPskValue;
        } else {
            checkVendorStatusAndLogFailure(status, "getWapiPsk");
        }
    }

    private boolean getWapiCertSelMode() {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("getWapiCertSelMode")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantVendorStaNetwork.getWapiCertSelMode(new ISupplicantVendorStaNetwork.getWapiCertSelModeCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getWapiCertSelMode$3$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getWapiCertSelMode");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getWapiCertSelMode$3$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int wapiCertSelModeValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mWapiCertSelMode = wapiCertSelModeValue;
        } else {
            checkVendorStatusAndLogFailure(status, "getWapiCertSelMode");
        }
    }

    private boolean getWapiCertSel() {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("getWapiCertSel")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantVendorStaNetwork.getWapiCertSel(new ISupplicantVendorStaNetwork.getWapiCertSelCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getWapiCertSel$4$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getWapiCertSel");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getWapiCertSel$4$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String wapiCertSelValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mWapiCertSel = wapiCertSelValue;
        } else {
            checkVendorStatusAndLogFailure(status, "getWapiCertSel");
        }
    }

    private boolean setProto(int protoMask) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setProto")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setProto(protoMask), "setProto");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setProto");
                return false;
            }
        }
    }

    private boolean setVendorProto(int protoMask) {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setVendorProto")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setVendorProto(protoMask), "setVendorProto");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setVendorProto");
                return false;
            }
        }
    }

    private boolean setAuthAlg(int authAlgMask) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setAuthAlg")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setAuthAlg(authAlgMask), "setAuthAlg");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setAuthAlg");
                return false;
            }
        }
    }

    private boolean setGroupCipher(int groupCipherMask) {
        SupplicantStatus status;
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setGroupCipher")) {
                return false;
            }
            try {
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork iSupplicantStaNetworkV12 = getV1_2StaNetwork();
                if (iSupplicantStaNetworkV12 != null) {
                    status = iSupplicantStaNetworkV12.setGroupCipher_1_2(groupCipherMask);
                } else {
                    status = this.mISupplicantStaNetwork.setGroupCipher(groupCipherMask);
                }
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(status, "setGroupCipher");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setGroupCipher");
                return false;
            }
        }
    }

    private boolean enableTlsSuiteBEapPhase1Param(boolean enable) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapPhase1Params")) {
                return false;
            }
            try {
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork iSupplicantStaNetworkV12 = getV1_2StaNetwork();
                if (iSupplicantStaNetworkV12 != null) {
                    boolean checkStatusAndLogFailure = checkStatusAndLogFailure(iSupplicantStaNetworkV12.enableTlsSuiteBEapPhase1Param(enable), "setEapPhase1Params");
                    return checkStatusAndLogFailure;
                }
                Log.e(TAG, "Supplicant HAL version does not support setEapPhase1Params");
                return false;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapPhase1Params");
                return false;
            }
        }
    }

    private boolean enableSuiteBEapOpenSslCiphers() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapOpenSslCiphers")) {
                return false;
            }
            try {
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork iSupplicantStaNetworkV12 = getV1_2StaNetwork();
                if (iSupplicantStaNetworkV12 != null) {
                    boolean checkStatusAndLogFailure = checkStatusAndLogFailure(iSupplicantStaNetworkV12.enableSuiteBEapOpenSslCiphers(), "setEapOpenSslCiphers");
                    return checkStatusAndLogFailure;
                }
                Log.e(TAG, "Supplicant HAL version does not support setEapOpenSslCiphers");
                return false;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapOpenSslCiphers");
                return false;
            }
        }
    }

    private boolean setPairwiseCipher(int pairwiseCipherMask) {
        SupplicantStatus status;
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setPairwiseCipher")) {
                return false;
            }
            try {
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork iSupplicantStaNetworkV12 = getV1_2StaNetwork();
                if (iSupplicantStaNetworkV12 != null) {
                    status = iSupplicantStaNetworkV12.setPairwiseCipher_1_2(pairwiseCipherMask);
                } else {
                    status = this.mISupplicantStaNetwork.setPairwiseCipher(pairwiseCipherMask);
                }
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(status, "setPairwiseCipher");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setPairwiseCipher");
                return false;
            }
        }
    }

    private boolean setGroupMgmtCipher(int groupMgmtCipherMask) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setGroupMgmtCipher")) {
                return false;
            }
            try {
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork iSupplicantStaNetworkV12 = getV1_2StaNetwork();
                if (iSupplicantStaNetworkV12 == null) {
                    return false;
                }
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(iSupplicantStaNetworkV12.setGroupMgmtCipher(groupMgmtCipherMask), "setGroupMgmtCipher");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setGroupMgmtCipher");
                return false;
            }
        }
    }

    private boolean setPskPassphrase(String psk) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setPskPassphrase")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setPskPassphrase(psk), "setPskPassphrase");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setPskPassphrase");
                return false;
            }
        }
    }

    private boolean setPsk(byte[] psk) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setPsk")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setPsk(psk), "setPsk");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setPsk");
                return false;
            }
        }
    }

    private boolean setWepKey(int keyIdx, ArrayList<Byte> wepKey) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setWepKey")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setWepKey(keyIdx, wepKey), "setWepKey");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setWepKey");
                return false;
            }
        }
    }

    private boolean setWepTxKeyIdx(int keyIdx) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setWepTxKeyIdx")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setWepTxKeyIdx(keyIdx), "setWepTxKeyIdx");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setWepTxKeyIdx");
                return false;
            }
        }
    }

    private boolean setRequirePmf(boolean enable) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setRequirePmf")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setRequirePmf(enable), "setRequirePmf");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setRequirePmf");
                return false;
            }
        }
    }

    private boolean setUpdateIdentifier(int identifier) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setUpdateIdentifier")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setUpdateIdentifier(identifier), "setUpdateIdentifier");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setUpdateIdentifier");
                return false;
            }
        }
    }

    private boolean setEapMethod(int method) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapMethod")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapMethod(method), "setEapMethod");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapMethod");
                return false;
            }
        }
    }

    private boolean setEapPhase2Method(int method) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapPhase2Method")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapPhase2Method(method), "setEapPhase2Method");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapPhase2Method");
                return false;
            }
        }
    }

    private boolean setEapIdentity(ArrayList<Byte> identity) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapIdentity")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapIdentity(identity), "setEapIdentity");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapIdentity");
                return false;
            }
        }
    }

    private boolean setEapAnonymousIdentity(ArrayList<Byte> identity) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapAnonymousIdentity")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapAnonymousIdentity(identity), "setEapAnonymousIdentity");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapAnonymousIdentity");
                return false;
            }
        }
    }

    private boolean setEapPassword(ArrayList<Byte> password) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapPassword")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapPassword(password), "setEapPassword");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapPassword");
                return false;
            }
        }
    }

    private boolean setEapCACert(String path) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapCACert")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapCACert(path), "setEapCACert");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapCACert");
                return false;
            }
        }
    }

    private boolean setEapCAPath(String path) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapCAPath")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapCAPath(path), "setEapCAPath");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapCAPath");
                return false;
            }
        }
    }

    private boolean setEapClientCert(String path) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapClientCert")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapClientCert(path), "setEapClientCert");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapClientCert");
                return false;
            }
        }
    }

    private boolean setEapPrivateKeyId(String id) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapPrivateKeyId")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapPrivateKeyId(id), "setEapPrivateKeyId");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapPrivateKeyId");
                return false;
            }
        }
    }

    private boolean setEapSubjectMatch(String match) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapSubjectMatch")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapSubjectMatch(match), "setEapSubjectMatch");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapSubjectMatch");
                return false;
            }
        }
    }

    private boolean setEapAltSubjectMatch(String match) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapAltSubjectMatch")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapAltSubjectMatch(match), "setEapAltSubjectMatch");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapAltSubjectMatch");
                return false;
            }
        }
    }

    private boolean setEapEngine(boolean enable) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapEngine")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapEngine(enable), "setEapEngine");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapEngine");
                return false;
            }
        }
    }

    private boolean setEapEngineID(String id) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapEngineID")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapEngineID(id), "setEapEngineID");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapEngineID");
                return false;
            }
        }
    }

    private boolean setEapDomainSuffixMatch(String match) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapDomainSuffixMatch")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setEapDomainSuffixMatch(match), "setEapDomainSuffixMatch");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapDomainSuffixMatch");
                return false;
            }
        }
    }

    private boolean setEapProactiveKeyCaching(boolean enable) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setEapProactiveKeyCaching")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setProactiveKeyCaching(enable), "setEapProactiveKeyCaching");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapProactiveKeyCaching");
                return false;
            }
        }
    }

    private boolean setEapErp(boolean enable) {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setEapErp")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setEapErp(enable), "setEapErp");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setEapErp");
                return false;
            }
        }
    }

    private boolean setDppConnector(String connector) {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setDppConnector")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setDppConnector(connector), "setDppConnector");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setDppConnector");
                return false;
            }
        }
    }

    private boolean setDppNetAccessKey(ArrayList<Byte> netAccessKey) {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setDppNetAccessKey")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setDppNetAccessKey(netAccessKey), "setDppNetAccessKey");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setDppNetAccessKey");
                return false;
            }
        }
    }

    private boolean setDppNetAccessKeyExpiry(int expiry) {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setDppNetAccessKeyExpiry")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setDppNetAccessKeyExpiry(expiry), "setDppNetAccessKeyExpiry");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setDppNetAccessKeyExpiry");
                return false;
            }
        }
    }

    private boolean setDppCsign(ArrayList<Byte> csign) {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setDppCsign")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setDppCsign(csign), "setDppCsign");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setDppCsign");
                return false;
            }
        }
    }

    private boolean setVendorSimNumber(int SimNum) {
        synchronized (this.mLock) {
            if (!checkISupplicantVendorStaNetworkAndLogFailure("setVendorSimNumber")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mISupplicantVendorStaNetwork.setVendorSimNumber(SimNum), "setVendorSimNumber");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setVendorSimNumber");
                return false;
            }
        }
    }

    private boolean setIdStr(String idString) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setIdStr")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.setIdStr(idString), "setIdStr");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setIdStr");
                return false;
            }
        }
    }

    private boolean setSaePassword(String saePassword) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setSaePassword")) {
                return false;
            }
            try {
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork iSupplicantStaNetworkV12 = getV1_2StaNetwork();
                if (iSupplicantStaNetworkV12 == null) {
                    return false;
                }
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(iSupplicantStaNetworkV12.setSaePassword(saePassword), "setSaePassword");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setSaePassword");
                return false;
            }
        }
    }

    private boolean setSaePasswordId(String saePasswordId) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("setSaePasswordId")) {
                return false;
            }
            try {
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork iSupplicantStaNetworkV12 = getV1_2StaNetwork();
                if (iSupplicantStaNetworkV12 == null) {
                    return false;
                }
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(iSupplicantStaNetworkV12.setSaePasswordId(saePasswordId), "setSaePasswordId");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setSaePasswordId");
                return false;
            }
        }
    }

    private boolean getSsid() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getSsid")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getSsid(new ISupplicantStaNetwork.getSsidCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                        SupplicantStaNetworkHal.this.lambda$getSsid$5$SupplicantStaNetworkHal(this.f$1, supplicantStatus, arrayList);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getSsid");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getSsid$5$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, ArrayList ssidValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mSsid = ssidValue;
        } else {
            checkStatusAndLogFailure(status, "getSsid");
        }
    }

    private boolean getBssid() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getBssid")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getBssid(new ISupplicantStaNetwork.getBssidCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, byte[] bArr) {
                        SupplicantStaNetworkHal.this.lambda$getBssid$6$SupplicantStaNetworkHal(this.f$1, supplicantStatus, bArr);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getBssid");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getBssid$6$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, byte[] bssidValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mBssid = bssidValue;
        } else {
            checkStatusAndLogFailure(status, "getBssid");
        }
    }

    private boolean getScanSsid() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getScanSsid")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getScanSsid(new ISupplicantStaNetwork.getScanSsidCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, boolean z) {
                        SupplicantStaNetworkHal.this.lambda$getScanSsid$7$SupplicantStaNetworkHal(this.f$1, supplicantStatus, z);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getScanSsid");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getScanSsid$7$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, boolean enabledValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mScanSsid = enabledValue;
        } else {
            checkStatusAndLogFailure(status, "getScanSsid");
        }
    }

    private boolean getKeyMgmt() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getKeyMgmt")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getKeyMgmt(new ISupplicantStaNetwork.getKeyMgmtCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getKeyMgmt$8$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getKeyMgmt");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getKeyMgmt$8$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int keyMgmtMaskValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mKeyMgmtMask = keyMgmtMaskValue;
        } else {
            checkStatusAndLogFailure(status, "getKeyMgmt");
        }
    }

    private boolean getProto() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getProto")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getProto(new ISupplicantStaNetwork.getProtoCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getProto$9$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getProto");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getProto$9$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int protoMaskValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mProtoMask = protoMaskValue;
        } else {
            checkStatusAndLogFailure(status, "getProto");
        }
    }

    private boolean getAuthAlg() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getAuthAlg")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getAuthAlg(new ISupplicantStaNetwork.getAuthAlgCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getAuthAlg$10$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getAuthAlg");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getAuthAlg$10$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int authAlgMaskValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mAuthAlgMask = authAlgMaskValue;
        } else {
            checkStatusAndLogFailure(status, "getAuthAlg");
        }
    }

    private boolean getGroupCipher() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getGroupCipher")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getGroupCipher(new ISupplicantStaNetwork.getGroupCipherCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getGroupCipher$11$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getGroupCipher");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getGroupCipher$11$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int groupCipherMaskValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mGroupCipherMask = groupCipherMaskValue;
        } else {
            checkStatusAndLogFailure(status, "getGroupCipher");
        }
    }

    private boolean getPairwiseCipher() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getPairwiseCipher")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getPairwiseCipher(new ISupplicantStaNetwork.getPairwiseCipherCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getPairwiseCipher$12$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getPairwiseCipher");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getPairwiseCipher$12$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int pairwiseCipherMaskValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mPairwiseCipherMask = pairwiseCipherMaskValue;
        } else {
            checkStatusAndLogFailure(status, "getPairwiseCipher");
        }
    }

    private boolean getGroupMgmtCipher() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getGroupMgmtCipher")) {
                return false;
            }
            try {
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork iSupplicantStaNetworkV12 = getV1_2StaNetwork();
                if (iSupplicantStaNetworkV12 == null) {
                    return false;
                }
                MutableBoolean statusOk = new MutableBoolean(false);
                iSupplicantStaNetworkV12.getGroupMgmtCipher(new ISupplicantStaNetwork.getGroupMgmtCipherCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getGroupMgmtCipher$13$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getGroupMgmtCipher");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getGroupMgmtCipher$13$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int groupMgmtCipherMaskValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mGroupMgmtCipherMask = groupMgmtCipherMaskValue;
        }
        checkStatusAndLogFailure(status, "getGroupMgmtCipher");
    }

    private boolean getPskPassphrase() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getPskPassphrase")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getPskPassphrase(new ISupplicantStaNetwork.getPskPassphraseCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getPskPassphrase$14$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getPskPassphrase");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getPskPassphrase$14$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String pskValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mPskPassphrase = pskValue;
        } else {
            checkStatusAndLogFailure(status, "getPskPassphrase");
        }
    }

    private boolean getSaePassword() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getSaePassword")) {
                return false;
            }
            try {
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork iSupplicantStaNetworkV12 = getV1_2StaNetwork();
                if (iSupplicantStaNetworkV12 == null) {
                    return false;
                }
                MutableBoolean statusOk = new MutableBoolean(false);
                iSupplicantStaNetworkV12.getSaePassword(new ISupplicantStaNetwork.getSaePasswordCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getSaePassword$15$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getSaePassword");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getSaePassword$15$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String saePassword) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mSaePassword = saePassword;
        }
        checkStatusAndLogFailure(status, "getSaePassword");
    }

    private boolean getPsk() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getPsk")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getPsk(new ISupplicantStaNetwork.getPskCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, byte[] bArr) {
                        SupplicantStaNetworkHal.this.lambda$getPsk$16$SupplicantStaNetworkHal(this.f$1, supplicantStatus, bArr);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getPsk");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getPsk$16$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, byte[] pskValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mPsk = pskValue;
        } else {
            checkStatusAndLogFailure(status, "getPsk");
        }
    }

    private boolean getWepKey(int keyIdx) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("keyIdx")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getWepKey(keyIdx, new ISupplicantStaNetwork.getWepKeyCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                        SupplicantStaNetworkHal.this.lambda$getWepKey$17$SupplicantStaNetworkHal(this.f$1, supplicantStatus, arrayList);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "keyIdx");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getWepKey$17$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, ArrayList wepKeyValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mWepKey = wepKeyValue;
            return;
        }
        Log.e(TAG, "keyIdx,  failed: " + status.debugMessage);
    }

    private boolean getWepTxKeyIdx() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getWepTxKeyIdx")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getWepTxKeyIdx(new ISupplicantStaNetwork.getWepTxKeyIdxCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getWepTxKeyIdx$18$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getWepTxKeyIdx");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getWepTxKeyIdx$18$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int keyIdxValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mWepTxKeyIdx = keyIdxValue;
        } else {
            checkStatusAndLogFailure(status, "getWepTxKeyIdx");
        }
    }

    private boolean getRequirePmf() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getRequirePmf")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getRequirePmf(new ISupplicantStaNetwork.getRequirePmfCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, boolean z) {
                        SupplicantStaNetworkHal.this.lambda$getRequirePmf$19$SupplicantStaNetworkHal(this.f$1, supplicantStatus, z);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getRequirePmf");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getRequirePmf$19$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, boolean enabledValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mRequirePmf = enabledValue;
        } else {
            checkStatusAndLogFailure(status, "getRequirePmf");
        }
    }

    private boolean getEapMethod() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapMethod")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapMethod(new ISupplicantStaNetwork.getEapMethodCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getEapMethod$20$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapMethod");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapMethod$20$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int methodValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapMethod = methodValue;
        } else {
            checkStatusAndLogFailure(status, "getEapMethod");
        }
    }

    private boolean getEapPhase2Method() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapPhase2Method")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapPhase2Method(new ISupplicantStaNetwork.getEapPhase2MethodCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, int i) {
                        SupplicantStaNetworkHal.this.lambda$getEapPhase2Method$21$SupplicantStaNetworkHal(this.f$1, supplicantStatus, i);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapPhase2Method");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapPhase2Method$21$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, int methodValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapPhase2Method = methodValue;
        } else {
            checkStatusAndLogFailure(status, "getEapPhase2Method");
        }
    }

    private boolean getEapIdentity() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapIdentity")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapIdentity(new ISupplicantStaNetwork.getEapIdentityCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                        SupplicantStaNetworkHal.this.lambda$getEapIdentity$22$SupplicantStaNetworkHal(this.f$1, supplicantStatus, arrayList);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapIdentity");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapIdentity$22$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, ArrayList identityValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapIdentity = identityValue;
        } else {
            checkStatusAndLogFailure(status, "getEapIdentity");
        }
    }

    private boolean getEapAnonymousIdentity() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapAnonymousIdentity")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapAnonymousIdentity(new ISupplicantStaNetwork.getEapAnonymousIdentityCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                        SupplicantStaNetworkHal.this.lambda$getEapAnonymousIdentity$23$SupplicantStaNetworkHal(this.f$1, supplicantStatus, arrayList);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapAnonymousIdentity");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapAnonymousIdentity$23$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, ArrayList identityValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapAnonymousIdentity = identityValue;
        } else {
            checkStatusAndLogFailure(status, "getEapAnonymousIdentity");
        }
    }

    public String fetchEapAnonymousIdentity() {
        synchronized (this.mLock) {
            if (!getEapAnonymousIdentity()) {
                return null;
            }
            String stringFromByteArrayList = NativeUtil.stringFromByteArrayList(this.mEapAnonymousIdentity);
            return stringFromByteArrayList;
        }
    }

    private boolean getEapPassword() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapPassword")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapPassword(new ISupplicantStaNetwork.getEapPasswordCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                        SupplicantStaNetworkHal.this.lambda$getEapPassword$24$SupplicantStaNetworkHal(this.f$1, supplicantStatus, arrayList);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapPassword");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapPassword$24$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, ArrayList passwordValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapPassword = passwordValue;
        } else {
            checkStatusAndLogFailure(status, "getEapPassword");
        }
    }

    private boolean getEapCACert() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapCACert")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapCACert(new ISupplicantStaNetwork.getEapCACertCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getEapCACert$25$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapCACert");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapCACert$25$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String pathValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapCACert = pathValue;
        } else {
            checkStatusAndLogFailure(status, "getEapCACert");
        }
    }

    private boolean getEapCAPath() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapCAPath")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapCAPath(new ISupplicantStaNetwork.getEapCAPathCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getEapCAPath$26$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapCAPath");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapCAPath$26$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String pathValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapCAPath = pathValue;
        } else {
            checkStatusAndLogFailure(status, "getEapCAPath");
        }
    }

    private boolean getEapClientCert() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapClientCert")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapClientCert(new ISupplicantStaNetwork.getEapClientCertCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getEapClientCert$27$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapClientCert");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapClientCert$27$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String pathValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapClientCert = pathValue;
        } else {
            checkStatusAndLogFailure(status, "getEapClientCert");
        }
    }

    private boolean getEapPrivateKeyId() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapPrivateKeyId")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapPrivateKeyId(new ISupplicantStaNetwork.getEapPrivateKeyIdCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getEapPrivateKeyId$28$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapPrivateKeyId");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapPrivateKeyId$28$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String idValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapPrivateKeyId = idValue;
        } else {
            checkStatusAndLogFailure(status, "getEapPrivateKeyId");
        }
    }

    private boolean getEapSubjectMatch() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapSubjectMatch")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapSubjectMatch(new ISupplicantStaNetwork.getEapSubjectMatchCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getEapSubjectMatch$29$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapSubjectMatch");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapSubjectMatch$29$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String matchValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapSubjectMatch = matchValue;
        } else {
            checkStatusAndLogFailure(status, "getEapSubjectMatch");
        }
    }

    private boolean getEapAltSubjectMatch() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapAltSubjectMatch")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapAltSubjectMatch(new ISupplicantStaNetwork.getEapAltSubjectMatchCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getEapAltSubjectMatch$30$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapAltSubjectMatch");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapAltSubjectMatch$30$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String matchValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapAltSubjectMatch = matchValue;
        } else {
            checkStatusAndLogFailure(status, "getEapAltSubjectMatch");
        }
    }

    private boolean getEapEngine() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapEngine")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapEngine(new ISupplicantStaNetwork.getEapEngineCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, boolean z) {
                        SupplicantStaNetworkHal.this.lambda$getEapEngine$31$SupplicantStaNetworkHal(this.f$1, supplicantStatus, z);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapEngine");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapEngine$31$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, boolean enabledValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapEngine = enabledValue;
        } else {
            checkStatusAndLogFailure(status, "getEapEngine");
        }
    }

    private boolean getEapEngineID() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapEngineID")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapEngineID(new ISupplicantStaNetwork.getEapEngineIDCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getEapEngineID$32$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapEngineID");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapEngineID$32$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String idValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapEngineID = idValue;
        } else {
            checkStatusAndLogFailure(status, "getEapEngineID");
        }
    }

    private boolean getEapDomainSuffixMatch() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getEapDomainSuffixMatch")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getEapDomainSuffixMatch(new ISupplicantStaNetwork.getEapDomainSuffixMatchCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getEapDomainSuffixMatch$33$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getEapDomainSuffixMatch");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getEapDomainSuffixMatch$33$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String matchValue) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mEapDomainSuffixMatch = matchValue;
        } else {
            checkStatusAndLogFailure(status, "getEapDomainSuffixMatch");
        }
    }

    private boolean getIdStr() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getIdStr")) {
                return false;
            }
            try {
                MutableBoolean statusOk = new MutableBoolean(false);
                this.mISupplicantStaNetwork.getIdStr(new ISupplicantStaNetwork.getIdStrCallback(statusOk) {
                    private final /* synthetic */ MutableBoolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantStaNetworkHal.this.lambda$getIdStr$34$SupplicantStaNetworkHal(this.f$1, supplicantStatus, str);
                    }
                });
                boolean z = statusOk.value;
                return z;
            } catch (RemoteException e) {
                handleRemoteException(e, "getIdStr");
                return false;
            }
        }
    }

    public /* synthetic */ void lambda$getIdStr$34$SupplicantStaNetworkHal(MutableBoolean statusOk, SupplicantStatus status, String idString) {
        statusOk.value = status.code == 0;
        if (statusOk.value) {
            this.mIdStr = idString;
        } else {
            checkStatusAndLogFailure(status, "getIdStr");
        }
    }

    private boolean enable(boolean noConnect) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("enable")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.enable(noConnect), "enable");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "enable");
                return false;
            }
        }
    }

    private boolean disable() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("disable")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.disable(), "disable");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "disable");
                return false;
            }
        }
    }

    public boolean select() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("select")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.select(), "select");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "select");
                return false;
            }
        }
    }

    public boolean sendNetworkEapSimGsmAuthResponse(String paramsStr) {
        synchronized (this.mLock) {
            try {
                Matcher match = GSM_AUTH_RESPONSE_PARAMS_PATTERN.matcher(paramsStr);
                ArrayList<ISupplicantStaNetwork.NetworkResponseEapSimGsmAuthParams> params = new ArrayList<>();
                while (match.find()) {
                    if (match.groupCount() != 2) {
                        Log.e(TAG, "Malformed gsm auth response params: " + paramsStr);
                        return false;
                    }
                    ISupplicantStaNetwork.NetworkResponseEapSimGsmAuthParams param = new ISupplicantStaNetwork.NetworkResponseEapSimGsmAuthParams();
                    byte[] kc = NativeUtil.hexStringToByteArray(match.group(1));
                    if (kc != null) {
                        if (kc.length == param.kc.length) {
                            byte[] sres = NativeUtil.hexStringToByteArray(match.group(2));
                            if (sres != null) {
                                if (sres.length == param.sres.length) {
                                    System.arraycopy(kc, 0, param.kc, 0, param.kc.length);
                                    System.arraycopy(sres, 0, param.sres, 0, param.sres.length);
                                    params.add(param);
                                }
                            }
                            Log.e(TAG, "Invalid sres value: " + match.group(2));
                            return false;
                        }
                    }
                    Log.e(TAG, "Invalid kc value: " + match.group(1));
                    return false;
                }
                if (params.size() <= 3) {
                    if (params.size() >= 2) {
                        boolean sendNetworkEapSimGsmAuthResponse = sendNetworkEapSimGsmAuthResponse(params);
                        return sendNetworkEapSimGsmAuthResponse;
                    }
                }
                Log.e(TAG, "Malformed gsm auth response params: " + paramsStr);
                return false;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Illegal argument " + paramsStr, e);
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private boolean sendNetworkEapSimGsmAuthResponse(ArrayList<ISupplicantStaNetwork.NetworkResponseEapSimGsmAuthParams> params) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("sendNetworkEapSimGsmAuthResponse")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.sendNetworkEapSimGsmAuthResponse(params), "sendNetworkEapSimGsmAuthResponse");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "sendNetworkEapSimGsmAuthResponse");
                return false;
            }
        }
    }

    public boolean sendNetworkEapSimGsmAuthFailure() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("sendNetworkEapSimGsmAuthFailure")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.sendNetworkEapSimGsmAuthFailure(), "sendNetworkEapSimGsmAuthFailure");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "sendNetworkEapSimGsmAuthFailure");
                return false;
            }
        }
    }

    public boolean sendNetworkEapSimUmtsAuthResponse(String paramsStr) {
        synchronized (this.mLock) {
            try {
                Matcher match = UMTS_AUTH_RESPONSE_PARAMS_PATTERN.matcher(paramsStr);
                if (match.find()) {
                    if (match.groupCount() == 3) {
                        ISupplicantStaNetwork.NetworkResponseEapSimUmtsAuthParams params = new ISupplicantStaNetwork.NetworkResponseEapSimUmtsAuthParams();
                        byte[] ik = NativeUtil.hexStringToByteArray(match.group(1));
                        if (ik != null) {
                            if (ik.length == params.ik.length) {
                                byte[] ck = NativeUtil.hexStringToByteArray(match.group(2));
                                if (ck != null) {
                                    if (ck.length == params.ck.length) {
                                        byte[] res = NativeUtil.hexStringToByteArray(match.group(3));
                                        if (res != null) {
                                            if (res.length != 0) {
                                                System.arraycopy(ik, 0, params.ik, 0, params.ik.length);
                                                System.arraycopy(ck, 0, params.ck, 0, params.ck.length);
                                                for (byte b : res) {
                                                    params.res.add(Byte.valueOf(b));
                                                }
                                                boolean sendNetworkEapSimUmtsAuthResponse = sendNetworkEapSimUmtsAuthResponse(params);
                                                return sendNetworkEapSimUmtsAuthResponse;
                                            }
                                        }
                                        Log.e(TAG, "Invalid res value: " + match.group(3));
                                        return false;
                                    }
                                }
                                Log.e(TAG, "Invalid ck value: " + match.group(2));
                                return false;
                            }
                        }
                        Log.e(TAG, "Invalid ik value: " + match.group(1));
                        return false;
                    }
                }
                Log.e(TAG, "Malformed umts auth response params: " + paramsStr);
                return false;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Illegal argument " + paramsStr, e);
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private boolean sendNetworkEapSimUmtsAuthResponse(ISupplicantStaNetwork.NetworkResponseEapSimUmtsAuthParams params) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("sendNetworkEapSimUmtsAuthResponse")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.sendNetworkEapSimUmtsAuthResponse(params), "sendNetworkEapSimUmtsAuthResponse");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "sendNetworkEapSimUmtsAuthResponse");
                return false;
            }
        }
    }

    public boolean sendNetworkEapSimUmtsAutsResponse(String paramsStr) {
        synchronized (this.mLock) {
            try {
                Matcher match = UMTS_AUTS_RESPONSE_PARAMS_PATTERN.matcher(paramsStr);
                if (match.find()) {
                    if (match.groupCount() == 1) {
                        byte[] auts = NativeUtil.hexStringToByteArray(match.group(1));
                        if (auts != null) {
                            if (auts.length == 14) {
                                boolean sendNetworkEapSimUmtsAutsResponse = sendNetworkEapSimUmtsAutsResponse(auts);
                                return sendNetworkEapSimUmtsAutsResponse;
                            }
                        }
                        Log.e(TAG, "Invalid auts value: " + match.group(1));
                        return false;
                    }
                }
                Log.e(TAG, "Malformed umts auts response params: " + paramsStr);
                return false;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Illegal argument " + paramsStr, e);
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private boolean sendNetworkEapSimUmtsAutsResponse(byte[] auts) {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("sendNetworkEapSimUmtsAutsResponse")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.sendNetworkEapSimUmtsAutsResponse(auts), "sendNetworkEapSimUmtsAutsResponse");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "sendNetworkEapSimUmtsAutsResponse");
                return false;
            }
        }
    }

    public boolean sendNetworkEapSimUmtsAuthFailure() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("sendNetworkEapSimUmtsAuthFailure")) {
                return false;
            }
            try {
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(this.mISupplicantStaNetwork.sendNetworkEapSimUmtsAuthFailure(), "sendNetworkEapSimUmtsAuthFailure");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "sendNetworkEapSimUmtsAuthFailure");
                return false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public android.hardware.wifi.supplicant.V1_1.ISupplicantStaNetwork getSupplicantStaNetworkForV1_1Mockable() {
        android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork iSupplicantStaNetwork = this.mISupplicantStaNetwork;
        if (iSupplicantStaNetwork == null) {
            return null;
        }
        return android.hardware.wifi.supplicant.V1_1.ISupplicantStaNetwork.castFrom(iSupplicantStaNetwork);
    }

    /* access modifiers changed from: protected */
    public android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork getSupplicantStaNetworkForV1_2Mockable() {
        android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork iSupplicantStaNetwork = this.mISupplicantStaNetwork;
        if (iSupplicantStaNetwork == null) {
            return null;
        }
        return android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork.castFrom(iSupplicantStaNetwork);
    }

    public boolean sendNetworkEapIdentityResponse(String identityStr, String encryptedIdentityStr) {
        boolean sendNetworkEapIdentityResponse;
        synchronized (this.mLock) {
            try {
                ArrayList<Byte> unencryptedIdentity = NativeUtil.stringToByteArrayList(identityStr);
                ArrayList<Byte> encryptedIdentity = null;
                if (!TextUtils.isEmpty(encryptedIdentityStr)) {
                    encryptedIdentity = NativeUtil.stringToByteArrayList(encryptedIdentityStr);
                }
                sendNetworkEapIdentityResponse = sendNetworkEapIdentityResponse(unencryptedIdentity, encryptedIdentity);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Illegal argument " + identityStr + "," + encryptedIdentityStr, e);
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
        return sendNetworkEapIdentityResponse;
    }

    private boolean sendNetworkEapIdentityResponse(ArrayList<Byte> unencryptedIdentity, ArrayList<Byte> encryptedIdentity) {
        SupplicantStatus status;
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("sendNetworkEapIdentityResponse")) {
                return false;
            }
            try {
                android.hardware.wifi.supplicant.V1_1.ISupplicantStaNetwork iSupplicantStaNetworkV11 = getSupplicantStaNetworkForV1_1Mockable();
                if (iSupplicantStaNetworkV11 == null || encryptedIdentity == null) {
                    status = this.mISupplicantStaNetwork.sendNetworkEapIdentityResponse(unencryptedIdentity);
                } else {
                    status = iSupplicantStaNetworkV11.sendNetworkEapIdentityResponse_1_1(unencryptedIdentity, encryptedIdentity);
                }
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(status, "sendNetworkEapIdentityResponse");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "sendNetworkEapIdentityResponse");
                return false;
            }
        }
    }

    public String getWpsNfcConfigurationToken() {
        synchronized (this.mLock) {
            ArrayList<Byte> token = getWpsNfcConfigurationTokenInternal();
            if (token == null) {
                return null;
            }
            String hexStringFromByteArray = NativeUtil.hexStringFromByteArray(NativeUtil.byteArrayFromArrayList(token));
            return hexStringFromByteArray;
        }
    }

    private ArrayList<Byte> getWpsNfcConfigurationTokenInternal() {
        synchronized (this.mLock) {
            if (!checkISupplicantStaNetworkAndLogFailure("getWpsNfcConfigurationToken")) {
                return null;
            }
            HidlSupport.Mutable<ArrayList<Byte>> gotToken = new HidlSupport.Mutable<>();
            try {
                this.mISupplicantStaNetwork.getWpsNfcConfigurationToken(new ISupplicantStaNetwork.getWpsNfcConfigurationTokenCallback(gotToken) {
                    private final /* synthetic */ HidlSupport.Mutable f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                        SupplicantStaNetworkHal.this.lambda$getWpsNfcConfigurationTokenInternal$35$SupplicantStaNetworkHal(this.f$1, supplicantStatus, arrayList);
                    }
                });
            } catch (RemoteException e) {
                handleRemoteException(e, "getWpsNfcConfigurationToken");
            }
            ArrayList<Byte> arrayList = (ArrayList) gotToken.value;
            return arrayList;
        }
    }

    public /* synthetic */ void lambda$getWpsNfcConfigurationTokenInternal$35$SupplicantStaNetworkHal(HidlSupport.Mutable gotToken, SupplicantStatus status, ArrayList token) {
        if (checkStatusAndLogFailure(status, "getWpsNfcConfigurationToken")) {
            gotToken.value = token;
        }
    }

    private boolean checkStatusAndLogFailure(SupplicantStatus status, String methodStr) {
        synchronized (this.mLock) {
            if (status.code != 0) {
                Log.e(TAG, "ISupplicantStaNetwork." + methodStr + " failed: " + status);
                return false;
            }
            if (this.mVerboseLoggingEnabled) {
                Log.d(TAG, "ISupplicantStaNetwork." + methodStr + " succeeded");
            }
            return true;
        }
    }

    private boolean checkVendorStatusAndLogFailure(SupplicantStatus status, String methodStr) {
        synchronized (this.mLock) {
            if (status.code != 0) {
                Log.e(TAG, "ISupplicantVendorStaNetwork." + methodStr + " failed: " + status);
                return false;
            }
            if (this.mVerboseLoggingEnabled) {
                Log.d(TAG, "ISupplicantVendorStaNetwork." + methodStr + " succeeded");
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void logCallback(String methodStr) {
        synchronized (this.mLock) {
            if (this.mVerboseLoggingEnabled) {
                Log.d(TAG, "ISupplicantStaNetworkCallback." + methodStr + " received");
            }
        }
    }

    private boolean checkISupplicantStaNetworkAndLogFailure(String methodStr) {
        synchronized (this.mLock) {
            if (this.mISupplicantStaNetwork != null) {
                return true;
            }
            Log.e(TAG, "Can't call " + methodStr + ", ISupplicantStaNetwork is null");
            return false;
        }
    }

    private boolean checkISupplicantVendorStaNetworkAndLogFailure(String methodStr) {
        synchronized (this.mLock) {
            if (this.mISupplicantVendorStaNetwork != null) {
                return true;
            }
            Log.e(TAG, "Can't call " + methodStr + ", ISupplicantVendorStaNetwork is null");
            return false;
        }
    }

    private void handleRemoteException(RemoteException e, String methodStr) {
        synchronized (this.mLock) {
            this.mISupplicantStaNetwork = null;
            Log.e(TAG, "ISupplicantStaNetwork." + methodStr + " failed with exception", e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0026, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.BitSet addFastTransitionFlags(java.util.BitSet r4) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            boolean r1 = r3.mSystemSupportsFastBssTransition     // Catch:{ all -> 0x0027 }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            return r4
        L_0x0009:
            java.lang.Object r1 = r4.clone()     // Catch:{ all -> 0x0027 }
            java.util.BitSet r1 = (java.util.BitSet) r1     // Catch:{ all -> 0x0027 }
            r2 = 1
            boolean r2 = r4.get(r2)     // Catch:{ all -> 0x0027 }
            if (r2 == 0) goto L_0x001a
            r2 = 6
            r1.set(r2)     // Catch:{ all -> 0x0027 }
        L_0x001a:
            r2 = 2
            boolean r2 = r4.get(r2)     // Catch:{ all -> 0x0027 }
            if (r2 == 0) goto L_0x0025
            r2 = 7
            r1.set(r2)     // Catch:{ all -> 0x0027 }
        L_0x0025:
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            return r1
        L_0x0027:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.SupplicantStaNetworkHal.addFastTransitionFlags(java.util.BitSet):java.util.BitSet");
    }

    private BitSet removeFastTransitionFlags(BitSet keyManagementFlags) {
        BitSet modifiedFlags;
        synchronized (this.mLock) {
            modifiedFlags = (BitSet) keyManagementFlags.clone();
            modifiedFlags.clear(6);
            modifiedFlags.clear(7);
        }
        return modifiedFlags;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002a, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.BitSet addSha256KeyMgmtFlags(java.util.BitSet r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            java.lang.Object r1 = r5.clone()     // Catch:{ all -> 0x002b }
            java.util.BitSet r1 = (java.util.BitSet) r1     // Catch:{ all -> 0x002b }
            android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork r2 = r4.getV1_2StaNetwork()     // Catch:{ all -> 0x002b }
            if (r2 != 0) goto L_0x0011
            monitor-exit(r0)     // Catch:{ all -> 0x002b }
            return r1
        L_0x0011:
            r3 = 1
            boolean r3 = r5.get(r3)     // Catch:{ all -> 0x002b }
            if (r3 == 0) goto L_0x001d
            r3 = 11
            r1.set(r3)     // Catch:{ all -> 0x002b }
        L_0x001d:
            r3 = 2
            boolean r3 = r5.get(r3)     // Catch:{ all -> 0x002b }
            if (r3 == 0) goto L_0x0029
            r3 = 12
            r1.set(r3)     // Catch:{ all -> 0x002b }
        L_0x0029:
            monitor-exit(r0)     // Catch:{ all -> 0x002b }
            return r1
        L_0x002b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002b }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.SupplicantStaNetworkHal.addSha256KeyMgmtFlags(java.util.BitSet):java.util.BitSet");
    }

    private BitSet removeSha256KeyMgmtFlags(BitSet keyManagementFlags) {
        BitSet modifiedFlags;
        synchronized (this.mLock) {
            modifiedFlags = (BitSet) keyManagementFlags.clone();
            modifiedFlags.clear(11);
            modifiedFlags.clear(12);
        }
        return modifiedFlags;
    }

    public static String createNetworkExtra(Map<String, String> values) {
        try {
            return URLEncoder.encode(new JSONObject(values).toString(), "UTF-8");
        } catch (NullPointerException e) {
            Log.e(TAG, "Unable to serialize networkExtra: " + e.toString());
            return null;
        } catch (UnsupportedEncodingException e2) {
            Log.e(TAG, "Unable to serialize networkExtra: " + e2.toString());
            return null;
        }
    }

    public static Map<String, String> parseNetworkExtra(String encoded) {
        if (TextUtils.isEmpty(encoded)) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(URLDecoder.decode(encoded, "UTF-8"));
            Map<String, String> values = new HashMap<>();
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = json.get(key);
                if (value instanceof String) {
                    values.put(key, (String) value);
                }
            }
            return values;
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Unable to deserialize networkExtra: " + e.toString());
            return null;
        } catch (JSONException e2) {
            return null;
        }
    }

    private class SupplicantStaNetworkHalCallback extends ISupplicantStaNetworkCallback.Stub {
        private final int mFramewokNetworkId;
        private final String mSsid;

        SupplicantStaNetworkHalCallback(int framewokNetworkId, String ssid) {
            this.mFramewokNetworkId = framewokNetworkId;
            this.mSsid = ssid;
        }

        public void onNetworkEapSimGsmAuthRequest(ISupplicantStaNetworkCallback.NetworkRequestEapSimGsmAuthParams params) {
            synchronized (SupplicantStaNetworkHal.this.mLock) {
                SupplicantStaNetworkHal.this.logCallback("onNetworkEapSimGsmAuthRequest");
                String[] data = new String[params.rands.size()];
                int i = 0;
                Iterator<byte[]> it = params.rands.iterator();
                while (it.hasNext()) {
                    data[i] = NativeUtil.hexStringFromByteArray(it.next());
                    i++;
                }
                SupplicantStaNetworkHal.this.mWifiMonitor.broadcastNetworkGsmAuthRequestEvent(SupplicantStaNetworkHal.this.mIfaceName, this.mFramewokNetworkId, this.mSsid, data);
            }
        }

        public void onNetworkEapSimUmtsAuthRequest(ISupplicantStaNetworkCallback.NetworkRequestEapSimUmtsAuthParams params) {
            synchronized (SupplicantStaNetworkHal.this.mLock) {
                SupplicantStaNetworkHal.this.logCallback("onNetworkEapSimUmtsAuthRequest");
                SupplicantStaNetworkHal.this.mWifiMonitor.broadcastNetworkUmtsAuthRequestEvent(SupplicantStaNetworkHal.this.mIfaceName, this.mFramewokNetworkId, this.mSsid, new String[]{NativeUtil.hexStringFromByteArray(params.rand), NativeUtil.hexStringFromByteArray(params.autn)});
            }
        }

        public void onNetworkEapIdentityRequest() {
            synchronized (SupplicantStaNetworkHal.this.mLock) {
                SupplicantStaNetworkHal.this.logCallback("onNetworkEapIdentityRequest");
                SupplicantStaNetworkHal.this.mWifiMonitor.broadcastNetworkIdentityRequestEvent(SupplicantStaNetworkHal.this.mIfaceName, this.mFramewokNetworkId, this.mSsid);
            }
        }
    }
}

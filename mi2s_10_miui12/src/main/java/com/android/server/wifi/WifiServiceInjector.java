package com.android.server.wifi;

import android.content.Context;
import android.location.LocationPolicyManager;
import android.net.wifi.WifiConfiguration;
import android.os.Binder;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class WifiServiceInjector {
    private static final String SUPPLICANT_CONFIG_FILE = "/data/misc/wifi/wpa_supplicant.conf";
    private static final String TAG = "WifiServiceInjector";
    private static final String WIFI_CONFIG_HEADER = "network={";

    public static boolean CheckIfBackgroundScanAllowed(Context ctx, WorkSource workSource) {
        int realOwner = workSource != null ? workSource.get(0) : Binder.getCallingUid();
        if (!UserHandle.isApp(realOwner)) {
            return true;
        }
        try {
            ctx.enforceCallingPermission("android.permission.ACCESS_COARSE_LOCATION", (String) null);
            return LocationPolicyManager.isAllowedByLocationPolicy(ctx, realOwner, 2);
        } catch (SecurityException e) {
            return true;
        }
    }

    public static void handleClientMessage(Message msg) {
        String wifiConfig;
        if (msg.what == 155553) {
            if (Binder.getCallingUid() != 1000) {
                replyToMessage(msg, 2, (Object) null);
            }
            WifiConfiguration config = msg.obj != null ? (WifiConfiguration) msg.obj : null;
            if (config == null || (wifiConfig = readWifiConfigFromSupplicantFile(config.SSID, parseKeyMgmt(config.allowedKeyManagement, WifiConfiguration.KeyMgmt.strings))) == null) {
                replyToMessage(msg, 2, (Object) null);
            } else {
                replyToMessage(msg, 1, Bundle.forPair("config", wifiConfig));
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:67:0x0125 A[SYNTHETIC, Splitter:B:67:0x0125] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0142 A[SYNTHETIC, Splitter:B:74:0x0142] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0148  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x014d A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0153 A[SYNTHETIC, Splitter:B:81:0x0153] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:71:0x012c=Splitter:B:71:0x012c, B:64:0x010f=Splitter:B:64:0x010f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String readWifiConfigFromSupplicantFile(java.lang.String r15, java.lang.String r16) {
        /*
            java.lang.String r0 = "ssid="
            java.lang.String r1 = "\""
            java.lang.String r2 = "WifiServiceInjector"
            r3 = 0
            r4 = 0
            java.io.BufferedReader r5 = new java.io.BufferedReader     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            java.io.FileReader r6 = new java.io.FileReader     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            java.lang.String r7 = "/data/misc/wifi/wpa_supplicant.conf"
            r6.<init>(r7)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r5.<init>(r6)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r4 = r5
            r5 = 0
            java.lang.String r6 = removeDoubleQuotes(r15)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r7.<init>()     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r7.append(r1)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r7.append(r6)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r7.append(r1)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            java.lang.String r1 = r7.toString()     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            java.lang.String r7 = r4.readLine()     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
        L_0x0031:
            if (r7 == 0) goto L_0x00fe
            java.lang.String r8 = "[ \\t]*network=\\{"
            boolean r8 = r7.matches(r8)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            if (r8 == 0) goto L_0x003c
            r5 = 1
        L_0x003c:
            if (r5 == 0) goto L_0x00f1
            java.lang.String r8 = r7.trim()     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r9 = 0
            boolean r10 = r8.startsWith(r0)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            java.lang.String r11 = "\n"
            if (r10 == 0) goto L_0x00b5
            r10 = 5
            java.lang.String r10 = r8.substring(r10)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            int r12 = r10.length()     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r13 = 1
            if (r12 <= r13) goto L_0x0073
            r12 = 0
            char r12 = r10.charAt(r12)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r14 = 34
            if (r12 != r14) goto L_0x0073
            int r12 = r10.length()     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            int r12 = r12 - r13
            char r12 = r10.charAt(r12)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            if (r12 != r14) goto L_0x0073
            boolean r12 = r1.equals(r10)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            if (r12 == 0) goto L_0x0094
            r9 = 1
            goto L_0x0094
        L_0x0073:
            boolean r12 = isUTF8(r10)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            if (r12 == 0) goto L_0x0083
            java.lang.String r12 = encodeUtf8SSID(r6)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            boolean r12 = r12.equals(r10)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            if (r12 != 0) goto L_0x0093
        L_0x0083:
            boolean r12 = isGBK(r10)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            if (r12 == 0) goto L_0x0094
            java.lang.String r12 = encodeGbkSSID(r6)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            boolean r12 = r12.equals(r10)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            if (r12 == 0) goto L_0x0094
        L_0x0093:
            r9 = 1
        L_0x0094:
            if (r9 == 0) goto L_0x00b4
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            java.lang.String r13 = "network={\n"
            r12.<init>(r13)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r3 = r12
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r12.<init>()     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r12.append(r0)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r12.append(r1)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r12.append(r11)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            java.lang.String r11 = r12.toString()     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r3.append(r11)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
        L_0x00b4:
            goto L_0x00c9
        L_0x00b5:
            if (r3 == 0) goto L_0x00b4
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r10.<init>()     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r10.append(r7)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r10.append(r11)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            java.lang.String r10 = r10.toString()     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r3.append(r10)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
        L_0x00c9:
            java.lang.String r10 = "key_mgmt="
            boolean r10 = r8.startsWith(r10)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            if (r10 == 0) goto L_0x00e4
            if (r3 == 0) goto L_0x00e4
            r10 = 9
            java.lang.String r10 = r8.substring(r10)     // Catch:{ FileNotFoundException -> 0x0129, IOException -> 0x010c, all -> 0x0107 }
            r11 = r16
            boolean r10 = r10.contains(r11)     // Catch:{ FileNotFoundException -> 0x00fc, IOException -> 0x00fa }
            if (r10 != 0) goto L_0x00e6
            r3 = 0
            goto L_0x00e6
        L_0x00e4:
            r11 = r16
        L_0x00e6:
            java.lang.String r10 = "[ \\t]*\\}"
            boolean r10 = r7.matches(r10)     // Catch:{ FileNotFoundException -> 0x00fc, IOException -> 0x00fa }
            if (r10 == 0) goto L_0x00f3
            if (r3 == 0) goto L_0x00f3
            goto L_0x0100
        L_0x00f1:
            r11 = r16
        L_0x00f3:
            java.lang.String r8 = r4.readLine()     // Catch:{ FileNotFoundException -> 0x00fc, IOException -> 0x00fa }
            r7 = r8
            goto L_0x0031
        L_0x00fa:
            r0 = move-exception
            goto L_0x010f
        L_0x00fc:
            r0 = move-exception
            goto L_0x012c
        L_0x00fe:
            r11 = r16
        L_0x0100:
            r4.close()     // Catch:{ IOException -> 0x0105 }
        L_0x0104:
            goto L_0x0146
        L_0x0105:
            r0 = move-exception
            goto L_0x0146
        L_0x0107:
            r0 = move-exception
            r11 = r16
        L_0x010a:
            r1 = r0
            goto L_0x0151
        L_0x010c:
            r0 = move-exception
            r11 = r16
        L_0x010f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x014f }
            r1.<init>()     // Catch:{ all -> 0x014f }
            java.lang.String r5 = "Could not read /data/misc/wifi/wpa_supplicant.conf, "
            r1.append(r5)     // Catch:{ all -> 0x014f }
            r1.append(r0)     // Catch:{ all -> 0x014f }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x014f }
            android.util.Log.e(r2, r1)     // Catch:{ all -> 0x014f }
            if (r4 == 0) goto L_0x0104
            r4.close()     // Catch:{ IOException -> 0x0105 }
            goto L_0x0104
        L_0x0129:
            r0 = move-exception
            r11 = r16
        L_0x012c:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x014f }
            r1.<init>()     // Catch:{ all -> 0x014f }
            java.lang.String r5 = "Could not open /data/misc/wifi/wpa_supplicant.conf, "
            r1.append(r5)     // Catch:{ all -> 0x014f }
            r1.append(r0)     // Catch:{ all -> 0x014f }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x014f }
            android.util.Log.e(r2, r1)     // Catch:{ all -> 0x014f }
            if (r4 == 0) goto L_0x0104
            r4.close()     // Catch:{ IOException -> 0x0105 }
            goto L_0x0104
        L_0x0146:
            if (r3 == 0) goto L_0x014d
            java.lang.String r0 = r3.toString()
            goto L_0x014e
        L_0x014d:
            r0 = 0
        L_0x014e:
            return r0
        L_0x014f:
            r0 = move-exception
            goto L_0x010a
        L_0x0151:
            if (r4 == 0) goto L_0x0159
            r4.close()     // Catch:{ IOException -> 0x0157 }
            goto L_0x0159
        L_0x0157:
            r0 = move-exception
            goto L_0x015a
        L_0x0159:
        L_0x015a:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiServiceInjector.readWifiConfigFromSupplicantFile(java.lang.String, java.lang.String):java.lang.String");
    }

    private static void replyToMessage(Message message, int arg1, Object obj) {
        try {
            Message reply = Message.obtain();
            reply.what = message.what;
            reply.arg1 = arg1;
            reply.obj = obj;
            message.replyTo.send(reply);
        } catch (RemoteException e) {
            Log.d(TAG, "replyToMessage Failed");
        }
    }

    private static String encodeUtf8SSID(String ssid) {
        try {
            return toHex(ssid.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encodeUtf8 to hex failed when read wifi data from wpa_supplicant" + e);
            return "";
        }
    }

    private static String encodeGbkSSID(String ssid) {
        try {
            return toHex(ssid.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encodeGbk to hex failed when read wifi data from wpa_supplicant" + e);
            return "";
        }
    }

    private static String toHex(byte[] octets) {
        StringBuilder sb = new StringBuilder(octets.length * 2);
        int length = octets.length;
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x", new Object[]{Integer.valueOf(octets[i] & 255)}));
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String text) {
        if ((text.length() & 1) != 1) {
            byte[] data = new byte[(text.length() >> 1)];
            int position = 0;
            for (int n = 0; n < text.length(); n += 2) {
                data[position] = (byte) (((fromHex(text.charAt(n), false) & 15) << 4) | (fromHex(text.charAt(n + 1), false) & 15));
                position++;
            }
            return data;
        }
        throw new NumberFormatException("Odd length hex string: " + text.length());
    }

    private static int fromHex(char ch, boolean lenient) throws NumberFormatException {
        if (ch <= '9' && ch >= '0') {
            return ch - '0';
        }
        if (ch >= 'a' && ch <= 'f') {
            return (ch + 10) - 97;
        }
        if (ch <= 'F' && ch >= 'A') {
            return (ch + 10) - 65;
        }
        if (lenient) {
            return -1;
        }
        throw new NumberFormatException("Bad hex-character: " + ch);
    }

    private static boolean isGBK(String hex) {
        byte[] bytes = hexToBytes(hex);
        boolean isAllASCII = true;
        int i = 0;
        while (i < bytes.length) {
            int byte1 = bytes[i] & 255;
            if (byte1 >= 129 && byte1 < 255 && i + 1 < bytes.length) {
                int byte2 = bytes[i + 1] & 255;
                if (byte2 < 64 || byte2 >= 255 || byte2 == 127) {
                    return false;
                }
                isAllASCII = false;
                i++;
            } else if (byte1 >= 128) {
                return false;
            }
            i++;
        }
        return !isAllASCII;
    }

    private static boolean isUTF8(String hex) {
        int nBytes;
        byte[] bytes = hexToBytes(hex);
        int nBytes2 = 0;
        boolean isAllASCII = true;
        for (byte b : bytes) {
            int chr = b & 255;
            if ((chr & 128) != 0) {
                isAllASCII = false;
            }
            if (nBytes2 == 0) {
                if (chr < 128) {
                    continue;
                } else {
                    if (chr >= 252 && chr <= 253) {
                        nBytes = 6;
                    } else if (chr >= 248) {
                        nBytes = 5;
                    } else if (chr >= 240) {
                        nBytes = 4;
                    } else if (chr >= 224) {
                        nBytes = 3;
                    } else if (chr < 192) {
                        return false;
                    } else {
                        nBytes = 2;
                    }
                    nBytes2 = nBytes - 1;
                }
            } else if ((chr & 192) != 128) {
                return false;
            } else {
                nBytes2--;
            }
        }
        if (nBytes2 > 0 || isAllASCII) {
            return false;
        }
        return true;
    }

    private static String parseKeyMgmt(BitSet set, String[] strings) {
        StringBuffer buf = new StringBuffer();
        int nextSetBit = -1;
        BitSet set2 = set.get(0, strings.length);
        while (true) {
            int nextSetBit2 = set2.nextSetBit(nextSetBit + 1);
            nextSetBit = nextSetBit2;
            if (nextSetBit2 == -1) {
                break;
            }
            buf.append(strings[nextSetBit]);
            buf.append(' ');
        }
        if (set2.cardinality() > 0) {
            buf.setLength(buf.length() - 1);
        }
        return buf.toString().replace('_', '-');
    }

    private static String removeDoubleQuotes(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        int length = string.length();
        if (length > 1 && string.charAt(0) == '\"' && string.charAt(length - 1) == '\"') {
            return string.substring(1, length - 1);
        }
        return string;
    }
}

package com.android.server.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import com.android.server.wifi.hotspot2.anqp.Constants;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import miui.telephony.phonenumber.Prefix;

final class Utils {
    private static final char[] ALPHABETS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final int EID_VENDOR_SPECIFIC = 221;
    private static final byte[] VENDOR_SPECIFIC_INFO_IOS = {0, 23, -14, 6, 1, 1, 3, 1};

    Utils() {
    }

    static String bytesToHex(byte[] bytes) {
        char[] buf = new char[(bytes.length * 2)];
        int c = 0;
        for (byte b : bytes) {
            int c2 = c + 1;
            char[] cArr = ALPHABETS;
            buf[c] = cArr[(b >> 4) & 15];
            c = c2 + 1;
            buf[c2] = cArr[b & 15];
        }
        return new String(buf);
    }

    static String byteToHex(byte b) {
        char[] cArr = ALPHABETS;
        return new String(new char[]{cArr[(b >> 4) & 15], cArr[b & 15]});
    }

    static String toHex(byte[] octets) {
        StringBuilder sb = new StringBuilder(octets.length * 2);
        int length = octets.length;
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x", new Object[]{Integer.valueOf(octets[i] & 255)}));
        }
        return sb.toString();
    }

    static byte[] hexToBytes(String text) {
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

    static int fromHex(char ch, boolean lenient) throws NumberFormatException {
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

    static String removeDoubleQuotes(String string) {
        if (TextUtils.isEmpty(string)) {
            return Prefix.EMPTY;
        }
        int length = string.length();
        if (length > 1 && string.charAt(0) == '\"' && string.charAt(length - 1) == '\"') {
            return string.substring(1, length - 1);
        }
        return string;
    }

    static boolean isUTF8(String hex) {
        int nBytes;
        byte[] bytes = hexToBytes(hex);
        int nBytes2 = 0;
        boolean isAllASCII = true;
        for (byte b : bytes) {
            int chr = b & Constants.BYTE_MASK;
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
            } else if ((chr & WifiConfigManager.SCAN_CACHE_ENTRIES_MAX_SIZE) != 128) {
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

    static boolean isGBK(String hex) {
        byte[] bytes = hexToBytes(hex);
        boolean isAllASCII = true;
        int i = 0;
        while (i < bytes.length) {
            int byte1 = bytes[i] & Constants.BYTE_MASK;
            if (byte1 >= 129 && byte1 < 255 && i + 1 < bytes.length) {
                int byte2 = bytes[i + 1] & Constants.BYTE_MASK;
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

    static ScanResult.InformationElement[] parseInformationElements(byte[] bytes) {
        if (bytes == null) {
            return new ScanResult.InformationElement[0];
        }
        ByteBuffer data = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        ArrayList<ScanResult.InformationElement> infoElements = new ArrayList<>();
        while (data.remaining() > 1) {
            int eid = data.get() & Constants.BYTE_MASK;
            int elementLength = data.get() & 255;
            if (elementLength > data.remaining()) {
                break;
            }
            ScanResult.InformationElement ie = new ScanResult.InformationElement();
            ie.id = eid;
            ie.bytes = new byte[elementLength];
            data.get(ie.bytes);
            infoElements.add(ie);
        }
        return (ScanResult.InformationElement[]) infoElements.toArray(new ScanResult.InformationElement[infoElements.size()]);
    }

    static boolean isMeteredHint(ScanResult.InformationElement[] infoElements) {
        if (infoElements == null) {
            return false;
        }
        for (int i = 0; i < infoElements.length; i++) {
            if (infoElements[i].id == 221 && Arrays.equals(Arrays.copyOf(infoElements[i].bytes, VENDOR_SPECIFIC_INFO_IOS.length), VENDOR_SPECIFIC_INFO_IOS)) {
                return true;
            }
        }
        return false;
    }

    static String getWifiConfigStringWithPassword(WifiConfiguration config) {
        return ConfigUtils.getWifiConfigStringWithPassword(config);
    }
}

package com.miui.maml.util.net;

import android.util.Base64;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.maml.util.net.SimpleRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;

public class SecureRequest {
    private static final String UTF8 = "utf-8";

    private static String decryptResponse(String str, String str2) {
        String str3;
        Cipher newAESCipher = CloudCoder.newAESCipher(str2, 2);
        if (newAESCipher != null) {
            try {
                str3 = new String(newAESCipher.doFinal(Base64.decode(str, 2)), "utf-8");
            } catch (Exception unused) {
                str3 = null;
            }
            if (str3 != null) {
                return str3;
            }
            throw new InvalidResponseException("failed to decrypt response");
        }
        throw new CipherException("failed to init cipher");
    }

    public static Map<String, String> encryptParams(String str, String str2, Map<String, String> map, String str3) {
        Cipher newAESCipher = CloudCoder.newAESCipher(str3, 1);
        if (newAESCipher != null) {
            HashMap hashMap = new HashMap();
            if (map != null && !map.isEmpty()) {
                for (Map.Entry next : map.entrySet()) {
                    String str4 = (String) next.getKey();
                    String str5 = (String) next.getValue();
                    if (!(str4 == null || str5 == null)) {
                        if (!str4.startsWith("_")) {
                            try {
                                str5 = Base64.encodeToString(newAESCipher.doFinal(str5.getBytes("utf-8")), 2);
                            } catch (Exception e) {
                                throw new CipherException("failed to encrypt request params", e);
                            }
                        }
                        hashMap.put(str4, str5);
                    }
                }
            }
            hashMap.put(WarningModel.Columns.SIGNATURE, CloudCoder.generateSignature(str, str2, hashMap, str3));
            return hashMap;
        }
        throw new CipherException("failed to init cipher");
    }

    public static SimpleRequest.MapContent getAsMap(String str, Map<String, String> map, Map<String, String> map2, boolean z, String str2) {
        return SimpleRequest.convertStringToMap(getAsString(str, map, map2, z, str2));
    }

    public static SimpleRequest.StringContent getAsString(String str, Map<String, String> map, Map<String, String> map2, boolean z, String str2) {
        return processStringResponse(SimpleRequest.getAsString(str, encryptParams("GET", str, map, str2), map2, z), str2);
    }

    public static SimpleRequest.MapContent postAsMap(String str, Map<String, String> map, Map<String, String> map2, boolean z, String str2) {
        return SimpleRequest.convertStringToMap(postAsString(str, map, map2, z, str2));
    }

    public static SimpleRequest.StringContent postAsString(String str, Map<String, String> map, Map<String, String> map2, boolean z, String str2) {
        return processStringResponse(SimpleRequest.postAsString(str, encryptParams("POST", str, map, str2), map2, z), str2);
    }

    private static SimpleRequest.StringContent processStringResponse(SimpleRequest.StringContent stringContent, String str) {
        if (stringContent != null) {
            String body = stringContent.getBody();
            if (body != null) {
                SimpleRequest.StringContent stringContent2 = new SimpleRequest.StringContent(decryptResponse(body, str));
                stringContent2.putHeaders(stringContent.getHeaders());
                return stringContent2;
            }
            throw new InvalidResponseException("invalid response from server");
        }
        throw new IOException("no response from server");
    }
}

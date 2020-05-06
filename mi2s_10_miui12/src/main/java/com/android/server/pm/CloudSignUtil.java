package com.android.server.pm;

import com.android.server.pm.CloudControlPreinstallService;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.BiConsumer;

public class CloudSignUtil {
    private CloudSignUtil() {
    }

    public static String getNonceStr() {
        return UUID.randomUUID().toString();
    }

    public static String getSign(TreeMap<String, Object> paramsMap, String nonceStr) {
        StringBuilder unEncryptedStr = new StringBuilder();
        paramsMap.forEach(new BiConsumer(unEncryptedStr) {
            private final /* synthetic */ StringBuilder f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj, Object obj2) {
                CloudSignUtil.lambda$getSign$0(this.f$0, (String) obj, obj2);
            }
        });
        unEncryptedStr.append("#");
        unEncryptedStr.append(nonceStr);
        return md5(unEncryptedStr.toString());
    }

    static /* synthetic */ void lambda$getSign$0(StringBuilder unEncryptedStr, String key, Object value) {
        unEncryptedStr.append(key);
        unEncryptedStr.append("&");
        unEncryptedStr.append(value);
    }

    public static String getSign(String imeiMd5, String device, String miuiVersion, String channel, String region, boolean isCn, String lang, String nonceStr) {
        Map<String, Object> map = new TreeMap<>();
        map.put("imeiMd5", imeiMd5);
        map.put(CloudControlPreinstallService.ConnectEntity.DEVICE, device);
        map.put(CloudControlPreinstallService.ConnectEntity.MIUI_VERSION, miuiVersion);
        map.put(CloudControlPreinstallService.ConnectEntity.CHANNEL, channel);
        map.put(CloudControlPreinstallService.ConnectEntity.REGION, region);
        map.put(CloudControlPreinstallService.ConnectEntity.IS_CN, Boolean.valueOf(isCn));
        map.put(CloudControlPreinstallService.ConnectEntity.LANG, lang);
        StringBuilder unEncryptedStr = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            unEncryptedStr.append(entry.getKey());
            unEncryptedStr.append("&");
            unEncryptedStr.append(entry.getValue());
        }
        unEncryptedStr.append("#");
        unEncryptedStr.append(nonceStr);
        return md5(unEncryptedStr.toString());
    }

    public static boolean vaildateSign(String imeiMd5, String device, String miuiVersion, String channel, String region, boolean isCn, String lang, String nonceStr, String sign) {
        return sign.equals(getSign(imeiMd5, device, miuiVersion, channel, region, isCn, lang, nonceStr));
    }

    public static String md5(String unEncryptedStr) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(unEncryptedStr.getBytes());
            String md5code = new BigInteger(1, md.digest()).toString(16);
            StringBuilder md5CodeSb = new StringBuilder();
            for (int i = 0; i < 32 - md5code.length(); i++) {
                md5CodeSb.append("0");
            }
            md5CodeSb.append(md5code);
            return md5CodeSb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }
}

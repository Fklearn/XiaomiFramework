package com.android.server.location;

import android.util.Base64;
import com.android.server.usb.descriptors.UsbDescriptor;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypt {
    public static final byte[] encBytes(byte[] srcBytes, byte[] key, byte[] newIv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, new SecretKeySpec(key, "AES"), new IvParameterSpec(newIv));
        return cipher.doFinal(srcBytes);
    }

    public static final String encText(String sSrc) throws Exception {
        byte[] ivk = {UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION, UsbDescriptor.DESCRIPTORTYPE_ENDPOINT_COMPANION};
        return Base64.encodeToString(encBytes(sSrc.getBytes("utf-8"), new byte[]{97, 114, 101, 121, 111, 117, 111, 107, 97, 114, 101, 121, 111, 117, 111, 107}, ivk), 0).replace("\n", "").replace("\r", "");
    }
}

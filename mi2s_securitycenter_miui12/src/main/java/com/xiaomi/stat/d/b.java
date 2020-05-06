package com.xiaomi.stat.d;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import com.google.android.exoplayer2.C;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.GregorianCalendar;
import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8502a = "AndroidKeyStoreUtils";

    /* renamed from: b  reason: collision with root package name */
    private static final String f8503b = "AndroidKeyStore";

    /* renamed from: c  reason: collision with root package name */
    private static final String f8504c = "RSA/ECB/PKCS1Padding";

    /* renamed from: d  reason: collision with root package name */
    private static final String f8505d = "RSA_KEY";

    public static synchronized String a(Context context, String str) {
        synchronized (b.class) {
            Cipher instance = Cipher.getInstance(f8504c);
            KeyStore instance2 = KeyStore.getInstance(f8503b);
            instance2.load((KeyStore.LoadStoreParameter) null);
            a(context, instance2);
            Certificate certificate = instance2.getCertificate(f8505d);
            if (certificate == null) {
                return null;
            }
            instance.init(1, certificate.getPublicKey());
            String encodeToString = Base64.encodeToString(instance.doFinal(str.getBytes(C.UTF8_NAME)), 0);
            return encodeToString;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v1, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void a() {
        /*
            java.lang.String r0 = "android.security.keystore.KeyGenParameterSpec$Builder"
            java.lang.Class r0 = java.lang.Class.forName(r0)
            if (r0 == 0) goto L_0x00e5
            r1 = 2
            java.lang.Class[] r2 = new java.lang.Class[r1]
            java.lang.Class<java.lang.String> r3 = java.lang.String.class
            r4 = 0
            r2[r4] = r3
            java.lang.Class r3 = java.lang.Integer.TYPE
            r5 = 1
            r2[r5] = r3
            java.lang.reflect.Constructor r2 = r0.getConstructor(r2)
            java.lang.String r3 = "android.security.keystore.KeyProperties"
            java.lang.Class r3 = java.lang.Class.forName(r3)
            java.lang.String r6 = "PURPOSE_ENCRYPT"
            java.lang.reflect.Field r6 = r3.getDeclaredField(r6)
            r7 = 0
            int r6 = r6.getInt(r7)
            java.lang.String r8 = "PURPOSE_DECRYPT"
            java.lang.reflect.Field r8 = r3.getDeclaredField(r8)
            int r8 = r8.getInt(r7)
            java.lang.Object[] r9 = new java.lang.Object[r1]
            java.lang.String r10 = "RSA_KEY"
            r9[r4] = r10
            r6 = r6 | r8
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r9[r5] = r6
            java.lang.Object r2 = r2.newInstance(r9)
            java.lang.Class[] r6 = new java.lang.Class[r5]
            java.lang.Class<java.lang.String[]> r8 = java.lang.String[].class
            r6[r4] = r8
            java.lang.String r8 = "setDigests"
            java.lang.reflect.Method r6 = r0.getMethod(r8, r6)
            java.lang.String r8 = "DIGEST_SHA256"
            java.lang.reflect.Field r8 = r3.getDeclaredField(r8)
            java.lang.Object r8 = r8.get(r7)
            java.lang.String r8 = (java.lang.String) r8
            java.lang.String r9 = "DIGEST_SHA512"
            java.lang.reflect.Field r9 = r3.getDeclaredField(r9)
            java.lang.Object r9 = r9.get(r7)
            java.lang.String r9 = (java.lang.String) r9
            java.lang.Object[] r10 = new java.lang.Object[r5]
            java.lang.String[] r11 = new java.lang.String[r1]
            r11[r4] = r8
            r11[r5] = r9
            r10[r4] = r11
            r6.invoke(r2, r10)
            java.lang.Class[] r6 = new java.lang.Class[r5]
            java.lang.Class<java.lang.String[]> r8 = java.lang.String[].class
            r6[r4] = r8
            java.lang.String r8 = "setEncryptionPaddings"
            java.lang.reflect.Method r6 = r0.getMethod(r8, r6)
            java.lang.String r8 = "ENCRYPTION_PADDING_RSA_PKCS1"
            java.lang.reflect.Field r3 = r3.getDeclaredField(r8)
            java.lang.Object r3 = r3.get(r7)
            java.lang.String r3 = (java.lang.String) r3
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String[] r9 = new java.lang.String[r5]
            r9[r4] = r3
            r8[r4] = r9
            r6.invoke(r2, r8)
            java.lang.Class[] r3 = new java.lang.Class[r4]
            java.lang.String r6 = "build"
            java.lang.reflect.Method r0 = r0.getMethod(r6, r3)
            java.lang.Object[] r3 = new java.lang.Object[r4]
            java.lang.Object r0 = r0.invoke(r2, r3)
            java.lang.String r2 = "java.security.KeyPairGenerator"
            java.lang.Class r2 = java.lang.Class.forName(r2)
            if (r2 == 0) goto L_0x00e5
            java.lang.Class[] r3 = new java.lang.Class[r1]
            java.lang.Class<java.lang.String> r6 = java.lang.String.class
            r3[r4] = r6
            java.lang.Class<java.lang.String> r6 = java.lang.String.class
            r3[r5] = r6
            java.lang.String r6 = "getInstance"
            java.lang.reflect.Method r3 = r2.getMethod(r6, r3)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r6 = "RSA"
            r1[r4] = r6
            java.lang.String r6 = "AndroidKeyStore"
            r1[r5] = r6
            java.lang.Object r1 = r3.invoke(r7, r1)
            java.security.KeyPairGenerator r1 = (java.security.KeyPairGenerator) r1
            java.lang.Class[] r3 = new java.lang.Class[r5]
            java.lang.Class<java.security.spec.AlgorithmParameterSpec> r6 = java.security.spec.AlgorithmParameterSpec.class
            r3[r4] = r6
            java.lang.String r6 = "initialize"
            java.lang.reflect.Method r2 = r2.getMethod(r6, r3)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r4] = r0
            r2.invoke(r1, r3)
            r1.generateKeyPair()
        L_0x00e5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.d.b.a():void");
    }

    private static void a(Context context) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
        gregorianCalendar2.add(1, 1);
        KeyPairGeneratorSpec build = new KeyPairGeneratorSpec.Builder(context).setAlias(f8505d).setSubject(new X500Principal("CN=RSA_KEY")).setSerialNumber(BigInteger.valueOf(1337)).setStartDate(gregorianCalendar.getTime()).setEndDate(gregorianCalendar2.getTime()).build();
        KeyPairGenerator instance = KeyPairGenerator.getInstance("RSA", f8503b);
        instance.initialize(build);
        instance.generateKeyPair();
    }

    private static void a(Context context, KeyStore keyStore) {
        try {
            if (!keyStore.containsAlias(f8505d) && Build.VERSION.SDK_INT >= 18) {
                if (Build.VERSION.SDK_INT < 23) {
                    a(context);
                } else {
                    a();
                }
            }
        } catch (Exception e) {
            k.d(f8502a, "createKey e", e);
        }
    }

    public static synchronized String b(Context context, String str) {
        String str2;
        synchronized (b.class) {
            Cipher instance = Cipher.getInstance(f8504c);
            KeyStore instance2 = KeyStore.getInstance(f8503b);
            instance2.load((KeyStore.LoadStoreParameter) null);
            a(context, instance2);
            instance.init(2, (PrivateKey) instance2.getKey(f8505d, (char[]) null));
            str2 = new String(instance.doFinal(Base64.decode(str, 0)), C.UTF8_NAME);
        }
        return str2;
    }
}

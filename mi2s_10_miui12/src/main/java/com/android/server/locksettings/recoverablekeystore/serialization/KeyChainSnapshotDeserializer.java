package com.android.server.locksettings.recoverablekeystore.serialization;

import android.security.keystore.recovery.KeyChainProtectionParams;
import android.security.keystore.recovery.KeyChainSnapshot;
import android.security.keystore.recovery.KeyDerivationParams;
import android.security.keystore.recovery.WrappedApplicationKey;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class KeyChainSnapshotDeserializer {
    public static KeyChainSnapshot deserialize(InputStream inputStream) throws KeyChainSnapshotParserException, IOException {
        try {
            return deserializeInternal(inputStream);
        } catch (XmlPullParserException e) {
            throw new KeyChainSnapshotParserException("Malformed KeyChainSnapshot XML", e);
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x004e, code lost:
        if (r7.equals("serverParams") != false) goto L_0x009a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.security.keystore.recovery.KeyChainSnapshot deserializeInternal(java.io.InputStream r16) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException, com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotParserException {
        /*
            org.xmlpull.v1.XmlPullParser r1 = android.util.Xml.newPullParser()
            java.lang.String r0 = "UTF-8"
            r2 = r16
            r1.setInput(r2, r0)
            r1.nextTag()
            java.lang.String r0 = com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotSchema.NAMESPACE
            java.lang.String r3 = "keyChainSnapshot"
            r4 = 2
            r1.require(r4, r0, r3)
            android.security.keystore.recovery.KeyChainSnapshot$Builder r0 = new android.security.keystore.recovery.KeyChainSnapshot$Builder
            r0.<init>()
            r5 = r0
        L_0x001d:
            int r0 = r1.next()
            r6 = 3
            if (r0 == r6) goto L_0x00fd
            int r0 = r1.getEventType()
            if (r0 == r4) goto L_0x002b
            goto L_0x001d
        L_0x002b:
            java.lang.String r7 = r1.getName()
            int r8 = r7.hashCode()
            r9 = 0
            r10 = 1
            java.lang.String r11 = "serverParams"
            java.lang.String r12 = "counterId"
            java.lang.String r13 = "snapshotVersion"
            java.lang.String r14 = "thmCertPath"
            java.lang.String r15 = "recoveryKeyMaterial"
            java.lang.String r0 = "maxAttempts"
            switch(r8) {
                case -1719931702: goto L_0x0091;
                case -1388433662: goto L_0x0087;
                case -1370381871: goto L_0x007f;
                case -1368437758: goto L_0x0077;
                case 481270388: goto L_0x006f;
                case 1190285858: goto L_0x0064;
                case 1352257591: goto L_0x005c;
                case 1596875199: goto L_0x0051;
                case 1806980777: goto L_0x004a;
                default: goto L_0x0049;
            }
        L_0x0049:
            goto L_0x0099
        L_0x004a:
            boolean r8 = r7.equals(r11)
            if (r8 == 0) goto L_0x0049
            goto L_0x009a
        L_0x0051:
            java.lang.String r6 = "keyChainProtectionParamsList"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x0049
            r6 = 7
            goto L_0x009a
        L_0x005c:
            boolean r6 = r7.equals(r12)
            if (r6 == 0) goto L_0x0049
            r6 = r4
            goto L_0x009a
        L_0x0064:
            java.lang.String r6 = "applicationKeysList"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x0049
            r6 = 8
            goto L_0x009a
        L_0x006f:
            boolean r6 = r7.equals(r13)
            if (r6 == 0) goto L_0x0049
            r6 = r9
            goto L_0x009a
        L_0x0077:
            boolean r6 = r7.equals(r14)
            if (r6 == 0) goto L_0x0049
            r6 = 5
            goto L_0x009a
        L_0x007f:
            boolean r6 = r7.equals(r15)
            if (r6 == 0) goto L_0x0049
            r6 = r10
            goto L_0x009a
        L_0x0087:
            java.lang.String r6 = "backendPublicKey"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x0049
            r6 = 6
            goto L_0x009a
        L_0x0091:
            boolean r6 = r7.equals(r0)
            if (r6 == 0) goto L_0x0049
            r6 = 4
            goto L_0x009a
        L_0x0099:
            r6 = -1
        L_0x009a:
            switch(r6) {
                case 0: goto L_0x00f3;
                case 1: goto L_0x00ea;
                case 2: goto L_0x00e2;
                case 3: goto L_0x00da;
                case 4: goto L_0x00d2;
                case 5: goto L_0x00c0;
                case 6: goto L_0x00bf;
                case 7: goto L_0x00b7;
                case 8: goto L_0x00af;
                default: goto L_0x009d;
            }
        L_0x009d:
            com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotParserException r0 = new com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotParserException
            java.util.Locale r3 = java.util.Locale.US
            java.lang.Object[] r4 = new java.lang.Object[r10]
            r4[r9] = r7
            java.lang.String r6 = "Unexpected tag %s in keyChainSnapshot"
            java.lang.String r3 = java.lang.String.format(r3, r6, r4)
            r0.<init>(r3)
            throw r0
        L_0x00af:
            java.util.List r0 = readWrappedApplicationKeys(r1)
            r5.setWrappedApplicationKeys(r0)
            goto L_0x00fb
        L_0x00b7:
            java.util.List r0 = readKeyChainProtectionParamsList(r1)
            r5.setKeyChainProtectionParams(r0)
            goto L_0x00fb
        L_0x00bf:
            goto L_0x00fb
        L_0x00c0:
            java.security.cert.CertPath r0 = readCertPathTag(r1, r14)     // Catch:{ CertificateException -> 0x00c9 }
            r5.setTrustedHardwareCertPath(r0)     // Catch:{ CertificateException -> 0x00c9 }
            goto L_0x00fb
        L_0x00c9:
            r0 = move-exception
            com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotParserException r3 = new com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotParserException
            java.lang.String r4 = "Could not set trustedHardwareCertPath"
            r3.<init>(r4, r0)
            throw r3
        L_0x00d2:
            int r0 = readIntTag(r1, r0)
            r5.setMaxAttempts(r0)
            goto L_0x00fb
        L_0x00da:
            byte[] r0 = readBlobTag(r1, r11)
            r5.setServerParams(r0)
            goto L_0x00fb
        L_0x00e2:
            long r8 = readLongTag(r1, r12)
            r5.setCounterId(r8)
            goto L_0x00fb
        L_0x00ea:
            byte[] r0 = readBlobTag(r1, r15)
            r5.setEncryptedRecoveryKeyBlob(r0)
            goto L_0x00fb
        L_0x00f3:
            int r0 = readIntTag(r1, r13)
            r5.setSnapshotVersion(r0)
        L_0x00fb:
            goto L_0x001d
        L_0x00fd:
            java.lang.String r0 = com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotSchema.NAMESPACE
            r1.require(r6, r0, r3)
            android.security.keystore.recovery.KeyChainSnapshot r0 = r5.build()     // Catch:{ NullPointerException -> 0x0107 }
            return r0
        L_0x0107:
            r0 = move-exception
            r3 = r0
            r0 = r3
            com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotParserException r3 = new com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotParserException
            java.lang.String r4 = "Failed to build KeyChainSnapshot"
            r3.<init>(r4, r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotDeserializer.deserializeInternal(java.io.InputStream):android.security.keystore.recovery.KeyChainSnapshot");
    }

    private static List<WrappedApplicationKey> readWrappedApplicationKeys(XmlPullParser parser) throws IOException, XmlPullParserException, KeyChainSnapshotParserException {
        parser.require(2, KeyChainSnapshotSchema.NAMESPACE, "applicationKeysList");
        ArrayList<WrappedApplicationKey> keys = new ArrayList<>();
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                keys.add(readWrappedApplicationKey(parser));
            }
        }
        parser.require(3, KeyChainSnapshotSchema.NAMESPACE, "applicationKeysList");
        return keys;
    }

    private static WrappedApplicationKey readWrappedApplicationKey(XmlPullParser parser) throws IOException, XmlPullParserException, KeyChainSnapshotParserException {
        parser.require(2, KeyChainSnapshotSchema.NAMESPACE, "applicationKey");
        WrappedApplicationKey.Builder builder = new WrappedApplicationKey.Builder();
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                String name = parser.getName();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != -1712279890) {
                    if (hashCode != -963209050) {
                        if (hashCode == 92902992 && name.equals("alias")) {
                            c = 0;
                        }
                    } else if (name.equals("keyMaterial")) {
                        c = 1;
                    }
                } else if (name.equals("keyMetadata")) {
                    c = 2;
                }
                if (c == 0) {
                    builder.setAlias(readStringTag(parser, "alias"));
                } else if (c == 1) {
                    builder.setEncryptedKeyMaterial(readBlobTag(parser, "keyMaterial"));
                } else if (c == 2) {
                    builder.setMetadata(readBlobTag(parser, "keyMetadata"));
                } else {
                    throw new KeyChainSnapshotParserException(String.format(Locale.US, "Unexpected tag %s in wrappedApplicationKey", new Object[]{name}));
                }
            }
        }
        parser.require(3, KeyChainSnapshotSchema.NAMESPACE, "applicationKey");
        try {
            return builder.build();
        } catch (NullPointerException e) {
            throw new KeyChainSnapshotParserException("Failed to build WrappedApplicationKey", e);
        }
    }

    private static List<KeyChainProtectionParams> readKeyChainProtectionParamsList(XmlPullParser parser) throws IOException, XmlPullParserException, KeyChainSnapshotParserException {
        parser.require(2, KeyChainSnapshotSchema.NAMESPACE, "keyChainProtectionParamsList");
        ArrayList<KeyChainProtectionParams> keyChainProtectionParamsList = new ArrayList<>();
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                keyChainProtectionParamsList.add(readKeyChainProtectionParams(parser));
            }
        }
        parser.require(3, KeyChainSnapshotSchema.NAMESPACE, "keyChainProtectionParamsList");
        return keyChainProtectionParamsList;
    }

    private static KeyChainProtectionParams readKeyChainProtectionParams(XmlPullParser parser) throws IOException, XmlPullParserException, KeyChainSnapshotParserException {
        parser.require(2, KeyChainSnapshotSchema.NAMESPACE, "keyChainProtectionParams");
        KeyChainProtectionParams.Builder builder = new KeyChainProtectionParams.Builder();
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                String name = parser.getName();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != -776797115) {
                    if (hashCode != -696958923) {
                        if (hashCode == 912448924 && name.equals("keyDerivationParams")) {
                            c = 2;
                        }
                    } else if (name.equals("userSecretType")) {
                        c = 1;
                    }
                } else if (name.equals("lockScreenUiType")) {
                    c = 0;
                }
                if (c == 0) {
                    builder.setLockScreenUiFormat(readIntTag(parser, "lockScreenUiType"));
                } else if (c == 1) {
                    builder.setUserSecretType(readIntTag(parser, "userSecretType"));
                } else if (c == 2) {
                    builder.setKeyDerivationParams(readKeyDerivationParams(parser));
                } else {
                    throw new KeyChainSnapshotParserException(String.format(Locale.US, "Unexpected tag %s in keyChainProtectionParams", new Object[]{name}));
                }
            }
        }
        parser.require(3, KeyChainSnapshotSchema.NAMESPACE, "keyChainProtectionParams");
        try {
            return builder.build();
        } catch (NullPointerException e) {
            throw new KeyChainSnapshotParserException("Failed to build KeyChainProtectionParams", e);
        }
    }

    private static KeyDerivationParams readKeyDerivationParams(XmlPullParser parser) throws XmlPullParserException, IOException, KeyChainSnapshotParserException {
        KeyDerivationParams keyDerivationParams;
        parser.require(2, KeyChainSnapshotSchema.NAMESPACE, "keyDerivationParams");
        int memoryDifficulty = -1;
        int algorithm = -1;
        byte[] salt = null;
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                String name = parser.getName();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != -973274212) {
                    if (hashCode != 3522646) {
                        if (hashCode == 225490031 && name.equals("algorithm")) {
                            c = 1;
                        }
                    } else if (name.equals("salt")) {
                        c = 2;
                    }
                } else if (name.equals("memoryDifficulty")) {
                    c = 0;
                }
                if (c == 0) {
                    memoryDifficulty = readIntTag(parser, "memoryDifficulty");
                } else if (c == 1) {
                    algorithm = readIntTag(parser, "algorithm");
                } else if (c == 2) {
                    salt = readBlobTag(parser, "salt");
                } else {
                    throw new KeyChainSnapshotParserException(String.format(Locale.US, "Unexpected tag %s in keyDerivationParams", new Object[]{name}));
                }
            }
        }
        if (salt != null) {
            if (algorithm == 1) {
                keyDerivationParams = KeyDerivationParams.createSha256Params(salt);
            } else if (algorithm == 2) {
                keyDerivationParams = KeyDerivationParams.createScryptParams(salt, memoryDifficulty);
            } else {
                throw new KeyChainSnapshotParserException("Unknown algorithm in keyDerivationParams");
            }
            parser.require(3, KeyChainSnapshotSchema.NAMESPACE, "keyDerivationParams");
            return keyDerivationParams;
        }
        throw new KeyChainSnapshotParserException("salt was not set in keyDerivationParams");
    }

    private static int readIntTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException, KeyChainSnapshotParserException {
        parser.require(2, KeyChainSnapshotSchema.NAMESPACE, tagName);
        String text = readText(parser);
        parser.require(3, KeyChainSnapshotSchema.NAMESPACE, tagName);
        try {
            return Integer.valueOf(text).intValue();
        } catch (NumberFormatException e) {
            throw new KeyChainSnapshotParserException(String.format(Locale.US, "%s expected int but got '%s'", new Object[]{tagName, text}), e);
        }
    }

    private static long readLongTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException, KeyChainSnapshotParserException {
        parser.require(2, KeyChainSnapshotSchema.NAMESPACE, tagName);
        String text = readText(parser);
        parser.require(3, KeyChainSnapshotSchema.NAMESPACE, tagName);
        try {
            return Long.valueOf(text).longValue();
        } catch (NumberFormatException e) {
            throw new KeyChainSnapshotParserException(String.format(Locale.US, "%s expected long but got '%s'", new Object[]{tagName, text}), e);
        }
    }

    private static String readStringTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(2, KeyChainSnapshotSchema.NAMESPACE, tagName);
        String text = readText(parser);
        parser.require(3, KeyChainSnapshotSchema.NAMESPACE, tagName);
        return text;
    }

    private static byte[] readBlobTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException, KeyChainSnapshotParserException {
        parser.require(2, KeyChainSnapshotSchema.NAMESPACE, tagName);
        String text = readText(parser);
        parser.require(3, KeyChainSnapshotSchema.NAMESPACE, tagName);
        try {
            return Base64.decode(text, 0);
        } catch (IllegalArgumentException e) {
            throw new KeyChainSnapshotParserException(String.format(Locale.US, "%s expected base64 encoded bytes but got '%s'", new Object[]{tagName, text}), e);
        }
    }

    private static CertPath readCertPathTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException, KeyChainSnapshotParserException {
        try {
            return CertificateFactory.getInstance("X.509").generateCertPath(new ByteArrayInputStream(readBlobTag(parser, tagName)));
        } catch (CertificateException e) {
            throw new KeyChainSnapshotParserException("Could not parse CertPath in tag " + tagName, e);
        }
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        if (parser.next() != 4) {
            return "";
        }
        String result = parser.getText();
        parser.nextTag();
        return result;
    }

    private KeyChainSnapshotDeserializer() {
    }
}

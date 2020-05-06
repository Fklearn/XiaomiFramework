package com.android.server.pm;

import android.content.pm.PackageParser;
import android.content.pm.Signature;
import com.android.internal.util.XmlUtils;
import com.android.server.am.AssistDataRequester;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class PackageSignatures {
    PackageParser.SigningDetails mSigningDetails;

    PackageSignatures(PackageSignatures orig) {
        if (orig == null || orig.mSigningDetails == PackageParser.SigningDetails.UNKNOWN) {
            this.mSigningDetails = PackageParser.SigningDetails.UNKNOWN;
        } else {
            this.mSigningDetails = new PackageParser.SigningDetails(orig.mSigningDetails);
        }
    }

    PackageSignatures(PackageParser.SigningDetails signingDetails) {
        this.mSigningDetails = signingDetails;
    }

    PackageSignatures() {
        this.mSigningDetails = PackageParser.SigningDetails.UNKNOWN;
    }

    /* access modifiers changed from: package-private */
    public void writeXml(XmlSerializer serializer, String tagName, ArrayList<Signature> writtenSignatures) throws IOException {
        if (this.mSigningDetails.signatures != null) {
            serializer.startTag((String) null, tagName);
            serializer.attribute((String) null, AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT, Integer.toString(this.mSigningDetails.signatures.length));
            serializer.attribute((String) null, "schemeVersion", Integer.toString(this.mSigningDetails.signatureSchemeVersion));
            writeCertsListXml(serializer, writtenSignatures, this.mSigningDetails.signatures, false);
            if (this.mSigningDetails.pastSigningCertificates != null) {
                serializer.startTag((String) null, "pastSigs");
                serializer.attribute((String) null, AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT, Integer.toString(this.mSigningDetails.pastSigningCertificates.length));
                writeCertsListXml(serializer, writtenSignatures, this.mSigningDetails.pastSigningCertificates, true);
                serializer.endTag((String) null, "pastSigs");
            }
            serializer.endTag((String) null, tagName);
        }
    }

    private void writeCertsListXml(XmlSerializer serializer, ArrayList<Signature> writtenSignatures, Signature[] signatures, boolean isPastSigs) throws IOException {
        for (Signature sig : signatures) {
            serializer.startTag((String) null, "cert");
            int sigHash = sig.hashCode();
            int numWritten = writtenSignatures.size();
            int j = 0;
            while (true) {
                if (j >= numWritten) {
                    break;
                }
                Signature writtenSig = writtenSignatures.get(j);
                if (writtenSig.hashCode() == sigHash && writtenSig.equals(sig)) {
                    serializer.attribute((String) null, AssistDataRequester.KEY_RECEIVER_EXTRA_INDEX, Integer.toString(j));
                    break;
                }
                j++;
            }
            if (j >= numWritten) {
                writtenSignatures.add(sig);
                serializer.attribute((String) null, AssistDataRequester.KEY_RECEIVER_EXTRA_INDEX, Integer.toString(numWritten));
                serializer.attribute((String) null, "key", sig.toCharsString());
            }
            if (isPastSigs) {
                serializer.attribute((String) null, "flags", Integer.toString(sig.getFlags()));
            }
            serializer.endTag((String) null, "cert");
        }
    }

    /* access modifiers changed from: package-private */
    public void readXml(XmlPullParser parser, ArrayList<Signature> readSignatures) throws IOException, XmlPullParserException {
        int signatureSchemeVersion;
        XmlPullParser xmlPullParser = parser;
        PackageParser.SigningDetails.Builder builder = new PackageParser.SigningDetails.Builder();
        String countStr = xmlPullParser.getAttributeValue((String) null, AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT);
        if (countStr == null) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <sigs> has no count at " + parser.getPositionDescription());
            XmlUtils.skipCurrentTag(parser);
            return;
        }
        int count = Integer.parseInt(countStr);
        String schemeVersionStr = xmlPullParser.getAttributeValue((String) null, "schemeVersion");
        if (schemeVersionStr == null) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <sigs> has no schemeVersion at " + parser.getPositionDescription());
            signatureSchemeVersion = 0;
        } else {
            signatureSchemeVersion = Integer.parseInt(schemeVersionStr);
        }
        builder.setSignatureSchemeVersion(signatureSchemeVersion);
        ArrayList<Signature> signatureList = new ArrayList<>();
        ArrayList<Signature> signatureList2 = signatureList;
        int pos = readCertsListXml(parser, readSignatures, signatureList, count, false, builder);
        builder.setSignatures((Signature[]) signatureList2.toArray(new Signature[signatureList2.size()]));
        if (pos < count) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <sigs> count does not match number of  <cert> entries" + parser.getPositionDescription());
        }
        try {
            this.mSigningDetails = builder.build();
        } catch (CertificateException e) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <sigs> unable to convert certificate(s) to public key(s).");
            this.mSigningDetails = PackageParser.SigningDetails.UNKNOWN;
        }
    }

    private int readCertsListXml(XmlPullParser parser, ArrayList<Signature> readSignatures, ArrayList<Signature> signatures, int count, boolean isPastSigs, PackageParser.SigningDetails.Builder builder) throws IOException, XmlPullParserException {
        String countStr;
        String str;
        int i;
        XmlPullParser xmlPullParser = parser;
        ArrayList<Signature> arrayList = readSignatures;
        ArrayList<Signature> arrayList2 = signatures;
        int i2 = count;
        int outerDepth = parser.getDepth();
        PackageParser.SigningDetails.Builder builder2 = builder;
        int pos = 0;
        while (true) {
            int pos2 = parser.next();
            int type = pos2;
            if (pos2 != 1) {
                if (type == 3 && parser.getDepth() <= outerDepth) {
                    int i3 = type;
                    break;
                }
                if (type == 3) {
                } else if (type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals("cert")) {
                        if (pos < i2) {
                            String index = xmlPullParser.getAttributeValue((String) null, AssistDataRequester.KEY_RECEIVER_EXTRA_INDEX);
                            if (index != null) {
                                boolean signatureParsed = false;
                                try {
                                    int idx = Integer.parseInt(index);
                                    String key = xmlPullParser.getAttributeValue((String) null, "key");
                                    if (key != null) {
                                        Signature sig = new Signature(key);
                                        while (readSignatures.size() < idx) {
                                            arrayList.add((Object) null);
                                        }
                                        arrayList.add(sig);
                                        arrayList2.add(sig);
                                        signatureParsed = true;
                                    } else if (idx < 0 || idx >= readSignatures.size()) {
                                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> index " + index + " is out of bounds at " + parser.getPositionDescription());
                                    } else {
                                        Signature sig2 = arrayList.get(idx);
                                        if (sig2 != null) {
                                            arrayList2.add(sig2);
                                            signatureParsed = true;
                                        } else {
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("Error in package manager settings: <cert> index ");
                                            sb.append(index);
                                            Signature signature = sig2;
                                            sb.append(" is not defined at ");
                                            sb.append(parser.getPositionDescription());
                                            PackageManagerService.reportSettingsProblem(5, sb.toString());
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> index " + index + " is not a number at " + parser.getPositionDescription());
                                } catch (IllegalArgumentException e2) {
                                    PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> index " + index + " has an invalid signature at " + parser.getPositionDescription() + ": " + e2.getMessage());
                                }
                                if (isPastSigs) {
                                    String flagsStr = xmlPullParser.getAttributeValue((String) null, "flags");
                                    if (flagsStr != null) {
                                        try {
                                            int flagsValue = Integer.parseInt(flagsStr);
                                            if (signatureParsed) {
                                                arrayList2.get(signatures.size() - 1).setFlags(flagsValue);
                                            } else {
                                                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: signature not available at index " + pos + " to set flags at " + parser.getPositionDescription());
                                            }
                                        } catch (NumberFormatException e3) {
                                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> flags " + flagsStr + " is not a number at " + parser.getPositionDescription());
                                        }
                                    } else {
                                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> has no flags at " + parser.getPositionDescription());
                                    }
                                }
                            } else {
                                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> has no index at " + parser.getPositionDescription());
                            }
                        } else {
                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: too many <cert> tags, expected " + i2 + " at " + parser.getPositionDescription());
                        }
                        pos++;
                        XmlUtils.skipCurrentTag(parser);
                        int i4 = type;
                    } else if (!tagName.equals("pastSigs")) {
                        int i5 = type;
                        PackageManagerService.reportSettingsProblem(5, "Unknown element under <sigs>: " + parser.getName());
                        XmlUtils.skipCurrentTag(parser);
                    } else if (!isPastSigs) {
                        String countStr2 = xmlPullParser.getAttributeValue((String) null, AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT);
                        if (countStr2 == null) {
                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <pastSigs> has no count at " + parser.getPositionDescription());
                            XmlUtils.skipCurrentTag(parser);
                        } else {
                            try {
                                int pastSigsCount = Integer.parseInt(countStr2);
                                ArrayList<Signature> pastSignatureList = new ArrayList<>();
                                countStr = countStr2;
                                str = " is not a number at ";
                                String str2 = tagName;
                                int i6 = type;
                                try {
                                    int pastSigsPos = readCertsListXml(parser, readSignatures, pastSignatureList, pastSigsCount, true, builder2);
                                    builder2 = builder2.setPastSigningCertificates((Signature[]) pastSignatureList.toArray(new Signature[pastSignatureList.size()]));
                                    if (pastSigsPos < pastSigsCount) {
                                        i = 5;
                                        try {
                                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <pastSigs> count does not match number of <cert> entries " + parser.getPositionDescription());
                                        } catch (NumberFormatException e4) {
                                        }
                                    }
                                } catch (NumberFormatException e5) {
                                    i = 5;
                                    PackageManagerService.reportSettingsProblem(i, "Error in package manager settings: <pastSigs> count " + countStr + str + parser.getPositionDescription());
                                    xmlPullParser = parser;
                                }
                            } catch (NumberFormatException e6) {
                                countStr = countStr2;
                                str = " is not a number at ";
                                String str3 = tagName;
                                int i7 = type;
                                i = 5;
                                PackageManagerService.reportSettingsProblem(i, "Error in package manager settings: <pastSigs> count " + countStr + str + parser.getPositionDescription());
                                xmlPullParser = parser;
                            }
                        }
                    } else {
                        int i8 = type;
                        PackageManagerService.reportSettingsProblem(5, "<pastSigs> encountered multiple times under the same <sigs> at " + parser.getPositionDescription());
                        XmlUtils.skipCurrentTag(parser);
                    }
                    xmlPullParser = parser;
                }
                xmlPullParser = parser;
            } else {
                int i9 = type;
                break;
            }
        }
        return pos;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(128);
        buf.append("PackageSignatures{");
        buf.append(Integer.toHexString(System.identityHashCode(this)));
        buf.append(" version:");
        buf.append(this.mSigningDetails.signatureSchemeVersion);
        buf.append(", signatures:[");
        if (this.mSigningDetails.signatures != null) {
            for (int i = 0; i < this.mSigningDetails.signatures.length; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(Integer.toHexString(this.mSigningDetails.signatures[i].hashCode()));
            }
        }
        buf.append("]");
        buf.append(", past signatures:[");
        if (this.mSigningDetails.pastSigningCertificates != null) {
            for (int i2 = 0; i2 < this.mSigningDetails.pastSigningCertificates.length; i2++) {
                if (i2 > 0) {
                    buf.append(", ");
                }
                buf.append(Integer.toHexString(this.mSigningDetails.pastSigningCertificates[i2].hashCode()));
                buf.append(" flags: ");
                buf.append(Integer.toHexString(this.mSigningDetails.pastSigningCertificates[i2].getFlags()));
            }
        }
        buf.append("]}");
        return buf.toString();
    }
}

package com.android.server.biometrics.fingerprint;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.fingerprint.Fingerprint;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.server.biometrics.BiometricUserState;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class FingerprintUserState extends BiometricUserState {
    private static final String ATTR_DEVICE_ID = "deviceId";
    private static final String ATTR_FINGER_ID = "fingerId";
    private static final String ATTR_GROUP_ID = "groupId";
    private static final String ATTR_NAME = "name";
    private static final String FINGERPRINT_FILE = "settings_fingerprint.xml";
    private static final String TAG = "FingerprintState";
    private static final String TAG_FINGERPRINT = "fingerprint";
    private static final String TAG_FINGERPRINTS = "fingerprints";

    public FingerprintUserState(Context context, int userId) {
        super(context, userId);
    }

    /* access modifiers changed from: protected */
    public String getBiometricsTag() {
        return TAG_FINGERPRINTS;
    }

    /* access modifiers changed from: protected */
    public String getBiometricFile() {
        return FINGERPRINT_FILE;
    }

    /* access modifiers changed from: protected */
    public int getNameTemplateResource() {
        return 17040077;
    }

    public void addBiometric(BiometricAuthenticator.Identifier identifier) {
        if (identifier instanceof Fingerprint) {
            super.addBiometric(identifier);
        } else {
            Slog.w(TAG, "Attempted to add non-fingerprint identifier");
        }
    }

    /* access modifiers changed from: protected */
    public ArrayList getCopy(ArrayList array) {
        ArrayList<Fingerprint> result = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Fingerprint fp = (Fingerprint) array.get(i);
            result.add(new Fingerprint(fp.getName(), fp.getGroupId(), fp.getBiometricId(), fp.getDeviceId()));
        }
        return result;
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: protected */
    public void doWriteState() {
        ArrayList<Fingerprint> fingerprints;
        AtomicFile destination = new AtomicFile(this.mFile);
        synchronized (this) {
            fingerprints = getCopy(this.mBiometrics);
        }
        try {
            FileOutputStream out = destination.startWrite();
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(out, "utf-8");
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startDocument((String) null, true);
            serializer.startTag((String) null, TAG_FINGERPRINTS);
            int count = fingerprints.size();
            for (int i = 0; i < count; i++) {
                Fingerprint fp = fingerprints.get(i);
                serializer.startTag((String) null, TAG_FINGERPRINT);
                serializer.attribute((String) null, ATTR_FINGER_ID, Integer.toString(fp.getBiometricId()));
                serializer.attribute((String) null, "name", fp.getName().toString());
                serializer.attribute((String) null, ATTR_GROUP_ID, Integer.toString(fp.getGroupId()));
                serializer.attribute((String) null, ATTR_DEVICE_ID, Long.toString(fp.getDeviceId()));
                serializer.endTag((String) null, TAG_FINGERPRINT);
            }
            serializer.endTag((String) null, TAG_FINGERPRINTS);
            serializer.endDocument();
            destination.finishWrite(out);
            IoUtils.closeQuietly(out);
        } catch (Throwable t) {
            IoUtils.closeQuietly((AutoCloseable) null);
            throw t;
        }
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"this"})
    public void parseBiometricsLocked(XmlPullParser parser) throws IOException, XmlPullParserException {
        XmlPullParser xmlPullParser = parser;
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            } else if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            } else if (type == 3) {
                xmlPullParser = parser;
            } else if (type != 4) {
                if (parser.getName().equals(TAG_FINGERPRINT)) {
                    String name = xmlPullParser.getAttributeValue((String) null, "name");
                    String groupId = xmlPullParser.getAttributeValue((String) null, ATTR_GROUP_ID);
                    String fingerId = xmlPullParser.getAttributeValue((String) null, ATTR_FINGER_ID);
                    String deviceId = xmlPullParser.getAttributeValue((String) null, ATTR_DEVICE_ID);
                    ArrayList arrayList = this.mBiometrics;
                    Fingerprint fingerprint = r6;
                    Fingerprint fingerprint2 = new Fingerprint(name, Integer.parseInt(groupId), Integer.parseInt(fingerId), Long.parseLong(deviceId));
                    arrayList.add(fingerprint);
                }
                xmlPullParser = parser;
            }
        }
    }
}

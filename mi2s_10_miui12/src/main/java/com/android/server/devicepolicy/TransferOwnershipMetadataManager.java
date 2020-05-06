package com.android.server.devicepolicy;

import android.content.ComponentName;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.xmlpull.v1.XmlSerializer;

class TransferOwnershipMetadataManager {
    static final String ADMIN_TYPE_DEVICE_OWNER = "device-owner";
    static final String ADMIN_TYPE_PROFILE_OWNER = "profile-owner";
    public static final String OWNER_TRANSFER_METADATA_XML = "owner-transfer-metadata.xml";
    private static final String TAG = TransferOwnershipMetadataManager.class.getName();
    @VisibleForTesting
    static final String TAG_ADMIN_TYPE = "admin-type";
    @VisibleForTesting
    static final String TAG_SOURCE_COMPONENT = "source-component";
    @VisibleForTesting
    static final String TAG_TARGET_COMPONENT = "target-component";
    @VisibleForTesting
    static final String TAG_USER_ID = "user-id";
    private final Injector mInjector;

    TransferOwnershipMetadataManager() {
        this(new Injector());
    }

    @VisibleForTesting
    TransferOwnershipMetadataManager(Injector injector) {
        this.mInjector = injector;
    }

    /* access modifiers changed from: package-private */
    public boolean saveMetadataFile(Metadata params) {
        File transferOwnershipMetadataFile = new File(this.mInjector.getOwnerTransferMetadataDir(), OWNER_TRANSFER_METADATA_XML);
        AtomicFile atomicFile = new AtomicFile(transferOwnershipMetadataFile);
        FileOutputStream stream = null;
        try {
            stream = atomicFile.startWrite();
            XmlSerializer serializer = new FastXmlSerializer();
            serializer.setOutput(stream, StandardCharsets.UTF_8.name());
            serializer.startDocument((String) null, true);
            insertSimpleTag(serializer, TAG_USER_ID, Integer.toString(params.userId));
            insertSimpleTag(serializer, TAG_SOURCE_COMPONENT, params.sourceComponent.flattenToString());
            insertSimpleTag(serializer, TAG_TARGET_COMPONENT, params.targetComponent.flattenToString());
            insertSimpleTag(serializer, TAG_ADMIN_TYPE, params.adminType);
            serializer.endDocument();
            atomicFile.finishWrite(stream);
            return true;
        } catch (IOException e) {
            String str = TAG;
            Slog.e(str, "Caught exception while trying to save Owner Transfer Params to file " + transferOwnershipMetadataFile, e);
            transferOwnershipMetadataFile.delete();
            atomicFile.failWrite(stream);
            return false;
        }
    }

    private void insertSimpleTag(XmlSerializer serializer, String tagName, String value) throws IOException {
        serializer.startTag((String) null, tagName);
        serializer.text(value);
        serializer.endTag((String) null, tagName);
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0042, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004b, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.server.devicepolicy.TransferOwnershipMetadataManager.Metadata loadMetadataFile() {
        /*
            r6 = this;
            java.io.File r0 = new java.io.File
            com.android.server.devicepolicy.TransferOwnershipMetadataManager$Injector r1 = r6.mInjector
            java.io.File r1 = r1.getOwnerTransferMetadataDir()
            java.lang.String r2 = "owner-transfer-metadata.xml"
            r0.<init>(r1, r2)
            boolean r1 = r0.exists()
            r2 = 0
            if (r1 != 0) goto L_0x0016
            return r2
        L_0x0016:
            java.lang.String r1 = TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Loading TransferOwnershipMetadataManager from "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            android.util.Slog.d(r1, r3)
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ IOException | IllegalArgumentException | XmlPullParserException -> 0x004c }
            r1.<init>(r0)     // Catch:{ IOException | IllegalArgumentException | XmlPullParserException -> 0x004c }
            org.xmlpull.v1.XmlPullParser r3 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x0040 }
            r3.setInput(r1, r2)     // Catch:{ all -> 0x0040 }
            com.android.server.devicepolicy.TransferOwnershipMetadataManager$Metadata r4 = r6.parseMetadataFile(r3)     // Catch:{ all -> 0x0040 }
            r1.close()     // Catch:{ IOException | IllegalArgumentException | XmlPullParserException -> 0x004c }
            return r4
        L_0x0040:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0042 }
        L_0x0042:
            r4 = move-exception
            r1.close()     // Catch:{ all -> 0x0047 }
            goto L_0x004b
        L_0x0047:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ IOException | IllegalArgumentException | XmlPullParserException -> 0x004c }
        L_0x004b:
            throw r4     // Catch:{ IOException | IllegalArgumentException | XmlPullParserException -> 0x004c }
        L_0x004c:
            r1 = move-exception
            java.lang.String r3 = TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Caught exception while trying to load the owner transfer params from file "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            android.util.Slog.e(r3, r4, r1)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.TransferOwnershipMetadataManager.loadMetadataFile():com.android.server.devicepolicy.TransferOwnershipMetadataManager$Metadata");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.server.devicepolicy.TransferOwnershipMetadataManager.Metadata parseMetadataFile(org.xmlpull.v1.XmlPullParser r13) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r12 = this;
            int r0 = r13.getDepth()
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
        L_0x0008:
            int r5 = r13.next()
            r6 = r5
            r7 = 1
            if (r5 == r7) goto L_0x0087
            r5 = 3
            if (r6 != r5) goto L_0x0019
            int r8 = r13.getDepth()
            if (r8 <= r0) goto L_0x0087
        L_0x0019:
            if (r6 == r5) goto L_0x0008
            r8 = 4
            if (r6 != r8) goto L_0x001f
            goto L_0x0008
        L_0x001f:
            java.lang.String r8 = r13.getName()
            r9 = -1
            int r10 = r8.hashCode()
            r11 = 2
            switch(r10) {
                case -337219647: goto L_0x004d;
                case -147180963: goto L_0x0042;
                case 281362891: goto L_0x0037;
                case 641951480: goto L_0x002d;
                default: goto L_0x002c;
            }
        L_0x002c:
            goto L_0x0058
        L_0x002d:
            java.lang.String r10 = "admin-type"
            boolean r8 = r8.equals(r10)
            if (r8 == 0) goto L_0x002c
            r8 = r5
            goto L_0x0059
        L_0x0037:
            java.lang.String r10 = "source-component"
            boolean r8 = r8.equals(r10)
            if (r8 == 0) goto L_0x002c
            r8 = r11
            goto L_0x0059
        L_0x0042:
            java.lang.String r10 = "user-id"
            boolean r8 = r8.equals(r10)
            if (r8 == 0) goto L_0x002c
            r8 = 0
            goto L_0x0059
        L_0x004d:
            java.lang.String r10 = "target-component"
            boolean r8 = r8.equals(r10)
            if (r8 == 0) goto L_0x002c
            r8 = r7
            goto L_0x0059
        L_0x0058:
            r8 = r9
        L_0x0059:
            if (r8 == 0) goto L_0x007a
            if (r8 == r7) goto L_0x0072
            if (r8 == r11) goto L_0x006a
            if (r8 == r5) goto L_0x0062
            goto L_0x0086
        L_0x0062:
            r13.next()
            java.lang.String r4 = r13.getText()
            goto L_0x0086
        L_0x006a:
            r13.next()
            java.lang.String r2 = r13.getText()
            goto L_0x0086
        L_0x0072:
            r13.next()
            java.lang.String r3 = r13.getText()
            goto L_0x0086
        L_0x007a:
            r13.next()
            java.lang.String r5 = r13.getText()
            int r1 = java.lang.Integer.parseInt(r5)
        L_0x0086:
            goto L_0x0008
        L_0x0087:
            com.android.server.devicepolicy.TransferOwnershipMetadataManager$Metadata r5 = new com.android.server.devicepolicy.TransferOwnershipMetadataManager$Metadata
            r5.<init>((java.lang.String) r2, (java.lang.String) r3, (int) r1, (java.lang.String) r4)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.TransferOwnershipMetadataManager.parseMetadataFile(org.xmlpull.v1.XmlPullParser):com.android.server.devicepolicy.TransferOwnershipMetadataManager$Metadata");
    }

    /* access modifiers changed from: package-private */
    public void deleteMetadataFile() {
        new File(this.mInjector.getOwnerTransferMetadataDir(), OWNER_TRANSFER_METADATA_XML).delete();
    }

    /* access modifiers changed from: package-private */
    public boolean metadataFileExists() {
        return new File(this.mInjector.getOwnerTransferMetadataDir(), OWNER_TRANSFER_METADATA_XML).exists();
    }

    static class Metadata {
        final String adminType;
        final ComponentName sourceComponent;
        final ComponentName targetComponent;
        final int userId;

        Metadata(ComponentName sourceComponent2, ComponentName targetComponent2, int userId2, String adminType2) {
            this.sourceComponent = sourceComponent2;
            this.targetComponent = targetComponent2;
            Preconditions.checkNotNull(sourceComponent2);
            Preconditions.checkNotNull(targetComponent2);
            Preconditions.checkStringNotEmpty(adminType2);
            this.userId = userId2;
            this.adminType = adminType2;
        }

        Metadata(String flatSourceComponent, String flatTargetComponent, int userId2, String adminType2) {
            this(unflattenComponentUnchecked(flatSourceComponent), unflattenComponentUnchecked(flatTargetComponent), userId2, adminType2);
        }

        private static ComponentName unflattenComponentUnchecked(String flatComponent) {
            Preconditions.checkNotNull(flatComponent);
            return ComponentName.unflattenFromString(flatComponent);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Metadata)) {
                return false;
            }
            Metadata params = (Metadata) obj;
            if (this.userId != params.userId || !this.sourceComponent.equals(params.sourceComponent) || !this.targetComponent.equals(params.targetComponent) || !TextUtils.equals(this.adminType, params.adminType)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (((((((1 * 31) + this.userId) * 31) + this.sourceComponent.hashCode()) * 31) + this.targetComponent.hashCode()) * 31) + this.adminType.hashCode();
        }
    }

    @VisibleForTesting
    static class Injector {
        Injector() {
        }

        public File getOwnerTransferMetadataDir() {
            return Environment.getDataSystemDirectory();
        }
    }
}

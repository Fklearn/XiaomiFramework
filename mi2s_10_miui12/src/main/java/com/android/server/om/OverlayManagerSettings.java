package com.android.server.om;

import android.content.om.OverlayInfo;
import android.util.ArrayMap;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.XmlUtils;
import com.android.server.om.OverlayManagerSettings;
import com.android.server.pm.PackageManagerService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

final class OverlayManagerSettings {
    private final ArrayList<SettingsItem> mItems = new ArrayList<>();

    OverlayManagerSettings() {
    }

    /* access modifiers changed from: package-private */
    public void init(String packageName, int userId, String targetPackageName, String targetOverlayableName, String baseCodePath, boolean isStatic, int priority, String overlayCategory) {
        remove(packageName, userId);
        SettingsItem item = new SettingsItem(packageName, userId, targetPackageName, targetOverlayableName, baseCodePath, isStatic, priority, overlayCategory);
        if (isStatic) {
            boolean unused = item.setEnabled(true);
            int i = this.mItems.size() - 1;
            while (true) {
                if (i < 0) {
                    int i2 = priority;
                    break;
                }
                SettingsItem parentItem = this.mItems.get(i);
                if (!parentItem.mIsStatic) {
                    int i3 = priority;
                } else if (parentItem.mPriority <= priority) {
                    break;
                }
                i--;
            }
            int pos = i + 1;
            if (pos == this.mItems.size()) {
                this.mItems.add(item);
            } else {
                this.mItems.add(pos, item);
            }
        } else {
            int i4 = priority;
            this.mItems.add(item);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean remove(String packageName, int userId) {
        int idx = select(packageName, userId);
        if (idx < 0) {
            return false;
        }
        this.mItems.remove(idx);
        return true;
    }

    /* access modifiers changed from: package-private */
    public OverlayInfo getOverlayInfo(String packageName, int userId) throws BadKeyException {
        int idx = select(packageName, userId);
        if (idx >= 0) {
            return this.mItems.get(idx).getOverlayInfo();
        }
        throw new BadKeyException(packageName, userId);
    }

    /* access modifiers changed from: package-private */
    public boolean setBaseCodePath(String packageName, int userId, String path) throws BadKeyException {
        int idx = select(packageName, userId);
        if (idx >= 0) {
            return this.mItems.get(idx).setBaseCodePath(path);
        }
        throw new BadKeyException(packageName, userId);
    }

    /* access modifiers changed from: package-private */
    public boolean setCategory(String packageName, int userId, String category) throws BadKeyException {
        int idx = select(packageName, userId);
        if (idx >= 0) {
            return this.mItems.get(idx).setCategory(category);
        }
        throw new BadKeyException(packageName, userId);
    }

    /* access modifiers changed from: package-private */
    public boolean getEnabled(String packageName, int userId) throws BadKeyException {
        int idx = select(packageName, userId);
        if (idx >= 0) {
            return this.mItems.get(idx).isEnabled();
        }
        throw new BadKeyException(packageName, userId);
    }

    /* access modifiers changed from: package-private */
    public boolean setEnabled(String packageName, int userId, boolean enable) throws BadKeyException {
        int idx = select(packageName, userId);
        if (idx >= 0) {
            return this.mItems.get(idx).setEnabled(enable);
        }
        throw new BadKeyException(packageName, userId);
    }

    /* access modifiers changed from: package-private */
    public int getState(String packageName, int userId) throws BadKeyException {
        int idx = select(packageName, userId);
        if (idx >= 0) {
            return this.mItems.get(idx).getState();
        }
        throw new BadKeyException(packageName, userId);
    }

    /* access modifiers changed from: package-private */
    public boolean setState(String packageName, int userId, int state) throws BadKeyException {
        int idx = select(packageName, userId);
        if (idx >= 0) {
            return this.mItems.get(idx).setState(state);
        }
        throw new BadKeyException(packageName, userId);
    }

    /* access modifiers changed from: package-private */
    public List<OverlayInfo> getOverlaysForTarget(String targetPackageName, int userId) {
        return (List) selectWhereTarget(targetPackageName, userId).filter($$Lambda$OverlayManagerSettings$ATr0DZmWpSWdKD0COw4t2qSDRk.INSTANCE).map($$Lambda$OverlayManagerSettings$WYtPK6Ebqjgxm8_8Cotijv_z_8.INSTANCE).collect(Collectors.toList());
    }

    static /* synthetic */ boolean lambda$getOverlaysForTarget$0(SettingsItem i) {
        return !i.isStatic() || !PackageManagerService.PLATFORM_PACKAGE_NAME.equals(i.getTargetPackageName());
    }

    /* access modifiers changed from: package-private */
    public ArrayMap<String, List<OverlayInfo>> getOverlaysForUser(int userId) {
        return (ArrayMap) selectWhereUser(userId).filter($$Lambda$OverlayManagerSettings$IkswmT9ZZJXmNAztGRVrD3hODMw.INSTANCE).map($$Lambda$OverlayManagerSettings$jZUujzDxrP0hpAqUxnqEfbnQc.INSTANCE).collect(Collectors.groupingBy($$Lambda$OverlayManagerSettings$sx0Nyvq91kCH_A4Ctf09G_0u9M.INSTANCE, $$Lambda$bXuJGR0fITXNwGnQfQHv9KSXgY.INSTANCE, Collectors.toList()));
    }

    static /* synthetic */ boolean lambda$getOverlaysForUser$2(SettingsItem i) {
        return !i.isStatic() || !PackageManagerService.PLATFORM_PACKAGE_NAME.equals(i.getTargetPackageName());
    }

    /* access modifiers changed from: package-private */
    public int[] getUsers() {
        return this.mItems.stream().mapToInt($$Lambda$OverlayManagerSettings$vXm2C4y9QF5yYZNimBLr6woI.INSTANCE).distinct().toArray();
    }

    /* access modifiers changed from: package-private */
    public boolean removeUser(int userId) {
        boolean removed = false;
        int i = 0;
        while (i < this.mItems.size()) {
            if (this.mItems.get(i).getUserId() == userId) {
                this.mItems.remove(i);
                removed = true;
                i--;
            }
            i++;
        }
        return removed;
    }

    /* access modifiers changed from: package-private */
    public boolean setPriority(String packageName, String newParentPackageName, int userId) {
        int moveIdx;
        int parentIdx;
        if (packageName.equals(newParentPackageName) || (moveIdx = select(packageName, userId)) < 0 || (parentIdx = select(newParentPackageName, userId)) < 0) {
            return false;
        }
        SettingsItem itemToMove = this.mItems.get(moveIdx);
        if (!itemToMove.getTargetPackageName().equals(this.mItems.get(parentIdx).getTargetPackageName())) {
            return false;
        }
        this.mItems.remove(moveIdx);
        int newParentIdx = select(newParentPackageName, userId) + 1;
        this.mItems.add(newParentIdx, itemToMove);
        if (moveIdx != newParentIdx) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean setLowestPriority(String packageName, int userId) {
        int idx = select(packageName, userId);
        if (idx <= 0) {
            return false;
        }
        SettingsItem item = this.mItems.get(idx);
        this.mItems.remove(item);
        this.mItems.add(0, item);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean setHighestPriority(String packageName, int userId) {
        int idx = select(packageName, userId);
        if (idx < 0 || idx == this.mItems.size() - 1) {
            return false;
        }
        this.mItems.remove(idx);
        this.mItems.add(this.mItems.get(idx));
        return true;
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter p, DumpState dumpState) {
        Stream<SettingsItem> items = this.mItems.stream();
        if (dumpState.getUserId() != -1) {
            items = items.filter(new Predicate() {
                public final boolean test(Object obj) {
                    return OverlayManagerSettings.lambda$dump$6(DumpState.this, (OverlayManagerSettings.SettingsItem) obj);
                }
            });
        }
        if (dumpState.getPackageName() != null) {
            items = items.filter(new Predicate() {
                public final boolean test(Object obj) {
                    return ((OverlayManagerSettings.SettingsItem) obj).mPackageName.equals(DumpState.this.getPackageName());
                }
            });
        }
        IndentingPrintWriter pw = new IndentingPrintWriter(p, "  ");
        if (dumpState.getField() != null) {
            items.forEach(new Consumer(pw, dumpState) {
                private final /* synthetic */ IndentingPrintWriter f$1;
                private final /* synthetic */ DumpState f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    OverlayManagerSettings.this.lambda$dump$8$OverlayManagerSettings(this.f$1, this.f$2, (OverlayManagerSettings.SettingsItem) obj);
                }
            });
        } else {
            items.forEach(new Consumer(pw) {
                private final /* synthetic */ IndentingPrintWriter f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    OverlayManagerSettings.this.lambda$dump$9$OverlayManagerSettings(this.f$1, (OverlayManagerSettings.SettingsItem) obj);
                }
            });
        }
    }

    static /* synthetic */ boolean lambda$dump$6(DumpState dumpState, SettingsItem item) {
        return item.mUserId == dumpState.getUserId();
    }

    public /* synthetic */ void lambda$dump$8$OverlayManagerSettings(IndentingPrintWriter pw, DumpState dumpState, SettingsItem item) {
        dumpSettingsItemField(pw, item, dumpState.getField());
    }

    /* access modifiers changed from: private */
    /* renamed from: dumpSettingsItem */
    public void lambda$dump$9$OverlayManagerSettings(IndentingPrintWriter pw, SettingsItem item) {
        pw.println(item.mPackageName + ":" + item.getUserId() + " {");
        pw.increaseIndent();
        StringBuilder sb = new StringBuilder();
        sb.append("mPackageName...........: ");
        sb.append(item.mPackageName);
        pw.println(sb.toString());
        pw.println("mUserId................: " + item.getUserId());
        pw.println("mTargetPackageName.....: " + item.getTargetPackageName());
        pw.println("mTargetOverlayableName.: " + item.getTargetOverlayableName());
        pw.println("mBaseCodePath..........: " + item.getBaseCodePath());
        pw.println("mState.................: " + OverlayInfo.stateToString(item.getState()));
        pw.println("mIsEnabled.............: " + item.isEnabled());
        pw.println("mIsStatic..............: " + item.isStatic());
        pw.println("mPriority..............: " + item.mPriority);
        pw.println("mCategory..............: " + item.mCategory);
        pw.decreaseIndent();
        pw.println("}");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void dumpSettingsItemField(com.android.internal.util.IndentingPrintWriter r2, com.android.server.om.OverlayManagerSettings.SettingsItem r3, java.lang.String r4) {
        /*
            r1 = this;
            int r0 = r4.hashCode()
            switch(r0) {
                case -1750736508: goto L_0x006c;
                case -1248283232: goto L_0x0061;
                case -1165461084: goto L_0x0055;
                case -836029914: goto L_0x004a;
                case 50511102: goto L_0x003f;
                case 109757585: goto L_0x0034;
                case 440941271: goto L_0x0029;
                case 697685016: goto L_0x001e;
                case 909712337: goto L_0x0013;
                case 1693907299: goto L_0x0009;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x0077
        L_0x0009:
            java.lang.String r0 = "basecodepath"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 4
            goto L_0x0078
        L_0x0013:
            java.lang.String r0 = "packagename"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 0
            goto L_0x0078
        L_0x001e:
            java.lang.String r0 = "isstatic"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 7
            goto L_0x0078
        L_0x0029:
            java.lang.String r0 = "isenabled"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 6
            goto L_0x0078
        L_0x0034:
            java.lang.String r0 = "state"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 5
            goto L_0x0078
        L_0x003f:
            java.lang.String r0 = "category"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 9
            goto L_0x0078
        L_0x004a:
            java.lang.String r0 = "userid"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 1
            goto L_0x0078
        L_0x0055:
            java.lang.String r0 = "priority"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 8
            goto L_0x0078
        L_0x0061:
            java.lang.String r0 = "targetpackagename"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 2
            goto L_0x0078
        L_0x006c:
            java.lang.String r0 = "targetoverlayablename"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 3
            goto L_0x0078
        L_0x0077:
            r0 = -1
        L_0x0078:
            switch(r0) {
                case 0: goto L_0x00c8;
                case 1: goto L_0x00c0;
                case 2: goto L_0x00b8;
                case 3: goto L_0x00b0;
                case 4: goto L_0x00a8;
                case 5: goto L_0x009c;
                case 6: goto L_0x0094;
                case 7: goto L_0x008c;
                case 8: goto L_0x0084;
                case 9: goto L_0x007c;
                default: goto L_0x007b;
            }
        L_0x007b:
            goto L_0x00d0
        L_0x007c:
            java.lang.String r0 = r3.mCategory
            r2.println(r0)
            goto L_0x00d0
        L_0x0084:
            int r0 = r3.mPriority
            r2.println(r0)
            goto L_0x00d0
        L_0x008c:
            boolean r0 = r3.mIsStatic
            r2.println(r0)
            goto L_0x00d0
        L_0x0094:
            boolean r0 = r3.mIsEnabled
            r2.println(r0)
            goto L_0x00d0
        L_0x009c:
            int r0 = r3.mState
            java.lang.String r0 = android.content.om.OverlayInfo.stateToString(r0)
            r2.println(r0)
            goto L_0x00d0
        L_0x00a8:
            java.lang.String r0 = r3.mBaseCodePath
            r2.println(r0)
            goto L_0x00d0
        L_0x00b0:
            java.lang.String r0 = r3.mTargetOverlayableName
            r2.println(r0)
            goto L_0x00d0
        L_0x00b8:
            java.lang.String r0 = r3.mTargetPackageName
            r2.println(r0)
            goto L_0x00d0
        L_0x00c0:
            int r0 = r3.mUserId
            r2.println(r0)
            goto L_0x00d0
        L_0x00c8:
            java.lang.String r0 = r3.mPackageName
            r2.println(r0)
        L_0x00d0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.om.OverlayManagerSettings.dumpSettingsItemField(com.android.internal.util.IndentingPrintWriter, com.android.server.om.OverlayManagerSettings$SettingsItem, java.lang.String):void");
    }

    /* access modifiers changed from: package-private */
    public void restore(InputStream is) throws IOException, XmlPullParserException {
        Serializer.restore(this.mItems, is);
    }

    /* access modifiers changed from: package-private */
    public void persist(OutputStream os) throws IOException, XmlPullParserException {
        Serializer.persist(this.mItems, os);
    }

    @VisibleForTesting
    static final class Serializer {
        private static final String ATTR_BASE_CODE_PATH = "baseCodePath";
        private static final String ATTR_CATEGORY = "category";
        private static final String ATTR_IS_ENABLED = "isEnabled";
        private static final String ATTR_IS_STATIC = "isStatic";
        private static final String ATTR_PACKAGE_NAME = "packageName";
        private static final String ATTR_PRIORITY = "priority";
        private static final String ATTR_STATE = "state";
        private static final String ATTR_TARGET_OVERLAYABLE_NAME = "targetOverlayableName";
        private static final String ATTR_TARGET_PACKAGE_NAME = "targetPackageName";
        private static final String ATTR_USER_ID = "userId";
        private static final String ATTR_VERSION = "version";
        @VisibleForTesting
        static final int CURRENT_VERSION = 3;
        private static final String TAG_ITEM = "item";
        private static final String TAG_OVERLAYS = "overlays";

        Serializer() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0058, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
            r0.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x005d, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x005e, code lost:
            r1.addSuppressed(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0061, code lost:
            throw r2;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static void restore(java.util.ArrayList<com.android.server.om.OverlayManagerSettings.SettingsItem> r8, java.io.InputStream r9) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
            /*
                java.io.InputStreamReader r0 = new java.io.InputStreamReader
                r0.<init>(r9)
                r8.clear()     // Catch:{ all -> 0x0056 }
                org.xmlpull.v1.XmlPullParser r1 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x0056 }
                r1.setInput(r0)     // Catch:{ all -> 0x0056 }
                java.lang.String r2 = "overlays"
                com.android.internal.util.XmlUtils.beginDocument(r1, r2)     // Catch:{ all -> 0x0056 }
                java.lang.String r2 = "version"
                int r2 = com.android.internal.util.XmlUtils.readIntAttribute(r1, r2)     // Catch:{ all -> 0x0056 }
                r3 = 3
                if (r2 == r3) goto L_0x0022
                upgrade(r2)     // Catch:{ all -> 0x0056 }
            L_0x0022:
                int r3 = r1.getDepth()     // Catch:{ all -> 0x0056 }
            L_0x0026:
                boolean r4 = com.android.internal.util.XmlUtils.nextElementWithin(r1, r3)     // Catch:{ all -> 0x0056 }
                if (r4 == 0) goto L_0x0052
                java.lang.String r4 = r1.getName()     // Catch:{ all -> 0x0056 }
                r5 = -1
                int r6 = r4.hashCode()     // Catch:{ all -> 0x0056 }
                r7 = 3242771(0x317b13, float:4.54409E-39)
                if (r6 == r7) goto L_0x003b
            L_0x003a:
                goto L_0x0045
            L_0x003b:
                java.lang.String r6 = "item"
                boolean r4 = r4.equals(r6)     // Catch:{ all -> 0x0056 }
                if (r4 == 0) goto L_0x003a
                r5 = 0
            L_0x0045:
                if (r5 == 0) goto L_0x0048
                goto L_0x0051
            L_0x0048:
                int r4 = r3 + 1
                com.android.server.om.OverlayManagerSettings$SettingsItem r4 = restoreRow(r1, r4)     // Catch:{ all -> 0x0056 }
                r8.add(r4)     // Catch:{ all -> 0x0056 }
            L_0x0051:
                goto L_0x0026
            L_0x0052:
                r0.close()
                return
            L_0x0056:
                r1 = move-exception
                throw r1     // Catch:{ all -> 0x0058 }
            L_0x0058:
                r2 = move-exception
                r0.close()     // Catch:{ all -> 0x005d }
                goto L_0x0061
            L_0x005d:
                r3 = move-exception
                r1.addSuppressed(r3)
            L_0x0061:
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.om.OverlayManagerSettings.Serializer.restore(java.util.ArrayList, java.io.InputStream):void");
        }

        private static void upgrade(int oldVersion) throws XmlPullParserException {
            if (oldVersion == 0 || oldVersion == 1 || oldVersion == 2) {
                throw new XmlPullParserException("old version " + oldVersion + "; ignoring");
            }
            throw new XmlPullParserException("unrecognized version " + oldVersion);
        }

        private static SettingsItem restoreRow(XmlPullParser parser, int depth) throws IOException {
            XmlPullParser xmlPullParser = parser;
            return new SettingsItem(XmlUtils.readStringAttribute(xmlPullParser, ATTR_PACKAGE_NAME), XmlUtils.readIntAttribute(xmlPullParser, ATTR_USER_ID), XmlUtils.readStringAttribute(xmlPullParser, ATTR_TARGET_PACKAGE_NAME), XmlUtils.readStringAttribute(xmlPullParser, ATTR_TARGET_OVERLAYABLE_NAME), XmlUtils.readStringAttribute(xmlPullParser, ATTR_BASE_CODE_PATH), XmlUtils.readIntAttribute(xmlPullParser, ATTR_STATE), XmlUtils.readBooleanAttribute(xmlPullParser, ATTR_IS_ENABLED), XmlUtils.readBooleanAttribute(xmlPullParser, ATTR_IS_STATIC), XmlUtils.readIntAttribute(xmlPullParser, ATTR_PRIORITY), XmlUtils.readStringAttribute(xmlPullParser, ATTR_CATEGORY));
        }

        public static void persist(ArrayList<SettingsItem> table, OutputStream os) throws IOException, XmlPullParserException {
            FastXmlSerializer xml = new FastXmlSerializer();
            xml.setOutput(os, "utf-8");
            xml.startDocument((String) null, true);
            xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            xml.startTag((String) null, TAG_OVERLAYS);
            XmlUtils.writeIntAttribute(xml, ATTR_VERSION, 3);
            int n = table.size();
            for (int i = 0; i < n; i++) {
                persistRow(xml, table.get(i));
            }
            xml.endTag((String) null, TAG_OVERLAYS);
            xml.endDocument();
        }

        private static void persistRow(FastXmlSerializer xml, SettingsItem item) throws IOException {
            xml.startTag((String) null, "item");
            XmlUtils.writeStringAttribute(xml, ATTR_PACKAGE_NAME, item.mPackageName);
            XmlUtils.writeIntAttribute(xml, ATTR_USER_ID, item.mUserId);
            XmlUtils.writeStringAttribute(xml, ATTR_TARGET_PACKAGE_NAME, item.mTargetPackageName);
            XmlUtils.writeStringAttribute(xml, ATTR_TARGET_OVERLAYABLE_NAME, item.mTargetOverlayableName);
            XmlUtils.writeStringAttribute(xml, ATTR_BASE_CODE_PATH, item.mBaseCodePath);
            XmlUtils.writeIntAttribute(xml, ATTR_STATE, item.mState);
            XmlUtils.writeBooleanAttribute(xml, ATTR_IS_ENABLED, item.mIsEnabled);
            XmlUtils.writeBooleanAttribute(xml, ATTR_IS_STATIC, item.mIsStatic);
            XmlUtils.writeIntAttribute(xml, ATTR_PRIORITY, item.mPriority);
            XmlUtils.writeStringAttribute(xml, ATTR_CATEGORY, item.mCategory);
            xml.endTag((String) null, "item");
        }
    }

    private static final class SettingsItem {
        /* access modifiers changed from: private */
        public String mBaseCodePath;
        private OverlayInfo mCache;
        /* access modifiers changed from: private */
        public String mCategory;
        /* access modifiers changed from: private */
        public boolean mIsEnabled;
        /* access modifiers changed from: private */
        public boolean mIsStatic;
        /* access modifiers changed from: private */
        public final String mPackageName;
        /* access modifiers changed from: private */
        public int mPriority;
        /* access modifiers changed from: private */
        public int mState;
        /* access modifiers changed from: private */
        public final String mTargetOverlayableName;
        /* access modifiers changed from: private */
        public final String mTargetPackageName;
        /* access modifiers changed from: private */
        public final int mUserId;

        SettingsItem(String packageName, int userId, String targetPackageName, String targetOverlayableName, String baseCodePath, int state, boolean isEnabled, boolean isStatic, int priority, String category) {
            this.mPackageName = packageName;
            this.mUserId = userId;
            this.mTargetPackageName = targetPackageName;
            this.mTargetOverlayableName = targetOverlayableName;
            this.mBaseCodePath = baseCodePath;
            this.mState = state;
            this.mIsEnabled = isEnabled || isStatic;
            this.mCategory = category;
            this.mCache = null;
            this.mIsStatic = isStatic;
            this.mPriority = priority;
        }

        SettingsItem(String packageName, int userId, String targetPackageName, String targetOverlayableName, String baseCodePath, boolean isStatic, int priority, String category) {
            this(packageName, userId, targetPackageName, targetOverlayableName, baseCodePath, -1, false, isStatic, priority, category);
        }

        /* access modifiers changed from: private */
        public String getTargetPackageName() {
            return this.mTargetPackageName;
        }

        /* access modifiers changed from: private */
        public String getTargetOverlayableName() {
            return this.mTargetOverlayableName;
        }

        /* access modifiers changed from: private */
        public int getUserId() {
            return this.mUserId;
        }

        /* access modifiers changed from: private */
        public String getBaseCodePath() {
            return this.mBaseCodePath;
        }

        /* access modifiers changed from: private */
        public boolean setBaseCodePath(String path) {
            if (this.mBaseCodePath.equals(path)) {
                return false;
            }
            this.mBaseCodePath = path;
            invalidateCache();
            return true;
        }

        /* access modifiers changed from: private */
        public int getState() {
            return this.mState;
        }

        /* access modifiers changed from: private */
        public boolean setState(int state) {
            if (this.mState == state) {
                return false;
            }
            this.mState = state;
            invalidateCache();
            return true;
        }

        /* access modifiers changed from: private */
        public boolean isEnabled() {
            return this.mIsEnabled;
        }

        /* access modifiers changed from: private */
        public boolean setEnabled(boolean enable) {
            if (this.mIsStatic || this.mIsEnabled == enable) {
                return false;
            }
            this.mIsEnabled = enable;
            invalidateCache();
            return true;
        }

        /* access modifiers changed from: private */
        public boolean setCategory(String category) {
            if (Objects.equals(this.mCategory, category)) {
                return false;
            }
            this.mCategory = category == null ? null : category.intern();
            invalidateCache();
            return true;
        }

        /* access modifiers changed from: private */
        public OverlayInfo getOverlayInfo() {
            if (this.mCache == null) {
                this.mCache = new OverlayInfo(this.mPackageName, this.mTargetPackageName, this.mTargetOverlayableName, this.mCategory, this.mBaseCodePath, this.mState, this.mUserId, this.mPriority, this.mIsStatic);
            }
            return this.mCache;
        }

        private void invalidateCache() {
            this.mCache = null;
        }

        /* access modifiers changed from: private */
        public boolean isStatic() {
            return this.mIsStatic;
        }

        private int getPriority() {
            return this.mPriority;
        }
    }

    private int select(String packageName, int userId) {
        int n = this.mItems.size();
        for (int i = 0; i < n; i++) {
            SettingsItem item = this.mItems.get(i);
            if (item.mUserId == userId && item.mPackageName.equals(packageName)) {
                return i;
            }
        }
        return -1;
    }

    static /* synthetic */ boolean lambda$selectWhereUser$10(int userId, SettingsItem item) {
        return item.mUserId == userId;
    }

    private Stream<SettingsItem> selectWhereUser(int userId) {
        return this.mItems.stream().filter(new Predicate(userId) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return OverlayManagerSettings.lambda$selectWhereUser$10(this.f$0, (OverlayManagerSettings.SettingsItem) obj);
            }
        });
    }

    private Stream<SettingsItem> selectWhereTarget(String targetPackageName, int userId) {
        return selectWhereUser(userId).filter(new Predicate(targetPackageName) {
            private final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ((OverlayManagerSettings.SettingsItem) obj).getTargetPackageName().equals(this.f$0);
            }
        });
    }

    static final class BadKeyException extends RuntimeException {
        BadKeyException(String packageName, int userId) {
            super("Bad key mPackageName=" + packageName + " mUserId=" + userId);
        }
    }
}

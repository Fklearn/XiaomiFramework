package com.android.server.wm;

import android.os.Environment;
import android.provider.Settings;
import android.util.AtomicFile;
import android.util.Slog;
import android.view.DisplayAddress;
import android.view.DisplayInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class DisplayWindowSettings {
    private static final int IDENTIFIER_PORT = 1;
    private static final int IDENTIFIER_UNIQUE_ID = 0;
    private static final String TAG = "WindowManager";
    private final HashMap<String, Entry> mEntries;
    @DisplayIdentifierType
    private int mIdentifier;
    private final WindowManagerService mService;
    private final SettingPersister mStorage;

    @interface DisplayIdentifierType {
    }

    interface SettingPersister {
        void finishWrite(OutputStream outputStream, boolean z);

        InputStream openRead() throws IOException;

        OutputStream startWrite() throws IOException;
    }

    private static class Entry {
        /* access modifiers changed from: private */
        public int mFixedToUserRotation;
        /* access modifiers changed from: private */
        public int mForcedDensity;
        /* access modifiers changed from: private */
        public int mForcedHeight;
        /* access modifiers changed from: private */
        public int mForcedScalingMode;
        /* access modifiers changed from: private */
        public int mForcedWidth;
        /* access modifiers changed from: private */
        public final String mName;
        /* access modifiers changed from: private */
        public int mOverscanBottom;
        /* access modifiers changed from: private */
        public int mOverscanLeft;
        /* access modifiers changed from: private */
        public int mOverscanRight;
        /* access modifiers changed from: private */
        public int mOverscanTop;
        /* access modifiers changed from: private */
        public int mRemoveContentMode;
        /* access modifiers changed from: private */
        public boolean mShouldShowIme;
        /* access modifiers changed from: private */
        public boolean mShouldShowSystemDecors;
        /* access modifiers changed from: private */
        public boolean mShouldShowWithInsecureKeyguard;
        /* access modifiers changed from: private */
        public int mUserRotation;
        /* access modifiers changed from: private */
        public int mUserRotationMode;
        /* access modifiers changed from: private */
        public int mWindowingMode;

        private Entry(String name) {
            this.mWindowingMode = 0;
            this.mUserRotationMode = 0;
            this.mUserRotation = 0;
            this.mForcedScalingMode = 0;
            this.mRemoveContentMode = 0;
            this.mShouldShowWithInsecureKeyguard = false;
            this.mShouldShowSystemDecors = false;
            this.mShouldShowIme = false;
            this.mFixedToUserRotation = 0;
            this.mName = name;
        }

        private Entry(String name, Entry copyFrom) {
            this(name);
            this.mOverscanLeft = copyFrom.mOverscanLeft;
            this.mOverscanTop = copyFrom.mOverscanTop;
            this.mOverscanRight = copyFrom.mOverscanRight;
            this.mOverscanBottom = copyFrom.mOverscanBottom;
            this.mWindowingMode = copyFrom.mWindowingMode;
            this.mUserRotationMode = copyFrom.mUserRotationMode;
            this.mUserRotation = copyFrom.mUserRotation;
            this.mForcedWidth = copyFrom.mForcedWidth;
            this.mForcedHeight = copyFrom.mForcedHeight;
            this.mForcedDensity = copyFrom.mForcedDensity;
            this.mForcedScalingMode = copyFrom.mForcedScalingMode;
            this.mRemoveContentMode = copyFrom.mRemoveContentMode;
            this.mShouldShowWithInsecureKeyguard = copyFrom.mShouldShowWithInsecureKeyguard;
            this.mShouldShowSystemDecors = copyFrom.mShouldShowSystemDecors;
            this.mShouldShowIme = copyFrom.mShouldShowIme;
            this.mFixedToUserRotation = copyFrom.mFixedToUserRotation;
        }

        /* access modifiers changed from: private */
        public boolean isEmpty() {
            return this.mOverscanLeft == 0 && this.mOverscanTop == 0 && this.mOverscanRight == 0 && this.mOverscanBottom == 0 && this.mWindowingMode == 0 && this.mUserRotationMode == 0 && this.mUserRotation == 0 && this.mForcedWidth == 0 && this.mForcedHeight == 0 && this.mForcedDensity == 0 && this.mForcedScalingMode == 0 && this.mRemoveContentMode == 0 && !this.mShouldShowWithInsecureKeyguard && !this.mShouldShowSystemDecors && !this.mShouldShowIme && this.mFixedToUserRotation == 0;
        }
    }

    DisplayWindowSettings(WindowManagerService service) {
        this(service, new AtomicFileStorage());
    }

    @VisibleForTesting
    DisplayWindowSettings(WindowManagerService service, SettingPersister storageImpl) {
        this.mEntries = new HashMap<>();
        this.mIdentifier = 0;
        this.mService = service;
        this.mStorage = storageImpl;
        readSettings();
    }

    private Entry getEntry(DisplayInfo displayInfo) {
        Entry entry = this.mEntries.get(getIdentifier(displayInfo));
        Entry entry2 = entry;
        if (entry != null) {
            return entry2;
        }
        Entry entry3 = this.mEntries.get(displayInfo.name);
        Entry entry4 = entry3;
        if (entry3 != null) {
            return updateIdentifierForEntry(entry4, displayInfo);
        }
        return null;
    }

    private Entry getOrCreateEntry(DisplayInfo displayInfo) {
        Entry entry = getEntry(displayInfo);
        return entry != null ? entry : new Entry(getIdentifier(displayInfo));
    }

    private Entry updateIdentifierForEntry(Entry entry, DisplayInfo displayInfo) {
        Entry newEntry = new Entry(getIdentifier(displayInfo), entry);
        removeEntry(displayInfo);
        this.mEntries.put(newEntry.mName, newEntry);
        return newEntry;
    }

    /* access modifiers changed from: package-private */
    public void setOverscanLocked(DisplayInfo displayInfo, int left, int top, int right, int bottom) {
        Entry entry = getOrCreateEntry(displayInfo);
        int unused = entry.mOverscanLeft = left;
        int unused2 = entry.mOverscanTop = top;
        int unused3 = entry.mOverscanRight = right;
        int unused4 = entry.mOverscanBottom = bottom;
        writeSettingsIfNeeded(entry, displayInfo);
    }

    /* access modifiers changed from: package-private */
    public void setUserRotation(DisplayContent displayContent, int rotationMode, int rotation) {
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        Entry entry = getOrCreateEntry(displayInfo);
        int unused = entry.mUserRotationMode = rotationMode;
        int unused2 = entry.mUserRotation = rotation;
        writeSettingsIfNeeded(entry, displayInfo);
    }

    /* access modifiers changed from: package-private */
    public void setForcedSize(DisplayContent displayContent, int width, int height) {
        String sizeString;
        if (displayContent.isDefaultDisplay) {
            if (width == 0 || height == 0) {
                sizeString = "";
            } else {
                sizeString = width + "," + height;
            }
            Settings.Global.putString(this.mService.mContext.getContentResolver(), "display_size_forced", sizeString);
            return;
        }
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        Entry entry = getOrCreateEntry(displayInfo);
        int unused = entry.mForcedWidth = width;
        int unused2 = entry.mForcedHeight = height;
        writeSettingsIfNeeded(entry, displayInfo);
    }

    /* access modifiers changed from: package-private */
    public void setForcedDensity(DisplayContent displayContent, int density, int userId) {
        if (displayContent.isDefaultDisplay) {
            Settings.Secure.putStringForUser(this.mService.mContext.getContentResolver(), "display_density_forced", density == 0 ? "" : Integer.toString(density), userId);
            return;
        }
        String densityString = displayContent.getDisplayInfo();
        Entry entry = getOrCreateEntry(densityString);
        int unused = entry.mForcedDensity = density;
        writeSettingsIfNeeded(entry, densityString);
    }

    /* access modifiers changed from: package-private */
    public void setForcedScalingMode(DisplayContent displayContent, int mode) {
        if (displayContent.isDefaultDisplay) {
            Settings.Global.putInt(this.mService.mContext.getContentResolver(), "display_scaling_force", mode);
            return;
        }
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        Entry entry = getOrCreateEntry(displayInfo);
        int unused = entry.mForcedScalingMode = mode;
        writeSettingsIfNeeded(entry, displayInfo);
    }

    /* access modifiers changed from: package-private */
    public void setFixedToUserRotation(DisplayContent displayContent, int fixedToUserRotation) {
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        Entry entry = getOrCreateEntry(displayInfo);
        int unused = entry.mFixedToUserRotation = fixedToUserRotation;
        writeSettingsIfNeeded(entry, displayInfo);
    }

    private int getWindowingModeLocked(Entry entry, int displayId) {
        int windowingMode;
        boolean forceDesktopMode = false;
        if (entry != null) {
            windowingMode = entry.mWindowingMode;
        } else {
            windowingMode = 0;
        }
        int windowingMode2 = 5;
        if (windowingMode == 5 && !this.mService.mSupportsFreeformWindowManagement) {
            return 1;
        }
        if (windowingMode != 0) {
            return windowingMode;
        }
        if (this.mService.mForceDesktopModeOnExternalDisplays && displayId != 0) {
            forceDesktopMode = true;
        }
        if (!this.mService.mSupportsFreeformWindowManagement || (!this.mService.mIsPc && !forceDesktopMode)) {
            windowingMode2 = 1;
        }
        return windowingMode2;
    }

    /* access modifiers changed from: package-private */
    public int getWindowingModeLocked(DisplayContent dc) {
        return getWindowingModeLocked(getEntry(dc.getDisplayInfo()), dc.getDisplayId());
    }

    /* access modifiers changed from: package-private */
    public void setWindowingModeLocked(DisplayContent dc, int mode) {
        DisplayInfo displayInfo = dc.getDisplayInfo();
        Entry entry = getOrCreateEntry(displayInfo);
        int unused = entry.mWindowingMode = mode;
        dc.setWindowingMode(mode);
        writeSettingsIfNeeded(entry, displayInfo);
    }

    /* access modifiers changed from: package-private */
    public int getRemoveContentModeLocked(DisplayContent dc) {
        Entry entry = getEntry(dc.getDisplayInfo());
        if (entry != null && entry.mRemoveContentMode != 0) {
            return entry.mRemoveContentMode;
        }
        if (dc.isPrivate()) {
            return 2;
        }
        return 1;
    }

    /* access modifiers changed from: package-private */
    public void setRemoveContentModeLocked(DisplayContent dc, int mode) {
        DisplayInfo displayInfo = dc.getDisplayInfo();
        Entry entry = getOrCreateEntry(displayInfo);
        int unused = entry.mRemoveContentMode = mode;
        writeSettingsIfNeeded(entry, displayInfo);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldShowWithInsecureKeyguardLocked(DisplayContent dc) {
        Entry entry = getEntry(dc.getDisplayInfo());
        if (entry == null) {
            return false;
        }
        return entry.mShouldShowWithInsecureKeyguard;
    }

    /* access modifiers changed from: package-private */
    public void setShouldShowWithInsecureKeyguardLocked(DisplayContent dc, boolean shouldShow) {
        if (dc.isPrivate() || !shouldShow) {
            DisplayInfo displayInfo = dc.getDisplayInfo();
            Entry entry = getOrCreateEntry(displayInfo);
            boolean unused = entry.mShouldShowWithInsecureKeyguard = shouldShow;
            writeSettingsIfNeeded(entry, displayInfo);
            return;
        }
        Slog.e("WindowManager", "Public display can't be allowed to show content when locked");
    }

    /* access modifiers changed from: package-private */
    public boolean shouldShowSystemDecorsLocked(DisplayContent dc) {
        if (dc.getDisplayId() == 0) {
            return true;
        }
        Entry entry = getEntry(dc.getDisplayInfo());
        if (entry == null) {
            return false;
        }
        return entry.mShouldShowSystemDecors;
    }

    /* access modifiers changed from: package-private */
    public void setShouldShowSystemDecorsLocked(DisplayContent dc, boolean shouldShow) {
        if (dc.getDisplayId() != 0 || shouldShow) {
            DisplayInfo displayInfo = dc.getDisplayInfo();
            Entry entry = getOrCreateEntry(displayInfo);
            boolean unused = entry.mShouldShowSystemDecors = shouldShow;
            writeSettingsIfNeeded(entry, displayInfo);
            return;
        }
        Slog.e("WindowManager", "Default display should show system decors");
    }

    /* access modifiers changed from: package-private */
    public boolean shouldShowImeLocked(DisplayContent dc) {
        if (dc.getDisplayId() == 0) {
            return true;
        }
        Entry entry = getEntry(dc.getDisplayInfo());
        if (entry == null) {
            return false;
        }
        return entry.mShouldShowIme;
    }

    /* access modifiers changed from: package-private */
    public void setShouldShowImeLocked(DisplayContent dc, boolean shouldShow) {
        if (dc.getDisplayId() != 0 || shouldShow) {
            DisplayInfo displayInfo = dc.getDisplayInfo();
            Entry entry = getOrCreateEntry(displayInfo);
            boolean unused = entry.mShouldShowIme = shouldShow;
            writeSettingsIfNeeded(entry, displayInfo);
            return;
        }
        Slog.e("WindowManager", "Default display should show IME");
    }

    /* access modifiers changed from: package-private */
    public void applySettingsToDisplayLocked(DisplayContent dc) {
        DisplayInfo displayInfo = dc.getDisplayInfo();
        Entry entry = getOrCreateEntry(displayInfo);
        dc.setWindowingMode(getWindowingModeLocked(entry, dc.getDisplayId()));
        displayInfo.overscanLeft = entry.mOverscanLeft;
        displayInfo.overscanTop = entry.mOverscanTop;
        displayInfo.overscanRight = entry.mOverscanRight;
        displayInfo.overscanBottom = entry.mOverscanBottom;
        dc.getDisplayRotation().restoreSettings(entry.mUserRotationMode, entry.mUserRotation, entry.mFixedToUserRotation);
        if (entry.mForcedDensity != 0) {
            dc.mBaseDisplayDensity = entry.mForcedDensity;
        }
        if (!(entry.mForcedWidth == 0 || entry.mForcedHeight == 0)) {
            dc.updateBaseDisplayMetrics(entry.mForcedWidth, entry.mForcedHeight, dc.mBaseDisplayDensity);
        }
        boolean z = true;
        if (entry.mForcedScalingMode != 1) {
            z = false;
        }
        dc.mDisplayScalingDisabled = z;
    }

    /* access modifiers changed from: package-private */
    public boolean updateSettingsForDisplay(DisplayContent dc) {
        if (dc.getWindowingMode() == getWindowingModeLocked(dc)) {
            return false;
        }
        dc.setWindowingMode(getWindowingModeLocked(dc));
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0027 A[Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094, all -> 0x0091 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0089 A[SYNTHETIC, Splitter:B:35:0x0089] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:32:0x0081=Splitter:B:32:0x0081, B:65:0x010c=Splitter:B:65:0x010c, B:72:0x012c=Splitter:B:72:0x012c, B:79:0x014c=Splitter:B:79:0x014c, B:58:0x00ed=Splitter:B:58:0x00ed, B:51:0x00ce=Splitter:B:51:0x00ce, B:44:0x00af=Splitter:B:44:0x00af} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readSettings() {
        /*
            r11 = this;
            java.lang.String r0 = "Failed parsing "
            java.lang.String r1 = "WindowManager"
            com.android.server.wm.DisplayWindowSettings$SettingPersister r2 = r11.mStorage     // Catch:{ IOException -> 0x015f }
            java.io.InputStream r2 = r2.openRead()     // Catch:{ IOException -> 0x015f }
            r3 = 0
            org.xmlpull.v1.XmlPullParser r4 = android.util.Xml.newPullParser()     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            java.nio.charset.Charset r5 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            java.lang.String r5 = r5.name()     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            r4.setInput(r2, r5)     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
        L_0x0019:
            int r5 = r4.next()     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            r6 = r5
            r7 = 1
            r8 = 2
            if (r5 == r8) goto L_0x0025
            if (r6 == r7) goto L_0x0025
            goto L_0x0019
        L_0x0025:
            if (r6 != r8) goto L_0x0089
            int r5 = r4.getDepth()     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
        L_0x002b:
            int r8 = r4.next()     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            r6 = r8
            if (r8 == r7) goto L_0x0079
            r8 = 3
            if (r6 != r8) goto L_0x003b
            int r9 = r4.getDepth()     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            if (r9 <= r5) goto L_0x0079
        L_0x003b:
            if (r6 == r8) goto L_0x002b
            r8 = 4
            if (r6 != r8) goto L_0x0041
            goto L_0x002b
        L_0x0041:
            java.lang.String r8 = r4.getName()     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            java.lang.String r9 = "display"
            boolean r9 = r8.equals(r9)     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            if (r9 == 0) goto L_0x0051
            r11.readDisplay(r4)     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            goto L_0x0078
        L_0x0051:
            java.lang.String r9 = "config"
            boolean r9 = r8.equals(r9)     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            if (r9 == 0) goto L_0x005d
            r11.readConfig(r4)     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            goto L_0x0078
        L_0x005d:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            r9.<init>()     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            java.lang.String r10 = "Unknown element under <display-settings>: "
            r9.append(r10)     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            java.lang.String r10 = r4.getName()     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            r9.append(r10)     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            java.lang.String r9 = r9.toString()     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            android.util.Slog.w(r1, r9)     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            com.android.internal.util.XmlUtils.skipCurrentTag(r4)     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
        L_0x0078:
            goto L_0x002b
        L_0x0079:
            r3 = 1
            if (r3 != 0) goto L_0x0081
            java.util.HashMap<java.lang.String, com.android.server.wm.DisplayWindowSettings$Entry> r0 = r11.mEntries
            r0.clear()
        L_0x0081:
            r2.close()     // Catch:{ IOException -> 0x0086 }
        L_0x0084:
            goto L_0x0151
        L_0x0086:
            r0 = move-exception
            goto L_0x0151
        L_0x0089:
            java.lang.IllegalStateException r5 = new java.lang.IllegalStateException     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            java.lang.String r7 = "no start tag found"
            r5.<init>(r7)     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
            throw r5     // Catch:{ IllegalStateException -> 0x0131, NullPointerException -> 0x0111, NumberFormatException -> 0x00f1, XmlPullParserException -> 0x00d2, IOException -> 0x00b3, IndexOutOfBoundsException -> 0x0094 }
        L_0x0091:
            r0 = move-exception
            goto L_0x0152
        L_0x0094:
            r4 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0091 }
            r5.<init>()     // Catch:{ all -> 0x0091 }
            r5.append(r0)     // Catch:{ all -> 0x0091 }
            r5.append(r4)     // Catch:{ all -> 0x0091 }
            java.lang.String r0 = r5.toString()     // Catch:{ all -> 0x0091 }
            android.util.Slog.w(r1, r0)     // Catch:{ all -> 0x0091 }
            if (r3 != 0) goto L_0x00af
            java.util.HashMap<java.lang.String, com.android.server.wm.DisplayWindowSettings$Entry> r0 = r11.mEntries
            r0.clear()
        L_0x00af:
            r2.close()     // Catch:{ IOException -> 0x0086 }
            goto L_0x0084
        L_0x00b3:
            r4 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0091 }
            r5.<init>()     // Catch:{ all -> 0x0091 }
            r5.append(r0)     // Catch:{ all -> 0x0091 }
            r5.append(r4)     // Catch:{ all -> 0x0091 }
            java.lang.String r0 = r5.toString()     // Catch:{ all -> 0x0091 }
            android.util.Slog.w(r1, r0)     // Catch:{ all -> 0x0091 }
            if (r3 != 0) goto L_0x00ce
            java.util.HashMap<java.lang.String, com.android.server.wm.DisplayWindowSettings$Entry> r0 = r11.mEntries
            r0.clear()
        L_0x00ce:
            r2.close()     // Catch:{ IOException -> 0x0086 }
            goto L_0x0084
        L_0x00d2:
            r4 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0091 }
            r5.<init>()     // Catch:{ all -> 0x0091 }
            r5.append(r0)     // Catch:{ all -> 0x0091 }
            r5.append(r4)     // Catch:{ all -> 0x0091 }
            java.lang.String r0 = r5.toString()     // Catch:{ all -> 0x0091 }
            android.util.Slog.w(r1, r0)     // Catch:{ all -> 0x0091 }
            if (r3 != 0) goto L_0x00ed
            java.util.HashMap<java.lang.String, com.android.server.wm.DisplayWindowSettings$Entry> r0 = r11.mEntries
            r0.clear()
        L_0x00ed:
            r2.close()     // Catch:{ IOException -> 0x0086 }
            goto L_0x0084
        L_0x00f1:
            r4 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0091 }
            r5.<init>()     // Catch:{ all -> 0x0091 }
            r5.append(r0)     // Catch:{ all -> 0x0091 }
            r5.append(r4)     // Catch:{ all -> 0x0091 }
            java.lang.String r0 = r5.toString()     // Catch:{ all -> 0x0091 }
            android.util.Slog.w(r1, r0)     // Catch:{ all -> 0x0091 }
            if (r3 != 0) goto L_0x010c
            java.util.HashMap<java.lang.String, com.android.server.wm.DisplayWindowSettings$Entry> r0 = r11.mEntries
            r0.clear()
        L_0x010c:
            r2.close()     // Catch:{ IOException -> 0x0086 }
            goto L_0x0084
        L_0x0111:
            r4 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0091 }
            r5.<init>()     // Catch:{ all -> 0x0091 }
            r5.append(r0)     // Catch:{ all -> 0x0091 }
            r5.append(r4)     // Catch:{ all -> 0x0091 }
            java.lang.String r0 = r5.toString()     // Catch:{ all -> 0x0091 }
            android.util.Slog.w(r1, r0)     // Catch:{ all -> 0x0091 }
            if (r3 != 0) goto L_0x012c
            java.util.HashMap<java.lang.String, com.android.server.wm.DisplayWindowSettings$Entry> r0 = r11.mEntries
            r0.clear()
        L_0x012c:
            r2.close()     // Catch:{ IOException -> 0x0086 }
            goto L_0x0084
        L_0x0131:
            r4 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0091 }
            r5.<init>()     // Catch:{ all -> 0x0091 }
            r5.append(r0)     // Catch:{ all -> 0x0091 }
            r5.append(r4)     // Catch:{ all -> 0x0091 }
            java.lang.String r0 = r5.toString()     // Catch:{ all -> 0x0091 }
            android.util.Slog.w(r1, r0)     // Catch:{ all -> 0x0091 }
            if (r3 != 0) goto L_0x014c
            java.util.HashMap<java.lang.String, com.android.server.wm.DisplayWindowSettings$Entry> r0 = r11.mEntries
            r0.clear()
        L_0x014c:
            r2.close()     // Catch:{ IOException -> 0x0086 }
            goto L_0x0084
        L_0x0151:
            return
        L_0x0152:
            if (r3 != 0) goto L_0x0159
            java.util.HashMap<java.lang.String, com.android.server.wm.DisplayWindowSettings$Entry> r1 = r11.mEntries
            r1.clear()
        L_0x0159:
            r2.close()     // Catch:{ IOException -> 0x015d }
            goto L_0x015e
        L_0x015d:
            r1 = move-exception
        L_0x015e:
            throw r0
        L_0x015f:
            r0 = move-exception
            java.lang.String r2 = "No existing display settings, starting empty"
            android.util.Slog.i(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DisplayWindowSettings.readSettings():void");
    }

    private int getIntAttribute(XmlPullParser parser, String name) {
        return getIntAttribute(parser, name, 0);
    }

    private int getIntAttribute(XmlPullParser parser, String name, int defaultValue) {
        try {
            String str = parser.getAttributeValue((String) null, name);
            return str != null ? Integer.parseInt(str) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean getBooleanAttribute(XmlPullParser parser, String name) {
        return getBooleanAttribute(parser, name, false);
    }

    private boolean getBooleanAttribute(XmlPullParser parser, String name, boolean defaultValue) {
        try {
            String str = parser.getAttributeValue((String) null, name);
            return str != null ? Boolean.parseBoolean(str) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void readDisplay(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException {
        String name = parser.getAttributeValue((String) null, com.android.server.pm.Settings.ATTR_NAME);
        if (name != null) {
            Entry entry = new Entry(name);
            int unused = entry.mOverscanLeft = getIntAttribute(parser, "overscanLeft");
            int unused2 = entry.mOverscanTop = getIntAttribute(parser, "overscanTop");
            int unused3 = entry.mOverscanRight = getIntAttribute(parser, "overscanRight");
            int unused4 = entry.mOverscanBottom = getIntAttribute(parser, "overscanBottom");
            int unused5 = entry.mWindowingMode = getIntAttribute(parser, "windowingMode", 0);
            int unused6 = entry.mUserRotationMode = getIntAttribute(parser, "userRotationMode", 0);
            int unused7 = entry.mUserRotation = getIntAttribute(parser, "userRotation", 0);
            int unused8 = entry.mForcedWidth = getIntAttribute(parser, "forcedWidth");
            int unused9 = entry.mForcedHeight = getIntAttribute(parser, "forcedHeight");
            int unused10 = entry.mForcedDensity = getIntAttribute(parser, "forcedDensity");
            int unused11 = entry.mForcedScalingMode = getIntAttribute(parser, "forcedScalingMode", 0);
            int unused12 = entry.mRemoveContentMode = getIntAttribute(parser, "removeContentMode", 0);
            boolean unused13 = entry.mShouldShowWithInsecureKeyguard = getBooleanAttribute(parser, "shouldShowWithInsecureKeyguard");
            boolean unused14 = entry.mShouldShowSystemDecors = getBooleanAttribute(parser, "shouldShowSystemDecors");
            boolean unused15 = entry.mShouldShowIme = getBooleanAttribute(parser, "shouldShowIme");
            int unused16 = entry.mFixedToUserRotation = getIntAttribute(parser, "fixedToUserRotation");
            this.mEntries.put(name, entry);
        }
        XmlUtils.skipCurrentTag(parser);
    }

    private void readConfig(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException {
        this.mIdentifier = getIntAttribute(parser, "identifier");
        XmlUtils.skipCurrentTag(parser);
    }

    private void writeSettingsIfNeeded(Entry changedEntry, DisplayInfo displayInfo) {
        if (!changedEntry.isEmpty() || removeEntry(displayInfo)) {
            this.mEntries.put(getIdentifier(displayInfo), changedEntry);
            writeSettings();
        }
    }

    private void writeSettings() {
        try {
            OutputStream stream = this.mStorage.startWrite();
            try {
                XmlSerializer out = new FastXmlSerializer();
                out.setOutput(stream, StandardCharsets.UTF_8.name());
                out.startDocument((String) null, true);
                out.startTag((String) null, "display-settings");
                out.startTag((String) null, "config");
                out.attribute((String) null, "identifier", Integer.toString(this.mIdentifier));
                out.endTag((String) null, "config");
                for (Entry entry : this.mEntries.values()) {
                    out.startTag((String) null, "display");
                    out.attribute((String) null, com.android.server.pm.Settings.ATTR_NAME, entry.mName);
                    if (entry.mOverscanLeft != 0) {
                        out.attribute((String) null, "overscanLeft", Integer.toString(entry.mOverscanLeft));
                    }
                    if (entry.mOverscanTop != 0) {
                        out.attribute((String) null, "overscanTop", Integer.toString(entry.mOverscanTop));
                    }
                    if (entry.mOverscanRight != 0) {
                        out.attribute((String) null, "overscanRight", Integer.toString(entry.mOverscanRight));
                    }
                    if (entry.mOverscanBottom != 0) {
                        out.attribute((String) null, "overscanBottom", Integer.toString(entry.mOverscanBottom));
                    }
                    if (entry.mWindowingMode != 0) {
                        out.attribute((String) null, "windowingMode", Integer.toString(entry.mWindowingMode));
                    }
                    if (entry.mUserRotationMode != 0) {
                        out.attribute((String) null, "userRotationMode", Integer.toString(entry.mUserRotationMode));
                    }
                    if (entry.mUserRotation != 0) {
                        out.attribute((String) null, "userRotation", Integer.toString(entry.mUserRotation));
                    }
                    if (!(entry.mForcedWidth == 0 || entry.mForcedHeight == 0)) {
                        out.attribute((String) null, "forcedWidth", Integer.toString(entry.mForcedWidth));
                        out.attribute((String) null, "forcedHeight", Integer.toString(entry.mForcedHeight));
                    }
                    if (entry.mForcedDensity != 0) {
                        out.attribute((String) null, "forcedDensity", Integer.toString(entry.mForcedDensity));
                    }
                    if (entry.mForcedScalingMode != 0) {
                        out.attribute((String) null, "forcedScalingMode", Integer.toString(entry.mForcedScalingMode));
                    }
                    if (entry.mRemoveContentMode != 0) {
                        out.attribute((String) null, "removeContentMode", Integer.toString(entry.mRemoveContentMode));
                    }
                    if (entry.mShouldShowWithInsecureKeyguard) {
                        out.attribute((String) null, "shouldShowWithInsecureKeyguard", Boolean.toString(entry.mShouldShowWithInsecureKeyguard));
                    }
                    if (entry.mShouldShowSystemDecors) {
                        out.attribute((String) null, "shouldShowSystemDecors", Boolean.toString(entry.mShouldShowSystemDecors));
                    }
                    if (entry.mShouldShowIme) {
                        out.attribute((String) null, "shouldShowIme", Boolean.toString(entry.mShouldShowIme));
                    }
                    if (entry.mFixedToUserRotation != 0) {
                        out.attribute((String) null, "fixedToUserRotation", Integer.toString(entry.mFixedToUserRotation));
                    }
                    out.endTag((String) null, "display");
                }
                out.endTag((String) null, "display-settings");
                out.endDocument();
                this.mStorage.finishWrite(stream, true);
            } catch (IOException e) {
                Slog.w("WindowManager", "Failed to write display window settings.", e);
                this.mStorage.finishWrite(stream, false);
            }
        } catch (IOException e2) {
            Slog.w("WindowManager", "Failed to write display settings: " + e2);
        }
    }

    private boolean removeEntry(DisplayInfo displayInfo) {
        boolean z = true;
        boolean removed = (this.mEntries.remove(getIdentifier(displayInfo)) != null) | (this.mEntries.remove(displayInfo.uniqueId) != null);
        if (this.mEntries.remove(displayInfo.name) == null) {
            z = false;
        }
        return removed | z;
    }

    private String getIdentifier(DisplayInfo displayInfo) {
        if (this.mIdentifier != 1 || displayInfo.address == null || !(displayInfo.address instanceof DisplayAddress.Physical)) {
            return displayInfo.uniqueId;
        }
        return "port:" + displayInfo.address.getPort();
    }

    private static class AtomicFileStorage implements SettingPersister {
        private final AtomicFile mAtomicFile = new AtomicFile(new File(new File(Environment.getDataDirectory(), "system"), "display_settings.xml"), "wm-displays");

        AtomicFileStorage() {
        }

        public InputStream openRead() throws FileNotFoundException {
            return this.mAtomicFile.openRead();
        }

        public OutputStream startWrite() throws IOException {
            return this.mAtomicFile.startWrite();
        }

        public void finishWrite(OutputStream os, boolean success) {
            if (os instanceof FileOutputStream) {
                FileOutputStream fos = (FileOutputStream) os;
                if (success) {
                    this.mAtomicFile.finishWrite(fos);
                } else {
                    this.mAtomicFile.failWrite(fos);
                }
            } else {
                throw new IllegalArgumentException("Unexpected OutputStream as argument: " + os);
            }
        }
    }
}

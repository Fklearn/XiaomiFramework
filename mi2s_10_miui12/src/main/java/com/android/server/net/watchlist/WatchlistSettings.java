package com.android.server.net.watchlist;

import android.os.Environment;
import android.util.AtomicFile;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.HexDump;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class WatchlistSettings {
    private static final String FILE_NAME = "watchlist_settings.xml";
    private static final int SECRET_KEY_LENGTH = 48;
    private static final String TAG = "WatchlistSettings";
    private static final WatchlistSettings sInstance = new WatchlistSettings();
    private byte[] mPrivacySecretKey;
    private final AtomicFile mXmlFile;

    public static WatchlistSettings getInstance() {
        return sInstance;
    }

    private WatchlistSettings() {
        this(getSystemWatchlistFile());
    }

    static File getSystemWatchlistFile() {
        return new File(Environment.getDataSystemDirectory(), FILE_NAME);
    }

    @VisibleForTesting
    protected WatchlistSettings(File xmlFile) {
        this.mPrivacySecretKey = null;
        this.mXmlFile = new AtomicFile(xmlFile, "net-watchlist");
        reloadSettings();
        if (this.mPrivacySecretKey == null) {
            this.mPrivacySecretKey = generatePrivacySecretKey();
            saveSettings();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0050, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0051, code lost:
        if (r1 != null) goto L_0x0053;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x005b, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void reloadSettings() {
        /*
            r6 = this;
            java.lang.String r0 = "WatchlistSettings"
            android.util.AtomicFile r1 = r6.mXmlFile
            boolean r1 = r1.exists()
            if (r1 != 0) goto L_0x000b
            return
        L_0x000b:
            android.util.AtomicFile r1 = r6.mXmlFile     // Catch:{ IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x005c }
            java.io.FileInputStream r1 = r1.openRead()     // Catch:{ IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x005c }
            org.xmlpull.v1.XmlPullParser r2 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x004e }
            java.nio.charset.Charset r3 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ all -> 0x004e }
            java.lang.String r3 = r3.name()     // Catch:{ all -> 0x004e }
            r2.setInput(r1, r3)     // Catch:{ all -> 0x004e }
            java.lang.String r3 = "network-watchlist-settings"
            com.android.internal.util.XmlUtils.beginDocument(r2, r3)     // Catch:{ all -> 0x004e }
            int r3 = r2.getDepth()     // Catch:{ all -> 0x004e }
        L_0x0028:
            boolean r4 = com.android.internal.util.XmlUtils.nextElementWithin(r2, r3)     // Catch:{ all -> 0x004e }
            if (r4 == 0) goto L_0x0042
            java.lang.String r4 = r2.getName()     // Catch:{ all -> 0x004e }
            java.lang.String r5 = "secret-key"
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x004e }
            if (r4 == 0) goto L_0x0028
            byte[] r4 = r6.parseSecretKey(r2)     // Catch:{ all -> 0x004e }
            r6.mPrivacySecretKey = r4     // Catch:{ all -> 0x004e }
            goto L_0x0028
        L_0x0042:
            java.lang.String r4 = "Reload watchlist settings done"
            android.util.Slog.i(r0, r4)     // Catch:{ all -> 0x004e }
            if (r1 == 0) goto L_0x004d
            r1.close()     // Catch:{ IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x005c }
        L_0x004d:
            goto L_0x0062
        L_0x004e:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0050 }
        L_0x0050:
            r3 = move-exception
            if (r1 == 0) goto L_0x005b
            r1.close()     // Catch:{ all -> 0x0057 }
            goto L_0x005b
        L_0x0057:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x005c }
        L_0x005b:
            throw r3     // Catch:{ IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x005c }
        L_0x005c:
            r1 = move-exception
            java.lang.String r2 = "Failed parsing xml"
            android.util.Slog.e(r0, r2, r1)
        L_0x0062:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.watchlist.WatchlistSettings.reloadSettings():void");
    }

    private byte[] parseSecretKey(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(2, (String) null, "secret-key");
        byte[] key = HexDump.hexStringToByteArray(parser.nextText());
        parser.require(3, (String) null, "secret-key");
        if (key != null && key.length == 48) {
            return key;
        }
        Log.e(TAG, "Unable to parse secret key");
        return null;
    }

    /* access modifiers changed from: package-private */
    public synchronized byte[] getPrivacySecretKey() {
        byte[] key;
        key = new byte[48];
        System.arraycopy(this.mPrivacySecretKey, 0, key, 0, 48);
        return key;
    }

    private byte[] generatePrivacySecretKey() {
        byte[] key = new byte[48];
        new SecureRandom().nextBytes(key);
        return key;
    }

    private void saveSettings() {
        try {
            FileOutputStream stream = this.mXmlFile.startWrite();
            try {
                XmlSerializer out = new FastXmlSerializer();
                out.setOutput(stream, StandardCharsets.UTF_8.name());
                out.startDocument((String) null, true);
                out.startTag((String) null, "network-watchlist-settings");
                out.startTag((String) null, "secret-key");
                out.text(HexDump.toHexString(this.mPrivacySecretKey));
                out.endTag((String) null, "secret-key");
                out.endTag((String) null, "network-watchlist-settings");
                out.endDocument();
                this.mXmlFile.finishWrite(stream);
            } catch (IOException e) {
                Log.w(TAG, "Failed to write display settings, restoring backup.", e);
                this.mXmlFile.failWrite(stream);
            }
        } catch (IOException e2) {
            Log.w(TAG, "Failed to write display settings: " + e2);
        }
    }
}

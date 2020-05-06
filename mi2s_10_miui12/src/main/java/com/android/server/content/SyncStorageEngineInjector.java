package com.android.server.content;

import android.accounts.Account;
import android.content.SyncResult;
import android.content.SyncStatusInfo;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.util.FastXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class SyncStorageEngineInjector {
    private static final String MI_PAUSE_FILE_NAME = "mi_pause.xml";
    private static final String MI_STRATEGY_FILE_NAME = "mi_strategy.xml";
    private static final String TAG = "SyncManager";
    private static final String TAG_FILE = "SyncManagerFile";
    private static AtomicFile mMiPauseFile = null;
    private static AtomicFile mMiStrategyFile = null;
    private static SparseArray<Map<String, MiSyncPause>> mMiSyncPause = new SparseArray<>();
    private static SparseArray<Map<String, MiSyncStrategy>> mMiSyncStrategy = new SparseArray<>();

    public static void initAndReadAndWriteLocked(File syncDir) {
        mMiPauseFile = new AtomicFile(new File(syncDir, MI_PAUSE_FILE_NAME));
        mMiStrategyFile = new AtomicFile(new File(syncDir, MI_STRATEGY_FILE_NAME));
        readAndWriteLocked();
    }

    private static void readAndWriteLocked() {
        readLocked();
        writeLocked();
    }

    private static void readLocked() {
        readMiPauseLocked();
        readMiStrategyLocked();
    }

    private static void writeLocked() {
        writeMiPauseLocked();
        writeMiStrategyLocked();
    }

    private static void clear() {
        mMiSyncPause.clear();
        mMiSyncStrategy.clear();
    }

    public static void clearAndReadAndWriteLocked() {
        clear();
        readAndWriteLocked();
    }

    public static void doDatabaseCleanupLocked(Account[] accounts, int uid) {
        doMiPauseCleanUpLocked(accounts, uid);
        doMiStrategyCleanUpLocked(accounts, uid);
        writeLocked();
    }

    private static void readMiPauseLocked() {
        int version;
        FileInputStream fis = null;
        try {
            fis = mMiPauseFile.openRead();
            if (Log.isLoggable(TAG_FILE, 2)) {
                Slog.v(TAG_FILE, "Reading " + mMiPauseFile.getBaseFile());
            }
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, StandardCharsets.UTF_8.name());
            int eventType = parser.getEventType();
            while (eventType != 2 && eventType != 1) {
                eventType = parser.next();
            }
            if (eventType == 1) {
                Slog.i("SyncManager", "No initial mi pause");
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                }
            } else {
                if (MiSyncPause.XML_FILE_NAME.equalsIgnoreCase(parser.getName())) {
                    String versionString = parser.getAttributeValue((String) null, "version");
                    if (versionString == null) {
                        version = 0;
                    } else {
                        try {
                            version = Integer.parseInt(versionString);
                        } catch (NumberFormatException e2) {
                            version = 0;
                        }
                    }
                    if (version >= 1) {
                        int eventType2 = parser.next();
                        do {
                            if (eventType2 == 2) {
                                setMiPauseInternalLocked(MiSyncPause.readFromXML(parser));
                            }
                            eventType2 = parser.next();
                        } while (eventType2 != 1);
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e3) {
                    }
                }
            }
        } catch (XmlPullParserException e4) {
            Slog.w("SyncManager", "Error reading mi pause", e4);
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e5) {
                }
            }
        } catch (IOException e6) {
            if (fis == null) {
                Slog.i("SyncManager", "No initial mi pause");
            } else {
                Slog.w("SyncManager", "Error reading mi pause", e6);
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e7) {
                }
            }
        } catch (Throwable th) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e8) {
                }
            }
            throw th;
        }
    }

    private static void readMiStrategyLocked() {
        int version;
        FileInputStream fis = null;
        try {
            fis = mMiStrategyFile.openRead();
            if (Log.isLoggable(TAG_FILE, 2)) {
                Slog.v(TAG_FILE, "Reading " + mMiStrategyFile.getBaseFile());
            }
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, StandardCharsets.UTF_8.name());
            int eventType = parser.getEventType();
            while (eventType != 2 && eventType != 1) {
                eventType = parser.next();
            }
            if (eventType == 1) {
                Slog.i("SyncManager", "No initial mi strategy");
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                }
            } else {
                if (MiSyncStrategy.XML_FILE_NAME.equalsIgnoreCase(parser.getName())) {
                    String versionString = parser.getAttributeValue((String) null, "version");
                    if (versionString == null) {
                        version = 0;
                    } else {
                        try {
                            version = Integer.parseInt(versionString);
                        } catch (NumberFormatException e2) {
                            version = 0;
                        }
                    }
                    if (version >= 1) {
                        int eventType2 = parser.next();
                        do {
                            if (eventType2 == 2) {
                                setMiStrategyInternalLocked(MiSyncStrategy.readFromXML(parser));
                            }
                            eventType2 = parser.next();
                        } while (eventType2 != 1);
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e3) {
                    }
                }
            }
        } catch (XmlPullParserException e4) {
            Slog.w("SyncManager", "Error reading mi strategy", e4);
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e5) {
                }
            }
        } catch (IOException e6) {
            if (fis == null) {
                Slog.i("SyncManager", "No initial mi strategy");
            } else {
                Slog.w("SyncManager", "Error reading mi strategy", e6);
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e7) {
                }
            }
        } catch (Throwable th) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e8) {
                }
            }
            throw th;
        }
    }

    private static void writeMiPauseLocked() {
        if (Log.isLoggable(TAG_FILE, 2)) {
            Slog.v(TAG_FILE, "Writing new " + mMiPauseFile.getBaseFile());
        }
        try {
            FileOutputStream fos = mMiPauseFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag((String) null, MiSyncPause.XML_FILE_NAME);
            out.attribute((String) null, "version", Integer.toString(1));
            int m = mMiSyncPause.size();
            for (int i = 0; i < m; i++) {
                for (MiSyncPause item : mMiSyncPause.valueAt(i).values()) {
                    item.writeToXML(out);
                }
            }
            out.endTag((String) null, MiSyncPause.XML_FILE_NAME);
            out.endDocument();
            mMiPauseFile.finishWrite(fos);
        } catch (IOException e1) {
            Slog.w("SyncManager", "Error writing mi pause", e1);
            if (0 != 0) {
                mMiPauseFile.failWrite((FileOutputStream) null);
            }
        }
    }

    private static void writeMiStrategyLocked() {
        if (Log.isLoggable(TAG_FILE, 2)) {
            Slog.v(TAG_FILE, "Writing new " + mMiStrategyFile.getBaseFile());
        }
        try {
            FileOutputStream fos = mMiStrategyFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag((String) null, MiSyncStrategy.XML_FILE_NAME);
            out.attribute((String) null, "version", Integer.toString(1));
            int m = mMiSyncStrategy.size();
            for (int i = 0; i < m; i++) {
                for (MiSyncStrategy item : mMiSyncStrategy.valueAt(i).values()) {
                    item.writeToXML(out);
                }
            }
            out.endTag((String) null, MiSyncStrategy.XML_FILE_NAME);
            out.endDocument();
            mMiStrategyFile.finishWrite(fos);
        } catch (IOException e1) {
            Slog.w("SyncManager", "Error writing mi strategy", e1);
            if (0 != 0) {
                mMiStrategyFile.failWrite((FileOutputStream) null);
            }
        }
    }

    private static void setMiPauseInternalLocked(MiSyncPause miSyncPause) {
        if (miSyncPause != null) {
            setMiPauseInternalLocked(miSyncPause.getAccountName(), miSyncPause.getPauseEndTime(), miSyncPause.getUid());
        } else if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "setMiPauseInternalLocked: miSyncPause is null");
        }
    }

    private static void setMiPauseInternalLocked(String accountName, long pauseTimeMillis, int uid) {
        if (!TextUtils.isEmpty(accountName)) {
            getOrCreateMiSyncPauseLocked(accountName, uid).setPauseToTime(pauseTimeMillis);
        } else if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "setMiPauseInternalLocked: accountName is null");
        }
    }

    private static void setMiStrategyInternalLocked(MiSyncStrategy miSyncStrategy) {
        if (miSyncStrategy != null) {
            setMiStrategyInternalLocked(miSyncStrategy.getAccountName(), miSyncStrategy.getStrategy(), miSyncStrategy.getUid());
        } else if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "setMiStrategyInternalLocked: miSyncStrategy is null");
        }
    }

    private static void setMiStrategyInternalLocked(String accountName, int strategy, int uid) {
        if (!TextUtils.isEmpty(accountName)) {
            getOrCreateMiSyncStrategyLocked(accountName, uid).setStrategy(strategy);
        } else if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "setMiStrategyInternalLocked: accountName is null");
        }
    }

    private static void doMiPauseCleanUpLocked(Account[] runningAccounts, int uid) {
        Map<String, MiSyncPause> map = mMiSyncPause.get(uid);
        if (map != null) {
            for (String accountName : getRemovingAccounts(runningAccounts, map.keySet())) {
                map.remove(accountName);
            }
        } else if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "doMiPauseCleanUpLocked: map is null");
        }
    }

    private static void doMiStrategyCleanUpLocked(Account[] runningAccounts, int uid) {
        Map<String, MiSyncStrategy> map = mMiSyncStrategy.get(uid);
        if (map != null) {
            for (String accountName : getRemovingAccounts(runningAccounts, map.keySet())) {
                map.remove(accountName);
            }
        } else if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "doMiStrategyCleanUpLocked: map is null");
        }
    }

    private static List<String> getRemovingAccounts(Account[] runningAccounts, Set<String> currentAccountNameSet) {
        List<String> removing = new ArrayList<>();
        if (currentAccountNameSet == null) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "getRemovingAccounts: Argument is null");
            }
            return removing;
        }
        if (runningAccounts == null) {
            runningAccounts = new Account[0];
        }
        for (String accountName : currentAccountNameSet) {
            if (!containsXiaomiAccountName(runningAccounts, accountName)) {
                removing.add(accountName);
            }
        }
        return removing;
    }

    private static boolean containsXiaomiAccountName(Account[] accounts, String accountName) {
        if (accounts == null) {
            return false;
        }
        for (Account account : accounts) {
            if (MiSyncUtils.checkAccount(account) && TextUtils.equals(account.name, accountName)) {
                return true;
            }
        }
        return false;
    }

    public static void setMiSyncPauseToTimeLocked(Account account, long pauseTimeMillis, int uid) {
        if (MiSyncUtils.checkAccount(account)) {
            MiSyncPause miSyncPause = getOrCreateMiSyncPauseLocked(account.name, uid);
            if (miSyncPause.getPauseEndTime() != pauseTimeMillis) {
                miSyncPause.setPauseToTime(pauseTimeMillis);
                writeMiPauseLocked();
            } else if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "setMiSyncPauseTimeLocked: pause time is not changed");
            }
        } else if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "setMiSyncPauseToTimeLocked: account is null");
        }
    }

    public static void setMiSyncStrategyLocked(Account account, int strategy, int uid) {
        if (MiSyncUtils.checkAccount(account)) {
            MiSyncStrategy miSyncStrategy = getOrCreateMiSyncStrategyLocked(account.name, uid);
            if (miSyncStrategy.getStrategy() != strategy) {
                miSyncStrategy.setStrategy(strategy);
                writeMiStrategyLocked();
            } else if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "setMiSyncPauseTimeLocked: strategy is not changed");
            }
        } else if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "setMiSyncStrategyLocked: account is null");
        }
    }

    public static long getMiSyncPauseToTimeLocked(Account account, int uid) {
        if (MiSyncUtils.checkAccount(account)) {
            return getOrCreateMiSyncPauseLocked(account.name, uid).getPauseEndTime();
        }
        if (!Log.isLoggable("SyncManager", 2)) {
            return 0;
        }
        Slog.v("SyncManager", "getMiSyncPauseToTimeLocked: not xiaomi account");
        return 0;
    }

    public static int getMiSyncStrategyLocked(Account account, int uid) {
        if (MiSyncUtils.checkAccount(account)) {
            return getOrCreateMiSyncStrategyLocked(account.name, uid).getStrategy();
        }
        if (!Log.isLoggable("SyncManager", 2)) {
            return 1;
        }
        Slog.v("SyncManager", "getMiSyncStrategyLocked: not xiaomi account");
        return 1;
    }

    public static MiSyncPause getMiSyncPauseLocked(String accountName, int uid) {
        return getOrCreateMiSyncPauseLocked(accountName, uid);
    }

    public static MiSyncStrategy getMiSyncStrategyLocked(String accountName, int uid) {
        return getOrCreateMiSyncStrategyLocked(accountName, uid);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: com.android.server.content.MiSyncPause} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.android.server.content.MiSyncPause getOrCreateMiSyncPauseLocked(java.lang.String r3, int r4) {
        /*
            android.util.SparseArray<java.util.Map<java.lang.String, com.android.server.content.MiSyncPause>> r0 = mMiSyncPause
            java.lang.Object r0 = r0.get(r4)
            java.util.Map r0 = (java.util.Map) r0
            if (r0 != 0) goto L_0x0015
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r0 = r1
            android.util.SparseArray<java.util.Map<java.lang.String, com.android.server.content.MiSyncPause>> r1 = mMiSyncPause
            r1.put(r4, r0)
        L_0x0015:
            r1 = 0
            if (r3 != 0) goto L_0x001a
            java.lang.String r3 = ""
        L_0x001a:
            boolean r2 = r0.containsKey(r3)
            if (r2 == 0) goto L_0x0027
            java.lang.Object r2 = r0.get(r3)
            r1 = r2
            com.android.server.content.MiSyncPause r1 = (com.android.server.content.MiSyncPause) r1
        L_0x0027:
            if (r1 != 0) goto L_0x0032
            com.android.server.content.MiSyncPause r2 = new com.android.server.content.MiSyncPause
            r2.<init>(r4, r3)
            r1 = r2
            r0.put(r3, r1)
        L_0x0032:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngineInjector.getOrCreateMiSyncPauseLocked(java.lang.String, int):com.android.server.content.MiSyncPause");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: com.android.server.content.MiSyncStrategy} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.android.server.content.MiSyncStrategy getOrCreateMiSyncStrategyLocked(java.lang.String r3, int r4) {
        /*
            android.util.SparseArray<java.util.Map<java.lang.String, com.android.server.content.MiSyncStrategy>> r0 = mMiSyncStrategy
            java.lang.Object r0 = r0.get(r4)
            java.util.Map r0 = (java.util.Map) r0
            if (r0 != 0) goto L_0x0015
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r0 = r1
            android.util.SparseArray<java.util.Map<java.lang.String, com.android.server.content.MiSyncStrategy>> r1 = mMiSyncStrategy
            r1.put(r4, r0)
        L_0x0015:
            r1 = 0
            if (r3 != 0) goto L_0x001a
            java.lang.String r3 = ""
        L_0x001a:
            boolean r2 = r0.containsKey(r3)
            if (r2 == 0) goto L_0x0027
            java.lang.Object r2 = r0.get(r3)
            r1 = r2
            com.android.server.content.MiSyncStrategy r1 = (com.android.server.content.MiSyncStrategy) r1
        L_0x0027:
            if (r1 != 0) goto L_0x0032
            com.android.server.content.MiSyncStrategy r2 = new com.android.server.content.MiSyncStrategy
            r2.<init>(r4, r3)
            r1 = r2
            r0.put(r3, r1)
        L_0x0032:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngineInjector.getOrCreateMiSyncStrategyLocked(java.lang.String, int):com.android.server.content.MiSyncStrategy");
    }

    public static void updateResultStatusLocked(SyncStatusInfo syncStatusInfo, String lastSyncMessage, SyncResult syncResult) {
        MiSyncResultStatusAdapter.updateResultStatus(syncStatusInfo, lastSyncMessage, syncResult);
    }
}

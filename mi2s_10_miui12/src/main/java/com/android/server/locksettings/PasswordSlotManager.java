package com.android.server.locksettings;

import android.os.SystemProperties;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.net.watchlist.WatchlistLoggingHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PasswordSlotManager {
    private static final String GSI_RUNNING_PROP = "ro.gsid.image_running";
    private static final String SLOT_MAP_DIR = "/metadata/password_slots";
    private static final String TAG = "PasswordSlotManager";
    private Set<Integer> mActiveSlots;
    private Map<Integer, String> mSlotMap;

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public String getSlotMapDir() {
        return SLOT_MAP_DIR;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public int getGsiImageNumber() {
        return SystemProperties.getInt(GSI_RUNNING_PROP, 0);
    }

    public void refreshActiveSlots(Set<Integer> activeSlots) throws RuntimeException {
        if (this.mSlotMap == null) {
            this.mActiveSlots = new HashSet(activeSlots);
            return;
        }
        HashSet<Integer> slotsToDelete = new HashSet<>();
        for (Map.Entry<Integer, String> entry : this.mSlotMap.entrySet()) {
            if (entry.getValue().equals(getMode())) {
                slotsToDelete.add(entry.getKey());
            }
        }
        Iterator<Integer> it = slotsToDelete.iterator();
        while (it.hasNext()) {
            this.mSlotMap.remove(it.next());
        }
        for (Integer slot : activeSlots) {
            this.mSlotMap.put(slot, getMode());
        }
        saveSlotMap();
    }

    public void markSlotInUse(int slot) throws RuntimeException {
        ensureSlotMapLoaded();
        if (!this.mSlotMap.containsKey(Integer.valueOf(slot)) || this.mSlotMap.get(Integer.valueOf(slot)).equals(getMode())) {
            this.mSlotMap.put(Integer.valueOf(slot), getMode());
            saveSlotMap();
            return;
        }
        throw new RuntimeException("password slot " + slot + " is not available");
    }

    public void markSlotDeleted(int slot) throws RuntimeException {
        ensureSlotMapLoaded();
        if (!this.mSlotMap.containsKey(Integer.valueOf(slot)) || this.mSlotMap.get(Integer.valueOf(slot)).equals(getMode())) {
            this.mSlotMap.remove(Integer.valueOf(slot));
            saveSlotMap();
            return;
        }
        throw new RuntimeException("password slot " + slot + " cannot be deleted");
    }

    public Set<Integer> getUsedSlots() {
        ensureSlotMapLoaded();
        return Collections.unmodifiableSet(this.mSlotMap.keySet());
    }

    private File getSlotMapFile() {
        return Paths.get(getSlotMapDir(), new String[]{"slot_map"}).toFile();
    }

    private String getMode() {
        int gsiIndex = getGsiImageNumber();
        if (gsiIndex <= 0) {
            return WatchlistLoggingHandler.WatchlistEventKeys.HOST;
        }
        return "gsi" + gsiIndex;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public Map<Integer, String> loadSlotMap(InputStream stream) throws IOException {
        HashMap<Integer, String> map = new HashMap<>();
        Properties props = new Properties();
        props.load(stream);
        for (String slotString : props.stringPropertyNames()) {
            int slot = Integer.parseInt(slotString);
            map.put(Integer.valueOf(slot), props.getProperty(slotString));
        }
        return map;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001a, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001e, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Map<java.lang.Integer, java.lang.String> loadSlotMap() {
        /*
            r4 = this;
            java.io.File r0 = r4.getSlotMapFile()
            boolean r1 = r0.exists()
            if (r1 == 0) goto L_0x0027
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ Exception -> 0x001f }
            r1.<init>(r0)     // Catch:{ Exception -> 0x001f }
            r2 = 0
            java.util.Map r3 = r4.loadSlotMap(r1)     // Catch:{ all -> 0x0018 }
            $closeResource(r2, r1)     // Catch:{ Exception -> 0x001f }
            return r3
        L_0x0018:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x001a }
        L_0x001a:
            r3 = move-exception
            $closeResource(r2, r1)     // Catch:{ Exception -> 0x001f }
            throw r3     // Catch:{ Exception -> 0x001f }
        L_0x001f:
            r1 = move-exception
            java.lang.String r2 = "PasswordSlotManager"
            java.lang.String r3 = "Could not load slot map file"
            android.util.Slog.e(r2, r3, r1)
        L_0x0027:
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.PasswordSlotManager.loadSlotMap():java.util.Map");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    private void ensureSlotMapLoaded() {
        if (this.mSlotMap == null) {
            this.mSlotMap = loadSlotMap();
            Set<Integer> set = this.mActiveSlots;
            if (set != null) {
                refreshActiveSlots(set);
                this.mActiveSlots = null;
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void saveSlotMap(OutputStream stream) throws IOException {
        if (this.mSlotMap != null) {
            Properties props = new Properties();
            for (Map.Entry<Integer, String> entry : this.mSlotMap.entrySet()) {
                props.setProperty(entry.getKey().toString(), entry.getValue());
            }
            props.store(stream, "");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0046, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        $closeResource(r2, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004a, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void saveSlotMap() {
        /*
            r4 = this;
            java.util.Map<java.lang.Integer, java.lang.String> r0 = r4.mSlotMap
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            java.io.File r0 = r4.getSlotMapFile()
            java.io.File r0 = r0.getParentFile()
            boolean r0 = r0.exists()
            java.lang.String r1 = "PasswordSlotManager"
            if (r0 != 0) goto L_0x0033
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Not saving slot map, "
            r0.append(r2)
            java.lang.String r2 = r4.getSlotMapDir()
            r0.append(r2)
            java.lang.String r2 = " does not exist"
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            android.util.Slog.w(r1, r0)
            return
        L_0x0033:
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x004b }
            java.io.File r2 = r4.getSlotMapFile()     // Catch:{ IOException -> 0x004b }
            r0.<init>(r2)     // Catch:{ IOException -> 0x004b }
            r2 = 0
            r4.saveSlotMap(r0)     // Catch:{ all -> 0x0044 }
            $closeResource(r2, r0)     // Catch:{ IOException -> 0x004b }
            goto L_0x0051
        L_0x0044:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0046 }
        L_0x0046:
            r3 = move-exception
            $closeResource(r2, r0)     // Catch:{ IOException -> 0x004b }
            throw r3     // Catch:{ IOException -> 0x004b }
        L_0x004b:
            r0 = move-exception
            java.lang.String r2 = "failed to save password slot map"
            android.util.Slog.e(r1, r2, r0)
        L_0x0051:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.PasswordSlotManager.saveSlotMap():void");
    }
}

package miui.cloud.sync;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import miui.cloud.sync.providers.AntispamSyncInfoProvider;
import miui.cloud.sync.providers.BrowserSyncInfoProvider;
import miui.cloud.sync.providers.CalendarSyncInfoProvider;
import miui.cloud.sync.providers.CalllogSyncInfoProvider;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;
import miui.cloud.sync.providers.GallerySyncInfoProvider;
import miui.cloud.sync.providers.GlobalBrowserSyncInfoProvider;
import miui.cloud.sync.providers.MusicSyncInfoProvider;
import miui.cloud.sync.providers.NotesSyncInfoProvider;
import miui.cloud.sync.providers.PersonalAssistantSyncInfoProvider;
import miui.cloud.sync.providers.PhraseSyncInfoProvider;
import miui.cloud.sync.providers.QuickSearchBoxProvider;
import miui.cloud.sync.providers.SmsSyncInfoProvider;
import miui.cloud.sync.providers.SoundRecorderSyncInfoProvider;
import miui.cloud.sync.providers.WifiSyncInfoProvider;

public final class SyncInfoHelper {
    public static final int INVALID_COUNT = -1;
    public static final String TAG = "SyncInfoHelper";
    private static final Map<String, SyncInfoProvider> authorityMap = new HashMap();

    static {
        authorityMap.put("com.android.contacts", new ContactsSyncInfoProvider());
        authorityMap.put("sms", new SmsSyncInfoProvider());
        authorityMap.put("com.miui.gallery.cloud.provider", new GallerySyncInfoProvider());
        authorityMap.put("call_log", new CalllogSyncInfoProvider());
        authorityMap.put("notes", new NotesSyncInfoProvider());
        authorityMap.put("wifi", new WifiSyncInfoProvider());
        authorityMap.put("records", new SoundRecorderSyncInfoProvider());
        authorityMap.put("com.miui.browser", new BrowserSyncInfoProvider());
        authorityMap.put(GlobalBrowserSyncInfoProvider.AUTHORITY, new GlobalBrowserSyncInfoProvider());
        authorityMap.put("antispam", new AntispamSyncInfoProvider());
        authorityMap.put("com.android.calendar", new CalendarSyncInfoProvider());
        authorityMap.put("personal_assistant", new PersonalAssistantSyncInfoProvider());
        authorityMap.put("com.android.quicksearchbox.cloud", new QuickSearchBoxProvider());
        authorityMap.put("com.miui.player.cloud", new MusicSyncInfoProvider());
        authorityMap.put("com.miui.player", new MusicSyncInfoProvider());
        authorityMap.put(PhraseSyncInfoProvider.AUTHORITY, new PhraseSyncInfoProvider());
    }

    public static int getSyncedDataCount(Context context, String str) {
        SyncInfoProvider syncInfoProvider = authorityMap.get(str);
        if (syncInfoProvider != null) {
            return syncInfoProvider.getSyncedCount(context);
        }
        throw new SyncInfoUnavailableException("getSyncedDataCount not implemented on authority: " + str);
    }

    public static int getUnSyncedSecretDataCount(Context context, String str) {
        SyncInfoProvider syncInfoProvider = authorityMap.get(str);
        if (syncInfoProvider != null) {
            return syncInfoProvider.getUnSyncedSecretCount(context);
        }
        throw new SyncInfoUnavailableException("getUnsyncedSecretDataCount not implemented on authority: " + str);
    }

    public static int getUnsyncedDataCount(Context context, String str) {
        SyncInfoProvider syncInfoProvider = authorityMap.get(str);
        if (syncInfoProvider != null) {
            return syncInfoProvider.getUnsyncedCount(context);
        }
        throw new SyncInfoUnavailableException("getUnsyncedDataCount not implemented on authority: " + str);
    }

    public static int getWifiOnlyUnsyncedDataCount(Context context, String str) {
        SyncInfoProvider syncInfoProvider = authorityMap.get(str);
        if (syncInfoProvider != null) {
            return syncInfoProvider.getWifiOnlyUnsyncedCount(context);
        }
        throw new SyncInfoUnavailableException("getWifiOnlyUnsyncedDataCount not implemented on authority: " + str);
    }
}

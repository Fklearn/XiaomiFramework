package miui.cloud.sync.providers;

import android.content.Context;
import android.net.Uri;
import miui.cloud.sync.SyncInfoProviderBase;

public class PhraseSyncInfoProvider extends SyncInfoProviderBase {
    public static final String AUTHORITY = "miui.phrase";
    private static final String TAG = "PhraseSyncInfoProvider";
    private Uri SYNCED_CONTENT_URI = Uri.parse("content://miui.phrase/phrase/synced");
    private Uri UNSYNCED_CONTENT_URI = Uri.parse("content://miui.phrase/phrase/unsynced");

    public int getSyncedCount(Context context) {
        return queryCount(context, this.SYNCED_CONTENT_URI, (String) null, (String[]) null);
    }

    public int getUnsyncedCount(Context context) {
        return queryCount(context, this.UNSYNCED_CONTENT_URI, (String) null, (String[]) null);
    }

    public int getWifiOnlyUnsyncedCount(Context context) {
        return 0;
    }
}

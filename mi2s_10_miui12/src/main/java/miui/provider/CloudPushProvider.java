package miui.provider;

import android.accounts.Account;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import java.util.List;
import miui.accounts.ExtraAccountManager;

public abstract class CloudPushProvider extends ContentProvider {
    public static final String NAME_COLUMNS = "name";
    public static final int NAME_COLUMNS_INDEX = 0;
    public static final String[] PROJECTION = {"name", "value", "type"};
    public static final String TYPE_COLUMNS = "type";
    public static final int TYPE_COLUMNS_INDEX = 2;
    public static final String VALUE_COLUMNS = "value";
    public static final int VALUE_COLUMNS_INDEX = 1;
    private static final int WATERMARK_LIST = 1;
    private static final String WATERMARK_LIST_PATH = "watermark_list";

    /* access modifiers changed from: protected */
    public abstract String getAuthority();

    /* access modifiers changed from: protected */
    public abstract List<Watermark> getWatermarkList(Context context, Account account);

    public boolean onCreate() {
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        UriMatcher uriMatcher = new UriMatcher(-1);
        uriMatcher.addURI(getAuthority(), WATERMARK_LIST_PATH, 1);
        Account account = ExtraAccountManager.getXiaomiAccount(getContext());
        if (uriMatcher.match(uri) == 1) {
            return getWatermarkListCursor(account);
        }
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private MatrixCursor getWatermarkListCursor(Account account) {
        MatrixCursor cursor = null;
        if (account != null) {
            cursor = new MatrixCursor(PROJECTION);
            for (Watermark watermark : getWatermarkList(getContext(), account)) {
                cursor.addRow(new Object[]{watermark.mName, Long.valueOf(watermark.mValue), watermark.mType});
            }
        }
        return cursor;
    }

    public static class Watermark {
        public String mName;
        public String mType;
        public long mValue;

        public Watermark(String name, long value, String type) {
            this.mName = name;
            this.mValue = value;
            this.mType = type;
        }
    }
}

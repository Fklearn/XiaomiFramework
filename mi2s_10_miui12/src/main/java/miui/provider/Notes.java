package miui.provider;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import miui.telephony.phonenumber.Prefix;

public class Notes {
    public static final String AUTHORITY = "notes";
    public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";
    private static final String NOTES_PACKAGE_NAME = "com.miui.notes";
    private static final String TAG = "Notes";

    public interface Account extends BaseColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final Uri CONTENT_URI = Uri.parse("content://notes/account");
        public static final String DATA = "data";
    }

    public interface CallData extends Data {
        public static final String CALL_DATE = "data1";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/call_note";
        public static final String PHONE_NUMBER = "data3";
    }

    public interface Data extends BaseColumns {
        public static final String CONTENT = "content";
        public static final Uri CONTENT_URI = Uri.parse("content://notes/data");
        public static final Uri CONTENT_URI_FOR_SYNC_ADAPTER = Notes.appendSyncAdapterFlag(CONTENT_URI);
        public static final String CREATED_DATE = "created_date";
        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATA3 = "data3";
        public static final String DATA4 = "data4";
        public static final String DATA5 = "data5";
        public static final String DIRTY = "dirty";
        public static final String FILE_ID = "file_id";
        public static final String ID = "_id";
        public static final Uri MEDIA_URI = Uri.parse("content://notes/data/media");
        public static final String MIME_TYPE = "mime_type";
        public static final String MODIFIED_DATE = "modified_date";
        public static final String NOTE_ID = "note_id";
    }

    public static class Intents {
        public static final String INTENT_ACTION_REFRESH_ALARM = "com.miui.notes.action.REFRESH_ALARM";
        public static final String INTENT_ACTION_REFRESH_WIDGET = "com.miui.notes.action.REFRESH_WIDGET";
        public static final String INTENT_EXTRA_ALERT_DATE = "com.miui.notes.alert_date";
        public static final String INTENT_EXTRA_BACKGROUND_ID = "com.miui.notes.background_color_id";
        public static final String INTENT_EXTRA_CALL_DATE = "com.miui.notes.call_date";
        public static final String INTENT_EXTRA_FOLDER_ID = "com.miui.notes.folder_id";
        public static final String INTENT_EXTRA_SNIPPET = "com.miui.notes.snippet";
        public static final String INTENT_EXTRA_SOURCE_INTENT = "com.miui.notes.source_intent";
        public static final String INTENT_EXTRA_SOURCE_NAME = "com.miui.notes.source_name";
        public static final String INTENT_EXTRA_WIDGET_ID = "com.miui.notes.widget_id";
        public static final String INTENT_EXTRA_WIDGET_TYPE = "com.miui.notes.widget_type";
    }

    public interface Note extends BaseColumns {
        public static final String ACCOUNT_ID = "account_id";
        public static final String ALERTED_DATE = "alert_date";
        public static final String ALERT_TAG = "alert_tag";
        public static final String BG_COLOR_ID = "bg_color_id";
        public static final Uri CONTENT_URI = Uri.parse("content://notes/note");
        public static final Uri CONTENT_URI_ATOMIC = Uri.parse("content://notes/note/atomic");
        public static final Uri CONTENT_URI_FOR_SYNC_ADAPTER = Notes.appendSyncAdapterFlag(CONTENT_URI);
        public static final String CREATED_DATE = "created_date";
        public static final String DELETION_TAG = "deletion_tag";
        public static final String HAS_ATTACHMENT = "has_attachment";
        public static final String ID = "_id";
        public static final int ID_CALL_RECORD_FOLDER = -2;
        public static final int ID_PRIVACY_FOLER = -4;
        public static final int ID_ROOT_FOLDER = 0;
        public static final int ID_TEMPARAY_FOLDER = -1;
        public static final int ID_TRASH_FOLER = -3;
        public static final String IN_VALID_FOLDER_SELECTION = "(parent_id>=0 OR parent_id=-2 OR parent_id=-4)";
        public static final String LOCAL_MODIFIED = "local_modified";
        public static final String MODIFIED_DATE = "modified_date";
        public static final String MOVED_DATE = "moved_date";
        public static final String NOTES_COUNT = "notes_count";
        public static final String ORIGIN_PARENT_ID = "origin_parent_id";
        public static final String PARENT_ID = "parent_id";
        public static final String PLAIN_TEXT = "plain_text";
        public static final String SNIPPET = "snippet";
        public static final String SOURCE_INTENT = "source_intent";
        public static final String SOURCE_NAME = "source_name";
        public static final String SOURCE_PACKAGE = "source_package";
        public static final String STICK_DATE = "stick_date";
        public static final String SUBJECT = "subject";
        public static final String SYNC_DATA1 = "sync_data1";
        public static final String SYNC_DATA2 = "sync_data2";
        public static final String SYNC_DATA3 = "sync_data3";
        public static final String SYNC_DATA4 = "sync_data4";
        public static final String SYNC_DATA5 = "sync_data5";
        public static final String SYNC_DATA6 = "sync_data6";
        public static final String SYNC_DATA7 = "sync_data7";
        public static final String SYNC_DATA8 = "sync_data8";
        public static final String SYNC_ID = "sync_id";
        public static final String SYNC_TAG = "sync_tag";
        public static final String THEME_ID = "theme_id";
        public static final String TYPE = "type";
        public static final int TYPE_FOLDER = 1;
        public static final int TYPE_NOTE = 0;
        public static final int TYPE_SYSTEM = 2;
        public static final int TYPE_WIDGET_INVALIDE = -1;
        public static final int TYPE_WIDGET_SIMPLE = 1;
        public static final String VALID_FOLDER_SELECTION = "((type=1 AND parent_id=0) OR _id=-2)";
        public static final String VERSION = "version";
        public static final String WIDGET_ID = "widget_id";
        public static final String WIDGET_TYPE = "widget_type";
    }

    public interface TextData extends Data {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/text_note";
    }

    public static Uri appendSyncAdapterFlag(Uri uri) {
        return uri.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build();
    }

    public static final class Utils {
        public static final int CLEAR_ACCOUNT_WIPE_ALL = 0;
        public static final int CLEAR_ACCOUNT_WIPE_NONE = 2;
        public static final int CLEAR_ACCOUNT_WIPE_SYNC = 1;
        private static final int IMAGE_DIMENSION_MAX = 1920;
        private static final String KEY_DATA_BYTES = "data_bytes";
        private static final String KEY_DATA_VALUES = "data_values";

        public static Bitmap createThumbnail(String imgPath) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bitmap = scaleBitmap(imgPath, opts);
            if (bitmap == null) {
                Log.e(Notes.TAG, "Fail to createThumbnail");
                return null;
            }
            Bitmap bitmap2 = rotateBitmap(imgPath, bitmap, opts.outMimeType);
            if (bitmap2 != null) {
                return bitmap2;
            }
            Log.e(Notes.TAG, "Fail to rotateBitmap");
            return null;
        }

        private static Bitmap scaleBitmap(String imgPath, BitmapFactory.Options opts) {
            String str = imgPath;
            BitmapFactory.Options options = opts;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgPath, opts);
            int width = options.outWidth;
            int height = options.outHeight;
            if (width <= 0 || height <= 0) {
                return null;
            }
            if (width > IMAGE_DIMENSION_MAX || height > IMAGE_DIMENSION_MAX) {
                float scaleRatio = ((float) Math.max(width, height)) / 1920.0f;
                int destWidth = Math.max(1, (int) (((float) width) / scaleRatio));
                int destHeight = Math.max(1, (int) (((float) height) / scaleRatio));
                int sampleSize = 1;
                for (int sampleRatio = (int) scaleRatio; sampleRatio > 1; sampleRatio >>= 1) {
                    sampleSize <<= 1;
                }
                if ((width * height) / (sampleSize * sampleSize) > 7372800) {
                    return scaleBitmapByRegion(imgPath, width, height, destWidth, destHeight, sampleSize);
                }
                return miui.graphics.BitmapFactory.decodeBitmap(str, destWidth, destHeight, false);
            }
            try {
                return miui.graphics.BitmapFactory.decodeBitmap(str, false);
            } catch (IOException e) {
                Log.e(Notes.TAG, "Fail to decode " + str, e);
                return null;
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:17:0x00ad, code lost:
            if (r9 != null) goto L_0x008e;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static android.graphics.Bitmap scaleBitmapByRegion(java.lang.String r16, int r17, int r18, int r19, int r20, int r21) throws java.io.IOException {
            /*
                r1 = r17
                r2 = r18
                r3 = r19
                r4 = r20
                r0 = 0
                r5 = r16
                android.graphics.BitmapRegionDecoder r6 = android.graphics.BitmapRegionDecoder.newInstance(r5, r0)
                r7 = 0
                r8 = 0
                r9 = 0
                int r10 = r1 >> 1
                int r11 = r2 >> 1
                int r12 = r3 >> 1
                int r13 = r4 >> 1
                android.graphics.Rect r14 = new android.graphics.Rect     // Catch:{ OutOfMemoryError -> 0x0094 }
                r14.<init>()     // Catch:{ OutOfMemoryError -> 0x0094 }
                android.graphics.Rect r15 = new android.graphics.Rect     // Catch:{ OutOfMemoryError -> 0x0094 }
                r15.<init>()     // Catch:{ OutOfMemoryError -> 0x0094 }
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ OutOfMemoryError -> 0x0094 }
                android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r3, r4, r0)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r7 = r0
                android.graphics.Canvas r0 = new android.graphics.Canvas     // Catch:{ OutOfMemoryError -> 0x0094 }
                r0.<init>(r7)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r9 = r0
                android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ OutOfMemoryError -> 0x0094 }
                r0.<init>()     // Catch:{ OutOfMemoryError -> 0x0094 }
                r5 = r21
                r0.inSampleSize = r5     // Catch:{ OutOfMemoryError -> 0x0094 }
                r5 = 0
                r14.set(r5, r5, r10, r11)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r15.set(r5, r5, r12, r13)     // Catch:{ OutOfMemoryError -> 0x0094 }
                android.graphics.Bitmap r5 = r6.decodeRegion(r14, r0)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r8 = r5
                r5 = 0
                r9.drawBitmap(r8, r5, r15, r5)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r8.recycle()     // Catch:{ OutOfMemoryError -> 0x0094 }
                r5 = 0
                r14.set(r10, r5, r1, r11)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r15.set(r12, r5, r3, r13)     // Catch:{ OutOfMemoryError -> 0x0094 }
                android.graphics.Bitmap r5 = r6.decodeRegion(r14, r0)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r8 = r5
                r5 = 0
                r9.drawBitmap(r8, r5, r15, r5)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r8.recycle()     // Catch:{ OutOfMemoryError -> 0x0094 }
                r5 = 0
                r14.set(r5, r11, r10, r2)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r15.set(r5, r13, r12, r4)     // Catch:{ OutOfMemoryError -> 0x0094 }
                android.graphics.Bitmap r5 = r6.decodeRegion(r14, r0)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r8 = r5
                r5 = 0
                r9.drawBitmap(r8, r5, r15, r5)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r8.recycle()     // Catch:{ OutOfMemoryError -> 0x0094 }
                r14.set(r10, r11, r1, r2)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r15.set(r12, r13, r3, r4)     // Catch:{ OutOfMemoryError -> 0x0094 }
                android.graphics.Bitmap r5 = r6.decodeRegion(r14, r0)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r8 = r5
                r5 = 0
                r9.drawBitmap(r8, r5, r15, r5)     // Catch:{ OutOfMemoryError -> 0x0094 }
                r8.recycle()     // Catch:{ OutOfMemoryError -> 0x0094 }
                r6.recycle()
                r8.recycle()
            L_0x008e:
                releaseCanvas(r9)
                goto L_0x00b0
            L_0x0092:
                r0 = move-exception
                goto L_0x00b1
            L_0x0094:
                r0 = move-exception
                java.lang.String r5 = "Notes"
                java.lang.String r10 = "Fail to scaleBitmapByRegion"
                android.util.Log.e(r5, r10, r0)     // Catch:{ all -> 0x0092 }
                if (r7 == 0) goto L_0x00a3
                r7.recycle()     // Catch:{ all -> 0x0092 }
                r5 = 0
                r7 = r5
            L_0x00a3:
                if (r6 == 0) goto L_0x00a8
                r6.recycle()
            L_0x00a8:
                if (r8 == 0) goto L_0x00ad
                r8.recycle()
            L_0x00ad:
                if (r9 == 0) goto L_0x00b0
                goto L_0x008e
            L_0x00b0:
                return r7
            L_0x00b1:
                if (r6 == 0) goto L_0x00b6
                r6.recycle()
            L_0x00b6:
                if (r8 == 0) goto L_0x00bb
                r8.recycle()
            L_0x00bb:
                if (r9 == 0) goto L_0x00c0
                releaseCanvas(r9)
            L_0x00c0:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: miui.provider.Notes.Utils.scaleBitmapByRegion(java.lang.String, int, int, int, int, int):android.graphics.Bitmap");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:30:0x0086, code lost:
            if (r8 == null) goto L_0x0089;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static android.graphics.Bitmap rotateBitmap(java.lang.String r11, android.graphics.Bitmap r12, java.lang.String r13) {
            /*
                java.lang.String r0 = "Notes"
                java.lang.String r1 = "image/jpeg"
                boolean r1 = r1.equals(r13)
                if (r1 != 0) goto L_0x000b
                return r12
            L_0x000b:
                android.media.ExifInterface r1 = new android.media.ExifInterface     // Catch:{ IOException -> 0x0093 }
                r1.<init>(r11)     // Catch:{ IOException -> 0x0093 }
                r2 = 1
                java.lang.String r3 = "Orientation"
                int r3 = r1.getAttributeInt(r3, r2)
                if (r3 != r2) goto L_0x001b
                return r12
            L_0x001b:
                int r2 = r12.getWidth()
                int r4 = r12.getHeight()
                r5 = 0
                android.graphics.Matrix r6 = new android.graphics.Matrix
                r6.<init>()
                r7 = 3
                if (r3 == r7) goto L_0x004e
                r7 = 6
                if (r3 == r7) goto L_0x0041
                r7 = 8
                if (r3 == r7) goto L_0x0034
                goto L_0x005a
            L_0x0034:
                r7 = 1132920832(0x43870000, float:270.0)
                int r8 = r2 / 2
                float r8 = (float) r8
                int r9 = r2 / 2
                float r9 = (float) r9
                r6.postRotate(r7, r8, r9)
                r5 = 1
                goto L_0x005a
            L_0x0041:
                r7 = 1119092736(0x42b40000, float:90.0)
                int r8 = r4 / 2
                float r8 = (float) r8
                int r9 = r4 / 2
                float r9 = (float) r9
                r6.postRotate(r7, r8, r9)
                r5 = 1
                goto L_0x005a
            L_0x004e:
                r7 = 1127481344(0x43340000, float:180.0)
                int r8 = r2 / 2
                float r8 = (float) r8
                int r9 = r4 / 2
                float r9 = (float) r9
                r6.postRotate(r7, r8, r9)
            L_0x005a:
                if (r5 == 0) goto L_0x005f
                r7 = r2
                r2 = r4
                r4 = r7
            L_0x005f:
                r7 = 0
                r8 = 0
                android.graphics.Bitmap$Config r9 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ OutOfMemoryError -> 0x007c }
                android.graphics.Bitmap r9 = android.graphics.Bitmap.createBitmap(r2, r4, r9)     // Catch:{ OutOfMemoryError -> 0x007c }
                r7 = r9
                android.graphics.Canvas r9 = new android.graphics.Canvas     // Catch:{ OutOfMemoryError -> 0x007c }
                r9.<init>(r7)     // Catch:{ OutOfMemoryError -> 0x007c }
                r8 = r9
                r9 = 0
                r8.drawBitmap(r12, r6, r9)     // Catch:{ OutOfMemoryError -> 0x007c }
                r12.recycle()
            L_0x0076:
                releaseCanvas(r8)
                goto L_0x0089
            L_0x007a:
                r0 = move-exception
                goto L_0x008a
            L_0x007c:
                r9 = move-exception
                java.lang.String r10 = "Fail to rotateBitmap"
                android.util.Log.e(r0, r10, r9)     // Catch:{ all -> 0x007a }
                r12.recycle()
                if (r8 == 0) goto L_0x0089
                goto L_0x0076
            L_0x0089:
                return r7
            L_0x008a:
                r12.recycle()
                if (r8 == 0) goto L_0x0092
                releaseCanvas(r8)
            L_0x0092:
                throw r0
            L_0x0093:
                r1 = move-exception
                java.lang.String r2 = "createThumbnail fail"
                android.util.Log.e(r0, r2, r1)
                return r12
            */
            throw new UnsupportedOperationException("Method not decompiled: miui.provider.Notes.Utils.rotateBitmap(java.lang.String, android.graphics.Bitmap, java.lang.String):android.graphics.Bitmap");
        }

        private static void releaseCanvas(Canvas canvas) {
            try {
                Method method = Canvas.class.getDeclaredMethod("release", new Class[0]);
                method.setAccessible(true);
                method.invoke(canvas, new Object[0]);
            } catch (Exception e) {
                Log.w(Notes.TAG, "invoke Canvas.release failed", e);
            }
        }

        public static boolean clearAccount(Context context, boolean wipeData) {
            return clearAccount(context, wipeData ? 0 : 2);
        }

        public static boolean clearAccount(Context context, int wipeMode) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ops.add(getAccountDeleteOP());
            if (wipeMode != 0) {
                if (wipeMode == 1) {
                    ops.add(getNoteDeleteOP(true));
                } else if (wipeMode != 2) {
                    Log.w(Notes.TAG, "Unknown wipeMode: " + wipeMode);
                }
                ops.add(getTemporaryDeleteOP());
                ops.add(getSyncClearOP());
                ops.add(getDirtyUpdateOP());
            } else {
                ops.add(getNoteDeleteOP(false));
            }
            try {
                context.getContentResolver().applyBatch("notes", ops);
                if (wipeMode != 2) {
                    updateAllWidgets(context);
                }
                return true;
            } catch (RemoteException e) {
                Log.e(Notes.TAG, "Fail to clear account", e);
                return false;
            } catch (OperationApplicationException e2) {
                Log.e(Notes.TAG, "Fail to clear account", e2);
                return false;
            }
        }

        private static ContentProviderOperation getAccountDeleteOP() {
            return ContentProviderOperation.newDelete(Account.CONTENT_URI).build();
        }

        private static ContentProviderOperation getNoteDeleteOP(boolean onlyWipeSync) {
            String selection = "_id>0";
            if (onlyWipeSync) {
                selection = selection + " AND sync_id>0 AND local_modified=0";
            }
            return ContentProviderOperation.newDelete(Note.CONTENT_URI_FOR_SYNC_ADAPTER).withSelection(selection, (String[]) null).build();
        }

        private static ContentProviderOperation getTemporaryDeleteOP() {
            return ContentProviderOperation.newDelete(Note.CONTENT_URI_FOR_SYNC_ADAPTER).withSelection("not (parent_id>=0 OR parent_id=-2 OR parent_id=-4)", (String[]) null).build();
        }

        private static ContentProviderOperation getSyncClearOP() {
            ContentValues values = new ContentValues();
            values.put(Note.SYNC_ID, 0);
            values.put(Note.SYNC_TAG, Prefix.EMPTY);
            values.put(Note.LOCAL_MODIFIED, 1);
            values.put("version", 0);
            values.put(Note.ORIGIN_PARENT_ID, 0);
            values.put("account_id", 0);
            values.put(Note.SYNC_DATA1, 0);
            values.put(Note.SYNC_DATA2, 0);
            values.put(Note.SYNC_DATA3, 0);
            values.put(Note.SYNC_DATA4, 0);
            values.put(Note.SYNC_DATA5, 0);
            values.put(Note.SYNC_DATA6, Prefix.EMPTY);
            values.put(Note.SYNC_DATA7, Prefix.EMPTY);
            values.put(Note.SYNC_DATA8, Prefix.EMPTY);
            return ContentProviderOperation.newUpdate(Note.CONTENT_URI_FOR_SYNC_ADAPTER).withValues(values).withSelection("_id>0", (String[]) null).build();
        }

        private static ContentProviderOperation getDirtyUpdateOP() {
            ContentValues values = new ContentValues();
            values.put(Data.DIRTY, 1);
            values.put("file_id", Prefix.EMPTY);
            return ContentProviderOperation.newUpdate(Data.CONTENT_URI_FOR_SYNC_ADAPTER).withValues(values).build();
        }

        public static int updateNoteAtomic(Context context, long noteId, ContentValues noteValues, ArrayList<ContentValues> dataValuesList, String selection, String[] selectionArgs) {
            return updateNoteAtomic(context, noteId, noteValues, dataValuesList, selection, selectionArgs, false);
        }

        public static int updateNoteAtomic(Context context, long noteId, ContentValues noteValues, ArrayList<ContentValues> dataValuesList, String selection, String[] selectionArgs, boolean isSyncAdapter) {
            addDataValuesToNoteValues(noteValues, dataValuesList);
            Uri uri = ContentUris.withAppendedId(Note.CONTENT_URI_ATOMIC, noteId);
            if (isSyncAdapter) {
                uri = Notes.appendSyncAdapterFlag(uri);
            }
            return context.getContentResolver().update(uri, noteValues, selection, selectionArgs);
        }

        public static Uri insertNoteAtomic(Context context, ContentValues noteValues, ArrayList<ContentValues> dataValuesList) {
            return insertNoteAtomic(context, noteValues, dataValuesList, false);
        }

        public static Uri insertNoteAtomic(Context context, ContentValues noteValues, ArrayList<ContentValues> dataValuesList, boolean isSyncAdapter) {
            addDataValuesToNoteValues(noteValues, dataValuesList);
            Uri uri = Note.CONTENT_URI_ATOMIC;
            if (isSyncAdapter) {
                uri = Notes.appendSyncAdapterFlag(uri);
            }
            return context.getContentResolver().insert(uri, noteValues);
        }

        private static void addDataValuesToNoteValues(ContentValues noteValues, ArrayList<ContentValues> dataValuesList) {
            if (dataValuesList != null && !dataValuesList.isEmpty()) {
                removeSnippetIfHasDataContent(noteValues, dataValuesList);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(KEY_DATA_VALUES, dataValuesList);
                noteValues.put(KEY_DATA_BYTES, marshall(bundle));
            }
        }

        private static void removeSnippetIfHasDataContent(ContentValues noteValues, ArrayList<ContentValues> dataValuesList) {
            String dataContent;
            if (noteValues.containsKey("snippet") && (dataContent = getContentFromData(dataValuesList)) != null && dataContent.equals(noteValues.getAsString("snippet"))) {
                noteValues.put("snippet", (String) null);
            }
        }

        private static String getContentFromData(ArrayList<ContentValues> dataValuesList) {
            Iterator<ContentValues> it = dataValuesList.iterator();
            while (it.hasNext()) {
                ContentValues values = it.next();
                if (values.containsKey(Data.MIME_TYPE) && TextData.CONTENT_ITEM_TYPE.equals(values.getAsString(Data.MIME_TYPE)) && values.containsKey("content")) {
                    return values.getAsString("content");
                }
            }
            return null;
        }

        private static byte[] marshall(Bundle bundle) {
            Parcel parcel = Parcel.obtain();
            try {
                bundle.writeToParcel(parcel, 0);
                return parcel.marshall();
            } finally {
                parcel.recycle();
            }
        }

        private static Bundle unmarshall(byte[] data) {
            Parcel parcel = Parcel.obtain();
            try {
                parcel.unmarshall(data, 0, data.length);
                parcel.setDataPosition(0);
                return parcel.readBundle();
            } finally {
                parcel.recycle();
            }
        }

        public static ArrayList<ContentValues> removeDataValuesFromNoteValues(ContentValues noteValues) {
            String dataContent;
            byte[] data = noteValues.getAsByteArray(KEY_DATA_BYTES);
            noteValues.remove(KEY_DATA_BYTES);
            if (data == null) {
                return null;
            }
            ArrayList<ContentValues> dataArray = unmarshall(data).getParcelableArrayList(KEY_DATA_VALUES);
            if (noteValues.containsKey("snippet") && noteValues.getAsString("snippet") == null && (dataContent = getContentFromData(dataArray)) != null) {
                noteValues.put("snippet", dataContent);
            }
            return dataArray;
        }

        public static void updateAllAlarms(Context context) {
            Intent intent = new Intent(Intents.INTENT_ACTION_REFRESH_ALARM);
            intent.setPackage(Notes.NOTES_PACKAGE_NAME);
            context.sendBroadcast(intent);
        }

        public static void updateAllWidgets(Context context) {
            Intent intent = new Intent(Intents.INTENT_ACTION_REFRESH_WIDGET);
            intent.setPackage(Notes.NOTES_PACKAGE_NAME);
            context.sendBroadcast(intent);
        }

        public static int getTotalUnsyncedCount(Context context) {
            int totalCount = 0;
            for (int count : getUnsyncedCount(context)) {
                totalCount += count;
            }
            return totalCount;
        }

        public static int[] getUnsyncedCount(Context context) {
            int noteCount = 0;
            int folderCount = 0;
            Cursor cursor = context.getContentResolver().query(Note.CONTENT_URI_FOR_SYNC_ADAPTER, new String[]{"_id", "type"}, "local_modified=1 AND _id>0 AND (sync_id>0 OR (sync_id<=0 AND snippet<>''))", (String[]) null, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    try {
                        int type = cursor.getInt(1);
                        if (type == 0) {
                            noteCount++;
                        } else if (type == 1) {
                            folderCount++;
                        }
                    } finally {
                        cursor.close();
                    }
                }
            } else {
                Log.e(Notes.TAG, "getUnsyncedCount: cursor is null");
            }
            return new int[]{noteCount, folderCount};
        }
    }
}

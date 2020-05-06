package com.miui.maml.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteFullException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.miui.activityutil.o;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.AsyncQueryHandler;
import com.miui.maml.data.VariableBinder;
import com.miui.maml.elements.ImageScreenElement;
import com.miui.maml.elements.ListScreenElement;
import com.miui.maml.util.TextFormatter;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import miui.cloud.CloudPushConstants;
import miui.os.SystemProperties;
import org.w3c.dom.Element;

public class ContentProviderBinder extends VariableBinder {
    private static final boolean DBG = false;
    private static final String LOG_TAG = "ContentProviderBinder";
    private static final int QUERY_TOKEN = 100;
    public static final String TAG_NAME = "ContentProviderBinder";
    protected String[] mArgs;
    private boolean mAwareChangeWhilePause;
    public ChangeObserver mChangeObserver;
    protected String[] mColumns;
    protected String mCountName;
    private IndexedVariable mCountVar;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private long mLastQueryTime;
    private Uri mLastUri;
    private List mList;
    private boolean mNeedsRequery;
    protected String mOrder;
    private QueryHandler mQueryHandler;
    private boolean mSystemBootCompleted;
    private int mUpdateInterval;
    private Runnable mUpdater;
    protected TextFormatter mUriFormatter;
    protected TextFormatter mWhereFormatter;

    /* renamed from: com.miui.maml.data.ContentProviderBinder$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type = new int[ListScreenElement.ColumnInfo.Type.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(12:0|1|2|3|4|5|6|7|8|9|10|(3:11|12|14)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type[] r0 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type = r0
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.DOUBLE     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.FLOAT     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.INTEGER     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.LONG     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.STRING     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x004b }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.BITMAP     // Catch:{ NoSuchFieldError -> 0x004b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.ContentProviderBinder.AnonymousClass2.<clinit>():void");
        }
    }

    public static class Builder {
        private ContentProviderBinder mBinder;

        protected Builder(ContentProviderBinder contentProviderBinder) {
            this.mBinder = contentProviderBinder;
        }

        public void addVariable(String str, String str2, String str3, int i, Variables variables) {
            Variable variable = new Variable(str, str2, variables);
            variable.mColumn = str3;
            variable.mRow = i;
            this.mBinder.addVariable(variable);
        }

        public Builder setArgs(String[] strArr) {
            this.mBinder.mArgs = strArr;
            return this;
        }

        public Builder setColumns(String[] strArr) {
            this.mBinder.mColumns = strArr;
            return this;
        }

        public Builder setCountName(String str) {
            ContentProviderBinder contentProviderBinder = this.mBinder;
            contentProviderBinder.mCountName = str;
            contentProviderBinder.createCountVar();
            return this;
        }

        public Builder setName(String str) {
            this.mBinder.mName = str;
            return this;
        }

        public Builder setOrder(String str) {
            this.mBinder.mOrder = str;
            return this;
        }

        public Builder setWhere(String str) {
            ContentProviderBinder contentProviderBinder = this.mBinder;
            contentProviderBinder.mWhereFormatter = new TextFormatter(contentProviderBinder.getVariables(), str);
            return this;
        }

        public Builder setWhere(String str, String str2) {
            ContentProviderBinder contentProviderBinder = this.mBinder;
            contentProviderBinder.mWhereFormatter = new TextFormatter(contentProviderBinder.getVariables(), str, str2);
            return this;
        }
    }

    protected class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(ContentProviderBinder.this.mHandler);
        }

        public boolean deliverSelfNotifications() {
            return true;
        }

        public void onChange(boolean z) {
            ContentProviderBinder.this.onContentChanged();
        }
    }

    private static class List {
        private ListScreenElement mList;
        private int mMaxCount;
        private String mName;
        private ScreenElementRoot mRoot;

        public List(Element element, ScreenElementRoot screenElementRoot) {
            this.mName = element.getAttribute(CloudPushConstants.XML_NAME);
            this.mMaxCount = Utils.getAttrAsInt(element, "maxCount", Integer.MAX_VALUE);
            this.mRoot = screenElementRoot;
        }

        public void fill(Cursor cursor) {
            String str;
            if (cursor != null) {
                if (this.mList == null) {
                    this.mList = (ListScreenElement) this.mRoot.findElement(this.mName);
                    if (this.mList == null) {
                        str = "fail to find list: " + this.mName;
                        Log.e("ContentProviderBinder", str);
                        return;
                    }
                }
                this.mList.removeAllItems();
                ArrayList<ListScreenElement.ColumnInfo> columnsInfo = this.mList.getColumnsInfo();
                int size = columnsInfo.size();
                int[] iArr = new int[size];
                Object[] objArr = new Object[size];
                int i = 0;
                while (i < iArr.length) {
                    try {
                        iArr[i] = cursor.getColumnIndexOrThrow(columnsInfo.get(i).mVarName);
                        i++;
                    } catch (IllegalArgumentException e) {
                        str = "illegal column:" + columnsInfo.get(i).mVarName + " " + e.toString();
                    }
                }
                cursor.moveToFirst();
                int count = cursor.getCount();
                int i2 = this.mMaxCount;
                if (count > i2) {
                    count = i2;
                }
                for (int i3 = 0; i3 < count; i3++) {
                    for (int i4 = 0; i4 < size; i4++) {
                        objArr[i4] = null;
                        ListScreenElement.ColumnInfo columnInfo = columnsInfo.get(i4);
                        int i5 = iArr[i4];
                        if (!cursor.isNull(i5)) {
                            int i6 = AnonymousClass2.$SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[columnInfo.mType.ordinal()];
                            if (i6 == 5) {
                                objArr[i4] = cursor.getString(i5);
                            } else if (i6 != 6) {
                                int i7 = AnonymousClass2.$SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[columnInfo.mType.ordinal()];
                                if (i7 == 1) {
                                    objArr[i4] = Double.valueOf(cursor.getDouble(i5));
                                } else if (i7 == 2) {
                                    objArr[i4] = Float.valueOf(cursor.getFloat(i5));
                                } else if (i7 == 3) {
                                    objArr[i4] = Integer.valueOf(cursor.getInt(i5));
                                } else if (i7 == 4) {
                                    objArr[i4] = Long.valueOf(cursor.getLong(i5));
                                }
                            } else {
                                byte[] blob = cursor.getBlob(i5);
                                if (blob != null) {
                                    objArr[i4] = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                                }
                            }
                        }
                    }
                    this.mList.addItem(objArr);
                    cursor.moveToNext();
                }
            }
        }
    }

    public interface QueryCompleteListener {
        void onQueryCompleted(String str);
    }

    private final class QueryHandler extends AsyncQueryHandler {

        protected class CatchingWorkerHandler extends AsyncQueryHandler.WorkerHandler {
            public CatchingWorkerHandler(Looper looper) {
                super(looper);
            }

            public void handleMessage(Message message) {
                try {
                    super.handleMessage(message);
                } catch (SQLiteDatabaseCorruptException | SQLiteDiskIOException | SQLiteFullException e) {
                    Log.w("ContentProviderBinder", "Exception on background worker thread", e);
                }
            }
        }

        public QueryHandler(Context context) {
            super(Looper.getMainLooper(), context.getContentResolver());
        }

        /* access modifiers changed from: protected */
        public Handler createHandler(Looper looper) {
            return new CatchingWorkerHandler(looper);
        }

        /* access modifiers changed from: protected */
        public void onQueryComplete(int i, Object obj, Cursor cursor) {
            ContentProviderBinder.this.onQueryComplete(cursor);
        }
    }

    private static class Variable extends VariableBinder.Variable {
        public static final int BLOB_BITMAP = 1001;
        public boolean mBlocked;
        public String mColumn;
        private ImageScreenElement mImageVar;
        private boolean mNoImageElement;
        public int mRow;

        public Variable(String str, String str2, Variables variables) {
            super(str, str2, variables);
        }

        public Variable(Element element, Variables variables) {
            super(element, variables);
            this.mColumn = element.getAttribute("column");
            this.mRow = Utils.getAttrAsInt(element, "row", 0);
        }

        public ImageScreenElement getImageElement(ScreenElementRoot screenElementRoot) {
            if (this.mImageVar == null && !this.mNoImageElement) {
                this.mImageVar = (ImageScreenElement) screenElementRoot.findElement(this.mName);
                this.mNoImageElement = this.mImageVar == null;
            }
            return this.mImageVar;
        }

        /* access modifiers changed from: protected */
        public int parseType(String str) {
            int parseType = super.parseType(str);
            if ("blob.bitmap".equalsIgnoreCase(this.mTypeStr)) {
                return 1001;
            }
            this.mNoImageElement = true;
            return parseType;
        }

        public void setNull(ScreenElementRoot screenElementRoot) {
            if (getImageElement(screenElementRoot) != null) {
                getImageElement(screenElementRoot).setBitmap((Bitmap) null);
            } else {
                set((Object) null);
            }
        }
    }

    public ContentProviderBinder(ScreenElementRoot screenElementRoot) {
        this((Element) null, screenElementRoot);
    }

    public ContentProviderBinder(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mChangeObserver = new ChangeObserver();
        this.mUpdateInterval = -1;
        this.mNeedsRequery = true;
        this.mHandler = screenElementRoot.getContext().getHandler();
        this.mQueryHandler = new QueryHandler(getContext().mContext);
        if (element != null) {
            load(element);
        }
    }

    /* access modifiers changed from: private */
    public void checkUpdate() {
        if (this.mUpdateInterval > 0) {
            this.mHandler.removeCallbacks(this.mUpdater);
            long currentTimeMillis = System.currentTimeMillis() - this.mLastQueryTime;
            if (currentTimeMillis >= ((long) (this.mUpdateInterval * 1000))) {
                startQuery();
                currentTimeMillis = 0;
            }
            this.mHandler.postDelayed(this.mUpdater, ((long) (this.mUpdateInterval * 1000)) - currentTimeMillis);
        }
    }

    private void load(Element element) {
        Variables variables = getVariables();
        this.mUriFormatter = new TextFormatter(variables, element.getAttribute("uri"), element.getAttribute("uriFormat"), element.getAttribute("uriParas"), Expression.build(variables, element.getAttribute("uriExp")), Expression.build(variables, element.getAttribute("uriFormatExp")));
        String attribute = element.getAttribute("columns");
        this.mColumns = TextUtils.isEmpty(attribute) ? null : attribute.split(",");
        this.mWhereFormatter = new TextFormatter(variables, element.getAttribute("where"), element.getAttribute("whereFormat"), element.getAttribute("whereParas"), Expression.build(variables, element.getAttribute("whereExp")), Expression.build(variables, element.getAttribute("whereFormatExp")));
        String attribute2 = element.getAttribute("args");
        this.mArgs = TextUtils.isEmpty(attribute2) ? null : attribute2.split(",");
        String attribute3 = element.getAttribute("order");
        if (TextUtils.isEmpty(attribute3)) {
            attribute3 = null;
        }
        this.mOrder = attribute3;
        String attribute4 = element.getAttribute("countName");
        if (TextUtils.isEmpty(attribute4)) {
            attribute4 = null;
        }
        this.mCountName = attribute4;
        String str = this.mCountName;
        if (str != null) {
            this.mCountVar = new IndexedVariable(str, variables, true);
        }
        this.mUpdateInterval = Utils.getAttrAsInt(element, "updateInterval", -1);
        if (this.mUpdateInterval > 0) {
            this.mUpdater = new Runnable() {
                public void run() {
                    ContentProviderBinder.this.checkUpdate();
                }
            };
        }
        loadVariables(element);
        Element child = Utils.getChild(element, ListScreenElement.TAG_NAME);
        if (child != null) {
            try {
                this.mList = new List(child, this.mRoot);
            } catch (IllegalArgumentException unused) {
                Log.e("ContentProviderBinder", "invalid List");
            }
        }
        this.mAwareChangeWhilePause = Boolean.parseBoolean(element.getAttribute("vigilant"));
    }

    /* access modifiers changed from: private */
    public void onQueryComplete(Cursor cursor) {
        if (!this.mFinished) {
            updateVariables(cursor);
        }
        if (cursor != null) {
            cursor.close();
        }
        onUpdateComplete();
    }

    private void registerObserver(Uri uri, boolean z) {
        String str;
        StringBuilder sb;
        ContentResolver contentResolver = getContext().mContext.getContentResolver();
        contentResolver.unregisterContentObserver(this.mChangeObserver);
        if (z) {
            try {
                contentResolver.registerContentObserver(uri, true, this.mChangeObserver);
                return;
            } catch (IllegalArgumentException e) {
                sb = new StringBuilder();
                str = e.toString();
            } catch (SecurityException e2) {
                sb = new StringBuilder();
                str = e2.toString();
            }
        } else {
            return;
        }
        sb.append(str);
        sb.append("  uri:");
        sb.append(uri);
        Log.e("ContentProviderBinder", sb.toString());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v22, resolved type: android.graphics.Bitmap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v25, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateVariables(android.database.Cursor r12) {
        /*
            r11 = this;
            java.lang.String r0 = "ContentProviderBinder"
            r1 = 0
            if (r12 != 0) goto L_0x0007
            r2 = r1
            goto L_0x000b
        L_0x0007:
            int r2 = r12.getCount()
        L_0x000b:
            com.miui.maml.data.IndexedVariable r3 = r11.mCountVar
            if (r3 == 0) goto L_0x0013
            double r4 = (double) r2
            r3.set((double) r4)
        L_0x0013:
            com.miui.maml.data.ContentProviderBinder$List r3 = r11.mList
            if (r3 == 0) goto L_0x001a
            r3.fill(r12)
        L_0x001a:
            if (r12 == 0) goto L_0x012f
            if (r2 != 0) goto L_0x0020
            goto L_0x012f
        L_0x0020:
            java.util.ArrayList<com.miui.maml.data.VariableBinder$Variable> r2 = r11.mVariables
            java.util.Iterator r2 = r2.iterator()
        L_0x0026:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x012e
            java.lang.Object r3 = r2.next()
            com.miui.maml.data.VariableBinder$Variable r3 = (com.miui.maml.data.VariableBinder.Variable) r3
            r4 = r3
            com.miui.maml.data.ContentProviderBinder$Variable r4 = (com.miui.maml.data.ContentProviderBinder.Variable) r4
            boolean r5 = r4.mBlocked
            if (r5 == 0) goto L_0x003a
            goto L_0x0026
        L_0x003a:
            r5 = 0
            int r7 = r4.mRow
            boolean r7 = r12.moveToPosition(r7)
            if (r7 == 0) goto L_0x0026
            java.lang.String r7 = r4.mColumn     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            int r7 = r12.getColumnIndexOrThrow(r7)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            boolean r8 = r12.isNull(r7)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            if (r8 != 0) goto L_0x00fc
            int r8 = r3.mType     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            r9 = 2
            if (r8 == r9) goto L_0x00f7
            r9 = 1001(0x3e9, float:1.403E-42)
            r10 = 7
            if (r8 == r9) goto L_0x00d9
            if (r8 == r10) goto L_0x00d9
            r9 = 8
            if (r8 == r9) goto L_0x00be
            r9 = 9
            if (r8 == r9) goto L_0x00a3
            int r8 = r3.mType     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            r9 = 3
            if (r8 == r9) goto L_0x009a
            r9 = 4
            if (r8 == r9) goto L_0x0094
            r9 = 5
            if (r8 == r9) goto L_0x008e
            r9 = 6
            if (r8 == r9) goto L_0x0089
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            r7.<init>()     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            java.lang.String r8 = "invalide type"
            r7.append(r8)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            java.lang.String r8 = r3.mTypeStr     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            r7.append(r8)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            java.lang.String r7 = r7.toString()     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            android.util.Log.w(r0, r7)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            goto L_0x009f
        L_0x0089:
            double r5 = r12.getDouble(r7)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            goto L_0x009f
        L_0x008e:
            float r5 = r12.getFloat(r7)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            double r5 = (double) r5     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            goto L_0x009f
        L_0x0094:
            long r5 = r12.getLong(r7)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            double r5 = (double) r5     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            goto L_0x009f
        L_0x009a:
            int r5 = r12.getInt(r7)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            double r5 = (double) r5     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
        L_0x009f:
            r3.set((double) r5)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            goto L_0x0026
        L_0x00a3:
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            r5.<init>()     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
        L_0x00a8:
            java.lang.String r6 = r12.getString(r7)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            r5.add(r6)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            boolean r6 = r12.moveToNext()     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            if (r6 != 0) goto L_0x00a8
            java.lang.Object[] r5 = r5.toArray()     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
        L_0x00b9:
            r3.set((java.lang.Object) r5)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            goto L_0x0026
        L_0x00be:
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            r5.<init>()     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
        L_0x00c3:
            double r8 = r12.getDouble(r7)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            java.lang.Double r6 = java.lang.Double.valueOf(r8)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            r5.add(r6)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            boolean r6 = r12.moveToNext()     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            if (r6 != 0) goto L_0x00c3
            java.lang.Object[] r5 = r5.toArray()     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            goto L_0x00b9
        L_0x00d9:
            r5 = 0
            byte[] r6 = r12.getBlob(r7)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            if (r6 == 0) goto L_0x00e5
            int r5 = r6.length     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeByteArray(r6, r1, r5)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
        L_0x00e5:
            int r6 = r3.mType     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            if (r6 != r10) goto L_0x00ea
            goto L_0x00b9
        L_0x00ea:
            com.miui.maml.ScreenElementRoot r3 = r11.mRoot     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            com.miui.maml.elements.ImageScreenElement r3 = r4.getImageElement(r3)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            if (r3 == 0) goto L_0x0026
            r3.setBitmap(r5)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            goto L_0x0026
        L_0x00f7:
            java.lang.String r5 = r12.getString(r7)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            goto L_0x00b9
        L_0x00fc:
            com.miui.maml.ScreenElementRoot r3 = r11.mRoot     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            r4.setNull(r3)     // Catch:{ NumberFormatException -> 0x0121, IllegalArgumentException -> 0x0109, Exception -> 0x0103 }
            goto L_0x0026
        L_0x0103:
            r3 = move-exception
            java.lang.String r3 = r3.toString()
            goto L_0x011c
        L_0x0109:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "column does not exist: "
            r3.append(r5)
            java.lang.String r4 = r4.mColumn
            r3.append(r4)
            java.lang.String r3 = r3.toString()
        L_0x011c:
            android.util.Log.e(r0, r3)
            goto L_0x0026
        L_0x0121:
            java.lang.Object[] r3 = new java.lang.Object[r1]
            java.lang.String r4 = "failed to get value from cursor"
            java.lang.String r3 = java.lang.String.format(r4, r3)
            android.util.Log.w(r0, r3)
            goto L_0x0026
        L_0x012e:
            return
        L_0x012f:
            java.util.ArrayList<com.miui.maml.data.VariableBinder$Variable> r12 = r11.mVariables
            java.util.Iterator r12 = r12.iterator()
        L_0x0135:
            boolean r0 = r12.hasNext()
            if (r0 == 0) goto L_0x0147
            java.lang.Object r0 = r12.next()
            com.miui.maml.data.ContentProviderBinder$Variable r0 = (com.miui.maml.data.ContentProviderBinder.Variable) r0
            com.miui.maml.ScreenElementRoot r1 = r11.mRoot
            r0.setNull(r1)
            goto L_0x0135
        L_0x0147:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.ContentProviderBinder.updateVariables(android.database.Cursor):void");
    }

    public void createCountVar() {
        String str = this.mCountName;
        if (str == null) {
            this.mCountVar = null;
        } else {
            this.mCountVar = new IndexedVariable(str, getContext().mVariables, true);
        }
    }

    public void finish() {
        this.mLastUri = null;
        registerObserver((Uri) null, false);
        this.mHandler.removeCallbacks(this.mUpdater);
        setBlockedColumns((String[]) null);
        super.finish();
    }

    public final String getUriText() {
        return this.mUriFormatter.getText();
    }

    public void onContentChanged() {
        Log.i("ContentProviderBinder", "ChangeObserver: content changed.");
        if (!this.mFinished) {
            if (!this.mPaused || this.mAwareChangeWhilePause) {
                startQuery();
            } else {
                this.mNeedsRequery = true;
            }
        }
    }

    /* access modifiers changed from: protected */
    public Variable onLoadVariable(Element element) {
        return new Variable(element, getContext().mVariables);
    }

    public void pause() {
        super.pause();
        this.mHandler.removeCallbacks(this.mUpdater);
    }

    public void refresh() {
        super.refresh();
        startQuery();
    }

    public void resume() {
        super.resume();
        if (this.mNeedsRequery) {
            startQuery();
        } else {
            checkUpdate();
        }
    }

    public final void setBlockedColumns(String[] strArr) {
        HashSet hashSet;
        if (strArr != null) {
            hashSet = new HashSet();
            for (String add : strArr) {
                hashSet.add(add);
            }
        } else {
            hashSet = null;
        }
        Iterator<VariableBinder.Variable> it = this.mVariables.iterator();
        while (it.hasNext()) {
            Variable variable = (Variable) it.next();
            variable.mBlocked = hashSet != null ? hashSet.contains(variable.mColumn) : false;
        }
    }

    public void startQuery() {
        String uriText = getUriText();
        if (uriText == null) {
            Log.e("ContentProviderBinder", "start query: uri null");
            return;
        }
        if (!this.mSystemBootCompleted && uriText.startsWith("content://sms/")) {
            this.mSystemBootCompleted = o.f2310b.equals(SystemProperties.get("sys.boot_completed"));
            if (!this.mSystemBootCompleted) {
                return;
            }
        }
        this.mNeedsRequery = false;
        this.mQueryHandler.cancelOperation(100);
        Uri parse = Uri.parse(uriText);
        if (parse != null) {
            if (this.mUpdateInterval == -1 && !parse.equals(this.mLastUri)) {
                registerObserver(parse, true);
                this.mLastUri = parse;
            }
            this.mQueryHandler.startQuery(100, (Object) null, parse, this.mColumns, this.mWhereFormatter.getText(), this.mArgs, this.mOrder);
            this.mLastQueryTime = System.currentTimeMillis();
            checkUpdate();
        }
    }
}

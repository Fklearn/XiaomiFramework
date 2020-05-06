package com.miui.maml.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.lang.ref.WeakReference;

public abstract class AsyncQueryHandler extends Handler {
    private static final int EVENT_ARG_DELETE = 4;
    private static final int EVENT_ARG_INSERT = 2;
    private static final int EVENT_ARG_QUERY = 1;
    private static final int EVENT_ARG_UPDATE = 3;
    private static final String TAG = "AsyncQuery";
    private static final boolean localLOGV = false;
    private static Looper sLooper;
    final WeakReference<ContentResolver> mResolver;
    private Handler mWorkerThreadHandler;

    protected static final class WorkerArgs {
        public Object cookie;
        public Handler handler;
        public String orderBy;
        public String[] projection;
        public Object result;
        public String selection;
        public String[] selectionArgs;
        public Uri uri;
        public ContentValues values;

        protected WorkerArgs() {
        }
    }

    protected class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            Integer num;
            int i;
            ContentResolver contentResolver = (ContentResolver) AsyncQueryHandler.this.mResolver.get();
            if (contentResolver != null) {
                WorkerArgs workerArgs = (WorkerArgs) message.obj;
                int i2 = message.what;
                int i3 = message.arg1;
                if (i3 == 1) {
                    try {
                        Cursor query = contentResolver.query(workerArgs.uri, workerArgs.projection, workerArgs.selection, workerArgs.selectionArgs, workerArgs.orderBy);
                        num = query;
                        if (query != null) {
                            query.getCount();
                            num = query;
                        }
                    } catch (Exception e) {
                        Log.w(AsyncQueryHandler.TAG, "Exception thrown during handling EVENT_ARG_QUERY", e);
                        num = null;
                    }
                } else if (i3 != 2) {
                    if (i3 != 3) {
                        if (i3 == 4) {
                            i = contentResolver.delete(workerArgs.uri, workerArgs.selection, workerArgs.selectionArgs);
                        }
                        Message obtainMessage = workerArgs.handler.obtainMessage(i2);
                        obtainMessage.obj = workerArgs;
                        obtainMessage.arg1 = message.arg1;
                        obtainMessage.sendToTarget();
                    }
                    i = contentResolver.update(workerArgs.uri, workerArgs.values, workerArgs.selection, workerArgs.selectionArgs);
                    num = Integer.valueOf(i);
                } else {
                    num = contentResolver.insert(workerArgs.uri, workerArgs.values);
                }
                workerArgs.result = num;
                Message obtainMessage2 = workerArgs.handler.obtainMessage(i2);
                obtainMessage2.obj = workerArgs;
                obtainMessage2.arg1 = message.arg1;
                obtainMessage2.sendToTarget();
            }
        }
    }

    public AsyncQueryHandler(ContentResolver contentResolver) {
        this.mResolver = new WeakReference<>(contentResolver);
        synchronized (android.content.AsyncQueryHandler.class) {
            if (sLooper == null) {
                HandlerThread handlerThread = new HandlerThread("AsyncQueryWorker");
                handlerThread.start();
                sLooper = handlerThread.getLooper();
            }
        }
        this.mWorkerThreadHandler = createHandler(sLooper);
    }

    public AsyncQueryHandler(Looper looper, ContentResolver contentResolver) {
        super(looper);
        this.mResolver = new WeakReference<>(contentResolver);
        synchronized (android.content.AsyncQueryHandler.class) {
            if (sLooper == null) {
                HandlerThread handlerThread = new HandlerThread("AsyncQueryWorker");
                handlerThread.start();
                sLooper = handlerThread.getLooper();
            }
        }
        this.mWorkerThreadHandler = createHandler(sLooper);
    }

    public final void cancelOperation(int i) {
        this.mWorkerThreadHandler.removeMessages(i);
    }

    /* access modifiers changed from: protected */
    public Handler createHandler(Looper looper) {
        return new WorkerHandler(looper);
    }

    public void handleMessage(Message message) {
        WorkerArgs workerArgs = (WorkerArgs) message.obj;
        int i = message.what;
        int i2 = message.arg1;
        if (i2 == 1) {
            onQueryComplete(i, workerArgs.cookie, (Cursor) workerArgs.result);
        } else if (i2 == 2) {
            onInsertComplete(i, workerArgs.cookie, (Uri) workerArgs.result);
        } else if (i2 == 3) {
            onUpdateComplete(i, workerArgs.cookie, ((Integer) workerArgs.result).intValue());
        } else if (i2 == 4) {
            onDeleteComplete(i, workerArgs.cookie, ((Integer) workerArgs.result).intValue());
        }
    }

    /* access modifiers changed from: protected */
    public void onDeleteComplete(int i, Object obj, int i2) {
    }

    /* access modifiers changed from: protected */
    public void onInsertComplete(int i, Object obj, Uri uri) {
    }

    /* access modifiers changed from: protected */
    public void onQueryComplete(int i, Object obj, Cursor cursor) {
    }

    /* access modifiers changed from: protected */
    public void onUpdateComplete(int i, Object obj, int i2) {
    }

    public final void startDelete(int i, Object obj, Uri uri, String str, String[] strArr) {
        Message obtainMessage = this.mWorkerThreadHandler.obtainMessage(i);
        obtainMessage.arg1 = 4;
        WorkerArgs workerArgs = new WorkerArgs();
        workerArgs.handler = this;
        workerArgs.uri = uri;
        workerArgs.cookie = obj;
        workerArgs.selection = str;
        workerArgs.selectionArgs = strArr;
        obtainMessage.obj = workerArgs;
        this.mWorkerThreadHandler.sendMessage(obtainMessage);
    }

    public final void startInsert(int i, Object obj, Uri uri, ContentValues contentValues) {
        Message obtainMessage = this.mWorkerThreadHandler.obtainMessage(i);
        obtainMessage.arg1 = 2;
        WorkerArgs workerArgs = new WorkerArgs();
        workerArgs.handler = this;
        workerArgs.uri = uri;
        workerArgs.cookie = obj;
        workerArgs.values = contentValues;
        obtainMessage.obj = workerArgs;
        this.mWorkerThreadHandler.sendMessage(obtainMessage);
    }

    public void startQuery(int i, Object obj, Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Message obtainMessage = this.mWorkerThreadHandler.obtainMessage(i);
        obtainMessage.arg1 = 1;
        WorkerArgs workerArgs = new WorkerArgs();
        workerArgs.handler = this;
        workerArgs.uri = uri;
        workerArgs.projection = strArr;
        workerArgs.selection = str;
        workerArgs.selectionArgs = strArr2;
        workerArgs.orderBy = str2;
        workerArgs.cookie = obj;
        obtainMessage.obj = workerArgs;
        this.mWorkerThreadHandler.sendMessage(obtainMessage);
    }

    public final void startUpdate(int i, Object obj, Uri uri, ContentValues contentValues, String str, String[] strArr) {
        Message obtainMessage = this.mWorkerThreadHandler.obtainMessage(i);
        obtainMessage.arg1 = 3;
        WorkerArgs workerArgs = new WorkerArgs();
        workerArgs.handler = this;
        workerArgs.uri = uri;
        workerArgs.cookie = obj;
        workerArgs.values = contentValues;
        workerArgs.selection = str;
        workerArgs.selectionArgs = strArr;
        obtainMessage.obj = workerArgs;
        this.mWorkerThreadHandler.sendMessage(obtainMessage);
    }
}

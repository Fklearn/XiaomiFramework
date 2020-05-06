package com.miui.maml;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.miui.maml.util.MobileDataUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class NotifierManager {
    /* access modifiers changed from: private */
    public static boolean DBG = true;
    private static final String LOG_TAG = "NotifierManager";
    public static String TYPE_MOBILE_DATA = "MobileData";
    public static String TYPE_TIME_CHANGED = "TimeChanged";
    public static String TYPE_WIFI_STATE = "WifiState";
    private static NotifierManager sInstance;
    private Context mContext;
    private HashMap<String, BaseNotifier> mNotifiers = new HashMap<>();

    public static abstract class BaseNotifier {
        private int mActiveCount;
        protected Context mContext;
        private ArrayList<Listener> mListeners = new ArrayList<>();
        private int mRefCount;
        private boolean mRegistered;

        private static class Listener {
            public Context context;
            public Intent intent;
            public Object obj;
            /* access modifiers changed from: private */
            public boolean paused;
            private boolean pendingNotify;
            public WeakReference<OnNotifyListener> ref;

            public Listener(OnNotifyListener onNotifyListener) {
                this.ref = new WeakReference<>(onNotifyListener);
            }

            public void onNotify(Context context2, Intent intent2, Object obj2) {
                if (this.paused) {
                    this.pendingNotify = true;
                    this.context = context2;
                    this.intent = intent2;
                    this.obj = obj2;
                    return;
                }
                OnNotifyListener onNotifyListener = (OnNotifyListener) this.ref.get();
                if (onNotifyListener != null) {
                    onNotifyListener.onNotify(context2, intent2, obj2);
                }
            }

            public void pause() {
                this.paused = true;
            }

            public void resume() {
                OnNotifyListener onNotifyListener;
                this.paused = false;
                if (this.pendingNotify && (onNotifyListener = (OnNotifyListener) this.ref.get()) != null) {
                    onNotifyListener.onNotify(this.context, this.intent, this.obj);
                    this.pendingNotify = false;
                    this.context = null;
                    this.intent = null;
                    this.obj = null;
                }
            }
        }

        public BaseNotifier(Context context) {
            this.mContext = context;
        }

        private final void checkListeners() {
            synchronized (this.mListeners) {
                checkListenersLocked();
            }
        }

        private final void checkListenersLocked() {
            this.mActiveCount = 0;
            for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                Listener listener = this.mListeners.get(size);
                if (listener.ref.get() == null) {
                    this.mListeners.remove(size);
                } else if (!listener.paused) {
                    this.mActiveCount++;
                }
            }
            this.mRefCount = this.mListeners.size();
        }

        private final Listener findListenerLocked(OnNotifyListener onNotifyListener) {
            Iterator<Listener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                Listener next = it.next();
                if (next.ref.get() == onNotifyListener) {
                    return next;
                }
            }
            return null;
        }

        public final void addListener(OnNotifyListener onNotifyListener) {
            synchronized (this.mListeners) {
                if (findListenerLocked(onNotifyListener) == null) {
                    this.mListeners.add(new Listener(onNotifyListener));
                    checkListenersLocked();
                }
            }
        }

        public void finish() {
            unregister();
        }

        public final int getActiveCount() {
            checkListeners();
            return this.mActiveCount;
        }

        public final int getRef() {
            checkListeners();
            return this.mRefCount;
        }

        public void init() {
            register();
        }

        /* access modifiers changed from: protected */
        public void onNotify(Context context, Intent intent, Object obj) {
            checkListeners();
            synchronized (this.mListeners) {
                Iterator<Listener> it = this.mListeners.iterator();
                while (it.hasNext()) {
                    it.next().onNotify(context, intent, obj);
                }
            }
        }

        /* access modifiers changed from: protected */
        public abstract void onRegister();

        /* access modifiers changed from: protected */
        public abstract void onUnregister();

        public void pause() {
            unregister();
        }

        public final int pauseListener(OnNotifyListener onNotifyListener) {
            synchronized (this.mListeners) {
                Listener findListenerLocked = findListenerLocked(onNotifyListener);
                if (findListenerLocked == null) {
                    Log.w(NotifierManager.LOG_TAG, "pauseListener, listener not exist");
                    int i = this.mActiveCount;
                    return i;
                }
                findListenerLocked.pause();
                checkListenersLocked();
                int i2 = this.mActiveCount;
                return i2;
            }
        }

        /* access modifiers changed from: protected */
        public void register() {
            if (!this.mRegistered) {
                onRegister();
                this.mRegistered = true;
                if (NotifierManager.DBG) {
                    Log.i(NotifierManager.LOG_TAG, "onRegister: " + toString());
                }
            }
        }

        public final void removeListener(OnNotifyListener onNotifyListener) {
            synchronized (this.mListeners) {
                Listener findListenerLocked = findListenerLocked(onNotifyListener);
                if (findListenerLocked != null) {
                    this.mListeners.remove(findListenerLocked);
                    checkListenersLocked();
                }
            }
        }

        public void resume() {
            register();
        }

        public final int resumeListener(OnNotifyListener onNotifyListener) {
            synchronized (this.mListeners) {
                Listener findListenerLocked = findListenerLocked(onNotifyListener);
                if (findListenerLocked == null) {
                    Log.w(NotifierManager.LOG_TAG, "resumeListener, listener not exist");
                    int i = this.mActiveCount;
                    return i;
                }
                findListenerLocked.resume();
                checkListenersLocked();
                int i2 = this.mActiveCount;
                return i2;
            }
        }

        /* access modifiers changed from: protected */
        public void unregister() {
            if (this.mRegistered) {
                try {
                    onUnregister();
                } catch (IllegalArgumentException e) {
                    Log.w(NotifierManager.LOG_TAG, e.toString());
                }
                this.mRegistered = false;
                if (NotifierManager.DBG) {
                    Log.i(NotifierManager.LOG_TAG, "onUnregister: " + toString());
                }
            }
        }
    }

    public static class BroadcastNotifier extends BaseNotifier {
        private String mAction;
        private IntentFilter mIntentFilter;
        private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (NotifierManager.DBG) {
                    Log.i(NotifierManager.LOG_TAG, "onNotify: " + BroadcastNotifier.this.toString());
                }
                BroadcastNotifier.this.onNotify(context, intent, (Object) null);
            }
        };

        public BroadcastNotifier(Context context) {
            super(context);
        }

        public BroadcastNotifier(Context context, String str) {
            super(context);
            this.mAction = str;
        }

        /* access modifiers changed from: protected */
        public IntentFilter createIntentFilter() {
            String intentAction = getIntentAction();
            if (intentAction == null) {
                return null;
            }
            return new IntentFilter(intentAction);
        }

        /* access modifiers changed from: protected */
        public String getIntentAction() {
            return this.mAction;
        }

        /* access modifiers changed from: protected */
        public void onRegister() {
            if (this.mIntentFilter == null) {
                this.mIntentFilter = createIntentFilter();
            }
            IntentFilter intentFilter = this.mIntentFilter;
            if (intentFilter == null) {
                Log.e(NotifierManager.LOG_TAG, "onRegister: mIntentFilter is null");
                return;
            }
            Intent registerReceiver = this.mContext.registerReceiver(this.mIntentReceiver, intentFilter);
            if (registerReceiver != null) {
                onNotify(this.mContext, registerReceiver, (Object) null);
            }
        }

        /* access modifiers changed from: protected */
        public void onUnregister() {
            try {
                this.mContext.unregisterReceiver(this.mIntentReceiver);
            } catch (IllegalArgumentException unused) {
            }
        }
    }

    public static class ContentChangeNotifier extends BaseNotifier {
        protected final ContentObserver mObserver = new ContentObserver((Handler) null) {
            public void onChange(boolean z) {
                if (NotifierManager.DBG) {
                    Log.i(NotifierManager.LOG_TAG, "onNotify: " + ContentChangeNotifier.this.toString());
                }
                ContentChangeNotifier.this.onNotify((Context) null, (Intent) null, Boolean.valueOf(z));
            }
        };
        private Uri mUri;

        public ContentChangeNotifier(Context context, Uri uri) {
            super(context);
            this.mUri = uri;
        }

        /* access modifiers changed from: protected */
        public void onRegister() {
            this.mContext.getContentResolver().registerContentObserver(this.mUri, false, this.mObserver);
            onNotify((Context) null, (Intent) null, true);
        }

        /* access modifiers changed from: protected */
        public void onUnregister() {
            this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
        }
    }

    public static class MobileDataNotifier extends ContentChangeNotifier {
        private MobileDataUtils mMobileDataUtils = MobileDataUtils.getInstance();

        public MobileDataNotifier(Context context) {
            super(context, (Uri) null);
        }

        /* access modifiers changed from: protected */
        public void onRegister() {
            this.mMobileDataUtils.registerContentObserver(this.mContext, this.mObserver);
            onNotify((Context) null, (Intent) null, true);
        }
    }

    public static class MultiBroadcastNotifier extends BroadcastNotifier {
        private String[] mIntents;

        public MultiBroadcastNotifier(Context context, String... strArr) {
            super(context);
            this.mIntents = strArr;
        }

        /* access modifiers changed from: protected */
        public IntentFilter createIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            for (String addAction : this.mIntents) {
                intentFilter.addAction(addAction);
            }
            return intentFilter;
        }
    }

    public interface OnNotifyListener {
        void onNotify(Context context, Intent intent, Object obj);
    }

    private NotifierManager(Context context) {
        this.mContext = context;
    }

    private static BaseNotifier createNotifier(String str, Context context) {
        if (DBG) {
            Log.i(LOG_TAG, "createNotifier:" + str);
        }
        return TYPE_MOBILE_DATA.equals(str) ? new MobileDataNotifier(context) : TYPE_WIFI_STATE.equals(str) ? new MultiBroadcastNotifier(context, "android.net.wifi.WIFI_STATE_CHANGED", "android.net.wifi.SCAN_RESULTS", "android.net.wifi.STATE_CHANGE") : TYPE_TIME_CHANGED.equals(str) ? new MultiBroadcastNotifier(context, "android.intent.action.TIMEZONE_CHANGED", "android.intent.action.TIME_SET") : new BroadcastNotifier(context, str);
    }

    public static synchronized NotifierManager getInstance(Context context) {
        NotifierManager notifierManager;
        synchronized (NotifierManager.class) {
            if (sInstance == null) {
                sInstance = new NotifierManager(context);
            }
            notifierManager = sInstance;
        }
        return notifierManager;
    }

    private BaseNotifier safeGet(String str) {
        BaseNotifier baseNotifier;
        synchronized (this.mNotifiers) {
            baseNotifier = this.mNotifiers.get(str);
        }
        return baseNotifier;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0046, code lost:
        r1.addListener(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0049, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void acquireNotifier(java.lang.String r4, com.miui.maml.NotifierManager.OnNotifyListener r5) {
        /*
            r3 = this;
            boolean r0 = DBG
            if (r0 == 0) goto L_0x0026
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "acquireNotifier:"
            r0.append(r1)
            r0.append(r4)
            java.lang.String r1 = "  "
            r0.append(r1)
            java.lang.String r1 = r5.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "NotifierManager"
            android.util.Log.i(r1, r0)
        L_0x0026:
            java.util.HashMap<java.lang.String, com.miui.maml.NotifierManager$BaseNotifier> r0 = r3.mNotifiers
            monitor-enter(r0)
            java.util.HashMap<java.lang.String, com.miui.maml.NotifierManager$BaseNotifier> r1 = r3.mNotifiers     // Catch:{ all -> 0x004a }
            java.lang.Object r1 = r1.get(r4)     // Catch:{ all -> 0x004a }
            com.miui.maml.NotifierManager$BaseNotifier r1 = (com.miui.maml.NotifierManager.BaseNotifier) r1     // Catch:{ all -> 0x004a }
            if (r1 != 0) goto L_0x0045
            android.content.Context r1 = r3.mContext     // Catch:{ all -> 0x004a }
            com.miui.maml.NotifierManager$BaseNotifier r1 = createNotifier(r4, r1)     // Catch:{ all -> 0x004a }
            if (r1 != 0) goto L_0x003d
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            return
        L_0x003d:
            r1.init()     // Catch:{ all -> 0x004a }
            java.util.HashMap<java.lang.String, com.miui.maml.NotifierManager$BaseNotifier> r2 = r3.mNotifiers     // Catch:{ all -> 0x004a }
            r2.put(r4, r1)     // Catch:{ all -> 0x004a }
        L_0x0045:
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            r1.addListener(r5)
            return
        L_0x004a:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.NotifierManager.acquireNotifier(java.lang.String, com.miui.maml.NotifierManager$OnNotifyListener):void");
    }

    public void pause(String str, OnNotifyListener onNotifyListener) {
        BaseNotifier safeGet = safeGet(str);
        if (safeGet != null && safeGet.pauseListener(onNotifyListener) == 0) {
            safeGet.pause();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0047, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void releaseNotifier(java.lang.String r3, com.miui.maml.NotifierManager.OnNotifyListener r4) {
        /*
            r2 = this;
            boolean r0 = DBG
            if (r0 == 0) goto L_0x0026
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "releaseNotifier:"
            r0.append(r1)
            r0.append(r3)
            java.lang.String r1 = "  "
            r0.append(r1)
            java.lang.String r1 = r4.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "NotifierManager"
            android.util.Log.i(r1, r0)
        L_0x0026:
            java.util.HashMap<java.lang.String, com.miui.maml.NotifierManager$BaseNotifier> r0 = r2.mNotifiers
            monitor-enter(r0)
            java.util.HashMap<java.lang.String, com.miui.maml.NotifierManager$BaseNotifier> r1 = r2.mNotifiers     // Catch:{ all -> 0x0048 }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x0048 }
            com.miui.maml.NotifierManager$BaseNotifier r1 = (com.miui.maml.NotifierManager.BaseNotifier) r1     // Catch:{ all -> 0x0048 }
            if (r1 != 0) goto L_0x0035
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            return
        L_0x0035:
            r1.removeListener(r4)     // Catch:{ all -> 0x0048 }
            int r4 = r1.getRef()     // Catch:{ all -> 0x0048 }
            if (r4 != 0) goto L_0x0046
            r1.finish()     // Catch:{ all -> 0x0048 }
            java.util.HashMap<java.lang.String, com.miui.maml.NotifierManager$BaseNotifier> r4 = r2.mNotifiers     // Catch:{ all -> 0x0048 }
            r4.remove(r3)     // Catch:{ all -> 0x0048 }
        L_0x0046:
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            return
        L_0x0048:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.NotifierManager.releaseNotifier(java.lang.String, com.miui.maml.NotifierManager$OnNotifyListener):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0014, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void resume(java.lang.String r2, com.miui.maml.NotifierManager.OnNotifyListener r3) {
        /*
            r1 = this;
            monitor-enter(r1)
            com.miui.maml.NotifierManager$BaseNotifier r2 = r1.safeGet(r2)     // Catch:{ all -> 0x0015 }
            if (r2 != 0) goto L_0x0009
            monitor-exit(r1)
            return
        L_0x0009:
            int r3 = r2.resumeListener(r3)     // Catch:{ all -> 0x0015 }
            r0 = 1
            if (r3 != r0) goto L_0x0013
            r2.resume()     // Catch:{ all -> 0x0015 }
        L_0x0013:
            monitor-exit(r1)
            return
        L_0x0015:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.NotifierManager.resume(java.lang.String, com.miui.maml.NotifierManager$OnNotifyListener):void");
    }
}

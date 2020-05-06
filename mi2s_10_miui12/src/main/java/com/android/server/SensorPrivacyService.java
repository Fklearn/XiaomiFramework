package com.android.server;

import android.content.Context;
import android.hardware.ISensorPrivacyListener;
import android.hardware.ISensorPrivacyManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import org.xmlpull.v1.XmlSerializer;

public final class SensorPrivacyService extends SystemService {
    private static final String SENSOR_PRIVACY_XML_FILE = "sensor_privacy.xml";
    private static final String TAG = "SensorPrivacyService";
    private static final String XML_ATTRIBUTE_ENABLED = "enabled";
    private static final String XML_TAG_SENSOR_PRIVACY = "sensor-privacy";
    /* access modifiers changed from: private */
    public final SensorPrivacyServiceImpl mSensorPrivacyServiceImpl;

    public SensorPrivacyService(Context context) {
        super(context);
        this.mSensorPrivacyServiceImpl = new SensorPrivacyServiceImpl(context);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.SensorPrivacyService$SensorPrivacyServiceImpl, android.os.IBinder] */
    public void onStart() {
        publishBinderService("sensor_privacy", this.mSensorPrivacyServiceImpl);
    }

    class SensorPrivacyServiceImpl extends ISensorPrivacyManager.Stub {
        @GuardedBy({"mLock"})
        private final AtomicFile mAtomicFile;
        private final Context mContext;
        @GuardedBy({"mLock"})
        private boolean mEnabled;
        private final SensorPrivacyHandler mHandler;
        private final Object mLock = new Object();

        SensorPrivacyServiceImpl(Context context) {
            this.mContext = context;
            this.mHandler = new SensorPrivacyHandler(FgThread.get().getLooper(), this.mContext);
            this.mAtomicFile = new AtomicFile(new File(Environment.getDataSystemDirectory(), SensorPrivacyService.SENSOR_PRIVACY_XML_FILE));
            synchronized (this.mLock) {
                this.mEnabled = readPersistedSensorPrivacyEnabledLocked();
            }
        }

        public void setSensorPrivacy(boolean enable) {
            enforceSensorPrivacyPermission();
            synchronized (this.mLock) {
                this.mEnabled = enable;
                FileOutputStream outputStream = null;
                try {
                    XmlSerializer serializer = new FastXmlSerializer();
                    outputStream = this.mAtomicFile.startWrite();
                    serializer.setOutput(outputStream, StandardCharsets.UTF_8.name());
                    serializer.startDocument((String) null, true);
                    serializer.startTag((String) null, SensorPrivacyService.XML_TAG_SENSOR_PRIVACY);
                    serializer.attribute((String) null, SensorPrivacyService.XML_ATTRIBUTE_ENABLED, String.valueOf(enable));
                    serializer.endTag((String) null, SensorPrivacyService.XML_TAG_SENSOR_PRIVACY);
                    serializer.endDocument();
                    this.mAtomicFile.finishWrite(outputStream);
                } catch (IOException e) {
                    Log.e(SensorPrivacyService.TAG, "Caught an exception persisting the sensor privacy state: ", e);
                    this.mAtomicFile.failWrite(outputStream);
                }
            }
            this.mHandler.onSensorPrivacyChanged(enable);
        }

        private void enforceSensorPrivacyPermission() {
            if (this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_SENSOR_PRIVACY") != 0) {
                throw new SecurityException("Changing sensor privacy requires the following permission: android.permission.MANAGE_SENSOR_PRIVACY");
            }
        }

        public boolean isSensorPrivacyEnabled() {
            boolean z;
            synchronized (this.mLock) {
                z = this.mEnabled;
            }
            return z;
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0042, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0043, code lost:
            if (r0 != null) goto L_0x0045;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
            r0.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x004d, code lost:
            throw r2;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean readPersistedSensorPrivacyEnabledLocked() {
            /*
                r5 = this;
                android.util.AtomicFile r0 = r5.mAtomicFile
                boolean r0 = r0.exists()
                if (r0 != 0) goto L_0x000a
                r0 = 0
                return r0
            L_0x000a:
                android.util.AtomicFile r0 = r5.mAtomicFile     // Catch:{ IOException | XmlPullParserException -> 0x004e }
                java.io.FileInputStream r0 = r0.openRead()     // Catch:{ IOException | XmlPullParserException -> 0x004e }
                org.xmlpull.v1.XmlPullParser r1 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x0040 }
                java.nio.charset.Charset r2 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ all -> 0x0040 }
                java.lang.String r2 = r2.name()     // Catch:{ all -> 0x0040 }
                r1.setInput(r0, r2)     // Catch:{ all -> 0x0040 }
                java.lang.String r2 = "sensor-privacy"
                com.android.internal.util.XmlUtils.beginDocument(r1, r2)     // Catch:{ all -> 0x0040 }
                r1.next()     // Catch:{ all -> 0x0040 }
                java.lang.String r2 = r1.getName()     // Catch:{ all -> 0x0040 }
                r3 = 0
                java.lang.String r4 = "enabled"
                java.lang.String r3 = r1.getAttributeValue(r3, r4)     // Catch:{ all -> 0x0040 }
                java.lang.Boolean r3 = java.lang.Boolean.valueOf(r3)     // Catch:{ all -> 0x0040 }
                boolean r3 = r3.booleanValue()     // Catch:{ all -> 0x0040 }
                r1 = r3
                if (r0 == 0) goto L_0x003f
                r0.close()     // Catch:{ IOException | XmlPullParserException -> 0x004e }
            L_0x003f:
                goto L_0x005c
            L_0x0040:
                r1 = move-exception
                throw r1     // Catch:{ all -> 0x0042 }
            L_0x0042:
                r2 = move-exception
                if (r0 == 0) goto L_0x004d
                r0.close()     // Catch:{ all -> 0x0049 }
                goto L_0x004d
            L_0x0049:
                r3 = move-exception
                r1.addSuppressed(r3)     // Catch:{ IOException | XmlPullParserException -> 0x004e }
            L_0x004d:
                throw r2     // Catch:{ IOException | XmlPullParserException -> 0x004e }
            L_0x004e:
                r0 = move-exception
                java.lang.String r1 = "SensorPrivacyService"
                java.lang.String r2 = "Caught an exception reading the state from storage: "
                android.util.Log.e(r1, r2, r0)
                android.util.AtomicFile r1 = r5.mAtomicFile
                r1.delete()
                r1 = 0
            L_0x005c:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.SensorPrivacyService.SensorPrivacyServiceImpl.readPersistedSensorPrivacyEnabledLocked():boolean");
        }

        /* access modifiers changed from: private */
        public void persistSensorPrivacyState() {
            synchronized (this.mLock) {
                FileOutputStream outputStream = null;
                try {
                    XmlSerializer serializer = new FastXmlSerializer();
                    outputStream = this.mAtomicFile.startWrite();
                    serializer.setOutput(outputStream, StandardCharsets.UTF_8.name());
                    serializer.startDocument((String) null, true);
                    serializer.startTag((String) null, SensorPrivacyService.XML_TAG_SENSOR_PRIVACY);
                    serializer.attribute((String) null, SensorPrivacyService.XML_ATTRIBUTE_ENABLED, String.valueOf(this.mEnabled));
                    serializer.endTag((String) null, SensorPrivacyService.XML_TAG_SENSOR_PRIVACY);
                    serializer.endDocument();
                    this.mAtomicFile.finishWrite(outputStream);
                } catch (IOException e) {
                    Log.e(SensorPrivacyService.TAG, "Caught an exception persisting the sensor privacy state: ", e);
                    this.mAtomicFile.failWrite(outputStream);
                }
            }
        }

        public void addSensorPrivacyListener(ISensorPrivacyListener listener) {
            if (listener != null) {
                this.mHandler.addListener(listener);
                return;
            }
            throw new NullPointerException("listener cannot be null");
        }

        public void removeSensorPrivacyListener(ISensorPrivacyListener listener) {
            if (listener != null) {
                this.mHandler.removeListener(listener);
                return;
            }
            throw new NullPointerException("listener cannot be null");
        }
    }

    private final class SensorPrivacyHandler extends Handler {
        private static final int MESSAGE_SENSOR_PRIVACY_CHANGED = 1;
        private final Context mContext;
        private final ArrayMap<ISensorPrivacyListener, DeathRecipient> mDeathRecipients = new ArrayMap<>();
        private final Object mListenerLock = new Object();
        @GuardedBy({"mListenerLock"})
        private final RemoteCallbackList<ISensorPrivacyListener> mListeners = new RemoteCallbackList<>();

        SensorPrivacyHandler(Looper looper, Context context) {
            super(looper);
            this.mContext = context;
        }

        public void onSensorPrivacyChanged(boolean enabled) {
            sendMessage(PooledLambda.obtainMessage($$Lambda$2rlj96lJ7chZcASbtixW5GQdw.INSTANCE, this, Boolean.valueOf(enabled)));
            sendMessage(PooledLambda.obtainMessage($$Lambda$SensorPrivacyService$SensorPrivacyHandler$ctW6BcqPnLm_33mG1WatsFwFT7w.INSTANCE, SensorPrivacyService.this.mSensorPrivacyServiceImpl));
        }

        public void addListener(ISensorPrivacyListener listener) {
            synchronized (this.mListenerLock) {
                this.mDeathRecipients.put(listener, new DeathRecipient(listener));
                this.mListeners.register(listener);
            }
        }

        public void removeListener(ISensorPrivacyListener listener) {
            synchronized (this.mListenerLock) {
                DeathRecipient deathRecipient = this.mDeathRecipients.remove(listener);
                if (deathRecipient != null) {
                    deathRecipient.destroy();
                }
                this.mListeners.unregister(listener);
            }
        }

        public void handleSensorPrivacyChanged(boolean enabled) {
            int count = this.mListeners.beginBroadcast();
            for (int i = 0; i < count; i++) {
                ISensorPrivacyListener listener = this.mListeners.getBroadcastItem(i);
                try {
                    listener.onSensorPrivacyChanged(enabled);
                } catch (RemoteException e) {
                    Log.e(SensorPrivacyService.TAG, "Caught an exception notifying listener " + listener + ": ", e);
                }
            }
            this.mListeners.finishBroadcast();
        }
    }

    private final class DeathRecipient implements IBinder.DeathRecipient {
        private ISensorPrivacyListener mListener;

        DeathRecipient(ISensorPrivacyListener listener) {
            this.mListener = listener;
            try {
                this.mListener.asBinder().linkToDeath(this, 0);
            } catch (RemoteException e) {
            }
        }

        public void binderDied() {
            SensorPrivacyService.this.mSensorPrivacyServiceImpl.removeSensorPrivacyListener(this.mListener);
        }

        public void destroy() {
            try {
                this.mListener.asBinder().unlinkToDeath(this, 0);
            } catch (NoSuchElementException e) {
            }
        }
    }
}

package com.android.server.connectivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.StringNetworkSpecifier;
import android.net.wifi.WifiInfo;
import android.os.UserHandle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.Toast;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.notification.SystemNotificationChannels;

public class NetworkNotificationManager {
    private static final boolean DBG = true;
    private static final String TAG = NetworkNotificationManager.class.getSimpleName();
    private static final boolean VDBG = false;
    private final Context mContext;
    private final NotificationManager mNotificationManager;
    private final SparseIntArray mNotificationTypeMap = new SparseIntArray();
    private final TelephonyManager mTelephonyManager;

    public enum NotificationType {
        LOST_INTERNET(742),
        NETWORK_SWITCH(743),
        NO_INTERNET(741),
        LOGGED_IN(744),
        PARTIAL_CONNECTIVITY(745),
        SIGN_IN(740);
        
        public final int eventId;

        private NotificationType(int eventId2) {
            this.eventId = eventId2;
            Holder.sIdToTypeMap.put(eventId2, this);
        }

        private static class Holder {
            /* access modifiers changed from: private */
            public static SparseArray<NotificationType> sIdToTypeMap;

            private Holder() {
            }

            static {
                sIdToTypeMap = new SparseArray<>();
            }
        }

        public static NotificationType getFromId(int id) {
            return (NotificationType) Holder.sIdToTypeMap.get(id);
        }
    }

    public NetworkNotificationManager(Context c, TelephonyManager t, NotificationManager n) {
        this.mContext = c;
        this.mTelephonyManager = t;
        this.mNotificationManager = n;
    }

    private static int getFirstTransportType(NetworkAgentInfo nai) {
        for (int i = 0; i < 64; i++) {
            if (nai.networkCapabilities.hasTransport(i)) {
                return i;
            }
        }
        return -1;
    }

    private static String getTransportName(int transportType) {
        Resources r = Resources.getSystem();
        try {
            return r.getStringArray(17236104)[transportType];
        } catch (IndexOutOfBoundsException e) {
            return r.getString(17040530);
        }
    }

    private static int getIcon(int transportType, NotificationType notifyType) {
        if (transportType != 1) {
            return 17303565;
        }
        if (notifyType == NotificationType.LOGGED_IN) {
            return 17302879;
        }
        return 17303569;
    }

    public void showNotification(int id, NotificationType notifyType, NetworkAgentInfo nai, NetworkAgentInfo switchToNai, PendingIntent intent, boolean highPriority) {
        String name;
        int transportType;
        boolean z;
        CharSequence title;
        CharSequence details;
        String str;
        CharSequence title2;
        int i = id;
        NotificationType notificationType = notifyType;
        NetworkAgentInfo networkAgentInfo = nai;
        String tag = tagFor(id);
        int eventId = notificationType.eventId;
        if (networkAgentInfo != null) {
            int transportType2 = getFirstTransportType(nai);
            String extraInfo = networkAgentInfo.networkInfo.getExtraInfo();
            name = TextUtils.isEmpty(extraInfo) ? networkAgentInfo.networkCapabilities.getSSID() : extraInfo;
            if (networkAgentInfo.networkCapabilities.hasCapability(12)) {
                transportType = transportType2;
            } else {
                return;
            }
        } else {
            name = null;
            transportType = 0;
        }
        int previousEventId = this.mNotificationTypeMap.get(i);
        NotificationType previousNotifyType = NotificationType.getFromId(previousEventId);
        if (priority(previousNotifyType) > priority(notifyType)) {
            Slog.d(TAG, String.format("ignoring notification %s for network %s with existing notification %s", new Object[]{notificationType, Integer.valueOf(id), previousNotifyType}));
            return;
        }
        clearNotification(id);
        Slog.d(TAG, String.format("showNotification tag=%s event=%s transport=%s name=%s highPriority=%s", new Object[]{tag, nameOf(eventId), getTransportName(transportType), name, Boolean.valueOf(highPriority)}));
        Resources r = Resources.getSystem();
        int icon = getIcon(transportType, notificationType);
        if (notificationType == NotificationType.NO_INTERNET && transportType == 1) {
            CharSequence title3 = r.getString(17041403, new Object[]{WifiInfo.removeDoubleQuotes(networkAgentInfo.networkCapabilities.getSSID())});
            int i2 = previousEventId;
            details = r.getString(17041404);
            z = false;
            title = title3;
        } else if (notificationType == NotificationType.PARTIAL_CONNECTIVITY && transportType == 1) {
            CharSequence title4 = r.getString(17040525, new Object[]{WifiInfo.removeDoubleQuotes(networkAgentInfo.networkCapabilities.getSSID())});
            int i3 = previousEventId;
            details = r.getString(17040526);
            z = false;
            title = title4;
        } else if (notificationType == NotificationType.LOST_INTERNET && transportType == 1) {
            CharSequence title5 = r.getString(17041403, new Object[]{WifiInfo.removeDoubleQuotes(networkAgentInfo.networkCapabilities.getSSID())});
            int i4 = previousEventId;
            details = r.getString(17041404);
            z = false;
            title = title5;
        } else if (notificationType != NotificationType.SIGN_IN) {
            if (notificationType == NotificationType.LOGGED_IN) {
                CharSequence title6 = WifiInfo.removeDoubleQuotes(networkAgentInfo.networkCapabilities.getSSID());
                details = r.getString(17039661);
                z = false;
                title = title6;
            } else if (notificationType == NotificationType.NETWORK_SWITCH) {
                String fromTransport = getTransportName(transportType);
                String toTransport = getTransportName(getFirstTransportType(switchToNai));
                z = false;
                title = r.getString(17040527, new Object[]{toTransport});
                details = r.getString(17040528, new Object[]{toTransport, fromTransport});
            } else {
                Resources resources = r;
                PendingIntent pendingIntent = intent;
                if (notificationType != NotificationType.NO_INTERNET && notificationType != NotificationType.PARTIAL_CONNECTIVITY) {
                    Slog.wtf(TAG, "Unknown notification type " + notificationType + " on network transport " + getTransportName(transportType));
                    return;
                }
                return;
            }
        } else if (transportType == 0) {
            CharSequence title7 = r.getString(17040521, new Object[]{0});
            StringNetworkSpecifier networkSpecifier = networkAgentInfo.networkCapabilities.getNetworkSpecifier();
            int subId = Integer.MAX_VALUE;
            if (networkSpecifier instanceof StringNetworkSpecifier) {
                try {
                    subId = Integer.parseInt(networkSpecifier.specifier);
                    int i5 = previousEventId;
                    title2 = title7;
                } catch (NumberFormatException e) {
                    NumberFormatException numberFormatException = e;
                    String str2 = TAG;
                    int i6 = previousEventId;
                    StringBuilder sb = new StringBuilder();
                    title2 = title7;
                    sb.append("NumberFormatException on ");
                    sb.append(networkSpecifier.specifier);
                    Slog.e(str2, sb.toString());
                }
            } else {
                title2 = title7;
            }
            details = this.mTelephonyManager.createForSubscriptionId(subId).getNetworkOperatorName();
            title = title2;
            z = false;
        } else if (transportType != 1) {
            CharSequence title8 = r.getString(17040521, new Object[]{0});
            int i7 = previousEventId;
            details = r.getString(17040522, new Object[]{name});
            z = false;
            title = title8;
        } else {
            CharSequence title9 = r.getString(17041393, new Object[]{0});
            int i8 = previousEventId;
            details = r.getString(17040522, new Object[]{WifiInfo.removeDoubleQuotes(networkAgentInfo.networkCapabilities.getSSID())});
            z = false;
            title = title9;
        }
        boolean hasPreviousNotification = previousNotifyType != null ? true : z;
        if (!highPriority || hasPreviousNotification) {
            str = SystemNotificationChannels.NETWORK_STATUS;
        } else {
            str = SystemNotificationChannels.NETWORK_ALERTS;
        }
        String channelId = str;
        NotificationType previousNotifyType2 = previousNotifyType;
        Resources resources2 = r;
        Notification.Builder when = new Notification.Builder(this.mContext, channelId).setWhen(System.currentTimeMillis());
        boolean z2 = notificationType == NotificationType.NETWORK_SWITCH ? true : z;
        NotificationType notificationType2 = previousNotifyType2;
        Notification.Builder builder = when.setShowWhen(z2).setSmallIcon(icon).setAutoCancel(true).setTicker(title).setColor(this.mContext.getColor(17170460)).setContentTitle(title).setContentIntent(intent).setLocalOnly(true).setOnlyAlertOnce(true);
        if (notificationType == NotificationType.NETWORK_SWITCH) {
            builder.setStyle(new Notification.BigTextStyle().bigText(details));
        } else {
            builder.setContentText(details);
        }
        if (notificationType == NotificationType.SIGN_IN) {
            builder.extend(new Notification.TvExtender().setChannelId(channelId));
        }
        Notification notification = builder.build();
        this.mNotificationTypeMap.put(i, eventId);
        if (transportType == 1) {
            try {
                if (notificationType == NotificationType.SIGN_IN) {
                    NetworkNotificationManagerInjector.showLogin(this.mContext, intent.getIntent(), name);
                    return;
                }
            } catch (NullPointerException npe) {
                Slog.d(TAG, "setNotificationVisible: visible notificationManager error", npe);
                return;
            }
        }
        this.mNotificationManager.notifyAsUser(tag, eventId, notification, UserHandle.ALL);
    }

    public void clearNotification(int id, NotificationType notifyType) {
        if (notifyType == NotificationType.getFromId(this.mNotificationTypeMap.get(id))) {
            clearNotification(id);
        }
    }

    public void clearNotification(int id) {
        if (this.mNotificationTypeMap.indexOfKey(id) >= 0) {
            String tag = tagFor(id);
            int eventId = this.mNotificationTypeMap.get(id);
            Slog.d(TAG, String.format("clearing notification tag=%s event=%s", new Object[]{tag, nameOf(eventId)}));
            try {
                this.mNotificationManager.cancelAsUser(tag, eventId, UserHandle.ALL);
            } catch (NullPointerException npe) {
                Slog.d(TAG, String.format("failed to clear notification tag=%s event=%s", new Object[]{tag, nameOf(eventId)}), npe);
            }
            this.mNotificationTypeMap.delete(id);
        }
    }

    public void setProvNotificationVisible(boolean visible, int id, String action) {
        if (visible) {
            int i = id;
            showNotification(i, NotificationType.SIGN_IN, (NetworkAgentInfo) null, (NetworkAgentInfo) null, PendingIntent.getBroadcast(this.mContext, 0, new Intent(action), 0), false);
            return;
        }
        clearNotification(id);
    }

    public void showToast(NetworkAgentInfo fromNai, NetworkAgentInfo toNai) {
        String fromTransport = getTransportName(getFirstTransportType(fromNai));
        String toTransport = getTransportName(getFirstTransportType(toNai));
        Toast.makeText(this.mContext, this.mContext.getResources().getString(17040529, new Object[]{fromTransport, toTransport}), 1).show();
    }

    @VisibleForTesting
    static String tagFor(int id) {
        return String.format("ConnectivityNotification:%d", new Object[]{Integer.valueOf(id)});
    }

    @VisibleForTesting
    static String nameOf(int eventId) {
        NotificationType t = NotificationType.getFromId(eventId);
        return t != null ? t.name() : "UNKNOWN";
    }

    private static int priority(NotificationType t) {
        if (t == null) {
            return 0;
        }
        switch (t) {
            case SIGN_IN:
                return 5;
            case PARTIAL_CONNECTIVITY:
                return 4;
            case NO_INTERNET:
                return 3;
            case NETWORK_SWITCH:
                return 2;
            case LOST_INTERNET:
            case LOGGED_IN:
                return 1;
            default:
                return 0;
        }
    }
}

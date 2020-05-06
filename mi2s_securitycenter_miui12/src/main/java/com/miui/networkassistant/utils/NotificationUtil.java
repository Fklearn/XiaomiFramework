package com.miui.networkassistant.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.ui.NetworkAssistantActivity;
import com.miui.networkassistant.ui.activity.FirewallActivity;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity;
import com.miui.networkassistant.ui.activity.NetworkOverLimitActivity;
import com.miui.networkassistant.ui.activity.NetworkStatsExceptionAlertActivity;
import com.miui.networkassistant.ui.activity.TrafficConfigAlertActivity;
import com.miui.networkassistant.ui.fragment.InternationalRoamingSettingFragment;
import com.miui.networkassistant.ui.fragment.LockScreenTrafficFragment;
import com.miui.networkassistant.ui.fragment.OperatorSettingFragment;
import com.miui.networkassistant.ui.fragment.RoamingWhiteListFragment;
import com.miui.networkassistant.ui.fragment.TcSmsReportFragment;
import com.miui.networkassistant.ui.fragment.TrafficLimitSettingFragment;
import com.miui.securitycenter.R;
import java.util.HashMap;

public class NotificationUtil {
    public static final String CANCEL_FLOAT_NOTIFICATION = "cancel_float_notification";
    private static final String NOISE_NOTIFY_CHANNEL_ID = "networkassistant_noise_notify_channel";
    private static final String NOTIFY_CHANNEL_ID = "networkassistant_notify_channel";
    private static final int NOTIFY_ID_CORRECTION_SUCCEED = 33;
    private static final int NOTIFY_ID_DAILY_CARD_OVER_LIMIT = 67;
    private static final int NOTIFY_ID_DAILY_LIMIT_WARNING = 13;
    private static final int NOTIFY_ID_DATA_USAGE_CORRECTION_TIMEOUT = 1;
    private static final int NOTIFY_ID_DATA_USAGE_OVER_LIMIT = 3;
    private static final int NOTIFY_ID_LEISURE_DATA_USAGE_WARNING = 4;
    private static final int NOTIFY_ID_LOCK_SCREEN_TRAFFIC_GUIDE = 0;
    private static final int NOTIFY_ID_LOCK_SCREEN_TRAFFIC_WARNING = 16;
    private static final int NOTIFY_ID_LOW_PRIORITY = 0;
    private static final int NOTIFY_ID_NETWORK_BLOCKED = 22;
    private static final int NOTIFY_ID_NETWORK_CHANGED = 80;
    public static final int NOTIFY_ID_NETWORK_RESTRICT = 32;
    private static final int NOTIFY_ID_NETWORK_STATS_EXCEPTION = 69;
    private static final int NOTIFY_ID_NORMAL_DATA_USAGE_WARNING = 2;
    private static final int NOTIFY_ID_NOT_LIMITED_DATA_USAGE_OVER_LIMIT = 81;
    public static final int NOTIFY_ID_PACKAGE_CHANGE = 48;
    private static final int NOTIFY_ID_PACKAGE_SETTING = 0;
    private static final int NOTIFY_ID_ROAMING_DAILY_LIMIT_WARNING = 34;
    private static final int NOTIFY_ID_ROAMING_STATE = 11;
    private static final int NOTIFY_ID_ROAMING_WHITE_LIST_SETTED = 12;
    private static final int NOTIFY_ID_SIM_LOCATION_ERROR = 0;
    private static final int NOTIFY_ID_TC_SMS_RECEIVED = 9;
    private static final int NOTIFY_ID_TC_SMS_TIMEOUT_OR_FAILURE_NOTIFY = 10;
    private static final int NOTIFY_ID_TETHER_LIMT = 70;
    private static final int NOTIFY_ID_TOTAL_PACKAGE_NOT_SETTED = 5;
    private static final int NOTIFY_ID_TRAFFIC_SETTING_DAILY_LIMIT = 0;
    private static final String SECURITYCENTER_NOTIFY_CHANNEL_ID = "com.miui.securitycenter";

    static abstract class IExtraBuilder {
        private String mChannel = NotificationUtil.NOTIFY_CHANNEL_ID;
        /* access modifiers changed from: private */
        public int mIconRes = R.drawable.ic_launcher_network_assistant;

        IExtraBuilder() {
        }

        /* access modifiers changed from: package-private */
        public String getChannel() {
            return this.mChannel;
        }

        public int getIconRes() {
            return this.mIconRes;
        }

        /* access modifiers changed from: package-private */
        public void onBuild(Notification.Builder builder) {
        }

        /* access modifiers changed from: package-private */
        public void onCreateIntent(Intent intent) {
        }

        /* access modifiers changed from: package-private */
        public void setChannel(String str) {
            this.mChannel = str;
        }

        public void setIconRes(int i) {
            this.mIconRes = i;
        }
    }

    private class PendingIntentType {
        static final int ACTIVITY = 2;
        static final int BROADCAST = 1;

        private PendingIntentType() {
        }
    }

    public static void cancelAllLowPriorityNotify(Context context) {
        cancelNotification(context, 0);
    }

    public static void cancelDailyLimitWarning(Context context) {
        cancelNotification(context, 13);
    }

    public static void cancelDataUsageCorrectionTimeOutOrFailureNotify(Context context) {
        cancelNotification(context, 1);
    }

    public static void cancelDataUsageOverLimit(Context context) {
        cancelNotification(context, 3);
    }

    public static void cancelFirewallRestrictionNotification(Context context) {
        cancelNotification(context, 32);
    }

    public static void cancelNetworkBlockedNotify(Context context) {
        cancelNotification(context, 22);
    }

    public static void cancelNetworkChangedNotify(Context context) {
        cancelNotification(context, 80);
    }

    public static void cancelNormalDataUsageWarning(Context context) {
        cancelNotification(context, 2);
    }

    public static void cancelNormalTotalPackageNotSetted(Context context) {
        cancelNotification(context, 5);
    }

    public static void cancelNotification(Context context, int i) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        notificationManager.cancel(i);
        g.a(notificationManager, i);
    }

    public static void cancelOpenDataRoamingNotify(Context context) {
        cancelNotification(context, 11);
    }

    public static void cancelOpenRoamingWhiteListNotify(Context context) {
        cancelNotification(context, 12);
    }

    public static void cancelRoamingDailyLimitWarning(Context context) {
        cancelNotification(context, 34);
    }

    public static void cancelSimLocationErrorNotify(Context context) {
        cancelNotification(context, 0);
    }

    public static void cancelTcSmsReceivedNotify(Context context) {
        cancelNotification(context, 9);
    }

    public static void cancelTcSmsTimeOutOrFailureNotify(Context context) {
        cancelNotification(context, 10);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x002b A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void createChannel(android.content.Context r4, java.lang.String r5) {
        /*
            int r0 = r5.hashCode()
            r1 = -1417479130(0xffffffffab82fc26, float:-9.307041E-13)
            r2 = 1
            if (r0 == r1) goto L_0x001a
            r1 = 1520416188(0x5a9fb5bc, float:2.24771702E16)
            if (r0 == r1) goto L_0x0010
            goto L_0x0024
        L_0x0010:
            java.lang.String r0 = "networkassistant_notify_channel"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0024
            r0 = 0
            goto L_0x0025
        L_0x001a:
            java.lang.String r0 = "com.miui.securitycenter"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0024
            r0 = r2
            goto L_0x0025
        L_0x0024:
            r0 = -1
        L_0x0025:
            r1 = 2131755432(0x7f1001a8, float:1.9141743E38)
            r3 = 2
            if (r0 == 0) goto L_0x002d
            if (r0 == r2) goto L_0x0032
        L_0x002d:
            java.lang.String r0 = r4.getString(r1)
            goto L_0x003a
        L_0x0032:
            r3 = 4
            r0 = 2131756996(0x7f1007c4, float:1.9144915E38)
            java.lang.String r0 = r4.getString(r0)
        L_0x003a:
            b.b.c.j.v.a((android.content.Context) r4, (java.lang.String) r5, (java.lang.String) r0, (int) r3)
            java.lang.String r5 = "notification"
            java.lang.Object r4 = r4.getSystemService(r5)
            android.app.NotificationManager r4 = (android.app.NotificationManager) r4
            java.lang.String r5 = "networkassistant_noise_notify_channel"
            b.b.c.j.v.a((android.app.NotificationManager) r4, (java.lang.String) r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.utils.NotificationUtil.createChannel(android.content.Context, java.lang.String):void");
    }

    private static String getChannelByExtraBuilder(IExtraBuilder iExtraBuilder) {
        return iExtraBuilder != null ? iExtraBuilder.getChannel() : NOTIFY_CHANNEL_ID;
    }

    private static String getDualCardTitle(Context context, int i, String str) {
        return String.format("%s%s", new Object[]{DeviceUtil.IS_DUAL_CARD ? context.getString(getSimResIdBySlotNum(i)) : "", str});
    }

    private static CharSequence getNotificationPrefix(Context context, CharSequence charSequence, int i, boolean z) {
        if (!DeviceUtil.IS_DUAL_CARD || !z) {
            return charSequence;
        }
        return String.format("%s-%s", new Object[]{charSequence, context.getText(getSimResIdBySlotNum(i))});
    }

    private static CharSequence getNotificationPrefix(Context context, CharSequence charSequence, boolean z) {
        return getNotificationPrefix(context, charSequence, Sim.getCurrentActiveSlotNum(), z);
    }

    private static PendingIntent getPendingIntent(Context context, int i, Intent intent, int i2) {
        return i2 != 1 ? g.a(context, i, intent) : g.b(context, i, intent);
    }

    private static int getSimResIdBySlotNum(int i) {
        return i == 0 ? R.string.dual_setting_simcard1 : R.string.dual_setting_simcard2;
    }

    public static void sendBillWarningNotify(Context context, String str, int i, Intent intent) {
        CharSequence text = context.getText(R.string.bill_limit_notification_title);
        String format = String.format(context.getText(R.string.bill_limit_notification_text_format).toString(), new Object[]{Integer.valueOf(i + 1), str});
        AnonymousClass18 r9 = new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setPriority(2);
            }
        };
        int unused = r9.mIconRes = -1;
        showNotification(context, 34, intent, text, (CharSequence) format, -1, true, false, (IExtraBuilder) r9, false, 2);
    }

    public static void sendCorrectionAlertNotify(Context context, String str, String str2, int i) {
        Intent intent = new Intent(context, NetworkAssistantActivity.class);
        intent.putExtra(Sim.SIM_SLOT_NUM_TAG, i);
        showFloatNotification(context, 33, intent, str, str2, (IExtraBuilder) null, false, false, 2);
    }

    public static void sendDailyCardDataUsageOverLimit(Context context, int i) {
        Context context2 = context;
        showFloatNotification(context2, 67, new Intent(context, NetworkAssistantActivity.class), context.getResources().getQuantityString(R.plurals.daily_sim_over_limit_notify_title, i, new Object[]{Integer.valueOf(i)}), context.getString(R.string.data_usage_warning_body_notify), new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setWhen(0);
                builder.setPriority(2);
            }

            public void onCreateIntent(Intent intent) {
                intent.putExtra(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
            }
        }, true, false, 2);
    }

    public static void sendDailyLimitWarning(Context context) {
        Context context2 = context;
        showFloatNotification(context2, 13, new Intent(context, NetworkAssistantActivity.class), context.getText(R.string.reach_daily_limit_value_title), context.getText(R.string.reach_daily_limit_value_body), new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setWhen(0);
                builder.setPriority(2);
            }

            public void onCreateIntent(Intent intent) {
                intent.putExtra(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
            }
        }, true, false, 2);
    }

    public static void sendDataUsageCorrectionTimeOutOrFailureNotify(final Context context, CharSequence charSequence, String str, final int i) {
        showNotification(context, 1, new Intent(context, NetworkAssistantActivity.class), getNotificationPrefix(context, charSequence, i, true), (CharSequence) str, (int) R.drawable.network_assistant_small, true, false, (IExtraBuilder) !B.g() ? new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setWhen(0);
                Intent intent = b.b.c.c.b.g.getIntent(context, TcSmsReportFragment.class, (Bundle) null);
                intent.putExtra(Sim.SIM_SLOT_NUM_TAG, i);
                Bundle bundle = new Bundle();
                bundle.putString("view_from", NotificationUtil.class.getSimpleName());
                intent.putExtra(b.b.c.c.b.g.FRAGMENT_ARGS, bundle);
                String string = context.getString(R.string.tc_sms_report_title);
                if (DeviceUtil.IS_INTERNATIONAL_BUILD) {
                    setIconRes(-1);
                }
                NotificationUtil.showRightButton(context, builder, intent, string, 2);
            }
        } : null, false, 2);
    }

    public static void sendDataUsageOverLimit(Context context, int i, int i2) {
        CharSequence charSequence;
        CharSequence text;
        int i3;
        CharSequence quantityString;
        CharSequence text2;
        CharSequence text3 = context.getText(R.string.data_usage_mobile_limit_title);
        int i4 = R.string.data_usage_limit_body;
        CharSequence text4 = context.getText(R.string.data_usage_limit_body);
        if (i == 0) {
            quantityString = context.getText(R.string.data_usage_mobile_limit_title);
        } else {
            i4 = R.string.daily_limit_network_available_body;
            if (i == 1) {
                quantityString = context.getText(R.string.reach_daily_limit_value_title);
            } else {
                if (i == 2) {
                    text = context.getText(R.string.roaming_limit_notification_title);
                    i3 = R.string.roaming_limit_network_notification_body;
                } else if (i == 3) {
                    text = context.getText(R.string.data_usage_mobile_limit_title);
                    i3 = R.string.leisure_usage_dialog_title;
                } else if (i == 4) {
                    quantityString = context.getResources().getQuantityString(R.plurals.daily_sim_over_limit_notify_title, i2, new Object[]{Integer.valueOf(i2)});
                } else if (i == 5) {
                    text = context.getText(R.string.data_usage_protected_notify_title);
                    i3 = R.string.data_usage_protected_notify_message;
                } else {
                    charSequence = text4;
                    Context context2 = context;
                    showNotification(context2, 3, (Class<? extends Activity>) NetworkOverLimitActivity.class, text3, charSequence, (int) R.drawable.notify_mobile_disabled, true, false, (IExtraBuilder) new IExtraBuilder() {
                        public void onBuild(Notification.Builder builder) {
                            builder.setWhen(0);
                            builder.setPriority(2);
                        }

                        public void onCreateIntent(Intent intent) {
                            intent.putExtra(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
                        }
                    }, SimCardHelper.getInstance(context).isDualSimInserted(), 2);
                }
                text2 = context.getText(i3);
                text3 = quantityString;
                charSequence = text2;
                Context context22 = context;
                showNotification(context22, 3, (Class<? extends Activity>) NetworkOverLimitActivity.class, text3, charSequence, (int) R.drawable.notify_mobile_disabled, true, false, (IExtraBuilder) new IExtraBuilder() {
                    public void onBuild(Notification.Builder builder) {
                        builder.setWhen(0);
                        builder.setPriority(2);
                    }

                    public void onCreateIntent(Intent intent) {
                        intent.putExtra(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
                    }
                }, SimCardHelper.getInstance(context).isDualSimInserted(), 2);
            }
        }
        text2 = context.getText(i4);
        text3 = quantityString;
        charSequence = text2;
        Context context222 = context;
        showNotification(context222, 3, (Class<? extends Activity>) NetworkOverLimitActivity.class, text3, charSequence, (int) R.drawable.notify_mobile_disabled, true, false, (IExtraBuilder) new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setWhen(0);
                builder.setPriority(2);
            }

            public void onCreateIntent(Intent intent) {
                intent.putExtra(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
            }
        }, SimCardHelper.getInstance(context).isDualSimInserted(), 2);
    }

    public static void sendLeisureDataUsageWarning(Context context) {
        Context context2 = context;
        showNotification(context2, 4, NetworkAssistantActivity.class, context.getText(R.string.data_usage_leisure_warning_title), context.getText(R.string.data_usage_leisure_warning_body), R.drawable.network_assistant_small, true, false, new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setWhen(0);
                builder.setPriority(2);
            }
        });
    }

    public static void sendLockScreenTrafficGuideNotify(Context context, long j) {
        String string = context.getString(R.string.lock_screen_traffic_guide_notification_title);
        String format = String.format(context.getString(R.string.lock_screen_traffic_guide_notification_summary), new Object[]{FormatBytesUtil.formatBytes(context, j)});
        Bundle bundle = new Bundle();
        bundle.putInt(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
        showNotification(context, 0, b.b.c.c.b.g.getIntent(context, TrafficLimitSettingFragment.class, bundle), (CharSequence) string, (CharSequence) format, (int) R.drawable.network_assistant_small, true, false, (IExtraBuilder) null, false, 2);
    }

    public static void sendLockScreenTrafficUsed(Context context, long j, long j2, long j3, HashMap<Integer, Long> hashMap) {
        Object[] objArr;
        String str;
        Context context2 = context;
        long j4 = j2;
        long j5 = j3;
        CharSequence text = context.getText(R.string.lock_screen_traffic_warn_title);
        if (DeviceUtil.isLargeScaleMode()) {
            str = context.getString(R.string.lock_screen_traffic_notify_body);
            objArr = new Object[]{DateUtil.formatDataTime(j5, 3), DateUtil.getFormatedTime(context, j4), FormatBytesUtil.formatBytes(context, j)};
        } else {
            str = context.getString(R.string.lock_screen_traffic_notify_body);
            objArr = new Object[]{DateUtil.formatDataTime(j5, 4), DateUtil.getFormatedTime(context, j4), FormatBytesUtil.formatBytes(context, j)};
        }
        String format = String.format(str, objArr);
        Bundle bundle = new Bundle();
        bundle.putSerializable(LockScreenTrafficFragment.BUNDLE_KEY_UID_MAP, hashMap);
        bundle.putString(LockScreenTrafficFragment.BUNDLE_KEY_LIST_HEADER, format.toString());
        showNotification(context, 16, b.b.c.c.b.g.getIntent(context, LockScreenTrafficFragment.class, bundle), text, (CharSequence) format, R.drawable.network_assistant_small, true, false, (IExtraBuilder) null, false, 2);
    }

    public static void sendNetworkRestrictNotify(final Context context, String str, String str2, final String str3, final int i) {
        showFloatNotification(context, 32, new Intent(context, FirewallActivity.class), str, str2, new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setWhen(0);
                builder.setPriority(2);
                Intent intent = new Intent(Constants.App.ACTION_BROADCAST_ALLOW_APP_FIREWALL);
                intent.putExtra("packageName", str3);
                intent.putExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, i);
                String string = context.getString(R.string.firewall_allow_network);
                if (DeviceUtil.IS_INTERNATIONAL_BUILD) {
                    setIconRes(-1);
                }
                NotificationUtil.showRightButton(context, builder, intent, string, 1);
            }
        }, false, true, 2);
    }

    public static void sendNetworkStatsExceptionNotify(Context context) {
        Context context2 = context;
        showFloatNotification(context2, 69, new Intent(context, NetworkStatsExceptionAlertActivity.class), context.getString(R.string.exception_titile), context.getString(R.string.exception_notify_message), new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setPriority(2);
            }
        }, false, false, 2);
    }

    public static void sendNormalDataUsageOverWarning(Context context) {
        Context context2 = context;
        showNotification(context2, 3, NetworkAssistantActivity.class, context.getText(R.string.data_usage_warning_title_notify), context.getText(R.string.data_usage_warning_body_notify), R.drawable.notify_mobile_disabled, true, false, new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setWhen(0);
                builder.setPriority(2);
            }

            public void onCreateIntent(Intent intent) {
                intent.putExtra(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
            }
        });
    }

    public static void sendNormalDataUsageWarning(Context context, boolean z) {
        String string = context.getString(R.string.traffic_warning_purchase_title);
        if (z) {
            AnonymousClass3 r9 = new IExtraBuilder() {
                public void onBuild(Notification.Builder builder) {
                    builder.setPriority(2);
                }

                public void onCreateIntent(Intent intent) {
                    Bundle bundle = new Bundle();
                    bundle.putString("bundle_key_purchase_from", "100004");
                    intent.putExtra("bundle_key_com", bundle);
                }
            };
            Context context2 = context;
            showNotification(context2, 2, new Intent(Constants.App.ACTION_NETWORK_ASSISTANT_TRAFFIC_PURCHASE), (CharSequence) string, (CharSequence) context.getString(R.string.traffic_purchase_body), (int) R.drawable.network_assistant_small, true, false, (IExtraBuilder) r9, true, 2);
            return;
        }
        Context context3 = context;
        showNotification(context3, 2, NetworkAssistantActivity.class, string, context.getString(R.string.data_usage_warning_body), R.drawable.network_assistant_small, true, false, new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setPriority(2);
            }
        });
    }

    public static void sendNormalTotalPackageNotSetted(Context context, int i) {
        CharSequence text = context.getText(R.string.data_usage_no_total_package_title);
        CharSequence text2 = context.getText(R.string.data_usage_no_total_package_body);
        Bundle bundle = new Bundle();
        if (DeviceUtil.IS_DUAL_CARD) {
            text2 = String.format(context.getString(R.string.data_usage_no_total_package_body_dual), new Object[]{context.getString(getSimResIdBySlotNum(i))});
            bundle.putInt(Sim.SIM_SLOT_NUM_TAG, i);
        }
        bundle.putBoolean(OperatorSettingFragment.BUNDLE_KEY_FROM_NOTIFICATION, true);
        AnonymousClass2 r9 = new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setDefaults(33);
                builder.setPriority(2);
            }
        };
        r9.setChannel(SECURITYCENTER_NOTIFY_CHANNEL_ID);
        Intent intent = b.b.c.c.b.g.getIntent(context, OperatorSettingFragment.class, bundle);
        intent.putExtra(Sim.SIM_SLOT_NUM_TAG, i);
        showNotification(context, 5, intent, text, text2, R.drawable.network_assistant_small, true, false, r9, false, 2, true);
    }

    public static void sendNotLimitedDataUsageOverWarning(Context context, long j) {
        Context context2 = context;
        showFloatNotification(context2, 81, new Intent(context, NetworkAssistantActivity.class), context.getString(R.string.traffic_usage_warning_title), String.format(context.getString(R.string.traffic_usage_warning_body), new Object[]{FormatBytesUtil.formatBytes(context, j)}), new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setWhen(0);
                builder.setPriority(2);
            }

            public void onCreateIntent(Intent intent) {
                intent.putExtra(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
            }
        }, true, false, 2);
    }

    public static void sendOpenDataRoamingNotify(Context context) {
        Context context2 = context;
        showFloatNotification(context2, 11, b.b.c.c.b.g.getIntent(context, InternationalRoamingSettingFragment.class, (Bundle) null), context.getText(R.string.roaming_not_start_title), context.getText(R.string.roaming_not_start_body), new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setPriority(2);
            }
        }, false, false, 2);
    }

    public static void sendOpenRoamingWhiteListNotify(Context context) {
        Context context2 = context;
        showNotification(context2, 12, b.b.c.c.b.g.getIntent(context, RoamingWhiteListFragment.class, (Bundle) null), context.getText(R.string.roaming_start_title), context.getText(R.string.roaming_start_body), (int) R.drawable.network_assistant_small, true, false, (IExtraBuilder) new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setPriority(2);
            }
        }, false, 2);
    }

    public static void sendOtherNetworkBlockedNotify(final Context context, boolean z) {
        String string = context.getString(R.string.data_network_blocked_notification_title);
        String string2 = context.getString(R.string.network_blocked_notification_summary);
        Intent intent = new Intent(context, NetworkDiagnosticsActivity.class);
        AnonymousClass16 r9 = new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setWhen(0);
                builder.setPriority(2);
                builder.setSmallIcon(R.drawable.network_diagnosis_small);
                setIconRes(DeviceUtil.IS_INTERNATIONAL_BUILD ? -1 : R.drawable.icon_data_network_blocked);
                NotificationUtil.showRightButton(context, builder, (Intent) null, (CharSequence) null, 2);
            }
        };
        if (z) {
            showFloatNotification(context, 22, intent, string, string2, r9, false, false, 2);
        } else {
            showNotification(context, 22, intent, (CharSequence) string, (CharSequence) string2, (int) R.drawable.network_diagnosis_small, true, false, (IExtraBuilder) r9, false, 2);
        }
    }

    public static void sendPackageChangeNotify(Context context, CharSequence charSequence, CharSequence charSequence2, String str, int i, String str2, TrafficUsedStatus trafficUsedStatus, boolean z) {
        CharSequence charSequence3 = charSequence;
        CharSequence notificationPrefix = getNotificationPrefix(context, charSequence, i, true);
        Intent intent = new Intent(context, TrafficConfigAlertActivity.class);
        intent.putExtra(TrafficConfigAlertActivity.BUNDLE_KEY_IS_STABLE_PKG, z);
        String str3 = str;
        intent.putExtra(TrafficConfigAlertActivity.BUNDLE_KEY_BODY, str);
        intent.putExtra(Sim.SIM_SLOT_NUM_TAG, i);
        String str4 = str2;
        intent.putExtra(TrafficConfigAlertActivity.BUNDLE_KEY_IMSI, str2);
        TrafficUsedStatus trafficUsedStatus2 = trafficUsedStatus;
        intent.putExtra(TrafficConfigAlertActivity.BUNDLE_KEY_TRAFFIC_USED_STATUS, trafficUsedStatus);
        showFloatNotification(context, i + 48, intent, notificationPrefix, charSequence2, (IExtraBuilder) null, false, false, 2);
    }

    public static void sendRoamingDailyLimitWarning(Context context) {
        Context context2 = context;
        showFloatNotification(context2, 34, new Intent(context, NetworkAssistantActivity.class), context.getText(R.string.roaming_limit_notification_title), context.getText(R.string.roaming_limit_warning_notification_body), new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setPriority(2);
            }
        }, true, false, 2);
    }

    public static void sendSettingDailyLimitNotify(Context context, long j) {
        String dualCardTitle = getDualCardTitle(context, Sim.getCurrentActiveSlotNum(), String.format(context.getString(R.string.notify_daily_limit_title), new Object[]{FormatBytesUtil.formatBytesByMB(context, j)}));
        String string = context.getString(R.string.notify_daily_limit_body);
        Bundle bundle = new Bundle();
        bundle.putInt(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
        showNotification(context, 0, b.b.c.c.b.g.getIntent(context, TrafficLimitSettingFragment.class, bundle), (CharSequence) dualCardTitle, (CharSequence) string, (int) R.drawable.network_assistant_small, true, false, (IExtraBuilder) null, false, 2);
    }

    public static void sendSimLocationErrorNotify(Context context, int i) {
        CharSequence notificationPrefix = getNotificationPrefix(context, context.getText(R.string.sim_location_error_notify_title), i, true);
        CharSequence text = context.getText(R.string.sim_location_error_notify_message);
        Bundle bundle = new Bundle();
        bundle.putBoolean(OperatorSettingFragment.UPDATE_OPERATOR_FROM_NOTIFICATION, true);
        bundle.putInt(Sim.SIM_SLOT_NUM_TAG, i);
        Context context2 = context;
        showNotification(context2, 0, b.b.c.c.b.g.getIntent(context, OperatorSettingFragment.class, bundle), notificationPrefix, text, (int) R.drawable.network_assistant_small, true, false, (IExtraBuilder) new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setPriority(2);
            }
        }, false, 2);
    }

    public static void sendTcSmsReceivedNotify(Context context, String str, String str2, int i) {
        Intent intent = b.b.c.c.b.g.getIntent(context, TcSmsReportFragment.class, (Bundle) null);
        intent.putExtra(Sim.SIM_SLOT_NUM_TAG, i);
        Bundle bundle = new Bundle();
        bundle.putString("view_from", NotificationUtil.class.getSimpleName());
        intent.putExtra(b.b.c.c.b.g.FRAGMENT_ARGS, bundle);
        showNotification(context, 9, intent, (CharSequence) str, (CharSequence) str2, (int) R.drawable.network_assistant_small, true, false, (IExtraBuilder) null, false, 2);
    }

    public static void sendTcSmsTimeOutOrFailureNotify(final Context context, String str, String str2, int i) {
        AnonymousClass11 r8 = new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                Intent intent = new Intent();
                intent.setAction(Constants.App.ACTION_BROADCAST_TC_SMS_REPORT_STATUS);
                builder.setDeleteIntent(g.b(context, 0, intent));
            }
        };
        Intent intent = b.b.c.c.b.g.getIntent(context, TcSmsReportFragment.class, (Bundle) null);
        intent.putExtra(Sim.SIM_SLOT_NUM_TAG, i);
        Bundle bundle = new Bundle();
        bundle.putString("view_from", NotificationUtil.class.getSimpleName());
        intent.putExtra(b.b.c.c.b.g.FRAGMENT_ARGS, bundle);
        showNotification(context, 10, intent, (CharSequence) str, (CharSequence) str2, (int) R.drawable.network_assistant_small, true, false, (IExtraBuilder) r8, false, 2);
    }

    public static void sendTetherOverLimitWaringNotify(Context context) {
        String string = context.getString(R.string.tether_over_limit_warning_notify_title);
        String string2 = context.getString(R.string.tether_over_limit_warning_notify_message);
        AnonymousClass21 r6 = new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setPriority(2);
            }
        };
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$TetherSettingsActivity");
        showFloatNotification(context, 70, intent, string, string2, r6, false, false, 2);
    }

    public static void sendTrafficSettingDailyNotify(Context context, long j) {
        showOperatorSetting(context, getDualCardTitle(context, Sim.getCurrentActiveSlotNum(), String.format(context.getString(R.string.notify_daily_limit_title), new Object[]{FormatBytesUtil.formatBytesByMB(context, j)})), context.getString(R.string.notify_daily_data_usage_body));
    }

    public static void sendTrafficSettingMonthlyNotify(Context context, long j) {
        showOperatorSetting(context, getDualCardTitle(context, Sim.getCurrentActiveSlotNum(), String.format(context.getString(R.string.notify_month_traffic_title), new Object[]{FormatBytesUtil.formatBytesByMB(context, j)})), context.getString(R.string.notify_month_traffic_body));
    }

    public static void sendWifiNetworkBlockedNotify(final Context context, boolean z) {
        if (DeviceUtil.isMiPushRestricted(context)) {
            Log.w("NA_ND", "MiPush restrict sendWifiNetworkBlockedNotify() ");
            return;
        }
        String string = context.getString(R.string.wifi_network_blocked_notification_title);
        String string2 = context.getString(R.string.network_blocked_notification_summary);
        final String string3 = context.getString(R.string.network_blocked_switch_wifi);
        Intent intent = new Intent(context, NetworkDiagnosticsActivity.class);
        AnonymousClass15 r9 = new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setWhen(0);
                builder.setPriority(2);
                Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                builder.setSmallIcon(R.drawable.network_diagnosis_small);
                setIconRes(DeviceUtil.IS_INTERNATIONAL_BUILD ? -1 : R.drawable.icon_wifi_network_blocked);
                NotificationUtil.showRightButton(context, builder, intent, string3, 2);
            }
        };
        if (z) {
            showFloatNotification(context, 22, intent, string, string2, r9, false, false, 2);
        } else {
            showNotification(context, 22, intent, (CharSequence) string, (CharSequence) string2, (int) R.drawable.network_diagnosis_small, true, false, (IExtraBuilder) r9, false, 2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:0x0075  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void showFloatNotification(android.content.Context r3, int r4, android.content.Intent r5, java.lang.CharSequence r6, java.lang.CharSequence r7, com.miui.networkassistant.utils.NotificationUtil.IExtraBuilder r8, boolean r9, boolean r10, int r11) {
        /*
            java.lang.String r0 = "notification"
            java.lang.Object r0 = r3.getSystemService(r0)
            android.app.NotificationManager r0 = (android.app.NotificationManager) r0
            java.lang.CharSequence r6 = getNotificationPrefix(r3, r6, r9)
            android.app.PendingIntent r5 = getPendingIntent(r3, r4, r5, r11)
            java.lang.String r9 = "com.miui.securitycenter"
            createChannel(r3, r9)
            android.app.Notification$Builder r9 = b.b.c.j.v.a((android.content.Context) r3, (java.lang.String) r9)
            r11 = 33
            android.app.Notification$Builder r11 = r9.setDefaults(r11)
            long r1 = java.lang.System.currentTimeMillis()
            android.app.Notification$Builder r11 = r11.setWhen(r1)
            r1 = 0
            android.app.Notification$Builder r11 = r11.setOngoing(r1)
            r2 = 2131232019(0x7f080513, float:1.8080135E38)
            android.app.Notification$Builder r11 = r11.setSmallIcon(r2)
            android.app.Notification$Builder r5 = r11.setContentIntent(r5)
            android.app.Notification$Builder r5 = r5.setContentTitle(r6)
            android.app.Notification$Builder r5 = r5.setContentText(r7)
            r6 = 1
            r5.setAutoCancel(r6)
            if (r8 == 0) goto L_0x0058
            r8.onBuild(r9)
            int r5 = r8.getIconRes()
            r7 = -1
            if (r5 == r7) goto L_0x0066
            android.content.res.Resources r5 = r3.getResources()
            int r7 = r8.getIconRes()
            goto L_0x005f
        L_0x0058:
            android.content.res.Resources r5 = r3.getResources()
            r7 = 2131231687(0x7f0803c7, float:1.8079462E38)
        L_0x005f:
            android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeResource(r5, r7)
            r9.setLargeIcon(r5)
        L_0x0066:
            android.app.Notification r5 = r9.build()
            b.b.o.a.a.b(r5, r6)
            b.b.o.a.a.a((android.app.Notification) r5, (boolean) r6)
            b.b.o.a.a.a((android.app.Notification) r5, (int) r1)
            if (r10 == 0) goto L_0x0088
            android.content.Intent r6 = new android.content.Intent
            java.lang.String r7 = "action_broadcast_cancel_notification"
            r6.<init>(r7)
            java.lang.String r7 = "cancel_float_notification"
            r6.putExtra(r7, r4)
            android.app.PendingIntent r3 = b.b.c.j.g.b((android.content.Context) r3, (int) r4, (android.content.Intent) r6)
            b.b.o.a.a.a((android.app.Notification) r5, (android.app.PendingIntent) r3)
        L_0x0088:
            b.b.c.j.g.a((android.app.NotificationManager) r0, (int) r4, (android.app.Notification) r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.utils.NotificationUtil.showFloatNotification(android.content.Context, int, android.content.Intent, java.lang.CharSequence, java.lang.CharSequence, com.miui.networkassistant.utils.NotificationUtil$IExtraBuilder, boolean, boolean, int):void");
    }

    public static void showNetworkChangedNotify(Context context) {
        Context context2 = context;
        showFloatNotification(context2, 80, new Intent("android.settings.WIFI_SETTINGS"), context.getResources().getString(R.string.network_changed_noti_title), context.getResources().getString(R.string.network_changed_noti_summary), new IExtraBuilder() {
            public void onBuild(Notification.Builder builder) {
                builder.setPriority(2);
            }

            public void onCreateIntent(Intent intent) {
            }
        }, false, true, 0);
    }

    private static void showNotification(Context context, int i, Intent intent, CharSequence charSequence, CharSequence charSequence2, int i2, boolean z, boolean z2, IExtraBuilder iExtraBuilder, boolean z3, int i3) {
        showNotification(context, i, intent, charSequence, charSequence2, i2, z, z2, iExtraBuilder, z3, i3, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x008b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void showNotification(android.content.Context r3, int r4, android.content.Intent r5, java.lang.CharSequence r6, java.lang.CharSequence r7, int r8, boolean r9, boolean r10, com.miui.networkassistant.utils.NotificationUtil.IExtraBuilder r11, boolean r12, int r13, boolean r14) {
        /*
            java.lang.String r0 = "notification"
            java.lang.Object r0 = r3.getSystemService(r0)
            android.app.NotificationManager r0 = (android.app.NotificationManager) r0
            java.lang.CharSequence r6 = getNotificationPrefix(r3, r6, r12)
            android.app.PendingIntent r5 = getPendingIntent(r3, r4, r5, r13)
            java.lang.String r12 = getChannelByExtraBuilder(r11)
            createChannel(r3, r12)
            android.app.Notification$Builder r12 = b.b.c.j.v.a((android.content.Context) r3, (java.lang.String) r12)
            r13 = 32
            android.app.Notification$Builder r13 = r12.setDefaults(r13)
            long r1 = java.lang.System.currentTimeMillis()
            android.app.Notification$Builder r13 = r13.setWhen(r1)
            android.app.Notification$Builder r9 = r13.setAutoCancel(r9)
            android.app.Notification$Builder r9 = r9.setOngoing(r10)
            android.app.Notification$Builder r8 = r9.setSmallIcon(r8)
            android.app.Notification$Builder r5 = r8.setContentIntent(r5)
            android.app.Notification$Builder r5 = r5.setContentTitle(r6)
            r5.setContentText(r7)
            boolean r5 = android.text.TextUtils.isEmpty(r7)
            r8 = 1
            r9 = 0
            if (r5 == 0) goto L_0x004c
            r12.setTicker(r6)
            goto L_0x005c
        L_0x004c:
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r9] = r6
            r5[r8] = r7
            java.lang.String r6 = "%s:%s"
            java.lang.String r5 = java.lang.String.format(r6, r5)
            r12.setTicker(r5)
        L_0x005c:
            if (r11 == 0) goto L_0x0071
            r11.onBuild(r12)
            int r5 = r11.getIconRes()
            r6 = -1
            if (r5 == r6) goto L_0x007f
            android.content.res.Resources r3 = r3.getResources()
            int r5 = r11.getIconRes()
            goto L_0x0078
        L_0x0071:
            android.content.res.Resources r3 = r3.getResources()
            r5 = 2131231687(0x7f0803c7, float:1.8079462E38)
        L_0x0078:
            android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeResource(r3, r5)
            r12.setLargeIcon(r3)
        L_0x007f:
            android.app.Notification r3 = r12.build()
            b.b.o.a.a.b(r3, r9)
            b.b.o.a.a.a((android.app.Notification) r3, (boolean) r8)
            if (r14 != 0) goto L_0x008e
            b.b.o.a.a.a((android.app.Notification) r3, (int) r9)
        L_0x008e:
            b.b.c.j.g.a((android.app.NotificationManager) r0, (int) r4, (android.app.Notification) r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.utils.NotificationUtil.showNotification(android.content.Context, int, android.content.Intent, java.lang.CharSequence, java.lang.CharSequence, int, boolean, boolean, com.miui.networkassistant.utils.NotificationUtil$IExtraBuilder, boolean, int, boolean):void");
    }

    private static void showNotification(Context context, int i, Class<? extends Activity> cls, CharSequence charSequence, CharSequence charSequence2, int i2, boolean z, boolean z2, IExtraBuilder iExtraBuilder) {
        showNotification(context, i, cls, charSequence, charSequence2, i2, z, z2, iExtraBuilder, true, 2);
    }

    private static void showNotification(Context context, int i, Class<? extends Activity> cls, CharSequence charSequence, CharSequence charSequence2, int i2, boolean z, boolean z2, IExtraBuilder iExtraBuilder, boolean z3, int i3) {
        IExtraBuilder iExtraBuilder2 = iExtraBuilder;
        Context context2 = context;
        Class<? extends Activity> cls2 = cls;
        Intent intent = new Intent(context, cls);
        if (iExtraBuilder2 != null) {
            iExtraBuilder2.onCreateIntent(intent);
        }
        showNotification(context, i, intent, charSequence, charSequence2, i2, z, z2, iExtraBuilder, z3, i3);
    }

    private static void showOperatorSetting(Context context, String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putInt(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
        bundle.putBoolean(OperatorSettingFragment.BUNDLE_KEY_FROM_NOTIFICATION, true);
        Intent intent = b.b.c.c.b.g.getIntent(context, OperatorSettingFragment.class, bundle);
        intent.putExtra(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
        showNotification(context, 0, intent, str, str2, R.drawable.network_assistant_small, true, false, (IExtraBuilder) null, false, 2, true);
    }

    /* access modifiers changed from: private */
    public static void showRightButton(Context context, Notification.Builder builder, Intent intent, CharSequence charSequence, int i) {
        if (!TextUtils.isEmpty(charSequence)) {
            builder.addAction(R.drawable.selector_notification_btn_bg, charSequence, getPendingIntent(context, 0, intent, i));
            Bundle bundle = new Bundle();
            bundle.putBoolean("miui.showAction", true);
            builder.setExtras(bundle);
        }
    }
}

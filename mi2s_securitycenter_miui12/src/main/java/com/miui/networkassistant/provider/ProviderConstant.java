package com.miui.networkassistant.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProviderConstant {
    public static final String AUTHORITY = "com.miui.networkassistant.provider";
    public static final String AUTHORITY_FILE = "com.miui.networkassistant.fileprovider";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.traffic.provider";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.traffic.provider";

    public static final class BillPackageDetailColumns {
        public static final String PACKAGE_REMAINED = "package_remained";
        public static final String PACKAGE_SETTED = "package_setted";
        public static final String PACKAGE_TOTAL = "package_total";
        public static final String PACKAGE_USED = "package_used";
        public static final String SLOT_NUM = "slot_num";
        public static final String TABLE_NAME = "bill_detail";

        private BillPackageDetailColumns() {
        }
    }

    public static final class CallTimePackageDetailColumns {
        public static final String PACKAGE_REMAINED = "package_remained";
        public static final String PACKAGE_SETTED = "package_setted";
        public static final String PACKAGE_TOTAL = "package_total";
        public static final String PACKAGE_USED = "package_used";
        public static final String SLOT_NUM = "slot_num";
        public static final String TABLE_NAME = "calltime_detail";

        private CallTimePackageDetailColumns() {
        }
    }

    public static final class DataUsageNotiStatusColumns {
        public static final String COLUMN_ACTION1 = "action1";
        public static final String COLUMN_ACTION2 = "action2";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_TEXT1 = "text1";
        public static final String COLUMN_TEXT2 = "text2";
        public static final String TABLE_NAME = "datausage_noti_status";

        private DataUsageNotiStatusColumns() {
        }
    }

    public static final class DataUsageStatusColumns {
        public static final String MONTH_USED = "month_used";
        public static final String MONTH_WARNING = "month_warning";
        public static final String PURCHASE_TIPS_ENABLE = "purchase_tips_enable";
        public static final String TABLE_NAME = "datausage_status";
        public static final String TODAY_USED = "today_used";
        public static final String TOTAL_LIMIT = "total_limit";

        private DataUsageStatusColumns() {
        }
    }

    public static final class DataUsageStatusDetailedColumns {
        public static final String BILL_ICON = "bill_icon";
        public static final String BILL_NAME = "bill_name";
        public static final String BILL_UNIT = "bill_unit";
        public static final String BILL_VALUE = "bill_value";
        public static final String CLICK_ACTION = "click_action";
        public static final String MONTH_USED = "month_used";
        public static final String PACKAGE_TYPE = "package_type";
        public static final String SIM_SLOT = "sim_slot";
        public static final String TABLE_NAME = "datausage_status_detailed";
        public static final String TODAY_USED = "today_used";
        public static final String TOTAL_LIMIT = "total_limit";
        public static final String TRAFFIC_ICON = "traffic_icon";
        public static final String TRAFFIC_NAME = "traffic_name";
        public static final String TRAFFIC_TIME = "traffic_time";
        public static final String TRAFFIC_UNIT = "traffic_unit";
        public static final String TRAFFIC_VALUE = "traffic_value";

        private DataUsageStatusDetailedColumns() {
        }
    }

    public static final class FirewallBackgroundRestrictColumns {
        public static final String PACKAGE_NAME = "package_name";
        public static final String TABLE_NAME = "firewall_background_restrict";

        private FirewallBackgroundRestrictColumns() {
        }
    }

    public static final class FirewallColumns {
        public static final String MOBILE_RULE = "mobile_rule";
        public static final String PACKAGE_NAME = "package_name";
        public static final String TABLE_NAME = "firewall";
        public static final String WIFI_RULE = "wifi_rule";

        private FirewallColumns() {
        }
    }

    public static final class MobileFirewallColumns {
        public static final String FIREWALL_MOBILE_RULE = "mobile_rule";
        public static final String FIREWALL_MOBILE_RULE_SLOTNUM = "mobile_rule_slot";
        public static final String PACKAGE_NAME = "package_name";
        public static final String SRC_PACKAGE = "source_package_name";
        public static final String TABLE_NAME = "mobile_firewall";

        private MobileFirewallColumns() {
        }
    }

    public static final class MobileRestrictColumns {
        public static final String PACKAGE_NAME = "package_name";
        public static final String TABLE_NAME = "mobile_restrict";

        private MobileRestrictColumns() {
        }
    }

    public static final class NASettingsInfoColumns {
        public static final String AUTO_TRAFFIC_CORRECTION = "auto_traffic_correction";
        public static final String CORRECTION_TIME = "correction_time";
        public static final String NEEDED_TRAFFIC_PURCHASE = "needed_traffic_purchase";
        public static final String OPERATOR_SETTED = "operator_setted";
        public static final String OVERSEA_VERSION = "oversea_version";
        public static final String SHOW_STATUS_BAR_SETTED = "show_status_bar_setted";
        public static final String TABLE_NAME = "na_settings_info";
        public static final String TC_DIAGNOSTIC = "tc_diagnostic";
        public static final String TRAFFIC_SAVING_ENABLED = "traffic_saving_enabled";
        public static final String TRAFFIC_SAVING_STARTED = "traffic_saving_started";

        private NASettingsInfoColumns() {
        }
    }

    public static final class SmsCorrectionColumns {
        public static final String BILL_CORRECTION = "bill_correction";
        public static final String CALLTIME_CORRECTION = "calltime_correction";
        public static final String TABLE_NAME = "sms_correction";
        public static final String TRAFFIC_CORRECTION = "traffic_correction";

        private SmsCorrectionColumns() {
        }
    }

    public static final class TempMobileFirewallColumns {
        public static final String FIREWALL_TEMP_MOBILE_RULE = "temp_mobile_rule";
        public static final String FIREWALL_TEMP_MOBILE_RULE_SLOTNUM = "temp_mobile_rule_slot";
        public static final String PACKAGE_NAME = "package_name";
        public static final String SRC_PACKAGE = "source_package_name";
        public static final String TABLE_NAME = "temp_mobile_firewall";

        private TempMobileFirewallColumns() {
        }
    }

    public static final class TempWifiFirewallColumns {
        public static final String FIREWALL_TEMP_WIFI_RULE = "temp_wifi_rule";
        public static final String PACKAGE_NAME = "package_name";
        public static final String SRC_PACKAGE = "source_package_name";
        public static final String TABLE_NAME = "temp_wifi_firewall";

        private TempWifiFirewallColumns() {
        }
    }

    public static final class TetheringLimitColumns {
        public static final String TABLE_NAME = "tethering_limit";
        public static final String TETHERING_LIMIT_ENABLED = "tethering_limit_enabled";

        private TetheringLimitColumns() {
        }
    }

    public static final class TrafficDistributionColumns implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.miui.networkassistant.provider/traffic_distribution");
        public static final String DEFAULT_SORT_ORDER = "_id desc";
        public static final String FROM_PKGNAME = "from_pkgname";
        public static final String IMSI = "imsi";
        public static final String MOBILE_RXBYTES = "mobile_rxbytes";
        public static final String MOBILE_TXBYTES = "mobile_txbytes";
        public static final String STORAGE_TIME = "storage_time";
        public static final String TABLE_NAME = "traffic_distribution";
        public static final String TO_PKGNAME = "to_pkgname";
        public static final String WIFI_RXBYTES = "wifi_rxbytes";
        public static final String WIFI_TXBYTES = "wifi_txbytes";

        private TrafficDistributionColumns() {
        }
    }

    public static final class TrafficPurchaseConfigColumns {
        public static final String FIRST_ENTER_CONFIG = "first_enter_config";
        public static final String SLOT_NUM = "slot_num";
        public static final String TABLE_NAME = "traffic_purchase_config";
        public static final String TRAFFIC_ALERT = "traffic_alert";

        private TrafficPurchaseConfigColumns() {
        }
    }

    public static final class TrafficPurchaseStatusColumns {
        public static final String TABLE_NAME = "na_traffic_purchase";
        public static final String TRAFFIC_PURCHASE_ENABLED = "traffic_purchase_enabled";

        private TrafficPurchaseStatusColumns() {
        }
    }

    public static final class TrafficStatsColumns {
        public static final String TABLE_NAME = "na_traffic_stats";
        public static final String TOTAL_RX_BYTE = "total_rx_byte";
        public static final String TOTAL_TX_BYTE = "total_tx_byte";

        private TrafficStatsColumns() {
        }
    }

    public static final class TrafficUsedAppListColumns {
        public static final String PACKAGE_NAME = "package_name";
        public static final String TABLE_NAME = "top_usage_app";
        public static final String TRAFFIC_USED = "traffic_used";
        public static final String _ID = "_id";

        private TrafficUsedAppListColumns() {
        }
    }

    public static final class WifiFirewallColumns {
        public static final String FIREWALL_WIFI_RULE = "wifi_rule";
        public static final String PACKAGE_NAME = "package_name";
        public static final String SRC_PACKAGE = "source_package_name";
        public static final String TABLE_NAME = "wifi_firewall";

        private WifiFirewallColumns() {
        }
    }

    public static final class WlanRestrictColumns {
        public static final String PACKAGE_NAME = "package_name";
        public static final String TABLE_NAME = "wlan_restrict";

        private WlanRestrictColumns() {
        }
    }

    private ProviderConstant() {
    }
}

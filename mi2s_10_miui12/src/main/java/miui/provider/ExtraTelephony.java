package miui.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.MiuiIntent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.BaseColumns;
import android.provider.CallLog;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.telephony.PhoneConstants;
import com.android.server.wifi.rtt.RttServiceImpl;
import java.io.Closeable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import miui.os.Build;
import miui.provider.ExtraContacts;
import miui.telephony.PhoneNumberUtils;
import miui.telephony.phonenumber.CountryCode;
import miui.telephony.phonenumber.Prefix;
import miui.util.IOUtils;

public final class ExtraTelephony {
    public static final String BANK_CATEGORY_NUMBER_PREFIX_106 = "106";
    public static final Pattern BANK_CATEGORY_PATTERN = Pattern.compile("银行|信用卡|Bank|BANK|支付宝|中国银联");
    public static final Pattern BANK_CATEGORY_SNIPPET_PATTERN = Pattern.compile("((\\[[\\s\\S]*(银行|信用卡|Bank|BANK|支付宝|中国银联)\\])|(\\【[\\s\\S]*(银行|信用卡|Bank|BANK|支付宝|中国银联)\\】))$");
    public static final String BLOCKED_CONV_ADDR = "blocked_conv_addr";
    public static final String BLOCKED_FLAG = "blocked_flag";
    public static final String BLOCKED_FLAG_ALL_MSG = "2";
    public static final String BLOCKED_FLAG_BLOCKED_MSG = "1";
    public static final String BLOCKED_FLAG_NO_BLOCKED_MSG = "0";
    public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";
    public static final String CHECK_DUPLICATION = "check_duplication";
    public static final int DEFAULT_THREADS_LIST_TYPE_SP = 1;
    public static final String DIRTY_QUERY_LIMIT = "dirty_query_limit";
    public static final String FORCE_DELETE = "force_delete";
    public static final int INTERCEPT_STATE_ALL = 0;
    public static final int INTERCEPT_STATE_CALL = 2;
    public static final int INTERCEPT_STATE_SMS = 1;
    public static final String LOCAL_PRIVATE_ADDRESS_SYNC = "local.priaddr.sync";
    public static final String LOCAL_SMS_SYNC = "local.sms.sync";
    public static final String LOCAL_STICKY_THREAD_SYNC = "local.stkthrd.sync";
    public static final String LOCAL_SYNC_NAME = "localName";
    public static final String NEED_FULL_INSERT_URI = "need_full_insert_uri";
    public static final String NO_NOTIFY_FLAG = "no_notify";
    public static final String PRIVACY_FLAG = "privacy_flag";
    public static final String PRIVACY_FLAG_ALL_MSG = "2";
    public static final String PRIVACY_FLAG_NO_PRIVATE_MSG = "0";
    public static final String PRIVACY_FLAG_PRIVATE_MSG = "1";
    public static final String PROVIDER_NAME = "antispam";
    public static final String PrefixCode = "***";
    public static final int SOURCE_ANYONE = 0;
    public static final int SOURCE_CONTACT = 1;
    public static final int SOURCE_STAR = 2;
    public static final int SOURCE_VIP = 3;
    public static final String SUPPRESS_MAKING_MMS_PREVIEW = "supress_making_mms_preview";
    private static final String TAG = "ExtraTelephony";
    public static final String THREADS_LIST_TYPE = "threads_list_type";
    public static final int THREADS_LIST_TYPE_COMPOSITE = 0;
    public static final int TYPE_INTERCEPT_ADDRESS = 2;
    public static final int TYPE_INTERCEPT_NUMBER = 1;
    public static final int TYPE_INTERCEPT_NUMBER_FRAGMENT = 3;
    public static final String ZEN_MODE = "zen_mode";
    public static final int ZEN_MODE_ALARMS = 3;
    public static final int ZEN_MODE_IMPORTANT_INTERRUPTIONS = 1;
    public static final int ZEN_MODE_MIUI_SILENT = 4;
    public static final int ZEN_MODE_NO_INTERRUPTIONS = 2;
    public static final int ZEN_MODE_OFF = 0;
    /* access modifiers changed from: private */
    public static Set<QuietModeEnableListener> mQuietListeners = new HashSet();
    private static SilentModeObserver mSilentModeObserver;

    public static class AdvancedSeen {
        public static final int NON_SP_UNSEEN = 0;
        public static final int SEEN = 3;
        public static final int SP_NOTIFIED = 2;
        public static final int SP_UNSEEN = 1;
    }

    public static final class AntiSpamMode implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/antispam-mode";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/antispam-mode";
        public static final Uri CONTENT_URI = Uri.parse("content://antispam/mode");
        public static final String NAME = "name";
        public static final String STATE = "state";
    }

    public static final class AntiSpamSim implements BaseColumns {
        public static final String BACKSOUND_MODE = "backsound_mode";
        public static final String CALL_WAIT = "call_wait";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/antispam-sim";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/antispam-sim";
        public static final Uri CONTENT_URI = Uri.parse("content://antispam/sim");
        public static final String NAME = "name";
        public static final String SIM_ID = "sim_id";
        public static final String STATE = "state";
    }

    public static final class Blacklist implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/firewall-blacklist";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/firewall-blacklist";
        public static final Uri CONTENT_URI = Uri.parse("content://firewall/blacklist");
        public static final String DISPLAY_NUMBER = "display_number";
        public static final String NOTES = "notes";
        public static final String NUMBER = "number";
        public static final String STATE = "state";
    }

    public interface DeletableSyncColumns extends SyncColumns {
        public static final String DELETED = "deleted";
    }

    public static final class FirewallLog implements BaseColumns {
        public static final String CALL_TYPE = "callType";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/antispam-log";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/antispam-log";
        public static final Uri CONTENT_URI = Uri.parse("content://antispam/log");
        public static final Uri CONTENT_URI_LOG_CONVERSATION = Uri.parse("content://antispam/logconversation");
        public static final Uri CONTENT_URI_SMS_LOG = Uri.parse("content://antispam/log_sms");
        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATE = "date";
        public static final String MODE = "mode";
        public static final String NUMBER = "number";
        public static final String READ = "read";
        public static final String REASON = "reason";
        public static final String SIM_ID = "simid";
        public static final String TYPE = "type";
        public static final int TYPE_CALL = 1;
        public static final int TYPE_SMS = 2;

        public interface CallBlockType {
            public static final int ADDRESS = 13;
            public static final int AGENT = 10;
            public static final int BLACKLIST = 3;
            public static final int CALL_TRANSFER = 15;
            public static final int CLOUDS = 16;
            public static final int CONTACT = 9;
            public static final int FRAUD = 8;
            public static final int HARASS = 14;
            public static final int IMPORT = 5;
            public static final int MUTE_BY_QM = 1;
            public static final int MUTE_NEED_CHECK = 2;
            public static final int NONE = 0;
            public static final int NONE_NEED_CHECK = -1;
            public static final int OVERSEA = 17;
            public static final int PREFIX = 6;
            public static final int PRIVATE_CALL = 4;
            public static final int SELL = 12;
            public static final int STRANGER = 7;
        }

        public interface SmsBlockType {
            public static final int ADDRESS = 13;
            public static final int BLACKLIST = 3;
            public static final int CLOUDS = 16;
            public static final int CONTACT = 9;
            public static final int FILTER = 4;
            public static final int IMPORT = 5;
            public static final int KEYWORDS = 12;
            public static final int NONE = 0;
            public static final int NONE_BUT_MUTE = 1;
            public static final int PREFIX = 6;
            public static final int SERVICE = 10;
            public static final int STRANGER = 7;
            public static final int URL = 8;
        }
    }

    public static final class Hms implements BaseColumns {
        public static final String ADDRESS = "address";
        public static final String ADVANCED_SEEN = "advanced_seen";
        public static final Uri CONTENT_URI = Uri.parse("content://hms/");
        public static final String DATE = "date";
        public static final String MX_CONTENT = "mx_content";
        public static final String MX_EXTENSION = "mx_extension";
        public static final String MX_MESSAGE_ID = "mx_message_id";
        public static final String MX_SEQ = "mx_seq";
        public static final String MX_TYPE = "mx_type";
        public static final String READ = "read";
        public static final String SEEN = "seen";
        public static final String SNIPPET = "snippet";
        public static final String THREAD_ID = "thread_id";
        public static final Uri THREAD_ID_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "threadId");
        public static final String TYPE = "type";
    }

    public static final class Judge implements BaseColumns {
        public static final Uri CALL_CONTENT_URI = Uri.parse("content://antispam/call_judge");
        public static final Uri CALL_TRANSFER_CONTENT_URI = Uri.parse("content://antispam/call_transfer_intercept_judge");
        public static final int FORWARD_CALL_ALLOW = 0;
        public static final int FORWARD_CALL_INTERCEPT = 1;
        public static final String IS_FORWARD_CALL = "is_forward_call";
        public static final String IS_REPEATED_BLOCKED_CALL = "is_repeated_blocked_call";
        public static final String IS_REPEATED_NORMAL_CALL = "is_repeated_normal_call";
        public static final Uri SERVICE_NUM_CONTENT_URI = Uri.parse("content://antispam/service_num_judge");
        public static final Uri SMS_CONTENT_URI = Uri.parse("content://antispam/sms_judge");
        public static final Uri URL_CONTENT_URI = Uri.parse("content://antispam/url_judge");
        public static final int URL_SCAN_RESULT_DANGEROUS = 2;
        public static final int URL_SCAN_RESULT_NORMAL = 0;
        public static final int URL_SCAN_RESULT_RISKY = 1;
        public static final int URL_SCAN_RESULT_UNKNOWN = -1;
    }

    public static final class Keyword implements BaseColumns {
        public static final String CLOUD_UID = "cloudUid";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/antispam-keyword";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/antispam-keyword";
        public static final Uri CONTENT_URI = Uri.parse("content://antispam/keyword");
        public static final String DATA = "data";
        public static final String SIM_ID = "sim_id";
        public static final String TYPE = "type";
        public static final int TYPE_CLOUDS_BLACK = 2;
        public static final int TYPE_CLOUDS_WHITE = 3;
        public static final int TYPE_LOCAL_BLACK = 1;
        public static final int TYPE_LOCAL_WHITE = 4;
    }

    public static final class Mms implements DeletableSyncColumns {
        public static final String ACCOUNT = "account";
        public static final String ADDRESSES = "addresses";
        public static final String ADVANCED_SEEN = "advanced_seen";
        public static final String BIND_ID = "bind_id";
        public static final String BLOCK_TYPE = "block_type";
        public static final String DATE_FULL = "date_full";
        public static final String DATE_MS_PART = "date_ms_part";
        public static final String ERROR_TYPE = "error_type";
        public static final String FAVORITE_DATE = "favorite_date";
        public static final String FILE_ID = "file_id";
        public static final String MX_EXTENSION = "mx_extension";
        public static final String MX_ID = "mx_id";
        public static final String MX_STATUS = "mx_status";
        public static final String MX_TYPE = "mx_type";
        public static final String NEED_DOWNLOAD = "need_download";
        public static final String OUT_TIME = "out_time";
        public static final String PREVIEW_DATA = "preview_data";
        public static final String PREVIEW_DATA_TS = "preview_data_ts";
        public static final String PREVIEW_TYPE = "preview_type";
        public static final String SIM_ID = "sim_id";
        public static final String SNIPPET = "snippet";
        public static final String TIMED = "timed";

        public static final class Intents {
            public static final String MAKE_MMS_PREVIEW_ACTION = "android.provider.Telephony.MAKE_MMS_PREVIEW";
        }

        public static final class PreviewType {
            public static final int AUDIO = 3;
            public static final int IMAGE = 2;
            public static final int NONE = 1;
            public static final int SLIDESHOW = 6;
            public static final int UNKNOWN = 0;
            public static final int VCARD = 5;
            public static final int VIDEO = 4;
        }
    }

    public static final class MmsSms {
        public static final Uri BLOCKED_CONVERSATION_CONTENT_URI = Uri.parse("content://mms-sms/blocked");
        public static final Uri BLOCKED_THREAD_CONTENT_URI = Uri.parse("content://mms-sms/blocked-thread");
        public static final Uri CONTENT_ALL_LOCKED_URI = Uri.parse("content://mms-sms/locked/all");
        public static final Uri CONTENT_ALL_UNDERSTAND_INFO_URI = Uri.parse("content://mms-sms/understand-info/all");
        public static final Uri CONTENT_EXPIRED_URI = Uri.parse("content://mms-sms/expired");
        public static final Uri CONTENT_PREVIEW_URI = Uri.parse("content://mms-sms/message/preview");
        public static final Uri CONTENT_RECENT_RECIPIENTS_URI = Uri.parse("content://mms-sms/recent-recipients");
        public static final Uri CONTENT_UNDERSTAND_INFO_URI = Uri.parse("content://mms-sms/understand-info");
        public static final String EXCLUDE_VERIFICATION_CODES_FLAG = "exclude_verification_codes";
        public static final String EXCLUDE_VERIFICATION_CODES_FLAG_EXCLUDE = "1";
        public static final String EXCLUDE_VERIFICATION_CODES_FLAG_NOT_EXCLUDE = "0";
        public static final String INSERT_PATH_IGNORED = "ignored";
        public static final String INSERT_PATH_INSERTED = "inserted";
        public static final String INSERT_PATH_RESTORED = "restored";
        public static final String INSERT_PATH_UPDATED = "updated";
        public static final int PREVIEW_ADDRESS_COLUMN_INDEX = 1;
        public static final int PREVIEW_BODY_COLUMN_INDEX = 4;
        public static final int PREVIEW_CHARSET_COLUMN_INDEX = 5;
        public static final int PREVIEW_DATE_COLUMN_INDEX = 2;
        public static final int PREVIEW_ID_COLUMN_INDEX = 0;
        public static final int PREVIEW_THREAD_ID_COLUMN_INDEX = 6;
        public static final int PREVIEW_TYPE_COLUMN_INDEX = 3;
        public static final int SYNC_STATE_DIRTY = 0;
        public static final int SYNC_STATE_ERROR = 3;
        public static final int SYNC_STATE_MARKED_DELETING = 65538;
        public static final int SYNC_STATE_NOT_UPLOADABLE = 4;
        public static final int SYNC_STATE_SYNCED = 2;
        public static final int SYNC_STATE_SYNCING = 1;
    }

    public static final class Mx {
        public static final int TYPE_COMMON = 0;
        public static final int TYPE_DELIVERED = 17;
        public static final int TYPE_FAILED = 131073;
        public static final int TYPE_INCOMING = 65537;
        public static final int TYPE_PENDING = 1;
        public static final int TYPE_READ = 256;
        public static final int TYPE_SENT = 16;
        public static final int TYPE_WEB = 196609;
    }

    public static class MxType {
        public static final int AUDIO = 3;
        public static final int IMAGE = 2;
        public static final int MMS = 1;
        public static final int NONE_MX = 0;
        public static final int RED = 5;
        public static final int VIDEO = 4;
    }

    public static final class Phonelist implements BaseColumns {
        public static final String CLOUD_UUID = "cloudUid";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/antispam-phone_list";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/antispam-phone_list";
        public static final Uri CONTENT_URI = Uri.parse("content://antispam/phone_list");
        public static final Uri CONTENT_URI_SYNCED_COUNT = Uri.parse("content://antispam/synced_count");
        public static final Uri CONTENT_URI_UNSYNCED_COUNT = Uri.parse("content://antispam/unsynced_count");
        public static final String DISPLAY_NUMBER = "display_number";
        public static final String E_TAG = "e_tag";
        public static final String IS_DISPLAY = "isdisplay";
        public static final String LOCATION = "location";
        public static final String NOTES = "notes";
        public static final String NUMBER = "number";
        public static final String RECORD_ID = "record_id";
        public static final String SIM_ID = "sim_id";
        public static final String STATE = "state";
        public static final String SYNC_DIRTY = "sync_dirty";
        public static final String TYPE = "type";
        public static final String TYPE_BLACK = "1";
        public static final String TYPE_CLOUDS_BLACK = "4";
        public static final String TYPE_CLOUDS_WHITE = "5";
        public static final String TYPE_STRONG_CLOUDS_BLACK = "6";
        public static final String TYPE_STRONG_CLOUDS_WHITE = "7";
        public static final String TYPE_VIP = "3";
        public static final String TYPE_WHITE = "2";
        public static final String UNKNOWN_NUMBER = "-1";

        public static final class Location {
            public static final int IS_CLOUD = 1;
            public static final int IS_LOCAL = 0;
        }

        public static final class State {
            public static final int ALL = 0;
            public static final int CALL = 2;
            public static final int MSG = 1;
        }

        public static final class SyncDirty {
            public static final int ADD = 0;
            public static final int DELETE = 1;
            public static final int SYNCED = 3;
            public static final int UPDATE = 2;
        }
    }

    public interface PrivateAddresses extends DeletableSyncColumns {
        public static final String ADDRESS = "address";
        public static final Uri CONTENT_URI = Uri.parse("content://mms-sms/private-addresses");
        public static final String _ID = "_id";
    }

    public interface QuietModeEnableListener {
        void onQuietModeEnableChange(boolean z);
    }

    public static class ServiceCategory {
        public static final int DEFAULT_SERVICE_NUMBER = 1;
        public static final int FINANCE_NUMBER = 2;
        public static final int NOT_SERVICE_NUMBER = 0;
    }

    public static final class SimCards {
        public static final String BIND_ID = "bind_id";
        public static final Uri CONTENT_URI = Uri.parse("content://mms-sms/sim-cards");
        public static final String DL_STATUS = "download_status";
        @Deprecated
        public static final String IMSI = "imsi";
        public static final String MARKER1 = "marker1";
        public static final String MARKER2 = "marker2";
        public static final String MARKER_BASE = "marker_base";
        public static final String NUMBER = "number";
        public static final String SIM_ID = "sim_id";
        @Deprecated
        public static final String SLOT = "slot";
        public static final String SYNC_ENABLED = "sync_enabled";
        public static final String SYNC_EXTRA_INFO = "sync_extra_info";
        public static final String _ID = "_id";

        public static final class DLStatus {
            public static final int FINISH = 2;
            public static final int INIT = 0;
            public static final int NEED = 1;
        }

        public static final class SyncStatus {
            public static final int ACTIVE = 1;
            public static final int CLOSED = 2;
            public static final int DIRTY_MASK = 10000;
            public static final int INACTIVE = 0;
        }
    }

    public static final class Sms implements TextBasedSmsColumns, DeletableSyncColumns {
        public static final String ACCOUNT = "account";
        public static final String ADDRESSES = "addresses";
        public static final String ADVANCED_SEEN = "advanced_seen";
        public static final String B2C_NUMBERS = "b2c_numbers";
        public static final String B2C_TTL = "b2c_ttl";
        public static final String BIND_ID = "bind_id";
        public static final String BLOCK_TYPE = "block_type";
        public static final String FAKE_CELL_TYPE = "fake_cell_type";
        public static final String FAVORITE_DATE = "favorite_date";
        public static final String MX_ID = "mx_id";
        public static final String MX_STATUS = "mx_status";
        public static final String OUT_TIME = "out_time";
        public static final String SIM_ID = "sim_id";
        public static final String TIMED = "timed";
        public static final String URL_RISKY_TYPE = "url_risky_type";

        public static final class FakeCellType {
            public static final int CHECKED_SAFE = -1;
            public static final int FAKE = 1;
            public static final int NORMAL = 0;
        }

        public static final class Intents {
            public static final String DISMISS_NEW_MESSAGE_NOTIFICATION_ACTION = "android.provider.Telephony.DISMISS_NEW_MESSAGE_NOTIFICATION";
        }

        public static final class UrlRiskyType {
            public static final int URL_FRAUD_DANGEROUS = 3;
            public static final int URL_RISKY = 2;
            public static final int URL_SAFE = 0;
            public static final int URL_SUSPICIOUS = 1;
        }
    }

    public static final class SmsPhrase {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/smsphrase";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/smsphrase";
    }

    public interface SyncColumns {
        public static final String MARKER = "marker";
        public static final String SOURCE = "source";
        public static final String SYNC_STATE = "sync_state";
    }

    public interface TextBasedSmsColumns {
        public static final int MESSAGE_TYPE_INVALID = 7;
    }

    public static final class Threads implements ThreadsColumns {

        public static final class Intents {
            public static final String THREADS_OBSOLETED_ACTION = "android.intent.action.SMS_THREADS_OBSOLETED_ACTION";
        }
    }

    public interface ThreadsColumns extends SyncColumns {
        public static final String HAS_DRAFT = "has_draft";
        public static final String LAST_SIM_ID = "last_sim_id";
        public static final String MX_SEQ = "mx_seq";
        public static final String PRIVATE_ADDR_IDS = "private_addr_ids";
        public static final String RMS_TYPE = "rms_type";
        public static final String SP_TYPE = "sp_type";
        public static final String STICK_TIME = "stick_time";
        public static final String UNREAD_COUNT = "unread_count";
    }

    public static final class UnderstandInfo {
        public static final String CLASS = "class";
        public static final String MSG_ID = "msg_id";
        public static final String MSG_TYPE = "msg_type";
        public static final String OUT_OF_DATE = "out_of_date";
        public static final String VERSION = "version";

        public static final class MessageType {
            public static final int RMS = 1;
            public static final int SMS = 0;
        }

        public static final class UnderstandClass {
            public static final int NORMAL = 0;
            public static final int VERIFICATION_CODE = 1;
        }
    }

    public static final class Whitelist implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/firewall-whitelist";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/firewall-whitelist";
        public static final Uri CONTENT_URI = Uri.parse("content://firewall/whitelist");
        public static final String DISPLAY_NUMBER = "display_number";
        public static final String ISDISPLAY = "isdisplay";
        public static final String NOTES = "notes";
        public static final String NUMBER = "number";
        public static final String STATE = "state";
        public static final String VIP = "vip";
    }

    public static boolean isInBlacklist(Context context, String number) {
        String nomalizeNumber;
        boolean z = false;
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        if (number.contains("*")) {
            nomalizeNumber = normalizeNumber(number);
        } else {
            nomalizeNumber = PhoneNumberUtils.PhoneNumber.parse(number).getNormalizedNumber(false, true);
        }
        String number2 = TextUtils.isEmpty(nomalizeNumber) ? number : nomalizeNumber;
        if (number2.matches("[a-zA-Z]*-[a-zA-Z]*")) {
            number2 = number2.substring(number2.indexOf("-"));
        }
        Cursor c = null;
        try {
            c = context.getContentResolver().query(Phonelist.CONTENT_URI, (String[]) null, "number = ? AND type = ? AND sync_dirty <> ? ", new String[]{number2, "1", String.valueOf(1)}, (String) null);
            if (c != null) {
                if (c.getCount() > 0) {
                    z = true;
                }
                IOUtils.closeQuietly(c);
                return z;
            }
        } catch (Exception e) {
            Log.e(TAG, "Cursor exception in isInBlacklist(): ", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(c);
        return false;
    }

    public static boolean isInBlacklist(Context context, String number, int simId) {
        String nomalizeNumber;
        boolean z = false;
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        if (number.contains("*")) {
            nomalizeNumber = normalizeNumber(number);
        } else {
            nomalizeNumber = PhoneNumberUtils.PhoneNumber.parse(number).getNormalizedNumber(false, true);
        }
        String number2 = TextUtils.isEmpty(nomalizeNumber) ? number : nomalizeNumber;
        if (number2.matches("[a-zA-Z]*-[a-zA-Z]*")) {
            number2 = number2.substring(number2.indexOf("-"));
        }
        Cursor c = context.getContentResolver().query(Phonelist.CONTENT_URI, (String[]) null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{number2, "1", String.valueOf(simId), String.valueOf(1)}, (String) null);
        if (c != null) {
            try {
                if (c.getCount() > 0) {
                    z = true;
                }
                return z;
            } catch (Exception e) {
                Log.e(TAG, "Cursor exception in isInBlacklist(): ", e);
            } finally {
                c.close();
            }
        }
        return false;
    }

    public static boolean isInBlacklist(Context context, String number, int state, int simId) {
        Cursor c;
        int dbState;
        if (!TextUtils.isEmpty(number) && (c = context.getContentResolver().query(Phonelist.CONTENT_URI, (String[]) null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{number, "1", String.valueOf(simId), String.valueOf(1)}, (String) null)) != null) {
            try {
                if (c.moveToNext() && ((dbState = c.getInt(c.getColumnIndex("state"))) == 0 || dbState == state)) {
                    c.close();
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "Cursor exception in isInBlacklist(): ", e);
            } catch (Throwable th) {
                c.close();
                throw th;
            }
            c.close();
        }
        return false;
    }

    public static boolean isPrefixInBlack(Context context, String number, int state, int simId) {
        int dbState;
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        String tmp = Prefix.EMPTY;
        for (int i = 0; i < number.length(); i++) {
            tmp = tmp + number.charAt(i);
            Cursor c = context.getContentResolver().query(Phonelist.CONTENT_URI, (String[]) null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{tmp + "*", "1", String.valueOf(simId), String.valueOf(1)}, (String) null);
            if (c != null) {
                try {
                    if (c.moveToNext() && ((dbState = c.getInt(c.getColumnIndex("state"))) == 0 || dbState == state)) {
                        c.close();
                        return true;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Cursor exception in isPrefixInBlack(): ", e);
                } catch (Throwable th) {
                    c.close();
                    throw th;
                }
                c.close();
            }
        }
        return false;
    }

    public static boolean isAddressInBlack(Context context, String rawNumber, int state, int simId) {
        int dbState;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Phonelist.CONTENT_URI;
        Cursor c = contentResolver.query(uri, (String[]) null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{PrefixCode + PhoneNumberUtils.PhoneNumber.getLocationAreaCode(context, rawNumber), "1", String.valueOf(simId), String.valueOf(1)}, (String) null);
        if (c != null) {
            try {
                if (c.moveToNext() && ((dbState = c.getInt(c.getColumnIndex("state"))) == 0 || dbState == state)) {
                    c.close();
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "Cursor exception in isAddressInBlack(): ", e);
            } catch (Throwable th) {
                c.close();
                throw th;
            }
            c.close();
        }
        return false;
    }

    public static boolean isInWhiteList(Context context, String number) {
        String number2;
        boolean z = false;
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        if (number.contains("*")) {
            number2 = normalizeNumber(number);
        } else {
            number2 = PhoneNumberUtils.PhoneNumber.parse(number).getNormalizedNumber(false, true);
        }
        Cursor c = context.getContentResolver().query(Phonelist.CONTENT_URI, (String[]) null, "number = ? AND type = ? AND sync_dirty <> ? ", new String[]{number2, "2", String.valueOf(1)}, (String) null);
        if (c != null) {
            try {
                if (c.getCount() > 0) {
                    z = true;
                }
                return z;
            } catch (Exception e) {
                Log.e(TAG, "Cursor exception in isInWhiteList(): ", e);
            } finally {
                c.close();
            }
        }
        return false;
    }

    public static boolean isInWhiteList(Context context, String number, int simId) {
        String number2;
        boolean z = false;
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        if (number.contains("*")) {
            number2 = normalizeNumber(number);
        } else {
            number2 = PhoneNumberUtils.PhoneNumber.parse(number).getNormalizedNumber(false, true);
        }
        Cursor c = context.getContentResolver().query(Phonelist.CONTENT_URI, (String[]) null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{number2, "2", String.valueOf(simId), String.valueOf(1)}, (String) null);
        if (c != null) {
            try {
                if (c.getCount() > 0) {
                    z = true;
                }
                return z;
            } catch (Exception e) {
                Log.e(TAG, "Cursor exception in isInWhiteList(): ", e);
            } finally {
                c.close();
            }
        }
        return false;
    }

    public static boolean isInWhiteList(Context context, String rawNumber, int state, int simId) {
        int dbState;
        int dbState2;
        int dbState3;
        int i = state;
        if (TextUtils.isEmpty(rawNumber)) {
            return false;
        }
        int i2 = 4;
        Cursor cursor = context.getContentResolver().query(Phonelist.CONTENT_URI, (String[]) null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{PrefixCode + PhoneNumberUtils.PhoneNumber.getLocationAreaCode(context, rawNumber), "2", String.valueOf(simId), String.valueOf(1)}, (String) null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext() && ((dbState3 = cursor.getInt(cursor.getColumnIndex("state"))) == 0 || dbState3 == i)) {
                    cursor.close();
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "Cursor exception when area check in whiteList: ", e);
            } catch (Throwable th) {
                cursor.close();
                throw th;
            }
            cursor.close();
        }
        String normalizedNum = PhoneNumberUtils.PhoneNumber.parse(rawNumber).getNormalizedNumber(false, true);
        int i3 = 0;
        String tmp = Prefix.EMPTY;
        while (i3 < normalizedNum.length()) {
            tmp = tmp + normalizedNum.charAt(i3);
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Phonelist.CONTENT_URI;
            String[] strArr = new String[i2];
            strArr[0] = tmp + "*";
            strArr[1] = "2";
            strArr[2] = String.valueOf(simId);
            strArr[3] = String.valueOf(1);
            Cursor cursor2 = contentResolver.query(uri, (String[]) null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", strArr, (String) null);
            if (cursor2 != null) {
                try {
                    if (cursor2.moveToNext() && ((dbState2 = cursor2.getInt(cursor2.getColumnIndex("state"))) == 0 || dbState2 == i)) {
                        cursor2.close();
                        return true;
                    }
                } catch (Exception e2) {
                    Log.e(TAG, "Cursor exception when prefix check in whiteList: ", e2);
                } catch (Throwable th2) {
                    cursor2.close();
                    throw th2;
                }
                cursor2.close();
            }
            i3++;
            i2 = 4;
        }
        Cursor cursor3 = context.getContentResolver().query(Phonelist.CONTENT_URI, (String[]) null, "number= ? AND type= ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{normalizedNum, "2", String.valueOf(simId), String.valueOf(1)}, (String) null);
        if (cursor3 != null) {
            try {
                if (cursor3.moveToNext() && ((dbState = cursor3.getInt(cursor3.getColumnIndex("state"))) == 0 || dbState == i)) {
                    cursor3.close();
                    return true;
                }
            } catch (Exception e3) {
                Log.e(TAG, "Cursor exception when complete check in whiteList: ", e3);
            } catch (Throwable th3) {
                cursor3.close();
                throw th3;
            }
            cursor3.close();
        }
        return false;
    }

    public static boolean isInVipList(Context context, String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        boolean z = true;
        Cursor cursor = context.getContentResolver().query(Phonelist.CONTENT_URI, (String[]) null, "number = ? AND type = ? AND sync_dirty <> ? ", new String[]{number, Phonelist.TYPE_VIP, String.valueOf(1)}, (String) null);
        if (cursor != null) {
            try {
                if (cursor.getCount() <= 0) {
                    z = false;
                }
                return z;
            } catch (Exception e) {
                Log.e(TAG, "Cursor exception in isInVipList(): ", e);
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    public static boolean isInCloudPhoneList(Context context, String number, int state, String type) {
        Cursor cursor;
        if (!TextUtils.isEmpty(number) && (cursor = context.getContentResolver().query(Phonelist.CONTENT_URI, (String[]) null, "type = ? AND state in (0, ?)", new String[]{type, String.valueOf(state)}, (String) null)) != null) {
            do {
                try {
                    if (!cursor.moveToNext()) {
                        cursor.close();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Cursor exception when check prefix cloudPhoneList: ", e);
                } catch (Throwable th) {
                    cursor.close();
                    throw th;
                }
            } while (!number.matches(number2regex(cursor.getString(cursor.getColumnIndex("number")))));
            cursor.close();
            return true;
        }
        return false;
    }

    private static String number2regex(String number) {
        return number.replace("*", "[\\s\\S]*").replace("#", "[\\s\\S]").replace(CountryCode.GSM_GENERAL_IDD_CODE, "\\+");
    }

    public static boolean containsKeywords(Context context, String smsBody, int type, int simId) {
        Cursor cursor = context.getContentResolver().query(Keyword.CONTENT_URI, (String[]) null, "type = ? AND sim_id = ? ", new String[]{String.valueOf(type), String.valueOf(simId)}, (String) null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String keyword = cursor.getString(cursor.getColumnIndex("data")).trim();
                    if (!TextUtils.isEmpty(keyword) && smsBody.toLowerCase().contains(keyword.toLowerCase())) {
                        cursor.close();
                        return true;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Cursor exception in shouldFilter()", e);
                } catch (Throwable th) {
                    cursor.close();
                    throw th;
                }
            }
            cursor.close();
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0051 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0038 A[Catch:{ Exception -> 0x0057, all -> 0x0055 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isInSmsWhiteList(android.content.Context r8, java.lang.String r9) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r9)
            r1 = 0
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            android.content.ContentResolver r2 = r8.getContentResolver()
            android.net.Uri r3 = miui.yellowpage.YellowPageContract.AntispamWhiteList.CONTNET_URI
            java.lang.String r0 = "number"
            java.lang.String[] r4 = new java.lang.String[]{r0}
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "number LIKE '"
            r0.append(r5)
            r0.append(r9)
            java.lang.String r5 = "%'"
            r0.append(r5)
            java.lang.String r5 = r0.toString()
            r6 = 0
            r7 = 0
            android.database.Cursor r0 = r2.query(r3, r4, r5, r6, r7)
            if (r0 == 0) goto L_0x0060
        L_0x0032:
            boolean r2 = r0.moveToNext()     // Catch:{ Exception -> 0x0057 }
            if (r2 == 0) goto L_0x0051
            java.lang.String r2 = r0.getString(r1)     // Catch:{ Exception -> 0x0057 }
            boolean r3 = android.text.TextUtils.equals(r9, r2)     // Catch:{ Exception -> 0x0057 }
            if (r3 != 0) goto L_0x004c
            java.lang.String r3 = "106"
            boolean r3 = r9.startsWith(r3)     // Catch:{ Exception -> 0x0057 }
            if (r3 == 0) goto L_0x004b
            goto L_0x004c
        L_0x004b:
            goto L_0x0032
        L_0x004c:
            r1 = 1
            r0.close()
            return r1
        L_0x0051:
            r0.close()
            goto L_0x0060
        L_0x0055:
            r1 = move-exception
            goto L_0x005c
        L_0x0057:
            r2 = move-exception
            r2.printStackTrace()     // Catch:{ all -> 0x0055 }
            goto L_0x0051
        L_0x005c:
            r0.close()
            throw r1
        L_0x0060:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraTelephony.isInSmsWhiteList(android.content.Context, java.lang.String):boolean");
    }

    public static boolean checkMarkedNumberIntercept(Context context, int simId, int cid, String number, int antispamProviderId, boolean isUserMarked, int markedCount) {
        String markType = (String) ((HashMap) MiuiSettings.AntiSpam.mapIdToState.get(Integer.valueOf(simId))).get(Integer.valueOf(cid));
        if (markType == null) {
            Slog.d(TAG, "the mark type of cid is not found ... allow");
            return false;
        }
        if (!(MiuiSettings.AntiSpam.getMode(context, markType, 1) == 0)) {
            Slog.d(TAG, "the switch of " + markType + " is not open ... allow");
            return false;
        } else if (isRelatedNumber(context, number)) {
            Slog.d(TAG, "call number is a related number... allow");
            return false;
        } else {
            boolean threshold = MiuiSettings.AntiSpam.getMode(context, (String) ((HashMap) MiuiSettings.AntiSpam.mapIdToMarkTime.get(Integer.valueOf(simId))).get(Integer.valueOf(cid)), 50) <= markedCount;
            Slog.d(TAG, "marking threshold reached ? " + threshold);
            if (!isUserMarked && antispamProviderId != 398 && !threshold) {
                return false;
            }
            Slog.d(TAG, "should intercept this marked call !");
            return true;
        }
    }

    private static boolean isRelatedNumber(Context context, String number) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{"type"}, "number = ? OR normalized_number = ? ", new String[]{number, number}, ExtraContacts.Calls.DEFAULT_SORT_ORDER);
            if (c != null) {
                while (c.moveToNext()) {
                    if (c.getInt(0) == 2) {
                        IOUtils.closeQuietly(c);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Cursor exception in isRelatedNumber(): ", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(c);
        return false;
    }

    public static int getCallBlockType(Context context, String number, int slotId, boolean isForwardCall, boolean isRepeated, boolean isRepeatedBlocked) {
        if (TextUtils.isEmpty(number)) {
            return 0;
        }
        final Context context2 = context;
        final String str = number;
        final int i = slotId;
        final boolean z = isForwardCall;
        final boolean z2 = isRepeated;
        final boolean z3 = isRepeatedBlocked;
        FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
            public Integer call() throws Exception {
                ContentResolver contentResolver = context2.getContentResolver();
                Uri uri = Judge.CALL_CONTENT_URI;
                ContentValues contentValues = new ContentValues();
                String[] strArr = new String[5];
                strArr[0] = str;
                strArr[1] = String.valueOf(i);
                boolean z = z;
                String str = Prefix.EMPTY;
                strArr[2] = z ? Judge.IS_FORWARD_CALL : str;
                strArr[3] = z2 ? Judge.IS_REPEATED_NORMAL_CALL : str;
                if (z3) {
                    str = Judge.IS_REPEATED_BLOCKED_CALL;
                }
                strArr[4] = str;
                return Integer.valueOf(contentResolver.update(uri, contentValues, (String) null, strArr));
            }
        });
        try {
            new Thread(task).start();
            return task.get(RttServiceImpl.HAL_RANGING_TIMEOUT_MS, TimeUnit.MILLISECONDS).intValue();
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException when getCallBlockType", e);
            return 0;
        } catch (ExecutionException e2) {
            Log.e(TAG, "ExecutionException when getCallBlockType", e2);
            return 0;
        } catch (TimeoutException e3) {
            if (!task.isDone()) {
                task.cancel(true);
            }
            Log.e(TAG, "TimeoutException when getCallBlockType", e3);
            return 0;
        }
    }

    public static int getSmsBlockType(Context context, String number, String body, int slotId) {
        String smsBody = Prefix.EMPTY;
        String smsNumber = number == null ? smsBody : number;
        if (body != null) {
            smsBody = body;
        }
        int smsBlockType = 0;
        try {
            smsBlockType = context.getContentResolver().update(Judge.SMS_CONTENT_URI, new ContentValues(), (String) null, new String[]{smsNumber, smsBody, String.valueOf(slotId)});
        } catch (Exception e) {
            Log.e(TAG, "getSmsBlockType error", e);
        }
        if (smsBlockType < 0) {
            return 0;
        }
        return smsBlockType;
    }

    public static boolean isCallTransferBlocked(Context context, int slotId) {
        try {
            if (context.getContentResolver().update(Judge.CALL_TRANSFER_CONTENT_URI, new ContentValues(), (String) null, new String[]{String.valueOf(slotId)}) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Exception when isCallTransferBlocked()", e);
            return false;
        }
    }

    public static int getSmsURLScanResult(Context context, String num, String url) {
        String num2 = PhoneNumberUtils.PhoneNumber.parse(num).getNormalizedNumber(false, true);
        try {
            return context.getContentResolver().update(Judge.URL_CONTENT_URI, new ContentValues(), (String) null, new String[]{num2, url});
        } catch (Exception e) {
            Log.e(TAG, "Exception when getSmsURLScanResult()", e);
            return -1;
        }
    }

    public static boolean isTargetServiceNum(Context context, String num) {
        String num2 = PhoneNumberUtils.PhoneNumber.parse(num).getNormalizedNumber(false, true);
        try {
            if (context.getContentResolver().update(Judge.SERVICE_NUM_CONTENT_URI, new ContentValues(), (String) null, new String[]{num2}) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Exception when isTargetServiceNum()", e);
            return false;
        }
    }

    public static int getRealBlockType(int blockType) {
        return blockType & 127;
    }

    public static boolean isURLFlagRisky(int blockType) {
        return (blockType & 128) == 128;
    }

    public static void sendCallInterceptNotification(Context context, String number, int blockType, int subId) {
        Intent intent = new Intent(MiuiIntent.ACTION_FIREWALL_UPDATED);
        intent.putExtra("key_sim_id", subId);
        intent.putExtra("notification_intercept_number", number);
        intent.putExtra(MiuiIntent.INTENT_EXTRA_KEY_BLOCK_LOG_TYPE, 1);
        intent.putExtra("notification_block_type", blockType);
        if (blockType == 3 || blockType == 6 || blockType == 13) {
            intent.putExtra("notification_show_type", 0);
        } else {
            intent.putExtra("notification_show_type", 1);
        }
        context.sendBroadcastAsUser(intent, UserHandle.CURRENT_OR_SELF);
    }

    public static void sendMsgInterceptNotification(Context context, int blockType, int subId) {
        if (Build.IS_MIUI) {
            Intent intent = new Intent(MiuiIntent.ACTION_FIREWALL_UPDATED);
            intent.putExtra("key_sim_id", subId);
            intent.putExtra(MiuiIntent.INTENT_EXTRA_KEY_BLOCK_LOG_TYPE, 2);
            if (blockType == 3 || blockType == 13 || blockType == 6 || blockType == 12) {
                intent.putExtra("notification_show_type", 0);
            } else {
                intent.putExtra("notification_show_type", 1);
            }
            context.sendBroadcast(intent);
        }
    }

    private static String convertPresentationToFilterNumber(int presentation, String logNumber) {
        if (presentation == PhoneConstants.PRESENTATION_RESTRICTED) {
            return PhoneNumberUtils.PRIVATE_NUMBER;
        }
        if (presentation == PhoneConstants.PRESENTATION_PAYPHONE) {
            return PhoneNumberUtils.PAYPHONE_NUMBER;
        }
        if (TextUtils.isEmpty(logNumber) || presentation == PhoneConstants.PRESENTATION_UNKNOWN) {
            return "-1";
        }
        return logNumber;
    }

    private static class SilentModeObserver extends ContentObserver {
        private Context mContext;

        public SilentModeObserver(Context context, Handler handler) {
            super(handler);
            this.mContext = context.getApplicationContext() != null ? context.getApplicationContext() : context;
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (ExtraTelephony.mQuietListeners != null) {
                boolean isSilenceModeEnable = MiuiSettings.SilenceMode.isSilenceModeEnable(this.mContext);
                for (QuietModeEnableListener onQuietModeEnableChange : ExtraTelephony.mQuietListeners) {
                    onQuietModeEnableChange.onQuietModeEnableChange(isSilenceModeEnable);
                }
            }
        }
    }

    public static void registerQuietModeEnableListener(Context context, QuietModeEnableListener quietListener) {
        if (Build.IS_MIUI) {
            mQuietListeners.add(quietListener);
            if (mSilentModeObserver == null) {
                mSilentModeObserver = new SilentModeObserver(context, new Handler());
                if (MiuiSettings.SilenceMode.isSupported) {
                    registerContentObserver(context.getContentResolver(), Settings.Global.getUriFor(ZEN_MODE), false, mSilentModeObserver, -1);
                    registerContentObserver(context.getContentResolver(), Settings.System.getUriFor("vibrate_in_silent"), false, mSilentModeObserver, -1);
                    registerContentObserver(context.getContentResolver(), Settings.System.getUriFor("show_notification"), false, mSilentModeObserver, -1);
                    return;
                }
                registerContentObserver(context.getContentResolver(), Settings.Secure.getUriFor("quiet_mode_enable"), false, mSilentModeObserver, -1);
            }
        }
    }

    public static void unRegisterQuietModeEnableListener(Context context, QuietModeEnableListener quietListener) {
        mQuietListeners.remove(quietListener);
        if (mQuietListeners.size() <= 0 && mSilentModeObserver != null) {
            context.getContentResolver().unregisterContentObserver(mSilentModeObserver);
            mSilentModeObserver = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x005d, code lost:
        if (r0 != null) goto L_0x0068;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0066, code lost:
        if (r0 == null) goto L_0x006c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0068, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x006c, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean checkKeyguardForQuiet(android.content.Context r9, java.lang.String r10) {
        /*
            boolean r0 = miui.os.Build.IS_MIUI
            r1 = 1
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            boolean r0 = android.provider.MiuiSettings.SilenceMode.isSupported
            if (r0 == 0) goto L_0x000f
            boolean r0 = checkKeyguardForSilentMode(r9)
            return r0
        L_0x000f:
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r0 >= r2) goto L_0x001a
            boolean r0 = android.provider.MiuiSettings.AntiSpam.isQuietModeEnable(r9)
            return r0
        L_0x001a:
            boolean r0 = android.provider.MiuiSettings.AntiSpam.isQuietModeEnable(r9)
            r2 = 0
            if (r0 == 0) goto L_0x0074
            java.lang.String r0 = "com.android.mms"
            boolean r0 = r0.equals(r10)
            if (r0 == 0) goto L_0x002a
            return r1
        L_0x002a:
            java.lang.String r0 = "com.android.incallui"
            boolean r0 = r0.equals(r10)
            if (r0 != 0) goto L_0x0073
            java.lang.String r0 = "com.android.server.telecom"
            boolean r0 = r0.equals(r10)
            if (r0 == 0) goto L_0x003b
            goto L_0x0073
        L_0x003b:
            r0 = 0
            android.content.ContentResolver r3 = r9.getContentResolver()     // Catch:{ Exception -> 0x0062 }
            java.lang.String r4 = "content://antispamCommon/zenmode"
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x0062 }
            java.lang.String r5 = "4"
            android.net.Uri r4 = android.net.Uri.withAppendedPath(r4, r5)     // Catch:{ Exception -> 0x0062 }
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0062 }
            r0 = r3
            if (r0 == 0) goto L_0x005d
            r0.close()
            return r2
        L_0x005d:
            if (r0 == 0) goto L_0x006b
            goto L_0x0068
        L_0x0060:
            r1 = move-exception
            goto L_0x006d
        L_0x0062:
            r2 = move-exception
            r2.printStackTrace()     // Catch:{ all -> 0x0060 }
            if (r0 == 0) goto L_0x006b
        L_0x0068:
            r0.close()
        L_0x006b:
            return r1
        L_0x006d:
            if (r0 == 0) goto L_0x0072
            r0.close()
        L_0x0072:
            throw r1
        L_0x0073:
            return r1
        L_0x0074:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraTelephony.checkKeyguardForQuiet(android.content.Context, java.lang.String):boolean");
    }

    public static boolean checkKeyguardForSilentMode(Context context) {
        if (!Build.IS_MIUI) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 21) {
            return MiuiSettings.SilenceMode.isSilenceModeEnable(context);
        }
        if (MiuiSettings.SilenceMode.getZenMode(context) == 1) {
            return true;
        }
        return false;
    }

    public static String normalizeNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return Prefix.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        int len = phoneNumber.length();
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            appendNonSeparator(sb, c, i);
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                sb.append(digit);
            } else if (i == 0 && c == '+') {
                sb.append(c);
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                return normalizeNumber(PhoneNumberUtils.convertKeypadLettersToDigits(phoneNumber));
            }
        }
        return sb.toString();
    }

    private static void appendNonSeparator(StringBuilder sb, char c, int pos) {
        if (!(pos == 0 && c == '+') && Character.digit(c, 10) == -1 && PhoneNumberUtils.isNonSeparator(c)) {
            sb.append(c);
        }
    }

    public static boolean isServiceNumber(String address) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }
        PhoneNumberUtils.PhoneNumber phoneNumber = PhoneNumberUtils.PhoneNumber.parse(address);
        if (phoneNumber.isServiceNumber()) {
            return true;
        }
        if (!phoneNumber.isChineseNumber() || !address.startsWith(BANK_CATEGORY_NUMBER_PREFIX_106)) {
            return false;
        }
        return true;
    }

    private static void registerContentObserver(ContentResolver cr, Uri uri, boolean notifyForDescendents, ContentObserver observer, int userHandle) {
        Class<ContentResolver> cls = ContentResolver.class;
        try {
            Method method = cls.getDeclaredMethod("registerContentObserver", new Class[]{Uri.class, Boolean.TYPE, ContentObserver.class, Integer.TYPE});
            method.setAccessible(true);
            method.invoke(cr, new Object[]{uri, Boolean.valueOf(notifyForDescendents), observer, Integer.valueOf(userHandle)});
        } catch (Exception e) {
            Log.w(TAG, "invoke registerContentObserver failed", e);
        }
    }
}

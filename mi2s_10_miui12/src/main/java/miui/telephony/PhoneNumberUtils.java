package miui.telephony;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.PhoneConstants;
import com.miui.internal.telephony.phonenumber.ChineseTelocation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miui.system.R;
import miui.telephony.phonenumber.CountryCode;
import miui.telephony.phonenumber.CountryCodeConverter;
import miui.telephony.phonenumber.Prefix;
import miui.util.AppConstants;

public class PhoneNumberUtils extends android.telephony.PhoneNumberUtils {
    private static final String CHINA_COUNTRY_CODE = "86";
    private static final int CHINA_IOT_MOBILE_NUMBER_LENGTH = 13;
    public static final String CHINA_MCC = "460";
    private static final int CHINA_MOBILE_NUMBER_LENGTH = 11;
    private static final String CHINA_MOBILE_NUMBER_PREFIX = "1";
    private static final String CHINA_REGION_CODE1 = "+86";
    private static final String CHINA_REGION_CODE2 = "0086";
    private static final String[] EMERGENCY_NUMBERS = {"110", "112", "119", "120", "122", "911", "999", "995", "100", "101", "102", "190"};
    static final String LOG_TAG = "PhoneNumberUtils";
    public static final int MASK_PHONE_NUMBER_MODE_HEAD = 0;
    public static final int MASK_PHONE_NUMBER_MODE_MIDDLE = 2;
    public static final int MASK_PHONE_NUMBER_MODE_TAIL = 1;
    private static final int MIN_QUERY_LOCATION_EFFECTIVE_IOT_NUMBER_LENGTH = 9;
    private static final int MIN_QUERY_LOCATION_EFFECTIVE_NUMBER_LENGTH = 7;
    public static final String PAYPHONE_NUMBER = "-3";
    public static final String PRIVATE_NUMBER = "-2";
    public static final String UNKNOWN_NUMBER = "-1";

    public interface OperatorQueryListener {
        void onComplete(Object obj, Object obj2, Object obj3, Object obj4, String str);
    }

    public interface TelocationAndOperatorQueryListener {
        void onComplete(Object obj, Object obj2, Object obj3, Object obj4, String str, String str2);
    }

    public interface TelocationQueryListener {
        void onComplete(Object obj, Object obj2, Object obj3, Object obj4, String str);
    }

    public static String stripSeparatorsAndCountryCode(String phoneNumber) {
        String number = stripSeparators(phoneNumber);
        if (number == null) {
            return number;
        }
        if (number.startsWith(CHINA_REGION_CODE1)) {
            return number.substring(CHINA_REGION_CODE1.length());
        }
        if (number.startsWith(CHINA_REGION_CODE2)) {
            return number.substring(CHINA_REGION_CODE2.length());
        }
        return number;
    }

    private static int minPositive(int a, int b) {
        if (a >= 0 && b >= 0) {
            return a < b ? a : b;
        }
        if (a >= 0) {
            return a;
        }
        if (b >= 0) {
            return b;
        }
        return -1;
    }

    private static int indexOfLastNetworkChar(String a) {
        int origLength = a.length();
        int trimIndex = minPositive(a.indexOf(44), a.indexOf(59));
        if (trimIndex < 0) {
            return origLength - 1;
        }
        return trimIndex - 1;
    }

    public static String extractNetworkPortion(String phoneNumber) {
        return extractNetworkPortion(phoneNumber, 0);
    }

    public static String extractNetworkPortion(String phoneNumber, int phoneType) {
        if (phoneNumber == null) {
            return null;
        }
        if (phoneType == 3 || invokeIsUriNumber(phoneNumber)) {
            return phoneNumber.substring(0, indexOfLastNetworkChar(phoneNumber) + 1).trim();
        }
        return android.telephony.PhoneNumberUtils.extractNetworkPortion(phoneNumber);
    }

    private static boolean invokeIsUriNumber(String phoneNumber) {
        try {
            Method method = android.telephony.PhoneNumberUtils.class.getDeclaredMethod("isUriNumber", new Class[]{String.class});
            method.setAccessible(true);
            return ((Boolean) method.invoke((Object) null, new Object[]{phoneNumber})).booleanValue();
        } catch (Exception e) {
            Log.w(LOG_TAG, "invoke isUriNumber failed", e);
            return false;
        }
    }

    public static String extractNetworkPortionAlt(String phoneNumber) {
        return extractNetworkPortionAlt(phoneNumber, 0);
    }

    public static String extractNetworkPortionAlt(String phoneNumber, int phoneType) {
        if (phoneNumber == null) {
            return null;
        }
        if (phoneType == 3 || invokeIsUriNumber(phoneNumber)) {
            return phoneNumber.substring(0, indexOfLastNetworkChar(phoneNumber) + 1).trim();
        }
        return invokeExtractNetworkPortionAlt(phoneNumber);
    }

    private static String invokeExtractNetworkPortionAlt(String phoneNumber) {
        try {
            Method method = android.telephony.PhoneNumberUtils.class.getDeclaredMethod("extractNetworkPortionAlt", new Class[]{String.class});
            method.setAccessible(true);
            return (String) method.invoke((Object) null, new Object[]{phoneNumber});
        } catch (Exception e) {
            Log.w(LOG_TAG, "invoke extractNetworkPortionAlt failed", e);
            return null;
        }
    }

    public static boolean isDialable(String address) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }
        int count = address.length();
        for (int i = 0; i < count; i++) {
            if (!isDialable(address.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isChinaMobileNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) {
            return false;
        }
        String number = stripSeparators(phoneNumber);
        if (number.length() >= CHINA_COUNTRY_CODE.length() + 11) {
            return number.substring((number.length() - 11) - CHINA_COUNTRY_CODE.length()).startsWith("861");
        }
        if (number.length() >= 11) {
            return number.substring(number.length() - 11).startsWith("1");
        }
        return false;
    }

    public static String[] splitNetworkAndPostDialPortion(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        int networkEnd = indexOfLastNetworkChar(phoneNumber) + 1;
        String[] ret = new String[2];
        ret[0] = phoneNumber.substring(0, networkEnd);
        ret[1] = networkEnd == phoneNumber.length() ? Prefix.EMPTY : phoneNumber.substring(networkEnd);
        return ret;
    }

    public static boolean isServiceNumber(String phoneNumber) {
        PhoneNumber pn = PhoneNumber.parse(phoneNumber);
        return pn != null && pn.isServiceNumber();
    }

    public static boolean isChineseOperator(String operator) {
        return !TextUtils.isEmpty(operator) && operator.startsWith("460");
    }

    public static class PhoneNumber {
        private static final String EMPTY = "";
        private static final char HASH_STRING_INDICATOR = '\u0001';
        private static final int MAX_NUMBER_LENGTH = 256;
        private static final char MISSING_AREA_CODE_INDICATOR = '\u0002';
        private static final int POOL_SIZE = 10;
        private static final PhoneNumber[] sPool = new PhoneNumber[10];
        private static int sPoolIndex = -1;
        private String mAreaCode;
        private StringBuffer mBuffer = new StringBuffer(256);
        private String mCountryCode;
        private String mDefaultCountryCode;
        private String mEffectiveNumber;
        private int mEffectiveNumberStart;
        private boolean mIsChinaEnvironment;
        private String mNetIddCode;
        private CharSequence mOriginal;
        private String mPostDialString;
        private int mPostDialStringStart;
        private String mPrefix;

        private PhoneNumber() {
            clear();
        }

        private void clear() {
            this.mBuffer.setLength(0);
            this.mPrefix = null;
            this.mCountryCode = null;
            this.mAreaCode = null;
            this.mEffectiveNumberStart = 0;
            this.mEffectiveNumber = null;
            this.mPostDialStringStart = 0;
            this.mPostDialString = null;
            this.mIsChinaEnvironment = false;
            this.mNetIddCode = null;
        }

        private void attach(CharSequence number) {
            if (number == null) {
                number = "";
            }
            this.mOriginal = number;
            boolean postDialString = false;
            int len = number.length();
            for (int i = 0; i < len; i++) {
                char c = number.charAt(i);
                if (postDialString && PhoneNumberUtils.isNonSeparator(c)) {
                    this.mBuffer.append(c);
                } else if (i == 0 && c == '+') {
                    this.mBuffer.append(c);
                } else if (c >= '0' && c <= '9') {
                    this.mBuffer.append(c);
                } else if (!postDialString && PhoneNumberUtils.isStartsPostDial(c)) {
                    this.mPostDialStringStart = this.mBuffer.length();
                    postDialString = true;
                    this.mBuffer.append(c);
                }
            }
            if (!postDialString) {
                this.mPostDialStringStart = this.mBuffer.length();
            }
        }

        public static PhoneNumber parse(CharSequence number) {
            return parse(number, CountryCode.isChinaEnvironment(), (String) null);
        }

        public static PhoneNumber parse(CharSequence number, boolean isChinaEnvironment) {
            return parse(number, isChinaEnvironment, (String) null);
        }

        public static PhoneNumber parse(CharSequence number, boolean isChinaEnvironment, String iddCode) {
            PhoneNumber pn;
            synchronized (sPool) {
                if (sPoolIndex == -1) {
                    pn = new PhoneNumber();
                } else {
                    pn = sPool[sPoolIndex];
                    PhoneNumber[] phoneNumberArr = sPool;
                    int i = sPoolIndex;
                    sPoolIndex = i - 1;
                    phoneNumberArr[i] = null;
                }
            }
            pn.attach(number);
            pn.mIsChinaEnvironment = isChinaEnvironment;
            pn.mNetIddCode = iddCode;
            return pn;
        }

        public void recycle() {
            clear();
            synchronized (sPool) {
                if (sPoolIndex < sPool.length) {
                    PhoneNumber[] phoneNumberArr = sPool;
                    int i = sPoolIndex + 1;
                    sPoolIndex = i;
                    phoneNumberArr[i] = this;
                }
            }
        }

        public void setDefaultCountryCode(String defaultCountryCode) {
            this.mDefaultCountryCode = defaultCountryCode;
        }

        public String getPrefix() {
            if (this.mPrefix == null && this.mIsChinaEnvironment) {
                StringBuffer stringBuffer = this.mBuffer;
                int i = this.mEffectiveNumberStart;
                this.mPrefix = Prefix.parse(stringBuffer, i, this.mPostDialStringStart - i);
                this.mEffectiveNumberStart += this.mPrefix.length();
            }
            return this.mPrefix;
        }

        public String getCountryCode() {
            if (this.mCountryCode == null) {
                getPrefix();
                Iterator<String> it = CountryCode.getIddCodes().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    String iddCode = it.next();
                    String idd = CountryCode.GSM_GENERAL_IDD_CODE;
                    if (!areEqual(this.mBuffer, this.mEffectiveNumberStart, idd, 0, idd.length())) {
                        if (TextUtils.isEmpty(this.mNetIddCode)) {
                            idd = iddCode;
                        } else {
                            idd = this.mNetIddCode;
                        }
                        if (!areEqual(this.mBuffer, this.mEffectiveNumberStart, idd, 0, idd.length())) {
                            idd = null;
                        }
                    }
                    if (idd != null) {
                        this.mEffectiveNumberStart += idd.length();
                        StringBuffer stringBuffer = this.mBuffer;
                        int i = this.mEffectiveNumberStart;
                        this.mCountryCode = CountryCodeConverter.parse(stringBuffer, i, this.mPostDialStringStart - i);
                        if (this.mCountryCode.length() != 0) {
                            this.mEffectiveNumberStart += this.mCountryCode.length();
                            break;
                        }
                        this.mEffectiveNumberStart -= idd.length();
                    } else {
                        this.mCountryCode = "";
                    }
                }
            }
            return this.mCountryCode;
        }

        public boolean isChineseNumber() {
            String countryCode = getCountryCode();
            if (!TextUtils.isEmpty(countryCode)) {
                return PhoneNumberUtils.CHINA_COUNTRY_CODE.equals(countryCode);
            }
            return this.mIsChinaEnvironment || PhoneNumberUtils.CHINA_COUNTRY_CODE.equals(this.mDefaultCountryCode);
        }

        public String getAreaCode() {
            if (this.mAreaCode == null) {
                this.mAreaCode = "";
                if (isChineseNumber() && !Prefix.isSmsPrefix(getPrefix())) {
                    boolean areacodeExists = true;
                    String countryCode = getCountryCode();
                    if (TextUtils.isEmpty(countryCode)) {
                        areacodeExists = false;
                        int length = this.mBuffer.length() - 1;
                        int i = this.mEffectiveNumberStart;
                        if (length > i && this.mBuffer.charAt(i) == '0') {
                            areacodeExists = true;
                            this.mEffectiveNumberStart++;
                        }
                    }
                    if (areacodeExists) {
                        ChineseTelocation instance = ChineseTelocation.getInstance();
                        StringBuffer stringBuffer = this.mBuffer;
                        int i2 = this.mEffectiveNumberStart;
                        this.mAreaCode = instance.parseAreaCode(stringBuffer, i2, this.mPostDialStringStart - i2);
                        if (this.mAreaCode.length() != 0 || !TextUtils.isEmpty(countryCode)) {
                            this.mEffectiveNumberStart += this.mAreaCode.length();
                        } else {
                            this.mEffectiveNumberStart--;
                        }
                    }
                }
            }
            return this.mAreaCode;
        }

        public String getEffectiveNumber() {
            if (this.mEffectiveNumber == null) {
                getAreaCode();
                int length = this.mBuffer.length();
                int i = this.mEffectiveNumberStart;
                if (length > i) {
                    this.mEffectiveNumber = this.mBuffer.substring(i, this.mPostDialStringStart);
                } else {
                    this.mEffectiveNumber = "";
                }
            }
            if (!TextUtils.isEmpty(this.mEffectiveNumber)) {
                return this.mEffectiveNumber;
            }
            String charSequence = this.mOriginal.toString();
            this.mOriginal = charSequence;
            return charSequence.toString();
        }

        public String getFakeNumberToQueryLocation() {
            String effectiveNumber = getEffectiveNumber();
            if (!TextUtils.isEmpty(getAreaCode()) || !effectiveNumber.startsWith("1")) {
                return this.mOriginal.toString();
            }
            int effectiveNumberlength = effectiveNumber.length();
            int minLength = 7;
            int fakeNumberLength = 11;
            if (effectiveNumber.startsWith("141") || effectiveNumber.startsWith("1064")) {
                minLength = 9;
                fakeNumberLength = 13;
            }
            if (effectiveNumberlength < minLength || effectiveNumberlength >= fakeNumberLength) {
                return this.mOriginal.toString();
            }
            StringBuilder sb = new StringBuilder(this.mOriginal);
            for (int i = effectiveNumberlength; i < fakeNumberLength; i++) {
                sb.append('9');
            }
            return sb.toString();
        }

        public String getPostDialString() {
            if (this.mPostDialString == null) {
                int length = this.mBuffer.length();
                int i = this.mPostDialStringStart;
                if (length > i) {
                    this.mPostDialString = this.mBuffer.substring(i);
                } else {
                    this.mPostDialString = "";
                }
            }
            return this.mPostDialString;
        }

        public boolean isNormalMobileNumber() {
            getAreaCode();
            if (isChineseNumber()) {
                int i = this.mPostDialStringStart;
                int i2 = this.mEffectiveNumberStart;
                int length = i - i2;
                if (length == 11) {
                    if (this.mBuffer.charAt(i2) == '1') {
                        switch (this.mBuffer.charAt(this.mEffectiveNumberStart + 1)) {
                            case '3':
                                if (this.mBuffer.charAt(this.mEffectiveNumberStart + 2) == '8' && this.mBuffer.charAt(this.mEffectiveNumberStart + 3) == '0' && this.mBuffer.charAt(this.mEffectiveNumberStart + 4) == '0' && this.mBuffer.charAt(this.mEffectiveNumberStart + 5) == '1' && this.mBuffer.charAt(this.mEffectiveNumberStart + 6) == '3' && this.mBuffer.charAt(this.mEffectiveNumberStart + 7) == '8' && this.mBuffer.charAt(this.mEffectiveNumberStart + 8) == '0' && this.mBuffer.charAt(this.mEffectiveNumberStart + 9) == '0' && this.mBuffer.charAt(this.mEffectiveNumberStart + 10) == '0') {
                                    return false;
                                }
                                return true;
                            case '4':
                            case '5':
                            case '6':
                            case '8':
                            case '9':
                                return true;
                            case '7':
                                if (this.mBuffer.charAt(this.mEffectiveNumberStart + 2) != '9') {
                                    return true;
                                }
                                return false;
                            default:
                                return false;
                        }
                    }
                } else if (length == 13 && this.mBuffer.charAt(i2) == '1') {
                    char c = this.mBuffer.charAt(this.mEffectiveNumberStart + 1);
                    if (c != '0') {
                        if (c == '4' && this.mBuffer.charAt(this.mEffectiveNumberStart + 2) == '1') {
                            return true;
                        }
                        return false;
                    } else if (this.mBuffer.charAt(this.mEffectiveNumberStart + 2) == '6' && this.mBuffer.charAt(this.mEffectiveNumberStart + 3) == '4') {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:3:0x000b, code lost:
            r0 = r10.mPostDialStringStart;
            r2 = r10.mEffectiveNumberStart;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isServiceNumber() {
            /*
                r10 = this;
                r10.getAreaCode()
                boolean r0 = r10.isChineseNumber()
                r1 = 0
                if (r0 != 0) goto L_0x000b
                return r1
            L_0x000b:
                int r0 = r10.mPostDialStringStart
                int r2 = r10.mEffectiveNumberStart
                int r0 = r0 - r2
                r3 = 2
                if (r0 <= r3) goto L_0x0094
                java.lang.StringBuffer r4 = r10.mBuffer
                char r2 = r4.charAt(r2)
                java.lang.StringBuffer r4 = r10.mBuffer
                int r5 = r10.mEffectiveNumberStart
                r6 = 1
                int r5 = r5 + r6
                char r4 = r4.charAt(r5)
                java.lang.StringBuffer r5 = r10.mBuffer
                int r7 = r10.mEffectiveNumberStart
                int r7 = r7 + r3
                char r3 = r5.charAt(r7)
                r5 = 50
                r7 = 49
                r8 = 48
                if (r2 != r7) goto L_0x003b
                if (r4 == r8) goto L_0x003a
                if (r4 == r7) goto L_0x003a
                if (r4 != r5) goto L_0x003b
            L_0x003a:
                return r6
            L_0x003b:
                if (r2 == r5) goto L_0x008b
                r5 = 51
                if (r2 == r5) goto L_0x008b
                r5 = 53
                if (r2 == r5) goto L_0x008b
                r7 = 54
                if (r2 == r7) goto L_0x008b
                r9 = 55
                if (r2 != r9) goto L_0x004e
                goto L_0x008b
            L_0x004e:
                r9 = 5
                if (r0 == r9) goto L_0x0081
                r5 = 10
                if (r0 == r5) goto L_0x006f
                r5 = 11
                if (r0 == r5) goto L_0x005a
                goto L_0x0094
            L_0x005a:
                java.lang.String r5 = r10.getEffectiveNumber()
                java.lang.String r7 = " "
                java.lang.String r8 = ""
                java.lang.String r5 = r5.replaceAll(r7, r8)
                java.lang.String r7 = "13800138000"
                boolean r7 = r7.equals(r5)
                if (r7 == 0) goto L_0x0094
                return r6
            L_0x006f:
                r5 = 52
                if (r2 == r5) goto L_0x0077
                r5 = 56
                if (r2 != r5) goto L_0x0094
            L_0x0077:
                int r5 = r10.mEffectiveNumberStart
                if (r5 != 0) goto L_0x0080
                if (r4 != r8) goto L_0x0080
                if (r3 != r8) goto L_0x0080
                r1 = r6
            L_0x0080:
                return r1
            L_0x0081:
                r8 = 57
                if (r2 != r8) goto L_0x0094
                if (r4 == r5) goto L_0x0089
                if (r4 != r7) goto L_0x008a
            L_0x0089:
                r1 = r6
            L_0x008a:
                return r1
            L_0x008b:
                if (r4 != r8) goto L_0x0093
                if (r3 != r8) goto L_0x0093
                r5 = 7
                if (r0 <= r5) goto L_0x0093
                r1 = r6
            L_0x0093:
                return r1
            L_0x0094:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: miui.telephony.PhoneNumberUtils.PhoneNumber.isServiceNumber():boolean");
        }

        public String getNumberWithoutPrefix(boolean withPostDialString) {
            int start = 0;
            if (!TextUtils.isEmpty(getPrefix())) {
                start = getPrefix().length();
            }
            StringBuffer stringBuffer = this.mBuffer;
            return withPostDialString ? stringBuffer.substring(start) : stringBuffer.substring(start, this.mPostDialStringStart);
        }

        public String getNormalizedNumber(boolean withCountryCode, boolean withPostDialString) {
            if (!isChineseNumber()) {
                int start = this.mEffectiveNumberStart;
                if (withCountryCode) {
                    start -= getCountryCode().length();
                }
                String number = this.mBuffer.substring(start, withPostDialString ? this.mBuffer.length() : this.mPostDialStringStart);
                if (!withCountryCode || getCountryCode().length() <= 0) {
                    return number;
                }
                return CountryCode.GSM_GENERAL_IDD_CODE + number;
            } else if (isNormalMobileNumber()) {
                String number2 = this.mBuffer.substring(this.mEffectiveNumberStart, withPostDialString ? this.mBuffer.length() : this.mPostDialStringStart);
                if (!withCountryCode) {
                    return number2;
                }
                return PhoneNumberUtils.CHINA_REGION_CODE1 + number2;
            } else {
                int end = withPostDialString ? this.mBuffer.length() : this.mPostDialStringStart;
                if (TextUtils.isEmpty(getAreaCode()) || isServiceNumber()) {
                    return this.mBuffer.substring(this.mEffectiveNumberStart, end);
                }
                String number3 = this.mBuffer.substring(this.mEffectiveNumberStart - getAreaCode().length(), end);
                if (withCountryCode) {
                    return PhoneNumberUtils.CHINA_REGION_CODE1 + number3;
                }
                return "0" + number3;
            }
        }

        public String getLocation(Context context) {
            Locale locale = context.getResources().getConfiguration().locale;
            if (!locale.getLanguage().equals(Locale.CHINA.getLanguage()) || !isChineseNumber()) {
                return ChineseTelocation.getInstance().getExternalLocation(context, getCountryCode(), this.mOriginal, locale);
            }
            ChineseTelocation instance = ChineseTelocation.getInstance();
            StringBuffer stringBuffer = this.mBuffer;
            int i = this.mEffectiveNumberStart;
            return instance.getLocation(context, stringBuffer, i, this.mPostDialStringStart - i, isNormalMobileNumber() || getAreaCode().length() > 0);
        }

        public String getOperator(Context context) {
            if (!context.getResources().getConfiguration().locale.getLanguage().equals(Locale.CHINA.getLanguage()) || !isChineseNumber()) {
                return "";
            }
            ChineseTelocation instance = ChineseTelocation.getInstance();
            String stringBuffer = this.mBuffer.toString();
            int i = this.mEffectiveNumberStart;
            return instance.getOperator(context, stringBuffer, i, this.mPostDialStringStart - i, isNormalMobileNumber());
        }

        public String getLocationAreaCode(Context context) {
            if (!isChineseNumber()) {
                return "";
            }
            if (!isNormalMobileNumber()) {
                return getAreaCode();
            }
            ChineseTelocation instance = ChineseTelocation.getInstance();
            StringBuffer stringBuffer = this.mBuffer;
            int i = this.mEffectiveNumberStart;
            return instance.getAreaCode(stringBuffer, i, this.mPostDialStringStart - i);
        }

        public boolean isSmsPrefix() {
            return Prefix.isSmsPrefix(getPrefix());
        }

        public static String addCountryCode(String number) {
            if (TextUtils.isEmpty(number)) {
                return number;
            }
            PhoneNumber pn = parse(number);
            boolean addCountryCode = TextUtils.isEmpty(pn.getCountryCode()) && !PhoneNumberUtils.isEmergencyNumber(number);
            if (addCountryCode && pn.isChineseNumber()) {
                if (!TextUtils.isEmpty(pn.getPrefix())) {
                    addCountryCode = false;
                } else if (pn.isServiceNumber()) {
                    addCountryCode = false;
                } else if (!pn.isNormalMobileNumber()) {
                    addCountryCode = !TextUtils.isEmpty(pn.getAreaCode());
                }
            }
            if ((number.startsWith("*") || number.startsWith("#")) && number.endsWith("#")) {
                addCountryCode = false;
            }
            String result = number;
            if (addCountryCode) {
                String countryCode = CountryCode.getUserDefinedCountryCode();
                if (TextUtils.isEmpty(countryCode)) {
                    countryCode = CountryCode.getIccCountryCode();
                }
                if (!TextUtils.isEmpty(countryCode)) {
                    if ("39".equals(countryCode) || number.charAt(0) != '0') {
                        result = CountryCode.GSM_GENERAL_IDD_CODE + countryCode + number;
                    } else {
                        result = CountryCode.GSM_GENERAL_IDD_CODE + countryCode + number.substring(1);
                    }
                }
            }
            pn.recycle();
            return result;
        }

        public static String getHashString(String dialable) {
            String effectiveNumber;
            String result;
            PhoneNumber pn = parse(dialable);
            String str = dialable;
            if (pn.isSmsPrefix()) {
                effectiveNumber = pn.getPrefix() + pn.getEffectiveNumber();
            } else {
                effectiveNumber = pn.getEffectiveNumber();
            }
            if (!pn.isChineseNumber()) {
                result = String.format("%c(00%s)%s%s", new Object[]{Character.valueOf(HASH_STRING_INDICATOR), pn.getCountryCode(), effectiveNumber, pn.getPostDialString()});
            } else if (pn.isNormalMobileNumber()) {
                result = String.format("%c(00%s)%s%s", new Object[]{Character.valueOf(HASH_STRING_INDICATOR), PhoneNumberUtils.CHINA_COUNTRY_CODE, effectiveNumber, pn.getPostDialString()});
            } else if (!TextUtils.isEmpty(pn.getCountryCode())) {
                if (!TextUtils.isEmpty(pn.getAreaCode())) {
                    result = String.format("%c(00%s)%s-%s%s", new Object[]{Character.valueOf(HASH_STRING_INDICATOR), PhoneNumberUtils.CHINA_COUNTRY_CODE, pn.getAreaCode(), effectiveNumber, pn.getPostDialString()});
                } else {
                    result = String.format("%c(00%s)%s%s", new Object[]{Character.valueOf(HASH_STRING_INDICATOR), PhoneNumberUtils.CHINA_COUNTRY_CODE, effectiveNumber, pn.getPostDialString()});
                }
            } else if (!TextUtils.isEmpty(pn.getAreaCode())) {
                result = String.format("%c(00%s)%s-%s%s", new Object[]{Character.valueOf(HASH_STRING_INDICATOR), PhoneNumberUtils.CHINA_COUNTRY_CODE, pn.getAreaCode(), effectiveNumber, pn.getPostDialString()});
            } else {
                result = String.format("%c(00%s)%c%s%s", new Object[]{Character.valueOf(HASH_STRING_INDICATOR), PhoneNumberUtils.CHINA_COUNTRY_CODE, Character.valueOf(MISSING_AREA_CODE_INDICATOR), effectiveNumber, pn.getPostDialString()});
            }
            pn.recycle();
            return result;
        }

        public static String getDialableNumber(String hash) {
            int index;
            if (TextUtils.isEmpty(hash)) {
                return "";
            }
            int index2 = hash.indexOf(2);
            if (index2 < 0) {
                index = 1;
            } else {
                index = index2 + 1;
            }
            return hash.charAt(0) == 1 ? hash.substring(index) : hash;
        }

        public static String replaceCdmaInternationalAccessCode(String number) {
            if (number.startsWith(PhoneNumberUtils.CHINA_REGION_CODE1) && PhoneNumberUtils.CHINA_COUNTRY_CODE.equals(CountryCode.getNetworkCountryCode())) {
                String leftNumber = number.substring(3);
                if (PhoneNumberUtils.isChinaMobileNumber(leftNumber) || leftNumber.charAt(0) == '0') {
                    return leftNumber;
                }
                return '0' + leftNumber;
            } else if (TextUtils.isEmpty(number) || number.charAt(0) != '+') {
                return number;
            } else {
                List<String> iddCodes = CountryCode.getIddCodes();
                return iddCodes.get(0) + number.substring(1);
            }
        }

        public static String getLocation(Context context, CharSequence number) {
            PhoneNumber pn = parse(number);
            String location = pn.getLocation(context);
            pn.recycle();
            return location;
        }

        public static String getOperator(Context context, CharSequence number) {
            PhoneNumber pn = parse(number);
            String operator = pn.getOperator(context);
            pn.recycle();
            return operator;
        }

        public static String getLocationAreaCode(Context context, String number) {
            PhoneNumber pn = parse(number);
            String location = pn.getLocationAreaCode(context);
            pn.recycle();
            return location;
        }

        public static boolean isValidCountryCode(String countryCode) {
            return CountryCodeConverter.isValidCountryCode(countryCode);
        }

        public static String getDefaultCountryCode() {
            return CountryCode.getIccCountryCode();
        }

        public static boolean isChineseOperator() {
            return CountryCode.isChinaEnvironment();
        }

        private static boolean areEqual(CharSequence s1, int b1, CharSequence s2, int b2, int length) {
            if (s1 == null || s2 == null || b1 < 0 || b2 < 0 || length < 0 || s1.length() < b1 + length || s2.length() < b2 + length) {
                return false;
            }
            for (int i = 0; i < length; i++) {
                if (s1.charAt(b1 + i) != s2.charAt(b2 + i)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class TelocationAsyncQueryHandler extends AsyncQueryHandler {
        private static final int EVENT_QUERY_OPERATOR = 20;
        private static final int EVENT_QUERY_TELOCATION = 10;
        private static final int EVENT_QUERY_TELOCATION_AND_OPERATOR = 30;
        private Handler mWorkerHandler;

        private static class SingletonHolder {
            /* access modifiers changed from: private */
            public static final TelocationAsyncQueryHandler INSTANCE = new TelocationAsyncQueryHandler();

            private SingletonHolder() {
            }
        }

        public static TelocationAsyncQueryHandler getInstance() {
            return SingletonHolder.INSTANCE;
        }

        public static String queryTelocation(Context context, CharSequence phoneNumber) {
            return PhoneNumber.getLocation(context, phoneNumber);
        }

        public static String queryOperator(Context context, CharSequence phoneNumber) {
            return PhoneNumber.getOperator(context, phoneNumber);
        }

        protected static final class TelocationWorkerArgs {
            public Context context;
            public Object cookie1;
            public Object cookie2;
            public Object cookie3;
            public Object cookie4;
            public Handler handler;
            public String location;
            public String operator;
            public OperatorQueryListener operatorQueryListener;
            public String phoneNumber;
            public TelocationAndOperatorQueryListener telocationAndOperatorQueryListener;
            public TelocationQueryListener telocationQueryListener;

            protected TelocationWorkerArgs() {
            }
        }

        protected class TelocationWorkerHandler extends AsyncQueryHandler.WorkerHandler {
            public TelocationWorkerHandler(Looper looper) {
                super(TelocationAsyncQueryHandler.this, looper);
            }

            public void handleMessage(Message msg) {
                TelocationWorkerArgs args = (TelocationWorkerArgs) msg.obj;
                if (msg.arg1 == 10 || msg.arg1 == 30) {
                    args.location = TelocationAsyncQueryHandler.queryTelocation(args.context, args.phoneNumber);
                }
                if (msg.arg1 == 20 || msg.arg1 == 30) {
                    args.operator = TelocationAsyncQueryHandler.queryOperator(args.context, args.phoneNumber);
                }
                Message reply = args.handler.obtainMessage(msg.what);
                reply.arg1 = msg.arg1;
                reply.obj = msg.obj;
                reply.sendToTarget();
            }
        }

        private TelocationAsyncQueryHandler() {
            super((ContentResolver) null);
        }

        /* JADX WARNING: type inference failed for: r0v2, types: [android.os.Handler, miui.telephony.PhoneNumberUtils$TelocationAsyncQueryHandler$TelocationWorkerHandler] */
        /* access modifiers changed from: protected */
        public Handler createHandler(Looper looper) {
            if (this.mWorkerHandler == null) {
                this.mWorkerHandler = new TelocationWorkerHandler(looper);
            }
            return this.mWorkerHandler;
        }

        public void startQueryTelocationString(int token, Object cookie1, Object cookie2, Object cookie3, Object cookie4, TelocationQueryListener listener, Context context, String phoneNumber) {
            TelocationWorkerArgs args = new TelocationWorkerArgs();
            args.telocationQueryListener = listener;
            sendMsg(args, 10, token, cookie1, cookie2, cookie3, cookie4, context, phoneNumber);
        }

        public void startQueryOperatorString(int token, Object cookie1, Object cookie2, Object cookie3, Object cookie4, OperatorQueryListener listener, Context context, String phoneNumber) {
            TelocationWorkerArgs args = new TelocationWorkerArgs();
            args.operatorQueryListener = listener;
            sendMsg(args, 20, token, cookie1, cookie2, cookie3, cookie4, context, phoneNumber);
        }

        public void startQueryTelocationAndOperatorString(int token, Object cookie1, Object cookie2, Object cookie3, Object cookie4, TelocationAndOperatorQueryListener listener, Context context, String phoneNumber) {
            TelocationWorkerArgs args = new TelocationWorkerArgs();
            args.telocationAndOperatorQueryListener = listener;
            sendMsg(args, 30, token, cookie1, cookie2, cookie3, cookie4, context, phoneNumber);
        }

        private void sendMsg(TelocationWorkerArgs args, int arg1, int token, Object cookie1, Object cookie2, Object cookie3, Object cookie4, Context context, String phoneNumber) {
            args.handler = this;
            args.context = context;
            args.phoneNumber = phoneNumber;
            args.cookie1 = cookie1;
            args.cookie2 = cookie2;
            args.cookie3 = cookie3;
            args.cookie4 = cookie4;
            args.location = null;
            Message msg = this.mWorkerHandler.obtainMessage(token);
            msg.arg1 = arg1;
            msg.obj = args;
            msg.sendToTarget();
        }

        public void handleMessage(Message msg) {
            TelocationWorkerArgs args = (TelocationWorkerArgs) msg.obj;
            if (msg.arg1 == 10 && args.telocationQueryListener != null) {
                args.telocationQueryListener.onComplete(args.cookie1, args.cookie2, args.cookie3, args.cookie4, args.location);
            } else if (msg.arg1 == 20 && args.operatorQueryListener != null) {
                args.operatorQueryListener.onComplete(args.cookie1, args.cookie2, args.cookie3, args.cookie4, args.operator);
            } else if (msg.arg1 == 30 && args.telocationAndOperatorQueryListener != null) {
                args.telocationAndOperatorQueryListener.onComplete(args.cookie1, args.cookie2, args.cookie3, args.cookie4, args.location, args.operator);
            }
        }
    }

    public static void queryTelocationStringAsync(int token, Object cookie1, Object cookie2, Object cookie3, Object cookie4, TelocationQueryListener listener, Context context, String phoneNumber) {
        if (ChineseTelocation.isTelocationEnable(context.getContentResolver())) {
            TelocationAsyncQueryHandler.getInstance().startQueryTelocationString(token, cookie1, cookie2, cookie3, cookie4, listener, context, phoneNumber);
        } else {
            listener.onComplete(cookie1, cookie2, cookie3, cookie4, (String) null);
        }
    }

    public static void queryOperatorStringAsync(int token, Object cookie1, Object cookie2, Object cookie3, Object cookie4, OperatorQueryListener listener, Context context, String phoneNumber) {
        if (ChineseTelocation.isTelocationEnable(context.getContentResolver())) {
            TelocationAsyncQueryHandler.getInstance().startQueryOperatorString(token, cookie1, cookie2, cookie3, cookie4, listener, context, phoneNumber);
        } else {
            listener.onComplete(cookie1, cookie2, cookie3, cookie4, (String) null);
        }
    }

    public static void queryTelocationAndOperatorStringAsync(int token, Object cookie1, Object cookie2, Object cookie3, Object cookie4, TelocationAndOperatorQueryListener listener, Context context, String phoneNumber) {
        if (ChineseTelocation.isTelocationEnable(context.getContentResolver())) {
            TelocationAsyncQueryHandler.getInstance().startQueryTelocationAndOperatorString(token, cookie1, cookie2, cookie3, cookie4, listener, context, phoneNumber);
        } else {
            listener.onComplete(cookie1, cookie2, cookie3, cookie4, (String) null, (String) null);
        }
    }

    public static void queryTelocationStringAsync(int token, Object cookie1, Object cookie2, Object cookie3, Object cookie4, TelocationQueryListener listener, Context context, String phoneNumber, boolean enableTelocation) {
        if (enableTelocation) {
            TelocationAsyncQueryHandler.getInstance().startQueryTelocationString(token, cookie1, cookie2, cookie3, cookie4, listener, context, phoneNumber);
        } else {
            listener.onComplete(cookie1, cookie2, cookie3, cookie4, (String) null);
        }
    }

    public static void queryOperatorStringAsync(int token, Object cookie1, Object cookie2, Object cookie3, Object cookie4, OperatorQueryListener listener, Context context, String phoneNumber, boolean enableTelocation) {
        if (enableTelocation) {
            TelocationAsyncQueryHandler.getInstance().startQueryOperatorString(token, cookie1, cookie2, cookie3, cookie4, listener, context, phoneNumber);
        } else {
            listener.onComplete(cookie1, cookie2, cookie3, cookie4, (String) null);
        }
    }

    public static void queryTelocationAndOperatorStringAsync(int token, Object cookie1, Object cookie2, Object cookie3, Object cookie4, TelocationAndOperatorQueryListener listener, Context context, String phoneNumber, boolean enableTelocation) {
        if (enableTelocation) {
            TelocationAsyncQueryHandler.getInstance().startQueryTelocationAndOperatorString(token, cookie1, cookie2, cookie3, cookie4, listener, context, phoneNumber);
        } else {
            listener.onComplete(cookie1, cookie2, cookie3, cookie4, (String) null, (String) null);
        }
    }

    public static void cancelAsyncTelocationQuery(int token) {
        TelocationAsyncQueryHandler.getInstance().cancelOperation(token);
    }

    public static String parseTelocationString(Context context, CharSequence phoneNumber) {
        return TelocationAsyncQueryHandler.queryTelocation(context, phoneNumber);
    }

    public static String getDefaultIpBySim(Context context) {
        return getDefaultIpBySim(context, SubscriptionManager.getDefault().getDefaultSlotId());
    }

    public static String getDefaultIpBySim(Context context, int slotId) {
        TelephonyManager telephonyManager = TelephonyManager.getDefault();
        String simOperator = telephonyManager.getSimOperatorForSlot(slotId);
        if (telephonyManager.isSameOperator(simOperator, TelephonyManager.OPERATOR_NUMERIC_CHINA_MOBILE)) {
            return Prefix.PREFIX_17951;
        }
        if (telephonyManager.isSameOperator(simOperator, TelephonyManager.OPERATOR_NUMERIC_CHINA_UNICOM)) {
            return Prefix.PREFIX_17911;
        }
        if (telephonyManager.isSameOperator(simOperator, TelephonyManager.OPERATOR_NUMERIC_CHINA_TELECOM)) {
            return Prefix.PREFIX_17901;
        }
        return Prefix.EMPTY;
    }

    public static String removeDashesAndBlanks(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < phoneNumber.length(); i++) {
            char c = phoneNumber.charAt(i);
            if (!(c == ' ' || c == '-')) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Deprecated
    public static boolean isMiuiEmergencyNumber(String number, boolean useExactMatch) {
        if (number == null) {
            return false;
        }
        for (String emergencyNum : EMERGENCY_NUMBERS) {
            if (useExactMatch) {
                if (emergencyNum.equals(number)) {
                    return true;
                }
            } else if (number.startsWith(emergencyNum)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmergencyNumber(String number) {
        if (Build.VERSION.SDK_INT < 21 || !miui.os.Build.IS_MIUI) {
            return isMiuiEmergencyNumber(number, true) || android.telephony.PhoneNumberUtils.isEmergencyNumber(number);
        }
        return invokeIsLocalEmergencyNumber(AppConstants.getCurrentApplication(), number);
    }

    private static boolean invokeIsLocalEmergencyNumber(Context context, String number) {
        try {
            Method method = Class.forName("miui.telephony.TelephonyManagerEx").getDeclaredMethod("isLocalEmergencyNumber", new Class[]{Context.class, String.class});
            method.setAccessible(true);
            return ((Boolean) method.invoke(number, new Object[]{context, number})).booleanValue();
        } catch (Exception e) {
            Log.w(LOG_TAG, "invokeIsLocalEmergencyNumber failed", e);
            return false;
        }
    }

    public static String parseNumber(String number) {
        PhoneNumber pn;
        if (TelephonyManager.getDefault().getSimState() != 5 || (pn = PhoneNumber.parse(number)) == null) {
            return number;
        }
        return pn.getEffectiveNumber();
    }

    public static String maskPhoneNumber(String phoneNumber, int cutMode) {
        int cutLength;
        int cutStart;
        if (phoneNumber == null) {
            return Prefix.EMPTY;
        }
        int alnumCount = 0;
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (isAlnum(phoneNumber.charAt(i))) {
                alnumCount++;
            }
        }
        if (alnumCount < 7) {
            return new String(phoneNumber);
        }
        if (alnumCount < 11) {
            cutLength = 2;
        } else {
            cutLength = 3;
        }
        if (cutMode == 0) {
            cutStart = 0;
        } else if (cutMode == 1) {
            cutStart = alnumCount - cutLength;
        } else if (cutMode == 2) {
            cutStart = (alnumCount - cutLength) / 2;
        } else {
            throw new IllegalArgumentException("Invalid cut mode");
        }
        StringBuilder result = new StringBuilder();
        int addedAlnumCount = 0;
        for (int i2 = 0; i2 < phoneNumber.length(); i2++) {
            if (isAlnum(phoneNumber.charAt(i2))) {
                if (addedAlnumCount < cutStart || cutLength <= 0) {
                    result.append(phoneNumber.charAt(i2));
                } else {
                    result.append('?');
                    cutLength--;
                }
                addedAlnumCount++;
            } else {
                result.append(phoneNumber.charAt(i2));
            }
        }
        return result.toString();
    }

    private static boolean isAlnum(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    public static String miuiFormatNumber(String phoneNumber, String phoneNumberE164, String defaultCountryIso) {
        PhoneNumber pn;
        if (Locale.getDefault().equals(Locale.SIMPLIFIED_CHINESE) && (pn = PhoneNumber.parse(phoneNumber)) != null) {
            String prefix = pn.getPrefix();
            if (!TextUtils.isEmpty(prefix) && phoneNumber.startsWith(prefix)) {
                String formatedNumber = android.telephony.PhoneNumberUtils.formatNumber(phoneNumber.substring(prefix.length()), phoneNumberE164, defaultCountryIso);
                return prefix + " " + formatedNumber;
            }
        }
        return android.telephony.PhoneNumberUtils.formatNumber(phoneNumber, phoneNumberE164, defaultCountryIso);
    }

    public static int getPresentation(CharSequence number) {
        if (TextUtils.isEmpty(number) || TextUtils.equals(number, "-1")) {
            return PhoneConstants.PRESENTATION_UNKNOWN;
        }
        if (TextUtils.equals(number, PRIVATE_NUMBER)) {
            return PhoneConstants.PRESENTATION_RESTRICTED;
        }
        if (TextUtils.equals(number, PAYPHONE_NUMBER)) {
            return PhoneConstants.PRESENTATION_PAYPHONE;
        }
        return PhoneConstants.PRESENTATION_ALLOWED;
    }

    public static String getPresentationString(int presentation) {
        if (presentation == PhoneConstants.PRESENTATION_RESTRICTED) {
            return Resources.getSystem().getString(R.string.presentation_private);
        }
        if (presentation == PhoneConstants.PRESENTATION_PAYPHONE) {
            return Resources.getSystem().getString(R.string.presentation_payphone);
        }
        if (presentation == PhoneConstants.PRESENTATION_UNKNOWN) {
            return Resources.getSystem().getString(R.string.presentation_unknown);
        }
        return Prefix.EMPTY;
    }

    public static String toLogSafePhoneNumber(String number) {
        return toLogSafePhoneNumber(number, 0);
    }

    public static String toLogSafePhoneNumber(String number, int originalCount) {
        int length = number == null ? 0 : number.length();
        if (length == 0) {
            return Prefix.EMPTY;
        }
        StringBuilder builder = new StringBuilder(length);
        int originalIndex = length > originalCount ? length - originalCount : length;
        for (int i = 0; i < length; i++) {
            char c = number.charAt(i);
            if (i >= originalIndex || c == '-' || c == '@' || c == '.') {
                builder.append(c);
            } else {
                builder.append('x');
            }
        }
        return builder.toString();
    }
}

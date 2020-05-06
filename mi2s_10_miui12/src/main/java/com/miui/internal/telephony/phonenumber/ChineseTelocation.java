package com.miui.internal.telephony.phonenumber;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.location.Country;
import android.location.CountryDetector;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import java.util.Locale;
import miui.reflect.Method;
import miui.telephony.phonenumber.ChineseTelocationConverter;
import miui.telephony.phonenumber.CountryCode;

public class ChineseTelocation {
    public static final String COUNTRY_DETECTOR = "country_detector";
    private static final String EMPTY = "";
    private static final String ENABLE_TELOCATION = "enable_telocation";
    private static final String TAG = "ChineseTelocation";
    private static ChineseTelocation sInstance = new ChineseTelocation();
    private boolean mAllowTelocation;
    private Context mContext;
    private ContentObserver mSettingObserver;

    private ChineseTelocation() {
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        if (this.mSettingObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mSettingObserver);
        }
        super.finalize();
    }

    public static ChineseTelocation getInstance() {
        return sInstance;
    }

    /* access modifiers changed from: private */
    public void updateTelocationSetting() {
        this.mAllowTelocation = isTelocationEnable(this.mContext.getContentResolver());
    }

    public static boolean isTelocationEnable(ContentResolver cr) {
        try {
            Class clazz = Class.forName("android.provider.MiuiSettings$Telephony");
            return Method.of(clazz, "isTelocationEnable", Boolean.TYPE, new Class[]{ContentResolver.class}).invokeBoolean(clazz, (Object) null, new Object[]{cr});
        } catch (Exception e) {
            return true;
        }
    }

    private void initObserver(Context context) {
        this.mContext = context.getApplicationContext();
        if (this.mContext == null) {
            this.mContext = context;
        }
        updateTelocationSetting();
        this.mSettingObserver = new ContentObserver(new Handler(this.mContext.getMainLooper())) {
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                Log.d(ChineseTelocation.TAG, "telocation setting changed, reloading ...");
                ChineseTelocation.this.updateTelocationSetting();
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(ENABLE_TELOCATION), false, this.mSettingObserver);
    }

    public String getLocation(Context context, CharSequence cs, int start, int length, boolean withAreaCode) {
        if (this.mContext == null) {
            initObserver(context);
        }
        if (!this.mAllowTelocation) {
            return "";
        }
        int id = -1;
        if (withAreaCode) {
            id = ChineseTelocationConverter.getInstance().getUniqId(cs, start, length, true);
        }
        if (id > 0) {
            return ChineseTelocationConverter.getInstance().getLocation(cs, start, length, withAreaCode);
        }
        return "";
    }

    public String getOperator(Context context, CharSequence cs, int start, int length, boolean isNormalMobileNumber) {
        if (this.mContext == null) {
            initObserver(context);
        }
        if (this.mAllowTelocation && isNormalMobileNumber) {
            return ChineseTelocationConverter.getInstance().getOperator(cs, start, length);
        }
        return "";
    }

    public String getAreaCode(CharSequence cs, int start, int length) {
        return ChineseTelocationConverter.getInstance().getAreaCode(cs, start, length);
    }

    public String parseAreaCode(CharSequence cs, int start, int length) {
        return ChineseTelocationConverter.getInstance().parseAreaCode(cs, start, length);
    }

    public String getExternalLocation(Context context, String countryCode, CharSequence number, Locale locale) {
        if (TextUtils.isEmpty(countryCode)) {
            countryCode = CountryCode.getUserDefinedCountryCode();
            if (TextUtils.isEmpty(countryCode)) {
                countryCode = CountryCode.getIccCountryCode();
            }
        }
        try {
            if (!TextUtils.isEmpty(countryCode)) {
                return PhoneNumberOfflineGeocoder.getInstance().getDescriptionForNumber(PhoneNumberCompat.parse(PhoneNumberUtil.getInstance(), number, getCurrentCountryIso(context)), locale);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public static String getCurrentCountryIso(Context context) {
        String countryIso = null;
        CountryDetector detector = (CountryDetector) context.getSystemService(COUNTRY_DETECTOR);
        if (detector != null) {
            Country country = detector.detectCountry();
            if (country != null) {
                countryIso = country.getCountryIso();
            } else {
                Log.e(TAG, "getCurrentCountryIso CountryDetector.detectCountry() is null.");
            }
        }
        if (countryIso == null) {
            return context.getResources().getConfiguration().locale.getCountry();
        }
        return countryIso;
    }
}

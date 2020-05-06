package com.miui.internal.telephony.phonenumber;

import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;

public class PhoneNumberCompat {
    public static Phonenumber.PhoneNumber parse(PhoneNumberUtil util, CharSequence numberToParse, String defaultRegion) throws NumberParseException, NumberParseException {
        return util.parse(numberToParse, defaultRegion);
    }
}

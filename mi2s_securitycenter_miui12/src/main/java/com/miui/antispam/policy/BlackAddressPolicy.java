package com.miui.antispam.policy;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import b.b.a.a;
import com.miui.activityutil.o;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import com.miui.maml.elements.AdvancedSlider;
import java.util.ArrayList;
import java.util.HashMap;
import miui.provider.ExtraTelephony;
import miui.telephony.PhoneNumberUtils;

public class BlackAddressPolicy extends a {

    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        private static final ArrayList<String> f2351a = new ArrayList<>(4);

        /* renamed from: b  reason: collision with root package name */
        private static final HashMap<String, String> f2352b = new HashMap<>(16);

        static {
            f2351a.add("24");
            f2351a.add("28");
            f2351a.add("29");
            f2351a.add("731");
            f2352b.put("沈阳", "24");
            f2352b.put("铁岭", "247");
            f2352b.put("抚顺", "245");
            f2352b.put("成都", "28");
            f2352b.put("资阳", "282");
            f2352b.put("眉山", "283");
            f2352b.put("西安", "29");
            f2352b.put("咸阳", "293");
            f2352b.put("长沙", "731");
            f2352b.put("株洲", "7312");
            f2352b.put("湘潭", "7315");
        }

        public static String a(Context context, String str, String str2) {
            String location = PhoneNumberUtils.PhoneNumber.getLocation(context, str);
            int length = location == null ? 0 : location.length();
            if (length > 2) {
                location = location.substring(length - 2);
            }
            HashMap<String, String> hashMap = f2352b;
            String str3 = hashMap != null ? hashMap.get(location) : null;
            return str3 == null ? str2 : str3;
        }

        public static boolean a(String str) {
            ArrayList<String> arrayList = f2351a;
            return arrayList != null && arrayList.contains(str);
        }
    }

    public BlackAddressPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    private boolean isAddressInBlack(Context context, String str, int i, int i2) {
        int i3;
        String locationAreaCode = PhoneNumberUtils.PhoneNumber.getLocationAreaCode(context, str);
        if (a.a(locationAreaCode)) {
            locationAreaCode = a.a(this.mContext, str, locationAreaCode);
        }
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ExtraTelephony.Phonelist.CONTENT_URI;
        Cursor query = contentResolver.query(uri, (String[]) null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{"***" + locationAreaCode, o.f2310b, String.valueOf(i2), String.valueOf(1)}, (String) null);
        if (query != null) {
            try {
                if (query.moveToNext() && ((i3 = query.getInt(query.getColumnIndex(AdvancedSlider.STATE))) == 0 || i3 == i)) {
                    query.close();
                    return true;
                }
            } catch (Exception e) {
                Log.e("BlackAddressPolicy", "Cursor exception in isAddressInBlack(): ", e);
            } catch (Throwable th) {
                query.close();
                throw th;
            }
            query.close();
        }
        return false;
    }

    public a.C0035a dbQuery(e eVar) {
        if (!isAddressInBlack(this.mContext, eVar.f2368a, eVar.f2371d, eVar.f2370c)) {
            return null;
        }
        int i = eVar.f2371d;
        return new a.C0035a(this, true, 13);
    }

    public int getType() {
        return a.c.f1311a;
    }

    public a.C0035a handleData(e eVar) {
        if (!this.mJudge.b()) {
            return dbQuery(eVar);
        }
        if (!this.mJudge.a(eVar.f2368a, eVar.f2371d, a.c.f1311a, eVar.f2370c)) {
            return null;
        }
        int i = eVar.f2371d;
        return new a.C0035a(this, true, 13);
    }
}

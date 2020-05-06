package com.miui.optimizemanage.settings;

import androidx.preference.Preference;
import com.miui.powercenter.y;
import com.miui.securitycenter.R;
import miuix.preference.DropDownPreference;

class f implements Preference.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ g f5996a;

    f(g gVar) {
        this.f5996a = gVar;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        String str;
        DropDownPreference g;
        if (preference == this.f5996a.f6000d) {
            c.a(((Boolean) obj).booleanValue());
        } else {
            if (preference == this.f5996a.f5998b) {
                str = (String) obj;
                int[] intArray = this.f5996a.getContext().getResources().getIntArray(R.array.pc_time_choice_items);
                int i = 0;
                for (int i2 = 0; i2 < intArray.length; i2++) {
                    if (this.f5996a.a(intArray[i2]).equals(str)) {
                        i = i2;
                    }
                }
                y.d(intArray[i] * 60);
                g = this.f5996a.f5998b;
            } else if (preference == this.f5996a.f5999c) {
                str = (String) obj;
                int[] intArray2 = this.f5996a.getContext().getResources().getIntArray(R.array.om_occupy_memory_percent);
                int i3 = 0;
                for (int i4 = 0; i4 < intArray2.length; i4++) {
                    if (this.f5996a.b(intArray2[i4]).equals(str)) {
                        i3 = i4;
                    }
                }
                c.c(intArray2[i3]);
                g = this.f5996a.f5999c;
            }
            g.b(str);
        }
        return false;
    }
}

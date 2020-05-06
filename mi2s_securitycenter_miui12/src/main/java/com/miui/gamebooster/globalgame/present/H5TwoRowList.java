package com.miui.gamebooster.globalgame.present;

import android.support.annotation.Keep;
import com.miui.gamebooster.globalgame.global.GlobalCardVH;
import com.miui.gamebooster.globalgame.present.H5OneRowList;
import com.miui.securitycenter.R;

public class H5TwoRowList extends H5OneRowList {
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static int[] f4398b = {R.id.gameItemHub00, R.id.gameItemHub01};

    @Keep
    public static class VH extends H5OneRowList.VH {
        /* access modifiers changed from: protected */
        public int[] getIdArr() {
            return H5TwoRowList.f4398b;
        }

        /* access modifiers changed from: protected */
        public int getLimit() {
            return 8;
        }
    }

    public Class<? extends GlobalCardVH> a() {
        return VH.class;
    }

    public int b() {
        return R.layout.gbg_card_h5_2row_list;
    }

    public int d() {
        return 8;
    }
}

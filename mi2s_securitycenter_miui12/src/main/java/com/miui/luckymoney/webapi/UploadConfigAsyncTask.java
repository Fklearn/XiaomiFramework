package com.miui.luckymoney.webapi;

import android.os.AsyncTask;
import b.b.c.g.a;
import b.b.c.g.c;
import b.b.c.g.f;
import b.b.c.h.j;
import com.miui.activityutil.o;
import com.miui.luckymoney.config.Constants;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.securitycenter.h;
import com.xiaomi.stat.d;
import java.util.ArrayList;

public class UploadConfigAsyncTask extends AsyncTask<Boolean, Void, UploadSettingResult> {
    /* access modifiers changed from: protected */
    public UploadSettingResult doInBackground(Boolean... boolArr) {
        if (!h.i()) {
            return null;
        }
        ArrayList<c> arrayList = new ArrayList<>();
        arrayList.add(new c(d.V, f.a(DeviceUtil.getImeiMd5())));
        boolean booleanValue = boolArr[0].booleanValue();
        String str = o.f2310b;
        arrayList.add(new c("rpStatus", booleanValue ? str : o.f2309a));
        boolean z = true;
        if (!boolArr[1].booleanValue()) {
            str = o.f2309a;
        }
        arrayList.add(new c("rpAlarmStatus", str));
        arrayList.add(new c("sign", f.a(arrayList, Constants.UUID_UPLOAD_CONFIG)));
        StringBuilder sb = new StringBuilder();
        for (c cVar : arrayList) {
            sb.append(!z ? "&" : "?");
            sb.append(cVar.a());
            sb.append("=");
            sb.append(cVar.b());
            z = false;
        }
        return new UploadSettingResult(a.a(Constants.reportStatusApiUrl + sb.toString(), new j("luckymoney_reportstatus")));
    }
}

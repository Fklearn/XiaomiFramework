package com.miui.antispam.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import com.miui.securitycenter.R;
import java.util.ArrayList;

class G implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ EditText f2532a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ KeywordListActivity f2533b;

    G(KeywordListActivity keywordListActivity, EditText editText) {
        this.f2533b = keywordListActivity;
        this.f2532a = editText;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        KeywordListActivity keywordListActivity;
        Context context;
        int i2;
        String trim = this.f2532a.getText().toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            String[] split = trim.split(",|，");
            ArrayList arrayList = new ArrayList();
            if (split != null && split.length > 0) {
                for (String trim2 : split) {
                    String trim3 = trim2.trim();
                    if (!TextUtils.isEmpty(trim3) && !arrayList.contains(trim3)) {
                        arrayList.add(trim3);
                    }
                }
            }
            if (arrayList.size() > 0) {
                this.f2533b.a((ArrayList<String>) arrayList);
                return;
            }
            context = this.f2533b.getApplicationContext();
            keywordListActivity = this.f2533b;
            i2 = R.string.toast_keyword_invalid;
        } else {
            context = this.f2533b.getApplicationContext();
            keywordListActivity = this.f2533b;
            i2 = R.string.toast_keyword_blank;
        }
        Toast.makeText(context, keywordListActivity.getString(i2), 0).show();
    }
}

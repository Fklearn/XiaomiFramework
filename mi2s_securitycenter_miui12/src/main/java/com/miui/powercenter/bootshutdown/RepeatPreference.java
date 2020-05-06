package com.miui.powercenter.bootshutdown;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.AttributeSet;
import com.miui.powercenter.utils.h;
import com.miui.securitycenter.R;
import java.text.DateFormatSymbols;
import miui.app.AlertDialog;
import miui.os.Build;
import miuix.preference.TextPreference;

public class RepeatPreference extends TextPreference {
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public c f6946d = new c(0);
    /* access modifiers changed from: private */
    public Context mContext;

    private class a extends AsyncTask<Void, Void, String[]> {
        private a() {
        }

        /* synthetic */ a(RepeatPreference repeatPreference, j jVar) {
            this();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(String[] strArr) {
            if (RepeatPreference.this.mContext != null) {
                int i = 0;
                if (RepeatPreference.this.mContext instanceof Activity) {
                    Activity activity = (Activity) RepeatPreference.this.mContext;
                    if (activity.isFinishing() || activity.isDestroyed()) {
                        return;
                    }
                }
                if (strArr != null) {
                    int i2 = -1;
                    int[] intArray = RepeatPreference.this.getContext().getResources().getIntArray(Build.IS_INTERNATIONAL_BUILD ? R.array.alarm_repeat_type_no_workdays_values : R.array.alarm_repeat_type_values);
                    int c2 = RepeatPreference.this.c();
                    while (true) {
                        if (i >= intArray.length) {
                            break;
                        } else if (c2 == intArray[i]) {
                            i2 = i;
                            break;
                        } else {
                            i++;
                        }
                    }
                    new AlertDialog.Builder(RepeatPreference.this.getContext()).setSingleChoiceItems(strArr, i2, new l(this, intArray)).show();
                }
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public String[] doInBackground(Void... voidArr) {
            if (Build.IS_INTERNATIONAL_BUILD) {
                return RepeatPreference.this.getContext().getResources().getStringArray(R.array.alarm_repeat_type_no_workdays);
            }
            String[] stringArray = RepeatPreference.this.getContext().getResources().getStringArray(R.array.alarm_repeat_type);
            int i = R.string.legal_workday_message;
            if (h.a(RepeatPreference.this.getContext())) {
                i = R.string.legal_workday_invalidate_message;
            }
            stringArray[2] = stringArray[2] + RepeatPreference.this.getContext().getString(i);
            return stringArray;
        }
    }

    public RepeatPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    /* access modifiers changed from: private */
    public void e() {
        String[] weekdays = new DateFormatSymbols().getWeekdays();
        String[] strArr = {weekdays[2], weekdays[3], weekdays[4], weekdays[5], weekdays[6], weekdays[7], weekdays[1]};
        c cVar = new c(0);
        cVar.a(this.f6946d);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(strArr, this.f6946d.a(), new j(this, cVar));
        builder.setPositiveButton(17039370, new k(this, cVar));
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        builder.show();
    }

    /* access modifiers changed from: private */
    public void f() {
        a(this.f6946d.a(getContext(), true));
        callChangeListener(this.f6946d);
    }

    public void a(c cVar) {
        this.f6946d.a(cVar);
    }

    public int c() {
        int b2 = this.f6946d.b();
        if (b2 == 0) {
            return 0;
        }
        if (b2 == 31) {
            return 3;
        }
        if (b2 != 127) {
            return b2 != 128 ? 4 : 2;
        }
        return 1;
    }

    public c d() {
        return this.f6946d;
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        new a(this, (j) null).execute(new Void[0]);
    }
}

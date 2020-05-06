package b.b.a.d.b;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.preference.Preference;
import b.b.a.e.c;
import com.miui.antispam.ui.activity.BlackListActivity;
import com.miui.antispam.ui.activity.CallInterceptSettingsActivity;
import com.miui.antispam.ui.activity.MsgInterceptSettingsActivity;
import com.miui.antispam.ui.activity.WhiteListActivity;
import com.miui.securitycenter.R;
import miuix.preference.DropDownPreference;

public class v extends e {
    private void d() {
        boolean z = !c.e(this.m);
        this.f1385b.setEnabled(z);
        this.f1386c.setEnabled(z);
        this.f1387d.setEnabled(z);
        this.e.setEnabled(z);
        this.f.setEnabled(z);
        (a() ? this.h : this.i).setEnabled(z);
    }

    private void e() {
        int i = 2;
        int a2 = c.a(this.m, 2);
        if (a2 == 0) {
            i = 0;
        } else if (a2 == 1) {
            i = 1;
        } else if (a2 != 2) {
            i = -1;
        }
        new AlertDialog.Builder(this.m).setTitle(R.string.antispam_notification_setting_title).setSingleChoiceItems(this.n, i, new u(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    public void c() {
        this.f1384a.setChecked(c.e(this.m));
        this.f1385b.setChecked(c.b(this.m, 2));
        if (a()) {
            this.h.a(c.a(this.m, 2));
        } else {
            this.i.a(this.n[c.a(this.m, 2)]);
        }
        d();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.g.d(this.j);
        this.g.d(this.k);
        c();
        b();
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.f1384a) {
            c.a(this.m, ((Boolean) obj).booleanValue());
            d();
            return true;
        } else if (preference == this.f1385b) {
            c.a(this.m, 2, ((Boolean) obj).booleanValue());
            return true;
        } else {
            DropDownPreference dropDownPreference = this.h;
            if (preference != dropDownPreference) {
                return false;
            }
            dropDownPreference.b((String) obj);
            c.a(this.m, this.h.d(), 2);
            return false;
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        Intent intent;
        if (preference == this.i) {
            e();
            return false;
        }
        if (preference == this.f1386c) {
            intent = new Intent(this.m, MsgInterceptSettingsActivity.class);
        } else if (preference == this.f1387d) {
            intent = new Intent(this.m, CallInterceptSettingsActivity.class);
        } else if (preference == this.e) {
            intent = new Intent(this.m, BlackListActivity.class);
        } else if (preference != this.f) {
            return false;
        } else {
            intent = new Intent(this.m, WhiteListActivity.class);
        }
        intent.putExtra("key_sim_id", 2);
        startActivity(intent);
        return false;
    }

    public void onResume() {
        super.onResume();
        new t(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}

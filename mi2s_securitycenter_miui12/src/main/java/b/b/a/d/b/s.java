package b.b.a.d.b;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.Preference;
import b.b.a.e.c;
import b.b.c.j.f;
import com.miui.antispam.service.AntiSpamService;
import com.miui.antispam.service.a.b;
import com.miui.antispam.ui.activity.BlackListActivity;
import com.miui.antispam.ui.activity.CallInterceptSettingsActivity;
import com.miui.antispam.ui.activity.MsgInterceptSettingsActivity;
import com.miui.antispam.ui.activity.WhiteListActivity;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import java.text.SimpleDateFormat;
import java.util.Date;
import miui.app.AlertDialog;
import miui.app.ProgressDialog;
import miui.os.Build;
import miui.telephony.TelephonyManager;
import miuix.preference.DropDownPreference;

public class s extends e {
    /* access modifiers changed from: private */
    public int r = 1;

    /* access modifiers changed from: private */
    public void a(int i) {
        ProgressDialog progressDialog;
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed() && (progressDialog = this.l) != null && progressDialog.isShowing()) {
            try {
                Toast.makeText(getActivity(), i, 0).show();
                this.l.dismiss();
                this.l = null;
            } catch (Exception e) {
                Log.e("SettingsFragmentForSim1", "error dismiss dialog", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void e() {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            long a2 = c.a(this.m);
            if (a2 != 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                this.j.setSummary((CharSequence) getString(R.string.ad_update_time, new Object[]{simpleDateFormat.format(new Date(a2))}));
                return;
            }
            this.j.setSummary((CharSequence) getString(R.string.ad_update_time_unKnown));
        }
    }

    private void f() {
        int a2 = c.a(this.m, this.r);
        int i = 2;
        if (a2 == 0) {
            i = 0;
        } else if (a2 == 1) {
            i = 1;
        } else if (a2 != 2) {
            i = -1;
        }
        new AlertDialog.Builder(this.m).setTitle(R.string.antispam_notification_setting_title).setSingleChoiceItems(this.n, i, new r(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    private void g() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.dlg_update_date_title).setMessage(R.string.dlg_sec_network_unavailable).setPositiveButton(R.string.dlg_update_btn_open, new p(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void h() {
        this.l = ProgressDialog.show(getActivity(), (CharSequence) null, getString(R.string.dlg_update_updating), true, true);
        b.a((Context) getActivity()).a((Runnable) null, (b.C0036b) new o(this), true);
        Intent intent = new Intent(getActivity(), AntiSpamService.class);
        intent.setAction(AntiSpamService.f2390b);
        getActivity().startService(intent);
    }

    private void i() {
        if (!f.b(getActivity())) {
            Toast.makeText(getActivity(), R.string.toast_update_nonetwork, 0).show();
        } else if (!h.i()) {
            g();
        } else if (f.a(getActivity())) {
            c();
        } else {
            h();
        }
    }

    public void c() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.dlg_update_date_title).setMessage(R.string.dlg_update_data_content).setPositiveButton(R.string.dlg_update_btn_contiue, new q(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    public void d() {
        this.f1385b.setChecked(c.b(this.m, this.r));
        if (a()) {
            this.h.a(c.a(this.m, this.r));
        } else {
            this.i.a(this.n[c.a(this.m, this.r)]);
        }
        this.j.setChecked(c.f(this.m));
        e();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getPreferenceScreen().d(this.f1384a);
        if (this.p == 1 && TelephonyManager.getDefault().getPhoneCount() != 1 && !c.e(this.m)) {
            this.r = this.o.get(0).getSlotId() + 1;
        }
        if (Build.IS_INTERNATIONAL_BUILD) {
            this.g.d(this.j);
            this.g.d(this.k);
        }
        d();
        b();
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.f1385b) {
            c.a(this.m, this.r, ((Boolean) obj).booleanValue());
            return true;
        } else if (preference == this.j) {
            c.d(this.m, ((Boolean) obj).booleanValue());
            return true;
        } else {
            DropDownPreference dropDownPreference = this.h;
            if (preference != dropDownPreference) {
                return false;
            }
            dropDownPreference.b((String) obj);
            c.a(this.m, this.h.d(), this.r);
            return false;
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        Intent intent;
        if (preference == this.i) {
            f();
            return false;
        }
        if (preference == this.f1386c) {
            intent = new Intent(this.m, MsgInterceptSettingsActivity.class);
        } else if (preference == this.f1387d) {
            intent = new Intent(this.m, CallInterceptSettingsActivity.class);
        } else if (preference == this.e) {
            intent = new Intent(this.m, BlackListActivity.class);
        } else if (preference == this.f) {
            intent = new Intent(this.m, WhiteListActivity.class);
        } else if (preference != this.k) {
            return false;
        } else {
            i();
            return false;
        }
        intent.putExtra("key_sim_id", this.r);
        startActivity(intent);
        return false;
    }

    public void onResume() {
        super.onResume();
        new n(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}

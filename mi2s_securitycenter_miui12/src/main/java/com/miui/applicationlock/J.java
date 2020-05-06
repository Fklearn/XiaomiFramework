package com.miui.applicationlock;

import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import com.miui.applicationlock.ChooseAccessControl;
import com.miui.applicationlock.c.o;
import com.miui.applicationlock.c.p;
import com.miui.securitycenter.R;

class J implements p {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ChooseAccessControl f3182a;

    J(ChooseAccessControl chooseAccessControl) {
        this.f3182a = chooseAccessControl;
    }

    private void b(Editable editable) {
        boolean z = false;
        if ("pattern".equals(this.f3182a.n)) {
            this.f3182a.f3116b.setText(R.string.lockpattern_recording_inprogress);
            this.f3182a.f3117c.setEnabled(false);
            this.f3182a.f3118d.setEnabled(false);
            o.a(this.f3182a.o, this.f3182a.getResources().getString(R.string.lockpattern_recording_inprogress));
        } else if (editable != null) {
            if (this.f3182a.s == ChooseAccessControl.c.Introduction) {
                String unused = this.f3182a.l = editable.toString();
                TextView e = this.f3182a.f3118d;
                if (editable.length() >= ChooseAccessControl.f3115a) {
                    z = true;
                }
                e.setEnabled(z);
            } else if (this.f3182a.s == ChooseAccessControl.c.NeedToConfirm || this.f3182a.s == ChooseAccessControl.c.ConfirmWrong) {
                Editable unused2 = this.f3182a.m = editable;
                TextView e2 = this.f3182a.f3118d;
                if (editable.length() >= ChooseAccessControl.f3115a) {
                    z = true;
                }
                e2.setEnabled(z);
                if (this.f3182a.s == ChooseAccessControl.c.ConfirmWrong) {
                    ChooseAccessControl.c unused3 = this.f3182a.s = ChooseAccessControl.c.NeedToConfirm;
                    this.f3182a.a(ChooseAccessControl.c.NeedToConfirm);
                }
            }
        }
    }

    public void a() {
    }

    public void a(Editable editable) {
        b(editable);
    }

    public void a(String str) {
        ChooseAccessControl chooseAccessControl;
        ChooseAccessControl.c cVar;
        if (this.f3182a.s == ChooseAccessControl.c.NeedToConfirm || this.f3182a.s == ChooseAccessControl.c.ConfirmWrong) {
            if (this.f3182a.l == null) {
                Log.d("ChooseAccessControl", "null choose pattern in stage 'need to confirm");
                return;
            } else if (this.f3182a.l.equals(str)) {
                this.f3182a.a(ChooseAccessControl.c.ChoiceConfirmed);
                if (!"pattern".equals(this.f3182a.n)) {
                    this.f3182a.n();
                    return;
                }
                return;
            } else {
                chooseAccessControl = this.f3182a;
                cVar = ChooseAccessControl.c.ConfirmWrong;
            }
        } else if ((this.f3182a.s != ChooseAccessControl.c.Introduction && this.f3182a.s != ChooseAccessControl.c.ChoiceTooShort) || TextUtils.isEmpty(str)) {
            return;
        } else {
            if (str.length() < ChooseAccessControl.f3115a) {
                chooseAccessControl = this.f3182a;
                cVar = ChooseAccessControl.c.ChoiceTooShort;
            } else {
                String unused = this.f3182a.l = str;
                chooseAccessControl = this.f3182a;
                cVar = ChooseAccessControl.c.FirstChoiceValid;
            }
        }
        chooseAccessControl.a(cVar);
    }

    public void b() {
    }
}

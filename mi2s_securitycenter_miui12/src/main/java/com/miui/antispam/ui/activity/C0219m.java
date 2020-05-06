package com.miui.antispam.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.miui.antispam.ui.activity.BackSoundActivity;
import com.miui.securitycenter.R;
import miuix.preference.RadioButtonPreference;
import miuix.preference.RadioSetPreferenceCategory;

/* renamed from: com.miui.antispam.ui.activity.m  reason: case insensitive filesystem */
class C0219m extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BackSoundActivity.a f2601a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0219m(BackSoundActivity.a aVar, Looper looper) {
        super(looper);
        this.f2601a = aVar;
    }

    public void handleMessage(Message message) {
        if (!this.f2601a.e.isFinishing() && !this.f2601a.e.isDestroyed()) {
            int i = R.string.back_sound_success;
            RadioButtonPreference radioButtonPreference = (RadioButtonPreference) message.obj;
            int i2 = message.what;
            if (i2 == -3) {
                i = R.string.back_sound_data;
            } else if (i2 == -2) {
                i = R.string.back_sound_support;
            } else if (i2 != -1) {
                if (i2 == 0) {
                    ((RadioButtonPreference) ((RadioSetPreferenceCategory) this.f2601a.f2517c.a(this.f2601a.f())).a(0)).setChecked(false);
                    radioButtonPreference.setChecked(true);
                }
                new AlertDialog.Builder(this.f2601a.e).setMessage(i).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).show();
            } else {
                i = R.string.back_sound_fail;
            }
            this.f2601a.a(radioButtonPreference);
            new AlertDialog.Builder(this.f2601a.e).setMessage(i).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).show();
        }
        if (message.what == 0) {
            this.f2601a.a(message.arg1);
        }
    }
}

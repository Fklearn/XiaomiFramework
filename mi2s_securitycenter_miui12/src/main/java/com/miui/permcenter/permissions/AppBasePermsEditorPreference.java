package com.miui.permcenter.permissions;

import android.content.Context;
import androidx.preference.Preference;
import com.miui.securitycenter.R;

public class AppBasePermsEditorPreference extends Preference {

    /* renamed from: a  reason: collision with root package name */
    protected int f6188a = 2;

    /* renamed from: b  reason: collision with root package name */
    protected long f6189b = 0;

    /* renamed from: c  reason: collision with root package name */
    protected boolean f6190c = true;

    public AppBasePermsEditorPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.pm_app_permission_preference);
    }

    public static AppBasePermsEditorPreference a(Context context, boolean z) {
        return z ? new AppSensitivePermsEditorPreference(context) : new AppPermsEditorPreference(context);
    }

    public final void a(int i) {
        this.f6188a = i;
        notifyChanged();
    }

    public final void a(long j) {
        this.f6189b = j;
    }

    public final void setEnabled(boolean z) {
        this.f6190c = z;
        super.setEnabled(z);
    }
}

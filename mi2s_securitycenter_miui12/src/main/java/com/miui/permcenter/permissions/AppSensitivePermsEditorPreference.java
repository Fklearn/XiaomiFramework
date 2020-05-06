package com.miui.permcenter.permissions;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.preference.A;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.permcenter.privacymanager.b.g;
import com.miui.permcenter.privacymanager.b.m;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;

public class AppSensitivePermsEditorPreference extends AppBasePermsEditorPreference {
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f6207d = false;
    /* access modifiers changed from: private */
    public boolean e = false;
    private Context mContext;

    final class a implements ViewTreeObserver.OnGlobalLayoutListener {

        /* renamed from: a  reason: collision with root package name */
        final Context f6208a;

        /* renamed from: b  reason: collision with root package name */
        final View f6209b;

        a(@NonNull Context context, @NonNull View view) {
            this.f6208a = context;
            this.f6209b = view;
        }

        public void onGlobalLayout() {
            this.f6209b.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            if (AppSensitivePermsEditorPreference.this.f6207d && this.f6209b != null && !AppSensitivePermsEditorPreference.this.e) {
                Handler handler = new Handler();
                View findViewById = this.f6209b.findViewById(R.id.sensitive_pref_item_container);
                String string = this.f6208a.getResources().getString(R.string.intl_perm_intro_message);
                m.a aVar = new m.a(this.f6208a);
                aVar.a(findViewById);
                aVar.a(string);
                aVar.a(m.b.OUTSIDE);
                m a2 = aVar.a();
                a2.b();
                g.f().a(false);
                handler.postDelayed(new m(this, a2), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                boolean unused = AppSensitivePermsEditorPreference.this.e = true;
            }
        }
    }

    public AppSensitivePermsEditorPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.pm_app_sensitive_permission_preference);
        this.mContext = context;
    }

    private int a() {
        long j = this.f6189b;
        if (j == PermissionManager.PERM_ID_CALENDAR) {
            return R.drawable.calendar;
        }
        if (j == 16) {
            return R.drawable.call_log;
        }
        if (j == PermissionManager.PERM_ID_VIDEO_RECORDER) {
            return R.drawable.camera;
        }
        if (j == PermissionManager.PERM_ID_AUDIO_RECORDER) {
            return R.drawable.micro_phone;
        }
        if (j == 8) {
            return R.drawable.contact;
        }
        if (j == 32) {
            return R.drawable.location;
        }
        if (j == PermissionManager.PERM_ID_EXTERNAL_STORAGE) {
            return R.drawable.storage;
        }
        if (j == PermissionManager.PERM_ID_GET_ACCOUNTS) {
            return R.drawable.account;
        }
        return 0;
    }

    public void a(boolean z) {
        this.f6207d = z;
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        View view = a2.itemView;
        view.getRootView().setPadding(0, 0, 0, 0);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.action_bg);
        ImageView imageView = (ImageView) view.findViewById(R.id.action);
        TextView textView = (TextView) view.findViewById(R.id.summary);
        imageView.setImageResource(a());
        int i = this.f6188a;
        int i2 = R.string.permission_action_prompt;
        if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    relativeLayout.setBackgroundResource(R.drawable.shape_sensitive_permission_icon_bg);
                    i2 = R.string.permission_action_accept;
                } else if (i == 6) {
                    relativeLayout.setBackgroundResource(R.drawable.shape_sensitive_permission_icon_enable_bg);
                    i2 = R.string.permission_action_foreground;
                }
            }
            relativeLayout.setBackgroundResource(R.drawable.shape_sensitive_permission_icon_bg);
        } else {
            relativeLayout.setBackgroundResource(R.drawable.shape_sensitive_permission_icon_bg);
            i2 = R.string.permission_action_reject;
        }
        if (i2 != 0) {
            String string = view.getContext().getString(i2);
            imageView.setContentDescription(string);
            if (textView != null) {
                textView.setText(string);
            }
        }
        if (g.f().e() && view != null) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new a(getContext(), view));
        }
    }
}

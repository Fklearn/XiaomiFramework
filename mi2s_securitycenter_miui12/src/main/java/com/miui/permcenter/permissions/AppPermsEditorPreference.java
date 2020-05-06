package com.miui.permcenter.permissions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.preference.A;
import com.miui.securitycenter.R;

public class AppPermsEditorPreference extends AppBasePermsEditorPreference {
    public AppPermsEditorPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.pm_app_permission_preference);
    }

    public void onBindViewHolder(A a2) {
        int i;
        super.onBindViewHolder(a2);
        ImageView imageView = (ImageView) a2.b((int) R.id.action);
        int i2 = this.f6188a;
        if (i2 == 1) {
            imageView.setImageResource(this.f6190c ? R.drawable.icon_action_reject : R.drawable.icon_action_reject_disable);
            i = R.string.permission_action_reject;
        } else if (i2 == 2) {
            imageView.setImageResource(this.f6190c ? R.drawable.icon_action_prompt : R.drawable.icon_action_prompt_disable);
            i = R.string.permission_action_prompt;
        } else if (i2 == 3) {
            imageView.setImageResource(this.f6190c ? R.drawable.icon_action_accept : R.drawable.icon_action_accept_disable);
            i = R.string.permission_action_accept;
        } else if (i2 == 6) {
            imageView.setImageResource(R.drawable.icon_action_foreground);
            i = R.string.permission_action_foreground;
        } else if (i2 != 7) {
            imageView.setImageDrawable((Drawable) null);
            i = 0;
        } else {
            imageView.setImageResource(this.f6190c ? R.drawable.icon_action_virtual : R.drawable.icon_action_virtual_disable);
            i = R.string.permission_action_virtual;
        }
        if (i != 0) {
            imageView.setContentDescription(getContext().getString(i));
        }
    }
}

package com.android.server.am;

import android.content.Context;
import android.miui.R;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MiuiUserSwitchingDialog extends BaseUserSwitchingDialog {
    public /* bridge */ /* synthetic */ void show() {
        super.show();
    }

    public MiuiUserSwitchingDialog(ActivityManagerService service, Context context, int userId) {
        super(service, context, R.style.MiuiUserSwitchDialog, userId);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(getContext().obtainStyledAttributes((AttributeSet) null, com.miui.internal.R.styleable.AlertDialog, 16842845, 0).getResourceId(com.miui.internal.R.styleable.AlertDialog_progressLayout, com.miui.internal.R.layout.progress_dialog), (ViewGroup) null);
        view.findViewById(16908301).setVisibility(8);
        setView(view);
        ((TextView) view.findViewById(miui.R.id.message)).setText(getContext().getString(R.string.user_switching_dialog_text));
        super.onCreate(savedInstanceState);
    }
}

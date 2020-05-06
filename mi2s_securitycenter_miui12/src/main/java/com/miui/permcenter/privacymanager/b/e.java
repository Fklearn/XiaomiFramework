package com.miui.permcenter.privacymanager.b;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.miui.permcenter.n;
import com.miui.permcenter.widget.ChoiceItemView;
import com.miui.permission.PermissionInfo;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.util.List;
import miui.app.AlertDialog;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private static PermissionManager f6355a;

    /* renamed from: b  reason: collision with root package name */
    private static List<PermissionInfo> f6356b;

    public static int a(int i) {
        if (i == 1) {
            return 4;
        }
        if (i == 2) {
            return 3;
        }
        if (i == 3) {
            return 0;
        }
        if (i != 6) {
            return i != 7 ? -1 : 2;
        }
        return 1;
    }

    public static String a(@NonNull Context context, long j) {
        if (context == null) {
            return "";
        }
        a(context);
        List<PermissionInfo> list = f6356b;
        if (!(list == null || list.size() == 0)) {
            for (PermissionInfo next : f6356b) {
                if (next.getId() == j) {
                    return next.getDesc();
                }
            }
        }
        return "";
    }

    public static void a(Activity activity, String str, long j, String str2, int i, n.c cVar, boolean z, boolean z2, String str3, String str4, boolean z3) {
        Activity activity2 = activity;
        long j2 = j;
        String[] strArr = new String[5];
        String[] strArr2 = new String[5];
        String string = activity2.getString(R.string.permission_action_always);
        String string2 = activity2.getString(R.string.permission_action_reject);
        String a2 = a(activity2, j2);
        if (Build.VERSION.SDK_INT < 28 || !z3) {
            strArr[0] = string;
            strArr2[0] = activity2.getString(R.string.permission_action_desc_always);
        } else {
            if (j2 != PermissionManager.PERM_ID_VIDEO_RECORDER || c.a(activity)) {
                strArr[0] = string;
                strArr2[0] = activity2.getString(R.string.permission_action_desc_always);
            }
            strArr[1] = activity2.getString(R.string.permission_action_foreground);
            strArr2[1] = activity2.getString(R.string.permission_action_desc_foreground);
        }
        if (PermissionManager.virtualMap.containsKey(Long.valueOf(j))) {
            strArr[2] = activity2.getString(R.string.permission_action_virtual);
            strArr2[2] = "";
        }
        if (!z) {
            strArr[3] = activity2.getString(R.string.permission_action_prompt);
            strArr2[3] = activity2.getString(R.string.permission_action_desc_prompt);
        }
        if (!z2) {
            strArr[4] = string2;
            strArr2[4] = activity2.getString(R.string.permission_action_desc_reject);
        }
        int a3 = a(i);
        View inflate = LayoutInflater.from(activity).inflate(R.layout.dialog_permission_manager_intl, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.dialog_permission_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
        builder.setView(inflate);
        AlertDialog show = builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
        FrameLayout frameLayout = (FrameLayout) inflate.getParent().getParent();
        int paddingLeft = frameLayout.getPaddingLeft();
        int paddingRight = frameLayout.getPaddingRight();
        frameLayout.setPadding(0, frameLayout.getPaddingTop(), 0, frameLayout.getPaddingBottom());
        textView.setText(str2);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        ((TextView) inflate.findViewById(R.id.dialog_package_name)).setText(a2);
        View view = inflate;
        int i2 = a3;
        d dVar = new d(show, i, activity, str, j, cVar);
        ChoiceItemView[] choiceItemViewArr = {(ChoiceItemView) view.findViewById(R.id.select_allow), (ChoiceItemView) view.findViewById(R.id.select_foreground), (ChoiceItemView) view.findViewById(R.id.select_virtual), (ChoiceItemView) view.findViewById(R.id.select_ask), (ChoiceItemView) view.findViewById(R.id.select_deny)};
        int length = choiceItemViewArr.length;
        for (int i3 = 0; i3 <= length - 1; i3++) {
            if (TextUtils.isEmpty(strArr[i3])) {
                choiceItemViewArr[i3].setVisibility(8);
            } else {
                choiceItemViewArr[i3].a(0, paddingLeft, paddingRight);
                choiceItemViewArr[i3].setOnClickListener(dVar);
                choiceItemViewArr[i3].setTitle(strArr[i3]);
                choiceItemViewArr[i3].a();
                if (!TextUtils.isEmpty(strArr2[i3])) {
                    choiceItemViewArr[i3].setSummary(strArr2[i3]);
                }
                if (i3 == 2) {
                    choiceItemViewArr[i3].setSummary(activity.getResources().getString(R.string.permission_action_desc));
                    choiceItemViewArr[i3].setTips(activity.getResources().getString(R.string.permission_action_tag));
                }
            }
        }
        int i4 = i2;
        if (i4 != -1) {
            choiceItemViewArr[i4].setSelectedVisible(true);
        }
    }

    private static void a(@NonNull Context context) {
        if (f6355a == null) {
            f6355a = PermissionManager.getInstance(context);
        }
        PermissionManager permissionManager = f6355a;
        if (permissionManager != null) {
            f6356b = permissionManager.getAllPermissions(0);
        }
    }
}

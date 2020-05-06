package com.miui.powercenter.autotask;

import android.content.Context;
import android.content.DialogInterface;
import miui.app.AlertDialog;

/* renamed from: com.miui.powercenter.autotask.b  reason: case insensitive filesystem */
public class C0473b {
    public static void a(Context context, CharSequence charSequence) {
        new AlertDialog.Builder(context).setTitle(charSequence).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).show();
    }

    public static void a(Context context, CharSequence charSequence, CharSequence charSequence2, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context).setTitle(charSequence).setPositiveButton(charSequence2, onClickListener).setNegativeButton(17039360, new C0472a()).show();
    }

    public static void a(Context context, CharSequence charSequence, CharSequence[] charSequenceArr, int i, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context).setTitle(charSequence).setSingleChoiceItems(charSequenceArr, i, onClickListener).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }
}

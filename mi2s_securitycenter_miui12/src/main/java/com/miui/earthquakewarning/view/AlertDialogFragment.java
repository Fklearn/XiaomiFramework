package com.miui.earthquakewarning.view;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import miui.R;
import miui.app.AlertDialog;

public class AlertDialogFragment extends BaseDialogFragment {
    private static final String CANCELABLE_DIALOG = "cancelable";
    private static final String DIALOG_FRAGMENT_TAG = "AlertDialogFragment";
    private static final String MESSAGE_DIALOG = "message";
    private static final String NEGATIVE_BTN_DIALOG = "negative";
    private static final String POSITIVE_BTN_DIALOG = "positive";
    private static final String SET_MOVEMENT_METHOD_DIALOG = "set_movement_method";
    private static final String TITLE_DIALOG = "title";
    private DialogInterface.OnClickListener mNegativeClickListener;
    private DialogInterface.OnClickListener mPositiveClickListener;

    public static class Builder {
        boolean mCancelable = false;
        CharSequence mMessage;
        DialogInterface.OnClickListener mNegativeButtonListener;
        String mNegativeButtonText;
        DialogInterface.OnClickListener mPositiveButtonListener;
        String mPositiveButtonText;
        boolean mSetMovementMethod = false;
        CharSequence mTitle;

        public Builder setCancelable(boolean z) {
            this.mCancelable = z;
            return this;
        }

        public Builder setMessage(CharSequence charSequence) {
            this.mMessage = charSequence;
            return this;
        }

        public Builder setMovementMethod(boolean z) {
            this.mSetMovementMethod = z;
            return this;
        }

        public Builder setNegativeButton(String str, DialogInterface.OnClickListener onClickListener) {
            this.mNegativeButtonText = str;
            this.mNegativeButtonListener = onClickListener;
            return this;
        }

        public Builder setPositiveButton(String str, DialogInterface.OnClickListener onClickListener) {
            this.mPositiveButtonText = str;
            this.mPositiveButtonListener = onClickListener;
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.mTitle = charSequence;
            return this;
        }

        public void show(FragmentManager fragmentManager) {
            AlertDialogFragment access$000 = AlertDialogFragment.createDialogFragment(this.mTitle, this.mMessage, this.mPositiveButtonText, this.mNegativeButtonText, this.mPositiveButtonListener, this.mNegativeButtonListener, this.mCancelable, this.mSetMovementMethod);
            access$000.setCancelable(false);
            access$000.show(fragmentManager, AlertDialogFragment.DIALOG_FRAGMENT_TAG);
        }
    }

    /* access modifiers changed from: private */
    public static AlertDialogFragment createDialogFragment(CharSequence charSequence, CharSequence charSequence2, String str, String str2, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onClickListener2, boolean z, boolean z2) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("title", charSequence);
        bundle.putCharSequence(MESSAGE_DIALOG, charSequence2);
        bundle.putString(POSITIVE_BTN_DIALOG, str);
        bundle.putString(NEGATIVE_BTN_DIALOG, str2);
        bundle.putBoolean(CANCELABLE_DIALOG, z);
        bundle.putBoolean(SET_MOVEMENT_METHOD_DIALOG, z2);
        alertDialogFragment.setArguments(bundle);
        alertDialogFragment.setPositiveClickListener(onClickListener);
        alertDialogFragment.setNegativeClickListener(onClickListener2);
        return alertDialogFragment;
    }

    private void setNegativeClickListener(DialogInterface.OnClickListener onClickListener) {
        this.mNegativeClickListener = onClickListener;
    }

    private void setPositiveClickListener(DialogInterface.OnClickListener onClickListener) {
        this.mPositiveClickListener = onClickListener;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        CharSequence charSequence = arguments.getCharSequence("title");
        CharSequence charSequence2 = arguments.getCharSequence(MESSAGE_DIALOG);
        String string = arguments.getString(POSITIVE_BTN_DIALOG);
        String string2 = arguments.getString(NEGATIVE_BTN_DIALOG);
        boolean z = arguments.getBoolean(CANCELABLE_DIALOG);
        AlertDialog.Builder cancelable = new AlertDialog.Builder(getActivity()).setPositiveButton(string, this.mPositiveClickListener).setNegativeButton(string2, this.mNegativeClickListener).setCancelable(z);
        if (!TextUtils.isEmpty(charSequence)) {
            cancelable.setTitle(charSequence);
        }
        if (!TextUtils.isEmpty(charSequence2)) {
            cancelable.setMessage(charSequence2);
        }
        AlertDialog create = cancelable.create();
        create.setCanceledOnTouchOutside(z);
        return create;
    }

    public void onStart() {
        super.onStart();
        if (getArguments().getBoolean(SET_MOVEMENT_METHOD_DIALOG, false)) {
            ((TextView) getDialog().findViewById(R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}

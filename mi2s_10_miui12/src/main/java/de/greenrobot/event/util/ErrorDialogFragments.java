package de.greenrobot.event.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class ErrorDialogFragments {
    public static int ERROR_DIALOG_ICON = 0;
    public static Class<?> EVENT_TYPE_ON_CLICK;

    public static Dialog createDialog(Context context, Bundle arguments, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(arguments.getString(ErrorDialogManager.KEY_TITLE));
        builder.setMessage(arguments.getString(ErrorDialogManager.KEY_MESSAGE));
        int i = ERROR_DIALOG_ICON;
        if (i != 0) {
            builder.setIcon(i);
        }
        builder.setPositiveButton(17039370, onClickListener);
        return builder.create();
    }

    public static void handleOnClick(DialogInterface dialog, int which, Activity activity, Bundle arguments) {
        Class<?> cls = EVENT_TYPE_ON_CLICK;
        if (cls != null) {
            try {
                ErrorDialogManager.factory.config.getEventBus().post(cls.newInstance());
            } catch (Exception e) {
                throw new RuntimeException("Event cannot be constructed", e);
            }
        }
        if (arguments.getBoolean(ErrorDialogManager.KEY_FINISH_AFTER_DIALOG, false) && activity != null) {
            activity.finish();
        }
    }

    @TargetApi(11)
    public static class Honeycomb extends DialogFragment implements DialogInterface.OnClickListener {
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ErrorDialogFragments.createDialog(getActivity(), getArguments(), this);
        }

        public void onClick(DialogInterface dialog, int which) {
            ErrorDialogFragments.handleOnClick(dialog, which, getActivity(), getArguments());
        }
    }

    public static class Support extends android.support.v4.app.DialogFragment implements DialogInterface.OnClickListener {
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ErrorDialogFragments.createDialog(getActivity(), getArguments(), this);
        }

        public void onClick(DialogInterface dialog, int which) {
            ErrorDialogFragments.handleOnClick(dialog, which, getActivity(), getArguments());
        }
    }
}

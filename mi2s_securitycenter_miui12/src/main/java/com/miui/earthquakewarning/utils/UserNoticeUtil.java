package com.miui.earthquakewarning.utils;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import com.miui.earthquakewarning.view.AlertDialogFragment;
import com.miui.securitycenter.R;
import java.util.Locale;
import miui.os.Build;

public class UserNoticeUtil {

    public interface ClickButtonListener {
        void onAccept();

        void onNotAccept();
    }

    public static String getStatementMessage(Context context) {
        return context.getString(R.string.ew_guide_statement_text, new Object[]{Build.getRegion(), Locale.getDefault().toString()});
    }

    public static String getUserNoticeMessage(Context context) {
        return context.getString(R.string.ew_china_version_first_start_network_msg, new Object[]{Build.getRegion(), Locale.getDefault().toString()});
    }

    public static void showUserNoticeDialog(Context context, FragmentManager fragmentManager, final ClickButtonListener clickButtonListener) {
        new AlertDialogFragment.Builder().setTitle(context.getString(R.string.ew_first_start_network_title)).setMessage(Html.fromHtml(getUserNoticeMessage(context))).setPositiveButton(context.getString(R.string.ew_button_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ClickButtonListener clickButtonListener = clickButtonListener;
                if (clickButtonListener != null) {
                    clickButtonListener.onAccept();
                }
                dialogInterface.cancel();
            }
        }).setNegativeButton(context.getString(R.string.ew_button_exit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                ClickButtonListener clickButtonListener = clickButtonListener;
                if (clickButtonListener != null) {
                    clickButtonListener.onNotAccept();
                }
            }
        }).setMovementMethod(true).show(fragmentManager);
    }
}

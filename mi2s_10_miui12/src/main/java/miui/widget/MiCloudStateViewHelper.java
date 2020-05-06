package miui.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.miui.system.internal.R;

public class MiCloudStateViewHelper {
    public static int getDisablePaddingBottom(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.cloud_state_message_bg_padding_outter);
    }

    public static int getEnablePaddingTop(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.cloud_state_message_enabled_bg_padding_top);
    }

    public static int getEnablePaddingBottom(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.cloud_state_message_enabled_bg_padding_bottom);
    }

    public static int getTextViewsMargin(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.cloud_state_message_textview_margin);
    }

    public static void setLayoutMinHeight(Context context, View view, int contentHeight) {
        view.setMinimumHeight(getEnablePaddingTop(context) + contentHeight + getEnablePaddingBottom(context));
    }

    public static void setUnsyncedTextColor(Context context, TextView textView) {
        textView.setTextColor(context.getResources().getColor(R.color.cloud_state_message_textcolor_unsynced));
    }
}

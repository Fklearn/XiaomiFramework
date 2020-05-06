package miui.keyguard.clock;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import com.miui.system.internal.R;

class MiuiClockAccessibilityDelegate extends View.AccessibilityDelegate {
    private final String mFancyColon;

    public MiuiClockAccessibilityDelegate(Context context) {
        this.mFancyColon = context.getString(R.string.miui_clock_fancy_colon);
    }

    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(host, event);
        CharSequence text = event.getContentDescription();
        if (!TextUtils.isEmpty(text)) {
            event.setContentDescription(replaceFancyColon(text));
        }
    }

    public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        CharSequence text;
        if (host instanceof TextView) {
            text = ((TextView) host).getText();
        } else {
            text = event.getContentDescription();
        }
        if (!TextUtils.isEmpty(text)) {
            event.getText().add(replaceFancyColon(text));
        }
    }

    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        if (!TextUtils.isEmpty(info.getText())) {
            info.setText(replaceFancyColon(info.getText()));
        }
        if (!TextUtils.isEmpty(info.getContentDescription())) {
            info.setContentDescription(replaceFancyColon(info.getContentDescription()));
        }
    }

    private CharSequence replaceFancyColon(CharSequence text) {
        return text.toString().replace(this.mFancyColon, ":");
    }
}

package b.b.i.b;

import android.graphics.Color;
import android.view.View;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import miui.animation.Folme;
import miui.animation.IFolme;
import miui.animation.ITouchStyle;
import miui.animation.base.AnimConfig;

public class a {
    public static void a(View view) {
        if (view != null) {
            try {
                int color = Application.d().getResources().getColor(R.color.storage_list_item_pressed_bg);
                ITouchStyle iTouchStyle = Folme.useAt(new View[]{view}).touch();
                iTouchStyle.setTint((((float) Color.alpha(color)) * 1.0f) / 255.0f, (((float) Color.red(color)) * 1.0f) / 255.0f, (((float) Color.green(color)) * 1.0f) / 255.0f, (((float) Color.blue(color)) * 1.0f) / 255.0f);
                iTouchStyle.setScale(1.0f, new ITouchStyle.TouchType[]{ITouchStyle.TouchType.DOWN});
                iTouchStyle.handleTouchOf(view, new AnimConfig[0]);
            } catch (Throwable unused) {
            }
        }
    }

    public static void b(View view) {
        if (view != null) {
            try {
                IFolme useAt = Folme.useAt(new View[]{view});
                useAt.touch().setAlpha(0.6f, new ITouchStyle.TouchType[]{ITouchStyle.TouchType.DOWN});
                useAt.touch().setScale(1.0f, new ITouchStyle.TouchType[0]);
                useAt.touch().handleTouchOf(view, new AnimConfig[0]);
            } catch (Throwable unused) {
            }
        }
    }
}

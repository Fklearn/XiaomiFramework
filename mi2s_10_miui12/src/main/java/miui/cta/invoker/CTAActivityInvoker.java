package miui.cta.invoker;

import android.util.Log;
import miui.cta.CTAManager;
import miui.extension.invoker.Invoker;

public class CTAActivityInvoker implements Invoker {
    private static final String TAG = "ActivityInvoker";

    public final void invoke(String action, Object... args) {
        if ("onCreate".equals(action)) {
            CTAManager.showAgreementIfNeed(args[0]);
            return;
        }
        Log.w(TAG, "Action is not supported: " + action);
    }
}

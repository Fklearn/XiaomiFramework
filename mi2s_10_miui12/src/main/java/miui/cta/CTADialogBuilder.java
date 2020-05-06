package miui.cta;

import android.content.Context;
import android.content.DialogInterface;
import com.miui.system.internal.R;
import miui.app.AlertDialog;

public class CTADialogBuilder extends AlertDialog.Builder {
    private Context mContext;

    public CTADialogBuilder(Context context) {
        super(context);
        initialize(context);
    }

    public CTADialogBuilder(Context context, int theme) {
        super(context, theme);
        initialize(context);
    }

    private void initialize(Context context) {
        this.mContext = context;
        setTitle(R.string.cta_title);
        setCancelable(false);
    }

    public CTADialogBuilder setPositiveButton(DialogInterface.OnClickListener listener) {
        setPositiveButton(R.string.cta_button_continue, listener);
        return this;
    }

    public CTADialogBuilder setNegativeButton(DialogInterface.OnClickListener listener) {
        setNegativeButton(R.string.cta_button_quit, listener);
        return this;
    }
}

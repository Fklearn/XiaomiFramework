package b.b.b.d;

import android.app.Dialog;
import android.content.DialogInterface;

public class h implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public DialogInterface.OnClickListener f1529a;

    /* renamed from: b  reason: collision with root package name */
    private DialogInterface.OnDismissListener f1530b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public DialogInterface.OnCancelListener f1531c;

    public h(DialogInterface.OnClickListener onClickListener) {
        this.f1529a = onClickListener;
    }

    public h(DialogInterface.OnDismissListener onDismissListener) {
        this.f1530b = onDismissListener;
    }

    public void a(Dialog dialog) {
        if (dialog.getWindow() != null) {
            dialog.getWindow().getDecorView().getViewTreeObserver().addOnWindowAttachListener(new g(this));
        }
    }

    public void onCancel(DialogInterface dialogInterface) {
        DialogInterface.OnCancelListener onCancelListener = this.f1531c;
        if (onCancelListener != null) {
            onCancelListener.onCancel(dialogInterface);
        }
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        DialogInterface.OnClickListener onClickListener = this.f1529a;
        if (onClickListener != null) {
            onClickListener.onClick(dialogInterface, i);
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        DialogInterface.OnDismissListener onDismissListener = this.f1530b;
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialogInterface);
            this.f1530b = null;
        }
    }
}

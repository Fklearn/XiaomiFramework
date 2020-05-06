package b.b.c.c.b;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import java.lang.ref.WeakReference;
import miui.app.AlertDialog;

public abstract class c {
    protected Activity mActivity;
    private a mBaseDialogClickListener;
    private AlertDialog mDialog;
    private boolean mIsWeakReferenceEnabled = true;
    private String mMessage;
    private String mNagetiveText;
    private String mPostiveText;
    private String mTitle;

    private static class a implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<c> f1623a;

        /* renamed from: b  reason: collision with root package name */
        private c f1624b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f1625c;

        private a(c cVar, boolean z) {
            if (z) {
                this.f1623a = new WeakReference<>(cVar);
            } else {
                this.f1624b = cVar;
            }
            this.f1625c = z;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            c cVar = this.f1624b;
            if (this.f1625c) {
                cVar = (c) this.f1623a.get();
            }
            if (cVar != null) {
                cVar.onClick(dialogInterface, i);
            }
        }
    }

    protected c(Activity activity) {
        this.mActivity = activity;
    }

    /* access modifiers changed from: protected */
    public void clearDialog() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDialog = null;
        }
    }

    public AlertDialog getDialog() {
        return this.mDialog;
    }

    /* access modifiers changed from: protected */
    public abstract int getNegativeButtonText();

    /* access modifiers changed from: protected */
    public DialogInterface.OnClickListener getOnClickListener() {
        return this.mBaseDialogClickListener;
    }

    /* access modifiers changed from: protected */
    public abstract int getPositiveButtonText();

    /* access modifiers changed from: protected */
    public abstract void onBuild(AlertDialog alertDialog);

    /* access modifiers changed from: protected */
    public abstract void onClick(DialogInterface dialogInterface, int i);

    /* access modifiers changed from: protected */
    public void onPrepareBuild(AlertDialog.Builder builder) {
    }

    /* access modifiers changed from: protected */
    public abstract void onShow(AlertDialog alertDialog);

    public void setMessage(String str) {
        this.mMessage = str;
    }

    public void setNagetiveText(String str) {
        this.mNagetiveText = str;
    }

    public void setPostiveText(String str) {
        this.mPostiveText = str;
    }

    public void setTitle(String str) {
        this.mTitle = str;
    }

    /* access modifiers changed from: protected */
    public void setWeakReferenceEnabled(boolean z) {
        this.mIsWeakReferenceEnabled = z;
    }

    /* access modifiers changed from: protected */
    public void showDialog() {
        Activity activity = this.mActivity;
        if (activity != null && !activity.isFinishing() && !this.mActivity.isDestroyed()) {
            if (this.mDialog == null) {
                this.mBaseDialogClickListener = new a(this.mIsWeakReferenceEnabled);
                AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
                onPrepareBuild(builder);
                this.mDialog = builder.create();
                Resources resources = this.mActivity.getResources();
                int positiveButtonText = getPositiveButtonText();
                if (positiveButtonText > 0 && this.mPostiveText == null) {
                    this.mPostiveText = resources.getString(positiveButtonText);
                }
                int negativeButtonText = getNegativeButtonText();
                if (negativeButtonText > 0 && this.mNagetiveText == null) {
                    this.mNagetiveText = resources.getString(negativeButtonText);
                }
                String str = this.mPostiveText;
                if (str != null) {
                    this.mDialog.setButton(-1, str, this.mBaseDialogClickListener);
                }
                String str2 = this.mNagetiveText;
                if (str2 != null) {
                    this.mDialog.setButton(-2, str2, this.mBaseDialogClickListener);
                }
                onBuild(this.mDialog);
            }
            this.mDialog.setTitle(this.mTitle);
            this.mDialog.setMessage(this.mMessage);
            this.mDialog.show();
            onShow(this.mDialog);
        }
    }
}

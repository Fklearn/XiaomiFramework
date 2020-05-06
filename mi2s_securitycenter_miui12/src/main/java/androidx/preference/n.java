package androidx.preference;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.preference.DialogPreference;

@Deprecated
public abstract class n extends DialogFragment implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private DialogPreference f1039a;

    /* renamed from: b  reason: collision with root package name */
    private CharSequence f1040b;

    /* renamed from: c  reason: collision with root package name */
    private CharSequence f1041c;

    /* renamed from: d  reason: collision with root package name */
    private CharSequence f1042d;
    private CharSequence e;
    @LayoutRes
    private int f;
    private BitmapDrawable g;
    private int h;

    /* access modifiers changed from: protected */
    @Deprecated
    public View a(Context context) {
        int i = this.f;
        if (i == 0) {
            return null;
        }
        return LayoutInflater.from(context).inflate(i, (ViewGroup) null);
    }

    @Deprecated
    public DialogPreference a() {
        if (this.f1039a == null) {
            this.f1039a = (DialogPreference) ((DialogPreference.a) getTargetFragment()).findPreference(getArguments().getString("key"));
        }
        return this.f1039a;
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public void a(AlertDialog.Builder builder) {
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public void a(View view) {
        View findViewById = view.findViewById(16908299);
        if (findViewById != null) {
            CharSequence charSequence = this.e;
            int i = 8;
            if (!TextUtils.isEmpty(charSequence)) {
                if (findViewById instanceof TextView) {
                    ((TextView) findViewById).setText(charSequence);
                }
                i = 0;
            }
            if (findViewById.getVisibility() != i) {
                findViewById.setVisibility(i);
            }
        }
    }

    @Deprecated
    public abstract void a(boolean z);

    @Deprecated
    public void onClick(DialogInterface dialogInterface, int i) {
        this.h = i;
    }

    public void onCreate(Bundle bundle) {
        BitmapDrawable bitmapDrawable;
        super.onCreate(bundle);
        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof DialogPreference.a) {
            DialogPreference.a aVar = (DialogPreference.a) targetFragment;
            String string = getArguments().getString("key");
            if (bundle == null) {
                this.f1039a = (DialogPreference) aVar.findPreference(string);
                this.f1040b = this.f1039a.d();
                this.f1041c = this.f1039a.f();
                this.f1042d = this.f1039a.e();
                this.e = this.f1039a.c();
                this.f = this.f1039a.b();
                Drawable a2 = this.f1039a.a();
                if (a2 == null || (a2 instanceof BitmapDrawable)) {
                    bitmapDrawable = (BitmapDrawable) a2;
                } else {
                    Bitmap createBitmap = Bitmap.createBitmap(a2.getIntrinsicWidth(), a2.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    a2.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    a2.draw(canvas);
                    bitmapDrawable = new BitmapDrawable(getResources(), createBitmap);
                }
                this.g = bitmapDrawable;
                return;
            }
            this.f1040b = bundle.getCharSequence("PreferenceDialogFragment.title");
            this.f1041c = bundle.getCharSequence("PreferenceDialogFragment.positiveText");
            this.f1042d = bundle.getCharSequence("PreferenceDialogFragment.negativeText");
            this.e = bundle.getCharSequence("PreferenceDialogFragment.message");
            this.f = bundle.getInt("PreferenceDialogFragment.layout", 0);
            Bitmap bitmap = (Bitmap) bundle.getParcelable("PreferenceDialogFragment.icon");
            if (bitmap != null) {
                this.g = new BitmapDrawable(getResources(), bitmap);
                return;
            }
            return;
        }
        throw new IllegalStateException("Target fragment must implement TargetFragment interface");
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        a(this.h == -1);
    }

    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putCharSequence("PreferenceDialogFragment.title", this.f1040b);
        bundle.putCharSequence("PreferenceDialogFragment.positiveText", this.f1041c);
        bundle.putCharSequence("PreferenceDialogFragment.negativeText", this.f1042d);
        bundle.putCharSequence("PreferenceDialogFragment.message", this.e);
        bundle.putInt("PreferenceDialogFragment.layout", this.f);
        BitmapDrawable bitmapDrawable = this.g;
        if (bitmapDrawable != null) {
            bundle.putParcelable("PreferenceDialogFragment.icon", bitmapDrawable.getBitmap());
        }
    }
}

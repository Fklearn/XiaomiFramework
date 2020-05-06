package miuix.preference;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.preference.A;
import androidx.preference.Preference;

public class TextPreference extends Preference {

    /* renamed from: a  reason: collision with root package name */
    private CharSequence f8890a;

    /* renamed from: b  reason: collision with root package name */
    private int f8891b;

    /* renamed from: c  reason: collision with root package name */
    private a f8892c;

    public interface a<T extends TextPreference> {
        CharSequence a(T t);
    }

    public TextPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public TextPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, v.textPreferenceStyle);
    }

    public TextPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CharSequence a() {
        return b() != null ? b().a(this) : this.f8890a;
    }

    public void a(int i) {
        a(getContext().getString(i));
        this.f8891b = i;
    }

    public void a(String str) {
        if (b() != null) {
            throw new IllegalStateException("Preference already has a TextProvider set.");
        } else if (!TextUtils.equals(str, this.f8890a)) {
            this.f8891b = 0;
            this.f8890a = str;
            notifyChanged();
        }
    }

    @Nullable
    public final a b() {
        return this.f8892c;
    }

    public void onBindViewHolder(A a2) {
        int i;
        super.onBindViewHolder(a2);
        TextView textView = (TextView) a2.itemView.findViewById(x.text_right);
        if (textView != null) {
            CharSequence a3 = a();
            if (!TextUtils.isEmpty(a3)) {
                textView.setText(a3);
                i = 0;
            } else {
                i = 8;
            }
            textView.setVisibility(i);
        }
    }
}

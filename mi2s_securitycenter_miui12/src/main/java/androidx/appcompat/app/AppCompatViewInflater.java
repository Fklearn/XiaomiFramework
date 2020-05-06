package androidx.appcompat.app;

import a.a.d.d;
import a.a.j;
import a.c.i;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.C0103i;
import androidx.appcompat.widget.C0107k;
import androidx.appcompat.widget.C0109l;
import androidx.appcompat.widget.C0113p;
import androidx.appcompat.widget.C0116t;
import androidx.appcompat.widget.C0119w;
import androidx.appcompat.widget.C0120x;
import androidx.appcompat.widget.I;
import androidx.appcompat.widget.K;
import androidx.appcompat.widget.r;
import androidx.appcompat.widget.sa;
import androidx.core.view.ViewCompat;
import com.miui.maml.elements.ButtonScreenElement;
import com.miui.maml.util.ConfigFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AppCompatViewInflater {

    /* renamed from: a  reason: collision with root package name */
    private static final Class<?>[] f261a = {Context.class, AttributeSet.class};

    /* renamed from: b  reason: collision with root package name */
    private static final int[] f262b = {16843375};

    /* renamed from: c  reason: collision with root package name */
    private static final String[] f263c = {"android.widget.", "android.view.", "android.webkit."};

    /* renamed from: d  reason: collision with root package name */
    private static final i<String, Constructor<? extends View>> f264d = new i<>();
    private final Object[] e = new Object[2];

    private static class a implements View.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private final View f265a;

        /* renamed from: b  reason: collision with root package name */
        private final String f266b;

        /* renamed from: c  reason: collision with root package name */
        private Method f267c;

        /* renamed from: d  reason: collision with root package name */
        private Context f268d;

        public a(@NonNull View view, @NonNull String str) {
            this.f265a = view;
            this.f266b = str;
        }

        private void a(@Nullable Context context) {
            String str;
            Method method;
            while (context != null) {
                try {
                    if (!context.isRestricted() && (method = context.getClass().getMethod(this.f266b, new Class[]{View.class})) != null) {
                        this.f267c = method;
                        this.f268d = context;
                        return;
                    }
                } catch (NoSuchMethodException unused) {
                }
                context = context instanceof ContextWrapper ? ((ContextWrapper) context).getBaseContext() : null;
            }
            int id = this.f265a.getId();
            if (id == -1) {
                str = "";
            } else {
                str = " with id '" + this.f265a.getContext().getResources().getResourceEntryName(id) + "'";
            }
            throw new IllegalStateException("Could not find method " + this.f266b + "(View) in a parent or ancestor Context for android:onClick attribute defined on view " + this.f265a.getClass() + str);
        }

        public void onClick(@NonNull View view) {
            if (this.f267c == null) {
                a(this.f265a.getContext());
            }
            try {
                this.f267c.invoke(this.f268d, new Object[]{view});
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not execute non-public method for android:onClick", e);
            } catch (InvocationTargetException e2) {
                throw new IllegalStateException("Could not execute method for android:onClick", e2);
            }
        }
    }

    private static Context a(Context context, AttributeSet attributeSet, boolean z, boolean z2) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, j.View, 0, 0);
        int resourceId = z ? obtainStyledAttributes.getResourceId(j.View_android_theme, 0) : 0;
        if (z2 && resourceId == 0 && (resourceId = obtainStyledAttributes.getResourceId(j.View_theme, 0)) != 0) {
            Log.i("AppCompatViewInflater", "app:theme is now deprecated. Please move to using android:theme instead.");
        }
        obtainStyledAttributes.recycle();
        return resourceId != 0 ? (!(context instanceof d) || ((d) context).a() != resourceId) ? new d(context, resourceId) : context : context;
    }

    private View a(Context context, String str, String str2) {
        String str3;
        Constructor<? extends U> constructor = f264d.get(str);
        if (constructor == null) {
            if (str2 != null) {
                try {
                    str3 = str2 + str;
                } catch (Exception unused) {
                    return null;
                }
            } else {
                str3 = str;
            }
            constructor = Class.forName(str3, false, context.getClassLoader()).asSubclass(View.class).getConstructor(f261a);
            f264d.put(str, constructor);
        }
        constructor.setAccessible(true);
        return (View) constructor.newInstance(this.e);
    }

    private void a(View view, AttributeSet attributeSet) {
        Context context = view.getContext();
        if (!(context instanceof ContextWrapper)) {
            return;
        }
        if (Build.VERSION.SDK_INT < 15 || ViewCompat.o(view)) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, f262b);
            String string = obtainStyledAttributes.getString(0);
            if (string != null) {
                view.setOnClickListener(new a(view, string));
            }
            obtainStyledAttributes.recycle();
        }
    }

    private void a(View view, String str) {
        if (view == null) {
            throw new IllegalStateException(AppCompatViewInflater.class.getName() + " asked to inflate view for <" + str + ">, but returned null");
        }
    }

    private View b(Context context, String str, AttributeSet attributeSet) {
        if (str.equals("view")) {
            str = attributeSet.getAttributeValue((String) null, "class");
        }
        try {
            this.e[0] = context;
            this.e[1] = attributeSet;
            if (-1 == str.indexOf(46)) {
                for (String a2 : f263c) {
                    View a3 = a(context, str, a2);
                    if (a3 != null) {
                        return a3;
                    }
                }
                Object[] objArr = this.e;
                objArr[0] = null;
                objArr[1] = null;
                return null;
            }
            View a4 = a(context, str, (String) null);
            Object[] objArr2 = this.e;
            objArr2[0] = null;
            objArr2[1] = null;
            return a4;
        } catch (Exception unused) {
            return null;
        } finally {
            Object[] objArr3 = this.e;
            objArr3[0] = null;
            objArr3[1] = null;
        }
    }

    /* access modifiers changed from: protected */
    @Nullable
    public View a(Context context, String str, AttributeSet attributeSet) {
        return null;
    }

    /* access modifiers changed from: package-private */
    public final View a(View view, String str, @NonNull Context context, @NonNull AttributeSet attributeSet, boolean z, boolean z2, boolean z3, boolean z4) {
        View view2;
        Context context2 = (!z || view == null) ? context : view.getContext();
        if (z2 || z3) {
            context2 = a(context2, attributeSet, z2, z3);
        }
        if (z4) {
            context2 = sa.a(context2);
        }
        char c2 = 65535;
        switch (str.hashCode()) {
            case -1946472170:
                if (str.equals("RatingBar")) {
                    c2 = 11;
                    break;
                }
                break;
            case -1455429095:
                if (str.equals("CheckedTextView")) {
                    c2 = 8;
                    break;
                }
                break;
            case -1346021293:
                if (str.equals("MultiAutoCompleteTextView")) {
                    c2 = 10;
                    break;
                }
                break;
            case -938935918:
                if (str.equals("TextView")) {
                    c2 = 0;
                    break;
                }
                break;
            case -937446323:
                if (str.equals("ImageButton")) {
                    c2 = 5;
                    break;
                }
                break;
            case -658531749:
                if (str.equals("SeekBar")) {
                    c2 = 12;
                    break;
                }
                break;
            case -339785223:
                if (str.equals("Spinner")) {
                    c2 = 4;
                    break;
                }
                break;
            case 776382189:
                if (str.equals("RadioButton")) {
                    c2 = 7;
                    break;
                }
                break;
            case 799298502:
                if (str.equals("ToggleButton")) {
                    c2 = 13;
                    break;
                }
                break;
            case 1125864064:
                if (str.equals("ImageView")) {
                    c2 = 1;
                    break;
                }
                break;
            case 1413872058:
                if (str.equals("AutoCompleteTextView")) {
                    c2 = 9;
                    break;
                }
                break;
            case 1601505219:
                if (str.equals(ConfigFile.TAG_CHECK_BOX)) {
                    c2 = 6;
                    break;
                }
                break;
            case 1666676343:
                if (str.equals("EditText")) {
                    c2 = 3;
                    break;
                }
                break;
            case 2001146706:
                if (str.equals(ButtonScreenElement.TAG_NAME)) {
                    c2 = 2;
                    break;
                }
                break;
        }
        switch (c2) {
            case 0:
                view2 = m(context2, attributeSet);
                break;
            case 1:
                view2 = g(context2, attributeSet);
                break;
            case 2:
                view2 = b(context2, attributeSet);
                break;
            case 3:
                view2 = e(context2, attributeSet);
                break;
            case 4:
                view2 = l(context2, attributeSet);
                break;
            case 5:
                view2 = f(context2, attributeSet);
                break;
            case 6:
                view2 = c(context2, attributeSet);
                break;
            case 7:
                view2 = i(context2, attributeSet);
                break;
            case 8:
                view2 = d(context2, attributeSet);
                break;
            case 9:
                view2 = a(context2, attributeSet);
                break;
            case 10:
                view2 = h(context2, attributeSet);
                break;
            case 11:
                view2 = j(context2, attributeSet);
                break;
            case 12:
                view2 = k(context2, attributeSet);
                break;
            case 13:
                view2 = n(context2, attributeSet);
                break;
            default:
                view2 = a(context2, str, attributeSet);
                break;
        }
        a(view2, str);
        if (view2 == null && context != context2) {
            view2 = b(context2, str, attributeSet);
        }
        if (view2 != null) {
            a(view2, attributeSet);
        }
        return view2;
    }

    /* access modifiers changed from: protected */
    @NonNull
    public C0103i a(Context context, AttributeSet attributeSet) {
        return new C0103i(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public C0107k b(Context context, AttributeSet attributeSet) {
        return new C0107k(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public C0109l c(Context context, AttributeSet attributeSet) {
        return new C0109l(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public AppCompatCheckedTextView d(Context context, AttributeSet attributeSet) {
        return new AppCompatCheckedTextView(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public C0113p e(Context context, AttributeSet attributeSet) {
        return new C0113p(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public r f(Context context, AttributeSet attributeSet) {
        return new r(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public AppCompatImageView g(Context context, AttributeSet attributeSet) {
        return new AppCompatImageView(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public C0116t h(Context context, AttributeSet attributeSet) {
        return new C0116t(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public AppCompatRadioButton i(Context context, AttributeSet attributeSet) {
        return new AppCompatRadioButton(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public C0119w j(Context context, AttributeSet attributeSet) {
        return new C0119w(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public C0120x k(Context context, AttributeSet attributeSet) {
        return new C0120x(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public AppCompatSpinner l(Context context, AttributeSet attributeSet) {
        return new AppCompatSpinner(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public I m(Context context, AttributeSet attributeSet) {
        return new I(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public K n(Context context, AttributeSet attributeSet) {
        return new K(context, attributeSet);
    }
}

package androidx.appcompat.view.menu;

import a.a.a;
import a.a.f;
import a.a.g;
import a.a.j;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.t;
import androidx.appcompat.widget.va;
import androidx.core.view.ViewCompat;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class ListMenuItemView extends LinearLayout implements t.a, AbsListView.SelectionBoundsAdjuster {

    /* renamed from: a  reason: collision with root package name */
    private n f356a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f357b;

    /* renamed from: c  reason: collision with root package name */
    private RadioButton f358c;

    /* renamed from: d  reason: collision with root package name */
    private TextView f359d;
    private CheckBox e;
    private TextView f;
    private ImageView g;
    private ImageView h;
    private LinearLayout i;
    private Drawable j;
    private int k;
    private Context l;
    private boolean m;
    private Drawable n;
    private boolean o;
    private LayoutInflater p;
    private boolean q;

    public ListMenuItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, a.listMenuViewStyle);
    }

    public ListMenuItemView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet);
        va a2 = va.a(getContext(), attributeSet, j.MenuView, i2, 0);
        this.j = a2.b(j.MenuView_android_itemBackground);
        this.k = a2.g(j.MenuView_android_itemTextAppearance, -1);
        this.m = a2.a(j.MenuView_preserveIconSpacing, false);
        this.l = context;
        this.n = a2.b(j.MenuView_subMenuArrow);
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes((AttributeSet) null, new int[]{16843049}, a.dropDownListViewStyle, 0);
        this.o = obtainStyledAttributes.hasValue(0);
        a2.b();
        obtainStyledAttributes.recycle();
    }

    private void a() {
        this.e = (CheckBox) getInflater().inflate(g.abc_list_menu_item_checkbox, this, false);
        a(this.e);
    }

    private void a(View view) {
        a(view, -1);
    }

    private void a(View view, int i2) {
        LinearLayout linearLayout = this.i;
        if (linearLayout != null) {
            linearLayout.addView(view, i2);
        } else {
            addView(view, i2);
        }
    }

    private void b() {
        this.f357b = (ImageView) getInflater().inflate(g.abc_list_menu_item_icon, this, false);
        a((View) this.f357b, 0);
    }

    private void d() {
        this.f358c = (RadioButton) getInflater().inflate(g.abc_list_menu_item_radio, this, false);
        a(this.f358c);
    }

    private LayoutInflater getInflater() {
        if (this.p == null) {
            this.p = LayoutInflater.from(getContext());
        }
        return this.p;
    }

    private void setSubMenuArrowVisible(boolean z) {
        ImageView imageView = this.g;
        if (imageView != null) {
            imageView.setVisibility(z ? 0 : 8);
        }
    }

    public void a(n nVar, int i2) {
        this.f356a = nVar;
        setVisibility(nVar.isVisible() ? 0 : 8);
        setTitle(nVar.a((t.a) this));
        setCheckable(nVar.isCheckable());
        a(nVar.l(), nVar.c());
        setIcon(nVar.getIcon());
        setEnabled(nVar.isEnabled());
        setSubMenuArrowVisible(nVar.hasSubMenu());
        setContentDescription(nVar.getContentDescription());
    }

    public void a(boolean z, char c2) {
        int i2 = (!z || !this.f356a.l()) ? 8 : 0;
        if (i2 == 0) {
            this.f.setText(this.f356a.d());
        }
        if (this.f.getVisibility() != i2) {
            this.f.setVisibility(i2);
        }
    }

    public void adjustListItemSelectionBounds(Rect rect) {
        ImageView imageView = this.h;
        if (imageView != null && imageView.getVisibility() == 0) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.h.getLayoutParams();
            rect.top += this.h.getHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
        }
    }

    public boolean c() {
        return false;
    }

    public n getItemData() {
        return this.f356a;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        ViewCompat.a((View) this, this.j);
        this.f359d = (TextView) findViewById(f.title);
        int i2 = this.k;
        if (i2 != -1) {
            this.f359d.setTextAppearance(this.l, i2);
        }
        this.f = (TextView) findViewById(f.shortcut);
        this.g = (ImageView) findViewById(f.submenuarrow);
        ImageView imageView = this.g;
        if (imageView != null) {
            imageView.setImageDrawable(this.n);
        }
        this.h = (ImageView) findViewById(f.group_divider);
        this.i = (LinearLayout) findViewById(f.content);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        if (this.f357b != null && this.m) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.f357b.getLayoutParams();
            if (layoutParams.height > 0 && layoutParams2.width <= 0) {
                layoutParams2.width = layoutParams.height;
            }
        }
        super.onMeasure(i2, i3);
    }

    public void setCheckable(boolean z) {
        CompoundButton compoundButton;
        CompoundButton compoundButton2;
        if (z || this.f358c != null || this.e != null) {
            if (this.f356a.h()) {
                if (this.f358c == null) {
                    d();
                }
                compoundButton2 = this.f358c;
                compoundButton = this.e;
            } else {
                if (this.e == null) {
                    a();
                }
                compoundButton2 = this.e;
                compoundButton = this.f358c;
            }
            if (z) {
                compoundButton2.setChecked(this.f356a.isChecked());
                if (compoundButton2.getVisibility() != 0) {
                    compoundButton2.setVisibility(0);
                }
                if (compoundButton != null && compoundButton.getVisibility() != 8) {
                    compoundButton.setVisibility(8);
                    return;
                }
                return;
            }
            CheckBox checkBox = this.e;
            if (checkBox != null) {
                checkBox.setVisibility(8);
            }
            RadioButton radioButton = this.f358c;
            if (radioButton != null) {
                radioButton.setVisibility(8);
            }
        }
    }

    public void setChecked(boolean z) {
        CompoundButton compoundButton;
        if (this.f356a.h()) {
            if (this.f358c == null) {
                d();
            }
            compoundButton = this.f358c;
        } else {
            if (this.e == null) {
                a();
            }
            compoundButton = this.e;
        }
        compoundButton.setChecked(z);
    }

    public void setForceShowIcon(boolean z) {
        this.q = z;
        this.m = z;
    }

    public void setGroupDividerEnabled(boolean z) {
        ImageView imageView = this.h;
        if (imageView != null) {
            imageView.setVisibility((this.o || !z) ? 8 : 0);
        }
    }

    public void setIcon(Drawable drawable) {
        boolean z = this.f356a.k() || this.q;
        if (!z && !this.m) {
            return;
        }
        if (this.f357b != null || drawable != null || this.m) {
            if (this.f357b == null) {
                b();
            }
            if (drawable != null || this.m) {
                ImageView imageView = this.f357b;
                if (!z) {
                    drawable = null;
                }
                imageView.setImageDrawable(drawable);
                if (this.f357b.getVisibility() != 0) {
                    this.f357b.setVisibility(0);
                    return;
                }
                return;
            }
            this.f357b.setVisibility(8);
        }
    }

    public void setTitle(CharSequence charSequence) {
        TextView textView;
        int i2;
        if (charSequence != null) {
            this.f359d.setText(charSequence);
            if (this.f359d.getVisibility() != 0) {
                textView = this.f359d;
                i2 = 0;
            } else {
                return;
            }
        } else {
            i2 = 8;
            if (this.f359d.getVisibility() != 8) {
                textView = this.f359d;
            } else {
                return;
            }
        }
        textView.setVisibility(i2);
    }
}

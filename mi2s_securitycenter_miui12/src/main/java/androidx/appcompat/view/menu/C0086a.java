package androidx.appcompat.view.menu;

import a.d.b.a.b;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.graphics.drawable.a;
import androidx.core.view.C0124b;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
/* renamed from: androidx.appcompat.view.menu.a  reason: case insensitive filesystem */
public class C0086a implements b {

    /* renamed from: a  reason: collision with root package name */
    private final int f360a;

    /* renamed from: b  reason: collision with root package name */
    private final int f361b;

    /* renamed from: c  reason: collision with root package name */
    private final int f362c;

    /* renamed from: d  reason: collision with root package name */
    private CharSequence f363d;
    private CharSequence e;
    private Intent f;
    private char g;
    private int h = MpegAudioHeader.MAX_FRAME_SIZE_BYTES;
    private char i;
    private int j = MpegAudioHeader.MAX_FRAME_SIZE_BYTES;
    private Drawable k;
    private Context l;
    private MenuItem.OnMenuItemClickListener m;
    private CharSequence n;
    private CharSequence o;
    private ColorStateList p = null;
    private PorterDuff.Mode q = null;
    private boolean r = false;
    private boolean s = false;
    private int t = 16;

    public C0086a(Context context, int i2, int i3, int i4, int i5, CharSequence charSequence) {
        this.l = context;
        this.f360a = i3;
        this.f361b = i2;
        this.f362c = i5;
        this.f363d = charSequence;
    }

    private void a() {
        if (this.k == null) {
            return;
        }
        if (this.r || this.s) {
            this.k = a.h(this.k);
            this.k = this.k.mutate();
            if (this.r) {
                a.a(this.k, this.p);
            }
            if (this.s) {
                a.a(this.k, this.q);
            }
        }
    }

    public b a(C0124b bVar) {
        throw new UnsupportedOperationException();
    }

    public boolean collapseActionView() {
        return false;
    }

    public boolean expandActionView() {
        return false;
    }

    public ActionProvider getActionProvider() {
        throw new UnsupportedOperationException();
    }

    public View getActionView() {
        return null;
    }

    public int getAlphabeticModifiers() {
        return this.j;
    }

    public char getAlphabeticShortcut() {
        return this.i;
    }

    public CharSequence getContentDescription() {
        return this.n;
    }

    public int getGroupId() {
        return this.f361b;
    }

    public Drawable getIcon() {
        return this.k;
    }

    public ColorStateList getIconTintList() {
        return this.p;
    }

    public PorterDuff.Mode getIconTintMode() {
        return this.q;
    }

    public Intent getIntent() {
        return this.f;
    }

    public int getItemId() {
        return this.f360a;
    }

    public ContextMenu.ContextMenuInfo getMenuInfo() {
        return null;
    }

    public int getNumericModifiers() {
        return this.h;
    }

    public char getNumericShortcut() {
        return this.g;
    }

    public int getOrder() {
        return this.f362c;
    }

    public SubMenu getSubMenu() {
        return null;
    }

    public C0124b getSupportActionProvider() {
        return null;
    }

    public CharSequence getTitle() {
        return this.f363d;
    }

    public CharSequence getTitleCondensed() {
        CharSequence charSequence = this.e;
        return charSequence != null ? charSequence : this.f363d;
    }

    public CharSequence getTooltipText() {
        return this.o;
    }

    public boolean hasSubMenu() {
        return false;
    }

    public boolean isActionViewExpanded() {
        return false;
    }

    public boolean isCheckable() {
        return (this.t & 1) != 0;
    }

    public boolean isChecked() {
        return (this.t & 2) != 0;
    }

    public boolean isEnabled() {
        return (this.t & 16) != 0;
    }

    public boolean isVisible() {
        return (this.t & 8) == 0;
    }

    public MenuItem setActionProvider(ActionProvider actionProvider) {
        throw new UnsupportedOperationException();
    }

    public b setActionView(int i2) {
        throw new UnsupportedOperationException();
    }

    public b setActionView(View view) {
        throw new UnsupportedOperationException();
    }

    public MenuItem setAlphabeticShortcut(char c2) {
        this.i = Character.toLowerCase(c2);
        return this;
    }

    public MenuItem setAlphabeticShortcut(char c2, int i2) {
        this.i = Character.toLowerCase(c2);
        this.j = KeyEvent.normalizeMetaState(i2);
        return this;
    }

    public MenuItem setCheckable(boolean z) {
        this.t = z | (this.t & true) ? 1 : 0;
        return this;
    }

    public MenuItem setChecked(boolean z) {
        this.t = (z ? 2 : 0) | (this.t & -3);
        return this;
    }

    public b setContentDescription(CharSequence charSequence) {
        this.n = charSequence;
        return this;
    }

    public MenuItem setEnabled(boolean z) {
        this.t = (z ? 16 : 0) | (this.t & -17);
        return this;
    }

    public MenuItem setIcon(int i2) {
        this.k = androidx.core.content.a.c(this.l, i2);
        a();
        return this;
    }

    public MenuItem setIcon(Drawable drawable) {
        this.k = drawable;
        a();
        return this;
    }

    public MenuItem setIconTintList(@Nullable ColorStateList colorStateList) {
        this.p = colorStateList;
        this.r = true;
        a();
        return this;
    }

    public MenuItem setIconTintMode(PorterDuff.Mode mode) {
        this.q = mode;
        this.s = true;
        a();
        return this;
    }

    public MenuItem setIntent(Intent intent) {
        this.f = intent;
        return this;
    }

    public MenuItem setNumericShortcut(char c2) {
        this.g = c2;
        return this;
    }

    public MenuItem setNumericShortcut(char c2, int i2) {
        this.g = c2;
        this.h = KeyEvent.normalizeMetaState(i2);
        return this;
    }

    public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener onActionExpandListener) {
        throw new UnsupportedOperationException();
    }

    public MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        this.m = onMenuItemClickListener;
        return this;
    }

    public MenuItem setShortcut(char c2, char c3) {
        this.g = c2;
        this.i = Character.toLowerCase(c3);
        return this;
    }

    public MenuItem setShortcut(char c2, char c3, int i2, int i3) {
        this.g = c2;
        this.h = KeyEvent.normalizeMetaState(i2);
        this.i = Character.toLowerCase(c3);
        this.j = KeyEvent.normalizeMetaState(i3);
        return this;
    }

    public void setShowAsAction(int i2) {
    }

    public b setShowAsActionFlags(int i2) {
        setShowAsAction(i2);
        return this;
    }

    public MenuItem setTitle(int i2) {
        this.f363d = this.l.getResources().getString(i2);
        return this;
    }

    public MenuItem setTitle(CharSequence charSequence) {
        this.f363d = charSequence;
        return this;
    }

    public MenuItem setTitleCondensed(CharSequence charSequence) {
        this.e = charSequence;
        return this;
    }

    public b setTooltipText(CharSequence charSequence) {
        this.o = charSequence;
        return this;
    }

    public MenuItem setVisible(boolean z) {
        int i2 = 8;
        int i3 = this.t & 8;
        if (z) {
            i2 = 0;
        }
        this.t = i3 | i2;
        return this;
    }
}

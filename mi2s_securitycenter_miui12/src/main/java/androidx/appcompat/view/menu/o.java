package androidx.appcompat.view.menu;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.ActionProvider;
import android.view.CollapsibleActionView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.core.view.C0124b;
import java.lang.reflect.Method;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class o extends c implements MenuItem {

    /* renamed from: d  reason: collision with root package name */
    private final a.d.b.a.b f401d;
    private Method e;

    private class a extends C0124b {

        /* renamed from: d  reason: collision with root package name */
        final ActionProvider f402d;

        a(Context context, ActionProvider actionProvider) {
            super(context);
            this.f402d = actionProvider;
        }

        public void a(SubMenu subMenu) {
            this.f402d.onPrepareSubMenu(o.this.a(subMenu));
        }

        public boolean a() {
            return this.f402d.hasSubMenu();
        }

        public View c() {
            return this.f402d.onCreateActionView();
        }

        public boolean d() {
            return this.f402d.onPerformDefaultAction();
        }
    }

    @RequiresApi(16)
    private class b extends a implements ActionProvider.VisibilityListener {
        private C0124b.C0015b f;

        b(Context context, ActionProvider actionProvider) {
            super(context, actionProvider);
        }

        public View a(MenuItem menuItem) {
            return this.f402d.onCreateActionView(menuItem);
        }

        public void a(C0124b.C0015b bVar) {
            this.f = bVar;
            this.f402d.setVisibilityListener(bVar != null ? this : null);
        }

        public boolean b() {
            return this.f402d.isVisible();
        }

        public boolean e() {
            return this.f402d.overridesItemVisibility();
        }

        public void onActionProviderVisibilityChanged(boolean z) {
            C0124b.C0015b bVar = this.f;
            if (bVar != null) {
                bVar.onActionProviderVisibilityChanged(z);
            }
        }
    }

    static class c extends FrameLayout implements a.a.d.c {

        /* renamed from: a  reason: collision with root package name */
        final CollapsibleActionView f403a;

        c(View view) {
            super(view.getContext());
            this.f403a = (CollapsibleActionView) view;
            addView(view);
        }

        /* access modifiers changed from: package-private */
        public View a() {
            return (View) this.f403a;
        }

        public void onActionViewCollapsed() {
            this.f403a.onActionViewCollapsed();
        }

        public void onActionViewExpanded() {
            this.f403a.onActionViewExpanded();
        }
    }

    private class d implements MenuItem.OnActionExpandListener {

        /* renamed from: a  reason: collision with root package name */
        private final MenuItem.OnActionExpandListener f404a;

        d(MenuItem.OnActionExpandListener onActionExpandListener) {
            this.f404a = onActionExpandListener;
        }

        public boolean onMenuItemActionCollapse(MenuItem menuItem) {
            return this.f404a.onMenuItemActionCollapse(o.this.a(menuItem));
        }

        public boolean onMenuItemActionExpand(MenuItem menuItem) {
            return this.f404a.onMenuItemActionExpand(o.this.a(menuItem));
        }
    }

    private class e implements MenuItem.OnMenuItemClickListener {

        /* renamed from: a  reason: collision with root package name */
        private final MenuItem.OnMenuItemClickListener f406a;

        e(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
            this.f406a = onMenuItemClickListener;
        }

        public boolean onMenuItemClick(MenuItem menuItem) {
            return this.f406a.onMenuItemClick(o.this.a(menuItem));
        }
    }

    public o(Context context, a.d.b.a.b bVar) {
        super(context);
        if (bVar != null) {
            this.f401d = bVar;
            return;
        }
        throw new IllegalArgumentException("Wrapped Object can not be null.");
    }

    public void a(boolean z) {
        try {
            if (this.e == null) {
                this.e = this.f401d.getClass().getDeclaredMethod("setExclusiveCheckable", new Class[]{Boolean.TYPE});
            }
            this.e.invoke(this.f401d, new Object[]{Boolean.valueOf(z)});
        } catch (Exception e2) {
            Log.w("MenuItemWrapper", "Error while calling setExclusiveCheckable", e2);
        }
    }

    public boolean collapseActionView() {
        return this.f401d.collapseActionView();
    }

    public boolean expandActionView() {
        return this.f401d.expandActionView();
    }

    public ActionProvider getActionProvider() {
        C0124b supportActionProvider = this.f401d.getSupportActionProvider();
        if (supportActionProvider instanceof a) {
            return ((a) supportActionProvider).f402d;
        }
        return null;
    }

    public View getActionView() {
        View actionView = this.f401d.getActionView();
        return actionView instanceof c ? ((c) actionView).a() : actionView;
    }

    public int getAlphabeticModifiers() {
        return this.f401d.getAlphabeticModifiers();
    }

    public char getAlphabeticShortcut() {
        return this.f401d.getAlphabeticShortcut();
    }

    public CharSequence getContentDescription() {
        return this.f401d.getContentDescription();
    }

    public int getGroupId() {
        return this.f401d.getGroupId();
    }

    public Drawable getIcon() {
        return this.f401d.getIcon();
    }

    public ColorStateList getIconTintList() {
        return this.f401d.getIconTintList();
    }

    public PorterDuff.Mode getIconTintMode() {
        return this.f401d.getIconTintMode();
    }

    public Intent getIntent() {
        return this.f401d.getIntent();
    }

    public int getItemId() {
        return this.f401d.getItemId();
    }

    public ContextMenu.ContextMenuInfo getMenuInfo() {
        return this.f401d.getMenuInfo();
    }

    public int getNumericModifiers() {
        return this.f401d.getNumericModifiers();
    }

    public char getNumericShortcut() {
        return this.f401d.getNumericShortcut();
    }

    public int getOrder() {
        return this.f401d.getOrder();
    }

    public SubMenu getSubMenu() {
        return a(this.f401d.getSubMenu());
    }

    public CharSequence getTitle() {
        return this.f401d.getTitle();
    }

    public CharSequence getTitleCondensed() {
        return this.f401d.getTitleCondensed();
    }

    public CharSequence getTooltipText() {
        return this.f401d.getTooltipText();
    }

    public boolean hasSubMenu() {
        return this.f401d.hasSubMenu();
    }

    public boolean isActionViewExpanded() {
        return this.f401d.isActionViewExpanded();
    }

    public boolean isCheckable() {
        return this.f401d.isCheckable();
    }

    public boolean isChecked() {
        return this.f401d.isChecked();
    }

    public boolean isEnabled() {
        return this.f401d.isEnabled();
    }

    public boolean isVisible() {
        return this.f401d.isVisible();
    }

    public MenuItem setActionProvider(ActionProvider actionProvider) {
        C0124b bVar = Build.VERSION.SDK_INT >= 16 ? new b(this.f368a, actionProvider) : new a(this.f368a, actionProvider);
        a.d.b.a.b bVar2 = this.f401d;
        if (actionProvider == null) {
            bVar = null;
        }
        bVar2.a(bVar);
        return this;
    }

    public MenuItem setActionView(int i) {
        this.f401d.setActionView(i);
        View actionView = this.f401d.getActionView();
        if (actionView instanceof CollapsibleActionView) {
            this.f401d.setActionView((View) new c(actionView));
        }
        return this;
    }

    public MenuItem setActionView(View view) {
        if (view instanceof CollapsibleActionView) {
            view = new c(view);
        }
        this.f401d.setActionView(view);
        return this;
    }

    public MenuItem setAlphabeticShortcut(char c2) {
        this.f401d.setAlphabeticShortcut(c2);
        return this;
    }

    public MenuItem setAlphabeticShortcut(char c2, int i) {
        this.f401d.setAlphabeticShortcut(c2, i);
        return this;
    }

    public MenuItem setCheckable(boolean z) {
        this.f401d.setCheckable(z);
        return this;
    }

    public MenuItem setChecked(boolean z) {
        this.f401d.setChecked(z);
        return this;
    }

    public MenuItem setContentDescription(CharSequence charSequence) {
        this.f401d.setContentDescription(charSequence);
        return this;
    }

    public MenuItem setEnabled(boolean z) {
        this.f401d.setEnabled(z);
        return this;
    }

    public MenuItem setIcon(int i) {
        this.f401d.setIcon(i);
        return this;
    }

    public MenuItem setIcon(Drawable drawable) {
        this.f401d.setIcon(drawable);
        return this;
    }

    public MenuItem setIconTintList(ColorStateList colorStateList) {
        this.f401d.setIconTintList(colorStateList);
        return this;
    }

    public MenuItem setIconTintMode(PorterDuff.Mode mode) {
        this.f401d.setIconTintMode(mode);
        return this;
    }

    public MenuItem setIntent(Intent intent) {
        this.f401d.setIntent(intent);
        return this;
    }

    public MenuItem setNumericShortcut(char c2) {
        this.f401d.setNumericShortcut(c2);
        return this;
    }

    public MenuItem setNumericShortcut(char c2, int i) {
        this.f401d.setNumericShortcut(c2, i);
        return this;
    }

    public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener onActionExpandListener) {
        this.f401d.setOnActionExpandListener(onActionExpandListener != null ? new d(onActionExpandListener) : null);
        return this;
    }

    public MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        this.f401d.setOnMenuItemClickListener(onMenuItemClickListener != null ? new e(onMenuItemClickListener) : null);
        return this;
    }

    public MenuItem setShortcut(char c2, char c3) {
        this.f401d.setShortcut(c2, c3);
        return this;
    }

    public MenuItem setShortcut(char c2, char c3, int i, int i2) {
        this.f401d.setShortcut(c2, c3, i, i2);
        return this;
    }

    public void setShowAsAction(int i) {
        this.f401d.setShowAsAction(i);
    }

    public MenuItem setShowAsActionFlags(int i) {
        this.f401d.setShowAsActionFlags(i);
        return this;
    }

    public MenuItem setTitle(int i) {
        this.f401d.setTitle(i);
        return this;
    }

    public MenuItem setTitle(CharSequence charSequence) {
        this.f401d.setTitle(charSequence);
        return this;
    }

    public MenuItem setTitleCondensed(CharSequence charSequence) {
        this.f401d.setTitleCondensed(charSequence);
        return this;
    }

    public MenuItem setTooltipText(CharSequence charSequence) {
        this.f401d.setTooltipText(charSequence);
        return this;
    }

    public MenuItem setVisible(boolean z) {
        return this.f401d.setVisible(z);
    }
}

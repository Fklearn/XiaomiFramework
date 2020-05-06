package a.a.d;

import a.a.d.b;
import a.c.i;
import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.o;
import androidx.appcompat.view.menu.u;
import java.util.ArrayList;

@RestrictTo({RestrictTo.a.f224c})
public class f extends ActionMode {

    /* renamed from: a  reason: collision with root package name */
    final Context f15a;

    /* renamed from: b  reason: collision with root package name */
    final b f16b;

    @RestrictTo({RestrictTo.a.f224c})
    public static class a implements b.a {

        /* renamed from: a  reason: collision with root package name */
        final ActionMode.Callback f17a;

        /* renamed from: b  reason: collision with root package name */
        final Context f18b;

        /* renamed from: c  reason: collision with root package name */
        final ArrayList<f> f19c = new ArrayList<>();

        /* renamed from: d  reason: collision with root package name */
        final i<Menu, Menu> f20d = new i<>();

        public a(Context context, ActionMode.Callback callback) {
            this.f18b = context;
            this.f17a = callback;
        }

        private Menu a(Menu menu) {
            Menu menu2 = this.f20d.get(menu);
            if (menu2 != null) {
                return menu2;
            }
            u uVar = new u(this.f18b, (a.d.b.a.a) menu);
            this.f20d.put(menu, uVar);
            return uVar;
        }

        public void a(b bVar) {
            this.f17a.onDestroyActionMode(b(bVar));
        }

        public boolean a(b bVar, Menu menu) {
            return this.f17a.onCreateActionMode(b(bVar), a(menu));
        }

        public boolean a(b bVar, MenuItem menuItem) {
            return this.f17a.onActionItemClicked(b(bVar), new o(this.f18b, (a.d.b.a.b) menuItem));
        }

        public ActionMode b(b bVar) {
            int size = this.f19c.size();
            for (int i = 0; i < size; i++) {
                f fVar = this.f19c.get(i);
                if (fVar != null && fVar.f16b == bVar) {
                    return fVar;
                }
            }
            f fVar2 = new f(this.f18b, bVar);
            this.f19c.add(fVar2);
            return fVar2;
        }

        public boolean b(b bVar, Menu menu) {
            return this.f17a.onPrepareActionMode(b(bVar), a(menu));
        }
    }

    public f(Context context, b bVar) {
        this.f15a = context;
        this.f16b = bVar;
    }

    public void finish() {
        this.f16b.a();
    }

    public View getCustomView() {
        return this.f16b.b();
    }

    public Menu getMenu() {
        return new u(this.f15a, (a.d.b.a.a) this.f16b.c());
    }

    public MenuInflater getMenuInflater() {
        return this.f16b.d();
    }

    public CharSequence getSubtitle() {
        return this.f16b.e();
    }

    public Object getTag() {
        return this.f16b.f();
    }

    public CharSequence getTitle() {
        return this.f16b.g();
    }

    public boolean getTitleOptionalHint() {
        return this.f16b.h();
    }

    public void invalidate() {
        this.f16b.i();
    }

    public boolean isTitleOptional() {
        return this.f16b.j();
    }

    public void setCustomView(View view) {
        this.f16b.a(view);
    }

    public void setSubtitle(int i) {
        this.f16b.a(i);
    }

    public void setSubtitle(CharSequence charSequence) {
        this.f16b.a(charSequence);
    }

    public void setTag(Object obj) {
        this.f16b.a(obj);
    }

    public void setTitle(int i) {
        this.f16b.b(i);
    }

    public void setTitle(CharSequence charSequence) {
        this.f16b.b(charSequence);
    }

    public void setTitleOptionalHint(boolean z) {
        this.f16b.a(z);
    }
}

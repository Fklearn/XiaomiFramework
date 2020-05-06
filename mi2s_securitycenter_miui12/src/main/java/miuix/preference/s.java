package miuix.preference;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Ja;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.r;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.util.AttributeResolver;
import miuix.springback.view.SpringBackLayout;

public abstract class s extends r {
    private static final String DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG";
    /* access modifiers changed from: private */
    public boolean mAdapterInvalid = true;
    private a mFrameDecoration;
    /* access modifiers changed from: private */
    public u mGroupAdapter;

    private class a extends RecyclerView.f {

        /* renamed from: a  reason: collision with root package name */
        private Paint f8906a;

        /* renamed from: b  reason: collision with root package name */
        private int f8907b;

        /* renamed from: c  reason: collision with root package name */
        private int f8908c;

        /* renamed from: d  reason: collision with root package name */
        private int f8909d;
        private int e;
        private int f;
        private b g;
        private Map<Integer, b> h;
        private int i;

        private a(Context context) {
            this.f8907b = context.getResources().getDimensionPixelSize(w.miuix_preference_checkable_item_mask_padding_top);
            this.f8908c = context.getResources().getDimensionPixelSize(w.miuix_preference_checkable_item_mask_padding_bottom);
            this.f8909d = context.getResources().getDimensionPixelSize(w.miuix_preference_checkable_item_mask_padding_start);
            this.e = context.getResources().getDimensionPixelSize(w.miuix_preference_checkable_item_mask_padding_end);
            this.f = context.getResources().getDimensionPixelSize(w.miuix_preference_checkable_item_mask_radius);
            this.f8906a = new Paint();
            this.f8906a.setColor(AttributeResolver.resolveColor(context, v.preferenceCheckableMaskColor));
            this.f8906a.setAntiAlias(true);
            this.h = new HashMap();
            this.i = s.this.getContext().getResources().getDisplayMetrics().heightPixels;
        }

        private int a(RecyclerView recyclerView, View view, int i2, int i3, boolean z) {
            View childAt;
            if (!z) {
                for (int i4 = i2 - 1; i4 > i3; i4--) {
                    View childAt2 = recyclerView.getChildAt(i4);
                    if (childAt2 != null) {
                        return ((int) childAt2.getY()) + childAt2.getHeight();
                    }
                }
            } else if (view == null || view.getBottom() + view.getHeight() >= this.i) {
                return -1;
            } else {
                do {
                    i2++;
                    if (i2 < i3) {
                        childAt = recyclerView.getChildAt(i2);
                    }
                } while (childAt == null);
                return (int) childAt.getY();
            }
            return -1;
        }

        private void a(@NonNull Canvas canvas, int i2, int i3, int i4, int i5, boolean z, boolean z2, boolean z3, boolean z4) {
            PorterDuffXfermode porterDuffXfermode;
            Paint paint;
            if (!s.this.mAdapterInvalid) {
                float f2 = (float) i3;
                float f3 = (float) i5;
                RectF rectF = new RectF((float) i2, f2, (float) i4, f3);
                RectF rectF2 = new RectF((float) (i2 + (z4 ? this.e : this.f8909d)), f2, (float) (i4 - (z4 ? this.f8909d : this.e)), f3);
                Path path = new Path();
                float f4 = 0.0f;
                float f5 = z ? (float) this.f : 0.0f;
                if (z2) {
                    f4 = (float) this.f;
                }
                path.addRoundRect(rectF2, new float[]{f5, f5, f5, f5, f4, f4, f4, f4}, Path.Direction.CW);
                int saveLayer = canvas.saveLayer(rectF, this.f8906a, 31);
                canvas.drawRect(rectF, this.f8906a);
                if (z3) {
                    paint = this.f8906a;
                    porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
                } else {
                    paint = this.f8906a;
                    porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
                }
                paint.setXfermode(porterDuffXfermode);
                canvas.drawPath(path, this.f8906a);
                this.f8906a.setXfermode((Xfermode) null);
                canvas.restoreToCount(saveLayer);
            }
        }

        private void a(RecyclerView recyclerView, b bVar) {
            int i2;
            int size = bVar.f8910a.size();
            int i3 = 0;
            int i4 = 0;
            int i5 = -1;
            int i6 = -1;
            for (int i7 = 0; i7 < size; i7++) {
                View childAt = recyclerView.getChildAt(bVar.f8910a.get(i7).intValue());
                if (childAt != null) {
                    int top = childAt.getTop();
                    int bottom = childAt.getBottom();
                    if (i7 == 0) {
                        i4 = bottom;
                        i3 = top;
                    }
                    if (i3 > top) {
                        i3 = top;
                    }
                    if (i4 < bottom) {
                        i4 = bottom;
                    }
                }
                int i8 = bVar.f;
                if (i8 != -1 && i8 > bVar.e) {
                    i5 = i8 - this.f8907b;
                }
                int i9 = bVar.e;
                if (i9 != -1 && i9 < (i2 = bVar.f)) {
                    i6 = i2 - this.f8907b;
                }
            }
            bVar.f8912c = new int[]{i3, i4};
            if (i5 != -1) {
                i4 = i5;
            }
            if (i6 != -1) {
                i3 = i6;
            }
            bVar.f8911b = new int[]{i3, i4};
        }

        private boolean a(RecyclerView recyclerView, int i2, int i3) {
            int i4 = i2 + 1;
            if (i4 >= i3) {
                return false;
            }
            return !(s.this.mGroupAdapter.a(recyclerView.f(recyclerView.getChildAt(i4))) instanceof RadioSetPreferenceCategory);
        }

        public void a(@NonNull Rect rect, @NonNull View view, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.r rVar) {
            int f2;
            Preference a2;
            if (!s.this.mAdapterInvalid && (a2 = s.this.mGroupAdapter.a(f2)) != null && (a2.getParent() instanceof RadioSetPreferenceCategory)) {
                if (Ja.a(recyclerView)) {
                    rect.left = recyclerView.getScrollBarSize();
                } else {
                    rect.right = recyclerView.getScrollBarSize();
                }
                int b2 = s.this.mGroupAdapter.b((f2 = recyclerView.f(view)));
                if (b2 == 1) {
                    rect.top += this.f8907b;
                } else if (b2 == 2) {
                    rect.top += this.f8907b;
                    return;
                } else if (b2 != 4) {
                    return;
                }
                rect.bottom += this.f8908c;
            }
        }

        public void b(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.r rVar) {
            int i2;
            int i3;
            View view;
            int i4;
            RecyclerView recyclerView2 = recyclerView;
            if (!s.this.mAdapterInvalid) {
                this.h.clear();
                int childCount = recyclerView.getChildCount();
                int scrollBarSize = recyclerView.getScrollBarSize();
                boolean a2 = Ja.a(recyclerView);
                if (a2) {
                    i3 = scrollBarSize;
                    i2 = recyclerView.getWidth();
                } else {
                    i2 = recyclerView.getWidth() - scrollBarSize;
                    i3 = 0;
                }
                for (int i5 = 0; i5 < childCount; i5++) {
                    View childAt = recyclerView2.getChildAt(i5);
                    int f2 = recyclerView2.f(childAt);
                    Preference a3 = s.this.mGroupAdapter.a(f2);
                    if (a3 != null && (a3.getParent() instanceof RadioSetPreferenceCategory)) {
                        int b2 = s.this.mGroupAdapter.b(f2);
                        if (b2 == 1 || b2 == 2) {
                            this.g = new b();
                            b bVar = this.g;
                            bVar.h = true;
                            i4 = b2;
                            view = childAt;
                            bVar.e = a(recyclerView, childAt, i5, 0, false);
                            this.g.a(i5);
                        } else {
                            i4 = b2;
                            view = childAt;
                        }
                        if (this.g != null && (i4 == 4 || i4 == 3)) {
                            this.g.a(i5);
                        }
                        if (this.g != null && (i4 == 1 || i4 == 4)) {
                            this.g.f = a(recyclerView, view, i5, childCount, true);
                            this.g.f8913d = this.h.size();
                            this.g.g = a(recyclerView2, i5, childCount);
                            b bVar2 = this.g;
                            bVar2.i = 4;
                            this.h.put(Integer.valueOf(bVar2.f8913d), this.g);
                            this.g = null;
                        }
                    }
                }
                b bVar3 = this.g;
                if (bVar3 != null && bVar3.f8910a.size() > 0) {
                    b bVar4 = this.g;
                    bVar4.f = -1;
                    bVar4.f8913d = this.h.size();
                    b bVar5 = this.g;
                    bVar5.g = false;
                    bVar5.i = -1;
                    this.h.put(Integer.valueOf(bVar5.f8913d), this.g);
                    this.g = null;
                }
                Map<Integer, b> map = this.h;
                if (map != null && map.size() > 0) {
                    for (Map.Entry<Integer, b> value : this.h.entrySet()) {
                        a(recyclerView2, (b) value.getValue());
                    }
                    for (Map.Entry next : this.h.entrySet()) {
                        int intValue = ((Integer) next.getKey()).intValue();
                        b bVar6 = (b) next.getValue();
                        int i6 = bVar6.f8911b[1];
                        int i7 = intValue == 0 ? bVar6.f8912c[0] : bVar6.e + this.f8908c;
                        Canvas canvas2 = canvas;
                        int i8 = i3;
                        int i9 = i2;
                        boolean z = a2;
                        a(canvas2, i8, i7 - this.f8907b, i9, i7, false, false, true, z);
                        a(canvas2, i8, i6, i9, i6 + this.f8908c, false, false, true, z);
                        a(canvas2, i8, i7, i9, i6, true, true, false, z);
                    }
                }
            }
        }
    }

    private class b {

        /* renamed from: a  reason: collision with root package name */
        public List<Integer> f8910a;

        /* renamed from: b  reason: collision with root package name */
        public int[] f8911b;

        /* renamed from: c  reason: collision with root package name */
        public int[] f8912c;

        /* renamed from: d  reason: collision with root package name */
        public int f8913d;
        public int e;
        public int f;
        public boolean g;
        public boolean h;
        public int i;

        private b() {
            this.f8910a = new ArrayList();
            this.f8911b = null;
            this.f8912c = null;
            this.f8913d = 0;
            this.e = -1;
            this.f = -1;
            this.g = false;
            this.h = false;
            this.i = -1;
        }

        public void a(int i2) {
            this.f8910a.add(Integer.valueOf(i2));
        }

        public String toString() {
            return "PreferenceGroupRect{preferenceList=" + this.f8910a + ", currentMovetb=" + Arrays.toString(this.f8911b) + ", currentEndtb=" + Arrays.toString(this.f8912c) + ", index=" + this.f8913d + ", preViewHY=" + this.e + ", nextViewY=" + this.f + ", end=" + this.g + '}';
        }
    }

    /* access modifiers changed from: protected */
    public final RecyclerView.a onCreateAdapter(PreferenceScreen preferenceScreen) {
        this.mGroupAdapter = new u(preferenceScreen);
        boolean z = true;
        if (this.mGroupAdapter.getItemCount() >= 1) {
            z = false;
        }
        this.mAdapterInvalid = z;
        return this.mGroupAdapter;
    }

    public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        RecyclerView recyclerView = (RecyclerView) layoutInflater.inflate(y.miuix_preference_recyclerview, viewGroup, false);
        if (recyclerView instanceof miuix.recyclerview.widget.RecyclerView) {
            ((miuix.recyclerview.widget.RecyclerView) recyclerView).setSpringEnabled(false);
        }
        recyclerView.setLayoutManager(onCreateLayoutManager());
        this.mFrameDecoration = new a(recyclerView.getContext());
        recyclerView.a((RecyclerView.f) this.mFrameDecoration);
        if (viewGroup instanceof SpringBackLayout) {
            ((SpringBackLayout) viewGroup).setTarget(recyclerView);
        }
        return recyclerView;
    }

    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment;
        boolean a2 = getCallbackFragment() instanceof r.b ? ((r.b) getCallbackFragment()).a(this, preference) : false;
        if (!a2 && (getActivity() instanceof r.b)) {
            a2 = ((r.b) getActivity()).a(this, preference);
        }
        if (!a2 && getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) == null) {
            if (preference instanceof EditTextPreference) {
                dialogFragment = i.a(preference.getKey());
            } else if (preference instanceof ListPreference) {
                dialogFragment = m.a(preference.getKey());
            } else if (preference instanceof MultiSelectListPreference) {
                dialogFragment = o.a(preference.getKey());
            } else {
                throw new IllegalArgumentException("Cannot display dialog for an unknown Preference type: " + preference.getClass().getSimpleName() + ". Make sure to implement onPreferenceDisplayDialog() to handle " + "displaying a custom dialog for this Preference.");
            }
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
        }
    }
}

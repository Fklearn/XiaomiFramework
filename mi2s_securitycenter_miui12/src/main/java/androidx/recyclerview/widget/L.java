package androidx.recyclerview.widget;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.C0123a;
import androidx.core.view.ViewCompat;
import androidx.core.view.a.c;
import androidx.core.view.a.d;
import java.util.Map;
import java.util.WeakHashMap;

public class L extends C0123a {

    /* renamed from: a  reason: collision with root package name */
    final RecyclerView f1093a;

    /* renamed from: b  reason: collision with root package name */
    private final a f1094b;

    public static class a extends C0123a {

        /* renamed from: a  reason: collision with root package name */
        final L f1095a;

        /* renamed from: b  reason: collision with root package name */
        private Map<View, C0123a> f1096b = new WeakHashMap();

        public a(@NonNull L l) {
            this.f1095a = l;
        }

        /* access modifiers changed from: package-private */
        public C0123a a(View view) {
            return this.f1096b.remove(view);
        }

        /* access modifiers changed from: package-private */
        public void b(View view) {
            C0123a b2 = ViewCompat.b(view);
            if (b2 != null && b2 != this) {
                this.f1096b.put(view, b2);
            }
        }

        public boolean dispatchPopulateAccessibilityEvent(@NonNull View view, @NonNull AccessibilityEvent accessibilityEvent) {
            C0123a aVar = this.f1096b.get(view);
            return aVar != null ? aVar.dispatchPopulateAccessibilityEvent(view, accessibilityEvent) : super.dispatchPopulateAccessibilityEvent(view, accessibilityEvent);
        }

        @Nullable
        public d getAccessibilityNodeProvider(@NonNull View view) {
            C0123a aVar = this.f1096b.get(view);
            return aVar != null ? aVar.getAccessibilityNodeProvider(view) : super.getAccessibilityNodeProvider(view);
        }

        public void onInitializeAccessibilityEvent(@NonNull View view, @NonNull AccessibilityEvent accessibilityEvent) {
            C0123a aVar = this.f1096b.get(view);
            if (aVar != null) {
                aVar.onInitializeAccessibilityEvent(view, accessibilityEvent);
            } else {
                super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            }
        }

        public void onInitializeAccessibilityNodeInfo(View view, c cVar) {
            if (!this.f1095a.b() && this.f1095a.f1093a.getLayoutManager() != null) {
                this.f1095a.f1093a.getLayoutManager().a(view, cVar);
                C0123a aVar = this.f1096b.get(view);
                if (aVar != null) {
                    aVar.onInitializeAccessibilityNodeInfo(view, cVar);
                    return;
                }
            }
            super.onInitializeAccessibilityNodeInfo(view, cVar);
        }

        public void onPopulateAccessibilityEvent(@NonNull View view, @NonNull AccessibilityEvent accessibilityEvent) {
            C0123a aVar = this.f1096b.get(view);
            if (aVar != null) {
                aVar.onPopulateAccessibilityEvent(view, accessibilityEvent);
            } else {
                super.onPopulateAccessibilityEvent(view, accessibilityEvent);
            }
        }

        public boolean onRequestSendAccessibilityEvent(@NonNull ViewGroup viewGroup, @NonNull View view, @NonNull AccessibilityEvent accessibilityEvent) {
            C0123a aVar = this.f1096b.get(viewGroup);
            return aVar != null ? aVar.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent) : super.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
        }

        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            if (this.f1095a.b() || this.f1095a.f1093a.getLayoutManager() == null) {
                return super.performAccessibilityAction(view, i, bundle);
            }
            C0123a aVar = this.f1096b.get(view);
            if (aVar != null) {
                if (aVar.performAccessibilityAction(view, i, bundle)) {
                    return true;
                }
            } else if (super.performAccessibilityAction(view, i, bundle)) {
                return true;
            }
            return this.f1095a.f1093a.getLayoutManager().a(view, i, bundle);
        }

        public void sendAccessibilityEvent(@NonNull View view, int i) {
            C0123a aVar = this.f1096b.get(view);
            if (aVar != null) {
                aVar.sendAccessibilityEvent(view, i);
            } else {
                super.sendAccessibilityEvent(view, i);
            }
        }

        public void sendAccessibilityEventUnchecked(@NonNull View view, @NonNull AccessibilityEvent accessibilityEvent) {
            C0123a aVar = this.f1096b.get(view);
            if (aVar != null) {
                aVar.sendAccessibilityEventUnchecked(view, accessibilityEvent);
            } else {
                super.sendAccessibilityEventUnchecked(view, accessibilityEvent);
            }
        }
    }

    public L(@NonNull RecyclerView recyclerView) {
        this.f1093a = recyclerView;
        C0123a a2 = a();
        this.f1094b = (a2 == null || !(a2 instanceof a)) ? new a(this) : (a) a2;
    }

    @NonNull
    public C0123a a() {
        return this.f1094b;
    }

    /* access modifiers changed from: package-private */
    public boolean b() {
        return this.f1093a.j();
    }

    public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(view, accessibilityEvent);
        if ((view instanceof RecyclerView) && !b()) {
            RecyclerView recyclerView = (RecyclerView) view;
            if (recyclerView.getLayoutManager() != null) {
                recyclerView.getLayoutManager().a(accessibilityEvent);
            }
        }
    }

    public void onInitializeAccessibilityNodeInfo(View view, c cVar) {
        super.onInitializeAccessibilityNodeInfo(view, cVar);
        if (!b() && this.f1093a.getLayoutManager() != null) {
            this.f1093a.getLayoutManager().a(cVar);
        }
    }

    public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
        if (super.performAccessibilityAction(view, i, bundle)) {
            return true;
        }
        if (b() || this.f1093a.getLayoutManager() == null) {
            return false;
        }
        return this.f1093a.getLayoutManager().a(i, bundle);
    }
}

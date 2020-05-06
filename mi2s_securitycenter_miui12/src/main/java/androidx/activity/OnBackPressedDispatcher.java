package androidx.activity;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.f;
import androidx.lifecycle.g;
import androidx.lifecycle.i;
import java.util.ArrayDeque;
import java.util.Iterator;

public final class OnBackPressedDispatcher {
    @Nullable

    /* renamed from: a  reason: collision with root package name */
    private final Runnable f203a;

    /* renamed from: b  reason: collision with root package name */
    final ArrayDeque<d> f204b = new ArrayDeque<>();

    private class LifecycleOnBackPressedCancellable implements g, a {

        /* renamed from: a  reason: collision with root package name */
        private final f f205a;

        /* renamed from: b  reason: collision with root package name */
        private final d f206b;
        @Nullable

        /* renamed from: c  reason: collision with root package name */
        private a f207c;

        LifecycleOnBackPressedCancellable(@NonNull f fVar, @NonNull d dVar) {
            this.f205a = fVar;
            this.f206b = dVar;
            fVar.a(this);
        }

        public void a(@NonNull i iVar, @NonNull f.a aVar) {
            if (aVar == f.a.ON_START) {
                this.f207c = OnBackPressedDispatcher.this.a(this.f206b);
            } else if (aVar == f.a.ON_STOP) {
                a aVar2 = this.f207c;
                if (aVar2 != null) {
                    aVar2.cancel();
                }
            } else if (aVar == f.a.ON_DESTROY) {
                cancel();
            }
        }

        public void cancel() {
            this.f205a.b(this);
            this.f206b.b(this);
            a aVar = this.f207c;
            if (aVar != null) {
                aVar.cancel();
                this.f207c = null;
            }
        }
    }

    private class a implements a {

        /* renamed from: a  reason: collision with root package name */
        private final d f209a;

        a(d dVar) {
            this.f209a = dVar;
        }

        public void cancel() {
            OnBackPressedDispatcher.this.f204b.remove(this.f209a);
            this.f209a.b(this);
        }
    }

    public OnBackPressedDispatcher(@Nullable Runnable runnable) {
        this.f203a = runnable;
    }

    /* access modifiers changed from: package-private */
    @MainThread
    @NonNull
    public a a(@NonNull d dVar) {
        this.f204b.add(dVar);
        a aVar = new a(dVar);
        dVar.a((a) aVar);
        return aVar;
    }

    @MainThread
    public void a() {
        Iterator<d> descendingIterator = this.f204b.descendingIterator();
        while (descendingIterator.hasNext()) {
            d next = descendingIterator.next();
            if (next.b()) {
                next.a();
                return;
            }
        }
        Runnable runnable = this.f203a;
        if (runnable != null) {
            runnable.run();
        }
    }

    @MainThread
    public void a(@NonNull i iVar, @NonNull d dVar) {
        f a2 = iVar.a();
        if (a2.a() != f.b.DESTROYED) {
            dVar.a((a) new LifecycleOnBackPressedCancellable(a2, dVar));
        }
    }
}

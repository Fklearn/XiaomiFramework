package androidx.recyclerview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.a.c;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class StaggeredGridLayoutManager extends RecyclerView.g implements RecyclerView.q.a {
    boolean A = false;
    private BitSet B;
    int C = -1;
    int D = Integer.MIN_VALUE;
    LazySpanLookup E = new LazySpanLookup();
    private int F = 2;
    private boolean G;
    private boolean H;
    private SavedState I;
    private int J;
    private final Rect K = new Rect();
    private final a L = new a();
    private boolean M = false;
    private boolean N = true;
    private int[] O;
    private final Runnable P = new S(this);
    private int s = -1;
    c[] t;
    @NonNull
    B u;
    @NonNull
    B v;
    private int w;
    private int x;
    @NonNull
    private final C0180v y;
    boolean z = false;

    static class LazySpanLookup {

        /* renamed from: a  reason: collision with root package name */
        int[] f1155a;

        /* renamed from: b  reason: collision with root package name */
        List<FullSpanItem> f1156b;

        @SuppressLint({"BanParcelableUsage"})
        static class FullSpanItem implements Parcelable {
            public static final Parcelable.Creator<FullSpanItem> CREATOR = new T();
            int mGapDir;
            int[] mGapPerSpan;
            boolean mHasUnwantedGapAfter;
            int mPosition;

            FullSpanItem() {
            }

            FullSpanItem(Parcel parcel) {
                this.mPosition = parcel.readInt();
                this.mGapDir = parcel.readInt();
                this.mHasUnwantedGapAfter = parcel.readInt() != 1 ? false : true;
                int readInt = parcel.readInt();
                if (readInt > 0) {
                    this.mGapPerSpan = new int[readInt];
                    parcel.readIntArray(this.mGapPerSpan);
                }
            }

            public int describeContents() {
                return 0;
            }

            /* access modifiers changed from: package-private */
            public int getGapForSpan(int i) {
                int[] iArr = this.mGapPerSpan;
                if (iArr == null) {
                    return 0;
                }
                return iArr[i];
            }

            public String toString() {
                return "FullSpanItem{mPosition=" + this.mPosition + ", mGapDir=" + this.mGapDir + ", mHasUnwantedGapAfter=" + this.mHasUnwantedGapAfter + ", mGapPerSpan=" + Arrays.toString(this.mGapPerSpan) + '}';
            }

            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(this.mPosition);
                parcel.writeInt(this.mGapDir);
                parcel.writeInt(this.mHasUnwantedGapAfter ? 1 : 0);
                int[] iArr = this.mGapPerSpan;
                if (iArr == null || iArr.length <= 0) {
                    parcel.writeInt(0);
                    return;
                }
                parcel.writeInt(iArr.length);
                parcel.writeIntArray(this.mGapPerSpan);
            }
        }

        LazySpanLookup() {
        }

        private void c(int i, int i2) {
            List<FullSpanItem> list = this.f1156b;
            if (list != null) {
                for (int size = list.size() - 1; size >= 0; size--) {
                    FullSpanItem fullSpanItem = this.f1156b.get(size);
                    int i3 = fullSpanItem.mPosition;
                    if (i3 >= i) {
                        fullSpanItem.mPosition = i3 + i2;
                    }
                }
            }
        }

        private void d(int i, int i2) {
            List<FullSpanItem> list = this.f1156b;
            if (list != null) {
                int i3 = i + i2;
                for (int size = list.size() - 1; size >= 0; size--) {
                    FullSpanItem fullSpanItem = this.f1156b.get(size);
                    int i4 = fullSpanItem.mPosition;
                    if (i4 >= i) {
                        if (i4 < i3) {
                            this.f1156b.remove(size);
                        } else {
                            fullSpanItem.mPosition = i4 - i2;
                        }
                    }
                }
            }
        }

        private int g(int i) {
            if (this.f1156b == null) {
                return -1;
            }
            FullSpanItem c2 = c(i);
            if (c2 != null) {
                this.f1156b.remove(c2);
            }
            int size = this.f1156b.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    i2 = -1;
                    break;
                } else if (this.f1156b.get(i2).mPosition >= i) {
                    break;
                } else {
                    i2++;
                }
            }
            if (i2 == -1) {
                return -1;
            }
            this.f1156b.remove(i2);
            return this.f1156b.get(i2).mPosition;
        }

        public FullSpanItem a(int i, int i2, int i3, boolean z) {
            List<FullSpanItem> list = this.f1156b;
            if (list == null) {
                return null;
            }
            int size = list.size();
            for (int i4 = 0; i4 < size; i4++) {
                FullSpanItem fullSpanItem = this.f1156b.get(i4);
                int i5 = fullSpanItem.mPosition;
                if (i5 >= i2) {
                    return null;
                }
                if (i5 >= i && (i3 == 0 || fullSpanItem.mGapDir == i3 || (z && fullSpanItem.mHasUnwantedGapAfter))) {
                    return fullSpanItem;
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public void a() {
            int[] iArr = this.f1155a;
            if (iArr != null) {
                Arrays.fill(iArr, -1);
            }
            this.f1156b = null;
        }

        /* access modifiers changed from: package-private */
        public void a(int i) {
            int[] iArr = this.f1155a;
            if (iArr == null) {
                this.f1155a = new int[(Math.max(i, 10) + 1)];
                Arrays.fill(this.f1155a, -1);
            } else if (i >= iArr.length) {
                this.f1155a = new int[f(i)];
                System.arraycopy(iArr, 0, this.f1155a, 0, iArr.length);
                int[] iArr2 = this.f1155a;
                Arrays.fill(iArr2, iArr.length, iArr2.length, -1);
            }
        }

        /* access modifiers changed from: package-private */
        public void a(int i, int i2) {
            int[] iArr = this.f1155a;
            if (iArr != null && i < iArr.length) {
                int i3 = i + i2;
                a(i3);
                int[] iArr2 = this.f1155a;
                System.arraycopy(iArr2, i, iArr2, i3, (iArr2.length - i) - i2);
                Arrays.fill(this.f1155a, i, i3, -1);
                c(i, i2);
            }
        }

        /* access modifiers changed from: package-private */
        public void a(int i, c cVar) {
            a(i);
            this.f1155a[i] = cVar.e;
        }

        public void a(FullSpanItem fullSpanItem) {
            if (this.f1156b == null) {
                this.f1156b = new ArrayList();
            }
            int size = this.f1156b.size();
            for (int i = 0; i < size; i++) {
                FullSpanItem fullSpanItem2 = this.f1156b.get(i);
                if (fullSpanItem2.mPosition == fullSpanItem.mPosition) {
                    this.f1156b.remove(i);
                }
                if (fullSpanItem2.mPosition >= fullSpanItem.mPosition) {
                    this.f1156b.add(i, fullSpanItem);
                    return;
                }
            }
            this.f1156b.add(fullSpanItem);
        }

        /* access modifiers changed from: package-private */
        public int b(int i) {
            List<FullSpanItem> list = this.f1156b;
            if (list != null) {
                for (int size = list.size() - 1; size >= 0; size--) {
                    if (this.f1156b.get(size).mPosition >= i) {
                        this.f1156b.remove(size);
                    }
                }
            }
            return e(i);
        }

        /* access modifiers changed from: package-private */
        public void b(int i, int i2) {
            int[] iArr = this.f1155a;
            if (iArr != null && i < iArr.length) {
                int i3 = i + i2;
                a(i3);
                int[] iArr2 = this.f1155a;
                System.arraycopy(iArr2, i3, iArr2, i, (iArr2.length - i) - i2);
                int[] iArr3 = this.f1155a;
                Arrays.fill(iArr3, iArr3.length - i2, iArr3.length, -1);
                d(i, i2);
            }
        }

        public FullSpanItem c(int i) {
            List<FullSpanItem> list = this.f1156b;
            if (list == null) {
                return null;
            }
            for (int size = list.size() - 1; size >= 0; size--) {
                FullSpanItem fullSpanItem = this.f1156b.get(size);
                if (fullSpanItem.mPosition == i) {
                    return fullSpanItem;
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public int d(int i) {
            int[] iArr = this.f1155a;
            if (iArr == null || i >= iArr.length) {
                return -1;
            }
            return iArr[i];
        }

        /* access modifiers changed from: package-private */
        public int e(int i) {
            int[] iArr = this.f1155a;
            if (iArr == null || i >= iArr.length) {
                return -1;
            }
            int g = g(i);
            if (g == -1) {
                int[] iArr2 = this.f1155a;
                Arrays.fill(iArr2, i, iArr2.length, -1);
                return this.f1155a.length;
            }
            int i2 = g + 1;
            Arrays.fill(this.f1155a, i, i2, -1);
            return i2;
        }

        /* access modifiers changed from: package-private */
        public int f(int i) {
            int length = this.f1155a.length;
            while (length <= i) {
                length *= 2;
            }
            return length;
        }
    }

    @SuppressLint({"BanParcelableUsage"})
    @RestrictTo({RestrictTo.a.LIBRARY})
    public static class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR = new U();
        boolean mAnchorLayoutFromEnd;
        int mAnchorPosition;
        List<LazySpanLookup.FullSpanItem> mFullSpanItems;
        boolean mLastLayoutRTL;
        boolean mReverseLayout;
        int[] mSpanLookup;
        int mSpanLookupSize;
        int[] mSpanOffsets;
        int mSpanOffsetsSize;
        int mVisibleAnchorPosition;

        public SavedState() {
        }

        SavedState(Parcel parcel) {
            this.mAnchorPosition = parcel.readInt();
            this.mVisibleAnchorPosition = parcel.readInt();
            this.mSpanOffsetsSize = parcel.readInt();
            int i = this.mSpanOffsetsSize;
            if (i > 0) {
                this.mSpanOffsets = new int[i];
                parcel.readIntArray(this.mSpanOffsets);
            }
            this.mSpanLookupSize = parcel.readInt();
            int i2 = this.mSpanLookupSize;
            if (i2 > 0) {
                this.mSpanLookup = new int[i2];
                parcel.readIntArray(this.mSpanLookup);
            }
            boolean z = false;
            this.mReverseLayout = parcel.readInt() == 1;
            this.mAnchorLayoutFromEnd = parcel.readInt() == 1;
            this.mLastLayoutRTL = parcel.readInt() == 1 ? true : z;
            this.mFullSpanItems = parcel.readArrayList(LazySpanLookup.FullSpanItem.class.getClassLoader());
        }

        public SavedState(SavedState savedState) {
            this.mSpanOffsetsSize = savedState.mSpanOffsetsSize;
            this.mAnchorPosition = savedState.mAnchorPosition;
            this.mVisibleAnchorPosition = savedState.mVisibleAnchorPosition;
            this.mSpanOffsets = savedState.mSpanOffsets;
            this.mSpanLookupSize = savedState.mSpanLookupSize;
            this.mSpanLookup = savedState.mSpanLookup;
            this.mReverseLayout = savedState.mReverseLayout;
            this.mAnchorLayoutFromEnd = savedState.mAnchorLayoutFromEnd;
            this.mLastLayoutRTL = savedState.mLastLayoutRTL;
            this.mFullSpanItems = savedState.mFullSpanItems;
        }

        public int describeContents() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public void invalidateAnchorPositionInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mAnchorPosition = -1;
            this.mVisibleAnchorPosition = -1;
        }

        /* access modifiers changed from: package-private */
        public void invalidateSpanInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mSpanLookupSize = 0;
            this.mSpanLookup = null;
            this.mFullSpanItems = null;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mAnchorPosition);
            parcel.writeInt(this.mVisibleAnchorPosition);
            parcel.writeInt(this.mSpanOffsetsSize);
            if (this.mSpanOffsetsSize > 0) {
                parcel.writeIntArray(this.mSpanOffsets);
            }
            parcel.writeInt(this.mSpanLookupSize);
            if (this.mSpanLookupSize > 0) {
                parcel.writeIntArray(this.mSpanLookup);
            }
            parcel.writeInt(this.mReverseLayout ? 1 : 0);
            parcel.writeInt(this.mAnchorLayoutFromEnd ? 1 : 0);
            parcel.writeInt(this.mLastLayoutRTL ? 1 : 0);
            parcel.writeList(this.mFullSpanItems);
        }
    }

    class a {

        /* renamed from: a  reason: collision with root package name */
        int f1157a;

        /* renamed from: b  reason: collision with root package name */
        int f1158b;

        /* renamed from: c  reason: collision with root package name */
        boolean f1159c;

        /* renamed from: d  reason: collision with root package name */
        boolean f1160d;
        boolean e;
        int[] f;

        a() {
            b();
        }

        /* access modifiers changed from: package-private */
        public void a() {
            this.f1158b = this.f1159c ? StaggeredGridLayoutManager.this.u.b() : StaggeredGridLayoutManager.this.u.f();
        }

        /* access modifiers changed from: package-private */
        public void a(int i) {
            this.f1158b = this.f1159c ? StaggeredGridLayoutManager.this.u.b() - i : StaggeredGridLayoutManager.this.u.f() + i;
        }

        /* access modifiers changed from: package-private */
        public void a(c[] cVarArr) {
            int length = cVarArr.length;
            int[] iArr = this.f;
            if (iArr == null || iArr.length < length) {
                this.f = new int[StaggeredGridLayoutManager.this.t.length];
            }
            for (int i = 0; i < length; i++) {
                this.f[i] = cVarArr[i].b(Integer.MIN_VALUE);
            }
        }

        /* access modifiers changed from: package-private */
        public void b() {
            this.f1157a = -1;
            this.f1158b = Integer.MIN_VALUE;
            this.f1159c = false;
            this.f1160d = false;
            this.e = false;
            int[] iArr = this.f;
            if (iArr != null) {
                Arrays.fill(iArr, -1);
            }
        }
    }

    public static class b extends RecyclerView.h {
        c e;
        boolean f;

        public b(int i, int i2) {
            super(i, i2);
        }

        public b(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public b(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public b(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public final int e() {
            c cVar = this.e;
            if (cVar == null) {
                return -1;
            }
            return cVar.e;
        }

        public boolean f() {
            return this.f;
        }
    }

    class c {

        /* renamed from: a  reason: collision with root package name */
        ArrayList<View> f1161a = new ArrayList<>();

        /* renamed from: b  reason: collision with root package name */
        int f1162b = Integer.MIN_VALUE;

        /* renamed from: c  reason: collision with root package name */
        int f1163c = Integer.MIN_VALUE;

        /* renamed from: d  reason: collision with root package name */
        int f1164d = 0;
        final int e;

        c(int i) {
            this.e = i;
        }

        /* access modifiers changed from: package-private */
        public int a(int i) {
            int i2 = this.f1163c;
            if (i2 != Integer.MIN_VALUE) {
                return i2;
            }
            if (this.f1161a.size() == 0) {
                return i;
            }
            a();
            return this.f1163c;
        }

        /* access modifiers changed from: package-private */
        public int a(int i, int i2, boolean z) {
            return a(i, i2, false, false, z);
        }

        /* access modifiers changed from: package-private */
        public int a(int i, int i2, boolean z, boolean z2, boolean z3) {
            int f2 = StaggeredGridLayoutManager.this.u.f();
            int b2 = StaggeredGridLayoutManager.this.u.b();
            int i3 = i2 > i ? 1 : -1;
            while (i != i2) {
                View view = this.f1161a.get(i);
                int d2 = StaggeredGridLayoutManager.this.u.d(view);
                int a2 = StaggeredGridLayoutManager.this.u.a(view);
                boolean z4 = false;
                boolean z5 = !z3 ? d2 < b2 : d2 <= b2;
                if (!z3 ? a2 > f2 : a2 >= f2) {
                    z4 = true;
                }
                if (z5 && z4) {
                    if (!z || !z2) {
                        if (!z2 && d2 >= f2 && a2 <= b2) {
                        }
                    } else if (d2 >= f2 && a2 <= b2) {
                    }
                    return StaggeredGridLayoutManager.this.l(view);
                }
                i += i3;
            }
            return -1;
        }

        public View a(int i, int i2) {
            View view = null;
            if (i2 != -1) {
                int size = this.f1161a.size() - 1;
                while (size >= 0) {
                    View view2 = this.f1161a.get(size);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = StaggeredGridLayoutManager.this;
                    if (staggeredGridLayoutManager.z && staggeredGridLayoutManager.l(view2) >= i) {
                        break;
                    }
                    StaggeredGridLayoutManager staggeredGridLayoutManager2 = StaggeredGridLayoutManager.this;
                    if ((!staggeredGridLayoutManager2.z && staggeredGridLayoutManager2.l(view2) <= i) || !view2.hasFocusable()) {
                        break;
                    }
                    size--;
                    view = view2;
                }
            } else {
                int size2 = this.f1161a.size();
                int i3 = 0;
                while (i3 < size2) {
                    View view3 = this.f1161a.get(i3);
                    StaggeredGridLayoutManager staggeredGridLayoutManager3 = StaggeredGridLayoutManager.this;
                    if (staggeredGridLayoutManager3.z && staggeredGridLayoutManager3.l(view3) <= i) {
                        break;
                    }
                    StaggeredGridLayoutManager staggeredGridLayoutManager4 = StaggeredGridLayoutManager.this;
                    if ((!staggeredGridLayoutManager4.z && staggeredGridLayoutManager4.l(view3) >= i) || !view3.hasFocusable()) {
                        break;
                    }
                    i3++;
                    view = view3;
                }
            }
            return view;
        }

        /* access modifiers changed from: package-private */
        public void a() {
            LazySpanLookup.FullSpanItem c2;
            ArrayList<View> arrayList = this.f1161a;
            View view = arrayList.get(arrayList.size() - 1);
            b b2 = b(view);
            this.f1163c = StaggeredGridLayoutManager.this.u.a(view);
            if (b2.f && (c2 = StaggeredGridLayoutManager.this.E.c(b2.a())) != null && c2.mGapDir == 1) {
                this.f1163c += c2.getGapForSpan(this.e);
            }
        }

        /* access modifiers changed from: package-private */
        public void a(View view) {
            b b2 = b(view);
            b2.e = this;
            this.f1161a.add(view);
            this.f1163c = Integer.MIN_VALUE;
            if (this.f1161a.size() == 1) {
                this.f1162b = Integer.MIN_VALUE;
            }
            if (b2.c() || b2.b()) {
                this.f1164d += StaggeredGridLayoutManager.this.u.b(view);
            }
        }

        /* access modifiers changed from: package-private */
        public void a(boolean z, int i) {
            int a2 = z ? a(Integer.MIN_VALUE) : b(Integer.MIN_VALUE);
            c();
            if (a2 != Integer.MIN_VALUE) {
                if (z && a2 < StaggeredGridLayoutManager.this.u.b()) {
                    return;
                }
                if (z || a2 <= StaggeredGridLayoutManager.this.u.f()) {
                    if (i != Integer.MIN_VALUE) {
                        a2 += i;
                    }
                    this.f1163c = a2;
                    this.f1162b = a2;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public int b(int i) {
            int i2 = this.f1162b;
            if (i2 != Integer.MIN_VALUE) {
                return i2;
            }
            if (this.f1161a.size() == 0) {
                return i;
            }
            b();
            return this.f1162b;
        }

        /* access modifiers changed from: package-private */
        public b b(View view) {
            return (b) view.getLayoutParams();
        }

        /* access modifiers changed from: package-private */
        public void b() {
            LazySpanLookup.FullSpanItem c2;
            View view = this.f1161a.get(0);
            b b2 = b(view);
            this.f1162b = StaggeredGridLayoutManager.this.u.d(view);
            if (b2.f && (c2 = StaggeredGridLayoutManager.this.E.c(b2.a())) != null && c2.mGapDir == -1) {
                this.f1162b -= c2.getGapForSpan(this.e);
            }
        }

        /* access modifiers changed from: package-private */
        public void c() {
            this.f1161a.clear();
            i();
            this.f1164d = 0;
        }

        /* access modifiers changed from: package-private */
        public void c(int i) {
            int i2 = this.f1162b;
            if (i2 != Integer.MIN_VALUE) {
                this.f1162b = i2 + i;
            }
            int i3 = this.f1163c;
            if (i3 != Integer.MIN_VALUE) {
                this.f1163c = i3 + i;
            }
        }

        /* access modifiers changed from: package-private */
        public void c(View view) {
            b b2 = b(view);
            b2.e = this;
            this.f1161a.add(0, view);
            this.f1162b = Integer.MIN_VALUE;
            if (this.f1161a.size() == 1) {
                this.f1163c = Integer.MIN_VALUE;
            }
            if (b2.c() || b2.b()) {
                this.f1164d += StaggeredGridLayoutManager.this.u.b(view);
            }
        }

        public int d() {
            int i;
            int i2;
            if (StaggeredGridLayoutManager.this.z) {
                i2 = this.f1161a.size() - 1;
                i = -1;
            } else {
                i2 = 0;
                i = this.f1161a.size();
            }
            return a(i2, i, true);
        }

        /* access modifiers changed from: package-private */
        public void d(int i) {
            this.f1162b = i;
            this.f1163c = i;
        }

        public int e() {
            int i;
            int i2;
            if (StaggeredGridLayoutManager.this.z) {
                i2 = 0;
                i = this.f1161a.size();
            } else {
                i2 = this.f1161a.size() - 1;
                i = -1;
            }
            return a(i2, i, true);
        }

        public int f() {
            return this.f1164d;
        }

        /* access modifiers changed from: package-private */
        public int g() {
            int i = this.f1163c;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            a();
            return this.f1163c;
        }

        /* access modifiers changed from: package-private */
        public int h() {
            int i = this.f1162b;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            b();
            return this.f1162b;
        }

        /* access modifiers changed from: package-private */
        public void i() {
            this.f1162b = Integer.MIN_VALUE;
            this.f1163c = Integer.MIN_VALUE;
        }

        /* access modifiers changed from: package-private */
        public void j() {
            int size = this.f1161a.size();
            View remove = this.f1161a.remove(size - 1);
            b b2 = b(remove);
            b2.e = null;
            if (b2.c() || b2.b()) {
                this.f1164d -= StaggeredGridLayoutManager.this.u.b(remove);
            }
            if (size == 1) {
                this.f1162b = Integer.MIN_VALUE;
            }
            this.f1163c = Integer.MIN_VALUE;
        }

        /* access modifiers changed from: package-private */
        public void k() {
            View remove = this.f1161a.remove(0);
            b b2 = b(remove);
            b2.e = null;
            if (this.f1161a.size() == 0) {
                this.f1163c = Integer.MIN_VALUE;
            }
            if (b2.c() || b2.b()) {
                this.f1164d -= StaggeredGridLayoutManager.this.u.b(remove);
            }
            this.f1162b = Integer.MIN_VALUE;
        }
    }

    public StaggeredGridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        RecyclerView.g.b a2 = RecyclerView.g.a(context, attributeSet, i, i2);
        i(a2.f1127a);
        j(a2.f1128b);
        c(a2.f1129c);
        this.y = new C0180v();
        N();
    }

    private void N() {
        this.u = B.a(this, this.w);
        this.v = B.a(this, 1 - this.w);
    }

    private void O() {
        if (this.v.d() != 1073741824) {
            int e = e();
            float f = 0.0f;
            for (int i = 0; i < e; i++) {
                View c2 = c(i);
                float b2 = (float) this.v.b(c2);
                if (b2 >= f) {
                    if (((b) c2.getLayoutParams()).f()) {
                        b2 = (b2 * 1.0f) / ((float) this.s);
                    }
                    f = Math.max(f, b2);
                }
            }
            int i2 = this.x;
            int round = Math.round(f * ((float) this.s));
            if (this.v.d() == Integer.MIN_VALUE) {
                round = Math.min(round, this.v.g());
            }
            k(round);
            if (this.x != i2) {
                for (int i3 = 0; i3 < e; i3++) {
                    View c3 = c(i3);
                    b bVar = (b) c3.getLayoutParams();
                    if (!bVar.f) {
                        if (!M() || this.w != 1) {
                            int i4 = bVar.e.e;
                            int i5 = this.x * i4;
                            int i6 = i4 * i2;
                            if (this.w == 1) {
                                c3.offsetLeftAndRight(i5 - i6);
                            } else {
                                c3.offsetTopAndBottom(i5 - i6);
                            }
                        } else {
                            int i7 = this.s;
                            int i8 = bVar.e.e;
                            c3.offsetLeftAndRight(((-((i7 - 1) - i8)) * this.x) - ((-((i7 - 1) - i8)) * i2));
                        }
                    }
                }
            }
        }
    }

    private void P() {
        this.A = (this.w == 1 || !M()) ? this.z : !this.z;
    }

    /* JADX WARNING: type inference failed for: r9v0 */
    /* JADX WARNING: type inference failed for: r9v1, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r9v4 */
    private int a(RecyclerView.n nVar, C0180v vVar, RecyclerView.r rVar) {
        c cVar;
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        View view;
        StaggeredGridLayoutManager staggeredGridLayoutManager;
        boolean z2;
        RecyclerView.n nVar2 = nVar;
        C0180v vVar2 = vVar;
        ? r9 = 0;
        this.B.set(0, this.s, true);
        int i7 = this.y.i ? vVar2.e == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE : vVar2.e == 1 ? vVar2.g + vVar2.f1267b : vVar2.f - vVar2.f1267b;
        e(vVar2.e, i7);
        int b2 = this.A ? this.u.b() : this.u.f();
        boolean z3 = false;
        while (vVar.a(rVar) && (this.y.i || !this.B.isEmpty())) {
            View a2 = vVar2.a(nVar2);
            b bVar = (b) a2.getLayoutParams();
            int a3 = bVar.a();
            int d2 = this.E.d(a3);
            boolean z4 = d2 == -1 ? true : r9;
            if (z4) {
                cVar = bVar.f ? this.t[r9] : a(vVar2);
                this.E.a(a3, cVar);
            } else {
                cVar = this.t[d2];
            }
            c cVar2 = cVar;
            bVar.e = cVar2;
            if (vVar2.e == 1) {
                b(a2);
            } else {
                b(a2, (int) r9);
            }
            a(a2, bVar, (boolean) r9);
            if (vVar2.e == 1) {
                int r = bVar.f ? r(b2) : cVar2.a(b2);
                int b3 = this.u.b(a2) + r;
                if (z4 && bVar.f) {
                    LazySpanLookup.FullSpanItem n = n(r);
                    n.mGapDir = -1;
                    n.mPosition = a3;
                    this.E.a(n);
                }
                i = b3;
                i2 = r;
            } else {
                int u2 = bVar.f ? u(b2) : cVar2.b(b2);
                i2 = u2 - this.u.b(a2);
                if (z4 && bVar.f) {
                    LazySpanLookup.FullSpanItem o = o(u2);
                    o.mGapDir = 1;
                    o.mPosition = a3;
                    this.E.a(o);
                }
                i = u2;
            }
            if (bVar.f && vVar2.f1269d == -1) {
                if (!z4) {
                    if (!(vVar2.e == 1 ? E() : F())) {
                        LazySpanLookup.FullSpanItem c2 = this.E.c(a3);
                        if (c2 != null) {
                            c2.mHasUnwantedGapAfter = true;
                        }
                    }
                }
                this.M = true;
            }
            a(a2, bVar, vVar2);
            if (!M() || this.w != 1) {
                int f = bVar.f ? this.v.f() : (cVar2.e * this.x) + this.v.f();
                i4 = f;
                i3 = this.v.b(a2) + f;
            } else {
                int b4 = bVar.f ? this.v.b() : this.v.b() - (((this.s - 1) - cVar2.e) * this.x);
                i3 = b4;
                i4 = b4 - this.v.b(a2);
            }
            if (this.w == 1) {
                staggeredGridLayoutManager = this;
                view = a2;
                i6 = i4;
                i4 = i2;
                i5 = i3;
            } else {
                staggeredGridLayoutManager = this;
                view = a2;
                i6 = i2;
                i5 = i;
                i = i3;
            }
            staggeredGridLayoutManager.a(view, i6, i4, i5, i);
            if (bVar.f) {
                e(this.y.e, i7);
            } else {
                a(cVar2, this.y.e, i7);
            }
            a(nVar2, this.y);
            if (this.y.h && a2.hasFocusable()) {
                if (bVar.f) {
                    this.B.clear();
                } else {
                    z2 = false;
                    this.B.set(cVar2.e, false);
                    r9 = z2;
                    z3 = true;
                }
            }
            z2 = false;
            r9 = z2;
            z3 = true;
        }
        int i8 = r9;
        if (!z3) {
            a(nVar2, this.y);
        }
        int f2 = this.y.e == -1 ? this.u.f() - u(this.u.f()) : r(this.u.b()) - this.u.b();
        return f2 > 0 ? Math.min(vVar2.f1267b, f2) : i8;
    }

    private c a(C0180v vVar) {
        int i;
        int i2;
        int i3 = -1;
        if (v(vVar.e)) {
            i2 = this.s - 1;
            i = -1;
        } else {
            i2 = 0;
            i3 = this.s;
            i = 1;
        }
        c cVar = null;
        if (vVar.e == 1) {
            int i4 = Integer.MAX_VALUE;
            int f = this.u.f();
            while (i2 != i3) {
                c cVar2 = this.t[i2];
                int a2 = cVar2.a(f);
                if (a2 < i4) {
                    cVar = cVar2;
                    i4 = a2;
                }
                i2 += i;
            }
            return cVar;
        }
        int i5 = Integer.MIN_VALUE;
        int b2 = this.u.b();
        while (i2 != i3) {
            c cVar3 = this.t[i2];
            int b3 = cVar3.b(b2);
            if (b3 > i5) {
                cVar = cVar3;
                i5 = b3;
            }
            i2 += i;
        }
        return cVar;
    }

    private void a(View view, int i, int i2, boolean z2) {
        a(view, this.K);
        b bVar = (b) view.getLayoutParams();
        int i3 = bVar.leftMargin;
        Rect rect = this.K;
        int d2 = d(i, i3 + rect.left, bVar.rightMargin + rect.right);
        int i4 = bVar.topMargin;
        Rect rect2 = this.K;
        int d3 = d(i2, i4 + rect2.top, bVar.bottomMargin + rect2.bottom);
        if (z2 ? b(view, d2, d3, bVar) : a(view, d2, d3, (RecyclerView.h) bVar)) {
            view.measure(d2, d3);
        }
    }

    private void a(View view, b bVar, C0180v vVar) {
        if (vVar.e == 1) {
            if (bVar.f) {
                p(view);
            } else {
                bVar.e.a(view);
            }
        } else if (bVar.f) {
            q(view);
        } else {
            bVar.e.c(view);
        }
    }

    private void a(View view, b bVar, boolean z2) {
        int i;
        int i2;
        if (bVar.f) {
            if (this.w == 1) {
                i2 = this.J;
            } else {
                a(view, RecyclerView.g.a(r(), s(), o() + p(), bVar.width, true), this.J, z2);
                return;
            }
        } else if (this.w == 1) {
            i2 = RecyclerView.g.a(this.x, s(), 0, bVar.width, false);
        } else {
            i2 = RecyclerView.g.a(r(), s(), o() + p(), bVar.width, true);
            i = RecyclerView.g.a(this.x, i(), 0, bVar.height, false);
            a(view, i2, i, z2);
        }
        i = RecyclerView.g.a(h(), i(), q() + n(), bVar.height, true);
        a(view, i2, i, z2);
    }

    private void a(RecyclerView.n nVar, int i) {
        int e = e() - 1;
        while (e >= 0) {
            View c2 = c(e);
            if (this.u.d(c2) >= i && this.u.f(c2) >= i) {
                b bVar = (b) c2.getLayoutParams();
                if (bVar.f) {
                    int i2 = 0;
                    while (i2 < this.s) {
                        if (this.t[i2].f1161a.size() != 1) {
                            i2++;
                        } else {
                            return;
                        }
                    }
                    for (int i3 = 0; i3 < this.s; i3++) {
                        this.t[i3].j();
                    }
                } else if (bVar.e.f1161a.size() != 1) {
                    bVar.e.j();
                } else {
                    return;
                }
                a(c2, nVar);
                e--;
            } else {
                return;
            }
        }
    }

    private void a(RecyclerView.n nVar, RecyclerView.r rVar, boolean z2) {
        int b2;
        int r = r(Integer.MIN_VALUE);
        if (r != Integer.MIN_VALUE && (b2 = this.u.b() - r) > 0) {
            int i = b2 - (-c(-b2, nVar, rVar));
            if (z2 && i > 0) {
                this.u.a(i);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0010, code lost:
        if (r4.e == -1) goto L_0x0012;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(androidx.recyclerview.widget.RecyclerView.n r3, androidx.recyclerview.widget.C0180v r4) {
        /*
            r2 = this;
            boolean r0 = r4.f1266a
            if (r0 == 0) goto L_0x004d
            boolean r0 = r4.i
            if (r0 == 0) goto L_0x0009
            goto L_0x004d
        L_0x0009:
            int r0 = r4.f1267b
            r1 = -1
            if (r0 != 0) goto L_0x001e
            int r0 = r4.e
            if (r0 != r1) goto L_0x0018
        L_0x0012:
            int r4 = r4.g
        L_0x0014:
            r2.a((androidx.recyclerview.widget.RecyclerView.n) r3, (int) r4)
            goto L_0x004d
        L_0x0018:
            int r4 = r4.f
        L_0x001a:
            r2.b((androidx.recyclerview.widget.RecyclerView.n) r3, (int) r4)
            goto L_0x004d
        L_0x001e:
            int r0 = r4.e
            if (r0 != r1) goto L_0x0037
            int r0 = r4.f
            int r1 = r2.s(r0)
            int r0 = r0 - r1
            if (r0 >= 0) goto L_0x002c
            goto L_0x0012
        L_0x002c:
            int r1 = r4.g
            int r4 = r4.f1267b
            int r4 = java.lang.Math.min(r0, r4)
            int r4 = r1 - r4
            goto L_0x0014
        L_0x0037:
            int r0 = r4.g
            int r0 = r2.t(r0)
            int r1 = r4.g
            int r0 = r0 - r1
            if (r0 >= 0) goto L_0x0043
            goto L_0x0018
        L_0x0043:
            int r1 = r4.f
            int r4 = r4.f1267b
            int r4 = java.lang.Math.min(r0, r4)
            int r4 = r4 + r1
            goto L_0x001a
        L_0x004d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.StaggeredGridLayoutManager.a(androidx.recyclerview.widget.RecyclerView$n, androidx.recyclerview.widget.v):void");
    }

    private void a(a aVar) {
        boolean z2;
        SavedState savedState = this.I;
        int i = savedState.mSpanOffsetsSize;
        if (i > 0) {
            if (i == this.s) {
                for (int i2 = 0; i2 < this.s; i2++) {
                    this.t[i2].c();
                    SavedState savedState2 = this.I;
                    int i3 = savedState2.mSpanOffsets[i2];
                    if (i3 != Integer.MIN_VALUE) {
                        i3 += savedState2.mAnchorLayoutFromEnd ? this.u.b() : this.u.f();
                    }
                    this.t[i2].d(i3);
                }
            } else {
                savedState.invalidateSpanInfo();
                SavedState savedState3 = this.I;
                savedState3.mAnchorPosition = savedState3.mVisibleAnchorPosition;
            }
        }
        SavedState savedState4 = this.I;
        this.H = savedState4.mLastLayoutRTL;
        c(savedState4.mReverseLayout);
        P();
        SavedState savedState5 = this.I;
        int i4 = savedState5.mAnchorPosition;
        if (i4 != -1) {
            this.C = i4;
            z2 = savedState5.mAnchorLayoutFromEnd;
        } else {
            z2 = this.A;
        }
        aVar.f1159c = z2;
        SavedState savedState6 = this.I;
        if (savedState6.mSpanLookupSize > 1) {
            LazySpanLookup lazySpanLookup = this.E;
            lazySpanLookup.f1155a = savedState6.mSpanLookup;
            lazySpanLookup.f1156b = savedState6.mFullSpanItems;
        }
    }

    private void a(c cVar, int i, int i2) {
        int f = cVar.f();
        if (i == -1) {
            if (cVar.h() + f > i2) {
                return;
            }
        } else if (cVar.g() - f < i2) {
            return;
        }
        this.B.set(cVar.e, false);
    }

    private boolean a(c cVar) {
        if (this.A) {
            if (cVar.g() < this.u.b()) {
                ArrayList<View> arrayList = cVar.f1161a;
                return !cVar.b(arrayList.get(arrayList.size() - 1)).f;
            }
        } else if (cVar.h() > this.u.f()) {
            return !cVar.b(cVar.f1161a.get(0)).f;
        }
        return false;
    }

    private void b(int i, RecyclerView.r rVar) {
        int i2;
        int i3;
        int b2;
        C0180v vVar = this.y;
        boolean z2 = false;
        vVar.f1267b = 0;
        vVar.f1268c = i;
        if (!x() || (b2 = rVar.b()) == -1) {
            i3 = 0;
            i2 = 0;
        } else {
            if (this.A == (b2 < i)) {
                i3 = this.u.g();
                i2 = 0;
            } else {
                i2 = this.u.g();
                i3 = 0;
            }
        }
        if (f()) {
            this.y.f = this.u.f() - i2;
            this.y.g = this.u.b() + i3;
        } else {
            this.y.g = this.u.a() + i3;
            this.y.f = -i2;
        }
        C0180v vVar2 = this.y;
        vVar2.h = false;
        vVar2.f1266a = true;
        if (this.u.d() == 0 && this.u.a() == 0) {
            z2 = true;
        }
        vVar2.i = z2;
    }

    private void b(RecyclerView.n nVar, int i) {
        while (e() > 0) {
            View c2 = c(0);
            if (this.u.a(c2) <= i && this.u.e(c2) <= i) {
                b bVar = (b) c2.getLayoutParams();
                if (bVar.f) {
                    int i2 = 0;
                    while (i2 < this.s) {
                        if (this.t[i2].f1161a.size() != 1) {
                            i2++;
                        } else {
                            return;
                        }
                    }
                    for (int i3 = 0; i3 < this.s; i3++) {
                        this.t[i3].k();
                    }
                } else if (bVar.e.f1161a.size() != 1) {
                    bVar.e.k();
                } else {
                    return;
                }
                a(c2, nVar);
            } else {
                return;
            }
        }
    }

    private void b(RecyclerView.n nVar, RecyclerView.r rVar, boolean z2) {
        int f;
        int u2 = u(Integer.MAX_VALUE);
        if (u2 != Integer.MAX_VALUE && (f = u2 - this.u.f()) > 0) {
            int c2 = f - c(f, nVar, rVar);
            if (z2 && c2 > 0) {
                this.u.a(-c2);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0045 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0046  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void c(int r7, int r8, int r9) {
        /*
            r6 = this;
            boolean r0 = r6.A
            if (r0 == 0) goto L_0x0009
            int r0 = r6.J()
            goto L_0x000d
        L_0x0009:
            int r0 = r6.I()
        L_0x000d:
            r1 = 8
            if (r9 != r1) goto L_0x001b
            if (r7 >= r8) goto L_0x0016
            int r2 = r8 + 1
            goto L_0x001d
        L_0x0016:
            int r2 = r7 + 1
            r3 = r2
            r2 = r8
            goto L_0x001f
        L_0x001b:
            int r2 = r7 + r8
        L_0x001d:
            r3 = r2
            r2 = r7
        L_0x001f:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r4 = r6.E
            r4.e(r2)
            r4 = 1
            if (r9 == r4) goto L_0x003e
            r5 = 2
            if (r9 == r5) goto L_0x0038
            if (r9 == r1) goto L_0x002d
            goto L_0x0043
        L_0x002d:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.E
            r9.b(r7, r4)
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r7 = r6.E
            r7.a((int) r8, (int) r4)
            goto L_0x0043
        L_0x0038:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.E
            r9.b(r7, r8)
            goto L_0x0043
        L_0x003e:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.E
            r9.a((int) r7, (int) r8)
        L_0x0043:
            if (r3 > r0) goto L_0x0046
            return
        L_0x0046:
            boolean r7 = r6.A
            if (r7 == 0) goto L_0x004f
            int r7 = r6.I()
            goto L_0x0053
        L_0x004f:
            int r7 = r6.J()
        L_0x0053:
            if (r2 > r7) goto L_0x0058
            r6.z()
        L_0x0058:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.StaggeredGridLayoutManager.c(int, int, int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:83:0x014b, code lost:
        if (G() != false) goto L_0x014f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void c(androidx.recyclerview.widget.RecyclerView.n r9, androidx.recyclerview.widget.RecyclerView.r r10, boolean r11) {
        /*
            r8 = this;
            androidx.recyclerview.widget.StaggeredGridLayoutManager$a r0 = r8.L
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r1 = r8.I
            r2 = -1
            if (r1 != 0) goto L_0x000b
            int r1 = r8.C
            if (r1 == r2) goto L_0x0018
        L_0x000b:
            int r1 = r10.a()
            if (r1 != 0) goto L_0x0018
            r8.b((androidx.recyclerview.widget.RecyclerView.n) r9)
            r0.b()
            return
        L_0x0018:
            boolean r1 = r0.e
            r3 = 0
            r4 = 1
            if (r1 == 0) goto L_0x0029
            int r1 = r8.C
            if (r1 != r2) goto L_0x0029
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r1 = r8.I
            if (r1 == 0) goto L_0x0027
            goto L_0x0029
        L_0x0027:
            r1 = r3
            goto L_0x002a
        L_0x0029:
            r1 = r4
        L_0x002a:
            if (r1 == 0) goto L_0x0043
            r0.b()
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r5 = r8.I
            if (r5 == 0) goto L_0x0037
            r8.a((androidx.recyclerview.widget.StaggeredGridLayoutManager.a) r0)
            goto L_0x003e
        L_0x0037:
            r8.P()
            boolean r5 = r8.A
            r0.f1159c = r5
        L_0x003e:
            r8.b((androidx.recyclerview.widget.RecyclerView.r) r10, (androidx.recyclerview.widget.StaggeredGridLayoutManager.a) r0)
            r0.e = r4
        L_0x0043:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r5 = r8.I
            if (r5 != 0) goto L_0x0060
            int r5 = r8.C
            if (r5 != r2) goto L_0x0060
            boolean r5 = r0.f1159c
            boolean r6 = r8.G
            if (r5 != r6) goto L_0x0059
            boolean r5 = r8.M()
            boolean r6 = r8.H
            if (r5 == r6) goto L_0x0060
        L_0x0059:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r5 = r8.E
            r5.a()
            r0.f1160d = r4
        L_0x0060:
            int r5 = r8.e()
            if (r5 <= 0) goto L_0x00c9
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r5 = r8.I
            if (r5 == 0) goto L_0x006e
            int r5 = r5.mSpanOffsetsSize
            if (r5 >= r4) goto L_0x00c9
        L_0x006e:
            boolean r5 = r0.f1160d
            if (r5 == 0) goto L_0x008e
            r1 = r3
        L_0x0073:
            int r5 = r8.s
            if (r1 >= r5) goto L_0x00c9
            androidx.recyclerview.widget.StaggeredGridLayoutManager$c[] r5 = r8.t
            r5 = r5[r1]
            r5.c()
            int r5 = r0.f1158b
            r6 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r5 == r6) goto L_0x008b
            androidx.recyclerview.widget.StaggeredGridLayoutManager$c[] r6 = r8.t
            r6 = r6[r1]
            r6.d(r5)
        L_0x008b:
            int r1 = r1 + 1
            goto L_0x0073
        L_0x008e:
            if (r1 != 0) goto L_0x00af
            androidx.recyclerview.widget.StaggeredGridLayoutManager$a r1 = r8.L
            int[] r1 = r1.f
            if (r1 != 0) goto L_0x0097
            goto L_0x00af
        L_0x0097:
            r1 = r3
        L_0x0098:
            int r5 = r8.s
            if (r1 >= r5) goto L_0x00c9
            androidx.recyclerview.widget.StaggeredGridLayoutManager$c[] r5 = r8.t
            r5 = r5[r1]
            r5.c()
            androidx.recyclerview.widget.StaggeredGridLayoutManager$a r6 = r8.L
            int[] r6 = r6.f
            r6 = r6[r1]
            r5.d(r6)
            int r1 = r1 + 1
            goto L_0x0098
        L_0x00af:
            r1 = r3
        L_0x00b0:
            int r5 = r8.s
            if (r1 >= r5) goto L_0x00c2
            androidx.recyclerview.widget.StaggeredGridLayoutManager$c[] r5 = r8.t
            r5 = r5[r1]
            boolean r6 = r8.A
            int r7 = r0.f1158b
            r5.a((boolean) r6, (int) r7)
            int r1 = r1 + 1
            goto L_0x00b0
        L_0x00c2:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$a r1 = r8.L
            androidx.recyclerview.widget.StaggeredGridLayoutManager$c[] r5 = r8.t
            r1.a((androidx.recyclerview.widget.StaggeredGridLayoutManager.c[]) r5)
        L_0x00c9:
            r8.a((androidx.recyclerview.widget.RecyclerView.n) r9)
            androidx.recyclerview.widget.v r1 = r8.y
            r1.f1266a = r3
            r8.M = r3
            androidx.recyclerview.widget.B r1 = r8.v
            int r1 = r1.g()
            r8.k(r1)
            int r1 = r0.f1157a
            r8.b((int) r1, (androidx.recyclerview.widget.RecyclerView.r) r10)
            boolean r1 = r0.f1159c
            if (r1 == 0) goto L_0x00f0
            r8.w(r2)
            androidx.recyclerview.widget.v r1 = r8.y
            r8.a((androidx.recyclerview.widget.RecyclerView.n) r9, (androidx.recyclerview.widget.C0180v) r1, (androidx.recyclerview.widget.RecyclerView.r) r10)
            r8.w(r4)
            goto L_0x00fb
        L_0x00f0:
            r8.w(r4)
            androidx.recyclerview.widget.v r1 = r8.y
            r8.a((androidx.recyclerview.widget.RecyclerView.n) r9, (androidx.recyclerview.widget.C0180v) r1, (androidx.recyclerview.widget.RecyclerView.r) r10)
            r8.w(r2)
        L_0x00fb:
            androidx.recyclerview.widget.v r1 = r8.y
            int r2 = r0.f1157a
            int r5 = r1.f1269d
            int r2 = r2 + r5
            r1.f1268c = r2
            r8.a((androidx.recyclerview.widget.RecyclerView.n) r9, (androidx.recyclerview.widget.C0180v) r1, (androidx.recyclerview.widget.RecyclerView.r) r10)
            r8.O()
            int r1 = r8.e()
            if (r1 <= 0) goto L_0x0121
            boolean r1 = r8.A
            if (r1 == 0) goto L_0x011b
            r8.a((androidx.recyclerview.widget.RecyclerView.n) r9, (androidx.recyclerview.widget.RecyclerView.r) r10, (boolean) r4)
            r8.b((androidx.recyclerview.widget.RecyclerView.n) r9, (androidx.recyclerview.widget.RecyclerView.r) r10, (boolean) r3)
            goto L_0x0121
        L_0x011b:
            r8.b((androidx.recyclerview.widget.RecyclerView.n) r9, (androidx.recyclerview.widget.RecyclerView.r) r10, (boolean) r4)
            r8.a((androidx.recyclerview.widget.RecyclerView.n) r9, (androidx.recyclerview.widget.RecyclerView.r) r10, (boolean) r3)
        L_0x0121:
            if (r11 == 0) goto L_0x014e
            boolean r11 = r10.d()
            if (r11 != 0) goto L_0x014e
            int r11 = r8.F
            if (r11 == 0) goto L_0x013f
            int r11 = r8.e()
            if (r11 <= 0) goto L_0x013f
            boolean r11 = r8.M
            if (r11 != 0) goto L_0x013d
            android.view.View r11 = r8.K()
            if (r11 == 0) goto L_0x013f
        L_0x013d:
            r11 = r4
            goto L_0x0140
        L_0x013f:
            r11 = r3
        L_0x0140:
            if (r11 == 0) goto L_0x014e
            java.lang.Runnable r11 = r8.P
            r8.a((java.lang.Runnable) r11)
            boolean r11 = r8.G()
            if (r11 == 0) goto L_0x014e
            goto L_0x014f
        L_0x014e:
            r4 = r3
        L_0x014f:
            boolean r11 = r10.d()
            if (r11 == 0) goto L_0x015a
            androidx.recyclerview.widget.StaggeredGridLayoutManager$a r11 = r8.L
            r11.b()
        L_0x015a:
            boolean r11 = r0.f1159c
            r8.G = r11
            boolean r11 = r8.M()
            r8.H = r11
            if (r4 == 0) goto L_0x016e
            androidx.recyclerview.widget.StaggeredGridLayoutManager$a r11 = r8.L
            r11.b()
            r8.c((androidx.recyclerview.widget.RecyclerView.n) r9, (androidx.recyclerview.widget.RecyclerView.r) r10, (boolean) r3)
        L_0x016e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.StaggeredGridLayoutManager.c(androidx.recyclerview.widget.RecyclerView$n, androidx.recyclerview.widget.RecyclerView$r, boolean):void");
    }

    private boolean c(RecyclerView.r rVar, a aVar) {
        aVar.f1157a = this.G ? q(rVar.a()) : p(rVar.a());
        aVar.f1158b = Integer.MIN_VALUE;
        return true;
    }

    private int d(int i, int i2, int i3) {
        if (i2 == 0 && i3 == 0) {
            return i;
        }
        int mode = View.MeasureSpec.getMode(i);
        return (mode == Integer.MIN_VALUE || mode == 1073741824) ? View.MeasureSpec.makeMeasureSpec(Math.max(0, (View.MeasureSpec.getSize(i) - i2) - i3), mode) : i;
    }

    private void e(int i, int i2) {
        for (int i3 = 0; i3 < this.s; i3++) {
            if (!this.t[i3].f1161a.isEmpty()) {
                a(this.t[i3], i, i2);
            }
        }
    }

    private int h(RecyclerView.r rVar) {
        if (e() == 0) {
            return 0;
        }
        return N.a(rVar, this.u, b(!this.N), a(!this.N), this, this.N);
    }

    private int i(RecyclerView.r rVar) {
        if (e() == 0) {
            return 0;
        }
        return N.a(rVar, this.u, b(!this.N), a(!this.N), this, this.N, this.A);
    }

    private int j(RecyclerView.r rVar) {
        if (e() == 0) {
            return 0;
        }
        return N.b(rVar, this.u, b(!this.N), a(!this.N), this, this.N);
    }

    private int l(int i) {
        if (e() == 0) {
            return this.A ? 1 : -1;
        }
        return (i < I()) != this.A ? -1 : 1;
    }

    private int m(int i) {
        if (i == 1) {
            return (this.w != 1 && M()) ? 1 : -1;
        }
        if (i == 2) {
            return (this.w != 1 && M()) ? -1 : 1;
        }
        if (i == 17) {
            return this.w == 0 ? -1 : Integer.MIN_VALUE;
        }
        if (i == 33) {
            return this.w == 1 ? -1 : Integer.MIN_VALUE;
        }
        if (i == 66) {
            return this.w == 0 ? 1 : Integer.MIN_VALUE;
        }
        if (i != 130) {
            return Integer.MIN_VALUE;
        }
        return this.w == 1 ? 1 : Integer.MIN_VALUE;
    }

    private LazySpanLookup.FullSpanItem n(int i) {
        LazySpanLookup.FullSpanItem fullSpanItem = new LazySpanLookup.FullSpanItem();
        fullSpanItem.mGapPerSpan = new int[this.s];
        for (int i2 = 0; i2 < this.s; i2++) {
            fullSpanItem.mGapPerSpan[i2] = i - this.t[i2].a(i);
        }
        return fullSpanItem;
    }

    private LazySpanLookup.FullSpanItem o(int i) {
        LazySpanLookup.FullSpanItem fullSpanItem = new LazySpanLookup.FullSpanItem();
        fullSpanItem.mGapPerSpan = new int[this.s];
        for (int i2 = 0; i2 < this.s; i2++) {
            fullSpanItem.mGapPerSpan[i2] = this.t[i2].b(i) - i;
        }
        return fullSpanItem;
    }

    private int p(int i) {
        int e = e();
        for (int i2 = 0; i2 < e; i2++) {
            int l = l(c(i2));
            if (l >= 0 && l < i) {
                return l;
            }
        }
        return 0;
    }

    private void p(View view) {
        for (int i = this.s - 1; i >= 0; i--) {
            this.t[i].a(view);
        }
    }

    private int q(int i) {
        for (int e = e() - 1; e >= 0; e--) {
            int l = l(c(e));
            if (l >= 0 && l < i) {
                return l;
            }
        }
        return 0;
    }

    private void q(View view) {
        for (int i = this.s - 1; i >= 0; i--) {
            this.t[i].c(view);
        }
    }

    private int r(int i) {
        int a2 = this.t[0].a(i);
        for (int i2 = 1; i2 < this.s; i2++) {
            int a3 = this.t[i2].a(i);
            if (a3 > a2) {
                a2 = a3;
            }
        }
        return a2;
    }

    private int s(int i) {
        int b2 = this.t[0].b(i);
        for (int i2 = 1; i2 < this.s; i2++) {
            int b3 = this.t[i2].b(i);
            if (b3 > b2) {
                b2 = b3;
            }
        }
        return b2;
    }

    private int t(int i) {
        int a2 = this.t[0].a(i);
        for (int i2 = 1; i2 < this.s; i2++) {
            int a3 = this.t[i2].a(i);
            if (a3 < a2) {
                a2 = a3;
            }
        }
        return a2;
    }

    private int u(int i) {
        int b2 = this.t[0].b(i);
        for (int i2 = 1; i2 < this.s; i2++) {
            int b3 = this.t[i2].b(i);
            if (b3 < b2) {
                b2 = b3;
            }
        }
        return b2;
    }

    private boolean v(int i) {
        if (this.w == 0) {
            return (i == -1) != this.A;
        }
        return ((i == -1) == this.A) == M();
    }

    private void w(int i) {
        C0180v vVar = this.y;
        vVar.e = i;
        int i2 = 1;
        if (this.A != (i == -1)) {
            i2 = -1;
        }
        vVar.f1269d = i2;
    }

    public boolean D() {
        return this.I == null;
    }

    /* access modifiers changed from: package-private */
    public boolean E() {
        int a2 = this.t[0].a(Integer.MIN_VALUE);
        for (int i = 1; i < this.s; i++) {
            if (this.t[i].a(Integer.MIN_VALUE) != a2) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean F() {
        int b2 = this.t[0].b(Integer.MIN_VALUE);
        for (int i = 1; i < this.s; i++) {
            if (this.t[i].b(Integer.MIN_VALUE) != b2) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean G() {
        int i;
        int i2;
        if (e() == 0 || this.F == 0 || !u()) {
            return false;
        }
        if (this.A) {
            i2 = J();
            i = I();
        } else {
            i2 = I();
            i = J();
        }
        if (i2 == 0 && K() != null) {
            this.E.a();
        } else if (!this.M) {
            return false;
        } else {
            int i3 = this.A ? -1 : 1;
            int i4 = i + 1;
            LazySpanLookup.FullSpanItem a2 = this.E.a(i2, i4, i3, true);
            if (a2 == null) {
                this.M = false;
                this.E.b(i4);
                return false;
            }
            LazySpanLookup.FullSpanItem a3 = this.E.a(i2, a2.mPosition, i3 * -1, true);
            if (a3 == null) {
                this.E.b(a2.mPosition);
            } else {
                this.E.b(a3.mPosition + 1);
            }
        }
        A();
        z();
        return true;
    }

    /* access modifiers changed from: package-private */
    public int H() {
        View a2 = this.A ? a(true) : b(true);
        if (a2 == null) {
            return -1;
        }
        return l(a2);
    }

    /* access modifiers changed from: package-private */
    public int I() {
        if (e() == 0) {
            return 0;
        }
        return l(c(0));
    }

    /* access modifiers changed from: package-private */
    public int J() {
        int e = e();
        if (e == 0) {
            return 0;
        }
        return l(c(e - 1));
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0074, code lost:
        if (r10 == r11) goto L_0x0088;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0086, code lost:
        if (r10 == r11) goto L_0x0088;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x008a, code lost:
        r10 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View K() {
        /*
            r12 = this;
            int r0 = r12.e()
            r1 = 1
            int r0 = r0 - r1
            java.util.BitSet r2 = new java.util.BitSet
            int r3 = r12.s
            r2.<init>(r3)
            int r3 = r12.s
            r4 = 0
            r2.set(r4, r3, r1)
            int r3 = r12.w
            r5 = -1
            if (r3 != r1) goto L_0x0020
            boolean r3 = r12.M()
            if (r3 == 0) goto L_0x0020
            r3 = r1
            goto L_0x0021
        L_0x0020:
            r3 = r5
        L_0x0021:
            boolean r6 = r12.A
            if (r6 == 0) goto L_0x0027
            r6 = r5
            goto L_0x002b
        L_0x0027:
            int r0 = r0 + 1
            r6 = r0
            r0 = r4
        L_0x002b:
            if (r0 >= r6) goto L_0x002e
            r5 = r1
        L_0x002e:
            if (r0 == r6) goto L_0x00ab
            android.view.View r7 = r12.c((int) r0)
            android.view.ViewGroup$LayoutParams r8 = r7.getLayoutParams()
            androidx.recyclerview.widget.StaggeredGridLayoutManager$b r8 = (androidx.recyclerview.widget.StaggeredGridLayoutManager.b) r8
            androidx.recyclerview.widget.StaggeredGridLayoutManager$c r9 = r8.e
            int r9 = r9.e
            boolean r9 = r2.get(r9)
            if (r9 == 0) goto L_0x0054
            androidx.recyclerview.widget.StaggeredGridLayoutManager$c r9 = r8.e
            boolean r9 = r12.a((androidx.recyclerview.widget.StaggeredGridLayoutManager.c) r9)
            if (r9 == 0) goto L_0x004d
            return r7
        L_0x004d:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$c r9 = r8.e
            int r9 = r9.e
            r2.clear(r9)
        L_0x0054:
            boolean r9 = r8.f
            if (r9 == 0) goto L_0x0059
            goto L_0x00a9
        L_0x0059:
            int r9 = r0 + r5
            if (r9 == r6) goto L_0x00a9
            android.view.View r9 = r12.c((int) r9)
            boolean r10 = r12.A
            if (r10 == 0) goto L_0x0077
            androidx.recyclerview.widget.B r10 = r12.u
            int r10 = r10.a((android.view.View) r7)
            androidx.recyclerview.widget.B r11 = r12.u
            int r11 = r11.a((android.view.View) r9)
            if (r10 >= r11) goto L_0x0074
            return r7
        L_0x0074:
            if (r10 != r11) goto L_0x008a
            goto L_0x0088
        L_0x0077:
            androidx.recyclerview.widget.B r10 = r12.u
            int r10 = r10.d(r7)
            androidx.recyclerview.widget.B r11 = r12.u
            int r11 = r11.d(r9)
            if (r10 <= r11) goto L_0x0086
            return r7
        L_0x0086:
            if (r10 != r11) goto L_0x008a
        L_0x0088:
            r10 = r1
            goto L_0x008b
        L_0x008a:
            r10 = r4
        L_0x008b:
            if (r10 == 0) goto L_0x00a9
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            androidx.recyclerview.widget.StaggeredGridLayoutManager$b r9 = (androidx.recyclerview.widget.StaggeredGridLayoutManager.b) r9
            androidx.recyclerview.widget.StaggeredGridLayoutManager$c r8 = r8.e
            int r8 = r8.e
            androidx.recyclerview.widget.StaggeredGridLayoutManager$c r9 = r9.e
            int r9 = r9.e
            int r8 = r8 - r9
            if (r8 >= 0) goto L_0x00a0
            r8 = r1
            goto L_0x00a1
        L_0x00a0:
            r8 = r4
        L_0x00a1:
            if (r3 >= 0) goto L_0x00a5
            r9 = r1
            goto L_0x00a6
        L_0x00a5:
            r9 = r4
        L_0x00a6:
            if (r8 == r9) goto L_0x00a9
            return r7
        L_0x00a9:
            int r0 = r0 + r5
            goto L_0x002e
        L_0x00ab:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.StaggeredGridLayoutManager.K():android.view.View");
    }

    public void L() {
        this.E.a();
        z();
    }

    /* access modifiers changed from: package-private */
    public boolean M() {
        return k() == 1;
    }

    public int a(int i, RecyclerView.n nVar, RecyclerView.r rVar) {
        return c(i, nVar, rVar);
    }

    public int a(RecyclerView.n nVar, RecyclerView.r rVar) {
        return this.w == 1 ? this.s : super.a(nVar, rVar);
    }

    public int a(RecyclerView.r rVar) {
        return h(rVar);
    }

    @Nullable
    public View a(View view, int i, RecyclerView.n nVar, RecyclerView.r rVar) {
        View c2;
        View a2;
        if (e() == 0 || (c2 = c(view)) == null) {
            return null;
        }
        P();
        int m = m(i);
        if (m == Integer.MIN_VALUE) {
            return null;
        }
        b bVar = (b) c2.getLayoutParams();
        boolean z2 = bVar.f;
        c cVar = bVar.e;
        int J2 = m == 1 ? J() : I();
        b(J2, rVar);
        w(m);
        C0180v vVar = this.y;
        vVar.f1268c = vVar.f1269d + J2;
        vVar.f1267b = (int) (((float) this.u.g()) * 0.33333334f);
        C0180v vVar2 = this.y;
        vVar2.h = true;
        vVar2.f1266a = false;
        a(nVar, vVar2, rVar);
        this.G = this.A;
        if (!z2 && (a2 = cVar.a(J2, m)) != null && a2 != c2) {
            return a2;
        }
        if (v(m)) {
            for (int i2 = this.s - 1; i2 >= 0; i2--) {
                View a3 = this.t[i2].a(J2, m);
                if (a3 != null && a3 != c2) {
                    return a3;
                }
            }
        } else {
            for (int i3 = 0; i3 < this.s; i3++) {
                View a4 = this.t[i3].a(J2, m);
                if (a4 != null && a4 != c2) {
                    return a4;
                }
            }
        }
        boolean z3 = (this.z ^ true) == (m == -1);
        if (!z2) {
            View b2 = b(z3 ? cVar.d() : cVar.e());
            if (!(b2 == null || b2 == c2)) {
                return b2;
            }
        }
        if (v(m)) {
            for (int i4 = this.s - 1; i4 >= 0; i4--) {
                if (i4 != cVar.e) {
                    View b3 = b(z3 ? this.t[i4].d() : this.t[i4].e());
                    if (!(b3 == null || b3 == c2)) {
                        return b3;
                    }
                }
            }
        } else {
            for (int i5 = 0; i5 < this.s; i5++) {
                View b4 = b(z3 ? this.t[i5].d() : this.t[i5].e());
                if (b4 != null && b4 != c2) {
                    return b4;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public View a(boolean z2) {
        int f = this.u.f();
        int b2 = this.u.b();
        View view = null;
        for (int e = e() - 1; e >= 0; e--) {
            View c2 = c(e);
            int d2 = this.u.d(c2);
            int a2 = this.u.a(c2);
            if (a2 > f && d2 < b2) {
                if (a2 <= b2 || !z2) {
                    return c2;
                }
                if (view == null) {
                    view = c2;
                }
            }
        }
        return view;
    }

    public RecyclerView.h a(Context context, AttributeSet attributeSet) {
        return new b(context, attributeSet);
    }

    public RecyclerView.h a(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof ViewGroup.MarginLayoutParams ? new b((ViewGroup.MarginLayoutParams) layoutParams) : new b(layoutParams);
    }

    @RestrictTo({RestrictTo.a.LIBRARY})
    public void a(int i, int i2, RecyclerView.r rVar, RecyclerView.g.a aVar) {
        int i3;
        int i4;
        if (this.w != 0) {
            i = i2;
        }
        if (e() != 0 && i != 0) {
            a(i, rVar);
            int[] iArr = this.O;
            if (iArr == null || iArr.length < this.s) {
                this.O = new int[this.s];
            }
            int i5 = 0;
            for (int i6 = 0; i6 < this.s; i6++) {
                C0180v vVar = this.y;
                if (vVar.f1269d == -1) {
                    i4 = vVar.f;
                    i3 = this.t[i6].b(i4);
                } else {
                    i4 = this.t[i6].a(vVar.g);
                    i3 = this.y.g;
                }
                int i7 = i4 - i3;
                if (i7 >= 0) {
                    this.O[i5] = i7;
                    i5++;
                }
            }
            Arrays.sort(this.O, 0, i5);
            for (int i8 = 0; i8 < i5 && this.y.a(rVar); i8++) {
                aVar.a(this.y.f1268c, this.O[i8]);
                C0180v vVar2 = this.y;
                vVar2.f1268c += vVar2.f1269d;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(int i, RecyclerView.r rVar) {
        int i2;
        int i3;
        if (i > 0) {
            i3 = J();
            i2 = 1;
        } else {
            i2 = -1;
            i3 = I();
        }
        this.y.f1266a = true;
        b(i3, rVar);
        w(i2);
        C0180v vVar = this.y;
        vVar.f1268c = i3 + vVar.f1269d;
        vVar.f1267b = Math.abs(i);
    }

    public void a(Rect rect, int i, int i2) {
        int i3;
        int i4;
        int o = o() + p();
        int q = q() + n();
        if (this.w == 1) {
            i4 = RecyclerView.g.a(i2, rect.height() + q, l());
            i3 = RecyclerView.g.a(i, (this.x * this.s) + o, m());
        } else {
            i3 = RecyclerView.g.a(i, rect.width() + o, m());
            i4 = RecyclerView.g.a(i2, (this.x * this.s) + q, l());
        }
        c(i3, i4);
    }

    public void a(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            this.I = (SavedState) parcelable;
            z();
        }
    }

    public void a(AccessibilityEvent accessibilityEvent) {
        super.a(accessibilityEvent);
        if (e() > 0) {
            View b2 = b(false);
            View a2 = a(false);
            if (b2 != null && a2 != null) {
                int l = l(b2);
                int l2 = l(a2);
                if (l < l2) {
                    accessibilityEvent.setFromIndex(l);
                    accessibilityEvent.setToIndex(l2);
                    return;
                }
                accessibilityEvent.setFromIndex(l2);
                accessibilityEvent.setToIndex(l);
            }
        }
    }

    public void a(RecyclerView.n nVar, RecyclerView.r rVar, View view, androidx.core.view.a.c cVar) {
        int i;
        int i2;
        int i3;
        int i4;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (!(layoutParams instanceof b)) {
            super.a(view, cVar);
            return;
        }
        b bVar = (b) layoutParams;
        int i5 = 1;
        if (this.w == 0) {
            i4 = bVar.e();
            if (bVar.f) {
                i5 = this.s;
            }
            i3 = i5;
            i2 = -1;
            i = -1;
        } else {
            i4 = -1;
            i3 = -1;
            i2 = bVar.e();
            if (bVar.f) {
                i5 = this.s;
            }
            i = i5;
        }
        cVar.b((Object) c.C0013c.a(i4, i3, i2, i, false, false));
    }

    public void a(RecyclerView recyclerView, int i, int i2) {
        c(i, i2, 1);
    }

    public void a(RecyclerView recyclerView, int i, int i2, int i3) {
        c(i, i2, 8);
    }

    public void a(RecyclerView recyclerView, int i, int i2, Object obj) {
        c(i, i2, 4);
    }

    public void a(String str) {
        if (this.I == null) {
            super.a(str);
        }
    }

    public boolean a() {
        return this.w == 0;
    }

    public boolean a(RecyclerView.h hVar) {
        return hVar instanceof b;
    }

    /* access modifiers changed from: package-private */
    public boolean a(RecyclerView.r rVar, a aVar) {
        int i;
        int i2;
        int i3;
        boolean z2 = false;
        if (!rVar.d() && (i = this.C) != -1) {
            if (i < 0 || i >= rVar.a()) {
                this.C = -1;
                this.D = Integer.MIN_VALUE;
            } else {
                SavedState savedState = this.I;
                if (savedState == null || savedState.mAnchorPosition == -1 || savedState.mSpanOffsetsSize < 1) {
                    View b2 = b(this.C);
                    if (b2 != null) {
                        aVar.f1157a = this.A ? J() : I();
                        if (this.D != Integer.MIN_VALUE) {
                            if (aVar.f1159c) {
                                i3 = this.u.b() - this.D;
                                i2 = this.u.a(b2);
                            } else {
                                i3 = this.u.f() + this.D;
                                i2 = this.u.d(b2);
                            }
                            aVar.f1158b = i3 - i2;
                            return true;
                        } else if (this.u.b(b2) > this.u.g()) {
                            aVar.f1158b = aVar.f1159c ? this.u.b() : this.u.f();
                            return true;
                        } else {
                            int d2 = this.u.d(b2) - this.u.f();
                            if (d2 < 0) {
                                aVar.f1158b = -d2;
                                return true;
                            }
                            int b3 = this.u.b() - this.u.a(b2);
                            if (b3 < 0) {
                                aVar.f1158b = b3;
                                return true;
                            }
                            aVar.f1158b = Integer.MIN_VALUE;
                        }
                    } else {
                        aVar.f1157a = this.C;
                        int i4 = this.D;
                        if (i4 == Integer.MIN_VALUE) {
                            if (l(aVar.f1157a) == 1) {
                                z2 = true;
                            }
                            aVar.f1159c = z2;
                            aVar.a();
                        } else {
                            aVar.a(i4);
                        }
                        aVar.f1160d = true;
                    }
                } else {
                    aVar.f1158b = Integer.MIN_VALUE;
                    aVar.f1157a = this.C;
                }
                return true;
            }
        }
        return false;
    }

    public int b(int i, RecyclerView.n nVar, RecyclerView.r rVar) {
        return c(i, nVar, rVar);
    }

    public int b(RecyclerView.n nVar, RecyclerView.r rVar) {
        return this.w == 0 ? this.s : super.b(nVar, rVar);
    }

    public int b(RecyclerView.r rVar) {
        return i(rVar);
    }

    /* access modifiers changed from: package-private */
    public View b(boolean z2) {
        int f = this.u.f();
        int b2 = this.u.b();
        int e = e();
        View view = null;
        for (int i = 0; i < e; i++) {
            View c2 = c(i);
            int d2 = this.u.d(c2);
            if (this.u.a(c2) > f && d2 < b2) {
                if (d2 >= f || !z2) {
                    return c2;
                }
                if (view == null) {
                    view = c2;
                }
            }
        }
        return view;
    }

    /* access modifiers changed from: package-private */
    public void b(RecyclerView.r rVar, a aVar) {
        if (!a(rVar, aVar) && !c(rVar, aVar)) {
            aVar.a();
            aVar.f1157a = 0;
        }
    }

    public void b(RecyclerView recyclerView, int i, int i2) {
        c(i, i2, 2);
    }

    public void b(RecyclerView recyclerView, RecyclerView.n nVar) {
        super.b(recyclerView, nVar);
        a(this.P);
        for (int i = 0; i < this.s; i++) {
            this.t[i].c();
        }
        recyclerView.requestLayout();
    }

    public boolean b() {
        return this.w == 1;
    }

    /* access modifiers changed from: package-private */
    public int c(int i, RecyclerView.n nVar, RecyclerView.r rVar) {
        if (e() == 0 || i == 0) {
            return 0;
        }
        a(i, rVar);
        int a2 = a(nVar, this.y, rVar);
        if (this.y.f1267b >= a2) {
            i = i < 0 ? -a2 : a2;
        }
        this.u.a(-i);
        this.G = this.A;
        C0180v vVar = this.y;
        vVar.f1267b = 0;
        a(nVar, vVar);
        return i;
    }

    public int c(RecyclerView.r rVar) {
        return j(rVar);
    }

    public RecyclerView.h c() {
        return this.w == 0 ? new b(-2, -1) : new b(-1, -2);
    }

    public void c(boolean z2) {
        a((String) null);
        SavedState savedState = this.I;
        if (!(savedState == null || savedState.mReverseLayout == z2)) {
            savedState.mReverseLayout = z2;
        }
        this.z = z2;
        z();
    }

    public int d(RecyclerView.r rVar) {
        return h(rVar);
    }

    public void d(int i) {
        super.d(i);
        for (int i2 = 0; i2 < this.s; i2++) {
            this.t[i2].c(i);
        }
    }

    public void d(RecyclerView recyclerView) {
        this.E.a();
        z();
    }

    public int e(RecyclerView.r rVar) {
        return i(rVar);
    }

    public void e(int i) {
        super.e(i);
        for (int i2 = 0; i2 < this.s; i2++) {
            this.t[i2].c(i);
        }
    }

    public void e(RecyclerView.n nVar, RecyclerView.r rVar) {
        c(nVar, rVar, true);
    }

    public int f(RecyclerView.r rVar) {
        return j(rVar);
    }

    public void f(int i) {
        if (i == 0) {
            G();
        }
    }

    public void g(RecyclerView.r rVar) {
        super.g(rVar);
        this.C = -1;
        this.D = Integer.MIN_VALUE;
        this.I = null;
        this.L.b();
    }

    public void h(int i) {
        SavedState savedState = this.I;
        if (!(savedState == null || savedState.mAnchorPosition == i)) {
            savedState.invalidateAnchorPositionInfo();
        }
        this.C = i;
        this.D = Integer.MIN_VALUE;
        z();
    }

    public void i(int i) {
        if (i == 0 || i == 1) {
            a((String) null);
            if (i != this.w) {
                this.w = i;
                B b2 = this.u;
                this.u = this.v;
                this.v = b2;
                z();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("invalid orientation.");
    }

    public void j(int i) {
        a((String) null);
        if (i != this.s) {
            L();
            this.s = i;
            this.B = new BitSet(this.s);
            this.t = new c[this.s];
            for (int i2 = 0; i2 < this.s; i2++) {
                this.t[i2] = new c(i2);
            }
            z();
        }
    }

    /* access modifiers changed from: package-private */
    public void k(int i) {
        this.x = i / this.s;
        this.J = View.MeasureSpec.makeMeasureSpec(i, this.v.d());
    }

    public boolean v() {
        return this.F != 0;
    }

    public Parcelable y() {
        int i;
        int i2;
        int[] iArr;
        SavedState savedState = this.I;
        if (savedState != null) {
            return new SavedState(savedState);
        }
        SavedState savedState2 = new SavedState();
        savedState2.mReverseLayout = this.z;
        savedState2.mAnchorLayoutFromEnd = this.G;
        savedState2.mLastLayoutRTL = this.H;
        LazySpanLookup lazySpanLookup = this.E;
        if (lazySpanLookup == null || (iArr = lazySpanLookup.f1155a) == null) {
            savedState2.mSpanLookupSize = 0;
        } else {
            savedState2.mSpanLookup = iArr;
            savedState2.mSpanLookupSize = savedState2.mSpanLookup.length;
            savedState2.mFullSpanItems = lazySpanLookup.f1156b;
        }
        if (e() > 0) {
            savedState2.mAnchorPosition = this.G ? J() : I();
            savedState2.mVisibleAnchorPosition = H();
            int i3 = this.s;
            savedState2.mSpanOffsetsSize = i3;
            savedState2.mSpanOffsets = new int[i3];
            for (int i4 = 0; i4 < this.s; i4++) {
                if (this.G) {
                    i = this.t[i4].a(Integer.MIN_VALUE);
                    if (i != Integer.MIN_VALUE) {
                        i2 = this.u.b();
                    } else {
                        savedState2.mSpanOffsets[i4] = i;
                    }
                } else {
                    i = this.t[i4].b(Integer.MIN_VALUE);
                    if (i != Integer.MIN_VALUE) {
                        i2 = this.u.f();
                    } else {
                        savedState2.mSpanOffsets[i4] = i;
                    }
                }
                i -= i2;
                savedState2.mSpanOffsets[i4] = i;
            }
        } else {
            savedState2.mAnchorPosition = -1;
            savedState2.mVisibleAnchorPosition = -1;
            savedState2.mSpanOffsetsSize = 0;
        }
        return savedState2;
    }
}

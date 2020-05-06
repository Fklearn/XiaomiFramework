package androidx.fragment.app;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.z;
import androidx.lifecycle.f;
import java.util.ArrayList;

@SuppressLint({"BanParcelableUsage"})
final class BackStackState implements Parcelable {
    public static final Parcelable.Creator<BackStackState> CREATOR = new C0132b();
    final int mBreadCrumbShortTitleRes;
    final CharSequence mBreadCrumbShortTitleText;
    final int mBreadCrumbTitleRes;
    final CharSequence mBreadCrumbTitleText;
    final int[] mCurrentMaxLifecycleStates;
    final ArrayList<String> mFragmentWhos;
    final int mIndex;
    final String mName;
    final int[] mOldMaxLifecycleStates;
    final int[] mOps;
    final boolean mReorderingAllowed;
    final ArrayList<String> mSharedElementSourceNames;
    final ArrayList<String> mSharedElementTargetNames;
    final int mTransition;
    final int mTransitionStyle;

    public BackStackState(Parcel parcel) {
        this.mOps = parcel.createIntArray();
        this.mFragmentWhos = parcel.createStringArrayList();
        this.mOldMaxLifecycleStates = parcel.createIntArray();
        this.mCurrentMaxLifecycleStates = parcel.createIntArray();
        this.mTransition = parcel.readInt();
        this.mTransitionStyle = parcel.readInt();
        this.mName = parcel.readString();
        this.mIndex = parcel.readInt();
        this.mBreadCrumbTitleRes = parcel.readInt();
        this.mBreadCrumbTitleText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mBreadCrumbShortTitleRes = parcel.readInt();
        this.mBreadCrumbShortTitleText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mSharedElementSourceNames = parcel.createStringArrayList();
        this.mSharedElementTargetNames = parcel.createStringArrayList();
        this.mReorderingAllowed = parcel.readInt() != 0;
    }

    public BackStackState(C0131a aVar) {
        int size = aVar.f953a.size();
        this.mOps = new int[(size * 5)];
        if (aVar.h) {
            this.mFragmentWhos = new ArrayList<>(size);
            this.mOldMaxLifecycleStates = new int[size];
            this.mCurrentMaxLifecycleStates = new int[size];
            int i = 0;
            int i2 = 0;
            while (i < size) {
                z.a aVar2 = aVar.f953a.get(i);
                int i3 = i2 + 1;
                this.mOps[i2] = aVar2.f957a;
                ArrayList<String> arrayList = this.mFragmentWhos;
                Fragment fragment = aVar2.f958b;
                arrayList.add(fragment != null ? fragment.f : null);
                int[] iArr = this.mOps;
                int i4 = i3 + 1;
                iArr[i3] = aVar2.f959c;
                int i5 = i4 + 1;
                iArr[i4] = aVar2.f960d;
                int i6 = i5 + 1;
                iArr[i5] = aVar2.e;
                iArr[i6] = aVar2.f;
                this.mOldMaxLifecycleStates[i] = aVar2.g.ordinal();
                this.mCurrentMaxLifecycleStates[i] = aVar2.h.ordinal();
                i++;
                i2 = i6 + 1;
            }
            this.mTransition = aVar.f;
            this.mTransitionStyle = aVar.g;
            this.mName = aVar.j;
            this.mIndex = aVar.u;
            this.mBreadCrumbTitleRes = aVar.k;
            this.mBreadCrumbTitleText = aVar.l;
            this.mBreadCrumbShortTitleRes = aVar.m;
            this.mBreadCrumbShortTitleText = aVar.n;
            this.mSharedElementSourceNames = aVar.o;
            this.mSharedElementTargetNames = aVar.p;
            this.mReorderingAllowed = aVar.q;
            return;
        }
        throw new IllegalStateException("Not on back stack");
    }

    public int describeContents() {
        return 0;
    }

    public C0131a instantiate(t tVar) {
        C0131a aVar = new C0131a(tVar);
        int i = 0;
        int i2 = 0;
        while (i < this.mOps.length) {
            z.a aVar2 = new z.a();
            int i3 = i + 1;
            aVar2.f957a = this.mOps[i];
            if (t.f937c) {
                Log.v("FragmentManager", "Instantiate " + aVar + " op #" + i2 + " base fragment #" + this.mOps[i3]);
            }
            String str = this.mFragmentWhos.get(i2);
            aVar2.f958b = str != null ? tVar.j.get(str) : null;
            aVar2.g = f.b.values()[this.mOldMaxLifecycleStates[i2]];
            aVar2.h = f.b.values()[this.mCurrentMaxLifecycleStates[i2]];
            int[] iArr = this.mOps;
            int i4 = i3 + 1;
            aVar2.f959c = iArr[i3];
            int i5 = i4 + 1;
            aVar2.f960d = iArr[i4];
            int i6 = i5 + 1;
            aVar2.e = iArr[i5];
            aVar2.f = iArr[i6];
            aVar.f954b = aVar2.f959c;
            aVar.f955c = aVar2.f960d;
            aVar.f956d = aVar2.e;
            aVar.e = aVar2.f;
            aVar.a(aVar2);
            i2++;
            i = i6 + 1;
        }
        aVar.f = this.mTransition;
        aVar.g = this.mTransitionStyle;
        aVar.j = this.mName;
        aVar.u = this.mIndex;
        aVar.h = true;
        aVar.k = this.mBreadCrumbTitleRes;
        aVar.l = this.mBreadCrumbTitleText;
        aVar.m = this.mBreadCrumbShortTitleRes;
        aVar.n = this.mBreadCrumbShortTitleText;
        aVar.o = this.mSharedElementSourceNames;
        aVar.p = this.mSharedElementTargetNames;
        aVar.q = this.mReorderingAllowed;
        aVar.a(1);
        return aVar;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(this.mOps);
        parcel.writeStringList(this.mFragmentWhos);
        parcel.writeIntArray(this.mOldMaxLifecycleStates);
        parcel.writeIntArray(this.mCurrentMaxLifecycleStates);
        parcel.writeInt(this.mTransition);
        parcel.writeInt(this.mTransitionStyle);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mIndex);
        parcel.writeInt(this.mBreadCrumbTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbTitleText, parcel, 0);
        parcel.writeInt(this.mBreadCrumbShortTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbShortTitleText, parcel, 0);
        parcel.writeStringList(this.mSharedElementSourceNames);
        parcel.writeStringList(this.mSharedElementTargetNames);
        parcel.writeInt(this.mReorderingAllowed ? 1 : 0);
    }
}

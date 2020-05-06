package androidx.fragment.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.f;

@SuppressLint({"BanParcelableUsage"})
final class FragmentState implements Parcelable {
    public static final Parcelable.Creator<FragmentState> CREATOR = new x();
    final Bundle mArguments;
    final String mClassName;
    final int mContainerId;
    final boolean mDetached;
    final int mFragmentId;
    final boolean mFromLayout;
    final boolean mHidden;
    Fragment mInstance;
    final int mMaxLifecycleState;
    final boolean mRemoving;
    final boolean mRetainInstance;
    Bundle mSavedFragmentState;
    final String mTag;
    final String mWho;

    FragmentState(Parcel parcel) {
        this.mClassName = parcel.readString();
        this.mWho = parcel.readString();
        boolean z = true;
        this.mFromLayout = parcel.readInt() != 0;
        this.mFragmentId = parcel.readInt();
        this.mContainerId = parcel.readInt();
        this.mTag = parcel.readString();
        this.mRetainInstance = parcel.readInt() != 0;
        this.mRemoving = parcel.readInt() != 0;
        this.mDetached = parcel.readInt() != 0;
        this.mArguments = parcel.readBundle();
        this.mHidden = parcel.readInt() == 0 ? false : z;
        this.mSavedFragmentState = parcel.readBundle();
        this.mMaxLifecycleState = parcel.readInt();
    }

    FragmentState(Fragment fragment) {
        this.mClassName = fragment.getClass().getName();
        this.mWho = fragment.f;
        this.mFromLayout = fragment.n;
        this.mFragmentId = fragment.w;
        this.mContainerId = fragment.x;
        this.mTag = fragment.y;
        this.mRetainInstance = fragment.B;
        this.mRemoving = fragment.m;
        this.mDetached = fragment.A;
        this.mArguments = fragment.g;
        this.mHidden = fragment.z;
        this.mMaxLifecycleState = fragment.S.ordinal();
    }

    public int describeContents() {
        return 0;
    }

    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull C0140j jVar) {
        Bundle bundle;
        Fragment fragment;
        if (this.mInstance == null) {
            Bundle bundle2 = this.mArguments;
            if (bundle2 != null) {
                bundle2.setClassLoader(classLoader);
            }
            this.mInstance = jVar.a(classLoader, this.mClassName);
            this.mInstance.m(this.mArguments);
            Bundle bundle3 = this.mSavedFragmentState;
            if (bundle3 != null) {
                bundle3.setClassLoader(classLoader);
                fragment = this.mInstance;
                bundle = this.mSavedFragmentState;
            } else {
                fragment = this.mInstance;
                bundle = new Bundle();
            }
            fragment.f884c = bundle;
            Fragment fragment2 = this.mInstance;
            fragment2.f = this.mWho;
            fragment2.n = this.mFromLayout;
            fragment2.p = true;
            fragment2.w = this.mFragmentId;
            fragment2.x = this.mContainerId;
            fragment2.y = this.mTag;
            fragment2.B = this.mRetainInstance;
            fragment2.m = this.mRemoving;
            fragment2.A = this.mDetached;
            fragment2.z = this.mHidden;
            fragment2.S = f.b.values()[this.mMaxLifecycleState];
            if (t.f937c) {
                Log.v("FragmentManager", "Instantiated fragment " + this.mInstance);
            }
        }
        return this.mInstance;
    }

    @NonNull
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("FragmentState{");
        sb.append(this.mClassName);
        sb.append(" (");
        sb.append(this.mWho);
        sb.append(")}:");
        if (this.mFromLayout) {
            sb.append(" fromLayout");
        }
        if (this.mContainerId != 0) {
            sb.append(" id=0x");
            sb.append(Integer.toHexString(this.mContainerId));
        }
        String str = this.mTag;
        if (str != null && !str.isEmpty()) {
            sb.append(" tag=");
            sb.append(this.mTag);
        }
        if (this.mRetainInstance) {
            sb.append(" retainInstance");
        }
        if (this.mRemoving) {
            sb.append(" removing");
        }
        if (this.mDetached) {
            sb.append(" detached");
        }
        if (this.mHidden) {
            sb.append(" hidden");
        }
        return sb.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mClassName);
        parcel.writeString(this.mWho);
        parcel.writeInt(this.mFromLayout ? 1 : 0);
        parcel.writeInt(this.mFragmentId);
        parcel.writeInt(this.mContainerId);
        parcel.writeString(this.mTag);
        parcel.writeInt(this.mRetainInstance ? 1 : 0);
        parcel.writeInt(this.mRemoving ? 1 : 0);
        parcel.writeInt(this.mDetached ? 1 : 0);
        parcel.writeBundle(this.mArguments);
        parcel.writeInt(this.mHidden ? 1 : 0);
        parcel.writeBundle(this.mSavedFragmentState);
        parcel.writeInt(this.mMaxLifecycleState);
    }
}

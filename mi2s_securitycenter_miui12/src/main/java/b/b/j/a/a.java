package b.b.j.a;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class a extends PagerAdapter {

    /* renamed from: a  reason: collision with root package name */
    private final FragmentManager f1783a;

    /* renamed from: b  reason: collision with root package name */
    private FragmentTransaction f1784b = null;

    /* renamed from: c  reason: collision with root package name */
    private Fragment f1785c = null;

    /* renamed from: d  reason: collision with root package name */
    private Fragment[] f1786d;

    public a(FragmentManager fragmentManager, Fragment[] fragmentArr) {
        this.f1783a = fragmentManager;
        this.f1786d = fragmentArr;
    }

    public static String a(int i, long j) {
        return "android:switcher:" + i + ":" + j;
    }

    public Fragment a(int i) {
        return this.f1786d[i];
    }

    public long b(int i) {
        return (long) i;
    }

    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        if (this.f1784b == null) {
            this.f1784b = this.f1783a.beginTransaction();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Detaching item #");
        sb.append(b(i));
        sb.append(": f=");
        sb.append(obj);
        sb.append(" v=");
        Fragment fragment = (Fragment) obj;
        sb.append(fragment.getView());
        Log.v("TabViewPagerAdapter", sb.toString());
        this.f1784b.detach(fragment);
    }

    public void finishUpdate(ViewGroup viewGroup) {
        FragmentTransaction fragmentTransaction = this.f1784b;
        if (fragmentTransaction != null) {
            fragmentTransaction.commitAllowingStateLoss();
            this.f1784b = null;
            this.f1783a.executePendingTransactions();
        }
    }

    public int getCount() {
        return this.f1786d.length;
    }

    public Object instantiateItem(ViewGroup viewGroup, int i) {
        if (this.f1784b == null) {
            this.f1784b = this.f1783a.beginTransaction();
        }
        long b2 = b(i);
        Fragment findFragmentByTag = this.f1783a.findFragmentByTag(a(viewGroup.getId(), b2));
        if (findFragmentByTag != null) {
            Log.v("TabViewPagerAdapter", "Attaching item #" + b2 + ": f=" + findFragmentByTag);
            this.f1784b.attach(findFragmentByTag);
        } else {
            findFragmentByTag = a(i);
            Log.v("TabViewPagerAdapter", "Adding item #" + b2 + ": f=" + findFragmentByTag);
            this.f1784b.add(viewGroup.getId(), findFragmentByTag, a(viewGroup.getId(), b2));
        }
        if (findFragmentByTag != this.f1785c) {
            findFragmentByTag.setMenuVisibility(false);
            findFragmentByTag.setUserVisibleHint(false);
        }
        return findFragmentByTag;
    }

    public boolean isViewFromObject(View view, Object obj) {
        return ((Fragment) obj).getView() == view;
    }

    public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
    }

    public Parcelable saveState() {
        return null;
    }

    public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
        Fragment fragment = (Fragment) obj;
        Fragment fragment2 = this.f1785c;
        if (fragment != fragment2) {
            if (fragment2 != null) {
                fragment2.setMenuVisibility(false);
                this.f1785c.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            this.f1785c = fragment;
        }
    }

    public void startUpdate(ViewGroup viewGroup) {
        if (viewGroup.getId() == -1) {
            throw new IllegalStateException("ViewPager with adapter " + this + " requires a view id");
        }
    }
}

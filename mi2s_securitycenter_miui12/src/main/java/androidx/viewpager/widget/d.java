package androidx.viewpager.widget;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ViewPager f1299a;

    d(ViewPager viewPager) {
        this.f1299a = viewPager;
    }

    public void run() {
        this.f1299a.setScrollState(0);
        this.f1299a.e();
    }
}

package miuix.nestedheader.widget;

import miuix.nestedheader.widget.c;

class a implements c.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ NestedHeaderLayout f8869a;

    a(NestedHeaderLayout nestedHeaderLayout) {
        this.f8869a = nestedHeaderLayout;
    }

    public void a(int i) {
        if (i == 0) {
            boolean unused = this.f8869a.Q = false;
        }
    }

    public void b(int i) {
        if (this.f8869a.R && !this.f8869a.P && this.f8869a.getScrollingProgress() != 0 && this.f8869a.getScrollingProgress() < this.f8869a.G && this.f8869a.getScrollingProgress() > this.f8869a.F) {
            int i2 = 0;
            if (this.f8869a.getScrollingProgress() > this.f8869a.F && ((float) this.f8869a.getScrollingProgress()) < ((float) this.f8869a.F) * 0.5f) {
                i2 = this.f8869a.F;
            } else if ((((float) this.f8869a.getScrollingProgress()) < ((float) this.f8869a.F) * 0.5f || this.f8869a.getScrollingProgress() >= 0) && ((this.f8869a.getScrollingProgress() <= 0 || ((float) this.f8869a.getScrollingProgress()) >= ((float) this.f8869a.G) * 0.5f) && ((float) this.f8869a.getScrollingProgress()) >= ((float) this.f8869a.G) * 0.5f && this.f8869a.getScrollingProgress() < this.f8869a.G)) {
                i2 = this.f8869a.G;
            }
            this.f8869a.d(i2);
        }
    }

    public void c(int i) {
        if (i == 0) {
            boolean unused = this.f8869a.Q = true;
        }
    }
}

package b.b.j;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import com.miui.securityscan.MainActivity;

class e implements View.OnTouchListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f1817a;

    e(h hVar) {
        this.f1817a = hVar;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action;
        float f;
        h hVar;
        Activity activity = this.f1817a.getActivity();
        if (this.f1817a.a(activity) && (action = motionEvent.getAction()) != 0) {
            if (action == 1) {
                float unused = this.f1817a.q = 0.0f;
                float unused2 = this.f1817a.r = 0.0f;
            } else if (action == 2) {
                if (this.f1817a.r == 0.0f) {
                    hVar = this.f1817a;
                    f = hVar.f1820a.getFirstY();
                } else {
                    hVar = this.f1817a;
                    f = hVar.r;
                }
                float unused3 = hVar.q = f;
                float unused4 = this.f1817a.r = motionEvent.getY();
                MainActivity mainActivity = (MainActivity) activity;
                boolean m = mainActivity.m();
                if (this.f1817a.r - this.f1817a.q > 5.0f && !m) {
                    mainActivity.a(true, true);
                } else if (this.f1817a.q - this.f1817a.r > 5.0f && m) {
                    mainActivity.a(false, true);
                }
            }
        }
        return false;
    }
}

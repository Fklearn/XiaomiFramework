package b.b.c.c;

import android.app.Fragment;
import android.os.Bundle;
import miui.app.Activity;

public abstract class b extends Activity {
    public abstract Fragment a();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        b.super.onCreate(bundle);
        getFragmentManager().beginTransaction().replace(16908290, a()).commit();
    }
}

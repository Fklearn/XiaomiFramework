package com.miui.networkassistant.ui.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import b.b.c.c.b.g;

public class TransActionBarUniFragmentActivity extends UniversalFragmentActivity {
    public static Intent getIntent(Context context, Class<? extends Fragment> cls, Bundle bundle) {
        Intent intent = new Intent(context.getApplicationContext(), TransActionBarUniFragmentActivity.class);
        Bundle bundle2 = new Bundle();
        bundle2.putString(g.FRAGMENT_NAME, cls.getName());
        bundle2.putBundle(g.FRAGMENT_ARGS, bundle);
        intent.putExtras(bundle2);
        return intent;
    }

    public static void startWithFragment(Activity activity, Class<? extends Fragment> cls) {
        startWithFragment(activity, cls, (Bundle) null);
    }

    public static void startWithFragment(Activity activity, Class<? extends Fragment> cls, Bundle bundle) {
        Intent intent = new Intent(activity, TransActionBarUniFragmentActivity.class);
        Bundle bundle2 = new Bundle();
        bundle2.putString(g.FRAGMENT_NAME, cls.getName());
        bundle2.putBundle(g.FRAGMENT_ARGS, bundle);
        intent.putExtras(bundle2);
        activity.startActivity(intent);
    }
}

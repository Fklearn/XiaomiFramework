package b.b.c.c.b;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import b.b.c.c.a.b;
import com.miui.networkassistant.utils.PackageUtil;
import java.lang.reflect.Field;
import miui.app.Activity;

public abstract class g extends Activity {
    protected static final String ACTION_UNIVERSAL_FRAGMENT_ACTIVITY = "miui.intent.action.NETWORKASSISTANT_UNIVERSAL_FRAGMENT_ACTIVITY";
    public static final String EXTRA_MIUI_STARTING_WINDOW_LABEL = ":miui:starting_window_label";
    public static final String FRAGMENT_ARGS = "fragment_arg";
    public static final String FRAGMENT_NAME = "fragment_name";
    private static final String TAG = "g";
    private MessageQueue mMsgQueue = Looper.myQueue();
    private Handler mUIHandler = new Handler();

    public static Intent getIntent(Context context, Class<? extends Fragment> cls, Bundle bundle) {
        Intent intent = new Intent(ACTION_UNIVERSAL_FRAGMENT_ACTIVITY);
        intent.setPackage(context.getPackageName());
        Bundle bundle2 = new Bundle();
        bundle2.putString(FRAGMENT_NAME, cls.getName());
        bundle2.putBundle(FRAGMENT_ARGS, bundle);
        intent.putExtras(bundle2);
        int i = -1;
        try {
            Field declaredField = cls.getDeclaredField(d.TITLE_FILED_NAME);
            if (declaredField != null) {
                declaredField.setAccessible(true);
                i = declaredField.getInt((Object) null);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        }
        intent.putExtra(":miui:starting_window_label", i > 0 ? context.getString(i) : "");
        return intent;
    }

    public static void startWithFragment(android.app.Activity activity, Class<? extends Fragment> cls) {
        startWithFragment(activity, cls, (Bundle) null);
    }

    public static void startWithFragment(android.app.Activity activity, Class<? extends Fragment> cls, Bundle bundle) {
        activity.startActivity(getIntent(activity, cls, bundle));
    }

    public static void startWithFragmentForResult(android.app.Activity activity, Class<? extends Fragment> cls, Bundle bundle, int i) {
        activity.startActivityForResult(getIntent(activity, cls, bundle), i);
    }

    public static void startWithFragmentForResult(Fragment fragment, Class<? extends Fragment> cls, Bundle bundle, int i) {
        fragment.startActivityForResult(getIntent(fragment.getActivity(), cls, bundle), i);
    }

    /* access modifiers changed from: protected */
    public abstract boolean checkAction(String str);

    /* access modifiers changed from: protected */
    public d getCurrentBaseFragment() {
        Fragment findFragmentById = getFragmentManager().findFragmentById(16908290);
        if (findFragmentById == null || !(findFragmentById instanceof d)) {
            return null;
        }
        return (d) findFragmentById;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, b.b.c.c.b.g, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void launchFragment(String str, Bundle bundle) {
        getFragmentManager().beginTransaction().replace(16908290, Fragment.instantiate(this, str, bundle)).commit();
        Log.i(TAG, String.format("Fragment %s Displayed", new Object[]{str}));
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.b.g, miui.app.Activity, android.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        g.super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if (TextUtils.isEmpty(action)) {
                finish();
            } else if (!checkAction(action)) {
                String str = TAG;
                Log.e(str, "invalid action: " + action);
                finish();
            } else if (!resolveAction(action, extras)) {
                String reflectGetReferrer = PackageUtil.reflectGetReferrer(this);
                if (extras != null && TextUtils.equals(reflectGetReferrer, getPackageName())) {
                    String string = extras.getString(FRAGMENT_NAME);
                    Bundle bundle2 = extras.getBundle(FRAGMENT_ARGS);
                    if (bundle == null) {
                        launchFragment(string, bundle2);
                    }
                }
            }
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        d currentBaseFragment = getCurrentBaseFragment();
        if (!(currentBaseFragment != null ? currentBaseFragment.onKeyDown(i, keyEvent) : false)) {
            return g.super.onKeyDown(i, keyEvent);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        d currentBaseFragment = getCurrentBaseFragment();
        if (!(currentBaseFragment != null ? currentBaseFragment.onOptionsItemSelectedByActivity(menuItem) : false)) {
            return g.super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        d currentBaseFragment = getCurrentBaseFragment();
        if (currentBaseFragment != null ? currentBaseFragment.onPrepareOptionsMenuByActivity(menu) : false) {
            return g.super.onPrepareOptionsMenu(menu);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void postOnIdleUiThread(MessageQueue.IdleHandler idleHandler) {
        this.mMsgQueue.addIdleHandler(idleHandler);
    }

    /* access modifiers changed from: protected */
    public void postOnUiThread(b bVar) {
        this.mUIHandler.post(bVar);
    }

    /* access modifiers changed from: protected */
    public abstract boolean resolveAction(String str, Bundle bundle);
}

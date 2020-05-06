package androidx.preference;

import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.XmlRes;
import androidx.core.content.res.h;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceGroup;
import androidx.preference.z;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@Deprecated
public abstract class r extends Fragment implements z.c, z.a, z.b, DialogPreference.a {
    @Deprecated
    public static final String ARG_PREFERENCE_ROOT = "androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT";
    private static final String DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG";
    private static final int MSG_BIND_PREFERENCES = 1;
    private static final String PREFERENCES_TAG = "android:preferences";
    private final a mDividerDecoration = new a();
    private final Handler mHandler = new o(this);
    private boolean mHavePrefs;
    private boolean mInitDone;
    private int mLayoutResId = F.preference_list_fragment;
    RecyclerView mList;
    private z mPreferenceManager;
    private final Runnable mRequestFocus = new p(this);
    private Runnable mSelectPreferenceRunnable;
    private Context mStyledContext;

    private class a extends RecyclerView.f {

        /* renamed from: a  reason: collision with root package name */
        private Drawable f1048a;

        /* renamed from: b  reason: collision with root package name */
        private int f1049b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f1050c = true;

        a() {
        }

        private boolean a(View view, RecyclerView recyclerView) {
            RecyclerView.u g = recyclerView.g(view);
            if (!((g instanceof A) && ((A) g).b())) {
                return false;
            }
            boolean z = this.f1050c;
            int indexOfChild = recyclerView.indexOfChild(view);
            if (indexOfChild >= recyclerView.getChildCount() - 1) {
                return z;
            }
            RecyclerView.u g2 = recyclerView.g(recyclerView.getChildAt(indexOfChild + 1));
            return (g2 instanceof A) && ((A) g2).a();
        }

        public void a(int i) {
            this.f1049b = i;
            r.this.mList.m();
        }

        public void a(Rect rect, View view, RecyclerView recyclerView, RecyclerView.r rVar) {
            if (a(view, recyclerView)) {
                rect.bottom = this.f1049b;
            }
        }

        public void a(Drawable drawable) {
            this.f1049b = drawable != null ? drawable.getIntrinsicHeight() : 0;
            this.f1048a = drawable;
            r.this.mList.m();
        }

        public void b(Canvas canvas, RecyclerView recyclerView, RecyclerView.r rVar) {
            if (this.f1048a != null) {
                int childCount = recyclerView.getChildCount();
                int width = recyclerView.getWidth();
                for (int i = 0; i < childCount; i++) {
                    View childAt = recyclerView.getChildAt(i);
                    if (a(childAt, recyclerView)) {
                        int y = ((int) childAt.getY()) + childAt.getHeight();
                        this.f1048a.setBounds(0, y, width, this.f1049b + y);
                        this.f1048a.draw(canvas);
                    }
                }
            }
        }

        public void b(boolean z) {
            this.f1050c = z;
        }
    }

    public interface b {
        boolean a(@NonNull r rVar, Preference preference);
    }

    public interface c {
        boolean a(r rVar, Preference preference);
    }

    public interface d {
        boolean a(r rVar, PreferenceScreen preferenceScreen);
    }

    private static class e extends RecyclerView.c {

        /* renamed from: a  reason: collision with root package name */
        private final RecyclerView.a f1052a;

        /* renamed from: b  reason: collision with root package name */
        private final RecyclerView f1053b;

        /* renamed from: c  reason: collision with root package name */
        private final Preference f1054c;

        /* renamed from: d  reason: collision with root package name */
        private final String f1055d;

        e(RecyclerView.a aVar, RecyclerView recyclerView, Preference preference, String str) {
            this.f1052a = aVar;
            this.f1053b = recyclerView;
            this.f1054c = preference;
            this.f1055d = str;
        }

        private void b() {
            this.f1052a.unregisterAdapterDataObserver(this);
            Preference preference = this.f1054c;
            int b2 = preference != null ? ((PreferenceGroup.b) this.f1052a).b(preference) : ((PreferenceGroup.b) this.f1052a).a(this.f1055d);
            if (b2 != -1) {
                this.f1053b.f(b2);
            }
        }

        public void a() {
            b();
        }

        public void a(int i, int i2) {
            b();
        }

        public void a(int i, int i2, int i3) {
            b();
        }

        public void a(int i, int i2, Object obj) {
            b();
        }

        public void b(int i, int i2) {
            b();
        }

        public void c(int i, int i2) {
            b();
        }
    }

    private void postBindPreferences() {
        if (!this.mHandler.hasMessages(1)) {
            this.mHandler.obtainMessage(1).sendToTarget();
        }
    }

    private void requirePreferenceManager() {
        if (this.mPreferenceManager == null) {
            throw new RuntimeException("This should be called after super.onCreate.");
        }
    }

    private void scrollToPreferenceInternal(Preference preference, String str) {
        q qVar = new q(this, preference, str);
        if (this.mList == null) {
            this.mSelectPreferenceRunnable = qVar;
        } else {
            qVar.run();
        }
    }

    private void unbindPreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.onDetached();
        }
        onUnbindPreferences();
    }

    @Deprecated
    public void addPreferencesFromResource(@XmlRes int i) {
        requirePreferenceManager();
        setPreferenceScreen(this.mPreferenceManager.a(this.mStyledContext, i, getPreferenceScreen()));
    }

    /* access modifiers changed from: package-private */
    public void bindPreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            getListView().setAdapter(onCreateAdapter(preferenceScreen));
            preferenceScreen.onAttached();
        }
        onBindPreferences();
    }

    @Deprecated
    public <T extends Preference> T findPreference(CharSequence charSequence) {
        z zVar = this.mPreferenceManager;
        if (zVar == null) {
            return null;
        }
        return zVar.a(charSequence);
    }

    @RestrictTo({RestrictTo.a.LIBRARY})
    public Fragment getCallbackFragment() {
        return null;
    }

    @Deprecated
    public final RecyclerView getListView() {
        return this.mList;
    }

    @Deprecated
    public z getPreferenceManager() {
        return this.mPreferenceManager;
    }

    @Deprecated
    public PreferenceScreen getPreferenceScreen() {
        return this.mPreferenceManager.h();
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.a.LIBRARY})
    public void onBindPreferences() {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(B.preferenceTheme, typedValue, true);
        int i = typedValue.resourceId;
        if (i == 0) {
            i = H.PreferenceThemeOverlay;
        }
        this.mStyledContext = new ContextThemeWrapper(getActivity(), i);
        this.mPreferenceManager = new z(this.mStyledContext);
        this.mPreferenceManager.a((z.b) this);
        onCreatePreferences(bundle, getArguments() != null ? getArguments().getString(ARG_PREFERENCE_ROOT) : null);
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public abstract RecyclerView.a onCreateAdapter(PreferenceScreen preferenceScreen);

    @Deprecated
    public RecyclerView.g onCreateLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Deprecated
    public abstract void onCreatePreferences(Bundle bundle, String str);

    @Deprecated
    public abstract RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle);

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Context context = this.mStyledContext;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes((AttributeSet) null, I.PreferenceFragment, h.a(context, B.preferenceFragmentStyle, 16844038), 0);
        this.mLayoutResId = obtainStyledAttributes.getResourceId(I.PreferenceFragment_android_layout, this.mLayoutResId);
        Drawable drawable = obtainStyledAttributes.getDrawable(I.PreferenceFragment_android_divider);
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(I.PreferenceFragment_android_dividerHeight, -1);
        boolean z = obtainStyledAttributes.getBoolean(I.PreferenceFragment_allowDividerAfterLastItem, true);
        obtainStyledAttributes.recycle();
        LayoutInflater cloneInContext = layoutInflater.cloneInContext(this.mStyledContext);
        View inflate = cloneInContext.inflate(this.mLayoutResId, viewGroup, false);
        View findViewById = inflate.findViewById(16908351);
        if (findViewById instanceof ViewGroup) {
            ViewGroup viewGroup2 = (ViewGroup) findViewById;
            RecyclerView onCreateRecyclerView = onCreateRecyclerView(cloneInContext, viewGroup2, bundle);
            if (onCreateRecyclerView != null) {
                this.mList = onCreateRecyclerView;
                onCreateRecyclerView.a((RecyclerView.f) this.mDividerDecoration);
                setDivider(drawable);
                if (dimensionPixelSize != -1) {
                    setDividerHeight(dimensionPixelSize);
                }
                this.mDividerDecoration.b(z);
                if (this.mList.getParent() == null) {
                    viewGroup2.addView(this.mList);
                }
                this.mHandler.post(this.mRequestFocus);
                return inflate;
            }
            throw new RuntimeException("Could not create RecyclerView");
        }
        throw new RuntimeException("Content has view with id attribute 'android.R.id.list_container' that is not a ViewGroup class");
    }

    public void onDestroyView() {
        this.mHandler.removeCallbacks(this.mRequestFocus);
        this.mHandler.removeMessages(1);
        if (this.mHavePrefs) {
            unbindPreferences();
        }
        this.mList = null;
        super.onDestroyView();
    }

    @Deprecated
    public abstract void onDisplayPreferenceDialog(Preference preference);

    @Deprecated
    public void onNavigateToScreen(PreferenceScreen preferenceScreen) {
        if (!(getCallbackFragment() instanceof d ? ((d) getCallbackFragment()).a(this, preferenceScreen) : false) && (getActivity() instanceof d)) {
            ((d) getActivity()).a(this, preferenceScreen);
        }
    }

    @Deprecated
    public boolean onPreferenceTreeClick(Preference preference) {
        boolean z = false;
        if (preference.getFragment() == null) {
            return false;
        }
        if (getCallbackFragment() instanceof c) {
            z = ((c) getCallbackFragment()).a(this, preference);
        }
        return (z || !(getActivity() instanceof c)) ? z : ((c) getActivity()).a(this, preference);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            Bundle bundle2 = new Bundle();
            preferenceScreen.saveHierarchyState(bundle2);
            bundle.putBundle(PREFERENCES_TAG, bundle2);
        }
    }

    public void onStart() {
        super.onStart();
        this.mPreferenceManager.a((z.c) this);
        this.mPreferenceManager.a((z.a) this);
    }

    public void onStop() {
        super.onStop();
        this.mPreferenceManager.a((z.c) null);
        this.mPreferenceManager.a((z.a) null);
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.a.LIBRARY})
    public void onUnbindPreferences() {
    }

    public void onViewCreated(View view, Bundle bundle) {
        Bundle bundle2;
        PreferenceScreen preferenceScreen;
        super.onViewCreated(view, bundle);
        if (!(bundle == null || (bundle2 = bundle.getBundle(PREFERENCES_TAG)) == null || (preferenceScreen = getPreferenceScreen()) == null)) {
            preferenceScreen.restoreHierarchyState(bundle2);
        }
        if (this.mHavePrefs) {
            bindPreferences();
            Runnable runnable = this.mSelectPreferenceRunnable;
            if (runnable != null) {
                runnable.run();
                this.mSelectPreferenceRunnable = null;
            }
        }
        this.mInitDone = true;
    }

    @Deprecated
    public void scrollToPreference(Preference preference) {
        scrollToPreferenceInternal(preference, (String) null);
    }

    @Deprecated
    public void scrollToPreference(String str) {
        scrollToPreferenceInternal((Preference) null, str);
    }

    @Deprecated
    public void setDivider(Drawable drawable) {
        this.mDividerDecoration.a(drawable);
    }

    @Deprecated
    public void setDividerHeight(int i) {
        this.mDividerDecoration.a(i);
    }

    @Deprecated
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        if (this.mPreferenceManager.a(preferenceScreen) && preferenceScreen != null) {
            onUnbindPreferences();
            this.mHavePrefs = true;
            if (this.mInitDone) {
                postBindPreferences();
            }
        }
    }

    @Deprecated
    public void setPreferencesFromResource(@XmlRes int i, @Nullable String str) {
        requirePreferenceManager();
        PreferenceScreen a2 = this.mPreferenceManager.a(this.mStyledContext, i, (PreferenceScreen) null);
        Object obj = a2;
        if (str != null) {
            Object a3 = a2.a((CharSequence) str);
            boolean z = a3 instanceof PreferenceScreen;
            obj = a3;
            if (!z) {
                throw new IllegalArgumentException("Preference object with key " + str + " is not a PreferenceScreen");
            }
        }
        setPreferenceScreen((PreferenceScreen) obj);
    }
}

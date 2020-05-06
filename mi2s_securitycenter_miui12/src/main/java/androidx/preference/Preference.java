package androidx.preference;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.AbsSavedState;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.res.h;
import androidx.preference.z;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Preference implements Comparable<Preference> {
    private static final String CLIPBOARD_ID = "Preference";
    public static final int DEFAULT_ORDER = Integer.MAX_VALUE;
    private boolean mAllowDividerAbove;
    private boolean mAllowDividerBelow;
    private boolean mBaseMethodCalled;
    private final View.OnClickListener mClickListener;
    private Context mContext;
    private boolean mCopyingEnabled;
    private Object mDefaultValue;
    private String mDependencyKey;
    private boolean mDependencyMet;
    private List<Preference> mDependents;
    private boolean mEnabled;
    private Bundle mExtras;
    private String mFragment;
    private boolean mHasId;
    private boolean mHasSingleLineTitleAttr;
    private Drawable mIcon;
    private int mIconResId;
    private boolean mIconSpaceReserved;
    private long mId;
    private Intent mIntent;
    private String mKey;
    private int mLayoutResId;
    private a mListener;
    private b mOnChangeListener;
    private c mOnClickListener;
    private d mOnCopyListener;
    private int mOrder;
    private boolean mParentDependencyMet;
    private PreferenceGroup mParentGroup;
    private boolean mPersistent;
    @Nullable
    private C0159m mPreferenceDataStore;
    @Nullable
    private z mPreferenceManager;
    private boolean mRequiresKey;
    private boolean mSelectable;
    private boolean mShouldDisableView;
    private boolean mSingleLineTitle;
    private CharSequence mSummary;
    private e mSummaryProvider;
    private CharSequence mTitle;
    private int mViewId;
    private boolean mVisible;
    private boolean mWasDetached;
    private int mWidgetLayoutResId;

    public static class BaseSavedState extends AbsSavedState {
        public static final Parcelable.Creator<BaseSavedState> CREATOR = new C0158l();

        public BaseSavedState(Parcel parcel) {
            super(parcel);
        }

        public BaseSavedState(Parcelable parcelable) {
            super(parcelable);
        }
    }

    interface a {
        void a(Preference preference);

        void c(Preference preference);

        void d(Preference preference);
    }

    public interface b {
        boolean onPreferenceChange(Preference preference, Object obj);
    }

    public interface c {
        boolean onPreferenceClick(Preference preference);
    }

    private static class d implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        /* renamed from: a  reason: collision with root package name */
        private final Preference f1018a;

        d(Preference preference) {
            this.f1018a = preference;
        }

        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            CharSequence summary = this.f1018a.getSummary();
            if (this.f1018a.isCopyingEnabled() && !TextUtils.isEmpty(summary)) {
                contextMenu.setHeaderTitle(summary);
                contextMenu.add(0, 0, 0, G.copy).setOnMenuItemClickListener(this);
            }
        }

        public boolean onMenuItemClick(MenuItem menuItem) {
            CharSequence summary = this.f1018a.getSummary();
            ((ClipboardManager) this.f1018a.getContext().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(Preference.CLIPBOARD_ID, summary));
            Toast.makeText(this.f1018a.getContext(), this.f1018a.getContext().getString(G.preference_copied, new Object[]{summary}), 0).show();
            return true;
        }
    }

    public interface e<T extends Preference> {
        CharSequence a(T t);
    }

    public Preference(Context context) {
        this(context, (AttributeSet) null);
    }

    public Preference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, h.a(context, B.preferenceStyle, 16842894));
    }

    public Preference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:0x00f3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Preference(android.content.Context r5, android.util.AttributeSet r6, int r7, int r8) {
        /*
            r4 = this;
            r4.<init>()
            r0 = 2147483647(0x7fffffff, float:NaN)
            r4.mOrder = r0
            r1 = 0
            r4.mViewId = r1
            r2 = 1
            r4.mEnabled = r2
            r4.mSelectable = r2
            r4.mPersistent = r2
            r4.mDependencyMet = r2
            r4.mParentDependencyMet = r2
            r4.mVisible = r2
            r4.mAllowDividerAbove = r2
            r4.mAllowDividerBelow = r2
            r4.mSingleLineTitle = r2
            r4.mShouldDisableView = r2
            int r3 = androidx.preference.F.preference
            r4.mLayoutResId = r3
            androidx.preference.k r3 = new androidx.preference.k
            r3.<init>(r4)
            r4.mClickListener = r3
            r4.mContext = r5
            int[] r3 = androidx.preference.I.Preference
            android.content.res.TypedArray r5 = r5.obtainStyledAttributes(r6, r3, r7, r8)
            int r6 = androidx.preference.I.Preference_icon
            int r7 = androidx.preference.I.Preference_android_icon
            int r6 = androidx.core.content.res.h.b((android.content.res.TypedArray) r5, (int) r6, (int) r7, (int) r1)
            r4.mIconResId = r6
            int r6 = androidx.preference.I.Preference_key
            int r7 = androidx.preference.I.Preference_android_key
            java.lang.String r6 = androidx.core.content.res.h.b(r5, r6, r7)
            r4.mKey = r6
            int r6 = androidx.preference.I.Preference_title
            int r7 = androidx.preference.I.Preference_android_title
            java.lang.CharSequence r6 = androidx.core.content.res.h.c(r5, r6, r7)
            r4.mTitle = r6
            int r6 = androidx.preference.I.Preference_summary
            int r7 = androidx.preference.I.Preference_android_summary
            java.lang.CharSequence r6 = androidx.core.content.res.h.c(r5, r6, r7)
            r4.mSummary = r6
            int r6 = androidx.preference.I.Preference_order
            int r7 = androidx.preference.I.Preference_android_order
            int r6 = androidx.core.content.res.h.a((android.content.res.TypedArray) r5, (int) r6, (int) r7, (int) r0)
            r4.mOrder = r6
            int r6 = androidx.preference.I.Preference_fragment
            int r7 = androidx.preference.I.Preference_android_fragment
            java.lang.String r6 = androidx.core.content.res.h.b(r5, r6, r7)
            r4.mFragment = r6
            int r6 = androidx.preference.I.Preference_layout
            int r7 = androidx.preference.I.Preference_android_layout
            int r8 = androidx.preference.F.preference
            int r6 = androidx.core.content.res.h.b((android.content.res.TypedArray) r5, (int) r6, (int) r7, (int) r8)
            r4.mLayoutResId = r6
            int r6 = androidx.preference.I.Preference_widgetLayout
            int r7 = androidx.preference.I.Preference_android_widgetLayout
            int r6 = androidx.core.content.res.h.b((android.content.res.TypedArray) r5, (int) r6, (int) r7, (int) r1)
            r4.mWidgetLayoutResId = r6
            int r6 = androidx.preference.I.Preference_enabled
            int r7 = androidx.preference.I.Preference_android_enabled
            boolean r6 = androidx.core.content.res.h.a((android.content.res.TypedArray) r5, (int) r6, (int) r7, (boolean) r2)
            r4.mEnabled = r6
            int r6 = androidx.preference.I.Preference_selectable
            int r7 = androidx.preference.I.Preference_android_selectable
            boolean r6 = androidx.core.content.res.h.a((android.content.res.TypedArray) r5, (int) r6, (int) r7, (boolean) r2)
            r4.mSelectable = r6
            int r6 = androidx.preference.I.Preference_persistent
            int r7 = androidx.preference.I.Preference_android_persistent
            boolean r6 = androidx.core.content.res.h.a((android.content.res.TypedArray) r5, (int) r6, (int) r7, (boolean) r2)
            r4.mPersistent = r6
            int r6 = androidx.preference.I.Preference_dependency
            int r7 = androidx.preference.I.Preference_android_dependency
            java.lang.String r6 = androidx.core.content.res.h.b(r5, r6, r7)
            r4.mDependencyKey = r6
            int r6 = androidx.preference.I.Preference_allowDividerAbove
            boolean r7 = r4.mSelectable
            boolean r6 = androidx.core.content.res.h.a((android.content.res.TypedArray) r5, (int) r6, (int) r6, (boolean) r7)
            r4.mAllowDividerAbove = r6
            int r6 = androidx.preference.I.Preference_allowDividerBelow
            boolean r7 = r4.mSelectable
            boolean r6 = androidx.core.content.res.h.a((android.content.res.TypedArray) r5, (int) r6, (int) r6, (boolean) r7)
            r4.mAllowDividerBelow = r6
            int r6 = androidx.preference.I.Preference_defaultValue
            boolean r6 = r5.hasValue(r6)
            if (r6 == 0) goto L_0x00d2
            int r6 = androidx.preference.I.Preference_defaultValue
        L_0x00cb:
            java.lang.Object r6 = r4.onGetDefaultValue(r5, r6)
            r4.mDefaultValue = r6
            goto L_0x00dd
        L_0x00d2:
            int r6 = androidx.preference.I.Preference_android_defaultValue
            boolean r6 = r5.hasValue(r6)
            if (r6 == 0) goto L_0x00dd
            int r6 = androidx.preference.I.Preference_android_defaultValue
            goto L_0x00cb
        L_0x00dd:
            int r6 = androidx.preference.I.Preference_shouldDisableView
            int r7 = androidx.preference.I.Preference_android_shouldDisableView
            boolean r6 = androidx.core.content.res.h.a((android.content.res.TypedArray) r5, (int) r6, (int) r7, (boolean) r2)
            r4.mShouldDisableView = r6
            int r6 = androidx.preference.I.Preference_singleLineTitle
            boolean r6 = r5.hasValue(r6)
            r4.mHasSingleLineTitleAttr = r6
            boolean r6 = r4.mHasSingleLineTitleAttr
            if (r6 == 0) goto L_0x00fd
            int r6 = androidx.preference.I.Preference_singleLineTitle
            int r7 = androidx.preference.I.Preference_android_singleLineTitle
            boolean r6 = androidx.core.content.res.h.a((android.content.res.TypedArray) r5, (int) r6, (int) r7, (boolean) r2)
            r4.mSingleLineTitle = r6
        L_0x00fd:
            int r6 = androidx.preference.I.Preference_iconSpaceReserved
            int r7 = androidx.preference.I.Preference_android_iconSpaceReserved
            boolean r6 = androidx.core.content.res.h.a((android.content.res.TypedArray) r5, (int) r6, (int) r7, (boolean) r1)
            r4.mIconSpaceReserved = r6
            int r6 = androidx.preference.I.Preference_isPreferenceVisible
            boolean r6 = androidx.core.content.res.h.a((android.content.res.TypedArray) r5, (int) r6, (int) r6, (boolean) r2)
            r4.mVisible = r6
            int r6 = androidx.preference.I.Preference_enableCopying
            boolean r6 = androidx.core.content.res.h.a((android.content.res.TypedArray) r5, (int) r6, (int) r6, (boolean) r1)
            r4.mCopyingEnabled = r6
            r5.recycle()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.preference.Preference.<init>(android.content.Context, android.util.AttributeSet, int, int):void");
    }

    private void dispatchSetInitialValue() {
        Object obj;
        boolean z = true;
        if (getPreferenceDataStore() != null) {
            onSetInitialValue(true, this.mDefaultValue);
            return;
        }
        if (!shouldPersist() || !getSharedPreferences().contains(this.mKey)) {
            obj = this.mDefaultValue;
            if (obj != null) {
                z = false;
            } else {
                return;
            }
        } else {
            obj = null;
        }
        onSetInitialValue(z, obj);
    }

    private void registerDependency() {
        if (!TextUtils.isEmpty(this.mDependencyKey)) {
            Preference findPreferenceInHierarchy = findPreferenceInHierarchy(this.mDependencyKey);
            if (findPreferenceInHierarchy != null) {
                findPreferenceInHierarchy.registerDependent(this);
                return;
            }
            throw new IllegalStateException("Dependency \"" + this.mDependencyKey + "\" not found for preference \"" + this.mKey + "\" (title: \"" + this.mTitle + "\"");
        }
    }

    private void registerDependent(Preference preference) {
        if (this.mDependents == null) {
            this.mDependents = new ArrayList();
        }
        this.mDependents.add(preference);
        preference.onDependencyChanged(this, shouldDisableDependents());
    }

    private void setEnabledStateOnViews(View view, boolean z) {
        view.setEnabled(z);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                setEnabledStateOnViews(viewGroup.getChildAt(childCount), z);
            }
        }
    }

    private void tryCommit(@NonNull SharedPreferences.Editor editor) {
        if (this.mPreferenceManager.j()) {
            editor.apply();
        }
    }

    private void unregisterDependency() {
        Preference findPreferenceInHierarchy;
        String str = this.mDependencyKey;
        if (str != null && (findPreferenceInHierarchy = findPreferenceInHierarchy(str)) != null) {
            findPreferenceInHierarchy.unregisterDependent(this);
        }
    }

    private void unregisterDependent(Preference preference) {
        List<Preference> list = this.mDependents;
        if (list != null) {
            list.remove(preference);
        }
    }

    /* access modifiers changed from: package-private */
    public void assignParent(@Nullable PreferenceGroup preferenceGroup) {
        if (preferenceGroup == null || this.mParentGroup == null) {
            this.mParentGroup = preferenceGroup;
            return;
        }
        throw new IllegalStateException("This preference already has a parent. You must remove the existing parent before assigning a new one.");
    }

    public boolean callChangeListener(Object obj) {
        b bVar = this.mOnChangeListener;
        return bVar == null || bVar.onPreferenceChange(this, obj);
    }

    /* access modifiers changed from: package-private */
    public final void clearWasDetached() {
        this.mWasDetached = false;
    }

    public int compareTo(@NonNull Preference preference) {
        int i = this.mOrder;
        int i2 = preference.mOrder;
        if (i != i2) {
            return i - i2;
        }
        CharSequence charSequence = this.mTitle;
        CharSequence charSequence2 = preference.mTitle;
        if (charSequence == charSequence2) {
            return 0;
        }
        if (charSequence == null) {
            return 1;
        }
        if (charSequence2 == null) {
            return -1;
        }
        return charSequence.toString().compareToIgnoreCase(preference.mTitle.toString());
    }

    /* access modifiers changed from: package-private */
    public void dispatchRestoreInstanceState(Bundle bundle) {
        Parcelable parcelable;
        if (hasKey() && (parcelable = bundle.getParcelable(this.mKey)) != null) {
            this.mBaseMethodCalled = false;
            onRestoreInstanceState(parcelable);
            if (!this.mBaseMethodCalled) {
                throw new IllegalStateException("Derived class did not call super.onRestoreInstanceState()");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchSaveInstanceState(Bundle bundle) {
        if (hasKey()) {
            this.mBaseMethodCalled = false;
            Parcelable onSaveInstanceState = onSaveInstanceState();
            if (!this.mBaseMethodCalled) {
                throw new IllegalStateException("Derived class did not call super.onSaveInstanceState()");
            } else if (onSaveInstanceState != null) {
                bundle.putParcelable(this.mKey, onSaveInstanceState);
            }
        }
    }

    /* access modifiers changed from: protected */
    @Nullable
    public <T extends Preference> T findPreferenceInHierarchy(@NonNull String str) {
        z zVar = this.mPreferenceManager;
        if (zVar == null) {
            return null;
        }
        return zVar.a((CharSequence) str);
    }

    public Context getContext() {
        return this.mContext;
    }

    public String getDependency() {
        return this.mDependencyKey;
    }

    public Bundle getExtras() {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        return this.mExtras;
    }

    /* access modifiers changed from: package-private */
    public StringBuilder getFilterableStringBuilder() {
        StringBuilder sb = new StringBuilder();
        CharSequence title = getTitle();
        if (!TextUtils.isEmpty(title)) {
            sb.append(title);
            sb.append(' ');
        }
        CharSequence summary = getSummary();
        if (!TextUtils.isEmpty(summary)) {
            sb.append(summary);
            sb.append(' ');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb;
    }

    public String getFragment() {
        return this.mFragment;
    }

    public Drawable getIcon() {
        int i;
        if (this.mIcon == null && (i = this.mIconResId) != 0) {
            this.mIcon = a.a.a.a.a.b(this.mContext, i);
        }
        return this.mIcon;
    }

    /* access modifiers changed from: package-private */
    public long getId() {
        return this.mId;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public String getKey() {
        return this.mKey;
    }

    public final int getLayoutResource() {
        return this.mLayoutResId;
    }

    public b getOnPreferenceChangeListener() {
        return this.mOnChangeListener;
    }

    public c getOnPreferenceClickListener() {
        return this.mOnClickListener;
    }

    public int getOrder() {
        return this.mOrder;
    }

    @Nullable
    public PreferenceGroup getParent() {
        return this.mParentGroup;
    }

    /* access modifiers changed from: protected */
    public boolean getPersistedBoolean(boolean z) {
        if (!shouldPersist()) {
            return z;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        return preferenceDataStore != null ? preferenceDataStore.a(this.mKey, z) : this.mPreferenceManager.i().getBoolean(this.mKey, z);
    }

    /* access modifiers changed from: protected */
    public float getPersistedFloat(float f) {
        if (!shouldPersist()) {
            return f;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        return preferenceDataStore != null ? preferenceDataStore.a(this.mKey, f) : this.mPreferenceManager.i().getFloat(this.mKey, f);
    }

    /* access modifiers changed from: protected */
    public int getPersistedInt(int i) {
        if (!shouldPersist()) {
            return i;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        return preferenceDataStore != null ? preferenceDataStore.a(this.mKey, i) : this.mPreferenceManager.i().getInt(this.mKey, i);
    }

    /* access modifiers changed from: protected */
    public long getPersistedLong(long j) {
        if (!shouldPersist()) {
            return j;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        return preferenceDataStore != null ? preferenceDataStore.a(this.mKey, j) : this.mPreferenceManager.i().getLong(this.mKey, j);
    }

    /* access modifiers changed from: protected */
    public String getPersistedString(String str) {
        if (!shouldPersist()) {
            return str;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        return preferenceDataStore != null ? preferenceDataStore.a(this.mKey, str) : this.mPreferenceManager.i().getString(this.mKey, str);
    }

    public Set<String> getPersistedStringSet(Set<String> set) {
        if (!shouldPersist()) {
            return set;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        return preferenceDataStore != null ? preferenceDataStore.a(this.mKey, set) : this.mPreferenceManager.i().getStringSet(this.mKey, set);
    }

    @Nullable
    public C0159m getPreferenceDataStore() {
        C0159m mVar = this.mPreferenceDataStore;
        if (mVar != null) {
            return mVar;
        }
        z zVar = this.mPreferenceManager;
        if (zVar != null) {
            return zVar.g();
        }
        return null;
    }

    public z getPreferenceManager() {
        return this.mPreferenceManager;
    }

    public SharedPreferences getSharedPreferences() {
        if (this.mPreferenceManager == null || getPreferenceDataStore() != null) {
            return null;
        }
        return this.mPreferenceManager.i();
    }

    public boolean getShouldDisableView() {
        return this.mShouldDisableView;
    }

    public CharSequence getSummary() {
        return getSummaryProvider() != null ? getSummaryProvider().a(this) : this.mSummary;
    }

    @Nullable
    public final e getSummaryProvider() {
        return this.mSummaryProvider;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public final int getWidgetLayoutResource() {
        return this.mWidgetLayoutResId;
    }

    public boolean hasKey() {
        return !TextUtils.isEmpty(this.mKey);
    }

    public boolean isCopyingEnabled() {
        return this.mCopyingEnabled;
    }

    public boolean isEnabled() {
        return this.mEnabled && this.mDependencyMet && this.mParentDependencyMet;
    }

    public boolean isIconSpaceReserved() {
        return this.mIconSpaceReserved;
    }

    public boolean isPersistent() {
        return this.mPersistent;
    }

    public boolean isSelectable() {
        return this.mSelectable;
    }

    public final boolean isShown() {
        if (!isVisible() || getPreferenceManager() == null) {
            return false;
        }
        if (this == getPreferenceManager().h()) {
            return true;
        }
        PreferenceGroup parent = getParent();
        if (parent == null) {
            return false;
        }
        return parent.isShown();
    }

    public boolean isSingleLineTitle() {
        return this.mSingleLineTitle;
    }

    public final boolean isVisible() {
        return this.mVisible;
    }

    /* access modifiers changed from: protected */
    public void notifyChanged() {
        a aVar = this.mListener;
        if (aVar != null) {
            aVar.c(this);
        }
    }

    public void notifyDependencyChange(boolean z) {
        List<Preference> list = this.mDependents;
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                list.get(i).onDependencyChanged(this, z);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void notifyHierarchyChanged() {
        a aVar = this.mListener;
        if (aVar != null) {
            aVar.d(this);
        }
    }

    public void onAttached() {
        registerDependency();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToHierarchy(z zVar) {
        this.mPreferenceManager = zVar;
        if (!this.mHasId) {
            this.mId = zVar.c();
        }
        dispatchSetInitialValue();
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void onAttachedToHierarchy(z zVar, long j) {
        this.mId = j;
        this.mHasId = true;
        try {
            onAttachedToHierarchy(zVar);
        } finally {
            this.mHasId = false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0081  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00da  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0043  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(androidx.preference.A r9) {
        /*
            r8 = this;
            android.view.View r0 = r9.itemView
            android.view.View$OnClickListener r1 = r8.mClickListener
            r0.setOnClickListener(r1)
            int r1 = r8.mViewId
            r0.setId(r1)
            r1 = 16908304(0x1020010, float:2.3877274E-38)
            android.view.View r1 = r9.b((int) r1)
            android.widget.TextView r1 = (android.widget.TextView) r1
            r2 = 0
            r3 = 0
            r4 = 8
            if (r1 == 0) goto L_0x0037
            java.lang.CharSequence r5 = r8.getSummary()
            boolean r6 = android.text.TextUtils.isEmpty(r5)
            if (r6 != 0) goto L_0x0034
            r1.setText(r5)
            r1.setVisibility(r3)
            int r1 = r1.getCurrentTextColor()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            goto L_0x0038
        L_0x0034:
            r1.setVisibility(r4)
        L_0x0037:
            r1 = r2
        L_0x0038:
            r5 = 16908310(0x1020016, float:2.387729E-38)
            android.view.View r5 = r9.b((int) r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            if (r5 == 0) goto L_0x0075
            java.lang.CharSequence r6 = r8.getTitle()
            boolean r7 = android.text.TextUtils.isEmpty(r6)
            if (r7 != 0) goto L_0x0072
            r5.setText(r6)
            r5.setVisibility(r3)
            boolean r6 = r8.mHasSingleLineTitleAttr
            if (r6 == 0) goto L_0x005c
            boolean r6 = r8.mSingleLineTitle
            r5.setSingleLine(r6)
        L_0x005c:
            boolean r6 = r8.isSelectable()
            if (r6 != 0) goto L_0x0075
            boolean r6 = r8.isEnabled()
            if (r6 == 0) goto L_0x0075
            if (r1 == 0) goto L_0x0075
            int r1 = r1.intValue()
            r5.setTextColor(r1)
            goto L_0x0075
        L_0x0072:
            r5.setVisibility(r4)
        L_0x0075:
            r1 = 16908294(0x1020006, float:2.3877246E-38)
            android.view.View r1 = r9.b((int) r1)
            android.widget.ImageView r1 = (android.widget.ImageView) r1
            r5 = 4
            if (r1 == 0) goto L_0x00b0
            int r6 = r8.mIconResId
            if (r6 != 0) goto L_0x0089
            android.graphics.drawable.Drawable r6 = r8.mIcon
            if (r6 == 0) goto L_0x009e
        L_0x0089:
            android.graphics.drawable.Drawable r6 = r8.mIcon
            if (r6 != 0) goto L_0x0097
            android.content.Context r6 = r8.mContext
            int r7 = r8.mIconResId
            android.graphics.drawable.Drawable r6 = a.a.a.a.a.b(r6, r7)
            r8.mIcon = r6
        L_0x0097:
            android.graphics.drawable.Drawable r6 = r8.mIcon
            if (r6 == 0) goto L_0x009e
            r1.setImageDrawable(r6)
        L_0x009e:
            android.graphics.drawable.Drawable r6 = r8.mIcon
            if (r6 == 0) goto L_0x00a6
            r1.setVisibility(r3)
            goto L_0x00b0
        L_0x00a6:
            boolean r6 = r8.mIconSpaceReserved
            if (r6 == 0) goto L_0x00ac
            r6 = r5
            goto L_0x00ad
        L_0x00ac:
            r6 = r4
        L_0x00ad:
            r1.setVisibility(r6)
        L_0x00b0:
            int r1 = androidx.preference.E.icon_frame
            android.view.View r1 = r9.b((int) r1)
            if (r1 != 0) goto L_0x00bf
            r1 = 16908350(0x102003e, float:2.3877403E-38)
            android.view.View r1 = r9.b((int) r1)
        L_0x00bf:
            if (r1 == 0) goto L_0x00d1
            android.graphics.drawable.Drawable r6 = r8.mIcon
            if (r6 == 0) goto L_0x00c9
            r1.setVisibility(r3)
            goto L_0x00d1
        L_0x00c9:
            boolean r3 = r8.mIconSpaceReserved
            if (r3 == 0) goto L_0x00ce
            r4 = r5
        L_0x00ce:
            r1.setVisibility(r4)
        L_0x00d1:
            boolean r1 = r8.mShouldDisableView
            if (r1 == 0) goto L_0x00da
            boolean r1 = r8.isEnabled()
            goto L_0x00db
        L_0x00da:
            r1 = 1
        L_0x00db:
            r8.setEnabledStateOnViews(r0, r1)
            boolean r1 = r8.isSelectable()
            r0.setFocusable(r1)
            r0.setClickable(r1)
            boolean r3 = r8.mAllowDividerAbove
            r9.a(r3)
            boolean r3 = r8.mAllowDividerBelow
            r9.b((boolean) r3)
            boolean r9 = r8.isCopyingEnabled()
            if (r9 == 0) goto L_0x0103
            androidx.preference.Preference$d r3 = r8.mOnCopyListener
            if (r3 != 0) goto L_0x0103
            androidx.preference.Preference$d r3 = new androidx.preference.Preference$d
            r3.<init>(r8)
            r8.mOnCopyListener = r3
        L_0x0103:
            if (r9 == 0) goto L_0x0108
            androidx.preference.Preference$d r3 = r8.mOnCopyListener
            goto L_0x0109
        L_0x0108:
            r3 = r2
        L_0x0109:
            r0.setOnCreateContextMenuListener(r3)
            r0.setLongClickable(r9)
            if (r9 == 0) goto L_0x0116
            if (r1 != 0) goto L_0x0116
            androidx.core.view.ViewCompat.a((android.view.View) r0, (android.graphics.drawable.Drawable) r2)
        L_0x0116:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.preference.Preference.onBindViewHolder(androidx.preference.A):void");
    }

    /* access modifiers changed from: protected */
    public void onClick() {
    }

    public void onDependencyChanged(Preference preference, boolean z) {
        if (this.mDependencyMet == z) {
            this.mDependencyMet = !z;
            notifyDependencyChange(shouldDisableDependents());
            notifyChanged();
        }
    }

    public void onDetached() {
        unregisterDependency();
        this.mWasDetached = true;
    }

    /* access modifiers changed from: protected */
    public Object onGetDefaultValue(TypedArray typedArray, int i) {
        return null;
    }

    @CallSuper
    @Deprecated
    public void onInitializeAccessibilityNodeInfo(androidx.core.view.a.c cVar) {
    }

    public void onParentChanged(Preference preference, boolean z) {
        if (this.mParentDependencyMet == z) {
            this.mParentDependencyMet = !z;
            notifyDependencyChange(shouldDisableDependents());
            notifyChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void onPrepareForRemoval() {
        unregisterDependency();
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        this.mBaseMethodCalled = true;
        if (parcelable != AbsSavedState.EMPTY_STATE && parcelable != null) {
            throw new IllegalArgumentException("Wrong state class -- expecting Preference State");
        }
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        this.mBaseMethodCalled = true;
        return AbsSavedState.EMPTY_STATE;
    }

    /* access modifiers changed from: protected */
    public void onSetInitialValue(@Nullable Object obj) {
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public void onSetInitialValue(boolean z, Object obj) {
        onSetInitialValue(obj);
    }

    public Bundle peekExtras() {
        return this.mExtras;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void performClick() {
        z.c e2;
        if (isEnabled() && isSelectable()) {
            onClick();
            c cVar = this.mOnClickListener;
            if (cVar == null || !cVar.onPreferenceClick(this)) {
                z preferenceManager = getPreferenceManager();
                if ((preferenceManager == null || (e2 = preferenceManager.e()) == null || !e2.onPreferenceTreeClick(this)) && this.mIntent != null) {
                    getContext().startActivity(this.mIntent);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void performClick(View view) {
        performClick();
    }

    /* access modifiers changed from: protected */
    public boolean persistBoolean(boolean z) {
        if (!shouldPersist()) {
            return false;
        }
        if (z == getPersistedBoolean(!z)) {
            return true;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        if (preferenceDataStore != null) {
            preferenceDataStore.b(this.mKey, z);
        } else {
            SharedPreferences.Editor b2 = this.mPreferenceManager.b();
            b2.putBoolean(this.mKey, z);
            tryCommit(b2);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean persistFloat(float f) {
        if (!shouldPersist()) {
            return false;
        }
        if (f == getPersistedFloat(Float.NaN)) {
            return true;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        if (preferenceDataStore != null) {
            preferenceDataStore.b(this.mKey, f);
        } else {
            SharedPreferences.Editor b2 = this.mPreferenceManager.b();
            b2.putFloat(this.mKey, f);
            tryCommit(b2);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean persistInt(int i) {
        if (!shouldPersist()) {
            return false;
        }
        if (i == getPersistedInt(~i)) {
            return true;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        if (preferenceDataStore != null) {
            preferenceDataStore.b(this.mKey, i);
        } else {
            SharedPreferences.Editor b2 = this.mPreferenceManager.b();
            b2.putInt(this.mKey, i);
            tryCommit(b2);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean persistLong(long j) {
        if (!shouldPersist()) {
            return false;
        }
        if (j == getPersistedLong(~j)) {
            return true;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        if (preferenceDataStore != null) {
            preferenceDataStore.b(this.mKey, j);
        } else {
            SharedPreferences.Editor b2 = this.mPreferenceManager.b();
            b2.putLong(this.mKey, j);
            tryCommit(b2);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean persistString(String str) {
        if (!shouldPersist()) {
            return false;
        }
        if (TextUtils.equals(str, getPersistedString((String) null))) {
            return true;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        if (preferenceDataStore != null) {
            preferenceDataStore.b(this.mKey, str);
        } else {
            SharedPreferences.Editor b2 = this.mPreferenceManager.b();
            b2.putString(this.mKey, str);
            tryCommit(b2);
        }
        return true;
    }

    public boolean persistStringSet(Set<String> set) {
        if (!shouldPersist()) {
            return false;
        }
        if (set.equals(getPersistedStringSet((Set<String>) null))) {
            return true;
        }
        C0159m preferenceDataStore = getPreferenceDataStore();
        if (preferenceDataStore != null) {
            preferenceDataStore.b(this.mKey, set);
        } else {
            SharedPreferences.Editor b2 = this.mPreferenceManager.b();
            b2.putStringSet(this.mKey, set);
            tryCommit(b2);
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void requireKey() {
        if (!TextUtils.isEmpty(this.mKey)) {
            this.mRequiresKey = true;
            return;
        }
        throw new IllegalStateException("Preference does not have a key assigned.");
    }

    public void restoreHierarchyState(Bundle bundle) {
        dispatchRestoreInstanceState(bundle);
    }

    public void saveHierarchyState(Bundle bundle) {
        dispatchSaveInstanceState(bundle);
    }

    public void setCopyingEnabled(boolean z) {
        if (this.mCopyingEnabled != z) {
            this.mCopyingEnabled = z;
            notifyChanged();
        }
    }

    public void setDefaultValue(Object obj) {
        this.mDefaultValue = obj;
    }

    public void setDependency(String str) {
        unregisterDependency();
        this.mDependencyKey = str;
        registerDependency();
    }

    public void setEnabled(boolean z) {
        if (this.mEnabled != z) {
            this.mEnabled = z;
            notifyDependencyChange(shouldDisableDependents());
            notifyChanged();
        }
    }

    public void setFragment(String str) {
        this.mFragment = str;
    }

    public void setIcon(int i) {
        setIcon(a.a.a.a.a.b(this.mContext, i));
        this.mIconResId = i;
    }

    public void setIcon(Drawable drawable) {
        if (this.mIcon != drawable) {
            this.mIcon = drawable;
            this.mIconResId = 0;
            notifyChanged();
        }
    }

    public void setIconSpaceReserved(boolean z) {
        if (this.mIconSpaceReserved != z) {
            this.mIconSpaceReserved = z;
            notifyChanged();
        }
    }

    public void setIntent(Intent intent) {
        this.mIntent = intent;
    }

    public void setKey(String str) {
        this.mKey = str;
        if (this.mRequiresKey && !hasKey()) {
            requireKey();
        }
    }

    public void setLayoutResource(int i) {
        this.mLayoutResId = i;
    }

    /* access modifiers changed from: package-private */
    public final void setOnPreferenceChangeInternalListener(a aVar) {
        this.mListener = aVar;
    }

    public void setOnPreferenceChangeListener(b bVar) {
        this.mOnChangeListener = bVar;
    }

    public void setOnPreferenceClickListener(c cVar) {
        this.mOnClickListener = cVar;
    }

    public void setOrder(int i) {
        if (i != this.mOrder) {
            this.mOrder = i;
            notifyHierarchyChanged();
        }
    }

    public void setPersistent(boolean z) {
        this.mPersistent = z;
    }

    public void setPreferenceDataStore(C0159m mVar) {
        this.mPreferenceDataStore = mVar;
    }

    public void setSelectable(boolean z) {
        if (this.mSelectable != z) {
            this.mSelectable = z;
            notifyChanged();
        }
    }

    public void setShouldDisableView(boolean z) {
        if (this.mShouldDisableView != z) {
            this.mShouldDisableView = z;
            notifyChanged();
        }
    }

    public void setSingleLineTitle(boolean z) {
        this.mHasSingleLineTitleAttr = true;
        this.mSingleLineTitle = z;
    }

    public void setSummary(int i) {
        setSummary((CharSequence) this.mContext.getString(i));
    }

    public void setSummary(CharSequence charSequence) {
        if (getSummaryProvider() != null) {
            throw new IllegalStateException("Preference already has a SummaryProvider set.");
        } else if (!TextUtils.equals(this.mSummary, charSequence)) {
            this.mSummary = charSequence;
            notifyChanged();
        }
    }

    public final void setSummaryProvider(@Nullable e eVar) {
        this.mSummaryProvider = eVar;
        notifyChanged();
    }

    public void setTitle(int i) {
        setTitle((CharSequence) this.mContext.getString(i));
    }

    public void setTitle(CharSequence charSequence) {
        if ((charSequence == null && this.mTitle != null) || (charSequence != null && !charSequence.equals(this.mTitle))) {
            this.mTitle = charSequence;
            notifyChanged();
        }
    }

    public void setViewId(int i) {
        this.mViewId = i;
    }

    public final void setVisible(boolean z) {
        if (this.mVisible != z) {
            this.mVisible = z;
            a aVar = this.mListener;
            if (aVar != null) {
                aVar.a(this);
            }
        }
    }

    public void setWidgetLayoutResource(int i) {
        this.mWidgetLayoutResId = i;
    }

    public boolean shouldDisableDependents() {
        return !isEnabled();
    }

    /* access modifiers changed from: protected */
    public boolean shouldPersist() {
        return this.mPreferenceManager != null && isPersistent() && hasKey();
    }

    public String toString() {
        return getFilterableStringBuilder().toString();
    }

    /* access modifiers changed from: package-private */
    public final boolean wasDetached() {
        return this.mWasDetached;
    }
}

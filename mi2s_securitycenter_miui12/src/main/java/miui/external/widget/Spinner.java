package miui.external.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;
import android.widget.ThemedSpinnerAdapter;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import d.b.a;
import d.b.d;
import java.lang.reflect.Field;
import miui.app.AlertDialog;
import miui.external.adapter.SpinnerCheckableArrayAdapter;
import miui.external.adapter.SpinnerDoubleLineContentAdapter;
import miui.external.graphics.TaggingDrawableUtil;
import miui.widget.ImmersionListPopupWindow;

public class Spinner extends android.widget.Spinner {
    private static Field FORWARDING_LISTENER = null;
    private static final int MAX_ITEMS_MEASURED = 15;
    private static final int MODE_DIALOG = 0;
    private static final int MODE_DROPDOWN = 1;
    private static final int MODE_THEME = -1;
    private static final String TAG = "Spinner";
    int mDropDownMinWidth;
    int mDropDownWidth;
    private OnSpinnerDismissListener mOnSpinnerDismissListener;
    /* access modifiers changed from: private */
    public SpinnerPopup mPopup;
    private final Context mPopupContext;
    private final boolean mPopupSet;
    private SpinnerAdapter mTempAdapter;
    final Rect mTempRect;
    /* access modifiers changed from: private */
    public float mTouchX;
    /* access modifiers changed from: private */
    public float mTouchY;

    private class DialogPopup implements SpinnerPopup, DialogInterface.OnClickListener {
        private ListAdapter mListAdapter;
        AlertDialog mPopup;
        private CharSequence mPrompt;

        private DialogPopup() {
        }

        public void dismiss() {
            AlertDialog alertDialog = this.mPopup;
            if (alertDialog != null) {
                alertDialog.dismiss();
                this.mPopup = null;
            }
        }

        public Drawable getBackground() {
            return null;
        }

        public CharSequence getHintText() {
            return this.mPrompt;
        }

        public int getHorizontalOffset() {
            return 0;
        }

        public int getHorizontalOriginalOffset() {
            return 0;
        }

        public int getVerticalOffset() {
            return 0;
        }

        public boolean isShowing() {
            AlertDialog alertDialog = this.mPopup;
            return alertDialog != null && alertDialog.isShowing();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Spinner.this.setSelection(i);
            if (Spinner.this.getOnItemClickListener() != null) {
                Spinner.this.performItemClick((View) null, i, this.mListAdapter.getItemId(i));
            }
            dismiss();
        }

        public void setAdapter(ListAdapter listAdapter) {
            this.mListAdapter = listAdapter;
        }

        public void setBackgroundDrawable(Drawable drawable) {
            Log.e(Spinner.TAG, "Cannot set popup background for MODE_DIALOG, ignoring");
        }

        public void setHorizontalOffset(int i) {
            Log.e(Spinner.TAG, "Cannot set horizontal offset for MODE_DIALOG, ignoring");
        }

        public void setHorizontalOriginalOffset(int i) {
            Log.e(Spinner.TAG, "Cannot set horizontal (original) offset for MODE_DIALOG, ignoring");
        }

        public void setPromptText(CharSequence charSequence) {
            this.mPrompt = charSequence;
        }

        public void setVerticalOffset(int i) {
            Log.e(Spinner.TAG, "Cannot set vertical offset for MODE_DIALOG, ignoring");
        }

        public void show(int i, int i2) {
            if (this.mListAdapter != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Spinner.this.getPopupContext());
                CharSequence charSequence = this.mPrompt;
                if (charSequence != null) {
                    builder.setTitle(charSequence);
                }
                this.mPopup = builder.setSingleChoiceItems(this.mListAdapter, Spinner.this.getSelectedItemPosition(), this).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialogInterface) {
                        Spinner.this.notifySpinnerDismiss();
                    }
                }).create();
                ListView listView = this.mPopup.getListView();
                listView.setTextDirection(i);
                listView.setTextAlignment(i2);
                this.mPopup.show();
            }
        }

        public void show(int i, int i2, float f, float f2) {
            show(i, i2);
        }
    }

    private static class DialogPopupAdapter extends DropDownAdapter {
        DialogPopupAdapter(@Nullable SpinnerAdapter spinnerAdapter, @Nullable Resources.Theme theme) {
            super(spinnerAdapter, theme);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            TaggingDrawableUtil.updateBackgroundState(view2, TaggingDrawableUtil.STATE_SET_MIDDLE);
            return view2;
        }
    }

    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;

        public DropDownAdapter(@Nullable SpinnerAdapter spinnerAdapter, @Nullable Resources.Theme theme) {
            this.mAdapter = spinnerAdapter;
            if (spinnerAdapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter) spinnerAdapter;
            }
            if (theme == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 23 && (spinnerAdapter instanceof ThemedSpinnerAdapter)) {
                ThemedSpinnerAdapter themedSpinnerAdapter = (ThemedSpinnerAdapter) spinnerAdapter;
                if (themedSpinnerAdapter.getDropDownViewTheme() != theme) {
                    themedSpinnerAdapter.setDropDownViewTheme(theme);
                }
            } else if (spinnerAdapter instanceof ThemedAdapter) {
                ThemedAdapter themedAdapter = (ThemedAdapter) spinnerAdapter;
                if (themedAdapter.getDropDownViewTheme() == null) {
                    themedAdapter.setDropDownViewTheme(theme);
                }
            }
        }

        public boolean areAllItemsEnabled() {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.areAllItemsEnabled();
            }
            return true;
        }

        public int getCount() {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return 0;
            }
            return spinnerAdapter.getCount();
        }

        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return null;
            }
            return spinnerAdapter.getDropDownView(i, view, viewGroup);
        }

        public Object getItem(int i) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return null;
            }
            return spinnerAdapter.getItem(i);
        }

        public long getItemId(int i) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return -1;
            }
            return spinnerAdapter.getItemId(i);
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            return getDropDownView(i, view, viewGroup);
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            return spinnerAdapter != null && spinnerAdapter.hasStableIds();
        }

        public boolean isEmpty() {
            return getCount() == 0;
        }

        public boolean isEnabled(int i) {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.isEnabled(i);
            }
            return true;
        }

        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter != null) {
                spinnerAdapter.registerDataSetObserver(dataSetObserver);
            }
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter != null) {
                spinnerAdapter.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    private static class DropDownPopupAdapter extends DropDownAdapter {
        DropDownPopupAdapter(@Nullable SpinnerAdapter spinnerAdapter, @Nullable Resources.Theme theme) {
            super(spinnerAdapter, theme);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            TaggingDrawableUtil.updateItemPadding(view2, i, getCount());
            return view2;
        }
    }

    private class DropdownPopup extends ImmersionListPopupWindow implements SpinnerPopup {
        private static final float SCREEN_MARGIN_BOTTOM_PROPORTION = 0.1f;
        private static final float SCREEN_MARGIN_TOP_PROPORTION = 0.1f;
        ListAdapter mAdapter;
        private CharSequence mHintText;
        private int mMarginScreen = 40;
        private int mOriginalHorizontalOffset;
        private final Rect mVisibleRect = new Rect();

        public DropdownPopup(Context context, AttributeSet attributeSet, int i) {
            super(context);
            setOnItemClickListener(new AdapterView.OnItemClickListener(Spinner.this) {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    Spinner.this.setSelection(i);
                    if (Spinner.this.getOnItemClickListener() != null) {
                        DropdownPopup dropdownPopup = DropdownPopup.this;
                        Spinner.this.performItemClick(view, i, dropdownPopup.mAdapter.getItemId(i));
                    }
                    DropdownPopup.this.dismiss();
                }
            });
        }

        private void changeWindowBackground(View view, float f) {
            if (view != null) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.flags |= 2;
                    layoutParams.dimAmount = f;
                    ((WindowManager) view.getContext().getSystemService("window")).updateViewLayout(view, layoutParams);
                    return;
                }
                return;
            }
            Log.w(Spinner.TAG, "can't change window dim with null view");
        }

        private int getListViewHeight() {
            if (getContentView() instanceof ListView) {
                ListAdapter adapter = ((ListView) getContentView()).getAdapter();
                int i = 0;
                for (int i2 = 0; i2 < adapter.getCount(); i2++) {
                    View view = adapter.getView(i2, (View) null, (ListView) getContentView());
                    view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
                    i += view.getMeasuredHeight();
                }
                return i;
            }
            getContentView().measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            return getContentView().getMeasuredHeight() + 0;
        }

        private void initListView(int i, int i2) {
            if (getContentView() instanceof ListView) {
                ListView listView = (ListView) getContentView();
                listView.setChoiceMode(1);
                listView.setTextDirection(i);
                listView.setTextAlignment(i2);
                int selectedItemPosition = Spinner.this.getSelectedItemPosition();
                listView.setSelection(selectedItemPosition);
                listView.setItemChecked(selectedItemPosition, true);
            }
        }

        private void showWithAnchor(View view, float f, float f2) {
            int[] iArr = new int[2];
            view.getLocationInWindow(iArr);
            int i = (int) f;
            boolean z = true;
            int i2 = iArr[1];
            View rootView = view.getRootView();
            rootView.getLocationInWindow(iArr);
            if (i > rootView.getWidth() / 2) {
                z = false;
            }
            int width = z ? this.mMarginScreen : (rootView.getWidth() - this.mMarginScreen) - getWidth();
            int listViewHeight = getListViewHeight();
            float f3 = (float) i2;
            if (f3 < ((float) rootView.getHeight()) * 0.1f) {
                f3 = ((float) rootView.getHeight()) * 0.1f;
            }
            float f4 = (float) listViewHeight;
            if (f3 + f4 > ((float) rootView.getHeight()) * 0.9f) {
                f3 = (((float) rootView.getHeight()) * 0.9f) - f4;
            }
            if (f3 < ((float) rootView.getHeight()) * 0.1f) {
                f3 = ((float) rootView.getHeight()) * 0.1f;
                setHeight((int) (((float) rootView.getHeight()) * 0.79999995f));
            }
            showAtLocation(view, 0, width, (int) f3);
        }

        public CharSequence getHintText() {
            return this.mHintText;
        }

        public int getHorizontalOffset() {
            return 0;
        }

        public int getHorizontalOriginalOffset() {
            return this.mOriginalHorizontalOffset;
        }

        public int getVerticalOffset() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public boolean isVisibleToUser(View view) {
            return ViewCompat.r(view) && view.getGlobalVisibleRect(this.mVisibleRect);
        }

        public void setAdapter(ListAdapter listAdapter) {
            Spinner.super.setAdapter(listAdapter);
            this.mAdapter = listAdapter;
        }

        public void setHorizontalOffset(int i) {
            Log.w(Spinner.TAG, "setHorizontalOffset do nothing");
        }

        public void setHorizontalOriginalOffset(int i) {
            this.mOriginalHorizontalOffset = i;
        }

        public void setPromptText(CharSequence charSequence) {
            this.mHintText = charSequence;
        }

        public void setVerticalOffset(int i) {
            Log.w(Spinner.TAG, "setVerticalOffset do nothing");
        }

        public void show(int i, int i2) {
            show(i, i2, 0.0f, 0.0f);
        }

        public void show(final int i, final int i2, float f, float f2) {
            ViewTreeObserver viewTreeObserver;
            boolean isShowing = isShowing();
            setInputMethodMode(2);
            show((View) Spinner.this, (ViewGroup) null, f, f2);
            initListView(i, i2);
            if (!isShowing && (viewTreeObserver = Spinner.this.getViewTreeObserver()) != null) {
                final AnonymousClass2 r7 = new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        DropdownPopup dropdownPopup = DropdownPopup.this;
                        if (!dropdownPopup.isVisibleToUser(Spinner.this)) {
                            DropdownPopup.this.dismiss();
                            return;
                        }
                        DropdownPopup dropdownPopup2 = DropdownPopup.this;
                        dropdownPopup2.show(i, i2, Spinner.this.mTouchX, Spinner.this.mTouchY);
                    }
                };
                viewTreeObserver.addOnGlobalLayoutListener(r7);
                setOnDismissListener(new PopupWindow.OnDismissListener() {
                    public void onDismiss() {
                        ViewTreeObserver viewTreeObserver = Spinner.this.getViewTreeObserver();
                        if (viewTreeObserver != null) {
                            viewTreeObserver.removeGlobalOnLayoutListener(r7);
                        }
                        Spinner.this.notifySpinnerDismiss();
                    }
                });
            }
        }

        public void show(View view, ViewGroup viewGroup, float f, float f2) {
            if (prepareShow(view, viewGroup)) {
                showWithAnchor(view, f, f2);
                changeWindowBackground(getContentView().getRootView(), 0.3f);
            }
        }
    }

    public interface OnSpinnerDismissListener {
        void onSpinnerDismiss();
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean mShowDropdown;

        SavedState(Parcel parcel) {
            super(parcel);
            this.mShowDropdown = parcel.readByte() != 0;
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeByte(this.mShowDropdown ? (byte) 1 : 0);
        }
    }

    private static class SpinnerCheckedProvider implements SpinnerCheckableArrayAdapter.CheckedStateProvider {
        private Spinner mSpinner;

        public SpinnerCheckedProvider(Spinner spinner) {
            this.mSpinner = spinner;
        }

        public boolean isChecked(int i) {
            return this.mSpinner.getSelectedItemPosition() == i;
        }
    }

    private interface SpinnerPopup {
        void dismiss();

        Drawable getBackground();

        CharSequence getHintText();

        int getHorizontalOffset();

        int getHorizontalOriginalOffset();

        int getVerticalOffset();

        boolean isShowing();

        void setAdapter(ListAdapter listAdapter);

        void setBackgroundDrawable(Drawable drawable);

        void setHorizontalOffset(int i);

        void setHorizontalOriginalOffset(int i);

        void setPromptText(CharSequence charSequence);

        void setVerticalOffset(int i);

        void show(int i, int i2);

        void show(int i, int i2, float f, float f2);
    }

    public interface ThemedAdapter extends SpinnerAdapter {
        @Nullable
        Resources.Theme getDropDownViewTheme();

        void setDropDownViewTheme(@Nullable Resources.Theme theme);
    }

    static {
        try {
            FORWARDING_LISTENER = android.widget.Spinner.class.getDeclaredField("mForwardingListener");
            FORWARDING_LISTENER.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "static initializer: ", e);
        }
    }

    public Spinner(Context context) {
        this(context, (AttributeSet) null);
    }

    public Spinner(Context context, int i) {
        this(context, (AttributeSet) null, a.miuiSpinnerStyle, i);
    }

    public Spinner(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, a.miuiSpinnerStyle);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, -1);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i, int i2) {
        this(context, attributeSet, i, i2, (Resources.Theme) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0035  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00bd  */
    /* JADX WARNING: Removed duplicated region for block: B:29:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Spinner(android.content.Context r6, android.util.AttributeSet r7, int r8, int r9, android.content.res.Resources.Theme r10) {
        /*
            r5 = this;
            r5.<init>(r6, r7, r8)
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r5.mTempRect = r0
            int[] r0 = d.b.e.Spinner
            r1 = 0
            android.content.res.TypedArray r0 = r6.obtainStyledAttributes(r7, r0, r8, r1)
            if (r10 == 0) goto L_0x001f
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 23
            if (r2 < r3) goto L_0x001f
            android.view.ContextThemeWrapper r2 = new android.view.ContextThemeWrapper
            r2.<init>(r6, r10)
            goto L_0x002c
        L_0x001f:
            int r10 = d.b.e.Spinner_popupTheme
            int r10 = r0.getResourceId(r10, r1)
            if (r10 == 0) goto L_0x002f
            android.view.ContextThemeWrapper r2 = new android.view.ContextThemeWrapper
            r2.<init>(r6, r10)
        L_0x002c:
            r5.mPopupContext = r2
            goto L_0x0031
        L_0x002f:
            r5.mPopupContext = r6
        L_0x0031:
            r10 = -1
            r2 = 1
            if (r9 != r10) goto L_0x003b
            int r9 = d.b.e.Spinner_spinnerModeCompat
            int r9 = r0.getInt(r9, r2)
        L_0x003b:
            r10 = 0
            if (r9 == 0) goto L_0x0088
            if (r9 == r2) goto L_0x0041
            goto L_0x009a
        L_0x0041:
            miui.external.widget.Spinner$DropdownPopup r9 = new miui.external.widget.Spinner$DropdownPopup
            android.content.Context r3 = r5.mPopupContext
            r9.<init>(r3, r7, r8)
            android.content.Context r3 = r5.mPopupContext
            int[] r4 = d.b.e.Spinner
            android.content.res.TypedArray r7 = r3.obtainStyledAttributes(r7, r4, r8, r1)
            int r8 = d.b.e.Spinner_android_dropDownWidth
            r3 = -2
            int r8 = r7.getLayoutDimension(r8, r3)
            r5.mDropDownWidth = r8
            int r8 = d.b.e.Spinner_dropDownMinWidth
            int r8 = r7.getLayoutDimension(r8, r3)
            r5.mDropDownMinWidth = r8
            int r8 = d.b.e.Spinner_android_popupBackground
            int r8 = r7.getResourceId(r8, r1)
            if (r8 == 0) goto L_0x006d
            r5.setPopupBackgroundResource(r8)
            goto L_0x0076
        L_0x006d:
            int r8 = d.b.e.Spinner_android_popupBackground
            android.graphics.drawable.Drawable r8 = r7.getDrawable(r8)
            r9.setBackgroundDrawable(r8)
        L_0x0076:
            int r8 = d.b.e.Spinner_android_prompt
            java.lang.String r8 = r0.getString(r8)
            r9.setPromptText(r8)
            r7.recycle()
            r5.mPopup = r9
            r5.makeSupperForwardingListenerInvalid()
            goto L_0x009a
        L_0x0088:
            miui.external.widget.Spinner$DialogPopup r7 = new miui.external.widget.Spinner$DialogPopup
            r7.<init>()
            r5.mPopup = r7
            miui.external.widget.Spinner$SpinnerPopup r7 = r5.mPopup
            int r8 = d.b.e.Spinner_android_prompt
            java.lang.String r8 = r0.getString(r8)
            r7.setPromptText(r8)
        L_0x009a:
            int r7 = d.b.e.Spinner_android_entries
            java.lang.CharSequence[] r7 = r0.getTextArray(r7)
            if (r7 == 0) goto L_0x00b4
            android.widget.ArrayAdapter r8 = new android.widget.ArrayAdapter
            int r9 = d.b.d.miuix_compat_simple_spinner_layout
            r1 = 16908308(0x1020014, float:2.3877285E-38)
            r8.<init>(r6, r9, r1, r7)
            int r6 = d.b.d.miuix_compat_simple_spinner_dropdown_item
            r8.setDropDownViewResource(r6)
            r5.setAdapter((android.widget.SpinnerAdapter) r8)
        L_0x00b4:
            r0.recycle()
            r5.mPopupSet = r2
            android.widget.SpinnerAdapter r6 = r5.mTempAdapter
            if (r6 == 0) goto L_0x00c2
            r5.setAdapter((android.widget.SpinnerAdapter) r6)
            r5.mTempAdapter = r10
        L_0x00c2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.external.widget.Spinner.<init>(android.content.Context, android.util.AttributeSet, int, int, android.content.res.Resources$Theme):void");
    }

    private int compatMeasureSelectItemWidth(SpinnerAdapter spinnerAdapter, Drawable drawable) {
        if (spinnerAdapter == null) {
            return 0;
        }
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
        View view = spinnerAdapter.getView(Math.max(0, getSelectedItemPosition()), (View) null, this);
        if (view.getLayoutParams() == null) {
            view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        }
        view.measure(makeMeasureSpec, makeMeasureSpec2);
        int max = Math.max(0, view.getMeasuredWidth());
        if (drawable == null) {
            return max;
        }
        drawable.getPadding(this.mTempRect);
        Rect rect = this.mTempRect;
        return max + rect.left + rect.right;
    }

    private void makeSupperForwardingListenerInvalid() {
        Field field = FORWARDING_LISTENER;
        if (field != null) {
            try {
                field.set(this, (Object) null);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "makeSupperForwardingListenerInvalid: ", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifySpinnerDismiss() {
        OnSpinnerDismissListener onSpinnerDismissListener = this.mOnSpinnerDismissListener;
        if (onSpinnerDismissListener != null) {
            onSpinnerDismissListener.onSpinnerDismiss();
        }
    }

    private boolean superViewPerformClick() {
        sendAccessibilityEvent(1);
        return false;
    }

    /* access modifiers changed from: package-private */
    public int compatMeasureContentWidth(SpinnerAdapter spinnerAdapter, Drawable drawable) {
        int i = 0;
        if (spinnerAdapter == null) {
            return 0;
        }
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
        int max = Math.max(0, getSelectedItemPosition());
        int min = Math.min(spinnerAdapter.getCount(), max + 15);
        int i2 = 0;
        View view = null;
        for (int max2 = Math.max(0, max - (15 - (min - max))); max2 < min; max2++) {
            int itemViewType = spinnerAdapter.getItemViewType(max2);
            if (itemViewType != i) {
                view = null;
                i = itemViewType;
            }
            view = spinnerAdapter.getView(max2, view, this);
            if (view.getLayoutParams() == null) {
                view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            }
            view.measure(makeMeasureSpec, makeMeasureSpec2);
            i2 = Math.max(i2, view.getMeasuredWidth());
        }
        if (drawable == null) {
            return i2;
        }
        drawable.getPadding(this.mTempRect);
        Rect rect = this.mTempRect;
        return i2 + rect.left + rect.right;
    }

    public int getDropDownHorizontalOffset() {
        SpinnerPopup spinnerPopup = this.mPopup;
        return spinnerPopup != null ? spinnerPopup.getHorizontalOffset() : super.getDropDownHorizontalOffset();
    }

    public int getDropDownVerticalOffset() {
        SpinnerPopup spinnerPopup = this.mPopup;
        return spinnerPopup != null ? spinnerPopup.getVerticalOffset() : super.getDropDownVerticalOffset();
    }

    public int getDropDownWidth() {
        return this.mPopup != null ? this.mDropDownWidth : super.getDropDownWidth();
    }

    public Drawable getPopupBackground() {
        SpinnerPopup spinnerPopup = this.mPopup;
        return spinnerPopup != null ? spinnerPopup.getBackground() : super.getPopupBackground();
    }

    public Context getPopupContext() {
        return this.mPopupContext;
    }

    public CharSequence getPrompt() {
        SpinnerPopup spinnerPopup = this.mPopup;
        return spinnerPopup != null ? spinnerPopup.getHintText() : super.getPrompt();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null && spinnerPopup.isShowing()) {
            this.mPopup.dismiss();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mPopup != null && View.MeasureSpec.getMode(i) == Integer.MIN_VALUE) {
            setMeasuredDimension(Math.min(Math.min(getMeasuredWidth(), compatMeasureSelectItemWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(i)), getMeasuredHeight());
        }
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        ViewTreeObserver viewTreeObserver;
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.mShowDropdown && (viewTreeObserver = getViewTreeObserver()) != null) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (!Spinner.this.mPopup.isShowing()) {
                        Spinner.this.showPopup();
                    }
                    ViewTreeObserver viewTreeObserver = Spinner.this.getViewTreeObserver();
                    if (viewTreeObserver != null) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        SpinnerPopup spinnerPopup = this.mPopup;
        savedState.mShowDropdown = spinnerPopup != null && spinnerPopup.isShowing();
        return savedState;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            this.mTouchX = motionEvent.getX();
            this.mTouchY = motionEvent.getY();
        }
        return super.onTouchEvent(motionEvent);
    }

    public boolean performClick() {
        int[] iArr = new int[2];
        getLocationInWindow(iArr);
        return performClick((float) iArr[0], (float) iArr[1]);
    }

    public boolean performClick(float f, float f2) {
        this.mTouchX = f;
        this.mTouchY = f2;
        if (superViewPerformClick()) {
            return true;
        }
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup == null) {
            return super.performClick();
        }
        if (!spinnerPopup.isShowing()) {
            showPopup(this.mTouchX, this.mTouchY);
        }
        return true;
    }

    public void setAdapter(SpinnerAdapter spinnerAdapter) {
        ListAdapter dropDownPopupAdapter;
        if (!this.mPopupSet) {
            this.mTempAdapter = spinnerAdapter;
            return;
        }
        super.setAdapter(spinnerAdapter);
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DialogPopup) {
            dropDownPopupAdapter = new DialogPopupAdapter(spinnerAdapter, getPopupContext().getTheme());
        } else if (spinnerPopup instanceof DropdownPopup) {
            dropDownPopupAdapter = new DropDownPopupAdapter(spinnerAdapter, getPopupContext().getTheme());
        } else {
            return;
        }
        spinnerPopup.setAdapter(dropDownPopupAdapter);
    }

    public void setDoubleLineContentAdapter(SpinnerDoubleLineContentAdapter spinnerDoubleLineContentAdapter) {
        setAdapter((SpinnerAdapter) new SpinnerCheckableArrayAdapter(getContext(), d.miuix_compat_simple_spinner_layout, spinnerDoubleLineContentAdapter, new SpinnerCheckedProvider(this)));
    }

    public void setDropDownHorizontalOffset(int i) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setHorizontalOriginalOffset(i);
            this.mPopup.setHorizontalOffset(i);
            return;
        }
        super.setDropDownHorizontalOffset(i);
    }

    public void setDropDownVerticalOffset(int i) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setVerticalOffset(i);
        } else {
            super.setDropDownVerticalOffset(i);
        }
    }

    public void setDropDownWidth(int i) {
        if (this.mPopup != null) {
            this.mDropDownWidth = i;
        } else {
            super.setDropDownWidth(i);
        }
    }

    public void setOnSpinnerDismissListener(OnSpinnerDismissListener onSpinnerDismissListener) {
        this.mOnSpinnerDismissListener = onSpinnerDismissListener;
    }

    public void setPopupBackgroundDrawable(Drawable drawable) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setBackgroundDrawable(drawable);
        } else {
            super.setPopupBackgroundDrawable(drawable);
        }
    }

    public void setPopupBackgroundResource(@DrawableRes int i) {
        setPopupBackgroundDrawable(Build.VERSION.SDK_INT >= 21 ? getPopupContext().getDrawable(i) : getPopupContext().getResources().getDrawable(i));
    }

    public void setPrompt(CharSequence charSequence) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setPromptText(charSequence);
        } else {
            super.setPrompt(charSequence);
        }
    }

    /* access modifiers changed from: package-private */
    public void showPopup() {
        this.mPopup.show(getTextDirection(), getTextAlignment());
    }

    /* access modifiers changed from: package-private */
    public void showPopup(float f, float f2) {
        this.mPopup.show(getTextDirection(), getTextAlignment(), f, f2);
    }
}

package miui.external.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import d.b.c;
import d.b.d;

public class SpinnerCheckableArrayAdapter extends ArrayAdapter {
    private static final int TAG_VIEW = c.tag_spinner_dropdown_view;
    private CheckedStateProvider mCheckProvider;
    private ArrayAdapter mContentAdapter;
    private LayoutInflater mInflater;

    public interface CheckedStateProvider {
        boolean isChecked(int i);
    }

    private static class ViewHolder {
        FrameLayout container;
        RadioButton radioButton;

        private ViewHolder() {
        }
    }

    public SpinnerCheckableArrayAdapter(@NonNull Context context, int i, ArrayAdapter arrayAdapter, CheckedStateProvider checkedStateProvider) {
        super(context, i, 16908308);
        this.mInflater = LayoutInflater.from(context);
        this.mContentAdapter = arrayAdapter;
        this.mCheckProvider = checkedStateProvider;
    }

    public SpinnerCheckableArrayAdapter(@NonNull Context context, ArrayAdapter arrayAdapter, CheckedStateProvider checkedStateProvider) {
        this(context, d.miuix_compat_simple_spinner_layout_integrated, arrayAdapter, checkedStateProvider);
    }

    public int getCount() {
        return this.mContentAdapter.getCount();
    }

    public View getDropDownView(int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        boolean z = false;
        if (view == null || view.getTag(TAG_VIEW) == null) {
            view = this.mInflater.inflate(d.miuix_compat_spinner_dropdown_checkable_item, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.container = (FrameLayout) view.findViewById(c.spinner_dropdown_container);
            viewHolder.radioButton = (RadioButton) view.findViewById(16908289);
            view.setTag(TAG_VIEW, viewHolder);
        }
        Object tag = view.getTag(TAG_VIEW);
        if (tag != null) {
            ViewHolder viewHolder2 = (ViewHolder) tag;
            View dropDownView = this.mContentAdapter.getDropDownView(i, viewHolder2.container.getChildAt(0), viewGroup);
            viewHolder2.container.removeAllViews();
            viewHolder2.container.addView(dropDownView);
            CheckedStateProvider checkedStateProvider = this.mCheckProvider;
            if (checkedStateProvider != null && checkedStateProvider.isChecked(i)) {
                z = true;
            }
            viewHolder2.radioButton.setChecked(z);
            view.setActivated(z);
        }
        return view;
    }

    @Nullable
    public Object getItem(int i) {
        return this.mContentAdapter.getItem(i);
    }

    public long getItemId(int i) {
        return this.mContentAdapter.getItemId(i);
    }

    public boolean hasStableIds() {
        return this.mContentAdapter.hasStableIds();
    }
}

package b.b.a.d.b;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.ListView;
import miui.R;
import miui.util.AttributeResolver;

public class m extends PreferenceFragment {
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ListView listView = (ListView) view.findViewById(16908298);
        if (listView != null) {
            listView.setClipToPadding(false);
            listView.setPadding(0, 0, 0, (int) AttributeResolver.resolveDimension(view.getContext(), R.attr.preferenceScreenPaddingBottom));
        }
    }
}

package com.miui.earthquakewarning.view;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.util.Log;

public class BaseDialogFragment extends DialogFragment {
    public void show(FragmentManager fragmentManager, String str) {
        try {
            super.show(fragmentManager, str);
        } catch (IllegalStateException e) {
            Log.e("BaseDialogFragment", "show fragment exception", e);
        }
    }
}

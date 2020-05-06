package miui.cta;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class CTADialogFragment extends DialogFragment {
    private static final String MESSAGE = "key_message";
    private static final String OPTIONAL = "key_optional";
    static final String TAG = "CTADialog";

    public void showDialog(Activity activity, String message, boolean optional) {
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
            setArguments(args);
        }
        args.putString(MESSAGE, message);
        args.putBoolean(OPTIONAL, optional);
        show(activity.getFragmentManager(), TAG);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        CTADialogBuilder builder = new CTADialogBuilder(activity);
        builder.setMessage(getArguments().getString(MESSAGE));
        OnDialogClickListener listener = new OnDialogClickListener();
        builder.setPositiveButton(listener);
        if (isOptional()) {
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        } else {
            builder.setNegativeButton(listener);
        }
        return builder.create();
    }

    public void onStart() {
        super.onStart();
        if (CTAManager.getInstance().isAccepted()) {
            dismiss();
        }
    }

    public void onCancel(DialogInterface dialog) {
        CTAManager.getInstance().onReject();
        Activity activity = getActivity();
        if (activity != null && !isOptional()) {
            activity.finish();
        }
    }

    private boolean isOptional() {
        return getArguments().getBoolean(OPTIONAL);
    }

    private class OnDialogClickListener implements DialogInterface.OnClickListener {
        private OnDialogClickListener() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (which == -1) {
                CTAManager.getInstance().onAccept(CTADialogFragment.this.getActivity());
            } else if (which == -2) {
                CTADialogFragment.this.onCancel(dialog);
            }
        }
    }
}

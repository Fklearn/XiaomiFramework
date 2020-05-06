package de.greenrobot.event.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import de.greenrobot.event.EventBus;

public class ErrorDialogManager {
    public static final String KEY_EVENT_TYPE_ON_CLOSE = "de.greenrobot.eventbus.errordialog.event_type_on_close";
    public static final String KEY_FINISH_AFTER_DIALOG = "de.greenrobot.eventbus.errordialog.finish_after_dialog";
    public static final String KEY_ICON_ID = "de.greenrobot.eventbus.errordialog.icon_id";
    public static final String KEY_MESSAGE = "de.greenrobot.eventbus.errordialog.message";
    public static final String KEY_TITLE = "de.greenrobot.eventbus.errordialog.title";
    protected static final String TAG_ERROR_DIALOG = "de.greenrobot.eventbus.error_dialog";
    protected static final String TAG_ERROR_DIALOG_MANAGER = "de.greenrobot.eventbus.error_dialog_manager";
    public static ErrorDialogFragmentFactory<?> factory;

    public static class SupportManagerFragment extends Fragment {
        protected Bundle argumentsForErrorDialog;
        private EventBus eventBus;
        private Object executionScope;
        protected boolean finishAfterDialog;
        private boolean skipRegisterOnNextResume;

        public void onCreate(Bundle savedInstanceState) {
            ErrorDialogManager.super.onCreate(savedInstanceState);
            this.eventBus = ErrorDialogManager.factory.config.getEventBus();
            this.eventBus.register(this);
            this.skipRegisterOnNextResume = true;
        }

        public void onResume() {
            ErrorDialogManager.super.onResume();
            if (this.skipRegisterOnNextResume) {
                this.skipRegisterOnNextResume = false;
                return;
            }
            this.eventBus = ErrorDialogManager.factory.config.getEventBus();
            this.eventBus.register(this);
        }

        public void onPause() {
            this.eventBus.unregister(this);
            ErrorDialogManager.super.onPause();
        }

        public void onEventMainThread(ThrowableFailureEvent event) {
            if (ErrorDialogManager.isInExecutionScope(this.executionScope, event)) {
                ErrorDialogManager.checkLogException(event);
                FragmentManager fm = getFragmentManager();
                fm.executePendingTransactions();
                DialogFragment existingFragment = fm.findFragmentByTag(ErrorDialogManager.TAG_ERROR_DIALOG);
                if (existingFragment != null) {
                    existingFragment.dismiss();
                }
                DialogFragment errorFragment = ErrorDialogManager.factory.prepareErrorFragment(event, this.finishAfterDialog, this.argumentsForErrorDialog);
                if (errorFragment != null) {
                    errorFragment.show(fm, ErrorDialogManager.TAG_ERROR_DIALOG);
                }
            }
        }

        public static void attachTo(Activity activity, Object executionScope2, boolean finishAfterDialog2, Bundle argumentsForErrorDialog2) {
            FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
            SupportManagerFragment fragment = fm.findFragmentByTag(ErrorDialogManager.TAG_ERROR_DIALOG_MANAGER);
            if (fragment == null) {
                fragment = new SupportManagerFragment();
                fm.beginTransaction().add(fragment, ErrorDialogManager.TAG_ERROR_DIALOG_MANAGER).commit();
                fm.executePendingTransactions();
            }
            fragment.finishAfterDialog = finishAfterDialog2;
            fragment.argumentsForErrorDialog = argumentsForErrorDialog2;
            fragment.executionScope = executionScope2;
        }
    }

    @TargetApi(11)
    public static class HoneycombManagerFragment extends android.app.Fragment {
        protected Bundle argumentsForErrorDialog;
        private EventBus eventBus;
        private Object executionScope;
        protected boolean finishAfterDialog;

        public void onResume() {
            super.onResume();
            this.eventBus = ErrorDialogManager.factory.config.getEventBus();
            this.eventBus.register(this);
        }

        public void onPause() {
            this.eventBus.unregister(this);
            super.onPause();
        }

        public void onEventMainThread(ThrowableFailureEvent event) {
            if (ErrorDialogManager.isInExecutionScope(this.executionScope, event)) {
                ErrorDialogManager.checkLogException(event);
                android.app.FragmentManager fm = getFragmentManager();
                fm.executePendingTransactions();
                android.app.DialogFragment existingFragment = (android.app.DialogFragment) fm.findFragmentByTag(ErrorDialogManager.TAG_ERROR_DIALOG);
                if (existingFragment != null) {
                    existingFragment.dismiss();
                }
                android.app.DialogFragment errorFragment = (android.app.DialogFragment) ErrorDialogManager.factory.prepareErrorFragment(event, this.finishAfterDialog, this.argumentsForErrorDialog);
                if (errorFragment != null) {
                    errorFragment.show(fm, ErrorDialogManager.TAG_ERROR_DIALOG);
                }
            }
        }

        public static void attachTo(Activity activity, Object executionScope2, boolean finishAfterDialog2, Bundle argumentsForErrorDialog2) {
            android.app.FragmentManager fm = activity.getFragmentManager();
            HoneycombManagerFragment fragment = (HoneycombManagerFragment) fm.findFragmentByTag(ErrorDialogManager.TAG_ERROR_DIALOG_MANAGER);
            if (fragment == null) {
                fragment = new HoneycombManagerFragment();
                fm.beginTransaction().add(fragment, ErrorDialogManager.TAG_ERROR_DIALOG_MANAGER).commit();
                fm.executePendingTransactions();
            }
            fragment.finishAfterDialog = finishAfterDialog2;
            fragment.argumentsForErrorDialog = argumentsForErrorDialog2;
            fragment.executionScope = executionScope2;
        }
    }

    public static void attachTo(Activity activity) {
        attachTo(activity, false, (Bundle) null);
    }

    public static void attachTo(Activity activity, boolean finishAfterDialog) {
        attachTo(activity, finishAfterDialog, (Bundle) null);
    }

    public static void attachTo(Activity activity, boolean finishAfterDialog, Bundle argumentsForErrorDialog) {
        attachTo(activity, activity.getClass(), finishAfterDialog, argumentsForErrorDialog);
    }

    public static void attachTo(Activity activity, Object executionScope, boolean finishAfterDialog, Bundle argumentsForErrorDialog) {
        if (factory == null) {
            throw new RuntimeException("You must set the static factory field to configure error dialogs for your app.");
        } else if (isSupportActivity(activity)) {
            SupportManagerFragment.attachTo(activity, executionScope, finishAfterDialog, argumentsForErrorDialog);
        } else {
            HoneycombManagerFragment.attachTo(activity, executionScope, finishAfterDialog, argumentsForErrorDialog);
        }
    }

    private static boolean isSupportActivity(Activity activity) {
        Class<?> c = activity.getClass().getSuperclass();
        while (c != null) {
            String name = c.getName();
            if (name.equals("android.support.v4.app.FragmentActivity")) {
                return true;
            }
            if (name.startsWith("com.actionbarsherlock.app") && (name.endsWith(".SherlockActivity") || name.endsWith(".SherlockListActivity") || name.endsWith(".SherlockPreferenceActivity"))) {
                throw new RuntimeException("Please use SherlockFragmentActivity. Illegal activity: " + name);
            } else if (!name.equals("android.app.Activity")) {
                c = c.getSuperclass();
            } else if (Build.VERSION.SDK_INT >= 11) {
                return false;
            } else {
                throw new RuntimeException("Illegal activity without fragment support. Either use Android 3.0+ or android.support.v4.app.FragmentActivity.");
            }
        }
        throw new RuntimeException("Illegal activity type: " + activity.getClass());
    }

    protected static void checkLogException(ThrowableFailureEvent event) {
        if (factory.config.logExceptions) {
            String tag = factory.config.tagForLoggingExceptions;
            if (tag == null) {
                tag = EventBus.TAG;
            }
            Log.i(tag, "Error dialog manager received exception", event.throwable);
        }
    }

    /* access modifiers changed from: private */
    public static boolean isInExecutionScope(Object executionScope, ThrowableFailureEvent event) {
        Object eventExecutionScope;
        if (event == null || (eventExecutionScope = event.getExecutionScope()) == null || eventExecutionScope.equals(executionScope)) {
            return true;
        }
        return false;
    }
}

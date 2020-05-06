package com.android.server.autofill.ui;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.autofill.BatchUpdates;
import android.service.autofill.CustomDescription;
import android.service.autofill.InternalOnClickAction;
import android.service.autofill.InternalTransformation;
import android.service.autofill.InternalValidator;
import android.service.autofill.SaveInfo;
import android.service.autofill.ValueFinder;
import android.text.Html;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.server.UiThread;
import com.android.server.autofill.Helper;
import java.io.PrintWriter;
import java.util.ArrayList;

final class SaveUi {
    private static final String TAG = "SaveUi";
    private static final int THEME_ID_DARK = 16974814;
    private static final int THEME_ID_LIGHT = 16974822;
    private final boolean mCompatMode;
    private final ComponentName mComponentName;
    private boolean mDestroyed;
    private final Dialog mDialog;
    private final Handler mHandler = UiThread.getHandler();
    private final OneActionThenDestroyListener mListener;
    private final MetricsLogger mMetricsLogger = new MetricsLogger();
    private final OverlayControl mOverlayControl;
    private final PendingUi mPendingUi;
    private final String mServicePackageName;
    private final CharSequence mSubTitle;
    private final int mThemeId;
    private final CharSequence mTitle;

    public interface OnSaveListener {
        void onCancel(IntentSender intentSender);

        void onDestroy();

        void onSave();
    }

    private class OneActionThenDestroyListener implements OnSaveListener {
        private boolean mDone;
        private final OnSaveListener mRealListener;

        OneActionThenDestroyListener(OnSaveListener realListener) {
            this.mRealListener = realListener;
        }

        public void onSave() {
            if (Helper.sDebug) {
                Slog.d(SaveUi.TAG, "OneTimeListener.onSave(): " + this.mDone);
            }
            if (!this.mDone) {
                this.mRealListener.onSave();
            }
        }

        public void onCancel(IntentSender listener) {
            if (Helper.sDebug) {
                Slog.d(SaveUi.TAG, "OneTimeListener.onCancel(): " + this.mDone);
            }
            if (!this.mDone) {
                this.mRealListener.onCancel(listener);
            }
        }

        public void onDestroy() {
            if (Helper.sDebug) {
                Slog.d(SaveUi.TAG, "OneTimeListener.onDestroy(): " + this.mDone);
            }
            if (!this.mDone) {
                this.mDone = true;
                this.mRealListener.onDestroy();
            }
        }
    }

    SaveUi(Context context, PendingUi pendingUi, CharSequence serviceLabel, Drawable serviceIcon, String servicePackageName, ComponentName componentName, SaveInfo info, ValueFinder valueFinder, OverlayControl overlayControl, OnSaveListener listener, boolean nightMode, boolean isUpdate, boolean compatMode) {
        int i;
        int i2;
        int i3;
        int i4;
        Context context2 = context;
        String str = servicePackageName;
        SaveInfo saveInfo = info;
        boolean z = nightMode;
        if (Helper.sVerbose) {
            Slog.v(TAG, "nightMode: " + z);
        }
        this.mThemeId = z ? THEME_ID_DARK : THEME_ID_LIGHT;
        this.mPendingUi = pendingUi;
        this.mListener = new OneActionThenDestroyListener(listener);
        this.mOverlayControl = overlayControl;
        this.mServicePackageName = str;
        this.mComponentName = componentName;
        this.mCompatMode = compatMode;
        if (TextUtils.equals(str, "com.miui.contentcatcher")) {
            this.mSubTitle = "Miui Autofill";
            this.mTitle = "Miui Autofill";
            this.mDialog = SaveUiInjector.showDialog(context2, this.mOverlayControl, new DialogInterface.OnClickListener(saveInfo) {
                private final /* synthetic */ SaveInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    SaveUi.this.lambda$new$0$SaveUi(this.f$1, dialogInterface, i);
                }
            }, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    SaveUi.this.lambda$new$1$SaveUi(dialogInterface, i);
                }
            }, new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    SaveUi.this.lambda$new$2$SaveUi(dialogInterface);
                }
            });
            return;
        }
        Context context3 = new ContextThemeWrapper(context2, this.mThemeId);
        View view = LayoutInflater.from(context3).inflate(17367103, (ViewGroup) null);
        TextView titleView = (TextView) view.findViewById(16908754);
        ArraySet<String> types = new ArraySet<>(3);
        int type = info.getType();
        if ((type & 1) != 0) {
            types.add(context3.getString(17039598));
        }
        if ((type & 2) != 0) {
            types.add(context3.getString(17039595));
        }
        if ((type & 4) != 0) {
            types.add(context3.getString(17039596));
        }
        if ((type & 8) != 0) {
            types.add(context3.getString(17039599));
        }
        if ((type & 16) != 0) {
            types.add(context3.getString(17039597));
        }
        int size = types.size();
        if (size == 1) {
            if (isUpdate) {
                i = 17039608;
            } else {
                i = 17039594;
            }
            this.mTitle = Html.fromHtml(context3.getString(i, new Object[]{types.valueAt(0), serviceLabel}), 0);
        } else if (size == 2) {
            if (isUpdate) {
                i2 = 17039606;
            } else {
                i2 = 17039592;
            }
            this.mTitle = Html.fromHtml(context3.getString(i2, new Object[]{types.valueAt(0), types.valueAt(1), serviceLabel}), 0);
        } else if (size != 3) {
            if (isUpdate) {
                i4 = 17039605;
            } else {
                i4 = 17039591;
            }
            this.mTitle = Html.fromHtml(context3.getString(i4, new Object[]{serviceLabel}), 0);
        } else {
            if (isUpdate) {
                i3 = 17039607;
            } else {
                i3 = 17039593;
            }
            this.mTitle = Html.fromHtml(context3.getString(i3, new Object[]{types.valueAt(0), types.valueAt(1), types.valueAt(2), serviceLabel}), 0);
        }
        titleView.setText(this.mTitle);
        setServiceIcon(context3, view, serviceIcon);
        boolean hasCustomDescription = applyCustomDescription(context3, view, valueFinder, saveInfo);
        if (hasCustomDescription) {
            this.mSubTitle = null;
            if (Helper.sDebug) {
                Slog.d(TAG, "on constructor: applied custom description");
            }
            boolean z2 = hasCustomDescription;
        } else {
            this.mSubTitle = info.getDescription();
            if (this.mSubTitle != null) {
                writeLog(1131, type);
                ViewGroup subtitleContainer = (ViewGroup) view.findViewById(16908751);
                TextView subtitleView = new TextView(context3);
                boolean z3 = hasCustomDescription;
                subtitleView.setText(this.mSubTitle);
                subtitleContainer.addView(subtitleView, new ViewGroup.LayoutParams(-1, -2));
                subtitleContainer.setVisibility(0);
            }
            if (Helper.sDebug) {
                Slog.d(TAG, "on constructor: title=" + this.mTitle + ", subTitle=" + this.mSubTitle);
            }
        }
        TextView noButton = (TextView) view.findViewById(16908753);
        if (info.getNegativeActionStyle() == 1) {
            noButton.setText(17041058);
        } else {
            noButton.setText(17039590);
        }
        noButton.setOnClickListener(new View.OnClickListener(saveInfo) {
            private final /* synthetic */ SaveInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                SaveUi.this.lambda$new$3$SaveUi(this.f$1, view);
            }
        });
        TextView yesButton = (TextView) view.findViewById(16908755);
        if (isUpdate) {
            yesButton.setText(17039609);
        }
        yesButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SaveUi.this.lambda$new$4$SaveUi(view);
            }
        });
        this.mDialog = new Dialog(context3, this.mThemeId);
        this.mDialog.setContentView(view);
        this.mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                SaveUi.this.lambda$new$5$SaveUi(dialogInterface);
            }
        });
        Window window = this.mDialog.getWindow();
        window.setType(2038);
        window.addFlags(393248);
        window.addPrivateFlags(16);
        window.setSoftInputMode(32);
        window.setGravity(81);
        window.setCloseOnTouchOutside(true);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.accessibilityTitle = context3.getString(17039589);
        params.windowAnimations = 16974610;
        show();
    }

    public /* synthetic */ void lambda$new$0$SaveUi(SaveInfo info, DialogInterface v, int w) {
        this.mListener.onCancel(info.getNegativeActionListener());
    }

    public /* synthetic */ void lambda$new$1$SaveUi(DialogInterface v, int w) {
        this.mListener.onSave();
    }

    public /* synthetic */ void lambda$new$2$SaveUi(DialogInterface d) {
        this.mListener.onCancel((IntentSender) null);
    }

    public /* synthetic */ void lambda$new$3$SaveUi(SaveInfo info, View v) {
        this.mListener.onCancel(info.getNegativeActionListener());
    }

    public /* synthetic */ void lambda$new$4$SaveUi(View v) {
        this.mListener.onSave();
    }

    public /* synthetic */ void lambda$new$5$SaveUi(DialogInterface d) {
        this.mListener.onCancel((IntentSender) null);
    }

    private boolean applyCustomDescription(Context context, View saveUiView, ValueFinder valueFinder, SaveInfo info) {
        SparseArray<InternalOnClickAction> actions;
        ArrayList<Pair<Integer, InternalTransformation>> transformations;
        int type;
        int type2;
        ArrayList<Pair<Integer, InternalTransformation>> transformations2;
        Context context2 = context;
        ValueFinder valueFinder2 = valueFinder;
        CustomDescription customDescription = info.getCustomDescription();
        if (customDescription == null) {
            return false;
        }
        int type3 = info.getType();
        writeLog(1129, type3);
        RemoteViews template = customDescription.getPresentation();
        if (template == null) {
            Slog.w(TAG, "No remote view on custom description");
            return false;
        }
        ArrayList<Pair<Integer, InternalTransformation>> transformations3 = customDescription.getTransformations();
        if (Helper.sVerbose) {
            Slog.v(TAG, "applyCustomDescription(): transformations = " + transformations3);
        }
        if (transformations3 == null || InternalTransformation.batchApply(valueFinder2, template, transformations3)) {
            try {
                View customSubtitleView = template.applyWithTheme(context2, (ViewGroup) null, new RemoteViews.OnClickHandler(type3) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onClickHandler(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
                        return SaveUi.this.lambda$applyCustomDescription$6$SaveUi(this.f$1, view, pendingIntent, remoteResponse);
                    }
                }, this.mThemeId);
                ArrayList<Pair<InternalValidator, BatchUpdates>> updates = customDescription.getUpdates();
                if (Helper.sVerbose) {
                    try {
                        Slog.v(TAG, "applyCustomDescription(): view = " + customSubtitleView + " updates=" + updates);
                    } catch (Exception e) {
                        e = e;
                        View view = saveUiView;
                        int i = type3;
                        ArrayList<Pair<Integer, InternalTransformation>> arrayList = transformations3;
                    }
                }
                if (updates != null) {
                    try {
                        int size = updates.size();
                        if (Helper.sDebug) {
                            Slog.d(TAG, "custom description has " + size + " batch updates");
                        }
                        int i2 = 0;
                        while (i2 < size) {
                            Pair<InternalValidator, BatchUpdates> pair = updates.get(i2);
                            InternalValidator condition = (InternalValidator) pair.first;
                            if (condition == null) {
                                type2 = type3;
                                transformations2 = transformations3;
                            } else if (!condition.isValid(valueFinder2)) {
                                InternalValidator internalValidator = condition;
                                type2 = type3;
                                transformations2 = transformations3;
                            } else {
                                BatchUpdates batchUpdates = (BatchUpdates) pair.second;
                                InternalValidator internalValidator2 = condition;
                                RemoteViews templateUpdates = batchUpdates.getUpdates();
                                if (templateUpdates != null) {
                                    if (Helper.sDebug) {
                                        type = type3;
                                        try {
                                            StringBuilder sb = new StringBuilder();
                                            transformations = transformations3;
                                            try {
                                                sb.append("Applying template updates for batch update #");
                                                sb.append(i2);
                                                Slog.d(TAG, sb.toString());
                                            } catch (Exception e2) {
                                                e = e2;
                                                View view2 = saveUiView;
                                                Slog.e(TAG, "Error applying custom description. ", e);
                                                return false;
                                            }
                                        } catch (Exception e3) {
                                            e = e3;
                                            ArrayList<Pair<Integer, InternalTransformation>> arrayList2 = transformations3;
                                            View view3 = saveUiView;
                                            Slog.e(TAG, "Error applying custom description. ", e);
                                            return false;
                                        }
                                    } else {
                                        type = type3;
                                        transformations = transformations3;
                                    }
                                    templateUpdates.reapply(context2, customSubtitleView);
                                } else {
                                    type = type3;
                                    transformations = transformations3;
                                }
                                ArrayList<Pair<Integer, InternalTransformation>> batchTransformations = batchUpdates.getTransformations();
                                if (batchTransformations != null) {
                                    BatchUpdates batchUpdates2 = batchUpdates;
                                    if (Helper.sDebug) {
                                        StringBuilder sb2 = new StringBuilder();
                                        RemoteViews remoteViews = templateUpdates;
                                        sb2.append("Applying child transformation for batch update #");
                                        sb2.append(i2);
                                        sb2.append(": ");
                                        sb2.append(batchTransformations);
                                        Slog.d(TAG, sb2.toString());
                                    }
                                    if (!InternalTransformation.batchApply(valueFinder2, template, batchTransformations)) {
                                        Slog.w(TAG, "Could not apply child transformation for batch update #" + i2 + ": " + batchTransformations);
                                        return false;
                                    }
                                    template.reapply(context2, customSubtitleView);
                                } else {
                                    RemoteViews remoteViews2 = templateUpdates;
                                }
                                i2++;
                                type3 = type;
                                transformations3 = transformations;
                            }
                            if (Helper.sDebug) {
                                Slog.d(TAG, "Skipping batch update #" + i2);
                            }
                            i2++;
                            type3 = type;
                            transformations3 = transformations;
                        }
                        ArrayList<Pair<Integer, InternalTransformation>> arrayList3 = transformations3;
                    } catch (Exception e4) {
                        e = e4;
                        int i3 = type3;
                        ArrayList<Pair<Integer, InternalTransformation>> arrayList4 = transformations3;
                        View view4 = saveUiView;
                        Slog.e(TAG, "Error applying custom description. ", e);
                        return false;
                    }
                } else {
                    int i4 = type3;
                    ArrayList<Pair<Integer, InternalTransformation>> arrayList5 = transformations3;
                }
                SparseArray<InternalOnClickAction> actions2 = customDescription.getActions();
                if (actions2 != null) {
                    int size2 = actions2.size();
                    if (Helper.sDebug) {
                        Slog.d(TAG, "custom description has " + size2 + " actions");
                    }
                    if (!(customSubtitleView instanceof ViewGroup)) {
                        Slog.w(TAG, "cannot apply actions because custom description root is not a ViewGroup: " + customSubtitleView);
                        SparseArray<InternalOnClickAction> sparseArray = actions2;
                    } else {
                        ViewGroup rootView = (ViewGroup) customSubtitleView;
                        int i5 = 0;
                        while (i5 < size2) {
                            int id = actions2.keyAt(i5);
                            InternalOnClickAction action = actions2.valueAt(i5);
                            View child = rootView.findViewById(id);
                            if (child == null) {
                                StringBuilder sb3 = new StringBuilder();
                                actions = actions2;
                                sb3.append("Ignoring action ");
                                sb3.append(action);
                                sb3.append(" for view ");
                                sb3.append(id);
                                sb3.append(" because it's not on ");
                                sb3.append(rootView);
                                Slog.w(TAG, sb3.toString());
                            } else {
                                actions = actions2;
                                child.setOnClickListener(new View.OnClickListener(action, rootView) {
                                    private final /* synthetic */ InternalOnClickAction f$0;
                                    private final /* synthetic */ ViewGroup f$1;

                                    {
                                        this.f$0 = r1;
                                        this.f$1 = r2;
                                    }

                                    public final void onClick(View view) {
                                        SaveUi.lambda$applyCustomDescription$7(this.f$0, this.f$1, view);
                                    }
                                });
                            }
                            i5++;
                            actions2 = actions;
                        }
                    }
                }
                try {
                    ViewGroup subtitleContainer = (ViewGroup) saveUiView.findViewById(16908751);
                    subtitleContainer.addView(customSubtitleView);
                    subtitleContainer.setVisibility(0);
                    return true;
                } catch (Exception e5) {
                    e = e5;
                    Slog.e(TAG, "Error applying custom description. ", e);
                    return false;
                }
            } catch (Exception e6) {
                e = e6;
                View view5 = saveUiView;
                int i6 = type3;
                ArrayList<Pair<Integer, InternalTransformation>> arrayList6 = transformations3;
                Slog.e(TAG, "Error applying custom description. ", e);
                return false;
            }
        } else {
            Slog.w(TAG, "could not apply main transformations on custom description");
            return false;
        }
    }

    public /* synthetic */ boolean lambda$applyCustomDescription$6$SaveUi(int type, View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse response) {
        Intent intent = (Intent) response.getLaunchOptions(view).first;
        LogMaker log = newLogMaker(1132, type);
        if (!isValidLink(pendingIntent, intent)) {
            log.setType(0);
            this.mMetricsLogger.write(log);
            return false;
        }
        if (Helper.sVerbose) {
            Slog.v(TAG, "Intercepting custom description intent");
        }
        IBinder token = this.mPendingUi.getToken();
        intent.putExtra("android.view.autofill.extra.RESTORE_SESSION_TOKEN", token);
        try {
            this.mPendingUi.client.startIntentSender(pendingIntent.getIntentSender(), intent);
            this.mPendingUi.setState(2);
            if (Helper.sDebug) {
                Slog.d(TAG, "hiding UI until restored with token " + token);
            }
            hide();
            log.setType(1);
            this.mMetricsLogger.write(log);
            return true;
        } catch (RemoteException e) {
            Slog.w(TAG, "error triggering pending intent: " + intent);
            log.setType(11);
            this.mMetricsLogger.write(log);
            return false;
        }
    }

    static /* synthetic */ void lambda$applyCustomDescription$7(InternalOnClickAction action, ViewGroup rootView, View v) {
        if (Helper.sVerbose) {
            Slog.v(TAG, "Applying " + action + " after " + v + " was clicked");
        }
        action.onClick(rootView);
    }

    private void setServiceIcon(Context context, View view, Drawable serviceIcon) {
        ImageView iconView = (ImageView) view.findViewById(16908752);
        int maxWidth = context.getResources().getDimensionPixelSize(17104948);
        int maxHeight = maxWidth;
        int actualWidth = serviceIcon.getMinimumWidth();
        int actualHeight = serviceIcon.getMinimumHeight();
        if (actualWidth > maxWidth || actualHeight > maxHeight) {
            Slog.w(TAG, "Not adding service icon of size (" + actualWidth + "x" + actualHeight + ") because maximum is (" + maxWidth + "x" + maxHeight + ").");
            ((ViewGroup) iconView.getParent()).removeView(iconView);
            return;
        }
        if (Helper.sDebug) {
            Slog.d(TAG, "Adding service icon (" + actualWidth + "x" + actualHeight + ") as it's less than maximum (" + maxWidth + "x" + maxHeight + ").");
        }
        iconView.setImageDrawable(serviceIcon);
    }

    private static boolean isValidLink(PendingIntent pendingIntent, Intent intent) {
        if (pendingIntent == null) {
            Slog.w(TAG, "isValidLink(): custom description without pending intent");
            return false;
        } else if (!pendingIntent.isActivity()) {
            Slog.w(TAG, "isValidLink(): pending intent not for activity");
            return false;
        } else if (intent != null) {
            return true;
        } else {
            Slog.w(TAG, "isValidLink(): no intent");
            return false;
        }
    }

    private LogMaker newLogMaker(int category, int saveType) {
        return newLogMaker(category).addTaggedData(1130, Integer.valueOf(saveType));
    }

    private LogMaker newLogMaker(int category) {
        return Helper.newLogMaker(category, this.mComponentName, this.mServicePackageName, this.mPendingUi.sessionId, this.mCompatMode);
    }

    private void writeLog(int category, int saveType) {
        this.mMetricsLogger.write(newLogMaker(category, saveType));
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    public void onPendingUi(int operation, IBinder token) {
        if (!this.mPendingUi.matches(token)) {
            Slog.w(TAG, "restore(" + operation + "): got token " + token + " instead of " + this.mPendingUi.getToken());
            return;
        }
        LogMaker log = newLogMaker(1134);
        if (operation == 1) {
            log.setType(5);
            if (Helper.sDebug) {
                Slog.d(TAG, "Cancelling pending save dialog for " + token);
            }
            hide();
        } else if (operation != 2) {
            try {
                log.setType(11);
                Slog.w(TAG, "restore(): invalid operation " + operation);
            } catch (Throwable th) {
                this.mMetricsLogger.write(log);
                throw th;
            }
        } else {
            if (Helper.sDebug) {
                Slog.d(TAG, "Restoring save dialog for " + token);
            }
            log.setType(1);
            show();
        }
        this.mMetricsLogger.write(log);
        this.mPendingUi.setState(4);
    }

    private void show() {
        Slog.i(TAG, "Showing save dialog: " + this.mTitle);
        this.mDialog.show();
        this.mOverlayControl.hideOverlays();
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    public PendingUi hide() {
        if (Helper.sVerbose) {
            Slog.v(TAG, "Hiding save dialog.");
        }
        try {
            this.mDialog.hide();
            this.mOverlayControl.showOverlays();
            return this.mPendingUi;
        } catch (Throwable th) {
            this.mOverlayControl.showOverlays();
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public void destroy() {
        try {
            if (Helper.sDebug) {
                Slog.d(TAG, "destroy()");
            }
            throwIfDestroyed();
            this.mListener.onDestroy();
            this.mHandler.removeCallbacksAndMessages(this.mListener);
            this.mDialog.dismiss();
            this.mDestroyed = true;
        } finally {
            this.mOverlayControl.showOverlays();
        }
    }

    private void throwIfDestroyed() {
        if (this.mDestroyed) {
            throw new IllegalStateException("cannot interact with a destroyed instance");
        }
    }

    public String toString() {
        CharSequence charSequence = this.mTitle;
        return charSequence == null ? "NO TITLE" : charSequence.toString();
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("title: ");
        pw.println(this.mTitle);
        pw.print(prefix);
        pw.print("subtitle: ");
        pw.println(this.mSubTitle);
        pw.print(prefix);
        pw.print("pendingUi: ");
        pw.println(this.mPendingUi);
        pw.print(prefix);
        pw.print("service: ");
        pw.println(this.mServicePackageName);
        pw.print(prefix);
        pw.print("app: ");
        pw.println(this.mComponentName.toShortString());
        pw.print(prefix);
        pw.print("compat mode: ");
        pw.println(this.mCompatMode);
        pw.print(prefix);
        pw.print("theme id: ");
        pw.print(this.mThemeId);
        int i = this.mThemeId;
        if (i == THEME_ID_DARK) {
            pw.println(" (dark)");
        } else if (i != THEME_ID_LIGHT) {
            pw.println("(UNKNOWN_MODE)");
        } else {
            pw.println(" (light)");
        }
        View view = this.mDialog.getWindow().getDecorView();
        int[] loc = view.getLocationOnScreen();
        pw.print(prefix);
        pw.print("coordinates: ");
        pw.print('(');
        pw.print(loc[0]);
        pw.print(',');
        pw.print(loc[1]);
        pw.print(')');
        pw.print('(');
        pw.print(loc[0] + view.getWidth());
        pw.print(',');
        pw.print(loc[1] + view.getHeight());
        pw.println(')');
        pw.print(prefix);
        pw.print("destroyed: ");
        pw.println(this.mDestroyed);
    }
}

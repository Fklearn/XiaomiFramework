package com.android.server.policy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Slog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.policy.PhoneWindow;
import com.android.server.pm.PackageManagerService;
import com.miui.internal.variable.Android_View_Window_class;
import miui.R;
import miui.os.Environment;
import miui.os.SystemProperties;
import miui.util.AttributeResolver;
import miui.util.DrawableUtil;

class PhoneWindowManagerInjector {
    static final String TAG = "starting_window";
    private static boolean sEnableSW = SystemProperties.getBoolean("persist.startingwindow.enable", false);

    PhoneWindowManagerInjector() {
    }

    static void setDefaultBackgroundDrawable(PhoneWindow win) {
        if (sEnableSW) {
            TypedArray a = win.getWindowStyle();
            int windowBackgroundId = a.getResourceId(1, 0);
            boolean windowIsTranslucent = a.getBoolean(5, false);
            boolean windowDisableStarting = a.getBoolean(12, false);
            if (windowBackgroundId == 0 || windowIsTranslucent || windowDisableStarting) {
                win.setBackgroundDrawable(new ColorDrawable(-1));
                Slog.d(TAG, "add default startingwindow");
            }
        }
    }

    static void performReleaseHapticFeedback(PhoneWindowManager manager, KeyEvent event, int policyFlags) {
        if (event.getAction() != 0) {
        }
    }

    static void addStartingWindow(Context context, View view, PhoneWindow win, CharSequence label) {
        int i;
        Context context2 = context;
        View view2 = view;
        PhoneWindow phoneWindow = win;
        CharSequence charSequence = label;
        if (Environment.isUsingMiui(context)) {
            int translucentStatus = AttributeResolver.resolveInt(context2, R.attr.windowTranslucentStatus, 0);
            int globalTranslucentStatus = context.getResources().getInteger(com.miui.internal.R.integer.window_translucent_status);
            if (globalTranslucentStatus >= 0 && globalTranslucentStatus <= 2) {
                translucentStatus = globalTranslucentStatus;
            }
            Android_View_Window_class windowWrapper = Android_View_Window_class.Factory.getInstance().get();
            windowWrapper.setTranslucentStatus(phoneWindow, translucentStatus);
            if (AttributeResolver.resolveBoolean(context2, R.attr.windowActionBar, true)) {
                int overlayRes = AttributeResolver.resolve(context2, R.attr.startingWindowOverlay);
                if (overlayRes > 0) {
                    if (overlayRes == com.miui.internal.R.layout.starting_window_simple) {
                        phoneWindow.setContentView(android.miui.R.layout.starting_window_simple);
                        View statusBar = view2.findViewById(android.miui.R.id.status_bar);
                        if (statusBar != null) {
                            ViewGroup.LayoutParams lp = statusBar.getLayoutParams();
                            lp.height = context.getResources().getDimensionPixelSize(context.getResources().getIdentifier("status_bar_height", "dimen", PackageManagerService.PLATFORM_PACKAGE_NAME));
                            statusBar.setLayoutParams(lp);
                        }
                        TypedArray a = context2.obtainStyledAttributes((AttributeSet) null, R.styleable.ActionBar, 16843470, 0);
                        int titleStyleRes = a.getResourceId(R.styleable.ActionBar_android_titleTextStyle, 0);
                        boolean titleCenter = a.getBoolean(com.miui.internal.R.styleable.ActionBar_titleCenter, false);
                        int displayOptions = a.getInt(R.styleable.ActionBar_android_displayOptions, 0);
                        a.recycle();
                        View actionBar = view2.findViewById(android.miui.R.id.action_bar);
                        if (DrawableUtil.isPlaceholder(actionBar.getBackground())) {
                            actionBar.setBackground(new ColorDrawable(AttributeResolver.resolveColor(context2, R.attr.colorPrimary)));
                        }
                        View titleLayout = view2.findViewById(android.miui.R.id.title_layout);
                        TextView titleView = (TextView) titleLayout.findViewById(android.miui.R.id.action_bar_title);
                        ImageView upView = (ImageView) actionBar.findViewById(android.miui.R.id.up);
                        if ((displayOptions & 8) == 0) {
                            int i2 = translucentStatus;
                            titleLayout.setVisibility(4);
                            upView.setVisibility(4);
                            return;
                        }
                        FrameLayout.LayoutParams titleLP = (FrameLayout.LayoutParams) titleLayout.getLayoutParams();
                        if (!titleCenter || (displayOptions & 2) != 0) {
                            Android_View_Window_class android_View_Window_class = windowWrapper;
                            View view3 = statusBar;
                            i = 0;
                            titleLP.setMarginStart(upView.getMeasuredWidth());
                        } else {
                            int i3 = globalTranslucentStatus;
                            Android_View_Window_class android_View_Window_class2 = windowWrapper;
                            View view4 = statusBar;
                            i = 0;
                            titleLayout.setPadding(0, titleLayout.getPaddingTop(), 0, titleLayout.getPaddingBottom());
                            titleLP.gravity = 17;
                            titleLP.setMarginStart(0);
                        }
                        titleLayout.setLayoutParams(titleLP);
                        upView.setVisibility((displayOptions & 4) != 0 ? i : 8);
                        if (titleStyleRes != 0) {
                            titleView.setTextAppearance(context2, titleStyleRes);
                        }
                        if (charSequence != null) {
                            titleView.setText(charSequence);
                            return;
                        }
                        return;
                    }
                    phoneWindow.setContentView(overlayRes);
                    return;
                }
                return;
            }
            int i4 = translucentStatus;
            int i5 = globalTranslucentStatus;
            Android_View_Window_class android_View_Window_class3 = windowWrapper;
        }
    }
}

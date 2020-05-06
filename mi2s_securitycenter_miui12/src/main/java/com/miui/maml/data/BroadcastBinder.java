package com.miui.maml.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.VariableBinder;
import java.util.Iterator;
import org.w3c.dom.Element;

public class BroadcastBinder extends VariableBinder {
    private static final boolean DBG = true;
    private static final String LOG_TAG = "BroadcastBinder";
    public static final String TAG_NAME = "BroadcastBinder";
    private String mAction;
    private IntentFilter mIntentFilter;
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i("BroadcastBinder", "onNotify: " + BroadcastBinder.this.toString());
            BroadcastBinder.this.onNotify(context, intent, (Object) null);
        }
    };
    private boolean mRegistered;

    private static class Variable extends VariableBinder.Variable {
        public String mExtraName;

        public Variable(Element element, Variables variables) {
            super(element, variables);
            this.mExtraName = element.getAttribute("extra");
        }
    }

    public BroadcastBinder(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
    }

    private void load(Element element) {
        if (element != null) {
            this.mAction = element.getAttribute("action");
            if (!TextUtils.isEmpty(this.mAction)) {
                this.mIntentFilter = new IntentFilter(this.mAction);
                loadVariables(element);
                return;
            }
            Log.e("BroadcastBinder", "no action in broadcast binder");
            throw new IllegalArgumentException("no action in broadcast binder element");
        }
        Log.e("BroadcastBinder", "ContentProviderBinder node is null");
        throw new NullPointerException("node is null");
    }

    private void updateVariables(Intent intent) {
        String str;
        if (intent != null) {
            Log.d("BroadcastBinder", "updateVariables: " + intent);
            Iterator<VariableBinder.Variable> it = this.mVariables.iterator();
            while (it.hasNext()) {
                Variable variable = (Variable) it.next();
                double d2 = 0.0d;
                int i = variable.mType;
                if (i != 2) {
                    if (i == 3) {
                        d2 = (double) intent.getIntExtra(variable.mExtraName, (int) variable.mDefNumberValue);
                    } else if (i == 4) {
                        d2 = (double) intent.getLongExtra(variable.mExtraName, (long) variable.mDefNumberValue);
                    } else if (i == 5) {
                        d2 = (double) intent.getFloatExtra(variable.mExtraName, (float) variable.mDefNumberValue);
                    } else if (i != 6) {
                        Log.w("BroadcastBinder", "invalide type" + variable.mTypeStr);
                    } else {
                        d2 = intent.getDoubleExtra(variable.mExtraName, variable.mDefNumberValue);
                    }
                    variable.set(d2);
                    str = String.format("%f", new Object[]{Double.valueOf(d2)});
                } else {
                    str = intent.getStringExtra(variable.mExtraName);
                    variable.set((Object) str == null ? variable.mDefStringValue : str);
                }
                String format = String.format("name:%s type:%s value:%s", new Object[]{variable.mName, variable.mTypeStr, str});
                Log.d("BroadcastBinder", "updateVariables: " + format);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void addVariable(Variable variable) {
        this.mVariables.add(variable);
    }

    public void finish() {
        super.finish();
        unregister();
    }

    public void init() {
        super.init();
        register();
    }

    /* access modifiers changed from: protected */
    public Variable onLoadVariable(Element element) {
        return new Variable(element, getContext().mVariables);
    }

    /* access modifiers changed from: protected */
    public void onNotify(Context context, Intent intent, Object obj) {
        updateVariables(intent);
        onUpdateComplete();
    }

    /* access modifiers changed from: protected */
    public void onRegister() {
        updateVariables(getContext().mContext.registerReceiver(this.mIntentReceiver, this.mIntentFilter));
        onUpdateComplete();
    }

    /* access modifiers changed from: protected */
    public void onUnregister() {
        getContext().mContext.unregisterReceiver(this.mIntentReceiver);
    }

    /* access modifiers changed from: protected */
    public void register() {
        if (!this.mRegistered) {
            onRegister();
            this.mRegistered = true;
        }
    }

    /* access modifiers changed from: protected */
    public void unregister() {
        if (this.mRegistered) {
            try {
                onUnregister();
            } catch (IllegalArgumentException unused) {
            }
            this.mRegistered = false;
        }
    }
}

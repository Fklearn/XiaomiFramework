package com.miui.maml.data;

import android.content.ContentResolver;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.VariableBinder;
import java.util.Iterator;
import org.w3c.dom.Element;

public class SettingsBinder extends VariableBinder {
    public static final String TAG_NAME = "SettingsBinder";
    private boolean mConst;
    /* access modifiers changed from: private */
    public ContentResolver mContentResolver = this.mRoot.getContext().mContext.getContentResolver();

    /* renamed from: com.miui.maml.data.SettingsBinder$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$SettingsBinder$Category = new int[Category.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        static {
            /*
                com.miui.maml.data.SettingsBinder$Category[] r0 = com.miui.maml.data.SettingsBinder.Category.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$data$SettingsBinder$Category = r0
                int[] r0 = $SwitchMap$com$miui$maml$data$SettingsBinder$Category     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.data.SettingsBinder$Category r1 = com.miui.maml.data.SettingsBinder.Category.System     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$data$SettingsBinder$Category     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.data.SettingsBinder$Category r1 = com.miui.maml.data.SettingsBinder.Category.Secure     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.SettingsBinder.AnonymousClass1.<clinit>():void");
        }
    }

    private enum Category {
        Secure,
        System
    }

    private class Variable extends VariableBinder.Variable {
        public Category mCategory;
        public String mKey;

        public Variable(Element element, Variables variables) {
            super(element, variables);
            this.mCategory = "secure".equals(element.getAttribute("category")) ? Category.Secure : Category.System;
            this.mKey = element.getAttribute("key");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0066, code lost:
            if (r0 == null) goto L_0x0068;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0068, code lost:
            r0 = r7.mDefStringValue;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x006a, code lost:
            set((java.lang.Object) r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x00b7, code lost:
            if (r0 == null) goto L_0x0068;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void query() {
            /*
                r7 = this;
                int[] r0 = com.miui.maml.data.SettingsBinder.AnonymousClass1.$SwitchMap$com$miui$maml$data$SettingsBinder$Category
                com.miui.maml.data.SettingsBinder$Category r1 = r7.mCategory
                int r1 = r1.ordinal()
                r0 = r0[r1]
                r1 = 1
                r2 = 6
                r3 = 5
                r4 = 4
                r5 = 3
                r6 = 2
                if (r0 == r1) goto L_0x006e
                if (r0 == r6) goto L_0x0016
                goto L_0x00ba
            L_0x0016:
                int r0 = r7.mType
                if (r0 == r6) goto L_0x005a
                if (r0 == r5) goto L_0x0046
                if (r0 == r4) goto L_0x0035
                if (r0 == r3) goto L_0x0024
                if (r0 == r2) goto L_0x0024
                goto L_0x00ba
            L_0x0024:
                com.miui.maml.data.SettingsBinder r0 = com.miui.maml.data.SettingsBinder.this
                android.content.ContentResolver r0 = r0.mContentResolver
                java.lang.String r1 = r7.mKey
                double r2 = r7.mDefNumberValue
                float r2 = (float) r2
                float r0 = android.provider.Settings.Secure.getFloat(r0, r1, r2)
            L_0x0033:
                double r0 = (double) r0
                goto L_0x0056
            L_0x0035:
                com.miui.maml.data.SettingsBinder r0 = com.miui.maml.data.SettingsBinder.this
                android.content.ContentResolver r0 = r0.mContentResolver
                java.lang.String r1 = r7.mKey
                double r2 = r7.mDefNumberValue
                long r2 = (long) r2
                long r0 = android.provider.Settings.Secure.getLong(r0, r1, r2)
            L_0x0044:
                double r0 = (double) r0
                goto L_0x0056
            L_0x0046:
                com.miui.maml.data.SettingsBinder r0 = com.miui.maml.data.SettingsBinder.this
                android.content.ContentResolver r0 = r0.mContentResolver
                java.lang.String r1 = r7.mKey
                double r2 = r7.mDefNumberValue
                int r2 = (int) r2
                int r0 = android.provider.Settings.Secure.getInt(r0, r1, r2)
            L_0x0055:
                double r0 = (double) r0
            L_0x0056:
                r7.set((double) r0)
                goto L_0x00ba
            L_0x005a:
                com.miui.maml.data.SettingsBinder r0 = com.miui.maml.data.SettingsBinder.this
                android.content.ContentResolver r0 = r0.mContentResolver
                java.lang.String r1 = r7.mKey
                java.lang.String r0 = android.provider.Settings.Secure.getString(r0, r1)
                if (r0 != 0) goto L_0x006a
            L_0x0068:
                java.lang.String r0 = r7.mDefStringValue
            L_0x006a:
                r7.set((java.lang.Object) r0)
                goto L_0x00ba
            L_0x006e:
                int r0 = r7.mType
                if (r0 == r6) goto L_0x00ab
                if (r0 == r5) goto L_0x009b
                if (r0 == r4) goto L_0x008b
                if (r0 == r3) goto L_0x007b
                if (r0 == r2) goto L_0x007b
                goto L_0x00ba
            L_0x007b:
                com.miui.maml.data.SettingsBinder r0 = com.miui.maml.data.SettingsBinder.this
                android.content.ContentResolver r0 = r0.mContentResolver
                java.lang.String r1 = r7.mKey
                double r2 = r7.mDefNumberValue
                float r2 = (float) r2
                float r0 = android.provider.Settings.System.getFloat(r0, r1, r2)
                goto L_0x0033
            L_0x008b:
                com.miui.maml.data.SettingsBinder r0 = com.miui.maml.data.SettingsBinder.this
                android.content.ContentResolver r0 = r0.mContentResolver
                java.lang.String r1 = r7.mKey
                double r2 = r7.mDefNumberValue
                long r2 = (long) r2
                long r0 = android.provider.Settings.System.getLong(r0, r1, r2)
                goto L_0x0044
            L_0x009b:
                com.miui.maml.data.SettingsBinder r0 = com.miui.maml.data.SettingsBinder.this
                android.content.ContentResolver r0 = r0.mContentResolver
                java.lang.String r1 = r7.mKey
                double r2 = r7.mDefNumberValue
                int r2 = (int) r2
                int r0 = android.provider.Settings.System.getInt(r0, r1, r2)
                goto L_0x0055
            L_0x00ab:
                com.miui.maml.data.SettingsBinder r0 = com.miui.maml.data.SettingsBinder.this
                android.content.ContentResolver r0 = r0.mContentResolver
                java.lang.String r1 = r7.mKey
                java.lang.String r0 = android.provider.Settings.System.getString(r0, r1)
                if (r0 != 0) goto L_0x006a
                goto L_0x0068
            L_0x00ba:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.SettingsBinder.Variable.query():void");
        }
    }

    public SettingsBinder(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        if (element != null) {
            loadVariables(element);
            this.mConst = !"false".equalsIgnoreCase(element.getAttribute("const"));
        }
    }

    /* access modifiers changed from: protected */
    public Variable onLoadVariable(Element element) {
        return new Variable(element, getContext().mVariables);
    }

    public void refresh() {
        super.refresh();
        startQuery();
    }

    public void resume() {
        super.resume();
        if (!this.mConst) {
            startQuery();
        }
    }

    public void startQuery() {
        Iterator<VariableBinder.Variable> it = this.mVariables.iterator();
        while (it.hasNext()) {
            ((Variable) it.next()).query();
        }
        onUpdateComplete();
    }
}

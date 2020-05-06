package com.miui.antispam.ui.activity;

import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import com.miui.antispam.ui.view.AntiSpamEditorTitleView;

public class AddPhoneListActivity extends r {
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public EditText f2510d;
    /* access modifiers changed from: private */
    public CheckBox e;
    /* access modifiers changed from: private */
    public CheckBox f;
    /* access modifiers changed from: private */
    public AntiSpamEditorTitleView g;
    /* access modifiers changed from: private */
    public long h;
    private int i;
    /* access modifiers changed from: private */
    public int j;
    /* access modifiers changed from: private */
    public String k;
    private String l;
    /* access modifiers changed from: private */
    public int m;
    /* access modifiers changed from: private */
    public boolean n;
    private boolean o = false;
    /* access modifiers changed from: private */
    public boolean p = false;
    /* access modifiers changed from: private */
    public InputMethodManager q;

    /* access modifiers changed from: private */
    public boolean c() {
        return this.e.isChecked() || this.f.isChecked();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01fd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r13) {
        /*
            r12 = this;
            super.onCreate(r13)
            r13 = 2131493078(0x7f0c00d6, float:1.8609626E38)
            r12.setContentView(r13)
            android.content.Intent r13 = r12.getIntent()
            java.lang.String r0 = "input_method"
            java.lang.Object r0 = r12.getSystemService(r0)
            android.view.inputmethod.InputMethodManager r0 = (android.view.inputmethod.InputMethodManager) r0
            r12.q = r0
            r0 = -1
            java.lang.String r2 = "id_edit_blacklist"
            long r2 = r13.getLongExtra(r2, r0)
            r12.h = r2
            java.lang.String r2 = com.miui.antispam.ui.activity.AddAntiSpamActivity.g
            r3 = 1
            int r2 = r13.getIntExtra(r2, r3)
            r12.m = r2
            java.lang.String r2 = "is_black"
            boolean r2 = r13.getBooleanExtra(r2, r3)
            long r4 = r12.h
            int r4 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            r5 = 0
            if (r4 == 0) goto L_0x0076
            java.lang.String r4 = "number_edit_blacklist"
            java.lang.String r4 = r13.getStringExtra(r4)
            r12.k = r4
            java.lang.String r4 = "state_edit_blacklist"
            int r4 = r13.getIntExtra(r4, r5)
            r12.i = r4
            java.lang.String r4 = "sync_edit_blacklist"
            int r4 = r13.getIntExtra(r4, r5)
            r12.j = r4
            java.lang.String r4 = "note_edit_blacklist"
            java.lang.String r13 = r13.getStringExtra(r4)
            r12.l = r13
            java.lang.String r13 = r12.k
            int r4 = r13.length()
            int r4 = r4 - r3
            char r13 = r13.charAt(r4)
            r4 = 42
            if (r13 != r4) goto L_0x0069
            r12.n = r3
            goto L_0x007e
        L_0x0069:
            java.lang.String r13 = r12.k
            java.lang.String r4 = "***"
            int r13 = r13.indexOf(r4)
            if (r13 != 0) goto L_0x007e
            r12.o = r3
            goto L_0x007e
        L_0x0076:
            java.lang.String r4 = com.miui.antispam.ui.activity.AddAntiSpamActivity.h
            boolean r13 = r13.getBooleanExtra(r4, r5)
            r12.n = r13
        L_0x007e:
            android.view.LayoutInflater r13 = r12.getLayoutInflater()
            r4 = 2131492931(0x7f0c0043, float:1.8609328E38)
            r6 = 0
            android.view.View r13 = r13.inflate(r4, r6)
            com.miui.antispam.ui.view.AntiSpamEditorTitleView r13 = (com.miui.antispam.ui.view.AntiSpamEditorTitleView) r13
            r12.g = r13
            com.miui.antispam.ui.view.AntiSpamEditorTitleView r13 = r12.g
            android.widget.Button r13 = r13.getOk()
            long r6 = r12.h
            int r4 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r4 == 0) goto L_0x009c
            r4 = r3
            goto L_0x009d
        L_0x009c:
            r4 = r5
        L_0x009d:
            r13.setEnabled(r4)
            com.miui.antispam.ui.view.AntiSpamEditorTitleView r13 = r12.g
            android.widget.Button r13 = r13.getOk()
            com.miui.antispam.ui.activity.d r4 = new com.miui.antispam.ui.activity.d
            r4.<init>(r12, r2)
            r13.setOnClickListener(r4)
            com.miui.antispam.ui.view.AntiSpamEditorTitleView r13 = r12.g
            android.widget.Button r13 = r13.getCancel()
            com.miui.antispam.ui.activity.e r4 = new com.miui.antispam.ui.activity.e
            r4.<init>(r12)
            r13.setOnClickListener(r4)
            com.miui.antispam.ui.view.AntiSpamEditorTitleView r13 = r12.g
            android.widget.TextView r13 = r13.getTitle()
            if (r2 == 0) goto L_0x00d2
            long r6 = r12.h
            int r4 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r4 != 0) goto L_0x00ce
            r4 = 2131757995(0x7f100bab, float:1.9146941E38)
            goto L_0x00df
        L_0x00ce:
            r4 = 2131758010(0x7f100bba, float:1.9146972E38)
            goto L_0x00df
        L_0x00d2:
            long r6 = r12.h
            int r4 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r4 != 0) goto L_0x00dc
            r4 = 2131757996(0x7f100bac, float:1.9146944E38)
            goto L_0x00df
        L_0x00dc:
            r4 = 2131758011(0x7f100bbb, float:1.9146974E38)
        L_0x00df:
            r13.setText(r4)
            miui.app.ActionBar r13 = r12.getActionBar()
            r4 = 16
            r6 = 29
            r13.setDisplayOptions(r4, r6)
            com.miui.antispam.ui.view.AntiSpamEditorTitleView r4 = r12.g
            android.app.ActionBar$LayoutParams r6 = new android.app.ActionBar$LayoutParams
            r7 = -1
            r8 = -2
            r6.<init>(r7, r8)
            r13.setCustomView(r4, r6)
            r13 = 2131297160(0x7f090388, float:1.8212257E38)
            android.view.View r13 = r12.findViewById(r13)
            android.widget.EditText r13 = (android.widget.EditText) r13
            r12.f2510d = r13
            android.widget.EditText r13 = r12.f2510d
            boolean r4 = r12.n
            if (r4 == 0) goto L_0x010e
            r4 = 2131757969(0x7f100b91, float:1.9146889E38)
            goto L_0x0111
        L_0x010e:
            r4 = 2131757970(0x7f100b92, float:1.914689E38)
        L_0x0111:
            r13.setHint(r4)
            boolean r13 = r12.n
            if (r13 == 0) goto L_0x0123
            android.widget.EditText r13 = r12.f2510d
            java.lang.String r4 = "+0123456789"
            android.text.method.DigitsKeyListener r4 = android.text.method.DigitsKeyListener.getInstance(r4)
            r13.setKeyListener(r4)
        L_0x0123:
            android.widget.EditText r13 = r12.f2510d
            com.miui.antispam.ui.activity.f r4 = new com.miui.antispam.ui.activity.f
            r4.<init>(r12)
            r13.addTextChangedListener(r4)
            r13 = 2131297357(0x7f09044d, float:1.8212657E38)
            android.view.View r13 = r12.findViewById(r13)
            android.widget.CheckBox r13 = (android.widget.CheckBox) r13
            r12.e = r13
            android.widget.CheckBox r13 = r12.e
            r13.setChecked(r3)
            android.widget.CheckBox r13 = r12.e
            if (r2 == 0) goto L_0x0145
            r4 = 2131757972(0x7f100b94, float:1.9146895E38)
            goto L_0x0148
        L_0x0145:
            r4 = 2131757973(0x7f100b95, float:1.9146897E38)
        L_0x0148:
            r13.setText(r4)
            android.widget.CheckBox r13 = r12.e
            com.miui.antispam.ui.activity.g r4 = new com.miui.antispam.ui.activity.g
            r4.<init>(r12)
            r13.setOnCheckedChangeListener(r4)
            r13 = 2131296576(0x7f090140, float:1.8211073E38)
            android.view.View r13 = r12.findViewById(r13)
            android.widget.CheckBox r13 = (android.widget.CheckBox) r13
            r12.f = r13
            android.widget.CheckBox r13 = r12.f
            r13.setChecked(r3)
            android.widget.CheckBox r13 = r12.f
            if (r2 == 0) goto L_0x016d
            r4 = 2131757974(0x7f100b96, float:1.9146899E38)
            goto L_0x0170
        L_0x016d:
            r4 = 2131757975(0x7f100b97, float:1.91469E38)
        L_0x0170:
            r13.setText(r4)
            android.widget.CheckBox r13 = r12.f
            com.miui.antispam.ui.activity.h r4 = new com.miui.antispam.ui.activity.h
            r4.<init>(r12)
            r13.setOnCheckedChangeListener(r4)
            boolean r13 = b.b.c.j.i.f()
            r4 = 8
            if (r13 == 0) goto L_0x018f
            android.widget.CheckBox r13 = r12.e
            r13.setVisibility(r4)
            android.widget.CheckBox r13 = r12.f
            r13.setVisibility(r4)
        L_0x018f:
            r13 = 2131297844(0x7f090634, float:1.8213644E38)
            android.view.View r13 = r12.findViewById(r13)
            android.widget.TextView r13 = (android.widget.TextView) r13
            r6 = 2131296965(0x7f0902c5, float:1.8211862E38)
            android.view.View r6 = r12.findViewById(r6)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r7 = 2131296608(0x7f090160, float:1.8211137E38)
            android.view.View r7 = r12.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            if (r2 == 0) goto L_0x01c0
            boolean r8 = r12.o
            if (r8 == 0) goto L_0x01b4
            r8 = 2131758016(0x7f100bc0, float:1.9146984E38)
            goto L_0x01d3
        L_0x01b4:
            boolean r8 = r12.n
            if (r8 == 0) goto L_0x01bc
            r8 = 2131758018(0x7f100bc2, float:1.9146988E38)
            goto L_0x01d3
        L_0x01bc:
            r8 = 2131758014(0x7f100bbe, float:1.914698E38)
            goto L_0x01d3
        L_0x01c0:
            boolean r8 = r12.o
            if (r8 == 0) goto L_0x01c8
            r8 = 2131758017(0x7f100bc1, float:1.9146986E38)
            goto L_0x01d3
        L_0x01c8:
            boolean r8 = r12.n
            if (r8 == 0) goto L_0x01d0
            r8 = 2131758019(0x7f100bc3, float:1.914699E38)
            goto L_0x01d3
        L_0x01d0:
            r8 = 2131758015(0x7f100bbf, float:1.9146982E38)
        L_0x01d3:
            java.lang.String r9 = ""
            if (r2 == 0) goto L_0x01df
            boolean r2 = r12.n
            if (r2 == 0) goto L_0x01eb
            r2 = 2131755677(0x7f10029d, float:1.914224E38)
            goto L_0x01e6
        L_0x01df:
            boolean r2 = r12.n
            if (r2 == 0) goto L_0x01eb
            r2 = 2131758593(0x7f100e01, float:1.9148154E38)
        L_0x01e6:
            java.lang.String r2 = r12.getString(r2)
            goto L_0x01ec
        L_0x01eb:
            r2 = r9
        L_0x01ec:
            r13.setText(r8)
            r6.setText(r2)
            java.lang.String r13 = r12.l
            r7.setText(r13)
            long r10 = r12.h
            int r13 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r13 == 0) goto L_0x023a
            int r13 = r12.i
            if (r13 != r3) goto L_0x0206
            android.widget.CheckBox r13 = r12.f
            r13.setChecked(r5)
        L_0x0206:
            int r13 = r12.i
            r0 = 2
            if (r13 != r0) goto L_0x0210
            android.widget.CheckBox r13 = r12.e
            r13.setChecked(r5)
        L_0x0210:
            boolean r13 = r12.o
            if (r13 == 0) goto L_0x0224
            android.widget.EditText r13 = r12.f2510d
            java.lang.String r0 = r12.k
            r13.setText(r0)
            android.widget.EditText r13 = r12.f2510d
            r13.setVisibility(r4)
            r7.setVisibility(r5)
            goto L_0x023a
        L_0x0224:
            java.lang.String r13 = r12.k
            java.lang.String r0 = "*"
            java.lang.String r13 = r13.replace(r0, r9)
            android.widget.EditText r0 = r12.f2510d
            r0.setText(r13)
            android.widget.EditText r0 = r12.f2510d
            int r13 = r13.length()
            r0.setSelection(r13)
        L_0x023a:
            android.os.Handler r13 = new android.os.Handler
            r13.<init>()
            com.miui.antispam.ui.activity.i r0 = new com.miui.antispam.ui.activity.i
            r0.<init>(r12)
            r1 = 200(0xc8, double:9.9E-322)
            r13.postDelayed(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.ui.activity.AddPhoneListActivity.onCreate(android.os.Bundle):void");
    }
}

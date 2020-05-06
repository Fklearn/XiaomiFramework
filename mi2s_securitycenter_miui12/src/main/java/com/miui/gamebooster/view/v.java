package com.miui.gamebooster.view;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import b.b.c.c.b.d;
import com.miui.securitycenter.R;

public class v extends d implements r {

    /* renamed from: a  reason: collision with root package name */
    private Fragment f5325a;

    public static v b(@NonNull Fragment fragment) {
        v vVar = new v();
        vVar.f5325a = fragment;
        return vVar;
    }

    public void a(Fragment fragment) {
        FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
        beginTransaction.setCustomAnimations(R.anim.gb_fragment_enter, R.anim.gb_fragment_exit, R.anim.gb_fragment_pop_enter, R.anim.gb_fragment_pop_exit);
        beginTransaction.replace(R.id.container, fragment);
        beginTransaction.addToBackStack((String) null);
        beginTransaction.commit();
    }

    /* access modifiers changed from: protected */
    public void initView() {
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.f5325a != null) {
            FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
            beginTransaction.replace(R.id.container, this.f5325a);
            beginTransaction.commit();
        }
    }

    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof q) {
            ((q) fragment).a(this);
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_stack;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void pop() {
        getChildFragmentManager().popBackStack();
    }
}

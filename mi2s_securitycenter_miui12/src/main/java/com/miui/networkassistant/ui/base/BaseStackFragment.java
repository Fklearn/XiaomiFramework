package com.miui.networkassistant.ui.base;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import b.b.c.c.b.d;
import b.b.c.c.b.h;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseStackFragment extends h {
    private static Map<String, BaseStackFragment> sCurrentStackMap;
    private String mActivityKey;
    private BaseStackFragment mLastFragment;

    private synchronized BaseStackFragment getCurrentFragment() {
        if (sCurrentStackMap == null) {
            return null;
        }
        return sCurrentStackMap.get(String.valueOf(this.mActivity.hashCode()));
    }

    private synchronized void setCurrentFragment(BaseStackFragment baseStackFragment) {
        if (sCurrentStackMap == null) {
            sCurrentStackMap = new HashMap();
        }
        if (baseStackFragment != null) {
            if (baseStackFragment.isAttatched()) {
                baseStackFragment.applyTitle();
            }
            BaseStackFragment baseStackFragment2 = sCurrentStackMap.get(this.mActivityKey);
            if (baseStackFragment2 != null) {
                this.mLastFragment = baseStackFragment2;
            }
            sCurrentStackMap.put(this.mActivityKey, baseStackFragment);
        } else {
            sCurrentStackMap.remove(this.mActivityKey);
        }
    }

    /* access modifiers changed from: protected */
    public void clearBackStack() {
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.setTransition(0);
        beginTransaction.remove(this.mLastFragment);
        BaseStackFragment baseStackFragment = this;
        while (baseStackFragment.mLastFragment != null) {
            beginTransaction.remove(baseStackFragment);
            baseStackFragment = baseStackFragment.mLastFragment;
        }
        beginTransaction.show(baseStackFragment);
        setCurrentFragment(baseStackFragment);
        beginTransaction.commit();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivityKey = String.valueOf(this.mActivity.hashCode());
        setCurrentFragment(this);
    }

    public void onDetach() {
        BaseStackFragment baseStackFragment;
        super.onDetach();
        BaseStackFragment baseStackFragment2 = this.mLastFragment;
        if (baseStackFragment2 == null) {
            baseStackFragment = null;
        } else if (baseStackFragment2 != null && isAttatched()) {
            try {
                FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
                beginTransaction.setTransition(4099);
                beginTransaction.show(this.mLastFragment);
                beginTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            baseStackFragment = this.mLastFragment;
        } else {
            return;
        }
        setCurrentFragment(baseStackFragment);
    }

    /* access modifiers changed from: protected */
    public Fragment switchToFragment(Class<? extends d> cls, Bundle bundle, boolean z) {
        return switchToFragment(cls.getName(), bundle, z);
    }

    /* access modifiers changed from: protected */
    public Fragment switchToFragment(String str, Bundle bundle, boolean z) {
        FragmentManager fragmentManager = getFragmentManager();
        BaseStackFragment currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
            beginTransaction.hide(currentFragment);
            beginTransaction.commit();
        }
        FragmentTransaction beginTransaction2 = fragmentManager.beginTransaction();
        beginTransaction2.setTransition(4099);
        Fragment instantiate = Fragment.instantiate(this.mActivity, str, bundle);
        if (bundle != null) {
            instantiate.setArguments(bundle);
        }
        beginTransaction2.add(16908290, instantiate, str);
        if (z) {
            beginTransaction2.addToBackStack(str);
        }
        beginTransaction2.commit();
        return instantiate;
    }
}

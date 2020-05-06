package b.b.c.c.b;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class h extends d {
    protected k mLoadingManager;

    public void hideLoadingView() {
        this.mLoadingManager.a(false);
        if (this.mView.getVisibility() != 0) {
            this.mView.setVisibility(0);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mLoadingManager = new k(this.mActivity);
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public void setAllowBackWhileLoading(boolean z) {
        this.mLoadingManager.b(z);
    }

    public void setLoadingText(int i) {
        this.mLoadingManager.a(i);
    }

    public void setLoadingText(CharSequence charSequence) {
        this.mLoadingManager.a(charSequence);
    }

    public void showLoadingView() {
        showLoadingView(false);
    }

    public void showLoadingView(boolean z) {
        if (z) {
            this.mView.setVisibility(4);
        }
        this.mLoadingManager.a();
    }
}

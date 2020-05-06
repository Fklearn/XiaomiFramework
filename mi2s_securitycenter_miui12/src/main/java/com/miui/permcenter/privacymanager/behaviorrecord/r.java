package com.miui.permcenter.privacymanager.behaviorrecord;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import com.miui.permcenter.privacymanager.a.a;
import com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity;
import java.util.List;

class r implements LoaderManager.LoaderCallbacks<List<a>> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyDetailActivity f6460a;

    r(PrivacyDetailActivity privacyDetailActivity) {
        this.f6460a = privacyDetailActivity;
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<List<a>> loader, List<a> list) {
        this.f6460a.v.setVisibility(8);
        this.f6460a.z();
    }

    public Loader onCreateLoader(int i, Bundle bundle) {
        PrivacyDetailActivity privacyDetailActivity = this.f6460a;
        PrivacyDetailActivity.j unused = privacyDetailActivity.X = new PrivacyDetailActivity.j(privacyDetailActivity);
        return this.f6460a.X;
    }

    public void onLoaderReset(Loader loader) {
    }
}

package com.miui.permcenter.privacymanager.behaviorrecord;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import com.miui.permcenter.privacymanager.a.a;
import com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity;
import java.util.List;

class h implements LoaderManager.LoaderCallbacks<List<a>> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppBehaviorRecordActivity f6446a;

    h(AppBehaviorRecordActivity appBehaviorRecordActivity) {
        this.f6446a = appBehaviorRecordActivity;
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<List<a>> loader, List<a> list) {
        this.f6446a.n.setVisibility(8);
        if (this.f6446a.u == 0) {
            AppBehaviorRecordActivity appBehaviorRecordActivity = this.f6446a;
            appBehaviorRecordActivity.a(appBehaviorRecordActivity.f6395d, this.f6446a.s[this.f6446a.u], false);
            return;
        }
        AppBehaviorRecordActivity appBehaviorRecordActivity2 = this.f6446a;
        new AppBehaviorRecordActivity.b(appBehaviorRecordActivity2, appBehaviorRecordActivity2.u).execute(new Void[0]);
    }

    public Loader onCreateLoader(int i, Bundle bundle) {
        AppBehaviorRecordActivity appBehaviorRecordActivity = this.f6446a;
        AppBehaviorRecordActivity.a unused = appBehaviorRecordActivity.f6394c = new AppBehaviorRecordActivity.a(appBehaviorRecordActivity);
        return this.f6446a.f6394c;
    }

    public void onLoaderReset(Loader loader) {
    }
}

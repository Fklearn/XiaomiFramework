package com.miui.securityscan;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import com.miui.securityscan.cards.n;
import com.miui.securityscan.scanner.ScoreManager;

class B implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7544a;

    B(L l) {
        this.f7544a = l;
    }

    public void run() {
        ScoreManager scoreManager;
        Uri uri;
        ContentResolver contentResolver;
        Activity activity = this.f7544a.getActivity();
        if (this.f7544a.a(activity) && (scoreManager = this.f7544a.l) != null) {
            if (scoreManager.t()) {
                contentResolver = activity.getContentResolver();
                uri = n.f7669d;
            } else {
                contentResolver = activity.getContentResolver();
                uri = n.f7668c;
            }
            contentResolver.notifyChange(uri, (ContentObserver) null);
        }
    }
}

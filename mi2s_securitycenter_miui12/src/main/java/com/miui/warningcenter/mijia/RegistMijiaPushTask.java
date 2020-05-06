package com.miui.warningcenter.mijia;

import android.content.Context;
import android.os.AsyncTask;
import com.miui.earthquakewarning.utils.MD5Util;
import com.miui.push.b;
import com.miui.securitycenter.Application;

public class RegistMijiaPushTask extends AsyncTask<String, Void, String> {
    private Context mContext = Application.d();
    private int mLoginStatus;
    private String mServer;
    private String mUserId;

    public RegistMijiaPushTask(String str, String str2, int i) {
        this.mUserId = str;
        this.mServer = str2;
        this.mLoginStatus = i;
    }

    /* access modifiers changed from: protected */
    public String doInBackground(String... strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("MJW_");
        int i = this.mLoginStatus;
        if (i == 0) {
            MijiaUtils.setPreviousAccount(this.mUserId);
            MijiaUtils.setPreviousServer(this.mServer);
            sb.append(this.mServer);
            sb.append("_");
            sb.append(MD5Util.encode(this.mUserId + MijiaConstants.UUID_MIJIA_ACCOUNT_CODE));
            b.a(Application.d()).b(this.mContext, sb.toString(), (String) null);
        } else if (i == 1) {
            String previousAccount = MijiaUtils.getPreviousAccount();
            sb.append(MijiaUtils.getPreviousServer());
            sb.append("_");
            sb.append(MD5Util.encode(previousAccount + MijiaConstants.UUID_MIJIA_ACCOUNT_CODE));
            MijiaUtils.setPreviousAccount("");
            MijiaUtils.setPreviousServer("");
            b.a(Application.d()).d(this.mContext, sb.toString(), (String) null);
        }
        return "";
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(String str) {
    }
}

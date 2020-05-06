package com.android.server.wifi;

import android.content.Context;

public class NetworkListSharedStoreData extends NetworkListStoreData {
    public NetworkListSharedStoreData(Context context) {
        super(context);
    }

    public int getStoreFileId() {
        return 0;
    }
}

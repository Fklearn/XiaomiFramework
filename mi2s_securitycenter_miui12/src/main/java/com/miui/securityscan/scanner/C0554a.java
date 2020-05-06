package com.miui.securityscan.scanner;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.miui.common.card.models.AdvCardModel;
import com.miui.securityscan.C0534a;

/* renamed from: com.miui.securityscan.scanner.a  reason: case insensitive filesystem */
public class C0554a extends Handler {
    public void handleMessage(Message message) {
        super.handleMessage(message);
        try {
            C0534a.a("VIEW", (AdvCardModel) message.obj);
        } catch (Exception e) {
            Log.e("ScanAdHandler", "handle message error ", e);
        }
    }
}

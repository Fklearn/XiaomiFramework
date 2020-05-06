package com.xiaomi.micloudsdk.sync;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import java.util.concurrent.CountDownLatch;

class FileSyncLogSender$1 extends ResultReceiver {
    final /* synthetic */ a this$0;
    final /* synthetic */ CountDownLatch val$waiter;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    FileSyncLogSender$1(a aVar, Handler handler, CountDownLatch countDownLatch) {
        super(handler);
        this.this$0 = aVar;
        this.val$waiter = countDownLatch;
    }

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        this.val$waiter.countDown();
    }
}

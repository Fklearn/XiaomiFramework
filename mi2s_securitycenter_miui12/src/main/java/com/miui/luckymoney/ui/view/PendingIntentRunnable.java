package com.miui.luckymoney.ui.view;

import android.app.PendingIntent;

public class PendingIntentRunnable implements Runnable {
    private PendingIntent pendingIntent;

    public PendingIntentRunnable(PendingIntent pendingIntent2) {
        this.pendingIntent = pendingIntent2;
    }

    public void run() {
        PendingIntent pendingIntent2 = this.pendingIntent;
        if (pendingIntent2 != null) {
            try {
                pendingIntent2.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }
}

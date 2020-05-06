package com.android.server;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.storage.StorageManager;
import android.util.Log;
import android.util.Slog;
import java.io.IOException;

public class MasterClearReceiver extends BroadcastReceiver {
    private static final String TAG = "MasterClear";
    private boolean mWipeEsims;
    /* access modifiers changed from: private */
    public boolean mWipeExternalStorage;

    public void onReceive(final Context context, final Intent intent) {
        if (!"com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction()) || "google.com".equals(intent.getStringExtra("from"))) {
            if ("android.intent.action.MASTER_CLEAR".equals(intent.getAction())) {
                Slog.w(TAG, "The request uses the deprecated Intent#ACTION_MASTER_CLEAR, Intent#ACTION_FACTORY_RESET should be used instead.");
            }
            if (intent.hasExtra("android.intent.extra.FORCE_MASTER_CLEAR")) {
                Slog.w(TAG, "The request uses the deprecated Intent#EXTRA_FORCE_MASTER_CLEAR, Intent#EXTRA_FORCE_FACTORY_RESET should be used instead.");
            }
            boolean booleanExtra = intent.getBooleanExtra("shutdown", false);
            String stringExtra = intent.getStringExtra("android.intent.extra.REASON");
            this.mWipeExternalStorage = intent.getBooleanExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", false);
            this.mWipeEsims = intent.getBooleanExtra("com.android.internal.intent.extra.WIPE_ESIMS", false);
            if (intent.getBooleanExtra("android.intent.extra.FORCE_MASTER_CLEAR", false) || intent.getBooleanExtra("android.intent.extra.FORCE_FACTORY_RESET", false)) {
            }
            Slog.w(TAG, "!!! FACTORY RESET !!!");
            Thread thr = new Thread("Reboot") {
                public void run() {
                    try {
                        MasterClearReceiverInjector.rebootFactoryReset(context, intent.getBooleanExtra("format_sdcard", false));
                        Log.wtf(MasterClearReceiver.TAG, "Still running after master clear?!");
                    } catch (IOException e) {
                        Slog.e(MasterClearReceiver.TAG, "Can't perform master clear/factory reset", e);
                    } catch (SecurityException e2) {
                        Slog.e(MasterClearReceiver.TAG, "Can't perform master clear/factory reset", e2);
                    }
                }
            };
            if (this.mWipeExternalStorage || this.mWipeEsims) {
                new WipeDataTask(context, thr).execute(new Void[0]);
            } else {
                thr.start();
            }
        } else {
            Slog.w(TAG, "Ignoring master clear request -- not from trusted server.");
        }
    }

    private class WipeDataTask extends AsyncTask<Void, Void, Void> {
        private final Thread mChainedTask;
        private final Context mContext;
        private final ProgressDialog mProgressDialog;

        public WipeDataTask(Context context, Thread chainedTask) {
            this.mContext = context;
            this.mChainedTask = chainedTask;
            this.mProgressDialog = new ProgressDialog(context);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.mProgressDialog.setIndeterminate(true);
            this.mProgressDialog.getWindow().setType(2003);
            this.mProgressDialog.setMessage(this.mContext.getText(17040980));
            this.mProgressDialog.show();
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            Slog.w(MasterClearReceiver.TAG, "Wiping adoptable disks");
            if (!MasterClearReceiver.this.mWipeExternalStorage) {
                return null;
            }
            ((StorageManager) this.mContext.getSystemService("storage")).wipeAdoptableDisks();
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void result) {
            this.mProgressDialog.dismiss();
            this.mChainedTask.start();
        }
    }
}

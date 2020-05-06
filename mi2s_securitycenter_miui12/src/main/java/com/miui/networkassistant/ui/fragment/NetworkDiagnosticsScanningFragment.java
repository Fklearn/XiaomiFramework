package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import b.b.c.c.b.d;
import b.b.c.h.f;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.networkassistant.netdiagnose.NetworkDiagnoseItemFactory;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsCallback;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsManager;
import com.miui.networkassistant.ui.view.ScanningBar;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class NetworkDiagnosticsScanningFragment extends d {
    private static final String TAG = "NetworkDiagnostics_ScanningFragment";
    private static final int TASK_DONE = 1;
    private static final int TASK_INIT = 2;
    private static final int TASK_ITEM_SCANNING_RET = 3;
    private static final int TITLE_FILED = 2131756946;
    private static final int showItemInterMillion = 1000;
    private Button mBtnStoptDignoze = null;
    private DiagnoseMobileDataTask mDiagnoseMobileDataTask = null;
    private DiagnoseUsbShareTask mDiagnoseUsbShareTask = null;
    private DiagnoseWifiTask mDiagnoseWifiTask = null;
    /* access modifiers changed from: private */
    public UIHandler mHandler = new UIHandler(this);
    /* access modifiers changed from: private */
    public boolean mIsScanning;
    /* access modifiers changed from: private */
    public NetworkDiagnosticsManager mNDManager = null;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (view.getId() == R.id.stop_diagnostic_btn) {
                NetworkDiagnosticsScanningFragment.this.stopDiagnostic();
            }
        }
    };
    /* access modifiers changed from: private */
    public ScanningBar mScanningBar = null;

    private static class DiagnoseMobileDataTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<NetworkDiagnosticsScanningFragment> mFragmentRef;

        DiagnoseMobileDataTask(NetworkDiagnosticsScanningFragment networkDiagnosticsScanningFragment) {
            this.mFragmentRef = new WeakReference<>(networkDiagnosticsScanningFragment);
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voidArr) {
            NetworkDiagnosticsScanningFragment networkDiagnosticsScanningFragment = (NetworkDiagnosticsScanningFragment) this.mFragmentRef.get();
            if (networkDiagnosticsScanningFragment == null || !networkDiagnosticsScanningFragment.mNDManager.isMobileDataEnable()) {
                return false;
            }
            List<AbstractNetworkDiagoneItem> allMobileDataItem = NetworkDiagnoseItemFactory.getInstance(networkDiagnosticsScanningFragment.mAppContext).getAllMobileDataItem();
            try {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < allMobileDataItem.size(); i++) {
                    allMobileDataItem.get(i).reset();
                    arrayList.add(allMobileDataItem.get(i).getItemName());
                }
                Message obtainMessage = networkDiagnosticsScanningFragment.mHandler.obtainMessage();
                obtainMessage.what = 2;
                obtainMessage.obj = arrayList;
                networkDiagnosticsScanningFragment.mHandler.sendMessage(obtainMessage);
                for (int i2 = 0; i2 < allMobileDataItem.size(); i2++) {
                    allMobileDataItem.get(i2).check();
                    if (!allMobileDataItem.get(i2).getIsContinueDiagnose() && !DeviceUtil.isSmartDiagnostics(((NetworkDiagnosticsScanningFragment) this.mFragmentRef.get()).getActivity())) {
                        return null;
                    }
                    Message obtainMessage2 = networkDiagnosticsScanningFragment.mHandler.obtainMessage();
                    obtainMessage2.what = 3;
                    obtainMessage2.arg1 = i2;
                    obtainMessage2.arg2 = allMobileDataItem.size();
                    networkDiagnosticsScanningFragment.mHandler.sendMessage(obtainMessage2);
                    Thread.sleep(1000);
                }
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            NetworkDiagnosticsScanningFragment networkDiagnosticsScanningFragment = (NetworkDiagnosticsScanningFragment) this.mFragmentRef.get();
            if (networkDiagnosticsScanningFragment != null) {
                networkDiagnosticsScanningFragment.mHandler.sendEmptyMessage(1);
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
        }
    }

    private static class DiagnoseUsbShareTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<NetworkDiagnosticsScanningFragment> mFragmentRef;

        DiagnoseUsbShareTask(NetworkDiagnosticsScanningFragment networkDiagnosticsScanningFragment) {
            this.mFragmentRef = new WeakReference<>(networkDiagnosticsScanningFragment);
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voidArr) {
            NetworkDiagnosticsScanningFragment networkDiagnosticsScanningFragment = (NetworkDiagnosticsScanningFragment) this.mFragmentRef.get();
            if (networkDiagnosticsScanningFragment == null || !networkDiagnosticsScanningFragment.mNDManager.isInternetByUsbshareNetEnable()) {
                return false;
            }
            List<AbstractNetworkDiagoneItem> allUsbShareItem = NetworkDiagnoseItemFactory.getInstance(networkDiagnosticsScanningFragment.mAppContext).getAllUsbShareItem();
            try {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < allUsbShareItem.size(); i++) {
                    allUsbShareItem.get(i).reset();
                    arrayList.add(allUsbShareItem.get(i).getItemName());
                }
                Message obtainMessage = networkDiagnosticsScanningFragment.mHandler.obtainMessage();
                obtainMessage.what = 2;
                obtainMessage.obj = arrayList;
                networkDiagnosticsScanningFragment.mHandler.sendMessage(obtainMessage);
                for (int i2 = 0; i2 < allUsbShareItem.size(); i2++) {
                    allUsbShareItem.get(i2).check();
                    if (!allUsbShareItem.get(i2).getIsStatusNormal()) {
                        return null;
                    }
                    Thread.sleep(1000);
                    Message obtainMessage2 = networkDiagnosticsScanningFragment.mHandler.obtainMessage();
                    obtainMessage2.what = 3;
                    obtainMessage2.arg1 = i2;
                    obtainMessage2.arg2 = allUsbShareItem.size();
                    networkDiagnosticsScanningFragment.mHandler.sendMessage(obtainMessage2);
                }
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            NetworkDiagnosticsScanningFragment networkDiagnosticsScanningFragment = (NetworkDiagnosticsScanningFragment) this.mFragmentRef.get();
            if (networkDiagnosticsScanningFragment != null) {
                networkDiagnosticsScanningFragment.mHandler.sendEmptyMessage(1);
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
        }
    }

    private static class DiagnoseWifiTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<NetworkDiagnosticsScanningFragment> mFragmentRef;

        DiagnoseWifiTask(NetworkDiagnosticsScanningFragment networkDiagnosticsScanningFragment) {
            this.mFragmentRef = new WeakReference<>(networkDiagnosticsScanningFragment);
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voidArr) {
            NetworkDiagnosticsScanningFragment networkDiagnosticsScanningFragment = (NetworkDiagnosticsScanningFragment) this.mFragmentRef.get();
            if (networkDiagnosticsScanningFragment == null || !networkDiagnosticsScanningFragment.mNDManager.isWifiEnable()) {
                return false;
            }
            List<AbstractNetworkDiagoneItem> allWifiItem = NetworkDiagnoseItemFactory.getInstance(networkDiagnosticsScanningFragment.mAppContext).getAllWifiItem();
            try {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < allWifiItem.size(); i++) {
                    allWifiItem.get(i).reset();
                    arrayList.add(allWifiItem.get(i).getItemName());
                }
                Message obtainMessage = networkDiagnosticsScanningFragment.mHandler.obtainMessage();
                obtainMessage.what = 2;
                obtainMessage.obj = arrayList;
                networkDiagnosticsScanningFragment.mHandler.sendMessage(obtainMessage);
                for (int i2 = 0; i2 < allWifiItem.size(); i2++) {
                    allWifiItem.get(i2).check();
                    if (!allWifiItem.get(i2).getIsContinueDiagnose()) {
                        return null;
                    }
                    Thread.sleep(1000);
                    Message obtainMessage2 = networkDiagnosticsScanningFragment.mHandler.obtainMessage();
                    obtainMessage2.what = 3;
                    obtainMessage2.arg1 = i2;
                    obtainMessage2.arg2 = allWifiItem.size();
                    networkDiagnosticsScanningFragment.mHandler.sendMessage(obtainMessage2);
                }
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            NetworkDiagnosticsScanningFragment networkDiagnosticsScanningFragment = (NetworkDiagnosticsScanningFragment) this.mFragmentRef.get();
            if (networkDiagnosticsScanningFragment != null) {
                networkDiagnosticsScanningFragment.mHandler.sendEmptyMessage(1);
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
        }
    }

    private static class UIHandler extends Handler {
        private WeakReference<NetworkDiagnosticsScanningFragment> mFragmentRef;

        UIHandler(NetworkDiagnosticsScanningFragment networkDiagnosticsScanningFragment) {
            this.mFragmentRef = new WeakReference<>(networkDiagnosticsScanningFragment);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            NetworkDiagnosticsScanningFragment networkDiagnosticsScanningFragment = (NetworkDiagnosticsScanningFragment) this.mFragmentRef.get();
            if (networkDiagnosticsScanningFragment != null && networkDiagnosticsScanningFragment.mActivity != null) {
                int i = message.what;
                if (i == 1) {
                    if (NetworkDiagnosticsCallback.class.isInstance(networkDiagnosticsScanningFragment.mActivity)) {
                        NetworkDiagnosticsCallback networkDiagnosticsCallback = (NetworkDiagnosticsCallback) networkDiagnosticsScanningFragment.mActivity;
                        networkDiagnosticsCallback.onNetworkDiagnosticsDone(networkDiagnosticsScanningFragment.mNDManager.getCurNetworkState());
                        networkDiagnosticsCallback.onNetworkDiagnosticsProcessChanged(100);
                    }
                    networkDiagnosticsScanningFragment.showDiagnosticResult();
                } else if (i != 2) {
                    if (i == 3 && networkDiagnosticsScanningFragment.mIsScanning) {
                        networkDiagnosticsScanningFragment.mScanningBar.setScanningRet(message.arg1, true);
                        if (NetworkDiagnosticsCallback.class.isInstance(networkDiagnosticsScanningFragment.mActivity)) {
                            ((NetworkDiagnosticsCallback) networkDiagnosticsScanningFragment.mActivity).onNetworkDiagnosticsProcessChanged((int) (((((double) (message.arg1 + 1)) * 1.0d) / ((double) message.arg2)) * 100.0d));
                        }
                    }
                } else if (message.obj != null) {
                    networkDiagnosticsScanningFragment.mScanningBar.setScanningItems((List) message.obj);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void showDiagnosticResult() {
        DiagnoseMobileDataTask diagnoseMobileDataTask = this.mDiagnoseMobileDataTask;
        if (diagnoseMobileDataTask != null) {
            diagnoseMobileDataTask.cancel(true);
            this.mDiagnoseMobileDataTask = null;
        }
        DiagnoseUsbShareTask diagnoseUsbShareTask = this.mDiagnoseUsbShareTask;
        if (diagnoseUsbShareTask != null) {
            diagnoseUsbShareTask.cancel(true);
            this.mDiagnoseUsbShareTask = null;
        }
        DiagnoseWifiTask diagnoseWifiTask = this.mDiagnoseWifiTask;
        if (diagnoseWifiTask != null) {
            diagnoseWifiTask.cancel(true);
            this.mDiagnoseWifiTask = null;
        }
        if (isAttatched() && NetworkDiagnosticsCallback.class.isInstance(this.mActivity)) {
            ((NetworkDiagnosticsCallback) this.mActivity).switchView(R.id.network_diagnostics_result_zone, R.id.network_diagnostics_scanning_zone, true);
        }
    }

    private void startMobileDataDiagnostic() {
        this.mScanningBar.resetScanningBar();
        if (this.mDiagnoseMobileDataTask == null) {
            this.mDiagnoseMobileDataTask = new DiagnoseMobileDataTask(this);
            this.mDiagnoseMobileDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    private void startUsbShareDiagnostic() {
        this.mScanningBar.resetScanningBar();
        if (this.mDiagnoseUsbShareTask == null) {
            this.mDiagnoseUsbShareTask = new DiagnoseUsbShareTask(this);
            this.mDiagnoseUsbShareTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    private void startWifiDiagnostic() {
        this.mScanningBar.resetScanningBar();
        if (this.mDiagnoseWifiTask == null) {
            this.mDiagnoseWifiTask = new DiagnoseWifiTask(this);
            this.mDiagnoseWifiTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    /* access modifiers changed from: private */
    public void stopDiagnostic() {
        this.mIsScanning = false;
        DiagnoseMobileDataTask diagnoseMobileDataTask = this.mDiagnoseMobileDataTask;
        if (diagnoseMobileDataTask != null) {
            diagnoseMobileDataTask.cancel(true);
            this.mDiagnoseMobileDataTask = null;
        }
        DiagnoseUsbShareTask diagnoseUsbShareTask = this.mDiagnoseUsbShareTask;
        if (diagnoseUsbShareTask != null) {
            diagnoseUsbShareTask.cancel(true);
            this.mDiagnoseUsbShareTask = null;
        }
        DiagnoseWifiTask diagnoseWifiTask = this.mDiagnoseWifiTask;
        if (diagnoseWifiTask != null) {
            diagnoseWifiTask.cancel(true);
            this.mDiagnoseWifiTask = null;
        }
        if (isAttatched() && NetworkDiagnosticsCallback.class.isInstance(this.mActivity)) {
            ((NetworkDiagnosticsCallback) this.mActivity).switchView(R.id.network_diagnostics_network_speed_for_apps, R.id.network_diagnostics_scanning_zone, true);
        }
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.mNDManager = NetworkDiagnosticsManager.getInstance(getActivity());
        this.mBtnStoptDignoze = (Button) findViewById(R.id.stop_diagnostic_btn);
        this.mBtnStoptDignoze.setOnClickListener(this.mOnClickListener);
        this.mScanningBar = (ScanningBar) findViewById(R.id.scanning_bar);
        View view = getView();
        if (view != null) {
            view.setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.fragment_network_diagnostics_scanning;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        this.mHandler.removeCallbacksAndMessages((Object) null);
    }

    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.network_diagnostics;
    }

    public void startDiagnostic() {
        NetworkDiagnosticsManager networkDiagnosticsManager;
        String b2;
        this.mIsScanning = true;
        int activeNetworkType = this.mNDManager.getActiveNetworkType();
        this.mNDManager.setDiagnosingNetworkType(activeNetworkType);
        this.mNDManager.setDiagnosingNetworkInterface("null");
        if (activeNetworkType == 0) {
            startMobileDataDiagnostic();
            networkDiagnosticsManager = this.mNDManager;
            b2 = f.f(this.mAppContext);
        } else if (activeNetworkType == 1) {
            startWifiDiagnostic();
            networkDiagnosticsManager = this.mNDManager;
            b2 = f.g(getActivity());
        } else if (activeNetworkType == 9) {
            startUsbShareDiagnostic();
            networkDiagnosticsManager = this.mNDManager;
            b2 = f.b(this.mAppContext);
        } else if (activeNetworkType != -1) {
            return;
        } else {
            if (this.mNDManager.isWifiEnable() && !this.mNDManager.checkWlanConnected()) {
                startWifiDiagnostic();
                return;
            } else if (!this.mNDManager.isWifiEnable() && this.mNDManager.isMobileDataEnable()) {
                startMobileDataDiagnostic();
                return;
            } else {
                return;
            }
        }
        networkDiagnosticsManager.setDiagnosingNetworkInterface(b2);
    }
}

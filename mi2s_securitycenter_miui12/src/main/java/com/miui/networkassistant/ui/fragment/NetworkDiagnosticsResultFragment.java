package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import b.b.c.c.b.d;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsCallback;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsManager;
import com.miui.networkassistant.netdiagnose.item.ApnCheck;
import com.miui.networkassistant.netdiagnose.item.FixMobileCheck;
import com.miui.networkassistant.netdiagnose.item.TitleCheck;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity;
import com.miui.networkassistant.ui.view.ButtonToolbarItemView;
import com.miui.networkassistant.ui.view.TitleBarItemView;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.List;
import miui.app.ProgressDialog;

public class NetworkDiagnosticsResultFragment extends d {
    private static final String TAG = "NA_ND_ResultFragment";
    private List<AbstractNetworkDiagoneItem> mAllProblemItem;
    private View mBottomPanel;
    /* access modifiers changed from: private */
    public UiHandler mHandler;
    /* access modifiers changed from: private */
    public boolean mIsFixing = false;
    private ListView mIssuesListView;
    private NetworkDiagnosticsManager mNDManager;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (view.getId() == R.id.btn_finish_diagnostic) {
                NetworkDiagnosticsResultFragment.this.finishDiagnostic();
            }
        }
    };
    /* access modifiers changed from: private */
    public ProgressDialogFragment mProgressDialog;

    private class IssueListViewAdapter extends BaseAdapter {
        protected static final String TAG = "IssueListViewAdapter";
        private List<AbstractNetworkDiagoneItem> allItem;
        private Context context;

        IssueListViewAdapter(Context context2, List<AbstractNetworkDiagoneItem> list) {
            this.context = context2;
            this.allItem = list;
        }

        private ButtonToolbarItemView getButtonItem(final AbstractNetworkDiagoneItem abstractNetworkDiagoneItem) {
            final ButtonToolbarItemView buttonToolbarItemView = new ButtonToolbarItemView(this.context);
            buttonToolbarItemView.setSummaryText(abstractNetworkDiagoneItem.getItemSummary());
            buttonToolbarItemView.setTitleViewText(abstractNetworkDiagoneItem.getItemName());
            buttonToolbarItemView.setFixButtonText(abstractNetworkDiagoneItem.getItemSolution());
            if (TextUtils.isEmpty(abstractNetworkDiagoneItem.getItemSolution())) {
                buttonToolbarItemView.getFixButton().setVisibility(8);
            } else {
                buttonToolbarItemView.getFixButton().setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        ProgressDialogFragment unused = NetworkDiagnosticsResultFragment.this.mProgressDialog = ProgressDialogFragment.newInstance(abstractNetworkDiagoneItem.getFixingWaitProgressDlgMsg());
                        if (!NetworkDiagnosticsResultFragment.this.mIsFixing) {
                            boolean unused2 = NetworkDiagnosticsResultFragment.this.mIsFixing = true;
                            AbstractNetworkDiagoneItem abstractNetworkDiagoneItem = abstractNetworkDiagoneItem;
                            if ((abstractNetworkDiagoneItem instanceof FixMobileCheck) || (abstractNetworkDiagoneItem instanceof ApnCheck)) {
                                buttonToolbarItemView.getFixButton().setVisibility(8);
                                buttonToolbarItemView.getProgressBar().setVisibility(0);
                                buttonToolbarItemView.getImageView().setVisibility(8);
                            }
                            new Thread(new Runnable() {
                                public void run() {
                                    NetworkDiagnosticsResultFragment.this.mHandler.sendEmptyMessage(0);
                                    AbstractNetworkDiagoneItem.FixedResult fix = abstractNetworkDiagoneItem.fix();
                                    Message obtainMessage = NetworkDiagnosticsResultFragment.this.mHandler.obtainMessage();
                                    obtainMessage.what = 1;
                                    obtainMessage.arg1 = fix.ordinal();
                                    NetworkDiagnosticsResultFragment.this.mHandler.sendMessageDelayed(obtainMessage, 3000);
                                    NetworkDiagnosticsResultFragment.this.mHandler.post(new Runnable() {
                                        public void run() {
                                            AbstractNetworkDiagoneItem abstractNetworkDiagoneItem = abstractNetworkDiagoneItem;
                                            if ((abstractNetworkDiagoneItem instanceof FixMobileCheck) || (abstractNetworkDiagoneItem instanceof ApnCheck)) {
                                                buttonToolbarItemView.getProgressBar().setVisibility(8);
                                                buttonToolbarItemView.getImageView().setVisibility(0);
                                            }
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                });
            }
            return buttonToolbarItemView;
        }

        private TitleBarItemView getTitleItem(AbstractNetworkDiagoneItem abstractNetworkDiagoneItem) {
            TitleBarItemView titleBarItemView = new TitleBarItemView(this.context);
            titleBarItemView.setTitleViewText(abstractNetworkDiagoneItem.getItemName());
            return titleBarItemView;
        }

        public int getCount() {
            return this.allItem.size();
        }

        public Object getItem(int i) {
            return this.allItem.get(i);
        }

        public long getItemId(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            AbstractNetworkDiagoneItem abstractNetworkDiagoneItem = this.allItem.get(i);
            return abstractNetworkDiagoneItem instanceof TitleCheck ? getTitleItem(abstractNetworkDiagoneItem) : getButtonItem(abstractNetworkDiagoneItem);
        }
    }

    public static class ProgressDialogFragment extends DialogFragment {
        private static final String ARG_MESSAGE = "message";

        public static ProgressDialogFragment newInstance(String str) {
            ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ARG_MESSAGE, str);
            progressDialogFragment.setArguments(bundle);
            return progressDialogFragment;
        }

        public Dialog onCreateDialog(Bundle bundle) {
            setRetainInstance(true);
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            Bundle arguments = getArguments();
            if (arguments.containsKey(ARG_MESSAGE)) {
                progressDialog.setMessage(arguments.getString(ARG_MESSAGE));
            }
            return progressDialog;
        }

        public void onDestroyView() {
            if (getDialog() != null && getRetainInstance()) {
                getDialog().setDismissMessage((Message) null);
            }
            super.onDestroyView();
        }
    }

    private static class UiHandler extends Handler {
        static final int WHAT_NETWORK_DIAGNOSTICS_FIX_END = 1;
        static final int WHAT_NETWORK_DIAGNOSTICS_FIX_START = 0;
        private WeakReference<NetworkDiagnosticsResultFragment> fragmentRef;

        UiHandler(NetworkDiagnosticsResultFragment networkDiagnosticsResultFragment) {
            this.fragmentRef = new WeakReference<>(networkDiagnosticsResultFragment);
        }

        public void handleMessage(Message message) {
            NetworkDiagnosticsResultFragment networkDiagnosticsResultFragment = (NetworkDiagnosticsResultFragment) this.fragmentRef.get();
            if (networkDiagnosticsResultFragment != null) {
                super.handleMessage(message);
                try {
                    if (message.what == 1) {
                        boolean unused = networkDiagnosticsResultFragment.mIsFixing = false;
                        if (AbstractNetworkDiagoneItem.FixedResult.NETWORKCHANGED.ordinal() == message.arg1) {
                            Toast.makeText(networkDiagnosticsResultFragment.getActivity(), R.string.networkchanged_exception_summary, 1).show();
                            networkDiagnosticsResultFragment.finishDiagnostic();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addTitleView() {
        if (DeviceUtil.isSmartDiagnostics(this.mAppContext)) {
            int size = this.mAllProblemItem.size();
            this.mAllProblemItem.add(new TitleCheck(this.mAppContext, getString(R.string.nd_result_title_optimize)));
            this.mAllProblemItem.add(new FixMobileCheck(this.mAppContext));
            if (size != 1) {
                this.mAllProblemItem.add(1, new TitleCheck(this.mAppContext, getString(R.string.nd_result_title_auxiliary)));
            }
            this.mAllProblemItem.add(0, new TitleCheck(this.mAppContext, getString(R.string.nd_result_title_network)));
        }
    }

    /* access modifiers changed from: private */
    public void finishDiagnostic() {
        if (isAttatched()) {
            Activity activity = this.mActivity;
            if (activity instanceof NetworkDiagnosticsCallback) {
                ((NetworkDiagnosticsCallback) activity).switchView(R.id.network_diagnostics_network_speed_for_apps, R.id.network_diagnostics_result_zone, true);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x002b, code lost:
        if (com.miui.networkassistant.utils.DeviceUtil.isSmartDiagnostics(getActivity()) != false) goto L_0x0045;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem> getBrokenItemWithoutDuplicate() {
        /*
            r5 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsManager r1 = r5.mNDManager
            int r1 = r1.getActiveNetworkType()
            android.app.Activity r2 = r5.getActivity()
            com.miui.networkassistant.netdiagnose.NetworkDiagnoseItemFactory r2 = com.miui.networkassistant.netdiagnose.NetworkDiagnoseItemFactory.getInstance(r2)
            com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem r3 = r2.getNetworkChangedCheckItem()
            r3.check()
            boolean r4 = r3.getIsStatusNormal()
            if (r4 != 0) goto L_0x002e
            r0.add(r3)
            android.app.Activity r1 = r5.getActivity()
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.isSmartDiagnostics(r1)
            if (r1 == 0) goto L_0x007d
            goto L_0x0045
        L_0x002e:
            r3 = -1
            if (r1 == r3) goto L_0x004d
            if (r1 == 0) goto L_0x0045
            r3 = 1
            if (r1 == r3) goto L_0x0040
            r3 = 9
            if (r1 == r3) goto L_0x003b
            goto L_0x007d
        L_0x003b:
            java.util.List r1 = r2.getAllBrokenUsbShareItem()
            goto L_0x0049
        L_0x0040:
            java.util.List r1 = r2.getAllBrokenWifiItem()
            goto L_0x0049
        L_0x0045:
            java.util.List r1 = r2.getAllBrokenMobileDataItem()
        L_0x0049:
            r0.addAll(r1)
            goto L_0x007d
        L_0x004d:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "activeNetworkType ="
            r3.append(r4)
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            java.lang.String r3 = "NA_ND_ResultFragment"
            android.util.Log.d(r3, r1)
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsManager r1 = r5.mNDManager
            boolean r1 = r1.isWifiEnable()
            if (r1 == 0) goto L_0x0074
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsManager r1 = r5.mNDManager
            boolean r1 = r1.checkWlanConnected()
            if (r1 != 0) goto L_0x0074
            goto L_0x0040
        L_0x0074:
            r0.clear()
            java.lang.String r1 = "clear all broken check items"
            android.util.Log.d(r3, r1)
            goto L_0x0045
        L_0x007d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.fragment.NetworkDiagnosticsResultFragment.getBrokenItemWithoutDuplicate():java.util.List");
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.mHandler = new UiHandler(this);
        ((Button) findViewById(R.id.btn_finish_diagnostic)).setOnClickListener(this.mOnClickListener);
        this.mIssuesListView = (ListView) findViewById(R.id.list_issues);
        this.mBottomPanel = findViewById(R.id.bottom_panel);
        this.mNDManager = NetworkDiagnosticsManager.getInstance(getActivity());
        View view = getView();
        if (view != null) {
            view.setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.fragment_network_diagnostics_result;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        this.mHandler.removeCallbacksAndMessages((Object) null);
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        ProgressDialogFragment progressDialogFragment = this.mProgressDialog;
        if (!(progressDialogFragment == null || progressDialogFragment.getFragmentManager() == null)) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
        Activity activity = getActivity();
        if ((activity instanceof NetworkDiagnosticsActivity) && ((NetworkDiagnosticsActivity) activity).isFromSettings()) {
            finishDiagnostic();
        }
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.network_diagnostics;
    }

    public void showResult() {
        List<AbstractNetworkDiagoneItem> list = this.mAllProblemItem;
        if (list != null) {
            list.clear();
        }
        this.mAllProblemItem = getBrokenItemWithoutDuplicate();
        int i = 0;
        this.mBottomPanel.setVisibility(0);
        while (true) {
            if (i >= this.mAllProblemItem.size()) {
                break;
            } else if (!TextUtils.isEmpty(this.mAllProblemItem.get(i).getItemSolution())) {
                this.mBottomPanel.setVisibility(8);
                break;
            } else {
                i++;
            }
        }
        addTitleView();
        this.mIssuesListView.setAdapter(new IssueListViewAdapter(getActivity(), this.mAllProblemItem));
    }
}

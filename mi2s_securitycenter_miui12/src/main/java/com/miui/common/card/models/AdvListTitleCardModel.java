package com.miui.common.card.models;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import b.b.c.j.r;
import b.c.a.b.d;
import com.miui.applicationlock.c.y;
import com.miui.common.card.BaseViewHolder;
import com.miui.securitycenter.R;
import com.miui.securitycenter.p;
import com.miui.securityscan.C0534a;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import java.util.ArrayList;
import java.util.List;

public class AdvListTitleCardModel extends TitleCardModel {
    private static final String TAG = "AdvListTitleCardModel";
    /* access modifiers changed from: private */
    public int usePosition;

    public static class AdvListTitleViewHolder extends BaseViewHolder {
        private View closeView;
        d option = r.g;

        public AdvListTitleViewHolder(View view) {
            super(view);
            initView(view);
        }

        private void initView(View view) {
            this.closeView = view.findViewById(R.id.view_close);
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            super.fillData(view, baseCardModel, i);
            final AdvListTitleCardModel advListTitleCardModel = (AdvListTitleCardModel) baseCardModel;
            AnonymousClass1 r1 = new View.OnClickListener() {
                public void onClick(View view) {
                    advListTitleCardModel.onClick(view);
                }
            };
            View view2 = this.closeView;
            if (view2 != null) {
                view2.setOnClickListener(r1);
            }
        }

        /* access modifiers changed from: protected */
        public void setIconDisplayOption(d dVar) {
            this.option = dVar;
        }
    }

    public AdvListTitleCardModel(int i) {
        super(R.layout.card_layout_adv_list_title);
        this.usePosition = i;
    }

    private void closeNormalAd(final C0534a aVar, View view) {
        View inflate = aVar.getLayoutInflater().inflate(R.layout.result_unlike_pop_window, (ViewGroup) null);
        Resources resources = aVar.getResources();
        int i = resources.getDisplayMetrics().widthPixels;
        final PopupWindow popupWindow = new PopupWindow(inflate, -2, -2);
        inflate.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(10, Integer.MIN_VALUE));
        inflate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                popupWindow.dismiss();
                List<BaseCardModel> subCardModelList = AdvListTitleCardModel.this.getSubCardModelList();
                if (subCardModelList != null && subCardModelList.size() > 0) {
                    C0534a aVar = aVar;
                    AdvListTitleCardModel advListTitleCardModel = AdvListTitleCardModel.this;
                    aVar.a((BaseCardModel) advListTitleCardModel, subCardModelList, advListTitleCardModel.usePosition);
                }
            }
        });
        int measuredWidth = inflate.getMeasuredWidth();
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        popupWindow.showAtLocation(view, 0, iArr[0] - measuredWidth, iArr[1] - resources.getDimensionPixelOffset(R.dimen.result_popwindow_offset));
    }

    private void closeNormalAdNewStyle(final C0534a aVar) {
        String str;
        y b2 = y.b();
        AnonymousClass2 r2 = new IAdFeedbackListener.Stub() {
            public void onFinished(int i) {
                if (i > 0) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            List<BaseCardModel> subCardModelList = AdvListTitleCardModel.this.getSubCardModelList();
                            if (subCardModelList != null && subCardModelList.size() > 0) {
                                AnonymousClass2 r1 = AnonymousClass2.this;
                                C0534a aVar = aVar;
                                AdvListTitleCardModel advListTitleCardModel = AdvListTitleCardModel.this;
                                aVar.a((BaseCardModel) advListTitleCardModel, subCardModelList, advListTitleCardModel.usePosition);
                            }
                        }
                    });
                }
                y.b().b(aVar.getApplicationContext());
            }
        };
        if (b2.a(aVar.getApplicationContext())) {
            List<BaseCardModel> subCardModelList = getSubCardModelList();
            if (subCardModelList != null && subCardModelList.size() > 0) {
                ArrayList arrayList = new ArrayList();
                for (BaseCardModel next : subCardModelList) {
                    if (next instanceof AdvCardModel) {
                        arrayList.add(((AdvCardModel) next).getEx());
                    }
                }
                if (arrayList.size() > 0) {
                    try {
                        if (p.a() >= 10) {
                            b2.a(aVar.getApplicationContext(), (IAdFeedbackListener) r2, aVar.getPackageName(), "com.miui.securitycenter_scanresult", (List<String>) arrayList);
                        } else {
                            b2.a(aVar.getApplicationContext(), (IAdFeedbackListener) r2, aVar.getPackageName(), "com.miui.securitycenter_scanresult", (String) arrayList.get(0));
                        }
                    } catch (Exception unused) {
                        str = "showDislikeWindow failed,maybe method showDislikeWindow() does not match or exits ";
                    }
                }
            }
        } else {
            str = "connect failed,maybe not support dislike window";
            Log.e(TAG, str);
        }
    }

    public BaseViewHolder createViewHolder(View view) {
        return new AdvListTitleViewHolder(view);
    }

    public boolean isLocal() {
        List<BaseCardModel> subCardModelList = getSubCardModelList();
        if (subCardModelList != null && subCardModelList.size() > 0) {
            BaseCardModel baseCardModel = subCardModelList.get(0);
            return (baseCardModel instanceof AdvCardModel) && ((AdvCardModel) baseCardModel).isLocal();
        }
    }

    public void onClick(View view) {
        C0534a aVar = (C0534a) view.getContext();
        if (view.getId() == R.id.view_close) {
            if (p.a() < 5 || isLocal()) {
                closeNormalAd(aVar, view);
            } else {
                closeNormalAdNewStyle(aVar);
            }
        }
    }

    public boolean validate() {
        return true;
    }
}

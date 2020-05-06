package com.miui.common.card.models;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.ui.main.ColorfulDotView;
import com.miui.securityscan.ui.main.ColorfulRingView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.cloud.util.SysHelper;

public class FuncCloudSpaceCardModel extends FunctionCardModel {
    /* access modifiers changed from: private */
    public int[] colors;

    public class FuncCloudSpaceCardViewHolder extends FunctionCardModel.FunctionViewHolder {
        public static final String APPDATA = "AppData";
        public static final String APPLIST = "AppList";
        public static final String DUOKAN = "Duokan";
        public static final String GALLERY_IMAGE = "GalleryImage";
        public static final String MUSIC = "Music";
        public static final String RECORDER = "Recorder";
        private int mColorIndex = 0;
        private List<Integer> mColorList;
        private List<ColorfulRingView.a> mColorfulRingList;
        private ColorfulRingView mColorfulRingView;
        private LinearLayout mContainer;
        private Context mContext;
        private int mRingDefaultColor;
        private TextView mStorageFullView;
        private TextView mStoragePercentageView;
        private TextView mStorageSpaceView;
        private Map<String, Integer> mSupportedCategory;

        public FuncCloudSpaceCardViewHolder(View view) {
            super(view);
            this.mContext = view.getContext();
            this.mColorList = new ArrayList();
            this.mColorfulRingList = new ArrayList();
            this.mRingDefaultColor = this.mContext.getResources().getColor(R.color.micloud_storage_default);
            initView(view);
            initSupportCategory();
        }

        private void initSupportCategory() {
            Resources resources = this.mContext.getResources();
            this.mSupportedCategory = new HashMap();
            this.mSupportedCategory.put(GALLERY_IMAGE, Integer.valueOf(resources.getColor(R.color.micloud_storage_gallery)));
            this.mSupportedCategory.put(RECORDER, Integer.valueOf(resources.getColor(R.color.micloud_storage_recorder)));
            this.mSupportedCategory.put(APPLIST, Integer.valueOf(resources.getColor(R.color.micloud_storage_applist)));
            this.mSupportedCategory.put(DUOKAN, Integer.valueOf(resources.getColor(R.color.micloud_storage_duokan)));
            this.mSupportedCategory.put(MUSIC, Integer.valueOf(resources.getColor(R.color.micloud_storage_music)));
            this.mSupportedCategory.put(APPDATA, Integer.valueOf(resources.getColor(R.color.micloud_storage_app)));
        }

        private void initView(View view) {
            this.mStoragePercentageView = (TextView) view.findViewById(R.id.tv_storage_percentage);
            this.mStorageFullView = (TextView) view.findViewById(R.id.tv_storage_full);
            this.mStorageSpaceView = (TextView) view.findViewById(R.id.tv_storage_space);
            this.mColorfulRingView = (ColorfulRingView) view.findViewById(R.id.ring);
            this.mColorfulRingView.a(this.mColorfulRingList, this.mRingDefaultColor);
            this.mContainer = (LinearLayout) view.findViewById(R.id.ll_container);
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            MiCloudStatusInfo.QuotaInfo quotaInfo;
            MiCloudStatusInfo.QuotaInfo quotaInfo2;
            MiCloudStatusInfo.QuotaInfo quotaInfo3;
            int i2;
            super.fillData(view, baseCardModel, i);
            FuncCloudSpaceCardModel funcCloudSpaceCardModel = (FuncCloudSpaceCardModel) baseCardModel;
            MiCloudStatusInfo fromUserData = MiCloudStatusInfo.fromUserData(this.mContext);
            if (fromUserData != null && (quotaInfo = fromUserData.getQuotaInfo()) != null) {
                ArrayList<MiCloudStatusInfo.ItemInfo> itemInfoList = quotaInfo.getItemInfoList();
                if (itemInfoList == null || itemInfoList.size() <= 0) {
                    quotaInfo2 = quotaInfo;
                } else {
                    this.mColorfulRingList.clear();
                    this.mContainer.removeAllViews();
                    this.mColorList.clear();
                    Iterator<MiCloudStatusInfo.ItemInfo> it = itemInfoList.iterator();
                    while (it.hasNext()) {
                        MiCloudStatusInfo.ItemInfo next = it.next();
                        if (next != null) {
                            String name = next.getName();
                            if (this.mSupportedCategory.containsKey(name)) {
                                i2 = this.mSupportedCategory.get(name).intValue();
                            } else {
                                int[] access$000 = FuncCloudSpaceCardModel.this.colors;
                                int i3 = this.mColorIndex;
                                i2 = access$000[i3];
                                this.mColorIndex = (i3 + 1) % FuncCloudSpaceCardModel.this.colors.length;
                            }
                            ColorfulRingView.a aVar = new ColorfulRingView.a();
                            aVar.f7990b = (((float) next.getUsed()) * 1.0f) / ((float) quotaInfo.getTotal());
                            aVar.f7989a = i2;
                            this.mColorfulRingList.add(aVar);
                            this.mColorList.add(Integer.valueOf(i2));
                        }
                    }
                    int size = itemInfoList.size() % 2 == 0 ? itemInfoList.size() / 2 : (itemInfoList.size() / 2) + 1;
                    int i4 = 0;
                    while (i4 < size) {
                        View inflate = View.inflate(this.mContext, R.layout.card_layout_cloud_space_items, (ViewGroup) null);
                        View findViewById = inflate.findViewById(R.id.view_left);
                        View findViewById2 = inflate.findViewById(R.id.view_right);
                        ColorfulDotView colorfulDotView = (ColorfulDotView) findViewById.findViewById(R.id.dot);
                        TextView textView = (TextView) findViewById.findViewById(R.id.tv_title);
                        TextView textView2 = (TextView) findViewById.findViewById(R.id.tv_summary);
                        ColorfulDotView colorfulDotView2 = (ColorfulDotView) findViewById2.findViewById(R.id.dot);
                        TextView textView3 = (TextView) findViewById2.findViewById(R.id.tv_title);
                        TextView textView4 = (TextView) findViewById2.findViewById(R.id.tv_summary);
                        if (i4 == size - 1) {
                            quotaInfo3 = quotaInfo;
                            int i5 = size * 2;
                            if (itemInfoList.size() < i5) {
                                findViewById.setVisibility(0);
                                findViewById2.setVisibility(4);
                                int i6 = i4 * 2;
                                colorfulDotView.setColor(this.mColorList.get(i6).intValue());
                                textView.setText(itemInfoList.get(i6).getLocalizedName());
                                textView2.setText(SysHelper.getQuantityStringWithUnit(itemInfoList.get(i6).getUsed()));
                            } else if (itemInfoList.size() != i5) {
                            }
                            this.mContainer.addView(inflate);
                            i4++;
                            quotaInfo = quotaInfo3;
                        } else {
                            quotaInfo3 = quotaInfo;
                        }
                        findViewById.setVisibility(0);
                        findViewById2.setVisibility(0);
                        int i7 = i4 * 2;
                        colorfulDotView.setColor(this.mColorList.get(i7).intValue());
                        textView.setText(itemInfoList.get(i7).getLocalizedName());
                        textView2.setText(SysHelper.getQuantityStringWithUnit(itemInfoList.get(i7).getUsed()));
                        int i8 = i7 + 1;
                        colorfulDotView2.setColor(this.mColorList.get(i8).intValue());
                        textView3.setText(itemInfoList.get(i8).getLocalizedName());
                        textView4.setText(SysHelper.getQuantityStringWithUnit(itemInfoList.get(i8).getUsed()));
                        this.mContainer.addView(inflate);
                        i4++;
                        quotaInfo = quotaInfo3;
                    }
                    quotaInfo2 = quotaInfo;
                    this.mColorfulRingView.a(this.mColorfulRingList, this.mRingDefaultColor);
                }
                if (quotaInfo2.isSpaceFull()) {
                    this.mStoragePercentageView.setVisibility(8);
                    this.mStorageFullView.setVisibility(0);
                } else {
                    this.mStoragePercentageView.setVisibility(0);
                    this.mStorageFullView.setVisibility(8);
                    this.mStoragePercentageView.setText(((int) ((((float) quotaInfo2.getUsed()) * 100.0f) / ((float) quotaInfo2.getTotal()))) + "%");
                }
                String quantityStringWithUnit = SysHelper.getQuantityStringWithUnit(quotaInfo2.getUsed());
                String quantityStringWithUnit2 = SysHelper.getQuantityStringWithUnit(quotaInfo2.getTotal());
                this.mStorageSpaceView.setText(quantityStringWithUnit + "/" + quantityStringWithUnit2);
            }
        }
    }

    public FuncCloudSpaceCardModel() {
        this((AbsModel) null);
    }

    public FuncCloudSpaceCardModel(AbsModel absModel) {
        super(R.layout.card_layout_cloud_space, absModel);
        this.colors = new int[]{R.color.micloud_storage_video, R.color.micloud_storage_contacts, R.color.micloud_storage_sms, R.color.micloud_storage_calllog, R.color.micloud_storage_notes, R.color.micloud_storage_wifi};
    }

    public BaseViewHolder createViewHolder(View view) {
        return new FuncCloudSpaceCardViewHolder(view);
    }

    public boolean validate() {
        return true;
    }
}

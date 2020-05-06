package com.miui.common.card.models;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.common.card.BaseViewHolder;
import com.miui.securitycenter.R;
import com.miui.securityscan.scanner.C0569p;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScanResultBottomCardModelNew extends BaseCardModel {
    private Map<String, C0569p> cleanupMap;
    private int cleanupResId;
    private String cleanupStatus;
    private String cleanupTitle;
    private Map<String, C0569p> securityMap;
    private int securityResId;
    private String securityStatus;
    private String securityTitle;
    private Map<String, C0569p> systemMap;
    private int systemResId;
    private String systemStatus;
    private String systemTitle;

    static class ChildViewHolder {
        TextView contentTextView;

        ChildViewHolder() {
        }
    }

    public static class ScanResultBottomViewHolder extends BaseViewHolder {
        List<View> cleanupChildViewCacheList = new ArrayList();
        LinearLayout cleanupContainer;
        ImageView cleanupIconImageView;
        TextView cleanupStatusTextView;
        TextView cleanupTitleTextView;
        View cleanupTitleView;
        Context context;
        LayoutInflater inflater = LayoutInflater.from(this.context);
        List<View> securityChildViewCacheList = new ArrayList();
        LinearLayout securityContainer;
        ImageView securityIconImageView;
        TextView securityStatusTextView;
        TextView securityTitleTextView;
        View securityTitleView;
        List<View> systemChildViewCacheList = new ArrayList();
        LinearLayout systemContainer;
        ImageView systemIconImageView;
        TextView systemStatusTextView;
        TextView systemTitleTextView;
        View systemTitleView;

        public ScanResultBottomViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            initView(view);
        }

        private void addChildView(ArrayList<C0569p> arrayList, LinearLayout linearLayout, List<View> list) {
            ChildViewHolder childViewHolder;
            View view;
            int i;
            Resources resources;
            TextView textView;
            linearLayout.removeAllViews();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                C0569p pVar = arrayList.get(i2);
                if (i2 < list.size()) {
                    view = list.get(i2);
                    childViewHolder = (ChildViewHolder) view.getTag();
                } else {
                    view = this.inflater.inflate(R.layout.card_layout_scan_result_item, linearLayout, false);
                    childViewHolder = new ChildViewHolder();
                    childViewHolder.contentTextView = (TextView) view.findViewById(R.id.tv_content);
                    view.setTag(childViewHolder);
                    list.add(view);
                }
                childViewHolder.contentTextView.setText(pVar.a());
                if (pVar.b()) {
                    textView = childViewHolder.contentTextView;
                    resources = this.context.getResources();
                    i = R.color.optmizing_item_status;
                } else {
                    textView = childViewHolder.contentTextView;
                    resources = this.context.getResources();
                    i = R.color.blackalpha50;
                }
                textView.setTextColor(resources.getColor(i));
                linearLayout.addView(view);
            }
        }

        private void initView(View view) {
            this.systemTitleView = view.findViewById(R.id.system_title);
            this.systemIconImageView = (ImageView) this.systemTitleView.findViewById(R.id.iv_icon);
            this.systemTitleTextView = (TextView) this.systemTitleView.findViewById(R.id.tv_title);
            this.systemStatusTextView = (TextView) this.systemTitleView.findViewById(R.id.tv_status);
            this.systemContainer = (LinearLayout) view.findViewById(R.id.ll_container_system);
            this.cleanupTitleView = view.findViewById(R.id.cleanup_title);
            this.cleanupIconImageView = (ImageView) this.cleanupTitleView.findViewById(R.id.iv_icon);
            this.cleanupTitleTextView = (TextView) this.cleanupTitleView.findViewById(R.id.tv_title);
            this.cleanupStatusTextView = (TextView) this.cleanupTitleView.findViewById(R.id.tv_status);
            this.cleanupContainer = (LinearLayout) view.findViewById(R.id.ll_container_cleanup);
            this.securityTitleView = view.findViewById(R.id.security_title);
            this.securityIconImageView = (ImageView) this.securityTitleView.findViewById(R.id.iv_icon);
            this.securityTitleTextView = (TextView) this.securityTitleView.findViewById(R.id.tv_title);
            this.securityStatusTextView = (TextView) this.securityTitleView.findViewById(R.id.tv_status);
            this.securityContainer = (LinearLayout) view.findViewById(R.id.ll_container_security);
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            super.fillData(view, baseCardModel, i);
            ScanResultBottomCardModelNew scanResultBottomCardModelNew = (ScanResultBottomCardModelNew) baseCardModel;
            this.systemIconImageView.setImageResource(scanResultBottomCardModelNew.getSystemResId());
            this.systemTitleTextView.setText(scanResultBottomCardModelNew.getSystemTitle());
            this.systemStatusTextView.setText(scanResultBottomCardModelNew.getSystemStatus());
            addChildView(new ArrayList(scanResultBottomCardModelNew.getSystemMap().values()), this.systemContainer, this.systemChildViewCacheList);
            this.cleanupIconImageView.setImageResource(scanResultBottomCardModelNew.getCleanupResId());
            this.cleanupTitleTextView.setText(scanResultBottomCardModelNew.getCleanupTitle());
            this.cleanupStatusTextView.setText(scanResultBottomCardModelNew.getCleanupStatus());
            addChildView(new ArrayList(scanResultBottomCardModelNew.getCleanupMap().values()), this.cleanupContainer, this.cleanupChildViewCacheList);
            this.securityIconImageView.setImageResource(scanResultBottomCardModelNew.getSecurityResId());
            this.securityTitleTextView.setText(scanResultBottomCardModelNew.getSecurityTitle());
            this.securityStatusTextView.setText(scanResultBottomCardModelNew.getSecurityStatus());
            addChildView(new ArrayList(scanResultBottomCardModelNew.getSecurityMap().values()), this.securityContainer, this.securityChildViewCacheList);
        }
    }

    public ScanResultBottomCardModelNew() {
        super(R.layout.card_layout_scan_result_bottom_new);
    }

    public BaseViewHolder createViewHolder(View view) {
        return new ScanResultBottomViewHolder(view);
    }

    public Map<String, C0569p> getCleanupMap() {
        return this.cleanupMap;
    }

    public int getCleanupResId() {
        return this.cleanupResId;
    }

    public String getCleanupStatus() {
        return this.cleanupStatus;
    }

    public String getCleanupTitle() {
        return this.cleanupTitle;
    }

    public Map<String, C0569p> getSecurityMap() {
        return this.securityMap;
    }

    public int getSecurityResId() {
        return this.securityResId;
    }

    public String getSecurityStatus() {
        return this.securityStatus;
    }

    public String getSecurityTitle() {
        return this.securityTitle;
    }

    public Map<String, C0569p> getSystemMap() {
        return this.systemMap;
    }

    public int getSystemResId() {
        return this.systemResId;
    }

    public String getSystemStatus() {
        return this.systemStatus;
    }

    public String getSystemTitle() {
        return this.systemTitle;
    }

    public void setCleanupMap(Map<String, C0569p> map) {
        this.cleanupMap = map;
    }

    public void setCleanupResId(int i) {
        this.cleanupResId = i;
    }

    public void setCleanupStatus(String str) {
        this.cleanupStatus = str;
    }

    public void setCleanupTitle(String str) {
        this.cleanupTitle = str;
    }

    public void setSecurityMap(Map<String, C0569p> map) {
        this.securityMap = map;
    }

    public void setSecurityResId(int i) {
        this.securityResId = i;
    }

    public void setSecurityStatus(String str) {
        this.securityStatus = str;
    }

    public void setSecurityTitle(String str) {
        this.securityTitle = str;
    }

    public void setSystemMap(Map<String, C0569p> map) {
        this.systemMap = map;
    }

    public void setSystemResId(int i) {
        this.systemResId = i;
    }

    public void setSystemStatus(String str) {
        this.systemStatus = str;
    }

    public void setSystemTitle(String str) {
        this.systemTitle = str;
    }

    public boolean validate() {
        return true;
    }
}

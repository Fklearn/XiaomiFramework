package com.miui.common.card.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import b.b.c.j.l;
import b.b.c.j.r;
import b.b.g.a;
import b.b.g.b;
import b.c.a.b.d;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.FillParentDrawable;
import com.miui.securitycenter.R;
import com.miui.securityscan.cards.h;
import org.json.JSONObject;

public class AdvInternationalCardModel extends AdvCardModel {
    private static final String TAG = "AdvInternationalCardModel";
    /* access modifiers changed from: private */
    public boolean isLoaded;
    private int mGlobalADType;

    public class InternationalAdvViewHolder extends BaseViewHolder {
        private b mInternationalVH;
        private d option = r.g;

        public InternationalAdvViewHolder(View view) {
            super(view);
            this.mInternationalVH = h.a(view, (BaseCardModel) AdvInternationalCardModel.this);
            l.a(view);
        }

        public void fillData(View view, final BaseCardModel baseCardModel, int i) {
            AdvInternationalCardModel advInternationalCardModel;
            Log.d(AdvInternationalCardModel.TAG, "International Ads");
            if (baseCardModel instanceof AdvInternationalCardModel) {
                advInternationalCardModel = (AdvInternationalCardModel) baseCardModel;
                String positionId = advInternationalCardModel.getPositionId();
                h.a(positionId);
                Log.d(AdvInternationalCardModel.TAG, "International Ads reportPV : " + positionId);
            } else {
                advInternationalCardModel = null;
            }
            if (AdvInternationalCardModel.this.isLoaded) {
                b bVar = this.mInternationalVH;
                if (bVar.j) {
                    if (advInternationalCardModel != null) {
                        h.a(bVar, advInternationalCardModel.getGlobalADType(), advInternationalCardModel.getObject());
                    }
                    this.mInternationalVH.h.setBackgroundResource(R.drawable.card_bg_no_shadow_selector);
                    this.mInternationalVH.f1776a.setText(baseCardModel.getTitle());
                    this.mInternationalVH.e.setText(AdvInternationalCardModel.this.getCta());
                    if (TextUtils.isEmpty(baseCardModel.getSummary())) {
                        this.mInternationalVH.f1777b.setVisibility(8);
                    } else {
                        this.mInternationalVH.f1777b.setText(baseCardModel.getSummary());
                        this.mInternationalVH.f1777b.setVisibility(0);
                    }
                    if (this.mInternationalVH.f1779d != null) {
                        r.a(baseCardModel.getIcon(), this.mInternationalVH.f1779d, this.option, (int) R.drawable.card_icon_default);
                    }
                    if (this.mInternationalVH.f1778c != null) {
                        r.a(((AdvCardModel) baseCardModel).getMultiImgUrls()[0], this.mInternationalVH.f1778c, r.f1760d, (Drawable) new FillParentDrawable(view.getContext().getResources().getDrawable(R.drawable.big_backgroud_def)));
                    }
                    Context context = view.getContext();
                    if (advInternationalCardModel != null) {
                        h.a(context, this.mInternationalVH.f, advInternationalCardModel.getGlobalADType(), advInternationalCardModel.getObject(), this.mInternationalVH.i);
                    }
                    this.mInternationalVH.g.bringToFront();
                    this.mInternationalVH.g.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            baseCardModel.onClick(view);
                        }
                    });
                    return;
                }
            }
            view.setBackgroundResource(0);
        }
    }

    public AdvInternationalCardModel(JSONObject jSONObject, int i) {
        super(R.layout.result_template_ad_global_empty, jSONObject, i);
    }

    public BaseViewHolder createViewHolder(View view) {
        return new InternationalAdvViewHolder(view);
    }

    public void fillAd(AdvInternationalCardModel advInternationalCardModel) {
        if (advInternationalCardModel != null) {
            Log.d(TAG, "fill ad");
            setLayoutId(advInternationalCardModel.getLayoutId());
            setObject(advInternationalCardModel.getObject());
            setId(advInternationalCardModel.getId());
            setIcon(advInternationalCardModel.getIcon());
            setCta(advInternationalCardModel.getCta());
            setTitle(advInternationalCardModel.getTitle());
            setSummary(advInternationalCardModel.getSummary());
            setMultiImgUrls(advInternationalCardModel.getMultiImgUrls());
            setGlobalADType(advInternationalCardModel.getGlobalADType());
            setLoaded(true);
            a.a().a(advInternationalCardModel.getObject(), this);
        }
    }

    public int getGlobalADType() {
        return this.mGlobalADType;
    }

    public boolean isLoaded() {
        return this.isLoaded && this.mGlobalADType > 0;
    }

    public void setGlobalADType(int i) {
        this.mGlobalADType = i;
    }

    public void setLoaded(boolean z) {
        this.isLoaded = z;
    }

    public boolean validate() {
        return true;
    }
}

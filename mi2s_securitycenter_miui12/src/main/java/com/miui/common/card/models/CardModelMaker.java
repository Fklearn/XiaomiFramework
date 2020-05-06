package com.miui.common.card.models;

import com.miui.common.card.functions.BaseFunction;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.model.manualitem.ConsumePowerRankModel;
import com.miui.securityscan.model.manualitem.FlowRankModel;
import com.miui.securityscan.scanner.O;

public class CardModelMaker {
    private static CardModelMaker cardModelMaker;

    public static synchronized CardModelMaker getInstance() {
        CardModelMaker cardModelMaker2;
        synchronized (CardModelMaker.class) {
            if (cardModelMaker == null) {
                cardModelMaker = new CardModelMaker();
            }
            cardModelMaker2 = cardModelMaker;
        }
        return cardModelMaker2;
    }

    public FuncBtnBottomCardModel getFuncBtnBottomCardModel(AbsModel absModel, String str, String str2, BaseFunction baseFunction) {
        FuncBtnBottomCardModel funcBtnBottomCardModel = new FuncBtnBottomCardModel(absModel);
        funcBtnBottomCardModel.setTitle(absModel.getTitle());
        funcBtnBottomCardModel.setSummary(absModel.getSummary());
        funcBtnBottomCardModel.setButton(str);
        funcBtnBottomCardModel.setIcon(str2);
        funcBtnBottomCardModel.setFunction(baseFunction);
        funcBtnBottomCardModel.setScore(absModel.getScore());
        return funcBtnBottomCardModel;
    }

    public FuncCloudSpaceCardModel getFuncCloudSpaceCardModel(AbsModel absModel, String str, BaseFunction baseFunction) {
        FuncCloudSpaceCardModel funcCloudSpaceCardModel = new FuncCloudSpaceCardModel(absModel);
        funcCloudSpaceCardModel.setTitle(absModel.getTitle());
        funcCloudSpaceCardModel.setSummary(absModel.getSummary());
        funcCloudSpaceCardModel.setButton(str);
        funcCloudSpaceCardModel.setFunction(baseFunction);
        funcCloudSpaceCardModel.setScore(absModel.getScore());
        return funcCloudSpaceCardModel;
    }

    public FuncLeftBannerCardModel getFuncLeftBannerCardModel(AbsModel absModel, String str, String str2, BaseFunction baseFunction) {
        FuncLeftBannerCardModel funcLeftBannerCardModel = new FuncLeftBannerCardModel(absModel);
        funcLeftBannerCardModel.setTitle(absModel.getTitle());
        funcLeftBannerCardModel.setSummary(absModel.getSummary());
        funcLeftBannerCardModel.setButton(str);
        funcLeftBannerCardModel.setIcon(str2);
        funcLeftBannerCardModel.setFunction(baseFunction);
        return funcLeftBannerCardModel;
    }

    public FunNoIconCardModel getFuncTopBannerCardModel(AbsModel absModel, String str, BaseFunction baseFunction) {
        FunNoIconCardModel funNoIconCardModel = new FunNoIconCardModel(absModel);
        funNoIconCardModel.setTitle(absModel.getTitle());
        funNoIconCardModel.setSummary(absModel.getSummary());
        funNoIconCardModel.setButton(str);
        funNoIconCardModel.setFunction(baseFunction);
        funNoIconCardModel.setScore(absModel.getScore());
        return funNoIconCardModel;
    }

    public ListTitleCheckboxCardModel getListTitleCheckboxCardModel(O.a aVar, GroupModel groupModel, String str, String str2, String str3, String str4, boolean z, int i) {
        ListTitleCheckboxCardModel listTitleCheckboxCardModel = new ListTitleCheckboxCardModel(aVar, groupModel);
        listTitleCheckboxCardModel.setTitle(str);
        listTitleCheckboxCardModel.setSummary(str2);
        listTitleCheckboxCardModel.setBtnText(str3);
        listTitleCheckboxCardModel.setGroupToast(str4);
        listTitleCheckboxCardModel.setNeedRefreshManualItem(z);
        if (i != 0) {
            listTitleCheckboxCardModel.setResId(i);
        }
        return listTitleCheckboxCardModel;
    }

    public ListTitleConsumePowerRankCardModel getListTitleConsumePowerRankCardModel(O.a aVar, ConsumePowerRankModel consumePowerRankModel, String str) {
        ListTitleConsumePowerRankCardModel listTitleConsumePowerRankCardModel = new ListTitleConsumePowerRankCardModel(aVar, consumePowerRankModel);
        listTitleConsumePowerRankCardModel.setButton(str);
        listTitleConsumePowerRankCardModel.setTitle(consumePowerRankModel.getTitle());
        listTitleConsumePowerRankCardModel.setAppConsumeInfoList(consumePowerRankModel.getAppConsumeInfoList());
        listTitleConsumePowerRankCardModel.setScore(consumePowerRankModel.getScore());
        return listTitleConsumePowerRankCardModel;
    }

    public ListTitleFlowRankCardModel getListTitleFlowRankCardModel(O.a aVar, FlowRankModel flowRankModel, String str) {
        ListTitleFlowRankCardModel listTitleFlowRankCardModel = new ListTitleFlowRankCardModel(aVar, flowRankModel);
        listTitleFlowRankCardModel.setFlowRankDataModels(flowRankModel.getFlowRankDataModels());
        listTitleFlowRankCardModel.setTitle(flowRankModel.getTitle());
        listTitleFlowRankCardModel.setButton(str);
        listTitleFlowRankCardModel.setScore(flowRankModel.getScore());
        return listTitleFlowRankCardModel;
    }
}

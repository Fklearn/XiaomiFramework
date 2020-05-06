package com.miui.securityscan.model;

import android.app.Activity;
import android.content.Context;
import com.miui.securityscan.model.AbsModel;
import java.util.ArrayList;
import java.util.List;

public class GroupModel {
    private AbsModel curModel;
    private List<AbsModel> dangerModelList = new ArrayList();
    private List<AbsModel> modelGroup = new ArrayList();

    public boolean addModel(AbsModel absModel) {
        return this.modelGroup.add(absModel);
    }

    public AbsModel getCurModel() {
        return this.curModel;
    }

    public String getDesc() {
        return null;
    }

    public List<AbsModel> getModelList() {
        return this.modelGroup;
    }

    public boolean isGroupEmpty() {
        return this.modelGroup.isEmpty();
    }

    public void optimize(Context context) {
        List<AbsModel> list = this.dangerModelList;
        if (list != null && (context instanceof Activity)) {
            for (AbsModel next : list) {
                next.optimize(context);
                next.setFixed(true);
            }
        }
    }

    public void scan() {
        List<AbsModel> list;
        AbsModel absModel;
        List<AbsModel> list2;
        if (this.modelGroup != null && (list = this.dangerModelList) != null) {
            list.clear();
            for (AbsModel next : this.modelGroup) {
                next.scan();
                if (next.isSafe() != AbsModel.State.SAFE) {
                    this.dangerModelList.add(next);
                }
            }
            if (this.dangerModelList.size() > 0) {
                list2 = this.dangerModelList;
            } else if (this.modelGroup.size() > 0) {
                list2 = this.modelGroup;
            } else {
                absModel = null;
                this.curModel = absModel;
            }
            absModel = list2.get(0);
            this.curModel = absModel;
        }
    }
}

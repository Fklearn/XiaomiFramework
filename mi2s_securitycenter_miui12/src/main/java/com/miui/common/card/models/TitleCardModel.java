package com.miui.common.card.models;

import com.miui.common.card.GridFunctionData;
import java.util.ArrayList;
import java.util.List;

public class TitleCardModel extends BaseCardModel {
    public List<GridFunctionData> gridFunctionDataList;
    private long id;
    private boolean isHomePageFunc;
    private int position = -1;
    private List<BaseCardModel> subCardModelList = new ArrayList();
    private int subCardModelTemplate = -1;
    private boolean visible = true;

    public TitleCardModel(int i) {
        super(i);
    }

    public void addSubCardModelList(BaseCardModel baseCardModel) {
        this.subCardModelList.add(baseCardModel);
    }

    public void addSubCardModelList(ArrayList<BaseCardModel> arrayList) {
        this.subCardModelList.addAll(arrayList);
    }

    public void clear() {
        List<BaseCardModel> list = this.subCardModelList;
        if (list != null) {
            list.clear();
        }
    }

    public long getId() {
        return this.id;
    }

    public int getPosition() {
        return this.position;
    }

    public List<BaseCardModel> getSubCardModelList() {
        return this.subCardModelList;
    }

    public int getSubCardModelTemplate() {
        return this.subCardModelTemplate;
    }

    public boolean isHomePageFunc() {
        return this.isHomePageFunc;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setHomePageFunc(boolean z) {
        this.isHomePageFunc = z;
    }

    public void setId(long j) {
        this.id = j;
    }

    public void setPosition(int i) {
        this.position = i;
    }

    public void setSubCardModelTemplate(int i) {
        this.subCardModelTemplate = i;
    }

    public void setVisible(boolean z) {
        this.visible = z;
    }

    public boolean validate() {
        return true;
    }
}

package com.miui.maml.elements;

import a.c.d;
import android.graphics.Canvas;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.folme.FolmeConfigValue;
import d.a.i.b;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FolmeConfigScreenElement extends ScreenElement {
    public static final String TAG_NAME = "FolmeConfig";
    private ArrayList<ConfigData> mConfigs = new ArrayList<>();

    private class ConfigData {
        public Expression delayExp;
        public Expression[] easeExps;
        public Expression fromSpeedExp;
        public Expression[] onBeginCallbackExps;
        public Expression[] onCompleteCallbackExps;
        public Expression[] onUpdateCallbackExps;
        public Expression[] propertyExps;

        private ConfigData() {
        }
    }

    public FolmeConfigScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mConfigs.add(getConfigData(element));
        NodeList childNodes = element.getChildNodes();
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            if (childNodes.item(i).getNodeType() == 1) {
                Element element2 = (Element) childNodes.item(i);
                if ("Special".equals(element2.getTagName())) {
                    this.mConfigs.add(getConfigData(element2));
                }
            }
        }
    }

    private ConfigData getConfigData(Element element) {
        ConfigData configData = new ConfigData();
        configData.easeExps = Expression.buildMultiple(getVariables(), element.getAttribute("ease"));
        configData.fromSpeedExp = Expression.build(getVariables(), element.getAttribute("fromSpeed"));
        configData.delayExp = Expression.build(getVariables(), element.getAttribute("delay"));
        configData.onBeginCallbackExps = Expression.buildMultiple(getVariables(), element.getAttribute("onBeginCall"));
        configData.onCompleteCallbackExps = Expression.buildMultiple(getVariables(), element.getAttribute("onComplete"));
        configData.onUpdateCallbackExps = Expression.buildMultiple(getVariables(), element.getAttribute("onUpdate"));
        configData.propertyExps = Expression.buildMultiple(getVariables(), element.getAttribute("property"));
        return configData;
    }

    private b.a getEaseFromExpressions(Expression[] expressionArr) {
        if (expressionArr == null || expressionArr.length <= 0 || expressionArr[0] == null) {
            return null;
        }
        int evaluate = (int) expressionArr[0].evaluate();
        float[] fArr = new float[(expressionArr.length - 1)];
        int length = expressionArr.length;
        for (int i = 1; i < length; i++) {
            if (expressionArr[i] != null) {
                fArr[i - 1] = (float) expressionArr[i].evaluate();
            } else {
                fArr[i - 1] = 0.0f;
            }
        }
        return b.a(evaluate, fArr);
    }

    private void getNamesFromExpressions(Expression[] expressionArr, d<String> dVar) {
        dVar.clear();
        if (expressionArr != null && expressionArr.length > 0) {
            int length = expressionArr.length;
            for (int i = 0; i < length; i++) {
                if (expressionArr[i] != null) {
                    dVar.add(expressionArr[i].evaluateStr());
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
    }

    public ArrayList<FolmeConfigValue> getConfig() {
        ArrayList<FolmeConfigValue> arrayList = new ArrayList<>();
        int size = this.mConfigs.size();
        for (int i = 0; i < size; i++) {
            FolmeConfigValue folmeConfigValue = new FolmeConfigValue();
            ConfigData configData = this.mConfigs.get(i);
            Expression expression = configData.delayExp;
            if (expression != null) {
                folmeConfigValue.delay = (long) expression.evaluate();
            }
            Expression expression2 = configData.fromSpeedExp;
            if (expression2 != null) {
                folmeConfigValue.fromSpeed = (float) ((long) expression2.evaluate());
            }
            folmeConfigValue.ease = getEaseFromExpressions(configData.easeExps);
            getNamesFromExpressions(configData.propertyExps, folmeConfigValue.relatedProperty);
            getNamesFromExpressions(configData.onBeginCallbackExps, folmeConfigValue.onBeginCallback);
            getNamesFromExpressions(configData.onCompleteCallbackExps, folmeConfigValue.onCompleteCallback);
            getNamesFromExpressions(configData.onUpdateCallbackExps, folmeConfigValue.onUpdateCallback);
            arrayList.add(folmeConfigValue);
        }
        return arrayList;
    }

    public boolean isVisible() {
        return false;
    }

    public void tick(long j) {
    }
}

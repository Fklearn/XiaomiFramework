package com.miui.maml.elements;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.ContextVariables;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.elements.VariableArrayElement;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import miui.cloud.CloudPushConstants;
import org.w3c.dom.Element;

public class ListScreenElement extends ElementGroup {
    private static double ACC = -800.0d;
    /* access modifiers changed from: private */
    public static String DATA_TYPE_BITMAP = "bitmap";
    /* access modifiers changed from: private */
    public static String DATA_TYPE_DOUBLE = "double";
    /* access modifiers changed from: private */
    public static String DATA_TYPE_FLOAT = "float";
    /* access modifiers changed from: private */
    public static String DATA_TYPE_INTEGER = "int";
    /* access modifiers changed from: private */
    public static String DATA_TYPE_INTEGER1 = "integer";
    /* access modifiers changed from: private */
    public static String DATA_TYPE_LONG = "long";
    /* access modifiers changed from: private */
    public static String DATA_TYPE_STRING = "string";
    private static final String LOG_TAG = "ListScreenElement";
    public static final String TAG_NAME = "List";
    protected AttrDataBinders mAttrDataBinders;
    private int mBottomIndex;
    private int mCachedItemCount;
    private boolean mClearOnFinish;
    private ArrayList<ColumnInfo> mColumnsInfo;
    private int mCurrentIndex = -1;
    private long mCurrentTime;
    private ArrayList<DataIndexMap> mDataList = new ArrayList<>();
    private ArrayList<Integer> mIndexOrder = new ArrayList<>();
    private IndexedVariable[] mIndexedVariables;
    private ElementGroup mInnerGroup;
    private boolean mIsChildScroll;
    private boolean mIsScroll;
    private boolean mIsUpDirection;
    private ListItemElement mItem;
    private int mItemCount;
    private long mLastTime;
    protected ListData mListData;
    private Expression mMaxHeight;
    private boolean mMoving;
    private double mOffsetX;
    private double mOffsetY;
    private boolean mPressed;
    private ArrayList<Integer> mReuseIndex = new ArrayList<>();
    private AnimatedScreenElement mScrollBar;
    private int mSelectedId;
    private IndexedVariable mSelectedIdVar;
    private double mSpeed;
    private long mStartAnimTime;
    private float mStartAnimY;
    private int mTopIndex;
    private double mTouchStartX;
    private double mTouchStartY;
    private int mVisibleItemCount;

    /* renamed from: com.miui.maml.elements.ListScreenElement$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type = new int[ColumnInfo.Type.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(12:0|1|2|3|4|5|6|7|8|9|10|(3:11|12|14)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type[] r0 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type = r0
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.STRING     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.BITMAP     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.INTEGER     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.DOUBLE     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.LONG     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x004b }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.FLOAT     // Catch:{ NoSuchFieldError -> 0x004b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.ListScreenElement.AnonymousClass1.<clinit>():void");
        }
    }

    public static class Column {
        public ListScreenElement mList;
        public String mName;
        public VariableArrayElement.VarObserver mObserver;
        public ScreenElementRoot mRoot;
        public String mTarget;
        public VariableArrayElement mTargetElement;

        public Column(Element element, ScreenElementRoot screenElementRoot, ListScreenElement listScreenElement) {
            this.mRoot = screenElementRoot;
            this.mList = listScreenElement;
            if (element != null) {
                load(element);
            }
        }

        private void load(Element element) {
            this.mName = element.getAttribute(CloudPushConstants.XML_NAME);
            this.mTarget = element.getAttribute("target");
            this.mObserver = new VariableArrayElement.VarObserver() {
                public void onDataChange(Object[] objArr) {
                    Column column = Column.this;
                    column.mList.addColumn(column.mName, objArr);
                }
            };
        }

        public void finish() {
            VariableArrayElement variableArrayElement = this.mTargetElement;
            if (variableArrayElement != null) {
                variableArrayElement.registerVarObserver(this.mObserver, false);
            }
        }

        public void init() {
            if (this.mTargetElement == null) {
                ScreenElement findElement = this.mRoot.findElement(this.mTarget);
                if (findElement instanceof VariableArrayElement) {
                    this.mTargetElement = (VariableArrayElement) findElement;
                } else {
                    Log.e(ListScreenElement.LOG_TAG, "can't find VarArray:" + this.mTarget);
                    return;
                }
            }
            this.mTargetElement.registerVarObserver(this.mObserver, true);
        }
    }

    public static class ColumnInfo {
        public Type mType;
        public String mVarName;

        public enum Type {
            STRING,
            BITMAP,
            INTEGER,
            DOUBLE,
            LONG,
            FLOAT;

            public boolean isNumber() {
                return this == INTEGER || this == DOUBLE || this == LONG || this == FLOAT;
            }
        }

        public ColumnInfo(String str) {
            Type type;
            int indexOf = str.indexOf(":");
            if (indexOf != -1) {
                this.mVarName = str.substring(0, indexOf);
                String substring = str.substring(indexOf + 1);
                if (ListScreenElement.DATA_TYPE_STRING.equals(substring)) {
                    type = Type.STRING;
                } else if (ListScreenElement.DATA_TYPE_BITMAP.equals(substring)) {
                    type = Type.BITMAP;
                } else if (ListScreenElement.DATA_TYPE_INTEGER.equals(substring) || ListScreenElement.DATA_TYPE_INTEGER1.equals(substring)) {
                    type = Type.INTEGER;
                } else if (ListScreenElement.DATA_TYPE_DOUBLE.equals(substring)) {
                    type = Type.DOUBLE;
                } else if (ListScreenElement.DATA_TYPE_LONG.equals(substring)) {
                    type = Type.LONG;
                } else if (ListScreenElement.DATA_TYPE_FLOAT.equals(substring)) {
                    type = Type.FLOAT;
                } else {
                    throw new IllegalArgumentException("List: invalid item data type:" + substring);
                }
                this.mType = type;
                return;
            }
            throw new IllegalArgumentException("List: invalid item data " + str);
        }

        public static ArrayList<ColumnInfo> createColumnsInfo(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            ArrayList<ColumnInfo> arrayList = new ArrayList<>();
            for (String columnInfo : str.split(",")) {
                arrayList.add(new ColumnInfo(columnInfo));
            }
            return arrayList;
        }

        public boolean validate(Object obj) {
            if (obj == null) {
                return true;
            }
            switch (AnonymousClass1.$SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[this.mType.ordinal()]) {
                case 1:
                    return obj instanceof String;
                case 2:
                    return obj instanceof Bitmap;
                case 3:
                    return obj instanceof Integer;
                case 4:
                    return obj instanceof Double;
                case 5:
                    return obj instanceof Long;
                case 6:
                    return obj instanceof Float;
                default:
                    return false;
            }
        }
    }

    private static class DataIndexMap {
        public Object[] mData;
        public int mElementIndex;
        public boolean mNeedRebind;

        public DataIndexMap(Object[] objArr) {
            this(objArr, -1);
        }

        public DataIndexMap(Object[] objArr, int i) {
            this.mElementIndex = -1;
            this.mData = objArr;
            this.mElementIndex = i;
        }

        public void setData(int i, Object obj) {
            Object[] objArr = this.mData;
            if (objArr != null && objArr.length > i) {
                objArr[i] = obj;
                this.mNeedRebind = true;
            }
        }
    }

    public static class ListData {
        public ArrayList<Column> mColumns = new ArrayList<>();
        public ListScreenElement mList;
        public ScreenElementRoot mRoot;

        public ListData(Element element, ScreenElementRoot screenElementRoot, ListScreenElement listScreenElement) {
            this.mRoot = screenElementRoot;
            this.mList = listScreenElement;
            if (element != null) {
                load(element);
            }
        }

        private void load(Element element) {
            Utils.traverseXmlElementChildren(element, "Column", new Utils.XmlTraverseListener() {
                public void onChild(Element element) {
                    ListData listData = ListData.this;
                    listData.mColumns.add(new Column(element, listData.mRoot, listData.mList));
                }
            });
        }

        public void finish() {
            Iterator<Column> it = this.mColumns.iterator();
            while (it.hasNext()) {
                Column next = it.next();
                if (next != null) {
                    next.finish();
                }
            }
        }

        public void init() {
            Iterator<Column> it = this.mColumns.iterator();
            while (it.hasNext()) {
                Column next = it.next();
                if (next != null) {
                    next.init();
                }
            }
        }
    }

    private static class ListItemElement extends ElementGroup {
        public static final String TAG_NAME = "Item";
        private int mDataIndex = -1;
        private AnimatedScreenElement mDivider;
        protected Element mNode;

        public ListItemElement(Element element, ScreenElementRoot screenElementRoot) {
            super(element, screenElementRoot);
            this.mNode = element;
            ScreenElement findElement = findElement("divider");
            if (findElement instanceof AnimatedScreenElement) {
                this.mDivider = (AnimatedScreenElement) findElement;
                removeElement(findElement);
                addElement(this.mDivider);
            }
            this.mAlignV = ScreenElement.AlignV.TOP;
        }

        public int getDataIndex() {
            return this.mDataIndex;
        }

        public void setDataIndex(int i) {
            this.mDataIndex = i;
            AnimatedScreenElement animatedScreenElement = this.mDivider;
            if (animatedScreenElement != null) {
                animatedScreenElement.show(i > 0);
            }
        }
    }

    public ListScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        if (this.mItem != null) {
            setClip(true);
            this.mMaxHeight = Expression.build(getVariables(), element.getAttribute("maxHeight"));
            this.mClearOnFinish = Boolean.parseBoolean(element.getAttribute("clearOnFinish"));
            String attribute = element.getAttribute(DataSchemeDataSource.SCHEME_DATA);
            if (!TextUtils.isEmpty(attribute)) {
                this.mColumnsInfo = ColumnInfo.createColumnsInfo(attribute);
                ArrayList<ColumnInfo> arrayList = this.mColumnsInfo;
                if (arrayList != null) {
                    this.mIndexedVariables = new IndexedVariable[arrayList.size()];
                    Element child = Utils.getChild(element, AttrDataBinders.TAG);
                    if (child != null) {
                        this.mAttrDataBinders = new AttrDataBinders(child, this.mRoot.getContext().mContextVariables);
                        Element child2 = Utils.getChild(element, "Data");
                        if (child2 != null) {
                            this.mListData = new ListData(child2, this.mRoot, this);
                        }
                        ScreenElement findElement = findElement("scrollbar");
                        if (findElement instanceof AnimatedScreenElement) {
                            this.mScrollBar = (AnimatedScreenElement) findElement;
                            this.mScrollBar.mAlignV = ScreenElement.AlignV.TOP;
                            removeElement(findElement);
                            addElement(this.mScrollBar);
                        }
                        this.mSelectedIdVar = new IndexedVariable(this.mName + ".selectedId", this.mRoot.getContext().mVariables, true);
                        return;
                    }
                    Log.e(LOG_TAG, "no attr data binder");
                    throw new IllegalArgumentException("List: no attr data binder");
                }
                Log.e(LOG_TAG, "invalid item data");
                throw new IllegalArgumentException("List: invalid item data");
            }
            Log.e(LOG_TAG, "no data");
            throw new IllegalArgumentException("List: no data");
        }
        Log.e(LOG_TAG, "no item");
        throw new IllegalArgumentException("List: no item");
    }

    private void bindData(ListItemElement listItemElement, int i, int i2) {
        if (i2 < 0 || i2 >= this.mItemCount) {
            Log.e(LOG_TAG, "invalid item data");
            return;
        }
        Object[] objArr = this.mDataList.get(i2).mData;
        listItemElement.setDataIndex(i2);
        this.mDataList.get(i2).mElementIndex = i;
        this.mDataList.get(i2).mNeedRebind = false;
        listItemElement.setY((double) ((float) ((double) (((float) i2) * this.mItem.getHeight()))));
        int size = this.mColumnsInfo.size();
        ContextVariables contextVariables = getContext().mContextVariables;
        for (int i3 = 0; i3 < size; i3++) {
            contextVariables.setVar(this.mColumnsInfo.get(i3).mVarName, objArr[i3]);
        }
        AttrDataBinders attrDataBinders = this.mAttrDataBinders;
        if (attrDataBinders != null) {
            attrDataBinders.bind(listItemElement);
        }
    }

    private void checkVisibility() {
        ArrayList<ScreenElement> elements = this.mInnerGroup.getElements();
        for (int i = 0; i < elements.size(); i++) {
            ListItemElement listItemElement = (ListItemElement) elements.get(i);
            int dataIndex = listItemElement.getDataIndex();
            if (dataIndex < 0 || dataIndex < this.mTopIndex || dataIndex > this.mBottomIndex) {
                if (listItemElement.isVisible()) {
                    listItemElement.show(false);
                }
            } else if (!listItemElement.isVisible()) {
                listItemElement.show(true);
            }
        }
    }

    private void clearEmptyRow() {
        int size = this.mDataList.size() - 1;
        while (size >= 0) {
            Object[] objArr = this.mDataList.get(size).mData;
            int length = objArr.length;
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = true;
                    break;
                } else if (objArr[i] != null) {
                    break;
                } else {
                    i++;
                }
            }
            if (z) {
                removeItem(size);
                size--;
            } else {
                return;
            }
        }
    }

    private ListItemElement getItem(int i) {
        ListItemElement listItemElement = null;
        if (i >= 0 && i < this.mItemCount) {
            int i2 = this.mDataList.get(i).mElementIndex;
            if (i2 >= 0) {
                listItemElement = (ListItemElement) this.mInnerGroup.getElements().get(i2);
            }
            if (i2 < 0 || listItemElement.getDataIndex() != i) {
                i2 = getUseableElementIndex();
                listItemElement = (ListItemElement) this.mInnerGroup.getElements().get(i2);
                if (listItemElement.getDataIndex() < 0) {
                    listItemElement.reset();
                }
            }
            if (listItemElement.getDataIndex() != i || this.mDataList.get(i).mNeedRebind) {
                bindData(listItemElement, i2, i);
            }
        }
        return listItemElement;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0038  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getUseableElementIndex() {
        /*
            r4 = this;
            java.util.ArrayList<java.lang.Integer> r0 = r4.mReuseIndex
            int r0 = r0.size()
            r1 = 0
            if (r0 <= 0) goto L_0x0016
            java.util.ArrayList<java.lang.Integer> r0 = r4.mReuseIndex
        L_0x000b:
            java.lang.Object r0 = r0.remove(r1)
        L_0x000f:
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            goto L_0x002a
        L_0x0016:
            boolean r0 = r4.mIsUpDirection
            if (r0 == 0) goto L_0x001d
            java.util.ArrayList<java.lang.Integer> r0 = r4.mIndexOrder
            goto L_0x000b
        L_0x001d:
            java.util.ArrayList<java.lang.Integer> r0 = r4.mIndexOrder
            int r2 = r0.size()
            int r2 = r2 + -1
            java.lang.Object r0 = r0.remove(r2)
            goto L_0x000f
        L_0x002a:
            boolean r2 = r4.mIsUpDirection
            if (r2 == 0) goto L_0x0038
            java.util.ArrayList<java.lang.Integer> r1 = r4.mIndexOrder
            java.lang.Integer r2 = java.lang.Integer.valueOf(r0)
            r1.add(r2)
            goto L_0x0041
        L_0x0038:
            java.util.ArrayList<java.lang.Integer> r2 = r4.mIndexOrder
            java.lang.Integer r3 = java.lang.Integer.valueOf(r0)
            r2.add(r1, r3)
        L_0x0041:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.ListScreenElement.getUseableElementIndex():int");
    }

    private void moveTo(double d2) {
        if (d2 < ((double) (getHeight() - (((float) this.mItemCount) * this.mItem.getHeight())))) {
            d2 = (double) (getHeight() - (((float) this.mItemCount) * this.mItem.getHeight()));
            this.mStartAnimTime = 0;
        }
        if (d2 > 0.0d) {
            this.mStartAnimTime = 0;
            d2 = 0.0d;
        }
        this.mInnerGroup.setY((double) ((float) d2));
        this.mTopIndex = Math.min((int) Math.floor((-d2) / ((double) this.mItem.getHeight())), (this.mItemCount - ((int) (getHeight() / this.mItem.getHeight()))) - 1);
        this.mBottomIndex = Math.min(((int) (getHeight() / this.mItem.getHeight())) + this.mTopIndex, this.mItemCount - 1);
        for (int i = this.mTopIndex; i <= this.mBottomIndex; i++) {
            getItem(i);
        }
        checkVisibility();
        updateScorllBar();
    }

    private void resetInner() {
        AnimatedScreenElement animatedScreenElement = this.mScrollBar;
        if (animatedScreenElement != null) {
            animatedScreenElement.show(false);
        }
        this.mMoving = false;
        this.mIsScroll = false;
        this.mIsChildScroll = false;
        this.mStartAnimTime = -1;
        this.mSpeed = 0.0d;
    }

    private void setVariables() {
        int size = this.mColumnsInfo.size();
        for (int i = 0; i < size; i++) {
            ColumnInfo columnInfo = this.mColumnsInfo.get(i);
            if (columnInfo.mType != ColumnInfo.Type.BITMAP) {
                IndexedVariable[] indexedVariableArr = this.mIndexedVariables;
                if (indexedVariableArr[i] == null) {
                    indexedVariableArr[i] = new IndexedVariable(this.mName + "." + columnInfo.mVarName, this.mRoot.getContext().mVariables, columnInfo.mType.isNumber());
                }
                IndexedVariable indexedVariable = this.mIndexedVariables[i];
                int i2 = this.mSelectedId;
                indexedVariable.set(i2 < 0 ? null : this.mDataList.get(i2).mData[i]);
            }
        }
    }

    private void startAnimation() {
        this.mStartAnimTime = SystemClock.elapsedRealtime();
        this.mStartAnimY = this.mInnerGroup.getY();
    }

    private void updateScorllBar() {
        if (this.mScrollBar != null && this.mIsScroll) {
            double height = (double) (((float) this.mItemCount) * this.mItem.getHeight());
            double height2 = (double) getHeight();
            double d2 = height2 / height;
            boolean z = true;
            if (d2 >= 1.0d) {
                d2 = 0.0d;
                z = false;
            }
            double y = ((double) this.mInnerGroup.getY()) / (height2 - height);
            if (y > 1.0d) {
                y = 1.0d;
            }
            this.mScrollBar.setY((double) ((float) ((1.0d - d2) * height2 * y)));
            this.mScrollBar.setHeight((double) ((float) (height2 * d2)));
            if (this.mScrollBar.isVisible() != z) {
                this.mScrollBar.show(z);
            }
        }
    }

    public void addColumn(String str, Object[] objArr) {
        if (str != null && objArr != null) {
            int i = -1;
            int size = this.mColumnsInfo.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    break;
                } else if (str.equals(this.mColumnsInfo.get(i2).mVarName)) {
                    i = i2;
                    break;
                } else {
                    i2++;
                }
            }
            if (i >= 0) {
                int length = objArr.length;
                int size2 = this.mDataList.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    Object obj = null;
                    if (i3 < length) {
                        obj = objArr[i3];
                    }
                    this.mDataList.get(i3).setData(i, obj);
                    if (this.mDataList.get(i3).mElementIndex >= 0) {
                        getItem(i3);
                    }
                }
                while (size2 < length) {
                    Object[] objArr2 = new Object[this.mColumnsInfo.size()];
                    objArr2[i] = objArr[size2];
                    addItem(objArr2);
                    size2++;
                }
                clearEmptyRow();
                requestUpdate();
            }
        }
    }

    public void addItem(Object... objArr) {
        String str;
        if (objArr != null) {
            if (objArr.length != this.mColumnsInfo.size()) {
                str = "invalid item data count";
            } else {
                int length = objArr.length;
                int i = 0;
                while (i < length) {
                    if (!this.mColumnsInfo.get(i).validate(objArr[i])) {
                        str = "invalid item data type: " + objArr[i];
                    } else {
                        i++;
                    }
                }
                this.mDataList.add(new DataIndexMap((Object[]) objArr.clone()));
                this.mCurrentIndex++;
                this.mItemCount++;
                setActualHeight(descale((double) getHeight()));
                this.mVisibleItemCount = (int) (Math.max(super.getHeight(), scale(evaluate(this.mMaxHeight))) / this.mItem.getHeight());
                this.mCachedItemCount = this.mVisibleItemCount * 2;
                int size = this.mInnerGroup.getElements().size();
                if (size < this.mCachedItemCount) {
                    ListItemElement listItemElement = this.mItem;
                    ListItemElement listItemElement2 = new ListItemElement(listItemElement.mNode, listItemElement.mRoot);
                    this.mInnerGroup.addElement(listItemElement2);
                    this.mDataList.get(this.mCurrentIndex).mElementIndex = size;
                    this.mSelectedId = this.mCurrentIndex;
                    listItemElement2.init();
                    this.mSelectedId = -1;
                    bindData(listItemElement2, size, this.mCurrentIndex);
                    this.mIndexOrder.add(Integer.valueOf(this.mCurrentIndex));
                }
                requestUpdate();
                return;
            }
            Log.e(LOG_TAG, str);
        }
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        super.doTick(j);
        long j2 = this.mStartAnimTime;
        if (j2 >= 0 && !this.mPressed) {
            long j3 = j - j2;
            if (j2 != 0) {
                double d2 = this.mSpeed;
                double d3 = ACC;
                double d4 = (double) j3;
                if (((d3 * d4) / 1000.0d) + d2 >= 0.0d) {
                    this.mOffsetY = ((d2 * d4) / 1000.0d) + ((((d3 * 0.5d) * d4) * d4) / 1000000.0d);
                    moveTo(((double) this.mStartAnimY) + (this.mIsUpDirection ? -this.mOffsetY : this.mOffsetY));
                    requestUpdate();
                }
            }
            resetInner();
            requestUpdate();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0014, code lost:
        r0 = ((com.miui.maml.elements.ListScreenElement.ListItemElement) r2.mInnerGroup.getElements().get((r0 = r2.mDataList.get(r0).mElementIndex))).findElement(r3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.miui.maml.elements.ScreenElement findElement(java.lang.String r3) {
        /*
            r2 = this;
            int r0 = r2.mSelectedId
            if (r0 < 0) goto L_0x0027
            int r1 = r2.mItemCount
            if (r0 >= r1) goto L_0x0027
            java.util.ArrayList<com.miui.maml.elements.ListScreenElement$DataIndexMap> r1 = r2.mDataList
            java.lang.Object r0 = r1.get(r0)
            com.miui.maml.elements.ListScreenElement$DataIndexMap r0 = (com.miui.maml.elements.ListScreenElement.DataIndexMap) r0
            int r0 = r0.mElementIndex
            if (r0 < 0) goto L_0x0027
            com.miui.maml.elements.ElementGroup r1 = r2.mInnerGroup
            java.util.ArrayList r1 = r1.getElements()
            java.lang.Object r0 = r1.get(r0)
            com.miui.maml.elements.ListScreenElement$ListItemElement r0 = (com.miui.maml.elements.ListScreenElement.ListItemElement) r0
            com.miui.maml.elements.ScreenElement r0 = r0.findElement(r3)
            if (r0 == 0) goto L_0x0027
            return r0
        L_0x0027:
            com.miui.maml.elements.ScreenElement r3 = super.findElement(r3)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.ListScreenElement.findElement(java.lang.String):com.miui.maml.elements.ScreenElement");
    }

    public void finish() {
        super.finish();
        if (this.mClearOnFinish) {
            removeAllItems();
        }
        ListData listData = this.mListData;
        if (listData != null) {
            listData.finish();
        }
    }

    public ArrayList<ColumnInfo> getColumnsInfo() {
        return this.mColumnsInfo;
    }

    public float getHeight() {
        return this.mMaxHeight == null ? super.getHeight() : Math.min(((float) this.mItemCount) * this.mItem.getHeight(), scale(evaluate(this.mMaxHeight)));
    }

    public void init() {
        super.init();
        resetInner();
        this.mInnerGroup.setY(0.0d);
        setActualHeight(descale((double) getHeight()));
        this.mSelectedId = -1;
        this.mSelectedIdVar.set((double) this.mSelectedId);
        setVariables();
        ListData listData = this.mListData;
        if (listData != null) {
            listData.init();
        }
    }

    /* access modifiers changed from: protected */
    public ScreenElement onCreateChild(Element element) {
        if (!element.getTagName().equalsIgnoreCase(ListItemElement.TAG_NAME) || this.mInnerGroup != null) {
            return super.onCreateChild(element);
        }
        this.mItem = new ListItemElement(element, this.mRoot);
        this.mInnerGroup = new ElementGroup((Element) null, this.mRoot);
        return this.mInnerGroup;
    }

    /* JADX WARNING: Removed duplicated region for block: B:56:0x014a A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.MotionEvent r19) {
        /*
            r18 = this;
            r0 = r18
            boolean r1 = r18.isVisible()
            r2 = 0
            if (r1 != 0) goto L_0x000a
            return r2
        L_0x000a:
            float r1 = r19.getX()
            float r3 = r19.getY()
            int r4 = r19.getActionMasked()
            r5 = 0
            r7 = -1
            r9 = 1
            if (r4 == 0) goto L_0x00f8
            if (r4 == r9) goto L_0x00d1
            r10 = 2
            r11 = 3
            if (r4 == r10) goto L_0x003d
            if (r4 == r11) goto L_0x0029
        L_0x0025:
            r4 = r19
            goto L_0x0143
        L_0x0029:
            r0.mPressed = r2
            boolean r1 = r0.mMoving
            if (r1 == 0) goto L_0x0025
            java.lang.String r1 = "cancel"
            r0.performAction(r1)
            r18.resetInner()
            r0.mStartAnimTime = r7
        L_0x0039:
            r4 = r19
            goto L_0x0141
        L_0x003d:
            boolean r4 = r0.mMoving
            if (r4 == 0) goto L_0x0025
            long r7 = android.os.SystemClock.elapsedRealtime()
            r0.mCurrentTime = r7
            double r7 = (double) r3
            double r12 = r0.mTouchStartY
            double r12 = r7 - r12
            r0.mOffsetY = r12
            double r12 = (double) r1
            double r14 = r0.mTouchStartX
            double r12 = r12 - r14
            r0.mOffsetX = r12
            boolean r4 = r0.mIsScroll
            if (r4 != 0) goto L_0x0087
            boolean r4 = r0.mIsChildScroll
            if (r4 != 0) goto L_0x0087
            double r12 = r0.mOffsetY
            double r12 = java.lang.Math.abs(r12)
            double r14 = r0.mOffsetX
            double r14 = java.lang.Math.abs(r14)
            r16 = 4617315517961601024(0x4014000000000000, double:5.0)
            int r4 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r4 <= 0) goto L_0x0079
            boolean r4 = r0.mIsChildScroll
            if (r4 != 0) goto L_0x0079
            int r4 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r4 < 0) goto L_0x0079
            r0.mIsScroll = r9
            goto L_0x0087
        L_0x0079:
            int r4 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r4 <= 0) goto L_0x0087
            boolean r4 = r0.mIsScroll
            if (r4 != 0) goto L_0x0087
            int r4 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r4 >= 0) goto L_0x0087
            r0.mIsChildScroll = r9
        L_0x0087:
            double r12 = r0.mOffsetY
            int r4 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r4 < 0) goto L_0x0094
            boolean r4 = r0.mIsChildScroll
            if (r4 == 0) goto L_0x0092
            goto L_0x0094
        L_0x0092:
            r4 = r2
            goto L_0x0095
        L_0x0094:
            r4 = r9
        L_0x0095:
            r0.mIsUpDirection = r4
            boolean r4 = r0.mIsScroll
            if (r4 == 0) goto L_0x0039
            r4 = r19
            r4.setAction(r11)
            java.lang.String r5 = "move"
            r0.performAction(r5)
            r0.onActionMove(r1, r3)
            double r5 = r0.mOffsetY
            double r5 = java.lang.Math.abs(r5)
            long r10 = r0.mCurrentTime
            long r12 = r0.mLastTime
            long r10 = r10 - r12
            double r10 = (double) r10
            double r5 = r5 / r10
            r10 = 4652007308841189376(0x408f400000000000, double:1000.0)
            double r5 = r5 * r10
            r0.mSpeed = r5
            com.miui.maml.elements.ElementGroup r1 = r0.mInnerGroup
            float r1 = r1.getY()
            double r5 = (double) r1
            double r10 = r0.mOffsetY
            double r5 = r5 + r10
            r0.moveTo(r5)
            r0.mTouchStartY = r7
            long r5 = r0.mCurrentTime
            r0.mLastTime = r5
            goto L_0x0141
        L_0x00d1:
            r4 = r19
            r0.mPressed = r2
            boolean r1 = r0.mMoving
            if (r1 == 0) goto L_0x0143
            java.lang.String r1 = "ListScreenElement"
            java.lang.String r3 = "unlock touch up"
            android.util.Log.i(r1, r3)
            java.lang.String r1 = "up"
            r0.performAction(r1)
            r18.onActionUp()
            double r5 = r0.mSpeed
            r7 = 4645744490609377280(0x4079000000000000, double:400.0)
            int r1 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r1 >= 0) goto L_0x00f4
            r18.resetInner()
            goto L_0x0141
        L_0x00f4:
            r18.startAnimation()
            goto L_0x0141
        L_0x00f8:
            r4 = r19
            boolean r10 = r0.touched(r1, r3)
            if (r10 == 0) goto L_0x0143
            r0.mMoving = r9
            r0.mPressed = r9
            java.lang.String r10 = "down"
            r0.performAction(r10)
            r0.onActionDown(r1, r3)
            r0.mStartAnimTime = r7
            r0.mSpeed = r5
            long r5 = android.os.SystemClock.elapsedRealtime()
            r0.mLastTime = r5
            com.miui.maml.elements.ElementGroup r5 = r0.mInnerGroup
            float r5 = r5.getAbsoluteTop()
            float r5 = r3 - r5
            com.miui.maml.elements.ListScreenElement$ListItemElement r6 = r0.mItem
            float r6 = r6.getHeight()
            float r5 = r5 / r6
            double r5 = (double) r5
            double r5 = java.lang.Math.floor(r5)
            int r5 = (int) r5
            r0.mSelectedId = r5
            com.miui.maml.data.IndexedVariable r5 = r0.mSelectedIdVar
            int r6 = r0.mSelectedId
            double r6 = (double) r6
            r5.set((double) r6)
            r18.setVariables()
            double r5 = (double) r1
            r0.mTouchStartX = r5
            double r5 = (double) r3
            r0.mTouchStartY = r5
            r18.updateScorllBar()
        L_0x0141:
            r1 = r9
            goto L_0x0144
        L_0x0143:
            r1 = r2
        L_0x0144:
            boolean r3 = super.onTouch(r19)
            if (r3 != 0) goto L_0x0150
            if (r1 == 0) goto L_0x0151
            boolean r1 = r0.mInterceptTouch
            if (r1 == 0) goto L_0x0151
        L_0x0150:
            r2 = r9
        L_0x0151:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.ListScreenElement.onTouch(android.view.MotionEvent):boolean");
    }

    public void removeAllItems() {
        this.mInnerGroup.removeAllElements();
        this.mInnerGroup.setY(0.0d);
        this.mDataList.clear();
        this.mIndexOrder.clear();
        this.mReuseIndex.clear();
        this.mCurrentIndex = -1;
        this.mItemCount = 0;
        setActualHeight(descale((double) getHeight()));
    }

    public void removeItem(int i) {
        if (i >= 0 && i < this.mItemCount) {
            this.mDataList.remove(i);
            this.mItemCount--;
            setActualHeight(descale((double) getHeight()));
            int size = this.mIndexOrder.size();
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                ListItemElement listItemElement = (ListItemElement) this.mInnerGroup.getElements().get(this.mIndexOrder.get(i3).intValue());
                int dataIndex = listItemElement.getDataIndex();
                if (dataIndex == i) {
                    listItemElement.setDataIndex(-1);
                    listItemElement.setY(-1.7976931348623157E308d);
                    listItemElement.show(false);
                    i2 = i3;
                } else if (dataIndex > i) {
                    int i4 = dataIndex - 1;
                    listItemElement.setDataIndex(i4);
                    listItemElement.setY((double) (((float) i4) * this.mItem.getHeight()));
                }
            }
            if (size > 0) {
                int intValue = this.mIndexOrder.remove(i2).intValue();
                moveTo((double) this.mInnerGroup.getY());
                this.mReuseIndex.add(Integer.valueOf(intValue));
            }
            requestUpdate();
        }
    }
}

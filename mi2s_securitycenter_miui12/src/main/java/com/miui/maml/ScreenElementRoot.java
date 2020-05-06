package com.miui.maml;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewManager;
import android.view.WindowManager;
import com.miui.maml.FramerateTokenList;
import com.miui.maml.RendererController;
import com.miui.maml.SoundManager;
import com.miui.maml.StylesManager;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.data.DateTimeVariableUpdater;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.VariableBinder;
import com.miui.maml.data.VariableBinderManager;
import com.miui.maml.data.VariableNames;
import com.miui.maml.data.VariableUpdaterManager;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.AnimatedScreenElement;
import com.miui.maml.elements.ElementGroup;
import com.miui.maml.elements.ElementGroupRC;
import com.miui.maml.elements.FramerateController;
import com.miui.maml.elements.ITicker;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.elements.ScreenElementVisitor;
import com.miui.maml.util.ConfigFile;
import com.miui.maml.util.HideSdkDependencyUtils;
import com.miui.maml.util.MamlAccessHelper;
import com.miui.maml.util.Task;
import com.miui.maml.util.Utils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miui.os.SystemProperties;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ScreenElementRoot extends ScreenElement implements InteractiveListener {
    private static final boolean CALCULATE_FRAME_RATE = true;
    public static final int CAPABILITY_ALL = -1;
    public static final int CAPABILITY_CREATE_OBJ = 4;
    public static final int CAPABILITY_VAR_PERSISTENCE = 2;
    public static final int CAPABILITY_WEBSERVICE = 1;
    private static final int DEFAULT_RES_DENSITY = 240;
    private static final int DEFAULT_SCREEN_WIDTH = 480;
    private static final String EXTERNAL_COMMANDS_TAG_NAME = "ExternalCommands";
    private static final String LOG_TAG = "ScreenElementRoot";
    private static final int MAML_INTERNAL_VERSION = 5;
    private static final String MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String RAW_DENSITY = "__raw_density";
    private static final String ROOT_NAME = "__root";
    private static final String SCALE_FACTOR = "__scale_factor";
    private static final String THEMEMANAGER_PACKAGE_NAME = "com.android.thememanager";
    private static final String VARIABLE_VIEW_HEIGHT = "view_height";
    private static final String VARIABLE_VIEW_WIDTH = "view_width";
    private static final String VAR_MAML_VERSION = "__maml_version";
    protected float DEFAULT_FRAME_RATE = 30.0f;
    private List<AnimatedScreenElement> mAccessibleElements = new ArrayList();
    private boolean mAllowScreenRotation;
    private ArrayMap<String, ArrayList<BaseAnimation.AnimationItem>> mAnimationItems = new ArrayMap<>();
    private int mBgColor;
    private boolean mBlurWindow = false;
    private String mCacheDir;
    private int mCapability = -1;
    private long mCheckPoint;
    private boolean mClearCanvas;
    private ConfigFile mConfig;
    private String mConfigPath;
    protected ScreenContext mContext;
    protected RendererController mController;
    private int mDefaultResourceDensity;
    private int mDefaultScreenWidth;
    private ArrayMap<String, WeakReference<ScreenElement>> mElements = new ArrayMap<>();
    private WeakReference<OnExternCommandListener> mExternCommandListener;
    /* access modifiers changed from: private */
    public CommandTriggers mExternalCommandManager;
    private boolean mFinished;
    private float mFontScale;
    protected float mFrameRate;
    private IndexedVariable mFrameRateVar;
    private FramerateHelper mFramerateHelper = new FramerateHelper();
    private int mFrames;
    private float mHeight;
    private WeakReference<OnHoverChangeListener> mHoverChangeListenerRef;
    private AnimatedScreenElement mHoverElement;
    private Matrix mHoverMatrix = new Matrix();
    protected ElementGroup mInnerGroup;
    private boolean mKeepResource;
    private MamlAccessHelper mMamlAccessHelper;
    private WeakReference<OnExternCommandListener> mMamlViewExternCommandListener;
    private boolean mNeedDisallowInterceptTouchEvent;
    private IndexedVariable mNeedDisallowInterceptTouchEventVar;
    private boolean mNeedReset;
    private ArrayList<ITicker> mPreTickers = new ArrayList<>();
    protected HashMap<String, String> mRawAttrs = new HashMap<>();
    private int mRawDefaultResourceDensity;
    private int mRawHeight;
    private int mRawTargetDensity;
    private int mRawWidth;
    /* access modifiers changed from: private */
    public ArrayList<RendererController> mRendererControllers = new ArrayList<>();
    private String mRootTag;
    private float mScale;
    private boolean mScaleByDensity;
    public boolean mShowDebugLayout;
    private boolean mShowFramerate;
    private SoundManager mSoundManager;
    private StylesManager mStylesManager;
    private boolean mSupportAccessibilityService = false;
    private OnExternCommandListener mSystemExternCommandListener;
    private int mTargetDensity;
    protected int mTargetScreenHeight;
    protected int mTargetScreenWidth;
    private IndexedVariable mTouchBeginTime;
    private IndexedVariable mTouchBeginX;
    private IndexedVariable mTouchBeginY;
    private IndexedVariable mTouchX;
    private IndexedVariable mTouchY;
    protected VariableBinderManager mVariableBinderManager;
    private VariableUpdaterManager mVariableUpdaterManager;
    private int mVersion;
    private ViewManager mViewManager;
    private float mWidth;

    /* renamed from: com.miui.maml.ScreenElementRoot$4  reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType = new int[ExtraResource.MetricsType.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|8) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        static {
            /*
                com.miui.maml.ScreenElementRoot$ExtraResource$MetricsType[] r0 = com.miui.maml.ScreenElementRoot.ExtraResource.MetricsType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType = r0
                int[] r0 = $SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.ScreenElementRoot$ExtraResource$MetricsType r1 = com.miui.maml.ScreenElementRoot.ExtraResource.MetricsType.DEN     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.ScreenElementRoot$ExtraResource$MetricsType r1 = com.miui.maml.ScreenElementRoot.ExtraResource.MetricsType.SW     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.ScreenElementRoot$ExtraResource$MetricsType r1 = com.miui.maml.ScreenElementRoot.ExtraResource.MetricsType.SW_DEN     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ScreenElementRoot.AnonymousClass4.<clinit>():void");
        }
    }

    private static class ExtraResource {
        private ArrayList<ScaleMetrics> mResources = new ArrayList<>();
        private ArrayList<ScaleMetrics> mScales = new ArrayList<>();

        enum MetricsType {
            DEN,
            SW,
            SW_DEN
        }

        class Resource extends ScaleMetrics {
            String mPath;

            public Resource() {
                super();
            }

            public Resource(String str, MetricsType metricsType) {
                super(str, metricsType);
                int i;
                StringBuilder sb;
                int i2 = AnonymousClass4.$SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType[metricsType.ordinal()];
                if (i2 == 1) {
                    sb = new StringBuilder();
                    sb.append("den");
                    i = this.mDensity;
                } else if (i2 == 2) {
                    sb = new StringBuilder();
                    sb.append("sw");
                    i = this.mScreenWidth;
                } else {
                    return;
                }
                sb.append(i);
                this.mPath = sb.toString();
            }

            /* access modifiers changed from: protected */
            public void onParseInfo(String[] strArr) {
                this.mPath = strArr[strArr.length <= 2 ? (char) 0 : 1];
            }

            public String toString() {
                return super.toString() + " path:" + this.mPath;
            }
        }

        class ScaleMetrics {
            int mDensity;
            float mScale = 1.0f;
            int mScreenWidth;
            int mSizeType;

            public ScaleMetrics() {
            }

            public ScaleMetrics(String str, MetricsType metricsType) {
                String str2;
                try {
                    String[] split = str.split(":");
                    int i = AnonymousClass4.$SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType[metricsType.ordinal()];
                    char c2 = 1;
                    if (i == 1) {
                        this.mDensity = Integer.parseInt(split[0]);
                        this.mScreenWidth = (ResourceManager.translateDensity(this.mDensity) * ScreenElementRoot.DEFAULT_SCREEN_WIDTH) / 240;
                        if (split.length > 1) {
                            str2 = split[1];
                        } else {
                            return;
                        }
                    } else if (i == 2) {
                        this.mScreenWidth = Integer.parseInt(split[0]);
                        this.mDensity = ResourceManager.retranslateDensity((this.mScreenWidth * 240) / ScreenElementRoot.DEFAULT_SCREEN_WIDTH);
                        if (split.length > 1) {
                            str2 = split[1];
                        } else {
                            return;
                        }
                    } else if (i == 3) {
                        String[] split2 = split[0].split("-");
                        this.mSizeType = 0;
                        if (split2.length == 1) {
                            if (split2[0].startsWith("sw")) {
                                this.mScreenWidth = Integer.parseInt(split2[0].substring(2));
                                this.mDensity = ResourceManager.retranslateDensity((this.mScreenWidth * 240) / ScreenElementRoot.DEFAULT_SCREEN_WIDTH);
                            } else if (split2[0].startsWith("den")) {
                                this.mDensity = Integer.parseInt(split2[0].substring(3));
                                this.mScreenWidth = (ResourceManager.translateDensity(this.mDensity) * ScreenElementRoot.DEFAULT_SCREEN_WIDTH) / 240;
                            } else {
                                throw new IllegalArgumentException("invalid format: " + str);
                            }
                        } else if (split2.length >= 2) {
                            this.mScreenWidth = Integer.parseInt(split2[0].substring(2));
                            this.mDensity = Integer.parseInt(split2[1].substring(3));
                            if (split2.length == 3) {
                                this.mSizeType = ExtraResource.parseSizeType(split2[2]);
                            }
                        } else {
                            throw new IllegalArgumentException("invalid format: " + str);
                        }
                        if (split.length > 1) {
                            if (split.length != 2) {
                                c2 = 2;
                            }
                            this.mScale = Float.parseFloat(split[c2]);
                        }
                        onParseInfo(split);
                        return;
                    } else {
                        return;
                    }
                    this.mScale = Float.parseFloat(str2);
                } catch (NumberFormatException unused) {
                    Log.w(ScreenElementRoot.LOG_TAG, "format error of string: " + str);
                    throw new IllegalArgumentException("invalid format");
                }
            }

            /* access modifiers changed from: protected */
            public void onParseInfo(String[] strArr) {
            }

            public String toString() {
                return "ScaleMetrics sw:" + this.mScreenWidth + " den:" + this.mDensity + " sizeType:" + this.mSizeType + " scale:" + this.mScale;
            }
        }

        public ExtraResource(Element element, int i) {
            Resource resource = new Resource();
            resource.mDensity = i;
            resource.mScreenWidth = (ResourceManager.translateDensity(i) * ScreenElementRoot.DEFAULT_SCREEN_WIDTH) / 240;
            resource.mSizeType = 0;
            resource.mPath = null;
            resource.mScale = 1.0f;
            this.mResources.add(resource);
            inflateMetrics(this.mResources, element.getAttribute("extraResourcesDensity"), MetricsType.DEN);
            inflateMetrics(this.mResources, element.getAttribute("extraResourcesScreenWidth"), MetricsType.SW);
            inflateMetrics(this.mResources, element.getAttribute("extraResources"), MetricsType.SW_DEN);
            ScaleMetrics scaleMetrics = new ScaleMetrics();
            scaleMetrics.mDensity = i;
            scaleMetrics.mScreenWidth = (ResourceManager.translateDensity(i) * ScreenElementRoot.DEFAULT_SCREEN_WIDTH) / 240;
            scaleMetrics.mSizeType = 0;
            scaleMetrics.mScale = -1.0f;
            this.mScales.add(scaleMetrics);
            inflateMetrics(this.mScales, element.getAttribute("extraScaleByDensity"), MetricsType.DEN);
            inflateMetrics(this.mScales, element.getAttribute("extraScaleByScreenWidth"), MetricsType.SW);
            inflateMetrics(this.mScales, element.getAttribute("extraScales"), MetricsType.SW_DEN);
        }

        private void inflateMetrics(ArrayList<ScaleMetrics> arrayList, String str, MetricsType metricsType) {
            Object scaleMetrics;
            if (!TextUtils.isEmpty(str)) {
                for (String str2 : str.split(",")) {
                    try {
                        if (arrayList == this.mResources) {
                            scaleMetrics = new Resource(str2.trim(), metricsType);
                        } else if (arrayList == this.mScales) {
                            scaleMetrics = new ScaleMetrics(str2.trim(), metricsType);
                        }
                        arrayList.add(scaleMetrics);
                    } catch (IllegalArgumentException unused) {
                        Log.w(ScreenElementRoot.LOG_TAG, "format error of attribute: " + str);
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public static int parseSizeType(String str) {
            if ("small".equals(str)) {
                return 1;
            }
            if ("normal".equals(str)) {
                return 2;
            }
            if ("large".equals(str)) {
                return 3;
            }
            return "xlarge".equals(str) ? 4 : 0;
        }

        /* access modifiers changed from: package-private */
        public ScaleMetrics findMetrics(int i, int i2, int i3, ArrayList<ScaleMetrics> arrayList) {
            ArrayList arrayList2 = new ArrayList();
            Iterator<ScaleMetrics> it = arrayList.iterator();
            int i4 = Integer.MAX_VALUE;
            int i5 = Integer.MAX_VALUE;
            while (it.hasNext()) {
                ScaleMetrics next = it.next();
                int i6 = next.mSizeType;
                if (i6 == 0 || i6 == i3) {
                    int abs = Math.abs(i - next.mDensity);
                    if (abs < i4) {
                        int abs2 = Math.abs(i2 - next.mScreenWidth);
                        arrayList2.clear();
                        arrayList2.add(next);
                        i5 = abs2;
                        i4 = abs;
                    } else if (abs == i4) {
                        int abs3 = Math.abs(i2 - next.mScreenWidth);
                        if (abs3 < i5) {
                            arrayList2.clear();
                            arrayList2.add(next);
                            i5 = abs3;
                        } else if (abs3 == i5) {
                            arrayList2.add(next);
                        }
                    }
                }
            }
            Iterator it2 = arrayList2.iterator();
            ScaleMetrics scaleMetrics = null;
            while (it2.hasNext()) {
                ScaleMetrics scaleMetrics2 = (ScaleMetrics) it2.next();
                int i7 = scaleMetrics2.mSizeType;
                if (i7 == i3) {
                    return scaleMetrics2;
                }
                if (i7 == 0) {
                    scaleMetrics = scaleMetrics2;
                }
            }
            return scaleMetrics;
        }

        public Resource findResource(int i, int i2, int i3) {
            return (Resource) findMetrics(i, i2, i3, this.mResources);
        }

        public ScaleMetrics findScale(int i, int i2, int i3) {
            return findMetrics(i, i2, i3, this.mScales);
        }
    }

    private static class FramerateHelper {
        private String mFramerateText;
        private TextPaint mPaint;
        private int mRealFrameRate;
        private int mShowingFramerate;
        private int mTextX;
        private int mTextY;

        public FramerateHelper() {
            this(-65536, 14, 10, 10);
        }

        public FramerateHelper(int i, int i2, int i3, int i4) {
            this.mPaint = new TextPaint();
            this.mPaint.setColor(i);
            this.mPaint.setTextSize((float) i2);
            this.mTextX = i3;
            this.mTextY = i4;
        }

        public void draw(Canvas canvas) {
            if (this.mFramerateText == null || this.mShowingFramerate != this.mRealFrameRate) {
                this.mShowingFramerate = this.mRealFrameRate;
                this.mFramerateText = String.format("FPS %d", new Object[]{Integer.valueOf(this.mShowingFramerate)});
            }
            canvas.drawText(this.mFramerateText, (float) this.mTextX, (float) this.mTextY, this.mPaint);
        }

        public void set(int i) {
            this.mRealFrameRate = i;
        }
    }

    private static class InnerGroup extends ElementGroup {
        public InnerGroup(Element element, ScreenElementRoot screenElementRoot) {
            super(element, screenElementRoot);
        }

        public final RendererController getRendererController() {
            return this.mRoot.getRendererController();
        }
    }

    public interface OnExternCommandListener {
        void onCommand(String str, Double d2, String str2);
    }

    public interface OnHoverChangeListener {
        void onHoverChange(String str);
    }

    public ScreenElementRoot(ScreenContext screenContext) {
        super((Element) null, (ScreenElementRoot) null);
        this.mRoot = this;
        this.mContext = screenContext;
        this.mVariableUpdaterManager = new VariableUpdaterManager(this);
        this.mTouchX = new IndexedVariable(VariableNames.VAR_TOUCH_X, getContext().mVariables, true);
        this.mTouchY = new IndexedVariable(VariableNames.VAR_TOUCH_Y, getContext().mVariables, true);
        this.mTouchBeginX = new IndexedVariable(VariableNames.VAR_TOUCH_BEGIN_X, getContext().mVariables, true);
        this.mTouchBeginY = new IndexedVariable(VariableNames.VAR_TOUCH_BEGIN_Y, getContext().mVariables, true);
        this.mTouchBeginTime = new IndexedVariable(VariableNames.VAR_TOUCH_BEGIN_TIME, getContext().mVariables, true);
        this.mNeedDisallowInterceptTouchEventVar = new IndexedVariable(VariableNames.VAR_INTECEPT_SYS_TOUCH, getContext().mVariables, true);
        this.mSoundManager = new SoundManager(this.mContext);
        this.mSystemExternCommandListener = new SystemCommandListener(this);
    }

    private void loadConfig(String str) {
        if (str != null) {
            this.mConfig = new ConfigFile();
            if (!this.mConfig.load(str)) {
                this.mConfig.loadDefaultSettings(this.mContext.mResourceManager.getConfigRoot());
            }
            for (ConfigFile.Variable next : this.mConfig.getVariables()) {
                if (TextUtils.equals(next.type, "string")) {
                    Utils.putVariableString(next.name, this.mContext.mVariables, next.value);
                } else if (TextUtils.equals(next.type, "number")) {
                    try {
                        Utils.putVariableNumber(next.name, this.mContext.mVariables, Double.parseDouble(next.value));
                    } catch (NumberFormatException unused) {
                    }
                }
            }
            for (Task next2 : this.mConfig.getTasks()) {
                Variables variables = this.mContext.mVariables;
                variables.put(next2.id + ".name", (Object) next2.name);
                Variables variables2 = this.mContext.mVariables;
                variables2.put(next2.id + ".package", (Object) next2.packageName);
                Variables variables3 = this.mContext.mVariables;
                variables3.put(next2.id + ".class", (Object) next2.className);
            }
        }
    }

    private void loadRawAttrs(Element element) {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            this.mRawAttrs.put(item.getNodeName(), item.getNodeValue());
        }
    }

    private void processUseVariableUpdater(Element element) {
        String attribute = element.getAttribute("useVariableUpdater");
        if (TextUtils.isEmpty(attribute)) {
            onAddVariableUpdater(this.mVariableUpdaterManager);
        } else {
            this.mVariableUpdaterManager.addFromTag(attribute);
        }
    }

    private void setupScale(Element element) {
        String attribute = element.getAttribute("scaleByDensity");
        if (!TextUtils.isEmpty(attribute)) {
            this.mScaleByDensity = Boolean.parseBoolean(attribute);
        }
        this.mDefaultScreenWidth = Utils.getAttrAsInt(element, "defaultScreenWidth", 0);
        if (this.mDefaultScreenWidth == 0) {
            this.mDefaultScreenWidth = Utils.getAttrAsInt(element, "screenWidth", 0);
        }
        this.mRawDefaultResourceDensity = Utils.getAttrAsInt(element, "defaultResourceDensity", 0);
        if (this.mRawDefaultResourceDensity == 0) {
            this.mRawDefaultResourceDensity = Utils.getAttrAsInt(element, "resDensity", 0);
        }
        this.mDefaultResourceDensity = ResourceManager.translateDensity(this.mRawDefaultResourceDensity);
        if (this.mDefaultScreenWidth == 0 && this.mDefaultResourceDensity == 0) {
            this.mDefaultScreenWidth = DEFAULT_SCREEN_WIDTH;
            this.mDefaultResourceDensity = 240;
        } else {
            int i = this.mDefaultResourceDensity;
            if (i == 0) {
                this.mDefaultResourceDensity = (this.mDefaultScreenWidth * 240) / DEFAULT_SCREEN_WIDTH;
            } else if (this.mDefaultScreenWidth == 0) {
                this.mDefaultScreenWidth = (i * DEFAULT_SCREEN_WIDTH) / 240;
            }
        }
        this.mContext.mResourceManager.setDefaultResourceDensity(this.mDefaultResourceDensity);
        Display defaultDisplay = ((WindowManager) this.mContext.mContext.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        int rotation = defaultDisplay.getRotation();
        boolean z = true;
        if (!(rotation == 1 || rotation == 3)) {
            z = false;
        }
        this.mTargetScreenWidth = z ? point.y : point.x;
        this.mTargetScreenHeight = z ? point.x : point.y;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        this.mRawTargetDensity = displayMetrics.densityDpi;
        int i2 = this.mContext.mContext.getResources().getConfiguration().screenLayout & 15;
        int i3 = this.mRawDefaultResourceDensity;
        if (i3 == 0) {
            i3 = (this.mDefaultScreenWidth * 240) / DEFAULT_SCREEN_WIDTH;
        }
        ExtraResource extraResource = new ExtraResource(element, i3);
        ExtraResource.Resource findResource = extraResource.findResource(this.mRawTargetDensity, this.mTargetScreenWidth, i2);
        Log.d(LOG_TAG, "findResource: " + findResource.toString());
        this.mContext.mResourceManager.setExtraResource(findResource.mPath, (int) (((float) ResourceManager.translateDensity(findResource.mDensity)) / findResource.mScale));
        ExtraResource.ScaleMetrics findScale = extraResource.findScale(this.mRawTargetDensity, this.mTargetScreenWidth, i2);
        Log.d(LOG_TAG, "findScale: " + findScale.toString());
        if (this.mScaleByDensity) {
            this.mTargetDensity = ResourceManager.translateDensity(this.mRawTargetDensity);
            float f = findScale.mScale;
            if (f <= 0.0f) {
                this.mScale = ((float) this.mTargetDensity) / ((float) this.mDefaultResourceDensity);
            } else {
                this.mScale = f * ((((float) this.mRawTargetDensity) * 1.0f) / ((float) findScale.mDensity));
            }
        } else {
            int i4 = this.mTargetScreenWidth;
            this.mScale = ((float) i4) / ((float) this.mDefaultScreenWidth);
            this.mTargetDensity = (int) (((float) this.mDefaultResourceDensity) * this.mScale);
            float f2 = findScale.mScale;
            if (f2 > 0.0f) {
                this.mScale = f2 * ((((float) i4) * 1.0f) / ((float) findScale.mScreenWidth));
            }
        }
        Log.i(LOG_TAG, "set scale: " + this.mScale);
        this.mContext.mResourceManager.setTargetDensity(this.mTargetDensity);
        this.mRawWidth = Utils.getAttrAsInt(element, "width", 0);
        this.mRawHeight = Utils.getAttrAsInt(element, "height", 0);
        this.mWidth = (float) Math.round(((float) this.mRawWidth) * this.mScale);
        this.mHeight = (float) Math.round(((float) this.mRawHeight) * this.mScale);
    }

    private void traverseElements() {
        this.mRendererControllers.clear();
        acceptVisitor(new ScreenElementVisitor() {
            public void visit(ScreenElement screenElement) {
                RendererController rendererController;
                if ((screenElement instanceof FramerateController) && (rendererController = screenElement.getRendererController()) != null) {
                    rendererController.addFramerateController((FramerateController) screenElement);
                }
                if ((screenElement instanceof ElementGroupRC) || (screenElement instanceof ScreenElementRoot)) {
                    ScreenElementRoot.this.mRendererControllers.add(screenElement.getRendererController());
                }
            }
        });
    }

    public void acceptVisitor(ScreenElementVisitor screenElementVisitor) {
        super.acceptVisitor(screenElementVisitor);
        this.mInnerGroup.acceptVisitor(screenElementVisitor);
    }

    public void addAccessibleElements(AnimatedScreenElement animatedScreenElement) {
        animatedScreenElement.setVirtualViewId(this.mAccessibleElements.size());
        this.mAccessibleElements.add(animatedScreenElement);
    }

    public void addAccessibleList(List<AnimatedScreenElement> list) {
        this.mAccessibleElements.addAll(list);
        for (AnimatedScreenElement next : list) {
            next.setVirtualViewId(this.mAccessibleElements.indexOf(next));
        }
    }

    public void addAnimationItem(String str, BaseAnimation.AnimationItem animationItem) {
        if (this.mAnimationItems.containsKey(str)) {
            this.mAnimationItems.get(str).add(animationItem);
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(animationItem);
        this.mAnimationItems.put(str, arrayList);
    }

    public void addElement(String str, WeakReference weakReference) {
        this.mElements.put(str, weakReference);
    }

    public void addPreTicker(ITicker iTicker) {
        this.mPreTickers.add(iTicker);
    }

    public boolean allowScreenRotation() {
        return this.mAllowScreenRotation;
    }

    public void attachToRenderThread(RenderThread renderThread) {
        if (renderThread == null || this.mController == null) {
            throw new NullPointerException("thread or controller is null, MUST load before attaching");
        }
        int size = this.mRendererControllers.size();
        for (int i = 0; i < size; i++) {
            renderThread.addRendererController(this.mRendererControllers.get(i));
        }
    }

    public FramerateTokenList.FramerateToken createFramerateToken(String str) {
        return createToken(str);
    }

    public void detachFromRenderThread(RenderThread renderThread) {
        if (renderThread == null || this.mController == null) {
            throw new NullPointerException("thread or controller is null, MUST load before detaching");
        }
        int size = this.mRendererControllers.size();
        for (int i = 0; i < size; i++) {
            renderThread.removeRendererController(this.mRendererControllers.get(i));
        }
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
        if (!this.mFinished) {
            if (this.mClearCanvas) {
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            }
            int i = this.mBgColor;
            if (i != 0) {
                canvas.drawColor(i);
            }
            try {
                this.mInnerGroup.render(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
                Log.e(LOG_TAG, e2.toString());
            }
            if (this.mShowFramerate) {
                this.mFramerateHelper.draw(canvas);
            }
            this.mFrames++;
            this.mController.doneRender();
        }
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        if (!this.mFinished) {
            VariableBinderManager variableBinderManager = this.mVariableBinderManager;
            if (variableBinderManager != null) {
                variableBinderManager.tick();
            }
            this.mVariableUpdaterManager.tick(j);
            int size = this.mPreTickers.size();
            for (int i = 0; i < size; i++) {
                this.mPreTickers.get(i).tick(j);
            }
            this.mInnerGroup.tick(j);
            this.mNeedDisallowInterceptTouchEvent = this.mNeedDisallowInterceptTouchEventVar.getDouble() > 0.0d;
            if (this.mFrameRateVar == null) {
                this.mFrameRateVar = new IndexedVariable(VariableNames.FRAME_RATE, this.mContext.mVariables, true);
                this.mCheckPoint = 0;
            }
            long j2 = this.mCheckPoint;
            if (j2 != 0) {
                long j3 = j - j2;
                if (j3 >= 1000) {
                    int i2 = (int) (((long) (this.mFrames * 1000)) / j3);
                    this.mFramerateHelper.set(i2);
                    this.mFrameRateVar.set((double) i2);
                    this.mFrames = 0;
                } else {
                    return;
                }
            }
            this.mCheckPoint = j;
        }
    }

    public void doneRender() {
        this.mController.doneRender();
    }

    public VariableBinder findBinder(String str) {
        VariableBinderManager variableBinderManager = this.mVariableBinderManager;
        if (variableBinderManager != null) {
            return variableBinderManager.findBinder(str);
        }
        return null;
    }

    public ScreenElement findElement(String str) {
        return ROOT_NAME.equals(str) ? this : getElement(str);
    }

    public Task findTask(String str) {
        ConfigFile configFile = this.mConfig;
        if (configFile == null) {
            return null;
        }
        return configFile.getTask(str);
    }

    public void finish() {
        if (!this.mFinished) {
            super.finish();
            Log.d(LOG_TAG, "finish");
            this.mInnerGroup.performAction("preFinish");
            this.mInnerGroup.finish();
            this.mInnerGroup.performAction("finish");
            ConfigFile configFile = this.mConfig;
            if (configFile != null) {
                configFile.save(this.mContext.mContext.getApplicationContext());
            }
            VariableBinderManager variableBinderManager = this.mVariableBinderManager;
            if (variableBinderManager != null) {
                variableBinderManager.finish();
            }
            CommandTriggers commandTriggers = this.mExternalCommandManager;
            if (commandTriggers != null) {
                commandTriggers.finish();
            }
            VariableUpdaterManager variableUpdaterManager = this.mVariableUpdaterManager;
            if (variableUpdaterManager != null) {
                variableUpdaterManager.finish();
            }
            this.mSoundManager.release();
            this.mContext.mResourceManager.finish(this.mKeepResource);
            this.mFinished = true;
            this.mKeepResource = false;
            Expression.FunctionExpression.resetFunctions();
        }
    }

    public List<AnimatedScreenElement> getAccessibleElements() {
        return this.mAccessibleElements;
    }

    public ArrayList<BaseAnimation.AnimationItem> getAnimationItems(String str) {
        return this.mAnimationItems.get(str);
    }

    public String getCacheDir() {
        return this.mCacheDir;
    }

    public boolean getCapability(int i) {
        return (i & this.mCapability) != 0;
    }

    public ScreenContext getContext() {
        return this.mContext;
    }

    public int getDefaultScreenWidth() {
        return this.mDefaultScreenWidth;
    }

    public ScreenElement getElement(String str) {
        WeakReference weakReference = this.mElements.get(str);
        if (weakReference != null) {
            return (ScreenElement) weakReference.get();
        }
        return null;
    }

    public final float getFontScale() {
        return this.mFontScale;
    }

    public float getHeight() {
        return this.mHeight;
    }

    public AnimatedScreenElement getHoverElement() {
        return this.mHoverElement;
    }

    public MamlAccessHelper getMamlAccessHelper() {
        return this.mMamlAccessHelper;
    }

    public String getRawAttr(String str) {
        return this.mRawAttrs.get(str);
    }

    public RendererController getRendererController() {
        return this.mController;
    }

    public int getResourceDensity() {
        return this.mDefaultResourceDensity;
    }

    public String getRootTag() {
        return this.mRootTag;
    }

    public final float getScale() {
        float f = this.mScale;
        if (f != 0.0f) {
            return f;
        }
        Log.w(LOG_TAG, "scale not initialized!");
        return 1.0f;
    }

    public int getScreenHeight() {
        return this.mTargetScreenHeight;
    }

    public int getScreenWidth() {
        return this.mTargetScreenWidth;
    }

    public StylesManager.Style getStyle(String str) {
        StylesManager stylesManager;
        if (!TextUtils.isEmpty(str) && (stylesManager = this.mStylesManager) != null) {
            return stylesManager.getStyle(str);
        }
        return null;
    }

    public float getSystemFrameRate() {
        return ((WindowManager) this.mContext.mContext.getSystemService("window")).getDefaultDisplay().getRefreshRate();
    }

    public int getTargetDensity() {
        return this.mTargetDensity;
    }

    public ViewManager getViewManager() {
        return this.mViewManager;
    }

    public float getWidth() {
        return this.mWidth;
    }

    public void haptic(int i) {
    }

    public void init() {
        PackageManager packageManager;
        PackageInfo packageInfo;
        Variables variables = this.mContext.mVariables;
        variables.put("__objRoot", (Object) this);
        variables.put("__objContext", (Object) this.mContext);
        super.init();
        Log.d(LOG_TAG, "init");
        requestFramerate(this.mFrameRate);
        this.mCapability = -1;
        this.mShowDebugLayout = HideSdkDependencyUtils.isShowDebugLayout();
        int i = 0;
        this.mFinished = false;
        this.mContext.mResourceManager.init();
        this.mFontScale = getContext().mContext.getResources().getConfiguration().fontScale;
        variables.put("__fontScale", (double) this.mFontScale);
        Locale locale = this.mContext.mContext.getResources().getConfiguration().locale;
        ScreenContext screenContext = this.mContext;
        LanguageHelper.load(locale, screenContext.mResourceManager, screenContext.mVariables);
        variables.put(VariableNames.RAW_SCREEN_WIDTH, (double) this.mTargetScreenWidth);
        variables.put(VariableNames.RAW_SCREEN_HEIGHT, (double) this.mTargetScreenHeight);
        variables.put(VariableNames.SCREEN_WIDTH, (double) (((float) this.mTargetScreenWidth) / this.mScale));
        variables.put(VariableNames.SCREEN_HEIGHT, (double) (((float) this.mTargetScreenHeight) / this.mScale));
        int i2 = this.mRawWidth;
        if (i2 > 0) {
            variables.put("view_width", (double) i2);
        }
        int i3 = this.mRawHeight;
        if (i3 > 0) {
            variables.put("view_height", (double) i3);
        }
        variables.put("view_width", (double) (((float) this.mTargetScreenWidth) / this.mScale));
        variables.put("view_height", (double) (((float) this.mTargetScreenHeight) / this.mScale));
        variables.put(RAW_DENSITY, (double) this.mRawTargetDensity);
        variables.put(SCALE_FACTOR, (double) this.mScale);
        variables.put(VAR_MAML_VERSION, 5.0d);
        try {
            if (!(this.mContext == null || this.mContext.mContext == null || (packageManager = this.mContext.mContext.getPackageManager()) == null || (packageInfo = packageManager.getPackageInfo(THEMEMANAGER_PACKAGE_NAME, 0)) == null)) {
                i = packageInfo.versionCode;
            }
        } catch (Exception unused) {
            Log.e(LOG_TAG, "thememanager not found");
        }
        variables.put(VariableNames.VAR_THEMEMANAGER_VERSION, (double) i);
        variables.put(VariableNames.VAR_MIUI_VERSION_NAME, (Object) SystemProperties.get(MIUI_VERSION_NAME));
        variables.put(VariableNames.VAR_MIUI_VERSION_CODE, (Object) SystemProperties.get(MIUI_VERSION_CODE));
        variables.put(VariableNames.VAR_ANDROID_VERSION, (Object) Build.VERSION.RELEASE);
        variables.put(VariableNames.VAR_SYSTEM_VERSION, (Object) Build.VERSION.INCREMENTAL);
        loadConfig();
        VariableUpdaterManager variableUpdaterManager = this.mVariableUpdaterManager;
        if (variableUpdaterManager != null) {
            variableUpdaterManager.init();
        }
        VariableBinderManager variableBinderManager = this.mVariableBinderManager;
        if (variableBinderManager != null) {
            variableBinderManager.init();
        }
        CommandTriggers commandTriggers = this.mExternalCommandManager;
        if (commandTriggers != null) {
            commandTriggers.init();
        }
        this.mInnerGroup.performAction("init");
        this.mInnerGroup.init();
        this.mInnerGroup.performAction("postInit");
        this.mRoot.mHoverElement = null;
        this.mNeedReset = true;
        this.mController.setNeedReset(true);
        requestUpdate();
    }

    public boolean isMamlBlurWindow() {
        return this.mBlurWindow;
    }

    public boolean isSupportAccessibilityService() {
        return this.mSupportAccessibilityService;
    }

    public void issueExternCommand(String str, Double d2, String str2) {
        OnExternCommandListener onExternCommandListener;
        OnExternCommandListener onExternCommandListener2;
        this.mSystemExternCommandListener.onCommand(str, d2, str2);
        WeakReference<OnExternCommandListener> weakReference = this.mExternCommandListener;
        if (!(weakReference == null || (onExternCommandListener2 = (OnExternCommandListener) weakReference.get()) == null)) {
            onExternCommandListener2.onCommand(str, d2, str2);
            Log.d(LOG_TAG, "issueExternCommand: " + str + " " + d2 + " " + str2);
        }
        WeakReference<OnExternCommandListener> weakReference2 = this.mMamlViewExternCommandListener;
        if (weakReference2 != null && (onExternCommandListener = (OnExternCommandListener) weakReference2.get()) != null) {
            onExternCommandListener.onCommand(str, d2, str2);
            Log.d(LOG_TAG, "issueExternCommand to MamlView: " + str + " " + d2 + " " + str2);
        }
    }

    public final boolean load() {
        try {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            Element manifestRoot = this.mContext.mResourceManager.getManifestRoot();
            if (manifestRoot == null) {
                Log.e(LOG_TAG, "load error, manifest root is null");
                return false;
            }
            this.mRootTag = manifestRoot.getNodeName();
            loadRawAttrs(manifestRoot);
            processUseVariableUpdater(manifestRoot);
            setupScale(manifestRoot);
            this.mVariableBinderManager = new VariableBinderManager(Utils.getChild(manifestRoot, VariableBinderManager.TAG_NAME), this);
            Element child = Utils.getChild(manifestRoot, EXTERNAL_COMMANDS_TAG_NAME);
            if (child != null) {
                this.mExternalCommandManager = new CommandTriggers(child, this);
            }
            Element child2 = Utils.getChild(manifestRoot, "Styles");
            if (child2 != null) {
                this.mStylesManager = new StylesManager(child2);
            }
            this.mFrameRate = Utils.getAttrAsFloat(manifestRoot, "frameRate", this.DEFAULT_FRAME_RATE);
            this.mClearCanvas = Boolean.parseBoolean(manifestRoot.getAttribute("clearCanvas"));
            this.mSupportAccessibilityService = Boolean.parseBoolean(manifestRoot.getAttribute("supportAccessibityService"));
            this.mAllowScreenRotation = Boolean.parseBoolean(manifestRoot.getAttribute("allowScreenRotation"));
            this.mBlurWindow = Boolean.parseBoolean(manifestRoot.getAttribute("blurWindow"));
            this.mController = new RendererController();
            this.mInnerGroup = new InnerGroup(manifestRoot, this);
            if (this.mInnerGroup.getElements().size() <= 0) {
                Log.e(LOG_TAG, "load error, no element loaded");
                return false;
            }
            this.mVersion = Utils.getAttrAsInt(manifestRoot, "version", 1);
            if (!onLoad(manifestRoot)) {
                Log.e(LOG_TAG, "load error, onLoad fail");
                return false;
            }
            traverseElements();
            Log.d(LOG_TAG, "load finished, spent " + (SystemClock.elapsedRealtime() - elapsedRealtime) + " ms");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadConfig() {
        loadConfig(this.mConfigPath);
    }

    public boolean needDisallowInterceptTouchEvent() {
        return this.mNeedDisallowInterceptTouchEvent;
    }

    /* access modifiers changed from: protected */
    public void onAddVariableUpdater(VariableUpdaterManager variableUpdaterManager) {
        variableUpdaterManager.add(new DateTimeVariableUpdater(variableUpdaterManager));
    }

    public void onCommand(final String str) {
        if (this.mExternalCommandManager != null) {
            postRunnable(new Runnable() {
                public void run() {
                    try {
                        ScreenElementRoot.this.mExternalCommandManager.onAction(str);
                    } catch (Exception e) {
                        Log.e(ScreenElementRoot.LOG_TAG, e.toString());
                        e.printStackTrace();
                    }
                }
            });
            requestUpdate();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (this.mAllowScreenRotation) {
            setConfiguration(configuration);
            onCommand("orientationChange");
            requestUpdate();
        }
    }

    public boolean onHover(MotionEvent motionEvent) {
        if (this.mFinished) {
            return false;
        }
        return this.mInnerGroup.onHover(motionEvent);
    }

    public void onHoverChange(AnimatedScreenElement animatedScreenElement, String str) {
        this.mHoverElement = animatedScreenElement;
        WeakReference<OnHoverChangeListener> weakReference = this.mHoverChangeListenerRef;
        OnHoverChangeListener onHoverChangeListener = weakReference != null ? (OnHoverChangeListener) weakReference.get() : null;
        if (onHoverChangeListener != null) {
            onHoverChangeListener.onHoverChange(str);
        }
    }

    /* access modifiers changed from: protected */
    public boolean onLoad(Element element) {
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0095  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.MotionEvent r8) {
        /*
            r7 = this;
            boolean r0 = r7.mFinished
            r1 = 0
            if (r0 == 0) goto L_0x0006
            return r1
        L_0x0006:
            com.miui.maml.elements.AnimatedScreenElement r0 = r7.mHoverElement
            r2 = 1
            if (r0 == 0) goto L_0x0051
            float r0 = r0.getWidth()
            com.miui.maml.elements.AnimatedScreenElement r1 = r7.mHoverElement
            float r1 = r1.getHeight()
            com.miui.maml.elements.AnimatedScreenElement r3 = r7.mHoverElement
            float r3 = r3.getAbsoluteLeft()
            r4 = 1073741824(0x40000000, float:2.0)
            float r0 = r0 / r4
            float r3 = r3 + r0
            com.miui.maml.elements.AnimatedScreenElement r0 = r7.mHoverElement
            float r0 = r0.getAbsoluteTop()
            float r1 = r1 / r4
            float r0 = r0 + r1
            android.graphics.Matrix r1 = r7.mHoverMatrix
            float r4 = r8.getX()
            float r3 = r3 - r4
            float r4 = r8.getY()
            float r0 = r0 - r4
            r1.setTranslate(r3, r0)
            android.graphics.Matrix r0 = r7.mHoverMatrix
            r8.transform(r0)
            com.miui.maml.elements.AnimatedScreenElement r0 = r7.mHoverElement
            r0.onTouch(r8)
            int r0 = r8.getActionMasked()
            if (r0 == r2) goto L_0x004d
            int r8 = r8.getActionMasked()
            r0 = 3
            if (r8 != r0) goto L_0x0050
        L_0x004d:
            r8 = 0
            r7.mHoverElement = r8
        L_0x0050:
            return r2
        L_0x0051:
            float r0 = r8.getX()
            double r3 = (double) r0
            double r3 = r7.descale(r3)
            float r0 = r8.getY()
            double r5 = (double) r0
            double r5 = r7.descale(r5)
            com.miui.maml.data.IndexedVariable r0 = r7.mTouchX
            r0.set((double) r3)
            com.miui.maml.data.IndexedVariable r0 = r7.mTouchY
            r0.set((double) r5)
            int r0 = r8.getActionMasked()
            if (r0 == 0) goto L_0x0077
            if (r0 == r2) goto L_0x008b
            r1 = 2
            goto L_0x008d
        L_0x0077:
            com.miui.maml.data.IndexedVariable r0 = r7.mTouchBeginX
            r0.set((double) r3)
            com.miui.maml.data.IndexedVariable r0 = r7.mTouchBeginY
            r0.set((double) r5)
            com.miui.maml.data.IndexedVariable r0 = r7.mTouchBeginTime
            long r2 = java.lang.System.currentTimeMillis()
            double r2 = (double) r2
            r0.set((double) r2)
        L_0x008b:
            r7.mNeedDisallowInterceptTouchEvent = r1
        L_0x008d:
            com.miui.maml.elements.ElementGroup r0 = r7.mInnerGroup
            boolean r8 = r0.onTouch(r8)
            if (r8 != 0) goto L_0x009a
            com.miui.maml.RendererController r0 = r7.mController
            r0.requestUpdate()
        L_0x009a:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ScreenElementRoot.onTouch(android.view.MotionEvent):boolean");
    }

    public void onUIInteractive(ScreenElement screenElement, String str) {
    }

    public void pause() {
        super.pause();
        Log.d(LOG_TAG, "pause");
        this.mInnerGroup.performAction("pause");
        this.mInnerGroup.pause();
        this.mSoundManager.pause();
        VariableBinderManager variableBinderManager = this.mVariableBinderManager;
        if (variableBinderManager != null) {
            variableBinderManager.pause();
        }
        CommandTriggers commandTriggers = this.mExternalCommandManager;
        if (commandTriggers != null) {
            commandTriggers.pause();
        }
        VariableUpdaterManager variableUpdaterManager = this.mVariableUpdaterManager;
        if (variableUpdaterManager != null) {
            variableUpdaterManager.pause();
        }
        this.mContext.mResourceManager.pause();
        onHoverChange((AnimatedScreenElement) null, (String) null);
        ConfigFile configFile = this.mConfig;
        if (configFile != null) {
            configFile.save(this.mContext.mContext.getApplicationContext());
        }
    }

    /* access modifiers changed from: protected */
    public void pauseAnim(long j) {
        super.pauseAnim(j);
        this.mInnerGroup.pauseAnim(j);
    }

    /* access modifiers changed from: protected */
    public void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        super.playAnim(j, j2, j3, z, z2);
        this.mInnerGroup.playAnim(j, j2, j3, z, z2);
    }

    public int playSound(String str) {
        return playSound(str, new SoundManager.SoundOptions(false, false, 1.0f));
    }

    public int playSound(String str, SoundManager.SoundOptions soundOptions) {
        if (!TextUtils.isEmpty(str) && shouldPlaySound()) {
            return this.mSoundManager.playSound(str, soundOptions);
        }
        return 0;
    }

    public void playSound(int i, SoundManager.Command command) {
        try {
            this.mSoundManager.playSound(i, command);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    public boolean postDelayed(Runnable runnable, long j) {
        if (this.mFinished) {
            return false;
        }
        return this.mContext.postDelayed(runnable, j);
    }

    public void postMessage(MotionEvent motionEvent) {
        this.mController.postMessage(motionEvent);
    }

    /* access modifiers changed from: protected */
    public String putRawAttr(String str, String str2) {
        return this.mRawAttrs.put(str, str2);
    }

    public void removeAccessibleElement(AnimatedScreenElement animatedScreenElement) {
        this.mAccessibleElements.remove(animatedScreenElement);
        animatedScreenElement.setVirtualViewId(Integer.MIN_VALUE);
    }

    public void removeAllAccessibleElements() {
        for (AnimatedScreenElement virtualViewId : this.mAccessibleElements) {
            virtualViewId.setVirtualViewId(Integer.MIN_VALUE);
        }
        this.mAccessibleElements.clear();
    }

    public void removeCallbacks(Runnable runnable) {
        this.mContext.removeCallbacks(runnable);
    }

    public void removeElement(String str) {
        this.mElements.remove(str);
    }

    public void removePreTicker(ITicker iTicker) {
        this.mPreTickers.remove(iTicker);
    }

    public void requestFrameRateByCommand(float f) {
        this.mFrameRate = f;
        requestFramerate(f);
        if (f > 0.0f) {
            requestUpdate();
        }
    }

    public void requestUpdate() {
        int size = this.mRendererControllers.size();
        for (int i = 0; i < size; i++) {
            this.mRendererControllers.get(i).requestUpdate();
        }
    }

    public void reset(long j) {
        super.reset(j);
        this.mInnerGroup.reset(j);
    }

    public void resume() {
        super.resume();
        Log.d(LOG_TAG, "resume");
        this.mShowDebugLayout = HideSdkDependencyUtils.isShowDebugLayout();
        this.mInnerGroup.performAction("resume");
        this.mInnerGroup.resume();
        VariableBinderManager variableBinderManager = this.mVariableBinderManager;
        if (variableBinderManager != null) {
            variableBinderManager.resume();
        }
        CommandTriggers commandTriggers = this.mExternalCommandManager;
        if (commandTriggers != null) {
            commandTriggers.resume();
        }
        VariableUpdaterManager variableUpdaterManager = this.mVariableUpdaterManager;
        if (variableUpdaterManager != null) {
            variableUpdaterManager.resume();
        }
        this.mContext.mResourceManager.resume();
    }

    /* access modifiers changed from: protected */
    public void resumeAnim(long j) {
        super.resumeAnim(j);
        this.mInnerGroup.resumeAnim(j);
    }

    public void saveVar(String str, Double d2) {
        ConfigFile configFile = this.mConfig;
        if (configFile == null) {
            Log.w(LOG_TAG, "fail to saveVar, config file is null");
        } else if (d2 == null) {
            configFile.putNumber(str, "null");
        } else {
            configFile.putNumber(str, d2.doubleValue());
        }
    }

    public void saveVar(String str, String str2) {
        ConfigFile configFile = this.mConfig;
        if (configFile == null) {
            Log.w(LOG_TAG, "fail to saveVar, config file is null");
        } else {
            configFile.putString(str, str2);
        }
    }

    public void selfFinish() {
        this.mController.finish();
    }

    public void selfInit() {
        this.mController.init();
    }

    public void selfPause() {
        int size = this.mRendererControllers.size();
        for (int i = 0; i < size; i++) {
            this.mRendererControllers.get(i).selfPause();
        }
    }

    public void selfResume() {
        int size = this.mRendererControllers.size();
        for (int i = 0; i < size; i++) {
            this.mRendererControllers.get(i).selfResume();
        }
    }

    public void setBgColor(int i) {
        this.mBgColor = i;
    }

    public void setCacheDir(String str) {
        this.mCacheDir = str;
    }

    public void setCapability(int i, boolean z) {
        int i2;
        if (z) {
            i2 = i | this.mCapability;
        } else {
            i2 = (~i) & this.mCapability;
        }
        this.mCapability = i2;
    }

    public void setClearCanvas(boolean z) {
        this.mClearCanvas = z;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        ElementGroup elementGroup = this.mInnerGroup;
        if (elementGroup != null) {
            elementGroup.setColorFilter(colorFilter);
        }
    }

    public void setConfig(String str) {
        this.mConfigPath = str;
    }

    public void setConfiguration(Configuration configuration) {
        int i;
        if (this.mAllowScreenRotation) {
            Variables variables = this.mContext.mVariables;
            Utils.putVariableNumber(VariableNames.ORIENTATION, variables, Double.valueOf((double) configuration.orientation));
            int i2 = configuration.orientation;
            if (i2 == 1) {
                variables.put(VariableNames.RAW_SCREEN_WIDTH, (double) this.mTargetScreenWidth);
                variables.put(VariableNames.RAW_SCREEN_HEIGHT, (double) this.mTargetScreenHeight);
                variables.put(VariableNames.SCREEN_WIDTH, (double) (((float) this.mTargetScreenWidth) / this.mScale));
                i = this.mTargetScreenHeight;
            } else if (i2 == 2) {
                variables.put(VariableNames.RAW_SCREEN_WIDTH, (double) this.mTargetScreenHeight);
                variables.put(VariableNames.RAW_SCREEN_HEIGHT, (double) this.mTargetScreenWidth);
                variables.put(VariableNames.SCREEN_WIDTH, (double) (((float) this.mTargetScreenHeight) / this.mScale));
                i = this.mTargetScreenWidth;
            } else {
                return;
            }
            variables.put(VariableNames.SCREEN_HEIGHT, (double) (((float) i) / this.mScale));
        }
    }

    public void setDefaultFramerate(float f) {
        this.DEFAULT_FRAME_RATE = f;
    }

    public final void setKeepResource(boolean z) {
        this.mKeepResource = z;
    }

    public void setMamlAccessHelper(MamlAccessHelper mamlAccessHelper) {
        this.mMamlAccessHelper = mamlAccessHelper;
    }

    public void setMamlViewOnExternCommandListener(OnExternCommandListener onExternCommandListener) {
        this.mMamlViewExternCommandListener = onExternCommandListener == null ? null : new WeakReference<>(onExternCommandListener);
    }

    public void setOnExternCommandListener(OnExternCommandListener onExternCommandListener) {
        this.mExternCommandListener = onExternCommandListener == null ? null : new WeakReference<>(onExternCommandListener);
    }

    public void setOnHoverChangeListener(OnHoverChangeListener onHoverChangeListener) {
        this.mHoverChangeListenerRef = new WeakReference<>(onHoverChangeListener);
    }

    public void setRenderControllerListener(RendererController.Listener listener) {
        this.mController.setListener(listener);
    }

    public void setRenderControllerRenderable(RendererController.IRenderable iRenderable) {
        setRenderControllerListener(new SingleRootListener(this, iRenderable));
    }

    public void setScaleByDensity(boolean z) {
        this.mScaleByDensity = z;
    }

    public void setViewManager(ViewManager viewManager) {
        this.mViewManager = viewManager;
    }

    /* access modifiers changed from: protected */
    public boolean shouldPlaySound() {
        return true;
    }

    public void showCategory(String str, boolean z) {
        this.mInnerGroup.showCategory(str, z);
    }

    public void showFramerate(boolean z) {
        this.mShowFramerate = z;
    }

    public void tick(final long j) {
        if (this.mNeedReset) {
            postRunnableAtFrontOfQueue(new Runnable() {
                public void run() {
                    ScreenElementRoot.this.reset(j);
                }
            });
            onCommand("init");
            this.mNeedReset = false;
            this.mController.setNeedReset(false);
        }
        doTick(j);
    }

    public long update(long j) {
        int size = this.mRendererControllers.size();
        long j2 = Long.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            RendererController rendererController = this.mRendererControllers.get(i);
            if (!rendererController.isSelfPaused() || rendererController.hasRunnable()) {
                long update = rendererController.update(j);
                if (update < j2) {
                    j2 = update;
                }
            }
        }
        return j2;
    }

    public long updateIfNeeded(long j) {
        int size = this.mRendererControllers.size();
        long j2 = Long.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            RendererController rendererController = this.mRendererControllers.get(i);
            if (!rendererController.isSelfPaused() || rendererController.hasRunnable()) {
                long updateIfNeeded = rendererController.updateIfNeeded(j);
                if (updateIfNeeded < j2) {
                    j2 = updateIfNeeded;
                }
            }
        }
        return j2;
    }

    public final int version() {
        return this.mVersion;
    }
}

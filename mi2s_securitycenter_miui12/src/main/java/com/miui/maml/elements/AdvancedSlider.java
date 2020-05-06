package com.miui.maml.elements;

import android.content.Intent;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ActionCommand;
import com.miui.maml.CommandTrigger;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.animation.interpolater.InterpolatorHelper;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.VariableNames;
import com.miui.maml.data.Variables;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.util.IntentInfo;
import com.miui.maml.util.Task;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import miui.cloud.CloudPushConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AdvancedSlider extends ElementGroup {
    private static final boolean DEBUG = false;
    private static final int DEFAULT_DRAG_TOLERANCE = 150;
    private static final float FREE_ENDPOINT_DIST = 1.7014117E38f;
    private static final String LOG_TAG = "LockScreen_AdvancedSlider";
    public static final String MOVE_DIST = "move_dist";
    public static final String MOVE_X = "move_x";
    public static final String MOVE_Y = "move_y";
    private static final float NONE_ENDPOINT_DIST = Float.MAX_VALUE;
    public static final int SLIDER_STATE_NORMAL = 0;
    public static final int SLIDER_STATE_PRESSED = 1;
    public static final int SLIDER_STATE_REACHED = 2;
    public static final String STATE = "state";
    public static final String TAG_NAME = "Slider";
    private EndPoint mCurrentEndPoint;
    private ArrayList<EndPoint> mEndPoints;
    protected boolean mIsHaptic;
    private boolean mIsKeepStatusAfterLaunch;
    private IndexedVariable mMoveDistVar;
    private IndexedVariable mMoveXVar;
    private IndexedVariable mMoveYVar;
    private boolean mMoving;
    private OnLaunchListener mOnLaunchListener;
    private ReboundAnimationController mReboundAnimationController;
    /* access modifiers changed from: private */
    public StartPoint mStartPoint;
    /* access modifiers changed from: private */
    public boolean mStartPointPressed;
    private IndexedVariable mStateVar;
    /* access modifiers changed from: private */
    public float mTouchOffsetX;
    /* access modifiers changed from: private */
    public float mTouchOffsetY;

    /* renamed from: com.miui.maml.elements.AdvancedSlider$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$AdvancedSlider$State = new int[State.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|8) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        static {
            /*
                com.miui.maml.elements.AdvancedSlider$State[] r0 = com.miui.maml.elements.AdvancedSlider.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$elements$AdvancedSlider$State = r0
                int[] r0 = $SwitchMap$com$miui$maml$elements$AdvancedSlider$State     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.elements.AdvancedSlider$State r1 = com.miui.maml.elements.AdvancedSlider.State.Normal     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$elements$AdvancedSlider$State     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.elements.AdvancedSlider$State r1 = com.miui.maml.elements.AdvancedSlider.State.Pressed     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$elements$AdvancedSlider$State     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.elements.AdvancedSlider$State r1 = com.miui.maml.elements.AdvancedSlider.State.Reached     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.AdvancedSlider.AnonymousClass1.<clinit>():void");
        }
    }

    private class CheckTouchResult {
        public EndPoint endPoint;
        public boolean reached;

        private CheckTouchResult() {
        }

        /* synthetic */ CheckTouchResult(AdvancedSlider advancedSlider, AnonymousClass1 r2) {
            this();
        }
    }

    private class EndPoint extends SliderPoint {
        public static final String TAG_NAME = "EndPoint";
        public LaunchAction mAction;
        /* access modifiers changed from: private */
        public ArrayList<Position> mPath;
        private Expression mPathX;
        private Expression mPathY;
        private int mRawTolerance = AdvancedSlider.DEFAULT_DRAG_TOLERANCE;
        private float mTolerance;

        public EndPoint(Element element, ScreenElementRoot screenElementRoot) {
            super(element, screenElementRoot, TAG_NAME);
            load(element);
        }

        /* access modifiers changed from: private */
        public Utils.Point getNearestPoint(float f, float f2) {
            if (this.mPath == null) {
                return new Utils.Point((double) (f - AdvancedSlider.this.mTouchOffsetX), (double) (f2 - AdvancedSlider.this.mTouchOffsetY));
            }
            double d2 = Double.MAX_VALUE;
            Utils.Point point = null;
            for (int i = 1; i < this.mPath.size(); i++) {
                Position position = this.mPath.get(i - 1);
                Position position2 = this.mPath.get(i);
                Utils.Point point2 = new Utils.Point((double) position.getX(), (double) position.getY());
                Utils.Point point3 = new Utils.Point((double) position2.getX(), (double) position2.getY());
                Utils.Point point4 = new Utils.Point((double) (f - AdvancedSlider.this.mTouchOffsetX), (double) (f2 - AdvancedSlider.this.mTouchOffsetY));
                Utils.Point pointProjectionOnSegment = Utils.pointProjectionOnSegment(point2, point3, point4, true);
                double Dist = Utils.Dist(pointProjectionOnSegment, point4, false);
                if (Dist < d2) {
                    point = pointProjectionOnSegment;
                    d2 = Dist;
                }
            }
            return point;
        }

        private void load(Element element) {
            loadTask(element);
            loadPath(element);
        }

        private void loadPath(Element element) {
            Element child = Utils.getChild(element, "Path");
            if (child == null) {
                this.mPath = null;
                return;
            }
            this.mRawTolerance = getAttrAsInt(child, "tolerance", AdvancedSlider.DEFAULT_DRAG_TOLERANCE);
            this.mPath = new ArrayList<>();
            Variables variables = getVariables();
            this.mPathX = Expression.build(variables, child.getAttribute(AnimatedProperty.PROPERTY_NAME_X));
            this.mPathY = Expression.build(variables, child.getAttribute(AnimatedProperty.PROPERTY_NAME_Y));
            NodeList elementsByTagName = child.getElementsByTagName("Position");
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                this.mPath.add(new Position(variables, (Element) elementsByTagName.item(i), this.mPathX, this.mPathY));
            }
        }

        private void loadTask(Element element) {
            Element child = Utils.getChild(element, "Intent");
            Element child2 = Utils.getChild(element, ActionCommand.TAG_NAME);
            Element child3 = Utils.getChild(element, CommandTrigger.TAG_NAME);
            if (child != null || child2 != null || child3 != null) {
                this.mAction = new LaunchAction(AdvancedSlider.this, (AnonymousClass1) null);
                if (child != null) {
                    this.mAction.mIntentInfo = new IntentInfo(child, getVariables());
                } else if (child2 != null) {
                    this.mAction.mCommand = ActionCommand.create(child2, this.mRoot);
                    if (this.mAction.mCommand == null) {
                        Log.w(AdvancedSlider.LOG_TAG, "invalid Command element: " + child2.toString());
                    }
                } else if (child3 != null) {
                    this.mAction.mTrigger = new CommandTrigger(child3, this.mRoot);
                }
            }
        }

        public void finish() {
            super.finish();
            LaunchAction launchAction = this.mAction;
            if (launchAction != null) {
                launchAction.finish();
            }
        }

        public float getTransformedDist(Utils.Point point, float f, float f2) {
            if (this.mPath == null) {
                return AdvancedSlider.FREE_ENDPOINT_DIST;
            }
            if (point == null) {
                return AdvancedSlider.NONE_ENDPOINT_DIST;
            }
            float Dist = (float) Utils.Dist(point, new Utils.Point((double) (f - AdvancedSlider.this.mTouchOffsetX), (double) (f2 - AdvancedSlider.this.mTouchOffsetY)), true);
            return Dist < this.mTolerance ? Dist : AdvancedSlider.NONE_ENDPOINT_DIST;
        }

        public void init() {
            super.init();
            LaunchAction launchAction = this.mAction;
            if (launchAction != null) {
                launchAction.init();
            }
            this.mTolerance = scale((double) this.mRawTolerance);
        }

        /* access modifiers changed from: protected */
        public void onStateChange(State state, State state2) {
            if (state != State.Invalid) {
                if (AnonymousClass1.$SwitchMap$com$miui$maml$elements$AdvancedSlider$State[state2.ordinal()] == 3) {
                    this.mRoot.playSound(this.mReachedSound);
                }
                super.onStateChange(state, state2);
            }
        }

        public void pause() {
            super.pause();
            LaunchAction launchAction = this.mAction;
            if (launchAction != null) {
                launchAction.pause();
            }
        }

        public void resume() {
            super.resume();
            LaunchAction launchAction = this.mAction;
            if (launchAction != null) {
                launchAction.resume();
            }
        }
    }

    private class InterpolatorController extends ReboundAnimationController {
        private InterpolatorHelper mInterpolator;
        private long mReboundTime;
        private Expression mReboundTimeExp;

        public InterpolatorController(InterpolatorHelper interpolatorHelper, Expression expression) {
            super(AdvancedSlider.this, (AnonymousClass1) null);
            this.mInterpolator = interpolatorHelper;
            this.mReboundTimeExp = expression;
        }

        /* access modifiers changed from: protected */
        public long getDistance(long j) {
            long j2 = this.mReboundTime;
            if (j >= j2) {
                onStop();
                return (long) this.mTotalDistance;
            }
            return (long) (this.mTotalDistance * ((double) this.mInterpolator.get(((float) j) / ((float) j2))));
        }

        /* access modifiers changed from: protected */
        public void onStart() {
            this.mReboundTime = (long) this.mReboundTimeExp.evaluate();
        }
    }

    private class LaunchAction {
        public ActionCommand mCommand;
        public boolean mConfigTaskLoaded;
        public IntentInfo mIntentInfo;
        public CommandTrigger mTrigger;

        private LaunchAction() {
        }

        /* synthetic */ LaunchAction(AdvancedSlider advancedSlider, AnonymousClass1 r2) {
            this();
        }

        private Intent performTask() {
            IntentInfo intentInfo = this.mIntentInfo;
            if (intentInfo == null) {
                return null;
            }
            if (!this.mConfigTaskLoaded) {
                Task findTask = AdvancedSlider.this.mRoot.findTask(intentInfo.getId());
                if (findTask != null && !TextUtils.isEmpty(findTask.action)) {
                    this.mIntentInfo.set(findTask);
                }
                this.mConfigTaskLoaded = true;
            }
            if (Utils.isProtectedIntent(this.mIntentInfo.getAction())) {
                return null;
            }
            Intent intent = new Intent();
            this.mIntentInfo.update(intent);
            intent.setFlags(872415232);
            return intent;
        }

        public void finish() {
            ActionCommand actionCommand = this.mCommand;
            if (actionCommand != null) {
                actionCommand.finish();
            }
            CommandTrigger commandTrigger = this.mTrigger;
            if (commandTrigger != null) {
                commandTrigger.finish();
            }
            this.mConfigTaskLoaded = false;
        }

        public void init() {
            ActionCommand actionCommand = this.mCommand;
            if (actionCommand != null) {
                actionCommand.init();
            }
            CommandTrigger commandTrigger = this.mTrigger;
            if (commandTrigger != null) {
                commandTrigger.init();
            }
        }

        public void pause() {
            ActionCommand actionCommand = this.mCommand;
            if (actionCommand != null) {
                actionCommand.pause();
            }
            CommandTrigger commandTrigger = this.mTrigger;
            if (commandTrigger != null) {
                commandTrigger.pause();
            }
        }

        public Intent perform() {
            if (this.mIntentInfo != null) {
                return performTask();
            }
            ActionCommand actionCommand = this.mCommand;
            if (actionCommand != null) {
                actionCommand.perform();
                return null;
            }
            CommandTrigger commandTrigger = this.mTrigger;
            if (commandTrigger == null) {
                return null;
            }
            commandTrigger.perform();
            return null;
        }

        public void resume() {
            ActionCommand actionCommand = this.mCommand;
            if (actionCommand != null) {
                actionCommand.resume();
            }
            CommandTrigger commandTrigger = this.mTrigger;
            if (commandTrigger != null) {
                commandTrigger.resume();
            }
        }
    }

    public interface OnLaunchListener {
        boolean onLaunch(String str);
    }

    private class Position {
        public static final String TAG_NAME = "Position";
        private Expression mBaseX;
        private Expression mBaseY;
        private Expression mX;
        private Expression mY;

        public Position(Variables variables, Element element, Expression expression, Expression expression2) {
            this.mBaseX = expression;
            this.mBaseY = expression2;
            this.mX = Expression.build(variables, AdvancedSlider.this.getAttr(element, AnimatedProperty.PROPERTY_NAME_X));
            this.mY = Expression.build(variables, AdvancedSlider.this.getAttr(element, AnimatedProperty.PROPERTY_NAME_Y));
        }

        public float getX() {
            AdvancedSlider advancedSlider = AdvancedSlider.this;
            Expression expression = this.mX;
            double d2 = 0.0d;
            double evaluate = expression == null ? 0.0d : expression.evaluate();
            Expression expression2 = this.mBaseX;
            if (expression2 != null) {
                d2 = expression2.evaluate();
            }
            return advancedSlider.scale(evaluate + d2);
        }

        public float getY() {
            AdvancedSlider advancedSlider = AdvancedSlider.this;
            Expression expression = this.mY;
            double d2 = 0.0d;
            double evaluate = expression == null ? 0.0d : expression.evaluate();
            Expression expression2 = this.mBaseY;
            if (expression2 != null) {
                d2 = expression2.evaluate();
            }
            return advancedSlider.scale(evaluate + d2);
        }
    }

    private abstract class ReboundAnimationController implements ITicker {
        private int mBounceStartPointIndex;
        private EndPoint mEndPoint;
        private long mPreDistance;
        protected long mStartTime;
        private float mStartX;
        private float mStartY;
        protected double mTotalDistance;

        private ReboundAnimationController() {
            this.mStartTime = -1;
        }

        /* synthetic */ ReboundAnimationController(AdvancedSlider advancedSlider, AnonymousClass1 r2) {
            this();
        }

        private Utils.Point getPoint(float f, float f2, float f3, float f4, long j) {
            Utils.Point point = new Utils.Point((double) f, (double) f2);
            Utils.Point point2 = new Utils.Point((double) f3, (double) f4);
            double Dist = Utils.Dist(point, point2, true);
            double d2 = (double) j;
            if (d2 >= Dist) {
                return null;
            }
            double d3 = (Dist - d2) / Dist;
            double d4 = point2.x;
            double d5 = point.x;
            double d6 = point2.y;
            double d7 = point.y;
            return new Utils.Point(d5 + ((d4 - d5) * d3), d7 + ((d6 - d7) * d3));
        }

        /* access modifiers changed from: protected */
        public abstract long getDistance(long j);

        public void init() {
            this.mStartTime = -1;
        }

        public boolean isRunning() {
            return this.mStartTime >= 0;
        }

        /* access modifiers changed from: protected */
        public void onMove(float f, float f2) {
            AdvancedSlider.this.moveStartPoint(f, f2);
        }

        /* access modifiers changed from: protected */
        public void onStart() {
        }

        /* access modifiers changed from: protected */
        public void onStop() {
            this.mStartTime = -1;
            AdvancedSlider.this.cancelMoving();
        }

        public void start(EndPoint endPoint) {
            this.mStartTime = 0;
            this.mEndPoint = endPoint;
            this.mStartX = AdvancedSlider.this.mStartPoint.getOffsetX() + AdvancedSlider.this.mStartPoint.getAnchorX();
            this.mStartY = AdvancedSlider.this.mStartPoint.getOffsetY() + AdvancedSlider.this.mStartPoint.getAnchorY();
            this.mBounceStartPointIndex = -1;
            this.mTotalDistance = 0.0d;
            Utils.Point point = new Utils.Point((double) this.mStartX, (double) this.mStartY);
            if (endPoint != null && endPoint.mPath != null) {
                int i = 1;
                while (true) {
                    if (i >= endPoint.mPath.size()) {
                        break;
                    }
                    int i2 = i - 1;
                    Position position = (Position) endPoint.mPath.get(i2);
                    Position position2 = (Position) endPoint.mPath.get(i);
                    Utils.Point point2 = new Utils.Point((double) position.getX(), (double) position.getY());
                    Utils.Point point3 = new Utils.Point((double) position2.getX(), (double) position2.getY());
                    Utils.Point pointProjectionOnSegment = Utils.pointProjectionOnSegment(point2, point3, point, false);
                    if (pointProjectionOnSegment != null) {
                        this.mBounceStartPointIndex = i2;
                        this.mTotalDistance += Utils.Dist(point2, pointProjectionOnSegment, true);
                        break;
                    }
                    this.mTotalDistance += Utils.Dist(point2, point3, true);
                    i++;
                }
            } else {
                this.mTotalDistance = Utils.Dist(new Utils.Point((double) AdvancedSlider.this.mStartPoint.getAnchorX(), (double) AdvancedSlider.this.mStartPoint.getAnchorY()), point, true);
            }
            if (this.mTotalDistance < 3.0d) {
                onStop();
                return;
            }
            onStart();
            AdvancedSlider.this.requestUpdate();
        }

        public void stopRunning() {
            this.mStartTime = -1;
        }

        public void tick(long j) {
            long j2 = this.mStartTime;
            if (j2 >= 0) {
                if (j2 == 0) {
                    this.mStartTime = j;
                    this.mPreDistance = 0;
                } else {
                    long distance = getDistance(j - j2);
                    if (this.mStartTime >= 0) {
                        EndPoint endPoint = this.mEndPoint;
                        if (endPoint != null && endPoint.mPath != null) {
                            float offsetX = AdvancedSlider.this.mStartPoint.getOffsetX() + AdvancedSlider.this.mStartPoint.getAnchorX();
                            float offsetY = AdvancedSlider.this.mStartPoint.getOffsetY() + AdvancedSlider.this.mStartPoint.getAnchorY();
                            long j3 = distance - this.mPreDistance;
                            int i = this.mBounceStartPointIndex;
                            while (true) {
                                if (i < 0) {
                                    break;
                                }
                                Position position = (Position) this.mEndPoint.mPath.get(i);
                                Utils.Point point = getPoint(position.getX(), position.getY(), offsetX, offsetY, j3);
                                if (point != null) {
                                    this.mBounceStartPointIndex = i;
                                    onMove((float) point.x, (float) point.y);
                                    break;
                                } else if (i == 0) {
                                    break;
                                } else {
                                    j3 = (long) (((double) j3) - Utils.Dist(new Utils.Point((double) position.getX(), (double) position.getY()), new Utils.Point((double) offsetX, (double) offsetY), true));
                                    offsetX = position.getX();
                                    offsetY = position.getY();
                                    i--;
                                }
                            }
                        } else {
                            Utils.Point point2 = getPoint(AdvancedSlider.this.mStartPoint.getAnchorX(), AdvancedSlider.this.mStartPoint.getAnchorY(), this.mStartX, this.mStartY, distance);
                            if (point2 != null) {
                                onMove((float) point2.x, (float) point2.y);
                                this.mPreDistance = distance;
                            }
                        }
                        onStop();
                        this.mPreDistance = distance;
                    } else {
                        return;
                    }
                }
                AdvancedSlider.this.requestUpdate();
            }
        }
    }

    private class SliderPoint extends ElementGroup {
        private ScreenElement mCurrentStateElements;
        protected boolean mIsAlignChildren;
        protected String mName;
        protected String mNormalSound;
        protected ElementGroup mNormalStateElements;
        @Deprecated
        private CommandTrigger mNormalStateTrigger;
        private IndexedVariable mPointStateVar;
        protected String mPressedSound;
        protected ElementGroup mPressedStateElements;
        @Deprecated
        private CommandTrigger mPressedStateTrigger;
        protected String mReachedSound;
        private ElementGroup mReachedStateElements;
        @Deprecated
        private CommandTrigger mReachedStateTrigger;
        private State mState = State.Invalid;

        public SliderPoint(Element element, ScreenElementRoot screenElementRoot, String str) {
            super(element, screenElementRoot);
            load(element, str);
        }

        private void load(Element element, String str) {
            this.mName = getAttr(element, CloudPushConstants.XML_NAME);
            this.mNormalSound = getAttr(element, "normalSound");
            this.mPressedSound = getAttr(element, "pressedSound");
            this.mReachedSound = getAttr(element, "reachedSound");
            this.mNormalStateTrigger = loadTrigger(element, "NormalState");
            this.mPressedStateTrigger = loadTrigger(element, "PressedState");
            this.mReachedStateTrigger = loadTrigger(element, "ReachedState");
            if (!TextUtils.isEmpty(this.mName)) {
                this.mPointStateVar = new IndexedVariable(this.mName + "." + AdvancedSlider.STATE, getVariables(), true);
            }
            this.mIsAlignChildren = Boolean.parseBoolean(getAttr(element, "alignChildren"));
        }

        private CommandTrigger loadTrigger(Element element, String str) {
            Element child = Utils.getChild(element, str);
            if (child != null) {
                return CommandTrigger.fromParentElement(child, this.mRoot);
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void doRender(Canvas canvas) {
            canvas.save();
            if (!this.mIsAlignChildren) {
                canvas.translate(-getLeft(), -getTop());
            }
            super.doRender(canvas);
            canvas.restore();
        }

        /* access modifiers changed from: protected */
        public float getParentLeft() {
            float f = 0.0f;
            float left = this.mIsAlignChildren ? getLeft() : 0.0f;
            ElementGroup elementGroup = this.mParent;
            if (elementGroup != null) {
                f = elementGroup.getParentLeft();
            }
            return left + f;
        }

        /* access modifiers changed from: protected */
        public float getParentTop() {
            float f = 0.0f;
            float top = this.mIsAlignChildren ? getTop() : 0.0f;
            ElementGroup elementGroup = this.mParent;
            if (elementGroup != null) {
                f = elementGroup.getParentTop();
            }
            return top + f;
        }

        public State getState() {
            return this.mState;
        }

        public void init() {
            super.init();
            ElementGroup elementGroup = this.mNormalStateElements;
            if (elementGroup != null) {
                elementGroup.show(true);
            }
            ElementGroup elementGroup2 = this.mPressedStateElements;
            if (elementGroup2 != null) {
                elementGroup2.show(false);
            }
            ElementGroup elementGroup3 = this.mReachedStateElements;
            if (elementGroup3 != null) {
                elementGroup3.show(false);
            }
            setState(State.Normal);
            CommandTrigger commandTrigger = this.mNormalStateTrigger;
            if (commandTrigger != null) {
                commandTrigger.init();
            }
            CommandTrigger commandTrigger2 = this.mPressedStateTrigger;
            if (commandTrigger2 != null) {
                commandTrigger2.init();
            }
            CommandTrigger commandTrigger3 = this.mReachedStateTrigger;
            if (commandTrigger3 != null) {
                commandTrigger3.init();
            }
        }

        /* access modifiers changed from: protected */
        public ScreenElement onCreateChild(Element element) {
            String tagName = element.getTagName();
            if (tagName.equalsIgnoreCase("NormalState")) {
                ElementGroup elementGroup = new ElementGroup(element, this.mRoot);
                this.mNormalStateElements = elementGroup;
                return elementGroup;
            } else if (tagName.equalsIgnoreCase("PressedState")) {
                ElementGroup elementGroup2 = new ElementGroup(element, this.mRoot);
                this.mPressedStateElements = elementGroup2;
                return elementGroup2;
            } else if (!tagName.equalsIgnoreCase("ReachedState")) {
                return super.onCreateChild(element);
            } else {
                ElementGroup elementGroup3 = new ElementGroup(element, this.mRoot);
                this.mReachedStateElements = elementGroup3;
                return elementGroup3;
            }
        }

        /* access modifiers changed from: protected */
        public void onStateChange(State state, State state2) {
            String str;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$elements$AdvancedSlider$State[state2.ordinal()];
            if (i == 1) {
                CommandTrigger commandTrigger = this.mNormalStateTrigger;
                if (commandTrigger != null) {
                    commandTrigger.perform();
                }
                str = "normal";
            } else if (i == 2) {
                CommandTrigger commandTrigger2 = this.mPressedStateTrigger;
                if (commandTrigger2 != null) {
                    commandTrigger2.perform();
                }
                performAction("pressed");
                int i2 = AnonymousClass1.$SwitchMap$com$miui$maml$elements$AdvancedSlider$State[state.ordinal()];
                if (i2 == 1) {
                    str = "pressed_normal";
                } else if (i2 == 3) {
                    str = "pressed_reached";
                } else {
                    return;
                }
            } else if (i == 3) {
                CommandTrigger commandTrigger3 = this.mReachedStateTrigger;
                if (commandTrigger3 != null) {
                    commandTrigger3.perform();
                }
                str = "reached";
            } else {
                return;
            }
            performAction(str);
        }

        public void setState(State state) {
            boolean z;
            State state2 = this.mState;
            if (state2 != state) {
                this.mState = state;
                ElementGroup elementGroup = null;
                int i = AnonymousClass1.$SwitchMap$com$miui$maml$elements$AdvancedSlider$State[state.ordinal()];
                int i2 = 2;
                if (i == 1) {
                    elementGroup = this.mNormalStateElements;
                    z = this.mPressedStateElements != null;
                    i2 = 0;
                } else if (i == 2) {
                    ElementGroup elementGroup2 = this.mPressedStateElements;
                    if (elementGroup2 == null) {
                        elementGroup2 = this.mNormalStateElements;
                    }
                    elementGroup = elementGroup2;
                    z = this.mPressedStateElements != null && !AdvancedSlider.this.mStartPointPressed;
                    i2 = 1;
                } else if (i != 3) {
                    z = false;
                    i2 = 0;
                } else {
                    ElementGroup elementGroup3 = this.mReachedStateElements;
                    if (elementGroup3 == null && (elementGroup3 = this.mPressedStateElements) == null) {
                        elementGroup3 = this.mNormalStateElements;
                    }
                    elementGroup = elementGroup3;
                    z = this.mReachedStateElements != null;
                }
                ScreenElement screenElement = this.mCurrentStateElements;
                if (screenElement != elementGroup) {
                    if (screenElement != null) {
                        screenElement.show(false);
                    }
                    if (elementGroup != null) {
                        elementGroup.show(true);
                    }
                    this.mCurrentStateElements = elementGroup;
                }
                if (elementGroup != null && z) {
                    elementGroup.reset();
                }
                IndexedVariable indexedVariable = this.mPointStateVar;
                if (indexedVariable != null) {
                    indexedVariable.set((double) i2);
                }
                onStateChange(state2, this.mState);
            }
        }
    }

    private class SpeedAccController extends ReboundAnimationController implements ITicker {
        private int mBounceAccelation;
        private Expression mBounceAccelationExp;
        private int mBounceInitSpeed;
        private Expression mBounceInitSpeedExp;
        private IndexedVariable mBounceProgress;

        public SpeedAccController(Element element) {
            super(AdvancedSlider.this, (AnonymousClass1) null);
            this.mBounceInitSpeedExp = Expression.build(AdvancedSlider.this.getVariables(), AdvancedSlider.this.getAttr(element, "bounceInitSpeed"));
            this.mBounceAccelationExp = Expression.build(AdvancedSlider.this.getVariables(), AdvancedSlider.this.getAttr(element, "bounceAcceleration"));
            if (AdvancedSlider.this.mHasName) {
                this.mBounceProgress = new IndexedVariable(AdvancedSlider.this.mName + "." + VariableNames.BOUNCE_PROGRESS, AdvancedSlider.this.getVariables(), true);
            }
        }

        /* access modifiers changed from: protected */
        public long getDistance(long j) {
            int i = this.mBounceInitSpeed;
            int i2 = this.mBounceAccelation;
            long j2 = ((((long) i) * j) / 1000) + (((((long) i2) * j) * j) / 2000000);
            if (((long) i) + ((((long) i2) * j) / 1000) <= 0) {
                onStop();
                IndexedVariable indexedVariable = this.mBounceProgress;
                if (indexedVariable != null) {
                    indexedVariable.set(1.0d);
                }
            }
            double d2 = this.mTotalDistance;
            if (d2 > 0.0d) {
                double d3 = ((double) j2) / d2;
                IndexedVariable indexedVariable2 = this.mBounceProgress;
                if (indexedVariable2 != null) {
                    if (d3 > 1.0d) {
                        d3 = 1.0d;
                    }
                    indexedVariable2.set(d3);
                }
            }
            return j2;
        }

        public void init() {
            super.init();
            IndexedVariable indexedVariable = this.mBounceProgress;
            if (indexedVariable != null) {
                indexedVariable.set(1.0d);
            }
        }

        /* access modifiers changed from: protected */
        public void onStart() {
            Expression expression = this.mBounceInitSpeedExp;
            if (expression != null) {
                this.mBounceInitSpeed = (int) AdvancedSlider.this.evaluate(expression);
            }
            Expression expression2 = this.mBounceAccelationExp;
            if (expression2 != null) {
                this.mBounceAccelation = (int) AdvancedSlider.this.evaluate(expression2);
            }
            IndexedVariable indexedVariable = this.mBounceProgress;
            if (indexedVariable != null) {
                indexedVariable.set(0.0d);
            }
        }

        public void start(EndPoint endPoint) {
            if (this.mBounceInitSpeedExp == null) {
                onStop();
            } else {
                super.start(endPoint);
            }
        }
    }

    private class StartPoint extends SliderPoint {
        public static final String TAG_NAME = "StartPoint";
        private float mAnchorX;
        private float mAnchorY;
        protected float mOffsetX;
        protected float mOffsetY;
        public InterpolatorController mReboundController;

        public StartPoint(Element element, ScreenElementRoot screenElementRoot) {
            super(element, screenElementRoot, TAG_NAME);
            this.mAnchorX = Utils.getAttrAsFloat(element, "anchorX", 0.0f);
            this.mAnchorY = Utils.getAttrAsFloat(element, "anchorY", 0.0f);
            InterpolatorHelper create = InterpolatorHelper.create(getVariables(), element);
            Expression build = Expression.build(getVariables(), element.getAttribute("easeTime"));
            if (create != null && build != null) {
                this.mReboundController = new InterpolatorController(create, build);
            }
        }

        public void doRender(Canvas canvas) {
            int save = canvas.save();
            canvas.translate(this.mOffsetX, this.mOffsetY);
            super.doRender(canvas);
            canvas.restoreToCount(save);
        }

        /* access modifiers changed from: protected */
        public void doTick(long j) {
            super.doTick(j);
            InterpolatorController interpolatorController = this.mReboundController;
            if (interpolatorController != null) {
                interpolatorController.tick(j);
            }
        }

        public float getAnchorX() {
            return getLeft() + this.mAnchorX;
        }

        public float getAnchorY() {
            return getTop() + this.mAnchorY;
        }

        public float getOffsetX() {
            return this.mOffsetX;
        }

        public float getOffsetY() {
            return this.mOffsetY;
        }

        public void init() {
            super.init();
            InterpolatorController interpolatorController = this.mReboundController;
            if (interpolatorController != null) {
                interpolatorController.init();
            }
        }

        public void moveTo(float f, float f2) {
            this.mOffsetX = f;
            this.mOffsetY = f2;
        }

        /* access modifiers changed from: protected */
        public void onStateChange(State state, State state2) {
            String str;
            ScreenElementRoot screenElementRoot;
            if (state != State.Invalid) {
                int i = AnonymousClass1.$SwitchMap$com$miui$maml$elements$AdvancedSlider$State[state2.ordinal()];
                if (i != 1) {
                    if (i == 2 && !this.mPressed) {
                        screenElementRoot = this.mRoot;
                        str = this.mPressedSound;
                    }
                    super.onStateChange(state, state2);
                }
                screenElementRoot = this.mRoot;
                str = this.mNormalSound;
                screenElementRoot.playSound(str);
                super.onStateChange(state, state2);
            }
        }
    }

    private enum State {
        Normal,
        Pressed,
        Reached,
        Invalid
    }

    public AdvancedSlider(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
    }

    /* access modifiers changed from: private */
    public void cancelMoving() {
        resetInner();
        onCancel();
    }

    private boolean checkEndPoint(Utils.Point point, EndPoint endPoint) {
        if (endPoint.touched((float) point.x, (float) point.y, false)) {
            State state = endPoint.getState();
            State state2 = State.Reached;
            if (state != state2) {
                endPoint.setState(state2);
                Iterator<EndPoint> it = this.mEndPoints.iterator();
                while (it.hasNext()) {
                    EndPoint next = it.next();
                    if (next != endPoint) {
                        next.setState(State.Pressed);
                    }
                }
                onReach(endPoint.mName);
            }
            return true;
        }
        endPoint.setState(State.Pressed);
        return false;
    }

    private CheckTouchResult checkTouch(float f, float f2) {
        CheckTouchResult checkTouchResult = new CheckTouchResult(this, (AnonymousClass1) null);
        Iterator<EndPoint> it = this.mEndPoints.iterator();
        Utils.Point point = null;
        float f3 = Float.MAX_VALUE;
        while (it.hasNext()) {
            EndPoint next = it.next();
            Utils.Point access$1000 = next.getNearestPoint(f, f2);
            float transformedDist = next.getTransformedDist(access$1000, f, f2);
            if (transformedDist < f3) {
                checkTouchResult.endPoint = next;
                point = access$1000;
                f3 = transformedDist;
            }
        }
        boolean z = false;
        if (f3 < NONE_ENDPOINT_DIST) {
            moveStartPoint((float) point.x, (float) point.y);
            if (f3 >= FREE_ENDPOINT_DIST) {
                Iterator<EndPoint> it2 = this.mEndPoints.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    EndPoint next2 = it2.next();
                    if (next2.mPath == null && (z = checkEndPoint(point, next2))) {
                        checkTouchResult.endPoint = next2;
                        break;
                    }
                }
            } else {
                z = checkEndPoint(point, checkTouchResult.endPoint);
            }
            this.mStartPoint.setState(z ? State.Reached : State.Pressed);
            if (this.mHasName) {
                this.mStateVar.set(z ? 2.0d : 1.0d);
            }
            checkTouchResult.reached = z;
            return checkTouchResult;
        }
        Log.i(LOG_TAG, "unlock touch canceled due to exceeding tollerance");
        this.mStartPoint.performAction("cancel");
        return null;
    }

    private boolean doLaunch(EndPoint endPoint) {
        this.mStartPoint.performAction("launch");
        endPoint.performAction("launch");
        LaunchAction launchAction = endPoint.mAction;
        return onLaunch(endPoint.mName, launchAction != null ? launchAction.perform() : null);
    }

    private void load(Element element) {
        InterpolatorController interpolatorController;
        if (element != null) {
            if (this.mHasName) {
                this.mStateVar = new IndexedVariable(this.mName + "." + STATE, getVariables(), true);
                this.mMoveXVar = new IndexedVariable(this.mName + "." + MOVE_X, getVariables(), true);
                this.mMoveYVar = new IndexedVariable(this.mName + "." + MOVE_Y, getVariables(), true);
                this.mMoveDistVar = new IndexedVariable(this.mName + "." + MOVE_DIST, getVariables(), true);
            }
            StartPoint startPoint = this.mStartPoint;
            if (startPoint == null || (interpolatorController = startPoint.mReboundController) == null) {
                this.mReboundAnimationController = new SpeedAccController(element);
                this.mRoot.addPreTicker(this.mReboundAnimationController);
            } else {
                this.mReboundAnimationController = interpolatorController;
            }
            this.mIsHaptic = Boolean.parseBoolean(getAttr(element, "haptic"));
            this.mIsKeepStatusAfterLaunch = Boolean.parseBoolean(getAttr(element, "keepStatusAfterLaunch"));
        }
    }

    /* access modifiers changed from: private */
    public void moveStartPoint(float f, float f2) {
        float anchorX = f - this.mStartPoint.getAnchorX();
        float anchorY = f2 - this.mStartPoint.getAnchorY();
        this.mStartPoint.moveTo(anchorX, anchorY);
        if (this.mHasName) {
            double descale = descale((double) anchorX);
            double descale2 = descale((double) anchorY);
            double sqrt = Math.sqrt((descale * descale) + (descale2 * descale2));
            this.mMoveXVar.set(descale);
            this.mMoveYVar.set(descale2);
            this.mMoveDistVar.set(sqrt);
        }
    }

    public void finish() {
        super.finish();
        resetInner();
    }

    public void init() {
        super.init();
        this.mReboundAnimationController.init();
        resetInner();
    }

    /* access modifiers changed from: protected */
    public void onCancel() {
    }

    /* access modifiers changed from: protected */
    public ScreenElement onCreateChild(Element element) {
        String tagName = element.getTagName();
        if (tagName.equalsIgnoreCase(StartPoint.TAG_NAME)) {
            StartPoint startPoint = new StartPoint(element, this.mRoot);
            this.mStartPoint = startPoint;
            return startPoint;
        } else if (!tagName.equalsIgnoreCase(EndPoint.TAG_NAME)) {
            return super.onCreateChild(element);
        } else {
            EndPoint endPoint = new EndPoint(element, this.mRoot);
            if (this.mEndPoints == null) {
                this.mEndPoints = new ArrayList<>();
            }
            this.mEndPoints.add(endPoint);
            return endPoint;
        }
    }

    /* access modifiers changed from: protected */
    public boolean onLaunch(String str, Intent intent) {
        OnLaunchListener onLaunchListener = this.mOnLaunchListener;
        return onLaunchListener != null ? onLaunchListener.onLaunch(str) : this.mIsKeepStatusAfterLaunch;
    }

    /* access modifiers changed from: protected */
    public void onMove(float f, float f2) {
    }

    /* access modifiers changed from: protected */
    public void onReach(String str) {
        if (this.mIsHaptic) {
            this.mRoot.haptic(0);
        }
    }

    /* access modifiers changed from: protected */
    public void onRelease() {
        if (this.mIsHaptic) {
            this.mRoot.haptic(1);
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        if (this.mIsHaptic) {
            this.mRoot.haptic(1);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x011e A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.MotionEvent r7) {
        /*
            r6 = this;
            boolean r0 = r6.isVisible()
            r1 = 0
            if (r0 != 0) goto L_0x0008
            return r1
        L_0x0008:
            float r0 = r7.getX()
            float r2 = r7.getY()
            float r3 = r6.getAbsoluteLeft()
            float r0 = r0 - r3
            float r3 = r6.getAbsoluteTop()
            float r2 = r2 - r3
            int r3 = r7.getActionMasked()
            r4 = 1
            if (r3 == 0) goto L_0x00a2
            if (r3 == r4) goto L_0x0062
            r5 = 2
            if (r3 == r5) goto L_0x0046
            r0 = 3
            if (r3 == r0) goto L_0x002b
            goto L_0x0117
        L_0x002b:
            boolean r0 = r6.mMoving
            if (r0 == 0) goto L_0x0117
            com.miui.maml.elements.AdvancedSlider$ReboundAnimationController r0 = r6.mReboundAnimationController
            r2 = 0
            r0.start(r2)
            r6.mCurrentEndPoint = r2
            r6.mMoving = r1
            r6.onRelease()
            com.miui.maml.elements.AdvancedSlider$StartPoint r0 = r6.mStartPoint
            java.lang.String r2 = "cancel"
            r0.performAction(r2)
        L_0x0043:
            r0 = r4
            goto L_0x0118
        L_0x0046:
            boolean r3 = r6.mMoving
            if (r3 == 0) goto L_0x0117
            com.miui.maml.elements.AdvancedSlider$CheckTouchResult r3 = r6.checkTouch(r0, r2)
            if (r3 == 0) goto L_0x0058
            com.miui.maml.elements.AdvancedSlider$EndPoint r3 = r3.endPoint
            r6.mCurrentEndPoint = r3
            r6.onMove(r0, r2)
            goto L_0x0043
        L_0x0058:
            com.miui.maml.elements.AdvancedSlider$ReboundAnimationController r0 = r6.mReboundAnimationController
            com.miui.maml.elements.AdvancedSlider$EndPoint r2 = r6.mCurrentEndPoint
            r0.start(r2)
            r6.mMoving = r1
            goto L_0x009e
        L_0x0062:
            boolean r3 = r6.mMoving
            if (r3 == 0) goto L_0x0117
            java.lang.String r3 = "LockScreen_AdvancedSlider"
            java.lang.String r5 = "unlock touch up"
            android.util.Log.i(r3, r5)
            com.miui.maml.elements.AdvancedSlider$CheckTouchResult r0 = r6.checkTouch(r0, r2)
            if (r0 == 0) goto L_0x0092
            boolean r2 = r0.reached
            if (r2 == 0) goto L_0x007e
            com.miui.maml.elements.AdvancedSlider$EndPoint r2 = r0.endPoint
            boolean r2 = r6.doLaunch(r2)
            goto L_0x008d
        L_0x007e:
            com.miui.maml.elements.AdvancedSlider$StartPoint r2 = r6.mStartPoint
            java.lang.String r3 = "release"
            r2.performAction(r3)
            com.miui.maml.elements.AdvancedSlider$EndPoint r2 = r0.endPoint
            if (r2 == 0) goto L_0x008c
            r2.performAction(r3)
        L_0x008c:
            r2 = r1
        L_0x008d:
            com.miui.maml.elements.AdvancedSlider$EndPoint r0 = r0.endPoint
            r6.mCurrentEndPoint = r0
            goto L_0x0093
        L_0x0092:
            r2 = r1
        L_0x0093:
            r6.mMoving = r1
            if (r2 != 0) goto L_0x009e
            com.miui.maml.elements.AdvancedSlider$ReboundAnimationController r0 = r6.mReboundAnimationController
            com.miui.maml.elements.AdvancedSlider$EndPoint r2 = r6.mCurrentEndPoint
            r0.start(r2)
        L_0x009e:
            r6.onRelease()
            goto L_0x0043
        L_0x00a2:
            com.miui.maml.elements.AdvancedSlider$StartPoint r3 = r6.mStartPoint
            boolean r3 = r3.touched(r0, r2, r1)
            if (r3 == 0) goto L_0x0117
            r6.mMoving = r4
            com.miui.maml.elements.AdvancedSlider$StartPoint r3 = r6.mStartPoint
            float r3 = r3.getAnchorX()
            float r0 = r0 - r3
            r6.mTouchOffsetX = r0
            com.miui.maml.elements.AdvancedSlider$StartPoint r0 = r6.mStartPoint
            float r0 = r0.getAnchorY()
            float r2 = r2 - r0
            r6.mTouchOffsetY = r2
            com.miui.maml.elements.AdvancedSlider$ReboundAnimationController r0 = r6.mReboundAnimationController
            boolean r0 = r0.isRunning()
            if (r0 == 0) goto L_0x00e1
            com.miui.maml.elements.AdvancedSlider$ReboundAnimationController r0 = r6.mReboundAnimationController
            r0.stopRunning()
            float r0 = r6.mTouchOffsetX
            com.miui.maml.elements.AdvancedSlider$StartPoint r2 = r6.mStartPoint
            float r2 = r2.getOffsetX()
            float r0 = r0 - r2
            r6.mTouchOffsetX = r0
            float r0 = r6.mTouchOffsetY
            com.miui.maml.elements.AdvancedSlider$StartPoint r2 = r6.mStartPoint
            float r2 = r2.getOffsetY()
            float r0 = r0 - r2
            r6.mTouchOffsetY = r0
        L_0x00e1:
            com.miui.maml.elements.AdvancedSlider$StartPoint r0 = r6.mStartPoint
            com.miui.maml.elements.AdvancedSlider$State r2 = com.miui.maml.elements.AdvancedSlider.State.Pressed
            r0.setState(r2)
            java.util.ArrayList<com.miui.maml.elements.AdvancedSlider$EndPoint> r0 = r6.mEndPoints
            java.util.Iterator r0 = r0.iterator()
        L_0x00ee:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x0100
            java.lang.Object r2 = r0.next()
            com.miui.maml.elements.AdvancedSlider$EndPoint r2 = (com.miui.maml.elements.AdvancedSlider.EndPoint) r2
            com.miui.maml.elements.AdvancedSlider$State r3 = com.miui.maml.elements.AdvancedSlider.State.Pressed
            r2.setState(r3)
            goto L_0x00ee
        L_0x0100:
            r6.mStartPointPressed = r4
            boolean r0 = r6.mHasName
            if (r0 == 0) goto L_0x010d
            com.miui.maml.data.IndexedVariable r0 = r6.mStateVar
            r2 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r0.set((double) r2)
        L_0x010d:
            com.miui.maml.elements.AdvancedSlider$ReboundAnimationController r0 = r6.mReboundAnimationController
            r0.init()
            r6.onStart()
            goto L_0x0043
        L_0x0117:
            r0 = r1
        L_0x0118:
            boolean r7 = super.onTouch(r7)
            if (r7 != 0) goto L_0x0124
            if (r0 == 0) goto L_0x0125
            boolean r7 = r6.mInterceptTouch
            if (r7 == 0) goto L_0x0125
        L_0x0124:
            r1 = r4
        L_0x0125:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.AdvancedSlider.onTouch(android.view.MotionEvent):boolean");
    }

    public void pause() {
        super.pause();
        resetInner();
    }

    public void reset(long j) {
        super.reset(j);
        resetInner();
    }

    /* access modifiers changed from: protected */
    public void resetInner() {
        if (this.mStartPointPressed) {
            this.mStartPointPressed = false;
            this.mStartPoint.moveTo(0.0f, 0.0f);
            this.mStartPoint.setState(State.Normal);
            Iterator<EndPoint> it = this.mEndPoints.iterator();
            while (it.hasNext()) {
                it.next().setState(State.Normal);
            }
            if (this.mHasName) {
                this.mMoveXVar.set(0.0d);
                this.mMoveYVar.set(0.0d);
                this.mMoveDistVar.set(0.0d);
                this.mStateVar.set(0.0d);
            }
            this.mMoving = false;
            requestUpdate();
        }
    }

    public void setOnLaunchListener(OnLaunchListener onLaunchListener) {
        this.mOnLaunchListener = onLaunchListener;
    }
}

package com.miui.maml.data;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.VariableBinder;
import com.miui.maml.util.Utils;
import java.util.HashMap;
import java.util.Iterator;
import org.w3c.dom.Element;

public class SensorBinder extends VariableBinder {
    private static final String LOG_TAG = "SensorBinder";
    private static final HashMap<String, Integer> SENSOR_TYPES = new HashMap<>();
    public static final String TAG_NAME = "SensorBinder";
    private static final float THRESHOLD = 0.1f;
    private static SensorManager mSensorManager;
    private boolean mEnable;
    private Expression mEnableExp;
    private int mRate;
    private boolean mRegistered;
    private Sensor mSensor;
    private SensorEventListener mSensorEventListener;
    /* access modifiers changed from: private */
    public float mThreshold;
    private String mType;

    private static class Variable extends VariableBinder.Variable {
        public int mIndex;

        public Variable(Element element, Variables variables) {
            super(element, variables);
            this.mIndex = Utils.getAttrAsInt(element, "index", 0);
        }
    }

    static {
        SENSOR_TYPES.put(VariableNames.ORIENTATION, 3);
        SENSOR_TYPES.put("gravity", 9);
        SENSOR_TYPES.put("accelerometer", 1);
        SENSOR_TYPES.put("linear_acceleration", 10);
        SENSOR_TYPES.put("pressure", 6);
        SENSOR_TYPES.put("proximity", 8);
        SENSOR_TYPES.put("light", 5);
        SENSOR_TYPES.put("gyroscope", 4);
    }

    public SensorBinder(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mType = element.getAttribute("type");
        this.mRate = Utils.getAttrAsInt(element, "rate", 3);
        this.mThreshold = Utils.getAttrAsFloat(element, "threshold", 0.1f);
        this.mEnableExp = Expression.build(getVariables(), element.getAttribute("enable"));
        if (mSensorManager == null) {
            mSensorManager = (SensorManager) getContext().mContext.getSystemService("sensor");
        }
        this.mSensor = mSensorManager.getDefaultSensor(getSensorType(this.mType));
        if (this.mSensor == null) {
            Log.e("SensorBinder", "Fail to get sensor! TYPE: " + this.mType);
            return;
        }
        this.mSensorEventListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int i) {
            }

            public void onSensorChanged(SensorEvent sensorEvent) {
                int length = sensorEvent.values.length;
                Iterator<VariableBinder.Variable> it = SensorBinder.this.mVariables.iterator();
                boolean z = false;
                while (it.hasNext()) {
                    Variable variable = (Variable) it.next();
                    int i = variable.mIndex;
                    if (i >= 0 && i < length && Math.abs(variable.getNumber() - ((double) sensorEvent.values[variable.mIndex])) > ((double) SensorBinder.this.mThreshold)) {
                        variable.set((double) sensorEvent.values[variable.mIndex]);
                        z = true;
                    }
                }
                if (z) {
                    SensorBinder.this.onUpdateComplete();
                }
            }
        };
        loadVariables(element);
    }

    private int getSensorType(String str) {
        Integer num = SENSOR_TYPES.get(str);
        if (num != null) {
            return num.intValue();
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return 0;
        }
    }

    private void registerListener() {
        Sensor sensor;
        if (!this.mRegistered && (sensor = this.mSensor) != null && this.mEnable) {
            try {
                mSensorManager.registerListener(this.mSensorEventListener, sensor, this.mRate);
                this.mRegistered = true;
                Log.d("SensorBinder", "registerListener " + this.mType);
            } catch (Exception e) {
                Log.e("SensorBinder", "registerListener failed!");
                e.printStackTrace();
            }
        }
    }

    private void unregisterListener() {
        Sensor sensor;
        if (this.mRegistered && (sensor = this.mSensor) != null) {
            mSensorManager.unregisterListener(this.mSensorEventListener, sensor);
            this.mRegistered = false;
            Log.d("SensorBinder", "unregisterListener " + this.mType);
        }
    }

    public void finish() {
        unregisterListener();
        super.finish();
    }

    public void init() {
        super.init();
        Expression expression = this.mEnableExp;
        boolean z = true;
        if (expression != null && expression.evaluate() <= 0.0d) {
            z = false;
        }
        this.mEnable = z;
        registerListener();
    }

    /* access modifiers changed from: protected */
    public Variable onLoadVariable(Element element) {
        return new Variable(element, getContext().mVariables);
    }

    public void pause() {
        super.pause();
        unregisterListener();
    }

    public void resume() {
        super.resume();
        registerListener();
    }

    public void turnOffSensorBinder() {
        unregisterListener();
    }

    public void turnOnSensorBinder() {
        registerListener();
    }
}

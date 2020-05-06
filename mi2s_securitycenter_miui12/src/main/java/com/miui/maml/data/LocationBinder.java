package com.miui.maml.data;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.miui.maml.data.VariableBinder;
import java.util.Iterator;
import org.w3c.dom.Element;

public class LocationBinder extends VariableBinder {
    private static final float DEFAULT_MIN_DISTANCE = 10.0f;
    private static final long DEFAULT_MIN_TIME = 30000;
    private static final int DEFAULT_PROVIDER_TYPE = 0;
    private static final int GPS = 2;
    private static final int NETWORK = 1;
    public static final String TAG_NAME = "LocationBinder";
    private static LocationManager sLocationManager;
    private boolean mEnable;
    private Expression mEnableExp;
    private LocationListener mLocationListener;
    private String mLocationProvider = null;
    private float mMinDistance;
    private long mMinTime;
    private int mProviderType;
    private boolean mRegistered;

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0062, code lost:
        if (r5.contains("gps") != false) goto L_0x0064;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x006f, code lost:
        if (r5.contains("network") != false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x007f, code lost:
        if (r5.contains("gps") != false) goto L_0x0064;
     */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x008a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public LocationBinder(org.w3c.dom.Element r5, com.miui.maml.ScreenElementRoot r6) {
        /*
            r4 = this;
            r4.<init>(r5, r6)
            r6 = 0
            r4.mLocationProvider = r6
            java.lang.String r0 = "type"
            r1 = 0
            int r0 = com.miui.maml.util.Utils.getAttrAsInt(r5, r0, r1)
            r4.mProviderType = r0
            java.lang.String r0 = "time"
            r1 = 30000(0x7530, double:1.4822E-319)
            long r0 = com.miui.maml.util.Utils.getAttrAsLong(r5, r0, r1)
            r4.mMinTime = r0
            java.lang.String r0 = "distance"
            r1 = 1092616192(0x41200000, float:10.0)
            float r0 = com.miui.maml.util.Utils.getAttrAsFloat(r5, r0, r1)
            r4.mMinDistance = r0
            com.miui.maml.data.Variables r0 = r4.getVariables()
            java.lang.String r1 = "enable"
            java.lang.String r1 = r5.getAttribute(r1)
            com.miui.maml.data.Expression r0 = com.miui.maml.data.Expression.build(r0, r1)
            r4.mEnableExp = r0
            r4.loadVariables(r5)
            android.location.LocationManager r5 = sLocationManager
            if (r5 != 0) goto L_0x004a
            com.miui.maml.ScreenContext r5 = r4.getContext()
            android.content.Context r5 = r5.mContext
            java.lang.String r0 = "location"
            java.lang.Object r5 = r5.getSystemService(r0)
            android.location.LocationManager r5 = (android.location.LocationManager) r5
            sLocationManager = r5
        L_0x004a:
            android.location.LocationManager r5 = sLocationManager
            r0 = 1
            java.util.List r5 = r5.getProviders(r0)
            if (r5 != 0) goto L_0x0057
            r4.updateLocation(r6)
            return
        L_0x0057:
            int r1 = r4.mProviderType
            r2 = 2
            java.lang.String r3 = "gps"
            if (r1 != r2) goto L_0x0067
            boolean r5 = r5.contains(r3)
            if (r5 == 0) goto L_0x0082
        L_0x0064:
            r4.mLocationProvider = r3
            goto L_0x0082
        L_0x0067:
            java.lang.String r2 = "network"
            if (r1 != r0) goto L_0x0072
            boolean r5 = r5.contains(r2)
            if (r5 == 0) goto L_0x0082
            goto L_0x0078
        L_0x0072:
            boolean r0 = r5.contains(r2)
            if (r0 == 0) goto L_0x007b
        L_0x0078:
            r4.mLocationProvider = r2
            goto L_0x0082
        L_0x007b:
            boolean r5 = r5.contains(r3)
            if (r5 == 0) goto L_0x0082
            goto L_0x0064
        L_0x0082:
            java.lang.String r5 = r4.mLocationProvider
            if (r5 != 0) goto L_0x008a
            r4.updateLocation(r6)
            return
        L_0x008a:
            com.miui.maml.data.LocationBinder$1 r5 = new com.miui.maml.data.LocationBinder$1
            r5.<init>()
            r4.mLocationListener = r5
            android.location.LocationManager r5 = sLocationManager
            java.lang.String r6 = r4.mLocationProvider
            android.location.Location r5 = r5.getLastKnownLocation(r6)
            r4.updateLocation(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.LocationBinder.<init>(org.w3c.dom.Element, com.miui.maml.ScreenElementRoot):void");
    }

    private void registerListener() {
        String str;
        if (!this.mRegistered && this.mEnable && (str = this.mLocationProvider) != null) {
            sLocationManager.requestLocationUpdates(str, this.mMinTime, this.mMinDistance, this.mLocationListener);
            this.mRegistered = true;
        }
    }

    private void unregisterListener() {
        if (this.mRegistered) {
            sLocationManager.removeUpdates(this.mLocationListener);
            this.mRegistered = false;
        }
    }

    /* access modifiers changed from: private */
    public void updateLocation(Location location) {
        String[] strArr = location != null ? new String[]{String.valueOf(location.getAccuracy()), String.valueOf(location.getAltitude()), String.valueOf(location.getBearing()), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), String.valueOf(location.getSpeed()), String.valueOf(location.getTime())} : null;
        Iterator<VariableBinder.Variable> it = this.mVariables.iterator();
        while (it.hasNext()) {
            it.next().set((Object) strArr);
        }
        onUpdateComplete();
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
    public VariableBinder.Variable onLoadVariable(Element element) {
        return new VariableBinder.Variable(element, getContext().mVariables);
    }

    public void pause() {
        super.pause();
        unregisterListener();
    }

    public void resume() {
        super.resume();
        registerListener();
    }
}

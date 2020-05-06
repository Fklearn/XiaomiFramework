package com.miui.activityutil;

import android.hardware.Camera;
import java.util.List;

public final class c {

    /* renamed from: a  reason: collision with root package name */
    public static final int f2277a = 2;

    /* renamed from: b  reason: collision with root package name */
    private static final String f2278b = "unknown";

    private static int a() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == 0) {
                return i;
            }
        }
        return 2;
    }

    private static int a(int[] iArr) {
        int i = iArr[0];
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (i < iArr[i2]) {
                i = iArr[i2];
            }
        }
        return i;
    }

    private static String a(int i) {
        if (i == 2) {
            return "unknown";
        }
        Camera open = Camera.open(i);
        Camera.Parameters parameters = open.getParameters();
        parameters.set("camera-id", 1);
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        if (supportedPictureSizes == null) {
            return "unknown";
        }
        int[] iArr = new int[supportedPictureSizes.size()];
        int[] iArr2 = new int[supportedPictureSizes.size()];
        for (int i2 = 0; i2 < supportedPictureSizes.size(); i2++) {
            Camera.Size size = supportedPictureSizes.get(i2);
            int i3 = size.height;
            int i4 = size.width;
            iArr[i2] = i3;
            iArr2[i2] = i4;
        }
        open.release();
        return String.valueOf((a(iArr) * a(iArr2)) / 10000) + " 万";
    }

    private static int b() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == 1) {
                return i;
            }
        }
        return 2;
    }
}

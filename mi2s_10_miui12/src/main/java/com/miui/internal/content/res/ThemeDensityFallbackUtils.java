package com.miui.internal.content.res;

import android.content.res.Configuration;
import miui.telephony.phonenumber.Prefix;

public class ThemeDensityFallbackUtils {
    private static final int[] DENSITIES = {480, 320, 240, 0};
    private static final int DENSITY_NONE = 1;

    private ThemeDensityFallbackUtils() {
    }

    public static String getScreenWidthSuffix(Configuration config) {
        if (config.smallestScreenWidthDp >= 720) {
            return "-sw720dp";
        }
        return Prefix.EMPTY;
    }

    public static String getDensitySuffix(int density) {
        if (density == 0) {
            return Prefix.EMPTY;
        }
        if (density == 1) {
            return "-nodpi";
        }
        if (density == 120) {
            return "-ldpi";
        }
        if (density == 160) {
            return "-mdpi";
        }
        if (density == 240) {
            return "-hdpi";
        }
        if (density == 320) {
            return "-xhdpi";
        }
        if (density == 440) {
            return "-nxhdpi";
        }
        if (density == 480) {
            return "-xxhdpi";
        }
        if (density == 640) {
            return "-xxxhdpi";
        }
        int min = DENSITIES.length - 1;
        for (int j = min - 1; j > 0; j--) {
            if (Math.abs(DENSITIES[j] - density) < Math.abs(DENSITIES[min] - density)) {
                min = j;
            }
        }
        return getDensitySuffix(DENSITIES[min]);
    }

    public static int[] getFallbackOrder(int currentDensity) {
        boolean leftIsBetter;
        int left = DENSITIES.length - 1;
        while (left >= 0 && DENSITIES[left] <= currentDensity) {
            left--;
        }
        int right = 0;
        while (true) {
            int[] iArr = DENSITIES;
            if (right < iArr.length && iArr[right] >= currentDensity) {
                right++;
            }
        }
        int[] densities = new int[(DENSITIES.length + (left + 1 == right ? 1 : 0))];
        densities[0] = currentDensity;
        int index = 1;
        while (index < densities.length) {
            if (left < 0) {
                leftIsBetter = false;
            } else {
                int[] iArr2 = DENSITIES;
                if (right == iArr2.length) {
                    leftIsBetter = true;
                } else {
                    leftIsBetter = iArr2[left] - currentDensity < currentDensity - iArr2[right];
                }
            }
            if (leftIsBetter) {
                densities[index] = DENSITIES[left];
                index++;
                left--;
            } else {
                densities[index] = DENSITIES[right];
                index++;
                right++;
            }
        }
        return densities;
    }
}

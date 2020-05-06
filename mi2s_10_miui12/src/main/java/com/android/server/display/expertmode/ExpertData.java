package com.android.server.display.expertmode;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.server.usb.descriptors.UsbDescriptor;
import miui.util.FeatureParser;
import miui.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class ExpertData {
    public static final String COLOR_B = "color_b";
    public static final String COLOR_G = "color_g";
    public static final String COLOR_GAMUT = "color_gamut";
    public static final String COLOR_HUE = "color_hue";
    public static final String COLOR_R = "color_r";
    public static final String COLOR_SATURATION = "color_saturation";
    public static final String COLOR_VALUE = "color_value";
    public static final String CONTRAST_RATIO = "contrast_ratio";
    public static final int COOKIE_SET_NUM = 9;
    public static final int DEFAULT_EXPERT_COLOR_CONTRAST = FeatureParser.getInteger("expert_contrast_default", 0);
    public static final int DEFAULT_EXPERT_COLOR_GAMMA = FeatureParser.getInteger("expert_gamma_default", UsbDescriptor.CLASSID_DIAGNOSTIC);
    public static final int DEFAULT_EXPERT_COLOR_GAMUT = FeatureParser.getInteger("expert_gamut_default", 0);
    public static final int DEFAULT_EXPERT_COLOR_HUE = FeatureParser.getInteger("expert_hue_default", 0);
    public static final int DEFAULT_EXPERT_COLOR_RGB = FeatureParser.getInteger("expert_RGB_default", 255);
    public static final int DEFAULT_EXPERT_COLOR_SATURATION = FeatureParser.getInteger("expert_saturation_default", 0);
    public static final int DEFAULT_EXPERT_COLOR_VALUE = FeatureParser.getInteger("expert_value_default", 0);
    public static final String GAMMA = "gamma";
    public static final String PROVIDER_KEY = "expert_data";
    public static final boolean SUPPORT_DISPLAY_EXPERT_MODE = FeatureParser.getBoolean("support_display_expert_mode", false);
    private static final String TAG = "ExpertData";
    private int colorB;
    private int colorG;
    private int colorGamut;
    private int colorHue;
    private int colorR;
    private int colorSaturation;
    private int colorValue;
    private int contrastRatio;
    private int gamma;

    public static final class Cookie {
        public static final int COLOR_B_COOKIE = 3;
        public static final int COLOR_GAMUT_COOKIE = 0;
        public static final int COLOR_G_COOKIE = 2;
        public static final int COLOR_HUE_COOKIE = 4;
        public static final int COLOR_R_COOKIE = 1;
        public static final int COLOR_SATURATION_COOKIE = 5;
        public static final int COLOR_VALUE_COOKIE = 6;
        public static final int CONTRAST_RATIO_COOKIE = 7;
        public static final int COOKIE_RESTORE = 9;
        public static final int COOKIE_SET_LAST = 10;
        public static final int GAMMA_COOKIE = 8;
    }

    public static ExpertData getDefaultValue() {
        int i = DEFAULT_EXPERT_COLOR_GAMUT;
        int i2 = DEFAULT_EXPERT_COLOR_RGB;
        return new ExpertData(i, i2, i2, i2, DEFAULT_EXPERT_COLOR_HUE, DEFAULT_EXPERT_COLOR_SATURATION, DEFAULT_EXPERT_COLOR_VALUE, DEFAULT_EXPERT_COLOR_CONTRAST, DEFAULT_EXPERT_COLOR_GAMMA);
    }

    public int getByCookie(int cookie) {
        switch (cookie) {
            case 0:
                return this.colorGamut;
            case 1:
                return this.colorR;
            case 2:
                return this.colorG;
            case 3:
                return this.colorB;
            case 4:
                return this.colorHue;
            case 5:
                return this.colorSaturation;
            case 6:
                return this.colorValue;
            case 7:
                return this.contrastRatio;
            case 8:
                return this.gamma;
            default:
                Log.e(TAG, "getByCookie cookie illegal");
                return 0;
        }
    }

    public boolean equals(Object obj) {
        ExpertData data = (ExpertData) obj;
        return this.colorGamut == data.colorGamut && this.colorR == data.colorR && this.colorG == data.colorG && this.colorB == data.colorB && this.colorHue == data.colorHue && this.colorSaturation == data.colorSaturation && this.colorValue == data.colorValue && this.contrastRatio == data.contrastRatio && this.gamma == data.gamma;
    }

    public ExpertData(int colorGamut2, int colorR2, int colorG2, int colorB2, int colorHue2, int colorSaturation2, int colorValue2, int contrastRatio2, int gamma2) {
        this.colorGamut = colorGamut2;
        this.colorR = colorR2;
        this.colorG = colorG2;
        this.colorB = colorB2;
        this.colorHue = colorHue2;
        this.colorSaturation = colorSaturation2;
        this.colorValue = colorValue2;
        this.contrastRatio = contrastRatio2;
        this.gamma = gamma2;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(COLOR_GAMUT, this.colorGamut);
            jsonObject.put(COLOR_R, this.colorR);
            jsonObject.put(COLOR_G, this.colorG);
            jsonObject.put(COLOR_B, this.colorB);
            jsonObject.put(COLOR_HUE, this.colorHue);
            jsonObject.put(COLOR_SATURATION, this.colorSaturation);
            jsonObject.put(COLOR_VALUE, this.colorValue);
            jsonObject.put(CONTRAST_RATIO, this.contrastRatio);
            jsonObject.put(GAMMA, this.gamma);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static ExpertData getFromDatabase(Context context) {
        String data = Settings.System.getStringForUser(context.getContentResolver(), PROVIDER_KEY, 0);
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        try {
            return createFromJson(new JSONObject(data));
        } catch (JSONException e) {
            Log.e(TAG, "getFromDatabase failed");
            e.printStackTrace();
            return null;
        }
    }

    public static ExpertData createFromJson(JSONObject data) {
        try {
            return new ExpertData(data.getInt(COLOR_GAMUT), data.getInt(COLOR_R), data.getInt(COLOR_G), data.getInt(COLOR_B), data.getInt(COLOR_HUE), data.getInt(COLOR_SATURATION), data.getInt(COLOR_VALUE), data.getInt(CONTRAST_RATIO), data.getInt(GAMMA));
        } catch (JSONException e) {
            Log.e(TAG, "createFromJson failed");
            e.printStackTrace();
            return null;
        }
    }

    public String toString() {
        return "ExpertData{colorGamut=" + this.colorGamut + ", colorR=" + this.colorR + ", colorG=" + this.colorG + ", colorB=" + this.colorB + ", colorHue=" + this.colorHue + ", colorSaturation=" + this.colorSaturation + ", colorValue=" + this.colorValue + ", contrastRatio=" + this.contrastRatio + ", gamma=" + this.gamma + '}';
    }
}

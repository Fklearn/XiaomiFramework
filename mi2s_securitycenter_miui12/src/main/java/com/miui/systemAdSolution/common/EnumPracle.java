package com.miui.systemAdSolution.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.lang.Enum;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import miui.cloud.CloudPushConstants;

public class EnumPracle<T extends Enum> implements Parcelable {
    public static final Parcelable.Creator<EnumPracle> CREATOR = new Parcelable.Creator<EnumPracle>() {
        public EnumPracle createFromParcel(Parcel parcel) {
            return new EnumPracle(parcel);
        }

        public EnumPracle[] newArray(int i) {
            return new EnumPracle[i];
        }
    };
    private static final String TAG = "AdTrackType";
    private T mValue;

    protected EnumPracle(Parcel parcel) {
        int i;
        String str;
        String str2;
        if (parcel != null) {
            str2 = parcel.readString();
            str = parcel.readString();
            i = parcel.readInt();
        } else {
            i = -1;
            str2 = null;
            str = null;
        }
        if (i == -1 || TextUtils.isEmpty(str2) || TextUtils.isEmpty(str)) {
            noSupportLog(str2, str, i, (Throwable) null);
            return;
        }
        try {
            T[] tArr = (Enum[]) Enum.class.getDeclaredMethod("getSharedConstants", new Class[]{Class.class}).invoke((Object) null, new Object[]{Class.forName(str2)});
            if (tArr == null || tArr.length <= 0) {
                throw new UnsupportEnumException(String.format("the enum[%s] is not define.", new Object[]{str2}));
            }
            for (T t : tArr) {
                if (t != null) {
                    Field declaredField = Enum.class.getDeclaredField(CloudPushConstants.XML_NAME);
                    if (declaredField != null) {
                        if (!declaredField.isAccessible()) {
                            declaredField.setAccessible(true);
                        }
                        if (TextUtils.equals((String) declaredField.get(t), str)) {
                            Field declaredField2 = Enum.class.getDeclaredField("ordinal");
                            if (!declaredField2.isAccessible()) {
                                declaredField2.setAccessible(true);
                            }
                            int i2 = declaredField2.getInt(t);
                            if (i2 == i) {
                                this.mValue = t;
                                return;
                            } else {
                                throw new UnsupportEnumException(String.format("the value[%s] which name is [%s] in the enum[%s] is not equal the value defined[%s].", new Object[]{Integer.valueOf(i), str, str2, Integer.valueOf(i2)}));
                            }
                        }
                    }
                }
            }
            throw new UnsupportEnumException(String.format("the name[%s] in the enum[%s] is not defined.", new Object[]{str, str2}));
        } catch (ClassCastException | ClassNotFoundException | Exception | IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
            noSupportLog(str2, str, i, e);
        }
    }

    public EnumPracle(T t) {
        if (t != null) {
            this.mValue = t;
            return;
        }
        throw new IllegalArgumentException("type must not be null.");
    }

    private void noSupportLog(String str, String str2, int i, Throwable th) {
        String format = String.format("the value[%s] which name is [%s] in the enum[%s] is not support.", new Object[]{Integer.valueOf(i), str2, str});
        if (th == null) {
            Log.e(TAG, format);
        } else {
            Log.e(TAG, format, th);
        }
    }

    public int describeContents() {
        return 0;
    }

    public T getValue() {
        return this.mValue;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mValue.getClass().getName());
        parcel.writeString(this.mValue.name());
        parcel.writeInt(this.mValue.ordinal());
    }
}

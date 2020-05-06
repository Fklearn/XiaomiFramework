package com.miui.optimizecenter.storage.model;

import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import com.miui.securitycenter.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class StorageItemInfo {

    /* renamed from: a  reason: collision with root package name */
    public int f5753a = -1;
    /* access modifiers changed from: private */
    @StringRes

    /* renamed from: b  reason: collision with root package name */
    public int f5754b;

    /* renamed from: c  reason: collision with root package name */
    public long f5755c;
    @ColorRes

    /* renamed from: d  reason: collision with root package name */
    public int f5756d;
    public String e;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ItemType {
    }

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public StorageItemInfo f5757a = new StorageItemInfo();

        public a a(@ColorRes int i) {
            this.f5757a.f5756d = i;
            return this;
        }

        public a a(String str) {
            this.f5757a.e = str;
            return this;
        }

        public StorageItemInfo a() {
            return this.f5757a;
        }

        public a b(@StringRes int i) {
            int unused = this.f5757a.f5754b = i;
            return this;
        }

        public a c(int i) {
            this.f5757a.f5753a = i;
            return this;
        }
    }

    public static StorageItemInfo a(int i) {
        int i2;
        a aVar = new a();
        switch (i) {
            case 0:
                aVar.c(0);
                aVar.b(R.string.storage_item_other_name);
                i2 = R.color.storage_other_item;
                break;
            case 1:
                aVar.c(1);
                aVar.b(R.string.storage_item_appdata_name);
                aVar.a((int) R.color.storage_appdata_item);
                aVar.a("miui.intent.action.STORAGE_APP_INFO_LIST");
                break;
            case 3:
                aVar.c(3);
                aVar.b(R.string.storage_item_picture_name);
                i2 = R.color.storage_picture_item;
                break;
            case 4:
                aVar.c(4);
                aVar.b(R.string.storage_item_audio_name);
                i2 = R.color.storage_audio_item;
                break;
            case 5:
                aVar.c(5);
                aVar.b(R.string.storage_item_video_name);
                i2 = R.color.storage_video_item;
                break;
            case 6:
                aVar.c(6);
                aVar.b(R.string.storage_item_apk_name);
                i2 = R.color.storage_apk_item;
                break;
            case 7:
                aVar.c(7);
                aVar.b(R.string.storage_item_doc_name);
                i2 = R.color.storage_doc_item;
                break;
            case 8:
                aVar.c(8);
                aVar.b(R.string.storage_item_system_name);
                i2 = R.color.storage_system_item;
                break;
            default:
                aVar.c(i);
                break;
        }
        aVar.a(i2);
        return aVar.a();
    }

    public long a() {
        return this.f5755c;
    }
}

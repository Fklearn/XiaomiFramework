package com.market.sdk;

import android.net.Uri;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

class f implements JsonSerializer<Uri> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DesktopRecommendInfo f2226a;

    f(DesktopRecommendInfo desktopRecommendInfo) {
        this.f2226a = desktopRecommendInfo;
    }

    /* renamed from: a */
    public JsonElement serialize(Uri uri, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(uri.toString());
    }
}

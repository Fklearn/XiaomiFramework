package com.market.sdk;

import android.net.Uri;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;

class g implements JsonDeserializer<Uri> {
    g() {
    }

    public Uri deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        return Uri.parse(jsonElement.getAsJsonPrimitive().getAsString());
    }
}

package com.market.sdk.utils;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class b {
    public static <T, K> ConcurrentHashMap<T, K> a() {
        return new ConcurrentHashMap<>();
    }

    public static boolean a(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}

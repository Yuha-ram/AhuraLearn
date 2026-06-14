package com.ahuralearn.common.utils;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollUtils extends CollectionUtil {

    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    public static <T> Set<T> emptySet() {
        return Collections.emptySet();
    }

    public static <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }
}
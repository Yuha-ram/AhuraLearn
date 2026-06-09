package com.ahuralearn.common.utils;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CollUtils extends CollectionUtil {

    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    public static <T> Set<T> emptySet() {
        return Collections.emptySet();
    }
}
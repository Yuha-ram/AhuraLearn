package com.ahuralearn.common.utils;

import cn.hutool.core.bean.BeanUtil;

import java.util.List;

public class BeanUtils extends BeanUtil {

    /**
     * convert the original object to target object
     *
     * @param source original object
     * @param clazz  target object's class
     * @param <R>    original object type
     * @param <T>    target object type
     * @return target object
     */
    public static <R, T> T copyBean(R source, Class<T> clazz) {
        if (source == null)
            return null;
        return toBean(source, clazz);
    }

    /**
     * convert the list with original object to a new list with target object
     *
     * @param list  list with original object
     * @param clazz target object's class
     * @param <R>   original object type
     * @param <T>   target object type
     * @return new list with target object
     */
    public static <R, T> List<T> copyList(List<R> list, Class<T> clazz) {
        if (list == null || list.size() == 0)
            return CollUtils.emptyList();
        return copyToList(list, clazz);
    }
}

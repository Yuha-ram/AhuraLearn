package com.ahuralearn.common.utils;

public class UserContext {
    private static final ThreadLocal<Long> TL = new ThreadLocal<>();

    /**
     * get the userId of the thread
     * @return userId
     */
    public static Long getUser() {
        return TL.get();
    }

    /**
     * save the userId for the thread
     * @param val userId
     */
    public static void setUser(Long val) {
        TL.set(val);
    }

    /**
     * delete the userId of the thread
     */
    public static void removeUser() {
        TL.remove();
    }
}

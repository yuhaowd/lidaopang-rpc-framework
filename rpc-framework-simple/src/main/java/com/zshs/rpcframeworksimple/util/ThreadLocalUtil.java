package com.zshs.rpcframeworksimple.util;

/**
 * @ClassName ThreadLocalUtil
 * @Description
 * @Author lidaopang
 * @Date 2024/7/11 下午1:13
 * @Version 1.0
 */

public class ThreadLocalUtil {


    private static final ThreadLocal<String> threadLocal = new ThreadLocal();

    public static String get() {
        return threadLocal.get();
    }

    public static void set(String implName) {
        threadLocal.set(implName);
    }


}

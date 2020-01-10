package com.github.hdy.jdbcplus.log;


import com.github.hdy.jdbcplus.util.Logs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class SqlLogs {
    private final long period;
    private final AtomicLong now;

    /**
     * @param sql
     */
    public static void log(String sql, String type, Object data, long startTimeMillis) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        String className = null;
        String methodName = null;
        String fileName = null;
        int lineNumber = 0;
        for (StackTraceElement element : elements) {
            if (element.getClassName().indexOf("Service") != -1) {
                className = element.getClassName();
                methodName = element.getMethodName();
                lineNumber = element.getLineNumber();
                fileName = element.getFileName();
            }
        }
        long times = getCurrentTimeMillis() - startTimeMillis;
        Logs.info("┏━━━━━ " + className + "." + methodName);
        Logs.info("┣查询类型: " + type);
        Logs.info("┣ S Q L : " + sql);
        Logs.info("┣查询参数: " + "[]");
        Logs.info("┣返回数据: " + (data.toString().length() > 500 ? data.toString().substring(0, 500) + "..." : data.toString()));
        Logs.info("┣处理时间: " + times + "ms");
        Logs.info("┗━━━━━ " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")");
    }

    public static void log(String sql, String type, Object data, long startTimeMillis, Object... params) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        String className = null;
        String methodName = null;
        String fileName = null;
        int lineNumber = 0;
        for (StackTraceElement element : elements) {
            if (element.getClassName().indexOf("Service") != -1) {
                className = element.getClassName();
                methodName = element.getMethodName();
                lineNumber = element.getLineNumber();
                fileName = element.getFileName();
                break;
            }
        }
        long times = getCurrentTimeMillis() - startTimeMillis;
        Logs.info("┏━━━━━ " + className + "." + methodName);
        Logs.info("┣ 查询类型: " + type);
        Logs.info("┣  S Q L : " + sql);
        Logs.info("┣ 查询参数: " + unfixedToString(params));
        Logs.info("┣ 返回数据: " + (data.toString().length() > 500 ? data.toString().substring(0, 500) + "..." : data.toString()));
        Logs.info("┣ 处理时间: " + times + "ms");
        Logs.info("┗━━━━━ " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")");
    }

    public static void log(String sql, String type, Object data, long startTimeMillis, Map<String, ?> params) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        String className = null;
        String methodName = null;
        String fileName = null;
        int lineNumber = 0;
        for (StackTraceElement element : elements) {
            if (element.getClassName().indexOf("Service") != -1) {
                className = element.getClassName();
                methodName = element.getMethodName();
                lineNumber = element.getLineNumber();
                fileName = element.getFileName();
            }
        }
        long times = getCurrentTimeMillis() - startTimeMillis;
        Logs.info("┏━━━━━ " + className + "." + methodName);
        Logs.info("┣查询类型: " + type);
        Logs.info("┣ S Q L : " + sql);
        Logs.info("┣查询参数: " + params);
        Logs.info("┣返回数据: " + (data.toString().length() > 500 ? data.toString().substring(0, 500) + "..." : data.toString()));
        Logs.info("┣处理时间: " + times + "ms");
        Logs.info("┗━━━━━ " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")");
    }


    public static long getCurrentTimeMillis() {
        return now();
    }

    public static long now() {
        return instance().currentTimeMillis();
    }

    private long currentTimeMillis() {
        return now.get();
    }

    private static SqlLogs instance() {
        return InstanceHolder.INSTANCE;
    }

    private SqlLogs(long period) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "System Clock");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
    }


    private static class InstanceHolder {
        public static final SqlLogs INSTANCE = new SqlLogs(1);
    }

    /**
     * 不定数量参数转String
     */
    public static String unfixedToString(Object... objects) {
        if (objects == null || objects.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer("[");
        for (Object o : objects) {
            sb.append(o + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}

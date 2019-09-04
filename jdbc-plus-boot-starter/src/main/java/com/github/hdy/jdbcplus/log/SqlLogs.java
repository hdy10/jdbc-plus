package com.github.hdy.jdbcplus.log;



import com.github.hdy.jdbcplus.util.Logs;

import java.util.Map;


public class SqlLogs {

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
        Logs.info("┣返回数据: " + data.toString());
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
        Logs.info("┣ 返回数据: " + data.toString());
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
        Logs.info("┣返回数据: " + data.toString());
        Logs.info("┣处理时间: " + times + "ms");
        Logs.info("┗━━━━━ " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")");
    }


    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
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

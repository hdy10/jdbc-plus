package com.github.hdy.jdbcplus.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String 工具类
 *
 * @author 贺大爷
 * @date 2019/6/25
 */
public class Strings {

    /**
     * 空字符
     */
    public static final String EMPTY = "";
    /**
     * 下划线字符
     */
    public static final char UNDERLINE = '_';

    /**
     * 判断参数是否为空
     *
     * @param params 需要判断参数
     *
     * @return 判断结果
     */
    public static boolean isNull(Object... params) {
        if (null == params)
            return true;
        for (Object o : params) {
            if (null == o || o.equals("null") || o.toString().length() == 0) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断字符串是否为空
     *
     * @param cs 需要判断字符串
     *
     * @return 判断结果
     */
    public static boolean isEmpty(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Class<T> getClassGenricType(final Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     * 如public baseDao extends HibernateDao<User,Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     *
     * @return the index generic declaration, or Object.class if cannot be determined
     */
    @SuppressWarnings({"rawtypes"})
    public static Class getClassGenricType(final Class clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            Logs.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            Logs.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            Logs.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 字符串驼峰转下划线格式
     *
     * @param param 需要转换的字符串
     *
     * @return 转换好的字符串
     */
    public static String camelToUnderline(String param) {
        return camelToCustom(param, UNDERLINE);
    }

    /**
     * 字符串驼峰转自定义字符标识
     *
     * @param param   需要转换的字符串
     * @param replace 自定义标识符
     *
     * @return
     */
    public static String camelToCustom(String param, char replace) {
        if (isEmpty(param)) {
            return EMPTY;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(replace);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }


    /**
     * 拼接字符串第二个字符串第一个字母大写
     */
    public static String concatCapitalize(String concatStr, final String str) {
        if (isEmpty(concatStr)) {
            concatStr = EMPTY;
        }
        if (str == null || str.length() == 0) {
            return str;
        }
        final char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }
        return concatStr + Character.toTitleCase(firstChar) + str.substring(1);
    }

    /**
     * 字符串第一个字母大写
     *
     * @param str 被处理的字符串
     *
     * @return 首字母大写后的字符串
     */
    public static String capitalize(final String str) {
        return concatCapitalize(null, str);
    }

    /**
     * <p>
     * Checks if CharSequence contains a search CharSequence irrespective of case, handling {@code null}. Case-insensitivity is defined as by {@link String#equalsIgnoreCase(String)}.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.contains(null, *) = false
     * StringUtils.contains(*, null) = false
     * StringUtils.contains("", "") = true
     * StringUtils.contains("abc", "") = true
     * StringUtils.contains("abc", "a") = true
     * StringUtils.contains("abc", "z") = false
     * StringUtils.contains("abc", "A") = true
     * StringUtils.contains("abc", "Z") = false
     * </pre>
     */
    public static boolean isContainsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return StringUtils.containsIgnoreCase(str, searchStr);
    }

    /**
     * <p>
     * Gets the substring after the last occurrence of a separator. The separator is not returned.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
     * </pre>
     */
    public static String substringAfterLast(String str, String separator) {
        return StringUtils.substringAfterLast(str, separator);
    }

    /**
     * <p>
     * Checks if CharSequence contains a search CharSequence, handling {@code null}. This method uses {@link String#indexOf(String)} if possible.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "")   = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
     * </pre>
     */
    public static boolean isContains(final CharSequence seq, final CharSequence searchSeq) {
        return StringUtils.contains(seq, searchSeq);
    }

    /**
     * 找到指定的字符串开头位置（忽略大小写）
     * <p>
     * Case in-sensitive find of the first index within a CharSequence.
     * </p>
     * <p/>
     * <pre>
     * Strings.indexOfIgnoreCase(null, *)          = -1
     * Strings.indexOfIgnoreCase(*, null)          = -1
     * Strings.indexOfIgnoreCase("", "")           = 0
     * Strings.indexOfIgnoreCase("aabaabaa", "a")  = 0
     * Strings.indexOfIgnoreCase("aabaabaa", "b")  = 2
     * Strings.indexOfIgnoreCase("aabaabaa", "ab") = 1
     * </pre>
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return StringUtils.indexOfIgnoreCase(str, searchStr, 0);
    }

    public static String substring(String str, int start) {
        return StringUtils.substring(str, start);
    }

    /**
     * 判断是否为数字
     *
     * @param str
     *
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}

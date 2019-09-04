package com.github.hdy.jdbcplus.log;

import com.github.hdy.jdbcplus.util.Strings;

import java.util.ArrayList;
import java.util.List;

public class Sqls {
    private static final String TABLE_ALIAS = "table_alias";

    /**
     * 构建统计语句
     */
    public static String buildCountSql(String sql) {
        String upperCaseSql = sql.toLowerCase();
        StringBuilder countSql = new StringBuilder();
        String prefix = "select count(1) "; // sql 统计查询前缀
        String selectKey = "select"; // select 关键字
        String fromKey = "from"; // from 关键字
        // 带有group by 的进行特殊处理，直接返回
        if (Strings.isContainsIgnoreCase(sql, "group by")) {
            return prefix + fromKey + "(" + sql + ")" + TABLE_ALIAS;
        }
        Integer fromRelIndex = upperCaseSql.indexOf(fromKey);// 主sql中 from 关键字的位置
        List<Integer> fromIndexList = getStrKeyIndexList(upperCaseSql, fromKey);
        if (fromIndexList.size() > 1) {
            // 子查询处理
            for (int i = 0; i < fromIndexList.size(); i++) {
                Integer selectKeyCount = getStrKeyIndexList(upperCaseSql.substring(0, fromIndexList.get(i)), selectKey).size() - 1;
                if (selectKeyCount == i) {
                    fromRelIndex = fromIndexList.get(i);
                    break;
                }
            }
            fromRelIndex -= fromKey.length();
        }
        String afterFromSql = sql.substring(fromRelIndex); // 主sql from 关键字真实位置之后的sql语句
        if (Strings.isContains(upperCaseSql, ")")) {
            upperCaseSql = Strings.substringAfterLast(upperCaseSql, ")"); // 查找最后一个反括号；跳过子查询中的 order by 语句
        }
        if (Strings.isContainsIgnoreCase(upperCaseSql, "order")) {
            String query = Strings.substring(upperCaseSql, Strings.indexOfIgnoreCase(upperCaseSql, " order by "));
            afterFromSql = afterFromSql.substring(0, afterFromSql.length() - query.length());// 删除主sql 中的 order by 之后的语句；结果为 from 到 order by 之前的语句
        }
        sql = countSql.append(prefix + afterFromSql).toString(); // 拼装sql
        // 解析完的sql中在最后还包含 group by 语句的进行特殊处理
        // 例: select count(1) from .... group by column/function
        // if (Strings.isContainsIgnoreCase(sql, "group by")) {
        // String afterGroupSql = Strings.substringAfterIgnoreCases(sql, " group by ");
        // String LeftParenthesis = "(";// 左括号
        // String rightParenthesis = ")";// 右括号
        // 左括号和右括号是否匹配(个数匹配为函数,反之为子查询)
        // if (getStrKeyIndexList(afterGroupSql, LeftParenthesis).size() == getStrKeyIndexList(afterGroupSql, rightParenthesis).size()) {
        // return prefix + afterFromSql ;// 拼装sql
        // }
        // }
        return sql;
    }

    /**
     * 获取指定字符串在整个字符串中的位置集合
     *
     * @param str 整个字符转
     * @param key 要查找的额字符串
     */
    private static List<Integer> getStrKeyIndexList(String str, String key) {
        int temp = 0;
        List<Integer> indexList = new ArrayList<>();
        while ((temp = str.indexOf(key, temp)) != -1) {
            temp = temp + key.length();
            indexList.add(temp);
        }
        return indexList;
    }

}

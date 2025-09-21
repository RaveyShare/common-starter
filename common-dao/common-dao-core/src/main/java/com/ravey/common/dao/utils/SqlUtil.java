/*
 * Decompiled with CFR 0.152.
 */
package com.ravey.common.dao.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtil {
    public static final String ORDER_BY_REG_EXP = "ORDER\\s+BY";
    public static final String GROUP_BY_REG_EXP = "GROUP\\s+BY";
    public static final List<Pattern> PATTERNS = new LinkedList<Pattern>();

    public static int findKeyWordIndex(String sql) {
        if (sql == null || sql.length() == 0) {
            return -1;
        }
        String upSql = sql.toUpperCase();
        int whereIndex = upSql.lastIndexOf("WHERE");
        if (whereIndex != -1) {
            upSql = upSql.substring(whereIndex);
        }
        int index = upSql.lastIndexOf("LIMIT");
        for (Pattern pattern : PATTERNS) {
            Matcher matcher = pattern.matcher(upSql);
            while (matcher.find()) {
                index = matcher.start();
            }
        }
        if (index < 0) {
            return index;
        }
        if (whereIndex != -1) {
            return whereIndex - 1 + index;
        }
        return index;
    }

    public static void main(String[] args) {
        String sql = "select * from aa where 1 = 1 ";
        int index = SqlUtil.findKeyWordIndex(sql);
        System.out.println(index);
        if (index > -1) {
            System.out.println("*" + sql.substring(index));
            sql = sql.substring(0, index);
        }
        System.out.println(sql + "*");
        String s = "id,";
        System.out.println(s.substring(0, s.length() - 1));
    }

    static {
        PATTERNS.add(Pattern.compile(ORDER_BY_REG_EXP));
        PATTERNS.add(Pattern.compile(GROUP_BY_REG_EXP));
    }
}

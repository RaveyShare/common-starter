/*
 * Decompiled with CFR 0.152.
 */
package com.ravey.common.dao.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertUtil {
    public static final char UNDERLINE = '_';
    public static final Pattern TO_UNDERLINE_PATTERN = Pattern.compile("_(\\w)");
    public static final Pattern TO_CAMEL_PATTERN = Pattern.compile("[A-Z]");

    public static String camelToUnderline(String str) {
        Matcher matcher = TO_CAMEL_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (!matcher.find()) {
            return sb.toString();
        }
        sb = new StringBuffer();
        matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        matcher.appendTail(sb);
        return ConvertUtil.camelToUnderline(sb.toString());
    }

    public static String underlineToCamel(String str) {
        Matcher matcher = TO_UNDERLINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (!matcher.find()) {
            return sb.toString();
        }
        sb = new StringBuffer();
        matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        matcher.appendTail(sb);
        return ConvertUtil.underlineToCamel(sb.toString());
    }
}

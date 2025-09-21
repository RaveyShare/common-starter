/*
 * Decompiled with CFR 0.152.
 */
package com.ravey.common.dao.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectUtil {
    public static Map<String, List<Field>> fieldMap = new ConcurrentHashMap<String, List<Field>>();

    public static List<Field> getFields(Class<?> cl) {
        String className = cl.getName();
        List<Field> fields = fieldMap.get(className);
        if (fields != null && !fields.isEmpty()) {
            return fields;
        }
        fields = new ArrayList<Field>();
        for (Class<?> clazz = cl; clazz != Object.class; clazz = clazz.getSuperclass()) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        fieldMap.put(className, fields);
        return fields;
    }

    public static void setFieldValue(Object obj, Field field, Object fieldValue) {
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(obj, fieldValue);
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object getFieldValue(Object obj, Field field) {
        if (field != null) {
            try {
                field.setAccessible(true);
                return field.get(obj);
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void coverObj(Map<String, Object> baseMap, Object secondObj, boolean isForceReplace) throws IllegalAccessException {
        for (Map.Entry<String, Object> entry : baseMap.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            if (secondObj instanceof List) {
                List list = (List)secondObj;
                for (Object o : list) {
                    ReflectUtil.coverObj(k, v, o, isForceReplace);
                }
                continue;
            }
            ReflectUtil.coverObj(k, v, secondObj, isForceReplace);
        }
    }

    private static void coverObj(String firstObjName, Object firstObjVal, Object secondObj, boolean isForceReplace) throws IllegalAccessException {
        List<Field> secondFields = ReflectUtil.getFields(secondObj.getClass());
        for (Field second : secondFields) {
            if (!firstObjName.equals(second.getName())) continue;
            second.setAccessible(true);
            Object secondVal = second.get(secondObj);
            if (secondVal != null && !isForceReplace) continue;
            second.set(secondObj, firstObjVal);
        }
    }
}

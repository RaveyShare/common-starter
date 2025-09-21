package com.ravey.common.api.utils;

import java.util.HashMap;
import java.util.Map;

public final class MessageFormatter {
    static final char DELIM_START = '{';
    static final char DELIM_STOP = '}';
    static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';

    public MessageFormatter() {
    }

    public static String format(String messagePattern, Object... args) {
        if (messagePattern == null) {
            return null;
        } else if (args.length <= 0) {
            return messagePattern;
        } else {
            int i = 0;
            StringBuilder sb = new StringBuilder(messagePattern.length() + 50);

            for(int l = 0; l < args.length; ++l) {
                int j = messagePattern.indexOf("{}", i);
                if (j == -1) {
                    if (i == 0) {
                        return messagePattern;
                    }

                    sb.append(messagePattern, i, messagePattern.length());
                    return sb.toString();
                }

                if (isEscapedDelimeter(messagePattern, j)) {
                    if (!isDoubleEscaped(messagePattern, j)) {
                        --l;
                        sb.append(messagePattern, i, j - 1);
                        sb.append('{');
                        i = j + 1;
                    } else {
                        sb.append(messagePattern, i, j - 1);
                        deeplyAppendParameter(sb, args[l], new HashMap());
                        i = j + 2;
                    }
                } else {
                    sb.append(messagePattern, i, j);
                    deeplyAppendParameter(sb, args[l], new HashMap());
                    i = j + 2;
                }
            }

            sb.append(messagePattern, i, messagePattern.length());
            return sb.toString();
        }
    }

    private static boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex == 0) {
            return false;
        } else {
            char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
            return potentialEscape == '\\';
        }
    }

    private static boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        return delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == '\\';
    }

    private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Map<Object[], Object> seenMap) {
        if (o == null) {
            sbuf.append("null");
        } else {
            if (!o.getClass().isArray()) {
                safeObjectAppend(sbuf, o);
            } else if (o instanceof boolean[]) {
                booleanArrayAppend(sbuf, (boolean[])o);
            } else if (o instanceof byte[]) {
                byteArrayAppend(sbuf, (byte[])o);
            } else if (o instanceof char[]) {
                charArrayAppend(sbuf, (char[])o);
            } else if (o instanceof short[]) {
                shortArrayAppend(sbuf, (short[])o);
            } else if (o instanceof int[]) {
                intArrayAppend(sbuf, (int[])o);
            } else if (o instanceof long[]) {
                longArrayAppend(sbuf, (long[])o);
            } else if (o instanceof float[]) {
                floatArrayAppend(sbuf, (float[])o);
            } else if (o instanceof double[]) {
                doubleArrayAppend(sbuf, (double[])o);
            } else {
                objectArrayAppend(sbuf, (Object[])o, seenMap);
            }

        }
    }

    private static void safeObjectAppend(StringBuilder sbuf, Object o) {
        try {
            String oAsString = o.toString();
            sbuf.append(oAsString);
        } catch (Throwable var3) {
            sbuf.append("[FAILED toString()]");
        }

    }

    private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Map<Object[], Object> seenMap) {
        sbuf.append('[');
        if (!seenMap.containsKey(a)) {
            seenMap.put(a, (Object)null);
            int len = a.length;

            for(int i = 0; i < len; ++i) {
                deeplyAppendParameter(sbuf, a[i], seenMap);
                if (i != len - 1) {
                    sbuf.append(", ");
                }
            }

            seenMap.remove(a);
        } else {
            sbuf.append("...");
        }

        sbuf.append(']');
    }

    private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void charArrayAppend(StringBuilder sbuf, char[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void intArrayAppend(StringBuilder sbuf, int[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void longArrayAppend(StringBuilder sbuf, long[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }
}
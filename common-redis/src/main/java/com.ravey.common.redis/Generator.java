//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ravey.common.utils.json.JsonUtil;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class Generator {
    private final StringRedisTemplate redis;
    private Map<String, Object> secretMap;
    private static final String[] DATE_FORMAT_ARR = new String[]{"yyyy", "yy", "MM", "dd"};
    public static final String SHARP = "#";

    public Generator(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @PostConstruct
    public void init() {
        String val = (String)this.redis.opsForValue().get("SecretSet");
        if (!StringUtils.hasText(val)) {
            List<String> list = new ArrayList();

            for(int i = 0; i < 100; ++i) {
                list.add(this.stringFormat(i, 2));
            }

            this.secretMap = new HashMap<>();
            Random random = new Random();

            for(int i = 0; i < 100; ++i) {
                int index = random.nextInt(100 - i);
                this.secretMap.put(this.stringFormat(i, 2), list.get(index));
                list.remove(index);
            }

            this.redis.opsForValue().set("SecretSet", JsonUtil.bean2Json(this.secretMap));
        } else {
            this.secretMap = JsonUtil.json2Bean(val, new TypeReference<Map<String, Object>>() {});
        }

    }

    public String stringFormat(Serializable num, int len) {
        String format = "%0" + len + "d";
        return String.format(format, num);
    }

    public String newCode(String format, String group) {
        return this.newCode(format, group, true);
    }

    public String newCode(String format, String group, boolean isEncrypt) {
        String[] formatArr = format.split("#");
        if (formatArr.length >= 2 && formatArr.length <= 3) {
            if (group.endsWith("#")) {
                group = group + "_";
            }

            String[] groupArr = group.split("#");
            if (groupArr.length > 2) {
                throw new IllegalArgumentException("group格式不正确！正确使用如：SO#yyyyMMdd 或者 SOyyyyMMdd");
            } else {
                LocalDate now = LocalDate.now();
                int length;
                String prefix;
                if (formatArr.length == 2) {
                    length = Integer.parseInt(formatArr[1]);
                    prefix = this.replace(now, formatArr[0]);
                } else {
                    length = Integer.parseInt(formatArr[2]);
                    prefix = formatArr[0] + this.replace(now, formatArr[1]);
                }

                Assert.isTrue(length >= 2 && length <= 15, "format格式不正确！流水号只允许2-15位");
                String newGroup;
                if (groupArr.length == 1) {
                    newGroup = this.replace(now, groupArr[0]);
                } else {
                    newGroup = groupArr[0] + this.replace(now, groupArr[1]);
                }

                String key = "CodeGroup:" + newGroup + "#" + length;
                long originNo = this.increment(key, 1L);
                long no = originNo;
                if (no == 1L && isEncrypt) {
                    long max = (long)Math.pow(10.0, (double)(length - 1));
                    no = this.increment(key, (long)(Math.random() * (double)max));
                }

                if (originNo == 1L) {
                    this.setKeyExpireAt(key, groupArr);
                }

                return prefix + this.handleNo(no, length, isEncrypt);
            }
        } else {
            throw new IllegalArgumentException("format格式不正确！正确使用如：SO#yyyyMMdd#6 或者 SOyyyyMMdd#6");
        }
    }

    /** @deprecated */
    @Deprecated
    public String newConcurrencyCode(String format, String group) {
        return this.newCode(format, group, true);
    }

    /** @deprecated */
    @Deprecated
    public String newConcurrencyCode(String format, String group, boolean isEncrypt) {
        return this.newCode(format, group, isEncrypt);
    }

    private Long increment(String key, long increment) {
        Long no = this.redis.opsForValue().increment(key, increment);
        if (no == null) {
            throw new RuntimeException("生成流水号redis自增返回为空");
        } else {
            return no;
        }
    }

    private String handleNo(Serializable no, int length, boolean isEncrypt) {
        String code = this.stringFormat(no, length);
        int l = code.length();
        if (l > length) {
            code = code.substring(l - length, l);
        }

        for(int i = length - 1; isEncrypt && i > 0; code = this.secret(code)) {
            --i;
        }

        return code;
    }

    private void setKeyExpireAt(String key, String[] groupArr) {
        String str = groupArr[0];
        if (groupArr.length == 2) {
            str = groupArr[1];
        }

        int expireDays = -1;
        if (str.contains(DATE_FORMAT_ARR[3])) {
            expireDays = 3;
        } else if (str.contains(DATE_FORMAT_ARR[2])) {
            expireDays = 40;
        } else if (str.contains(DATE_FORMAT_ARR[1]) || str.contains(DATE_FORMAT_ARR[0])) {
            expireDays = 380;
        }

        if (expireDays > 0) {
            this.redis.expire(key, (long)expireDays, TimeUnit.DAYS);
        }

    }

    private String secret(String str) {
        int len = str.length();
        String first = len > 2 ? str.substring(0, 1) : "";
        String high = len > 3 ? str.substring(1, len - 2) : "";
        String low = (String) this.secretMap.get(str.substring(len - 2, len));
        return high + low + first;
    }

    private String replace(LocalDate now, String str) {
        String[] var3 = DATE_FORMAT_ARR;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String format = var3[var5];
            if (str.contains(format)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                String date = now.format(formatter);
                str = str.replace(format, date);
            }
        }

        return str;
    }

    public static void main(String[] args) {
        System.out.println(Math.random() * 100.0 * 100.0);
        String ss = "1#_";
        String[] split = ss.split("#");
        System.out.println(JsonUtil.bean2Json(split));
    }
}

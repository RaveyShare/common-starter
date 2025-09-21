//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.redis;

import java.util.UUID;

public class LockParam {
    private final String key;
    private final String value;

    public LockParam(String key) {
        this.key = key;
        this.value = UUID.randomUUID().toString();
    }

    public LockParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}

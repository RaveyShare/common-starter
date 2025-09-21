/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.TableField
 *  com.baomidou.mybatisplus.annotation.TableLogic
 *  com.baomidou.mybatisplus.annotation.Version
 */
package com.ravey.common.dao.mp.entity;

import com.ravey.common.dao.mp.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;

public class BaseBizEntity
extends BaseEntity
implements Serializable {
    private static final long serialVersionUID = -1434904198682162055L;
    public static final String IS_DELETED = "is_deleted";
    public static final String VERSION = "version";
    public static final String REMARK = "remark";
    @TableLogic(value="0", delval="1")
    @TableField(value="is_deleted")
    private Integer isDeleted;
    @Version
    @TableField(value="version")
    private Long version;
    @TableField(value="remark")
    private String remark;

    @Override
    public void cleanInit() {
        super.cleanInit();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BaseBizEntity)) {
            return false;
        }
        BaseBizEntity other = (BaseBizEntity)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Integer this$isDeleted = this.getIsDeleted();
        Integer other$isDeleted = other.getIsDeleted();
        if (this$isDeleted == null ? other$isDeleted != null : !((Object)this$isDeleted).equals(other$isDeleted)) {
            return false;
        }
        Long this$version = this.getVersion();
        Long other$version = other.getVersion();
        if (this$version == null ? other$version != null : !((Object)this$version).equals(other$version)) {
            return false;
        }
        String this$remark = this.getRemark();
        String other$remark = other.getRemark();
        return !(this$remark == null ? other$remark != null : !this$remark.equals(other$remark));
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof BaseBizEntity;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Integer $isDeleted = this.getIsDeleted();
        result = result * 59 + ($isDeleted == null ? 43 : ((Object)$isDeleted).hashCode());
        Long $version = this.getVersion();
        result = result * 59 + ($version == null ? 43 : ((Object)$version).hashCode());
        String $remark = this.getRemark();
        result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
        return result;
    }

    public Integer getIsDeleted() {
        return this.isDeleted;
    }

    public Long getVersion() {
        return this.version;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "BaseBizEntity(isDeleted=" + this.getIsDeleted() + ", version=" + this.getVersion() + ", remark=" + this.getRemark() + ")";
    }
}
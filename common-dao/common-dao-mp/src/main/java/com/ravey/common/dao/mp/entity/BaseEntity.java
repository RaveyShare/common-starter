/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.FieldFill
 *  com.baomidou.mybatisplus.annotation.TableField
 *  com.baomidou.mybatisplus.annotation.TableId
 */
package com.ravey.common.dao.mp.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

public class BaseEntity
implements Serializable {
    private static final long serialVersionUID = -6234220980944858070L;
    public static final String ID = "id";
    public static final String CREATOR = "creator";
    public static final String CREATOR_ID = "creator_id";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATER = "updater";
    public static final String UPDATER_ID = "updater_id";
    public static final String UPDATE_TIME = "update_time";
    @TableId(value="id")
    private Long id;
    @TableField(value="creator", fill=FieldFill.INSERT)
    private String creator;
    @TableField(value="creator_id", fill=FieldFill.INSERT)
    private String creatorId;
    @TableField(value="create_time", fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value="updater", fill=FieldFill.INSERT_UPDATE)
    private String updater;
    @TableField(value="updater_id", fill=FieldFill.INSERT_UPDATE)
    private String updaterId;
    @TableField(value="update_time", fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public void cleanInit() {
    }

    public Long getId() {
        return this.id;
    }

    public String getCreator() {
        return this.creator;
    }

    public String getCreatorId() {
        return this.creatorId;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public String getUpdater() {
        return this.updater;
    }

    public String getUpdaterId() {
        return this.updaterId;
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public void setUpdaterId(String updaterId) {
        this.updaterId = updaterId;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BaseEntity)) {
            return false;
        }
        BaseEntity other = (BaseEntity)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        String this$creator = this.getCreator();
        String other$creator = other.getCreator();
        if (this$creator == null ? other$creator != null : !this$creator.equals(other$creator)) {
            return false;
        }
        String this$creatorId = this.getCreatorId();
        String other$creatorId = other.getCreatorId();
        if (this$creatorId == null ? other$creatorId != null : !this$creatorId.equals(other$creatorId)) {
            return false;
        }
        LocalDateTime this$createTime = this.getCreateTime();
        LocalDateTime other$createTime = other.getCreateTime();
        if (this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime)) {
            return false;
        }
        String this$updater = this.getUpdater();
        String other$updater = other.getUpdater();
        if (this$updater == null ? other$updater != null : !this$updater.equals(other$updater)) {
            return false;
        }
        String this$updaterId = this.getUpdaterId();
        String other$updaterId = other.getUpdaterId();
        if (this$updaterId == null ? other$updaterId != null : !this$updaterId.equals(other$updaterId)) {
            return false;
        }
        LocalDateTime this$updateTime = this.getUpdateTime();
        LocalDateTime other$updateTime = other.getUpdateTime();
        return !(this$updateTime == null ? other$updateTime != null : !((Object)this$updateTime).equals(other$updateTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof BaseEntity;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        String $creator = this.getCreator();
        result = result * 59 + ($creator == null ? 43 : $creator.hashCode());
        String $creatorId = this.getCreatorId();
        result = result * 59 + ($creatorId == null ? 43 : $creatorId.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        String $updater = this.getUpdater();
        result = result * 59 + ($updater == null ? 43 : $updater.hashCode());
        String $updaterId = this.getUpdaterId();
        result = result * 59 + ($updaterId == null ? 43 : $updaterId.hashCode());
        LocalDateTime $updateTime = this.getUpdateTime();
        result = result * 59 + ($updateTime == null ? 43 : ((Object)$updateTime).hashCode());
        return result;
    }

    public String toString() {
        return "BaseEntity(id=" + this.getId() + ", creator=" + this.getCreator() + ", creatorId=" + this.getCreatorId() + ", createTime=" + this.getCreateTime() + ", updater=" + this.getUpdater() + ", updaterId=" + this.getUpdaterId() + ", updateTime=" + this.getUpdateTime() + ")";
    }
}
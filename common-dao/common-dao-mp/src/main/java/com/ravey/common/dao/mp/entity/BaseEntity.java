package com.ravey.common.dao.mp.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 * 包含所有表的通用字段：主键、创建人、创建时间、更新人、更新时间
 * 所有业务实体类都应该继承此类
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = -6234220980944858070L;

    // 字段名常量定义
    public static final String ID = "id";
    public static final String CREATOR = "creator";
    public static final String CREATOR_ID = "creator_id";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATER = "updater";
    public static final String UPDATER_ID = "updater_id";
    public static final String UPDATE_TIME = "update_time";

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 创建人姓名
     */
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    /**
     * 创建人ID
     */
    @TableField(value = "creator_id", fill = FieldFill.INSERT)
    private String creatorId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人姓名
     */
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    /**
     * 更新人ID
     */
    @TableField(value = "updater_id", fill = FieldFill.INSERT_UPDATE)
    private String updaterId;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
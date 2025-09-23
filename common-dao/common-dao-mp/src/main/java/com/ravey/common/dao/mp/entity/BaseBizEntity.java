package com.ravey.common.dao.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * 业务基础实体类
 * 继承自 BaseEntity，增加了业务相关的通用字段：逻辑删除标识、版本号
 * 适用于需要逻辑删除和乐观锁功能的业务表
 *
 * @author ravey
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseBizEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // 字段名常量定义
    public static final String IS_DELETED = "is_deleted";
    public static final String VERSION = "version";
    public static final String REMARK = "remark";

    /**
     * 逻辑删除标识
     * false: 未删除
     * true: 已删除
     */
    @TableLogic(value = "false", delval = "true")
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    /**
     * 版本号，用于乐观锁控制
     */
    @Version
    @TableField(value = "version")
    private Integer version;
    @TableField(value = "remark")
    private String remark;


}
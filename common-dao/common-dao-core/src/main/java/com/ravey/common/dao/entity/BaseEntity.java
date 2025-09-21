
package com.ravey.common.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
public class BaseEntity
implements Serializable {
    private static final long serialVersionUID = -4477584932415485999L;
    private Long id;
    private Long version;
    private Integer isDeleted;
    private String creator;
    private String creatorId;
    private LocalDateTime createTime;
    private String updater;
    private String updaterId;
    private LocalDateTime updateTime;
}

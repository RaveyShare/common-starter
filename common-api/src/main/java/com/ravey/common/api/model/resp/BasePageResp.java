package com.ravey.common.api.model.resp;



import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BasePageResp<T> extends BaseResp {
    private static final long serialVersionUID = -3494099557546069200L;
    private Long pageNo;
    private Long pageSize;
    private Long total;
    private Long pages;
    private List<T> data;

    public Long getPages() {
        return this.pages == null ? (this.total + this.pageSize - 1L) / this.pageSize : this.pages;
    }

}

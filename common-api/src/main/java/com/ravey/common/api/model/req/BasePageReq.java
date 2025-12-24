package com.ravey.common.api.model.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BasePageReq extends BaseReq {
    static final long serialVersionUID = -21555977526590037L;
    private long pageNo = 1L;
    private long pageSize = 10L;
    private String sorted;

    public long getOffset() {
        return (this.pageNo - 1L) * this.pageSize;
    }


}


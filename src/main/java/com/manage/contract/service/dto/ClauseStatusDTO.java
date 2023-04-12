package com.manage.contract.service.dto;

import com.manage.contract.domain.ClauseStatus;
import com.manage.contract.domain.EntityStatus;

public class ClauseStatusDTO {

    public EntityStatus status;

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = EntityStatus.valueOf(status);
    }
}

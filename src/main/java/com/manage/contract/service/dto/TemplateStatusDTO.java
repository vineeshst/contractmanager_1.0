package com.manage.contract.service.dto;

import com.manage.contract.domain.EntityStatus;
import com.manage.contract.domain.TemplateStatus;

public class TemplateStatusDTO {

    public EntityStatus status;

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = EntityStatus.valueOf(status);
    }
}

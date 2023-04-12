package com.manage.contract.service.dto;

import com.manage.contract.domain.EntityStatus;

public class ContractTypeStatusDTO {

    public EntityStatus status;

    public String message;

    public ContractTypeStatusDTO(EntityStatus status) {
        this.status = status;
    }

    public ContractTypeStatusDTO() {}

    public String getStatus() {
        return status.name();
    }

    public void setStatus(String status) {
        this.status = EntityStatus.valueOf(status);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

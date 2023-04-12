package com.manage.contract.domain;

public enum EntityStatus {
    DRAFT,
    CREATED,
    APPROVAL_PENDING,
    APPROVED,
    APPROVAL_REJECTED,
    SENT_FOR_SIGNATURE,
    SIGNER_REJECTED,
    COMPLETED,
    CANCELED,
    EXPIRED,
    REJECTED,
    ERROR,
    DELETED,
}

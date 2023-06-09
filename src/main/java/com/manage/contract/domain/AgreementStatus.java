package com.manage.contract.domain;

public enum AgreementStatus {
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
}
//APPROVE_RETURNED, SIGNER_RETURNED -> DRAFT
//SIGNED -> COMPLETED

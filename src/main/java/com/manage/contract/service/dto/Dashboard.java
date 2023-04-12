package com.manage.contract.service.dto;

public class Dashboard {

    private long taskCount;

    private long agreementsCount;

    private long expiringAgreementsCount;

    private long agreementsPendingApprovalCount;

    public long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(long taskCount) {
        this.taskCount = taskCount;
    }

    public long getAgreementsCount() {
        return agreementsCount;
    }

    public void setAgreementsCount(long agreementsCount) {
        this.agreementsCount = agreementsCount;
    }

    public long getExpiringAgreementsCount() {
        return expiringAgreementsCount;
    }

    public void setExpiringAgreementsCount(long expiringAgreementsCount) {
        this.expiringAgreementsCount = expiringAgreementsCount;
    }

    public long getAgreementsPendingApprovalCount() {
        return agreementsPendingApprovalCount;
    }

    public void setAgreementsPendingApprovalCount(long agreementsPendingApprovalCount) {
        this.agreementsPendingApprovalCount = agreementsPendingApprovalCount;
    }
}

package com.manage.contract.domain;

public class UserApprovalStatus {

    private String userId;

    private boolean hasApproved = true;

    public UserApprovalStatus(String userId, boolean hasApproved) {
        this.hasApproved = hasApproved;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isHasApproved() {
        return hasApproved;
    }

    public void setHasApproved(boolean hasApproved) {
        this.hasApproved = hasApproved;
    }
}

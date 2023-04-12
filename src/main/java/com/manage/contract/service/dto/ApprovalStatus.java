package com.manage.contract.service.dto;

import com.manage.contract.domain.UserApprovalStatus;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;

public class ApprovalStatus {

    private ApprovalCriteria approvalCriteria = ApprovalCriteria.ALL;

    private List<UserApprovalStatus> userApprovalStatus;

    public ApprovalCriteria getApprovalCriteria() {
        return approvalCriteria;
    }

    public void setApprovalCriteria(ApprovalCriteria approvalCriteria) {
        this.approvalCriteria = approvalCriteria;
    }

    public List<UserApprovalStatus> getUserApprovalStatus() {
        if (this.userApprovalStatus == null) userApprovalStatus = new ArrayList<>();
        return userApprovalStatus;
    }

    public void addApprovalStatus(UserApprovalStatus approvalStatus) {
        if (userApprovalStatus == null) userApprovalStatus = new ArrayList<>();

        var opStatus = userApprovalStatus.stream().filter(as -> as.getUserId().equals(approvalStatus.getUserId())).findFirst();
        opStatus.ifPresent(status -> status.setHasApproved(true));
        if (opStatus.isEmpty()) userApprovalStatus.add(approvalStatus);
    }

    public void setUserApprovalStatus(List<UserApprovalStatus> userApprovalStatus) {
        this.userApprovalStatus = userApprovalStatus;
    }
}

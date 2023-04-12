package com.manage.contract.domain;

import java.util.List;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class TeamGroups {

    @DBRef
    private List<UserGroup> secondaryOwner;

    @DBRef
    private List<UserGroup> contractAdmin;

    @DBRef
    private List<UserGroup> approver;

    @DBRef
    private List<UserGroup> observer;

    @DBRef
    private List<UserGroup> externalReviewer;

    @DBRef
    private List<UserGroup> externalSignatory;

    public List<UserGroup> getSecondaryOwner() {
        return secondaryOwner;
    }

    public void setSecondaryOwner(List<UserGroup> secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public List<UserGroup> getContractAdmin() {
        return contractAdmin;
    }

    public void setContractAdmin(List<UserGroup> contractAdmin) {
        this.contractAdmin = contractAdmin;
    }

    public List<UserGroup> getApprover() {
        return approver;
    }

    public void setApprover(List<UserGroup> approver) {
        this.approver = approver;
    }

    public List<UserGroup> getObserver() {
        return observer;
    }

    public void setObserver(List<UserGroup> observer) {
        this.observer = observer;
    }

    public List<UserGroup> getExternalReviewer() {
        return externalReviewer;
    }

    public void setExternalReviewer(List<UserGroup> externalReviewer) {
        this.externalReviewer = externalReviewer;
    }

    public List<UserGroup> getExternalSignatory() {
        return externalSignatory;
    }

    public void setExternalSignatory(List<UserGroup> externalSignatory) {
        this.externalSignatory = externalSignatory;
    }
}

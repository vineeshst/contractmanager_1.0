package com.manage.contract.domain;

import java.util.List;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class Team {

    @DBRef
    private User primaryOwner;

    @DBRef
    private List<User> secondaryOwner;

    @DBRef
    private List<User> contractAdmin;

    @DBRef
    private List<User> approver;

    @DBRef
    private List<User> observer;

    @DBRef
    private List<User> externalReviewer;

    @DBRef
    private List<User> externalSignatory;

    public User getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(User primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public List<User> getSecondaryOwner() {
        return secondaryOwner;
    }

    public void setSecondaryOwner(List<User> secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public List<User> getContractAdmin() {
        return contractAdmin;
    }

    public void setContractAdmin(List<User> contractAdmin) {
        this.contractAdmin = contractAdmin;
    }

    public List<User> getApprover() {
        return approver;
    }

    public void setApprover(List<User> approver) {
        this.approver = approver;
    }

    public List<User> getObserver() {
        return observer;
    }

    public void setObserver(List<User> observer) {
        this.observer = observer;
    }

    public List<User> getExternalReviewer() {
        return externalReviewer;
    }

    public void setExternalReviewer(List<User> externalReviewer) {
        this.externalReviewer = externalReviewer;
    }

    public List<User> getExternalSignatory() {
        return externalSignatory;
    }

    public void setExternalSignatory(List<User> externalSignatory) {
        this.externalSignatory = externalSignatory;
    }
}

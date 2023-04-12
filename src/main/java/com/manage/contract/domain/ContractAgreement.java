package com.manage.contract.domain;

import com.manage.contract.service.dto.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.types.Binary;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class ContractAgreement extends Model implements IEntity {

    @DBRef(lazy = true)
    private ContractCategory contractCategory;

    @DBRef(lazy = true)
    private ContractType contractType;

    private String agreementName;

    private String agreementDescription;

    private String agreementCode;

    private Set<Attribute> attributes;

    private EntityStatus agreementStatus;

    private Instant updatedOn;

    @Field("created_by")
    private String createdBy;

    @CreatedDate
    @Field("created_date")
    private Instant createdDate = Instant.now();

    @DBRef(lazy = true)
    private ContractTemplate template;

    private Team team;
    private TeamGroups teamGroups;

    private String filePath;
    private Binary agreementFile;
    private String signAgreementId;
    private String approverComment;
    private int version;
    private List<EventHistory> history = new ArrayList<>();

    @Transient
    private Privileges currentUserPrivilege = Privileges.NONE;

    private String entityRole;
    private ApprovalStatus approvalStatus;
    private NotificationConfig notificationConfig;

    public ContractAgreement() {}

    public ContractAgreement(ContractAgreementForm contractAgreementForm) {
        //        this.id = contractAgreement.getID();
        this.contractCategory = contractAgreementForm.getContractCategory();
        this.contractType = contractAgreementForm.getContractType();
        this.agreementName = contractAgreementForm.getAgreementName();
        this.agreementDescription = contractAgreementForm.getAgreementDescription();
        this.agreementCode = contractAgreementForm.getAgreementCode();
        this.attributes = contractAgreementForm.getAttributes();
        this.agreementStatus = contractAgreementForm.getAgreementStatus();
        this.updatedOn = contractAgreementForm.getUpdatedOn();
        this.createdBy = contractAgreementForm.getCreatedBy();
        this.createdDate = contractAgreementForm.getCreatedDate();
        this.template = contractAgreementForm.getTemplate();
        this.team = contractAgreementForm.getTeam();
        this.filePath = contractAgreementForm.getFilePath();
        this.agreementFile = contractAgreementForm.getAgreementFile();
        this.signAgreementId = contractAgreementForm.getSignAgreementId();
        this.approverComment = contractAgreementForm.getApproverComment();
        this.version = contractAgreementForm.getVersion();
        this.history = contractAgreementForm.getHistory();
        this.notificationConfig = contractAgreementForm.getNotificationConfig();
        this.teamGroups = contractAgreementForm.getTeamGroups();
    }

    public ContractCategory getContractCategory() {
        return contractCategory;
    }

    public void setContractCategory(ContractCategory contractCategory) {
        this.contractCategory = contractCategory;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public String getAgreementName() {
        return agreementName;
    }

    public void setAgreementName(String agreementName) {
        this.agreementName = agreementName;
    }

    public String getAgreementDescription() {
        return agreementDescription;
    }

    public void setAgreementDescription(String agreementDescription) {
        this.agreementDescription = agreementDescription;
    }

    public String getAgreementCode() {
        return agreementCode;
    }

    public void setAgreementCode(String agreementCode) {
        this.agreementCode = agreementCode;
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Instant updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public ContractTemplate getTemplate() {
        return template;
    }

    public void setTemplate(ContractTemplate template) {
        this.template = template;
    }

    public EntityStatus getAgreementStatus() {
        return agreementStatus;
    }

    public void setAgreementStatus(EntityStatus agreementStatus) {
        this.agreementStatus = agreementStatus;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public TeamGroups getTeamGroups() {
        return teamGroups;
    }

    public void setTeamGroups(TeamGroups teamGroups) {
        this.teamGroups = teamGroups;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Binary getAgreementFile() {
        return agreementFile;
    }

    public void setAgreementFile(Binary agreementFile) {
        this.agreementFile = agreementFile;
    }

    public String getSignAgreementId() {
        return signAgreementId;
    }

    public void setSignAgreementId(String signAgreementId) {
        this.signAgreementId = signAgreementId;
    }

    public String getApproverComment() {
        return approverComment;
    }

    public void setApproverComment(String approverComment) {
        this.approverComment = approverComment;
    }

    public List<EventHistory> getHistory() {
        return history;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    public void setHistory(List<EventHistory> history) {
        this.history = history;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Privileges getCurrentUserPrivilege() {
        return currentUserPrivilege;
    }

    public void setCurrentUserPrivilege(Privileges currentUserPrivilege) {
        this.currentUserPrivilege = currentUserPrivilege;
    }

    public void incrementVersion() {
        this.version = this.version + 1;
    }

    @Override
    public Set<User> getApprover() {
        return this.getApprover(this.getTeam(), this.getTeamGroups());
    }

    @Override
    public String getEntityId() {
        return this.getID();
    }

    @Override
    public String getEntityName() {
        return this.getAgreementName();
    }

    @Override
    public String getEntityRole() {
        return this.entityRole;
    }

    @Override
    public ApprovalStatus getApprovalStatus() {
        return this.approvalStatus == null ? new ApprovalStatus() : this.approvalStatus;
    }

    @Override
    public EntityStatus getEntityStatus() {
        return this.agreementStatus;
    }

    public void setEntityRole(String entityRole) {
        this.entityRole = entityRole;
    }

    public NotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public void setNotificationConfig(NotificationConfig notificationConfig) {
        this.notificationConfig = notificationConfig;
    }

    @Override
    public boolean isNotificationEnabled(String entityId) {
        if (getNotificationConfig() == null || getNotificationConfig().getUserConfig() == null) return true;
        return getNotificationConfig().getUserConfig().getOrDefault(entityId, false);
    }
}

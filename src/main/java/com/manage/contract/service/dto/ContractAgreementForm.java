package com.manage.contract.service.dto;

import com.manage.contract.domain.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bson.types.Binary;

public class ContractAgreementForm {

    private String id;
    private ContractCategory contractCategory;
    private ContractType contractType;
    private String agreementName;
    private String agreementDescription;
    private String agreementCode;
    private Set<Attribute> attributes;
    private EntityStatus agreementStatus;
    private Instant updatedOn;
    private String createdBy;
    private Instant createdDate;
    private ContractTemplate template;
    private Team team;
    private TeamGroups teamGroups;
    private String filePath;
    private Binary agreementFile;
    private String signAgreementId;
    private String approverComment;
    private int version;
    private List<EventHistory> history = new ArrayList<>();
    private String tempAgreementFile;
    private String tempTemplateFile;
    private Boolean genNewDocToUpdate = false;
    private String entityRole;
    private NotificationConfig notificationConfig;

    public ContractAgreementForm() {}

    public ContractAgreementForm(ContractAgreement contractAgreement) {
        this.id = contractAgreement.getID();
        this.contractCategory = contractAgreement.getContractCategory();
        this.contractType = contractAgreement.getContractType();
        this.agreementName = contractAgreement.getAgreementName();
        this.agreementDescription = contractAgreement.getAgreementDescription();
        this.agreementCode = contractAgreement.getAgreementCode();
        this.attributes = contractAgreement.getAttributes();
        this.agreementStatus = contractAgreement.getAgreementStatus();
        this.updatedOn = contractAgreement.getUpdatedOn();
        this.createdBy = contractAgreement.getCreatedBy();
        this.createdDate = contractAgreement.getCreatedDate();
        this.template = contractAgreement.getTemplate();
        this.team = contractAgreement.getTeam();
        this.filePath = contractAgreement.getFilePath();
        this.agreementFile = contractAgreement.getAgreementFile();
        this.signAgreementId = contractAgreement.getSignAgreementId();
        this.approverComment = contractAgreement.getApproverComment();
        this.version = contractAgreement.getVersion();
        this.history = contractAgreement.getHistory();
        this.notificationConfig = contractAgreement.getNotificationConfig();
        this.teamGroups = contractAgreement.getTeamGroups();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public EntityStatus getAgreementStatus() {
        return agreementStatus;
    }

    public void setAgreementStatus(EntityStatus agreementStatus) {
        this.agreementStatus = agreementStatus;
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<EventHistory> getHistory() {
        return history;
    }

    public void setHistory(List<EventHistory> history) {
        this.history = history;
    }

    public String getTempAgreementFile() {
        return tempAgreementFile;
    }

    public void setTempAgreementFile(String tempAgreementFile) {
        this.tempAgreementFile = tempAgreementFile;
    }

    public String getTempTemplateFile() {
        return tempTemplateFile;
    }

    public void setTempTemplateFile(String tempTemplateFile) {
        this.tempTemplateFile = tempTemplateFile;
    }

    public Boolean getGenNewDocToUpdate() {
        return genNewDocToUpdate;
    }

    public void setGenNewDocToUpdate(Boolean genNewDocToUpdate) {
        this.genNewDocToUpdate = genNewDocToUpdate;
    }

    public void incrementVersion() {
        this.version = this.version + 1;
    }

    public String getEntityRole() {
        return entityRole;
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

    public TeamGroups getTeamGroups() {
        return teamGroups;
    }

    public void setTeamGroups(TeamGroups teamGroups) {
        this.teamGroups = teamGroups;
    }
}

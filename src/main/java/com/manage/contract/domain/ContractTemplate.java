package com.manage.contract.domain;

import com.manage.contract.service.dto.ApprovalStatus;
import com.manage.contract.service.dto.IEntity;
import java.time.Instant;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Set;
import org.bson.types.Binary;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class ContractTemplate extends Model implements IEntity {

    @TextIndexed
    private String templateName;

    private String code;

    @DBRef(lazy = true)
    private ContractCategory contractCategory;

    @DBRef(lazy = true)
    private ContractType contractType;

    private String filePath;
    private Binary templateFile;
    private String language;
    private String secondaryLanguage;
    private String dateFormat;
    private Date effectiveFrom;
    private Date effectiveTo;
    private EntityStatus status;
    private Boolean isPrimary;
    private String description;
    private int version;
    private Team team;
    private TeamGroups teamGroups;
    private Instant updatedOn;

    @Field("created_by")
    private String createdBy;

    @CreatedDate
    @Field("created_date")
    private Instant createdDate = Instant.now();

    private List<EventHistory> history = new ArrayList<>();

    public ContractTemplate() {}

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Binary getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(Binary templateFile) {
        this.templateFile = templateFile;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSecondaryLanguage() {
        return secondaryLanguage;
    }

    public void setSecondaryLanguage(String secondaryLanguage) {
        this.secondaryLanguage = secondaryLanguage;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public Date getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(Date effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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
        return this.getTemplateName();
    }

    @Override
    public String getEntityRole() {
        return null;
    }

    @Override
    public ApprovalStatus getApprovalStatus() {
        return null;
    }

    @Override
    public EntityStatus getEntityStatus() {
        return null;
    }

    //    private Set<ContractAgreement> contractAgreements;

    @Override
    public boolean isNotificationEnabled(String entityId) {
        return false;
    }
}

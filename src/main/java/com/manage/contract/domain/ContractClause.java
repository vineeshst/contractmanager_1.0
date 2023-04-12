package com.manage.contract.domain;

import com.manage.contract.service.dto.ApprovalStatus;
import com.manage.contract.service.dto.IEntity;
import java.time.Instant;
import java.time.Instant;
import java.util.*;
import java.util.Date;
import org.bson.types.Binary;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class ContractClause extends Model implements IEntity {

    @TextIndexed
    private String clauseName;

    private String code;

    @DBRef(lazy = true)
    private ContractCategory contractCategory;

    @DBRef(lazy = true)
    private ContractType contractType;

    private String filePath;
    private Binary clauseFile;
    private String language;
    private EntityStatus status;
    private String description;
    private Boolean isEditable;
    private Boolean isDeviationAnalysis;
    private Boolean isDependentClause;
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

    public ContractClause() {}

    public String getClauseName() {
        return clauseName;
    }

    public void setClauseName(String clauseName) {
        this.clauseName = clauseName;
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

    public Binary getClauseFile() {
        return clauseFile;
    }

    public void setClauseFile(Binary clauseFile) {
        this.clauseFile = clauseFile;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEditable() {
        return isEditable;
    }

    public void setEditable(Boolean editable) {
        isEditable = editable;
    }

    public Boolean getDeviationAnalysis() {
        return isDeviationAnalysis;
    }

    public void setDeviationAnalysis(Boolean deviationAnalysis) {
        isDeviationAnalysis = deviationAnalysis;
    }

    public Boolean getDependentClause() {
        return isDependentClause;
    }

    public void setDependentClause(Boolean dependentClause) {
        isDependentClause = dependentClause;
    }

    public Integer getVersion() {
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
        return this.getClauseName();
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

    @Override
    public boolean isNotificationEnabled(String entityId) {
        return false;
    }
}

package com.manage.contract.service.dto;

import com.manage.contract.domain.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.Binary;

public class ContractClauseForm {

    private String id;
    private String clauseName;
    private String code;
    private ContractCategory contractCategory;
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
    private Instant updatedOn;
    private String createdBy;
    private Instant createdDate;
    private Boolean isClauseUploaded;
    private String tempFileName;
    private List<EventHistory> history = new ArrayList<>();
    private String entityRole;

    public ContractClauseForm() {}

    public ContractClauseForm(ContractClause contractClause) {
        this.id = contractClause.getID();
        this.clauseName = contractClause.getClauseName();
        this.code = contractClause.getCode();
        this.contractCategory = contractClause.getContractCategory();
        this.contractType = contractClause.getContractType();
        this.filePath = contractClause.getFilePath();
        this.language = contractClause.getLanguage();
        this.status = contractClause.getStatus();
        this.description = contractClause.getDescription();
        this.isEditable = contractClause.getEditable();
        this.isDeviationAnalysis = contractClause.getDeviationAnalysis();
        this.isDependentClause = contractClause.getDependentClause();
        this.version = contractClause.getVersion();
        this.team = contractClause.getTeam();
        this.updatedOn = contractClause.getUpdatedOn();
        this.createdBy = contractClause.getCreatedBy();
        this.createdDate = contractClause.getCreatedDate();
        this.history = contractClause.getHistory();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Boolean getClauseUploaded() {
        return isClauseUploaded;
    }

    public void setClauseUploaded(Boolean clauseUploaded) {
        isClauseUploaded = clauseUploaded;
    }

    public String getTempFileName() {
        return tempFileName;
    }

    public void setTempFileName(String tempFileName) {
        this.tempFileName = tempFileName;
    }

    public List<EventHistory> getHistory() {
        return history;
    }

    public void setHistory(List<EventHistory> history) {
        this.history = history;
    }

    public String getEntityRole() {
        return entityRole;
    }

    public void setEntityRole(String entityRole) {
        this.entityRole = entityRole;
    }
}

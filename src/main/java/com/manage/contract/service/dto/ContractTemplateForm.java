package com.manage.contract.service.dto;

import com.manage.contract.domain.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bson.types.Binary;

public class ContractTemplateForm {

    private String id;
    private String templateName;
    private String code;
    private ContractCategory contractCategory;
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
    private String versionComments;
    private Team team;
    private Instant updatedOn;
    private String createdBy;
    private Instant createdDate;
    private Boolean isTemplateUploaded;
    private String tempFileName;
    private List<EventHistory> history = new ArrayList<>();
    private String entityRole;

    public ContractTemplateForm() {}

    public ContractTemplateForm(ContractTemplate contractTemplate) {
        this.id = contractTemplate.getID();
        this.templateName = contractTemplate.getTemplateName();
        this.code = contractTemplate.getCode();
        this.contractCategory = contractTemplate.getContractCategory();
        this.contractType = contractTemplate.getContractType();
        this.filePath = contractTemplate.getFilePath();
        this.language = contractTemplate.getLanguage();
        this.secondaryLanguage = contractTemplate.getSecondaryLanguage();
        this.dateFormat = contractTemplate.getDateFormat();
        this.effectiveFrom = contractTemplate.getEffectiveFrom();
        this.effectiveTo = contractTemplate.getEffectiveTo();
        this.status = contractTemplate.getStatus();
        this.isPrimary = contractTemplate.getPrimary();
        this.description = contractTemplate.getDescription();
        this.version = contractTemplate.getVersion();
        this.team = contractTemplate.getTeam();
        this.updatedOn = contractTemplate.getUpdatedOn();
        this.createdBy = contractTemplate.getCreatedBy();
        this.createdDate = contractTemplate.getCreatedDate();
        this.history = contractTemplate.getHistory();
        this.entityRole = contractTemplate.getEntityRole();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Boolean getTemplateUploaded() {
        return isTemplateUploaded;
    }

    public void setTemplateUploaded(Boolean templateUploaded) {
        isTemplateUploaded = templateUploaded;
    }

    public String getTempFileName() {
        return tempFileName;
    }

    public void setTempFileName(String tempFileName) {
        this.tempFileName = tempFileName;
    }

    public String getEntityRole() {
        return entityRole;
    }

    public void setEntityRole(String entityRole) {
        this.entityRole = entityRole;
    }
}

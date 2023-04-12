package com.manage.contract.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.manage.contract.domain.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContractTypeForm {

    private String contractCategory;
    private String contractTypeName;
    private String contractTypeCode;
    private String description;
    private boolean allowThirdPartyPaper;
    private boolean allowClauseAssembly;
    private boolean qrCode;
    private boolean allowCopyWithAssociations;
    private boolean twoColumnAttributeLayout;
    private boolean enableCollaboration;
    private boolean enableAutoSupersede;
    private boolean expandDropdownOnMouseHover;
    private List<AttributeConfig> attributes;
    private DisplayPreference displayPreference;

    @JsonProperty("attributesMetaData")
    private List<Map<String, Object>> attributesMetaData;

    @JsonProperty("associations")
    private Set<AssociateContractType> associations;

    private String createdBy;
    private EntityStatus status;

    private Team team;

    private TeamGroups teamGroups;

    public String getContractCategory() {
        return contractCategory;
    }

    public void setContractCategory(String contractCategory) {
        this.contractCategory = contractCategory;
    }

    public String getContractTypeName() {
        return contractTypeName;
    }

    public void setContractTypeName(String contractTypeName) {
        this.contractTypeName = contractTypeName;
    }

    public String getContractTypeCode() {
        return contractTypeCode;
    }

    public void setContractTypeCode(String contractTypeCode) {
        this.contractTypeCode = contractTypeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String categoryDescription) {
        this.description = categoryDescription;
    }

    public boolean isAllowThirdPartyPaper() {
        return allowThirdPartyPaper;
    }

    public void setAllowThirdPartyPaper(boolean allowThirdPartyPaper) {
        this.allowThirdPartyPaper = allowThirdPartyPaper;
    }

    public boolean isAllowClauseAssembly() {
        return allowClauseAssembly;
    }

    public void setAllowClauseAssembly(boolean allowClauseAssembly) {
        this.allowClauseAssembly = allowClauseAssembly;
    }

    public boolean isQrCode() {
        return qrCode;
    }

    public void setQrCode(boolean qrCode) {
        this.qrCode = qrCode;
    }

    public boolean isAllowCopyWithAssociations() {
        return allowCopyWithAssociations;
    }

    public void setAllowCopyWithAssociations(boolean allowCopyWithAssociations) {
        this.allowCopyWithAssociations = allowCopyWithAssociations;
    }

    public boolean isTwoColumnAttributeLayout() {
        return twoColumnAttributeLayout;
    }

    public void setTwoColumnAttributeLayout(boolean twoColumnAttributeLayout) {
        this.twoColumnAttributeLayout = twoColumnAttributeLayout;
    }

    public boolean isEnableCollaboration() {
        return enableCollaboration;
    }

    public void setEnableCollaboration(boolean enableCollaboration) {
        this.enableCollaboration = enableCollaboration;
    }

    public boolean isEnableAutoSupersede() {
        return enableAutoSupersede;
    }

    public void setEnableAutoSupersede(boolean enableAutoSupersede) {
        this.enableAutoSupersede = enableAutoSupersede;
    }

    public boolean isExpandDropdownOnMouseHover() {
        return expandDropdownOnMouseHover;
    }

    public void setExpandDropdownOnMouseHover(boolean expandDropdownOnMouseHover) {
        this.expandDropdownOnMouseHover = expandDropdownOnMouseHover;
    }

    public List<Map<String, Object>> getAttributesMetaData() {
        return attributesMetaData;
    }

    public void setAttributesMetaData(List<Map<String, Object>> attributesMetaData) {
        this.attributesMetaData = attributesMetaData;
    }

    public Set<AssociateContractType> getAssociations() {
        return associations;
    }

    public void setAssociations(Set<AssociateContractType> associations) {
        this.associations = associations;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<AttributeConfig> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeConfig> attributes) {
        this.attributes = attributes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    public DisplayPreference getDisplayPreference() {
        return displayPreference;
    }

    public void setDisplayPreference(DisplayPreference displayPreference) {
        this.displayPreference = displayPreference;
    }

    public TeamGroups getTeamGroups() {
        return teamGroups;
    }

    public void setTeamGroups(TeamGroups teamGroups) {
        this.teamGroups = teamGroups;
    }
}

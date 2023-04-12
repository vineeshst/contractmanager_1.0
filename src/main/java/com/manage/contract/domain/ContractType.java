package com.manage.contract.domain;

import com.manage.contract.security.SecurityUtils;
import com.manage.contract.service.dto.*;
import java.time.Instant;
import java.util.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import reactor.core.publisher.Mono;

@Document
public class ContractType extends Model implements IEntity {

    @DBRef(lazy = true)
    private ContractCategory contractCategory;

    @TextIndexed
    private String name;

    private String code;
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

    private Set<AssociateContractType> associations;
    private int version;
    private Instant updatedOn;
    private Set<ContractTemplateForm> templates = new HashSet<>();
    private Set<ContractClauseForm> clauses = new HashSet<>();
    private EntityStatus status;
    private Team team;
    private TeamGroups teamGroups;

    private ApprovalStatus approvalStatus;
    private String entityRole;

    @Field("created_by")
    private String createdBy;

    @CreatedDate
    @Field("created_date")
    private Instant createdDate = Instant.now();

    private List<EventHistory> history = new ArrayList<>();
    private DisplayPreference displayPreference;

    private boolean isDeleted = false;

    public ContractCategory getContractCategory() {
        return contractCategory;
    }

    public void setContractCategory(ContractCategory contractCategory) {
        this.contractCategory = contractCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Set<AssociateContractType> getAssociations() {
        return associations;
    }

    public void setAssociations(Set<AssociateContractType> associations) {
        this.associations = associations;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Instant updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Set<ContractTemplateForm> getTemplates() {
        return templates;
    }

    public void setTemplates(Set<ContractTemplateForm> templates) {
        this.templates = templates;
    }

    public Set<ContractClauseForm> getClauses() {
        return clauses;
    }

    public void setClauses(Set<ContractClauseForm> clauses) {
        this.clauses = clauses;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    //    private Set<AssociateContractType> associations;
    //    private Set<ContractAgreement> contractAgreements;
    //    private Set<User> contractTypeApprovers;

    //    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contractType", cascade = CascadeType.ALL)
    //    private List<Attribute> attributes = new ArrayList<>();

    //    enum ContractTypeStatus
    //    {
    //        CREATED, APPROVED;
    //    }
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

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public List<EventHistory> getHistory() {
        if (this.history == null) this.history = new ArrayList<>();
        return history;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    //    public void setHistory(EventHistory history) {
    //        if (this.history == null) this.history = new ArrayList<EventHistory>();
    //
    //        this.history.add(history);
    //    }

    public void setHistory(List<EventHistory> history) {
        this.history = history;
    }

    public DisplayPreference getDisplayPreference() {
        return displayPreference;
    }

    public void setDisplayPreference(DisplayPreference displayPreference) {
        this.displayPreference = displayPreference;
    }

    public ApprovalStatus getApprovalStatus() {
        if (this.approvalStatus == null) approvalStatus = new ApprovalStatus();
        return approvalStatus;
    }

    @Override
    public EntityStatus getEntityStatus() {
        return this.getStatus();
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void incrementVersion() {
        this.version = this.version + 1;
    }

    @Override
    public Set<User> getApprover() {
        return this.getApprover(this.team, this.teamGroups);
    }

    @Override
    public String getEntityId() {
        return this.getID();
    }

    @Override
    public String getEntityName() {
        return this.getName();
    }

    @Override
    public String getEntityRole() {
        return this.entityRole;
    }

    public void setEntityRole(String entityRole) {
        this.entityRole = entityRole;
    }

    public TeamGroups getTeamGroups() {
        return teamGroups;
    }

    public void setTeamGroups(TeamGroups teamGroups) {
        this.teamGroups = teamGroups;
    }

    @Override
    public boolean isNotificationEnabled(String entityId) {
        return true;
    }
    //
    //    @Override
    //    public String getEntityRole(String userId) {
    //        if (this.team.getPrimaryOwner().getId().equals (userId))
    //            return "APPROVER";
    //        if (this.team.getApprover() != null && this.team.getApprover().stream().anyMatch(user -> user.getId().equals(userId)))
    //            return "APPROVER";
    //        if (this.team.getObserver() != null && this.team.getObserver().stream().anyMatch(user -> user.getId().equals(userId)))
    //            return "APPROVER";
    //        if (this.team.getExternalReviewer() != null && this.team.getExternalReviewer().stream().anyMatch(user -> user.getId().equals(userId)))
    //            return "APPROVER";
    //        if (this.team.getContractAdmin() != null && this.team.getContractAdmin().stream().anyMatch(user -> user.getId().equals(userId)))
    //            return "APPROVER";
    //        if (this.team.getSecondaryOwner() != null && this.team.getSecondaryOwner().stream().anyMatch(user -> user.getId().equals(userId)))
    //            return "APPROVER";
    //        return "NONE";
    //
    //    }

}

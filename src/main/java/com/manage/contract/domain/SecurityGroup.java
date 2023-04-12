package com.manage.contract.domain;

import com.manage.contract.service.dto.Privilege;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("security_group")
public class SecurityGroup extends AbstractAuditingEntity {

    @Id
    private String id;

    @NotNull
    private String name;

    private String description;

    private Set<Privilege> kpi;

    private Set<Privilege> report;

    private Set<Privilege> privilege;

    @DBRef
    private Set<User> members;

    private Instant createdDate;

    //    @DBRef
    //    private Set<User> members;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Privilege> getKpi() {
        if (kpi == null) {
            return new HashSet<>();
        }
        return kpi;
    }

    public void setKpi(Set<Privilege> kpi) {
        this.kpi = kpi;
    }

    public Set<Privilege> getReport() {
        if (report == null) {
            return new HashSet<>();
        }
        return report;
    }

    public void setReport(Set<Privilege> report) {
        this.report = report;
    }

    public Set<Privilege> getPrivilege() {
        if (privilege == null) {
            return new HashSet<>();
        }
        return privilege;
    }

    public void setPrivilege(Set<Privilege> privilege) {
        this.privilege = privilege;
    }

    public Set<User> getMembers() {
        return members != null ? members : new HashSet<>();
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public Instant getCreatedDate() {
        return super.getCreatedDate();
    }
}

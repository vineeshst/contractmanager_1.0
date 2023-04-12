package com.manage.contract.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user_groups")
public class UserGroup extends AbstractAuditingEntity implements Serializable {

    @Id
    private String id;

    private String name;

    private String description;

    private Instant createdDate;

    private String createdBy;

    @DBRef
    private List<User> members;

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

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public Instant getCreatedDate() {
        return super.getCreatedDate();
    }

    public String getCreatedBy() {
        return super.getCreatedBy();
    }
}

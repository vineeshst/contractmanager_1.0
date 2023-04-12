package com.manage.contract.domain;

import com.manage.contract.service.dto.Constants;
import com.manage.contract.service.dto.EntityType;
import java.io.Serializable;
import java.util.Date;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

public class Notification extends AbstractAuditingEntity implements Serializable {

    public Notification(String type, String userId, String actionUser, String entityName, String entityId, EntityType entityType) {
        this.type = type;
        this.userId = userId;
        this.actionUser = actionUser;
        this.entityName = entityName;
        this.entityId = entityId;
        this.entityType = entityType;
    }

    public Notification(String type, String userId, String actionUser) {
        this.type = type;
        this.userId = userId;
        this.actionUser = actionUser;
    }

    public Notification() {}

    @Id
    private String id;

    private String type;
    private String message;
    private boolean read = false;

    @CreatedDate
    private Date date;

    private String userId;
    private String actionUser;
    private String entityId;
    private String entityName;
    private EntityType entityType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActionUser() {
        return actionUser;
    }

    public void setActionUser(String actionUser) {
        this.actionUser = actionUser;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityType() {
        return entityType != null ? entityType.name() : "";
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
}

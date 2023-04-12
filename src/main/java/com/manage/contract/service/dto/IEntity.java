package com.manage.contract.service.dto;

import com.manage.contract.domain.EntityStatus;
import com.manage.contract.domain.EventHistory;
import com.manage.contract.domain.User;
import java.util.List;
import java.util.Set;

public interface IEntity extends ITeam {
    Set<User> getApprover();

    String getEntityId();

    String getEntityName();

    String getEntityRole();

    ApprovalStatus getApprovalStatus();

    EntityStatus getEntityStatus();

    List<EventHistory> getHistory();

    boolean isDeleted();
}

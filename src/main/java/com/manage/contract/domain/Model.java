package com.manage.contract.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.annotation.Id;

public abstract class Model implements Serializable {

    @Id
    private String id;

    public String getID() {
        return id;
    }

    protected Set<User> getApprover(Team team, TeamGroups teamGroups) {
        Set<User> approver = null;

        if (team != null && team.getApprover() != null) {
            approver = new HashSet<>(team.getApprover());

            if (teamGroups == null || teamGroups.getApprover() == null) return approver;
        }

        if (teamGroups != null && teamGroups.getApprover() != null) {
            if (approver == null) approver = new HashSet<>();

            for (UserGroup ug : teamGroups.getApprover()) {
                approver.addAll(ug.getMembers());
            }
        }
        return approver;
    }
}

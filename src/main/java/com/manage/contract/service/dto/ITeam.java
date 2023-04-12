package com.manage.contract.service.dto;

import com.manage.contract.domain.Team;
import com.manage.contract.domain.TeamGroups;

public interface ITeam {
    boolean isNotificationEnabled(String entityId);

    Team getTeam();

    TeamGroups getTeamGroups();
}

package com.manage.contract.service.dto;

import java.util.Map;

public class NotificationConfig {

    private Map<String, Boolean> userConfig;

    public Map<String, Boolean> getUserConfig() {
        return userConfig;
    }

    public void setUserConfig(Map<String, Boolean> userConfig) {
        this.userConfig = userConfig;
    }
}

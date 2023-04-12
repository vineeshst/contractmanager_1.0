package com.manage.contract.service.dto;

public class Preference {

    private int recordsPerPage;
    private String gridChoice;
    private String recordsLayout;
    private String dashboardLayout;
    private boolean isSearchPanelVisible;
    private boolean openRecordInSameTab;
    private String language;
    private String timeZone;
    private String dateFormat;
    private String timeFormat;
    private String preferredCurrency;

    public int getRecordsPerPage() {
        return recordsPerPage;
    }

    public void setRecordsPerPage(int recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }

    public String getGridChoice() {
        return gridChoice;
    }

    public void setGridChoice(String gridChoice) {
        this.gridChoice = gridChoice;
    }

    public String getRecordsLayout() {
        return recordsLayout;
    }

    public void setRecordsLayout(String recordsLayout) {
        this.recordsLayout = recordsLayout;
    }

    public String getDashboardLayout() {
        return dashboardLayout;
    }

    public void setDashboardLayout(String dashboardLayout) {
        this.dashboardLayout = dashboardLayout;
    }

    public boolean isSearchPanelVisible() {
        return isSearchPanelVisible;
    }

    public void setSearchPanelVisible(boolean searchPanelVisible) {
        isSearchPanelVisible = searchPanelVisible;
    }

    public boolean isOpenRecordInSameTab() {
        return openRecordInSameTab;
    }

    public void setOpenRecordInSameTab(boolean openRecordInSameTab) {
        this.openRecordInSameTab = openRecordInSameTab;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }
}

package com.manage.contract.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class MasterData extends Model{
    private String keyAttributeName;
    private String keyAttributeValue;
    private String priority;
    private List<Attribute> masterDataEntries;

    public String getKeyAttributeName() {
        return keyAttributeName;
    }

    public void setKeyAttributeName(String keyAttributeName) {
        this.keyAttributeName = keyAttributeName;
    }

    public String getKeyAttributeValue() {
        return keyAttributeValue;
    }

    public void setKeyAttributeValue(String keyAttributeValue) {
        this.keyAttributeValue = keyAttributeValue;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public List<Attribute> getMasterDataEntries() {
        return masterDataEntries;
    }

    public void setMasterDataEntries(List<Attribute> masterDataEntries) {
        this.masterDataEntries = masterDataEntries;
    }
}

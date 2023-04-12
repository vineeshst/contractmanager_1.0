package com.manage.contract.domain;

public class Attribute {

    private String attributeName;
    private String attributeValue;
    private String displayName;

    public Attribute() {}

    public Attribute(String attributeName, String attributeValue, String displayName) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.displayName = displayName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

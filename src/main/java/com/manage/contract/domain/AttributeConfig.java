
package com.manage.contract.domain;


public class AttributeConfig {

    private String attributeName;
    private AttributeOptions attributeOptions;
    private String dataType;
    private String dependsOnAttribute;
    private String displayName;
    private String group;
    private String helpMessage;
    private String pageName;
    private String source;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public AttributeOptions getAttributeOptions() {
        return attributeOptions;
    }

    public void setAttributeOptions(AttributeOptions attributeOptions) {
        this.attributeOptions = attributeOptions;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDependsOnAttribute() {
        return dependsOnAttribute;
    }

    public void setDependsOnAttribute(String dependsOnAttribute) {
        this.dependsOnAttribute = dependsOnAttribute;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}

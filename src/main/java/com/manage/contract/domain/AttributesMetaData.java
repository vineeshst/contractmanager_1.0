package com.manage.contract.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class AttributesMetaData extends Model {

    private List<AttributeConfig> attributeConfigs;

    public List<AttributeConfig> getAttributeConfigs() {
        return attributeConfigs;
    }

    public void setAttributeConfigs(List<AttributeConfig> attributeConfigs) {
        this.attributeConfigs = attributeConfigs;
    }
}

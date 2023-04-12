package com.manage.contract.domain;

import java.util.List;

public class DisplayPreference {

    public List<AttributeConfig> listAttributes;

    public List<AttributeConfig> tileAttributes;

    public List<AttributeConfig> getListAttributes() {
        return listAttributes;
    }

    public void setListAttributes(List<AttributeConfig> listAttributes) {
        this.listAttributes = listAttributes;
    }

    public List<AttributeConfig> getTileAttributes() {
        return tileAttributes;
    }

    public void setTileAttributes(List<AttributeConfig> tileAttributes) {
        this.tileAttributes = tileAttributes;
    }
}

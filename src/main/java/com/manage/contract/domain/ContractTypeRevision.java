package com.manage.contract.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Document
public class ContractTypeRevision{

    @Id
    public String id;
    public int version;
    public Instant updatedOn = Instant.now();
    public List<ContractType> contractTypeRevisions;
    //    @TextIndexed
    private String name;

    public ContractTypeRevision() {
    }

    public ContractTypeRevision(String id, int version, String name) {
        this.id = id;
        this.version = version;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addContractTypeRevisions(ContractType contractType) {
        if(this.contractTypeRevisions == null)
            this.contractTypeRevisions = new ArrayList<>();
        this.contractTypeRevisions.add(contractType);
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
    }
}

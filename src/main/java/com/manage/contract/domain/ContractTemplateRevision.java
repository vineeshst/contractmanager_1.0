package com.manage.contract.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ContractTemplateRevision {

    @Id
    public String id;

    public int version;
    public Instant updatedOn = Instant.now();
    public List<ContractTemplate> contractTemplateRevisions = new ArrayList<>();

    public ContractTemplateRevision() {}

    public ContractTemplateRevision(String id, int version) {
        this.id = id;
        this.version = version;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addContractTemplateRevisions(ContractTemplate contractTemplate) {
        this.contractTemplateRevisions.add(contractTemplate);
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

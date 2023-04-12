package com.manage.contract.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ContractClauseRevision {

    @Id
    public String id;

    public int version;
    public Instant updatedOn = Instant.now();
    public List<ContractClause> contractClauseRevisions;

    public ContractClauseRevision() {}

    public ContractClauseRevision(String id, int version) {
        this.id = id;
        this.version = version;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addContractClauseRevisions(ContractClause contractClause) {
        if (this.contractClauseRevisions == null) this.contractClauseRevisions = new ArrayList<>();
        this.contractClauseRevisions.add(contractClause);
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

package com.manage.contract.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ContractAgreementRevision {

    @Id
    public String id;

    public int version;
    public Instant updatedOn = Instant.now();
    public List<ContractAgreement> contractAgreementRevisions;

    public ContractAgreementRevision() {}

    public ContractAgreementRevision(String id, int version) {
        this.id = id;
        this.version = version;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addContractAgreementRevisions(ContractAgreement contractAgreement) {
        if (this.contractAgreementRevisions == null) this.contractAgreementRevisions = new ArrayList<>();
        this.contractAgreementRevisions.add(contractAgreement);
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

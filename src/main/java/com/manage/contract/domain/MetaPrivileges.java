package com.manage.contract.domain;

import com.manage.contract.service.dto.Privilege;
import java.time.Instant;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("meta_security_privileges")
public class MetaPrivileges extends Model {

    private Privilege[] kpi;
    private Privilege[] report;
    private Privilege[] privilege;

    private List<ContractType> contractTypes;

    private Instant date = Instant.now();

    public Privilege[] getKpi() {
        return kpi;
    }

    public void setKpi(Privilege[] kpi) {
        this.kpi = kpi;
    }

    public Privilege[] getReport() {
        return report;
    }

    public void setReport(Privilege[] report) {
        this.report = report;
    }

    public Privilege[] getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege[] privilege) {
        this.privilege = privilege;
    }

    public List<ContractType> getContractTypes() {
        return contractTypes;
    }

    public void setContractTypes(List<ContractType> contractTypes) {
        this.contractTypes = contractTypes;
    }

    public Instant getDate() {
        return date;
    }
}

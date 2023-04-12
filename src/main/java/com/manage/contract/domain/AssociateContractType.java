package com.manage.contract.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssociateContractType {
    private String contractTypeName;
    @JsonProperty("mandatory")
    private boolean isMandatory;

    @JsonProperty("workFlowRequired")
    private boolean isWorkFlowRequired;

    @JsonProperty("executionPhase")
    private String executionPhase;

    public String getContractTypeName() {
        return contractTypeName;
    }

    public void setContractTypeName(String contractTypeName) {
        this.contractTypeName = contractTypeName;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public boolean isWorkFlowRequired() {
        return isWorkFlowRequired;
    }

    public void setWorkFlowRequired(boolean workFlowRequired) {
        isWorkFlowRequired = workFlowRequired;
    }

    public String getExecutionPhase() {
        return executionPhase;
    }

    public void setExecutionPhase(String executionPhase) {
        this.executionPhase = executionPhase;
    }
}

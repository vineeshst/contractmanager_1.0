package com.manage.contract.service.mapper;

import com.manage.contract.domain.ContractType;
import com.manage.contract.service.dto.ContractTypeForm;
import org.springframework.stereotype.Service;

@Service
public class ContractTypeMapper {

    public ContractType contractTypeFormToContractType(ContractTypeForm contractTypeForm, ContractType contractType) {
        if (contractTypeForm == null) return null;

        contractType.setStatus(contractTypeForm.getStatus());
        contractType.setAttributes(contractTypeForm.getAttributes());
        contractType.setExpandDropdownOnMouseHover(contractTypeForm.isExpandDropdownOnMouseHover());
        contractType.setEnableAutoSupersede(contractTypeForm.isEnableAutoSupersede());
        contractType.setTwoColumnAttributeLayout(contractTypeForm.isTwoColumnAttributeLayout());
        contractType.setEnableCollaboration(contractTypeForm.isEnableCollaboration());
        contractType.setTeam(contractTypeForm.getTeam());
        contractType.setQrCode(contractTypeForm.isQrCode());
        //        contractType.setTemplateNames(contractTypeForm.t);
        contractType.setName(contractTypeForm.getContractTypeName());
        //        contractType.setClauseNames(contractTypeForm.clas);
        contractType.setAssociations(contractTypeForm.getAssociations());
        contractType.setDescription(contractTypeForm.getDescription());
        contractType.setAllowThirdPartyPaper(contractTypeForm.isAllowThirdPartyPaper());
        contractType.setAllowClauseAssembly(contractTypeForm.isAllowClauseAssembly());
        contractType.setAllowCopyWithAssociations(contractTypeForm.isAllowCopyWithAssociations());
        contractType.setDisplayPreference(contractTypeForm.getDisplayPreference());
        contractType.setTeamGroups(contractTypeForm.getTeamGroups());
        contractType.incrementVersion();
        return contractType;
    }
}

package com.manage.contract.service.dto;

import com.manage.contract.domain.ContractAgreement;
import com.manage.contract.domain.Recipient;
import java.util.ArrayList;

public class SendForSignatureForm {

    private String agreementId;
    private ArrayList<Recipient> recipients;
    private String agreementSignMessage;
    private boolean signOrderRequired;

    public SendForSignatureForm() {}

    public SendForSignatureForm(
        String agreementId,
        ArrayList<Recipient> recipients,
        String agreementSignMessage,
        boolean signOrderRequired
    ) {
        this.agreementId = agreementId;
        this.recipients = recipients;
        this.agreementSignMessage = agreementSignMessage;
        this.signOrderRequired = signOrderRequired;
    }

    public String getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(String agreementId) {
        this.agreementId = agreementId;
    }

    public ArrayList<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(ArrayList<Recipient> recipients) {
        this.recipients = recipients;
    }

    public String getAgreementSignMessage() {
        return agreementSignMessage;
    }

    public void setAgreementSignMessage(String agreementSignMessage) {
        this.agreementSignMessage = agreementSignMessage;
    }

    public boolean isSignOrderRequired() {
        return signOrderRequired;
    }

    public void setSignOrderRequired(boolean signOrderRequired) {
        this.signOrderRequired = signOrderRequired;
    }
}

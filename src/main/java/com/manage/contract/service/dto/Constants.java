package com.manage.contract.service.dto;

public class Constants {

    public static class EntityRoles {

        public static final String APPROVER = "APPROVER";
        public static final String OBSERVER = "OBSERVER";
        public static final String EXTERNAL_REVIEWER = "EXTERNAL_REVIEWER";
        public static final String CONTRACT_ADMIN = "CONTRACT_ADMIN";
        public static final String SECONDARY_OWNER = "SECONDARY_OWNER";
        public static final String PRIMARY_OWNER = "PRIMARY_OWNER";
        public static final String EXTERNAL_SIGNATORY = "EXTERNAL_SIGNATORY";
        public static final String INTERNAL_SIGNATORY = "INTERNAL_SIGNATORY";
    }

    public static class NotificationType {

        public static final String SENT_FOR_APPROVAL = "Sent For Approval";
        public static final String APPROVED = "Approved";
        public static final String REJECTED = "Rejected";
        public static final String MODIFIED = "Modified";
        public static final String CREATED = "Created";
        public static final String EXPIRED = "Expired";
        public static final String EXECUTED = "Executed";
        public static final String SENT_FOR_SIGNATURE = "Sent For Signature";
        public static final String SIGNED = "Signed";
    }
}

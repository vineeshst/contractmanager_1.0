package com.manage.contract.util;

import com.manage.contract.domain.*;
import com.manage.contract.repository.*;
import com.manage.contract.service.dto.Privilege;
import com.manage.contract.service.dto.Privileges;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    SecurityGroupRepository securityGroupRepository;

    @Autowired
    MetaUserRoleRepository metaUserRoleRepository;

    @Autowired
    MetaSecurityPrivilegesRepository ms_PrivilegesRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ContractCategoryRepository contractCategoryRepository;

    @Autowired
    ApiServiceRepository apiServiceRepository;

    @Override
    public void run(String... args) throws Exception {
        seedDataBaseOnStartUp();
    }

    private void seedDataBaseOnStartUp() {
        AtomicInteger contractCategoryCount = new AtomicInteger();
        contractCategoryRepository
            .findAll()
            .doOnNext(
                s -> {
                    contractCategoryCount.getAndIncrement();
                }
            )
            .doOnComplete(
                () -> {
                    System.out.println("Number of records in ContractCategory collection " + contractCategoryCount.get());
                    if (contractCategoryCount.get() == 0) {
                        System.out.println("Seeding data to Contract Category");
                        ContractCategory contractCategory = new ContractCategory();
                        contractCategory.setName("Default Category");
                        contractCategory.setName("Default Contract Category");
                        contractCategoryRepository.save(contractCategory).subscribe();
                    }
                }
            )
            .subscribe();

        AtomicInteger apiServiceCount = new AtomicInteger();
        apiServiceRepository
            .findAll()
            .doOnNext(
                s -> {
                    apiServiceCount.getAndIncrement();
                }
            )
            .doOnComplete(
                () -> {
                    System.out.println("Number of records in ApiService collection " + apiServiceCount.get());
                    if (apiServiceCount.get() == 0) {
                        System.out.println("Seeding data to ApiService");
                        ApiService apiService = new ApiService();
                        apiService.setServiceName("EsignApiService");
                        apiService.setAuthorizationEndPoint("https://secure.adobesign.com/public/oauth");
                        apiService.setRefreshTokenEndPoint("https://secure.echosign.com/oauth/refresh");
                        apiService.setAccessTokenEndPoint("https://secure.echosign.com/oauth/token");
                        apiService.setClientId("CBJCHBCAABAA-_ws1WRb5PBCxJ5XaJPq990ef3g1S4nB");
                        apiService.setClientSecret("fkOoovWOr2jnOsF5Hq2Y7tFNDrnCvLqC");
                        apiService.setRedirectUri("https://tolocalhost.com");
                        apiService.setTokenScope("user_login:self+agreement_send:account");
                        apiService.setImplicitAuthorization(false);
                        apiService.setRefreshToken("3AAABLblqZhBqT8x2_tCccsK1HKlvyOj92L_JH_TV_5I4K6Glm-QAY8fHbsSGkFttFn2FTQIyOIw*");
                        apiServiceRepository.save(apiService).subscribe();
                    }
                }
            )
            .subscribe();

        AtomicInteger metaSPCount = new AtomicInteger();
        ms_PrivilegesRepository
            .findAll()
            .doOnNext(
                s -> {
                    metaSPCount.getAndIncrement();
                }
            )
            .doOnComplete(
                () -> {
                    System.out.println("Number of records in meta_security_privileges collection " + metaSPCount.get());
                    if (metaSPCount.get() == 0) {
                        System.out.println("Seeding data to meta_security_privileges");
                        MetaPrivileges metaPrivileges = new MetaPrivileges();
                        Privilege[] kpiPrivileges = new Privilege[18];
                        kpiPrivileges[0] = new Privilege();
                        kpiPrivileges[0].setName("Agreement Deviation");
                        kpiPrivileges[0].setPrivilege(Privileges.NONE);
                        kpiPrivileges[1] = new Privilege();
                        kpiPrivileges[1].setName("Agreements Pending Approval");
                        kpiPrivileges[1].setPrivilege(Privileges.NONE);
                        kpiPrivileges[2] = new Privilege();
                        kpiPrivileges[2].setName("Agreements Pending Execution");
                        kpiPrivileges[2].setPrivilege(Privileges.NONE);
                        kpiPrivileges[3] = new Privilege();
                        kpiPrivileges[3].setName("Agreements Pending External Signatures");
                        kpiPrivileges[3].setPrivilege(Privileges.NONE);
                        kpiPrivileges[4] = new Privilege();
                        kpiPrivileges[4].setName("Agreements Pending Internal Signatures");
                        kpiPrivileges[4].setPrivilege(Privileges.NONE);
                        kpiPrivileges[5] = new Privilege();
                        kpiPrivileges[5].setName("Agreements with Contract");
                        kpiPrivileges[5].setPrivilege(Privileges.NONE);
                        kpiPrivileges[6] = new Privilege();
                        kpiPrivileges[6].setName("Approved Requests Waiting For Contract Creation");
                        kpiPrivileges[6].setPrivilege(Privileges.NONE);
                        kpiPrivileges[7] = new Privilege();
                        kpiPrivileges[7].setName("Agreements Pending Approval");
                        kpiPrivileges[7].setPrivilege(Privileges.NONE);
                        kpiPrivileges[8] = new Privilege();
                        kpiPrivileges[8].setName("Agreements Pending Approval");
                        kpiPrivileges[8].setPrivilege(Privileges.NONE);
                        kpiPrivileges[9] = new Privilege();
                        kpiPrivileges[9].setName("Agreement Deviation");
                        kpiPrivileges[9].setPrivilege(Privileges.NONE);
                        kpiPrivileges[10] = new Privilege();
                        kpiPrivileges[10].setName("Agreements Pending Approval");
                        kpiPrivileges[10].setPrivilege(Privileges.NONE);
                        kpiPrivileges[11] = new Privilege();
                        kpiPrivileges[11].setName("Agreements Pending Execution");
                        kpiPrivileges[11].setPrivilege(Privileges.NONE);
                        kpiPrivileges[12] = new Privilege();
                        kpiPrivileges[12].setName("Agreements Pending External Signatures");
                        kpiPrivileges[12].setPrivilege(Privileges.NONE);
                        kpiPrivileges[13] = new Privilege();
                        kpiPrivileges[13].setName("Agreements Pending Internal Signatures");
                        kpiPrivileges[13].setPrivilege(Privileges.NONE);
                        kpiPrivileges[14] = new Privilege();
                        kpiPrivileges[14].setName("Agreements with Contract");
                        kpiPrivileges[14].setPrivilege(Privileges.NONE);
                        kpiPrivileges[15] = new Privilege();
                        kpiPrivileges[15].setName("Approved Requests Waiting For Contract Creation");
                        kpiPrivileges[15].setPrivilege(Privileges.NONE);
                        kpiPrivileges[16] = new Privilege();
                        kpiPrivileges[16].setName("Agreements Pending Approval");
                        kpiPrivileges[16].setPrivilege(Privileges.NONE);
                        kpiPrivileges[17] = new Privilege();
                        kpiPrivileges[17].setName("Agreements Pending Approval");
                        kpiPrivileges[17].setPrivilege(Privileges.NONE);

                        metaPrivileges.setKpi(kpiPrivileges);

                        Privilege[] reportPrivileges = new Privilege[9];
                        reportPrivileges[0] = new Privilege();
                        reportPrivileges[0].setName("CLM Activity Report");
                        reportPrivileges[0].setPrivilege(Privileges.NONE);
                        reportPrivileges[1] = new Privilege();
                        reportPrivileges[1].setName("Cycle Time Report");
                        reportPrivileges[1].setPrivilege(Privileges.NONE);
                        reportPrivileges[2] = new Privilege();
                        reportPrivileges[2].setName("Expired Agreement Report");
                        reportPrivileges[2].setPrivilege(Privileges.NONE);
                        reportPrivileges[3] = new Privilege();
                        reportPrivileges[3].setName("Expiring Agreement");
                        reportPrivileges[3].setPrivilege(Privileges.NONE);
                        reportPrivileges[4] = new Privilege();
                        reportPrivileges[4].setName("SIgnature Type report");
                        reportPrivileges[4].setPrivilege(Privileges.NONE);
                        reportPrivileges[5] = new Privilege();
                        reportPrivileges[5].setName("Tagged Attributes Report");
                        reportPrivileges[5].setPrivilege(Privileges.NONE);
                        reportPrivileges[6] = new Privilege();
                        reportPrivileges[6].setName("User Login Report");
                        reportPrivileges[6].setPrivilege(Privileges.NONE);
                        reportPrivileges[7] = new Privilege();
                        reportPrivileges[7].setName("CLM Activity Report");
                        reportPrivileges[7].setPrivilege(Privileges.NONE);
                        reportPrivileges[8] = new Privilege();
                        reportPrivileges[8].setName("CLM Activity Report");
                        reportPrivileges[8].setPrivilege(Privileges.NONE);
                        metaPrivileges.setReport(reportPrivileges);

                        Privilege[] privileges = new Privilege[11];
                        privileges[0] = new Privilege();
                        privileges[0].setName("Add/Remove Team");
                        privileges[0].setPrivilege(Privileges.NONE);
                        privileges[1] = new Privilege();
                        privileges[1].setName("Agreement");
                        privileges[1].setPrivilege(Privileges.NONE);
                        privileges[2] = new Privilege();
                        privileges[2].setName("Associated Document");
                        privileges[2].setPrivilege(Privileges.NONE);
                        privileges[3] = new Privilege();
                        privileges[3].setName("Clause");
                        privileges[3].setPrivilege(Privileges.NONE);
                        privileges[4] = new Privilege();
                        privileges[4].setName("Contract Type");
                        privileges[4].setPrivilege(Privileges.NONE);
                        privileges[5] = new Privilege();
                        privileges[5].setName("Enable/Disable Team");
                        privileges[5].setPrivilege(Privileges.NONE);
                        privileges[6] = new Privilege();
                        privileges[6].setName("Attributes");
                        privileges[6].setPrivilege(Privileges.NONE);
                        privileges[7] = new Privilege();
                        privileges[7].setName("Masterdata");
                        privileges[7].setPrivilege(Privileges.NONE);
                        privileges[8] = new Privilege();
                        privileges[8].setName("Rules");
                        privileges[8].setPrivilege(Privileges.NONE);
                        privileges[9] = new Privilege();
                        privileges[9].setName("Templates");
                        privileges[9].setPrivilege(Privileges.NONE);
                        privileges[10] = new Privilege();
                        privileges[10].setName("Admin Task");
                        privileges[10].setPrivilege(Privileges.NONE);
                        metaPrivileges.setPrivilege(privileges);
                        ms_PrivilegesRepository.save(metaPrivileges).subscribe();
                    }
                }
            )
            .subscribe();

        AtomicInteger metaUserRoleCount = new AtomicInteger();
        metaUserRoleRepository
            .findAll()
            .doOnNext(
                s -> {
                    metaUserRoleCount.getAndIncrement();
                }
            )
            .doOnComplete(
                () -> {
                    System.out.println("Number of records in meta_user_role collection " + metaUserRoleCount.get());
                    if (metaUserRoleCount.get() == 0) {
                        System.out.println("Seeding data to meta_user_role");
                        UserRole userRole1 = new UserRole();
                        userRole1.setId("PRIMARY_OWNER");
                        userRole1.setName("Primary Owner");
                        userRole1.setDisplayName("Primary Owner 1");
                        metaUserRoleRepository.save(userRole1).subscribe();
                        UserRole userRole2 = new UserRole();
                        userRole2.setId("SECONDARY_OWNER");
                        userRole2.setName("Secondary Owner");
                        userRole2.setDisplayName("Secondary Owner");
                        metaUserRoleRepository.save(userRole2).subscribe();
                        UserRole userRole3 = new UserRole();
                        userRole3.setId("CONTRIBUTOR");
                        userRole3.setName("Contributor");
                        userRole3.setDisplayName("Contributor");
                        metaUserRoleRepository.save(userRole3).subscribe();
                        UserRole userRole4 = new UserRole();
                        userRole4.setId("APPROVER");
                        userRole4.setName("Approver");
                        userRole4.setDisplayName("Approver");
                        metaUserRoleRepository.save(userRole4).subscribe();
                        UserRole userRole5 = new UserRole();
                        userRole5.setId("DEVIATION_APPROVER");
                        userRole5.setName("Deviation Approver");
                        userRole5.setDisplayName("Deviation Approver");
                        metaUserRoleRepository.save(userRole5).subscribe();
                        UserRole userRole6 = new UserRole();
                        userRole6.setId("OBSERVER");
                        userRole6.setName("Observer");
                        userRole6.setDisplayName("Observer");
                        metaUserRoleRepository.save(userRole6).subscribe();
                        UserRole userRole7 = new UserRole();
                        userRole7.setId("REVIEWER");
                        userRole7.setName("Reviewer");
                        userRole7.setDisplayName("Reviewer");
                        metaUserRoleRepository.save(userRole7).subscribe();
                        UserRole userRole8 = new UserRole();
                        userRole8.setId("EXTERNAL_REVIEWER");
                        userRole8.setName("External Reviewer");
                        userRole8.setDisplayName("External Reviewer");
                        metaUserRoleRepository.save(userRole8).subscribe();
                        UserRole userRole9 = new UserRole();
                        userRole9.setId("INTERNAL_SIGNATORY");
                        userRole9.setName("Internal Signatory");
                        userRole9.setDisplayName("Internal Signatory");
                        metaUserRoleRepository.save(userRole9).subscribe();
                        UserRole userRole10 = new UserRole();
                        userRole10.setId("CONTRACT_ADMIN");
                        userRole10.setName("Contract Admin");
                        userRole10.setDisplayName("Contract Admin");
                        metaUserRoleRepository.save(userRole10).subscribe();
                        UserRole userRole11 = new UserRole();
                        userRole11.setId("EXTERNAL_SIGNATORY");
                        userRole11.setName("External Signatory");
                        userRole11.setDisplayName("External Signatory");
                        metaUserRoleRepository.save(userRole11).subscribe();
                    }
                }
            )
            .subscribe();

        Mono<User> userMono = userRepository.findOneByLogin("admin");
        Mono
            .from(userMono)
            .flatMap(
                user -> {
                    AtomicInteger adminSG_Count = new AtomicInteger();
                    securityGroupRepository
                        .findAllByUserId(user.getId())
                        .doOnNext(
                            s -> {
                                adminSG_Count.getAndIncrement();
                            }
                        )
                        .doOnComplete(
                            () -> {
                                System.out.println("Number of SG assigned to admin :" + adminSG_Count.get());
                                if (adminSG_Count.get() == 0) {
                                    SecurityGroup securityGroup = new SecurityGroup();
                                    securityGroup.setName("Test Security Group - 1");
                                    securityGroup.setDescription("Test Security Group - 1");

                                    Set<Privilege> kpiPrivileges = new HashSet<>();
                                    Privilege kpi1 = new Privilege();
                                    kpi1.setName("Agreements Pending Approval");
                                    kpi1.setPrivilege(Privileges.NONE);
                                    Privilege kpi2 = new Privilege();
                                    kpi2.setName("Agreements Pending Approval");
                                    kpi2.setPrivilege(Privileges.NONE);
                                    Privilege kpi3 = new Privilege();
                                    kpi3.setName("Agreements Pending External Signatures");
                                    kpi3.setPrivilege(Privileges.NONE);
                                    Privilege kpi4 = new Privilege();
                                    kpi4.setName("Agreements with Contract");
                                    kpi4.setPrivilege(Privileges.NONE);
                                    Privilege kpi5 = new Privilege();
                                    kpi5.setName("Approved Requests Waiting For Contract Creation");
                                    kpi5.setPrivilege(Privileges.NONE);
                                    Privilege kpi6 = new Privilege();
                                    kpi6.setName("Approved Requests Waiting For Contract Creation");
                                    kpi6.setPrivilege(Privileges.NONE);
                                    Privilege kpi7 = new Privilege();
                                    kpi7.setName("Agreements Pending Approval");
                                    kpi7.setPrivilege(Privileges.NONE);
                                    Privilege kpi8 = new Privilege();
                                    kpi8.setName("Agreements Pending Approval");
                                    kpi8.setPrivilege(Privileges.NONE);
                                    Privilege kpi9 = new Privilege();
                                    kpi9.setName("Agreements with Contract");
                                    kpi9.setPrivilege(Privileges.NONE);
                                    Privilege kpi10 = new Privilege();
                                    kpi10.setName("Agreements Pending Approval");
                                    kpi10.setPrivilege(Privileges.NONE);
                                    Privilege kpi11 = new Privilege();
                                    kpi11.setName("Agreements Pending Internal Signatures");
                                    kpi11.setPrivilege(Privileges.NONE);
                                    Privilege kpi12 = new Privilege();
                                    kpi12.setName("Agreement Deviation");
                                    kpi12.setPrivilege(Privileges.NONE);
                                    Privilege kpi13 = new Privilege();
                                    kpi13.setName("Agreement Deviation");
                                    kpi13.setPrivilege(Privileges.NONE);
                                    Privilege kpi14 = new Privilege();
                                    kpi14.setName("Agreements Pending Execution");
                                    kpi14.setPrivilege(Privileges.NONE);
                                    Privilege kpi15 = new Privilege();
                                    kpi15.setName("Agreements Pending Approval");
                                    kpi15.setPrivilege(Privileges.NONE);
                                    Privilege kpi16 = new Privilege();
                                    kpi16.setName("Agreements Pending External Signatures");
                                    kpi16.setPrivilege(Privileges.NONE);
                                    Privilege kpi17 = new Privilege();
                                    kpi17.setName("Agreements Pending Internal Signatures");
                                    kpi17.setPrivilege(Privileges.NONE);
                                    Privilege kpi18 = new Privilege();
                                    kpi18.setName("Agreements Pending Execution");
                                    kpi18.setPrivilege(Privileges.NONE);

                                    kpiPrivileges.add(kpi1);
                                    kpiPrivileges.add(kpi2);
                                    kpiPrivileges.add(kpi3);
                                    kpiPrivileges.add(kpi4);
                                    kpiPrivileges.add(kpi5);
                                    kpiPrivileges.add(kpi6);
                                    kpiPrivileges.add(kpi7);
                                    kpiPrivileges.add(kpi8);
                                    kpiPrivileges.add(kpi9);
                                    kpiPrivileges.add(kpi10);
                                    kpiPrivileges.add(kpi11);
                                    kpiPrivileges.add(kpi12);
                                    kpiPrivileges.add(kpi13);
                                    kpiPrivileges.add(kpi14);
                                    kpiPrivileges.add(kpi15);
                                    kpiPrivileges.add(kpi16);
                                    kpiPrivileges.add(kpi17);
                                    kpiPrivileges.add(kpi18);

                                    securityGroup.setKpi(kpiPrivileges);

                                    Set<Privilege> reportPrivileges = new HashSet<>();
                                    Privilege report1 = new Privilege();
                                    report1.setName("CLM Activity Report");
                                    report1.setPrivilege(Privileges.NONE);
                                    Privilege report2 = new Privilege();
                                    report2.setName("Expired Agreement Report");
                                    report2.setPrivilege(Privileges.NONE);
                                    Privilege report3 = new Privilege();
                                    report3.setName("Expiring Agreement");
                                    report3.setPrivilege(Privileges.NONE);
                                    Privilege report4 = new Privilege();
                                    report4.setName("CLM Activity Report");
                                    report4.setPrivilege(Privileges.NONE);
                                    Privilege report5 = new Privilege();
                                    report5.setName("Cycle Time Report");
                                    report5.setPrivilege(Privileges.NONE);
                                    Privilege report6 = new Privilege();
                                    report6.setName("User Login Report");
                                    report6.setPrivilege(Privileges.NONE);
                                    Privilege report7 = new Privilege();
                                    report7.setName("SIgnature Type report");
                                    report7.setPrivilege(Privileges.NONE);
                                    Privilege report8 = new Privilege();
                                    report8.setName("Tagged Attributes Report");
                                    report8.setPrivilege(Privileges.NONE);
                                    Privilege report9 = new Privilege();
                                    report9.setName("CLM Activity Report");
                                    report9.setPrivilege(Privileges.NONE);

                                    reportPrivileges.add(report1);
                                    reportPrivileges.add(report2);
                                    reportPrivileges.add(report3);
                                    reportPrivileges.add(report4);
                                    reportPrivileges.add(report5);
                                    reportPrivileges.add(report6);
                                    reportPrivileges.add(report7);
                                    reportPrivileges.add(report8);
                                    reportPrivileges.add(report9);

                                    securityGroup.setReport(reportPrivileges);

                                    Set<Privilege> privileges = new HashSet<>();

                                    Privilege privilege1 = new Privilege();
                                    privilege1.setName("Clause");
                                    privilege1.setPrivilege(Privileges.MANAGE);
                                    Privilege privilege2 = new Privilege();
                                    privilege2.setName("Add/Remove Team");
                                    privilege2.setPrivilege(Privileges.MANAGE);
                                    Privilege privilege3 = new Privilege();
                                    privilege3.setName("Contract Type");
                                    privilege3.setPrivilege(Privileges.MANAGE);
                                    Privilege privilege4 = new Privilege();
                                    privilege4.setName("Contract Type 90106");
                                    privilege4.setPrivilege(Privileges.NONE);
                                    Privilege privilege5 = new Privilege();
                                    privilege5.setName("Contract Type 81217");
                                    privilege5.setPrivilege(Privileges.NONE);
                                    Privilege privilege6 = new Privilege();
                                    privilege6.setName("sdfds");
                                    privilege6.setPrivilege(Privileges.NONE);
                                    privilege6.setName("Offer Letter Contract Type");
                                    privilege6.setPrivilege(Privileges.MANAGE);

                                    Privilege privilege7 = new Privilege();
                                    privilege7.setName("Contract Type 90105");
                                    privilege7.setPrivilege(Privileges.NONE);
                                    Privilege privilege8 = new Privilege();
                                    privilege8.setName("Agreement");
                                    privilege8.setPrivilege(Privileges.MANAGE);
                                    Privilege privilege9 = new Privilege();
                                    privilege9.setName("Contract Type 83001");
                                    privilege9.setPrivilege(Privileges.NONE);
                                    Privilege privilege10 = new Privilege();
                                    privilege10.setName("Contract Type 101401");
                                    privilege10.setPrivilege(Privileges.NONE);
                                    Privilege privilege11 = new Privilege();
                                    privilege11.setName("tra");
                                    privilege11.setPrivilege(Privileges.NONE);
                                    Privilege privilege12 = new Privilege();
                                    privilege12.setName("Contract Type 81218");
                                    privilege12.setPrivilege(Privileges.NONE);

                                    Privilege privilege13 = new Privilege();
                                    privilege13.setName("Contract Type 90104");
                                    privilege13.setPrivilege(Privileges.NONE);
                                    Privilege privilege14 = new Privilege();
                                    privilege14.setName("Enable/Disable Team");
                                    privilege14.setPrivilege(Privileges.MANAGE);
                                    Privilege privilege15 = new Privilege();
                                    privilege15.setName("Contract Type 90107");
                                    privilege15.setPrivilege(Privileges.NONE);
                                    Privilege privilege16 = new Privilege();
                                    privilege16.setName("Contract Type 1123");
                                    privilege16.setPrivilege(Privileges.NONE);
                                    Privilege privilege17 = new Privilege();
                                    privilege17.setName("Templates");
                                    privilege17.setPrivilege(Privileges.MANAGE);
                                    Privilege privilege18 = new Privilege();
                                    privilege18.setName("Contract Type 90101");
                                    privilege18.setPrivilege(Privileges.NONE);

                                    Privilege privilege19 = new Privilege();
                                    privilege19.setName("Contract Type 83003");
                                    privilege19.setPrivilege(Privileges.NONE);
                                    Privilege privilege20 = new Privilege();
                                    privilege20.setName("Contract Type 81216");
                                    privilege20.setPrivilege(Privileges.NONE);
                                    Privilege privilege21 = new Privilege();
                                    privilege21.setName("Contract Type 091001");
                                    privilege21.setPrivilege(Privileges.NONE);
                                    Privilege privilege22 = new Privilege();
                                    privilege22.setName("Contract Type 81218");
                                    privilege22.setPrivilege(Privileges.NONE);
                                    Privilege privilege23 = new Privilege();
                                    privilege23.setName("Rules");
                                    privilege23.setPrivilege(Privileges.MANAGE);
                                    Privilege privilege24 = new Privilege();
                                    privilege24.setName("Contract Type  83004");
                                    privilege24.setPrivilege(Privileges.NONE);

                                    Privilege privilege25 = new Privilege();
                                    privilege25.setName("Contract Type 8215");
                                    privilege25.setPrivilege(Privileges.NONE);
                                    Privilege privilege26 = new Privilege();
                                    privilege26.setName("Contract Type 100401");
                                    privilege26.setPrivilege(Privileges.NONE);
                                    Privilege privilege27 = new Privilege();
                                    privilege27.setName("Contract Type 90102");
                                    privilege27.setPrivilege(Privileges.NONE);
                                    Privilege privilege28 = new Privilege();
                                    privilege28.setName("Contract Type 091002");
                                    privilege28.setPrivilege(Privileges.NONE);
                                    Privilege privilege29 = new Privilege();
                                    privilege29.setName("Associated Document");
                                    privilege29.setPrivilege(Privileges.MANAGE);
                                    Privilege privilege30 = new Privilege();
                                    privilege30.setName("Admin Task");
                                    privilege30.setPrivilege(Privileges.MANAGE);

                                    Privilege privilege31 = new Privilege();
                                    privilege31.setName("Contract Type 83002");
                                    privilege31.setPrivilege(Privileges.NONE);
                                    Privilege privilege32 = new Privilege();
                                    privilege32.setName("Contract Type 90103");
                                    privilege32.setPrivilege(Privileges.NONE);
                                    Privilege privilege33 = new Privilege();
                                    privilege33.setName("Masterdata");
                                    privilege33.setPrivilege(Privileges.MANAGE);
                                    Privilege privilege34 = new Privilege();
                                    privilege34.setName("asda");
                                    privilege34.setPrivilege(Privileges.NONE);
                                    Privilege privilege35 = new Privilege();
                                    privilege35.setName("Attributes");
                                    privilege35.setPrivilege(Privileges.MANAGE);
                                    Privilege privilege36 = new Privilege();
                                    privilege36.setName("asd");
                                    privilege36.setPrivilege(Privileges.NONE);

                                    privileges.add(privilege1);
                                    privileges.add(privilege2);
                                    privileges.add(privilege3);
                                    privileges.add(privilege4);
                                    privileges.add(privilege5);
                                    privileges.add(privilege6);
                                    privileges.add(privilege7);
                                    privileges.add(privilege8);
                                    privileges.add(privilege9);
                                    privileges.add(privilege10);
                                    privileges.add(privilege11);
                                    privileges.add(privilege12);
                                    privileges.add(privilege13);
                                    privileges.add(privilege14);
                                    privileges.add(privilege15);
                                    privileges.add(privilege16);
                                    privileges.add(privilege17);
                                    privileges.add(privilege18);
                                    privileges.add(privilege19);
                                    privileges.add(privilege20);
                                    privileges.add(privilege21);
                                    privileges.add(privilege22);
                                    privileges.add(privilege23);
                                    privileges.add(privilege24);
                                    privileges.add(privilege25);
                                    privileges.add(privilege26);
                                    privileges.add(privilege27);
                                    privileges.add(privilege28);
                                    privileges.add(privilege29);
                                    privileges.add(privilege30);
                                    privileges.add(privilege31);
                                    privileges.add(privilege32);
                                    privileges.add(privilege33);
                                    privileges.add(privilege34);
                                    privileges.add(privilege35);
                                    privileges.add(privilege36);

                                    securityGroup.setPrivilege(privileges);

                                    Set<User> members = new HashSet<>();
                                    members.add(user);
                                    securityGroup.setMembers(members);
                                    System.out.println("Assign SG to admin ....");
                                    securityGroupRepository.save(securityGroup).subscribe();
                                }
                            }
                        )
                        .subscribe();

                    //            securityGroupRepository.findAll().doOnNext(s -> {
                    //                adminSG_Count.getAndIncrement();}).doOnComplete(()->{
                    //
                    //            }).subscribe();
                    return Mono.just(user);
                }
            )
            .subscribe();
    }
}

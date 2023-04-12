package com.manage.contract.controller;

import com.adobe.platform.operation.ExecutionContext;
import com.adobe.platform.operation.io.FileRef;
import com.adobe.platform.operation.pdfops.DocumentMergeOperation;
import com.adobe.platform.operation.pdfops.options.documentmerge.DocumentMergeOptions;
import com.adobe.platform.operation.pdfops.options.documentmerge.OutputFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import com.manage.contract.adapers.NextCloudHandler;
import com.manage.contract.domain.*;
import com.manage.contract.repository.UserTasksRepository;
import com.manage.contract.security.SecurityUtils;
import com.manage.contract.service.ContractService;
import com.manage.contract.service.MailService;
import com.manage.contract.service.dto.*;
import com.manage.contract.util.*;
import io.swagger.client.api.AgreementsApi;
import io.swagger.client.api.BaseUrisApi;
import io.swagger.client.api.TransientDocumentsApi;
import io.swagger.client.model.ApiClient;
import io.swagger.client.model.ApiException;
import io.swagger.client.model.agreements.AgreementCreationInfo;
import io.swagger.client.model.agreements.AgreementCreationResponse;
import io.swagger.client.model.agreements.ParticipantSetInfo;
import io.swagger.client.model.agreements.ParticipantSetMemberInfo;
import io.swagger.client.model.baseUris.BaseUriInfo;
import io.swagger.client.model.transientDocuments.TransientDocumentResponse;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.Instant;
import java.util.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.imageio.ImageIO;
import org.aarboard.nextcloud.api.ServerConfig;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping(value = "/api/contracts")
public class ContractManagerController {

    private final Logger log = LoggerFactory.getLogger(ContractManagerController.class);

    private ContractService contractService;

    private MailService mailService;

    private UserTaskUtil userTaskUtil;

    private NotificationsUtil notificationsUtil;

    private final UserTasksRepository userTasksRepository;

    @Value("${application.storage.binary-file.current-version}")
    private String fileStorageCurrent;

    @Value("${application.storage.binary-file.archive}")
    private String fileStorageArchive;

    public ContractManagerController(
        ContractService contractService,
        MailService mailService,
        UserTasksRepository userTasksRepository,
        UserTaskUtil userTaskUtil,
        NotificationsUtil notificationsUtil
    ) {
        this.contractService = contractService;
        this.mailService = mailService;
        this.userTasksRepository = userTasksRepository;
        this.userTaskUtil = userTaskUtil;
        this.notificationsUtil = notificationsUtil;
    }

    ServerConfig serverConfig = new ServerConfig("nextcloud.vineeshvanand.com", true, 443, "admin", "admin");
    //ServerConfig serverConfig = new ServerConfig("127.0.0.1", true, 9090, "admin", "admin");

    private NextCloudHandler nextCloudHandler = new NextCloudHandler(serverConfig);
    private static final String NEXTCLOUD_BASE_URL = "https://nextcloud.vineeshvanand.com//remote.php/webdav";
    private static final String OAUTH_ACCESS_TOKEN_ENDPOINT = "/token";
    private static final String OAUTH_REFRESH_TOKEN_ENDPOINT = "/refresh";
    private static final String BEARER = "Bearer ";
    private static final String EMBEDDED = "embedded";
    private static final String EXTERNAL = "external";

    private static final String eSignBaseUrl = "https://api.echosign.com/";
    private static final String eSignEndpointUrl = "/api/rest/v6";
    private static final String TEMP_FILES = "./src/main/webapp/content";

    @PostMapping("/templates")
    public Mono<ResponseEntity<ResponseMessage>> addTemplate(@RequestBody ContractTemplate contractTemplate, WebSession session) {
        try {
            String srcFilePath = TEMP_FILES + "/" + session.getAttribute("uploadedTemplate") + ".docx";
            if (this.fileStorageCurrent.equals(EXTERNAL)) {
                String destFolderPath =
                    "/Documents/ContractManager/Templates/" +
                    (contractTemplate.getContractType().getName()).replaceAll(" ", "_").toLowerCase();
                String destFilePath =
                    "Documents/ContractManager/Templates/" +
                    (contractTemplate.getContractType().getName() + "/" + contractTemplate.getTemplateName()).replaceAll(" ", "_")
                        .toLowerCase() +
                    ".docx";
                if (!nextCloudHandler.pathExists(destFolderPath)) nextCloudHandler.createFolder(destFolderPath);
                saveFileToExternal(srcFilePath, destFilePath).subscribe();
                contractTemplate.setFilePath(destFilePath);
            } else if (this.fileStorageCurrent.equals(EMBEDDED)) {
                //                Path path = Paths.get("./src/main/webapp/content/ContractTemplate.docx");
                //                byte[] data = Files.readAllBytes(path);
                contractTemplate.setTemplateFile(new Binary(BsonBinarySubType.BINARY, Files.readAllBytes(Paths.get(srcFilePath))));
            }
            contractTemplate.setCode(new RandomString(12).nextString());
            contractTemplate.setCreatedBy(contractTemplate.getTeam().getPrimaryOwner().getFirstName());
            contractTemplate.setStatus(EntityStatus.DRAFT);
            contractTemplate.setUpdatedOn(Instant.now());
            return logContractTemplateHistory(contractTemplate, "Contract Template Created")
                .then(
                    contractService
                        .addTemplate(contractTemplate)
                        .flatMap(
                            addTemplateResult -> {
                                if (addTemplateResult) {
                                    return Mono.just(
                                        new ResponseEntity<>(
                                            new ResponseMessage(
                                                "Added contract template successfully: " + contractTemplate.getTemplateName()
                                            ),
                                            HttpStatus.OK
                                        )
                                    );
                                } else return Mono.just(
                                    new ResponseEntity<>(
                                        new ResponseMessage("Could not add the template file: " + contractTemplate.getTemplateName() + "!"),
                                        HttpStatus.EXPECTATION_FAILED
                                    )
                                );
                            }
                        )
                        .onErrorReturn(
                            new ResponseEntity<>(
                                new ResponseMessage("Could not add the template file: " + contractTemplate.getTemplateName() + "!"),
                                HttpStatus.EXPECTATION_FAILED
                            )
                        )
                );
        } catch (Exception e) {
            return Mono.just(
                new ResponseEntity<>(
                    new ResponseMessage("Could not add the template file: " + contractTemplate.getTemplateName() + "!"),
                    HttpStatus.EXPECTATION_FAILED
                )
            );
        }
    }

    @PutMapping("/templates")
    public Mono<ResponseEntity<ResponseMessage>> updateTemplate(
        @RequestBody ContractTemplateForm contractTemplateForm,
        WebSession session
    ) {
        try {
            String srcFilePath = TEMP_FILES + "/" + session.getAttribute("uploadedTemplate") + ".docx";
            if (contractTemplateForm.getTemplateUploaded()) {
                if (this.fileStorageCurrent.equals(EXTERNAL)) {
                    //                    String destFilePath = "Documents/ContractManager/Templates/" + (contractTemplateForm.getContractType().getName() + "/" + contractTemplateForm.getTemplateName()).replaceAll(" ", "_").toLowerCase() + ".docx";
                    saveFileToExternal(srcFilePath, contractTemplateForm.getFilePath()).subscribe();
                } else if (this.fileStorageCurrent.equals(EMBEDDED)) {
                    contractTemplateForm.setTemplateFile(new Binary(BsonBinarySubType.BINARY, Files.readAllBytes(Paths.get(srcFilePath))));
                }
            }
            contractTemplateForm.setStatus(EntityStatus.DRAFT);
            return addVersionAndUpdateTemplate(contractTemplateForm)
                .flatMap(
                    updateTemplateResult -> {
                        if (updateTemplateResult) return Mono.just(
                            new ResponseEntity<>(new ResponseMessage("Contract Template saved successfully"), HttpStatus.OK)
                        ); else return Mono.just(
                            new ResponseEntity<>(new ResponseMessage("Could not save Contract Template"), HttpStatus.EXPECTATION_FAILED)
                        );
                    }
                )
                .onErrorReturn(
                    new ResponseEntity<>(new ResponseMessage("Could not save Contract Template"), HttpStatus.EXPECTATION_FAILED)
                );
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not save Contract Template"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @PutMapping("/templates/{templateId}")
    public Mono<ResponseEntity<ResponseMessage>> updateTemplateStatus(
        @PathVariable("templateId") String templateId,
        @RequestBody TemplateStatusDTO status
    ) {
        try {
            Mono<ContractTemplate> contractTemplateMono = contractService.getTemplateById(templateId);
            return Mono
                .from(contractTemplateMono)
                .flatMap(
                    contractTemplate -> {
                        String historyMessage =
                            "Template Status Changes from " + contractTemplate.getStatus() + " to " + status.getStatus();
                        contractTemplate.setStatus(status.getStatus());
                        return logContractTemplateHistory(contractTemplate, historyMessage)
                            .flatMap(
                                contractTemplate1 ->
                                    contractService
                                        .updateTemplate(contractTemplate1)
                                        .flatMap(
                                            updatedTemplate ->
                                                Mono.just(
                                                    new ResponseEntity<>(
                                                        new ResponseMessage("Contract Template Status updated successfully"),
                                                        HttpStatus.OK
                                                    )
                                                )
                                        )
                                        .onErrorReturn(
                                            new ResponseEntity<>(
                                                new ResponseMessage("Could not update Contract Template Status"),
                                                HttpStatus.EXPECTATION_FAILED
                                            )
                                        )
                            );
                    }
                );
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not update Template Status"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @PutMapping("/templates/publish")
    public Mono<ResponseEntity<ResponseMessage>> publishTemplate(@RequestBody ContractTemplate contractTemplate) {
        try {
            Mono<ContractTemplate> contractTemplateMono = contractService.getTemplateById(contractTemplate.getID());
            return Mono
                .from(contractTemplateMono)
                .flatMap(
                    contractTemplate1 ->
                        Mono.from(
                            handleContractTemplateWorkflow(contractTemplate1, EntityStatus.APPROVAL_PENDING)
                                .flatMap(
                                    typeStatus -> {
                                        contractTemplate1.setStatus(EntityStatus.APPROVAL_PENDING);
                                        return contractService
                                            .updateEntity(contractTemplate1)
                                            .thenReturn(new ResponseEntity<>(typeStatus, HttpStatus.OK));
                                    }
                                )
                        )
                )
                .thenReturn(new ResponseEntity<>(new ResponseMessage("Successfully updated the status."), HttpStatus.OK));
            //            Mono<ContractTemplate> contractTemplateMono = contractService.getTemplateById(contractTemplate.getID());
            //            return Mono
            //                .from(contractTemplateMono)
            //                .onErrorReturn(null)
            //                .flatMap(
            //                    contractTemplate1 -> {
            //                        if (contractTemplate1 != null) {
            //                            if (contractTemplate1.getTeam().getApprover().size() > 0) {
            //                                for (User user : contractTemplate1.getTeam().getApprover()) {
            //                                    mailService.sendEmail(
            //                                        user.getEmail(),
            //                                        "New Template Review",
            //                                        "Please review the template " + contractTemplate1.getTemplateName(),
            //                                        true,
            //                                        false
            //                                    );
            //                                }
            //                                contractTemplate1.setStatus(TemplateStatus.APPROVAL_PENDING);
            //                                return logContractTemplateHistory(contractTemplate1, "Template send for approval")
            //                                    .flatMap(
            //                                        contractTemplate2 ->
            //                                            contractService
            //                                                .updateTemplate(contractTemplate2)
            //                                                .onErrorReturn(null)
            //                                                .flatMap(
            //                                                    updatedTemplate -> {
            //                                                        if (updatedTemplate != null) return Mono.just(
            //                                                            new ResponseEntity<>(
            //                                                                new ResponseMessage("Template send for approval"),
            //                                                                HttpStatus.OK
            //                                                            )
            //                                                        ); else return Mono.just(
            //                                                            new ResponseEntity<>(
            //                                                                new ResponseMessage("Could not send the Template for approval"),
            //                                                                HttpStatus.EXPECTATION_FAILED
            //                                                            )
            //                                                        );
            //                                                    }
            //                                                )
            //                                    );
            //                            } else return Mono.just(
            //                                new ResponseEntity<>(
            //                                    new ResponseMessage("No Approver present in associated Team"),
            //                                    HttpStatus.EXPECTATION_FAILED
            //                                )
            //                            );
            //                        } else return Mono.just(
            //                            new ResponseEntity<>(new ResponseMessage("Template doesn't exists"), HttpStatus.EXPECTATION_FAILED)
            //                        );
            //                    }
            //                );
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(
                new ResponseEntity<>(new ResponseMessage("Could not send the Template for approval"), HttpStatus.EXPECTATION_FAILED)
            );
        }
    }

    @PutMapping("/templates/approve")
    public Mono<ResponseEntity<ResponseMessage>> approveTemplate(@RequestBody ContractTemplate contractTemplate) {
        try {
            Mono<ContractTemplate> contractTemplateMono = contractService.getTemplateById(contractTemplate.getID());
            return Mono
                .from(contractTemplateMono)
                .flatMap(
                    contractTemplate1 ->
                        Mono.from(
                            handleContractTemplateWorkflow(contractTemplate1, EntityStatus.APPROVED)
                                .flatMap(
                                    typeStatus -> {
                                        contractTemplate1.setStatus(EntityStatus.APPROVED);
                                        return contractService
                                            .updateEntity(contractTemplate1)
                                            .thenReturn(new ResponseEntity<>(typeStatus, HttpStatus.OK));
                                    }
                                )
                        )
                )
                .thenReturn(new ResponseEntity<>(new ResponseMessage("Successfully updated the status."), HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not approve the Template"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @PutMapping("/templates/reject")
    public Mono<ResponseEntity<ResponseMessage>> rejectTemplate(@RequestBody ContractTemplate contractTemplate) {
        try {
            Mono<ContractTemplate> contractTemplateMono = contractService.getTemplateById(contractTemplate.getID());
            return Mono
                .from(contractTemplateMono)
                .flatMap(
                    contractTemplate1 ->
                        Mono.from(
                            handleContractTemplateWorkflow(contractTemplate1, EntityStatus.REJECTED)
                                .flatMap(
                                    typeStatus -> {
                                        contractTemplate1.setStatus(EntityStatus.REJECTED);
                                        return contractService
                                            .updateEntity(contractTemplate1)
                                            .thenReturn(new ResponseEntity<>(typeStatus, HttpStatus.OK));
                                    }
                                )
                        )
                )
                .thenReturn(new ResponseEntity<>(new ResponseMessage("Successfully updated the status."), HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not reject the Template"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @GetMapping("/templates")
    public Flux<ContractTemplate> getAllTemplates() {
        return contractService.getAllTemplates();
    }

    @GetMapping("/templates/{templateId}")
    public Mono<ContractTemplateForm> getContractTemplateById(@PathVariable("templateId") String templateId) {
        return contractService
            .getTemplateById(templateId)
            .onErrorReturn(null)
            .flatMap(
                contractTemplate -> {
                    ByteArrayInputStream srcTemplateStream;
                    String tempFileName = "Template_" + Math.abs(ThreadLocalRandom.current().nextInt());
                    System.out.println("Template File Name" + tempFileName);
                    File targetFile = new File("./src/main/webapp/content/" + tempFileName + ".docx");
                    if (this.fileStorageCurrent.equals(EXTERNAL)) srcTemplateStream =
                        (ByteArrayInputStream) fileHostDownload(contractTemplate.getFilePath()).block(); else srcTemplateStream =
                        new ByteArrayInputStream(contractTemplate.getTemplateFile().getData());
                    try {
                        Files.copy(srcTemplateStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    TempFilesCleaner tempFilesCleaner = TempFilesCleaner.getInstance();
                    tempFilesCleaner.scheduleForDeletion(targetFile.toPath(), 600);
                    IOUtils.closeQuietly(srcTemplateStream);
                    ContractTemplateForm contractTemplateForm = new ContractTemplateForm(contractTemplate);
                    contractTemplateForm.setTempFileName(tempFileName);
                    return Mono
                        .just(contractTemplateForm)
                        .flatMap(
                            template ->
                                getEntityRole(template.getTeam(), null)
                                    .flatMap(
                                        role -> {
                                            template.setEntityRole(role);
                                            return Mono.just(template);
                                        }
                                    )
                        );
                }
            );
    }

    @GetMapping("/templates/revisions/{templateId}")
    public Mono<ContractTemplateRevision> getContractTemplateVersions(@PathVariable("templateId") String templateId) {
        return contractService.getMinifiedTemplateRevisionById(templateId);
    }

    @PutMapping("/templates/rollback")
    public Mono<ResponseEntity<ResponseMessage>> rollBackTemplate(@RequestBody ContractTemplate contractTemplate) {
        Mono<ContractTemplate> contractTemplateMono = contractService.getTemplateById(contractTemplate.getID());
        Mono<ContractTemplateRevision> contractTemplateRevisionMono = contractService.getContractTemplateRevisionById(
            contractTemplate.getID()
        );
        return Mono
            .zip(contractTemplateMono, contractTemplateRevisionMono)
            .flatMap(
                data -> {
                    ContractTemplate contractTemplate1 = data.getT1();
                    ContractTemplateRevision contractTemplateRevision = data.getT2();
                    ContractTemplate rollBackContractTemplate = null;
                    for (ContractTemplate contractTemplate2 : contractTemplateRevision.contractTemplateRevisions) {
                        if (contractTemplate2.getVersion() == contractTemplate.getVersion()) {
                            rollBackContractTemplate = contractTemplate2;
                            break;
                        }
                    }
                    if (rollBackContractTemplate != null) {
                        ContractTemplate finalRollBackContractTemplate = rollBackContractTemplate;
                        return addContractTemplateVersion(contractTemplate1)
                            .then(
                                logContractTemplateHistory(
                                    contractTemplate1,
                                    "Template version rolled back from " +
                                    contractTemplate1.getVersion() +
                                    " to " +
                                    rollBackContractTemplate.getVersion()
                                )
                                    .flatMap(
                                        contractTemplate2 -> {
                                            contractTemplate2.incrementVersion();
                                            finalRollBackContractTemplate.setVersion(contractTemplate2.getVersion());
                                            finalRollBackContractTemplate.setHistory(contractTemplate2.getHistory());
                                            finalRollBackContractTemplate.setUpdatedOn(Instant.now());
                                            return contractService.updateTemplate(finalRollBackContractTemplate);
                                        }
                                    )
                            )
                            .then(
                                Mono.just(
                                    new ResponseEntity<>(new ResponseMessage("Template Version is rolled back successfully"), HttpStatus.OK)
                                )
                            )
                            .onErrorReturn(
                                new ResponseEntity<>(
                                    new ResponseMessage("Could not roll back Template Version"),
                                    HttpStatus.EXPECTATION_FAILED
                                )
                            );
                    } else {
                        return Mono.just(
                            new ResponseEntity<>(new ResponseMessage("Could not roll back Template Version"), HttpStatus.EXPECTATION_FAILED)
                        );
                    }
                }
            );
    }

    private Mono<ApiService> getApiService(WebSession session, String serviceName) {
        //        ApiService apiService = null;

        if (session.getAttribute(serviceName) != null) {
            //            apiService = session.getAttribute(serviceName);
            return Mono.just(session.getAttribute(serviceName));
        }

        return contractService
            .getApiService(serviceName)
            .onErrorReturn(null)
            .flatMap(
                apiService1 -> {
                    if (apiService1 != null) {
                        session.getAttributes().put(serviceName, apiService1);
                    }
                    return Mono.just(apiService1);
                }
            );
        //        Mono.from(apiServiceMono).flatMap(apiService1 -> {})

        //        if (session.getAttribute(serviceName)!=null){
        //            apiService = session.getAttribute(serviceName);
        //            return Mono.just(apiService);
        //        }else{
        //            return contractService.getApiService(serviceName).onErrorReturn(null)
        //                .flatMap(apiService1 -> {
        //                    if (apiService1!=null) {
        //                        session.getAttributes().put(serviceName, apiService1);
        //                    }
        //                    return Mono.just(apiService1);
        //                });
        //        }

    }

    @PostMapping(value = "/templates/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadTemplate(@RequestPart("file") Mono<FilePart> filePartMono, WebSession session) {
        try {
            String tempFileName = "Template_" + Math.abs(ThreadLocalRandom.current().nextInt());
            Path folderPath = Files.createDirectories(Paths.get("./src/main/webapp/content/"));
            final Path uploadPath = folderPath.resolve(tempFileName + ".docx");
            TempFilesCleaner tempFilesCleaner = TempFilesCleaner.getInstance();
            tempFilesCleaner.scheduleForDeletion(uploadPath, 600);
            session.getAttributes().put("uploadedTemplate", tempFileName);
            System.out.println("File Name" + tempFileName);
            Mono<Void> fileCopyMono = filePartMono
                .doOnNext(fp -> System.out.println("Received File : " + fp.filename()))
                .flatMap(fp -> fp.transferTo(uploadPath))
                .then();
            return Mono.from(fileCopyMono).onErrorReturn(null).flatMap(fc -> Mono.just(tempFileName));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.empty();
        }
    }

    @GetMapping(value = "/templates/upload")
    public Mono<Map> getUploadedTemplateId(WebSession session) {
        Map<String, String> reponseMap = new HashMap<>();
        reponseMap.put("fileId", session.getAttribute("uploadedTemplate"));
        return Mono.just(reponseMap);
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    @PostMapping("/clauses")
    public Mono<ResponseEntity<ResponseMessage>> addClause(@RequestBody ContractClause contractClause, WebSession session) {
        try {
            String srcFilePath = TEMP_FILES + "/" + session.getAttribute("uploadedClause") + ".docx";
            if (this.fileStorageCurrent.equals(EXTERNAL)) {
                String destFolderPath =
                    "/Documents/ContractManager/Clauses/" + (contractClause.getContractType().getName()).replaceAll(" ", "_").toLowerCase();
                String destFilePath =
                    "Documents/ContractManager/Clauses/" +
                    (contractClause.getContractType().getName() + "/" + contractClause.getClauseName()).replaceAll(" ", "_").toLowerCase() +
                    ".docx";
                if (!nextCloudHandler.pathExists(destFolderPath)) nextCloudHandler.createFolder(destFolderPath);
                saveFileToExternal(srcFilePath, destFilePath).subscribe();
                contractClause.setFilePath(destFilePath);
            } else if (this.fileStorageCurrent.equals(EMBEDDED)) {
                //                Path path = Paths.get("./src/main/webapp/content/ContractTemplate.docx");
                //                byte[] data = Files.readAllBytes(path);
                contractClause.setClauseFile(new Binary(BsonBinarySubType.BINARY, Files.readAllBytes(Paths.get(srcFilePath))));
            }
            contractClause.setCode(new RandomString(12).nextString());
            contractClause.setCreatedBy(contractClause.getTeam().getPrimaryOwner().getFirstName());
            contractClause.setStatus(EntityStatus.DRAFT);
            contractClause.setUpdatedOn(Instant.now());
            return logContractClauseHistory(contractClause, "Contract Clause Created")
                .then(
                    contractService
                        .addClause(contractClause)
                        .flatMap(
                            addClauseResult -> {
                                if (addClauseResult) {
                                    return Mono.just(
                                        new ResponseEntity<>(
                                            new ResponseMessage("Added contract clause successfully: " + contractClause.getClauseName()),
                                            HttpStatus.OK
                                        )
                                    );
                                } else return Mono.just(
                                    new ResponseEntity<>(
                                        new ResponseMessage("Could not add the clause file: " + contractClause.getClauseName() + "!"),
                                        HttpStatus.EXPECTATION_FAILED
                                    )
                                );
                            }
                        )
                        .onErrorReturn(
                            new ResponseEntity<>(
                                new ResponseMessage("Could not add the clause file: " + contractClause.getClauseName() + "!"),
                                HttpStatus.EXPECTATION_FAILED
                            )
                        )
                );
            //            return contractService.addClause(contractClause).flatMap(
            //                    addClauseResult -> {
            //                        if (addClauseResult)
            //                            return logContractClauseHistory(contractClause, "Contract Clause Created")
            //                                .then(Mono.just(new ResponseEntity<>(new ResponseMessage("Added contract clause successfully: " + contractClause.getClauseName()), HttpStatus.OK)));
            //                        else
            //                            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not add the clause file: " + contractClause.getClauseName() + "!"), HttpStatus.EXPECTATION_FAILED));
            //                    }
            //                )
            //                .onErrorReturn(new ResponseEntity<>(new ResponseMessage("Could not add the clause file: " + contractClause.getClauseName() + "!"), HttpStatus.EXPECTATION_FAILED));
        } catch (Exception e) {
            return Mono.just(
                new ResponseEntity<>(
                    new ResponseMessage("Could not add the clause file: " + contractClause.getClauseName() + "!"),
                    HttpStatus.EXPECTATION_FAILED
                )
            );
        }
    }

    @PutMapping("/clauses")
    public Mono<ResponseEntity<ResponseMessage>> updateClause(@RequestBody ContractClauseForm contractClauseForm, WebSession session) {
        try {
            String srcFilePath = TEMP_FILES + "/" + session.getAttribute("uploadedClause") + ".docx";
            if (contractClauseForm.getClauseUploaded()) {
                if (this.fileStorageCurrent.equals(EXTERNAL)) {
                    //                    String destFilePath = "Documents/ContractManager/Templates/" + (contractTemplateForm.getContractType().getName() + "/" + contractTemplateForm.getTemplateName()).replaceAll(" ", "_").toLowerCase() + ".docx";
                    saveFileToExternal(srcFilePath, contractClauseForm.getFilePath()).subscribe();
                } else if (this.fileStorageCurrent.equals(EMBEDDED)) {
                    contractClauseForm.setClauseFile(new Binary(BsonBinarySubType.BINARY, Files.readAllBytes(Paths.get(srcFilePath))));
                }
            }
            contractClauseForm.setStatus(EntityStatus.DRAFT);
            return addVersionAndUpdateClause(contractClauseForm)
                .flatMap(
                    updateClauseResult -> {
                        if (updateClauseResult) return Mono.just(
                            new ResponseEntity<>(new ResponseMessage("Contract Clause saved successfully"), HttpStatus.OK)
                        ); else return Mono.just(
                            new ResponseEntity<>(new ResponseMessage("Could not save Contract Clause"), HttpStatus.EXPECTATION_FAILED)
                        );
                    }
                )
                .onErrorReturn(new ResponseEntity<>(new ResponseMessage("Could not save Contract Clause"), HttpStatus.EXPECTATION_FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not save Contract Clause"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @PutMapping("/clauses/{clauseId}")
    public Mono<ResponseEntity<ResponseMessage>> updateClauseStatus(
        @PathVariable("clauseId") String clauseId,
        @RequestBody ClauseStatusDTO status
    ) {
        try {
            Mono<ContractClause> contractClauseMono = contractService.getClauseById(clauseId);
            return Mono
                .from(contractClauseMono)
                .flatMap(
                    contractClause -> {
                        String historyMessage = "Clause Status Changes from " + contractClause.getStatus() + " to " + status.getStatus();
                        contractClause.setStatus(status.getStatus());
                        return logContractClauseHistory(contractClause, historyMessage)
                            .flatMap(
                                contractClause1 ->
                                    contractService
                                        .updateClause(contractClause1)
                                        .flatMap(
                                            updatedClause ->
                                                Mono.just(
                                                    new ResponseEntity<>(
                                                        new ResponseMessage("Contract Clause Status updated successfully"),
                                                        HttpStatus.OK
                                                    )
                                                )
                                        )
                                        .onErrorReturn(
                                            new ResponseEntity<>(
                                                new ResponseMessage("Could not update Contract Clause Status"),
                                                HttpStatus.EXPECTATION_FAILED
                                            )
                                        )
                            );
                    }
                );
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not update Clause Status"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    //    @PostMapping("/addclause")
    //    public Mono<ResponseEntity<ResponseMessage>> addClause(@RequestBody ContractClause contractClause, WebSession session) {
    //        String message = "";
    //        try {
    //
    //            String contractPath = "/Documents/ContractManager/Clauses/"+(contractClause.getContractTypeName()).replaceAll(" ", "_").toLowerCase();
    //            if (!nextCloudHandler.pathExists(contractPath))
    //                nextCloudHandler.createFolder(contractPath);
    //            String clausePath = NEXTCLOUD_BASE_URL + "/Documents/ContractManager/Clauses/"+(contractClause.getContractTypeName()+"/"+contractClause.getClauseName()).replaceAll(" ", "_").toLowerCase()+".jrxml";
    //            PutMethod putMethod = new PutMethod(clausePath);
    //            InputStream inputStream = session.getAttribute("UPLOADED_CLAUSE");
    //            RequestEntity requestEntity = new InputStreamRequestEntity(inputStream);
    //            putMethod.setRequestEntity(requestEntity);
    //            HttpClient client = getNextCloudConnection();
    //            client.executeMethod(putMethod);
    //
    //            System.out.println(putMethod.getStatusCode() + " " + putMethod.getStatusText());
    //            contractClause.setFilePath(clausePath);
    //
    //           return contractService.addClause(contractClause).flatMap(
    //                    addClauseResult -> {
    //                        if (addClauseResult)
    //                            return Mono.just(new ResponseEntity<>(new ResponseMessage("Added contract clause successfully: " + contractClause.getContractTypeName()), HttpStatus.OK));
    //                        else
    //                            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not add the clause file: " + contractClause.getContractTypeName()+ "!"), HttpStatus.EXPECTATION_FAILED));
    //                    }
    //                )
    //                .onErrorReturn(new ResponseEntity<>(new ResponseMessage("Could not add the clause file: " + contractClause.getContractTypeName()+ "!"), HttpStatus.EXPECTATION_FAILED));
    //        } catch (Exception e) {
    //            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not add the clause file: " + contractClause.getContractTypeName()+ "!"), HttpStatus.EXPECTATION_FAILED));
    //        }
    //    }
    //    @PostMapping("/uploadclause")
    //    public ResponseEntity<ResponseMessage> uploadClause(@RequestParam("file") MultipartFile file, WebSession session) {
    //        String message = "";
    //        try {
    //            session.getAttributes().put("UPLOADED_CLAUSE", file.getInputStream());
    ////            session.getAttributes("UPLOADED_CLAUSE", file.getInputStream());
    //            message = "Uploaded contract clause successfully: " + file.getOriginalFilename();
    //            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    //        } catch (Exception e) {
    //            message = "Could not upload the clause file: " + file.getOriginalFilename() + "!";
    //            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
    //        }
    //    }

    @PostMapping(value = "/clauses/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadClause(@RequestPart("file") Mono<FilePart> filePartMono, WebSession session) {
        try {
            String tempFileName = "Clause_" + Math.abs(ThreadLocalRandom.current().nextInt());
            Path folderPath = Files.createDirectories(Paths.get("./src/main/webapp/content/"));
            final Path uploadPath = folderPath.resolve(tempFileName + ".docx");
            TempFilesCleaner tempFilesCleaner = TempFilesCleaner.getInstance();
            tempFilesCleaner.scheduleForDeletion(uploadPath, 600);
            session.getAttributes().put("uploadedClause", tempFileName);
            System.out.println("File Name" + tempFileName);
            Mono<Void> fileCopyMono = filePartMono
                .doOnNext(fp -> System.out.println("Received File : " + fp.filename()))
                .flatMap(fp -> fp.transferTo(uploadPath))
                .then();
            return Mono.from(fileCopyMono).onErrorReturn(null).flatMap(fc -> Mono.just(tempFileName));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.empty();
        }
    }

    @GetMapping(value = "/clauses/upload")
    public Mono<Map> getUploadedClauseId(WebSession session) {
        Map<String, String> reponseMap = new HashMap<>();
        reponseMap.put("fileId", session.getAttribute("uploadedClause"));
        return Mono.just(reponseMap);
    }

    @GetMapping("/clauses")
    public Flux<ContractClause> getAllClauses() {
        return contractService.getAllClauses();
    }

    @GetMapping("/clauses/{clauseId}")
    public Mono<ContractClauseForm> getContractClauseById(@PathVariable("clauseId") String clauseId) {
        return contractService
            .getClauseById(clauseId)
            .onErrorReturn(null)
            .flatMap(
                contractClause -> {
                    ByteArrayInputStream srcTemplateStream;
                    String tempFileName = "Clause_" + Math.abs(ThreadLocalRandom.current().nextInt());
                    System.out.println("Clause File Name" + tempFileName);
                    File targetFile = new File("./src/main/webapp/content/" + tempFileName + ".docx");
                    if (this.fileStorageCurrent.equals(EXTERNAL)) srcTemplateStream =
                        (ByteArrayInputStream) fileHostDownload(contractClause.getFilePath()).block(); else srcTemplateStream =
                        new ByteArrayInputStream(contractClause.getClauseFile().getData());
                    try {
                        Files.copy(srcTemplateStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    TempFilesCleaner tempFilesCleaner = TempFilesCleaner.getInstance();
                    tempFilesCleaner.scheduleForDeletion(targetFile.toPath(), 600);
                    IOUtils.closeQuietly(srcTemplateStream);
                    ContractClauseForm contractClauseForm = new ContractClauseForm(contractClause);
                    contractClauseForm.setTempFileName(tempFileName);
                    return Mono
                        .just(contractClauseForm)
                        .flatMap(
                            clause ->
                                getEntityRole(clause.getTeam(), null)
                                    .flatMap(
                                        role -> {
                                            clause.setEntityRole(role);
                                            return Mono.just(clause);
                                        }
                                    )
                        );
                }
            );
    }

    @PutMapping("/clauses/publish")
    public Mono<ResponseEntity<ResponseMessage>> publishClause(@RequestBody ContractClause contractClause) {
        try {
            Mono<ContractClause> contractClauseMono = contractService.getClauseById(contractClause.getID());
            return Mono
                .from(contractClauseMono)
                .flatMap(
                    contractClause1 ->
                        Mono.from(
                            handleContractClauseWorkflow(contractClause1, EntityStatus.APPROVAL_PENDING)
                                .flatMap(
                                    typeStatus -> {
                                        contractClause1.setStatus(EntityStatus.APPROVAL_PENDING);
                                        return contractService
                                            .updateEntity(contractClause1)
                                            .thenReturn(new ResponseEntity<>(typeStatus, HttpStatus.OK));
                                    }
                                )
                        )
                )
                .thenReturn(new ResponseEntity<>(new ResponseMessage("Successfully updated the status."), HttpStatus.OK));
            //            return Mono
            //                .from(contractClauseMono)
            //                .onErrorReturn(null)
            //                .flatMap(
            //                    contractClause1 -> {
            //                        if (contractClause1 != null) {
            //                            if (contractClause1.getTeam().getApprover().size() > 0) {
            //                                for (User user : contractClause1.getTeam().getApprover()) {
            //                                    mailService.sendEmail(
            //                                        user.getEmail(),
            //                                        "New Clause Review",
            //                                        "Please review the clause " + contractClause1.getClauseName(),
            //                                        true,
            //                                        false
            //                                    );
            //                                }
            //                                contractClause1.setStatus(ClauseStatus.APPROVAL_PENDING);
            //                                return logContractClauseHistory(contractClause1, "Clause send for approval")
            //                                    .flatMap(
            //                                        contractClause2 ->
            //                                            contractService
            //                                                .updateClause(contractClause1)
            //                                                .onErrorReturn(null)
            //                                                .flatMap(
            //                                                    updatedClause -> {
            //                                                        if (updatedClause != null) return Mono.just(
            //                                                            new ResponseEntity<>(
            //                                                                new ResponseMessage("Clause send for approval"),
            //                                                                HttpStatus.OK
            //                                                            )
            //                                                        ); else return Mono.just(
            //                                                            new ResponseEntity<>(
            //                                                                new ResponseMessage("Could not send the Clause for approval"),
            //                                                                HttpStatus.EXPECTATION_FAILED
            //                                                            )
            //                                                        );
            //                                                    }
            //                                                )
            //                                    );
            //                            } else return Mono.just(
            //                                new ResponseEntity<>(
            //                                    new ResponseMessage("No Approver present in associated Team"),
            //                                    HttpStatus.EXPECTATION_FAILED
            //                                )
            //                            );
            //                        } else return Mono.just(
            //                            new ResponseEntity<>(new ResponseMessage("Clause doesn't exists"), HttpStatus.EXPECTATION_FAILED)
            //                        );
            //                    }
            //                );
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(
                new ResponseEntity<>(new ResponseMessage("Could not send the Clause for approval"), HttpStatus.EXPECTATION_FAILED)
            );
        }
    }

    @PutMapping("/clauses/approve")
    public Mono<ResponseEntity<ResponseMessage>> approveClause(@RequestBody ContractClause contractClause) {
        try {
            Mono<ContractClause> contractClauseMono = contractService.getClauseById(contractClause.getID());
            return Mono
                .from(contractClauseMono)
                .flatMap(
                    contractContract1 ->
                        Mono.from(
                            handleContractClauseWorkflow(contractContract1, EntityStatus.APPROVED)
                                .flatMap(
                                    typeStatus -> {
                                        contractContract1.setStatus(EntityStatus.APPROVED);
                                        return contractService
                                            .updateEntity(contractContract1)
                                            .thenReturn(new ResponseEntity<>(typeStatus, HttpStatus.OK));
                                    }
                                )
                        )
                )
                .thenReturn(new ResponseEntity<>(new ResponseMessage("Successfully updated the status."), HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not approve the Clause"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @PutMapping("/clauses/reject")
    public Mono<ResponseEntity<ResponseMessage>> rejectClause(@RequestBody ContractClause contractClause) {
        try {
            Mono<ContractClause> contractClauseMono = contractService.getClauseById(contractClause.getID());
            return Mono
                .from(contractClauseMono)
                .flatMap(
                    contractClause1 ->
                        Mono.from(
                            handleContractClauseWorkflow(contractClause1, EntityStatus.REJECTED)
                                .flatMap(
                                    typeStatus -> {
                                        contractClause1.setStatus(EntityStatus.REJECTED);
                                        return contractService
                                            .updateEntity(contractClause1)
                                            .thenReturn(new ResponseEntity<>(typeStatus, HttpStatus.OK));
                                    }
                                )
                        )
                )
                .thenReturn(new ResponseEntity<>(new ResponseMessage("Successfully updated the status."), HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not reject the Clause"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @GetMapping("/clauses/revisions/{clauseId}")
    public Mono<ContractClauseRevision> getContractClauseVersions(@PathVariable("clauseId") String clauseId) {
        return contractService.getMinifiedClauseRevisionById(clauseId);
    }

    @PutMapping("/clauses/rollback")
    public Mono<ResponseEntity<ResponseMessage>> rollBackClause(@RequestBody ContractClause contractClause) {
        Mono<ContractClause> contractClauseMono = contractService.getClauseById(contractClause.getID());
        Mono<ContractClauseRevision> contractClauseRevisionMono = contractService.getContractClauseRevisionById(contractClause.getID());
        return Mono
            .zip(contractClauseMono, contractClauseRevisionMono)
            .flatMap(
                data -> {
                    ContractClause contractClause1 = data.getT1();
                    ContractClauseRevision contractClauseRevision = data.getT2();
                    ContractClause rollBackContractClause = null;
                    for (ContractClause contractClause2 : contractClauseRevision.contractClauseRevisions) {
                        if (contractClause2.getVersion() == contractClause.getVersion()) {
                            rollBackContractClause = contractClause2;
                            break;
                        }
                    }
                    if (rollBackContractClause != null) {
                        ContractClause finalRollBackContractClause = rollBackContractClause;

                        return addContractClauseVersion(contractClause1)
                            .then(
                                logContractClauseHistory(
                                    contractClause1,
                                    "Clause version rolled back from " +
                                    contractClause1.getVersion() +
                                    " to " +
                                    rollBackContractClause.getVersion()
                                )
                                    .flatMap(
                                        contractClause2 -> {
                                            contractClause2.incrementVersion();
                                            finalRollBackContractClause.setVersion(contractClause2.getVersion());
                                            finalRollBackContractClause.setHistory(contractClause2.getHistory());
                                            finalRollBackContractClause.setUpdatedOn(Instant.now());
                                            return contractService.updateClause(finalRollBackContractClause);
                                        }
                                    )
                            )
                            .then(
                                Mono.just(
                                    new ResponseEntity<>(new ResponseMessage("Clause Version is rolled back successfully"), HttpStatus.OK)
                                )
                            )
                            .onErrorReturn(
                                new ResponseEntity<>(
                                    new ResponseMessage("Could not roll back Clause Version"),
                                    HttpStatus.EXPECTATION_FAILED
                                )
                            );
                        //                return   logContractClauseHistory(contractClause1, "Clause version rolled back from "+contractClause1.getVersion()+" to "+rollBackContractClause.getVersion())
                        //                    .flatMap(contractClause2->{
                        //                        contractClause2.incrementVersion();
                        //                        finalRollBackContractClause.setVersion(contractClause2.getVersion());
                        //                        finalRollBackContractClause.setHistory(contractClause2.getHistory());
                        //                        finalRollBackContractClause.setUpdatedOn(Instant.now());
                        //                        return contractService.updateClause(finalRollBackContractClause);
                        //                    }).then(Mono.just(new ResponseEntity<>(new ResponseMessage("Clause Version is rolled back successfully"), HttpStatus.OK)))
                        //                    .onErrorReturn(new ResponseEntity<>(new ResponseMessage("Could not roll back Clause Version"), HttpStatus.EXPECTATION_FAILED));
                        //
                    } else {
                        return Mono.just(
                            new ResponseEntity<>(new ResponseMessage("Could not roll back Clause Version"), HttpStatus.EXPECTATION_FAILED)
                        );
                    }
                }
            );
    }

    @PostMapping("/oauthapiservices")
    public Mono<ResponseEntity<ResponseMessage>> addOauthApiService(@RequestBody ApiService apiService) {
        String message = "";
        try {
            Mono<ApiService> apiServiceMono = contractService.addApiService(apiService);
            return Mono
                .from(apiServiceMono)
                .flatMap(
                    addedApiService ->
                        Mono.just(
                            new ResponseEntity<>(
                                new ResponseMessage("Added the API Service: " + apiService.getServiceName()),
                                HttpStatus.OK
                            )
                        )
                )
                .onErrorReturn(
                    new ResponseEntity<>(
                        new ResponseMessage("Couldn't add the API Service: " + apiService.getServiceName() + "!"),
                        HttpStatus.EXPECTATION_FAILED
                    )
                );
            //            message = "Added the API Service: " + apiService.getServiceName();
            //            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            //            message = "Couldn't add the API Service: " + apiService.getServiceName() + "!";
            //            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            return Mono.just(
                new ResponseEntity<>(
                    new ResponseMessage("Couldn't add the API Service: " + apiService.getServiceName() + "!"),
                    HttpStatus.EXPECTATION_FAILED
                )
            );
        }
    }

    @PutMapping("/oauthapiservices/authorized")
    public Mono<ResponseEntity<ResponseMessage>> updateAuthorization(@RequestBody ApiService apiService) {
        String message = "";
        try {
            String authorizationCode = apiService.getAuthorizationCode();
            Mono<ApiService> apiServiceMono = contractService.getApiService(apiService.getServiceName());
            //            apiService = contractService.getApiService(apiService.getServiceName()).block();
            //            if (contractService.getApiService(apiService.getServiceName()).isPresent()){
            //                apiService = contractService.getApiService(apiService.getServiceName()).get();
            //            }
            //            apiService.setAuthorizationCode(authorizationCode);
            //            updateOauthRefreshToken(apiService);

            Mono<Boolean> updateRefreshTokenMono = Mono
                .from(apiServiceMono)
                .flatMap(
                    apiService1 -> {
                        if (apiService1 != null) {
                            apiService1.setAuthorizationCode(authorizationCode);
                            return updateOauthRefreshToken(apiService1);
                        }
                        return Mono.just(false);
                    }
                );
            return Mono
                .from(updateRefreshTokenMono)
                .flatMap(
                    updateResult -> {
                        if (updateResult) return Mono.just(
                            new ResponseEntity<>(
                                new ResponseMessage("Authorized the API Service: " + apiService.getServiceName()),
                                HttpStatus.OK
                            )
                        ); else return Mono.just(
                            new ResponseEntity<>(
                                new ResponseMessage("Couldn't Authorize the API Service: " + apiService.getServiceName() + "!"),
                                HttpStatus.EXPECTATION_FAILED
                            )
                        );
                    }
                )
                .onErrorReturn(
                    new ResponseEntity<>(
                        new ResponseMessage("Couldn't Authorize the API Service: " + apiService.getServiceName() + "!"),
                        HttpStatus.EXPECTATION_FAILED
                    )
                );
            //            message = "Authorized the API Service: " + apiService.getServiceName();
            //            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            //            message = "Couldn't Authorize the API Service: " + apiService.getServiceName() + "!";
            //            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            return Mono.just(
                new ResponseEntity<>(
                    new ResponseMessage("Couldn't Authorize the API Service: " + apiService.getServiceName() + "!"),
                    HttpStatus.EXPECTATION_FAILED
                )
            );
        }
    }

    @GetMapping("/oauthapiservices")
    public Flux<ApiService> getOauthApiServices() {
        return contractService.getApiServices();
    }

    @GetMapping("/attributesmeta")
    public Mono<AttributesMetaData> getAttributesmeta() {
        //        Optional<AttributesMetaData> attributesMetaData = attributesMetaDataRepository.findAll().stream().findFirst();
        return contractService.getAttributesMetaData();
    }

    @PostMapping(value = "/attributesmeta", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ResponseMessage>> addAttributeMeta(@RequestPart("file") Mono<FilePart> filePartMono) {
        try {
            Mono<AttributesMetaData> attributesMetaDataMono = filePartMono.flatMap(
                fp ->
                    fp
                        .content()
                        .map(
                            dataBuffer -> {
                                AttributesMetaData attributesMetaData = new AttributesMetaData();
                                List<AttributeConfig> attributeConfigs = new ArrayList<>();
                                try {
                                    DataFormatter dataFormatter = new DataFormatter();
                                    Workbook workbook = WorkbookFactory.create(dataBuffer.asInputStream());
                                    Sheet sheet = workbook.getSheetAt(0);
                                    Row headerRow = sheet.getRow(0);
                                    ArrayList<String> fieldNames = new ArrayList<>();
                                    HashMap<String, Class<?>> fieldClassMapper = new HashMap<>();

                                    ArrayList<String> attributeConfigFields = getAllFieldsSimpleNames(AttributeConfig.class);
                                    ArrayList<String> attributeOptionsFields = getAllFieldsSimpleNames(AttributeOptions.class);

                                    headerRow.forEach(
                                        cell -> {
                                            String headerField = dataFormatter.formatCellValue(cell);
                                            fieldNames.add(headerField);

                                            if (attributeConfigFields.contains(headerField)) {
                                                fieldClassMapper.put(headerField, AttributeConfig.class);
                                            } else if (attributeOptionsFields.contains(headerField)) {
                                                fieldClassMapper.put(headerField, AttributeOptions.class);
                                            } else {
                                                throw new RuntimeException(
                                                    "Data Format Error. Uploaded data is not in the expected Format"
                                                );
                                            }
                                        }
                                    );
                                    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                                        AttributeConfig attributeConfig = new AttributeConfig();
                                        AttributeOptions attributeOptions = new AttributeOptions();
                                        Row currentRow = sheet.getRow(i);
                                        for (int j = 0; j < currentRow.getLastCellNum(); j++) {
                                            if (fieldClassMapper.get(fieldNames.get(j)) == AttributeConfig.class) {
                                                BeanUtils.setProperty(
                                                    attributeConfig,
                                                    fieldNames.get(j),
                                                    dataFormatter.formatCellValue(currentRow.getCell(j))
                                                );
                                            } else if (fieldClassMapper.get(fieldNames.get(j)) == AttributeOptions.class) {
                                                BeanUtils.setProperty(
                                                    attributeOptions,
                                                    fieldNames.get(j),
                                                    dataFormatter.formatCellValue(currentRow.getCell(j))
                                                );
                                            }
                                        }
                                        attributeConfig.setAttributeOptions(attributeOptions);
                                        attributeConfigs.add(attributeConfig);
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } catch (InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                                attributesMetaData.setAttributeConfigs(attributeConfigs);
                                return attributesMetaData;
                            }
                        )
                        .last()
            );

            return Mono
                .from(attributesMetaDataMono)
                .flatMap(attributesMetaData -> contractService.updateAttributesMetaData(attributesMetaData))
                .thenReturn(new ResponseEntity<>(new ResponseMessage("Uploaded attributes meta successfully"), HttpStatus.OK))
                .onErrorReturn(
                    new ResponseEntity<>(new ResponseMessage("Could not upload the attributes meta file !"), HttpStatus.EXPECTATION_FAILED)
                );
        } catch (Exception e) {
            return Mono.just(
                new ResponseEntity<>(new ResponseMessage("Could not upload the attributes meta file !"), HttpStatus.EXPECTATION_FAILED)
            );
        }
    }

    private static ArrayList<String> getAllFieldsSimpleNames(Class<?> beanClass) {
        ArrayList<String> fieldNames = new ArrayList<String>();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    InputStream getInputStreamFromFluxDataBuffer(Flux<DataBuffer> data) throws IOException {
        PipedOutputStream osPipe = new PipedOutputStream();
        PipedInputStream isPipe = new PipedInputStream(osPipe);

        DataBufferUtils
            .write(data, osPipe)
            .subscribeOn(Schedulers.elastic())
            .doOnComplete(
                () -> {
                    try {
                        osPipe.close();
                    } catch (IOException ignored) {}
                }
            )
            .subscribe(DataBufferUtils.releaseConsumer());
        return isPipe;
    }

    @GetMapping("/categories")
    public Flux<ContractCategory> getAllCategories() {
        return contractService.getAllCategories();
    }

    @PostMapping("/categories")
    public Mono<ResponseEntity<ResponseMessage>> addCategory(@RequestBody ContractCategory contractCategory) {
        try {
            return contractService
                .addCategory(contractCategory)
                .flatMap(
                    addedCategory ->
                        Mono.just(new ResponseEntity<>(new ResponseMessage("Contract Category Added successfully"), HttpStatus.OK))
                )
                .onErrorReturn(new ResponseEntity<>(new ResponseMessage("Could not Add Contract Category"), HttpStatus.EXPECTATION_FAILED));
            //            String message = "";
            //            contractService.addCategory(contractCategory);
            //            message = "Contract Category added successfully";
            //            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not Add Contract Category"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @PostMapping("/masterdata")
    public Mono<ResponseEntity<ResponseMessage>> addMasterData(@RequestBody MasterData masterData) {
        String message = "";
        try {
            return contractService
                .addMasterData(masterData)
                .flatMap(
                    addedMasterData -> Mono.just(new ResponseEntity<>(new ResponseMessage("Master Data Added successfully"), HttpStatus.OK))
                )
                .onErrorReturn(new ResponseEntity<>(new ResponseMessage("Could not Add Master Data"), HttpStatus.EXPECTATION_FAILED));
            //                contractService.addMasterData(masterData);
            //                message = "Master Data Table created successfully";
            //                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));

        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not Add Master Data"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @GetMapping("/agreements")
    public Flux<ContractAgreement> getContractAgreements() {
        return contractService.getAllAgreements();
    }

    @GetMapping("/agreements/q")
    public Flux<ContractAgreement> getContractAgreements(@RequestParam(name = "status") EntityStatus status) {
        return contractService.getAllAgreementsByStatus(status);
    }

    @DeleteMapping("/agreements/d/{agreementId}")
    public Mono<Boolean> deleteContractAgreement(@PathVariable("agreementId") String agreementId) {
        return this.contractService.deleteAgreementByStatus(agreementId);
    }

    @GetMapping("/agreements/{agreementId}")
    public Mono<ContractAgreementForm> getContractAgreement(@PathVariable("agreementId") String agreementId, WebSession session) {
        return contractService
            .getAgreementById(agreementId)
            .onErrorReturn(null)
            .flatMap(
                contractAgreement -> {
                    ByteArrayInputStream srcAgreementStream;
                    ByteArrayInputStream srcAgreementTemplateStream;
                    String tempAgreementFileName = "Agreement_" + Math.abs(ThreadLocalRandom.current().nextInt());
                    String tempTemplateFileName = "AgreementTemplate_" + Math.abs(ThreadLocalRandom.current().nextInt());
                    File targetAgreementFile = new File("./src/main/webapp/content/" + tempAgreementFileName + ".pdf");
                    File targetTemplateFile = new File("./src/main/webapp/content/" + tempTemplateFileName + ".docx");
                    if (this.fileStorageCurrent.equals(EXTERNAL)) {
                        srcAgreementStream = (ByteArrayInputStream) fileHostDownload(contractAgreement.getFilePath()).block();
                        srcAgreementTemplateStream =
                            (ByteArrayInputStream) fileHostDownload(contractAgreement.getTemplate().getFilePath()).block();
                    } else {
                        srcAgreementStream = new ByteArrayInputStream(contractAgreement.getAgreementFile().getData());
                        srcAgreementTemplateStream = new ByteArrayInputStream(contractAgreement.getTemplate().getTemplateFile().getData());
                    }
                    try {
                        Files.copy(srcAgreementStream, targetAgreementFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        Files.copy(srcAgreementTemplateStream, targetTemplateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    TempFilesCleaner tempFilesCleaner = TempFilesCleaner.getInstance();
                    tempFilesCleaner.scheduleForDeletion(targetAgreementFile.toPath(), 600);
                    tempFilesCleaner.scheduleForDeletion(targetTemplateFile.toPath(), 600);
                    IOUtils.closeQuietly(srcAgreementStream);
                    IOUtils.closeQuietly(srcAgreementTemplateStream);
                    session.getAttributes().put("downloadedAgreement", tempAgreementFileName);
                    ContractAgreementForm contractAgreementForm = new ContractAgreementForm(contractAgreement);
                    contractAgreementForm.setTempAgreementFile(tempAgreementFileName);
                    contractAgreementForm.setTempTemplateFile(tempTemplateFileName);
                    return Mono.from(
                        notificationsUtil
                            .markEntityNotificationAsRead(agreementId)
                            .then(
                                getEntityRole(contractAgreement.getTeam(), contractAgreement.getTeamGroups())
                                    .flatMap(
                                        role -> {
                                            contractAgreementForm.setEntityRole(role);
                                            return Mono.just(contractAgreementForm);
                                        }
                                    )
                            )
                    );
                }
            );
    }

    //    @GetMapping("/agreements/download/{agreementName}")
    //    public Mono<Boolean> downloadAgreementFile(@PathVariable("agreementName") String agreementName) {
    //        try {
    //            Mono<ContractAgreement> contractAgreementMono = contractService.getAgreementByName(agreementName);
    //            FileUtils.deleteQuietly(new File("src\\main\\webapp\\content\\temp_agreement.pdf"));
    //            return Mono.from(contractAgreementMono).flatMap(contractAgreement ->{
    //              if (contractAgreement != null){
    //                  try {
    //                      InputStream inputStream = nextCloudHandler.downloadFile(contractAgreement.getFilePath());
    //                      FileOutputStream fileOS = null;
    //                      fileOS = new FileOutputStream("src\\main\\webapp\\content\\temp_agreement.pdf");
    //                      IOUtils.copy(inputStream, fileOS);
    //                      return Mono.just(true);
    //                  } catch (Exception e) {
    //                      e.printStackTrace();
    //                      return Mono.just(false);
    //                  }
    //              }else {
    //                  return Mono.just(false);
    //              }
    //            });
    ////            ContractAgreement contractAgreement = contractService.getAgreementByName(agreementName).block();
    ////            if (contractAgreement == null)
    ////                throw new IllegalArgumentException("Contract Agreement not present");
    ////            FileUtils.deleteQuietly(new File("src\\main\\webapp\\content\\temp_agreement.pdf"));
    ////            InputStream inputStream = nextCloudHandler.downloadFile(contractAgreement.getFilePath());
    ////            FileOutputStream fileOS = new FileOutputStream("src\\main\\webapp\\content\\temp_agreement.pdf");
    ////            IOUtils.copy(inputStream, fileOS);
    ////
    ////            return true;
    //        }catch (Exception e){
    ////            return false;
    //            return Mono.just(false);
    //        }
    //    }
    @GetMapping("/agreements/attributes/download/{contractTypeName}")
    public Mono<ResponseEntity<byte[]>> downloadAttributesJson(@PathVariable("contractTypeName") String contractTypeName) {
        try {
            Mono<ContractType> contractTypeMono = contractService.getTypeByName(contractTypeName);
            return Mono
                .from(contractTypeMono)
                .flatMap(
                    contractType -> {
                        if (contractType == null) return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null));
                        List<AttributeConfig> attributeConfigs = contractType.getAttributes();
                        HashMap attributes = new HashMap();
                        for (AttributeConfig attributeConfig : attributeConfigs) {
                            attributes.put(attributeConfig.getAttributeName(), "");
                        }

                        Map agreementsAttributes = new HashMap();
                        agreementsAttributes.put("Attributes", attributes);
                        ObjectMapper objectMapper = new ObjectMapper();
                        String json = null;
                        try {
                            json = objectMapper.writeValueAsString(agreementsAttributes);
                        } catch (JsonProcessingException e) {
                            return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null));
                        }
                        byte[] isr = json.getBytes();
                        String fileName = "attributes.json";
                        HttpHeaders respHeaders = new HttpHeaders();
                        respHeaders.setContentLength(isr.length);
                        respHeaders.setContentType(new MediaType("text", "json"));
                        respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                        respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
                        return Mono.just(new ResponseEntity<>(isr, respHeaders, HttpStatus.OK));
                    }
                );
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null));
        }
        //        try {
        //            ContractType contractType = contractService.getTypeByName(contractTypeName);
        //            if (contractType==null)
        //                throw new IllegalArgumentException("Contract Type not present");
        ////            List<Map<String, Object>> contractTypeAttributesMeta = contractType.getAttributesMetaData();
        //            List<AttributeConfig> attributeConfigs = contractType.getAttributes();
        //            HashMap attributes = new HashMap();
        //            for ( AttributeConfig attributeConfig : attributeConfigs ) {
        //                attributes.put(attributeConfig.getAttributeName(),"");
        //            }
        ////            for ( Map<String, Object> attributeMetaMap : contractTypeAttributesMeta ) {
        ////                attributes.put(attributeMetaMap.keySet().toArray()[0],"");
        ////            }
        //            Map agreementsAttributes = new HashMap();
        //            agreementsAttributes.put("Attributes", attributes);
        //            ObjectMapper objectMapper = new ObjectMapper();
        //            String json = objectMapper.writeValueAsString(agreementsAttributes);
        //            byte[] isr = json.getBytes();
        //            String fileName = "attributes.json";
        //            HttpHeaders respHeaders = new HttpHeaders();
        //            respHeaders.setContentLength(isr.length);
        //            respHeaders.setContentType(new MediaType("text", "json"));
        //            respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        //            respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        //            return new ResponseEntity<byte[]>(isr, respHeaders, HttpStatus.OK);
        //        }catch (Exception e){
        //            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
        //        }
    }

    @GetMapping("/agreements/audit/{agreementName}")
    public Mono<Boolean> downloadAuditLogFile(@PathVariable("agreementName") String agreementName, WebSession session) {
        String message = "";
        try {
            message = "Contract Agreement published successfully";
            Mono<ContractAgreement> contractAgreementMono = contractService.getAgreementByName(agreementName);
            FileUtils.deleteQuietly(new File("src\\main\\webapp\\content\\agreement_audit_log.pdf"));
            //            ApiService apiService = getApiService(session,"EsignApiService");

            return getApiService(session, "EsignApiService")
                .flatMap(
                    apiService1 -> {
                        String esignAccessToken = getAccessTokenFromRefreshToken(apiService1);
                        String authorization = BEARER + esignAccessToken;
                        ApiClient apiClient = null;
                        try {
                            apiClient = getAppClient(authorization);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        AgreementsApi agreementsApi = new AgreementsApi(apiClient);
                        String xApiUser = null;
                        String xOnBehalfOfUser = null;
                        String ifNoneMatch = null;
                        return Mono
                            .from(contractAgreementMono)
                            .flatMap(
                                contractAgreement -> {
                                    try {
                                        byte auditStream[] = agreementsApi.getAuditTrail(
                                            authorization,
                                            contractAgreement.getSignAgreementId(),
                                            xApiUser,
                                            xOnBehalfOfUser,
                                            ifNoneMatch
                                        );
                                        if (auditStream != null) {
                                            FileOutputStream fileOS = new FileOutputStream(
                                                "src\\main\\webapp\\content\\agreement_audit_log.pdf"
                                            );
                                            IOUtils.copy(new ByteArrayInputStream(auditStream), fileOS);
                                            return Mono.just(true);
                                        } else {
                                            return Mono.just(false);
                                        }
                                    } catch (Exception e) {
                                        return Mono.just(false);
                                    }
                                }
                            );
                    }
                );
            //            return Mono.from(contractAgreementMono).flatMap(contractAgreement ->{
            //                try {
            //                    byte auditStream[] =agreementsApi.getAuditTrail(authorization, contractAgreement.getSignAgreementId(), xApiUser, xOnBehalfOfUser, ifNoneMatch );
            //                    if (auditStream != null) {
            //                        FileOutputStream fileOS = new FileOutputStream("src\\main\\webapp\\content\\agreement_audit_log.pdf");
            //                        IOUtils.copy(new ByteArrayInputStream(auditStream), fileOS);
            //                        return Mono.just(true);
            //                    } else {
            //                        return Mono.just(false);
            //                    }
            //                } catch (Exception e) {
            //                    return Mono.just(false);
            //                }
            //            });

            //            ContractAgreement contractAgreement = contractService.getAgreementByName(agreementName).block();
            //            if (contractAgreement == null)
            //                throw new IllegalArgumentException("Contract Agreement not present");
            //            FileUtils.deleteQuietly(new File("src\\main\\webapp\\content\\agreement_audit_log.pdf"));
            //            ApiService apiService = getApiService(session,"EsignApiService");
            //            String esignAccessToken = getAccessTokenFromRefreshToken(apiService);
            //            String authorization = BEARER + esignAccessToken;
            //            ApiClient apiClient = getAppClient(authorization);
            //            AgreementsApi agreementsApi = new AgreementsApi(apiClient);
            //            String xApiUser = null;
            //            String xOnBehalfOfUser = null;
            //            String ifNoneMatch = null;
            //            byte auditStream[] =agreementsApi.getAuditTrail(authorization, contractAgreement.getSignAgreementId(), xApiUser, xOnBehalfOfUser, ifNoneMatch );
            //            if (auditStream != null) {
            //                FileOutputStream fileOS = new FileOutputStream("src\\main\\webapp\\content\\agreement_audit_log.pdf");
            //                IOUtils.copy(new ByteArrayInputStream(auditStream), fileOS);
            ////                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            //                return true;
            //            } else {
            //                throw new Exception("Could not download audit trial");
            //            }
        } catch (Exception e) {
            //            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            return Mono.just(false);
        }
    }

    //    @PutMapping("/agreements/publish")
    //    public Mono<ResponseEntity<ResponseMessage>> publishAgreement(@RequestBody ContractAgreement contractAgreement) {
    //        try {
    //            Mono<ContractAgreement> contractAgreementMono = contractService.getAgreementById(contractAgreement.getID());
    //            return Mono
    //                .from(contractAgreementMono)
    //                .onErrorReturn(null)
    //                .flatMap(
    //                    contractAgreement1 -> {
    //                        if (contractAgreement1 != null) {
    //                            if (contractAgreement1.getTeam().getApprover().size() > 0) {
    //                                for (User user : contractAgreement1.getTeam().getApprover()) {
    //                                    mailService.sendEmail(
    //                                        user.getEmail(),
    //                                        "New Agreement Review",
    //                                        "Please review the agreement " + contractAgreement1.getAgreementName(),
    //                                        true,
    //                                        false
    //                                    );
    //                                }
    //                                contractAgreement1.setAgreementStatus(AgreementStatus.APPROVAL_PENDING);
    //                                return logContractAgreementHistory(contractAgreement1, "Agreement send for Approval")
    //                                    .flatMap(
    //                                        contractAgreement2 ->
    //                                            contractService
    //                                                .updateAgreement(contractAgreement2)
    //                                                .onErrorReturn(null)
    //                                                .flatMap(
    //                                                    updatedAgreement -> {
    //                                                        if (updatedAgreement != null) return Mono.just(
    //                                                            new ResponseEntity<>(
    //                                                                new ResponseMessage("Agreement send for approval"),
    //                                                                HttpStatus.OK
    //                                                            )
    //                                                        ); else return Mono.just(
    //                                                            new ResponseEntity<>(
    //                                                                new ResponseMessage("Could not send the Agreement for approval"),
    //                                                                HttpStatus.EXPECTATION_FAILED
    //                                                            )
    //                                                        );
    //                                                    }
    //                                                )
    //                                    );
    //                                //                             contractAgreement1.getHistory().add(new AuditEvent("Agreement send for Approval", contractAgreement1.getFilePath(), new Date()));
    //
    //                            } else return Mono.just(
    //                                new ResponseEntity<>(
    //                                    new ResponseMessage("No Approver present in associated Team"),
    //                                    HttpStatus.EXPECTATION_FAILED
    //                                )
    //                            );
    //                        } else return Mono.just(
    //                            new ResponseEntity<>(new ResponseMessage("Agreement doesn't exists"), HttpStatus.EXPECTATION_FAILED)
    //                        );
    //                    }
    //                );
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            return Mono.just(
    //                new ResponseEntity<>(new ResponseMessage("Could not send the Agreement for approval"), HttpStatus.EXPECTATION_FAILED)
    //            );
    //        }
    //    }

    @PutMapping("/agreements/publish")
    public Mono<ResponseEntity<ResponseMessage>> publishAgreement(@RequestBody ContractAgreement contractAgreement) {
        try {
            Mono<ContractAgreement> contractAgreementMono = contractService.getAgreementById(contractAgreement.getID());
            return Mono
                .from(contractAgreementMono)
                .flatMap(
                    contractAgreement1 ->
                        Mono.from(
                            handleContractAgreementWorkflow(contractAgreement1, EntityStatus.APPROVAL_PENDING)
                                .flatMap(
                                    typeStatus -> {
                                        contractAgreement1.setAgreementStatus(EntityStatus.APPROVAL_PENDING);
                                        return contractService
                                            .updateEntity(contractAgreement1)
                                            .thenReturn(new ResponseEntity<>(typeStatus, HttpStatus.OK));
                                    }
                                )
                        )
                )
                .thenReturn(new ResponseEntity<>(new ResponseMessage("Successfully updated the status."), HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(
                new ResponseEntity<>(new ResponseMessage("Could not send the Agreement for approval"), HttpStatus.EXPECTATION_FAILED)
            );
        }
    }

    @PutMapping("/agreements/approve")
    public Mono<ResponseEntity<ResponseMessage>> approveAgreement(@RequestBody ContractAgreement contractAgreement) {
        try {
            Mono<ContractAgreement> contractAgreementMono = contractService.getAgreementById(contractAgreement.getID());
            return Mono
                .from(contractAgreementMono)
                .flatMap(
                    contractAgreement1 ->
                        Mono.from(
                            handleContractAgreementWorkflow(contractAgreement1, EntityStatus.APPROVED)
                                .flatMap(
                                    typeStatus -> {
                                        contractAgreement1.setAgreementStatus(EntityStatus.APPROVED);
                                        return contractService
                                            .updateEntity(contractAgreement1)
                                            .thenReturn(new ResponseEntity<>(typeStatus, HttpStatus.OK));
                                    }
                                )
                        )
                )
                .thenReturn(new ResponseEntity<>(new ResponseMessage("Successfully updated the status."), HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not approve the Agreement"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @PutMapping("/agreements/reject")
    public Mono<ResponseEntity<ResponseMessage>> rejectAgreement(@RequestBody ContractAgreement contractAgreement) {
        try {
            Mono<ContractAgreement> contractAgreementMono = contractService.getAgreementById(contractAgreement.getID());
            return Mono
                .from(contractAgreementMono)
                .flatMap(
                    contractAgreement1 ->
                        Mono.from(
                            handleContractAgreementWorkflow(contractAgreement1, EntityStatus.REJECTED)
                                .flatMap(
                                    typeStatus -> {
                                        contractAgreement1.setAgreementStatus(EntityStatus.REJECTED);
                                        return contractService
                                            .updateEntity(contractAgreement1)
                                            .thenReturn(new ResponseEntity<>(typeStatus, HttpStatus.OK));
                                    }
                                )
                        )
                )
                .thenReturn(new ResponseEntity<>(new ResponseMessage("Successfully updated the status."), HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not reject the Agreement"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @PostMapping("/agreements/create")
    public Mono<ResponseEntity<ResponseMessage>> createAgreement(@RequestBody ContractAgreementForm contractAgreementForm) {
        try {
            generateContractAgreement(contractAgreementForm)
                .flatMap(contractAgreementForm1 -> _addAgreement(contractAgreementForm1))
                .subscribe();
            return Mono.just(
                new ResponseEntity<>(new ResponseMessage("Contract Agreement creation initiated successfully"), HttpStatus.OK)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(
                new ResponseEntity<>(new ResponseMessage("Could not initiate Contract Agreement creation"), HttpStatus.EXPECTATION_FAILED)
            );
        }
    }

    private Mono<Boolean> saveFileToExternal(String srcPath, String destPath) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
            () -> {
                try {
                    PutMethod putMethod = new PutMethod(NEXTCLOUD_BASE_URL + "/" + destPath);
                    InputStream inputStream = null;
                    inputStream = new FileInputStream(srcPath);
                    RequestEntity requestEntity = new InputStreamRequestEntity(inputStream);
                    putMethod.setRequestEntity(requestEntity);
                    HttpClient client = getNextCloudConnection();
                    client.executeMethod(putMethod);
                    System.out.println(putMethod.getStatusCode() + " " + putMethod.getStatusText());
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        );
        Mono<Boolean> monoFromFuture = Mono.fromFuture(future);
        return monoFromFuture;
    }

    private Mono<Boolean> fileHostCreateFolder(String path) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
            () -> {
                try {
                    if (!nextCloudHandler.pathExists(path)) nextCloudHandler.createFolder(path);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        );
        Mono<Boolean> monoFromFuture = Mono.fromFuture(future);
        return monoFromFuture;
    }

    private Mono<InputStream> fileHostDownload(String filePath) {
        CompletableFuture<InputStream> future = CompletableFuture.supplyAsync(
            () -> {
                try {
                    InputStream downloadInputStream = nextCloudHandler.downloadFile(filePath);
                    return downloadInputStream;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        );
        Mono<InputStream> monoFromFuture = Mono.fromFuture(future);
        return monoFromFuture;
    }

    private Mono<DocumentMergeOperation> getDocumentMergeOperation(ContractAgreementForm contractAgreementForm) {
        HashMap<String, HashMap<String, String>> offerLtrMap = new HashMap<>();
        HashMap<String, String> attributeMap = new HashMap<>();
        for (Attribute attribute : contractAgreementForm.getAttributes()) {
            attributeMap.put(attribute.getAttributeName(), attribute.getAttributeValue());
        }
        offerLtrMap.put("Attributes", attributeMap);
        org.json.JSONObject jsonDataForMerge = new org.json.JSONObject(offerLtrMap);
        DocumentMergeOptions documentMergeOptions = new DocumentMergeOptions(jsonDataForMerge, OutputFormat.PDF);
        // Create a new DocumentMergeOperation instance with the DocumentMergeOptions instance
        return Mono.just(DocumentMergeOperation.createNew(documentMergeOptions));
    }

    private Mono<ContractAgreementForm> generateContractAgreement(ContractAgreementForm contractAgreementForm) {
        try {
            contractAgreementForm.setAgreementCode(new RandomString(12).nextString());
            contractAgreementForm.incrementVersion();

            Mono<InputStream> templateStreamMono = getTemplateStream(contractAgreementForm.getTemplate().getID());
            Mono<DocumentMergeOperation> documentMergeOperationMono = getDocumentMergeOperation(contractAgreementForm);
            com.adobe.platform.operation.auth.Credentials credentials;
            credentials =
                com.adobe.platform.operation.auth.Credentials
                    .serviceAccountCredentialsBuilder()
                    .fromFile("./src/main/resources/config/adobe/pdftools-api-credentials.json")
                    .build();
            // Create an ExecutionContext using credentials.
            ExecutionContext executionContext = ExecutionContext.create(credentials);

            Mono<Boolean> agreementFolderMono;
            if (this.fileStorageCurrent.equals(EXTERNAL)) {
                String agreementPath =
                    "Documents/ContractManager/Agreements/" +
                    (contractAgreementForm.getAgreementName() + "/" + contractAgreementForm.getTemplate().getTemplateName()).replaceAll(
                            " ",
                            "_"
                        )
                        .toLowerCase();
                String agreementFilePath = agreementPath + "/" + contractAgreementForm.getVersion() + ".pdf";
                contractAgreementForm.setFilePath(agreementFilePath);
                if (!nextCloudHandler.pathExists(agreementPath)) nextCloudHandler.createFolder(agreementPath);
                agreementFolderMono = fileHostCreateFolder(agreementPath);
            } else agreementFolderMono = Mono.just(true);

            return Mono
                .zip(templateStreamMono, documentMergeOperationMono, agreementFolderMono)
                .flatMap(
                    data -> {
                        InputStream templateInputStream = data.getT1();

                        DocumentMergeOperation documentMergeOperation = data.getT2();
                        Boolean agreementPathCreated = data.getT3();
                        if (templateInputStream != null && agreementPathCreated) {
                            CompletableFuture<ContractAgreementForm> docGenfuture = CompletableFuture.supplyAsync(
                                () -> {
                                    try {
                                        FileRef documentTemplate = FileRef.createFromStream(
                                            templateInputStream,
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                        );
                                        documentMergeOperation.setInput(documentTemplate);
                                        // Execute the operation
                                        FileRef result = documentMergeOperation.execute(executionContext);
                                        ByteArrayOutputStream tempOutPutstream = new ByteArrayOutputStream();
                                        result.saveAs(tempOutPutstream);

                                        tempOutPutstream =
                                            addQrCode(tempOutPutstream.toByteArray(), contractAgreementForm.getAgreementCode());
                                        if (this.fileStorageCurrent.equals(EXTERNAL)) {
                                            InputStream inputStream = new ByteArrayInputStream(tempOutPutstream.toByteArray());
                                            RequestEntity requestEntity = new InputStreamRequestEntity(inputStream);
                                            PutMethod putMethod = new PutMethod(
                                                NEXTCLOUD_BASE_URL + "/" + contractAgreementForm.getFilePath()
                                            );
                                            putMethod.setRequestEntity(requestEntity);

                                            HttpClient client = getNextCloudConnection();
                                            client.executeMethod(putMethod);
                                            System.out.println(putMethod.getStatusCode() + " " + putMethod.getStatusText());
                                        } else {
                                            contractAgreementForm.setAgreementFile(
                                                new Binary(BsonBinarySubType.BINARY, tempOutPutstream.toByteArray())
                                            );
                                        }
                                        return contractAgreementForm;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                }
                            );
                            return Mono.fromFuture(docGenfuture).flatMap(contractAgreement1 -> Mono.just(contractAgreement1));
                        } else return Mono.just(null);
                    }
                );
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(null);
        }
    }

    @GetMapping("/agreements/revisions/{agreementId}")
    public Mono<ContractAgreementRevision> getContractAgreementVersions(@PathVariable("agreementId") String agreementId) {
        return contractService.getMinifiedAgreementRevisionById(agreementId);
    }

    @PutMapping("/agreements/rollback")
    public Mono<ResponseEntity<ResponseMessage>> rollBackAgreement(@RequestBody ContractAgreement contractAgreement) {
        Mono<ContractAgreement> contractAgreementMono = contractService.getAgreementById(contractAgreement.getID());
        Mono<ContractAgreementRevision> contractAgreementRevisionMono = contractService.getContractAgreementRevisionById(
            contractAgreement.getID()
        );
        return Mono
            .zip(contractAgreementMono, contractAgreementRevisionMono)
            .flatMap(
                data -> {
                    ContractAgreement contractAgreement1 = data.getT1();
                    ContractAgreementRevision contractAgreementRevision = data.getT2();
                    ContractAgreement rollBackContractAgreement = null;

                    for (ContractAgreement contractAgreement2 : contractAgreementRevision.contractAgreementRevisions) {
                        if (contractAgreement2.getVersion() == contractAgreement.getVersion()) {
                            rollBackContractAgreement = contractAgreement2;
                            break;
                        }
                    }
                    if (rollBackContractAgreement != null) {
                        ContractAgreement finalRollBackContractAgreement = rollBackContractAgreement;
                        return addContractAgreementVersion(contractAgreement1)
                            .then(
                                logContractAgreementHistory(
                                    contractAgreement1,
                                    "Agreement version rolled back from " +
                                    contractAgreement1.getVersion() +
                                    " to " +
                                    rollBackContractAgreement.getVersion()
                                )
                                    .flatMap(
                                        contractAgreement2 -> {
                                            contractAgreement2.incrementVersion();
                                            finalRollBackContractAgreement.setVersion(contractAgreement2.getVersion());
                                            finalRollBackContractAgreement.setHistory(contractAgreement2.getHistory());
                                            finalRollBackContractAgreement.setUpdatedOn(Instant.now());
                                            return contractService.updateAgreement(finalRollBackContractAgreement);
                                        }
                                    )
                            )
                            .then(
                                Mono.just(
                                    new ResponseEntity<>(
                                        new ResponseMessage("Agreement Version is rolled back successfully"),
                                        HttpStatus.OK
                                    )
                                )
                            )
                            .onErrorReturn(
                                new ResponseEntity<>(
                                    new ResponseMessage("Could not roll back Agreement Version"),
                                    HttpStatus.EXPECTATION_FAILED
                                )
                            );
                    } else {
                        return Mono.just(
                            new ResponseEntity<>(
                                new ResponseMessage("Could not roll back Agreement Version"),
                                HttpStatus.EXPECTATION_FAILED
                            )
                        );
                    }
                }
            );
    }

    @GetMapping("test")
    public Mono<EntityStatus> updateAgreement() {
        //        return logContractTypeHistory(new ContractAgreement(),"test").flatMap(user -> {
        //            log.debug("==================="+user);
        //            return Mono.just(user);
        //        });
        return getUser()
            .flatMap(
                user -> {
                    log.debug("34543" + user);
                    return Mono.just(user);
                }
            )
            .thenReturn(EntityStatus.CREATED);
    }

    @PutMapping("/agreements")
    public Mono<ResponseEntity<ResponseMessage>> updateAgreement(@RequestBody ContractAgreementForm contractAgreementForm) {
        try {
            log.debug("Updating agreement id:" + contractAgreementForm.getId());
            log.debug("Updating agreement id:" + contractAgreementForm.getNotificationConfig().getUserConfig());

            if (contractAgreementForm.getGenNewDocToUpdate()) {
                log.debug("Generating new document");
                return generateContractAgreement(contractAgreementForm)
                    .flatMap(
                        contractAgreementForm1 ->
                            addVersionAndUpdateAgreement(contractAgreementForm1)
                                .thenReturn(
                                    new ResponseEntity<>(
                                        new ResponseMessage("Contract Agreement updation initiated successfully"),
                                        HttpStatus.OK
                                    )
                                )
                    );
            } else {
                return addVersionAndUpdateAgreement(contractAgreementForm)
                    .thenReturn(
                        new ResponseEntity<>(new ResponseMessage("Contract Agreement updation initiated successfully"), HttpStatus.OK)
                    );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not save Contract Agreement"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    private Mono<ContractAgreement> _addAgreement(ContractAgreementForm contractAgreementForm) {
        try {
            ContractAgreement contractAgreement = new ContractAgreement(contractAgreementForm);
            contractAgreement.setUpdatedOn(Instant.now());
            contractAgreement.setCreatedBy(contractAgreement.getTeam().getPrimaryOwner().getFirstName());
            contractAgreement.getHistory().add(new EventHistory("Contract Agreement Created", "", Instant.now()));
            return contractService.addAgreement(contractAgreement);
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(null);
        }
    }

    private Mono<ContractAgreement> _updateAgreement(ContractAgreementForm contractAgreementForm) {
        log.debug("Enter: _updateAgreement" + contractAgreementForm);

        try {
            return contractService
                .getAgreementById(contractAgreementForm.getId())
                .flatMap(
                    contractAgreement1 -> {
                        if (contractAgreement1 != null) {
                            contractAgreement1.setContractCategory(contractAgreementForm.getContractCategory());
                            contractAgreement1.setContractType(contractAgreementForm.getContractType());
                            contractAgreement1.setAgreementName(contractAgreementForm.getAgreementName());
                            contractAgreement1.setAgreementDescription(contractAgreementForm.getAgreementDescription());
                            contractAgreement1.setAttributes(contractAgreementForm.getAttributes());
                            contractAgreement1.setUpdatedOn(Instant.now());
                            contractAgreement1.setTemplate(contractAgreementForm.getTemplate());
                            contractAgreement1.setFilePath(contractAgreementForm.getFilePath());
                            if (contractAgreementForm.getAgreementFile() != null) contractAgreement1.setAgreementFile(
                                contractAgreementForm.getAgreementFile()
                            );
                            contractAgreement1.setTeam(contractAgreementForm.getTeam());
                            contractAgreement1.setTeamGroups(contractAgreementForm.getTeamGroups());
                            contractAgreement1.incrementVersion();
                            contractAgreement1.setNotificationConfig(contractAgreementForm.getNotificationConfig());
                            return handleContractAgreementWorkflow(contractAgreement1, EntityStatus.DRAFT)
                                .doOnSuccess(contractAgreement1::setAgreementStatus)
                                .then(contractService.updateAgreement(contractAgreement1));
                        } else {
                            return Mono.just(null);
                        }
                    }
                );
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(null);
        }
    }

    private Mono<Boolean> addVersionAndUpdateTemplate(ContractTemplateForm contractTemplateForm) {
        try {
            Mono<ContractTemplate> contractTemplateMono = contractService.getTemplateById(contractTemplateForm.getId());
            return Mono
                .from(contractTemplateMono)
                .flatMap(
                    contractTemplate ->
                        addContractTemplateVersion(contractTemplate).then(_updateTemplate(contractTemplateForm)).then(Mono.just(true))
                );
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(false);
        }
    }

    private Mono<Boolean> addVersionAndUpdateClause(ContractClauseForm contractClauseForm) {
        try {
            Mono<ContractClause> contractClauseMono = contractService.getClauseById(contractClauseForm.getId());

            return Mono
                .from(contractClauseMono)
                .flatMap(
                    contractClause -> addContractClauseVersion(contractClause).then(_updateClause(contractClauseForm)).then(Mono.just(true))
                );
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(false);
        }
    }

    private Mono<ContractAgreement> addVersionAndUpdateAgreement(ContractAgreementForm contractAgreementForm) {
        try {
            log.debug("Enter: addVersionAndUpdateAgreement with" + contractAgreementForm);
            Mono<ContractAgreement> contractAgreementMono = contractService.getAgreementById(contractAgreementForm.getId());

            return Mono
                .from(contractAgreementMono)
                .flatMap(
                    contractAgreement ->
                        addContractAgreementVersion(contractAgreement)
                            .flatMap(contractAgreementRevision -> _updateAgreement(contractAgreementForm))
                );
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(null);
        }
    }

    private Mono<ContractTemplate> _updateTemplate(ContractTemplateForm contractTemplateForm) {
        if (contractTemplateForm != null) {
            return contractService
                .getTemplateById(contractTemplateForm.getId())
                .flatMap(
                    contractTemplate ->
                        logContractTemplateHistory(contractTemplate, "Contract Template Modified")
                            .flatMap(
                                contractTemplate1 -> {
                                    if (contractTemplateForm.getTemplateFile() != null) contractTemplate1.setTemplateFile(
                                        contractTemplateForm.getTemplateFile()
                                    );
                                    contractTemplate1.setLanguage(contractTemplateForm.getLanguage());
                                    contractTemplate1.setSecondaryLanguage(contractTemplateForm.getSecondaryLanguage());
                                    contractTemplate1.setDateFormat(contractTemplateForm.getDateFormat());
                                    contractTemplate1.setEffectiveFrom(contractTemplateForm.getEffectiveFrom());
                                    contractTemplate1.setEffectiveTo(contractTemplateForm.getEffectiveTo());
                                    contractTemplate1.setStatus(contractTemplateForm.getStatus());
                                    contractTemplate1.setPrimary(contractTemplateForm.getPrimary());
                                    contractTemplate1.setDescription(contractTemplateForm.getDescription());
                                    contractTemplate1.incrementVersion();
                                    contractTemplate1.setTeam(contractTemplateForm.getTeam());
                                    contractTemplate1.setUpdatedOn(Instant.now());
                                    return contractService.updateTemplate(contractTemplate1);
                                }
                            )
                );
        } else {
            return Mono.just(null);
        }
    }

    private Mono<ContractTemplateRevision> addContractTemplateVersion(ContractTemplate contractTemplate) {
        return contractService
            .getContractTemplateRevisionById(contractTemplate.getID())
            .flatMap(
                contractTemplateRevision -> {
                    //                contractTemplateRevision.setId(contractTemplate.getID());
                    contractTemplateRevision.setVersion(contractTemplate.getVersion());
                    contractTemplateRevision.addContractTemplateRevisions(contractTemplate);
                    return contractService.updateContractTemplateRevision(contractTemplateRevision);
                }
            )
            .switchIfEmpty(
                Mono.defer(
                    () -> {
                        ContractTemplateRevision contractTemplateRevision = new ContractTemplateRevision(
                            contractTemplate.getID(),
                            contractTemplate.getVersion()
                        );
                        contractTemplateRevision.addContractTemplateRevisions(contractTemplate);
                        return contractService.addContractTemplateRevision(contractTemplateRevision);
                    }
                )
            );
    }

    private Mono<ContractClauseRevision> addContractClauseVersion(ContractClause contractClause) {
        return contractService
            .getContractClauseRevisionById(contractClause.getID())
            .flatMap(
                contractClauseRevision -> {
                    //            contractClauseRevision.setId(contractClause.getID());
                    contractClauseRevision.setVersion(contractClause.getVersion());
                    contractClauseRevision.addContractClauseRevisions(contractClause);
                    return contractService.updateContractClauseRevision(contractClauseRevision);
                }
            )
            .switchIfEmpty(
                Mono.defer(
                    () -> {
                        ContractClauseRevision contractClauseRevision = new ContractClauseRevision(
                            contractClause.getID(),
                            contractClause.getVersion()
                        );
                        contractClauseRevision.addContractClauseRevisions(contractClause);
                        return contractService.addContractClauseRevision(contractClauseRevision);
                    }
                )
            );
    }

    private Mono<ContractAgreementRevision> addContractAgreementVersion(ContractAgreement contractAgreement) {
        return contractService
            .getContractAgreementRevisionById(contractAgreement.getID())
            .flatMap(
                contractAgreementRevision -> {
                    contractAgreementRevision.setVersion(contractAgreement.getVersion());
                    contractAgreementRevision.addContractAgreementRevisions(contractAgreement);
                    return contractService.updateContractAgreementRevision(contractAgreementRevision);
                }
            )
            .switchIfEmpty(
                Mono.defer(
                    () -> {
                        ContractAgreementRevision contractAgreementRevision = new ContractAgreementRevision(
                            contractAgreement.getID(),
                            contractAgreement.getVersion()
                        );
                        contractAgreementRevision.addContractAgreementRevisions(contractAgreement);
                        return contractService.addContractAgreementRevision(contractAgreementRevision);
                    }
                )
            );
    }

    //    private Mono<Boolean> _updateClause_1(ContractClauseForm contractClauseForm) {
    //        try {
    //            Mono<ContractClause> contractClauseMono = contractService.getClauseById(contractClauseForm.getId());
    //            Mono<ContractClause> updateClauseMono = Mono.from(contractClauseMono).flatMap(contractClause1 ->{
    //                if (contractClause1!=null){
    //                    contractClause1.setClauseFile(contractClauseForm.getClauseFile());
    //                    contractClause1.setLanguage(contractClauseForm.getLanguage());
    //                    contractClause1.setStatus(contractClauseForm.getStatus());
    //                    contractClause1.setDescription(contractClauseForm.getDescription());
    //                    contractClause1.setVersion(contractClauseForm.getVersion());
    //                    contractClause1.setVersionComments(contractClauseForm.getVersionComments());
    //                    contractClause1.setTeam(contractClauseForm.getTeam());
    //                    contractClause1.setUpdatedOn(Instant.now());
    //                    return contractService.updateClause(contractClause1);
    //                }else{
    //                    return Mono.just(null);
    //                }
    //            });
    //            return Mono.from(updateClauseMono).flatMap(updatedTemplate -> Mono.just(true)).onErrorReturn(false);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            return Mono.just(false);
    //        }
    //    }

    private Mono<ContractClause> _updateClause(ContractClauseForm contractClauseForm) {
        if (contractClauseForm != null) {
            return contractService
                .getClauseById(contractClauseForm.getId())
                .flatMap(
                    contractClause ->
                        logContractClauseHistory(contractClause, "Contract Clause Modified")
                            .flatMap(
                                contractClause1 -> {
                                    if (contractClauseForm.getClauseFile() != null) contractClause1.setClauseFile(
                                        contractClauseForm.getClauseFile()
                                    );
                                    contractClause1.setLanguage(contractClauseForm.getLanguage());
                                    contractClause1.setStatus(contractClauseForm.getStatus());
                                    contractClause1.setDescription(contractClauseForm.getDescription());
                                    //                contractClause1.setVersion(contractClauseForm.getVersion());
                                    contractClause1.setTeam(contractClauseForm.getTeam());
                                    contractClause1.setUpdatedOn(Instant.now());
                                    contractClause1.incrementVersion();
                                    return contractService.updateClause(contractClause1);
                                }
                            )
                );
        } else {
            return Mono.just(null);
        }
    }

    @DeleteMapping("/agreements/{agreementName}")
    public Mono<ResponseEntity<ResponseMessage>> deleteAgreement(@PathVariable("agreementName") String agreementName) {
        try {
            Mono<Boolean> deleteAgreementMono = contractService
                .getAgreementByName(agreementName)
                .flatMap(
                    contractAgreement -> {
                        if (contractAgreement != null) contractService.deleteAgreement(contractAgreement).thenReturn(Mono.just(true));
                        return Mono.just(false);
                    }
                );
            return Mono
                .from(deleteAgreementMono)
                .flatMap(
                    deleteResult -> {
                        if (deleteResult) return Mono.just(
                            new ResponseEntity<>(new ResponseMessage("Contract Agreement deleted successfully"), HttpStatus.OK)
                        ); else return Mono.just(
                            new ResponseEntity<>(
                                new ResponseMessage("Could not delete Contract Agreement, Contract Agreement not present "),
                                HttpStatus.EXPECTATION_FAILED
                            )
                        );
                    }
                )
                .onErrorReturn(
                    new ResponseEntity<>(new ResponseMessage("Could not delete Contract Agreement"), HttpStatus.EXPECTATION_FAILED)
                );
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(
                new ResponseEntity<>(new ResponseMessage("Could not delete Contract Agreement"), HttpStatus.EXPECTATION_FAILED)
            );
        }
        //        String message = "";
        //        try {
        //            ContractAgreement contractAgreement = contractService.getAgreementByName(agreementName).block();
        //            if (contractAgreement == null)
        //                throw new IllegalArgumentException("Contract Agreement not present");
        //            contractService.deleteAgreement(contractAgreement);
        //            message = "Contract Agreement deleted successfully";
        //            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        //        } catch (Exception e) {
        //            message = "Could not delete Contract Agreement";
        //            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        //        }
    }

    @PostMapping("/agreements/signature")
    public Mono<ResponseEntity<ResponseMessage>> sendAgreementForSignature(
        @RequestBody SendForSignatureForm sendForSignatureForm,
        WebSession session
    ) {
        String message = "";

        try {
            //            ApiService apiService = getApiService(session,"EsignApiService");

            Mono<ContractAgreement> contractAgreementMono = contractService
                .getAgreementById(sendForSignatureForm.getAgreementId())
                .onErrorReturn(null);
            Mono<ApiService> apiServiceMono = getApiService(session, "EsignApiService").onErrorReturn(null);

            return Mono
                .zip(contractAgreementMono, apiServiceMono)
                .flatMap(
                    data -> {
                        ContractAgreement contractAgreement = data.getT1();
                        ApiService apiService = data.getT2();
                        if (apiService != null) {
                            String esignAccessToken = getAccessTokenFromRefreshToken(apiService);

                            String authorization = BEARER + esignAccessToken;

                            ApiClient apiClient;
                            try {
                                apiClient = getAppClient(authorization);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return Mono.just(
                                    new ResponseEntity<>(
                                        new ResponseMessage("Could not send Agreement for signature"),
                                        HttpStatus.EXPECTATION_FAILED
                                    )
                                );
                            }

                            //                    String filePath = "./src/main/webapp/content/";
                            String fileName = session.getAttribute("downloadedAgreement") + ".pdf";
                            File agreementFile = new File("./src/main/webapp/content/" + fileName);
                            String xApiUser = null;
                            String xOnBehalfOfUser = null;
                            String mimeType = "application/pdf";
                            //            String mimeType ="application/vnd.openxmlformats-officedocument.wordprocessingml.document";

                            //            Get the id of the transient document.
                            TransientDocumentsApi transientDocumentsApi = new TransientDocumentsApi(apiClient);
                            TransientDocumentResponse response = null;
                            try {
                                response =
                                    transientDocumentsApi.createTransientDocument(
                                        authorization,
                                        agreementFile,
                                        xApiUser,
                                        xOnBehalfOfUser,
                                        fileName,
                                        mimeType
                                    );
                            } catch (ApiException e) {
                                e.printStackTrace();
                                return Mono.just(
                                    new ResponseEntity<>(
                                        new ResponseMessage("Could not send Agreement for signature"),
                                        HttpStatus.EXPECTATION_FAILED
                                    )
                                );
                            }
                            String transientDocumentId = response.getTransientDocumentId();

                            //            String transientDocumentId = "3AAABLblqZhC6srHZDJIRehNujpO9TZP2j1YYCS2sU4rAnMyhf0KT-vLyxx9qDv-0iII70J2X0zyWwA9zg26GbhW3ogEzTnHHvm_diBqAqBDP5X3C6eaRZnDltLfmSRT9vXutN4soZI80plcb235tMH2UK8rhCYLvvu00reQs9FBPnHmKO0F3MsA5DZC3O7Ln9GeN7lZ8Pmm0eBiwRuDee_h5dksUvENP3RFFRBRoXLBFrKqszZ3x0vxLS4tTTv8FLzeoD66NBH7IB9AZAUT188U3qqNOix8uH6v2xEdT73Lyt-EhlWHpP6zafYadGwWB-5j2P49x5IZ07zS5FRdmPMSVYE1BOF7UpSSK087fGg5uJHx414nYvw**";

                            //prepare request body for agreement creation.
                            AgreementCreationInfo agreementCreationInfo = new AgreementCreationInfo();
                            agreementCreationInfo.setName(contractAgreement.getAgreementName());
                            agreementCreationInfo.setSignatureType(AgreementCreationInfo.SignatureTypeEnum.ESIGN);
                            agreementCreationInfo.setState(AgreementCreationInfo.StateEnum.IN_PROCESS);
                            io.swagger.client.model.agreements.FileInfo fileInfo = new io.swagger.client.model.agreements.FileInfo();
                            fileInfo.setTransientDocumentId(transientDocumentId);
                            agreementCreationInfo.addFileInfosItem(fileInfo);
                            agreementCreationInfo.setMessage(sendForSignatureForm.getAgreementSignMessage());
                            //TODO : Provide email of recipient to whom agreement will be sent
                            int signOrder = 0;
                            for (Recipient recipient : sendForSignatureForm.getRecipients()) {
                                ParticipantSetInfo participantSetInfo = new ParticipantSetInfo();
                                ParticipantSetMemberInfo participantSetMemberInfo = new ParticipantSetMemberInfo();
                                participantSetMemberInfo.setEmail(recipient.getEmail());
                                if (sendForSignatureForm.isSignOrderRequired()) {
                                    participantSetInfo.setOrder(++signOrder);
                                } else {
                                    participantSetInfo.setOrder(1);
                                }
                                participantSetInfo.setRole(ParticipantSetInfo.RoleEnum.SIGNER);
                                participantSetInfo.addMemberInfosItem(participantSetMemberInfo);
                                agreementCreationInfo.addParticipantSetsInfoItem(participantSetInfo);
                            }
                            //Create agreement using the transient document.
                            AgreementsApi agreementsApi = new AgreementsApi(apiClient);
                            AgreementCreationResponse agreementCreationResponse = null;
                            try {
                                agreementCreationResponse =
                                    agreementsApi.createAgreement(authorization, agreementCreationInfo, xApiUser, xOnBehalfOfUser);
                            } catch (ApiException e) {
                                e.printStackTrace();
                                return Mono.just(
                                    new ResponseEntity<>(
                                        new ResponseMessage("Could not send Agreement for signature"),
                                        HttpStatus.EXPECTATION_FAILED
                                    )
                                );
                            }
                            String signAgreementId = agreementCreationResponse.getId();
                            //            String signAgreementId = "777777777";
                            System.out.println("Agreement ID = " + signAgreementId);

                            contractAgreement.setAgreementStatus(EntityStatus.SENT_FOR_SIGNATURE);
                            //contractAgreement
                            //    .getAuditLogs()
                            //    .add(new AuditEvent("Agreement send for Signature", contractAgreement.getFilePath(), new Date()));
                            contractAgreement.setSignAgreementId(signAgreementId);

                            //                    Mono<ContractAgreement> updateAgreementMono = contractService.getAgreementByName(sendForSignatureForm.getAgreement().getAgreementName()).flatMap(contractAgreement ->{
                            //                        if (contractAgreement!=null){
                            //                            contractAgreement.setAgreementStatus(AgreementStatus.SEND_FOR_SIGNATURE);
                            //                            contractAgreement.getAuditLogs().add(new AuditEvent("Agreement send for Signature", contractAgreement.getFilePath(), new Date()));
                            //                            contractAgreement.setSignAgreementId(signAgreementId);
                            //                            return contractService.updateAgreement(contractAgreement);
                            //                        }
                            //                        return Mono.just(null);
                            //                    });
                            return Mono
                                .from(contractService.updateAgreement(contractAgreement))
                                .flatMap(
                                    updatedAgreement -> {
                                        if (updatedAgreement != null) return Mono.just(
                                            new ResponseEntity<>(new ResponseMessage("Agreement send for signature"), HttpStatus.OK)
                                        ); else return Mono.just(
                                            new ResponseEntity<>(
                                                new ResponseMessage("Could not send Agreement for signature"),
                                                HttpStatus.EXPECTATION_FAILED
                                            )
                                        );
                                    }
                                )
                                .onErrorReturn(
                                    new ResponseEntity<>(
                                        new ResponseMessage("Could not send Agreement for signature"),
                                        HttpStatus.EXPECTATION_FAILED
                                    )
                                );
                        } else {
                            return Mono.just(
                                new ResponseEntity<>(
                                    new ResponseMessage("Could not send Agreement for signature"),
                                    HttpStatus.EXPECTATION_FAILED
                                )
                            );
                        }
                    }
                );
            //         return getApiService(session,"EsignApiService")
            //               .onErrorReturn(null)
            //               .flatMap(apiService ->{
            //                if (apiService!=null) {
            //                    String esignAccessToken = getAccessTokenFromRefreshToken(apiService);
            //
            //                    String authorization = BEARER + esignAccessToken;
            //
            //                    ApiClient apiClient;
            //                    try {
            //                        apiClient = getAppClient(authorization);
            //                    } catch (Exception e) {
            //                        e.printStackTrace();
            //                        return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not send Agreement for signature"), HttpStatus.EXPECTATION_FAILED));
            //                    }
            //
            //
            //                    String filePath = "./src/main/webapp/content/";
            //                    String fileName = "temp_agreement.pdf";
            //                    File file = new File(filePath + fileName);
            //                    String xApiUser = null;
            //                    String xOnBehalfOfUser = null;
            //                    String mimeType = "application/pdf";
            ////            String mimeType ="application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            //
            ////            Get the id of the transient document.
            //                    TransientDocumentsApi transientDocumentsApi = new TransientDocumentsApi(apiClient);
            //                    TransientDocumentResponse response = null;
            //                    try {
            //                        response = transientDocumentsApi.createTransientDocument(authorization, file, xApiUser, xOnBehalfOfUser, fileName, mimeType);
            //                    } catch (ApiException e) {
            //                        e.printStackTrace();
            //                        return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not send Agreement for signature"), HttpStatus.EXPECTATION_FAILED));
            //                    }
            //                    String transientDocumentId = response.getTransientDocumentId();
            //
            ////            String transientDocumentId = "3AAABLblqZhC6srHZDJIRehNujpO9TZP2j1YYCS2sU4rAnMyhf0KT-vLyxx9qDv-0iII70J2X0zyWwA9zg26GbhW3ogEzTnHHvm_diBqAqBDP5X3C6eaRZnDltLfmSRT9vXutN4soZI80plcb235tMH2UK8rhCYLvvu00reQs9FBPnHmKO0F3MsA5DZC3O7Ln9GeN7lZ8Pmm0eBiwRuDee_h5dksUvENP3RFFRBRoXLBFrKqszZ3x0vxLS4tTTv8FLzeoD66NBH7IB9AZAUT188U3qqNOix8uH6v2xEdT73Lyt-EhlWHpP6zafYadGwWB-5j2P49x5IZ07zS5FRdmPMSVYE1BOF7UpSSK087fGg5uJHx414nYvw**";
            //
            //                    //prepare request body for agreement creation.
            //                    AgreementCreationInfo agreementCreationInfo = new AgreementCreationInfo();
            //                    agreementCreationInfo.setName(sendForSignatureForm.getAgreement().getAgreementName());
            //                    agreementCreationInfo.setSignatureType(AgreementCreationInfo.SignatureTypeEnum.ESIGN);
            //                    agreementCreationInfo.setState(AgreementCreationInfo.StateEnum.IN_PROCESS);
            //                    io.swagger.client.model.agreements.FileInfo fileInfo = new io.swagger.client.model.agreements.FileInfo();
            //                    fileInfo.setTransientDocumentId(transientDocumentId);
            //                    agreementCreationInfo.addFileInfosItem(fileInfo);
            //                    agreementCreationInfo.setMessage(sendForSignatureForm.getAgreementSignMessage());
            //                    //TODO : Provide email of recipient to whom agreement will be sent
            //                    int signOrder = 0;
            //                    for (Recipient recipient:
            //                        sendForSignatureForm.getRecipients()) {
            //                        ParticipantSetInfo participantSetInfo = new ParticipantSetInfo();
            //                        ParticipantSetMemberInfo participantSetMemberInfo = new ParticipantSetMemberInfo();
            //                        participantSetMemberInfo.setEmail(recipient.getEmail());
            //                        if (sendForSignatureForm.isSignOrderRequired()){
            //                            participantSetInfo.setOrder(++signOrder);
            //                        }else{
            //                            participantSetInfo.setOrder(1);
            //                        }
            //                        participantSetInfo.setRole(ParticipantSetInfo.RoleEnum.SIGNER);
            //                        participantSetInfo.addMemberInfosItem(participantSetMemberInfo);
            //                        agreementCreationInfo.addParticipantSetsInfoItem(participantSetInfo);
            //                    }
            //                    //Create agreement using the transient document.
            //                    AgreementsApi agreementsApi = new AgreementsApi(apiClient);
            //                    AgreementCreationResponse agreementCreationResponse = null;
            //                    try {
            //                        agreementCreationResponse = agreementsApi.createAgreement(authorization, agreementCreationInfo, xApiUser, xOnBehalfOfUser);
            //                    } catch (ApiException e) {
            //                        e.printStackTrace();
            //                        return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not send Agreement for signature"), HttpStatus.EXPECTATION_FAILED));
            //                    }
            //                    String signAgreementId = agreementCreationResponse.getId();
            ////            String signAgreementId = "777777777";
            //                    System.out.println("Agreement ID = " + signAgreementId);
            //
            //                    Mono<ContractAgreement> updateAgreementMono = contractService.getAgreementByName(sendForSignatureForm.getAgreement().getAgreementName()).flatMap(contractAgreement ->{
            //                        if (contractAgreement!=null){
            //                            contractAgreement.setAgreementStatus(AgreementStatus.SEND_FOR_SIGNATURE);
            //                            contractAgreement.getAuditLogs().add(new AuditEvent("Agreement send for Signature", contractAgreement.getFilePath(), new Date()));
            //                            contractAgreement.setSignAgreementId(signAgreementId);
            //                            return contractService.updateAgreement(contractAgreement);
            //                        }
            //                        return Mono.just(null);
            //                    });
            //                    return Mono.from(updateAgreementMono).flatMap(contractAgreement ->{
            //                        if (contractAgreement != null)
            //                            return Mono.just(new ResponseEntity<>(new ResponseMessage("Agreement send for signature"), HttpStatus.OK));
            //                        else
            //                            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not send Agreement for signature"), HttpStatus.EXPECTATION_FAILED));
            //                    }).onErrorReturn(new ResponseEntity<>(new ResponseMessage("Could not send Agreement for signature"), HttpStatus.EXPECTATION_FAILED));
            //                }else{
            //                    return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not send Agreement for signature"), HttpStatus.EXPECTATION_FAILED));
            //                }
            //            });

            //            ContractAgreement contractAgreement = contractService.getAgreementByName(sendForSignatureForm.getAgreement().getAgreementName()).block();
            //            if (contractAgreement!=null){
            //                contractAgreement.setAgreementStatus(AgreementStatus.SEND_FOR_SIGNATURE);
            //                contractAgreement.getAuditLogs().add(new AuditEvent("Agreement send for Signature", contractAgreement.getFilePath(), new Date()));
            //                contractAgreement.setSignAgreementId(signAgreementId);
            //                contractService.updateAgreement(contractAgreement);
            //            }
            //            message = "Agreement send for signature";
            //            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
            //            message = "Could not send Agreement for signature";
            return Mono.just(
                new ResponseEntity<>(new ResponseMessage("Could not send Agreement for signature"), HttpStatus.EXPECTATION_FAILED)
            );
        }
    }

    private Mono<InputStream> getTemplateStream1(String templateId) {
        return contractService
            .getTemplateById(templateId)
            .onErrorReturn(null)
            .flatMap(
                contractTemplate -> {
                    XWPFDocument templateDoc = null;
                    System.out.println("Test 1 .....");
                    return Mono.just(new ByteArrayInputStream(contractTemplate.getTemplateFile().getData()));
                }
            );
        //        return  Mono.from(clauseTagMono).flatMap(templateDoc->{
        //            System.out.println("Test 2 .....");
        //            ByteArrayOutputStream b = new ByteArrayOutputStream();
        //            try {
        //                templateDoc.write(b); // doc should be a XWPFDocument
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //            InputStream inputStream = new ByteArrayInputStream(b.toByteArray());
        //            return Mono.just(inputStream);
        //        });

    }

    private Mono<InputStream> getTemplateStream(String templateId) {
        Mono<XWPFDocument> clauseTagMono = contractService
            .getTemplateById(templateId)
            .onErrorReturn(null)
            .flatMap(
                contractTemplate -> {
                    XWPFDocument templateDoc = null;
                    ByteArrayInputStream srcTemplateStream;
                    if (this.fileStorageCurrent.equals(EXTERNAL)) srcTemplateStream =
                        (ByteArrayInputStream) fileHostDownload(contractTemplate.getFilePath()).block(); else srcTemplateStream =
                        new ByteArrayInputStream(contractTemplate.getTemplateFile().getData());
                    try {
                        templateDoc = new XWPFDocument(srcTemplateStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (templateDoc != null) {
                        ArrayList<XWPFParagraph> clauseTags = new ArrayList<>();
                        Map<XWPFParagraph, List<XWPFParagraph>> clauseParagraphs = new HashMap<>();
                        //                    ArrayList<String> clauseParagraphs = new ArrayList<>();
                        for (XWPFParagraph templatePara : templateDoc.getParagraphs()) {
                            if (templatePara.getText().contains("{{Attributes.`CM_CLAUSE") && templatePara.getText().contains("`}}")) {
                                clauseTags.add(templatePara);
                                String clauseTagStr = templatePara.getParagraphText();
                                String clauseId = clauseTagStr.substring(
                                    clauseTagStr.indexOf("{{Attributes.`CM_CLAUSE_") + "{{Attributes.`CM_CLAUSE_".length(),
                                    clauseTagStr.indexOf("`}}")
                                );
                                contractService
                                    .getClauseById(clauseId)
                                    .onErrorReturn(null)
                                    .flatMap(
                                        contractClause -> {
                                            XWPFDocument clauseDoc = null;
                                            try {
                                                clauseDoc =
                                                    new XWPFDocument(new ByteArrayInputStream(contractClause.getClauseFile().getData()));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            if (clauseDoc != null) {
                                                //                                        ArrayList<XWPFParagraph> clauseParas = new ArrayList<>();
                                                //                                        for (XWPFParagraph clausePara : clauseDoc.getParagraphs()) {
                                                //                                            clauseParas.add(clausePara);
                                                //                                        }
                                                clauseParagraphs.put(templatePara, clauseDoc.getParagraphs());
                                            }
                                            return Mono.just(contractClause);
                                        }
                                    )
                                    .block();
                            }
                        }

                        for (XWPFParagraph clauseTag : clauseTags) {
                            replaceParagraphWithParagraphs(clauseTag, clauseParagraphs.get(clauseTag));
                        }
                    }
                    return Mono.just(templateDoc);
                }
            );

        return Mono
            .from(clauseTagMono)
            .flatMap(
                templateDoc -> {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    try {
                        templateDoc.write(b); // doc should be a XWPFDocument
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    InputStream inputStream = new ByteArrayInputStream(b.toByteArray());
                    return Mono.just(inputStream);
                }
            );
    }

    private void replaceParagraphWithParagraphs(XWPFParagraph clauseTag, List<XWPFParagraph> replaceParagraphs) {
        if (clauseTag != null) {
            XWPFDocument srcDoc = clauseTag.getDocument();
            XmlCursor cursor = clauseTag.getCTP().newCursor();
            for (XWPFParagraph replacePara : replaceParagraphs) {
                XWPFParagraph newP = srcDoc.createParagraph();
                newP.getCTP().setPPr(clauseTag.getCTP().getPPr());
                XWPFRun newR = newP.createRun();
                newR.getCTR().setRPr(clauseTag.getRuns().get(0).getCTR().getRPr());
                newR.setText(replacePara.getText());
                XmlCursor c2 = newP.getCTP().newCursor();
                c2.moveXml(cursor);
                c2.dispose();
            }
            cursor.removeXml(); // Removes replacement text paragraph
            cursor.dispose();
        }
    }

    private Mono<Boolean> updateOauthRefreshToken(ApiService apiService) {
        try {
            // Create header list.
            HashMap<String, String> headers = new HashMap();
            headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), RestApiUtils.MimeType.FORM.toString());
            // URL for token endpoint.
            String url = apiService.getAccessTokenEndPoint();
            String requestBody =
                "code=" +
                apiService.getAuthorizationCode() +
                "&client_id=" +
                apiService.getClientId() +
                "&client_secret=" +
                apiService.getClientSecret() +
                "&redirect_uri=" +
                apiService.getRedirectUri() +
                "&grant_type=authorization_code";

            JSONObject responseJSON = null;
            responseJSON = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.POST, headers, requestBody);
            System.out.println("Esign OAuth Access Token : " + responseJSON.get("access_token"));
            System.out.println("Esign OAuth Refresh Token : " + responseJSON.get("refresh_token"));

            if (responseJSON.get("refresh_token") != null) {
                apiService.setRefreshToken((String) responseJSON.get("refresh_token"));
                apiService.setAuthorizationCode("");
                Mono<ApiService> updateApiServiceMono = contractService.updateApiService(apiService);
                return Mono
                    .from(updateApiServiceMono)
                    .flatMap(
                        updatedApiService -> {
                            if (updatedApiService != null) return Mono.just(true); else return Mono.just(false);
                        }
                    );
            } else return Mono.just(false);
        } catch (IOException e) {
            return Mono.just(false);
        }
    }

    private String getAccessTokenFromRefreshToken(ApiService apiService) {
        try {
            // Create header list.
            HashMap<String, String> headers = new HashMap<>();
            headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), RestApiUtils.MimeType.FORM.toString());
            // URL for token endpoint.
            String url = apiService.getRefreshTokenEndPoint();
            String requestBody =
                "refresh_token=" +
                apiService.getRefreshToken() +
                "&client_id=" +
                apiService.getClientId() +
                "&client_secret=" +
                apiService.getClientSecret() +
                "&grant_type=refresh_token";
            JSONObject responseJSON;
            responseJSON = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.POST, headers, requestBody);
            String accessToken = (String) responseJSON.get("access_token");
            System.out.println("Esign OAuth Access Token : " + accessToken);
            if (accessToken != null) {
                if (responseJSON.get("refresh_token") != null) {
                    apiService.setRefreshToken((String) responseJSON.get("refresh_token"));
                    contractService
                        .updateApiService(apiService)
                        .onErrorReturn(null)
                        .flatMap(
                            updatedApiService -> {
                                if (updatedApiService == null) System.out.println("Refresh token update on API Service failed");
                                return Mono.just(updatedApiService);
                            }
                        )
                        .subscribe();
                }
            }
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ApiClient getAppClient(String authorization) throws Exception {
        ApiClient apiClient = new ApiClient();
        //Default baseUrl to make GET /baseUris API call.
        apiClient.setBasePath(eSignBaseUrl + eSignEndpointUrl);
        //Get the baseUris for the user and set it in apiClient.
        BaseUrisApi baseUrisApi = new BaseUrisApi(apiClient);
        BaseUriInfo baseUriInfo = baseUrisApi.getBaseUris(authorization);
        apiClient.setBasePath(baseUriInfo.getApiAccessPoint() + eSignEndpointUrl);
        return apiClient;
    }

    //    private void generateAgreementDoc(ContractAgreement contractAgreement, ContractTemplate contractTemplate)  {
    //        try {
    ////            String reportFormat = "docx";
    //            Map<String, Object> templateParams = new HashMap<>();
    //            Set<ContractClause> contractClauses = contractClauseRepository.findByClauseNameIn(contractTemplate.getClauseNames());
    //            contractClauses.forEach((contractClause) -> {
    //                try {
    //                    GetMethod getMethod = new GetMethod( contractClause.getFilePath());
    //                    getNextCloudConnection().executeMethod(getMethod);
    //                    templateParams.put(contractClause.getClauseName(),JasperCompileManager.compileReport(getMethod.getResponseBodyAsStream()));
    //                } catch (IOException | JRException e) {
    //                    e.printStackTrace();
    //                }
    //            });
    //            HttpClient client = getNextCloudConnection();
    //            GetMethod getMethod = new GetMethod(contractTemplate.getFilePath()) ;
    //            client.executeMethod(getMethod);
    //
    //
    //            InputStream inputStream = getMethod.getResponseBodyAsStream();
    //            JasperReport templateReport = JasperCompileManager.compileReport(inputStream);
    //
    //            Collection<Map<String, ?>> attributeMapList = new ArrayList<>();
    //            Map<String, String> attributesMap = new HashMap();
    //            contractAgreement.getAttributes().forEach((Attribute attribute) -> {
    //                attributesMap.put(attribute.getAttributeName(), attribute.getAttributeValue());
    //            });
    //            attributeMapList.add(attributesMap);
    //            JRMapCollectionDataSource attributeJrMap = new JRMapCollectionDataSource(attributeMapList);
    //            JasperPrint jasperPrint = JasperFillManager.fillReport(templateReport,templateParams, attributeJrMap);
    //
    //            Exporter exporter = new JRDocxExporter();
    //            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
    //            File exportReportFile = new File("agreements.docx");
    //            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(exportReportFile));
    //            exporter.exportReport();
    //
    //            JasperExportManager.exportReportToPdfFile(jasperPrint, "agreements.pdf");
    //            JasperExportManager.exportReportToHtmlFile(jasperPrint, "agreements.html");
    //
    //            String contractTypePath = "/Documents/ContractManager/Agreements/"+(contractAgreement.getContractTypeName()).replaceAll(" ", "_").toLowerCase();
    //            if (!nextCloudHandler.pathExists(contractTypePath))
    //                nextCloudHandler.createFolder(contractTypePath);
    //
    //            String templatePath = contractTypePath+"/"+(contractTemplate.getTemplateName()).replaceAll(" ", "_").toLowerCase();
    //            if (!nextCloudHandler.pathExists(templatePath))
    //                   nextCloudHandler.createFolder(templatePath);
    //
    //            PutMethod putMethod1 = new PutMethod(NEXTCLOUD_BASE_URL +templatePath+"/"+(contractAgreement.getAgreementName()+".docx").replaceAll(" ", "_").toLowerCase());
    //            PutMethod putMethod2 = new PutMethod(NEXTCLOUD_BASE_URL +templatePath+"/"+(contractAgreement.getAgreementName()+".pdf").replaceAll(" ", "_").toLowerCase());
    //            PutMethod putMethod3 = new PutMethod(NEXTCLOUD_BASE_URL +templatePath+"/"+(contractAgreement.getAgreementName()+".html").replaceAll(" ", "_").toLowerCase());
    //            putMethod1.setRequestEntity(new InputStreamRequestEntity(new FileInputStream("agreements.docx")));
    //            putMethod2.setRequestEntity(new InputStreamRequestEntity(new FileInputStream("agreements.pdf")));
    //            putMethod3.setRequestEntity(new InputStreamRequestEntity(new FileInputStream("agreements.html")));
    //            HttpClient client1 = getNextCloudConnection();
    //            client1.executeMethod(putMethod1);
    //            HttpClient client2 = getNextCloudConnection();
    //            client2.executeMethod(putMethod2);
    //            HttpClient client3 = getNextCloudConnection();
    //            client3.executeMethod(putMethod3);
    //            System.out.println(putMethod1.getStatusCode() + " " + putMethod1.getStatusText());
    //            System.out.println(putMethod2.getStatusCode() + " " + putMethod2.getStatusText());
    //            System.out.println(putMethod3.getStatusCode() + " " + putMethod3.getStatusText());
    //        } catch (IOException | JRException e) {
    //            e.printStackTrace();
    //        }
    //    }
    public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
        String charset = "UTF-8";
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        //        BitMatrix bitMatrix =
        //                barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);
        BitMatrix bitMatrix = new MultiFormatWriter()
            .encode(new String(barcodeText.getBytes(charset), charset), BarcodeFormat.QR_CODE, 100, 100);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static void addQrCodeToAgreement(ContractAgreement contractAgreement) throws Exception {
        String src = "temp_agreement.pdf";
        String dest = "src\\main\\webapp\\content\\temp_agreement.pdf";
        Gson gson = new Gson();
        String jsonInString = gson.toJson(contractAgreement);
        ImageIO.write(generateQRCodeImage(jsonInString), "jpg", new File("qr_img.jpg"));
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        Image image = Image.getInstance("qr_img.jpg");
        PdfImage stream = new PdfImage(image, "", null);
        stream.put(new PdfName("ITXT_SpecialId"), new PdfName("123456789"));
        PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
        image.setDirectReference(ref.getIndirectReference());
        image.setAbsolutePosition(400, 600);
        PdfContentByte over = stamper.getOverContent(1);
        over.addImage(image);
        stamper.close();
        reader.close();
    }

    public static ByteArrayOutputStream addQrCode(byte[] pdfByteArray, String qrContent) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(generateQRCodeImage(qrContent), "jpg", new File("./src/main/webapp/content/qr_img.jpg"));
        PdfReader srcPdfReader = new PdfReader(pdfByteArray);
        PdfStamper pdfStamper = new PdfStamper(srcPdfReader, byteArrayOutputStream);
        Image QRimage = Image.getInstance("./src/main/webapp/content/qr_img.jpg");
        PdfImage pdfQrImage = new PdfImage(QRimage, "", null);
        pdfQrImage.put(new PdfName("ITXT_SpecialId"), new PdfName("123456789"));
        PdfIndirectObject ref = pdfStamper.getWriter().addToBody(pdfQrImage);
        QRimage.setDirectReference(ref.getIndirectReference());

        QRimage.scaleToFit(100, 100);
        float offsetX = (100 - QRimage.getScaledWidth()) / 2;
        float offsetY = (100 - QRimage.getScaledHeight()) / 2;
        QRimage.setAbsolutePosition(36 + offsetX, 36 + offsetY);

        //        QRimage.setAbsolutePosition(400, 600);
        PdfContentByte pdfStamperOverContent = pdfStamper.getOverContent(1);
        pdfStamperOverContent.addImage(QRimage);

        pdfStamper.close();
        srcPdfReader.close();
        return byteArrayOutputStream;
    }

    private HttpClient getNextCloudConnection() {
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials("admin", "admin");
        client.getState().setCredentials(AuthScope.ANY, creds);
        return client;
    }

    private Mono<UserTasks> addUserTask(UserTasks userTasks) {
        return userTasksRepository
            .findByUserId(userTasks.getUser().getId())
            .flatMap(
                userTasks1 -> {
                    userTasks1.getTasks().add(userTasks.getTasks().get(0));
                    return userTasksRepository.save(userTasks1);
                }
            )
            .switchIfEmpty(userTasksRepository.insert(userTasks));
    }

    private Mono<EntityStatus> handleContractAgreementWorkflow(ContractAgreement contractAgreement, EntityStatus status) {
        log.debug("Enter handleContractAgreementWorkflow");

        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(
                login -> {
                    switch (status) {
                        case CREATED:
                            log.debug("Handling CREATED workflow");

                            notificationsUtil
                                .createNotification(contractAgreement, Constants.NotificationType.CREATED, login)
                                .subscribe(
                                    cm -> log.debug("Created notification successfully"),
                                    err -> log.debug("Error occurred while creating notification" + err)
                                );

                            return logContractTypeHistory(contractAgreement, "Contract Agreement Created").thenReturn(EntityStatus.CREATED);
                        case APPROVED:
                            log.debug("Handling APPROVED workflow");

                            notificationsUtil
                                .createNotification(contractAgreement, Constants.NotificationType.APPROVED, login)
                                .subscribe(
                                    cm -> log.debug("Created notification successfully"),
                                    err -> log.debug("Error occurred while creating notification" + err)
                                );

                            return userTaskUtil
                                .approve(contractAgreement)
                                .flatMap(
                                    contractTypeStatus ->
                                        Mono
                                            .from(logContractTypeHistory(contractAgreement, "Contract Agreement Approved"))
                                            .thenReturn(contractTypeStatus)
                                );
                        case APPROVAL_PENDING:
                            log.debug("Handling APPROVAL_PENDING workflow");

                            userTaskUtil.sendEntityForApproval(contractAgreement, "Please approve the Contract Agreement");

                            notificationsUtil
                                .createNotification(contractAgreement, Constants.NotificationType.SENT_FOR_APPROVAL, login)
                                .subscribe(
                                    cm -> log.debug("Created notification successfully"),
                                    err -> log.debug("Error occurred while creating notification" + err)
                                );

                            return logContractTypeHistory(contractAgreement, "Contract Agreement Sent for Approval ")
                                .thenReturn(EntityStatus.APPROVAL_PENDING);
                        case DRAFT:
                            log.debug("Handling DRAFT workflow");

                            userTaskUtil.removeAllTask(contractAgreement.getID(), TaskType.CONTRACT_AGREEMENT_APPROVAL).subscribe();

                            Mono<EntityStatus> entityStatusMono = logContractTypeHistory(contractAgreement, "Contract Agreement Modified")
                                .thenReturn(EntityStatus.DRAFT);

                            if (contractAgreement.getEntityStatus() == EntityStatus.DRAFT) return entityStatusMono;

                            notificationsUtil
                                .createNotification(contractAgreement, Constants.NotificationType.MODIFIED, login)
                                .publishOn(Schedulers.parallel())
                                .subscribe(
                                    cm -> log.debug("Created notification successfully"),
                                    err -> log.debug("Error occurred while creating notification" + err)
                                );

                            return entityStatusMono;
                        case REJECTED:
                            log.debug("Handling REJECTED workflow");

                            notificationsUtil
                                .createNotification(contractAgreement, Constants.NotificationType.REJECTED, login)
                                .subscribe(
                                    cm -> log.debug("Created notification successfully"),
                                    err -> log.debug("Error occurred while creating notification" + err)
                                );

                            return userTaskUtil
                                .reject(contractAgreement)
                                .flatMap(
                                    contractTypeStatus ->
                                        Mono
                                            .from(logContractTypeHistory(contractAgreement, "Contract Agreement Rejected"))
                                            .thenReturn(contractTypeStatus)
                                );
                        default:
                            return Mono.just(EntityStatus.DRAFT);
                    }
                }
            );
    }

    private Mono<EntityStatus> handleContractTemplateWorkflow(ContractTemplate contractTemplate, EntityStatus status) {
        switch (status) {
            case CREATED:
                return logContractTypeHistory(contractTemplate, "Contract Template Created").thenReturn(EntityStatus.CREATED);
            case APPROVED:
                return userTaskUtil
                    .approve(contractTemplate)
                    .flatMap(
                        contractTypeStatus ->
                            Mono.from(logContractTypeHistory(contractTemplate, "Contract Template Approved")).thenReturn(contractTypeStatus)
                    );
            case APPROVAL_PENDING:
                userTaskUtil.sendEntityForApproval(contractTemplate, "Please approve the Contract Template");
                return logContractTypeHistory(contractTemplate, "Contract Template Sent for Approval " + status)
                    .thenReturn(EntityStatus.APPROVAL_PENDING);
            case DRAFT:
                return Mono.just(EntityStatus.DRAFT);
            case REJECTED:
                return userTaskUtil
                    .reject(contractTemplate)
                    .flatMap(
                        contractTypeStatus ->
                            Mono.from(logContractTypeHistory(contractTemplate, "Contract Template Rejected")).thenReturn(contractTypeStatus)
                    );
            default:
                return Mono.just(EntityStatus.DRAFT);
        }
    }

    private Mono<EntityStatus> handleContractClauseWorkflow(ContractClause contractClause, EntityStatus status) {
        switch (status) {
            case CREATED:
                return logContractTypeHistory(contractClause, "Contract Template Created").thenReturn(EntityStatus.CREATED);
            case APPROVED:
                return userTaskUtil
                    .approve(contractClause)
                    .flatMap(
                        contractTypeStatus ->
                            Mono.from(logContractTypeHistory(contractClause, "Contract Template Approved")).thenReturn(contractTypeStatus)
                    );
            case APPROVAL_PENDING:
                userTaskUtil.sendEntityForApproval(contractClause, "Please approve the Contract Template");
                return logContractTypeHistory(contractClause, "Contract Template Sent for Approval " + status)
                    .thenReturn(EntityStatus.APPROVAL_PENDING);
            case DRAFT:
                return Mono.just(EntityStatus.DRAFT);
            case REJECTED:
                return userTaskUtil
                    .reject(contractClause)
                    .flatMap(
                        contractTypeStatus ->
                            Mono.from(logContractTypeHistory(contractClause, "Contract Template Rejected")).thenReturn(contractTypeStatus)
                    );
            default:
                return Mono.just(EntityStatus.DRAFT);
        }
    }

    private Mono<IEntity> logContractTypeHistory(IEntity entity, String message) {
        log.debug(String.format("Logging History for EntityId: %s, Message: %s", entity.getEntityId(), message));

        return getUser()
            .flatMap(
                user -> {
                    log.debug(String.format("Logging for user: %s", user));
                    entity.getHistory().add(new EventHistory(message, user, Instant.now()));
                    return Mono.just(entity);
                }
            );
    }

    private Mono<ContractAgreement> logContractAgreementHistory(ContractAgreement contractAgreement, String message) {
        log.debug(String.format("Logging History for EntityId: %s, Message: %s", contractAgreement.getEntityId(), message));
        return getUser()
            .flatMap(
                user -> {
                    log.debug(String.format("Logging History for UserLogin: %s", user));
                    contractAgreement.getHistory().add(new EventHistory(message, user, Instant.now()));
                    return Mono.just(contractAgreement);
                }
            );
    }

    private Mono<ContractTemplate> logContractTemplateHistory(ContractTemplate contractTemplate, String message) {
        return getUser()
            .flatMap(
                user -> {
                    contractTemplate.getHistory().add(new EventHistory(message, user, Instant.now()));
                    return Mono.just(contractTemplate);
                }
            );
    }

    private Mono<ContractClause> logContractClauseHistory(ContractClause contractClause, String message) {
        return getUser()
            .flatMap(
                user -> {
                    contractClause.getHistory().add(new EventHistory(message, user, Instant.now()));
                    return Mono.just(contractClause);
                }
            );
    }

    private Mono<String> getUser() {
        return SecurityUtils.getCurrentUserLogin();
    }

    private Mono<String> getEntityRole(Team team, TeamGroups teamGroups) {
        return getUser()
            .flatMap(
                login -> {
                    if (team.getPrimaryOwner().getLogin().equals(login)) return Mono.just(Constants.EntityRoles.PRIMARY_OWNER);

                    if (
                        (team.getApprover() != null && team.getApprover().stream().anyMatch(user -> user.getLogin().equals(login))) ||
                        (
                            teamGroups.getApprover() != null &&
                            teamGroups
                                .getApprover()
                                .stream()
                                .anyMatch(group -> group.getMembers().stream().anyMatch(x -> x.getLogin().equals(login)))
                        )
                    ) return Mono.just(Constants.EntityRoles.APPROVER);

                    if (
                        (team.getObserver() != null && team.getObserver().stream().anyMatch(user -> user.getLogin().equals(login))) ||
                        (
                            teamGroups.getObserver() != null &&
                            teamGroups
                                .getObserver()
                                .stream()
                                .anyMatch(group -> group.getMembers().stream().anyMatch(x -> x.getLogin().equals(login)))
                        )
                    ) return Mono.just(Constants.EntityRoles.OBSERVER);

                    if (
                        (
                            team.getExternalReviewer() != null &&
                            team.getExternalReviewer().stream().anyMatch(user -> user.getLogin().equals(login))
                        ) ||
                        (
                            teamGroups.getExternalReviewer() != null &&
                            teamGroups
                                .getExternalReviewer()
                                .stream()
                                .anyMatch(group -> group.getMembers().stream().anyMatch(x -> x.getLogin().equals(login)))
                        )
                    ) return Mono.just(Constants.EntityRoles.EXTERNAL_REVIEWER);
                    if (
                        team.getContractAdmin() != null && team.getContractAdmin().stream().anyMatch(user -> user.getLogin().equals(login))
                    ) return Mono.just(Constants.EntityRoles.CONTRACT_ADMIN);
                    if (
                        (
                            team.getSecondaryOwner() != null &&
                            team.getSecondaryOwner().stream().anyMatch(user -> user.getLogin().equals(login))
                        ) ||
                        (
                            teamGroups.getSecondaryOwner() != null &&
                            teamGroups
                                .getSecondaryOwner()
                                .stream()
                                .anyMatch(group -> group.getMembers().stream().anyMatch(x -> x.getLogin().equals(login)))
                        )
                    ) return Mono.just(Constants.EntityRoles.SECONDARY_OWNER);
                    return Mono.just("NONE");
                }
            );
    }
}

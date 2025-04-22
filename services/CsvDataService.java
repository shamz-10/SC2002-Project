package services;

import enumeration.FlatType;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;

import interfaces.IFileDataService;
import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import enumeration.MaritalStatus;
import models.BTOApplication;
import models.BTOProject;
import models.FlatTypeDetails;
import utils.FilePathsUtils;
import utils.EnumParser;
import stores.DataStore;
import view.CommonView;
import models.User;
import models.HDBOfficerRegistration;
import models.Enquiry;
import models.WithdrawalRequest;

public class CsvDataService implements IFileDataService {

private static List<String> applicantCsvHeaders = new ArrayList<String>();

private static List<String> hdbManagerCsvHeaders = new ArrayList<String>();

private static List<String> hdbOfficerCsvHeaders = new ArrayList<String>();

private static List<String> btoProjectCsvHeaders = new ArrayList<String>();

private static List<String> btoApplicationCsvHeaders = new ArrayList<String>();

private static List<String> hdbOfficerRegistrationCsvHeaders = new ArrayList<String>();

private static List<String> enquiryCsvHeaders = new ArrayList<String>();

private static List<String> withdrawalRequestCsvHeaders = new ArrayList<String>();

public CsvDataService() {
initializeHeaders();
}

private void initializeHeaders() {
applicantCsvHeaders.clear();
hdbManagerCsvHeaders.clear();
hdbOfficerCsvHeaders.clear();
btoProjectCsvHeaders.clear();
btoApplicationCsvHeaders.clear();
hdbOfficerRegistrationCsvHeaders.clear();
enquiryCsvHeaders.clear();
withdrawalRequestCsvHeaders.clear();
List<String> userHeaders = List.of("Name", "NRIC", "Age", "MaritalStatus", "Password");
applicantCsvHeaders.addAll(userHeaders);
hdbManagerCsvHeaders.addAll(userHeaders);
hdbOfficerCsvHeaders.addAll(userHeaders);

btoProjectCsvHeaders.addAll(List.of(
"ProjectName", "Neighborhood", "Type1", "NumberOfUnitsType1", "SellingPriceType1",
"Type2", "NumberOfUnitsType2", "SellingPriceType2", "ApplicationOpeningDate",
"ApplicationClosingDate", "Manager", "OfficerSlot", "Officers", "Visible"
));

btoApplicationCsvHeaders.addAll(List.of(
"ApplicationId", "ApplicantNRIC", "ProjectName", "FlatType", "Status"
));

hdbOfficerRegistrationCsvHeaders.addAll(List.of(
"RegistrationID", "HDBOfficerNRIC", "ProjectName", "Status"
));

enquiryCsvHeaders.addAll(List.of(
"EnquiryID", "ApplicantNRIC", "ProjectName", "Message", "Reply", "CreatedAt", "RepliedAt"
));

withdrawalRequestCsvHeaders.addAll(List.of(
"RequestId", "ApplicationId", "RequestedAt", "IsApproved", "ProcessedAt", "ProcessedBy"
));
}

private List<String> getHeadersForFileType(String fileType) {
switch (fileType) {
case "applicant":
return new ArrayList<>(applicantCsvHeaders);
case "hdbManager":
return new ArrayList<>(hdbManagerCsvHeaders);
case "hdbOfficer":
return new ArrayList<>(hdbOfficerCsvHeaders);
case "btoProject":
return new ArrayList<>(btoProjectCsvHeaders);
case "btoApplication":
return new ArrayList<>(btoApplicationCsvHeaders);
case "hdbOfficerRegistration":
return new ArrayList<>(hdbOfficerRegistrationCsvHeaders);
case "enquiry":
return new ArrayList<>(enquiryCsvHeaders);
case "withdrawalRequest":
return new ArrayList<>(withdrawalRequestCsvHeaders);
default:
return new ArrayList<>();
}
}

public List<String[]> readCsvFile(String filePath) {
List<String[]> dataList = new ArrayList<String[]>();
List<String> headers = new ArrayList<String>();

try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
String headerLine = br.readLine();
if (headerLine == null) {
String fileType = filePath.contains("Applicant") ? "applicant" :
filePath.contains("HDBManager") ? "hdbManager" :
filePath.contains("HDBOfficer") ? "hdbOfficer" :
filePath.contains("BTOProject") ? "btoProject" :
filePath.contains("BTOApplication") ? "btoApplication" :
filePath.contains("HDBOfficerRegistration") ? "hdbOfficerRegistration" :
filePath.contains("Enquiry") ? "enquiry" :
filePath.contains("WithdrawalRequest") ? "withdrawalRequest" : "";

headers.addAll(getHeadersForFileType(fileType));
return dataList;
}

String[] headerRow = headerLine.split(",");
for (String header : headerRow) {
headers.add(header);
}

String line = "";
while ((line = br.readLine()) != null) {
String[] values = line.split(",");
dataList.add(values);
}

} catch (IOException e) {
System.out.println("Cannot import data!" + filePath);
}

return dataList;
}

private boolean writeCsvFile(String filePath, String[] headers, List<String> lines) {
    try {
        File file = new File(filePath);
        boolean fileExists = file.exists();
        
        // Read existing data if file exists
        List<String> existingLines = new ArrayList<>();
        if (fileExists) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                // Skip header
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        existingLines.add(line);
                    }
                }
            }
        }
        
        // Create a map of existing entries to avoid duplicates
        Map<String, String> existingEntries = new HashMap<>();
        for (String line : existingLines) {
            try {
                String[] parts = line.split(",", -1); // -1 to keep empty fields
                if (parts.length > 0) {
                    String key = parts[0].trim();
                    if (!key.isEmpty()) {
                        existingEntries.put(key, line);
                    }
                }
            } catch (Exception e) {
                System.out.println("Warning: Skipping invalid line: " + line);
                continue;
            }
        }
        
        // Update or add new entries
        for (String line : lines) {
            try {
                String[] parts = line.split(",", -1); // -1 to keep empty fields
                if (parts.length > 0) {
                    String key = parts[0].trim();
                    if (!key.isEmpty()) {
                        existingEntries.put(key, line);
                    }
                }
            } catch (Exception e) {
                System.out.println("Warning: Skipping invalid line: " + line);
                continue;
            }
        }
        
        // Write all entries back to file
        try (FileWriter writer = new FileWriter(file, false)) { // false to overwrite
            // Write headers
            writer.write(String.join(",", headers) + "\n");
            
            // Write all entries
            for (String line : existingEntries.values()) {
                writer.write(line + "\n");
            }
        }
        return true;
    } catch (IOException e) {
        System.out.println("Error writing to file: " + e.getMessage());
        return false;
    }
}

private Map<String, String> parseUserRow(String[] userRow) {
String name = userRow[0];
String nric = userRow[1];
String age = userRow[2];
MaritalStatus maritalStatus = EnumParser.parseMaritalStatus(userRow[3]);
String password = userRow[4];

Map<String, String> userInfoMap = new HashMap<String, String>();
userInfoMap.put("name", name);
userInfoMap.put("nric", nric);
userInfoMap.put("age", age);
userInfoMap.put("maritalStatus", maritalStatus.toString());
userInfoMap.put("password", password);

return userInfoMap;
}

private BTOProject parseBTOProjectRow(String[] btoProjectRow) {
String projectName = btoProjectRow[0];
String neighborhood = btoProjectRow[1];

Map<FlatType, FlatTypeDetails> flatTypes = new HashMap<FlatType, FlatTypeDetails>();

for (int i = 2; i < 8; i += 3) {
FlatType flatType = EnumParser.parseFlatType(btoProjectRow[i]);
int numberOfUnits = Integer.parseInt(btoProjectRow[i + 1]);
double sellingPrice = Double.parseDouble(btoProjectRow[i + 2]);

FlatTypeDetails flatTypeDetails = new FlatTypeDetails(numberOfUnits, sellingPrice);
flatTypes.put(flatType, flatTypeDetails);
}

DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
LocalDate applicationOpeningDate = LocalDate.parse(btoProjectRow[8], formatter);
LocalDate applicationClosingDate = LocalDate.parse(btoProjectRow[9], formatter);

String managerName = btoProjectRow[10];
HDBManager manager = null;
for (HDBManager m : DataStore.getHDBManagersData().values()) {
if (m.getName().equals(managerName)) {
manager = m;
break;
}
}

int officerSlots = Integer.parseInt(btoProjectRow[11]);
boolean visible = Boolean.parseBoolean(btoProjectRow[12]);

List<HDBOfficer> hdbOfficers = new ArrayList<HDBOfficer>();
for (int i = 13; i < btoProjectRow.length; i++) {
String officerName = btoProjectRow[i];
for (HDBOfficer o : DataStore.getHDBOfficersData().values()) {
if (o.getName().equals(officerName)) {
hdbOfficers.add(o);
break;
}
}
}

return new BTOProject(projectName, neighborhood, applicationOpeningDate, applicationClosingDate,
flatTypes, manager, officerSlots, hdbOfficers, visible);
}

@Override
public Map<String, Applicant> importApplicantData(String applicantsFilePath) {
Map<String, Applicant> applicantsMap = new HashMap<String, Applicant>();

List<String[]> applicantsRows = this.readCsvFile(applicantsFilePath);

for (String[] applicantRow : applicantsRows) {
Map<String, String> applicantInfoMap = parseUserRow(applicantRow);

String name = applicantInfoMap.get("name");
String nric = applicantInfoMap.get("nric");
int age = Integer.parseInt(applicantInfoMap.get("age"));
MaritalStatus maritalStatus = EnumParser.parseMaritalStatus(applicantInfoMap.get("maritalStatus"));
String password = applicantInfoMap.get("password");

Applicant applicant = new Applicant(name, nric, age, maritalStatus, password);

applicantsMap.put(nric, applicant);
}

return applicantsMap;
}

@Override
public boolean exportApplicantData(String applicantsFilePath, Map<String, Applicant> applicantMap) {
List<String> applicantLines = new ArrayList<String>();

for (Applicant applicant : applicantMap.values()) {
String applicantLine = String.format("%s,%s,%d,%s,%s",
applicant.getName(),
applicant.getNric(),
applicant.getAge(),
applicant.getMaritalStatus(),
applicant.getPassword());

applicantLines.add(applicantLine);
}

boolean success = this.writeCsvFile(applicantsFilePath, applicantCsvHeaders.toArray(new String[0]), applicantLines);
return success;
}

@Override
public Map<String, HDBManager> importHDBManagerData(String hdbManagersFilePath) {
Map<String, HDBManager> hdbManagersMap = new HashMap<String, HDBManager>();

List<String[]> hdbManagersRows = this.readCsvFile(hdbManagersFilePath);

for (String[] hdbManagerRow : hdbManagersRows) {
Map<String, String> hdbManagerInfoMap = parseUserRow(hdbManagerRow);

String name = hdbManagerInfoMap.get("name");
String nric = hdbManagerInfoMap.get("nric");
int age = Integer.parseInt(hdbManagerInfoMap.get("age"));
MaritalStatus maritalStatus = EnumParser.parseMaritalStatus(hdbManagerInfoMap.get("maritalStatus"));
String password = hdbManagerInfoMap.get("password");

HDBManager hdbManager = new HDBManager(name, nric, age, maritalStatus, password);

hdbManagersMap.put(nric, hdbManager);
}

return hdbManagersMap;
}

@Override
public boolean exportHDBManagerData(String hdbManagersFilePath, Map<String, HDBManager> hdbManagerMap) {
List<String> hdbManagerLines = new ArrayList<String>();

for (HDBManager manager : hdbManagerMap.values()) {
String hdbManagerLine = String.format("%s,%s,%d,%s,%s",
manager.getName(),
manager.getNric(),
manager.getAge(),
manager.getMaritalStatus(),
manager.getPassword());

hdbManagerLines.add(hdbManagerLine);
}

boolean success = this.writeCsvFile(hdbManagersFilePath, hdbManagerCsvHeaders.toArray(new String[0]), hdbManagerLines);
return success;
}

@Override
public Map<String, HDBOfficer> importHDBOfficerData(String hdbOfficersFilePath) {
Map<String, HDBOfficer> hdbOfficersMap = new HashMap<String, HDBOfficer>();

List<String[]> hdbOfficersRows = this.readCsvFile(hdbOfficersFilePath);

for (String[] hdbOfficerRow : hdbOfficersRows) {
Map<String, String> hdbOfficerInfoMap = parseUserRow(hdbOfficerRow);

String name = hdbOfficerInfoMap.get("name");
String nric = hdbOfficerInfoMap.get("nric");
int age = Integer.parseInt(hdbOfficerInfoMap.get("age"));
MaritalStatus maritalStatus = EnumParser.parseMaritalStatus(hdbOfficerInfoMap.get("maritalStatus"));
String password = hdbOfficerInfoMap.get("password");

HDBOfficer hdbOfficer = new HDBOfficer(name, nric, age, maritalStatus, password);

hdbOfficersMap.put(nric, hdbOfficer);
}

return hdbOfficersMap;
}

@Override
public boolean exportHDBOfficerData(String hdbOfficersFilePath, Map<String, HDBOfficer> hdbOfficerMap) {
List<String> hdbOfficerLines = new ArrayList<String>();

for (HDBOfficer officer : hdbOfficerMap.values()) {
String hdbOfficerLine = String.format("%s,%s,%d,%s,%s",
officer.getName(),
officer.getNric(),
officer.getAge(),
officer.getMaritalStatus(),
officer.getPassword());

hdbOfficerLines.add(hdbOfficerLine);
}

boolean success = this.writeCsvFile(hdbOfficersFilePath, hdbOfficerCsvHeaders.toArray(new String[0]), hdbOfficerLines);
return success;
}

@Override
public Map<String, BTOProject> importBTOProjectData(String btoProjectFilePath) {
Map<String, BTOProject> btoProjectsMap = new HashMap<String, BTOProject>();

List<String[]> btoProjectsRows = this.readCsvFile(btoProjectFilePath);

for (String[] btoProjectRow : btoProjectsRows) {
BTOProject btoProject = parseBTOProjectRow(btoProjectRow);
btoProjectsMap.put(btoProject.getProjectName(), btoProject);
}

return btoProjectsMap;
}

@Override
public boolean exportBTOProjectData(String btoProjectFilePath, Map<String, BTOProject> btoProjectMap) {
    List<String> btoProjectLines = new ArrayList<String>();

    for (BTOProject project : btoProjectMap.values()) {
        try {
            StringBuilder line = new StringBuilder();
            
            // Basic project info
            line.append(project.getProjectName()).append(",");
            line.append(project.getNeighborhood()).append(",");

            // Flat types - ensure we have both types even if empty
            Map<FlatType, FlatTypeDetails> flatTypes = project.getFlatTypes();
            FlatTypeDetails twoRoom = flatTypes.get(FlatType.TWO_ROOM);
            FlatTypeDetails threeRoom = flatTypes.get(FlatType.THREE_ROOM);

            // 2-Room details
            line.append("2-Room").append(",");
            line.append(twoRoom != null ? twoRoom.getUnits() : 0).append(",");
            line.append(twoRoom != null ? twoRoom.getPrice() : 0).append(",");

            // 3-Room details
            line.append("3-Room").append(",");
            line.append(threeRoom != null ? threeRoom.getUnits() : 0).append(",");
            line.append(threeRoom != null ? threeRoom.getPrice() : 0).append(",");

            // Dates
            line.append(project.getApplicationOpeningDate()).append(",");
            line.append(project.getApplicationClosingDate()).append(",");

            // Manager
            line.append(project.getHDBManager() != null ? project.getHDBManager().getName() : "").append(",");

            // Officer slots and visibility
            line.append(project.getHDBOfficerSlots()).append(",");
            line.append(project.isVisible()).append(",");

            // Officers
            List<HDBOfficer> officers = project.getHDBOfficers();
            if (!officers.isEmpty()) {
                String[] officerNames = officers.stream()
                    .map(HDBOfficer::getName)
                    .toArray(String[]::new);
                line.append(String.join(",", officerNames));
            }

            btoProjectLines.add(line.toString());
        } catch (Exception e) {
            System.out.println("Warning: Error processing project " + project.getProjectName() + ": " + e.getMessage());
            continue;
        }
    }

    return this.writeCsvFile(btoProjectFilePath, btoProjectCsvHeaders.toArray(new String[0]), btoProjectLines);
}

private BTOApplication parseBTOApplicationRow(String[] btoApplicationRow) {
String applicationId = btoApplicationRow[0];
String applicantNric = btoApplicationRow[1];
String projectName = btoApplicationRow[2];
String flatType = btoApplicationRow[3];
String status = btoApplicationRow[4];

User applicant = DataStore.getApplicantsData().get(applicantNric);
if (applicant == null) {
applicant = DataStore.getHDBOfficersData().get(applicantNric);
}
BTOProject project = DataStore.getBTOProjectsData().get(projectName);

if (applicant == null || project == null) {
System.out.println("Warning: Skipping invalid BTO application " + applicationId +
" - " + (applicant == null ? "Applicant not found: " + applicantNric : "") +
(project == null ? "Project not found: " + projectName : ""));
return null;
}

FlatType parsedFlatType = "null".equals(flatType) ? null : EnumParser.parseFlatType(flatType);

return new BTOApplication(applicationId, applicant, project, parsedFlatType, EnumParser.parseBTOApplicationStatus(status));
}

@Override
public Map<String, BTOApplication> importBTOApplicationData(String btoApplicationFilePath) {
Map<String, BTOApplication> btoApplicationsMap = new HashMap<String, BTOApplication>();

List<String[]> btoApplicationsRows = this.readCsvFile(btoApplicationFilePath);

for (String[] btoApplicationRow : btoApplicationsRows) {
BTOApplication btoApplication = parseBTOApplicationRow(btoApplicationRow);
if (btoApplication != null) {
btoApplicationsMap.put(btoApplication.getApplicationId(), btoApplication);
}
}

return btoApplicationsMap;
}

@Override
public boolean exportBTOApplicationData(String btoApplicationFilePath, Map<String, BTOApplication> btoApplicationMap) {
List<String> btoApplicationLines = new ArrayList<String>();

for (BTOApplication application : btoApplicationMap.values()) {
StringBuilder line = new StringBuilder();
line.append(application.getApplicationId()).append(",");
line.append(application.getApplicant().getNric()).append(",");
line.append(application.getProject().getProjectName()).append(",");
line.append(application.getFlatType() != null ? application.getFlatType().getDisplayName() : "null").append(",");
line.append(application.getStatus().getDisplayName());

btoApplicationLines.add(line.toString());
}

boolean success = this.writeCsvFile(btoApplicationFilePath, btoApplicationCsvHeaders.toArray(new String[0]), btoApplicationLines);
return success;
}

private HDBOfficerRegistration parseHDBOfficerRegistrationRow(String[] hdbOfficerRegistrationRow) {
String registrationId = hdbOfficerRegistrationRow[0];
String hdbOfficerNric = hdbOfficerRegistrationRow[1];
String projectName = hdbOfficerRegistrationRow[2];
String status = hdbOfficerRegistrationRow[3];

HDBOfficer hdbOfficer = DataStore.getHDBOfficersData().get(hdbOfficerNric);
BTOProject project = DataStore.getBTOProjectsData().get(projectName);

if (hdbOfficer == null || project == null) {
System.out.println("Warning: Skipping invalid HDB officer registration " + registrationId +
" - " + (hdbOfficer == null ? "Officer not found: " + hdbOfficerNric : "") +
(project == null ? "Project not found: " + projectName : ""));
return null;
}

return new HDBOfficerRegistration(registrationId, hdbOfficer, project, EnumParser.parseHDBOfficerRegistrationStatus(status));
}

@Override
public Map<String, HDBOfficerRegistration> importHDBOfficerRegistrationData(String hdbOfficerRegistrationsFilePath) {
Map<String, HDBOfficerRegistration> hdbOfficerRegistrationsMap = new HashMap<String, HDBOfficerRegistration>();

List<String[]> hdbOfficerRegistrationsRows = this.readCsvFile(hdbOfficerRegistrationsFilePath);

for (String[] hdbOfficerRegistrationRow : hdbOfficerRegistrationsRows) {
HDBOfficerRegistration registration = parseHDBOfficerRegistrationRow(hdbOfficerRegistrationRow);
if (registration != null) {
hdbOfficerRegistrationsMap.put(registration.getRegistrationId(), registration);
}
}

return hdbOfficerRegistrationsMap;
}

@Override
public boolean exportHDBOfficerRegistrationData(String hdbOfficerRegistrationsFilePath, Map<String, HDBOfficerRegistration> hdbOfficerRegistrationMap) {
List<String> hdbOfficerRegistrationLines = new ArrayList<String>();

for (HDBOfficerRegistration registration : hdbOfficerRegistrationMap.values()) {
StringBuilder line = new StringBuilder();
line.append(registration.getRegistrationId()).append(",");
line.append(registration.getHDBOfficer().getNric()).append(",");
line.append(registration.getProject().getProjectName()).append(",");
line.append(registration.getStatus().getDisplayName());

hdbOfficerRegistrationLines.add(line.toString());
}

boolean success = this.writeCsvFile(hdbOfficerRegistrationsFilePath, hdbOfficerRegistrationCsvHeaders.toArray(new String[0]), hdbOfficerRegistrationLines);
return success;
}

@Override
public Map<String, Enquiry> importEnquiryData(String enquiryFilePath) {
Map<String, Enquiry> enquiryMap = new HashMap<>();
List<String[]> enquiryRows = this.readCsvFile(enquiryFilePath);

for (String[] enquiryRow : enquiryRows) {
String enquiryId = enquiryRow[0];
String applicantNric = enquiryRow[1];
String projectName = enquiryRow[2];
String message = enquiryRow[3];
String reply = enquiryRow[4];
LocalDateTime createdAt = LocalDateTime.parse(enquiryRow[5], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
LocalDateTime repliedAt = (enquiryRow.length > 6 && !enquiryRow[6].isEmpty()) ? LocalDateTime.parse(enquiryRow[6], DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;

Applicant applicant = DataStore.getApplicantsData().get(applicantNric);
BTOProject project = DataStore.getBTOProjectsData().get(projectName);

if (applicant == null || project == null) {
System.out.println("Warning: Skipping invalid enquiry " + enquiryId +
" - " + (applicant == null ? "Applicant not found: " + applicantNric : "") +
(project == null ? "Project not found: " + projectName : ""));
continue;
}

Enquiry enquiry = new Enquiry(enquiryId, applicant, project, message, reply, createdAt, repliedAt);
enquiryMap.put(enquiryId, enquiry);
}
return enquiryMap;
}

@Override
public boolean exportEnquiryData(String enquiryFilePath, Map<String, Enquiry> enquiryMap) {
List<String> enquiryLines = new ArrayList<>();

for (Enquiry enquiry : enquiryMap.values()) {
String line = String.format("%s,%s,%s,%s,%s,%s,%s",
enquiry.getEnquiryId(),
enquiry.getApplicant() != null ? enquiry.getApplicant().getNric() : "",
enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "",
enquiry.getMessage(),
enquiry.getReply() != null ? enquiry.getReply() : "",
enquiry.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
enquiry.getRepliedAt() != null ? enquiry.getRepliedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : ""
);
enquiryLines.add(line);
}

boolean success = this.writeCsvFile(enquiryFilePath, enquiryCsvHeaders.toArray(new String[0]), enquiryLines);
return success;
}

@Override
public Map<String, WithdrawalRequest> importWithdrawalRequestData(String withdrawalRequestFilePath) {
Map<String, WithdrawalRequest> withdrawalRequestMap = new HashMap<String, WithdrawalRequest>();

List<String[]> withdrawalRequestRows = this.readCsvFile(withdrawalRequestFilePath);

for (String[] withdrawalRequestRow : withdrawalRequestRows) {
WithdrawalRequest request = parseWithdrawalRequestRow(withdrawalRequestRow);
if (request != null) {
withdrawalRequestMap.put(request.getRequestId(), request);
}
}

return withdrawalRequestMap;
}

private WithdrawalRequest parseWithdrawalRequestRow(String[] withdrawalRequestRow) {
    String requestId = withdrawalRequestRow[0];
    String applicationId = withdrawalRequestRow[1];
    LocalDateTime requestedAt = LocalDateTime.parse(withdrawalRequestRow[2]);
    boolean isApproved = Boolean.parseBoolean(withdrawalRequestRow[3]);
    LocalDateTime processedAt = withdrawalRequestRow[4].equals("null") ? null : LocalDateTime.parse(withdrawalRequestRow[4]);
    String processedBy = withdrawalRequestRow[5].equals("null") ? null : withdrawalRequestRow[5];

    BTOApplication application = DataStore.getBTOApplicationsData().get(applicationId);
    if (application == null) {
        System.out.println("Warning: Skipping invalid withdrawal request " + requestId +
                " - Application not found: " + applicationId);
        return null;
    }

    WithdrawalRequest request = new WithdrawalRequest(requestId, application);
    request.setRequestedAt(requestedAt);
    request.setApproved(isApproved);
    request.setProcessedAt(processedAt);
    request.setProcessedBy(processedBy);
    
    return request;
}

@Override
public boolean exportWithdrawalRequestData(String withdrawalRequestFilePath, Map<String, WithdrawalRequest> withdrawalRequestMap) {
List<String> withdrawalRequestLines = new ArrayList<String>();

for (WithdrawalRequest request : withdrawalRequestMap.values()) {
StringBuilder line = new StringBuilder();
line.append(request.getRequestId()).append(",");
line.append(request.getApplication().getApplicationId()).append(",");
line.append(request.getRequestedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append(",");
line.append(request.isApproved()).append(",");
line.append(request.getProcessedAt() != null ? request.getProcessedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "null").append(",");
line.append(request.getProcessedBy() != null ? request.getProcessedBy() : "null");

withdrawalRequestLines.add(line.toString());
}

boolean success = this.writeCsvFile(withdrawalRequestFilePath, withdrawalRequestCsvHeaders.toArray(new String[0]), withdrawalRequestLines);
return success;
}
}
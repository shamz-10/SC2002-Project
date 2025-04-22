package stores;

import java.util.HashMap;
import java.util.Map;

import interfaces.IFileDataService;
import models.Applicant;
import models.BTOProject;
import models.HDBManager;
import models.HDBOfficer;
import models.BTOApplication;
import models.HDBOfficerRegistration;
import models.User;
import models.Enquiry;
import models.WithdrawalRequest;

public class DataStore {
private static IFileDataService fileDataService;

private static Map<String, String> filePathsMap;

private static Map<String, Applicant> applicantsData = new HashMap<String, Applicant>();

private static Map<String, HDBManager> hdbManagersData = new HashMap<String, HDBManager>();

private static Map<String, HDBOfficer> hdbOfficersData = new HashMap<String, HDBOfficer>();

private static Map<String, BTOProject> btoProjectsData = new HashMap<String, BTOProject>();

private static Map<String, BTOApplication> btoApplicationsData = new HashMap<String, BTOApplication>();

private static Map<String, HDBOfficerRegistration> hdbOfficerRegistrationsData = new HashMap<String, HDBOfficerRegistration>();

private static Map<String, Enquiry> enquiriesData = new HashMap<String, Enquiry>();

private static Map<String, WithdrawalRequest> withdrawalRequestsData = new HashMap<>();

private DataStore() {
}

public static boolean initDataStore(IFileDataService fileDataService, Map<String, String> filePathsMap) {
DataStore.filePathsMap = filePathsMap;
DataStore.fileDataService = fileDataService;

DataStore.applicantsData = fileDataService.importApplicantData(filePathsMap.get("applicant"));
DataStore.hdbManagersData = fileDataService.importHDBManagerData(filePathsMap.get("hdbManager"));
DataStore.hdbOfficersData = fileDataService.importHDBOfficerData(filePathsMap.get("hdbOfficer"));
DataStore.btoProjectsData = fileDataService.importBTOProjectData(filePathsMap.get("btoProject"));
DataStore.btoApplicationsData = fileDataService.importBTOApplicationData(filePathsMap.get("btoApplication"));
DataStore.hdbOfficerRegistrationsData = fileDataService.importHDBOfficerRegistrationData(filePathsMap.get("hdbOfficerRegistrations"));
DataStore.enquiriesData = fileDataService.importEnquiryData(filePathsMap.get("enquiry"));
DataStore.withdrawalRequestsData = fileDataService.importWithdrawalRequestData(filePathsMap.get("withdrawalRequest"));

for (BTOProject project : btoProjectsData.values()) {
for (HDBOfficer officer : project.getHDBOfficers()) {
officer.addHandledProject(project);
}
}

return true;
}

public static boolean saveData() {
DataStore.setApplicantsData(applicantsData);
DataStore.setHDBManagersData(hdbManagersData);
DataStore.setHDBOfficersData(hdbOfficersData);
DataStore.setBTOProjectsData(btoProjectsData);
DataStore.setBTOApplicationsData(btoApplicationsData);
DataStore.setHDBOfficerRegistrationsData(hdbOfficerRegistrationsData);
DataStore.setEnquiriesData(enquiriesData);
DataStore.setWithdrawalRequestsData(withdrawalRequestsData);

return true;
}

public static Map<String, Applicant> getApplicantsData() {
return DataStore.applicantsData;
}

public static void setApplicantsData(Map<String, Applicant> applicantsData) {
DataStore.applicantsData = applicantsData;
fileDataService.exportApplicantData(filePathsMap.get("applicant"), applicantsData);
}

public static Map<String, HDBManager> getHDBManagersData() {
return DataStore.hdbManagersData;
}

public static void setHDBManagersData(Map<String, HDBManager> hdbManagersData) {
DataStore.hdbManagersData = hdbManagersData;
fileDataService.exportHDBManagerData(filePathsMap.get("hdbManager"), hdbManagersData);
}

public static Map<String, HDBOfficer> getHDBOfficersData() {
return DataStore.hdbOfficersData;
}

public static void setHDBOfficersData(Map<String, HDBOfficer> hdbOfficersData) {
DataStore.hdbOfficersData = hdbOfficersData;
fileDataService.exportHDBOfficerData(filePathsMap.get("hdbOfficer"), hdbOfficersData);
}

 public static Map<String, BTOProject> getBTOProjectsData() {
        return DataStore.btoProjectsData;
    }
    public static boolean isProjectVisibleToUser(BTOProject project, User user, boolean isOfficerMode) {
        if (isOfficerMode && user instanceof HDBOfficer) {
            // In officer mode, show all
            return true;
        }
        // In applicant mode, only show visible projects
        return project.isVisible();
    }
    
    

public static void setBTOProjectsData(Map<String, BTOProject> btoProjectsData) {
DataStore.btoProjectsData = btoProjectsData;
fileDataService.exportBTOProjectData(filePathsMap.get("btoProject"), btoProjectsData);
}

public static Map<String, BTOApplication> getBTOApplicationsData() {
return DataStore.btoApplicationsData;
}

public static void setBTOApplicationsData(Map<String, BTOApplication> btoApplicationsData) {
DataStore.btoApplicationsData = btoApplicationsData;
fileDataService.exportBTOApplicationData(filePathsMap.get("btoApplication"), btoApplicationsData);
}

public static Map<String, HDBOfficerRegistration> getHDBOfficerRegistrationsData() {
return hdbOfficerRegistrationsData;
}

public static void setHDBOfficerRegistrationsData(Map<String, HDBOfficerRegistration> hdbOfficerRegistrationsData) {
DataStore.hdbOfficerRegistrationsData = hdbOfficerRegistrationsData;
fileDataService.exportHDBOfficerRegistrationData(filePathsMap.get("hdbOfficerRegistrations"), hdbOfficerRegistrationsData);
}

public static Map<String, Enquiry> getEnquiriesData() {
return enquiriesData;
}

public static void setEnquiriesData(Map<String, Enquiry> enquiriesData) {
DataStore.enquiriesData = enquiriesData;
fileDataService.exportEnquiryData(filePathsMap.get("enquiry"), enquiriesData);
}

public static Map<String, WithdrawalRequest> getWithdrawalRequestsData() {
return withdrawalRequestsData;
}

public static void setWithdrawalRequestsData(Map<String, WithdrawalRequest> withdrawalRequestsData) {
DataStore.withdrawalRequestsData = withdrawalRequestsData;
fileDataService.exportWithdrawalRequestData(filePathsMap.get("withdrawalRequest"), withdrawalRequestsData);
}
}
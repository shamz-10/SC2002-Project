package interfaces;

import java.util.Map;

import models.Applicant;
import models.BTOProject;
import models.HDBManager;
import models.HDBOfficer;
import models.BTOApplication;
import models.HDBOfficerRegistration;
import models.Enquiry;
import models.WithdrawalRequest;

public interface IFileDataService {
Map<String, Applicant> importApplicantData(String applicantsFilePath);

boolean exportApplicantData(String applicantsFilePath, Map<String, Applicant> applicantMap);

Map<String, HDBManager> importHDBManagerData(String hdbManagerFilePath);

boolean exportHDBManagerData(String hdbManagerFilePath, Map<String, HDBManager> hdbManagerMap);

Map<String, HDBOfficer> importHDBOfficerData(String hdbOfficerFilePath);

boolean exportHDBOfficerData(String hdbOfficerFilePath, Map<String, HDBOfficer> hdbOfficerMap);

Map<String, BTOProject> importBTOProjectData(String btoProjectFilePath);

boolean exportBTOProjectData(String btoProjectFilePath, Map<String, BTOProject> btoProjectMap);

Map<String, BTOApplication> importBTOApplicationData(String btoApplicationFilePath);

boolean exportBTOApplicationData(String btoApplicationFilePath, Map<String, BTOApplication> btoApplicationMap);

Map<String, HDBOfficerRegistration> importHDBOfficerRegistrationData(String hdbOfficerRegistrationsFilePath);

boolean exportHDBOfficerRegistrationData(String hdbOfficerRegistrationsFilePath, Map<String, HDBOfficerRegistration> hdbOfficerRegistrationMap);

Map<String, Enquiry> importEnquiryData(String enquiryFilePath);

boolean exportEnquiryData(String enquiryFilePath, Map<String, Enquiry> enquiryMap);

Map<String, WithdrawalRequest> importWithdrawalRequestData(String withdrawalRequestFilePath);

boolean exportWithdrawalRequestData(String withdrawalRequestFilePath, Map<String, WithdrawalRequest> withdrawalRequestMap);
}
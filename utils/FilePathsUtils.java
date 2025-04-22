package utils;

import java.util.HashMap;
import java.util.Map;

public class FilePathsUtils {
private static Map<String, String> filePathsMap = new HashMap<String, String>();

private FilePathsUtils() {
};

public static Map<String, String> csvFilePaths() {
filePathsMap.clear();

filePathsMap.put("applicant", "data/ApplicantList.csv");
filePathsMap.put("hdbManager", "data/HDBManagerList.csv");
filePathsMap.put("hdbOfficer", "data/HDBOfficerList.csv");
filePathsMap.put("hdbOfficers", "data/HDBOfficer.csv");
filePathsMap.put("btoProject", "data/BTOProjectList.csv");
filePathsMap.put("btoApplication", "data/BTOApplicationList.csv");
filePathsMap.put("hdbOfficerRegistrations", "data/HDBOfficerRegistrationList.csv");
filePathsMap.put("enquiry", "data/EnquiryList.csv");
filePathsMap.put("withdrawalRequest", "data/WithdrawalRequestList.csv");

return filePathsMap;
}
}
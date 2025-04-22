package services;

import models.Enquiry;
import models.BTOProject;
import models.Applicant;
import stores.DataStore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EnquiryService {

public List<Enquiry> getAllEnquiries() {
return DataStore.getEnquiriesData().values().stream()
.collect(Collectors.toList());
}

public List<Enquiry> getEnquiriesByProject(BTOProject project) {
return DataStore.getEnquiriesData().values().stream()
.filter(enquiry -> enquiry.getProject().equals(project))
.collect(Collectors.toList());
}

public List<Enquiry> getEnquiriesByApplicant(Applicant applicant) {
return DataStore.getEnquiriesData().values().stream()
.filter(enquiry -> enquiry.getApplicant().equals(applicant))
.collect(Collectors.toList());
}

public List<Enquiry> getPendingEnquiries() {
return DataStore.getEnquiriesData().values().stream()
.filter(enquiry -> !enquiry.hasReply())
.collect(Collectors.toList());
}

public Enquiry createEnquiry(Applicant applicant, BTOProject project, String message) {
String enquiryId = "ENQ" + System.currentTimeMillis();
Enquiry enquiry = new Enquiry(
enquiryId,
applicant,
project,
message,
null,
LocalDateTime.now(),
null
);

DataStore.getEnquiriesData().put(enquiryId, enquiry);
DataStore.saveData();

return enquiry;
}

public boolean replyToEnquiry(Enquiry enquiry, String reply) {
if (enquiry.hasReply()) {
return false;
}

reply = reply.replace(",", " ");

enquiry.setReply(reply);
DataStore.saveData();

return true;
}

public boolean deleteEnquiry(Enquiry enquiry) {
if (DataStore.getEnquiriesData().remove(enquiry.getEnquiryId()) != null) {
DataStore.saveData();
return true;
}
return false;
}
}
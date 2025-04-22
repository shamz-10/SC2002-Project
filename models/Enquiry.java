package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Enquiry {
private String enquiryId;
private final Applicant applicant;
private final BTOProject project;
private String message;
private String reply;
private final LocalDateTime createdAt;
private LocalDateTime repliedAt;

public Enquiry(Applicant applicant, BTOProject project, String message) {
this.enquiryId = generateEnquiryId();
this.applicant = applicant;
this.project = project;
this.message = message;
this.createdAt = LocalDateTime.now();
this.reply = null;
this.repliedAt = null;
}

public Enquiry(String enquiryId, Applicant applicant, BTOProject project, String message,
String reply, LocalDateTime createdAt, LocalDateTime repliedAt) {
this.enquiryId = enquiryId;
this.applicant = applicant;
this.project = project;
this.message = message;
this.reply = reply;
this.createdAt = createdAt;
this.repliedAt = repliedAt;
}

private static String generateEnquiryId() {
String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
return "ENQ-" + date + "-" + UUID.randomUUID().toString().substring(0, 8);
}

public String getEnquiryId() {
return enquiryId;
}

public Applicant getApplicant() {
return applicant;
}

public BTOProject getProject() {
return project;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public String getReply() {
return reply;
}

public void setReply(String reply) {
this.reply = reply;
this.repliedAt = LocalDateTime.now();
}

public LocalDateTime getCreatedAt() {
return createdAt;
}

public LocalDateTime getRepliedAt() {
return repliedAt;
}

public boolean hasReply() {
return reply != null && !reply.isEmpty();
}
}
public class Enquiry {
    private String enquiryID;
    private Applicant applicant;
    private Project project;
    private String message;
    private String response;

    public Enquiry(String enquiryID, Applicant applicant, Project project, String message) {
        this.enquiryID = enquiryID;
        this.applicant = applicant;
        this.project = project;
        this.message = message;
        this.response = null;
    }

    public void submit() {
        System.out.println("Enquiry submitted: " + message);
    }

    public void reply(String response) {
        this.response = response;
        System.out.println("Response recorded: " + response);
    }

    public String getEnquiryID() {
        return enquiryID;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Project getProject() {
        return project;
    }

    public String getMessage() {
        return message;
    }

    public String getResponse() {
        return response;
    }
}

// HDBOfficer class extending Applicant
public class HDBOfficer extends Applicant {
    private Project assignedProject;
    private String registrationStatus;

    public HDBOfficer(String nric, String password, String age, String maritalStatus) {
        super(nric, password, age, maritalStatus);
        this.registrationStatus = "Pending";
    }

    public boolean registerAsOfficer(Project project) {
        if (this.assignedProject == null) {
            this.assignedProject = project;
            return true;
        }
        return false;
    }

    public String viewOfficerStatus() {
        return this.registrationStatus;
    }

    public Project viewProjectDetails() {
        return this.assignedProject;
    }

    public boolean updateFlatAvailability(String flatType) {
        return true;
    }

    public boolean bookFlatForApplicant(Applicant applicant, String flatType) {
        return true;
    }

    public Receipt generateFlatBookingReceipt(Applicant applicant) {
        return new Receipt();
    }

    public List<Enquiry> viewEnquiries() {
        return new ArrayList<>();
    }

    public boolean replyToEnquiry(Enquiry enquiry, String response) {
        return true;
    }
}

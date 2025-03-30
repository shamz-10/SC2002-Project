// Applicant class
public class Applicant extends User {
    private Project appliedProject;
    private String applicationStatus;
    private List<Enquiry> enquiries;

    public Applicant(String nric, String password, String age, String maritalStatus) {
        super(nric, password, age, maritalStatus);
        this.applicationStatus = "Pending";
        this.enquiries = new ArrayList<>();
    }

    public boolean applyForProject(Project project) {
        if (this.appliedProject == null) {
            this.appliedProject = project;
            this.applicationStatus = "Pending";
            return true;
        }
        return false;
    }

    public String viewApplicationStatus() {
        return this.applicationStatus;
    }

    public boolean withdrawApplication() {
        if (this.appliedProject != null) {
            this.appliedProject = null;
            this.applicationStatus = "Withdrawn";
            return true;
        }
        return false;
    }

    public boolean submitEnquiry(String message) {
        enquiries.add(new Enquiry(message));
        return true;
    }
}
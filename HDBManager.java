// HDBManager class

import java.util.List;
import java.util.ArrayList;
public class HDBManager extends User {
    private Project managedProject;

    public HDBManager(String nric, String password, String age, String maritalStatus) {
        super(nric, password, age, maritalStatus);
    }

    public boolean createProject(Project project) {
        this.managedProject = project;
        return true;
    }

    public boolean editProject(Project project, Map<String, String> updatedDetails) {
        return true;
    }

    public boolean deleteProject(Project project) {
        return true;
    }

    public boolean toggleVisibility(Project project, boolean status) {
        return true;
    }

    public boolean approveOfficer(HDBOfficer officer) {
        return true;
    }

    public boolean rejectOfficer(HDBOfficer officer) {
        return true;
    }

    public boolean approveApplication(Applicant applicant) {
        return true;
    }

    public boolean rejectApplication(Applicant applicant) {
        return true;
    }

    public boolean approveWithdrawal(Applicant applicant) {
        return true;
    }

    public Report generateReport(String filter) {
        return new Report();
    }

    public List<Enquiry> viewAllEnquiries() {
        return new ArrayList<>();
    }

    public boolean replyToEnquiry(Enquiry enquiry, String response) {
        return true;
    }
}

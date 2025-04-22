package models;

import enumeration.MaritalStatus;
import enumeration.UserType;
import java.util.ArrayList;
import java.util.List;

public class HDBOfficer extends Applicant {  // 👈 Changed from User to Applicant
    private List<BTOProject> handledProjects;

    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);  // ✅ Uses Applicant's constructor
        this.setUserType(UserType.HDB_OFFICER);            // ✅ Make sure you set the correct user type
        this.handledProjects = new ArrayList<>();
    }

    public List<BTOProject> getHandledProjects() {
        return handledProjects;
    }

    public void addHandledProject(BTOProject project) {
        handledProjects.add(project);
    }

    public void removeHandledProject(BTOProject project) {
        handledProjects.remove(project);
    }
}

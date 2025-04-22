package services;

import models.Applicant;
import models.BTOProject;
import models.FlatTypeDetails;
import models.HDBOfficer;
import models.BTOApplication;
import models.User;
import stores.DataStore;
import enumeration.FlatType;
import enumeration.MaritalStatus;
import interfaces.IBTOProjectService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Paths;
import java.nio.file.Path;

public class BTOProjectService implements IBTOProjectService {

    public boolean isEligible(User user, BTOProject project) {
        if (!project.isVisible()) {
            return false;
        }

        LocalDate today = LocalDate.now();
        if (today.isBefore(project.getApplicationOpeningDate()) ||
            today.isAfter(project.getApplicationClosingDate())) {
            return false;
        }

        if (user instanceof Applicant) {
            Applicant applicant = (Applicant) user;

            // If applicant already applied and is NOT marked as unsuccessful, deny eligibility
            if (hasExistingApplication(applicant) && !isUnsuccessfulApplicant(applicant)) {
                return false;
            }
        }

        if (user instanceof HDBOfficer && hasExistingApplication((HDBOfficer) user)) {
            return false;
        }

        if (user instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) user;
            if (officer.getHandledProjects().contains(project)) {
                return false;
            }
            if (!canOfficerApplyForProject(project, officer)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<BTOProject> getAvailableProjects(User user) {
        return DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.isVisible())  // First filter by visibility
            .filter(project -> isEligible(user, project))  // Then check other eligibility criteria
            .collect(Collectors.toList());
    }
    
    

    public Map<FlatType, FlatTypeDetails> getEligibleFlatTypes(BTOProject project, Applicant applicant) {
        Map<FlatType, FlatTypeDetails> allFlatTypes = project.getFlatTypes();
        Map<FlatType, FlatTypeDetails> eligibleFlatTypes = new HashMap<>();

        int age = applicant.getAge();
        boolean isMarried = applicant.getMaritalStatus() == MaritalStatus.MARRIED;

        if (isMarried && age >= 21) {
            return allFlatTypes;
        }

        if (!isMarried && age >= 35) {
            if (allFlatTypes.containsKey(FlatType.TWO_ROOM)) {
                eligibleFlatTypes.put(FlatType.TWO_ROOM, allFlatTypes.get(FlatType.TWO_ROOM));
            }
        }

        return eligibleFlatTypes;
    }

    public Map<FlatType, FlatTypeDetails> getEligibleFlatTypes(BTOProject project, HDBOfficer hdbOfficer) {
        Map<FlatType, FlatTypeDetails> allFlatTypes = project.getFlatTypes();
        Map<FlatType, FlatTypeDetails> eligibleFlatTypes = new HashMap<>();

        int age = hdbOfficer.getAge();
        boolean isMarried = hdbOfficer.getMaritalStatus() == MaritalStatus.MARRIED;

        if (isMarried && age >= 21) {
            return allFlatTypes;
        }

        if (!isMarried && age >= 35) {
            if (allFlatTypes.containsKey(FlatType.TWO_ROOM)) {
                eligibleFlatTypes.put(FlatType.TWO_ROOM, allFlatTypes.get(FlatType.TWO_ROOM));
            }
        }

        return eligibleFlatTypes;
    }

    public void applyForBTOProject(BTOApplication application) {
        DataStore.getBTOApplicationsData().put(application.getApplicationId(), application);
        DataStore.saveData();
    }

    public boolean hasExistingApplication(Applicant applicant) {
        return DataStore.getBTOApplicationsData().values().stream()
            .anyMatch(application -> application.getApplicant().equals(applicant));
    }

    public boolean hasExistingApplication(HDBOfficer hdbOfficer) {
        return DataStore.getBTOApplicationsData().values().stream()
            .anyMatch(application -> application.getApplicant().equals(hdbOfficer));
    }

    public List<BTOApplication> getApplicationsByApplicant(Applicant applicant) {
        return DataStore.getBTOApplicationsData().values().stream()
            .filter(application -> application.getApplicant().equals(applicant))
            .collect(Collectors.toList());
    }

    public List<BTOApplication> getApplicationsByApplicant(HDBOfficer hdbOfficer) {
        return DataStore.getBTOApplicationsData().values().stream()
            .filter(application -> application.getApplicant().equals(hdbOfficer))
            .collect(Collectors.toList());
    }

    public List<BTOProject> getJoinableProjects(HDBOfficer hdbOfficer) {
        return DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.isVisible() &&
                project.getHDBOfficers().size() < project.getHDBOfficerSlots() &&
                !project.getHDBOfficers().contains(hdbOfficer))
            .collect(Collectors.toList());
    }

    public List<BTOProject> getJoinedProjects(HDBOfficer hdbOfficer) {
        return DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBOfficers().contains(hdbOfficer))
            .collect(Collectors.toList());
    }

    public void joinProjectAsOfficer(BTOProject project, HDBOfficer hdbOfficer) {
        project.addHDBOfficer(hdbOfficer);
        DataStore.saveData();
    }

    public void leaveProjectAsOfficer(BTOProject project, HDBOfficer hdbOfficer) {
        project.removeHDBOfficer(hdbOfficer);
        DataStore.saveData();
    }

    public boolean canOfficerApplyForProject(BTOProject project, HDBOfficer hdbOfficer) {
        return !project.getHDBOfficers().contains(hdbOfficer);
    }

    public boolean isUnsuccessfulApplicant(Applicant applicant) {
        String applicantNRIC = applicant.getNric();
        String line;

        // Dynamically resolve the file path to the CSV file in the "data" folder
        Path filePath = Paths.get(System.getProperty("user.dir"), "data", "BTOApplicationList.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1); // Handles blank fields safely
                if (parts.length >= 5) {
                    String nric = parts[1].trim();
                    String status = parts[4].trim().toLowerCase();

                    if (nric.equalsIgnoreCase(applicantNRIC)) {
                        return status.equals("unsuccessful");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}


package controllers;

import enumeration.FlatType;
import enumeration.BTOApplicationStatus;
import java.util.Scanner;
import java.util.List;
import models.Applicant;
import models.BTOApplication;
import models.BTOProject;
import models.User;
import stores.AuthStore;
import view.BTOProjectAvailableView;
import view.BTOApplicationView;
import services.BTOProjectService;
import utils.TextDecorationUtils;
import java.time.LocalDate;
import models.Enquiry;
import stores.DataStore;
import java.util.stream.Collectors;
import services.EnquiryService;
import models.FlatTypeDetails;
import java.util.Map;
import models.WithdrawalRequest;

public class ApplicantController extends UserController {

private static final Scanner sc = new Scanner(System.in);
private BTOProjectService projectService;
private BTOProjectAvailableView projectView;
private BTOApplicationView applicationView;
private final EnquiryService enquiryService;

public ApplicantController() {
this.projectService = new BTOProjectService();
this.projectView = new BTOProjectAvailableView();
this.applicationView = new BTOApplicationView();
this.enquiryService = new EnquiryService();

}

public void start() {
int choice;

do {
System.out.println(TextDecorationUtils.boldText("Hi, " + AuthStore.getCurrentUser().getName() + "!"));

System.out.println(TextDecorationUtils.underlineText("SETTINGS"));
System.out.println("1. Change Password");

System.out.println(TextDecorationUtils.underlineText("BTO PROJECTS"));
System.out.println("2. View Available BTO Projects");
System.out.println("3. Apply for a BTO Project");
System.out.println("4. View My BTO Applications");
System.out.println("5. Withdraw BTO Application");
System.out.println("6. Request Flat Booking");

System.out.println(TextDecorationUtils.underlineText("ENQUIRIES"));
System.out.println("7. Submit Enquiry");
System.out.println("8. View My Enquiries");

System.out.println("\n0. Logout");

String input = sc.nextLine();
if (input.matches("[0-9]+")) {
choice = Integer.parseInt(input);
if (choice < 0 || choice > 8) {
System.out.println("Invalid input. Please enter 0-8!");
continue;
}
} else {
System.out.println("Invalid input. Please enter an integer.\n");
continue;
}

switch (choice) {
case 1:
if (changePassword()) {
System.out.println("Restarting session...");
AuthController.endSession();
return;
}
break;
case 2:
viewAvailableBTOProjects();
break;
case 3:
applyForBTOProject();
break;
case 4:
viewMyBTOApplications();
break;
case 5:
withdrawBTOApplication();
break;
case 6:
requestFlatBooking();
break;
case 7:
submitEnquiry();
break;
case 8:
viewMyEnquiries();
break;
case 0:
System.out.println("Logging out...");
AuthController.endSession();
return;
default:
System.out.println("Invalid choice. Please try again.");
}
} while (true);
}

protected void viewAvailableBTOProjects() {
    User user = AuthStore.getCurrentUser();
    List<BTOProject> availableProjects = projectService.getAvailableProjects(user);

    if (availableProjects.isEmpty()) {
        System.out.println("\nNo available BTO projects at the moment.");
        return;
    }

    System.out.println("\nFilter BTO Projects by:");
    System.out.println("1. No Filter (View All)");
    System.out.println("2. Project Name");
    System.out.println("3. Neighborhood");
    System.out.println("4. Flat Type");
    System.out.println("5. Number of Units");
    System.out.println("6. Application Period");
    System.out.println("7. Manager");
    System.out.println("8. Officer Slots");
    System.out.println("9. Number of Officers");
    System.out.println("0. Back");
    
    System.out.print("\nEnter your choice: ");
    int filterChoice;
    try {
        filterChoice = Integer.parseInt(sc.nextLine());
    } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter a number.");
        return;
    }
    
    List<BTOProject> filteredProjects = availableProjects.stream()
    .filter(project -> DataStore.isProjectVisibleToUser(project, AuthStore.getCurrentUser(), false))
    .collect(Collectors.toList());

    
    switch (filterChoice) {
        case 0:
            return;
            case 1:
            // No filter, show all in alphabetical order by project name
            filteredProjects = availableProjects.stream()
                .sorted((p1, p2) -> p1.getProjectName().compareToIgnoreCase(p2.getProjectName()))
                .collect(Collectors.toList());
            break;
        case 2:
            // Filter by Project Name
            System.out.print("Enter Project Name: ");
            String projectName = sc.nextLine();
            filteredProjects = availableProjects.stream()
                .filter(p -> p.getProjectName().toLowerCase().contains(projectName.toLowerCase()))
                .collect(Collectors.toList());
            break;
        case 3:
            // Filter by Neighborhood
            System.out.print("Enter Neighborhood: ");
            String neighborhood = sc.nextLine();
            filteredProjects = availableProjects.stream()
                .filter(p -> p.getNeighborhood().toLowerCase().contains(neighborhood.toLowerCase()))
                .collect(Collectors.toList());
            break;
        case 4:
            // Filter by Flat Type
            System.out.println("\nSelect Flat Type:");
            System.out.println("1. 2-Room");
            System.out.println("2. 3-Room");
            System.out.println("3. 4-Room");
            System.out.println("4. 5-Room");
            System.out.println("5. Executive");
            
            System.out.print("\nEnter your choice: ");
            int typeChoice;
            try {
                typeChoice = Integer.parseInt(sc.nextLine());
                if (typeChoice < 1 || typeChoice > 5) {
                    System.out.println("Invalid choice.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }
            
            FlatType selectedType = null;
            switch (typeChoice) {
                case 1: selectedType = FlatType.TWO_ROOM; break;
                case 2: selectedType = FlatType.THREE_ROOM; break;
            }
            
            final FlatType finalSelectedType = selectedType;
            filteredProjects = availableProjects.stream()
                .filter(p -> p.getFlatTypes().containsKey(finalSelectedType) && 
                        p.getFlatTypes().get(finalSelectedType).getUnits() > 0)
                .collect(Collectors.toList());
            break;
        case 5:
            // Filter by Number of Units
            System.out.print("Enter minimum number of units: ");
            int minUnits;
            try {
                minUnits = Integer.parseInt(sc.nextLine());
                if (minUnits < 0) {
                    System.out.println("Invalid input. Number of units cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }
            
            filteredProjects = availableProjects.stream()
                .filter(p -> p.getFlatTypes().values().stream()
                        .anyMatch(details -> details.getUnits() >= minUnits))
                .collect(Collectors.toList());
            break;
        
        case 6:
            // Filter by Application Period
            System.out.print("Enter start date (YYYY-MM-DD): ");
            LocalDate startDate;
            try {
                startDate = LocalDate.parse(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                return;
            }
            
            System.out.print("Enter end date (YYYY-MM-DD): ");
            LocalDate endDate;
            try {
                endDate = LocalDate.parse(sc.nextLine());
                if (endDate.isBefore(startDate)) {
                    System.out.println("Invalid input. End date cannot be before start date.");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                return;
            }
            
            filteredProjects = availableProjects.stream()
                .filter(p -> 
                    (p.getApplicationOpeningDate().isEqual(startDate) || p.getApplicationOpeningDate().isAfter(startDate)) &&
                    (p.getApplicationClosingDate().isEqual(endDate) || p.getApplicationClosingDate().isBefore(endDate))
                )
                .collect(Collectors.toList());
            break;
        case 7:
            // Filter by Manager
            System.out.print("Enter Manager name: ");
            String managerName = sc.nextLine();
            filteredProjects = availableProjects.stream()
                .filter(p -> p.getHDBManager() != null && 
                        p.getHDBManager().getName().toLowerCase().contains(managerName.toLowerCase()))
                .collect(Collectors.toList());
            break;
        case 8:
            // Filter by Officer Slots
            System.out.print("Enter minimum number of officer slots: ");
            int minSlots;
            try {
                minSlots = Integer.parseInt(sc.nextLine());
                if (minSlots < 0) {
                    System.out.println("Invalid input. Number of slots cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }
            
            filteredProjects = availableProjects.stream()
                .filter(p -> p.getHDBOfficerSlots() >= minSlots)
                .collect(Collectors.toList());
            break;
        case 9:
            // Filter by Number of Officers
            System.out.print("Enter minimum number of officers: ");
            int minOfficers;
            try {
                minOfficers = Integer.parseInt(sc.nextLine());
                if (minOfficers < 0) {
                    System.out.println("Invalid input. Number of officers cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }
            
            filteredProjects = availableProjects.stream()
                .filter(p -> p.getHDBOfficers() != null && p.getHDBOfficers().size() >= minOfficers)
                .collect(Collectors.toList());
            break;
        default:
            System.out.println("Invalid choice. Showing all projects.");
            break;
    }
    
    if (filteredProjects.isEmpty()) {
        System.out.println("\nNo projects match your filter criteria.");
        return;
    }
    
    System.out.println("\nFiltered BTO Projects:");
    for (BTOProject project : filteredProjects) {
        projectView.displayProjectInfo(project);
        System.out.println("----------------------------------------");
    }
}

protected void applyForBTOProject() {
Applicant applicant = (Applicant) AuthStore.getCurrentUser();

List<BTOProject> availableProjects = projectService.getAvailableProjects(applicant);

if (availableProjects.isEmpty()) {
System.out.println("\nNo available BTO projects at the moment.");
return;
}

System.out.print("Enter the project name you want to apply for (Enter X to cancel): ");
String projectName = sc.nextLine();

if (projectName.equalsIgnoreCase("X")) {
return;
}

BTOProject selectedProject = null;
for (BTOProject project : availableProjects) {
if (project.getProjectName().equals(projectName)) {
selectedProject = project;
break;
}
}

if (selectedProject == null) {
System.out.println("Invalid project name. Please try again.");
return;
}

if (!projectService.isEligible(applicant, selectedProject)) {
System.out.println("\nYou are not eligible to apply for this project.");
if (!selectedProject.isVisible()) {
System.out.println("This project is not visible.");
}
if (LocalDate.now().isBefore(selectedProject.getApplicationOpeningDate()) ||
LocalDate.now().isAfter(selectedProject.getApplicationClosingDate())) {
System.out.println("This project is not within its application period.");
System.out.println("Application period: " + selectedProject.getApplicationOpeningDate() +
" to " + selectedProject.getApplicationClosingDate());
}
if (projectService.hasExistingApplication(applicant)) {
System.out.println("You already have an existing BTO application.");
}
return;
}

BTOApplication application = new BTOApplication(applicant, selectedProject);
projectService.applyForBTOProject(application);

System.out.println("Application submitted successfully. An HDB officer will contact you for flat booking if your application is successful.");
}

protected void viewMyBTOApplications() {
Applicant applicant = (Applicant) AuthStore.getCurrentUser();
List<BTOApplication> applications = projectService.getApplicationsByApplicant(applicant);

if (applications.isEmpty()) {
System.out.println("\nYou have no BTO applications at the moment.");
return;
}

System.out.println("\nYour BTO Applications:");
for (BTOApplication application : applications) {
applicationView.displayApplicationInfo(application);
}
}

protected void submitEnquiry() {
Applicant applicant = (Applicant) AuthStore.getCurrentUser();

List<BTOProject> availableProjects = projectService.getAvailableProjects(applicant);



System.out.println("\nAvailable BTO Projects:");
for (int i = 0; i < availableProjects.size(); i++) {
BTOProject project = availableProjects.get(i);
System.out.println((i + 1) + ". " + project.getProjectName());
}

System.out.print("\nEnter project number (0 to cancel): ");
int projectChoice;
try {
projectChoice = Integer.parseInt(sc.nextLine());
if (projectChoice == 0) {
return;
}
if (projectChoice < 1 || projectChoice > availableProjects.size()) {
System.out.println("Invalid project number.");
return;
}
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
return;
}

BTOProject selectedProject = availableProjects.get(projectChoice - 1);

System.out.print("Enter your enquiry message: ");
String message = sc.nextLine();

if (message.trim().isEmpty()) {
System.out.println("Enquiry message cannot be empty.");
return;
}

Enquiry enquiry = enquiryService.createEnquiry(applicant, selectedProject, message);

System.out.println("\nEnquiry submitted successfully!");
System.out.println("Enquiry ID: " + enquiry.getEnquiryId());
}

protected void viewMyEnquiries() {
Applicant applicant = (Applicant) AuthStore.getCurrentUser();

List<Enquiry> myEnquiries = enquiryService.getEnquiriesByApplicant(applicant);

if (myEnquiries.isEmpty()) {
System.out.println("\nYou have not submitted any enquiries.");
return;
}

System.out.println("\nYour Enquiries:");
for (Enquiry enquiry : myEnquiries) {
System.out.println("\nEnquiry ID: " + enquiry.getEnquiryId());
System.out.println("Project: " + enquiry.getProject().getProjectName());
System.out.println("Message: " + enquiry.getMessage());
System.out.println("Submitted: " + enquiry.getCreatedAt());

if (enquiry.getReply() != null) {
System.out.println("Reply: " + enquiry.getReply());
System.out.println("Replied: " + enquiry.getRepliedAt());
} else {
System.out.println("Status: Pending reply");
}
System.out.println("----------------------------------------");
}
}

protected void withdrawBTOApplication() {
Applicant applicant = (Applicant) AuthStore.getCurrentUser();

List<BTOApplication> myApplications = projectService.getApplicationsByApplicant(applicant);

if (myApplications.isEmpty()) {
System.out.println("\nYou have no BTO applications to withdraw.");
return;
}

System.out.println("\n===== Your BTO Applications =====");
for (int i = 0; i < myApplications.size(); i++) {
BTOApplication application = myApplications.get(i);
System.out.println("\n" + (i + 1) + ". Application ID: " + application.getApplicationId());
System.out.println("   Project: " + application.getProject().getProjectName());
System.out.println("   Status: " + application.getStatus());
if (application.getFlatType() != null) {
System.out.println("   Flat Type: " + application.getFlatType().getDisplayName());
}
System.out.println("----------------------------------------");
}

System.out.print("\nEnter application number to withdraw (0 to cancel): ");
int choice;
try {
choice = Integer.parseInt(sc.nextLine());
if (choice == 0) {
return;
}
if (choice < 1 || choice > myApplications.size()) {
System.out.println("Invalid application number.");
return;
}
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
return;
}

BTOApplication selectedApplication = myApplications.get(choice - 1);

if (selectedApplication.getStatus() == BTOApplicationStatus.BOOKED) {
System.out.println("\nCannot withdraw an application that has already been booked.");
return;
}

boolean hasPendingRequest = DataStore.getWithdrawalRequestsData().values().stream()
.anyMatch(request -> request.getApplication().equals(selectedApplication) && !request.isApproved());

if (hasPendingRequest) {
System.out.println("\nYou already have a pending withdrawal request for this application.");
return;
}

System.out.print("Are you sure you want to withdraw your application for " +
selectedApplication.getProject().getProjectName() + "? (yes/no): ");
String confirmation = sc.nextLine().toLowerCase();

if (!confirmation.equals("yes")) {
System.out.println("Withdrawal cancelled.");
return;
}

WithdrawalRequest request = new WithdrawalRequest(selectedApplication);
DataStore.getWithdrawalRequestsData().put(request.getRequestId(), request);
DataStore.saveData();

System.out.println("\nWithdrawal request submitted successfully!");
System.out.println("Request ID: " + request.getRequestId());
System.out.println("Please wait for the HDB manager to process your request.");
}

protected void requestFlatBooking() {
Applicant applicant = (Applicant) AuthStore.getCurrentUser();

List<BTOApplication> successfulApplications = DataStore.getBTOApplicationsData().values().stream()
.filter(application -> application.getApplicant().equals(applicant) &&
application.getStatus() == BTOApplicationStatus.SUCCESSFUL)
.collect(Collectors.toList());

if (successfulApplications.isEmpty()) {
System.out.println("\nYou have no successful applications to request booking for.");
return;
}

boolean hasBookedFlat = DataStore.getBTOApplicationsData().values().stream()
.anyMatch(app -> app.getApplicant().equals(applicant) &&
app.getStatus() == BTOApplicationStatus.BOOKED);

if (hasBookedFlat) {
System.out.println("\nYou already have a booked flat.");
return;
}

System.out.println("\n===== Your Successful Applications =====");
for (int i = 0; i < successfulApplications.size(); i++) {
BTOApplication application = successfulApplications.get(i);
BTOProject project = application.getProject();
Map<FlatType, FlatTypeDetails> eligibleFlatTypes = projectService.getEligibleFlatTypes(project, applicant);

System.out.println("\n" + (i + 1) + ". Application ID: " + application.getApplicationId());
System.out.println("   Project: " + project.getProjectName());
if (application.getFlatType() != null) {
System.out.println("   Flat Type: " + application.getFlatType().getDisplayName());
System.out.println("   Status: Booking request submitted, waiting for HDB officer approval");
} else {
System.out.println("   Available Flat Types: " + getEligibleFlatTypesString(eligibleFlatTypes));
}
System.out.println("----------------------------------------");
}

System.out.print("\nEnter application number to request booking (0 to cancel): ");
int choice;
try {
choice = Integer.parseInt(sc.nextLine());
if (choice == 0) {
return;
}
if (choice < 1 || choice > successfulApplications.size()) {
System.out.println("Invalid application number.");
return;
}
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
return;
}

BTOApplication selectedApplication = successfulApplications.get(choice - 1);

if (selectedApplication.getFlatType() != null) {
System.out.println("\nYou have already submitted a booking request for this application.");
System.out.println("Please wait for the HDB officer to process your request.");
return;
}

BTOProject project = selectedApplication.getProject();

Map<FlatType, FlatTypeDetails> eligibleFlatTypes = projectService.getEligibleFlatTypes(project, applicant);

System.out.println("\nAvailable Flat Types for " + project.getProjectName() + ":");
for (Map.Entry<FlatType, FlatTypeDetails> entry : eligibleFlatTypes.entrySet()) {
if (entry.getValue().getUnits() > 0) {
System.out.println(entry.getKey().getDisplayName() + " - " + entry.getValue().getUnits() + " units available");
}
}

System.out.print("\nEnter flat type to book (0 to cancel): ");
String flatTypeInput = sc.nextLine();
if (flatTypeInput.equals("0")) {
return;
}

FlatType selectedFlatType = null;
for (FlatType flatType : eligibleFlatTypes.keySet()) {
if (flatType.getDisplayName().equalsIgnoreCase(flatTypeInput)) {
selectedFlatType = flatType;
break;
}
}

if (selectedFlatType == null) {
System.out.println("Invalid flat type or you are not eligible for this flat type.");
return;
}

FlatTypeDetails flatTypeDetails = eligibleFlatTypes.get(selectedFlatType);
if (flatTypeDetails == null || flatTypeDetails.getUnits() <= 0) {
System.out.println("\nNo units available for the selected flat type.");
return;
}

System.out.print("Confirm booking request for " + project.getProjectName() + " (" + selectedFlatType.getDisplayName() + ")? (yes/no): ");
String confirmation = sc.nextLine().toLowerCase();

if (!confirmation.equals("yes")) {
System.out.println("Booking request cancelled.");
return;
}

selectedApplication.setFlatType(selectedFlatType);

DataStore.saveData();

System.out.println("\nFlat booking request submitted successfully!");
System.out.println("An HDB officer will process your request shortly.");
System.out.println("Application ID: " + selectedApplication.getApplicationId());
System.out.println("Project: " + project.getProjectName());
System.out.println("Flat Type: " + selectedFlatType.getDisplayName());
}

private String getEligibleFlatTypesString(Map<FlatType, FlatTypeDetails> eligibleFlatTypes) {
StringBuilder availableTypes = new StringBuilder();
for (Map.Entry<FlatType, FlatTypeDetails> entry : eligibleFlatTypes.entrySet()) {
if (entry.getValue().getUnits() > 0) {
availableTypes.append(entry.getKey().getDisplayName())
.append(" (")
.append(entry.getValue().getUnits())
.append(" units), ");
}
}

if (availableTypes.length() > 0) {
availableTypes.setLength(availableTypes.length() - 2); // Remove last comma and space
} else {
availableTypes.append("None");
}

return availableTypes.toString();
}
}



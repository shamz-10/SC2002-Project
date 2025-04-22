package controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import enumeration.BTOApplicationStatus;
import enumeration.FlatType;
import enumeration.HDBOfficerRegistrationStatus;
import enumeration.MaritalStatus;
import models.BTOApplication;
import models.BTOProject;
import models.FlatTypeDetails;
import models.HDBManager;
import models.HDBOfficer;
import models.HDBOfficerRegistration;
import models.User;
import models.Applicant;
import models.Enquiry;
import models.WithdrawalRequest;
import services.BTOProjectService;
import services.EnquiryService;
import services.ReportService;
import stores.DataStore;
import view.BTOProjectView;
import view.HDBOfficerRegistrationView;
import view.EnquiryView;
import view.ReportView;
import utils.TextDecorationUtils;
import services.BTOProjectManagementService;
import view.BTOProjectManagementView;

public class HDBManagerController extends UserController {

private static final Scanner sc = new Scanner(System.in);
private final HDBManager hdbManager;
private final BTOProjectService btoProjectService;
private final BTOProjectView projectView;
private final HDBOfficerRegistrationView registrationView;
private final EnquiryView enquiryView;
private final ReportView reportView;
private final EnquiryService enquiryService;
private final ReportService reportService;
private final BTOProjectManagementService projectManagementService;
private final BTOProjectManagementView projectManagementView;

public HDBManagerController(HDBManager hdbManager) {
this.hdbManager = hdbManager;
this.btoProjectService = new BTOProjectService();
this.projectView = new BTOProjectView();
this.registrationView = new HDBOfficerRegistrationView();
this.enquiryView = new EnquiryView();
this.reportView = new ReportView();
this.enquiryService = new EnquiryService();
this.reportService = new ReportService();
this.projectManagementService = new BTOProjectManagementService();
this.projectManagementView = new BTOProjectManagementView();
}

public void start() {
int choice;

do {
System.out.println("=====================================================");
System.out.println(TextDecorationUtils.boldText("Hi, " + hdbManager.getName() + "!"));

System.out.println(TextDecorationUtils.underlineText("PROJECT MANAGEMENT"));
System.out.println("1. Create BTO Project");
System.out.println("2. Edit BTO Project");
System.out.println("3. Delete BTO Project");
System.out.println("4. View All Projects");
System.out.println("5. View My Projects");
System.out.println("6. Toggle Project Visibility");

System.out.println(TextDecorationUtils.underlineText("HDB OFFICER MANAGEMENT"));
System.out.println("7. View HDB Officer Registrations");
System.out.println("8. Approve/Reject HDB Officer Registration");

System.out.println(TextDecorationUtils.underlineText("APPLICATION MANAGEMENT"));
System.out.println("9. View BTO Applications");
System.out.println("10. Approve/Reject BTO Application");
System.out.println("11. Approve/Reject Application Withdrawal");
System.out.println("12. Generate Applicant Report");

System.out.println(TextDecorationUtils.underlineText("ENQUIRY MANAGEMENT"));
System.out.println("13. View All Enquiries");
System.out.println("14. View and Reply to Project Enquiries");

System.out.println(TextDecorationUtils.underlineText("SETTINGS"));
System.out.println("15. Change Password");

System.out.println("\n0. Logout");
System.out.print("Enter your choice: ");

String input = sc.nextLine();
if (input.matches("[0-9]+")) {
choice = Integer.parseInt(input);
if (choice < 0 || choice > 15) {
System.out.println("Invalid input. Please enter 0-15!");
continue;
}
} else {
System.out.println("Invalid input. Please enter an integer.\n");
continue;
}

switch (choice) {
case 0:
System.out.println("Logging out...");
AuthController.endSession();
return;
case 1:
createBTOProject();
break;
case 2:
editBTOProject();
break;
case 3:
deleteBTOProject();
break;
case 4:
viewAllProjects();
break;
case 5:
viewMyProjects();
break;
case 6:
toggleProjectVisibility();
break;
case 7:
viewHDBOfficerRegistrations();
break;
case 8:
approveRejectHDBOfficerRegistration();
break;
case 9:
viewBTOApplications();
break;
case 10:
approveRejectBTOApplication();
break;
case 11:
approveRejectApplicationWithdrawal();
break;
case 12:
generateApplicantReport();
break;
case 13:
viewAllEnquiries();
break;
case 14:
viewAndReplyToProjectEnquiries();
break;
case 15:
changePassword();
break;
default:
System.out.println("Invalid choice. Please try again.");
break;
}
} while (true);
}

private void createBTOProject() {
if (projectManagementService.isHandlingActiveProject(hdbManager)) {
System.out.println("You are already handling a project within an application period.");
return;
}

System.out.println("\n===== Create New BTO Project =====");
Object[] details = projectManagementView.getProjectCreationDetails();
if (details == null) {
System.out.println("Project creation cancelled or invalid input provided.");
return;
}

try {
if (!(details[4] instanceof Map<?, ?>)) {
System.out.println("Invalid flat types data.");
return;
}

Map<?, ?> rawMap = (Map<?, ?>) details[4];
Map<FlatType, FlatTypeDetails> flatTypes = new HashMap<>();

for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
if (!(entry.getKey() instanceof FlatType) || !(entry.getValue() instanceof FlatTypeDetails)) {
System.out.println("Invalid flat types data format.");
return;
}
flatTypes.put((FlatType) entry.getKey(), (FlatTypeDetails) entry.getValue());
}

BTOProject project = projectManagementService.createProject(
(String) details[0],
(String) details[1],
(LocalDate) details[2],
(LocalDate) details[3],
flatTypes,
hdbManager,
(int) details[5]
);

if (project != null) {
System.out.println("\nBTO Project created successfully!");
System.out.println("Project Name: " + project.getProjectName());
System.out.println("Neighborhood: " + project.getNeighborhood());
System.out.println("Application Period: " + project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate());
System.out.println("HDB Officer Slots: " + project.getHDBOfficerSlots());
System.out.println("Flat Types:");
for (Map.Entry<FlatType, FlatTypeDetails> entry : project.getFlatTypes().entrySet()) {
System.out.println("  " + entry.getKey() + ": " + entry.getValue().getUnits() + " units at $" + entry.getValue().getPrice() + " each");
}
} else {
System.out.println("Failed to create project. Please try again.");
}
} catch (Exception e) {
System.out.println("An error occurred while creating the project: " + e.getMessage());
}
}

private void editBTOProject() {
System.out.println("\n===== Edit BTO Project =====");

BTOProject project = selectProject("Select project to edit: ", true);
if (project == null) {
return;
}

LocalDate today = LocalDate.now();
if (today.isAfter(project.getApplicationOpeningDate()) && today.isBefore(project.getApplicationClosingDate())) {
System.out.println("Cannot edit a project that is currently in application period.");
return;
}

System.out.println("\nCurrent project details:");
projectManagementView.displayProjectDetails(project);

int choice = projectManagementView.getEditChoice();
if (choice == -1) {
return;
}

switch (choice) {
case 0:
return;
case 1:
String newName = projectManagementView.getNewProjectName();
projectManagementService.updateProjectName(project, newName);
break;
case 2:
String newNeighborhood = projectManagementView.getNewNeighborhood();
projectManagementService.updateNeighborhood(project, newNeighborhood);
break;
case 3:
case 4:
LocalDate[] newDates = projectManagementView.getNewApplicationDates();
if (newDates != null) {
projectManagementService.updateApplicationDates(project, newDates[0], newDates[1]);
}
break;
case 5:
Map<FlatType, FlatTypeDetails> newFlatTypes = projectManagementView.getFlatTypes();
if (newFlatTypes != null) {
projectManagementService.updateFlatTypes(project, newFlatTypes);
}
break;
case 6:
int newSlots = projectManagementView.getNewHDBOfficerSlots(project.getHDBOfficers().size());
if (newSlots != -1) {
projectManagementService.updateHDBOfficerSlots(project, newSlots);
}
break;
case 7:
boolean newVisibility = projectManagementView.getNewVisibility();
projectManagementService.updateVisibility(project, newVisibility);
break;
default:
System.out.println("Invalid choice.");
return;
}

System.out.println("Project updated successfully!");
}

private void deleteBTOProject() {
System.out.println("\n===== Delete BTO Project =====");

BTOProject project = selectProject("Select project to delete: ", true);
if (project == null) {
return;
}

LocalDate today = LocalDate.now();
if (today.isAfter(project.getApplicationOpeningDate()) && today.isBefore(project.getApplicationClosingDate())) {
System.out.println("Cannot delete a project that is currently in application period.");
return;
}

System.out.println("\nProject details:");
projectManagementView.displayProjectDetails(project);

if (projectManagementView.getDeletionConfirmation()) {
projectManagementService.deleteProject(project);
System.out.println("Project deleted successfully!");
} else {
System.out.println("Deletion cancelled.");
}
}

private void viewAllProjects() {
System.out.println("\n===== All BTO Projects =====");

List<BTOProject> projects = new ArrayList<BTOProject>(DataStore.getBTOProjectsData().values());

if (projects.isEmpty()) {
System.out.println("No projects found.");
return;
}

for (int i = 0; i < projects.size(); i++) {
System.out.println("\nProject " + (i + 1) + ":");
projectManagementView.displayProjectDetails(projects.get(i));
}
}

private void viewMyProjects() {
System.out.println("\n===== My BTO Projects =====");

List<BTOProject> myProjects = projectManagementService.getManagedProjects(hdbManager);

if (myProjects.isEmpty()) {
System.out.println("You have not created any projects.");
return;
}

for (int i = 0; i < myProjects.size(); i++) {
System.out.println("\nProject " + (i + 1) + ":");
projectManagementView.displayProjectDetails(myProjects.get(i));
}
}

private void toggleProjectVisibility() {
System.out.println("\n===== Toggle Project Visibility =====");

BTOProject project = selectProject("Select project to toggle visibility: ", true);
if (project == null) {
return;
}

System.out.println("\nCurrent project details:");
projectManagementView.displayProjectDetails(project);

System.out.println("Current visibility: " + (project.isVisible() ? "Visible" : "Hidden"));
System.out.print("Set visibility to (true/false): ");

try {
boolean newVisibility = Boolean.parseBoolean(sc.nextLine());
projectManagementService.updateVisibility(project, newVisibility);
System.out.println("Project visibility updated successfully!");
} catch (Exception e) {
System.out.println("Invalid input. Please enter true or false.");
}
}

private void viewHDBOfficerRegistrations() {
System.out.println("\n===== HDB Officer Registrations =====");

List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
.filter(project -> project.getHDBManager().equals(hdbManager))
.collect(Collectors.toList());

if (myProjects.isEmpty()) {
System.out.println("You have not created any projects.");
return;
}

List<HDBOfficerRegistration> registrations = DataStore.getHDBOfficerRegistrationsData().values().stream()
.filter(registration -> myProjects.contains(registration.getProject()))
.collect(Collectors.toList());

if (registrations.isEmpty()) {
System.out.println("No HDB Officer registrations found for your projects.");
return;
}

for (int i = 0; i < registrations.size(); i++) {
HDBOfficerRegistration registration = registrations.get(i);
System.out.println("\nRegistration " + (i + 1) + ":");
System.out.println("Registration ID: " + registration.getRegistrationId());
System.out.println("HDB Officer: " + registration.getHDBOfficer().getName() + " (" + registration.getHDBOfficer().getNric() + ")");
System.out.println("Project: " + registration.getProject().getProjectName());
System.out.println("Status: " + registration.getStatus().getDisplayName());
}
}

private void approveRejectHDBOfficerRegistration() {
System.out.println("\n===== Approve/Reject HDB Officer Registration =====");

List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
.filter(project -> project.getHDBManager().equals(hdbManager))
.collect(Collectors.toList());

if (myProjects.isEmpty()) {
System.out.println("You have not created any projects.");
return;
}

List<HDBOfficerRegistration> pendingRegistrations = DataStore.getHDBOfficerRegistrationsData().values().stream()
.filter(registration -> myProjects.contains(registration.getProject()) &&
registration.getStatus() == HDBOfficerRegistrationStatus.PENDING)
.collect(Collectors.toList());

if (pendingRegistrations.isEmpty()) {
System.out.println("No pending HDB Officer registrations found for your projects.");
return;
}

for (int i = 0; i < pendingRegistrations.size(); i++) {
HDBOfficerRegistration registration = pendingRegistrations.get(i);
System.out.println("\nRegistration " + (i + 1) + ":");
System.out.println("Registration ID: " + registration.getRegistrationId());
System.out.println("HDB Officer: " + registration.getHDBOfficer().getName() + " (" + registration.getHDBOfficer().getNric() + ")");
System.out.println("Project: " + registration.getProject().getProjectName());
}

System.out.print("Enter registration number to approve/reject (0 to cancel): ");
int choice = 0;
try {
choice = Integer.parseInt(sc.nextLine());
if (choice == 0) {
return;
}
if (choice < 1 || choice > pendingRegistrations.size()) {
System.out.println("Invalid registration number.");
return;
}
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
return;
}

HDBOfficerRegistration registration = pendingRegistrations.get(choice - 1);
BTOProject project = registration.getProject();
HDBOfficer officer = registration.getHDBOfficer();

if (project.getHDBOfficers().size() >= project.getHDBOfficerSlots()) {
System.out.println("Project has no available HDB Officer slots.");
return;
}

System.out.print("Approve registration? (yes/no): ");
String approval = sc.nextLine().toLowerCase();

if (approval.equals("yes")) {
registration.setStatus(HDBOfficerRegistrationStatus.APPROVED);
project.addHDBOfficer(officer);
officer.addHandledProject(project);
System.out.println("Registration approved successfully!");
} else {
registration.setStatus(HDBOfficerRegistrationStatus.REJECTED);
System.out.println("Registration rejected.");
}

DataStore.saveData();
}

private void viewBTOApplications() {
System.out.println("\n===== BTO Applications =====");

List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
.filter(project -> project.getHDBManager().equals(hdbManager))
.collect(Collectors.toList());

if (myProjects.isEmpty()) {
System.out.println("You have not created any projects.");
return;
}

List<BTOApplication> applications = DataStore.getBTOApplicationsData().values().stream()
.filter(application -> myProjects.contains(application.getProject()))
.collect(Collectors.toList());

if (applications.isEmpty()) {
System.out.println("No BTO applications found for your projects.");
return;
}

for (int i = 0; i < applications.size(); i++) {
BTOApplication application = applications.get(i);
System.out.println("\nApplication " + (i + 1) + ":");
System.out.println("Application ID: " + application.getApplicationId());
System.out.println("Applicant: " + application.getApplicant().getName() + " (" + application.getApplicant().getNric() + ")");
System.out.println("Project: " + application.getProject().getProjectName());
System.out.println("Flat Type: " + (application.getFlatType() != null ? application.getFlatType().getDisplayName() : "Not specified"));
System.out.println("Status: " + application.getStatus().getDisplayName());
}
}

private void approveRejectBTOApplication() {
System.out.println("\n===== Approve/Reject BTO Application =====");

List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
.filter(project -> project.getHDBManager().equals(hdbManager))
.collect(Collectors.toList());

if (myProjects.isEmpty()) {
System.out.println("You have not created any projects.");
return;
}

List<BTOApplication> pendingApplications = DataStore.getBTOApplicationsData().values().stream()
.filter(application -> myProjects.contains(application.getProject()) &&
application.getStatus() == BTOApplicationStatus.PENDING)
.collect(Collectors.toList());

if (pendingApplications.isEmpty()) {
System.out.println("No pending BTO applications found for your projects.");
return;
}

for (int i = 0; i < pendingApplications.size(); i++) {
BTOApplication application = pendingApplications.get(i);
System.out.println("\nApplication " + (i + 1) + ":");
System.out.println("Application ID: " + application.getApplicationId());
System.out.println("Applicant: " + application.getApplicant().getName() + " (" + application.getApplicant().getNric() + ")");
System.out.println("Project: " + application.getProject().getProjectName());

Map<FlatType, FlatTypeDetails> eligibleFlatTypes = btoProjectService.getEligibleFlatTypes(
application.getProject(),
(Applicant) application.getApplicant()
);

System.out.println("Eligible Flat Types:");
for (Map.Entry<FlatType, FlatTypeDetails> entry : eligibleFlatTypes.entrySet()) {
if (entry.getValue().getUnits() > 0) {
System.out.println(entry.getKey().getDisplayName() + " (" + entry.getValue().getUnits() + " units available)");
}
}
}

System.out.print("Enter application number to approve/reject (0 to cancel): ");
int choice = 0;
try {
choice = Integer.parseInt(sc.nextLine());
if (choice == 0) {
return;
}
if (choice < 1 || choice > pendingApplications.size()) {
System.out.println("Invalid application number.");
return;
}
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
return;
}

BTOApplication application = pendingApplications.get(choice - 1);
BTOProject project = application.getProject();
Applicant applicant = (Applicant) application.getApplicant();

Map<FlatType, FlatTypeDetails> eligibleFlatTypes = btoProjectService.getEligibleFlatTypes(project, applicant);

boolean hasAvailableUnits = false;
for (Map.Entry<FlatType, FlatTypeDetails> entry : eligibleFlatTypes.entrySet()) {
if (entry.getValue().getUnits() > 0) {
hasAvailableUnits = true;
break;
}
}

if (!hasAvailableUnits) {
System.out.println("No units available for any eligible flat type in this project.");
return;
}

System.out.print("Approve application? (yes/no): ");
String approval = sc.nextLine().toLowerCase();

if (approval.equals("yes")) {
application.setStatus(BTOApplicationStatus.SUCCESSFUL);
System.out.println("Application approved successfully!");
System.out.println("The applicant can now request to book a flat.");
} else {
application.setStatus(BTOApplicationStatus.UNSUCCESSFUL);
System.out.println("Application rejected.");
}

DataStore.saveData();
}

private void approveRejectApplicationWithdrawal() {
System.out.println("\n===== Approve/Reject Application Withdrawal =====");

List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
.filter(project -> project.getHDBManager().equals(hdbManager))
.collect(Collectors.toList());

if (myProjects.isEmpty()) {
System.out.println("You have not created any projects.");
return;
}

// Get all withdrawal requests for the manager's projects
List<WithdrawalRequest> pendingRequests = DataStore.getWithdrawalRequestsData().values().stream()
.filter(request -> {
BTOProject project = request.getApplication().getProject();
return myProjects.contains(project) && 
       !request.isApproved() && 
       request.getProcessedAt() == null;
})
.collect(Collectors.toList());

if (pendingRequests.isEmpty()) {
System.out.println("No pending withdrawal requests found for your projects.");
return;
}

for (int i = 0; i < pendingRequests.size(); i++) {
WithdrawalRequest request = pendingRequests.get(i);
BTOApplication application = request.getApplication();
System.out.println("\nRequest " + (i + 1) + ":");
System.out.println("Request ID: " + request.getRequestId());
System.out.println("Application ID: " + application.getApplicationId());
System.out.println("Applicant: " + application.getApplicant().getName() + " (" + application.getApplicant().getNric() + ")");
System.out.println("Project: " + application.getProject().getProjectName());
System.out.println("Requested At: " + request.getRequestedAt());
System.out.println("Status: Pending");
System.out.println("----------------------------------------");
}

System.out.print("\nEnter request number to approve/reject (0 to cancel): ");
int choice;
try {
choice = Integer.parseInt(sc.nextLine());
if (choice == 0) {
return;
}
if (choice < 1 || choice > pendingRequests.size()) {
System.out.println("Invalid request number.");
return;
}
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
return;
}

WithdrawalRequest selectedRequest = pendingRequests.get(choice - 1);
BTOApplication application = selectedRequest.getApplication();

if (application.getStatus() == BTOApplicationStatus.BOOKED) {
System.out.println("\nCannot process withdrawal for an application that has already been booked.");
return;
}

System.out.print("Approve withdrawal request? (yes/no): ");
String approval = sc.nextLine().toLowerCase();

if (approval.equals("yes")) {
selectedRequest.approve(hdbManager.getName());
System.out.println("Withdrawal request approved successfully!");
System.out.println("The application has been marked as unsuccessful.");
} else {
selectedRequest.reject(hdbManager.getName());
System.out.println("Withdrawal request rejected.");
}

DataStore.saveData();
}

private void generateApplicantReport() {
System.out.println("\n===== Generate Applicant Report =====");

List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
.filter(project -> project.getHDBManager().equals(hdbManager))
.collect(Collectors.toList());

if (myProjects.isEmpty()) {
System.out.println("You have not created any projects.");
return;
}

List<BTOApplication> successfulApplications = reportService.getAllSuccessfulApplications();

if (successfulApplications.isEmpty()) {
System.out.println("No successful BTO applications found for your projects.");
return;
}

int filterChoice = reportView.displayFilterOptions();
if (filterChoice == -1) {
return;
}

List<BTOApplication> filteredApplications = new ArrayList<>(successfulApplications);

switch (filterChoice) {
case 1:
break;
case 2:
int projectChoice = reportView.getProjectSelection(myProjects);
if (projectChoice == -1) {
return;
}
filteredApplications = reportService.filterByProject(filteredApplications, myProjects.get(projectChoice - 1));
break;
case 3:
FlatType flatType = reportView.getFlatTypeSelection();
if (flatType == null) {
return;
}
filteredApplications = reportService.filterByFlatType(filteredApplications, flatType);
break;
case 4:
MaritalStatus maritalStatus = reportView.getMaritalStatusSelection();
if (maritalStatus == null) {
return;
}
filteredApplications = reportService.filterByMaritalStatus(filteredApplications, maritalStatus);
break;
case 5:
int[] ageRange = reportView.getAgeRange();
if (ageRange == null) {
return;
}
filteredApplications = reportService.filterByAgeRange(filteredApplications, ageRange[0], ageRange[1]);
break;
default:
System.out.println("Invalid choice.");
return;
}

reportView.displayReport(filteredApplications);
}

private void viewAllEnquiries() {
System.out.println("\n===== All Enquiries =====");

List<Enquiry> allEnquiries = enquiryService.getAllEnquiries();

if (allEnquiries.isEmpty()) {
System.out.println("There are no enquiries in the system.");
return;
}

for (int i = 0; i < allEnquiries.size(); i++) {
Enquiry enquiry = allEnquiries.get(i);
System.out.println("\n" + (i + 1) + ". Enquiry ID: " + enquiry.getEnquiryId());
System.out.println("   Project: " + enquiry.getProject().getProjectName());
System.out.println("   Applicant: " + enquiry.getApplicant().getName() + " (" + enquiry.getApplicant().getNric() + ")");
System.out.println("   Message: " + enquiry.getMessage());
System.out.println("   Submitted: " + enquiry.getCreatedAt());
if (enquiry.hasReply()) {
System.out.println("   Reply: " + enquiry.getReply());
System.out.println("   Replied: " + enquiry.getRepliedAt());
} else {
System.out.println("   Status: Pending reply");
}
System.out.println("----------------------------------------");
}
}

private void viewAndReplyToProjectEnquiries() {
List<BTOProject> projects = new ArrayList<BTOProject>(DataStore.getBTOProjectsData().values());

if (projects.isEmpty()) {
System.out.println("\nThere are no BTO projects available.");
return;
}

System.out.println("\n===== BTO Projects =====");
for (int i = 0; i < projects.size(); i++) {
BTOProject project = projects.get(i);
System.out.println((i + 1) + ". " + project.getProjectName());
}

System.out.print("\nEnter project number: ");
int projectChoice;
try {
projectChoice = Integer.parseInt(sc.nextLine());
if (projectChoice < 1 || projectChoice > projects.size()) {
System.out.println("Invalid project number.");
return;
}
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
return;
}

BTOProject selectedProject = projects.get(projectChoice - 1);

List<Enquiry> projectEnquiries = enquiryService.getEnquiriesByProject(selectedProject);

if (projectEnquiries.isEmpty()) {
System.out.println("\nThere are no enquiries for this project.");
return;
}

System.out.println("\n===== Project Enquiries =====");
for (int i = 0; i < projectEnquiries.size(); i++) {
Enquiry enquiry = projectEnquiries.get(i);
System.out.println("\n" + (i + 1) + ". Enquiry ID: " + enquiry.getEnquiryId());
System.out.println("   Applicant: " + enquiry.getApplicant().getName() + " (" + enquiry.getApplicant().getNric() + ")");
System.out.println("   Message: " + enquiry.getMessage());
System.out.println("   Submitted: " + enquiry.getCreatedAt());
if (enquiry.hasReply()) {
System.out.println("   Reply: " + enquiry.getReply());
System.out.println("   Replied: " + enquiry.getRepliedAt());
} else {
System.out.println("   Status: Pending reply");
}
System.out.println("----------------------------------------");
}

System.out.print("\nEnter enquiry number to reply (0 to cancel): ");
int choice;
try {
choice = Integer.parseInt(sc.nextLine());
if (choice == 0) {
return;
}
if (choice < 1 || choice > projectEnquiries.size()) {
System.out.println("Invalid enquiry number.");
return;
}
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
return;
}

Enquiry selectedEnquiry = projectEnquiries.get(choice - 1);

if (selectedEnquiry.hasReply()) {
System.out.println("\nThis enquiry has already been replied to.");
return;
}

System.out.print("Enter your reply: ");
String reply = sc.nextLine();

if (reply.trim().isEmpty()) {
System.out.println("Reply cannot be empty.");
return;
}

if (enquiryService.replyToEnquiry(selectedEnquiry, reply)) {
System.out.println("\nReply submitted successfully!");
} else {
System.out.println("\nFailed to submit reply.");
}
}

private boolean isHandlingActiveProject() {
LocalDate today = LocalDate.now();

return DataStore.getBTOProjectsData().values().stream()
.filter(project -> project.getHDBManager().equals(hdbManager))
.anyMatch(project ->
!today.isBefore(project.getApplicationOpeningDate()) &&
!today.isAfter(project.getApplicationClosingDate())
);
}

private BTOProject selectProject(String prompt, boolean myProjectsOnly) {
List<BTOProject> projects;

if (myProjectsOnly) {
projects = DataStore.getBTOProjectsData().values().stream()
.filter(project -> project.getHDBManager().equals(hdbManager))
.collect(Collectors.toList());
} else {
projects = new ArrayList<BTOProject>(DataStore.getBTOProjectsData().values());
}

if (projects.isEmpty()) {
System.out.println("No projects found.");
return null;
}

System.out.println("\nAvailable projects:");
for (int i = 0; i < projects.size(); i++) {
BTOProject project = projects.get(i);
System.out.println((i + 1) + ". " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
}

System.out.print(prompt);
int choice = 0;
try {
choice = Integer.parseInt(sc.nextLine());
if (choice < 1 || choice > projects.size()) {
System.out.println("Invalid project number.");
return null;
}
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
return null;
}

return projects.get(choice - 1);
}

private void displayProjectDetails(BTOProject project) {
System.out.println("Project Name: " + project.getProjectName());
System.out.println("Neighborhood: " + project.getNeighborhood());
System.out.println("Application Opening Date: " + project.getApplicationOpeningDate());
System.out.println("Application Closing Date: " + project.getApplicationClosingDate());
System.out.println("HDB Manager: " + project.getHDBManager().getName() + " (" + project.getHDBManager().getNric() + ")");
System.out.println("HDB Officer Slots: " + project.getHDBOfficerSlots());
System.out.println("HDB Officers: " + project.getHDBOfficers().size());
System.out.println("Visible: " + (project.isVisible() ? "Yes" : "No"));

System.out.println("Flat Types:");
for (Map.Entry<FlatType, FlatTypeDetails> entry : project.getFlatTypes().entrySet()) {
System.out.println("  " + entry.getKey().getDisplayName() + ": " + entry.getValue().getUnits() + " units, $" + entry.getValue().getPrice());
}
}
}
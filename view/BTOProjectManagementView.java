package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import enumeration.FlatType;
import models.BTOProject;
import models.FlatTypeDetails;

public class BTOProjectManagementView {
private static final Scanner sc = new Scanner(System.in);
private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

public Object[] getProjectCreationDetails() {
System.out.println("\n===== Create BTO Project =====");

System.out.print("Enter project name: ");
String projectName = sc.nextLine();
if (projectName.trim().isEmpty()) {
System.out.println("Project name cannot be empty.");
return null;
}

System.out.print("Enter neighborhood (e.g. Yishun, Boon Lay): ");
String neighborhood = sc.nextLine();
if (neighborhood.trim().isEmpty()) {
System.out.println("Neighborhood cannot be empty.");
return null;
}

System.out.println("\nEnter application dates (format: yyyy-MM-dd)");
LocalDate openingDate = getDate("Enter application opening date: ");
if (openingDate == null) {
System.out.println("Invalid opening date.");
return null;
}

LocalDate closingDate = getDate("Enter application closing date: ");
if (closingDate == null) {
System.out.println("Invalid closing date.");
return null;
}

if (closingDate.isBefore(openingDate)) {
System.out.println("Closing date must be after opening date.");
return null;
}

System.out.println("\nEnter flat types and details:");
Map<FlatType, FlatTypeDetails> flatTypes = getFlatTypes();
if (flatTypes == null || flatTypes.isEmpty()) {
System.out.println("At least one flat type must be specified.");
return null;
}

System.out.println("\nEnter HDB Officer slots:");
int hdbOfficerSlots = getHDBOfficerSlots();
if (hdbOfficerSlots == -1) {
System.out.println("Invalid HDB Officer slots.");
return null;
}

return new Object[]{projectName, neighborhood, openingDate, closingDate, flatTypes, hdbOfficerSlots};
}

private LocalDate getDate(String prompt) {
while (true) {
System.out.print(prompt);
String input = sc.nextLine();
if (input.trim().isEmpty()) {
System.out.println("Date cannot be empty.");
continue;
}
try {
return LocalDate.parse(input, formatter);
} catch (DateTimeParseException e) {
System.out.println("Invalid date format. Please use yyyy-MM-dd (e.g. 2024-12-31).");
}
}
}

public Map<FlatType, FlatTypeDetails> getFlatTypes() {
Map<FlatType, FlatTypeDetails> flatTypes = new HashMap<>();

System.out.println("\nAvailable flat types:");
for (FlatType type : FlatType.values()) {
System.out.println(type.ordinal() + 1 + ". " + type.getDisplayName());
}

System.out.println("\nEnter flat type details (format: typeNumber,units,price)");
System.out.println("Example: 1,10,350000.0 for 10 units of 2-Room at $350,000 each");
System.out.println("Enter 'done' when finished");

while (true) {
System.out.print("\nEnter flat type details: ");
String input = sc.nextLine().trim();

if (input.equalsIgnoreCase("done")) {
if (flatTypes.isEmpty()) {
System.out.println("At least one flat type must be specified.");
continue;
}
break;
}

// Skip empty input
if (input.isEmpty()) {
continue;
}

try {
String[] parts = input.split(",");
if (parts.length != 3) {
System.out.println("Invalid format. Please use: typeNumber,units,price");
continue;
}

// Validate type number
int typeNumber;
try {
typeNumber = Integer.parseInt(parts[0].trim());
} catch (NumberFormatException e) {
System.out.println("Invalid flat type number. Please enter a number between 1 and " + FlatType.values().length);
continue;
}

if (typeNumber < 1 || typeNumber > FlatType.values().length) {
System.out.println("Invalid flat type number. Please enter a number between 1 and " + FlatType.values().length);
continue;
}

FlatType selectedType = FlatType.values()[typeNumber - 1];
if (flatTypes.containsKey(selectedType)) {
System.out.println("This flat type has already been added.");
continue;
}

// Validate units
int units;
try {
units = Integer.parseInt(parts[1].trim());
} catch (NumberFormatException e) {
System.out.println("Invalid number of units. Please enter a positive number.");
continue;
}

if (units <= 0) {
System.out.println("Number of units must be positive.");
continue;
}

// Validate price
double price;
try {
price = Double.parseDouble(parts[2].trim());
} catch (NumberFormatException e) {
System.out.println("Invalid price. Please enter a positive number.");
continue;
}

if (price <= 0) {
System.out.println("Price must be positive.");
continue;
}

flatTypes.put(selectedType, new FlatTypeDetails(units, price));
System.out.println("Flat type added successfully: " + selectedType.getDisplayName() + 
                 " (" + units + " units at $" + price + " each)");

} catch (Exception e) {
System.out.println("Invalid input. Please use the format: typeNumber,units,price");
System.out.println("Example: 1,10,350000.0");
}
}

return flatTypes;
}

private int getHDBOfficerSlots() {
while (true) {
System.out.print("Enter number of HDB Officer slots (max 10): ");
try {
int slots = Integer.parseInt(sc.nextLine());
if (slots < 0 || slots > 10) {
System.out.println("HDB Officer slots must be between 0 and 10.");
continue;
}
return slots;
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
}
}
}

public void displayProjectDetails(BTOProject project) {
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

public int getEditChoice() {
System.out.println("\nWhat would you like to edit?");
System.out.println("1. Project Name");
System.out.println("2. Neighborhood");
System.out.println("3. Application Opening Date");
System.out.println("4. Application Closing Date");
System.out.println("5. Flat Types and Units");
System.out.println("6. HDB Officer Slots");
System.out.println("7. Visibility");
System.out.println("0. Cancel");
System.out.print("Enter your choice: ");

try {
return Integer.parseInt(sc.nextLine());
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
return -1;
}
}

public String getNewProjectName() {
System.out.print("Enter new project name: ");
return sc.nextLine();
}

public String getNewNeighborhood() {
System.out.print("Enter new neighborhood: ");
return sc.nextLine();
}

public LocalDate[] getNewApplicationDates() {
LocalDate openingDate = getDate("Enter new application opening date (yyyy-MM-dd): ");
if (openingDate == null) return null;

LocalDate closingDate = getDate("Enter new application closing date (yyyy-MM-dd): ");
if (closingDate == null) return null;

if (closingDate.isBefore(openingDate)) {
System.out.println("Closing date must be after opening date.");
return null;
}

return new LocalDate[]{openingDate, closingDate};
}

public int getNewHDBOfficerSlots(int currentOfficers) {
System.out.print("Enter new number of HDB Officer slots (max 10): ");
try {
int newSlots = Integer.parseInt(sc.nextLine());
if (newSlots < currentOfficers) {
System.out.println("HDB Officer slots must be at least the number of current officers.");
return -1;
}
if (newSlots > 10) {
System.out.println("HDB Officer slots cannot exceed 10.");
return -1;
}
return newSlots;
} catch (NumberFormatException e) {
System.out.println("Invalid input. Please enter a number.");
return -1;
}
}

public boolean getNewVisibility() {
System.out.print("Set project visibility (true/false): ");
try {
return Boolean.parseBoolean(sc.nextLine());
} catch (Exception e) {
System.out.println("Invalid input. Please enter true or false.");
return false;
}
}

public boolean getDeletionConfirmation() {
System.out.print("Are you sure you want to delete this project? (yes/no): ");
return sc.nextLine().toLowerCase().equals("yes");
}
}
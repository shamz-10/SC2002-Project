package controllers;

import java.util.Scanner;

import interfaces.IAuthService;

import services.AuthApplicantService;
import services.AuthHDBOfficerService;
import services.AuthHDBManagerService;

public class AuthController {
private static final Scanner sc = new Scanner(System.in);

private static IAuthService authService;

private AuthController() {
};

public static void startSession() {
int choice;
boolean authenticated = false;

do {

while (true) {
System.out.println("<Enter 0 to shutdown system>\n");
System.out.println("Login as:");
System.out.println("1. Applicant");
System.out.println("2. HDB Officer");
System.out.println("3. HDB Manager");

String input = sc.nextLine();

if (input.matches("[0-9]+")) { // If the input is an integer, proceed with the code
choice = Integer.parseInt(input);

if (choice < 0 || choice > 3) {
System.out.println("Invalid input. Please enter 0-3!");
} else {
break;
}
} else { // If the input is not an integer, prompt the user to enter again
System.out.println("Invalid input. Please enter an integer.\n");
}

}

switch (choice) {
case 0:
System.out.println("Shutting down BTOMS...");
return;
case 1:
authService = new AuthApplicantService();
break;
case 2:
authService = new AuthHDBOfficerService();
break;
case 3:
authService = new AuthHDBManagerService();
break;
}

String nric, password;

System.out.print("NRIC: ");
nric = sc.nextLine();

if (!isValidNRIC(nric)) {
System.out.println("Invalid NRIC format! NRIC must start with S or T, followed by 7 digits and end with a letter.\n");
continue;
}

System.out.print("Password: ");
password = sc.nextLine();

authenticated = authService.login(nric, password);
if (!authenticated) {
System.out.println("Credentials invalid! Note that NRIC and Password are case-sensitive.\n");
}
} while (!authenticated);
}

public static void endSession() {
if (authService != null) {
authService.logout();
}
authService = null;
}

private static boolean isValidNRIC(String nric) {
if (nric == null || nric.length() != 9) {
return false;
}

char firstChar = nric.charAt(0);
if (firstChar != 'S' && firstChar != 'T') {
return false;
}

for (int i = 1; i < 8; i++) {
if (!Character.isDigit(nric.charAt(i))) {
return false;
}
}

char lastChar = nric.charAt(8);
if (!Character.isLetter(lastChar)) {
return false;
}

return true;
}
}
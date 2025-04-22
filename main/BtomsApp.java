package main;

import controllers.AuthController;
import controllers.ApplicantController;
import controllers.HDBOfficerController;
import controllers.HDBManagerController;
import models.User;
import models.Applicant;
import models.HDBOfficer;
import services.CsvDataService;
import stores.AuthStore;
import stores.DataStore;
import utils.FilePathsUtils;
import view.CommonView;
import utils.FileResetUtils;

import java.util.Scanner;

public class BtomsApp {
    private BtomsApp() {}

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nBTOMS is shutting down...");
            // Reset CSV files to original state
            FileResetUtils.resetCsvFiles();
            AuthController.endSession();
        }));

        try {
            do {
                DataStore.initDataStore(new CsvDataService(), FilePathsUtils.csvFilePaths());

                CommonView.printSplashScreen();

                AuthController.startSession();
                if (!AuthStore.isLoggedIn()) return;

                User user = AuthStore.getCurrentUser();

                switch (user.getUserType()) {
                    case APPLICANT:
                        new ApplicantController().start();
                        break;

                    case HDB_OFFICER:
                        handleHDBOfficerSession((HDBOfficer) user);
                        break;

                    case HDB_MANAGER:
                        new HDBManagerController((models.HDBManager) user).start();
                        break;
                }
            } while (true);

        } catch (Exception e) {
            // Reset CSV files on crash
            FileResetUtils.resetCsvFiles();
            AuthController.endSession();

            System.out.println("BTOMS crashed. Please restart the system.");
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleHDBOfficerSession(HDBOfficer officer) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nYou are logged in as HDB Officer: " + officer.getName());
            System.out.println("Select mode:");
            System.out.println("1. Applicant Mode");
            System.out.println("2. HDB Officer Mode");
            System.out.println("0. Logout");

            System.out.print("Enter choice: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    new ApplicantController().start();
                    break;
                case "2":
                    new HDBOfficerController().start();
                    break;
                case "0":
                    AuthController.endSession();
                    return;
                default:
                    System.out.println("Invalid input. Try again.");
            }
        }
    }
}

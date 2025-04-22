package services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import enumeration.FlatType;
import models.BTOProject;
import models.FlatTypeDetails;
import models.HDBManager;
import stores.DataStore;

public class BTOProjectManagementService {

public BTOProject createProject(
String projectName,
String neighborhood,
LocalDate openingDate,
LocalDate closingDate,
Map<FlatType, FlatTypeDetails> flatTypes,
HDBManager hdbManager,
int hdbOfficerSlots
) {
BTOProject project = new BTOProject(
projectName,
neighborhood,
openingDate,
closingDate,
flatTypes,
hdbManager,
hdbOfficerSlots,
new ArrayList<>(),
true
);

DataStore.getBTOProjectsData().put(projectName, project);
DataStore.saveData();

return project;
}

public void updateProjectName(BTOProject project, String newName) {
DataStore.getBTOProjectsData().remove(project.getProjectName());
project.setProjectName(newName);
DataStore.getBTOProjectsData().put(newName, project);
DataStore.saveData();
}

public void updateNeighborhood(BTOProject project, String newNeighborhood) {
project.setNeighborhood(newNeighborhood);
DataStore.saveData();
}

public void updateApplicationDates(BTOProject project, LocalDate newOpeningDate, LocalDate newClosingDate) {
project.setApplicationOpeningDate(newOpeningDate);
project.setApplicationClosingDate(newClosingDate);
DataStore.saveData();
}

public void updateFlatTypes(BTOProject project, Map<FlatType, FlatTypeDetails> newFlatTypes) {
project.setFlatTypes(newFlatTypes);
DataStore.saveData();
}

public void updateHDBOfficerSlots(BTOProject project, int newSlots) {
project.setHDBOfficerSlots(newSlots);
DataStore.saveData();
}

public void updateVisibility(BTOProject project, boolean newVisibility) {
project.setVisible(newVisibility);
DataStore.saveData();
}

public void deleteProject(BTOProject project) {
DataStore.getBTOProjectsData().remove(project.getProjectName());
DataStore.saveData();
}

public boolean isHandlingActiveProject(HDBManager hdbManager) {
LocalDate today = LocalDate.now();

return DataStore.getBTOProjectsData().values().stream()
.filter(project -> project.getHDBManager().equals(hdbManager))
.anyMatch(project ->
!today.isBefore(project.getApplicationOpeningDate()) &&
!today.isAfter(project.getApplicationClosingDate())
);
}

public List<BTOProject> getManagedProjects(HDBManager hdbManager) {
return DataStore.getBTOProjectsData().values().stream()
.filter(project -> project.getHDBManager().equals(hdbManager))
.collect(Collectors.toList());
}
}
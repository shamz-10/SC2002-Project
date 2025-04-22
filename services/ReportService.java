package services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import enumeration.BTOApplicationStatus;
import enumeration.FlatType;
import enumeration.MaritalStatus;
import models.BTOApplication;
import models.BTOProject;
import stores.DataStore;

public class ReportService {

public List<BTOApplication> getAllSuccessfulApplications() {
return DataStore.getBTOApplicationsData().values().stream()
.filter(application -> application.getStatus() == BTOApplicationStatus.SUCCESSFUL)
.collect(Collectors.toList());
}

public List<BTOApplication> filterByProject(List<BTOApplication> applications, BTOProject project) {
return applications.stream()
.filter(application -> application.getProject().equals(project))
.collect(Collectors.toList());
}

public List<BTOApplication> filterByFlatType(List<BTOApplication> applications, FlatType flatType) {
return applications.stream()
.filter(application -> application.getFlatType() == flatType)
.collect(Collectors.toList());
}

public List<BTOApplication> filterByMaritalStatus(List<BTOApplication> applications, MaritalStatus maritalStatus) {
return applications.stream()
.filter(application -> application.getApplicant().getMaritalStatus() == maritalStatus)
.collect(Collectors.toList());
}

public List<BTOApplication> filterByAgeRange(List<BTOApplication> applications, int minAge, int maxAge) {
return applications.stream()
.filter(application -> {
int age = application.getApplicant().getAge();
return age >= minAge && age <= maxAge;
})
.collect(Collectors.toList());
}
}
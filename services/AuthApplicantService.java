package services;

import java.util.Map;

import models.Applicant;
import stores.AuthStore;
import stores.DataStore;

public class AuthApplicantService extends AuthService {
public AuthApplicantService() {
};

@Override
public boolean login(String nric, String password) {
Map<String, Applicant> applicantData = DataStore.getApplicantsData();

Applicant applicant = applicantData.get(nric);

if (authenticate(applicant, password) == false)
return false;

AuthStore.setCurrentUser(applicant);
return true;
}

}
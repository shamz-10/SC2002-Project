package services;

import java.util.Map;

import models.HDBOfficer;
import stores.AuthStore;
import stores.DataStore;

public class AuthHDBOfficerService extends AuthService {
public AuthHDBOfficerService() {
};

@Override
public boolean login(String nric, String password) {
Map<String, HDBOfficer> hdbOfficerData = DataStore.getHDBOfficersData();

HDBOfficer hdbOfficer = hdbOfficerData.get(nric);

if (authenticate(hdbOfficer, password) == false)
return false;

AuthStore.setCurrentUser(hdbOfficer);
return true;
}

}
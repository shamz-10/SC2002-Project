package services;

import java.util.Map;

import models.HDBManager;
import stores.AuthStore;
import stores.DataStore;

public class AuthHDBManagerService extends AuthService {
public AuthHDBManagerService() {
};

@Override
public boolean login(String nric, String password) {
Map<String, HDBManager> hdbManagerData = DataStore.getHDBManagersData();

HDBManager hdbManager = hdbManagerData.get(nric);

if (authenticate(hdbManager, password) == false)
return false;

AuthStore.setCurrentUser(hdbManager);
return true;
}

}
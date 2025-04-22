package services;

import interfaces.IAuthService;
import models.User;
import stores.AuthStore;

public abstract class AuthService implements IAuthService {
public abstract boolean login(String nric, String password);

public AuthService() {
};

@Override
public boolean logout() {
AuthStore.setCurrentUser(null);
return true;
};

protected boolean authenticate(User user, String password) {
if (user == null)
return false;
if (!user.getPassword().equals(password))
return false;
return true;
}
}
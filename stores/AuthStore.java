package stores;

import models.User;

public class AuthStore {
private static User currentUser;

private AuthStore() {
}

public static void setCurrentUser(User currentUser) {
AuthStore.currentUser = currentUser;
}

public static boolean isLoggedIn() {
return currentUser != null;
}

public static User getCurrentUser() {
return AuthStore.currentUser;
}
}
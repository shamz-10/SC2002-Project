// User class
import java.util.List;

public class User {
    protected String nric;
    protected String password;
    protected String age;
    protected String maritalStatus;

    public User(String nric, String password, String age, String maritalStatus) {
        this.nric = nric;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    public boolean login() {
        return true;
    }

    public String changePassword(String newPassword) {
        this.password = newPassword;
        return "Password changed successfully";
    }
}

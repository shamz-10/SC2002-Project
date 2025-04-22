package interfaces;

public interface IAuthService {
public boolean login(String nric, String password);

public boolean logout();
}
package vttp.miniproject.hodlscout.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.miniproject.hodlscout.login.enums.AuthenticationResult;

@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepo;

    public void saveUser(UserModel newUser) {
        loginRepo.saveUser(newUser);
    }

    public AuthenticationResult authenticateUser(UserModel user) {
        if (!loginRepo.hasUser(user))
            return AuthenticationResult.USER_NOT_FOUND;
        
        if (loginRepo.getPassword(user).equals(user.getPassword()))
            return AuthenticationResult.SUCCESS;
        else
            return AuthenticationResult.INCORRECT_PASSWORD;
    }
    
}

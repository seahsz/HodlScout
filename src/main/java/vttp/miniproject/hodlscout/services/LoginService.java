package vttp.miniproject.hodlscout.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.miniproject.hodlscout.models.User;
import vttp.miniproject.hodlscout.repositories.LoginRepository;

@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepo;

    public void saveUser(User newUser) {
        loginRepo.saveUser(newUser);
    }
    
}

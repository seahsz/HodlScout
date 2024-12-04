package vttp.miniproject.hodlscout.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

import vttp.miniproject.hodlscout.models.User;
import vttp.miniproject.hodlscout.services.LoginService;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Controller
@RequestMapping("/signup")
public class SignUpController {

    @Autowired
    private LoginService loginSvc;

    @GetMapping
    public String getSignUp(Model model) {

        model.addAttribute(TH_USER, new User());

        return "signup";
    }

    @PostMapping
    public String postSignUp(@Valid User newUser, BindingResult binding) {

        if(binding.hasErrors())
            return "signup";

        // Save the new username-password combination to Redis
        loginSvc.saveUser(newUser);

        return "temp";
    }
    
}

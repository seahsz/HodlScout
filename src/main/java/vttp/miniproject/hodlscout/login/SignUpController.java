package vttp.miniproject.hodlscout.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Controller
@RequestMapping("/signup")
public class SignUpController {

    @Autowired
    private LoginService loginSvc;

    @GetMapping
    public String getSignUp(Model model) {

        model.addAttribute(TH_USER, new UserModel());

        return "signup";
    }

    @PostMapping
    public String postSignUp(@Valid @ModelAttribute(name = "user") UserModel newUser, BindingResult binding) {

        if(binding.hasErrors())
            return "signup";

        // Save the new username-password combination to Redis
        loginSvc.saveUser(newUser);

        return "temp";
    }
    
}

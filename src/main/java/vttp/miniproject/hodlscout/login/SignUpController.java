package vttp.miniproject.hodlscout.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Controller
@RequestMapping
public class SignUpController {

    @Autowired
    private LoginService loginSvc;

    @GetMapping("/signup")
    public String getSignUp(Model model, HttpSession session) {

        if (session.getAttribute(SESSION_IS_LOGGED_IN) != null && (boolean) session.getAttribute(SESSION_IS_LOGGED_IN)) {
            return "redirect:/";
        }

        model.addAttribute(TH_USER, new UserModel());

        return "signup";
    }

    @PostMapping("/signup")
    public String postSignUp(@Valid @ModelAttribute(name = "user") UserModel newUser, 
                             BindingResult binding, 
                             Model model, RedirectAttributes redirectAttr) {

        // Check if username is already taken
        // Used rejectValue instead of addError because addError removed the original value of username when 
        // returning the view
        if(!newUser.getUsername().isBlank() && loginSvc.hasUser(newUser)) {
            binding.rejectValue("username", "error.username", "Username already taken");
            return "signup";
        }

        if(binding.hasErrors())
            return "signup";

        // Save the new username-password combination to Redis
        loginSvc.saveUser(newUser);
        redirectAttr.addFlashAttribute(TH_REDIRECT_SIGNUP_TO_HOMEPAGE, true);
        return "redirect:/";
    }
    
}

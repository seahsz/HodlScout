package vttp.miniproject.hodlscout.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vttp.miniproject.hodlscout.login.enums.AuthenticationResult;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginSvc;

    @GetMapping
    public String getLogin(Model model, HttpSession session) {

        if (session.getAttribute(SESSION_IS_LOGGED_IN) != null && (boolean) session.getAttribute(SESSION_IS_LOGGED_IN)) {
            return "redirect:/";
        }

        model.addAttribute(TH_USER, new UserModel());
        return "login";
    }

    // No validation of input field needed per se (only happens for sign up)
    // Note: needed to add name="user" --> if not for some reason binding cannot find the Bean even
    //      though the variable name is the same as the th:object name
    @PostMapping
    public String postLogin(@ModelAttribute(name="user") UserModel user, BindingResult binding,
                            HttpSession session, RedirectAttributes redirectAttr) {
        AuthenticationResult result = loginSvc.authenticateUser(user);

        switch (result) {
            case SUCCESS:
                session.setAttribute(SESSION_IS_LOGGED_IN, true);
                // conditional so that homepage can later display "login successful" message above
                redirectAttr.addFlashAttribute(TH_REDIRECT_LOGIN_TO_HOMEPAGE, true);
                return "redirect:/";

            case USER_NOT_FOUND:
                binding.addError(new FieldError(TH_USER, "username", "Username does not exist"));
                return "login";
        
            case INCORRECT_PASSWORD:
                binding.addError(new FieldError(TH_USER, "password", "Incorrect password"));;
                return "login";

            default:
                return "login";
        }

    }
    
}

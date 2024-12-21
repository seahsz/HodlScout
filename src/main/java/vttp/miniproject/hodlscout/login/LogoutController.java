package vttp.miniproject.hodlscout.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Controller
@RequestMapping
public class LogoutController {

    @GetMapping("/logout")
    public String getLogout(HttpSession session, RedirectAttributes redirectAttr) {

        if (session.getAttribute(SESSION_IS_LOGGED_IN) == null || !(boolean) session.getAttribute(SESSION_IS_LOGGED_IN))
            return "redirect:/";

        session.invalidate();

        redirectAttr.addFlashAttribute(TH_REDIRECT_LOGOUT_TO_HOMEPAGE, true);

        return "redirect:/";
    }
    
}

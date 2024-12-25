package vttp.miniproject.watchlist;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Controller
@RequestMapping
public class WatchlistController {

    @GetMapping("/watchlist")
    public String getWatchlist(HttpSession session, Model model) {

        // check if user is logged in, if not redirect to homepage
        if (session.getAttribute(SESSION_IS_LOGGED_IN) == null || (boolean) session.getAttribute(SESSION_IS_LOGGED_IN)) {
            model.addAttribute(TH_UNAUTHORIZED, true);
        }

        // Get the username from the session
        String username = (String) session.getAttribute(SESSION_USERNAME);

        return "";
    }
    
}

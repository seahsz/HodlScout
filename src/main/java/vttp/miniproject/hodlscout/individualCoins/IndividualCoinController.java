package vttp.miniproject.hodlscout.individualCoins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import vttp.miniproject.hodlscout.utilities.HelperFunctions;
import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Controller
@RequestMapping
public class IndividualCoinController {

    @Autowired
    private IndividualCoinService individualCoinSvc;

    @GetMapping("/coin/{coinId}")
    public String getIndividualCoin(
            Model model,
            HttpSession session,
            @PathVariable(name = "coinId") String coinId) {

        if (!individualCoinSvc.isCoinIdInList(coinId)) {
            HelperFunctions.populateCustomErrorViewModel(
                    model,
                    session,
                    "Failed to Retrieve Coin Details",
                    "Coin %s does not exist or is not supported by this website".formatted(coinId));
            return "customError";
        }

        model.addAttribute(TH_INDIVIDUAL_COIN, individualCoinSvc.fetchApiIndividualCoinData(coinId));
        model.addAttribute(TH_IS_LOGGED_IN, HelperFunctions.isUserLoggedIn(session));

        if (HelperFunctions.isUserLoggedIn(session))
            model.addAttribute(TH_USERNAME, (String) session.getAttribute(SESSION_USERNAME));

        return "individualCoin";
    }

    @GetMapping("/search")
    public String getSearch(
            @RequestParam(required = false) String coinId,
            Model model,
            HttpSession session) {

        if (coinId == null || coinId.isBlank()) {
            HelperFunctions.populateCustomErrorViewModel(
                    model,
                    session,
                    "Failed to Retrieve Coin Details",
                    "No coin was selected");
            return "customError";
        }

        return "redirect:/coin/%s".formatted(coinId);
    }

}

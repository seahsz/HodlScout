package vttp.miniproject.hodlscout.watchlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp.miniproject.hodlscout.homepage.CoinModel;
import vttp.miniproject.hodlscout.utilities.HelperFunctions;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

import java.util.List;

// Functionalities of watchlist
/*
 * 1. Dropdown list
 * 2. Display a table with coins in the watchlist
 * 3. Ability to remove coin from watchlist (with Delete button on the right?)
 * 
 * If user is logged in, when he/she accesses watchlist directly, we will retrieve the 
 * user's watchlist from Redis --> send the ids to API --> obtain coin market data
 */

@Controller
@RequestMapping
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistSvc;

    @GetMapping("/watchlist")
    public String getWatchlist(HttpSession session, Model model) {
        // check if user is logged in, if not show up different view for Watchlist
        if (!HelperFunctions.isUserLoggedIn(session)) {
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "watchlistUnauthorized";
        }

        // Get First watchlist name if there is
        String username = (String) session.getAttribute(SESSION_USERNAME);

        if (!watchlistSvc.watchlistHashKeyExist(username)) {
            model.addAttribute(TH_USERNAME, username);
            model.addAttribute(TH_WATCHLIST_FORM, new WatchlistModel());
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "watchlistNoWatchlist";
        }

        return "redirect:/watchlist/%s".formatted(watchlistSvc.getFirstWatchlistName(username));

    }

    @GetMapping("/watchlist/{watchlistName}")
    public String getWatchlistWithName(@PathVariable String watchlistName,
            HttpSession session,
            Model model) {

        // check if user is logged in, if not show up different view for Watchlist
        if (!HelperFunctions.isUserLoggedIn(session)) {
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "watchlistUnauthorized";
        }

        // Get the username from the session
        String username = (String) session.getAttribute(SESSION_USERNAME);

        // Check if watchlist Hash Key exists - if it does not, return [View 1]
        if (!watchlistSvc.watchlistHashKeyExist(username)) {
            model.addAttribute(TH_WATCHLIST_FORM, new WatchlistModel());
            model.addAttribute(TH_USERNAME, username);
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "watchlistNoWatchlist";
        }

        // Check if watchlist name exists - if it does not, return [View 2]
        // Only happens if someone does /watchlist/(xxx) whr xxx does not exist
        if (!watchlistSvc.watchlistNameExist(username, watchlistName)) {
            HelperFunctions.populateCustomErrorViewModel(model,
                    session,
                    "Failed to Get Watchlist",
                    "Watchlist name does not exist for this user");
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "customError";
        }

        List<CoinModel> coins = watchlistSvc.getWatchlistCoinsMarket(username, watchlistName);

        // If watchlist name exist, but no coins added return [View 3]
        if (coins.isEmpty()) {
            model.addAttribute(TH_USERNAME, username);
            model.addAttribute(TH_WATCHLIST_NAME, watchlistName);
            model.addAttribute(TH_WATCHLIST_NAME_LIST, watchlistSvc.getAllWatchlistNames(username));
            model.addAttribute(TH_WATCHLIST_FORM, new WatchlistModel());
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "watchlistEmpty";

        } else {
            model.addAttribute(TH_COINS, coins);
            model.addAttribute(TH_USERNAME, username);
            model.addAttribute(TH_WATCHLIST_NAME, watchlistName);
            model.addAttribute(TH_WATCHLIST_NAME_LIST, watchlistSvc.getAllWatchlistNames(username));
            model.addAttribute(TH_WATCHLIST_FORM, new WatchlistModel());
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "watchlist";
        }
    }

    // Strictly from watchlistNoWatchlist view [there is another view if watchlist
    // is created in watchlist view]
    @PostMapping("/watchlist/create_first")
    public String postWatchlistCreateFirst(@Valid @ModelAttribute(name = "watchlist_form") WatchlistModel watchlist,
            BindingResult binding,
            HttpSession session,
            Model model) {

        String username = (String) session.getAttribute(SESSION_USERNAME);

        if (binding.hasErrors()) {
            model.addAttribute(TH_USERNAME, username);
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "watchlistNoWatchlist";
        }

        // If watchlist name is already taken
        if (watchlistSvc.watchlistHashKeyExist(username)
                && watchlistSvc.watchlistNameExist(username, watchlist.getWatchlistName())) {
            binding.addError(new FieldError(TH_WATCHLIST_FORM, "watchlistName", "Watchlist name already exists"));
            model.addAttribute(TH_USERNAME, username);
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "watchlistNoWatchlist";
        }

        watchlistSvc.createAndSaveWatchlistToRepo(username, watchlist.getWatchlistName());

        return "redirect:/watchlist/%s".formatted(watchlist.getWatchlistName());
    }

    // Watchlist is created in watchlist view
    @PostMapping("/watchlist/{oldWatchlistName}/create")
    public String postWatchlistCreate(
            @PathVariable String oldWatchlistName,
            @Valid @ModelAttribute(name = "watchlist_form") WatchlistModel newWatchlist,
            BindingResult binding,
            HttpSession session,
            RedirectAttributes redirectAttr,
            Model model) {

        if (!HelperFunctions.isUserLoggedIn(session)) {
            HelperFunctions.populateCustomErrorViewModel(model,
                    session,
                    "Failed to Create Watchlist",
                    "You are not logged in. Please login to use this feature.");
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "customError";
        }

        String username = (String) session.getAttribute(SESSION_USERNAME);

        if (!watchlistSvc.watchlistHashKeyExist(username)
                || !watchlistSvc.watchlistNameExist(username, oldWatchlistName)) {
            HelperFunctions.populateCustomErrorViewModel(model,
                    session,
                    "Failed to Create Watchlist",
                    "The watchlist name in the URL is invalid");
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "customError";
        }

        if (binding.hasErrors()) {
            redirectAttr.addFlashAttribute(TH_WATCHLIST_ERROR_MSG,
                    "Failed to create watchlist: watchlist name was left empty");
            return "redirect:/watchlist/%s".formatted(oldWatchlistName);
        }

        if (watchlistSvc.watchlistNameExist(username, newWatchlist.getWatchlistName())) {
            redirectAttr.addFlashAttribute(TH_WATCHLIST_ERROR_MSG,
                    "Failed to create watchlist: watchlist name already exists");
            return "redirect:/watchlist/%s".formatted(oldWatchlistName);
        }

        watchlistSvc.createAndSaveWatchlistToRepo(username, newWatchlist.getWatchlistName());

        return "redirect:/watchlist/%s".formatted(newWatchlist.getWatchlistName());

    }

    // Add coin to watchlist - if watchlist is not empty
    @PostMapping("/watchlist/{watchlistName}/add_coin")
    public String postWatchlistAddCoin(
            @RequestParam(name = "coinId", required = false) String coinId,
            @PathVariable String watchlistName,
            HttpSession session,
            RedirectAttributes redirectAttr,
            Model model) {

        if (!HelperFunctions.isUserLoggedIn(session)) {
            HelperFunctions.populateCustomErrorViewModel(model,
                    session,
                    "Failed to Add Coin to Watchlist",
                    "You are not logged in. Please login to use this feature.");
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "customError";
        }

        String username = (String) session.getAttribute(SESSION_USERNAME);

        if (!watchlistSvc.watchlistHashKeyExist(username)
                || !watchlistSvc.watchlistNameExist(username, watchlistName)) {
            HelperFunctions.populateCustomErrorViewModel(model,
                    session,
                    "Failed to Add Coin to Watchlist",
                    "The watchlist name in the URL is invalid");
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "customError";
        }

        if (coinId == null || coinId.isEmpty()) {
            redirectAttr.addFlashAttribute(TH_WATCHLIST_ERROR_MSG,
                    "Failed to add coin to watchlist: no coin was selected");
            return "redirect:/watchlist/%s".formatted(watchlistName);
        } else if (!watchlistSvc.isCoinIdInList(coinId)) {
            redirectAttr.addFlashAttribute(TH_WATCHLIST_ERROR_MSG,
                    "Failed to add coin to watchlist: coin id submitted was not valid");
            return "redirect:/watchlist/%s".formatted(watchlistName);
        } else if (watchlistSvc.isCoinIdInWatchlist(coinId, username, watchlistName)) {
            redirectAttr.addFlashAttribute(TH_WATCHLIST_ERROR_MSG,
                    "Failed to add coin to watchlist: coin is already in watchlist");
            return "redirect:/watchlist/%s".formatted(watchlistName);
        }

        watchlistSvc.modifyAndSaveWatchlistToRepo(username, watchlistName, coinId, "add");

        return "redirect:/watchlist/%s".formatted(watchlistName);
    }

    // Delete coin from watchlist
    @PostMapping("/watchlist/{watchlistName}/delete_coin")
    public String postWatchlistDeleteCoin(@RequestParam(name = "coinId") String coinId,
            @PathVariable String watchlistName,
            HttpSession session,
            Model model) {

        if (!HelperFunctions.isUserLoggedIn(session)) {
            HelperFunctions.populateCustomErrorViewModel(model,
                    session,
                    "Failed to Delete Coin from Watchlist",
                    "You are not logged in. Please login to use this feature.");
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "customError";
        }

        String username = (String) session.getAttribute(SESSION_USERNAME);

        if (!watchlistSvc.watchlistHashKeyExist(username)
                || !watchlistSvc.watchlistNameExist(username, watchlistName)) {
            HelperFunctions.populateCustomErrorViewModel(model,
                    session,
                    "Failed to Delete Coin from Watchlist",
                    "The watchlist name in the URL is invalid");
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "customError";
        }

        if (!watchlistSvc.isCoinIdInWatchlist(coinId, username, watchlistName)) {
            HelperFunctions.populateCustomErrorViewModel(model,
                    session,
                    "Failed to Delete Coin from Watchlist",
                    "The coin id provided is invalid / does not exist in the watchlist (%s)".formatted(watchlistName));
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "customError";
        }

        watchlistSvc.modifyAndSaveWatchlistToRepo(username, watchlistName, coinId, "delete");

        return "redirect:/watchlist/%s".formatted(watchlistName);

    }

    // Delete current watchlist
    @PostMapping("/watchlist/{watchlistName}/delete_watchlist")
    public String postWatchlistDeleteWatchlist(
            @PathVariable String watchlistName,
            HttpSession session,
            Model model) {

        if (!HelperFunctions.isUserLoggedIn(session)) {
            HelperFunctions.populateCustomErrorViewModel(model,
                    session,
                    "Failed to Delete Watchlist",
                    "You are not logged in. Please login to use this feature.");
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "customError";
        }

        String username = (String) session.getAttribute(SESSION_USERNAME);

        if (!watchlistSvc.watchlistHashKeyExist(username)
                || !watchlistSvc.watchlistNameExist(username, watchlistName)) {
            HelperFunctions.populateCustomErrorViewModel(model,
                    session,
                    "Failed to Delete Watchlist",
                    "The watchlist name in the URL is invalid");
            model.addAttribute(TH_COIN_NAME_AND_ID_LIST, watchlistSvc.getCoinNameAndIdList());
            return "customError";
        }

        watchlistSvc.deleteWatchlistFromRepo(username, watchlistName);

        return "redirect:/watchlist";
    }

}

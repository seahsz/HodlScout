package vttp.miniproject.hodlscout.homepage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Controller
@RequestMapping()
public class HomepageController {

    @Autowired
    private CryptoService cryptoSvc;

    // Currently fix the pageSize to be 100 [Have not implemented functionality to change page size]
    private final static int pageSize = 100;

    @GetMapping("/")
    public String getIndex(
        @RequestParam(name = "page", defaultValue = "1") int page,
        Model model,
        HttpSession session) {

        List<CoinModel> coins = cryptoSvc.getHomepageCoinsMarket(page, pageSize);

        long totalCoins = cryptoSvc.getCoinsMarketListSize();

        if (session.getAttribute(SESSION_IS_LOGGED_IN) == null) {
            session.setAttribute(SESSION_IS_LOGGED_IN, false);
        }

        model.addAttribute(TH_COINS, coins);
        model.addAttribute(TH_CURRENT_PAGE, page);
        model.addAttribute(TH_PAGE_SIZE, pageSize);
        model.addAttribute(TH_TOTAL_PAGES, getTotalPages());
        model.addAttribute(TH_TOTAL_COINS, totalCoins);
        model.addAttribute(TH_COIN_START, (page - 1) * pageSize + 1);
        model.addAttribute(TH_COIN_END, Math.min(page * pageSize, totalCoins));
        model.addAttribute(TH_IS_LOGGED_IN, (boolean) session.getAttribute(SESSION_IS_LOGGED_IN));
        
        return "homepage";
    }

    // HELPER FUNCTIONS //

    // Calculate total number of pages
    private int getTotalPages() {
        long totalCoins = cryptoSvc.getCoinsMarketListSize();
        return (int) Math.ceil(Double.valueOf(totalCoins) / pageSize);
    }
    
}

package vttp.miniproject.hodlscout.homepage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        Model model) {

        List<CoinModel> coins = cryptoSvc.getHomepageCoinsMarket(page, pageSize);

        model.addAttribute(TH_COINS, coins);
        model.addAttribute(TH_CURRENT_PAGE, page);
        model.addAttribute(TH_PAGE_SIZE, pageSize);
        model.addAttribute(TH_TOTAL_PAGES, getTotalPages());

        return "homepage";
    }

    // HELPER FUNCTIONS //

    // Calculate total number of pages
    private int getTotalPages() {
        long totalCoins = cryptoSvc.getCoinsMarketListSize();
        return (int) Math.ceil(Double.valueOf(totalCoins) / pageSize);
    }
    
}

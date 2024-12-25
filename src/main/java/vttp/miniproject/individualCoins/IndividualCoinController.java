package vttp.miniproject.individualCoins;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class IndividualCoinController {

    @GetMapping("/coins/{coinId}")
    public String getIndividualCoin(Model model, 
                                    @PathVariable(name = "coinId") String coindId) {
        

        return "";
    }
    
}

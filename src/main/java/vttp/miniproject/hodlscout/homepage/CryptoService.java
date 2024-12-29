package vttp.miniproject.hodlscout.homepage;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

import vttp.miniproject.hodlscout.runner.CoinListRepository;
import vttp.miniproject.hodlscout.utilities.HelperFunctions;
import vttp.miniproject.hodlscout.watchlist.CoinNameAndIdModel;

@Service
public class CryptoService {

    @Autowired
    private CryptoRepository cryptoRepo;

    @Autowired
    private CoinListRepository coinListRepo;

    @Value("${spring.api.coingecko.url}")
    private String coingeckoUrl;

    @Value("${spring.api.coingecko.api.key}")
    private String coingeckoApiKey;

    // Coins market data query parameters
    private String vsCurrency = "usd";
    private int perPage = 250;

    // Number of coins to be retrieved
    @Value("${spring.api.coingecko.coins.market.total.coins}")
    private int totalCoinsToRetrieve;

    private Logger logger = Logger.getLogger(CryptoService.class.getName());

    // Get list of coin ids and names to populate the forms / search bar
    public List<CoinNameAndIdModel> getCoinNameAndIdList() {
        return HelperFunctions.coinListToCoinNameAndIdModelList(coinListRepo.getCoinNameAndIdList());
    }


    public List<CoinModel> getHomepageCoinsMarket(int page, int pageSize) {

        if (!cryptoRepo.keyExist(REDIS_KEY_COINS_MARKET_CACHE))
            fetchApiCoinsMarket();

        int startIdx = (page - 1) * pageSize;
        int endIdx = (page * pageSize) - 1;

        return cryptoRepo.getCoinsMarketData(startIdx, endIdx)
                         .stream()
                         .map(str -> HelperFunctions.jsonObjectToCoinModel(str))
                         .collect(Collectors.toList());
        
    }

    public void fetchApiCoinsMarket() {

        logger.info("Fetching coins market data from API");

        int numIterations = totalCoinsToRetrieve / perPage;
        for (int i = 1; i <= numIterations; i++) {

            // Generate the Url
            String queryUrl = generateApiCoinsMarketUrl(i);

            // Call Api
            String payload = callApiCoinsMarket(queryUrl);

            // [To do Later] Add in "..." if payload is "[]"

            // Parse coin market data
            List<String> coins = HelperFunctions.parseApiCoinsMarket(payload);

            // Add list to Redis database (Redis List)
            cryptoRepo.cacheCoinsMarketData(coins);
        }
    }

    public long getCoinsMarketListSize() {
        return cryptoRepo.getListSize(REDIS_KEY_COINS_MARKET_CACHE);
    }

    // HELPER FUNCTIONS //

    // Generate Url for Coingecko API /coins/markets endpoint
    private String generateApiCoinsMarketUrl(int page) {

        return UriComponentsBuilder
                .fromUriString(coingeckoUrl + "coins/markets")
                .queryParam("x_cg_demo_api_key", coingeckoApiKey)
                .queryParam("vs_currency", vsCurrency)
                .queryParam("per_page", perPage)
                .queryParam("page", page)
                .toUriString();
    }

    // Call Coingecko API /coins/markets endpoint
    private String callApiCoinsMarket(String queryUrl) {

        // Generate the Request
        RequestEntity<Void> request = RequestEntity
                .get(queryUrl)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // Create the REST Template
        RestTemplate template = new RestTemplate();

        ResponseEntity<String> response;

        try {
            logger.info("Trying to retrieve API");
            response = template.exchange(request, String.class);
            return response.getBody();

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.warning(ex.getMessage());

            // Return empty JsonArray if error
            return "[]";
        }
    }
}

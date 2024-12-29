package vttp.miniproject.hodlscout.runner;

import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

// Decided not to go get coinList directly from coins/list/
// endpoint because it contains a lot of super small coins
// that do not even have market cap rank

@Component
public class CoinListLoader implements CommandLineRunner {

    @Autowired
    private CoinListRepository coinListRepo;

    private Logger logger = Logger.getLogger(CoinListLoader.class.getName());

    @Value("${spring.api.coingecko.url}")
    private String coingeckoUrl;

    @Value("${spring.api.coingecko.api.key}")
    private String coingeckoApiKey;

    // Coins market data query parameters
    private String vsCurrency = "usd";
    private int perPage = 250;

    // Number of coins to be retrieved
    @Value("${spring.api.coingecko.coins.market.num.coins.list}")
    private int numCoinsToRetrieveInOneCall;

    // To customize the size of the eventual coin list
    private int totalCoinsToRetrieve = 5000;


    @Override
    public void run(String... args) {
        logger.info("Running command line task - fetching Coin List And Saving To Repo");
        if (!coinListRepo.coinNameAndIdListExist()) {

            int iterationsNeeded = totalCoinsToRetrieve / numCoinsToRetrieveInOneCall;
        
            for (int i = 1; i <= iterationsNeeded; i++) {

                // e.g. 2500 / 250 = 10
                int numIterationsPerFetch = numCoinsToRetrieveInOneCall / perPage;

                // 10i - 9
                int startPage = 1 + (numIterationsPerFetch * (i - 1));

                //
                int endPage = startPage + numIterationsPerFetch;

                fetchApiCoinListAndSaveToRepo(startPage, endPage);
            }

            // Change order from market_cap_rank -> alphabetical.ignore.case
            sortAndSaveCoinNameAndIdList();

            // Save a separate list for coin ID
            saveCoinIdListToRepo();
        }
    }

    // 1. Delete Coin Name and ID List, 2. Delete Coin ID List,
    // 3. Get new info from API and update both in Repo

    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    public void scheduledFetchApiCoinListAndReplaceInRepo1() {
        logger.info("Running 12.00am task");
        coinListRepo.deleteCoinNameAndIdList();

        fetchApiCoinListAndSaveToRepo(1, 10);
        sortAndSaveCoinNameAndIdList();
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at 12.01am
    public void scheduledFetchApiCoinListAndReplaceInRepo2() {
        logger.info("Running 12.01am task");

        fetchApiCoinListAndSaveToRepo(11, 20);
        sortAndSaveCoinNameAndIdList();

        coinListRepo.deleteCoinIdList();
        saveCoinIdListToRepo();
    }

    // Helper function //
    private void fetchApiCoinListAndSaveToRepo(int startPage, int endPage) {

        for (int i = startPage; i <= endPage; i++) {

            // Generate the Url
            String queryUrl = generateApiCoinsMarketUrl(i);

            // Call Api
            String payload = callApiCoinsMarket(queryUrl);

            // Payload -> List<JsonObject with coin id and name.toString()>
            List<String> coinIdAndNameList = extractCoinIdAndNameFromPayload(payload);

            // Cache .toString into Repo
            coinListRepo.cacheCoinNameAndIdList(coinIdAndNameList);
        }

    }

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

    // Extract coin "id" and "Name" for each entry
    private List<String> extractCoinIdAndNameFromPayload(String payload) {

        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonArray jsonArr = reader.readArray();

        return jsonArr.stream()
            .map(val -> val.asJsonObject())
            .filter(obj -> !obj.isNull("market_cap_rank"))
            .map(obj -> {
                return Json.createObjectBuilder()
                    .add("id", obj.getString("id"))
                    .add("name", obj.getString("name"))
                    .build()
                    .toString();
            })
            .collect(Collectors.toList());
    }

    // Sort and save the entire CoinIdAndNameList
    private void sortAndSaveCoinNameAndIdList() {

        List<String> coinNameAndIdList = coinListRepo.getCoinNameAndIdList();
        List<String> sortedCoinNameAndIdList = coinNameAndIdList
            .stream()
            .map(str -> Json.createReader(new StringReader(str)).readObject())
            .sorted((obj1, obj2) -> String.CASE_INSENSITIVE_ORDER
                .compare(obj1.getString("name"), obj2.getString("name")))
            .map(obj -> obj.toString())
            .collect(Collectors.toList());
        coinListRepo.deleteCoinNameAndIdList();
        coinListRepo.cacheCoinNameAndIdList(sortedCoinNameAndIdList);
    }

    // Save the coin id into a separate list for easier validation
    // (compared to a List of JsonObject with name and id)
    private void saveCoinIdListToRepo() {

        List<String> coinNameAndIdList = coinListRepo.getCoinNameAndIdList();
        List<String> coinIdList = coinNameAndIdList.stream()
                .map(str -> {
                    JsonReader reader = Json.createReader(new StringReader(str));
                    JsonObject jsonObj = reader.readObject();
                    return jsonObj.getString("id");
                })
                .collect(Collectors.toList());
        coinListRepo.cacheCoinIdList(coinIdList);
    }

}

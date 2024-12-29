package vttp.miniproject.hodlscout.individualCoins;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp.miniproject.hodlscout.runner.CoinListRepository;
import vttp.miniproject.hodlscout.utilities.HelperFunctions;

@Service
public class IndividualCoinService {

    @Autowired
    private CoinListRepository coinListRepo;

    @Autowired
    private IndividualCoinRepository individualCoinRepo;

    @Value("${spring.api.coingecko.url}")
    private String coingeckoUrl;

    @Value("${spring.api.coingecko.api.key}")
    private String coingeckoApiKey;

    private Logger logger = Logger.getLogger(IndividualCoinService.class.getName());

    public boolean isCoinIdInList(String coinId) {
        return coinListRepo.getCoinIdList().contains(coinId);
    }

    public IndividualCoinModel fetchApiIndividualCoinData(String coinId) {

        if (individualCoinRepo.doesCoinExist(coinId)) {
            return parseIndividualCoinData(individualCoinRepo.getIndividualCoin(coinId), "usd");
        }

        String queryUrl = generateUrlIndividualCoin(coinId);

        String payload = callApiCoinId(queryUrl);

        individualCoinRepo.cacheIndividualCoin(coinId, payload);

        // Parse individual coin data
        IndividualCoinModel newCoin = parseIndividualCoinData(payload, "usd");

        return newCoin;

    }

    // HELPER FUNCTION // 

    // Parse the Individual Coin Data
    private IndividualCoinModel parseIndividualCoinData(String payload, String currency) {
        JsonObject obj = Json.createReader(new StringReader(payload)).readObject();

        JsonObject marketData = obj.getJsonObject("market_data");

        JsonObject communityData = obj.getJsonObject("community_data");

        return new IndividualCoinModel(
            obj.getString("symbol").toUpperCase(), 
            obj.getString("name").trim(), 
            obj.getJsonObject("links").getJsonArray("homepage").getString(0), 
            !obj.getJsonObject("links").isNull("twitter_screen_name")
                ? obj.getJsonObject("links").getString("twitter_screen_name")
                : "elonmusk", 
            obj.getJsonObject("image").getString("large"), 
            obj.getInt("market_cap_rank"), 
            HelperFunctions.currentPriceFormatter(marketData.getJsonObject("current_price").getJsonNumber(currency)), 
            HelperFunctions.currentPriceFormatter(marketData.getJsonObject("ath").getJsonNumber(currency)), 
            !marketData.isNull("ath_change_percentage")
                ? HelperFunctions.decimalStringFormatter(String.valueOf(marketData.getJsonObject("ath_change_percentage").getJsonNumber(currency)))
                : "-",
            !marketData.isNull("ath_date")
                ? convertIso8601ToLocalDate(marketData.getJsonObject("ath_date").getString(currency))
                : null, 
            HelperFunctions.currentPriceFormatter(marketData.getJsonObject("atl").getJsonNumber(currency)), 
            !marketData.isNull("atl_change_percentage")
                ? HelperFunctions.decimalStringFormatter(String.valueOf(marketData.getJsonObject("atl_change_percentage").getJsonNumber(currency)))
                : "-",
            !marketData.isNull("atl_date")
                ? convertIso8601ToLocalDate(marketData.getJsonObject("atl_date").getString(currency))
                : null, 
            !marketData.isNull("market_cap")
                ? HelperFunctions.largeNumberStringFormatter(String.valueOf(marketData.getJsonObject("market_cap").getJsonNumber(currency)))
                : "-", 
            !marketData.isNull("total_volume")
                ? HelperFunctions.largeNumberStringFormatter(String.valueOf(marketData.getJsonObject("total_volume").getJsonNumber(currency)))
                : "-", 
            !marketData.isNull("high_24h")
                ? HelperFunctions.currentPriceFormatter(marketData.getJsonObject("high_24h").getJsonNumber(currency))
                : "-", 
            !marketData.isNull("low_24h")
                ? HelperFunctions.currentPriceFormatter(marketData.getJsonObject("low_24h").getJsonNumber(currency))
                : "-", 
            !marketData.isNull("price_change_24h")
                ? HelperFunctions.decimalStringFormatter(String.valueOf(marketData.getJsonNumber("price_change_24h")))
                : "-", 
            !marketData.isNull("circulating_supply")
                ? HelperFunctions.largeNumberStringFormatter(String.valueOf(marketData.getJsonNumber("circulating_supply")))
                : "-", 
            !communityData.isNull("twitter_followers")
                ? HelperFunctions.largeNumberStringFormatter(String.valueOf(communityData.getJsonNumber("twitter_followers")))
                : "-");


    }

    // Convert ISO 8601 Date to Local Date
    private LocalDate convertIso8601ToLocalDate(String iso8601Date) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(iso8601Date);
        return zonedDateTime.toLocalDate();
    }


    // Generate Url for Coingecko API /coins/markets endpoint
    private String generateUrlIndividualCoin(String coinId) {

        return UriComponentsBuilder
                .fromUriString(coingeckoUrl + "coins/" + coinId)
                .queryParam("x_cg_demo_api_key", coingeckoApiKey)
                .queryParam("localization", false)
                .queryParam("tickers", false)
                .queryParam("developer_data", false)
                .toUriString();
    }

    // Call Coingecko API /coins/{coin_id} endpoint
    private String callApiCoinId(String queryUrl) {

        RequestEntity<Void> request = RequestEntity
            .get(queryUrl)
            .accept(MediaType.APPLICATION_JSON)
            .build();

        RestTemplate template = new RestTemplate();

        ResponseEntity<String> response;

        try {
            response = template.exchange(request, String.class);
            return response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(e.getMessage()); 
            
            return "[]";
        }
    }
    
}

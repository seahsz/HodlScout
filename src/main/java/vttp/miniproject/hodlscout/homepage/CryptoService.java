package vttp.miniproject.hodlscout.homepage;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Service
public class CryptoService {

    @Autowired
    private CryptoRepository cryptoRepo;

    @Value("${spring.api.coingecko.url}")
    private String coingeckoUrl;

    @Value("${spring.api.coingecko.api.key}")
    private String coingeckoApiKey;

    // Coins market data query parameters
    private String vsCurrency = "usd";
    private int perPage = 250;

    // Number of coins to be retrieved
    @Value("${spring.api.coingecko.coins-market.total-coins}")
    private int totalCoinsToRetrieve;

    private Logger logger = Logger.getLogger(CryptoService.class.getName());


    public List<CoinModel> getHomepageCoinsMarket(int page, int pageSize) {

        if (!cryptoRepo.keyExist(REDIS_KEY_COINS_MARKET_CACHE))
            fetchApiCoinsMarket();

        int startIdx = (page - 1) * pageSize;
        int endIdx = (page * pageSize) - 1;

        return cryptoRepo.getCoinsMarketData(startIdx, endIdx)
                         .stream()
                         .map(str -> jsonObjectToCryptoModel(str))
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
            List<String> coins = parseApiCoinsMarket(payload);

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

    // Parse payload --> List<JsonObject> from /coins/markets endpoint
    /*
     * Notes:
     * 1) need to use getJsonNumber instead of getInt (if not cannot get the correct data from Json - not sure why)
     * 2) Displaying market_cap, price_change and circulating_supply as String because some
     * cryptocurrencies do not have data (null) for these categories
     * 3) Formatting beforehand so that these large integers display nicely in html
     */
    private List<String> parseApiCoinsMarket(String payload) {

        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonArray jsonArr = reader.readArray();

        return jsonArr.stream()
                .map(val -> val.asJsonObject())
                .filter(obj -> !obj.isNull("market_cap_rank")) // ignore coins that do not have market_cap_rank
                .sorted((obj1, obj2) -> Integer.compare(
                    obj1.getInt("market_cap_rank"),
                    obj2.getInt("market_cap_rank")))
                .map(obj -> {
                    return Json.createObjectBuilder()
                            .add("symbol", obj.getString("symbol").toUpperCase())
                            .add("name", obj.getString("name"))
                            .add("image", obj.getString("image"))
                            .add("current_price", obj.getJsonNumber("current_price"))
                            .add("market_cap",
                                    !obj.isNull("market_cap") 
                                            ? largeNumberStringFormatter(String.valueOf(obj.getJsonNumber("market_cap"))) 
                                            : "-")
                            .add("market_cap_rank", obj.getInt("market_cap_rank"))
                            .add("total_volume", obj.getJsonNumber("total_volume"))
                            .add("price_change_percentage_24h",
                                    !obj.isNull("price_change_percentage_24h")
                                            ? decimalStringFormatter(String.valueOf(obj.getJsonNumber("price_change_percentage_24h")))
                                            : "-")
                            .add("circulating_supply",
                                    !obj.isNull("circulating_supply") 
                                            ? largeNumberStringFormatter(String.valueOf(obj.getJsonNumber("circulating_supply")))
                                            : "-")
                            .build().toString();
                })
                .collect(Collectors.toList());
    }

    // Convert JsonObject (as String) to CryptoModel
    private CoinModel jsonObjectToCryptoModel(String jsonString) {
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonObject obj = reader.readObject();

        return new CoinModel(obj.getString("symbol"), 
                               obj.getString("name"),
                               obj.getString("image"),
                               currentPriceFormatter(obj.getJsonNumber("current_price")),
                               obj.getString("market_cap"), 
                               obj.getInt("market_cap_rank"), 
                               largeNumberStringFormatter(String.valueOf(obj.getJsonNumber("total_volume"))), 
                               obj.getString("price_change_percentage_24h"), 
                               obj.getString("circulating_supply"));
    }

    // Format the String (of the Large Integer)
    private String largeNumberStringFormatter(String numAsString) {
        if (numAsString.equals("-"))
            return numAsString;

        BigDecimal number = new BigDecimal(numAsString).setScale(0, RoundingMode.DOWN);

        return NumberFormat.getInstance().format(number);
    }

    // Format the String (of Decimal)
    private String decimalStringFormatter(String decimalAsString) {
        if (decimalAsString.equals("-"))
            return "nil";

        BigDecimal number = new BigDecimal(decimalAsString);

        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);

        return formatter.format(number);
    }


    // Convert currentPrice to BigDecimal with appropriate formatting
    /*
     * Price > 1: 1,000.00
     * Price < 1: 5sf 0.0076588
     */
    private String currentPriceFormatter(JsonNumber jsonNumber) {

        // Convert jsonNumber -> String -> BigDecimal
        BigDecimal numAsBigDecimal = new BigDecimal(String.valueOf(jsonNumber));

        // If number is > 1
        if (numAsBigDecimal.compareTo(BigDecimal.ONE) == 1) {
            BigDecimal formattedNum = numAsBigDecimal.setScale(2, RoundingMode.HALF_EVEN);

            NumberFormat formatter = NumberFormat.getInstance();
            formatter.setMinimumFractionDigits(2);
            return formatter.format(formattedNum);
        }

        else return formatToSignificantFigures(numAsBigDecimal, 5);
    }

    private String formatToSignificantFigures(BigDecimal number, int sf) {
        // Remove trailing zeroes
        number = number.stripTrailingZeros();

        // precision is kind of like significant figures (e.g. 0.34 if 2 precision)
        if (number.precision() <= sf)
            return number.toString();

        int numberOfLeadingZeros = number.scale() - number.precision();
        int newScale = numberOfLeadingZeros + sf;

        return number.setScale(newScale, RoundingMode.HALF_EVEN).stripTrailingZeros().toString();

    }
}

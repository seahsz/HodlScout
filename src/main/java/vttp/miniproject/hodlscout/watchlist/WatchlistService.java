package vttp.miniproject.hodlscout.watchlist;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.miniproject.hodlscout.homepage.CoinModel;
import vttp.miniproject.hodlscout.runner.CoinListRepository;
import vttp.miniproject.hodlscout.utilities.HelperFunctions;

@Service
public class WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepo;

    @Autowired
    private CoinListRepository coinListRepo;

    @Value("${spring.api.coingecko.url}")
    private String coingeckoUrl;

    @Value("${spring.api.coingecko.api.key}")
    private String coingeckoApiKey;

    // Coins market data query parameters
    private String vsCurrency = "usd";

    private Logger logger = Logger.getLogger(WatchlistService.class.getName());

    // Check if coinId is valid (viz among the coinId list)
    public boolean isCoinIdInList(String coinId) {
        return coinListRepo.getCoinIdList().contains(coinId);
    }

    // Get list of coin ids and names to populate the forms / search bar
    public List<CoinNameAndIdModel> getCoinNameAndIdList() {
        return HelperFunctions.coinListToCoinNameAndIdModelList(coinListRepo.getCoinNameAndIdList());
    }

    // Check if Coin Id is already in watchlist
    public boolean isCoinIdInWatchlist(String coinId, String username, String watchlistName) {
        JsonArray watchlists = getWatchlists(username);

        JsonObject currWatchlist = getWatchlistByName(watchlists, watchlistName);

        String[] coinIds = currWatchlist.getString("coinIds").split(",");

        List<String> coinIdsList = List.of(coinIds);

        return coinIdsList.contains(coinId);
    }

    // check if watchlist hashkey exist
    public boolean watchlistHashKeyExist(String username) {
        return watchlistRepo.watchlistHashKeyExist(username);
    }

    // Get List<String> of watchlist names
    public List<String> getAllWatchlistNames(String username) {
        JsonArray jsonArr = getWatchlists(username);

        return jsonArr.stream()
                      .map(val -> val.asJsonObject())
                      .map(obj -> obj.getString("name"))
                      .collect(Collectors.toList());
    }

    // Return first Watchlist name
    public String getFirstWatchlistName(String username) {
        JsonArray jsonArr = getWatchlists(username);

        return jsonArr.getJsonObject(0).getString("name");

    }

    // Check if watchlist name exist - ignores case (assuming watchlist hashkey exist)
    public boolean watchlistNameExist(String username, String watchlistName) {
        JsonArray jsonArr = getWatchlists(username);

        Optional<JsonObject> opt = jsonArr.stream()
                .map(val -> val.asJsonObject())
                .filter(obj -> obj.getString("name").equalsIgnoreCase(watchlistName))
                .findFirst();

        if (opt.isEmpty())
            return false;

        return true;
    }

    // Assumes that Watchlist Hashkey exist & watchlist name exists
    public List<CoinModel> getWatchlistCoinsMarket(String username, String watchlistName) {

        JsonArray watchlists = getWatchlists(username);

        JsonObject currWatchList = getWatchlistByName(watchlists, watchlistName);

        String coinIds = currWatchList.getString("coinIds");

        // Case 1: watchlist exist, but has no coin id saved
        if (coinIds.isBlank()) {
            return List.of();
        } else {
            // Case 2: watchlist exist with coin ids
            String payload = fetchApiCoinsMarket(coinIds);

            // return List<CoinModel>
            return HelperFunctions.parseApiCoinsMarketToCoinModel(payload);
        }

    }

    public void modifyAndSaveWatchlistToRepo(String username, String watchlistName, String coinId, String function) {

        if (watchlistRepo.watchlistHashKeyExist(username)) {
            JsonArray jsonArr = getWatchlists(username);

            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

            boolean isReplaced = false;

            for (int i = 0; i < jsonArr.size(); i++) {
                // if there is a watchlist with the same name -> replace it in the new array
                if (jsonArr.getJsonObject(i).getString("name").equalsIgnoreCase(watchlistName)) {

                    if (function.equals("add"))
                        arrayBuilder.add(addCoinIdToWatchlist(jsonArr.getJsonObject(i), coinId));
                    else if (function.equals("delete"))
                        arrayBuilder.add(deleteCoinIdFromWatchlist(jsonArr.getJsonObject(i), coinId));
                    isReplaced = !isReplaced;
                } else {
                    arrayBuilder.add(jsonArr.get(i));
                }
            }

            // watchlist hashkey exist, but this specific watchlist does not
            if (!isReplaced) {
                if (function.equals("add")) {
                    arrayBuilder.add(createNewWatchlist(watchlistName, coinId));
                }
            }

            watchlistRepo.saveWatchlists(username, arrayBuilder.build().toString());

        } else {
            // watchlist hashkey does not exist and function is "add"
            if (function.equals("add")) {
                JsonArray newWatchlists = Json.createArrayBuilder()
                        .add(createNewWatchlist(watchlistName, coinId))
                        .build();

                watchlistRepo.saveWatchlists(username, newWatchlists.toString());
            }
        }
    }

    public void createAndSaveWatchlistToRepo(String username, String watchlistName) {

        JsonObject newWatchlist = Json.createObjectBuilder()
                .add("name", watchlistName)
                .add("coinIds", "")
                .build();

        JsonArray newWatchlistList;

        // if watchlist hashkey exists
        if (watchlistRepo.watchlistHashKeyExist(username)) {
            JsonArray oldWatchlistList = getWatchlists(username);
            newWatchlistList = addWatchlistToWatchlistHashKey(oldWatchlistList, newWatchlist);

        } else {
            newWatchlistList = Json.createArrayBuilder()
                                   .add(newWatchlist)
                                   .build();
        }
        watchlistRepo.saveWatchlists(username, newWatchlistList.toString());
    }

    public void deleteWatchlistFromRepo(String username, String watchlistName) {
        
        JsonArray jsonArr = getWatchlists(username);

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        // find watchlist that matches watchlist name
        for (int i = 0; i < jsonArr.size(); i++) {
            if (!jsonArr.getJsonObject(i).getString("name").equals(watchlistName)) {
                arrayBuilder.add(jsonArr.get(i));
            }
        }

        JsonArray newArray = arrayBuilder.build();

        // if the JsonArray is empty after deletion, delete the "watchlist" Hashkey
        if (newArray.size() == 0) {
            watchlistRepo.deleteWatchlistHashkey(username);
        } else {
            // else, you save the new jsonArray into Redis
            watchlistRepo.saveWatchlists(username, newArray.toString());
        }
    }

    // Export Watchlist as CSV
    public String watchlistToCsv(String username, String watchlistName) {

        JsonArray watchlists = getWatchlists(username);

        JsonObject currWatchlist = getWatchlistByName(watchlists, watchlistName);

        String coinIds = currWatchlist.getString("coinIds");

        if (coinIds.isBlank())
            return "";

        String payload = fetchApiCoinsMarket(coinIds);
        
        List<JsonObject> processedPayloadAsJsonObjList = HelperFunctions.parseApiCoinsMarketForExport(payload);

        if (processedPayloadAsJsonObjList.isEmpty())
            return "";

        StringBuilder csvBuilder = new StringBuilder();

        // Get header from the first JsonObject
        JsonObject firstObj = processedPayloadAsJsonObjList.get(0);
        List<String> headers = firstObj.keySet().stream().collect(Collectors.toList());
        csvBuilder.append(String.join(",", headers)).append("\n");
        
        // Append each row of data
        for (int i = 0; i < processedPayloadAsJsonObjList.size(); i++) {
            JsonObject currJsonObj = processedPayloadAsJsonObjList.get(i);
            String row = headers.stream()
                    .map(header -> escapeCsvValue(currJsonObj.getString(header)))
                    .reduce((a,b) -> a + "," + b)
                    .orElse("");
            csvBuilder.append(row).append("\n");
        }

        return csvBuilder.toString();
    }





    // HELPER FUNCTIONS //
    private String escapeCsvValue(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\""); // Escape double quotes
            return "\"" + value + "\"";
        }
        return value;
    }

    // Get watchlist from Watchlist Hash Key (A JsonArray) - assumes that watchlist name exists
    private JsonObject getWatchlistByName(JsonArray watchlists, String watchlistName) {
        return watchlists.stream().map(val -> val.asJsonObject())
            .filter(obj -> obj.getString("name").equals(watchlistName))
            .findFirst()
            .get();
    }

    // Add new watchlist to Watchlist Hash Key (a JsonArray)
    private JsonArray addWatchlistToWatchlistHashKey(JsonArray watchlistList, JsonObject newWatchlist) {

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (int i = 0; i < watchlistList.size(); i++)
            arrayBuilder.add(watchlistList.getJsonObject(i));
        
        arrayBuilder.add(newWatchlist);
        return arrayBuilder.build();
    }

    // Get watchlist hashkey
    private JsonArray getWatchlists(String username) {
        String watchlists = watchlistRepo.getWatchlists(username);
        JsonReader reader = Json.createReader(new StringReader(watchlists));
        return reader.readArray();
    }

    // Delete coinId from watchlist
    private JsonObject deleteCoinIdFromWatchlist(JsonObject oldWatchlist, String coinId) {
        String[] coinIdsArray = oldWatchlist.getString("coinIds").split(",");

        List<String> coinIdsList = new ArrayList<>(List.of(coinIdsArray));
        coinIdsList.remove(coinId);

        String coinIdsCsv = String.join(",", coinIdsList);

        return Json.createObjectBuilder()
                .add("name", oldWatchlist.getString("name"))
                .add("coinIds", coinIdsCsv)
                .build();
    }

    // Create new watchlist JsonObject
    private JsonObject createNewWatchlist(String watchlistName, String coinId) {
        return Json.createObjectBuilder()
                .add("name", watchlistName)
                .add("coinId", coinId)
                .build();
    }

    // Add new coinId to watchlist
    private JsonObject addCoinIdToWatchlist(JsonObject oldWatchlist, String coinId) {
        return Json.createObjectBuilder()
                .add("name", oldWatchlist.getString("name"))
                .add("coinIds", oldWatchlist.getString("coinIds") + "," + coinId)
                .build();
    }

    // Generate Url for Coingecko API /coins/markets endpoint
    private String generateApiCoinsMarketUrl(String coinIds) {

        return UriComponentsBuilder
                .fromUriString(coingeckoUrl + "coins/markets")
                .queryParam("x_cg_demo_api_key", coingeckoApiKey)
                .queryParam("vs_currency", vsCurrency)
                .queryParam("ids", coinIds)
                .toUriString();
    }

    private String fetchApiCoinsMarket(String coinIds) {

        RequestEntity<Void> request = RequestEntity
                .get(generateApiCoinsMarketUrl(coinIds))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        RestTemplate template = new RestTemplate();

        ResponseEntity<String> response;

        try {
            logger.info("Getting Coins Market Data for Watchlist Coins");;
            response = template.exchange(request, String.class);
            return response.getBody();

        } catch (Exception ex) {
            ex.printStackTrace();
            return "[]";
        }
    }

}

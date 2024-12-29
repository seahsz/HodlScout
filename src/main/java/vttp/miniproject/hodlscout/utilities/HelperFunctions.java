package vttp.miniproject.hodlscout.utilities;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ui.Model;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpSession;
import vttp.miniproject.hodlscout.homepage.CoinModel;
import vttp.miniproject.hodlscout.watchlist.CoinNameAndIdModel;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

public class HelperFunctions {

        public static void populateCustomErrorViewModel(Model model, HttpSession session, String errorMsgTitle, String errorMsgBody) {

                boolean isLoggedIn = isUserLoggedIn(session);

                model.addAttribute(TH_IS_LOGGED_IN, isLoggedIn);
                if (isLoggedIn)
                        model.addAttribute(TH_USERNAME, session.getAttribute(SESSION_USERNAME));
                model.addAttribute(TH_CUSTOM_ERROR_MESSAGE_TITLE, errorMsgTitle);
                model.addAttribute(TH_CUSTOM_ERROR_MESSAGE_BODY, errorMsgBody);
        }

        public static boolean isUserLoggedIn(HttpSession session) {
                if (session.getAttribute(SESSION_IS_LOGGED_IN) == null
                                || !(boolean) session.getAttribute(SESSION_IS_LOGGED_IN)) {
                        return false;
                }
                return true;
        }

        public static List<CoinNameAndIdModel> coinListToCoinNameAndIdModelList(List<String> coinIdList) {
                return coinIdList.stream()
                                .map(str -> jsonObjectToCoinNameAndIdModel(str))
                                .collect(Collectors.toList());
        }

        private static CoinNameAndIdModel jsonObjectToCoinNameAndIdModel(String coinIdString) {
                JsonReader reader = Json.createReader(new StringReader(coinIdString));
                JsonObject obj = reader.readObject();

                return new CoinNameAndIdModel(obj.getString("id"), obj.getString("name"));
        }

        // Parse payload --> List<JsonObject> from /coins/markets endpoint
        /*
         * Notes:
         * 1) need to use getJsonNumber instead of getInt (if not cannot get the correct
         * data from Json - not sure why)
         * 2) Displaying market_cap, price_change and circulating_supply as String
         * because some
         * cryptocurrencies do not have data (null) for these categories
         * 3) Formatting beforehand so that these large integers display nicely in html
         */

        public static List<String> parseApiCoinsMarket(String payload) {

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
                                                   .add("id", obj.getString("id"))
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

        // Convert JsonObject (as String) to CoinModel
        public static CoinModel jsonObjectToCoinModel(String jsonString) {
                JsonReader reader = Json.createReader(new StringReader(jsonString));
                JsonObject obj = reader.readObject();

                return new CoinModel(obj.getString("id"),
                                obj.getString("symbol"),
                                obj.getString("name"),
                                obj.getString("image"),
                                currentPriceFormatter(obj.getJsonNumber("current_price")),
                                obj.getString("market_cap"),
                                obj.getInt("market_cap_rank"),
                                largeNumberStringFormatter(String.valueOf(obj.getJsonNumber("total_volume"))),
                                obj.getString("price_change_percentage_24h"),
                                obj.getString("circulating_supply"));
        }

        // Parse payload --> List<JsonObject> from /coins/markets endpoint --> Direct to
        // CoinModel
        public static List<CoinModel> parseApiCoinsMarketToCoinModel(String payload) {

                JsonReader reader = Json.createReader(new StringReader(payload));
                JsonArray jsonArr = reader.readArray();

                return jsonArr.stream()
                              .map(val -> val.asJsonObject())
                              .filter(obj -> !obj.isNull("market_cap_rank")) // ignore coins that do not have market_cap_rank
                              .sorted((obj1, obj2) -> Integer.compare(
                                                obj1.getInt("market_cap_rank"),
                                                obj2.getInt("market_cap_rank")))
                              .map(obj -> {return new CoinModel(obj.getString("id"),
                                                                obj.getString("symbol").toUpperCase(),
                                                                obj.getString("name"),
                                                                obj.getString("image"),
                                                                currentPriceFormatter(obj.getJsonNumber("current_price")),
                                                                !obj.isNull("market_cap")
                                                                        ? largeNumberStringFormatter(String.valueOf(obj.getJsonNumber("market_cap")))
                                                                        : "-",
                                                                obj.getInt("market_cap_rank"),
                                                                largeNumberStringFormatter(String.valueOf(obj.getJsonNumber("total_volume"))),
                                                                !obj.isNull("price_change_percentage_24h")
                                                                        ? decimalStringFormatter(String.valueOf(obj.getJsonNumber("price_change_percentage_24h")))
                                                                        : "-",
                                                                !obj.isNull("circulating_supply")
                                                                        ? largeNumberStringFormatter(String.valueOf(obj.getJsonNumber("circulating_supply")))
                                                                        : "-");
                                })
                                .collect(Collectors.toList());
        }

        // Format the String (of the Large Integer)
        public static String largeNumberStringFormatter(String numAsString) {
                if (numAsString.equals("-"))
                        return numAsString;

                BigDecimal number = new BigDecimal(numAsString).setScale(0, RoundingMode.DOWN);

                return NumberFormat.getInstance().format(number);
        }

        // Format the String (of Decimal)
        public static String decimalStringFormatter(String decimalAsString) {
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
        public static String currentPriceFormatter(JsonNumber jsonNumber) {

                // Convert jsonNumber -> String -> BigDecimal
                BigDecimal numAsBigDecimal = new BigDecimal(String.valueOf(jsonNumber));

                // If number is > 1
                if (numAsBigDecimal.compareTo(BigDecimal.ONE) == 1) {
                        BigDecimal formattedNum = numAsBigDecimal.setScale(2, RoundingMode.HALF_EVEN);

                        NumberFormat formatter = NumberFormat.getInstance();
                        formatter.setMinimumFractionDigits(2);
                        return formatter.format(formattedNum);
                }

                else
                        return formatToSignificantFigures(numAsBigDecimal, 5);
        }

        public static String formatToSignificantFigures(BigDecimal number, int sf) {
                // Remove trailing zeroes
                number = number.stripTrailingZeros();

                // precision is kind of like significant figures (e.g. 0.34 if 2 precision)
                if (number.precision() <= sf)
                        return number.toString();

                int numberOfLeadingZeros = number.scale() - number.precision();
                int newScale = numberOfLeadingZeros + sf;

                return number.setScale(newScale, RoundingMode.HALF_EVEN).stripTrailingZeros().toString();

        }

        public static List<JsonObject> parseApiCoinsMarketForExport(String payload) {

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
                                                   .add("current_price", currentPriceFormatter(obj.getJsonNumber("current_price")))
                                                   .add("market_cap",
                                                        !obj.isNull("market_cap")
                                                        ? largeNumberStringFormatter(String.valueOf(obj.getJsonNumber("market_cap")))
                                                        : "-")
                                                   .add("market_cap_rank", String.valueOf(obj.getInt("market_cap_rank")))
                                                   .add("total_volume", largeNumberStringFormatter(String.valueOf(obj.getJsonNumber("total_volume"))))
                                                   .add("price_change_percentage_24h",
                                                        !obj.isNull("price_change_percentage_24h")
                                                        ? decimalStringFormatter(String.valueOf(obj.getJsonNumber("price_change_percentage_24h")))
                                                        : "-")
                                                   .add("circulating_supply",
                                                        !obj.isNull("circulating_supply")
                                                        ? largeNumberStringFormatter(String.valueOf(obj.getJsonNumber("circulating_supply")))  + " " + obj.getString("symbol").toUpperCase()
                                                        : "-")
                                                   .build();
                                })
                                .collect(Collectors.toList());
        }
}

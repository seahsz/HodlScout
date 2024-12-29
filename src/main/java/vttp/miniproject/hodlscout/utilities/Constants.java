package vttp.miniproject.hodlscout.utilities;

public class Constants {

    // Thymeleaf related constants
    public static final String TH_USER = "user";

    public static final String TH_COINS = "coins";

    public static final String TH_CURRENT_PAGE = "currentPage";

    public static final String TH_PAGE_SIZE = "pageSize";

    public static final String TH_TOTAL_PAGES = "totalPages";

    public static final String TH_TOTAL_COINS = "totalCoins";

    public static final String TH_COIN_START = "start";

    public static final String TH_COIN_END = "end";

    public static final String TH_IS_LOGGED_IN = "isLoggedIn";

    public static final String TH_USERNAME = "username";

    public static final String TH_UNAUTHORIZED = "unauthorized";

    public static final String TH_WATCHLIST_FORM = "watchlist_form";

    public static final String TH_WATCHLIST_NAME = "watchlist_name";

    public static final String TH_WATCHLIST_NAME_LIST = "watchlist_name_list";
    
    public static final String TH_COIN_NAME_AND_ID_LIST = "coin_name_and_id_list";

    public static final String TH_ADD_COIN_TO_WATCHLIST_ERR_MSG = "add_coin_to_watchlist_err_msg";

    public static final String TH_INDIVIDUAL_COIN = "coin";

        // For the custom error page
    public static final String TH_CUSTOM_ERROR_MESSAGE_TITLE = "custom_error_message_title";

    public static final String TH_CUSTOM_ERROR_MESSAGE_BODY = "custom_error_message_body";

    // Redirect Attributes
    public static final String TH_REDIRECT_LOGIN_TO_HOMEPAGE = "redirectLoginToHomepage";

    public static final String TH_REDIRECT_LOGOUT_TO_HOMEPAGE = "redirectLogoutToHomepage";

    public static final String TH_REDIRECT_SIGNUP_TO_HOMEPAGE = "redirectSignupToHomepage";

    public static final String TH_WATCHLIST_ERROR_MSG = "watchlist_error_msg";

    // Redis related constants
    public static final String REDIS_KEY_USER = "user_login";

    public static final String REDIS_KEY_COINS_MARKET_CACHE = "crypto_coins_market";

    public static final String REDIS_KEY_COINS_ID_NAME_LIST_CACHE = "crypto_coins_id_name_list";

    public static final String REDIS_KEY_COINS_ID_LIST_CACHE = "crypto_coins_id_list";

    public static final String REDIS_KEY_INDIVIDUAL_COIN_CACHE = "crypto_individual_coin";

    // HttpSession related constants
    public static final String SESSION_IS_LOGGED_IN = "isLoggedIn";
    
    public static final String SESSION_LOGIN_TO_HOMEPAGE = "loginToHomepage";

    public static final String SESSION_LOGOUT_SUCCESSFUL = "logoutSuccessful";

    public static final String SESSION_USERNAME = "username";

}

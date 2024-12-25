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

    public static final String TH_UNAUTHORIZED = "unauthorized";

    // Redirect Attributes
    public static final String TH_REDIRECT_LOGIN_TO_HOMEPAGE = "redirectLoginToHomepage";

    public static final String TH_REDIRECT_LOGOUT_TO_HOMEPAGE = "redirectLogoutToHomepage";

    public static final String TH_REDIRECT_SIGNUP_TO_HOMEPAGE = "redirectSignupToHomepage";

    // Redis related constants
    public static final String REDIS_KEY_USER = "user_login";

    public static final String REDIS_KEY_COINS_MARKET_CACHE = "crypto_coins_market";

    // HttpSession related constants
    public static final String SESSION_IS_LOGGED_IN = "isLoggedIn";
    
    public static final String SESSION_LOGIN_TO_HOMEPAGE = "loginToHomepage";

    public static final String SESSION_LOGOUT_SUCCESSFUL = "logoutSuccessful";

    public static final String SESSION_USERNAME = "username";

}

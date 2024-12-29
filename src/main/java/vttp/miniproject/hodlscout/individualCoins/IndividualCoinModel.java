package vttp.miniproject.hodlscout.individualCoins;

import java.time.LocalDate;

public class IndividualCoinModel {

    private String symbol;
    private String name;
    private String linkHomepage;
    private String twitterScreenName;
    private String image;
    private int marketCapRank;
    private String currentPrice;
    
    private String ath;
    private String athChangePercentage;
    private LocalDate athDate;

    private String atl;
    private String atlChangePercentage;
    private LocalDate atlDate;
    
    private String marketCap;
    private String totalVolume;

    private String high24h;
    private String low24h;

    private String priceChangePercentage24h;

    private String circulatingSupply;

    private String twitterFollowers;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkHomepage() {
        return linkHomepage;
    }

    public void setLinkHomepage(String linkHomepage) {
        this.linkHomepage = linkHomepage;
    }

    public String getTwitterScreenName() {
        return twitterScreenName;
    }

    public void setTwitterScreenName(String twitterScreenName) {
        this.twitterScreenName = twitterScreenName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMarketCapRank() {
        return marketCapRank;
    }

    public void setMarketCapRank(int marketCapRank) {
        this.marketCapRank = marketCapRank;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getAth() {
        return ath;
    }

    public void setAth(String ath) {
        this.ath = ath;
    }

    public String getAthChangePercentage() {
        return athChangePercentage;
    }

    public void setAthChangePercentage(String athChangePercentage) {
        this.athChangePercentage = athChangePercentage;
    }

    public LocalDate getAthDate() {
        return athDate;
    }

    public void setAthDate(LocalDate athDate) {
        this.athDate = athDate;
    }

    public String getAtl() {
        return atl;
    }

    public void setAtl(String atl) {
        this.atl = atl;
    }

    public String getAtlChangePercentage() {
        return atlChangePercentage;
    }

    public void setAtlChangePercentage(String atlChangePercentage) {
        this.atlChangePercentage = atlChangePercentage;
    }

    public LocalDate getAtlDate() {
        return atlDate;
    }

    public void setAtlDate(LocalDate atlDate) {
        this.atlDate = atlDate;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public String getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(String totalVolume) {
        this.totalVolume = totalVolume;
    }

    public String getHigh24h() {
        return high24h;
    }

    public void setHigh24h(String high24h) {
        this.high24h = high24h;
    }

    public String getLow24h() {
        return low24h;
    }

    public void setLow24h(String low24h) {
        this.low24h = low24h;
    }

    public String getPriceChangePercentage24h() {
        return priceChangePercentage24h;
    }

    public void setPriceChangePercentage24h(String priceChangePercentage24h) {
        this.priceChangePercentage24h = priceChangePercentage24h;
    }

    public String getCirculatingSupply() {
        return circulatingSupply;
    }

    public void setCirculatingSupply(String circulatingSupply) {
        this.circulatingSupply = circulatingSupply;
    }

    public String getTwitterFollowers() {
        return twitterFollowers;
    }

    public void setTwitterFollowers(String twitterFollowers) {
        this.twitterFollowers = twitterFollowers;
    }

    public IndividualCoinModel(String symbol, String name, String linkHomepage, String twitterScreenName, String image,
            int marketCapRank, String currentPrice, String ath, String athChangePercentage, LocalDate athDate,
            String atl, String atlChangePercentage, LocalDate atlDate, String marketCap, String totalVolume,
            String high24h, String low24h, String priceChangePercentage24h, String circulatingSupply,
            String twitterFollowers) {
        this.symbol = symbol;
        this.name = name;
        this.linkHomepage = linkHomepage;
        this.twitterScreenName = twitterScreenName;
        this.image = image;
        this.marketCapRank = marketCapRank;
        this.currentPrice = currentPrice;
        this.ath = ath;
        this.athChangePercentage = athChangePercentage;
        this.athDate = athDate;
        this.atl = atl;
        this.atlChangePercentage = atlChangePercentage;
        this.atlDate = atlDate;
        this.marketCap = marketCap;
        this.totalVolume = totalVolume;
        this.high24h = high24h;
        this.low24h = low24h;
        this.priceChangePercentage24h = priceChangePercentage24h;
        this.circulatingSupply = circulatingSupply;
        this.twitterFollowers = twitterFollowers;
    }


    

}

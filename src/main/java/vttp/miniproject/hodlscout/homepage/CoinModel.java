package vttp.miniproject.hodlscout.homepage;

public class CoinModel {

    // currentPrice, marketCap, totalVolume, priceChangePercentage24h, circulatingSupply
    // are String.class because need to format before displaying
    // No cons as not using these "double" for any arithmetic operations

    private String symbol;
    private String name;
    private String image;
    private String currentPrice;
    private String marketCap;
    private int marketCapRank;
    private String totalVolume;
    private String priceChange;
    private String circulatingSupply;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(String currentPrice) { this.currentPrice = currentPrice; }
    
    public String getMarketCap() { return marketCap; }
    public void setMarketCap(String marketCap) { this.marketCap = marketCap; }

    public int getMarketCapRank() { return marketCapRank; }
    public void setMarketCapRank(int marketCapRank) { this.marketCapRank = marketCapRank; }

    public String getTotalVolume() { return totalVolume; }
    public void setTotalVolume(String totalVolume) { this.totalVolume = totalVolume; }

    public String getPriceChange() { return priceChange; }
    public void setPriceChange(String priceChange) { this.priceChange = priceChange; }

    public String getCirculatingSupply() { return circulatingSupply; }
    public void setCirculatingSupply(String circulatingSupply) { this.circulatingSupply = circulatingSupply; }
    
    public CoinModel(String symbol, String name, String image, String currentPrice, String marketCap, int marketCapRank,
        String totalVolume, String priceChange, String circulatingSupply) {
        this.symbol = symbol;
        this.name = name;
        this.image = image;
        this.currentPrice = currentPrice;
        this.marketCap = marketCap;
        this.marketCapRank = marketCapRank;
        this.totalVolume = totalVolume;
        this.priceChange = priceChange;
        this.circulatingSupply = circulatingSupply;
    }


    
    
}

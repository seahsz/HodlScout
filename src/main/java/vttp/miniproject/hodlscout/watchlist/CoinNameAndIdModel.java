package vttp.miniproject.hodlscout.watchlist;

// to be more easily passed into the various searches
public class CoinNameAndIdModel {

    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CoinNameAndIdModel (String id, String name) {
        this.id = id;
        this.name = name;
    }
    
}

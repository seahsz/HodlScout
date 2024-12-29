package vttp.miniproject.hodlscout.watchlist;

import jakarta.validation.constraints.NotBlank;

public class WatchlistModel {

    @NotBlank(message = "Watchlist name cannot be empty")
    private String watchlistName;

    public String getWatchlistName() {
        return watchlistName;
    }

    public void setWatchlistName(String watchlistName) {
        this.watchlistName = watchlistName;
    }
    
}

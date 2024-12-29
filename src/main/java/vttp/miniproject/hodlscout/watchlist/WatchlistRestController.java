package vttp.miniproject.hodlscout.watchlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@RestController
@RequestMapping
public class WatchlistRestController {

    @Autowired
    private WatchlistService watchlistSvc;

    @GetMapping("/watchlist/{watchlistName}/export")
    public ResponseEntity<String> getWatchlistExport(
        @PathVariable String watchlistName,
        HttpSession session) {

        String username = (String) session.getAttribute(SESSION_USERNAME);

        String csvContent = watchlistSvc.watchlistToCsv(username, watchlistName);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s.csv".formatted(watchlistName));
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }
    
}

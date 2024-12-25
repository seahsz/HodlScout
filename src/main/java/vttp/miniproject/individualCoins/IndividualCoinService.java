package vttp.miniproject.individualCoins;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class IndividualCoinService {

    @Value("${spring.api.coingecko.url}")
    private String coingeckoUrl;

    @Value("${spring.api.coingecko.api.key}")
    private String coingeckoApiKey;

    private Logger logger = Logger.getLogger(IndividualCoinService.class.getName());

    public void fetchApiIndividualCoinData(String coinId) {

        String queryUrl = generateUrlIndividualCoin(coinId);

        String payload = callApiCoinId(queryUrl);

        // [To do Later] Add in "..." if payload is "[]"

        // Parse individual coin data


        

    }

    // HELPER FUNCTION // 

    // Generate Url for Coingecko API /coins/markets endpoint
    private String generateUrlIndividualCoin(String coinId) {

        return UriComponentsBuilder
                .fromUriString(coingeckoUrl + "coins/" + coinId)
                .queryParam("x_cg_demo_api_key", coingeckoApiKey)
                .queryParam("localization", false)
                .queryParam("tickers", false)
                .queryParam("developer_data", false)
                .toUriString();
    }

    // Call Coingecko API /coins/{coin_id} endpoint
    private String callApiCoinId(String queryUrl) {

        RequestEntity<Void> request = RequestEntity
            .get(queryUrl)
            .accept(MediaType.APPLICATION_JSON)
            .build();

        RestTemplate template = new RestTemplate();

        ResponseEntity<String> response;

        try {
            response = template.exchange(request, String.class);
            return response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(e.getMessage()); 
            
            return "[]";
        }
    }
    
}

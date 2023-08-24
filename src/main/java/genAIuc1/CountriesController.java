package genAIuc1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;

@RestController
public class CountriesController {

    public static final String COUNTRIES_URL = "https://restcountries.com/v3.1/all";

    @GetMapping("/getCountries")
    public String getCountries(@RequestParam(required = false) String param1, @RequestParam(required = false) String param2,
                               @RequestParam(required = false) String param3, @RequestParam(required = false) String param4) throws JsonProcessingException {
        String url = COUNTRIES_URL;
        RestTemplate templ = new RestTemplate();
        String response = templ.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);
        return "SUCCESS!";
    }
}

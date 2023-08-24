package genAIuc1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;

@RestController
public class CountriesController {

    public static final String COUNTRIES_URL = "https://restcountries.com/v3.1/all";

    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/getCountries")
    public String getCountries(@RequestParam(required = false) String countryName, @RequestParam(required = false) String param2,
                               @RequestParam(required = false) String param3, @RequestParam(required = false) String param4) throws JsonProcessingException {
        String url = COUNTRIES_URL;
        RestTemplate templ = new RestTemplate();
        String response = templ.getForObject(url, String.class);
        JsonNode result = objectMapper.readTree(response);
        if (countryName != null) {
            result = filterByCountryName(countryName, result);
        }
        return result.toString();
    }

    private JsonNode filterByCountryName(String searchCountryName, JsonNode rootNode) {
        Iterator<JsonNode> nodesItr = rootNode.elements();
        ArrayNode filteredNodes = objectMapper.createArrayNode();
        while (nodesItr.hasNext()) {
            JsonNode countryNode = nodesItr.next();
            String countryName = countryNode.at("/name/common").asText();
            if (countryName != null && countryName.toLowerCase().contains(searchCountryName.toLowerCase())) {
                filteredNodes.add(countryNode);
            }
        }
        return filteredNodes;
    }
}

package genAIuc1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class CountriesController {

    public static final String COUNTRIES_URL = "https://restcountries.com/v3.1/all";
    public static final String NAME_PATH = "/name/common";

    private ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate templ = new RestTemplate();

    @GetMapping("/getCountries")
    public String getCountries(@RequestParam(required = false) String countryName,
                               @RequestParam(required = false) Integer population,
                               @RequestParam(required = false) SortDirection sort,
                               @RequestParam(required = false) String param4) throws JsonProcessingException {
        String response = templ.getForObject(COUNTRIES_URL, String.class);
        JsonNode result = objectMapper.readTree(response);
        if (countryName != null) {
            result = filterByCountryName(countryName, result);
        }
        if (population != null) {
            result = filterByPopulation(population, result);
        }
        if (sort != null) {
            result = sortByName(sort, result);
        }
        return result.toString();
    }

    private JsonNode filterByCountryName(String searchCountryName, JsonNode rootNode) {
        ArrayNode filteredNodes = objectMapper.createArrayNode();
        for (JsonNode countryNode : rootNode) {
            String countryName = countryNode.at(NAME_PATH).asText();
            if (countryName != null && countryName.toLowerCase().contains(searchCountryName.toLowerCase())) {
                filteredNodes.add(countryNode);
            }
        }
        return filteredNodes;
    }

    private JsonNode filterByPopulation(Integer searchPopulation, JsonNode rootNode) {
        ArrayNode filteredNodes = objectMapper.createArrayNode();
        for (JsonNode countryNode : rootNode) {
            int population = countryNode.at("/population").asInt(Integer.MAX_VALUE);
            if (population < searchPopulation * 1000000) {
                filteredNodes.add(countryNode);
            }
        }
        return filteredNodes;
    }

    private JsonNode sortByName(SortDirection direction, JsonNode rootNode) {
        ArrayNode sortedNodes = objectMapper.createArrayNode();
        List<JsonNode> nodes = new ArrayList<>();
        for (JsonNode countryNode : rootNode) {
            nodes.add(countryNode);
        }
        Comparator<JsonNode> nameComparator = Comparator.comparing(node -> node.at(NAME_PATH).asText());
        if (direction == SortDirection.descend) {
            nameComparator = nameComparator.reversed();
        }
        nodes.stream().sorted(nameComparator).forEach(sortedNodes::add);
        return sortedNodes;
    }

    private enum SortDirection {
        ascend, descend
    }
}

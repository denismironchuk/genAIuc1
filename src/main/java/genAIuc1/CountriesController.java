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
import java.util.Iterator;
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
                               @RequestParam(required = false) Integer limit) throws JsonProcessingException {
        String response = templ.getForObject(COUNTRIES_URL, String.class);
        JsonNode countries = objectMapper.readTree(response);
        if (countryName != null) {
            countries = filterByCountryName(countryName, countries);
        }
        if (population != null) {
            countries = filterByPopulation(population, countries);
        }
        if (sort != null) {
            countries = sortByName(sort, countries);
        }
        if (limit != null) {
            countries = limitCountries(limit, countries);
        }
        return countries.toString();
    }

    JsonNode filterByCountryName(String searchCountryName, JsonNode rootNode) {
        ArrayNode filteredNodes = objectMapper.createArrayNode();
        for (JsonNode countryNode : rootNode) {
            String countryName = countryNode.at(NAME_PATH).asText();
            if (countryName != null && countryName.toLowerCase().contains(searchCountryName.toLowerCase())) {
                filteredNodes.add(countryNode);
            }
        }
        return filteredNodes;
    }

    JsonNode filterByPopulation(Integer searchPopulation, JsonNode rootNode) {
        ArrayNode filteredNodes = objectMapper.createArrayNode();
        for (JsonNode countryNode : rootNode) {
            int population = countryNode.at("/population").asInt(Integer.MAX_VALUE);
            if (population < searchPopulation * 1000000) {
                filteredNodes.add(countryNode);
            }
        }
        return filteredNodes;
    }

    JsonNode sortByName(SortDirection direction, JsonNode rootNode) {
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

    JsonNode limitCountries(Integer limit, JsonNode rootNode) {
        Iterator<JsonNode> countriesItr = rootNode.elements();
        ArrayNode limitedNodes = objectMapper.createArrayNode();
        while (countriesItr.hasNext() && limitedNodes.size() < limit) {
            limitedNodes.add(countriesItr.next());
        }
        return limitedNodes;
    }

    enum SortDirection {
        ascend, descend
    }
}

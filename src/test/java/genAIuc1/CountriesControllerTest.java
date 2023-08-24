package genAIuc1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CountriesControllerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private CountriesController testInstance = new CountriesController();

    private static final String COUNTRIES_JSON = """
                [
                    {
                        "name": {
                            "common": "name2"
                        },
                        "population": 2000000
                    },
                    {
                        "name": {
                            "common": "name1"
                        },
                        "population": 1000000
                    },
                    {
                        "name": {
                            "common": "name3"
                        },
                        "population": 100000
                    }
                ]
                """;

    @Test
    public void testFilterByName() throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(COUNTRIES_JSON);
        JsonNode filteredNode = testInstance.filterByCountryName("e3", node);
        assertThat(filteredNode.size(), is(1));
        assertThat(getNameFromNode(filteredNode.get(0)), is("name3"));
    }

    @Test
    public void testFilterByPopulation() throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(COUNTRIES_JSON);
        JsonNode filteredNode = testInstance.filterByPopulation(1, node);
        assertThat(filteredNode.size(), is(1));
        assertThat(getNameFromNode(filteredNode.get(0)), is("name3"));
        JsonNode filteredNode2 = testInstance.filterByPopulation(2, node);
        assertThat(filteredNode2.size(), is(2));
        Set<String> names = Set.of(getNameFromNode(filteredNode2.get(0)),
                getNameFromNode(filteredNode2.get(1)));
        assertThat(names, hasItems("name1", "name3"));
    }

    @Test
    public void testSorting() throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(COUNTRIES_JSON);
        JsonNode sortedNode = testInstance.sortByName(CountriesController.SortDirection.ascend, node);
        assertThat(getNameFromNode(sortedNode.get(0)), is("name1"));
        assertThat(getNameFromNode(sortedNode.get(1)), is("name2"));
        assertThat(getNameFromNode(sortedNode.get(2)), is("name3"));

        JsonNode descSortedNode = testInstance.sortByName(CountriesController.SortDirection.descend, node);
        assertThat(getNameFromNode(descSortedNode.get(0)), is("name3"));
        assertThat(getNameFromNode(descSortedNode.get(1)), is("name2"));
        assertThat(getNameFromNode(descSortedNode.get(2)), is("name1"));
    }

    @Test
    public void testLimit() throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(COUNTRIES_JSON);
        JsonNode limitNode = testInstance.limitCountries(2, node);
        assertThat(limitNode.size(), is(2));
        assertThat(getNameFromNode(limitNode.get(0)), is("name2"));
        assertThat(getNameFromNode(limitNode.get(1)), is("name1"));
    }

    private String getNameFromNode(JsonNode node) {
        return node.at("/name/common").asText();
    }
}

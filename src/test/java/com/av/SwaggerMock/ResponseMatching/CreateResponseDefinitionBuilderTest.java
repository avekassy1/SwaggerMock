package com.av.SwaggerMock.ResponseMatching;

import com.av.SwaggerMock.OpenApiToWireMockService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class CreateResponseDefinitionBuilderTest {
    // Tests for expected behaviour like response body json to correct stub
    @Autowired
    OpenApiToWireMockService openApiToWireMockService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldProcess200ResponseWithJsonObjectInTheBody() throws Exception {
        String specWithBody = readFileToString("specs/200ResponseWithJsonObjectInTheBody.yaml");
        StubMapping stubMapping = openApiToWireMockService.generateStubMappings(specWithBody).get(0);
        // TODO - clarify expected json
        // Ignore id and uuid
        String expectedStub = readFileToString("stubs/StubFor200ResponseWithJsonObjectInTheBody.json");

        String actualJson = objectMapper.writeValueAsString(stubMapping.toString());
        JsonNode actualNode = objectMapper.readTree(actualJson);
        JsonNode expectedNode = objectMapper.readTree(expectedStub);

//        Assertions.assertThat(stubMapping).isEqualTo(expectedStub);
        // This approach looks cleaner but much harder to see the differences as they are not highlighted
        // like with Assertions.assertThat()
//        assertThatJson(actualNode).whenIgnoringPaths("id", "uuid")
//            .isEqualTo(expectedNode);
    }

    private String readFileToString(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new FileNotFoundException("Could not find resource " + path);
        }
        File file = new File(resource.getFile());
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }
}

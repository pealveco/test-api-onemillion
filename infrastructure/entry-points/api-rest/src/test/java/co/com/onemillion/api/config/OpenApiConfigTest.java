package co.com.onemillion.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenApiConfigTest {

    @Test
    void shouldCreateOpenApiDefinition() {
        OpenAPI openAPI = new OpenApiConfig().customOpenAPI();

        assertEquals("OMC Leads API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertEquals("Apache 2.0", openAPI.getInfo().getLicense().getName());
    }
}

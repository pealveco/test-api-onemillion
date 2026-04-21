package co.com.onemillion.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UseCasesConfigCoverageTest {

    @Test
    void shouldInstantiateUseCasesConfig() {
        assertNotNull(new UseCasesConfig());
    }
}

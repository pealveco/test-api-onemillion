package co.com.onemillion;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

class MainApplicationTest {

    @Test
    void shouldInstantiateApplication() {
        assertNotNull(new MainApplication());
    }

    @Test
    void shouldDelegateToSpringApplicationRun() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            MainApplication.main(new String[]{"--spring.main.web-application-type=none"});

            springApplication.verify(() -> SpringApplication.run(MainApplication.class,
                    new String[]{"--spring.main.web-application-type=none"}));
        }
    }
}

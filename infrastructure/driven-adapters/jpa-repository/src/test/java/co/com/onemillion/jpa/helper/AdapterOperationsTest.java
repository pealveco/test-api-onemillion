package co.com.onemillion.jpa.helper;

import co.com.onemillion.jpa.JPARepository;
import co.com.onemillion.jpa.JPARepositoryAdapter;
import co.com.onemillion.jpa.LeadData;
import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    @Mock
    private JPARepository repository;

    private JPARepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new JPARepositoryAdapter(repository);
    }

    @Test
    void shouldSaveLead() {
        LocalDateTime now = LocalDateTime.now();
        Lead lead = Lead.builder()
                .nombre("Ana")
                .email("ana@test.com")
                .fuente(LeadSource.INSTAGRAM)
                .createdAt(now)
                .updatedAt(now)
                .build();

        LeadData saved = LeadData.builder()
                .id(1L)
                .nombre("Ana")
                .email("ana@test.com")
                .fuente(LeadSource.INSTAGRAM)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(repository.save(any(LeadData.class))).thenReturn(saved);

        Lead result = adapter.save(lead);

        assertEquals(1L, result.getId());
        assertEquals("ana@test.com", result.getEmail());
    }

    @Test
    void shouldCheckEmailExistence() {
        when(repository.existsByEmailIgnoreCaseAndDeletedFalse("ana@test.com")).thenReturn(true);

        assertTrue(adapter.existsByEmail("ana@test.com"));
    }
}

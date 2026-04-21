package co.com.onemillion.jpa;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.exceptions.DuplicateLeadException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JPARepositoryAdapterTest {
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
        Lead lead = sampleLead(now);
        LeadData saved = sampleLeadData(now).toBuilder().id(1L).build();

        when(repository.save(any(LeadData.class))).thenReturn(saved);

        Lead result = adapter.save(lead);

        assertEquals(1L, result.getId());
        assertEquals("ana@test.com", result.getEmail());
        assertEquals(LeadSource.INSTAGRAM, result.getFuente());
    }

    @Test
    void shouldThrowDuplicateLeadWhenSaveViolatesUniqueConstraint() {
        Lead lead = sampleLead(LocalDateTime.now());

        when(repository.save(any(LeadData.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        DuplicateLeadException exception = assertThrows(DuplicateLeadException.class, () -> adapter.save(lead));

        assertTrue(exception.getMessage().contains("ana@test.com"));
    }

    @Test
    void shouldFindOnlyActiveLeadById() {
        LocalDateTime now = LocalDateTime.now();
        when(repository.findById(1L)).thenReturn(Optional.of(sampleLeadData(now).toBuilder().id(1L).deleted(false).build()));

        Optional<Lead> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void shouldIgnoreDeletedLeadWhenFindingById() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleLeadData(LocalDateTime.now()).toBuilder()
                .id(1L)
                .deleted(true)
                .build()));

        Optional<Lead> result = adapter.findById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindLeadByEmail() {
        LocalDateTime now = LocalDateTime.now();
        when(repository.findByEmailIgnoreCaseAndDeletedFalse("ana@test.com"))
                .thenReturn(Optional.of(sampleLeadData(now).toBuilder().id(5L).build()));

        Optional<Lead> result = adapter.findByEmail("ana@test.com");

        assertTrue(result.isPresent());
        assertEquals(5L, result.get().getId());
    }

    @Test
    void shouldFindAllUsingFilterAndSafePagination() {
        LocalDateTime from = LocalDateTime.parse("2026-04-01T00:00:00");
        LocalDateTime to = LocalDateTime.parse("2026-04-20T23:59:59");
        LeadFilter filter = LeadFilter.builder()
                .page(-1)
                .limit(0)
                .source(LeadSource.INSTAGRAM)
                .startDate(from)
                .endDate(to)
                .build();
        LocalDateTime now = LocalDateTime.now();

        when(repository.findAll(org.mockito.ArgumentMatchers.<Specification<LeadData>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(
                sampleLeadData(now).toBuilder().id(11L).build()
        )));

        LeadPage result = adapter.findAll(filter);

        ArgumentCaptor<Specification<LeadData>> specificationCaptor = ArgumentCaptor.forClass(Specification.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(specificationCaptor.capture(), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(1, pageable.getPageSize());
        assertEquals(-1, result.getPage());
        assertEquals(0, result.getLimit());
        assertEquals(1, result.getTotal());
        assertEquals(11L, result.getData().getFirst().getId());

        assertSpecificationWithAllFilters(specificationCaptor.getValue(), from, to);
    }

    @Test
    void shouldUpdateLeadDelegatingToSave() {
        LocalDateTime now = LocalDateTime.now();
        when(repository.save(any(LeadData.class))).thenReturn(sampleLeadData(now).toBuilder().id(12L).build());

        Lead result = adapter.update(sampleLead(now));

        assertEquals(12L, result.getId());
    }

    @Test
    void shouldSoftDeleteActiveLead() {
        LocalDateTime now = LocalDateTime.now();
        LeadData existing = sampleLeadData(now.minusDays(1)).toBuilder().id(4L).deleted(false).build();

        when(repository.findById(4L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        boolean result = adapter.softDelete(4L);

        assertTrue(result);
        assertTrue(existing.isDeleted());
        assertNotNull(existing.getUpdatedAt());
    }

    @Test
    void shouldReturnFalseWhenSoftDeletingMissingOrDeletedLead() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        boolean missing = adapter.softDelete(9L);

        when(repository.findById(10L)).thenReturn(Optional.of(sampleLeadData(LocalDateTime.now()).toBuilder()
                .id(10L)
                .deleted(true)
                .build()));

        boolean deleted = adapter.softDelete(10L);

        assertFalse(missing);
        assertFalse(deleted);
        verify(repository).findById(9L);
        verify(repository).findById(10L);
    }

    @Test
    void shouldCheckEmailExistence() {
        when(repository.existsByEmailIgnoreCaseAndDeletedFalse("ana@test.com")).thenReturn(true);

        assertTrue(adapter.existsByEmail("ana@test.com"));
    }

    @Test
    void shouldExposeAggregateMetrics() {
        when(repository.countByDeletedFalse()).thenReturn(14L);
        when(repository.countByFuente()).thenReturn(List.of(
                new Object[]{LeadSource.INSTAGRAM, 9L},
                new Object[]{LeadSource.FACEBOOK, 5L}
        ));
        when(repository.averagePresupuesto()).thenReturn(BigDecimal.valueOf(2300));
        when(repository.countByCreatedAtAfterAndDeletedFalse(any(LocalDateTime.class))).thenReturn(6L);

        assertEquals(14L, adapter.countAll());
        Map<LeadSource, Long> bySource = adapter.countByFuente();
        assertEquals(9L, bySource.get(LeadSource.INSTAGRAM));
        assertEquals(BigDecimal.valueOf(2300), adapter.averagePresupuesto());
        assertEquals(6L, adapter.countLast7Days());
    }

    @Test
    void shouldReturnZeroWhenAverageBudgetIsNull() {
        when(repository.averagePresupuesto()).thenReturn(null);

        assertEquals(BigDecimal.ZERO, adapter.averagePresupuesto());
    }

    @SuppressWarnings("unchecked")
    private void assertSpecificationWithAllFilters(
            Specification<LeadData> specification,
            LocalDateTime from,
            LocalDateTime to
    ) {
        Root<LeadData> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Path<Boolean> deletedPath = mock(Path.class);
        Path<LeadSource> fuentePath = mock(Path.class);
        Path<LocalDateTime> createdAtPath = mock(Path.class);
        Predicate deletedPredicate = mock(Predicate.class);
        Predicate fuentePredicate = mock(Predicate.class);
        Predicate fromPredicate = mock(Predicate.class);
        Predicate toPredicate = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        when(root.get("deleted")).thenReturn((Path) deletedPath);
        when(root.get("fuente")).thenReturn((Path) fuentePath);
        when(root.get("createdAt")).thenReturn((Path) createdAtPath);
        when(criteriaBuilder.isFalse(deletedPath)).thenReturn(deletedPredicate);
        when(criteriaBuilder.equal(fuentePath, LeadSource.INSTAGRAM)).thenReturn(fuentePredicate);
        when(criteriaBuilder.greaterThanOrEqualTo(createdAtPath, from)).thenReturn(fromPredicate);
        when(criteriaBuilder.lessThanOrEqualTo(createdAtPath, to)).thenReturn(toPredicate);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(finalPredicate);

        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        assertEquals(finalPredicate, result);
        verify(criteriaBuilder).isFalse(deletedPath);
        verify(criteriaBuilder).equal(fuentePath, LeadSource.INSTAGRAM);
        verify(criteriaBuilder).greaterThanOrEqualTo(createdAtPath, from);
        verify(criteriaBuilder).lessThanOrEqualTo(createdAtPath, to);
    }

    private Lead sampleLead(LocalDateTime now) {
        return Lead.builder()
                .id(1L)
                .nombre("Ana")
                .email("ana@test.com")
                .telefono("3001234567")
                .fuente(LeadSource.INSTAGRAM)
                .productoInteres("CRM")
                .presupuesto(BigDecimal.valueOf(1000))
                .deleted(false)
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build();
    }

    private LeadData sampleLeadData(LocalDateTime now) {
        return LeadData.builder()
                .id(1L)
                .nombre("Ana")
                .email("ana@test.com")
                .telefono("3001234567")
                .fuente(LeadSource.INSTAGRAM)
                .productoInteres("CRM")
                .presupuesto(BigDecimal.valueOf(1000))
                .deleted(false)
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build();
    }
}

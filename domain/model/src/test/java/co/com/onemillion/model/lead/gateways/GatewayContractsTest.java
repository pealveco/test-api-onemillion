package co.com.onemillion.model.lead.gateways;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.LeadSource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GatewayContractsTest {

    @Test
    void shouldImplementLeadRepositoryContract() {
        Lead lead = Lead.builder().id(1L).email("ana@test.com").fuente(LeadSource.INSTAGRAM).build();
        LeadFilter filter = LeadFilter.builder().page(0).limit(10).build();
        LeadPage page = LeadPage.builder().data(List.of(lead)).page(0).limit(10).total(1).build();

        LeadRepository repository = new LeadRepository() {
            @Override
            public Lead save(Lead value) {
                return value;
            }

            @Override
            public Optional<Lead> findById(Long id) {
                return Optional.of(lead);
            }

            @Override
            public Optional<Lead> findByEmail(String email) {
                return Optional.of(lead);
            }

            @Override
            public LeadPage findAll(LeadFilter filter) {
                return page;
            }

            @Override
            public Lead update(Lead value) {
                return value;
            }

            @Override
            public boolean softDelete(Long id) {
                return true;
            }

            @Override
            public boolean existsByEmail(String email) {
                return true;
            }

            @Override
            public long countAll() {
                return 1;
            }

            @Override
            public Map<LeadSource, Long> countByFuente() {
                return Map.of(LeadSource.INSTAGRAM, 1L);
            }

            @Override
            public BigDecimal averagePresupuesto() {
                return BigDecimal.TEN;
            }

            @Override
            public long countLast7Days() {
                return 1;
            }
        };

        assertEquals(lead, repository.save(lead));
        assertTrue(repository.findById(1L).isPresent());
        assertTrue(repository.findByEmail("ana@test.com").isPresent());
        assertEquals(page, repository.findAll(filter));
        assertEquals(lead, repository.update(lead));
        assertTrue(repository.softDelete(1L));
        assertTrue(repository.existsByEmail("ana@test.com"));
        assertEquals(1, repository.countAll());
        assertEquals(1L, repository.countByFuente().get(LeadSource.INSTAGRAM));
        assertEquals(BigDecimal.TEN, repository.averagePresupuesto());
        assertEquals(1, repository.countLast7Days());
    }

    @Test
    void shouldImplementAiSummaryGatewayContract() {
        AiSummaryGateway gateway = leads -> "summary:" + leads.size();

        assertEquals("summary:0", gateway.generateLeadSummary(List.of()));
    }
}

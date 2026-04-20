package co.com.onemillion.model.lead.gateways;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.LeadSource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public interface LeadRepository {
    Lead save(Lead lead);

    Optional<Lead> findById(Long id);

    Optional<Lead> findByEmail(String email);

    LeadPage findAll(LeadFilter filter);

    Lead update(Lead lead);

    boolean softDelete(Long id);

    boolean existsByEmail(String email);

    long countAll();

    Map<LeadSource, Long> countByFuente();

    BigDecimal averagePresupuesto();

    long countLast7Days();
}

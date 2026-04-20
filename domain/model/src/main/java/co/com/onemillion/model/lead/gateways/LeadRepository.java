package co.com.onemillion.model.lead.gateways;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LeadRepository {
    Lead save(Lead lead);

    Optional<Lead> findById(Long id);

    Optional<Lead> findByEmail(String email);

    List<Lead> findAll(int page, int limit, LeadSource fuente, LocalDateTime createdAtFrom, LocalDateTime createdAtTo);

    Lead update(Lead lead);

    boolean softDelete(Long id);

    boolean existsByEmail(String email);

    long countAll();

    Map<LeadSource, Long> countByFuente();

    BigDecimal averagePresupuesto();

    long countLast7Days();
}

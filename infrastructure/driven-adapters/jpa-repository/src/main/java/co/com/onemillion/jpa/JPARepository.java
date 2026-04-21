package co.com.onemillion.jpa;

import co.com.onemillion.model.lead.LeadSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JPARepository extends JpaRepository<LeadData, Long>, JpaSpecificationExecutor<LeadData> {
    boolean existsByEmailIgnoreCaseAndDeletedFalse(String email);

    Optional<LeadData> findByEmailIgnoreCaseAndDeletedFalse(String email);

    long countByDeletedFalse();

    long countByCreatedAtAfterAndDeletedFalse(LocalDateTime createdAt);

    @Query("select avg(l.presupuesto) from LeadData l where l.deleted = false and l.presupuesto is not null")
    BigDecimal averagePresupuesto();

    @Query("select l.fuente, count(l) from LeadData l where l.deleted = false group by l.fuente")
    List<Object[]> countByFuente();
}
